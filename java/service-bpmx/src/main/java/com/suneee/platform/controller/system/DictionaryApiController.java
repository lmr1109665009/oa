package com.suneee.platform.controller.system;


import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象功能:数据字典 控制器类
 */
@Controller
@RequestMapping("/api/system/dictionary/")
@Action(ownermodel = SysAuditModelType.SYSTEM_SETTING)
public class DictionaryApiController extends BaseController {


    @Resource
    private DictionaryService dictionaryService;
    @Resource
    private SysOrgService orgService;
    @Resource
    private GlobalTypeService globalTypeService;
    @Resource
    private UserPositionService userPositionService;
    @Resource
    private EnterpriseinfoService enterpriseinfoService;

    /**
    * 获取数据字典列表
    *
    *
    */
    @RequestMapping("listJson")
    @ResponseBody
    public ResultVo listJson(HttpServletRequest request ,HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = new QueryFilter(request, true);
		long typeId = RequestUtil.getLong(request, "typeId");
		if(typeId != 0l){
			GlobalType globalType = globalTypeService.getById(typeId);
			queryFilter.getFilters().put("nodePath",globalType.getNodePath()+"%");
		}
		Boolean isSuperAdmin = ContextUtil.isSuperAdmin();
        //获取当前用户企业编码
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        queryFilter.getFilters().put("enterpriseCode",enterpriseCode);
		PageList<GlobalType> globalTypeList = (PageList<GlobalType>) globalTypeService.getByQueryfilter(queryFilter);
		for(GlobalType gt:globalTypeList){
			List<Dictionary> dictionaryList = dictionaryService.getByTypeId(gt.getTypeId(), false);
			gt.setDicList(dictionaryList);
		}
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取字典数据列表成功！", PageUtil.getPageVo(globalTypeList));

    }

    /**
     * 根据类型获取字典。
     *重置
     * @param request
     * @return
     */
    @RequestMapping("getByTypeId")
    @ResponseBody
    public List<Dictionary> getByTypeId(HttpServletRequest request) {
        Long typeId = RequestUtil.getLong(request, "typeId");
        String itemname = RequestUtil.getString(request, "itemname");
        Long userId = ContextUtil.getCurrentUserId();
        SysOrg sysOrg = orgService.getByUserId(userId);
        String eid = sysOrg.getOrgCode();
        List<Dictionary> list = dictionaryService.getBytypeIdAndItemName(typeId, itemname, true);
        //List<Dictionary> list= dictionaryService.getByTypeId(typeId, true);
        return list;

    }

    /**
     * 编辑字典数据。
     *
     * @param request
     * @return
     */
    @RequestMapping("save")
    @ResponseBody

    public ResultVo save(HttpServletRequest request, HttpServletResponse response, Dictionary dictionary) {
        try {
            Long dicId = dictionary.getDicId();
            Long typeId = dictionary.getTypeId();
            String itemKey = dictionary.getItemKey();
            // 判断dicId/typeId/itemKey是否已经存在
            boolean isItemKeyExistsForUpdate = dictionaryService.isItemKeyExistsForUpdate(dicId, typeId, itemKey);
            if (isItemKeyExistsForUpdate) {
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "该数据字典已经存在，请重新输入！");
            }
        } catch (Exception e) {
            logger.error("保存数据字典失败：" + e.getMessage(), e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存数据字典失败：" + e.getMessage());
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "保存数据字典成功");
    }


    /**
     * 删除系统分类。
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws Exception
     */
    @RequestMapping("delSys")
    @Action(description = "删除系统分类", execOrder = ActionExecOrder.BEFORE, detail = "删除系统分类" + "<#assign entity=globalTypeService.getById(Long.valueOf(typeId))/>" + "【${entity.typeName}】")
    public void delSys(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultMessage resultMessage = null;
        try {
            Long typeId = RequestUtil.getLong(request, "typeId");
            globalTypeService.delByTypeId(typeId);
            resultMessage = new ResultMessage(ResultMessage.Success, "删除系统分类成功");
            writeResultMessage(response.getWriter(), resultMessage);
        } catch (Exception e) {
            String str = MessageUtil.getMessage();
            if (StringUtil.isNotEmpty(str)) {
                resultMessage = new ResultMessage(ResultMessage.Fail, "删除系统分类失败:" + str);
                writeResultMessage(response.getWriter(), resultMessage);
            } else {
                String message = ExceptionUtil.getExceptionMessage(e);
                resultMessage = new ResultMessage(ResultMessage.Fail, message);
                writeResultMessage(response.getWriter(), resultMessage);
            }
        }

    }

    /**
     * 删除数据字典
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("del")
    @Action(description = "删除数据字典", execOrder = ActionExecOrder.BEFORE, detail = "删除数据字典" + "<#list StringUtils.split(dicId,\",\") as item>" + "<#assign entity=dictionaryService.getById(Long.valueOf(item))/>" + "【${entity.itemName}】" + "</#list>")
    @ResponseBody
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception {
     
        try {
            
            Long[] dicIds = RequestUtil.getLongAryByStr(request, "dicId");
            Long[] typeIds = RequestUtil.getLongAryByStr(request, "typeId");
            dictionaryService.delByIds(dicIds);
            dictionaryService.delByIds(typeIds);
            return  new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除数据字典成功");
        } catch (Exception e) {
            return  new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除数据字典失败");
        }
    }



    /**
     * 获取数据字典详情
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    @RequestMapping("details")
    @Action(description="获取数据字典详情",detail="获取数据字典详情", execOrder=ActionExecOrder.AFTER, exectype="管理日志",
            ownermodel=SysAuditModelType.USER_MANAGEMENT)
    @ResponseBody
    public ResultVo details(HttpServletRequest request, HttpServletResponse response) throws Exception{
       Long typeId = RequestUtil.getLong(request, "typeId");
		GlobalType globalType = globalTypeService.getById(typeId);
		List<Dictionary> dictionaryList = dictionaryService.getByNodepath(globalType.getTypeId().toString());
		globalType.setDicList(dictionaryList);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取数据字典详情成功！", globalType);
    }

    /**
     * 归类
     *
     * @param request
     */
    @RequestMapping("file")
    @ResponseBody
    public ResultVo file(HttpServletRequest request, HttpServletResponse response, Dictionary dictionary) {
        try {

            Long typeId = dictionary.getTypeId();
            String itemKey = dictionary.getItemKey();
            String itemValue = dictionary.getItemValue();
            // 判断typeId/itemKey/itemValue是否已经存在
            boolean isItemKeyExistsForUpdate = dictionaryService.isItemKeyExists(typeId, itemKey,itemValue);
            if (isItemKeyExistsForUpdate) {
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "已为改分类，请重新输入！");
            }
        } catch (Exception e) {
            logger.error("分类失败：" + e.getMessage(), e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "分类失败：" + e.getMessage());
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "分类成功");
    }


    @RequestMapping("edit")
    @Action(description = "添加或编辑数据字典", execOrder = ActionExecOrder.AFTER, detail = "<#if isAdd==1>添加数据字典<#else>编辑数据字典" + "<#assign entity=dictionaryService.getById(Long.valueOf(dicId))/>" + "【${entity.itemName}】</#if>")
    @ResponseBody
    public ResultVo edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int isAdd = RequestUtil.getInt(request, "isAdd", 0);
        int isRoot = RequestUtil.getInt(request, "isRoot", 0);
        Long dicId = RequestUtil.getLong(request, "dicId", 0);
        long canReturn = RequestUtil.getLong(request, "canReturn", 0);
        Dictionary dictionary = null;
        
        try{
        if (isAdd == 1) {
            dictionary = new Dictionary();
            Long userId = ContextUtil.getCurrentUserId();
            SysOrg sysOrg = orgService.getByUserId(userId);
            String eid = sysOrg.getOrgCode();
            dictionary.setEid(eid);
            if (isRoot == 1) {
                GlobalType globalType = globalTypeService.getById(dicId);
                
                dictionary.setTypeId(dicId);
                dictionary.setParentId(dicId);
                dictionary.setType(globalType.getType());
            } else {
                Dictionary parentDic = dictionaryService.getById(dicId);
                dictionary.setParentId(dicId);
                dictionary.setTypeId(parentDic.getTypeId());
                dictionary.setType(parentDic.getType());
            }
        } else {
            dictionary = dictionaryService.getById(dicId);
        }
            
        String typeName = RequestUtil.getString(request, "typeName");
        if(typeName.length() ==0){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请填写字典名称！");
            }
            
            
        Long typeId = RequestUtil.getLong(request, "typeId", 0);
        if(typeId ==0){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请选择字典类别！");
        }
            
        Long type = RequestUtil.getLong(request, "type", 0);
        if(type ==0){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请选择字典样式！");
        }
        
        String itemName = RequestUtil.getString(request, "itemName");
        if(itemName.length() ==0){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请添加项名！");
        }
        
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"添加数据字典成功");
        }catch (Exception e){

            logger.error("添加数据字典失败：" + e.getMessage(), e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "添加数据字典失败：" + e.getMessage());
        }
    }


    @RequestMapping("childType")
    @Action(description = "添加或编辑数据字典", execOrder = ActionExecOrder.AFTER, detail = "<#if isAdd==1>添加数据字典<#else>编辑数据字典" + "<#assign entity=dictionaryService.getById(Long.valueOf(dicId))/>" + "【${entity.itemName}】</#if>")
    @ResponseBody
    public ResultVo childType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int isAdd = RequestUtil.getInt(request, "isAdd", 0);
        int isRoot = RequestUtil.getInt(request, "isRoot", 0);
        Dictionary dictionary = null;
        try{
            

            String typeName = RequestUtil.getString(request, "typeName");
            if(typeName.length() ==0){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请填写字典名称！");
            }
            

            String itemName = RequestUtil.getString(request, "itemName");
            if(itemName.length() ==0){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请添加项名！");
            }

            String companyName = RequestUtil.getString(request, "companyName");
            if(companyName.length() ==0){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请添加所属公司！");
            }

            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"添加子类成功");
        }catch (Exception e){

            logger.error("添加数据字典失败：" + e.getMessage(), e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "添加子类失败：" + e.getMessage());
        }
    }



    /**
     * 排序数据字典列表
     *
     * @param request
     * @param response
     * @throws Exception
     */

    @RequestMapping("sortList")
    @Action(description="获取同级数据字典列表", detail="获取数据字典的同级数据字典列表")
    @ResponseBody
    public ResultVo sortList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        Long id = RequestUtil.getLong(request, "id");
        if(id == 0){
            logger.error("获取同级数据字典列表失败：数据字典ID为空！");
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取同级数据字典列表失败：请求参数错误！");
        }
        try {
            Dictionary dictionary = dictionaryService.getById(id);

            if(dictionary == null){
                logger.error("获取同级数据字典列表失败：系统不存在ID为【" + id + "】的数据字典！");
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取同级数据字典列表失败：资源不存在！");
            }

            List<Dictionary> dictionaryList = dictionaryService.getByParentId(dictionary.getParentId());
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取同级数据字典列表成功！", dictionaryList);
        } catch (Exception e) {
            logger.error("获取同级数据字典列表失败：" + e.getMessage(), e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取同级数据字典列表失败：" + e.getMessage());
        }
    }
    /**
     * 排序数据字典
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("sort")
    @Action(description = "数据字典排序", detail = "数据字典排序")
    @ResponseBody
    public ResultVo sort(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long[] lAryId = RequestUtil.getLongAryByStr(request, "dicId");
        if(lAryId == null){
            logger.error("数据字典排序失败：数据字典ID为空！");
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "数据字典排序失败：请求参数错误！");
        }

        try {
            dictionaryService.updSn(lAryId);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "数据字典排序成功！");
        } catch (Exception e) {
            logger.error("数据字典排序失败：" + e.getMessage(), e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "数据字典排序失败：" + e.getMessage());
        }

    }


    /**
     * 取得系统分类表分页列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("tree")
    //@Action(description="查看系统分类列表")
    @ResponseBody
    public ResultVo tree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String catKey = RequestUtil.getString(request, "catKey");
        List<GlobalType> globalType =globalTypeService.selectByCatekey(catKey);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据字典成功",globalType);
    }

	/**
	 * 数据字典左侧分类树数据
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("dicTree")
	@ResponseBody
	public ResultVo dicTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter queryFilter = new QueryFilter(request, false);
		//添加数据字典分类判断条件（1.分类，0.数据字典）
		queryFilter.getFilters().put("isType",1);
        Boolean isSuperAdmin = ContextUtil.isSuperAdmin();
        if(!isSuperAdmin){
            //获取当前用户企业编码
            String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
            queryFilter.getFilters().put("enterpriseCode",enterpriseCode);
        }
        List<GlobalType> globalTypeList = globalTypeService.getByQueryfilter(queryFilter);
		//添加数据字典判断条件（1.分类，0.数据字典）
		queryFilter.getFilters().put("isType",0);
		List<GlobalType> userGlobalTypeList = globalTypeService.getByQueryfilter(queryFilter);
		//去除掉没有数据字典的分类项
		List<GlobalType> resultList = new ArrayList<>();
		for(GlobalType gt:globalTypeList){
			int number = 0;
			for(GlobalType ugt:userGlobalTypeList){
				if(ugt.getNodePath().contains(gt.getNodePath())){
					number++;
				}
			}
			if(number > 0){
				resultList.add(gt);
			}
		}
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据字典左侧分类树数据成功!",resultList);
	}
    
}
