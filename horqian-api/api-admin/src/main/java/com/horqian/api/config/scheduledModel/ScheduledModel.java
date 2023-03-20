package com.horqian.api.config.scheduledModel;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.horqian.api.config.util.TencentcloudSendSms;
import com.horqian.api.dictionaries.meet.MeetingStatusEnum;
import com.horqian.api.dictionaries.meet.MeetingTypeEnum;
import com.horqian.api.dictionaries.sys.MessageSendEnum;
import com.horqian.api.entity.im.ImGroup;
import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.entity.sys.SysMessage;
import com.horqian.api.service.meet.MeetMeetingService;
import com.horqian.api.service.meet.MeetParticipationUserService;
import com.horqian.api.service.sys.SysMessageService;
import com.horqian.api.util.WxTrTcUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author 孟
 * @date 2023/1/09
 * @description
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledModel {

    private final WxTrTcUtil wxTrTcUtil;

    private final MeetMeetingService meetMeetingService;

    private final SysMessageService sysMessageService;

    private final StringRedisTemplate stringRedisTemplate;

    private final MeetParticipationUserService meetParticipationUserService;

    /**
     * 修改会议状态
     */
    @Scheduled(cron = "0 */1 * * * ?")
    @Transactional(rollbackFor = Throwable.class)
    public void meetingStatus() {
        // 查询未开始或进行中的会议
        LambdaQueryWrapper<MeetMeeting> qw = Wrappers.lambdaQuery();
        qw.in(MeetMeeting::getStatus, MeetingStatusEnum.UNSTARTED.type, MeetingStatusEnum.HAVEINHAND.type);
        List<MeetMeeting> list = meetMeetingService.list(qw);
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        if (list != null && !list.isEmpty()) {
            // 创建会议集合
            List<MeetMeeting> meetings = new ArrayList<>();
            // 遍历会议集合
            list.stream().forEach(x -> {
                if (x.getStatus() == MeetingStatusEnum.UNSTARTED.type) {
                    // 开始时间
                    if (x.getStartTime().isBefore(now)) {
                        // 改变会议状态
                        x.setStatus(MeetingStatusEnum.HAVEINHAND.type);
                        // 添加到对应集合
                        meetings.add(x);
                        // 删除redis中标识 说明: 创建会议需要早8晚9发送短信 如果不在此时间段需要第二天早上八点统一发送，若会议开始时间早于第二天8点则把redis中的标识符删除不发送短信
                        stringRedisTemplate.delete(x.getId() + ":" + x.getOnlyId());
                    }
                } else {
                    // 结束时间
                    if (x.getEndTime().isBefore(now)) {
                        // 移除不在线用户
                        meetParticipationUserService.removeGroupUser(x.getGroupId());
                        // 获取用户
                        String userList = wxTrTcUtil.getUserList(x.getGroupId());
                        // 查询会议人数
                        ImGroup imGroup = JSON.parseObject(userList, ImGroup.class);
                        // 如果房间人数是0的话结束会议
                        if (!imGroup.getErrorCode().equals(10010)) {
                            if (imGroup.getMemberNum() == 0) {
                                x.setStatus(MeetingStatusEnum.END.type);
                                meetings.add(x);
                            }
                        }
                    }
                }
            });
            if (!meetings.isEmpty())
                meetMeetingService.updateBatchById(meetings);
        }
    }


    /**
     * 根据会议开始还有15分钟会议发送短信
     */
    @Scheduled(cron = "0 */1 * * * ?")
    @Transactional(rollbackFor = Throwable.class)
    public void smsMeetingSend() {
        //  获取当前时间 并在此基础上加上十五分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(15).withSecond(00);
        //String format = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00"));
        // 格式化时间
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 查询会议
        List<MeetMeeting> list = meetMeetingService.list(new LambdaQueryWrapper<MeetMeeting>()
                .eq(MeetMeeting::getStatus, MeetingStatusEnum.UNSTARTED.type)
                .ge(MeetMeeting::getStartTime, time.format(df))
                .lt(MeetMeeting::getStartTime, time.plusMinutes(1).format(df)));
        if (!list.isEmpty()) {
            list.stream().forEach(x ->{
                // 创建消息
                SysMessage sysMessage = new SysMessage();
                // 查询会议
                if (x.getType().equals(MeetingTypeEnum.INQUIRIES.type)) {
                    // 问诊距离开始时间还剩15分钟
                    sysMessage = sysMessageService.getById(MessageSendEnum.INQUIRYSTARTTIME15.id);
                } else {
                    // 会议距离开始时间还剩15分钟
                    sysMessage = sysMessageService.getById(MessageSendEnum.MEETINGSTARTTIME15M.id);
                }
                  List<String> data = new ArrayList<>();
                // 会议名称
                data.add(x.getName());
                // 会议开始时间结束时间
                data.add(df.format(x.getStartTime()) + " - " + df.format(x.getEndTime()));
                sysMessage.setData(data);
                // 发送短信手机号
                 List<String> listPhone = meetParticipationUserService.smsSend(x);
                 // todo 腾讯云发送短信
                 TencentcloudSendSms.sendSms(sysMessage, listPhone);
            });
        }
    }


    /**
     * 根据会议开始前24小时发送短信提醒
     */
    @Scheduled(cron = "0 */1 * * * ?")
    @Transactional(rollbackFor = Throwable.class)
    public void smsMeetingSend24H() {
        // 获取当前时间 并在此基础上加一天
        LocalDateTime time = LocalDateTime.now().plusDays(1).withSecond(00);
        String format = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
        // 格式化时间
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 查询会议
        List<MeetMeeting> list = meetMeetingService.list(new LambdaQueryWrapper<MeetMeeting>()
                .eq(MeetMeeting::getStatus, MeetingStatusEnum.UNSTARTED.type)
                .ge(MeetMeeting::getStartTime, time.format(df))
                .lt(MeetMeeting::getStartTime, time.plusMinutes(1).format(df)));

        if (!list.isEmpty()) {
            // 发送短信
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            // 开始时间
            LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 8, 0, 0);
            // 结束时间
            LocalDateTime end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 21, 0, 0);

            list.stream().forEach(x ->{
                if (now.isAfter(start) && now.isBefore(end)) {
                    // 创建消息
                    SysMessage sysMessage = sysMessageService.getById(MessageSendEnum.GETMEETINGSTARTTIME24H.id);
                    List<String> data = new ArrayList<>();
                    // 会议名称
                    data.add(x.getName());
                    // 会议开始时间结束时间
                    data.add(df.format(x.getStartTime()) + " - " + df.format(x.getEndTime()));

                    sysMessage.setData(data);
                    // 发送短信手机号
                    List<String> listPhone = meetParticipationUserService.smsSend(x);
                    // todo 腾讯云发送短信
                     TencentcloudSendSms.sendSms(sysMessage, listPhone);
                } else {
                    // 放入对应会议信息
                    stringRedisTemplate.opsForValue().set("H" + x.getId() + ":" + x.getOnlyId(), x.getId().toString());
                }
            });
        }
    }

    ///**
    // * 会议通知 会议时间是24小时 但不在 早8 - 晚10的时间段的
    // */
    //@Scheduled(cron = "0 10 8 * * ?")
    //@Transactional(rollbackFor = Throwable.class)
    //public void smsMeetingNotice() {
    //    // 查询未开始的会议
    //    List<MeetMeeting> list = meetMeetingService.list(new LambdaQueryWrapper<MeetMeeting>()
    //            .eq(MeetMeeting::getStatus, MeetingStatusEnum.UNSTARTED.type));
    //
    //    list.stream().forEach(x -> {
    //        // 放入对应会议信息
    //        String str = stringRedisTemplate.opsForValue().get("H" + x.getId() + ":" + x.getOnlyId());
    //        // 格式化时间
    //        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //
    //        if (StringUtils.hasText(str)) {
    //            SysMessage sysMessage = sysMessageService.getById(MessageSendEnum.GETMEETINGSTARTTIME24H.id);
    //            sysMessage.getData().add(x.getName());
    //            sysMessage.getData().add(df.format(x.getStartTime()) + " - " + df.format(x.getEndTime()));
    //            // 发送短信手机号
    //            List<String> listPhone = meetParticipationUserService.smsSend(x);
    //            // todo 腾讯云发送短信
    //            TencentcloudSendSms.sendSms(sysMessage, listPhone);
    //             // 删除狐疑24小时删除
    //            stringRedisTemplate.delete("H" + x.getId() + ":" + x.getOnlyId());
    //        }
    //
    //    });
    //}


    /**
     * 创建会议发送短信
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional(rollbackFor = Throwable.class)
    public void smsMeetingCreate() {
        // 查询未开始的会议
        List<MeetMeeting> list = meetMeetingService.list(new LambdaQueryWrapper<MeetMeeting>()
                .eq(MeetMeeting::getStatus, MeetingStatusEnum.UNSTARTED.type));

        list.stream().forEach(x -> {
            // 放入对应会议信息
            String str = stringRedisTemplate.opsForValue().get(x.getId() + ":" + x.getOnlyId());
            // 格式化时间
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            if (StringUtils.hasText(str)) {
                SysMessage sysMessage = sysMessageService.getById(MessageSendEnum.MEETINGCREATE.id);
                  List<String> data = new ArrayList<>();
                  data.add(x.getName());
                  data.add(df.format(x.getStartTime()) + " - " + df.format(x.getEndTime()));
                  sysMessage.setData(data);
                // 发送短信手机号
                List<String> listPhone = meetParticipationUserService.smsSend(x);
                // todo 腾讯云发送短信
                TencentcloudSendSms.sendSms(sysMessage, listPhone);
                 // 删除会议
                stringRedisTemplate.delete(x.getId() + ":" + x.getOnlyId());
            }

        });
    }
}
