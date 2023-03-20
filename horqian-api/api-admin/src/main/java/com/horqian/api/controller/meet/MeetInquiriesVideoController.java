package com.horqian.api.controller.meet;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.horqian.api.entity.meet.MeetInquiriesRecord;
import com.horqian.api.entity.meet.MeetInquiriesVideo;
import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.entity.meet.MeetParticipationUser;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.meet.MeetInquiriesRecordService;
import com.horqian.api.service.meet.MeetInquiriesVideoService;
import com.horqian.api.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 会议 - 问诊视频 前端控制器
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@RestController
@RequestMapping("/meet/inquiriesVideo")
@RequiredArgsConstructor
public class MeetInquiriesVideoController {

    private final MeetInquiriesVideoService meetInquiriesVideoService;

    /**
     * 会议 - 问诊视频列表
     *
     * @param pageUtils
     * @param meetingOnlyId 会议ID
     * @param phone 手机号
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<MeetInquiriesVideo> pageUtils, String meetingOnlyId, String phone) {
        LambdaQueryWrapper<MeetInquiriesVideo> qw = Wrappers.lambdaQuery();
        // 会议名称
        qw.like(MeetInquiriesVideo::getMeetingOnlyId, meetingOnlyId);
        // 手机号
        qw.like(StringUtils.hasText(phone), MeetInquiriesVideo::getPhone, phone);
        IPage<MeetInquiriesVideo> page = meetInquiriesVideoService.page(pageUtils.getPage(), qw);

        return ResultFactory.success(page.getRecords(), page.getTotal());
    }


    /**
     * 会议 - 问诊视频删除
     *
     * @return id 多个逗号分割
     */
    @DeleteMapping("/delete")
    public Result delete(String id) {
        List<String> ids = Arrays.asList(id.split(","));
        // 删除会议问诊 多个逗号分割
        boolean bool = meetInquiriesVideoService.removeByIds(ids);
        if (!bool)
            throw new BaseException("删除失败");

        return ResultFactory.success();
    }
}

