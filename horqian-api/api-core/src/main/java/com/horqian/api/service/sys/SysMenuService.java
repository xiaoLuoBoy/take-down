package com.horqian.api.service.sys;


import com.baomidou.mybatisplus.extension.service.IService;
import com.horqian.api.entity.sys.SysMenu;

/**
 * @author bz
 */
public interface SysMenuService extends IService<SysMenu> {

    SysMenu showRecursion(SysMenu sysMenu);

    boolean deleteRecursion(Integer id);
}
