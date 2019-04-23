package com.suneee.core.web.listener;

import com.suneee.core.util.ClassLoadUtil;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppServletContextListener extends ContextLoaderListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ClassLoadUtil.contextInitialized(event);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}