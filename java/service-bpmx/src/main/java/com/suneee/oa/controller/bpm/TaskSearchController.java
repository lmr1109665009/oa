package com.suneee.oa.controller.bpm;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.model.bpm.GlobalTypeAmount;
import com.suneee.oa.model.bpm.ProcessRunAmount;
import com.suneee.oa.model.bpm.TaskAmount;
import com.suneee.oa.service.bpm.BpmExtService;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.AuthorizeRight;
import com.suneee.platform.model.bpm.BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <pre>
 * 对象功能:流程左侧分类接口
 * 开发公司:深圳象羿
 * 开发人员:子华
 * 创建时间:2011-12-12 13:12:13
 * </pre>
 */
@Controller
@RequestMapping("/api/task/search/")
@Action(ownermodel = SysAuditModelType.PROCESS_MANAGEMENT)
public class TaskSearchController extends BaseController {
    @Resource
    private GlobalTypeService globalTypeService;
    @Resource
    private BpmDefAuthorizeService bpmDefAuthorizeService;
    @Resource
    private BpmDefinitionService bpmDefinitionService;
    @Resource
    private BpmExtService bpmExtService;
    @Resource
    private ProcessRunService processRunService;
    @Resource
    private BpmFormTableService formTableService;
    @Resource
    private BpmTaskExeService bpmTaskExeService;
    @Resource
    private BpmProTransToService bpmProTransToService;
    @Resource
    private BpmFormDefService bpmFormDefService;
	@Resource
	private BpmService bpmService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;

    //是否需要加载顶级分类
    private static final boolean CATEGORY_HAS_ROOT=true;


    /**
     * 根据流程分类获取流程定义
     * @param request
     * @return
     */
    @RequestMapping("flowByCate")
    @ResponseBody
    public ResultVo flowByCate(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, CATEGORY_HAS_ROOT);
        QueryFilter filter = new QueryFilter(request, false);
        //增加流程分管授权的启动权限分配查询判断
        Long userId = ContextUtil.getCurrentUserId();
        String isNeedRight = "";
        Map<String, AuthorizeRight> authorizeRightMap = null;
        if (!ContextUtil.isSuperAdmin()) {
            isNeedRight = "yes";
            //获得流程分管授权与用户相关的信息
            Map<String, Object> actRightMap = bpmDefAuthorizeService.getActRightByUserMap(userId, BPMDEFAUTHORIZE_RIGHT_TYPE.START, false, false);
            //获得流程分管授权与用户相关的信息集合的流程KEY
            String actRights = (String) actRightMap.get("authorizeIds");
            filter.addFilter("actRights", actRights);
        }
        filter.addFilter("isNeedRight", isNeedRight);
        filter.addFilter("typeIdList", BpmUtil.getTypeIdList(globalTypeList));
        List<GlobalTypeAmount> list = (List<GlobalTypeAmount>) bpmDefinitionService.getAllCountByCate(filter);

        List<GlobalType> globalTypeExts=convertGlobalTypeExt(globalTypeList);
        for (GlobalTypeAmount typeAmount:list){
            GlobalType globalType=findType(globalTypeExts,typeAmount);
            processTypeCount(globalType,typeAmount);
        }
        List<GlobalType> typeList=new CopyOnWriteArrayList<GlobalType>(globalTypeExts);
        updateFlowCount(typeList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取新建流程分类数据成功",typeList);
    }

    /**
     * 获取流程定义列表
     * @param request
     * @param typeIds
     * @return
     */
    private List<BpmDefinition> getBpmDefList(HttpServletRequest request, Set<Long> typeIds) {
        QueryFilter filter = new QueryFilter(request, false);
        String keyword=RequestUtil.getString(request,"keyword");
        if (StringUtil.isNotEmpty(keyword)){
            filter.addFilter("subject","%"+keyword+"%");
        }
        return bpmDefinitionService.getMyDefList(filter, typeIds);
    }

    /**
     * 更新流程分类流程数量
     * @param globalTypeList
     */
    private void updateFlowCount(List<GlobalType> globalTypeList) {
        for (GlobalType globalType:globalTypeList){
            GlobalTypeExt typeExt= (GlobalTypeExt) globalType;
            if (typeExt.getFlowSet()!=null&&typeExt.getFlowSet().size()>0){
                updateGlobalTypeCount(typeExt,globalTypeList,typeExt.getFlowCount());
            }
        }
        removeEmptyType(globalTypeList);

    }
    private void  updateGlobalTypeCount(List<GlobalType> globalTypeList){
        for (GlobalType globalType:globalTypeList){
            GlobalTypeExt typeExt= (GlobalTypeExt) globalType;
            if (typeExt.getFlowCount()==0){
                continue;
            }
            updateGlobalTypeCount(typeExt,globalTypeList,typeExt.getFlowCount());
        }
        removeEmptyType(globalTypeList);
    }

    /**
     * 删除分类下数据为空的对象
     * @param globalTypeList
     */
    private void removeEmptyType(List<GlobalType> globalTypeList){
        for (GlobalType globalType:globalTypeList){
            GlobalTypeExt typeExt= (GlobalTypeExt) globalType;
            if (typeExt.getFlowCount()==0){
                globalTypeList.remove(globalType);
            }
        }
    }

    /**
     * 更新流程分类的流程数量
     * @param currentType
     * @param globalTypeList
     * @param count
     */
    private void updateGlobalTypeCount(GlobalType currentType,List<GlobalType> globalTypeList,int count){
        if (currentType==null||currentType.getParentId()==null||currentType.getParentId()==0){
            return;
        }
        for (GlobalType typeItem:globalTypeList){
            if (!currentType.getParentId().equals(typeItem.getTypeId())){
                continue;
            }
            GlobalTypeExt typeExt= (GlobalTypeExt) typeItem;
            typeExt.setFlowCount(typeExt.getFlowCount()+count);
            updateGlobalTypeCount(typeItem,globalTypeList,count);
            break;
        }
    }

    /**
     * 处理流程分类计算流程数量
     * @param globalType
     * @param bpmDef
     */
    private void processTypeCount(GlobalType globalType, BpmDefinition bpmDef) {
        if (globalType==null){
            return;
        }
        GlobalTypeExt typeExt= (GlobalTypeExt) globalType;
        typeExt.setFlowCount(typeExt.getFlowCount()+1);
        typeExt.addFlow(bpmDef);
    }
    private void processTypeCount(GlobalType globalType, GlobalTypeAmount amount) {
        if (globalType==null){
            return;
        }
        GlobalTypeExt typeExt= (GlobalTypeExt) globalType;
        typeExt.setFlowCount(typeExt.getFlowCount()+amount.getTotal());
    }

    /**
     * 查找分类
     * @param globalTypeExts
     * @param bpmDef
     * @return
     */
    private GlobalType findType(List<GlobalType> globalTypeExts, BpmDefinition bpmDef) {
        for (GlobalType globalType:globalTypeExts){
            if (globalType.getTypeId().equals(bpmDef.getTypeId())){
                return globalType;
            }
        }
        return null;
    }
    private GlobalType findType(List<GlobalType> globalTypeExts, GlobalTypeAmount amount) {
        for (GlobalType globalType:globalTypeExts){
            if (globalType.getTypeId().equals(amount.getTypeId())){
                return globalType;
            }
        }
        return null;
    }


    /**
     * 流程分类拓展类
     */
    static class GlobalTypeExt extends GlobalType{
        //分类下面的数据大小
        private int flowCount=0;
        //分类下面的数据
        private Set<Object> flowSet;

        public int getFlowCount() {
            return flowCount;
        }

        public void setFlowCount(int flowCount) {
            this.flowCount = flowCount;
        }

        public Set<Object> getFlowSet() {
            return flowSet;
        }

        public void setFlowSet(Set<Object> flowSet) {
            this.flowSet = flowSet;
        }

        /**
         * 添加数据对象
         * @param obj
         */
        public void addFlow(Object obj){
            if (this.flowSet==null){
                flowSet=new HashSet<Object>();
            }
            flowSet.add(obj);
        }
    }
    /**
     * 移除当前数组的元素，并转换Long
     *
     * @param array
     * @param element
     * @return
     */
    private Long[] removeElementToLong(String[] array, Long element) {
        if (ArrayUtils.isEmpty(array))
            return null;
        Long[] l = new Long[array.length];
        for (int i = 0; i < array.length; i++) {
            l[i] = Long.parseLong(array[i]);
        }
        return (Long[]) ArrayUtils.removeElement(l, element);

    }

    /**
     * 根据流程分类获取待办事宜
     * @param request
     * @return
     */
    @RequestMapping("pendingMattersByCate")
    @ResponseBody
    public ResultVo pendingMattersByCate(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        Long userId = ContextUtil.getCurrentUserId();
        //流程分类列表
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, CATEGORY_HAS_ROOT);
        Set<Long> typeIdList=BpmUtil.getTypeIdList(globalTypeList);
        QueryFilter filter = new QueryFilter(request, false);
        filter.addFilter("userId",userId);
        filter.addFilter("typeIdList",typeIdList);
        //流程定义列表
        List<BpmDefinition> bpmDefList = getBpmDefList(request,typeIdList);
        List<TaskAmount> amountList=bpmExtService.getFlowTasksCount(filter);
        List<BpmDefinitionExt> list=new ArrayList<BpmDefinitionExt>();
        for (TaskAmount amountItem:amountList){
            for (BpmDefinition bpmItem:bpmDefList){
                if (amountItem.getDefId().equals(bpmItem.getDefId())){
                    BpmDefinitionExt bpmExt=new BpmDefinitionExt();
                    BeanUtils.copyProperties(bpmExt,bpmItem);
                    bpmExt.setTotal(amountItem.getTotal());
                    bpmExt.setNotRead(amountItem.getNotRead());
                    list.add(bpmExt);
                    break;
                }
            }
        }

        List<GlobalType> globalTypeExts=convertGlobalTypeExt(globalTypeList);

        for (BpmDefinition bpmDef:list){
            GlobalType globalType=findType(globalTypeExts,bpmDef);
            processTypeCount(globalType,bpmDef);
        }
        List<GlobalType> typeList=new CopyOnWriteArrayList<GlobalType>(globalTypeExts);
        updateFlowCount(typeList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取待办事宜数据分类成功",typeList);
    }

    /**
     * 流程定义拓展类
     */
    static class BpmDefinitionExt extends BpmDefinition{
        //任务总数量
        private int total=0;
        //未读任务
        private int notRead=0;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getNotRead() {
            return notRead;
        }

        public void setNotRead(int notRead) {
            this.notRead = notRead;
        }
    }
    /**
     * 根据流程分类获取已办事宜
     * @param request
     * @return
     */
    @RequestMapping("alreadyMattersByCate")
    @ResponseBody
    public ResultVo alreadyMattersByCate(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        Long userId = ContextUtil.getCurrentUserId();
        //流程分类列表
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, CATEGORY_HAS_ROOT);
        QueryFilter filter = new QueryFilter(request, false);
        Set<Long> typeIdList=BpmUtil.getTypeIdList(globalTypeList);
        //流程定义列表
        List<BpmDefinition> bpmDefList = getBpmDefList(request,typeIdList);
        filter.addFilter("assignee", userId);
        filter.addFilter("typeIdList",typeIdList);
        List<ProcessRunAmount> amountList=processRunService.getFlowAlreadyMattersCount(filter);
        List<BpmDefinitionExt> list=new ArrayList<BpmDefinitionExt>();
        processAmountData(bpmDefList, amountList, list);

        List<GlobalType> globalTypeExts=convertGlobalTypeExt(globalTypeList);

        for (BpmDefinition bpmDef:list){
            GlobalType globalType=findType(globalTypeExts,bpmDef);
            processTypeCount(globalType,bpmDef);
        }
        List<GlobalType> typeList=new CopyOnWriteArrayList<GlobalType>(globalTypeExts);
        updateFlowCount(typeList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取已办事宜数据分类成功",typeList);
    }

    /**
     * 根据流程分类获取我的申请
     * @param request
     * @return
     */
    @RequestMapping("myRequestByCate")
    @ResponseBody
    public ResultVo myRequestByCate(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        Long userId = ContextUtil.getCurrentUserId();
        //流程分类列表
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, CATEGORY_HAS_ROOT);
        QueryFilter filter = new QueryFilter(request, false);
        Set<Long> typeIdList=BpmUtil.getTypeIdList(globalTypeList);
        //流程定义列表
        List<BpmDefinition> bpmDefList = getBpmDefList(request,typeIdList);
        filter.addFilter("creatorId", userId);
        filter.addFilter("typeIdList",typeIdList);
        List<ProcessRunAmount> amountList=processRunService.getFlowMyRequestCount(filter);
        List<BpmDefinitionExt> list=new ArrayList<BpmDefinitionExt>();
        processAmountData(bpmDefList, amountList, list);

        List<GlobalType> globalTypeExts=convertGlobalTypeExt(globalTypeList);

        for (BpmDefinition bpmDef:list){
            GlobalType globalType=findType(globalTypeExts,bpmDef);
            processTypeCount(globalType,bpmDef);
        }
        List<GlobalType> typeList=new CopyOnWriteArrayList<GlobalType>(globalTypeExts);
        updateFlowCount(typeList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取我的申请数据分类成功",typeList);
    }

    /**
     * 根据流程分类获取我的申请
     * @param request
     * @return
     */
    @RequestMapping("myCompletedByCate")
    @ResponseBody
    public ResultVo myCompletedByCate(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        Long userId = ContextUtil.getCurrentUserId();
        //流程分类列表
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, CATEGORY_HAS_ROOT);
        QueryFilter filter = new QueryFilter(request, false);
        Set<Long> typeIdList=BpmUtil.getTypeIdList(globalTypeList);
        //流程定义列表
        List<BpmDefinition> bpmDefList = getBpmDefList(request,typeIdList);
        filter.addFilter("creatorId", userId);
        filter.addFilter("typeIdList",typeIdList);
        List<ProcessRunAmount> amountList=processRunService.getFlowMyCompletedCount(filter);
        List<BpmDefinitionExt> list=new ArrayList<BpmDefinitionExt>();
        processAmountData(bpmDefList, amountList, list);

        List<GlobalType> globalTypeExts=convertGlobalTypeExt(globalTypeList);

        for (BpmDefinition bpmDef:list){
            GlobalType globalType=findType(globalTypeExts,bpmDef);
            processTypeCount(globalType,bpmDef);
        }
        List<GlobalType> typeList=new CopyOnWriteArrayList<GlobalType>(globalTypeExts);
        updateFlowCount(typeList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取我的办结数据分类成功",typeList);
    }

    /**
     * 根据流程分类获取流程实例
     * @param request
     * @return
     */
    @RequestMapping("processInsByCate")
    @ResponseBody
    public ResultVo processInsByCate(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        Long userId = ContextUtil.getCurrentUserId();
        //流程分类列表
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, CATEGORY_HAS_ROOT);
        QueryFilter filter = new QueryFilter(request, false);
        Set<Long> typeIdList=BpmUtil.getTypeIdList(globalTypeList);

		// 过滤掉草稿实例
		filter.addFilter("exceptStatus", 4);
		// 过滤流程定义状态为"禁用实例" 的流程实例
		filter.addFilter("exceptDefStatus", 3);
		// 增加按新的流程分管授权中任务类型的权限获取流程的任务
		String isNeedRight = "";
		if (!ContextUtil.isSuperAdmin()) {
			isNeedRight = "yes";
			// 获得流程分管授权与用户相关的信息
			Map<String, Object> actRightMap = bpmDefAuthorizeService.getActRightByUserMap(userId, BPMDEFAUTHORIZE_RIGHT_TYPE.INSTANCE, true, false);
			// 获得流程分管授权与用户相关的信息集合的流程KEY
			String actRights = (String) actRightMap.get("authorizeIds");
			filter.addFilter("actRights", actRights);
		}
		filter.addFilter("isNeedRight", isNeedRight);
		filter.addFilter("typeIdList",typeIdList);

        //流程定义列表
        List<BpmDefinition> bpmDefList = getBpmDefList(request,typeIdList);
        //流程实例根据流程进行统计数量
        List<ProcessRunAmount> amountList=processRunService.getProcessInsCount(filter);

        List<BpmDefinitionExt> list=new ArrayList<BpmDefinitionExt>();
        processAmountData(bpmDefList, amountList, list);

        List<GlobalType> globalTypeExts=convertGlobalTypeExt(globalTypeList);

        for (BpmDefinition bpmDef:list){
            GlobalType globalType=findType(globalTypeExts,bpmDef);
            processTypeCount(globalType,bpmDef);
        }
        List<GlobalType> typeList=new CopyOnWriteArrayList<GlobalType>(globalTypeExts);
        updateFlowCount(typeList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程实例数据分类成功",typeList);
    }

	/**
	 * 根据流程分类获取流程任务
	 * @param request
	 * @return
	 */
	@RequestMapping("processTaskByCate")
	@ResponseBody
	public ResultVo processTaskByCate(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
		Long userId = ContextUtil.getCurrentUserId();
		//流程分类列表
		List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, CATEGORY_HAS_ROOT);
		QueryFilter filter = new QueryFilter(request, false);
		Set<Long> typeIdList=BpmUtil.getTypeIdList(globalTypeList);

		// 增加按新的流程分管授权中任务类型的权限获取流程的任务
		String isNeedRight = "";
		if (!ContextUtil.isSuperAdmin()) {
			isNeedRight = "yes";
			// 获得流程分管授权与用户相关的信息
			Map<String, Object> actRightMap = bpmDefAuthorizeService.getActRightByUserMap(userId, BPMDEFAUTHORIZE_RIGHT_TYPE.TASK, true, false);
			// 获得流程分管授权与用户相关的信息集合的流程KEY
			String actRights = (String) actRightMap.get("authorizeIds");
			filter.addFilter("actRights", actRights);
		}
		filter.addFilter("isNeedRight", isNeedRight);
        filter.addFilter("typeIdList",typeIdList);

		//流程定义列表
		List<BpmDefinition> bpmDefList = getBpmDefList(request,typeIdList);
		//流程实例根据流程进行统计数量
		List<ProcessRunAmount> amountList=bpmService.getProcessTaskCount(filter);

		List<BpmDefinitionExt> list=new ArrayList<BpmDefinitionExt>();
		processAmountData(bpmDefList, amountList, list);

		List<GlobalType> globalTypeExts=convertGlobalTypeExt(globalTypeList);

		for (BpmDefinition bpmDef:list){
			GlobalType globalType=findType(globalTypeExts,bpmDef);
			processTypeCount(globalType,bpmDef);
		}
		List<GlobalType> typeList=new CopyOnWriteArrayList<GlobalType>(globalTypeExts);
		updateFlowCount(typeList);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程任务数据分类成功",typeList);
	}

    private void processAmountData(List<BpmDefinition> bpmDefList, List<ProcessRunAmount> amountList, List<BpmDefinitionExt> list) throws IllegalAccessException, InvocationTargetException {
        for (ProcessRunAmount amountItem:amountList){
            for (BpmDefinition bpmItem:bpmDefList){
                if (amountItem.getDefId().equals(bpmItem.getDefId())){
                    BpmDefinitionExt bpmExt=new BpmDefinitionExt();
                    BeanUtils.copyProperties(bpmExt,bpmItem);
                    bpmExt.setTotal(amountItem.getTotal());
                    list.add(bpmExt);
                    break;
                }
            }
        }
    }

    /**
     * 根据流程分类获取自定义表(定义字段)
     * @param request
     * @return
     */
    @RequestMapping("bpmTableByCate")
    @ResponseBody
    public ResultVo bpmTableByCate(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FORM_TABLE, CATEGORY_HAS_ROOT);
        QueryFilter filter = new QueryFilter(request, false);
        Long glTypeId = RequestUtil.getLong(request, "typeId", 0);
        if(glTypeId == 0){
            filter.addFilter("ecode", ContextSupportUtil.getCurrentEnterpriseCode());
        }
        String keyword=RequestUtil.getString(request,"keyword");
        if (StringUtil.isNotEmpty(keyword)){
            filter.addFilter("cateName","%"+keyword+"%");
        }
        List<GlobalTypeAmount> amountList = formTableService.getAllCount(filter);
        List<GlobalType> globalTypeExts=convertGlobalTypeExt(globalTypeList);

        for (GlobalTypeAmount amountItem:amountList){
            GlobalType globalType=findType(globalTypeExts,amountItem);
            processTypeCount(globalType,amountItem);
        }
        List<GlobalType> typeList=new CopyOnWriteArrayList<GlobalType>(globalTypeExts);
        updateGlobalTypeCount(typeList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取自定义表(定义字段)分类数据成功",typeList);
    }

    /**
     * 根据流程分类获取自定义表单(左侧分类)
     * @param request
     * @return
     */
    @RequestMapping("formDefByCate")
    @ResponseBody
    public ResultVo formDefByCate(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FORM, CATEGORY_HAS_ROOT);
        QueryFilter filter = new QueryFilter(request, false);
        List<GlobalTypeAmount> amountList = bpmFormDefService.getAllCount(filter);
        List<GlobalType> globalTypeExts=convertGlobalTypeExt(globalTypeList);
        for (GlobalTypeAmount amountItem:amountList){
            GlobalType globalType=findType(globalTypeExts,amountItem);
            processTypeCount(globalType,amountItem);
        }
        List<GlobalType> typeList=new CopyOnWriteArrayList<GlobalType>(globalTypeExts);
        updateGlobalTypeCount(typeList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取自定义表单左侧分类数据成功",typeList);
    }

    /**
     * 转换成全局分类拓展对象
     * @param globalTypeList
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private List<GlobalType> convertGlobalTypeExt(List<GlobalType> globalTypeList) throws InvocationTargetException, IllegalAccessException {
        List<GlobalType> globalTypeExts=new ArrayList<GlobalType>();
        for (GlobalType globalTypeItem:globalTypeList){
            GlobalTypeExt typeExt=new GlobalTypeExt();
            BeanUtils.copyProperties(typeExt,globalTypeItem);
            globalTypeExts.add(typeExt);
        }
        return globalTypeExts;
    }

    /**
     * 根据流程定义ID获取转办、代理事宜 流程分类
     *
     * @Methodname: getByCatKeyForBpmAccording
     * @Discription:
     * @param request
     * @return
     */
    @RequestMapping("accordingMattersByCate")
    @ResponseBody
    public ResultVo accordingMattersByCate(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        Long userId = ContextUtil.getCurrentUserId();
        //流程分类列表
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, CATEGORY_HAS_ROOT);
        QueryFilter filter = new QueryFilter(request, false);
        Set<Long> typeIdList=BpmUtil.getTypeIdList(globalTypeList);
        //流程定义列表
        List<BpmDefinition> bpmDefList = getBpmDefList(request,typeIdList);
        if(!ContextUtil.isSuperAdmin()){
            filter.addFilter("ownerId", userId);
        }
        filter.addFilter("typeIdList",typeIdList);
        List<ProcessRunAmount> amountList = bpmTaskExeService.getAccordingMattersCount(filter);
        List<BpmDefinitionExt> list=new ArrayList<BpmDefinitionExt>();
        processAmountData(bpmDefList, amountList, list);
        List<GlobalType> globalTypeExts=convertGlobalTypeExt(globalTypeList);
        for (BpmDefinition bpmDef:list){
            GlobalType globalType=findType(globalTypeExts,bpmDef);
            processTypeCount(globalType,bpmDef);
        }
        List<GlobalType> typeList=new CopyOnWriteArrayList<GlobalType>(globalTypeExts);
        updateFlowCount(typeList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取代理事宜流程分类成功",typeList);
    }


    /**
     * 加签(流转)事宜 流程分类
     * @param request
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @RequestMapping("proTransMattersByCate")
    @ResponseBody
    public ResultVo proTransMattersByCate(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        Long userId = ContextUtil.getCurrentUserId();
        //流程分类列表
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, CATEGORY_HAS_ROOT);
        QueryFilter filter = new QueryFilter(request, false);
        Set<Long> typeIdList=BpmUtil.getTypeIdList(globalTypeList);
        //流程定义列表
        List<BpmDefinition> bpmDefList = getBpmDefList(request,typeIdList);
        filter.addFilter("createUserId", userId);
        filter.addFilter("typeIdList",typeIdList);
        List<ProcessRunAmount> amountList = bpmProTransToService.getTransMattersCount(filter);
        List<BpmDefinitionExt> list=new ArrayList<BpmDefinitionExt>();
        processAmountData(bpmDefList, amountList, list);
        List<GlobalType> globalTypeExts=convertGlobalTypeExt(globalTypeList);
        for (BpmDefinition bpmDef:list){
            GlobalType globalType=findType(globalTypeExts,bpmDef);
            processTypeCount(globalType,bpmDef);
        }
        List<GlobalType> typeList=new CopyOnWriteArrayList<GlobalType>(globalTypeExts);
        updateFlowCount(typeList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取加签(流转)事宜 流程分类成功",typeList);
    }

    /**
     * 根据流程分类获取流定义
     * @param request
     * @return
     */
    @RequestMapping("bpmDefinitionByCate")
    @ResponseBody
    public ResultVo bpmDefinitionByCate(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, CATEGORY_HAS_ROOT);
        QueryFilter filter = new QueryFilter(request, false);
        Long typeId = RequestUtil.getLong(request, "typeId", 0);
        if (typeId > 1) {
            GlobalType globalType = globalTypeService.getById(typeId);
            if (globalType != null) {
                filter.getFilters().put("nodePath",
                        globalType.getNodePath() + "%");
            }
        }
        // 增加流程分管授权查询判断
        Long userId = ContextUtil.getCurrentUserId();
        String isNeedRight = "";
        Map<String, AuthorizeRight> authorizeRightMap = null;
        if (!ContextUtil.isSuperAdmin()) {
            isNeedRight = "yes";
            // 获得流程分管授权与用户相关的信息
            Map<String, Object> actRightMap = bpmDefAuthorizeService
                    .getActRightByUserMap(userId,
                            BPMDEFAUTHORIZE_RIGHT_TYPE.MANAGEMENT, true, true);
            // 获得流程分管授权与用户相关的信息集合的流程KEY
            String actRights = (String) actRightMap.get("authorizeIds");
            filter.addFilter("actRights", actRights);
            // 获得流程分管授权与用户相关的信息集合的流程权限内容
            authorizeRightMap = (Map<String, AuthorizeRight>) actRightMap
                    .get("authorizeRightMap");
        }
        filter.addFilter("isNeedRight", isNeedRight);
        if(typeId == 0){
            filter.addFilter("typeIdList", BpmUtil.getTypeIdList(globalTypeList));
        }
        // 查询流程列表
        List<GlobalTypeAmount> list = (List<GlobalTypeAmount>) bpmDefinitionService.getAllCountByCate(filter);

        List<GlobalType> globalTypeExts=convertGlobalTypeExt(globalTypeList);
        for (GlobalTypeAmount typeAmount:list){
            GlobalType globalType=findType(globalTypeExts,typeAmount);
            processTypeCount(globalType,typeAmount);
        }
        List<GlobalType> typeList=new CopyOnWriteArrayList<GlobalType>(globalTypeExts);
        updateGlobalTypeCount(typeList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程定义左侧分类数据成功",typeList);
    }
}
