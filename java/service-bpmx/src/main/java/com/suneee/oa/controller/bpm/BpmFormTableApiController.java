package com.suneee.oa.controller.bpm;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.controller.mobile.MobileBaseController;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormFieldService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 对象功能:自定义表 控制器类
 * 开发公司:深圳象羿
 * 开发人员:子华
 * 创建时间:2017-12-16 14:29:22
 */
@Controller
@RequestMapping("/api/form/bpmFormTable/")
public class BpmFormTableApiController extends MobileBaseController{
    @Resource
    private BpmFormTableService service;
    @Resource
    private BpmFormDefService bpmFormDefService;
    @Resource
    private BpmFormFieldService bpmFormFieldService;
	@Resource
	private GlobalTypeService globalTypeService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;

    /**
     * 取得自定义表分页列表
     * @param request
     * @throws Exception
     */
    @RequestMapping("list")
    @Action(description = "查看自定义表分页列表")
    @ResponseBody
    public ResultVo list(HttpServletRequest request) throws Exception {
        QueryFilter filter = new QueryFilter(request, true);
        Long categoryId = RequestUtil.getLong(request, "categoryId");
        if(categoryId == 0){
            List<Long> typeIdList=globalTypeService.getTypeIdsByEcode(ContextSupportUtil.getCurrentEnterpriseCode());
            BpmUtil.typeIdFilter(typeIdList);
            filter.addFilter("typeIdList", typeIdList);
        }else{
			GlobalType globalType = globalTypeService.getById(categoryId);
			List<GlobalType> globalTypeList = globalTypeService.getByNodePath(globalType.getNodePath());
            List<Long> list = new ArrayList<>();
            for(GlobalType gt:globalTypeList){
                if(gt.getTypeId() != null){
                    list.add(gt.getTypeId());
                }
            }
            filter.addFilter("typeIdList",list);
            filter.getFilters().remove("categoryId");
        }
        List<BpmFormTable> list = service.getAll(filter);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取定义字段表数据列表成功",getPageList(list,filter));
    }

    /**
     * 编辑自定义表。
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping("edit")
    @ResponseBody
    @Action(description = "编辑自定义表")
    public ResultVo edit(HttpServletRequest request) throws Exception {
        Long tableId = RequestUtil.getLong(request, "tableId");
        boolean canGenerate=false;
        boolean canEditTbColName = true;
        // 无表单定义可以做任何的修改。
        if (tableId > 0) {
            Long tmpTableId = tableId;
            BpmFormTable bpmFormTable = service.getById(tmpTableId);
            // 如果是子表的情况，先取得主表的ID,根据主表id判断是否可以编辑。
            if (bpmFormTable.getIsMain().shortValue() == BpmFormTable.NOT_MAIN.shortValue()) {
                tmpTableId = bpmFormTable.getMainTableId();
                BpmFormTable mainTable =service.getById(tmpTableId);
                if(mainTable!=null){
                    canGenerate=mainTable.getIsPublished()==1;
                }
            }
            if (tmpTableId != null && tmpTableId > 0){
                canEditTbColName = !bpmFormDefService.isTableHasFormDef(tmpTableId);
            }
        }
        List<BpmFormTable> mainTableList = service.getAllUnpublishedMainTable();
        Map<String,Object> data=new HashMap<String, Object>();
        data.put("canEditTbColName",canEditTbColName);
        data.put("mainTableList",mainTableList);
        data.put("canGenerate",canGenerate);

        if (tableId!=null&&tableId>0){
            int mainTableIsPublished = RequestUtil.getInt(request, "mainTableIsPublished");
            BpmFormTable bpmFormTable = service.getById(tableId);
            // 子表展示
            if (BeanUtils.isNotEmpty(bpmFormTable) && bpmFormTable.getIsMain().shortValue() == 0) {
                Long mainTableId = bpmFormTable.getMainTableId();
                if (mainTableId > 0) {
                    BpmFormTable table = service.getById(mainTableId);
                    // 为已发布的主表 添加一个子表时
                    if (mainTableIsPublished == 1) {
                        bpmFormTable.setMainTableDesc(table.getTableName());
                    } else {
                        bpmFormTable.setMainTableDesc(StringUtils.isEmpty(table.getTableDesc()) ? table.getTableName() : table.getTableDesc());
                    }
                    bpmFormTable.setMainTableName(table.getTableName());
                }

            }
            List<BpmFormField> fieldList = bpmFormFieldService.getByTableIdContainHidden(tableId);
            data.put("table", bpmFormTable);
            converFieldScript(fieldList);
            data.put("fieldList", fieldList);
        }

        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"",data);
    }

    /**
     * 将隐藏字段的脚本放到对应字段的scriptID属性中。
     *
     * @param list
     */
    private void converFieldScript(List<BpmFormField> list) {
        Map<String, BpmFormField> fieldMap = new HashMap<String, BpmFormField>();
        for (BpmFormField field : list) {
            fieldMap.put(field.getFieldName(), field);
        }

        for (Iterator<BpmFormField> it = list.iterator(); it.hasNext();) {
            BpmFormField field = it.next();
            Short controleType = field.getControlType();
            // 是人员选择器
            boolean isSelector = service.isExecutorSelector(controleType);
            short valueFrom = field.getValueFrom().shortValue();
            // 选择器 脚本 隐藏字段。
            if (isSelector && field.getIsHidden().shortValue() == BpmFormField.HIDDEN && field.getIsDeleted().shortValue() == BpmFormField.IS_DELETE_N) {
                if (valueFrom == BpmFormField.VALUE_FROM_SCRIPT_SHOW || valueFrom == BpmFormField.VALUE_FROM_SCRIPT_HIDDEN) {
                    String script = field.getScript();
                    String fieldId = field.getFieldName();
                    fieldId = StringUtil.trimSufffix(fieldId, BpmFormField.FIELD_HIDDEN);

                    BpmFormField bpmFormField = fieldMap.get(fieldId);
                    bpmFormField.setScriptID(script);
                }
                // 删除隐藏字段。
                it.remove();
            }
        }
    }

    /**
     * 获取主表数据
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("mainTableList")
    @ResponseBody
    @Action(description = "获取主表数据")
    public ResultVo mainTableList(HttpServletRequest request) throws Exception {
        QueryFilter queryFilter = new QueryFilter(request,true);
        List<Long> typeIdList=globalTypeService.getTypeIdsByEcode(ContextSupportUtil.getCurrentEnterpriseCode());
        BpmUtil.typeIdFilter(typeIdList);
        queryFilter.addFilter("typeIds", typeIdList);
        List<BpmFormTable> bpmFormTableList = service.getAllMainTable(queryFilter);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取主表数据列表",getPageList(bpmFormTableList,queryFilter));
    }

    /**
     * 分组设置
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("team")
    @ResponseBody
    @Action(description = "分组设置")
    public ResultVo team(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long tableId = RequestUtil.getLong(request, "tableId");
        BpmFormTable bpmFormTable = service.getById(tableId);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取分组设置成功",bpmFormTable.getTeam());

    }

    /**
     * 重置自定义表。删除已生成的自定义物理表、删除标记为删除的字段。<br/>
     * 1、已经绑定表单的自定义表不能重置
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("reset")
    @ResponseBody
    @Action(description = "重置自定义表", detail = "重置自定义表【SysAuditLinkService.getBpmFormTableLink(Long.valueOf(tableId))}】")
    public ResultVo reset(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long tableId = RequestUtil.getLong(request, "tableId");
        ResultVo message = null;
        Long mainTableId = service.getMainTableId(tableId);
        boolean rtn = false;
        boolean flag = mainTableId==null || mainTableId==0;
        if(mainTableId==null || mainTableId==0){
            rtn = bpmFormDefService.isTableHasFormDef(tableId);
        }else{
            rtn = bpmFormDefService.isTableHasFormDef(mainTableId);
        }
        if (rtn && flag) {
            message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "已绑定表单，不能重置！");
        } else if(rtn && !flag) {
            message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "子表关联的主表已绑定表单，不能重置！");
        }else{
            service.reset(tableId);
            message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "重置成功！");
        }
        return message;
    }


    /**
     * 删除自定义表。 如果表已经定义表单，那么表不可以删除。
     * 1.判断表是否绑定表单（是子表时判断其主表）
     * 2.没有时，连同其子表一起删除
     * 3.同时删除在BPM_FORM_FIELD表中删除表字段信息
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("delByTableId")
    @ResponseBody
    @Action(description = "删除自定义表", execOrder = ActionExecOrder.BEFORE, detail = "<#assign entity=bpmFormTableService.getById(Long.valueOf(tableId))/>" + "自定义表: ${entity.tableDesc}【${entity.tableName}】," + "<#if !isTableHasFormDef>" + "已删除" + "<#else>不能删除</#if>")
    public ResultVo delByTableId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long[] aryTableId = RequestUtil.getLongAryByStr(request, "tableId");
        ResultVo message = null;
        boolean rtn = false;
        int notDelCount=0;
        List<BpmFormTable> notDelTables=new ArrayList<BpmFormTable>();
        for (Long tableId : aryTableId) {
            BpmFormTable table = service.getById(tableId);
            if (table==null){
                continue;
            }
            Long fatherTableId = 0L;
            if(BeanUtils.isNotEmpty(table)){
                fatherTableId = table.getMainTableId();
            }
            // 删除的时候 判断是否是子表 ，并且判断子表的父表是否绑定表单
            if (BeanUtils.isNotIncZeroEmpty(fatherTableId) ) {
                rtn = bpmFormDefService.isTableHasFormDef(fatherTableId);
                if (!rtn) {
                    // 表未定义表单，可删除
                    service.delByTableId(tableId);
                } else if (BeanUtils.isEmpty(message)) {
                    notDelCount++;
                    notDelTables.add(table);
                }
            } else {
                rtn = bpmFormDefService.isTableHasFormDef(tableId);
                SysAuditThreadLocalHolder.putParamerter("isTableHasFormDef", rtn);
                if (!rtn) {
                    // 表未定义表单，可删除
                    service.delByTableId(tableId);
                } else if (BeanUtils.isEmpty(message)) {
                    notDelCount++;
                    notDelTables.add(table);
                }
            }
        }
        String str="删除成功！";
        int status=ResultVo.COMMON_STATUS_SUCCESS;
        if (aryTableId.length==1){
            if (notDelCount>0){
                str="该数据库表已关联表单，请先删除对应表单！";
                status=ResultVo.COMMON_STATUS_FAILED;
            }
        }else {
            if (aryTableId.length==notDelCount){
                str="所选数据库表已关联表单，请先删除对应表单！";
                status=ResultVo.COMMON_STATUS_FAILED;
            }else if (notDelCount>0){
                str="";
                int counter=0;
                for (BpmFormTable item:notDelTables){
                    if (counter==notDelTables.size()-1){
                        str+=item.getTableDesc();
                    }else {
                        str+=item.getTableDesc()+"、";
                    }
                    counter++;
                }
                str+="部分数据库表已关联表单，请先删除对应表单！";
                status=ResultVo.COMMON_STATUS_SUCCESS;
            }
        }
        return new ResultVo(status,str);
    }


    /**
     * 取得自定义表明细
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("get")
    @ResponseBody
    @Action(description = "查看自定义表明细")
    public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long tableId = RequestUtil.getLong(request, "tableId");
        BpmFormTable table = service.getById(tableId);
        List<BpmFormField> fields = bpmFormFieldService.getByTableId(tableId);
        Map<String,Object> data=new HashMap<String, Object>();
        data.put("table",table);
        data.put("fields",fields);
        String mainTable = getText("controller.bpmFormTable.unallocated");
        if (table.getIsMain() == 1) {
            List<BpmFormTable> subList = service.getSubTableByMainTableId(tableId);
            data.put("subList",subList);
        } else {
            Long mainTableId = table.getMainTableId();
            if (mainTableId > 0) {
                BpmFormTable tb = service.getById(mainTableId);
                if (tb.getIsMain() == 1)
                    mainTable = tb.getTableName();
                data.put("mainTable",mainTable);
            }
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取自定义表明细成功",data);
    }

    /**
     * 复制表。
     *
     * @param request
     */
    @RequestMapping("saveCopy")
    @ResponseBody
    @Action(description = "保存复制自定义表", detail = "<#list jsonArray as item>" + "<#if item_index==0>" + "复制自定义表【${SysAuditLinkService.getBpmFormTableLink(Long.valueOf(item.tableId))}】," + "复制的自定义表名为【${item.tableName}】,表注释为【${item.tableDesc}】   " + "<#else>" + "复制子表【${SysAuditLinkService.getBpmFormTableLink(Long.valueOf(item.tableId))}】," + "复制的子表名【${item.tableName}】,子表注释【${item.tableDesc}】         " + "</#if>" + "</#list>")
    public ResultVo saveCopy(HttpServletRequest request){
        Long tableId=RequestUtil.getLong(request,"tableId");
        ResultVo resultVo = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "");
        if (tableId==null) {
            resultVo.setMessage("tableId参数为空！");
            return resultVo;
        }
        try {
            service.saveCopy(tableId);
            resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
            resultVo.setMessage("保存成功!");
            return resultVo;
        } catch (Exception ex) {
            String str = MessageUtil.getMessage();
            if (StringUtil.isNotEmpty(str)) {
                resultVo.setMessage(getText("controller.bpmFormTable.saveCopy.fail") + ":" + str);
            } else {
                String message = ExceptionUtil.getExceptionMessage(ex);
                resultVo.setMessage(message);
            }
            return resultVo;
        }
    }
}
