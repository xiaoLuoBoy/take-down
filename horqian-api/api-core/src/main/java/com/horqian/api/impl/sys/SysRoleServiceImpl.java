package com.horqian.api.impl.sys;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horqian.api.entity.sys.SysRole;
import com.horqian.api.mapper.sys.SysRoleMapper;
import com.horqian.api.service.sys.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author bz
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;

    @Autowired
    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }


    @Override
    public void dealRelate(SysRole sysRole) {
        sysRoleMapper.dealRelate(sysRole);
    }

}
