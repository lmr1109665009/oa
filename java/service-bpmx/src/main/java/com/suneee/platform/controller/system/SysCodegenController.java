package com.suneee.platform.controller.system;

import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.ZipUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.form.BpmFormDef;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.SysCodeTemplate;
import com.suneee.platform.service.form.*;
import com.suneee.platform.service.system.CodeUtil;
import com.suneee.platform.service.system.SysCodeTemplateService;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象功能:基于自定义表的代码生成器 控制器类 开发公司:广州宏天软件有限公司 开发人员:zyp 创建时间:2012-12-19 15:38:01
 */
@Controller
@RequestMapping("/platform/system/sysCodegen/")
public class SysCodegenController extends BaseController {
	@Resource
	private BpmFormDefService bpmFormDefService;
	@Resource
	private SysCodeTemplateService sysCodeTemplateService;
	@Resource
	private BpmFormFieldService bpmFormFieldService;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private BpmFormHandlerService bpmFormHandlerService;
	@Resource
	private FreemarkEngine freemarkEngine;
	@Resource
	private Map<String,IParseHandler> parseHandlerMap;
	
	private static String BASE_PATH = null; // 生成代码临时存放位置

	private String getBasePath(){
		if (BASE_PATH!=null){
			return BASE_PATH;
		}
		BASE_PATH=FileUtil.getRootPath() + File.separator + "codegen";
		return BASE_PATH;
	}
	
	
	/**
	 * 获取所有自定义表数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getTableData")
	public List<JSONObject> getTableData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String subject = RequestUtil.getString(request, "subject", "");
		List<JSONObject> list = new ArrayList<JSONObject>();
		List<BpmFormDef> bpmFormDefList = bpmFormDefService.getAllPublished(subject);
		for(BpmFormDef bdf:bpmFormDefList){
			JSONObject jo = new JSONObject();
			jo.put("subject", bdf.getSubject());
			jo.put("formDefId", bdf.getFormDefId());
			list.add(jo);
		}
		return list;
	}

	/**
	 * 代码模板文件列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("detail")
	public ModelAndView genDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<SysCodeTemplate> templateList = sysCodeTemplateService.getAll();
		return getAutoView().addObject("templateList", templateList)
				.addObject("flowName", RequestUtil.getString(request, "flowName", ""))
				.addObject("defKey", RequestUtil.getString(request, "defKey", ""))
				.addObject("defId", RequestUtil.getString(request, "defId", ""))
				.addObject("baseDir", RequestUtil.getString(request, "baseDir", ""))
				.addObject("system", RequestUtil.getString(request, "system", ""));
	}

	/**
	 * 生成代码
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("codegen")
	public void codegen(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 文件相关参数
		String[] templateIds = request.getParameterValues("templateId");
		int override = RequestUtil.getInt(request, "override");
		String flowKey = RequestUtil.getString(request, "defKey");
		String flowId = RequestUtil.getString(request, "defId");
		int isZip = RequestUtil.getInt(request, "isZip");
		String folderPath = RequestUtil.getString(request, "folderPath");
		String[] formDefIds = RequestUtil.getString(request, "formDefIds").split(",");
		// 自定义表相关
		String basePath = RequestUtil.getString(request, "baseDir");
		String system = RequestUtil.getString(request, "system");
		List<String> codeFiles = new ArrayList<String>();
		List<BpmFormTable> list = getTableModels(request);
		try {
			for (String formDefId : formDefIds) {
				BpmFormDef bpmFormDef = bpmFormDefService.getById(Long.parseLong(formDefId));
				List<BpmFormTable> tables = new ArrayList<BpmFormTable>();
				Long tableId = bpmFormDef.getTableId();
				for (BpmFormTable model : list) {
					if (model.getTableId().equals(tableId) || model.getMainTableId().equals(tableId)) {
						tables.add(model);
					}
				}
				List<String> fileList = genCode(basePath, system, tables, templateIds, override, flowId,flowKey, bpmFormDef);
				codeFiles.addAll(fileList);
			}
			// 压缩生成文件到本地
			if (isZip == 1) {
				String toDir = folderPath + File.separator + "codegen";
				for (String filePath : codeFiles) {
					FileUtil.createFolderFile(toDir + File.separator + filePath);
					FileUtil.copyFile(basePath + File.separator + filePath, toDir + File.separator + filePath);
				}
				ZipUtil.zip(toDir, true);
			}
			writeResultMessage(response.getWriter(), new ResultMessage(ResultMessage.Success, "自定义表生成代码成功"));
		} catch (Exception e) {
			writeResultMessage(response.getWriter(), new ResultMessage(ResultMessage.Fail, "自定义表生成代码失败:" + e.getMessage()));
		}
	}

//	private List<String> genCode_old(String basePath, String system, List<BpmFormTable> tables, String[] templateIds, int override, String flowKey, BpmFormDef bpmFormDef) throws Exception {
//		List<String> fileList = new ArrayList<String>();
//		for (int i = 0; i < templateIds.length; i++) {
//			Long templateId = Long.parseLong(templateIds[i]);
//			SysCodeTemplate template = sysCodeTemplateService.getById(templateId);
//			for (BpmFormTable table : tables) {
//				
//				
//				Map<String, String> variables = table.getVariable();
//				variables.put("system", system);
//				String fileName = template.getFileName();
//				String fileDir = template.getFileDir();
//				String path = basePath + File.separator + template.getFileDir();
//				
//				int isSub = template.getIsSub();
//				if (table.getIsMain() != 1) {
//					if (isSub == 0) {
//						continue;
//					}
//				}
//				
//				Map<String, Object> model = new HashMap<String, Object>();
//				if (template.getFormEdit() == 1) {
//					// 先解释表单的html
//					String str = getFormHtml(bpmFormDef, table, true);
//					model.put("html", str);
//				}
//				if (template.getFormDetail() == 1) {
//					String str = getFormHtml(bpmFormDef, table, false);
//					model.put("html", str);
//				}
//				
//				//其他参数
//				model.put("table", table);
//				model.put("system", system);
//				if (StringUtil.isNotEmpty(flowKey)) {
//					model.put("flowKey", flowKey);
//				}
//				
//				String templateStr = template.getHtml();
//				FreemarkEngine freemarkEngine = new FreemarkEngine();
//				String html = freemarkEngine.parseByStringTemplate(model, templateStr);
//				String fileStr = path + File.separator + fileName;
//				String filePath = StringUtil.replaceVariable(fileStr, variables);
//				addFile(filePath, html, override);
//				String relativePath = StringUtil.replaceVariable(fileDir + File.separator + fileName, variables);
//				fileList.add(relativePath);
//			}
//		}
//		return fileList;
//	}
	
	//TODO！！！整理一下垃圾代码
	private List<String> genCode(String basePath, String system, List<BpmFormTable> tables, String[] templateIds, int override,String defId, String flowKey, BpmFormDef bpmFormDef) throws Exception {
		List<String> fileList = new ArrayList<String>();
		for (int i = 0; i < templateIds.length; i++) {
			Long templateId = Long.parseLong(templateIds[i]);
			SysCodeTemplate template = sysCodeTemplateService.getById(templateId);
			for (BpmFormTable table : tables) {
				Map<String, String> variables = table.getVariable();
				variables.put("system", system);
				String fileName = template.getFileName();
				String fileDir = template.getFileDir();
				String path = basePath + File.separator + template.getFileDir();
				
				int isSub = template.getIsSub();
				if (table.getIsMain() != 1) {
					if (isSub == 0) {
						continue;
					}
				}
				
				Map<String, Object> model = new HashMap<String, Object>();
				
				
				IParseHandler parseHandler = parseHandlerMap.get(template.getTemplateAlias());
				
				if(parseHandler!=null){//找到解释器就说明有html要解释
					String html = bpmFormDef.getHtml();
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("table", table);
					param.put("bpmFormDef",bpmFormDef);
					String str = parseHandler.parseHtml(html, param);
					model.put("html", str);
				}
				
				//其他参数
				model.put("table", table);
				model.put("system", system);
				if (StringUtil.isNotEmpty(flowKey)) {
					model.put("flowKey", flowKey);
					model.put("defId", defId);
				}
				
				String templateStr = template.getHtml();
				String templatePath=SysCodeTemplateService.getRelateTemplatePath(template.getTemplateAlias());
				String html = freemarkEngine.mergeTemplateIntoString(templatePath , model);
				String fileStr = path + File.separator + fileName;
				String filePath = StringUtil.replaceVariable(fileStr, variables);
				addFile(filePath, html, override);
				String relativePath = StringUtil.replaceVariable(fileDir + File.separator + fileName, variables);
				fileList.add(relativePath);
			}
		}
		return fileList;
	}

	/**
	 * 根据前端所配置 获得表信息列表
	 * 
	 * @param request
	 * @return
	 */
	private List<BpmFormTable> getTableModels(HttpServletRequest request) {
		List<BpmFormTable> list = new ArrayList<BpmFormTable>();
		String[] tableIds = request.getParameterValues("tableId");
		String[] classNames = request.getParameterValues("className");
		String[] classVars = request.getParameterValues("classVar");
		String[] packageNames = request.getParameterValues("packageName");
		String system = request.getParameter("system");
		List<BpmFormTable> subtables = new ArrayList<BpmFormTable>();
		for (int i = 0; i < tableIds.length; i++) {
			Long tableId = Long.parseLong(tableIds[i]);
			Map<String, String> vars = new HashMap<String, String>();
			vars.put("class", classNames[i]);
			vars.put("classVar", classVars[i]);
			vars.put("package", packageNames[i]);
			vars.put("system", system);
			BpmFormTable bpmFormTable = bpmFormTableService.getById(tableId);
			bpmFormTable.setVariable(vars);
			List<BpmFormField> fieldList = bpmFormFieldService.getByTableIdContainHidden(tableId);
			List<BpmFormField> fields = new ArrayList<BpmFormField>();
			// 字段值来源为脚本计算时，脚本去掉换行处理
			for (BpmFormField field : fieldList) {
				/*if (bpmFormTable.getIsExternal() == 1) {//外部表变小写
					field.setFieldName(field.getFieldName().toLowerCase());
				}*/
				//下面开始处理单选，复选，下拉框的值
				String[] formDefIds = RequestUtil.getString(request, "formDefIds").split(",");
				try {
					for (String formDefId : formDefIds) {
						BpmFormDef bpmFormDef = bpmFormDefService.getById(Long.parseLong(formDefId));
						String options = CodeUtil.getDialogTags(getFormHtml(bpmFormDef, bpmFormTable, true), field);
						if (StringUtil.isNotEmpty(options)) {
							field.setOptions(options);
						}
					}
				} catch (Exception e) {
				}
				fields.add(field);
			}
			if (bpmFormTable.getIsExternal() == 1) {
				bpmFormTable.setPkField(bpmFormTable.getPkField().toLowerCase());
				if (bpmFormTable.getIsMain() != 1) {
					bpmFormTable.setRelation(bpmFormTable.getRelation().toLowerCase());
				}
			}
			bpmFormTable.setFieldList(fields);
			if (bpmFormTable.getIsMain() != 1) {
				subtables.add(bpmFormTable);
			}
			list.add(bpmFormTable);
		}
		for (BpmFormTable subtable : subtables) {
			for (BpmFormTable table : list) {
				if ((table.getIsMain() == 1) && (table.getTableId().equals(subtable.getMainTableId()))) {
					table.getSubTableList().add(subtable);
				}
			}
		}
		return list;
	}

	private String getFormHtml(BpmFormDef bpmFormDef, BpmFormTable table, boolean isEdit) throws Exception {
		String html = bpmFormDef.getHtml();
		if (BpmFormDef.DesignType_CustomDesign == bpmFormDef.getDesignType()) {
			ParseReult result = FormUtil.parseHtmlNoTable(html, table.getTableName(), table.getTableDesc());
			html = bpmFormHandlerService.obtainHtml(bpmFormDef.getTabTitle(), result, null, false);
		}
		String template = CodeUtil.getFreeMarkerTemplate(html, table, isEdit);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isEdit", isEdit);
		map.put("table", table);
		String output = freemarkEngine.parseByStringTemplate(map, template);
		return output;

	}
	

	private void addFile(String filePath, String html, int override) {
		File newFile = new File(filePath);
		if (newFile.exists()) {
			if (override == 1) {
				FileUtil.deleteFile(filePath);
				FileUtil.writeFile(filePath, html);
			}
		} else {
			FileUtil.writeFile(filePath, html);
		}
	}

	/**
	 * 生成代码zip
	 * <p>
	 * 由于原生成代码方法codegen()无论是否勾选打包文件，都会直接在平台中生成代码。因此极有可能带来平台文件管理的絮乱问题， 严重时还可能带来了平台的安全问题， 为了减小这种人为的疏忽错误，决定修改为：在生成代码时，先将所生成的代码打包成一个zip文件， 保持在webapp/codegen文件夹中，再提供用户下载。只能保留一个压缩文件。
	 * </p>
	 * 
	 * @param request
	 * @param response
	 * @author ouxb@jee-soft.cn
	 * @throws Exception
	 */
	@RequestMapping("codegenZip")
	public void codegenZip(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 文件相关参数
		String[] templateIds = request.getParameterValues("templateId");
		String flowKey = RequestUtil.getString(request, "defKey");
		String defId = RequestUtil.getString(request, "defId");
		String[] formDefIds = RequestUtil.getString(request, "formDefIds").split(",");
		// 自定义表相关
		String basePath = getBasePath() + File.separator + "codegen";
		String basePathZip = getBasePath() + File.separator + "codegen.zip";
		String system = RequestUtil.getString(request, "system");
		List<BpmFormTable> list = getTableModels(request);// 在获取过程中还解释
		try {
			for (String formDefId : formDefIds) {
				BpmFormDef bpmFormDef = bpmFormDefService.getById(Long.parseLong(formDefId));
				List<BpmFormTable> tables = new ArrayList<BpmFormTable>();
				Long tableId = bpmFormDef.getTableId();
				for (BpmFormTable model : list) {
					if (model.getTableId().equals(tableId) || model.getMainTableId().equals(tableId)) {
						tables.add(model);
					}
				}
				// 生成文件
				genCode(basePath, system, tables, templateIds, 1, defId,flowKey, bpmFormDef);
			}
			// 先删除掉已经压缩的文件
			FileUtil.deleteFile(basePathZip);
			// 压缩后删除其他被压缩文件
			ZipUtil.zip(basePath, true);

			writeResultMessage(response.getWriter(), new ResultMessage(ResultMessage.Success, "自定义表生成代码成功"));
		} catch (Exception e) {
			e.printStackTrace();
			writeResultMessage(response.getWriter(), new ResultMessage(ResultMessage.Fail, "自定义表生成代码失败:" + e.getMessage()));
		}
	}

	/**
	 * 下载文件的方法
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 * @throws Exception
	 */
	@RequestMapping("downLoadZip")
	public void downLoadZip(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			String basePathZip = getBasePath() + File.separator + "codegen.zip";
			File file = new File(basePathZip);
			InputStream in = new FileInputStream(file);
			String fileName = "codegen.zip";
			response.setContentType("application/x-download");
			if (System.getProperty("file.encoding").equals("GBK")) {
				response.setHeader("Content-Disposition", "attachment;filename=\"" + new String(fileName.getBytes(), "ISO-8859-1") + "\"");
			} else {
				response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(fileName, "utf-8") + "\"");
			}
			ServletOutputStream out = response.getOutputStream();
			IOUtils.copy(in, out);
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		} catch (Exception e) {
			writeResultMessage(response.getWriter(), new ResultMessage(ResultMessage.Fail, "文件不存在，请重新再生成一次!:" + e.getMessage()));
		}
	}

}
