package com.horqian.api.controller.meet;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.horqian.api.config.util.TencentcloudSendSms;
import com.horqian.api.dictionaries.meet.MeetingStatusEnum;
import com.horqian.api.dictionaries.meet.MeetingTypeEnum;
import com.horqian.api.dictionaries.sys.MessageSendEnum;
import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.entity.meet.MeetParticipationUser;
import com.horqian.api.entity.sys.SysMessage;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.meet.MeetMeetingService;
import com.horqian.api.service.meet.MeetParticipationUserService;
import com.horqian.api.service.sys.SysMessageService;
import com.horqian.api.service.sys.SysUserService;
import com.horqian.api.util.PageUtils;
import com.horqian.api.util.WxTrTcUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 会议 - 会议表 前端控制器
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@RestController
@RequestMapping("/meet/meeting")
@RequiredArgsConstructor
public class MeetMeetingController {

    private final WxTrTcUtil wxTrTcUtil;

    private final SysUserService sysUserService;

    private final SysMessageService sysMessageService;

    private final MeetMeetingService meetMeetingService;

    private final StringRedisTemplate stringRedisTemplate;

    private final MeetParticipationUserService meetParticipationUserService;

    /**
     * 会议 - 会议列表
     *
     * @param pageUtils
     * @param meetMeeting
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<MeetMeeting> pageUtils, MeetMeeting meetMeeting) {
        // 查询用户
        SysUser user = sysUserService.getUser();

        LambdaQueryWrapper<MeetMeeting> qw = Wrappers.lambdaQuery();
        // 会议名称
        qw.like(StringUtils.hasText(meetMeeting.getName()), MeetMeeting::getName, meetMeeting.getName());
        // 会议id
        qw.like(StringUtils.hasText(meetMeeting.getOnlyId()), MeetMeeting::getOnlyId, meetMeeting.getOnlyId());
        // 会议状态
        qw.eq(meetMeeting.getStatus() != null, MeetMeeting::getStatus, meetMeeting.getStatus());
        // 会议开始时间
        qw.ge(meetMeeting.getStartTime() != null, MeetMeeting::getStartTime, meetMeeting.getStartTime());
        // 会议结束时间
        qw.le(meetMeeting.getEndTime() != null, MeetMeeting::getStartTime, meetMeeting.getEndTime());
        // 查询我参与的会议
        List<MeetParticipationUser> participationUsers = meetParticipationUserService.list(new LambdaQueryWrapper<MeetParticipationUser>()
                .eq(MeetParticipationUser::getUserId, user.getId()));
        if (participationUsers.isEmpty()) {
            // 查询创建的会议
            qw.eq(MeetMeeting::getUserId, user.getId());
        } else {
            // 获取我参加过的会议id
            List<String> list = participationUsers.stream().map(MeetParticipationUser::getMeetingOnlyId).collect(Collectors.toList());
            qw.and(x -> x.eq(MeetMeeting::getUserId, user.getId()).or().in(MeetMeeting::getOnlyId, list));
        }
        // 根据开始时间排序
        qw.orderByDesc(MeetMeeting::getStartTime);

        // 查询会议列表
        IPage<MeetMeeting> page = meetMeetingService.page(pageUtils.getPage(), qw);

        // 筛选会议 确认自己是否是参会状态
        if (!page.getRecords().isEmpty()) {
            // 根据会议id分组
            Map<String, MeetParticipationUser> map = participationUsers.stream().collect(Collectors.toMap(x -> x.getMeetingOnlyId(), Function.identity()));
            page.getRecords().stream().forEach(x -> {
                MeetParticipationUser meetParticipationUser = map.get(x.getOnlyId());
                if (meetParticipationUser == null)
                    x.setMeetStatus(0);
                // 会议时间
                x.setOften(meetMeetingService.getTime(x));
            });
        }

        return ResultFactory.success(page.getRecords(), page.getTotal());
    }

    /**
     * 会议 - 修改保存
     *
     * @param meetMeeting
     * @return
     */
    @PostMapping("/save")
    @Transactional(rollbackFor = Throwable.class)
    public Result save(@RequestBody MeetMeeting meetMeeting) {
        // 标识符  标识 新建 还是 保存
        Boolean flag = false;
        // 格式化时间
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 会议id
        if (meetMeeting.getId() == null) {
            int count = meetMeetingService.count(new LambdaQueryWrapper<MeetMeeting>()
                    .eq(MeetMeeting::getOnlyId, meetMeeting.getOnlyId()));
            if (count > 0)
                throw new BaseException("会议ID已存在");
            // 获取当前登录用户
            SysUser user = sysUserService.getUser();
            // 用户id
            meetMeeting.setUserId(user.getId());
            // 用户名称
            meetMeeting.setUserName(user.getNickname());
            // 创建会议时间
            meetMeeting.setMeetingTime(LocalDateTime.now());
            // 获取对应的枚举
            MeetingTypeEnum meetingTypeEnum = MeetingTypeEnum.meetingTypeEnum(meetMeeting.getType());
            // 生成trtc房间号
            meetMeeting.setRoomId("Trtc_" + meetingTypeEnum.title + "_" + meetMeeting.getOnlyId());
            // 分配im群组
            String groupId = wxTrTcUtil.createGroup("Im_" + meetingTypeEnum.title + "_" + meetMeeting.getOnlyId());
            meetMeeting.setGroupId(groupId);
            // 创建会议
            flag = true;
        } else {
            // 判断会议类型
            if (meetMeeting.getStatus().equals(MeetingStatusEnum.UNSTARTED.type)) {
                MeetMeeting meeting = meetMeetingService.getById(meetMeeting.getId());
                // 查询会议开始时间是否有改动
                if (!meetMeeting.getStartTime().isEqual(meeting.getStartTime())) {
                    // 创建消息实列
                    SysMessage sysMessage = new SysMessage();
                    // 查询会议
                    if (meeting.getType().equals(MeetingTypeEnum.INQUIRIES.type)) {
                        // 查询短信模板
                        sysMessage = sysMessageService.getById(MessageSendEnum.INQUIRYUPDATETIME.id);
                    } else {
                        // 查询短信模板
                        sysMessage = sysMessageService.getById(MessageSendEnum.MEETINGUPDATETIME.id);
                    }
                    List<String> data = new ArrayList<>();
                    data.add(meetMeeting.getName());
                    data.add(df.format(meetMeeting.getStartTime()) + " - " + df.format(meetMeeting.getEndTime()));
                    data.add(df.format(meeting.getStartTime()) + " - " + df.format(meeting.getEndTime()));
                    sysMessage.setData(data);
                    // 发送短信手机号
                    List<String> list = meetParticipationUserService.smsSend(meeting);
                    // todo 腾讯云发送短信
                     TencentcloudSendSms.sendSms(sysMessage, list);
                }
            }
        }
        // 保存参会成员
        if (meetMeeting.getParticipationUserList() != null && !meetMeeting.getParticipationUserList().isEmpty()) {
            // 1.先清除所有的参会成员
            meetParticipationUserService.remove(new LambdaQueryWrapper<MeetParticipationUser>().eq(MeetParticipationUser::getMeetingOnlyId, meetMeeting.getOnlyId()));
            // 2.保存参会成员列表
            meetParticipationUserService.saveBatch(meetMeeting.getParticipationUserList());
        }

        // 会议开始时间
        if (StringUtils.hasText(meetMeeting.getMeetingStartTime())) {
            // 会议开始时间
            meetMeeting.setMeetingStartTime(String.valueOf(LocalDateTime.parse(meetMeeting.getMeetingStartTime(), df).toInstant(ZoneOffset.ofHours(8)).toEpochMilli()));
        }
        // 会议结束时间
        if (StringUtils.hasText(meetMeeting.getMeetingEndTime())) {
            // 会议结束时间
            meetMeeting.setMeetingEndTime(String.valueOf(LocalDateTime.parse(meetMeeting.getMeetingEndTime(), df).toInstant(ZoneOffset.ofHours(8)).toEpochMilli()));
        }
        // 本次会议开始时间
        if (StringUtils.hasText(meetMeeting.getAgainMeetingTime())) {
            // 本次会议开始时间
            meetMeeting.setAgainMeetingTime(String.valueOf(LocalDateTime.parse(meetMeeting.getAgainMeetingTime(), df).toInstant(ZoneOffset.ofHours(8)).toEpochMilli()));
        }

        boolean bool = meetMeetingService.saveOrUpdate(meetMeeting);
        if (!bool)
            throw new BaseException("保存失败");

        if (flag) {
             // 发送短信
             // 获取当前时间
             LocalDateTime now = LocalDateTime.now();
             // 开始时间
             LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 8, 0, 0);
             // 结束时间
             LocalDateTime end = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 21, 0, 0);
             if (now.isAfter(start) && now.isBefore(end)) {
                 // 查询消息模板
                 SysMessage sysMessage = sysMessageService.getById(MessageSendEnum.MEETINGCREATE.id);
                 List<String> data = new ArrayList<>();
                 data.add(meetMeeting.getName());
                 data.add(df.format(meetMeeting.getStartTime()) + " - " + df.format(meetMeeting.getEndTime()));
                 sysMessage.setData(data);
                 // 发送短信手机号
               List<String> list = meetParticipationUserService.smsSend(meetMeeting);
               // todo 腾讯云发送短信
               //TencentcloudSendSms.sendSms(sysMessage, list);
             } else {
                 // 放入对应会议信息
                 stringRedisTemplate.opsForValue().set(meetMeeting.getId() + ":" + meetMeeting.getOnlyId(), meetMeeting.getId().toString());
             }
        }

        return ResultFactory.success();
    }

    /**
     * 会议 - 生成会议id
     *
     * @return
     */
    @GetMapping("/random")
    public Result random() {
        // 生成5位随机数
        int code = (int) ((Math.random()*9+1)*10000);
        // 保证会议id唯一
        int count = meetMeetingService.count(new LambdaQueryWrapper<MeetMeeting>()
                .eq(MeetMeeting::getOnlyId, code));
        if (count > 0) {
            random();
        }
        return ResultFactory.success(code);
    }

    /**
     * 会议 - 查询会议时长
     *
     * @param meetingOnlyId 会议id
     * @return
     */
    @GetMapping("/time")
    public Result time(String meetingOnlyId) {
        // 查询会议
        MeetMeeting meetMeeting = meetMeetingService.getOne(new LambdaQueryWrapper<MeetMeeting>()
                .eq(MeetMeeting::getOnlyId, meetingOnlyId));
        // 计算时间戳
        Long time = meetMeetingService.getTime(meetMeeting);
        return ResultFactory.success(time);
    }

    /**
     * 会议 - 详情
     *
     * @param id
     * @return
     */
    @GetMapping("/show")
    public Result show(Integer id) {
        //Integer count = 0;
        // 会议
        MeetMeeting meetMeeting = meetMeetingService.getById(id);

        // // 会议开始时间在现在时间之后
        // if (meetMeeting.getStartTime().isBefore(LocalDateTime.now())) {
        //     meetMeeting.setStatus(MeetingStatusEnum.HAVEINHAND.type);
        //     count++;
        // }
        //  //会议开始时间在现在时间之后
        // if (meetMeeting.getEndTime().isBefore(LocalDateTime.now())) {
        //     meetMeeting.setStatus(MeetingStatusEnum.END.type);
        //     count++;
        // }
        // if (count > 0)
        //     meetMeetingService.updateById(meetMeeting);

        // 参会成员列表
        List<MeetParticipationUser> participationUserList = meetParticipationUserService.list(new LambdaQueryWrapper<MeetParticipationUser>()
                .eq(MeetParticipationUser::getMeetingOnlyId, meetMeeting.getOnlyId()));
        // 参会人员列表
        meetMeeting.setParticipationUserList(participationUserList);
        // 获取会议时间
        meetMeeting.setOften(meetMeetingService.getTime(meetMeeting));
        return ResultFactory.success(meetMeeting);
    }

    /**
     * 会议 - 取消
     *
     * @return meetMeeting 会议
     */
    @PostMapping("/cancel")
    public Result cancel(@RequestBody MeetMeeting meetMeeting) {
        // 格式化时间
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 会议取消
        boolean bool = meetMeetingService.updateById(meetMeeting);
        if (!bool)
            throw new BaseException("保存失败");
        // 创建消息
        SysMessage sysMessage = new SysMessage();
        // 查询会议
        MeetMeeting meeting = meetMeetingService.getById(meetMeeting.getId());
        // 查询会议
        if (meeting.getType().equals(MeetingTypeEnum.INQUIRIES.type)) {
            // 取消会议
            sysMessage = sysMessageService.getById(MessageSendEnum.INQUIRYCANCEL.id);
        } else {
            // 取消问诊
            sysMessage = sysMessageService.getById(MessageSendEnum.MEETINGCANCEL.id);
        }

        List<String> data = new ArrayList<>();
        data.add(meeting.getName());
        data.add(df.format(meeting.getStartTime()) + " - " + df.format(meeting.getEndTime()));
        sysMessage.setData(data);

        // 发送短信手机号
         List<String> list = meetParticipationUserService.smsSend(meeting);
         // todo 腾讯云发送短信
         TencentcloudSendSms.sendSms(sysMessage, list);

        return ResultFactory.success();
    }

    /**
     * 会议 - 删除
     *
     * @return id 多个逗号分割
     */
    @DeleteMapping("/delete")
    public Result delete(String id) {
        List<String> ids = Arrays.asList(id.split(","));
        boolean bool = meetMeetingService.removeByIds(ids);
        if (!bool)
            throw new BaseException("删除失败");

        return ResultFactory.success();
    }

    /**
     * 会议 - 在线问诊查询
     *
     * @return meetMeeting 会议模块
     */
    @PostMapping("/diagnostic")
    public Result diagnostic(PageUtils<MeetMeeting> pageUtils, MeetMeeting meetMeeting) {
        LambdaQueryWrapper<MeetMeeting> qw = Wrappers.lambdaQuery();
        // 在线问诊
        qw.eq(MeetMeeting::getType, MeetingTypeEnum.INQUIRIES.type);
        // 会议状态
        if (meetMeeting.getStatus() != null)
            qw.in(MeetMeeting::getStatus, MeetingStatusEnum.UNSTARTED, MeetingStatusEnum.HAVEINHAND);
        else
            qw.eq(MeetMeeting::getStatus, meetMeeting.getStatus());
        // 会议名称
        qw.eq(StringUtils.hasText(meetMeeting.getName()), MeetMeeting::getName, meetMeeting.getName());
        // 会议id
        qw.eq(StringUtils.hasText(meetMeeting.getOnlyId()), MeetMeeting::getOnlyId, meetMeeting.getOnlyId());
        Page<MeetMeeting> page = meetMeetingService.page(pageUtils.getPage(), qw);

        return ResultFactory.success(page.getRecords(), page.getTotal());
    }
}

