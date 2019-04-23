package com.suneee.platform.event.listener;

import java.util.List;

import javax.annotation.Resource;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.WorkFlowException;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.event.def.TriggerNewFlowEvent;
import com.suneee.platform.event.def.TriggerNewFlowModel;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.WorkFlowException;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.event.def.TriggerNewFlowEvent;
import com.suneee.platform.event.def.TriggerNewFlowModel;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.BpmNewFlowTrigger;
import com.suneee.platform.model.bpm.BpmUserCondition;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmNewFlowTriggerService;
import com.suneee.platform.service.bpm.BpmNodeUserService;
import com.suneee.platform.service.bpm.BpmUserConditionService;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.BpmFormHandlerService;

/**
 * 触发新流程事件。
 * 
 * @author ray
 * 
 */
@Service
public class TriggerNewFlowEventListener implements ApplicationListener<TriggerNewFlowEvent>, Ordered {
	
	@Resource
	BpmNewFlowTriggerService newflowTriggerService;
	@Resource
	BpmDefinitionService bpmDefinitionService;
	@Resource
	ProcessRunService processRunService;
	@Resource
	BpmUserConditionService userConditionService;
	@Resource
	BpmNodeUserService bpmNodeUserService;
	@Resource
	BpmFormHandlerService formHandlerService;
	
	@Override
	public void onApplicationEvent(TriggerNewFlowEvent event) {
		TriggerNewFlowModel model = (TriggerNewFlowModel) event.getSource();
		ProcessCmd fromCmd = model.getProcessCmd();
		
		String nodeId =model.getNodeId();
		String action =model.getAction();
		String formKey = fromCmd.getProcessRun().getFlowKey();
		
		if(StringUtil.isEmpty(action)) return;
			BpmNewFlowTrigger flowTrigger = newflowTriggerService.getByNodeAction(nodeId,formKey,action);
			if(flowTrigger == null) return;
			
			JSONArray jsonMapping = JSONArray.parseArray(flowTrigger.getJsonmaping());
			if(jsonMapping.size()<1) return ;
			
			String fromDataStr="";
			try {
				fromDataStr = formHandlerService.getBpmFormDataJson((ProcessRun)fromCmd.getProcessRun(), fromCmd.getBusinessKey(), nodeId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(StringUtil.isEmpty(fromDataStr)) return ;
			//通过映射获取新的表单数据
			String newFlowFormData = getNewFlowFormData(fromDataStr,jsonMapping);
			
			String newFlowKey = flowTrigger.getTriggerflowkey();
			BpmDefinition bpmDefinition = bpmDefinitionService.getMainDefByActDefKey(newFlowKey);
			
			//启动一个新的流程
			ProcessCmd newFlowCmd = new ProcessCmd();
			newFlowCmd.setFormData(newFlowFormData);
			newFlowCmd.setFlowKey(newFlowKey);
			newFlowCmd.addTransientVar(BpmConst.BPM_DEF, bpmDefinition);
			
			List<BpmUserCondition> userCondition = userConditionService.getTriggerNewFlowStartUserConditions(fromCmd.getActDefId(), nodeId);
			List<SysUser> starUsers =  bpmNodeUserService.getUserByCondition(fromCmd, userCondition, ContextUtil.getCurrentUserId());
			if(BeanUtils.isEmpty(starUsers)) throw new WorkFlowException("触发新流程失败，未设置流程发起人！");
			
			BpmUtil.startNewFlow(newFlowCmd, starUsers.get(0));
	}

	


	private String getNewFlowFormData(String formDataStr, JSONArray jsonMapping) {
		JSONObject newFlowFormData = JSONObject.parseObject("{main:{},sub:[]}");	
		JSONObject fromFlowData = JSONObject.parseObject(formDataStr);
		
		
		JSONObject mianTableMapping = jsonMapping.getJSONObject(0);
		JSONObject fieldData = fromFlowData.getJSONObject("main");
		//handlNewFormDate(newFlowFromData,fieldData,mianTableMapping.getJSONArray("children"));
		 
		 JSONArray fields = mianTableMapping.getJSONArray("children");
		 JSONObject mainFieldData = getFieldDataByFiledMapping(fieldData,fields,newFlowFormData,fromFlowData);
		 newFlowFormData.getJSONObject("main").put("fields", mainFieldData);
		 newFlowFormData.put("opinion", new JSONArray()); 
		 
		 return  newFlowFormData.toJSONString();
		 
	}
	


	private void handlSubTableFormData(JSONObject newFlowFromData, JSONObject subTableMaping, JSONObject subTablesJson) {
		String subTableName = subTableMaping.getString("triggerTableName");
		if(StringUtil.isEmpty(subTableName)) return; 
		
		String fromTableName = subTableMaping.getString("fieldName").toLowerCase();
		JSONObject subTable = new JSONObject();
		subTable.put("tableName", subTableName);
		JSONArray subTableDate = new JSONArray() ;
		
		if(!subTablesJson.containsKey(fromTableName)) return;
		JSONArray fromsubTableDataArr = subTablesJson.getJSONObject(fromTableName).getJSONArray("dataList");
		for (int i = 0; i < fromsubTableDataArr.size(); i++) {
			JSONObject fields = getFieldDataByFiledMapping(fromsubTableDataArr.getJSONObject(i),subTableMaping.getJSONArray("children"),null,null);
			subTableDate.add(fields);
		}
		subTable.put("fields", subTableDate);
		newFlowFromData.getJSONArray("sub").add(subTable);
	}
	
	private JSONObject getFieldDataByFiledMapping(JSONObject fieldData, JSONArray fields, JSONObject newFlowFromData, JSONObject fromFlowData) {
		JSONObject mainFieldData = new JSONObject();
		for (int i = 0; i < fields.size(); i++) {
			JSONObject fromField = fields.getJSONObject(i);
			String type  = fromField.getString("fieldType");
			//从数据库拿到的值，都是小写的、这里回填值得时候、先转小写。
			String fieldName = fromField.getString("fieldName").toLowerCase();
			if("table".equals(type)){
				handlSubTableFormData(newFlowFromData,fromField,fromFlowData.getJSONObject("sub"));
			}
			if(!fieldData.containsKey(fieldName)) continue;
			JSONArray targets = fromField.getJSONArray("children");
			if(BeanUtils.isNotEmpty(targets))
			for (int j = 0; j < targets.size(); j++) {
				String targetField =targets.getJSONObject(j).getString("fieldName");
				if("number".equals(type)){
					if(StringUtil.isNotEmpty(fieldData.getString(fieldName))){
						mainFieldData.put(targetField, fieldData.getDoubleValue(fieldName));
					}
				}else{ //("varchar".equals(type))
					mainFieldData.put(targetField, fieldData.getString(fieldName));
				}
			}
		}
		return mainFieldData;
	}

	@Override
	public int getOrder() {
		return 3;
	}

}
/*  {主表   children：[{主表字段映射},{字表  children：[字表字段映射] }]}
 [
    {
        "fieldId": 10000011960120,
        "tableId": 0,
        "fieldName": "borrow",
        "fieldType": "table",
        "fieldDesc": "借款(table)",
        "style": "cur",
        "type": 1,
		"triggerTableName": "data",
        "targetTableDesc": "资料表(table)"
        "children": [
        
            {
                "fieldId": 10000011960122,
                "tableId": 10000011960120,
                "fieldName": "jkr",
                "fieldType": "varchar",
                "fieldDesc": "借款人(varchar)",
                "icon": "/bpmx/styles/default/images/resicon/o_10.png",
                "children": [
                    {
                        "fieldId": 10000010470182,
                        "tableId": 10000011960122,
                        "fieldName": "name",
                        "fieldType": "varchar",
                        "fieldDesc": "姓名(varchar)",
                        "oldTableName": "data",
                        "targetTableName": "borrow"
                    }
                ]
            },
            {
                "fieldId": 10000011960123,
                "tableId": 10000011960120,
                "fieldName": "jkrID",
                "fieldType": "varchar",            
                "fieldDesc": "借款人ID(varchar)",
                "style": "cur"
                "children": [
                    {
                        "fieldId": 10000010470183,
                        "tableId": 10000011960123,
                        "fieldName": "sex",
                        "fieldType": "varchar",
                        "icon": "/bpmx//styles/default/images/resicon/tree_file.png",
                        "oldTableName": "data",
                        "targetTableName": "borrow"
                    }
                ]
            },
            {
                "fieldId": 10000011960124,
                "tableId": 10000011960120,
                "fieldName": "jksj",
                "fieldType": "date",
                "icon": "/bpmx/styles/default/images/resicon/o_10.png",
                "children": [
                    {
                        "createBy": null,
                        "createtime": null,
                        "updatetime": null,
                        "updateBy": null,
                        "fieldId": 10000010470184,
                        "tableId": 10000011960124,
                        "fieldName": "age",
                        "fieldType": "varchar",
                        "isRequired": 0,
                        "isList": 1,
                        "isQuery": 0,
                        "fieldDesc": "年龄(varchar)",
                        "charLen": 50,
                        "intLen": 0,
                        "decimalLen": 0,
                        "dictType": "",
                        "isDeleted": 0,
                        "validRule": "",
                        "originalName": "",
                        "sn": 3,
                        "valueFrom": 0,
                        "script": "",
                        "controlType": 1,
                        "isHidden": 0,
                        "isFlowVar": 1,
                        "identity": "",
                        "options": "",
                        "ctlProperty": "{\"format\":\"yyyy-MM-dd\",\"displayDate\":0}",
                        "isAllowMobile": 0,
                        "isDateString": 0,
                        "isCurrentDateStr": 0,
                        "style": "trigger",
                        "isReference": 0,
                        "datefmt": "yyyy-MM-dd",
                        "scriptID": "",
                        "type": 0,
                        "isWebSign": 0,
                        "isShowComdify": null,
                        "coinValue": null,
                        "ngModel": null,
                        "newName": null,
                        "defValue": null,
                        "propertyMap": {
                            "displayDate": "0",
                            "format": "yyyy-MM-dd"
                        },
                        "jsonOptions": "",
                        "dbFieldName": "F_age",
                        "aryOptions": {},
                        "executorSelectorHidden": false,
                        "added": false,
                        "fieldTypeDisplay": "字符串,varchar(50)",
                        "level": 2,
                        "tId": "curFormTree_10",
                        "parentTId": "curFormTree_4",
                        "open": false,
                        "isParent": false,
                        "zAsync": true,
                        "isFirstNode": true,
                        "isLastNode": true,
                        "isAjaxing": false,
                        "checked": false,
                        "checkedOld": false,
                        "nocheck": false,
                        "chkDisabled": false,
                        "halfCheck": false,
                        "check_Child_State": -1,
                        "check_Focus": false,
                        "isHover": false,
                        "editNameFlag": false,
                        "icon": "/bpmx//styles/default/images/resicon/tree_file.png",
                        "oldTableName": "data",
                        "targetTableName": "borrow"
                    }
                ]
            },
            {
                "createBy": null,
                "createtime": null,
                "updatetime": null,
                "updateBy": null,
                "fieldId": 10000011960125,
                "tableId": 10000011960120,
                "fieldName": "jkje",
                "fieldType": "number",
                "isRequired": 0,
                "isList": 1,
                "isQuery": 0,
                "fieldDesc": "借款金额(number)",
                "charLen": 0,
                "intLen": 13,
                "decimalLen": 2,
                "dictType": "",
                "isDeleted": 0,
                "validRule": "",
                "originalName": "",
                "sn": 4,
                "valueFrom": 0,
                "script": "",
                "controlType": 1,
                "isHidden": 0,
                "isFlowVar": 1,
                "identity": "",
                "options": "",
                "ctlProperty": "{\"coinValue\":\"￥\",\"isShowComdify\":1,\"decimalValue\":2}",
                "isAllowMobile": 0,
                "isDateString": 0,
                "isCurrentDateStr": 0,
                "style": "cur",
                "isReference": null,
                "datefmt": "yyyy-MM-dd",
                "scriptID": "",
                "type": 0,
                "isWebSign": 0,
                "isShowComdify": null,
                "coinValue": null,
                "ngModel": null,
                "newName": null,
                "defValue": null,
                "propertyMap": {
                    "isShowComdify": "1",
                    "coinValue": "￥",
                    "decimalValue": "2"
                },
                "jsonOptions": "",
                "dbFieldName": "F_jkje",
                "aryOptions": {},
                "executorSelectorHidden": false,
                "added": false,
                "fieldTypeDisplay": "数字,number(13,2)",
                "level": 1,
                "tId": "curFormTree_5",
                "parentTId": "curFormTree_1",
                "open": false,
                "isParent": false,
                "zAsync": true,
                "isFirstNode": false,
                "isLastNode": false,
                "isAjaxing": false,
                "checked": false,
                "checkedOld": false,
                "nocheck": false,
                "chkDisabled": false,
                "halfCheck": false,
                "check_Child_State": -1,
                "check_Focus": false,
                "isHover": false,
                "editNameFlag": false,
                "icon": "/bpmx/styles/default/images/resicon/o_10.png"
            },
            {
                "createBy": null,
                "createtime": null,
                "updatetime": null,
                "updateBy": null,
                "fieldId": 10000011960126,
                "tableId": 10000011960120,
                "fieldName": "ht",
                "fieldType": "varchar",
                "isRequired": 0,
                "isList": 1,
                "isQuery": 0,
                "fieldDesc": "合同(varchar)",
                "charLen": 2000,
                "intLen": 0,
                "decimalLen": 0,
                "dictType": "",
                "isDeleted": 0,
                "validRule": "",
                "originalName": "",
                "sn": 5,
                "valueFrom": 0,
                "script": "",
                "controlType": 9,
                "isHidden": 0,
                "isFlowVar": 0,
                "identity": "",
                "options": "",
                "ctlProperty": "{\"isDirectUpLoad\":1}",
                "isAllowMobile": 0,
                "isDateString": 0,
                "isCurrentDateStr": 0,
                "style": "cur",
                "isReference": null,
                "datefmt": "yyyy-MM-dd",
                "scriptID": "",
                "type": 0,
                "isWebSign": 0,
                "isShowComdify": null,
                "coinValue": null,
                "ngModel": null,
                "newName": null,
                "defValue": null,
                "propertyMap": {
                    "isDirectUpLoad": "1"
                },
                "jsonOptions": "",
                "dbFieldName": "F_ht",
                "aryOptions": {},
                "executorSelectorHidden": false,
                "added": false,
                "fieldTypeDisplay": "字符串,varchar(2000)",
                "level": 1,
                "tId": "curFormTree_6",
                "parentTId": "curFormTree_1",
                "open": false,
                "isParent": false,
                "zAsync": true,
                "isFirstNode": false,
                "isLastNode": false,
                "isAjaxing": false,
                "checked": false,
                "checkedOld": false,
                "nocheck": false,
                "chkDisabled": false,
                "halfCheck": false,
                "check_Child_State": -1,
                "check_Focus": false,
                "isHover": false,
                "editNameFlag": false,
                "icon": "/bpmx/styles/default/images/resicon/o_10.png"
            },
            {
                "createBy": null,
                "createtime": null,
                "updatetime": null,
                "updateBy": null,
                "fieldId": 10000011960131,
                "tableId": 10000011960120,
                "fieldName": "bz",
                "fieldType": "varchar",
                "isRequired": 0,
                "isList": 1,
                "isQuery": 0,
                "fieldDesc": "备注(varchar)",
                "charLen": 2000,
                "intLen": 0,
                "decimalLen": 0,
                "dictType": "",
                "isDeleted": 0,
                "validRule": "",
                "originalName": "",
                "sn": 6,
                "valueFrom": 0,
                "script": "",
                "controlType": 2,
                "isHidden": 0,
                "isFlowVar": 0,
                "identity": "",
                "options": "",
                "ctlProperty": "",
                "isAllowMobile": 0,
                "isDateString": 0,
                "isCurrentDateStr": 0,
                "style": "cur",
                "isReference": null,
                "datefmt": "yyyy-MM-dd",
                "scriptID": "",
                "type": 0,
                "isWebSign": 0,
                "isShowComdify": null,
                "coinValue": null,
                "ngModel": null,
                "newName": null,
                "defValue": null,
                "propertyMap": {},
                "jsonOptions": "",
                "dbFieldName": "F_bz",
                "aryOptions": {},
                "executorSelectorHidden": false,
                "added": false,
                "fieldTypeDisplay": "字符串,varchar(2000)",
                "level": 1,
                "tId": "curFormTree_7",
                "parentTId": "curFormTree_1",
                "open": false,
                "isParent": false,
                "zAsync": true,
                "isFirstNode": false,
                "isLastNode": true,
                "isAjaxing": false,
                "checked": false,
                "checkedOld": false,
                "nocheck": false,
                "chkDisabled": false,
                "halfCheck": false,
                "check_Child_State": -1,
                "check_Focus": false,
                "isHover": false,
                "editNameFlag": false,
                "icon": "/bpmx/styles/default/images/resicon/o_10.png"
            }
        ],
    }
]







*/
