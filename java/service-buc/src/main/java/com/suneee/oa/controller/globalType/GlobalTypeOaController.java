/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: globalTypeListController
 * Author:   lmr
 * Date:     2018/3/26 18:04
 * Description: 系统分类管理
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.oa.controller.globalType;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysTypeKey;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.IdentityService;
import com.suneee.platform.service.system.SysTypeKeyService;
import com.suneee.oa.service.user.EnterpriseinfoService;
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
 * 〈一句话功能简述〉<br>
 * 〈系统分类管理〉
 *
 * @author lmr
 * @create 2018/3/26
 * @since 1.0.0
 */
@Controller
@RequestMapping("/platform/system/newGlobalType/")
@Action(ownermodel = SysAuditModelType.SYSTEM_SETTING)
public class GlobalTypeOaController extends BaseController {
    @Resource
    private GlobalTypeService globalTypeService;

    @Resource
    private SysTypeKeyService sysTypeKeyService;

    @Resource
    private BpmDefinitionService bpmDefinitionService;

    @Resource
    private IdentityService identityService;

    @Resource
    private EnterpriseinfoService enterpriseinfoService;

    @Resource
    private BpmFormDefService bpmFormDefService;

    @Resource
    private BpmFormTableService bpmFormTableService;

    @Resource
    private DictionaryService dictionaryService;
    @RequestMapping("list")
    @ResponseBody
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<GlobalType> list = new ArrayList<GlobalType>();
        QueryFilter queryFilter = new QueryFilter(request,false);
        Long parentId = RequestUtil.getLong(request, "parentId");
        SysTypeKey sysTypeKey = sysTypeKeyService.getById(parentId);
        String catkey = sysTypeKey.getTypeKey();
        //获取当前用户企业编码
        Boolean isSuperAdmin = ContextUtil.isSuperAdmin();
        //是管理员则查看全部
        if(!isSuperAdmin){
            //获取当前用户企业编码
            String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
            queryFilter.getFilters().put("enterpriseCode",enterpriseCode);
        }
		if (catkey.equals(GlobalType.CAT_DIC)) {
            queryFilter.addFilter("isType", 1);
        }
        queryFilter.addFilter("catkey", catkey);
        List<GlobalType> globalTypeList = this.getList(queryFilter, list);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取系统分类列表成功！", globalTypeList);
    }

    public List<GlobalType> getList(QueryFilter queryFilter, List<GlobalType> list) {
        List<GlobalType> temp = globalTypeService.getByQueryfilter(queryFilter);
        if (temp != null && temp.size() != 0) {
            list.addAll(temp);
            for (GlobalType globalType : temp) {
                queryFilter.addFilter("parentId", globalType.getTypeId());
                this.getList(queryFilter, list);
            }
        }
        return list;
    }

    @RequestMapping("get")
    @Action(description = "查看系统分类表明细", detail = "查看系统分类表明细")
    @ResponseBody
    public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long id = RequestUtil.getLong(request, "typeId");
        GlobalType po = globalTypeService.getById(id);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取分类列表成功！", po);
    }

    /**
     * 排序分类列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("sortList")
    @Action(description = "排序分类列表", detail = "排序分类列表")
    @ResponseBody
    public ResultVo sortList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long parentId = RequestUtil.getLong(request, "parentId", -1);
        QueryFilter queryFilter = new QueryFilter(request);
        queryFilter.addFilter("parentId",parentId);
        //获取当前用户企业编码
        Boolean isSuperAdmin = ContextUtil.isSuperAdmin();
        //是管理员则查看全部
        if(!isSuperAdmin){
            //获取当前用户企业编码
            String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
            queryFilter.getFilters().put("enterpriseCode",enterpriseCode);
        }
        GlobalType globalType = globalTypeService.getById(parentId);
        String catkey="";
        if(globalType!=null){
            catkey=globalType.getCatKey();
        }else {
            SysTypeKey sysTypeKey = sysTypeKeyService.getById(parentId);
            catkey = sysTypeKey.getTypeKey();
        }
        if (catkey.equals(GlobalType.CAT_DIC)) {
            queryFilter.addFilter("isType", 1);
        }
        List<GlobalType> list = globalTypeService.getByQueryfilter(queryFilter);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取分类列表成功！", list);
    }

    /**
     * 排序分类
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("sort")
    @Action(description = "系统分类排序", detail = "查看系统分类表明细")
    @ResponseBody
    public ResultVo sort(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long[] lAryId = RequestUtil.getLongAryByStr(request, "typeIds");
        try {
            if (lAryId == null) {
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "排序失败");
            }
            if (BeanUtils.isNotEmpty(lAryId)) {
                for (int i = 0; i < lAryId.length; i++) {
                    Long typeId = lAryId[i];
                    long sn = i + 1;
                    globalTypeService.updSn(typeId, sn);
                }
            }
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "排序成功！");
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "排序失败：" + e.getMessage());
        }
    }

    @RequestMapping("delete")
    @ResponseBody
    @Action(description = "删除系统分类", execOrder = ActionExecOrder.BEFORE, detail = "删除系统分类" + "<#assign entity=globalTypeService.getById(Long.valueOf(typeId))/>" + "【${entity.typeName}】")
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultVo resultMessage = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除分类成功");

        try {
            Long typeId = RequestUtil.getLong(request, "typeId");
            QueryFilter filter = new QueryFilter(request, false);
            filter.addFilter("typeIdList", new Long[]{typeId});
            List<BpmDefinition> bpmDefinitionList = bpmDefinitionService.getAll(filter);
            List<BpmFormDef> bpmFormDefList  = bpmFormDefService.getAll(filter);
            List<BpmFormTable> bpmFormTableList =bpmFormTableService.getAll(filter);
            List<Dictionary> dictionaryList = dictionaryService.getAll(filter);
            if (bpmDefinitionList.size() > 0||bpmFormDefList.size()>0||bpmFormTableList.size()>0||dictionaryList.size()>0) {
                resultMessage.setStatus(ResultVo.COMMON_STATUS_FAILED);
                resultMessage.setMessage("无法删除，请先删除分类所关联的数据！");
            } else {
                globalTypeService.delByTypeId(typeId);
            }
        } catch (Exception e) {
            String str = MessageUtil.getMessage();
            if (StringUtil.isNotEmpty(str)) {
                resultMessage.setStatus(ResultVo.COMMON_STATUS_FAILED);
                resultMessage.setMessage("删除分类失败:" + str);
            } else {
                String message = ExceptionUtil.getExceptionMessage(e);
                resultMessage.setStatus(ResultVo.COMMON_STATUS_FAILED);
                resultMessage.setMessage(message);
            }
        }
        return resultMessage;
    }

    @RequestMapping("edit")
    @ResponseBody
    @Action(description = "添加或更新系统分类",
            detail = "<#if isAdd>添加<#else>更新</#if>系统分类" +
                    "【${SysAuditLinkService.getGlobalTypeLink(Long.valueOf(typeId))}】")
    public ResultVo edit(HttpServletRequest request, HttpServletResponse response, GlobalType globalType) throws Exception {
        String enterpriseCode = RequestUtil.getString(request, "ecodes");
        //父节点
        long parentId = RequestUtil.getLong(request, "parentId", 0);
        //是否根节点
        int isRoot = RequestUtil.getInt(request, "isRoot");

        int isPrivate = RequestUtil.getInt(request, "isPrivate", 0);
        globalType.setEnterpriseCode(enterpriseCode);
        Long userId = ContextUtil.getCurrentUserId();
        String resultMsg = null;
        if (globalType.getTypeId() == 0) {
            if (parentId != 0) {
                GlobalType parentGlobal = globalTypeService.getById(parentId);
                if (parentGlobal != null) {
                    parentGlobal.setIsLeaf(1);
                    globalTypeService.update(parentGlobal);
                }
            }
            GlobalType tmpGlobalType = globalTypeService.getInitGlobalType(isRoot, parentId);
            String catKey = tmpGlobalType.getCatKey();

            String newNodeKey = "";

            String pinKey = PinyinUtil.getPinYinHeadChar(globalType.getTypeName());
            newNodeKey = this.addNodeKey(pinKey, 1, globalType.getEnterpriseCode(), catKey);

            globalType.setNodeKey(newNodeKey);
            //分类key不为数据字典的情况
            if (!catKey.equals(GlobalType.CAT_DIC)) {
                globalType.setType(tmpGlobalType.getType());
            }else{
                globalType.setIsType(1);
            }
            //设置用户ID
            if (isPrivate == 1) {
                globalType.setUserId(userId);
            }
            globalType.setCatKey(catKey);
            globalType.setNodePath(tmpGlobalType.getNodePath());
            globalType.setTypeId(tmpGlobalType.getTypeId());
            globalType.setType(1);
            globalType.setDepth(1);
            globalType.setSn(0L);
            globalType.setIsLeaf(0);
            if (globalType.getNodeCodeType().equals(GlobalType.NODE_CODE_TYPE_AUTO_Y)) {
                globalType.setNodeCode(identityService.nextId(globalType.getNodeCode()));
            }
            globalTypeService.add(globalType);
            resultMsg = "添加系统分类成功";
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, resultMsg);
        } else {
            globalTypeService.updateAndSubPath(globalType);
            resultMsg = "更新分类成功";
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, resultMsg);
    }

    /**
     * 添加不重复的nodeKey
     * @param pinyinKey
     * @param count
     * @param ecodes
     * @param catKey
     * @return
     */
    public String addNodeKey(String pinyinKey, int count, String ecodes, String catKey) {
        boolean isExist = globalTypeService.isNodeKeyExists(catKey, pinyinKey, ecodes);
        String newKey = pinyinKey;
        if (isExist) {
            newKey = pinyinKey + count;
            count++;
            this.addNodeKey(newKey, count, ecodes, catKey);
        }
        return newKey;
    }

    /**
     * 更新不重复的key
     * @param pinyinKey
     * @param count
     * @param typeId
     * @param catKey
     * @return
     */
    public String updateNodeKey(String pinyinKey, int count, Long typeId, String catKey) {
        boolean isExist = globalTypeService.isNodeKeyExistsForUpdate(typeId,catKey, pinyinKey,CookieUitl.getCurrentEnterpriseCode());
        String newKey = pinyinKey;
        if (isExist) {
            newKey = pinyinKey + count;
            count++;
            this.updateNodeKey(newKey, count, typeId, catKey);
        }
        return newKey;
    }
}