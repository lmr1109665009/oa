package com.suneee.platform.service.form.impl;

import javax.annotation.Resource;

import com.suneee.core.api.org.model.IPosition;
import com.suneee.core.api.org.model.ISysOrg;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.model.FieldPool;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.platform.model.form.BpmFormField;
import org.springframework.stereotype.Service;

import com.suneee.core.api.org.model.IPosition;
import com.suneee.core.api.org.model.ISysOrg;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.model.FieldPool;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.service.form.FormUtil;
import com.suneee.platform.service.form.IFormDataCalculate;
import com.suneee.platform.service.system.IdentityService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 表单计算数据。
 * @author ray
 *
 */
@Service
public class FormDataCalculateImpl implements IFormDataCalculate {
	
	@Resource
	private IdentityService identityService;
	

	@Override
	public Object calcShowForm(BpmFormField field, boolean isPreView) {
		field.setCalculated(true);
		if (field.getValueFrom() == BpmFormField.VALUE_FROM_IDENTITY) {
			// 如果actDefId=#dataTem 则说明是业务数据模板调用数据,需要立刻获取流水号
			String id=genIdentity(field,isPreView);
			return id;
		}else if (field.getValueFrom() == BpmFormField.VALUE_FROM_SCRIPT_SHOW) {
			Object result = FormUtil.calcuteField(field.getScript(), null, TableModel.CUSTOMER_COLUMN_PREFIX);
			return result;
		}
		else if (field.getControlType() == 15 || "date".equals(field.getFieldType())) {
			String rtn=calcDate(field);
			return rtn;
		}
		else if (field.getControlType().shortValue() == FieldPool.SELECTOR_USER_SINGLE) {
			String rtn=calcSingleUser(field);
			return rtn;
		} 
		else if (FieldPool.SELECTOR_ORG_SINGLE == field.getControlType().shortValue() ) {
			String rtn=calcOrg(field);
			return rtn;
		}
		else if (FieldPool.SELECTOR_POSITION_SINGLE == field.getControlType()) {
			String rtn=calcPos(field);
			return rtn;
		}
		else if (FieldPool.CHECKBOX == field.getControlType()) {//处理复选框的默认值
			JSONArray arr = JSONArray.fromObject(field.getOptions());
			String val = "";
			for(int i=0;i<arr.size();i++){
				JSONObject obj = arr.getJSONObject(i);
				if(obj.get("isDefault")==null||!obj.getString("isDefault").equals("1")){
					continue;
				}
				if(StringUtil.isNotEmpty(val)){
					val+=",";
				}
				val+=obj.getString("key");
			}
			return val;
		}
		else if (FieldPool.RADIO_INPUT == field.getControlType()||FieldPool.SELECT_INPUT == field.getControlType()) {//处理复选框的默认值
			if(StringUtil.isNotEmpty(field.getOptions())){
				JSONArray arr = JSONArray.fromObject(field.getOptions());
				for(int i=0;i<arr.size();i++){
					JSONObject obj = arr.getJSONObject(i);
					if(obj.get("isDefault")!=null&&obj.getString("isDefault").equals("1")){
						return obj.getString("key");
					}
				}
			}
		}
		field.setCalculated(false);
		return "";
	}
	
	
	
	private String calcPos(BpmFormField field){
		String prop = field.getCtlProperty();
		if(StringUtil.isEmpty(prop)) return "";
		
		JSONObject jsonObject = JSONObject.fromObject(prop);
		if (!jsonObject.containsKey("showCurPos")) return "";
		String showCurUser = JSONObject.fromObject(prop).getString("showCurPos");
		IPosition pos= ContextUtil.getCurrentPos();
		if(pos==null) return "";
		if (showCurUser.equals("1")) {
			if (field.getIsHidden() == 1) {
				return pos.getPosId().toString();
			} else {
				return pos.getPosName();
			}
		}
		return "";

	}
	
	private String calcOrg(BpmFormField field){
		String prop = field.getCtlProperty();
		if(StringUtil.isEmpty(prop)) return "";
		JSONObject jsonObject = JSONObject.fromObject(prop);
		if (!jsonObject.containsKey("showCurOrg")) return "";
		ISysOrg org=ContextUtil.getCurrentOrg();
		if(org==null) return "";
		
		String showCurUser = JSONObject.fromObject(prop).getString("showCurOrg");
		if (showCurUser.equals("1")) {
			if (field.getIsHidden() == 1) {
				return org.getOrgId().toString();
			} else {
				return org.getOrgName();
			}
		}
		return "";
	}
	
	private String calcSingleUser(BpmFormField field){
		String prop = field.getCtlProperty();
		if(StringUtil.isEmpty(prop)) return "";
		JSONObject jsonObject = JSONObject.fromObject(prop);
		if (!jsonObject.containsKey("showCurUser")) return "";
		
		ISysUser user=ContextUtil.getCurrentUser();
		String showCurUser = JSONObject.fromObject(prop).getString("showCurUser");
		if (showCurUser.equals("1")) {
			if (field.getIsHidden() == 1) {
				return user.getUserId().toString();
			} else {
				return user.getFullname();
			}
		}
		return "";
			
	}
	
	private String calcDate(BpmFormField field){
		String prop = field.getCtlProperty();
		// {"format":"yyyy-MM-dd","displayDate":1,"condition":"like"}
		if (StringUtil.isNotEmpty(prop)) {
			try {
				JSONObject jsonObject = JSONObject.fromObject(prop);
				if (jsonObject.containsKey("displayDate")) {
					String format = jsonObject.getString("format");
					String displayDate = jsonObject.getString("displayDate");
					if (displayDate.equals("1")) {
						return TimeUtil.getDateString(format);
					}
				}
			} catch (Exception ex) {
//				logger.debug(ex.getMessage());
			}
		}
		return "";
	}
	
	/**
	 * 是否预览，如果为预览不更新流水号。
	 * @param field
	 * @param isPreView
	 * @return
	 */
	private String genIdentity(BpmFormField field,boolean isPreView){
		if (isPreView) {
			String id = identityService.preview(field.getIdentity());
			return id;
		}  
		else {
			String prop=field.getCtlProperty();
			if (StringUtil.isEmpty(prop)) return "";
			JSONObject jsonObject=JSONObject.fromObject(prop);
			
			if(jsonObject.containsKey("isShowidentity")){
				String isShowidentity=jsonObject.getString("isShowidentity");
				//1.在前端显示流水号，0，在后端显示流水号。
				if ("1".equals(isShowidentity)) {
					String idNo = identityService.nextId(field.getIdentity());
					return idNo;
				}
			}
			return "";
		}
		
	}

}
