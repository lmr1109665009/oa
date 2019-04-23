/**
 * @Title: BpmApi.java 
 * @Package com.suneee.eas.common.api.config 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.common.api.bpmx;

import com.suneee.eas.common.constant.ServiceConstant;

/**
 * @ClassName: BpmApi 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-22 17:53:45 
 *
 */
public class BpmApi {
	/** 
	 * 根据场景分类id获取分类详情 
	 */ 
	public static final String getSceneTypeById =  "/oa/mobileScene/getSceneTypeById.ht";
	
	/**
	 * 获取所有场景分类
	 */ 
	public static final String getSceneTypeList =  "/oa/mobileScene/getSceneType.ht";
	
	/**
	 * 根据流程定义id获取流程定义
	 */ 
	public static final String getSceneDefByDefId =  "/oa/mobileScene/getSceneDefByDefId.ht";
	
	/**
	 * 根据defKey获取流程定义
	 */ 
	public static final String getDefByKey =  "/api/bpm/bpmDefinition/getDefByKey.ht";
	
	/**
	 * 根据场景runId获取流程实例
	 */ 
	public static final String getProcessRun =  "/weixin/bpm/getProcessRun.ht";
	
	/**
	 * 根据defId获取自定义表
	 */ 
	public static final String getBpmTableByDefId =  "/oa/mobileScene/getBpmTableByDefId.ht";
	
	/** 
	 * 获取流程表单json字符串数据 
	 */ 
	public static final String getBpmTableJson =  "/weixin/bpm/getBpmTableJson.ht";
	
	/** 
	 * 保存草稿
	 */ 
	public static final String saveForm =  "/oa/oaProcessRun/saveForm.ht";
}
