package com.horqian.api.entity.meet;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 会议 - 问诊视频
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("meet_inquiries_video")
public class MeetInquiriesVideo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Integer id;

    /**
     * 会议id
     */
    @TableField("meeting_only_id")
    private String meetingOnlyId;

    /**
     * 问诊记录id
     */
    @TableField("record_id")
    private Integer recordId;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 视频链接
     */
    @TableField("video_url")
    private String videoUrl;

    /**
     * 封面图
     */
    @TableField("cover")
    private String cover;

    /**
     * 文件id
     */
    @TableField("file_id")
    private String fileId;

    /**
     * 任务id
     */
    @TableField("task_id")
    private String taskId;

    /**
     * 请求id
     */
    @TableField("request_id")
    private String requestId;

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
     * 0：代表本录制文件正常上传至点播平台
     * 1：代表本录制文件滞留在服务器或者备份存储上
     * 2：代表本录制文件上传点播任务异常
     */
    @TableField("status")
    private String status;

    /**
     * statue 不为0时，对应的错误信息
     */
    @TableField("err_msg")
    private String errMsg;

    @TableField(value = "is_delete", select = false)
    private Integer delete;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, select = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}
