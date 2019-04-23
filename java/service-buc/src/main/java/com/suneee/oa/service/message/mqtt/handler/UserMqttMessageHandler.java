/**
 *
 */
package com.suneee.oa.service.message.mqtt.handler;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.oa.model.user.UserEnterprise;
import com.suneee.oa.service.user.UserEnterpriseService;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.oa.service.message.mqtt.IMqttMessageHandler;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.oa.service.user.SysUserExtendService;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.service.system.SysUserExtService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 用户消息处理类
 *
 * @author xiongxianyun
 *
 */
public class UserMqttMessageHandler extends IMqttMessageHandler{
	@Resource
	private SysUserExtService sysUserExtService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private SysUserExtendService sysUserExtendService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	@Resource
	private UserEnterpriseService	userEnterpriseService;


	@Override
	public void handleMessage(JSONArray messagejsonArr, String operation) throws Exception{
		if(OPERATION_UPD.equals(operation)){
			//更新
			this.updateUser(messagejsonArr);
		} else if(OPERATION_ADD.equals(operation)){
			//新增
			this.addUser(messagejsonArr);
		} else if(OPERATION_DEL.equals(operation)){
			//删除
			this.removeUser(messagejsonArr);
		} else{
			LOGGER.info("没有条消息的操作类型，不做处理");
		}
	}

	/**
	 * 将json用户数据构造成本系统用户信息对象
	 * @param result
	 * @return
	 */
	private void constructUserinfo(SysUser sysUser, JSONObject result){
		if(sysUser == null || result == null){
			return;
		}
		Long ucUserId = result.get("userId")==null?null:Long.valueOf(result.get("userId").toString());
		String account = result.get("account")==null?null:result.get("account").toString();
		String phone = result.get("phone")==null?null:result.get("phone").toString();
		String mobile = result.get("mobile")==null?null:result.get("mobile").toString();
		String email = result.get("email")==null?null:result.get("email").toString();
		String password = result.get("password")==null?null:result.get("password").toString();
		String name = result.get("name")==null?null:result.get("name").toString();
		String aliasName = result.get("aliasName")==null?null:result.get("aliasName").toString();
		Boolean sex = result.get("sex")==null ? false : Boolean.valueOf(result.get("sex").toString());
		sysUser.setUcUserid(ucUserId);
		sysUser.setAccount(account);
		sysUser.setAliasName(aliasName);
		sysUser.setMobile(mobile);
		sysUser.setEmail(email);
		sysUser.setFullname(name);
		sysUser.setSex(sex ? "1" : "0");
		sysUser.setPhone(phone);
		sysUser.setPassword(password);
		sysUser.setUpdatetime(new Date());
		sysUser.setUpdateBy(-1L); // 表示非本系统入口修改
	}

	private void addUser(JSONArray array) throws Exception{
		if(null != array){
			for(Object obj:array){
				JSONObject result = JSONObject.fromObject(obj);
				String enterpriseCode = this.getEnCodeByKey(result, "enterpriseCode");
				// 查询用户所属的企业
				Enterpriseinfo enterpriseinfo = enterpriseinfoService.getByCompCode(enterpriseCode);
				if(null == enterpriseinfo){
					continue;
				}
				String inenterpriseCode = this.getEnCodeByKey(result, "inenterpriseCode");
				String groupCode = this.getEnCodeByKey(result, "groupCode");

				Long ucUserId = result.get("userId")==null?null:Long.valueOf(result.get("userId").toString());
				String account = result.get("account")==null?null:result.get("account").toString();
				String phone = result.get("phone")==null?null:result.get("phone").toString();
				String mobile = result.get("mobile")==null?null:result.get("mobile").toString();
				String email = result.get("email")==null?null:result.get("email").toString();
				String password = result.get("password")==null?null:result.get("password").toString();
				String name = result.get("name")==null?null:result.get("name").toString();
				String aliasName = result.get("aliasName")==null?null:result.get("aliasName").toString();
				Boolean sex = result.get("sex")==null ? false : Boolean.valueOf(result.get("sex").toString());
//                JSONObject registerTime = result.get("registerTime")==null?null:JSONObject.fromObject(result.get("registerTime"));
//                String time = registerTime.get("time")==null?null:registerTime.get("time").toString();

				if(StringUtils.isEmpty(account) && StringUtils.isEmpty(mobile)
						&&  StringUtils.isEmpty(email)){
					LOGGER.error("同步用户信息失败：account、mobile、email 都为空，"+result.toString());
					continue;
				}
				// 判断账号是否已经存在
				boolean isAccountExist = sysUserExtendService.isAccountExist(account,null);
				if(isAccountExist){
					LOGGER.error("同步用户信息失败：账号【" + account + "】已注册。");
					continue;
				}
				// 判断手机号是否已经存在
				boolean isMobileExist = sysUserExtendService.isMobileExist(mobile, null);
				if(isMobileExist){
					LOGGER.error("同步用户信息失败：手机号【" + mobile + "】已注册。");
					continue;
				}
				// 判断邮箱是否已经存在
				boolean isEmailExist = sysUserExtendService.isEmailExist(email, null);
				if(isEmailExist){
					LOGGER.error("同步用户信息失败： 邮箱【" + email + "】已注册。");
					continue;
				}
				SysUser user = new SysUser();
				user.setUpdatetime(new Date());
				user.setUpdateBy(-1L); // 表示非本系统入口修改
				user.setUcUserid(ucUserId);
				user.setAccount(account==null?mobile:account);
				user.setAliasName(aliasName);
				user.setMobile(mobile);
				user.setEmail(email);
				user.setFullname(name);
				user.setSex(sex?"1":"0");
				user.setPhone(phone);
				user.setPassword(password);
				user.setCreatetime(new Date());
				user.setSn(0l);


				user.setUserId(Long.valueOf(UniqueIdUtil.genId()));
				// 是否过期
				String isExpired = AppConfigUtil.get(Constants.USER_DEFAULT_ISEXPIRED, SysUser.UN_EXPIRED.toString());
				user.setIsExpired(Short.valueOf(isExpired));
				// 是否锁定
				String isLock = AppConfigUtil.get(Constants.USER_DEFAULT_ISLOCK, SysUser.UN_LOCKED.toString());
				user.setIsLock(Short.valueOf(isLock));
				// 员工状态
				String userStatus = AppConfigUtil.get(Constants.USER_DEFAULT_USERSTATUS);
				user.setUserStatus(userStatus);
				// 用户所属组织集团编码与账号的组合值：集团编码_account，主要用于解决定子链多企业账号重复问题
				String loginAccount = account;
				if(StringUtils.isNotBlank(enterpriseCode)){
					loginAccount = enterpriseCode + Constants.SEPARATOR_UNDERLINE + account;
				}
				user.setLoginAccount(loginAccount);
				sysUserService.add(user);
				//添加用户和企业关联
				/*Map<String, Object> map = new HashMap<>();
				map.put("enterpriseCode",enterpriseCode);
				map.put("userId",user.getUserId());
				map.put("createBy",ContextUtil.getCurrentUserId());
				map.put("createtime",new Date());
				sysUserService.saveUserAndEnterprise(map);*/
				UserEnterprise userEnterprise = new UserEnterprise();
				userEnterprise.setUserEnterpriseId(Long.valueOf(UniqueIdUtil.genId()));
				userEnterprise.setUserId(user.getUserId());
				userEnterprise.setCreateBy(ContextUtil.getCurrentUserId());
				userEnterprise.setEnterpriseCode(enterpriseCode);
				userEnterprise.setIsDelete((short) 0);
				userEnterpriseService.add(userEnterprise);

				//用户和角色关联

				//用户和组织关联
			}
		}
	}

	private void removeUser(JSONArray array) throws Exception{
		try {
			if(null != array){
				for(Object obj:array){
					JSONObject result = JSONObject.fromObject(obj);
//                    String inenterpriseCode = this.getEnCodeByKey(result, "inenterpriseCode");
//                    String groupCode = this.getEnCodeByKey(result, "groupCode");
					String enterpriseCode = this.getEnCodeByKey(result, "enterpriseCode");
					// 查询用户所属的所有企业
					Enterpriseinfo enterpriseinfo = enterpriseinfoService.getByCompCode(enterpriseCode);
					if(null == enterpriseinfo){
						continue;
					}
					Long ucUserId = result.get("userId")==null?null:Long.valueOf(result.get("userId").toString());
					SysUser user = sysUserExtService.getByUcUserid(ucUserId);
					if(null == user){
						continue;
					}
					sysUserService.updStatus(user.getUserId(), SysUser.STATUS_Del, null);
				}
			}
		} catch (Exception e) {
			LOGGER.error("删除用户信息失败," + e.getMessage(), e);
		}
	}

	private void updateUser(JSONArray array){
		SysUser user = null;
		JSONObject userJson = null;
		Pattern emailRegex = Pattern.compile(Constants.REGEX_EMAIL);
		Pattern mobileRegex = Pattern.compile(Constants.REGEX_MOBILE);
		for(Object jsonObj : array) {
			userJson = (JSONObject) jsonObj;
//			String enterpriseCode = this.getEnCodeByKey(userJson, "enterpriseCode");
//			// 查询用户所属的企业
//			Enterpriseinfo enterpriseinfo = enterpriseinfoService.getByCompCode(enterpriseCode);
//			if(null == enterpriseinfo){
//				continue;
//			}
			// 检查账号、手机号、邮箱是否为空
			String account = userJson.getString("account");
			String email = userJson.getString("email");
			String mobile = userJson.getString("mobile");
			if (StringUtils.isBlank(account) || StringUtils.isBlank(email) || StringUtils.isBlank(mobile)) {
				LOGGER.info("用户的账号、邮箱或手机号为空，不予处理：" + userJson.toString());
				continue;
			}
			user = sysUserExtService.getByUcUserid(userJson.getLong("userId"));
			if (user == null) {
				LOGGER.info("该用户信息在本系统中不存在或处于删除状态，不予处理：" + userJson.toString());
				continue;
			}
			// 判断账号的唯一性
			user = sysUserService.getByAccount(account);
			if (user != null && userJson.getLong("userId") != user.getUcUserid()) {
				LOGGER.info("该用户的账号在本系统中已经存在，不予处理：" + userJson.toString());
				continue;
			}
			// 判断邮箱的唯一性、格式
			if (!emailRegex.matcher(email).matches()) {
				LOGGER.info("邮箱格式错误，不予处理：" + userJson.toString());
				continue;
			}
			user = sysUserService.getByMail(email);
			if (user != null && userJson.getLong("userId") != user.getUcUserid()) {
				LOGGER.info("该用户的邮箱在本系统中已经存在，不予处理：" + userJson.toString());
				continue;
			}
			// 判断手机号的唯一性、格式
			if (!mobileRegex.matcher(mobile).matches()) {
				LOGGER.info("手机号码格式错误，不予处理：" + userJson.toString());
				continue;
			}
			user = sysUserService.getByMobile(mobile);
			if (user != null && userJson.getLong("userId") != user.getUcUserid()) {
				LOGGER.info("该用户的手机号在本系统中已经存在，不予处理：" + userJson.toString());
				continue;
			}
			constructUserinfo(user, userJson);
			sysUserExtService.updateByUcUserid(user);
		}
	}

	//获取企业Code
	private String getEnCodeByKey(JSONObject result, String key){
		String enterpriseCode = result.get(key)==null?null:result.get(key).toString();
		JSONArray enterpriseUserRoles = result.get("enterpriseUserRole")==null?null:JSONArray.fromObject(result.get("enterpriseUserRole"));
		JSONArray enterpriseUsers = result.get("enterpriseUser")==null?null:JSONArray.fromObject(result.get("enterpriseUser"));
		if(StringUtils.isEmpty(enterpriseCode)){
			enterpriseCode = this.getCode(enterpriseUserRoles, key);
		}
		if(StringUtils.isEmpty(enterpriseCode)){
			enterpriseCode = this.getCode(enterpriseUsers, key);
		}
		return enterpriseCode;
	}

	private String getCode(JSONArray array, String key){
		List<String> enterpriseCodes = new ArrayList<>();
		if(null != array){
			for(Object eu:array){
				JSONObject enterpriseUser = JSONObject.fromObject(eu);
				String enCode = enterpriseUser.get(key)==null?null:enterpriseUser.get(key).toString();
				if(StringUtils.isNotEmpty(enCode)){
					enterpriseCodes.add(enCode);
				}
			}
			if(enterpriseCodes.size() > 0){
				return enterpriseCodes.get(0);
			}
		}
		return null;
	}
}
