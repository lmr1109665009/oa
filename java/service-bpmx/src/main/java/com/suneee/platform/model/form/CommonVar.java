package com.suneee.platform.model.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.core.api.util.ContextUtil;

/**
 * 常用变量
 * @author zxh
 *
 */
public class CommonVar {
	
	//字段名
	private String name;
	//字段值
	private String alias;
	
	private Object value;
	
	/**
	 * 数字类型。
	 */
	private boolean isNumber=false;
	

	public CommonVar() {
	}
	
	public CommonVar(String name, String alias, Object value,boolean isNumber_) {
		this.name = name;
		this.alias = alias;
		this.value = value;
		this.isNumber=isNumber_;
	}
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	
	public boolean isNumber() {
		return isNumber;
	}

	public void setNumber(boolean isNumber) {
		this.isNumber = isNumber;
	}

	/**
	 * 获取当前系统变量。
	 * @param isRunTime	是否运行时，非运行时用户参数的设置。
	 * @return
	 */
	public static List<CommonVar> getCurrentVars(boolean isRunTime){
		List<CommonVar> list=new ArrayList<CommonVar>();
		Long userId=0L;
		Long orgId=0L;
		Long companyId=0L;
		Long posId=0L;
		String account="";
		if(isRunTime){
			SysUser user=(SysUser) ContextUtil.getCurrentUser();
			userId=user.getUserId();
			account=user.getAccount();
			companyId=ContextUtil.getCurrentCompanyId();
			orgId=ContextUtil.getCurrentOrgId();
			posId=ContextUtil.getCurrentPosId();
		}

		CommonVar varUser=new CommonVar("当前用户ID", "[CUR_USER]", userId,true);
		CommonVar varAccount=new CommonVar("当前用户帐号", "[CUR_ACCOUNT]", account,false);
		CommonVar varCompany=new CommonVar("当前公司", "[CUR_COMPANY]", companyId,true);
		CommonVar varOrg=new CommonVar("当前组织", "[CUR_ORG]", orgId,true);
		CommonVar varPos=new CommonVar("当前岗位", "[CUR_POS]", posId,true);
		
		list.add(varUser);
		list.add(varAccount);
		list.add(varCompany);
		list.add(varOrg);
		list.add(varPos);
		
		return list;
		
	}
	
	/**
	 * 设置当前变量。
	 * @param vars
	 */
	public static void setCurrentVars(Map<String,Object> vars){
		List<CommonVar> commonVars= getCurrentVars(true);
		for(CommonVar var:commonVars){
			vars.put(var.getAlias(), var.getValue());
		}
	}
	
	

	
	
}
