package com.horqian.api.entity.meet;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 会议 - 参会人员表
 * </p>
 *
 * @author 孟
 * @since 2023-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("meet_participation_user")
public class MeetParticipationUser implements Serializable {

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
     * 账号
     */
    @TableField("user_name")
    private String username;

    /**
     * 用户姓名
     */
    @TableField("nick_name")
    private String nickname;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 部门id
     */
    @TableField("department_id")
    private Integer departmentId;

    /**
     * 部门名称
     */
    @TableField("department_name")
    private String departmentName;

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


}
