package com.horqian.api.impl.sys;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horqian.api.context.UserContext;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.mapper.sys.SysUserMapper;
import com.horqian.api.service.sys.SysUserService;
import org.springframework.stereotype.Service;

/**
 * @author bz
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser getUser() {
        return this.baseMapper.selectById(UserContext.getContext());
    }

}
