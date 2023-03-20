package com.horqian.api.service.sys;


import com.baomidou.mybatisplus.extension.service.IService;
import com.horqian.api.entity.sys.SysRole;

/**
 * @author bz
 */
public interface SysRoleService extends IService<SysRole> {
    /**
     * 处理冗余字段
     *
     * @param sysRole
     */
    void dealRelate(SysRole sysRole);

}
