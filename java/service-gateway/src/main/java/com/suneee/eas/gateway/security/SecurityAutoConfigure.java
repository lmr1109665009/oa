package com.suneee.eas.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * spring security自动配置bean
 * @user 子华
 * @created 2018/9/12
 */
@Configuration
public class SecurityAutoConfigure{
    @Bean
    public SessionRegistry getSessionRegistry(){
        SessionRegistry sessionRegistry=new SessionRegistryImpl();
        return sessionRegistry;
    }
    @Bean
    public SessionAuthenticationStrategy getSessionAuthenticationStrategy(){
        SessionAuthenticationStrategy sessionAuthenticationStrategy=new ConcurrentSessionControlAuthenticationStrategy(getSessionRegistry());
        ((ConcurrentSessionControlAuthenticationStrategy) sessionAuthenticationStrategy).setMaximumSessions(1);
        return sessionAuthenticationStrategy;
    }
}