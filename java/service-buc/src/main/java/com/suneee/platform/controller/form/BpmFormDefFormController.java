package com.suneee.platform.controller.form;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.FormUtil;
import com.suneee.platform.service.system.ShareService;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * 对象功能:BPM_FORM_DEF 控制器类 开发公司:广州宏天软件有限公司 开发人员:xwy 创建时间:2011-12-22 11:07:56
 */
@Controller
@RequestMapping("/platform/form/bpmFormDef/")
@Action(ownermodel = SysAuditModelType.FORM_MANAGEMENT)
public class BpmFormDefFormController extends BaseFormController {
	@Resource
	private BpmFormDefService service;

	@Resource
	private ShareService shareService;
	/**
	 * 添加或更新自定义表单
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@ResponseBody
	@Action(description = "添加或更新自定义表单", execOrder = ActionExecOrder.AFTER, detail = "<#if isSuccess>" + "<#if isAdd>添加" + "<#else>更新" + "</#if>自定义表单：" + "【${SysAuditLinkService.getBpmFormDefLink(defId)}】成功" + "<#else>" + "添加或更新自定义表单失败</#if>")
	public ResultVo save(HttpServletRequest request) throws Exception {
		ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		// 表定义
		String data = request.getParameter("data");

		JSONObject formDefJson = JSONObject.fromObject(data);

		String publishTime = formDefJson.getString("publishTime");
		if (StringUtil.isEmpty(publishTime)) {
			formDefJson.put("publishTime", TimeUtil.getCurrentTime());
		}
		BpmFormDef bpmFormDef = (BpmFormDef) JSONObject.toBean(formDefJson, BpmFormDef.class);

		//表单添加
		boolean isAdd = bpmFormDef.getFormDefId() == 0;

		String formKey = bpmFormDef.getFormKey();
		if(isAdd&&(null==formKey ||"".equals(formKey))){
			formKey=getUniqueKey(bpmFormDef.getSubject(),0);
			bpmFormDef.setFormKey(formKey);
		}

		String html = bpmFormDef.getHtml();
		html = html.replace("？", "");
		String template = FormUtil.getFreeMarkerTemplate(html, bpmFormDef.getTableId());
		bpmFormDef.setTemplate(template);

		boolean isSuccess = true;
		long defId = 0;
		try {
			String msg="";
			if (isAdd) {
				service.addForm(bpmFormDef);
				msg = "添加自定义表单成功";
			} else {
				service.updateForm(bpmFormDef);
				msg = "更新自定义表单成功";
			}
			result.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
			result.setMessage(msg);
			defId = bpmFormDef.getFormDefId();
		} catch (Exception e) {
			e.printStackTrace();
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				result.setMessage("保存自定义表单数据失败:" + str);
			} else {
				String message = ExceptionUtil.getExceptionMessage(e);
				result.setMessage(message);
			}
			isSuccess = false;
		}
		SysAuditThreadLocalHolder.putParamerter("isAdd", isAdd);
		SysAuditThreadLocalHolder.putParamerter("isSuccess", isSuccess);
		SysAuditThreadLocalHolder.putParamerter("defId", String.valueOf(defId));
		return result;
	}

	/**
	 * 生成唯一key
	 * @param subject
	 * @param level
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String getUniqueKey(String subject,int level) throws UnsupportedEncodingException {
		String finalSubject=subject;
		if (level>0){
			finalSubject=subject+level;
		}
		String key=shareService.getPingyin(finalSubject);
		int rtn = service.getCountByFormKey(key);
		if (rtn>0){
			return getUniqueKey(subject,level+1);
		}else {
			return key;
		}
	}

}
