package com.horqian.api.entity.meet;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 会议 - 会议表
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("meet_meeting")
public class MeetMeeting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 会议名称
     */
    @TableField("name")
    private String name;

    /**
     * 会议类型 10内部会议 20外部会议 30在线问诊
     */
    @TableField("type")
    private Integer type;

    /**
     * 状态 10未开始 20进行中 30已取消 40已结束
     */
    @TableField("status")
    private Integer status;

    /**
     * 会议id
     */
    @TableField("only_id")
    private String onlyId;

    /**
     * 会议描述
     */
    @TableField("detail")
    private String detail;

    /**
     * 开始时间
     */
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 会议主持人id
     */
    @TableField("host_user_id")
    private Integer hostUserId;

    /**
     * 会议主持人名称
     */
    @TableField("host_name")
    private String hostName;

    /**
     * 管理员id
     */
    @TableField("admin_user_id")
    private Integer adminUserId;

    /**
     * 管理员名称
     */
    @TableField("admin_user_name")
    private String adminUserName;

    /**
     * 创建用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 创建用户名称
     */
    @TableField("user_name")
    private String userName;

    /**
     * 文件路径
     */
    @TableField("url")
    private String url;

    /**
     * 会议开始时间
     */
    @TableField("meeting_start_time")
    private String meetingStartTime;

    /**
     * 会议结束时间
     */
    @TableField("meeting_end_time")
    private String meetingEndTime;

    /**
     * 本次会议开始时间
     */
    @TableField("again_meeting_time")
    private String againMeetingTime;

    /**
     * im群组id
     */
    @TableField("group_id")
    private String groupId;

    /**
     * trtc房间id
     */
    @TableField("room_id")
    private String roomId;

    /**
     * 取消原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 任务id
     */
    @TableField("task_id")
    private String taskId;

    /**
     * 会议创建时间
     */
    @TableField(value = "meeting_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime meetingTime;

    @TableField(value = "is_delete", select = false)
    private Integer delete;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, select = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 是否参会 0否 1是
    @TableField(exist = false)
    private Integer meetStatus = 1;

    // 参会人员列表
    @TableField(exist = false)
    private List<MeetParticipationUser> participationUserList;

    // 会议视频
    @TableField(exist = false)
    private MeetInquiriesVideo MeetInquiriesVideo;

    // 问诊记录id
    @TableField(exist = false)
    private Integer recordId;

    // 手机号
    @TableField(exist = false)
    private String phone;

    // 会议时间
    @TableField(exist = false)
    private Long often;

    // 订阅视频流白名单
    @TableField(exist = false)
    private List<String> subscribeAudioUserIds;



}
