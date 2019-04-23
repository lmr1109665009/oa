package com.suneee.platform.service.form;

import java.io.IOException;
import java.util.*;

import javax.annotation.Resource;

import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.util.ParserParam;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.form.BpmFormFieldDao;
import com.suneee.platform.dao.form.BpmFormHandlerDao;
import com.suneee.platform.dao.form.BpmPrintTemplateDao;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.bpm.TaskOpinion;
import com.suneee.platform.model.form.*;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.util.ParserParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.form.BpmFormFieldDao;
import com.suneee.platform.dao.form.BpmFormHandlerDao;
import com.suneee.platform.dao.form.BpmFormTableDao;
import com.suneee.platform.dao.form.BpmPrintTemplateDao;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.BpmPrintTemplate;
import com.suneee.platform.model.form.SubTable;

import freemarker.template.TemplateException;


/**
 *<pre>
 * 对象功能:自定义表单打印模版 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-12-31 10:01:30
 *</pre>
 */
@Service
public class BpmPrintTemplateService extends BaseService<BpmPrintTemplate>
{
	@Resource
	private BpmPrintTemplateDao dao;
	@Resource
	private BpmFormFieldDao bpmFormFieldDao;
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	private BpmFormControlService bpmFormControlService;
	@Resource
	private FreemarkEngine freemarkEngine;
	@Resource
	private BpmFormHandlerDao bpmFormHandlerDao;
	@Resource
	private BpmFormHandlerService bpmFormHandlerService;
	@Resource
	private BpmFormRightsService bpmFormRightsService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private TaskOpinionService taskOpinionService;

	//文件访问URL地址
	@Value("#{configProperties['user.webSign.url']}")
	private String staticUrl;

	private String getStaticUrl() {
		if (staticUrl==null){
			return "";
		}
		return staticUrl;
	}

	// 流程实例分隔符
	private String InstanceIdSplitor = "#instanceId#";
	// 流程示意图替换符
	private String FlowChartSplitor = "(?s)<div[^>]*?\\s+name=\"editable-input\"\\s+class=\"flowchart\">(.*?)</div>";
	
	public BpmPrintTemplateService()
	{
	}
	
	@Override
	protected IEntityDao<BpmPrintTemplate, Long> getEntityDao()
	{
		return dao;
	}

	public int getCountByFormKey(String formKey) {
		return dao.getCountByFormKey(formKey);
	}

	public void setDefault(Long id, Long formKey) {
		dao.updateIsNotDefault(formKey);
		BpmPrintTemplate bpmPrintTemplate=dao.getById(id);
		bpmPrintTemplate.setIsDefault((short)1);
		update(bpmPrintTemplate);
	}

	public BpmPrintTemplate getDefaultByFormKey(String formKey) {
		return dao.getDefaultByFormKey(formKey);
	}
	
	public void delByFormKey(Long formKey){
		dao.delByFormKey(formKey);
	}

	public String obtainHtml(BpmPrintTemplate bpmPrintTemplate) throws TemplateException, IOException {
		Long tableId = bpmPrintTemplate.getTableId();
		String template = bpmPrintTemplate.getTemplate();
		BpmFormData data=new BpmFormData();
		List<BpmFormField> list = bpmFormFieldDao.getByTableIdContainHidden(tableId);
		// 获取流水号和脚本计算结果
		Map<String, Object> resultMap = bpmFormHandlerService.getCalculateDataMap(list,  false,true);
		// 将流水号和脚本计算结果加入data
		data.setMainFields(resultMap);

	
		Map<String, Map> model = new HashMap<String, Map>();
		model.put("main", data.getMainFields());
		model.put("opinion", data.getOptions());
		model.put("sub", data.getSubTableMap());
		model.put("others", new HashMap<String, String>());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("model", model);
		// 传入控制器的service，用于在模版中解析字段。
		map.put("service", bpmFormControlService);
	
		// 兼容之前生成的模版。
		map.put("table", new HashMap<Object, Object>());
		String output = freemarkEngine.parseByStringTemplate(map, template);

		output = output.replace(InstanceIdSplitor, "");
		return output;
	}
	
	
	/**
	 * update 2013-1-1 start
	 * 根据打印模版解析 出所打印表单的Html
	 * @param template
	 * @param processRun
	 * @return
	 * @throws Exception
	 */
	public String parseTempalte(BpmPrintTemplate template, ProcessRun processRun, String ctxPath) throws Exception {
		BpmFormDef bpmFormDef=bpmFormDefService.getDefaultVersionByFormKey(template.getFormKey());
		BpmFormTable bpmFormTable= bpmFormDef.getBpmFormTable();
		String tempStr="<input id='tableId' name='tableId' type='hidden' value='" + template.getTableId() + "'/>" +template.getTemplate();
		Map<String, Object> permission = bpmFormRightsService.getByFormKeyAndUserId(bpmFormDef , "","","",true);
		String instanceId=processRun.getActInstId();
		String pkValue=processRun.getBusinessKey();
		String actDefId=processRun.getActDefId();
		BpmFormData data=new BpmFormData();
		if(StringUtil.isNotEmpty(pkValue)){
			data = bpmFormHandlerDao.getByKey(bpmFormTable, pkValue,true);
			// 获取表单的意见。
			if (StringUtil.isNotEmpty(instanceId)) {
				Map<String, String> formOptions = bpmFormHandlerService.getFormOptionsByInstance(instanceId);
				if (BeanUtils.isNotEmpty(formOptions)) {
					data.setOptions(formOptions);
				}
			}
		}

		
		@SuppressWarnings("rawtypes")
		Map<String, Map> model = new HashMap<String, Map>();
		model.put("main", data.getMainFields());
		model.put("opinion", data.getOptions());
		model.put("sub", data.getSubTableMap());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("model", model);
		// 传入控制器的service，用于在模版中解析字段。
		map.put("service", bpmFormControlService);
		map.put("table", new HashMap());
		map.put("permission", permission);

		
		String html = freemarkEngine.parseByStringTemplate(map, tempStr);
		html = html.replace(InstanceIdSplitor, instanceId);
		// 流程图解析
		if (instanceId != "") {
			String repStr = "<iframe src=\""
					+ ctxPath
					+ "/platform/bpm/processRun/processImage.ht?actInstId="
					+ instanceId
					+ "\" name=\"flowchart\" id=\"flowchart\" marginwidth=\"0\" marginheight=\"0\" frameborder=\"0\" scrolling=\"auto\" width=\"100%\" onload='$(this).height($(this).parent().height())'></iframe>";
			html = html.replaceAll(FlowChartSplitor, repStr);
		} else if (actDefId != "") {
			String repStr = "<iframe src=\""
					+ ctxPath
					+ "/platform/bpm/bpmDefinition/flowImg.ht?actDefId="
					+ actDefId
					+ "\"  name=\"flowchart\" id=\"flowchart\" marginwidth=\"0\" marginheight=\"0\" frameborder=\"0\" scrolling=\"auto\" width=\"100%\" onload='$(this).height($(this).parent().height())'></iframe>";
			html = html.replaceAll(FlowChartSplitor, repStr);
		}

		//解析流程图控件
		List<TaskOpinion> taskOpinionList = new ArrayList<TaskOpinion>();
		if(StringUtil.isNotEmpty(instanceId)){
			taskOpinionList = taskOpinionService.getByActInstId(instanceId);
		}
		ParserParam param = new ParserParam(data, permission,taskOpinionList, null);
		param.putVar("instanceId", processRun.getActInstId());
		param.putVar("actDefId", processRun.getActDefId());

		html = BpmFormDefHtmlParser.parse(StringUtil.unescapeHtml(html), param);

		html=html.replaceAll("\\{\\{staticUrl\\}\\}",getStaticUrl());
		return html;
	}
	/**update 2013-1-1 end**/

	/**
	 * 获取默认的打印模板
	 */
	public BpmPrintTemplate getDefaultPrintTemplateByFormKey(String formKey) {
		BpmFormDef bpmFormDef=bpmFormDefService.getDefaultVersionByFormKey(formKey);
		String html=bpmFormDef.getHtml();
//		String template=bpmFormDef.getTemplate();
		String taskopinion ="<p><br /></p>"+
	    "<div name=\"editable-input\" class=\"taskopinion\" instanceid=\"#instanceId#\">"+
        	"<input type=\"text\" />"+
	    "</div><br />";
		BpmPrintTemplate bpmPrintTemplate = new BpmPrintTemplate();
		
		String name=bpmFormDef.getSubject();
		bpmPrintTemplate=new BpmPrintTemplate();
		bpmPrintTemplate.setFormKey(formKey);
		bpmPrintTemplate.setTemapalteName(name);
		bpmPrintTemplate.setTableId(bpmFormDef.getTableId());
		bpmPrintTemplate.setHtml(html+taskopinion);
//		bpmPrintTemplate.setTemplate(template+taskopinion);

		return bpmPrintTemplate;
	}
	
	/**
	 * 根据表单定义id和别名获取对象
	 * @param formKey
	 * @param alias
	 * @return
	 */
	public BpmPrintTemplate getByFormKeyAndAlias(String formKey, String alias) {
		return dao.getByFormKeyAndAlias(formKey,alias);
	}
	
	/**
	 * 获取所有流程结束后显示的打印的对象集
	 * @param formKey
	 * @return
	 */
	public List<BpmPrintTemplate> getAllByFormKeyAndFlowFinish(String formKey) {
		return dao.getAllByFormKeyAndFlowFinish(formKey);
	}
	
	/**
	 * 获取所有普通表单标识的对象集
	 * @param formKey
	 * @return
	 */
	public List<BpmPrintTemplate> getAllByFormKeyAndForm(String formKey) {
		return dao.getAllByFormKeyAndForm(formKey);
	}
	
	/**
	 * 打印模版
	 * @param bpmPrintTemplate
	 * @param businessKey 主表id
	 * @param ctx
	 * @return
	 * @throws Exception 
	 */
	public String parsePrintTempalte(BpmPrintTemplate bpmPrintTemplate,
			String businessKey, String ctxPath) throws Exception {
		BpmFormDef bpmFormDef=bpmFormDefService.getDefaultVersionByFormKey(bpmPrintTemplate.getFormKey());
		BpmFormTable formTable=bpmFormDef.getBpmFormTable();
		String tempStr="<input id='tableId' name='tableId' type='hidden' value='" + bpmPrintTemplate.getTableId() + "'/>" +bpmPrintTemplate.getTemplate();
		//Map<String, Map<String, String>> permission = bpmFormRightsService.getByFormKey(bpmFormDef);
		Long userId= ContextUtil.getCurrentUserId();
		Map<String, Object> permission = bpmFormRightsService.getByFormKeyAndUserId(bpmFormDef ,"","","",true);
		BpmFormData data = new BpmFormData();
		if(StringUtil.isNotEmpty(businessKey)){
			data = bpmFormHandlerDao.getByKey(formTable, businessKey,true);
		}
		//替换掉选项中的 选项key 为value
		//备注：此方法会改变data中复选框、单选框的值，影响表单打印功能
		//replaceOptionKeyWithValue(data);
		
		@SuppressWarnings("rawtypes")
		Map<String, Map> model = new HashMap<String, Map>();
		model.put("main", data.getMainFields());
		model.put("opinion", data.getOptions());
		model.put("sub", data.getSubTableMap());
		
		Map<String,String> otherMap = new HashMap<String, String>();
		
		model.put("others", otherMap);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("model", model);
		// 传入控制器的service，用于在模版中解析字段。
		map.put("service", bpmFormControlService);
		map.put("table", new HashMap());
		map.put("permission", permission);
		map.put("ctx", ctxPath);
		String html = freemarkEngine.parseByStringTemplate(map, tempStr);
		html = html.replace(InstanceIdSplitor, "");
		return html;
	}
	
	/**
	 * 判断别名是否唯一
	 * @param alias
	 * @param id
	 * @return
	 */
	public boolean isExistAlias(String alias, Long id) {
		return dao.isExistAlias(alias,id);
	}
	
}
