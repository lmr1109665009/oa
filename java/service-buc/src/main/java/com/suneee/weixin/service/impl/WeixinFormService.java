package com.suneee.weixin.service.impl;

import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.form.BpmFormHandlerDao;
import com.suneee.platform.dao.form.BpmMobileFormDefDao;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.BpmMobileFormDef;
import com.suneee.platform.model.form.SubTable;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import com.suneee.platform.service.form.BpmFormHandlerService;
import com.suneee.weixin.service.IWeixinFormService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Service
public class WeixinFormService implements IWeixinFormService {
	
	@Resource
	private BpmFormHandlerDao bpmFormHandlerDao;
	@Resource
	private BpmFormHandlerService bpmFormHandlerService;
	
	@Resource
	private BpmNodeSetService bpmNodeSetService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmMobileFormDefDao bpmMobileFormDefDao;
	

	@Override
	public JSONObject getByFormTable(BpmFormTable formTable, boolean isPreview) {
		BpmFormData data = bpmFormHandlerService.getByFormTable(formTable, isPreview);
		
		JSONObject jsonObj=convertFormDataToJson(data);
		
		return jsonObj;
	}
	
	/**
	 * 
	 * 将格式
	 * {"main": {
		             "sqr": "自由港,李广,马云",
		             "sqrID": "10001,10002,10004",
		              "hobby": "1,2",
		              "address": "广州",
		              "age": "21",
		              "memo":"备注测试",
		              "color":"1,2",
		              "manyidu":2,
		              "birthday":"2005-12-13"
		        },
		        "sub": {
		        	"customer":{row:{},rows:[]}
		        }}
		        转成如下格式：
		        {
				    "main": {
				        "fields": {
				            "xm": "ddd",
				            "dz": "ddd"
				        }
				    },
				    "sub": [
				        {
				            "tableName": "jsxx",
				            "fields": [
				                {
				                    "xm": "ddd",
				                    "gx": "ddd",
				                    "ID": ""
				                },
				                {
				                    "xm": "rrr",
				                    "gx": "sssss",
				                    "ID": ""
				                }
				            ]
				        }
				    ],
				    "opinion": []
				}
	 * @param jsonObj
	 * @return
	 */
	public static String convertJson(String json){
		JSONObject toJson=new JSONObject();
		JSONObject jsonObj=JSONObject.fromObject(json);
		//处理主表数据。
		JSONObject jsonMain=jsonObj.getJSONObject("main");
		JSONObject mainFields=new JSONObject();
		mainFields.put("fields", jsonMain);
		toJson.put("main", mainFields);
		
		//子表处理
		JSONObject jsonSub=jsonObj.getJSONObject("sub");
		
		Set<String> subTableNames=jsonSub.keySet();
		JSONArray toSubAry=new JSONArray();
		
		
		for(Iterator<String> it=subTableNames.iterator();it.hasNext();){
			String tbName=it.next();
			JSONObject jsonSubTb=jsonSub.getJSONObject(tbName);
			JSONArray arySubRows=jsonSubTb.getJSONArray("rows");
			
			JSONObject toJsonTb=new JSONObject();
			toJsonTb.put("tableName", tbName);
			toJsonTb.put("fields", arySubRows);
			toSubAry.add(toJsonTb);
		}
		toJson.put("sub", toSubAry);
		
		return toJson.toString();
	}
	
	public static void main(String[] args) {
		String json="{\"sub\":{\"spxm\":{rows:[{\"id\":10000005500068,\"khid\":\"1\",\"kh\":\"超级管理员\",\"mc\":\"梨\",\"xx\":\"2,2\"},{\"id\":10000005500067,\"khid\":\"1\",\"kh\":\"超级管理员\",\"mc\":\"苹果\",\"xx\":\"1\"}]}},\"main\":{\"id\":10000005500046,\"je\":\"90\",\"yf\":\"老李\",\"jf\":\"轰天软件\",\"ryid\":\"1\",\"ry\":\"超级管理员\"}}";
		String str=convertJson(json);
		System.out.println(str);
	}
	
	/**
	 * 将BpmFormData数据转成JSON格式。
	 * @param data
	 * @return
	 */
	private JSONObject convertFormDataToJson(BpmFormData data){
		JSONObject jsonObj=new JSONObject();
		JSONObject mainJson=new JSONObject();
		//构建主表JSON
		for (Entry<String, Object> entry : data.getMainFields().entrySet()) {
			mainJson.put(entry.getKey(), entry.getValue());
		}
		jsonObj.put("main", mainJson);
		//构建子表json。
		List<SubTable> subTables= data.getSubTableList();
		
		JSONObject tbsJson=new JSONObject();
		for(SubTable sub:subTables){
			JSONArray rows=new JSONArray();
			List<Map<String,Object>> dataList= sub.getDataList();
			for(Map<String,Object> row :dataList){
				JSONObject rowJson=new JSONObject();
				for (Entry<String, Object> entry :row.entrySet()) {
					rowJson.put(entry.getKey(), entry.getValue());
				}
				rows.add(rowJson);
			}
			
			JSONObject jsonTb=new JSONObject();
			jsonTb.put("row", sub.getRow());
			jsonTb.put("rows", rows);
			
			tbsJson.put(sub.getTableName(), jsonTb);
			
		}
		jsonObj.put("sub", tbsJson);
		return jsonObj;
	}

	@Override
	public JSONObject getApproveData(BpmFormTable formTable, String actDefId, String nodeId, String pkValue) throws Exception {
		BpmFormData data = bpmFormHandlerDao.getByKey(formTable, pkValue, actDefId, nodeId, true);
		bpmFormHandlerService.calcData(formTable, data, false, false,(short)0);
		JSONObject jsonObj=convertFormDataToJson(data);
		
		return jsonObj;
	}

	@Override
	public JSONObject getDraftData(BpmFormTable formTable, String pkValue, boolean isReCaculate) throws Exception {
		BpmFormData data = bpmFormHandlerDao.getByKey(formTable, pkValue, "", "", true);
		bpmFormHandlerService.calcData(formTable, data, false, isReCaculate,(short)0);
		JSONObject jsonObj=convertFormDataToJson(data);
		return jsonObj;
	}

	@Override
	public BpmMobileFormDef getByDefId(Long defId, String localFormKey, int version) {
		BpmDefinition definition= bpmDefinitionService.getById(defId);
		//获取开始节点配置。
		BpmNodeSet nodeSet=null;
		try {
			nodeSet=  bpmNodeSetService.getStartBpmNodeSet(definition.getActDefId(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(nodeSet==null) return null;
		
		String formKey=nodeSet.getMobileFormKey();
		BpmMobileFormDef formDef= getByMobileFormKey(formKey, localFormKey,version);
		//设置节点信息。
		formDef.setBpmNodeSet(nodeSet);
		//设置PC表单
		formDef.setPcFormKey(nodeSet.getFormKey());
		return formDef;
	}
	
	private BpmMobileFormDef getByMobileFormKey(String formKey,String localFormKey,int version){
		BpmMobileFormDef formDef= bpmMobileFormDefDao.getDefaultByFormKey(formKey);
		if(formDef==null) return formDef;
		if(formKey.equals(localFormKey) && version==formDef.getVersion()){
			formDef.setFormHtml("");
		}
		
		return formDef;
	}

	@Override
	public BpmMobileFormDef getByNodeId(String actDefId, String parentActDefId, String nodeId,String localFormKey,int version ) {
		//父流程定义ID不为空的情况
		BpmNodeSet  nodeSet= bpmNodeSetService.getFormByActDefIdNodeId(actDefId, nodeId, parentActDefId, true);
		BpmMobileFormDef formDef= getByMobileFormKey(nodeSet.getMobileFormKey(), localFormKey,version);
		formDef.setPcFormKey(nodeSet.getFormKey());
		return formDef;
	}

	@Override
	public BpmMobileFormDef getInstByDefId(String actDefId,String parentActDefId, String localFormKey, int version) {
		BpmNodeSet nodeSet= getInstMobile( actDefId,  parentActDefId);
		if(nodeSet==null) return null;
		String formKey=nodeSet.getMobileFormKey();
		
		if(StringUtil.isEmpty(formKey)) return null;
		
		BpmMobileFormDef formDef= getByMobileFormKey(formKey, localFormKey,version);
		formDef.setPcFormKey(nodeSet.getFormKey());
		return formDef;
	}
	
	private BpmNodeSet getInstMobile(String actDefId, String parentActDefId) {
		BpmNodeSet  nodeSet=null;
		//父流程定义ID为空
		if(StringUtil.isNotEmpty(parentActDefId)){
			//流程实例
			nodeSet=bpmNodeSetService.getBySetType(actDefId, (short)3, parentActDefId);
			if(nodeSet!=null ){
				if(nodeSet.isMobileEnabled()){
					return nodeSet;
				}
			}
			//全局
			else{
				nodeSet=bpmNodeSetService.getBySetType(actDefId, (short)2, parentActDefId);
				if(nodeSet!=null ){
					if(nodeSet.isMobileEnabled()){
						return nodeSet;
					}
				}
			}
		}
		//实例
		nodeSet=bpmNodeSetService.getBySetType(actDefId, (short)3);
		if(nodeSet!=null ){
			if(nodeSet.isMobileEnabled()){
				return nodeSet;
			}
		}
		//全局
		nodeSet=bpmNodeSetService.getBySetType(actDefId, (short)2);
		if(nodeSet!=null ){
			if(nodeSet.isMobileEnabled()){
				return nodeSet;
			}
		}
		
		return null;
	}

}
