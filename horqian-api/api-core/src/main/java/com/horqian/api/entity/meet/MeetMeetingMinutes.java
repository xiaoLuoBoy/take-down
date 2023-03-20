package com.horqian.api.entity.meet;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 会议 - 会议纪要
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("meet_meeting_minutes")
public class MeetMeetingMinutes implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 会议id
     */
    @TableField("meeting_only_id")
    private String meetingOnlyId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 用户名称
     */
    @TableField("user_name")
    private String userName;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 会议纪要
     */
    @TableField("summary")
    private String summary;

    @TableField(value = "is_delete", select = false)
    private Integer delete;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, select = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}
