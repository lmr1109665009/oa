package com.suneee.eas.gateway.security;

import com.suneee.core.util.BeanUtils;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.gateway.utils.RoleServiceUtil;
import com.suneee.platform.model.system.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @user 子华
 * @created 2018/9/13
 */
public class UserPrincipal implements UserDetails, Serializable {
    private static final long serialVersionUID = 8705596688319545279L;
    private SysUser user;
    private static ThreadLocal<Collection<GrantedAuthority>> roleList=new ThreadLocal<Collection<GrantedAuthority>>();

    public UserPrincipal(SysUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(roleList.get()!=null) return roleList.get();
        if (ContextSupportUtil.getCurrentEnterprise()==null){
            return roleList.get();
        }
        Collection<GrantedAuthority> rtnList= new ArrayList<GrantedAuthority>();
        Collection<String> totalRoleCol= null;
        try {
            totalRoleCol = RoleServiceUtil.getRoleService().getRolesByUserId(user.getUserId(),ContextSupportUtil.getCurrentEnterpriseCode());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(BeanUtils.isNotEmpty(totalRoleCol)){
            for(String role:totalRoleCol){
                rtnList.add(new SimpleGrantedAuthority(role));
            }
        }
        roleList.set(rtnList);
        return rtnList;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getAccount();
    }

    @Override
    public boolean isAccountNonExpired() {
        if(user.getIsExpired()==null) return true;
        if (user.getIsExpired().shortValue() == SysUser.UN_EXPIRED.shortValue()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAccountNonLocked() {
        if(user.getIsLock()==null) return true;
        if (user.getIsLock().shortValue() == SysUser.UN_LOCKED.shortValue()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == SysUser.STATUS_OK ? true : false;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }
}
