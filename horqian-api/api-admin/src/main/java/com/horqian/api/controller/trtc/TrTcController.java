package com.horqian.api.controller.trtc;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.horqian.api.config.util.TencentcloudSendSms;
import com.horqian.api.dictionaries.meet.MeetingTypeEnum;
import com.horqian.api.entity.meet.MeetInquiriesVideo;
import com.horqian.api.entity.meet.MeetMeeting;
import com.horqian.api.entity.trtc.EventInfo;
import com.horqian.api.entity.trtc.TencentVod;
import com.horqian.api.entity.trtc.TrtcCallbackDto;
import com.horqian.api.exception.BaseException;
import com.horqian.api.result.Result;
import com.horqian.api.result.ResultFactory;
import com.horqian.api.service.meet.MeetInquiriesVideoService;
import com.horqian.api.service.meet.MeetMeetingService;
import com.horqian.api.util.CommonUtils;
import com.horqian.api.util.CosUtil;
import com.horqian.api.util.ObsUtil;
import com.horqian.api.util.WxTrTcUtil;
import com.tencentcloudapi.trtc.v20190722.models.CreateCloudRecordingResponse;
import com.tencentcloudapi.trtc.v20190722.models.DeleteCloudRecordingResponse;
import com.tencentcloudapi.trtc.v20190722.models.DescribeCloudRecordingResponse;
import com.tencentcloudapi.trtc.v20190722.models.ModifyCloudRecordingResponse;
import lombok.AllArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @author ：孟
 * @date ：Created 2023/1/10
 * @description： 腾讯云TrTc控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping("/trTc")
public class TrTcController {

    private final ObsUtil obsUtil;

    private final CosUtil cosUtil;

    private final WxTrTcUtil wxTrTcUtil;

    private final MeetMeetingService meetMeetingService;

    private final MeetInquiriesVideoService meetInquiriesVideoService;

    /**
     * 生成用户trtc签名
     * @param meetingUserId 生成对应的trtc签名
     * @return
     */
    @PostMapping("/createUserSig")
    @Transactional(rollbackFor = Throwable.class)
    public Result createUserSig(String meetingUserId) {
        String userSig = wxTrTcUtil.getUserSig(meetingUserId);
        return ResultFactory.success(userSig);
    }


    /**
     * 开始录制
     *
     * @param meetMeeting 会议 - 会议表
     */
    @PostMapping("/startRecord")
    @Transactional(rollbackFor = Throwable.class)
    public Result startRecord(@RequestBody MeetMeeting meetMeeting) {
        // 获取对应的枚举
        MeetingTypeEnum meetingTypeEnum = MeetingTypeEnum.meetingTypeEnum(meetMeeting.getType());

        String userSig = wxTrTcUtil.getUserSig(meetingTypeEnum.title + "_" + meetMeeting.getOnlyId());
        // 开始录制
        CreateCloudRecordingResponse createCloudRecordingResponse = wxTrTcUtil.startInterviewRecord(meetMeeting.getRoomId(),
                meetingTypeEnum.title + "_" + meetMeeting.getOnlyId(),
                userSig,
                meetMeeting.getType(),
                meetMeeting.getSubscribeAudioUserIds(),
                1,
                1,
                null,
                null);
        // 打印请求成功参数
        if (createCloudRecordingResponse != null) {
            // 创建开始录制
            System.out.println(createCloudRecordingResponse);
            MeetInquiriesVideo meetInquiriesVideo = new MeetInquiriesVideo();
            // 会议id
            meetInquiriesVideo.setMeetingOnlyId(meetMeeting.getOnlyId());
            // 手机号
            meetInquiriesVideo.setPhone(meetMeeting.getPhone());
            // 问诊记录id
            meetInquiriesVideo.setRecordId(meetMeeting.getRecordId());
            // 任务id
            meetInquiriesVideo.setTaskId(createCloudRecordingResponse.getTaskId());
            // 请求id
            meetInquiriesVideo.setRequestId(createCloudRecordingResponse.getRequestId());
            // 创建开始时间
            meetInquiriesVideo.setStartTime(LocalDateTime.now());

            boolean bool = meetInquiriesVideoService.save(meetInquiriesVideo);
            if (!bool)
                throw new BaseException("保存失败");
            // 同步给会议最新的taskID
            MeetMeeting meet = new MeetMeeting();
            meet.setId(meetMeeting.getId());
            meet.setTaskId(createCloudRecordingResponse.getTaskId());
            meetMeetingService.updateById(meet);
        }

        return ResultFactory.success(createCloudRecordingResponse.getTaskId());
    }

    /**
     * 更新屏幕录制
     * @param meetMeeting    会议模块
     * @return
     */
    @PostMapping("/updateRecord")
    @Transactional(rollbackFor = Throwable.class)
    public Result updateRecord(@RequestBody MeetMeeting meetMeeting) {
        // 更新屏幕录制
        ModifyCloudRecordingResponse modifyCloudRecordingResponse = wxTrTcUtil.updateRecord(meetMeeting.getTaskId(), meetMeeting.getSubscribeAudioUserIds());
        // 打印返回参数
        System.out.println(modifyCloudRecordingResponse);
        return ResultFactory.success();
    }


    /**
     * 查询录制状态
     *
     * @param taskId 任务id
     */
    @PostMapping("/getRecordStatus")
    @Transactional(rollbackFor = Throwable.class)
    public Result getRecordStatus(String taskId) {
        // 通过任务id查询视频录制状态
        DescribeCloudRecordingResponse recordStatus = wxTrTcUtil.getRecordStatus(taskId);
        if (recordStatus != null) {
            return ResultFactory.success(recordStatus.getStatus());
        } else {
            return ResultFactory.success();
        }
    }


    /**
     * 停止录制
     *
     * @param taskId 任务id
     */
    @PostMapping("/stopRecord")
    @Transactional(rollbackFor = Throwable.class)
    public Result stopRecord(String taskId) {
        DeleteCloudRecordingResponse deleteCloudRecordingResponse = wxTrTcUtil.stopRecord(taskId);
        if (deleteCloudRecordingResponse != null) {
            // 打印参数看请求结果
            System.out.println(deleteCloudRecordingResponse);
        }
        return ResultFactory.success();
    }

    /**
     * trtc 录制回调接口
     */
    @PostMapping("/callback")
    @Transactional(rollbackFor = Throwable.class)
    public Result callback(@RequestBody String body) throws IOException {
        TrtcCallbackDto trtcCallbackDto = JSON.parseObject(body, TrtcCallbackDto.class);

        Long eventType = trtcCallbackDto.getEventType();
        if (eventType == 311) {
            EventInfo eventInfo = trtcCallbackDto.getEventInfo();
            if (eventInfo != null) {
                // 获取任务id
                String taskId = eventInfo.getTaskId();
                LambdaQueryWrapper<MeetInquiriesVideo> qw = Wrappers.lambdaQuery();
                qw.eq(MeetInquiriesVideo::getTaskId, taskId);
                MeetInquiriesVideo meetInquiriesVideo = meetInquiriesVideoService.getOne(qw);
                if (meetInquiriesVideo != null) {
                    Long status = eventInfo.getPayload().getStatus();
                    if (status != null) {
                        if (status != 0) {
                            meetInquiriesVideo.setStatus(String.valueOf(status));
                            meetInquiriesVideo.setErrMsg(eventInfo.getPayload().getErrmsg());
                        } else {
                            TencentVod tencentVod = eventInfo.getPayload().getTencentVod();

                            File file = cosUtil.downloadFile(tencentVod.getVideoUrl(), ".mp4");
                            try {
                                String obs = cosUtil.uploadVideo(new MockMultipartFile("file", CommonUtils.getTimeStr() + ".mp4", "mp4",
                                        new FileInputStream(file)), ".mp4");
                                // 视频视频链接
                                meetInquiriesVideo.setVideoUrl(obs);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                file.delete();
                            }

                            meetInquiriesVideo.setStatus(String.valueOf(status));
                            meetInquiriesVideo.setVideoUrl(tencentVod.getVideoUrl());
                            meetInquiriesVideo.setFileId(tencentVod.getFileId());
                            // 创建结束时间
                            meetInquiriesVideo.setEndTime(LocalDateTime.now());

                            System.out.println("调用生成封面接口了要！！！！！！！！！！！！！！！！！！！！！！fileid：" + tencentVod.getFileId());
                            // 生成封面
                            wxTrTcUtil.getCoverUrl(tencentVod.getFileId());
                        }
                        meetInquiriesVideoService.updateById(meetInquiriesVideo);
                    }
                }
            }
        }
        return ResultFactory.success();
    }

    /**
     * trtc 房间与媒体回调
     */
    @PostMapping("/room/callback")
    @Transactional(rollbackFor = Throwable.class)
    public Result roomCallback(@RequestBody String body) throws IOException {
        TrtcCallbackDto trtcCallbackDto = JSON.parseObject(body, TrtcCallbackDto.class);
        EventInfo eventInfo = trtcCallbackDto.getEventInfo();
        Long eventType = trtcCallbackDto.getEventType();
        // TODO: 2023/1/30  服务器响应时间
        // trtc 超时重试 问题
        //
        // 事件回调服务器在发送消息通知后，5秒内没有收到您的服务器的响应，即认为通知失败。首次通知失败后会立即重试，后续失败会以10秒的间隔继续重试，直到消息存续时间超过1分钟，不再重试。
        // 查询会议
        MeetMeeting meetMeeting = meetMeetingService.getOne(new LambdaQueryWrapper<MeetMeeting>().eq(MeetMeeting::getRoomId, eventInfo.getRoomId()));
        // 创建房间
        if (eventType == 101) {
            if (!StringUtils.hasText(meetMeeting.getMeetingStartTime())) {
                meetMeeting.setMeetingStartTime(trtcCallbackDto.getCallbackTs().toString());
            } else {
                meetMeeting.setAgainMeetingTime(trtcCallbackDto.getCallbackTs().toString());
            }
            // 修改会议时长
            meetMeetingService.updateById(meetMeeting);
        }
        // 解散房间
        if (eventType == 102) {
            if (!StringUtils.hasText(meetMeeting.getMeetingEndTime())) {
                meetMeeting.setMeetingEndTime(trtcCallbackDto.getCallbackTs().toString());
            } else {
                // 会议结束时间戳
                Long time = trtcCallbackDto.getCallbackTs() - Long.valueOf(meetMeeting.getAgainMeetingTime()) + Long.valueOf(meetMeeting.getMeetingEndTime());
                // 会议结束时间
                meetMeeting.setMeetingEndTime(time.toString());
                // 会议再次开始时间置空
                meetMeeting.setAgainMeetingTime("");
            }
            // 修改会议时长
            meetMeetingService.updateById(meetMeeting);
        }
        return ResultFactory.success();
    }



    /**
     * 封面url回调
     *
     * @param body 参数
     * @return
     */
    @PostMapping("/callbackCoverUrl")
    @Transactional(rollbackFor = Throwable.class)
    public Result callbackCoverUrl(@RequestBody String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        String eventType = (String) jsonObject.get("EventType");

        if (eventType.equals("ProcedureStateChanged")) {
            // 如果是任务流状态变更
            JSONObject procedureStateChangeEvent = (JSONObject) jsonObject.get("ProcedureStateChangeEvent");
            String status = (String) procedureStateChangeEvent.get("Status");
            if (status.equals("FINISH")) {
                // 如果是已完成，则取出fileid
                String fileId = (String) procedureStateChangeEvent.get("FileId");
                // 取出封面
                JSONArray MediaProcessResultSetArray = (JSONArray) procedureStateChangeEvent.get("MediaProcessResultSet");
                Object MediaProcessResultSetObj = MediaProcessResultSetArray.get(0);
                if (MediaProcessResultSetObj != null) {
                    JSONObject MediaProcessResultSet = (JSONObject) MediaProcessResultSetObj;
                    // 如果type为采样图，则截取第一个作为封面
                    if (MediaProcessResultSet.get("Type").toString().equals("SampleSnapshot")) {
                        // 取出采样图
                        JSONObject sampleSnapshotTask = (JSONObject) MediaProcessResultSet.get("SampleSnapshotTask");
                        Integer errCode = (Integer) sampleSnapshotTask.get("ErrCode");
                        if (errCode.equals(0)) {
                            JSONObject output = (JSONObject) sampleSnapshotTask.get("Output");
                            JSONArray imageUrlSet = (JSONArray) output.get("ImageUrlSet");
                            // 获取生成封面
                            System.out.println(imageUrlSet.get(0));
                            LambdaQueryWrapper<MeetInquiriesVideo> qw = Wrappers.lambdaQuery();
                            qw.eq(MeetInquiriesVideo::getFileId, fileId);
                            MeetInquiriesVideo meetInquiriesVideo = meetInquiriesVideoService.getOne(qw);
                            File file = obsUtil.downloadFile(imageUrlSet.get(6).toString(), ".jpg");
                            try {
                                String obs = obsUtil.uploadFile(new MockMultipartFile("file", CommonUtils.getTimeStr() + ".mp4", "mp4",
                                        new FileInputStream(file)), "video", ".jpg");
                                // 视频视频链接
                                meetInquiriesVideo.setCover(obs);
                                // 修改封面
                                meetInquiriesVideoService.updateById(meetInquiriesVideo);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                file.delete();
                            }


                            // // 根据fileId取出面试或者笔试监控
                            // LambdaQueryWrapper<ExamPenAnswerSheetTrtc> examPenAnswerSheetTrtcLambdaQueryWrapper = new LambdaQueryWrapper<>();
                            // examPenAnswerSheetTrtcLambdaQueryWrapper.eq(ExamPenAnswerSheetTrtc::getFileId, fileId);
                            // List<ExamPenAnswerSheetTrtc> examPenAnswerSheetTrtcList = examPenAnswerSheetTrtcService.list(examPenAnswerSheetTrtcLambdaQueryWrapper);
                            // if (examPenAnswerSheetTrtcList != null && examPenAnswerSheetTrtcList.size() > 0) {
                            //     examPenAnswerSheetTrtcList.stream().forEach(examPenAnswerSheetTrtc -> {
                            //         Object urlObj = imageUrlSet.get(0);
                            //         if (urlObj != null) {
                            //             examPenAnswerSheetTrtc.setPenCoverUrl(urlObj.toString());
                            //         }
                            //     });
                            //     examPenAnswerSheetTrtcService.updateBatchById(examPenAnswerSheetTrtcList);
                            // }
                            //
                            //
                            // // 根据fileId取出面试监控列表
                            // LambdaQueryWrapper<ExamInterviewAnswerSheetTrtc> examInterviewAnswerSheetTrtcLambdaQueryWrapper = new LambdaQueryWrapper<>();
                            // examInterviewAnswerSheetTrtcLambdaQueryWrapper.eq(ExamInterviewAnswerSheetTrtc::getFileId, fileId);
                            // List<ExamInterviewAnswerSheetTrtc> examInterviewAnswerSheetTrtcList = examInterviewAnswerSheetTrtcService.list(examInterviewAnswerSheetTrtcLambdaQueryWrapper);
                            // if (examInterviewAnswerSheetTrtcList != null && examInterviewAnswerSheetTrtcList.size() > 0) {
                            //     examInterviewAnswerSheetTrtcList.stream().forEach(examInterviewAnswerSheetTrtc -> {
                            //         Object urlObj = imageUrlSet.get(1);
                            //         if (urlObj != null) {
                            //             examInterviewAnswerSheetTrtc.setInterviewCoverUrl(urlObj.toString());
                            //         }
                            //     });
                            //     examInterviewAnswerSheetTrtcService.updateBatchById(examInterviewAnswerSheetTrtcList);
                            // }
                        }
                    }
                }

            }
        }

        return ResultFactory.success();
    }


    /**
     * 会议 - 用户移除房间
     * @param roomId        房间id
     * @param phone         手机号
     * @param meetingOnlyId 会议id
     * @return
     */
    @PostMapping("/remove/user")
    public Result removeUser(String roomId, String phone, String meetingOnlyId) {
        String perspective2 = "im_user_quiries_" + meetingOnlyId+ "_" + phone;
        String[] userIds = {perspective2};
        wxTrTcUtil.removeUser(roomId, userIds);
        return ResultFactory.success();
    }


}
