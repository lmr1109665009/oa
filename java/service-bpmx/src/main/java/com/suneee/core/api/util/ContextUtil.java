package com.suneee.core.api.util;

import com.suneee.core.api.org.ICurrentContext;
import com.suneee.core.api.org.model.IPosition;
import com.suneee.core.api.org.model.ISysOrg;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.util.AppUtil;
import com.suneee.eas.common.api.buc.UserApi;
import com.suneee.eas.common.constant.ServiceConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RestTemplateUtil;
import com.suneee.ucp.base.model.system.Position;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Locale;


public class ContextUtil {
	
	public static final String CurrentOrg="CurrentOrg_";
	public static final String CurrentCompany ="CurrentCompany_";
	
	public static final String CurrentPos="CurrentPos_";

	
	public static String getPositionKey(Long userId){
		String posKey=ContextUtil.CurrentPos+ContextSupportUtil.getCurrentEnterpriseCode()+"_"+ userId;
		return posKey;
	}
	
	public static String getOrgKey(Long userId){
		String orgKey=ContextUtil.CurrentOrg +ContextSupportUtil.getCurrentEnterpriseCode()+"_"+ userId;
		return orgKey;
	}
	
	public static String getCompanyKey(Long userId){
		String orgKey=ContextUtil.CurrentCompany +ContextSupportUtil.getCurrentEnterpriseCode()+"_"+ userId;
		return orgKey;
	}
	
	private static ICurrentContext context_;
	
	public synchronized static ICurrentContext getContext(){
		if(context_==null){
			context_= AppUtil.getBean(ICurrentContext.class);
		}
		return context_;
	}
	
	
	
	/**
	 * 获取当前用户。
	 * @return
	 */
	public static ISysUser getCurrentUser(){
		ICurrentContext context=getContext();
		ISysUser user= context.getCurrentUser();
		if (user!=null){
			return user;
		}
		return ContextSupportUtil.getCurrentUser();
	}
	
	/**
	 * 获取local对象。
	 * @return
	 */
	public static  Locale getLocale(){
		ICurrentContext context=getContext();
		return context.getLocale();
	}
	
	/**
	 * 设置local对象。
	 * @param locale
	 */
	public static  void setLocale(Locale locale){
		ICurrentContext context=getContext();
		context.setLocale(locale);
	}
	
	/**
	 * 获取当前用户ID。
	 * @return
	 */
	public static   Long getCurrentUserId(){
		ICurrentContext context=getContext();
		Long userId=context.getCurrentUserId();
		if (userId!=null){
			return userId;
		}
		return ContextSupportUtil.getCurrentUserId();
	}
	
	/**
	 * 设置当前用户帐号。
	 * @param account
	 */
	public static void setCurrentUserAccount(String account){
		ICurrentContext context=getContext();
		context.setCurrentUserAccount(account);
	}
	
	/**
	 * 设置当前用户。
	 * @param sysUser
	 */
	public static void setCurrentUser(ISysUser sysUser){
		ICurrentContext context=getContext();
		context.setCurrentUser(sysUser);
	}
	
	/**
	 * 设置当前岗位ID.
	 * @param posId
	 */
	public static void setCurrentPos(Long posId){
		ICurrentContext context=getContext();
		context.setCurrentPos(posId);
	}
	
	/**
	 * 获取当前组织。
	 * @return
	 */
	public static ISysOrg getCurrentOrg(){
		ICurrentContext context=getContext();
		ISysOrg org=context.getCurrentOrg();
		if (org!=null){
			return org;
		}
		try {
			MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
			params.add("userId",ContextSupportUtil.getCurrentUserId());
			Position position= RestTemplateUtil.get(ServiceConstant.getBucServiceUrl() + UserApi.getPrimaryPositionByUserId, Position.class,params);
			if (position!=null){
				context.setCurrentPos(position.getPosId());
				org=context.getCurrentOrg();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return org;
	}
	
	/**
	 * 获取当前公司。
	 * @return
	 */
	public static ISysOrg getCurrentCompany(){
		ICurrentContext context=getContext();
		return context.getCurrentCompany();
	}
	
	/**
	 * 获取当前公司。
	 * @return
	 */
	public static Long getCurrentCompanyId(){
		ICurrentContext context=getContext();
		return context.getCurrentCompanyId();
	}
	
	/**
	 * 获取当前岗位。
	 * @return
	 */
	public static IPosition getCurrentPos(){
		ICurrentContext context=getContext();
		IPosition position=context.getCurrentPos();
		if(position!=null){
			return position;
		}
		try {
			MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
			params.add("userId",ContextSupportUtil.getCurrentUserId());
			position= RestTemplateUtil.get(ServiceConstant.getBucServiceUrl() + UserApi.getPrimaryPositionByUserId, Position.class,params);
			if (position!=null){
				context.setCurrentPos(position.getPosId());
				position=context.getCurrentPos();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return position;
	}
	
	/**
	 * 获取当前岗位ID。
	 * @return
	 */
	public static Long getCurrentPosId(){
		ICurrentContext context=getContext();
		Long posId=context.getCurrentPosId();
		if (posId!=null){
			return posId;
		}
		try {
			MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
			params.add("userId",ContextSupportUtil.getCurrentUserId());
			Position position= RestTemplateUtil.get(ServiceConstant.getBucServiceUrl() + UserApi.getPrimaryPositionByUserId, Position.class,params);
			if (position!=null){
				context.setCurrentPos(position.getPosId());
				posId=context.getCurrentPosId();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return posId;
	}
	
	/**
	 * 获取当前组织ID。
	 * @return
	 */
	public static Long getCurrentOrgId(){
		ICurrentContext context=getContext();
		Long orgId=context.getCurrentOrgId();
		if (orgId!=null){
			return orgId;
		}
		try {
			MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
			params.add("userId",ContextSupportUtil.getCurrentUserId());
			Position position= RestTemplateUtil.get(ServiceConstant.getBucServiceUrl() + UserApi.getPrimaryPositionByUserId, Position.class,params);
			if (position!=null){
				context.setCurrentPos(position.getPosId());
				orgId=context.getCurrentOrgId();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return orgId;
	}
	
	/**
	 * 获取当前皮肤。
	 * @param request
	 * @return
	 */
	public static String getCurrentUserSkin(HttpServletRequest request){
		ICurrentContext context=getContext();
		return context.getCurrentUserSkin(request);
	}
	
	/**
	 * 清除当前用户。
	 */
	public static void cleanCurUser(){
		ICurrentContext context=getContext();
		context.cleanCurUser();
	}
	
	/**
	 * 清除当前组织。
	 */
	public static void removeCurrentOrg(){
		ICurrentContext context=getContext();
		context.removeCurrentOrg();
	}
	
	/**
	 * 清除上下文组织。
	 */
	public static void clearAll(){
		ICurrentContext context=getContext();
		context.clearAll();
	}
	
	/**
	 * 清除当前用户。
	 */
	public static void removeCurrentUser(){
		ICurrentContext context=getContext();
		context.removeCurrentUser();
	}

	/**
	 * 当前人是否超级管理员。
	 * @return
	 */
	public static boolean isSuperAdmin(){
		return ContextSupportUtil.isSuperAdmin();
	}

	public static boolean isSuperAdmin(ISysUser user){
		return ContextSupportUtil.isSuperAdmin(user);
	}

}
