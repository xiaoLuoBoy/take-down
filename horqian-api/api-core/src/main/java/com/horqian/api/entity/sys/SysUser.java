package com.horqian.api.entity.sys;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author bz
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     *id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     *用户名
     */
    @NotBlank(message = "账号不能为空")
    private String username;
    /**
     *密码
     */
    private String password;
    /**
     *昵称
     */
    @NotBlank(message = "姓名不能为空")
    private String nickname;
    /**
     *性别
     */
    private String sex;
    /**
     *电话
     */
    private String phone;
    /**
     *邮箱
     */
    private String email;
    /**
     *角色id
     */
    @NotNull(message = "角色不能为空")
    private Integer roleId;
    /**
     *角色名称
     */
    @NotBlank(message = "角色不能为空")
    private String roleName;

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

    /**
     * 用户头像
     */
    @TableField("avatar")
    private String avatar;


    @TableField(value = "is_delete", select = false)
    private Integer delete;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, select = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 权限
     */
    @TableField(exist = false)
    private String keywords;


}
