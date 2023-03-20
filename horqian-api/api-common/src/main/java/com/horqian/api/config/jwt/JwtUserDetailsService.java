package com.horqian.api.config.jwt;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author bz
 */
public interface JwtUserDetailsService extends UserDetailsService {

    /**
     * 查询系统用户
     *
     * @param userId
     * @return
     * @throws UsernameNotFoundException
     */
    JwtUserDetails loadUser(Integer userId) throws UsernameNotFoundException;
}
