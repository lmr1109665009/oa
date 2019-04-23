package com.suneee.platform.controller.form;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.table.TableModel;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象功能:自定义表 控制器类 开发公司:广州宏天软件有限公司 开发人员:xwy 创建时间:2011-11-30 14:29:22
 */
@Controller
@RequestMapping("/platform/form/bpmFormTable/")
@Action(ownermodel=SysAuditModelType.FORM_MANAGEMENT)
public class BpmFormTableFormController extends BaseFormController {
	@Resource
	private BpmFormTableService bpmFormTableService;

	/**
	 * 添加自定义表。
	 *
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("saveTable")
	@ResponseBody
	@Action(description="添加自定义表",
			execOrder=ActionExecOrder.AFTER,
			detail="<#if StringUtil.isNotEmpty(isAdd)>" +
						"<#if isAdd==0>添加" +
						"<#else>更新" +
						"</#if>" +
						"自定义表  :【${SysAuditLinkService.getBpmFormTableLink(Long.valueOf(id))}】" +
					"<#else>" +
						"添加或更新自定义表：【表名:${table.tableName}, 表注释:${table.tableDesc}】失败" +
					"</#if>")
	public ResultVo saveTable(HttpServletRequest request) throws Exception{

		String tableJson=request.getParameter("table");
		String fieldsJson=request.getParameter("fields");
//		Long categoryId = RequestUtil.getLong(request, "categoryId", null);

		int generator = RequestUtil.getInt(request, "generator");
		int isadd= 0;
		ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		try {
			List<BpmFormField> fieldList=getByFormFieldJson(fieldsJson);
			JSONObject tableJsonObj = JSONObject.fromObject(tableJson);

			tableJsonObj.remove("createtime");
			tableJsonObj.remove("publishTime");
//			if(categoryId != null){
//				tableJsonObj.accumulate("categoryId", categoryId);
//			}

			BpmFormTable table = (BpmFormTable) JSONObject.toBean(tableJsonObj,BpmFormTable.class);

			table.setFieldList(fieldList);
			String msg = "";

			//系统日志参数
			try {
				SysAuditThreadLocalHolder.putParamerter("table", table);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}

			if (table.getTableId() == 0) {
				if (bpmFormTableService.isTableNameExisted(table.getTableName())) {
					msg = "表名已存在";
					result.setMessage(msg);
					return result;
				}

				int rtn = bpmFormTableService.addFormTable(table);
				if (rtn == -1) {
					msg = "字段中存在"+ TableModel.CUSTOMER_COLUMN_CURRENTUSERID+"字段 ";
					result.setMessage(msg);
					return result;
				}
				isadd=1;
				result.setMessage("保存自定义表成功");
				result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
			} else {
				boolean isExist = bpmFormTableService.isTableNameExistedForUpd(table.getTableId(), table.getTableName());
				if (isExist) {
					//"输入的表名在系统中已经存在!"
					msg = "表名已存在";
					result.setMessage(msg);
					return result;
				}

				int rtn = bpmFormTableService.upd(table,generator);
				if (rtn == -1) {
					msg = "字段中存在"+TableModel.CUSTOMER_COLUMN_CURRENTUSERID+"字段 ";
					result.setMessage(msg);
					return result;
				} else if (rtn == -2) {
					//自定义数据表中已经有数据，字段不能设置为非空，请检查添加的字段!
					msg = "表中已经有数据，字段不能设置为非空，请检查添加的字段！";
					result.setMessage(msg);
					return result;
				} else if (rtn == 0) {
					result.setMessage("更新自定义表成功");
					result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
				}
			}
			// 是否需要为已生成的主表 生成新的子表
			if (generator == 1) {
				bpmFormTableService.generateTable(table.getTableId(), ContextUtil.getCurrentUser().getFullname());
			}
			SysAuditThreadLocalHolder.putParamerter("isAdd", String.valueOf(isadd));
			SysAuditThreadLocalHolder.putParamerter("id", table.getTableId().toString());
			return result;
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				result.setMessage("更新失败:" + str);
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				result.setMessage(message);
			}
			return result;
		}
	}

	/**
	 * 根据字段的JSON返回字段列表。
	 * @param fieldsJson
	 * @return
	 */
	private List<BpmFormField> getByFormFieldJson(String fieldsJson){
		JSONArray aryJson = JSONArray.fromObject(fieldsJson);
		List<BpmFormField> list = new ArrayList<BpmFormField>();
		for(Object obj : aryJson){
			JSONObject fieldJObject = (JSONObject)obj;
			String options = "";
			String ctlProperty="";
			if (fieldJObject.containsKey("options")) {
				options = fieldJObject.getString("options");
				fieldJObject.remove("options");
			}

			if (fieldJObject.containsKey("ctlProperty")) {
				ctlProperty = fieldJObject.getString("ctlProperty");
				fieldJObject.remove("ctlProperty");

			}
			BpmFormField bpmFormField = (BpmFormField)JSONObject.toBean(fieldJObject,BpmFormField.class);
			bpmFormField.setOptions(options);
			bpmFormField.setCtlProperty(ctlProperty);
			bpmFormField.setFieldName(StringUtil.trim(bpmFormField.getFieldName(), " "));

			list.add(bpmFormField);
		}
		return list;
	}

	@RequestMapping("saveExtTable")
	public void saveExtTable(HttpServletRequest request,HttpServletResponse response) throws Exception {

		String tableJson = request.getParameter("table");
		String fieldsJson = request.getParameter("fields");

		BpmFormTable table = (BpmFormTable) JSONObject.toBean(JSONObject.fromObject(tableJson), BpmFormTable.class);
		//表明转成小写
		table.setTableName(table.getTableName().toLowerCase());
		List<BpmFormField> list= getByFormFieldJson(fieldsJson);
		table.setFieldList(list);

		String msg = "";
		try {
			if (table.getTableId() == 0) {
				String tableName = table.getTableName();
				String dsAlias = table.getDsAlias();
				if (bpmFormTableService.isTableNameExternalExisted(tableName,dsAlias)) {
					//"表名已存在"
					msg = "表名已存在";
					writeResultMessage(response.getWriter(), msg, ResultMessage.Fail);
					return;
				}
				bpmFormTableService.addExt(table);
				msg = "保存外部表成功";
				writeResultMessage(response.getWriter(), msg,ResultMessage.Success);
			}
			else {
				bpmFormTableService.updExtTable(table);
				writeResultMessage(response.getWriter(), msg,ResultMessage.Success);
			}
		} catch (Exception ex) {
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, "保存外部表失败:" + str);
				response.getWriter().print(resultMessage);
			} else {
				String message = ExceptionUtil.getExceptionMessage(ex);
				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, message);
				response.getWriter().print(resultMessage);
			}
		}
	}

	/**
	 * 通过流程定义标题自动生成流程KEY
	 * 
	 * @param request
	 * @return flowkey
	 * @throws Exception
	 */
	@RequestMapping("getFieldKey")
	public void getFieldKey(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String subject = RequestUtil.getString(request, "subject");
		if (StringUtil.isEmpty(subject))
			return;
		String msg = "";
		String pingyin = PinyinUtil.getPinYinHeadCharFilter(subject);
		msg = pingyin;
		writeResultMessage(response.getWriter(), msg, ResultMessage.Success);
	}

	/**
	 * 通过流程定义标题自动生成流程KEY
	 * 
	 * @param request
	 * @return flowkey
	 * @throws Exception
	 */
	@RequestMapping("getTableKey")
	public void getTableKey(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String tableName = RequestUtil.getString(request, "subject");
		Long tableId = RequestUtil.getLong(request, "tableId");
		String msg = "";
		String pingyin = PinyinUtil.getPinYinHeadCharFilter(tableName);
		msg = pingyin;
		try {
			if (tableId == 0) {
				if (bpmFormTableService.isTableNameExisted(pingyin)) {
					msg = "表名已存在";
					writeResultMessage(response.getWriter(), msg,"exist",
							ResultMessage.Fail);
					return;
				}
			} else {
				boolean isExist = bpmFormTableService.isTableNameExistedForUpd(
						tableId, pingyin);
				if (isExist) {
					msg = "输入的表名在系统中已经存在!";
					writeResultMessage(response.getWriter(), msg,"exist",
							ResultMessage.Fail);
					return;
				}
			}
			writeResultMessage(response.getWriter(), msg, ResultMessage.Success);
		} catch (Exception ex) {
			writeResultMessage(response.getWriter(), ex.getMessage(),
					ResultMessage.Fail);
		}
	}
}
