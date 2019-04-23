/**
 * @Title: UserApi.java 
 * @Package com.suneee.eas.common.api.config 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.common.api.buc;

import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.constant.ServiceConstant;

/**
 * @ClassName: UserApi 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-01 14:13:29 
 *
 */
public class UserApi {

	/**
	 * 批量查询用户信息 
	 */ 
	public static final String batchFindUser = "/api/system/sysUserOrg/getUserListByIds.ht";
	
	/**
	 * 根据用户ID查询用户详情
	 */ 
	public static final String getUserDetails = ModuleConstant.USER_MODULE 
			+ FunctionConstant.USER + "details.ht";

	/**
	 * 获取用户的组织列表
	 */
	public static final String getOrgListByUserId = "/api/system/sysUserOrg/getOrgListByUserId.ht";

	/**
	 *获取当前企业下用户
	 */
	public static final String getCurrentEnterpriseUserList = ModuleConstant.USER_MODULE
			+ FunctionConstant.USER + "getCurrentEnterpriseUserList.ht";
	/**
	 * 根据账号获取用户岗位信息
	 */
	public static final String getPositionListByAccount = "/api/system/sysUserOrg/getUserPosition.ht";

	/**
	 * 根据账号获取用户信息
	 */
	public static final String getUserByAccount = ModuleConstant.USER_MODULE
			+ FunctionConstant.USER + "getByAccount.ht";

	/**批量查询组织信息
	 *
	 */
	public static final String batchFindOrg = "/api/system/sysUserOrg/getOrgListByIds.ht";

	/**
	 * 根据企业编码获取企业信息
	 */
	public static final String getEnterpriseByCompCode = ModuleConstant.USER_MODULE
			+ "/enterpriseinfo/getByCompCode.ht";

	/**
	 * 根据用户ID和企业编码获取用户与企业关系
	 */
	public static final String getUserEnterpriseByUserIdAndCode = ModuleConstant.USER_MODULE
			+ FunctionConstant.USER + "getByUserIdAndCode.ht";

	/**
	 *
	 */
	public static final String getEnterpriseListByUserId= ModuleConstant.USER_MODULE
			+ "/enterpriseinfo/getByUserId.ht";

	/**
	 * 根据用户id获取主岗位。
	 */
	public static final String getPrimaryPositionByUserId = ModuleConstant.BUC_PLATFORM
			+ "/position/getPrimaryPositionByUserId.ht";
	/**
	 * 根据用户id获取组织列表。
	 */
	public static final String getOrgByUserId = ModuleConstant.BUC_PLATFORMUSER
			+ "/getToUIdByOrg.ht";

	/**
	 * 根据用户id获取主组织。
	 */
	public static final String getPrimaryOrgByUserId = "/api/org/getPrimaryOrgByUserId.ht";



}
