package com.suneee.platform.model.system;

import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.model.BaseModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 对象功能:用户中心用户表表 Model对象
 * 开发人员:hotent
 * 创建时间:2011-11-03 16:02:45
 */
public class SysUcUser  extends BaseModel implements UserDetails,ISysUser {

    @Override
    public Long getUserId() {
        return null;
    }

    @Override
    public String getFullname() {
        return null;
    }

    @Override
    public String getAccount() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
