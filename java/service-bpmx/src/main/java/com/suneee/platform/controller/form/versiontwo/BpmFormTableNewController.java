package com.suneee.platform.controller.form.versiontwo;

import com.suneee.core.table.ColumnModel;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.util.FieldPool;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.core.table.ColumnModel;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 对象功能:自定义表 控制器类 开发公司:广州宏天软件有限公司 开发人员:xwy 创建时间:2011-11-30 14:29:22
 */
@Controller
@RequestMapping("/platform/form/bpmFormTable/")
@Action(ownermodel = SysAuditModelType.FORM_MANAGEMENT)
public class BpmFormTableNewController extends BaseController {
    @Resource
    private BpmFormTableService service;



    @Resource
    private BpmFormDefService bpmFormDefService;


    /**
     * 编辑自定义表。
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping("newEdit")
    public ModelAndView edit(HttpServletRequest request) throws Exception {
        ModelAndView mv = this.getAutoView();
//        ModelAndView mv=new ModelAndView("/platform/form/bpmFormTableNewEdit.jsp");
        Long tableId = RequestUtil.getLong(request, "tableId");

        boolean canGenerate=false;

        boolean canEditTbColName = true;
        // 无表单定义可以做任何的修改。
        if (tableId > 0) {
            //
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
        mv.addObject("canEditTbColName", canEditTbColName)
                .addObject("tableId", tableId)
                .addObject("mainTableList", mainTableList)
                .addObject("canGenerate", canGenerate);
        return mv;
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
     * 将表的列表转换为FieldLst。
     *
     * @param tableModel
     * @return
     */
    private List<BpmFormField> convertFieldList(TableModel tableModel) {
        List<BpmFormField> fieldList = new ArrayList<BpmFormField>();
        List<ColumnModel> colList = tableModel.getColumnList();
        for (ColumnModel model : colList) {
            // if(model.getIsPk()) continue;
            BpmFormField field = new BpmFormField();
            // field.setIsPk(0);

            field.setFieldName(model.getName());
            field.setFieldDesc(model.getComment());
            field.setCharLen(model.getCharLen());
            field.setIntLen(model.getIntLen());
            field.setDecimalLen(model.getDecimalLen());
            field.setFieldType(model.getColumnType());
            short isRequired = (short) (model.getIsNull() ? 0 : 1);
            field.setIsRequired(isRequired);

            setControlType(field);
            fieldList.add(field);

        }
        return fieldList;
    }

    /**
     * 设置字段的默认控件类型。
     *
     * @param field
     */
    private void setControlType(BpmFormField field) {
        String fieldType = field.getFieldType();
        if (StringUtil.isEmpty(fieldType)) {
            field.setFieldType(ColumnModel.COLUMNTYPE_VARCHAR);
            fieldType=ColumnModel.COLUMNTYPE_VARCHAR;
        }
        if (fieldType.equalsIgnoreCase(ColumnModel.COLUMNTYPE_VARCHAR) || fieldType.equalsIgnoreCase(ColumnModel.COLUMNTYPE_NUMBER)) {
            field.setControlType(FieldPool.TEXT_INPUT);
        } else if (fieldType.equalsIgnoreCase(ColumnModel.COLUMNTYPE_DATE)) {
            field.setControlType(FieldPool.DATEPICKER);
        } else if (fieldType.equalsIgnoreCase(ColumnModel.COLUMNTYPE_CLOB)) {
            field.setControlType(FieldPool.TEXTAREA);
        }

    }



    /**
     * 分组设置
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("newTeam")
    @Action(description = "分组设置")
    public ModelAndView team(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long tableId = RequestUtil.getLong(request, "tableId");
        BpmFormTable bpmFormTable = service.getById(tableId);

        return this.getAutoView().addObject("bpmFormTable", bpmFormTable);

    }



    /**
     * 比较两个列表是否相等。在比较两个列表的元素时，比较的方式为(o==null ? e==null : o.equals(e)).
     *
     * @param list1
     * @param list2
     * @return
     */
    private boolean isListEqual(List list1, List list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        if (list1.containsAll(list2)) {
            return true;
        }
        return false;
    }



    /**
     * 根据表获取JSONObject 对象。
     * @param bpmFormTable
     * @return
     */
    private JSONObject getByTable(BpmFormTable bpmFormTable){
        int isMain=bpmFormTable.getIsMain().intValue();
        JSONObject jsonTable=getJsonObj(bpmFormTable.getTableName(),bpmFormTable.getTableName(),bpmFormTable.getTableDesc(),1,isMain,"",bpmFormTable.isExtTable());
        JSONArray aryField=new JSONArray();

        List<BpmFormField> mainList= bpmFormTable.getFieldList();
        for(BpmFormField field:mainList){
            JSONObject json=getJsonObj(bpmFormTable.getTableName(),field.getFieldName(),field.getFieldDesc(),0,isMain,field.getFieldType(),bpmFormTable.isExtTable());
            aryField.add(json);
        }
        jsonTable.accumulate("children", aryField);

        return jsonTable;
    }

    private JSONObject getJsonObj(String tableName, String name,String comment,int isTable,int isMain,String dataType,boolean isExternal){
        JSONObject json=new JSONObject();
        json.accumulate("name", name)
                .accumulate("comment", comment)
                .accumulate("tableName", tableName)
                .accumulate("isTable", isTable)
                .accumulate("isMain", isMain)
                .accumulate("isExternal", isExternal?1:0)
                .accumulate("dataType", dataType);
        return json;
    }
}
