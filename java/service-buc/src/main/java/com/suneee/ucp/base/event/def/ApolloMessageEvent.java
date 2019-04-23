/**
 * 
 */
package com.suneee.ucp.base.event.def;

import org.springframework.context.ApplicationEvent;

/**
 * 定子链消息事件
 * @author xiongxianyun
 *
 */
public class ApolloMessageEvent extends ApplicationEvent{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6314902613150904950L;

	private ApolloMessage apolloMessage;
	
	/**
	 * @param source
	 */
	public ApolloMessageEvent(ApolloMessage apolloMessage) {
		super(apolloMessage);
		this.apolloMessage = apolloMessage;
	}
	
	/**
	 * @return the apolloMessage
	 */
	public ApolloMessage getApolloMessage() {
		return apolloMessage;
	}
	
	/**
	 * @param apolloMessage the apolloMessage to set
	 */
	public void setApolloMessage(ApolloMessage apolloMessage) {
		this.apolloMessage = apolloMessage;
	}
}
