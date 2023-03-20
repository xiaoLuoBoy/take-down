package com.horqian.api.service.sys;


import com.baomidou.mybatisplus.extension.service.IService;
import com.horqian.api.entity.sys.SysUser;

/**
 * @author bz
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 获取当前登录的用户
     * @return
     */
    SysUser getUser();

}
