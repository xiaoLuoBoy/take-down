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
 * 会议 -  im即时消息
 * </p>
 *
 * @author 孟
 * @since 2023-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("meet_iminfo")
public class MeetIminfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 会议id
     */
    @TableField("meeting_only_id")
    private String meetingOnlyId;

    /**
     * 会议类型 10内部会议 20外部会议 30在线问诊
     */
    @TableField("type")
    private Integer type;

    /**
     * 用户类型 1管理员 2用户
     */
    @TableField("user_type")
    private Integer userType;

    /**
     * 接收方
     */
    @TableField("receiver")
    private String receiver;

    /**
     * 接收方姓名
     */
    @TableField("receiver_name")
    private String receiverName;

    /**
     * 发送方
     */
    @TableField("sender")
    private String sender;

    /**
     * 发送方姓名
     */
    @TableField("sender_name")
    private String senderName;

    /**
     * 发送内容
     */
    @TableField("send_content")
    private String sendContent;

    /**
     * 发送时间
     */
    @TableField("send_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    /**
     *  发送类型 0文字 1附件 
     */
    @TableField("send_type")
    private Integer sendType;

    @TableField(value = "is_delete", select = false)
    private Integer delete;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, select = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;



}
