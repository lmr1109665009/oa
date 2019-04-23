/**
 * @Title: StatusConstant.java 
 * @Package com.suneee.eas.common.constant 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.common.constant;

/**
 * @ClassName: StatusConstant 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-07-31 11:38:23 
 *
 */
public class StatusConstant {
	/**
	 * 删除标志：1=已删除 
	 */ 
	public static final Byte DELETE_YES = 1;
	/**
	 * 删除标志：0=未删除 
	 */
	public static final Byte DELETE_NO = 0;

	/**
	 * 状态：0=未提交 
	 */ 
	public static final Byte STATUS_NOT_SUBMIT = 0;
	/**
	 * 状态：1=草稿 
	 */ 
	public static final Byte STATUS_DRAFT = 1;
	/**
	 * 状态：2=待审批 
	 */ 
	public static final Byte STATUS_PENDING = 2;
	/**
	 * 状态：3=审批中
	 */ 
	public static final Byte STATUS_PROCESSING = 3;
	/**
	 * 状态：4=已批准
	 */ 
	public static final Byte STATUS_PASS = 4;
	/**
	 * 状态：5=驳回
	 */ 
	public static final Byte STATUS_REJECT = 5;
	/**
	 * 状态：6=取消 
	 */ 
	public static final Byte STATUS_CANCEL = 6;
}
