package com.suneee.eas.gateway.security;

import com.suneee.eas.gateway.service.SysRoleService;
import com.suneee.eas.gateway.service.SysUserService;
import com.suneee.platform.model.system.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * 实现spring security根据用户名查询用户接口
 * @user 子华
 * @created 2018/9/12
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysRoleService roleService;

    /**
     * 重写UserDetailService的接口
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user= null;
        try {
            user = userService.getByAccount(username);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        UserPrincipal principal=new UserPrincipal(user);
        return principal;
    }
}
