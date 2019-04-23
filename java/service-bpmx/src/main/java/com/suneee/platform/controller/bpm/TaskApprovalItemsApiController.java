package com.suneee.platform.controller.bpm;


import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.TaskApprovalItems;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.TaskApprovalItemsService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象功能:常用语管理 控制器类
 * 开发公司:suneee
 * 开发人员:zousiyu
 * 创建时间:2018-03-12 10:53:20
 */

@Controller
@RequestMapping("/api/bpm/taskApprovalItems/")
@Action(ownermodel= SysAuditModelType.PROCESS_MANAGEMENT)
public class TaskApprovalItemsApiController extends BaseController
{
    @Resource
    private TaskApprovalItemsService taskApprovalItemsService;
    @Resource
    private BpmDefinitionService bpmDefinitionService;
    @Resource
    private GlobalTypeService globalTypeService;


    @RequestMapping("list")
    @Action(description="获取常用语管理列表")
    @ResponseBody
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception{
        Long currUserId= ContextUtil.getCurrentUserId();
        Long isAdmin= RequestUtil.getLong(request, "isAdmin");
        PageList<TaskApprovalItems> taskApprovalItemsList =null;
        QueryFilter queryFilter = new QueryFilter(request, true);
        if (isAdmin==1||currUserId==1) {
            taskApprovalItemsList = (PageList<TaskApprovalItems>) taskApprovalItemsService.getAll(queryFilter);
        }else {
            queryFilter.addFilter("userId",currUserId);
            taskApprovalItemsList = (PageList<TaskApprovalItems>) taskApprovalItemsService.getAll(queryFilter);
        }

        //保存流程分类为map，键为流程分类的Id，值为TypeName
        Map<Long, String> defTypeMap=new HashMap<Long, String>();
        Map<Long, String> defTypeTempMap=new HashMap<Long, String>();
        //保存流程为map，键为流程的defKey，值为subject
        Map<String, String> defMap=new HashMap<String, String>();
        Map<String, String> defTempMap=new HashMap<String, String>();

        //获取所有的最新版流程
        QueryFilter queryFilter1=new QueryFilter(request,false);
        queryFilter1.addFilter("isMain", 1);
        List<BpmDefinition> bpmDefinitionlist= bpmDefinitionService.getAll(queryFilter1);
        for (BpmDefinition bpmDefinition:bpmDefinitionlist) {
            defTempMap.put(bpmDefinition.getDefKey(), bpmDefinition.getSubject());
        }
        //获取所有的流程分类，处理流程分类
        List<GlobalType> globalTypeList= globalTypeService.getByCatKey(GlobalType.CAT_FLOW, false);
        for (GlobalType globalType:globalTypeList) {
            defTypeTempMap.put(globalType.getTypeId(), globalType.getTypeName());
        }

        for (TaskApprovalItems taskApprovalItems:taskApprovalItemsList) {
            if (taskApprovalItems.getType()==TaskApprovalItems.TYPE_FLOW) {
                defMap.put(taskApprovalItems.getDefKey(), defTempMap.get(taskApprovalItems.getDefKey()));
            }else if (taskApprovalItems.getType()==TaskApprovalItems.TYPE_FLOWTYPE) {
                defTypeMap.put(taskApprovalItems.getTypeId(), defTypeTempMap.get(taskApprovalItems.getTypeId()));
            }
        }

        Map map = new HashMap();
        map.put("taskApprovalItemsList", PageUtil.getPageVo(taskApprovalItemsList));
        map.put("defMap", defMap);
        map.put("defTypeMap", defTypeMap);
        map.put("defTempMap", defTempMap);
        map.put("isAdmin", isAdmin);
        map.put("currUserId", currUserId);
        map.put("globalTypeList", globalTypeList);

        return  new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取常用语成功", map);
    }




    @RequestMapping("edit")
    @Action(description="编辑常用语管理")
    @ResponseBody
    public ResultVo edit(HttpServletRequest request) throws Exception
    {
        TaskApprovalItems taskApprovalItems=null;
        GlobalType globalType=null;
        BpmDefinition bpmDefinition=null;
        Long id=RequestUtil.getLong(request,"itemId");
        if(id!=0){//没有获得常用语id，也就是什么都不用做；获得常用语id，就编辑
            //根据id 获得常用语对象
            taskApprovalItems=taskApprovalItemsService.getById(id);
            //获得常用语的类型，转为Integer
            Integer type=Integer.valueOf(taskApprovalItems.getType().toString());
            switch (type){
                case 1://全局的，也就是只有内容
                    break;
                case 2://根据类型id获得 类型对象
                    globalType=globalTypeService.getById(taskApprovalItems.getTypeId());
                    break;
                case 3://根据流程定义key获得 流程对象
//                    bpmDefinition=bpmDefinitionService.getByDefKey(taskApprovalItems.getDefKey()).get(0);
                    taskApprovalItems=taskApprovalItemsService.getById(id);
                    break;
                case 4://类型4的为个人常用语，只有内容的编辑，不用进行其他操作
                    break;
            }
        }
        //获取所有的流程分类，处理流程分类
        String isAdmin = RequestUtil.getString(request, "isAdmin");

        Map map = new HashMap();
        map.put("isAdmin", isAdmin);
        map.put("taskApprovalItems",taskApprovalItems);
        map.put("globalType",globalType);
        map.put("isAdmin", isAdmin);
        map.put("bpmDefinition",bpmDefinition);

        return  new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"编辑常用语成功", map);


    }

    /**
     *保存常用语
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("save")
    @Action(description="",
            detail=""
    )
    @ResponseBody
    public ResultVo save(HttpServletRequest request, HttpServletResponse response, TaskApprovalItems taskApprovalItems, BindingResult bindResult)
            throws Exception
    {
        Long currUserId=ContextUtil.getCurrentUserId();
        String approvalItem = RequestUtil.getString(request, "approvalItem");
        Short type=RequestUtil.getShort(request, "type", (short)1);
        String typeId=RequestUtil.getString(request, "flowTypeId");
        String defKey=RequestUtil.getString(request, "defKey");
        Long itemId=RequestUtil.getLong(request,"itemId",0L);
        String typeName=RequestUtil.getString(request, "typeName");
        if(type==0){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请选择常用语类别！");
        }
        if(approvalItem.length() ==0){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请填写常用语！");
        }
        try {
            if(itemId>0){
                if((taskApprovalItemsService.getByTypeCount(approvalItem,type)&&TaskApprovalItems.TYPE_GLOBAL.equals(type))
                        ||taskApprovalItemsService.getByTypeAndUserIdCount(approvalItem,ContextUtil.getCurrentUserId())
                        ||taskApprovalItemsService.getBydefKeyCount(approvalItem,type,typeName)&&TaskApprovalItems.TYPE_FLOW.equals(type)){
                    return  new ResultVo(ResultVo.COMMON_STATUS_FAILED,"该常用语已存在，请重新输入");
                }
                else {
                    if(TaskApprovalItems.TYPE_USER.equals(type)){
                        taskApprovalItemsService.updateTaskApproval(itemId,approvalItem, type, typeId, defKey,ContextUtil.getCurrentUserId());
                    }else if(TaskApprovalItems.TYPE_FLOW.equals(type)){
                        taskApprovalItemsService.updateTaskApproval(itemId,approvalItem, type, typeId, typeName,null);
                    }else {
                        taskApprovalItemsService.updateTaskApproval(itemId,approvalItem, type, typeId, defKey, null);
                    }

                }
            }  
            else {
                if((taskApprovalItemsService.getByTypeCount(approvalItem,type)&&TaskApprovalItems.TYPE_GLOBAL.equals(type))
                        ||taskApprovalItemsService.getByTypeAndUserIdCount(approvalItem,currUserId)
                        ||taskApprovalItemsService.getBydefKeyCount(approvalItem,type,defKey)&&TaskApprovalItems.TYPE_FLOW.equals(type)) {
                    return  new ResultVo(ResultVo.COMMON_STATUS_FAILED,"该常用语已存在，请重新输入");
                }
                else{
                    if(TaskApprovalItems.TYPE_USER.equals(type)){
                        taskApprovalItemsService.addTaskApproval(approvalItem, type, typeId, defKey, ContextUtil.getCurrentUserId());
                    }else {
                        taskApprovalItemsService.addTaskApproval(approvalItem, type, typeId, defKey,null);
                    }

                }

            }

            return  new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"保存常用语成功");
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            String str = MessageUtil.getMessage();
            if (StringUtil.isNotEmpty(str)) {
                return  new ResultVo(ResultVo.COMMON_STATUS_FAILED,"保存常用语失败:"+str);
            } else {
                return  new ResultVo(ResultVo.COMMON_STATUS_FAILED,"保存常用语失败");
            }
        }

    }

    @RequestMapping("del")
    @ResponseBody
    public ResultVo del(HttpServletRequest request,HttpServletResponse response) throws Exception
    {

        try{
            Long[] lAryId =RequestUtil.getLongAryByStr(request, "itemId");
            taskApprovalItemsService.delByIds(lAryId);
            return  new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"删除常用语成功");
        }
        catch(Exception ex){
            return  new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败:" + ex.getMessage());
        }
    }

    /**
     * 常用语排序列表。
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    @RequestMapping("sortList")
    @Action(description="常用语排序列表", detail="常用语排序列表")
    @ResponseBody
    public ResultVo sortList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long itemId = RequestUtil.getLong(request, "itemId", -1);
        Short type = RequestUtil.getShort(request, "type");
        Long currUserId = ContextUtil.getCurrentUserId();
        TaskApprovalItems taskApprovalItems = taskApprovalItemsService.getById(itemId);
        List<TaskApprovalItems> list = taskApprovalItemsService.getByType(taskApprovalItems.getType());
        List<TaskApprovalItems> list1 = taskApprovalItemsService.getByTypeAndUserId(currUserId);
        List<TaskApprovalItems> relist = new ArrayList<TaskApprovalItems>();
        if(TaskApprovalItems.TYPE_USER.equals(taskApprovalItems.getType())){
            if(itemId != 0 && list1.size() > 0 ){
                for (TaskApprovalItems taskApprovalItem1 : list1) {
                    if (taskApprovalItems.getType().equals(type) ) {
                        relist.add(taskApprovalItem1);
                    }
                }
                list1.removeAll(list1);
                list1.addAll(relist);
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取同级常用语列表成功！", list1);
            }

        }else {
            if (itemId != 0 && list.size() > 0) {

                for (TaskApprovalItems taskApprovalItem : list) {
                    if (taskApprovalItems.getType().equals(type)) {
                        relist.add(taskApprovalItem);
                    }
                }
                list1.removeAll(list);
                list1.addAll(relist);
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取同级常用语列表成功！", list);
            }
        }
        return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取同级常用语列表失败！");
    }

    /**
     * 常用语排序。
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("sort")
    @Action(description="常用语排序", detail="常用语排序")
    @ResponseBody
    public ResultVo sort(HttpServletRequest request,HttpServletResponse response) throws Exception{

        Long[] lAryId =RequestUtil.getLongAryByStr(request, "itemIds");
        if(lAryId == null){
            logger.error("常用语排序失败：常用语itemId为空！");
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "常用语排序失败：请求参数错误！");
        }
        try {
            taskApprovalItemsService.sort(lAryId);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "常用语排序成功！");
        } catch (Exception e) {
            logger.error("常用语排序失败：" + e.getMessage(), e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "常用语排序失败：" + e.getMessage());
        }

    }
    
}
