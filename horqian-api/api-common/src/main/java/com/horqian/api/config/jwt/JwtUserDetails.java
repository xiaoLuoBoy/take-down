package com.horqian.api.config.jwt;


import com.horqian.api.util.CommonUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author bz
 */
@Data
public class JwtUserDetails implements UserDetails {


    private Object user;
    private final List<GrantedAuthority> permissionList;


    public <T> JwtUserDetails(T user, List<GrantedAuthority> permissionList) {
        this.user = user;
        this.permissionList = permissionList;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissionList;
    }

    @SneakyThrows
    public Integer getId() {
        return (Integer) CommonUtils.getFieldValue("id", this.user);
    }

    @SneakyThrows
    @Override
    public String getPassword() {
        return (String) CommonUtils.getFieldValue("password", this.user);
    }

    @SneakyThrows
    @Override
    public String getUsername() {
        return (String) CommonUtils.getFieldValue("username", user);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
