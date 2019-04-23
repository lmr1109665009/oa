package com.suneee.eas.oa.service.scene.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suneee.core.util.BeanUtils;
import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.common.utils.StringUtil;
import com.suneee.eas.oa.dao.scene.MobileSceneDao;
import com.suneee.eas.oa.model.scene.MobileScene;
import com.suneee.eas.oa.model.scene.SubProcess;
import com.suneee.eas.oa.model.system.DicItem;
import com.suneee.eas.oa.service.bpm.BpmDefinitionService;
import com.suneee.eas.oa.service.bpm.BpmFormHandlerService;
import com.suneee.eas.oa.service.bpm.ProcessRunService;
import com.suneee.eas.oa.service.scene.MobileSceneService;
import com.suneee.eas.oa.service.scene.RelateProcessService;
import com.suneee.eas.oa.service.scene.SubProcessService;
import com.suneee.eas.oa.service.system.DicItemService;
import com.suneee.eas.oa.service.system.DicTypeService;

@Service
public class MobileSceneServiceImpl extends BaseServiceImpl<MobileScene> implements MobileSceneService{
    private MobileSceneDao mobileSceneDao;
    @Resource 
    private DicTypeService dicTypeService;
    @Resource
    private DicItemService dicItemService;
    @Resource
    private BpmDefinitionService bpmDefinitionService;
    @Resource
	private SubProcessService subProcessService;
    @Resource
    private RelateProcessService relateProcessService;
    @Resource
    private BpmFormHandlerService formHandlerService;
    @Resource
    private ProcessRunService processRunService;

    @Autowired
    protected void setMobileSceneDao(MobileSceneDao mobileSceneDao) {
    	this.mobileSceneDao = mobileSceneDao;
        setBaseDao(mobileSceneDao);
    }

    /** (non-Javadoc)
     * @Title: save 
     * @Description: 保存场景
     * @param mobileScene
     * @return 
     * @see com.suneee.eas.common.service.impl.BaseServiceImpl#save(java.lang.Object)
     */
    public int save(MobileScene mobileScene){
    	int rows = 0;
    	if(mobileScene == null){
    		return rows;
    	}
        DicItem  dicItem = dicItemService.findById(mobileScene.getTypeId());
        mobileScene.setTypeName(dicItem.getOption());
        Long Id = mobileScene.getId();
        
    	// 更新场景
        if (null != Id) {
        	// 流程场景需要处理流程信息
        	if(mobileScene.getType() != 1){
        		// 更新关联子流程信息
                Map<String, String> map = subProcessService.batchUpdate(mobileScene, mobileScene.getSubProcessList());
                
                // 更新相关流程信息
                relateProcessService.batchUpdate(mobileScene.getRelProcessList(), Id);
                
                mobileScene.setRelDefIds(map.get("relDefIds"));
                mobileScene.setRelDefNames(map.get("relDefNames"));
        	}
        	
            rows = this.update(mobileScene);
        } 
        // 新增场景
        else {

            mobileScene.setId(IdGeneratorUtil.getNextId());
            
        	// 流程场景需要处理流程信息
        	if(mobileScene.getType() != 1){
        		// 保存关联子流程
                Map<String, String> map = subProcessService.batchSave(mobileScene, mobileScene.getSubProcessList());
                
                // 保存相关流程信息
                relateProcessService.batchSave(mobileScene.getRelProcessList(), mobileScene.getId());
                

                mobileScene.setRelDefIds(map.get("relDefIds"));
                mobileScene.setRelDefNames(map.get("relDefNames"));
        	}
            
            // 多企业组织编码（获取新建人的组织编码）
            String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
            mobileScene.setEnterpriseCode(enterpriseCode);
            rows = mobileSceneDao.save(mobileScene);
        }
        return rows;
    }

    /** (non-Javadoc)
     * @Title: delBySceneIds 
     * @Description: 批量删除场景
     * @param idAry 
     * @see com.suneee.eas.oa.service.scene.MobileSceneService#delBySceneIds(java.lang.Long[])
     */
    public void delBySceneIds(Long[] idAry) {
        for (Long id:idAry){
        	// 删除关联子流程
            subProcessService.delBySceneId(id);
            
            // 删除相关流程
            relateProcessService.delBySceneId(id);
            
            this.deleteById(id);
        }
    }

    public List<MobileScene> getByTypeId(Long typeId) {
        return mobileSceneDao.getByTypeId(typeId);
    }

    public Long startSubProcess(SubProcess subProcess,Long runId) throws UnsupportedEncodingException {
        String jsonStr = subProcess.getJsonmaping();
        if(StringUtil.isNotEmpty(jsonStr)){
            //JSONObject definition = bpmDefinitionService.getByDefId(subProcess.getSubDefId());
            JSONArray jsonMapping = JSONArray.parseArray(jsonStr);
            JSONObject processRun = processRunService.getProcessRun(runId);
            String fromDataStr="";
                fromDataStr = formHandlerService.getBpmFormDataJson(processRun.getLong("runId"));
                //通过映射获取新的表单数据
                String newFlowFormData = getNewFlowFormData(fromDataStr,jsonMapping);
                //根据cmd保存一个新子流程草稿
                Long subRunId = processRunService.saveForm(subProcess.getSubActDefId(), newFlowFormData, subProcess.getSubDefKey());
                return subRunId;
        }else{
            return 0L;
        }
    }

    public String getNewFlowFormData(String formDataStr, JSONArray jsonMapping) {
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

    /** 
     * @Title: getByDefId 
     * @Description: 根据流程ID查询场景，当场景ID不为空时，查询结果去除该ID对应的场景
     * @param defId
     * @param sceneId
     * @return 
     * @see com.suneee.eas.oa.service.scene.MobileSceneService#getByDefId(java.lang.Long, java.lang.Long)
     */
    public List<MobileScene> getByDefId(Long defId, Long sceneId) {
        return mobileSceneDao.getByDefId(defId, sceneId);
    }
}
