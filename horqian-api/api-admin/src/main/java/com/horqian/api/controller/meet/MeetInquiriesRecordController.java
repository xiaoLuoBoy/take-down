package com.horqian.api.controller.meet;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.horqian.api.dictionaries.meet.MeetingInquiriesRecordinterviewStatusEnum;
import com.horqian.api.entity.meet.MeetInquiriesRecord;
import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.entity.meet.MeetParticipationUser;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.meet.MeetInquiriesRecordService;
import com.horqian.api.service.meet.MeetMeetingService;
import com.horqian.api.util.PageUtils;
import com.horqian.api.util.WxTrTcUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.Count;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 会议 - 问诊记录 前端控制器
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@RestController
@RequestMapping("/meet/inquiriesRecord")
@RequiredArgsConstructor
public class MeetInquiriesRecordController {

    private final WxTrTcUtil wxTrTcUtil;

    private final MeetMeetingService meetMeetingService;

    private final MeetInquiriesRecordService meetInquiriesRecordService;

    /**
     * 会议 - 问诊记录列表
     *
     * @param pageUtils
     * @param meetingOnlyId 会议id
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<MeetInquiriesRecord> pageUtils, String meetingOnlyId) {
        LambdaQueryWrapper<MeetInquiriesRecord> qw = Wrappers.lambdaQuery();
         // 会议id
         qw.eq(MeetInquiriesRecord::getMeetingOnlyId, meetingOnlyId);
         // 提交时间排序
         qw.orderByAsc(MeetInquiriesRecord::getSubmitTime);
        IPage<MeetInquiriesRecord> page = meetInquiriesRecordService.page(pageUtils.getPage(), qw);

        if (!page.getRecords().isEmpty()) {
            page.getRecords().stream().forEach(x -> {
                Integer userStatus = meetInquiriesRecordService.getUserStatus("im_user_quiries_" + x.getMeetingOnlyId()+ "_" + x.getPhone());
                x.setOnlineStatus(userStatus);
            });
        }

        return ResultFactory.success(page.getRecords(), page.getTotal());
    }

    /**
     * 会议 - 问诊记录列表修改保存
     *
     * @param meetInquiriesRecord
     * @return
     */
    @PostMapping("/save")
    @Transactional(rollbackFor = Throwable.class)
    public Result save(MeetInquiriesRecord meetInquiriesRecord) {
        // 查询会议
        MeetMeeting meetMeeting = meetMeetingService.getOne(new LambdaQueryWrapper<MeetMeeting>()
                .eq(MeetMeeting::getOnlyId, meetInquiriesRecord.getMeetingOnlyId()));
        // 开始时间
        if (LocalDateTime.now().isAfter(meetMeeting.getStartTime())) {
            throw new BaseException("会议已开始");
        }

        // 保证本次会议报名手机号唯一
        int count = meetInquiriesRecordService.count(new LambdaQueryWrapper<MeetInquiriesRecord>()
                // 手机号
                .eq(MeetInquiriesRecord::getPhone, meetInquiriesRecord.getPhone())
                // 会议id
                .eq(MeetInquiriesRecord::getMeetingOnlyId, meetInquiriesRecord.getMeetingOnlyId()));
        if (count > 0)
            throw new BaseException("手机号:" + meetInquiriesRecord.getPhone() + "已报名");
        // 会议排序
        count = meetInquiriesRecordService.count(new LambdaQueryWrapper<MeetInquiriesRecord>()
                // 会议id
                .eq(MeetInquiriesRecord::getMeetingOnlyId, meetInquiriesRecord.getMeetingOnlyId()));

        if (meetInquiriesRecord.getId() == null) {
            // 排序
            meetInquiriesRecord.setSort(count + 1);
            // 提交时间
            meetInquiriesRecord.setSubmitTime(LocalDateTime.now());
        }
        boolean bool = meetInquiriesRecordService.saveOrUpdate(meetInquiriesRecord);
        if (!bool)
            throw new BaseException("保存失败");
        return ResultFactory.success();
    }


    /**
     * 会议 - 问诊记录列验证
     *
     * @param phone 手机号
     * @param meetingOnlyId 会议Id
     * @return
     */
    @GetMapping("/verify")
    public Result verify(String phone, String meetingOnlyId) {
        // 验证是否为参会人员
        MeetInquiriesRecord meetInquiriesRecord = meetInquiriesRecordService.getOne(new LambdaQueryWrapper<MeetInquiriesRecord>()
                .eq(MeetInquiriesRecord::getPhone, phone)
                .eq(MeetInquiriesRecord::getMeetingOnlyId, meetingOnlyId));
        if (meetInquiriesRecord != null) {
            if (meetInquiriesRecord.getInterviewStatus().equals(MeetingInquiriesRecordinterviewStatusEnum.END.interviewStatus)) {
                throw new BaseException("您已结束问诊");
            }
            if (meetInquiriesRecord.getInterviewStatus().equals(MeetingInquiriesRecordinterviewStatusEnum.ABSENT.interviewStatus)) {
                    throw new BaseException("由于您未在规定时间在线,导师已把您设置为未到状态");
            }
        }
        // 0 没有参加此次会议 1 参加了会议
        return ResultFactory.success(meetInquiriesRecord == null ? 0 : 1);
    }

    /**
     * 会议 - 问诊状态修改
     *
     * @param meetInquiriesRecord 会议记录状态
     * @return
     */
    @PostMapping("/update")
    public Result update(MeetInquiriesRecord meetInquiriesRecord) {
        boolean bool = meetInquiriesRecordService.updateById(meetInquiriesRecord);
        if (!bool)
            throw new BaseException("保存失败");
        return ResultFactory.success();
    }


    /**
     * 会议 - 问诊记录详情
     *
     * @param phone 手机号
     * @param meetingOnlyId 会议Id
     * @return
     */
    @GetMapping("/show")
    public Result show(String phone, String meetingOnlyId) {
        MeetInquiriesRecord meetInquiriesRecord = meetInquiriesRecordService.getOne(new LambdaQueryWrapper<MeetInquiriesRecord>()
                .eq(MeetInquiriesRecord::getPhone, phone)
                .eq(MeetInquiriesRecord::getMeetingOnlyId, meetingOnlyId));

        // 查询在线问诊所有人员
        List<MeetInquiriesRecord> list = meetInquiriesRecordService.list(new LambdaQueryWrapper<MeetInquiriesRecord>()
                // 会议id
                .eq(MeetInquiriesRecord::getMeetingOnlyId, meetingOnlyId)
                // 人员状态: 10 未开始 20 问诊中 30 已结束 40未到人员
                .in(MeetInquiriesRecord::getInterviewStatus, MeetingInquiriesRecordinterviewStatusEnum.END.interviewStatus, MeetingInquiriesRecordinterviewStatusEnum.ABSENT.interviewStatus)
                // 排在前面的人数
                .lt(MeetInquiriesRecord::getSort, meetInquiriesRecord.getSort()));
        // 查询排位
        Integer count = meetInquiriesRecord.getSort();
        // 在线问诊
        if (list != null && !list.isEmpty()) {
            count = meetInquiriesRecord.getSort() - list.size();
        }

        meetInquiriesRecord.setRanking(count);
        // 排名在线状态刷新
        return ResultFactory.success(meetInquiriesRecord);
    }

    /**
     * 会议 - 问诊记录查询
     *
     * @param meetingOnlyId 会议id
     * @return
     */
    @GetMapping("/select")
    public Result select(String meetingOnlyId) {
        LambdaQueryWrapper<MeetInquiriesRecord> qw = Wrappers.lambdaQuery();
        // 会议id
        qw.eq(MeetInquiriesRecord::getMeetingOnlyId, meetingOnlyId);
        // 提交时间排序
        qw.orderByAsc(MeetInquiriesRecord::getSubmitTime);
        List<MeetInquiriesRecord> list = meetInquiriesRecordService.list(qw);
        if (!list.isEmpty()) {
            list.stream().forEach(x -> {
                Integer userStatus = meetInquiriesRecordService.getUserStatus("im_user_quiries_" + x.getMeetingOnlyId()+ "_" + x.getPhone());
                x.setOnlineStatus(userStatus);
            });
        }
        return ResultFactory.success(list);
    }


    /**
     * 会议 - 问诊记录删除
     *
     * @return id 多个逗号分割
     */
    @DeleteMapping("/delete")
    public Result delete(String id) {
        List<String> ids = Arrays.asList(id.split(","));
        boolean bool = meetInquiriesRecordService.removeByIds(ids);
        if (!bool)
            throw new BaseException("删除失败");
        // todo 1.移除trtc im房间

        return ResultFactory.success();
    }

    /**
     * 会议 - 根据手机号获取在线问诊
     *
     * @return phone 手机号
     */
    @GetMapping("/select/phone")
    public Result selectPhone(String phone) {
        // 查询问诊记录
        List<MeetInquiriesRecord> list = meetInquiriesRecordService.list(new LambdaQueryWrapper<MeetInquiriesRecord>()
                .eq(MeetInquiriesRecord::getPhone, phone));
        // 创建会议
        List<MeetMeeting> meetingList = new ArrayList<>();
        if (!list.isEmpty()) {
            // 查询会议信息
            meetingList = meetMeetingService.list(new LambdaQueryWrapper<MeetMeeting>()
                    .in(MeetMeeting::getOnlyId, list.stream().map(MeetInquiriesRecord::getMeetingOnlyId).collect(Collectors.toList())));
        }
        return ResultFactory.success(meetingList);
    }


}

