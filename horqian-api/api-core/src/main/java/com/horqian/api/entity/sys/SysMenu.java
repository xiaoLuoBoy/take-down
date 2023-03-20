package com.horqian.api.entity.sys;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @author bz
 */
@Data
@TableName("sys_menu")
public class SysMenu {
    /**
     * id
     */
    private Integer id;
    /**
     * 菜单id
     */
    private Integer menuId;
    /**
     * 菜单排序
     */
    private Integer menuOrder;
    /**
     *名称
     */
    private String name;
    /**
     *标题
     */
    private String title;
    ///**
    // *图标
    // */
    //private String icon;
    /**
     *跳转
     */
    private String jump;


    private String perm;
    /**
     *父级id
     */
    private Integer pid;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private transient List<SysMenu> list;
}
