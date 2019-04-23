package com.suneee.platform.web.listener;

import com.suneee.core.cache.ICache;
import com.suneee.core.sms.impl.ModemMessage;
import com.suneee.core.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 监控服务器启动和关闭事件。
 * <pre>
 * 1.服务器启动时，调用初始化系统模版事件。
 * 2.服务器关闭是，停止短信猫事件。
 * </pre>
 * @author ray
 *
 */
@WebListener
public class ServerListener implements ServletContextListener {
    	private Log logger = LogFactory.getLog(ServerListener.class);
	public void contextDestroyed(ServletContextEvent event) {
	    ModemMessage.getInstance().stopService();
	    logger.debug("[contextDestroyed]停止短信猫服务。");
	    
	    /**
		 * 清理缓存。
		 * 有可能是memcached缓存，在结束时需要清除缓存。
		 */
	    ICache icache= AppUtil.getBean(ICache.class);
		icache.clearAll();
	}

	public void contextInitialized(ServletContextEvent event) {

	}

}
