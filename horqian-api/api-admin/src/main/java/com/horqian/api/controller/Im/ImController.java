package com.horqian.api.controller.Im;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.horqian.api.dictionaries.meet.MeetingTypeEnum;
import com.horqian.api.entity.im.ImDto;
import com.horqian.api.entity.im.ImGroup;
import com.horqian.api.entity.im.InfoDto;
import com.horqian.api.entity.im.MemberList;
import com.horqian.api.entity.meet.MeetIminfo;
import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.entity.meet.MeetParticipationUser;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.meet.MeetIminfoService;
import com.horqian.api.service.meet.MeetMeetingService;
import com.horqian.api.service.meet.MeetParticipationUserService;
import com.horqian.api.util.WxTrTcUtil;
import com.tencentcloudapi.trtc.v20190722.models.DescribeCloudRecordingResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ：kk
 * @date ：Created 2022/8/31
 * @description： 腾讯云im控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping("/im")
public class ImController {

    private final WxTrTcUtil wxTrTcUtil;

    private final MeetIminfoService meetIminfoService;

    private final MeetMeetingService meetMeetingService;

    private final StringRedisTemplate stringRedisTemplate;

    private final MeetParticipationUserService meetParticipationUserService;


    /**
     * 创建游客唯一标识
     *
     * @return
     */
    @GetMapping("/create/uuid")
    public Result createUuid() {
        // 截取uuid
        String uuid = UUID.randomUUID().toString();
        String str = uuid.substring(3, 8) + uuid.substring(15, 17) + uuid.substring(20, 22);
        return ResultFactory.success(str,"");
    }

    /**
     * 获取在线用户
     *
     * @param groupId 分组id
     * @param meetingOnlyId 会议id
     * @return
     */
    @GetMapping("/room/user")
    public Result roomUser(String meetingOnlyId, String groupId) {
        // 移除不在线用户
        meetParticipationUserService.removeGroupUser(groupId);
        // 获取用户
        String userList = wxTrTcUtil.getUserList(groupId);
        ImGroup imGroup = JSON.parseObject(userList, ImGroup.class);
        // 参会人员
        Map<Integer, MeetParticipationUser> userMap = meetParticipationUserService.list(new LambdaQueryWrapper<MeetParticipationUser>()
                .eq(MeetParticipationUser::getMeetingOnlyId, meetingOnlyId)).stream().collect(Collectors.toMap(x -> x.getUserId(), Function.identity()));
        // im 分组
        if (imGroup != null &&  imGroup.getMemberList() != null) {
            imGroup.getMemberList().stream().forEach(x -> {
                String[] to_accountSpilt = x.getMember_Account().split("_");
                    // 获取用户id 12  asdfsdaf
                    String userId = to_accountSpilt[to_accountSpilt.length - 1];
                    try {
                        MeetParticipationUser meetParticipationUser = userMap.get(Integer.valueOf(userId));
                        x.setUsername(meetParticipationUser.getUsername());
                        x.setNickname(meetParticipationUser.getNickname());
                    } catch (Exception e) {
                        x.setNickname("游客" + userId);
                    }
            });
        }
        // 成员列表 按进群顺序排序
        List<MemberList> memberListList = imGroup.getMemberList().stream().sorted(Comparator.comparing(MemberList::getJoinTime)).collect(Collectors.toList());
        if (memberListList != null && !memberListList.isEmpty()) {
            // 成员列表
            memberListList = memberList(meetingOnlyId, memberListList);
        }
        return ResultFactory.success(memberListList);
    }

    /**
     * 获取在线用户人数
     *
     * @param meetingOnlyId 会议id
     * @return
     */
    @GetMapping("/user/number")
    public Result roomNumber(String meetingOnlyId) {

        // 用户人数
        MeetMeeting meetMeeting = meetMeetingService.getOne(new LambdaQueryWrapper<MeetMeeting>().eq(MeetMeeting::getOnlyId, meetingOnlyId));
        // 获取用户
        String userList = wxTrTcUtil.getUserList(meetMeeting.getGroupId());
        ImGroup imGroup = JSON.parseObject(userList, ImGroup.class);
        // 筛选人数
        List<MemberList> memberListList = new ArrayList<>();

        if (imGroup.getMemberList() != null && !imGroup.getMemberList().isEmpty()) {
            // 成员列表
            memberListList = memberList(meetingOnlyId, imGroup.getMemberList());
        }
        return ResultFactory.success(memberListList.size());
    }

    /**
     * im 回调接口
     */
    @PostMapping("/callback")
    public Result callback(@RequestBody String body) {
        ImDto imDto = JSON.parseObject(body, ImDto.class);
        System.out.println("im回调-------------------------------------------" + imDto);
        // 得到tcp链接状态
        InfoDto info = imDto.getInfo();
        // 得到用户id
        String to_account = info.getTo_Account();
        String[] to_accountSpilt = to_account.split("_");

        //  如果前缀是penRecord，则 如果有网络链接断开 和 网络链接超时   1.停止录制  2.如果有第二视角踢出第二视角用户
        // // 查询考生考试信息
        // LambdaQueryWrapper<ExamPenAnswerSheet> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // lambdaQueryWrapper.eq(ExamPenAnswerSheet::getExamMasterId, to_accountSpilt[1]);
        // lambdaQueryWrapper.eq(ExamPenAnswerSheet::getExamineeUserId, to_accountSpilt[2]);
        // ExamPenAnswerSheet examPenAnswerSheet = examPenAnswerSheetService.getOne(lambdaQueryWrapper);

        String action = info.getAction();
        // 用户上线登录
        if (action.equals("Login") && to_accountSpilt[1].equals("out")) {
            stringRedisTemplate.opsForHash().put("im_" + to_accountSpilt[3], to_accountSpilt[to_accountSpilt.length - 1], "1");
        }
        // im掉线或者退出发送离线消息
        if (action.equals("Logout") || action.equals("Disconnect")) {
            System.out.println("离线发送群组消息开始--------------------------------------------------------------------------------------");
            // 获取会议消息
            MeetMeeting meetMeeting = meetMeetingService.getOne(new LambdaQueryWrapper<MeetMeeting>().eq(MeetMeeting::getOnlyId, to_accountSpilt[3]));
            if (meetMeeting != null && StringUtils.isNotBlank(meetMeeting.getGroupId())) {
                MeetIminfo meetIminfo = new MeetIminfo();
                meetIminfo.setMeetingOnlyId(meetMeeting.getOnlyId());
                meetIminfo.setUserType(2);
                meetIminfo.setSender(info.getTo_Account());
                meetIminfo.setSenderName("离线人员")                                                                                                                                                             ;
                meetIminfo.setReceiver(meetMeeting.getOnlyId());
                meetIminfo.setReceiverName("管理员");
                meetIminfo.setSendContent("underline");
                // 发送离线消息
                wxTrTcUtil.sendMsgGroupId(meetMeeting.getGroupId(), JSON.toJSON(meetIminfo).toString());
                System.out.println("离线发送群组消息完毕--------------------------------------------------------------------------------------");
                // 下线踢出群成员
                wxTrTcUtil.removeGroupUser(meetMeeting.getGroupId(), to_account);
                // 如果掉线的是专家结束本次录制
                // 会议类型 10内部会议 20外部会议 30在线问诊
                if (to_accountSpilt[2].equals(MeetingTypeEnum.INQUIRIES.title) && to_accountSpilt[1].equals("admin")) {
                    // 查询录制任务状态
                    DescribeCloudRecordingResponse recordStatus = wxTrTcUtil.getRecordStatus(meetMeeting.getTaskId());
                    if (recordStatus.getStatus().equals("InProgress"))
                        wxTrTcUtil.stopRecord(meetMeeting.getTaskId());
                }
                // 外部用户下线
                if (to_accountSpilt[1].equals("out")) {
                    // 游客
                    stringRedisTemplate.opsForHash().delete("im_" + to_accountSpilt[3], to_accountSpilt[to_accountSpilt.length - 1]);
                }}
        }
        return ResultFactory.success();
    }

    /**
     * 过滤筛选 im人数
     * @param meetingOnlyId
     * @param memberListList
     * @return
     */
    public List<MemberList> memberList(String meetingOnlyId, List<MemberList> memberListList) {
        // 用户人数
        MeetMeeting meetMeeting = meetMeetingService.getOne(new LambdaQueryWrapper<MeetMeeting>().eq(MeetMeeting::getOnlyId, meetingOnlyId));
        // 获取对应人数枚举
        MeetingTypeEnum meetingTypeEnum = MeetingTypeEnum.meetingTypeEnum(meetMeeting.getType());
        // 成员列表
        List<MemberList> memberListArrayList = new ArrayList<>();
        // 对人员进行对比
        memberListList.stream().forEach(x -> {
            // 截取userId
            String[] account = x.getMember_Account().split("_");
            // 会议类型不相同
            if (!account[2].equals(meetingTypeEnum.title) || !account[3].equals(meetingOnlyId)) {
                // 把成员踢出房间
                wxTrTcUtil.removeGroupUser(meetMeeting.getGroupId(), x.getMember_Account());
            } else {
                memberListArrayList.add(x);
            }
        });

        return memberListArrayList;
    }


}
