package com.horqian.api.controller.meet;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.horqian.api.dictionaries.meet.MeetingTypeEnum;
import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.entity.meet.MeetParticipationUser;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.meet.MeetInquiriesRecordService;
import com.horqian.api.service.meet.MeetMeetingService;
import com.horqian.api.service.meet.MeetParticipationUserService;
import com.horqian.api.util.PageUtils;
import com.horqian.api.util.WxTrTcUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 会议 - 参会人员表 前端控制器
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@RestController
@RequestMapping("/meet/participationUser")
@RequiredArgsConstructor
public class MeetParticipationUserController {

    private final MeetParticipationUserService meetParticipationUserService;

    private final MeetInquiriesRecordService meetInquiriesRecordService;

    private final StringRedisTemplate stringRedisTemplate;

    private final MeetMeetingService meetMeetingService;

    private final WxTrTcUtil wxTrTcUtil;


    /**
     * 会议 - 参会人员表
     *
     * @param pageUtils
     * @param meetingOnlyId 会议id
     * @param type          会议类型
     * @return
     */
    @GetMapping("/list")
    public Result list(PageUtils<MeetParticipationUser> pageUtils, String meetingOnlyId, Integer type) {
        // 查询会议
        MeetMeeting meetMeeting = meetMeetingService.getOne(new LambdaQueryWrapper<MeetMeeting>().eq(MeetMeeting::getOnlyId, meetingOnlyId));

        meetParticipationUserService.removeGroupUser(meetMeeting.getGroupId());

        LambdaQueryWrapper<MeetParticipationUser> qw = Wrappers.lambdaQuery();
        // 会议id
        qw.eq(StringUtils.hasText(meetingOnlyId), MeetParticipationUser::getMeetingOnlyId, meetingOnlyId);
        // 分页查询
        IPage<MeetParticipationUser> page = meetParticipationUserService.page(pageUtils.getPage(), qw);
        if (!page.getRecords().isEmpty()) {
            // 会议类型枚举
            MeetingTypeEnum meetingTypeEnum = MeetingTypeEnum.meetingTypeEnum(type);
            page.getRecords().stream().forEach(x -> {
                // 生成im密钥
                Integer userStatus = meetInquiriesRecordService.getUserStatus("im_in_" + meetingTypeEnum.title + "_" + x.getMeetingOnlyId() + "_" + x.getUserId());
                x.setOnlineStatus(userStatus);
            });
        }

        return ResultFactory.success(page.getRecords(), page.getTotal());
    }

    /**
     * 会议 - 游客
     *
     * @param meetingOnlyId 会议id
     * @return
     */
    @GetMapping("/online")
    public Result onlinePersonnel(Integer limit, Integer page, String meetingOnlyId) {
        // 查询会议
        MeetMeeting meetMeeting = meetMeetingService.getOne(new LambdaQueryWrapper<MeetMeeting>().eq(MeetMeeting::getOnlyId, meetingOnlyId));
        meetParticipationUserService.removeGroupUser(meetMeeting.getGroupId());

        Set<Object> keys = stringRedisTemplate.opsForHash().keys("im_" + meetingOnlyId);
        // 内部会议 外部会议 游客列表
        List<Object> list = keys.stream().skip((page - 1) * limit).limit(limit).
                collect(Collectors.toList());
        // 参会人员列表
        List<MeetParticipationUser> meetParticipationUsers = new ArrayList<>();
        if (!list.isEmpty() && list != null) {
            list.stream().forEach(x ->{
                MeetParticipationUser meetParticipationUser = new MeetParticipationUser();
                meetParticipationUser.setNickname("游客"+ x.toString());
                meetParticipationUser.setOnlineStatus(1);
                meetParticipationUsers.add(meetParticipationUser);
            });
        }
        return ResultFactory.success(meetParticipationUsers, keys.size());
    }



}

