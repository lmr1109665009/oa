package com.suneee.eas.oa.controller.scene;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suneee.core.util.BeanUtils;
import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.common.utils.StringUtil;
import com.suneee.eas.oa.model.scene.MobileScene;
import com.suneee.eas.oa.model.scene.RelateProcess;
import com.suneee.eas.oa.model.scene.SubProcess;
import com.suneee.eas.oa.model.system.DicItem;
import com.suneee.eas.oa.model.system.DicType;
import com.suneee.eas.oa.service.bpm.BpmDefinitionService;
import com.suneee.eas.oa.service.bpm.BpmFormTableService;
import com.suneee.eas.oa.service.scene.MobileSceneService;
import com.suneee.eas.oa.service.scene.RelateProcessService;
import com.suneee.eas.oa.service.scene.SubProcessService;
import com.suneee.eas.oa.service.system.DicItemService;
import com.suneee.eas.oa.service.system.DicTypeService;

/**
 * <pre>
 * 对象功能:手机端场景控制类
 * 开发公司:深圳象翌
 * 开发人员:pengfeng
 * 创建时间:2018-5-14
 * </pre>
 */
@RestController
@RequestMapping("/oa/mobileScene/")
public class MobileSceneApiController{
	private static final Logger LOGGER = LogManager.getLogger(MobileSceneApiController.class);
    @Resource
    private MobileSceneService mobileSceneService;
    @Resource
    private DicTypeService dicTypeService;
    @Resource
    private DicItemService dicItemService;
    @Resource
    private SubProcessService subProcessService;
    @Resource
    private RelateProcessService relateProcessService;
    @Resource
    private BpmDefinitionService bpmDefinitionService;
    @Resource
    private BpmFormTableService bpmFormTableService;

    /** 获取场景分页列表
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getList")
    public ResponseMessage getList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        QueryFilter filter = new QueryFilter("getAll", request);
        //通过当前人的组织编码获取列表
        String  enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        filter.addFilter("enterpriseCode",enterpriseCode);
        try {
            Pager<MobileScene> allList = mobileSceneService.getPageBySqlKey(filter);
            return ResponseMessage.success("获取列表数据成功", allList);
        }catch (Exception e){
        	LOGGER.error("获取列表信息失败：" + e.getMessage(), e);
            return ResponseMessage.fail("获取列表信息失败：系统内部错误！");
        }
    }

    /**
     * 获取场景分类
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("getSceneType")
    public ResponseMessage getSceneType(HttpServletRequest request) throws Exception{
		try {
			DicType dicType = dicTypeService.getByCode("cjfl");
			if(dicType == null){
				LOGGER.error("获取所有场景分类失败:未添加场景分类");
				return ResponseMessage.fail("获取所有场景分类失败：请先添加场景分类！");
			}
			List<DicItem> itemList = dicItemService.listByDicId(dicType.getDicId());
			return ResponseMessage.success("获取所有场景分类成功！", itemList);
		} catch (Exception e) {
			LOGGER.error("获取所有场景分类失败：" + e.getMessage(), e);
			return ResponseMessage.fail("获取所有场景分类失败：系统内部错误！");
		}
    }

    /**
     * 按分类列出所有场景
     * 由于getMobileSceneTypeList实现了分类及场景的查询
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getTypeList")
    @Deprecated
    public ResponseMessage getTypeList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        JSONObject json = new JSONObject();
        List<Map<String,Object>> listAll = new ArrayList<>();
        try {
        	DicType dicType = dicTypeService.getByCode("cjfl");
			if(dicType == null){
				LOGGER.error("获取列表信息失败：未添加场景分类");
				return ResponseMessage.fail("获取列表信息失败：请先添加场景分类！");
			}
			List<DicItem> itemList = dicItemService.listByDicId(dicType.getDicId());
            for (DicItem item : itemList){
            	List<MobileScene> list = mobileSceneService.getByTypeId(item.getId());
                if(list.size() > 0){
                	Map<String,Object> map = new HashMap<>();
                    map.put("list",list);
                    listAll.add(map);
                }
            }
            json.put("allList",listAll);
            return ResponseMessage.success("获取列表数据成功", json);
        }catch (Exception e){
        	LOGGER.error("获取列表信息失败：" + e.getMessage(), e);
            return ResponseMessage.fail("获取列表信息失败：系统内部错误！");
        }
    }

    /**
     * 按分类列出所有场景
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getMobileSceneTypeList")
    public ResponseMessage getMobileSceneTypeList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        List listAll = new ArrayList();
        JSONObject json = new JSONObject();
        try {
            DicType dicType = dicTypeService.getByCode("cjfl");
            if(dicType == null){
                LOGGER.error("获取列表信息失败：未添加场景分类");
                return ResponseMessage.fail("获取列表信息失败：请先添加场景分类！");
            }
            List<DicItem> itemList = dicItemService.listByDicId(dicType.getDicId());
            for (DicItem item : itemList){
                Map<String,Object> dataItem=new HashMap<>();
                dataItem.put("typeId", item.getId());
                dataItem.put("typeName", item.getOption());
                List<MobileScene> listMobileScene= mobileSceneService.getByTypeId(item.getId());
                if(listMobileScene.size()>0){
                    dataItem.put("list", listMobileScene);
                    listAll.add(dataItem);
                }
            }
            json.put("listAll", listAll);
            return ResponseMessage.success("获取列表数据成功", json);
        }catch (Exception e){
            LOGGER.error("获取列表信息失败：" + e.getMessage(), e);
            return ResponseMessage.fail("获取列表信息失败：系统内部错误！");
        }
    }


    /** 保存场景
     * @param request
     * @param mobileScene
     * @return
     * @throws Exception
     */
    @RequestMapping("save")
    public ResponseMessage saveScene(HttpServletRequest request,@RequestBody MobileScene mobileScene) throws Exception{
    	Long Id = mobileScene.getId();
        String message = null;
        if(null != Id){
            message="更新场景";
        }else{
            message = "新增场景";
        }
        
        if(StringUtil.isEmpty(ContextSupportUtil.getCurrentEnterpriseCode())){
            return ResponseMessage.fail(message + "失败：当前用户没有分配企业，不能保存！");
        }
        
        List<MobileScene> list = mobileSceneService.getByDefId(mobileScene.getDefId(), mobileScene.getId());
        if(list.size()>0){
            return ResponseMessage.fail(message + "失败：该流程已定义了场景！");
        }
        try {
            mobileSceneService.save(mobileScene);
            return ResponseMessage.success(message + "成功！");
        }catch (Exception e){
        	LOGGER.error(message + "失败：" + e.getMessage(), e);
            return ResponseMessage.fail(message + "失败：系统内部错误！");
        }
    }

    /**
     * 判断子流程是否关联场景
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("isCorrelation")
    public ResponseMessage isCorrelation(HttpServletRequest request) throws Exception{
        long defId = RequestUtil.getLong(request, "defId");
        List<MobileScene> list = mobileSceneService.getByDefId(defId, null);
        if(list.size()>0){
            return ResponseMessage.success("已关联场景，可在推送中显示！");
        }else{
            return ResponseMessage.fail("未关联场景，不能在推送中显示！");
        }
    }

    /** （批量）删除场景
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("del")
    public ResponseMessage del(HttpServletRequest request) throws Exception{
        try {
            Long[] IdAry = RequestUtil.getLongAryByStr(request,"Id");
            if(IdAry.length>0){
                mobileSceneService.delBySceneIds(IdAry);
                return ResponseMessage.success("删除场景成功！");
            }else{
                return ResponseMessage.fail("没有选择要删除的记录！");
            }
        }catch (Exception e){
        	LOGGER.error("删除记录失败：" + e.getMessage(), e);
            return ResponseMessage.fail("删除记录失败：系统内部错误！");
        }
    }

    /** 获取场景详情
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("edit")
    public ResponseMessage edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
        Long Id = RequestUtil.getLong(request,"Id");
        JSONObject json = new JSONObject();
        try {
        	DicType dicType = dicTypeService.getByCode("cjfl");
			if(dicType == null){
				LOGGER.error("编辑接口调用失败：未添加场景分类");
				return ResponseMessage.fail("编辑接口调用失败：请先添加场景分类！");
			}
			List<DicItem> itemList = dicItemService.listByDicId(dicType.getDicId());
            json.put("typeList",itemList);
            if(Id>0){
                MobileScene scene = mobileSceneService.findById(Id);
                List<SubProcess> subProcesses = subProcessService.getBySceneId(Id);
                List<RelateProcess> relProcessList = relateProcessService.getBySceneId(Id);
                json.put("scene",scene);
                json.put("subProcesses",subProcesses);
                json.put("relProcesses", relProcessList);
            }
            return ResponseMessage.success("编辑接口调用成功!", json);
        }catch (Exception e){
        	LOGGER.error("编辑接口调用失败：" + e.getMessage(), e);
            return ResponseMessage.fail("编辑接口调用失败：系统内部错误！");
        }
    }

    /**
     * 通过流程key 获取表单信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getTableTreeByDefkey")
    public ResponseMessage getTableTreeByDefkey(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String ctx = request.getContextPath();
        String defKey = RequestUtil.getString(request,"defKey");
        String type = RequestUtil.getString(request, "type","trigger");
		try {
			JSONObject bpmDefinition = bpmDefinitionService.getByDefKey(defKey);

			JSONObject formTable = bpmFormTableService.getBpmTableByDefId(bpmDefinition.getLong("defId"));
			List<JSONObject> formFieldList = handelTableTOfieldTree(formTable);

			JSONArray array = new JSONArray();
			JSONObject json = null;
			for(JSONObject field : formFieldList){
			    String icon ;
			    if(type.equals("trigger")){
			        icon = ctx+"/styles/default/images/resicon/tree_file.png";
			    } else {
			    	icon = ctx+"/styles/default/images/resicon/o_10.png";
			    }
			    if("table".equals(field.getString("fieldType"))){
			    	icon = ctx+"/styles/default/images/resicon/tree_folder.gif";
			    }

			    json = new JSONObject();
			    json.put("style", type);
			    json.put("fieldId", field.getLong("fieldId"));
			    json.put("tableId", field.getLong("tableId"));
			    json.put("fieldType", field.getString("fieldType"));
			    json.put("fieldName", field.getString("fieldName"));
			    json.put("fieldDesc", field.getString("fieldDesc")+"("+field.getString("fieldType")+")");
			    json.put("icon", icon);
			    array.add(json);
			}
			return ResponseMessage.success("获取表单信息成功！", array);
		} catch (Exception e) {
			LOGGER.error("获取表单信息失败：" + e.getMessage(), e);
			return ResponseMessage.fail("获取表单信息失败：系统内部错误！");
		}
    }
    
    @RequestMapping("getSubProcess")
    public ResponseMessage startSubProcess(HttpServletRequest request,HttpServletResponse response) throws Exception{
        long subProcessId = RequestUtil.getLong(request, "Id", 0L);
        long runId = RequestUtil.getLong(request, "runId");
        try {
            SubProcess subProcess = subProcessService.findById(subProcessId);
            Long newRunId = mobileSceneService.startSubProcess(subProcess, runId);
            if(newRunId>=0) {
                return ResponseMessage.success("获取子流程成功",newRunId);
            }else{
                return ResponseMessage.fail("获取子流程失败");
            }
        }catch (Exception e){
        	LOGGER.error("获取子流程失败：" + e.getMessage(), e);
            return ResponseMessage.fail("获取子流程失败：系统内部错误！");
        }
    }



    private List<JSONObject> handelTableTOfieldTree(JSONObject table) {
        List<JSONObject> fieldList = new ArrayList<JSONObject>();
        JSONObject mainField = getFieldByTable(table);
        fieldList.add(mainField);
        fieldList.addAll(table.getJSONArray("fieldList").toJavaList(JSONObject.class));

        if(BeanUtils.isNotEmpty(table.getJSONArray("subTableList"))){
        	JSONArray subTableList = table.getJSONArray("subTableList");
        	JSONObject subTable = null;
        	for(int i = 0; i < subTableList.size(); i++){
        		subTable = subTableList.getJSONObject(i);
        		fieldList.add(getFieldByTable(subTable));
        		fieldList.addAll(subTable.getJSONArray("fieldList").toJavaList(JSONObject.class));
        	}
        }
        return fieldList;
    }


    private JSONObject getFieldByTable(JSONObject table) {
    	JSONObject field = new JSONObject();
    	field.put("tableId", table.getLong("mainTableId"));
    	field.put("fieldId", table.getLong("tableId"));
    	field.put("fieldName", table.getString("tableName"));
    	field.put("fieldDesc", table.getString("tableDesc"));
    	field.put("fieldType", "table");
    	field.put("type", table.getShort("isMain"));
        return field;
    }

}
