package com.horqian.api.entity.sys;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * @author bz
 */
@Data
@TableName("sys_role")
public class SysRole {

    /**
     * id
     */
    @TableId
    private Integer id;
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String name;
    /**
     * 角色描述
     */
    private String info;
    /**
     * 权限
     */
    private String keywords;
}
