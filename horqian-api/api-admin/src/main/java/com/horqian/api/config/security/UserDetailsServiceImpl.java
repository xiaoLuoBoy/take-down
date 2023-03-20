package com.horqian.api.config.security;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.horqian.api.config.jwt.JwtUserDetails;
import com.horqian.api.config.jwt.JwtUserDetailsService;
import com.horqian.api.exception.BaseException;
import com.horqian.api.entity.sys.SysUser;
import com.horqian.api.service.sys.SysRoleService;
import com.horqian.api.service.sys.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author bz
 */
@Service
public class UserDetailsServiceImpl implements JwtUserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;


    @Override
    public JwtUserDetails loadUser(Integer userId) throws UsernameNotFoundException {

        var authorities = new ArrayList<GrantedAuthority>();

        var sysUser = sysUserService.getById(userId);

        if (sysUser == null) {
            throw new BaseException("用户名或密码错误");
        }

        var keywords = sysRoleService.getById(sysUser.getRoleId()).getKeywords();
        // 角色权限
        sysUser.setKeywords(keywords);
        Arrays.stream(keywords.split(",")).forEach(s -> authorities.add(new SimpleGrantedAuthority(s)));

        System.err.println(authorities);

        return new JwtUserDetails(sysUser, authorities);

    }

    @Override
    public JwtUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var authorities = new ArrayList<GrantedAuthority>();

        var sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username));

        if (sysUser == null) {
            throw new BaseException("用户名或密码错误");
        }

        var keywords = sysRoleService.getById(sysUser.getRoleId()).getKeywords();
        if (StringUtils.hasText(keywords)) {
            Arrays.stream(keywords.split(",")).forEach(s -> authorities.add(new SimpleGrantedAuthority(s)));
        }

        return new JwtUserDetails(sysUser, authorities);
    }
}
