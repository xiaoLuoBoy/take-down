package com.horqian.api.entity.meet;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 会议 - 问诊记录
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("meet_inquiries_record")
public class MeetInquiriesRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 会议id
     */
    @TableField("meeting_only_id")
    private String meetingOnlyId;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 性别1男2女
     */
    @TableField("sex")
    private Integer sex;

    /**
     * 联系方式
     */
    @TableField("phone")
    private String phone;

    /**
     * 提交时间
     */
    @TableField("submit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    /**
     * 微信唯一标识
     */
    @TableField("openid")
    private String openid;

    /**
     * 详情
     */
    @TableField("detail")
    private String detail;

    /**
     * 状态 10 未开始 20 问诊中 30 已结束 40未到人员
     */
    @TableField("interview_status")
    private Integer interviewStatus;

    @TableField(value = "is_delete", select = false)
    private Integer delete;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, select = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 在线状态 0否 1是
    @TableField(exist = false)
    private Integer onlineStatus = 0;

    // 当前排名
    @TableField(exist = false)
    private Integer ranking;
}
