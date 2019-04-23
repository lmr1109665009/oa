package com.suneee.eas.gateway.security;

import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.gateway.component.AuthenticationProperty;
import com.suneee.eas.gateway.handler.user.SuneeeAccessDeniedHandler;
import com.suneee.eas.gateway.handler.user.SuneeeLogoutSuccessHandler;
import com.suneee.eas.gateway.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangyibo on 17/1/18.
 */


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;
    @Autowired
    private SuneeeSwitchEnterpriseFilter switchEnterpriseFilter;
    @Autowired
    private AuthenticationProperty authenticationProperty;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().permitAll()
                .and()
                //开启cookie保存用户数据
                .rememberMe()
                //设置cookie的私钥
                .key(SecurityUtil.rememberPrivateKey)
                .and()
                .logout().logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                //设置注销成功后跳转页面，默认是跳转到登录页面
                .logoutSuccessHandler(new SuneeeLogoutSuccessHandler());
        http.addFilterBefore(getXssFilter(),UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(switchEnterpriseFilter, SwitchUserFilter.class);
        http.addFilterBefore(switchEnterpriseFilter,SwitchUserFilter.class);
        http.exceptionHandling().accessDeniedHandler(new SuneeeAccessDeniedHandler());
        http.headers().frameOptions().sameOrigin();
        http.csrf().disable();
        http.sessionManagement().sessionAuthenticationStrategy(sessionAuthenticationStrategy);
    }

    @Bean("authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        String[] ignoreUrls=new String[authenticationProperty.getIgnoreUrls().size()];
        web.ignoring().antMatchers(authenticationProperty.getIgnoreUrls().toArray(ignoreUrls));
        web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

    /**
     * xss攻击过滤器
     * @return
     */
    public XssFilter getXssFilter(){
        XssFilter filter=new XssFilter();
        filter.setIngoreUrls(authenticationProperty.getXssIgnoreUrls());
        return filter;
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        return new DefaultHttpFirewall();
    }
}