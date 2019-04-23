package com.suneee.oa.Exception;

/**
 * <pre>
 * 对象功能:自定义异常类（用于抛出提示信息）
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2011-12-21 13:16:13
 * </pre>
 */
public class TipInfoException extends RuntimeException{

	public TipInfoException(String message){
		super(message);
	}
}
