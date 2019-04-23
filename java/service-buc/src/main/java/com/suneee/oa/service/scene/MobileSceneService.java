package com.suneee.oa.service.scene;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.oa.dao.scene.MobileSceneDao;
import com.suneee.oa.model.scene.MobileScene;
import com.suneee.oa.model.scene.SubProcess;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormHandlerService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.ucp.base.service.UcpBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MobileSceneService extends UcpBaseService<MobileScene> {
    @Resource
    private MobileSceneDao mobileSceneDao;
    @Resource
    private GlobalTypeService globalTypeService;
    @Resource
    private BpmDefinitionService bpmDefinitionService;
    @Resource
    private SubProcessService subProcessService;
    @Resource
    private SysOrgService sysOrgService;
    @Resource
    BpmFormHandlerService formHandlerService;
    @Resource
    private ProcessRunService processRunService;
    @Resource
    private BpmNodeSetService bpmNodeSetService;
    @Resource
    private BpmFormDefService bpmFormDefService;


    @Override
    protected IEntityDao<MobileScene, Long> getEntityDao() {
        return this.mobileSceneDao;
    }

    public void save(MobileScene mobileScene,List<SubProcess> subProcessList) throws IOException {
        BpmDefinition definition = bpmDefinitionService.getByDefId(mobileScene.getDefId().toString());
        GlobalType globalType = globalTypeService.getById(mobileScene.getTypeId());
        mobileScene.setDefKey(definition.getDefKey());
        mobileScene.setDefName(definition.getSubject());
        mobileScene.setActDefId(definition.getActDefId());
        mobileScene.setTypeName(globalType.getTypeName());
        String relDefIds = "";
        String relDefNames ="";
        Long Id = mobileScene.getId();
        if(null!=Id){
            List<SubProcess> oldSubProList = subProcessService.getBySceneId(Id);
            for (SubProcess oldSub:oldSubProList){
                subProcessService.delById(oldSub.getId());
            }
            //关联子表类新建保存
            if(null!=subProcessList&&subProcessList.size()>0) {
                Map<String, String> map = this.addSubProce(mobileScene, subProcessList);
                //场景类保存
                if(map.size()>0) {
                    relDefIds = map.get("relDefIds").substring(0, map.get("relDefIds").length()-1);
                    relDefNames =map.get("relDefNames").substring(0, map.get("relDefNames").length() - 1);
                }
            }
            mobileScene.setRelDefIds(relDefIds);
            mobileScene.setRelDefNames(relDefNames);
            this.update(mobileScene);
        }else{
            //多企业组织编码（获取新建人的组织编码）
            String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
            mobileScene.setEnterpriseCode(enterpriseCode);
            mobileScene.setId(UniqueIdUtil.genId());
            //关联子表类新建保存
            if(null!=subProcessList&&subProcessList.size()>0) {
                Map<String, String> map = this.addSubProce(mobileScene, subProcessList);
                //场景类保存
                if(map.size()>0) {
                    relDefIds = map.get("relDefIds").substring(0, map.get("relDefIds").length()-1);
                    relDefNames =map.get("relDefNames").substring(0, map.get("relDefNames").length() - 1);
                }
            }
            mobileScene.setRelDefIds(relDefIds);
            mobileScene.setRelDefNames(relDefNames);
            mobileSceneDao.add(mobileScene);
        }
    }

    public void delBySceneIds(Long[] idAry) throws IOException {
        for (Long Id:idAry){
            subProcessService.delBySceneId(Id);
            this.delById(Id);
        }
    }

    public Map<String,String> addSubProce(MobileScene mobileScene,List<SubProcess> subProcessList){
        Map<String,String> map = new HashMap<>();
        String relDefIds = "";
        String relDefNames ="";
        if(subProcessList.size()>0){
            for (SubProcess sub:subProcessList){
                BpmDefinition subBpmDefinition = bpmDefinitionService.getByDefId(sub.getSubDefId().toString());
                sub.setId(UniqueIdUtil.genId());
                sub.setSceneId(mobileScene.getId());
                sub.setSceneName(mobileScene.getSceneName());
                sub.setDefId(mobileScene.getDefId());
                sub.setSubDefName(subBpmDefinition.getSubject());
                sub.setSubDefKey(subBpmDefinition.getDefKey());
                sub.setSubActDefId(subBpmDefinition.getActDefId());
                sub.setDefName(mobileScene.getDefName());
                sub.setDefKey(mobileScene.getDefKey());
                subProcessService.add(sub);
                relDefIds+=sub.getSubDefId().toString()+",";
                relDefNames+=sub.getSubDefName()+",";
            }
            map.put("relDefIds",relDefIds);
            map.put("relDefNames",relDefNames);
            return map;
        }
        return map;
    }

    public List<MobileScene> getByTypeId(Long typeId) {
        return mobileSceneDao.getByTypeId(typeId);
    }

    public Long startSubProcess(SubProcess subProcess,Long runId) {
        String jsonStr = subProcess.getJsonmaping();
        if(StringUtil.isNotEmpty(jsonStr)){
            BpmDefinition definition = bpmDefinitionService.getByDefId(subProcess.getSubDefId().toString());
            JSONArray jsonMapping = JSONArray.parseArray(jsonStr);
            ProcessRun processRun = processRunService.getById(runId);
            String fromDataStr="";
            try {
                fromDataStr = formHandlerService.getBpmFormDataJson(processRun, processRun.getBusinessKey(), processRun.getStartNode());
                //通过映射获取新的表单数据
                String newFlowFormData = getNewFlowFormData(fromDataStr,jsonMapping);
                //根据cmd保存一个新子流程草稿
                ProcessCmd newFlowCmd = new ProcessCmd();
                newFlowCmd.setFormData(newFlowFormData);
                newFlowCmd.setActDefId(subProcess.getSubActDefId());
                newFlowCmd.setFlowKey(subProcess.getSubDefKey());
                newFlowCmd.addTransientVar(BpmConst.BPM_DEF, definition);
                return  this.saveForm(newFlowCmd, definition);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else{
            return 0L;
        }
    }

    private Long saveForm(ProcessCmd processCmd, BpmDefinition bpmDefinition) throws Exception{
        String actDefId = processCmd.getActDefId();
        // 开始节点
        String nodeId = "";
        if (!NodeCache.isMultipleFirstNode(actDefId)) {
            nodeId = NodeCache.getStartNode(actDefId).getNodeId();
        }
        BpmNodeSet bpmNodeSet =bpmNodeSetService.getStartBpmNodeSet(actDefId, false);
        ProcessRun processRun = processRunService.initProcessRun(bpmDefinition, processCmd,bpmNodeSet);
        String businessKey = "";
        // 保存草稿后，处理业务表单，外部调用不触发表单处理
        if (!processCmd.isInvokeExternal()) {
            BpmFormData bpmFormData = processRunService.handlerFormData(processCmd, processRun, "");
            if (bpmFormData != null) {
                businessKey = processCmd.getBusinessKey();
                processRun.setTableName(bpmFormData.getTableName());
                if (bpmFormData.getPkValue() != null) {
                    processRun.setPkName(bpmFormData.getPkValue().getName());
                    processRun.setDsAlias(bpmFormData.getDsAlias());
                }
                BpmFormDef defaultForm = bpmFormDefService.getDefaultPublishedByFormKey(bpmNodeSet.getFormKey());
                processRun.setFormDefId(defaultForm.getFormDefId());
            }
        }
        // 调用前置处理器
        if (!processCmd.isSkipPreHandler()) {
            invokeHandler(processCmd, bpmNodeSet, true);
        }
        if (StringUtil.isEmpty(businessKey)) {
            businessKey = processCmd.getBusinessKey();
        }
        String subject = processRunService.getSubject(bpmDefinition, processCmd);
        processRun.setBusinessKey(businessKey);
        processRun.setSubject(subject);
        processRun.setStatus(ProcessRun.STATUS_SUBPROCESS_FORM);
        processRun.setCreatetime(new Date());
        processRunService.add(processRun);
        // 调用前置处理器
        if (!processCmd.isSkipPreHandler()) {
            invokeHandler(processCmd, bpmNodeSet, true);
        }
        return processRun.getRunId();
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

    private void invokeHandler(ProcessCmd processCmd, BpmNodeSet bpmNodeSet, boolean isBefore) throws Exception {
        if (bpmNodeSet == null)
            return;
        String handler = "";
        if (isBefore) {
            handler = bpmNodeSet.getBeforeHandler();
        } else {
            handler = bpmNodeSet.getAfterHandler();
        }
        if (StringUtil.isEmpty(handler))
            return;

        String[] aryHandler = handler.split("[.]");
        if (aryHandler != null) {
            String beanId = aryHandler[0];
            String method = aryHandler[1];
            // 触发该Bean下的业务方法
            Object serviceBean = AppUtil.getBean(beanId);
            if (serviceBean != null) {
                Method invokeMethod = serviceBean.getClass().getDeclaredMethod(method, new Class[] { ProcessCmd.class });
                invokeMethod.invoke(serviceBean, processCmd);
            }
        }
    }

    public List<MobileScene> getByDefId(Long defId) {
        return mobileSceneDao.getByDefId(defId);
    }
}
