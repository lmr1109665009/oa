package com.suneee.platform.service.form;


import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.form.BpmFormTemplateDao;
import com.suneee.platform.model.form.BpmFormTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象功能:表单模板 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zyp
 * 创建时间:2012-01-09 16:35:25
 */
@Service
public class BpmFormTemplateService extends BaseService<BpmFormTemplate>
{
	private static Log logger = LogFactory.getLog(BpmFormTemplateService.class);
	@Resource
	private BpmFormTemplateDao dao;
	
	public BpmFormTemplateService()
	{
	}
	
	@Override
	protected IEntityDao<BpmFormTemplate, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 返回模版物理的路径。
	 * @return
	 */
	public static  String getFormTemplatePath(){
		return FileUtil.getClassesPath() + "template" + File.separator +"form" + File.separator;
	}
	
	/**
	 * 根据模版别名取得模版。
	 * @param alias
	 * @return
	 */
	public BpmFormTemplate getByTemplateAlias(String alias){
		return dao.getByTemplateAlias(alias);
	}
	
	/**
	 * 获取所有的系统原始模板
	 * @return
	 * @throws Exception 
	 */
	public void initAllTemplate() throws Exception{
		dao.delSystem();
		addTemplate();
	}
	
	/**
	 * 当模版数据为空时，将form目录下的模版添加到数据库中。
	 */
	public void init()  throws Exception{
		Integer amount=dao.getHasData();
		if(amount==0){
			addTemplate();
		}
	}
	
	/**
	 * 初始化模版，在系统启用的时候进行调用。
	 */
	public static void initTemplate(){
		BpmFormTemplateService service= (BpmFormTemplateService) AppUtil.getBean(BpmFormTemplateService.class);
		try {
			service.init();
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
	}
	
	/**
	 * 初始化添加form下的模版数据到数据库。
	 */
	private void addTemplate()  throws Exception{
		String templatePath=getFormTemplatePath();
		String xml= FileUtil.readFile(templatePath +"templates.xml");
		Document document= Dom4jUtil.loadXml(xml);
		Element root=document.getRootElement();
		List<Element> list=root.elements();
		for(Element element:list){
			addTemplate(element,templatePath);
		}
	}
	
	/**
	 * 添加模版。
	 * @param element
	 * @param templatePath
	 */
	private void addTemplate(Element element,String templatePath){
		String alias=element.attributeValue("alias");
		String name=element.attributeValue("name");
		String type=element.attributeValue("type");
		String templateDesc=element.attributeValue("templateDesc");
		String macroAlias=element.attributeValue("macroAlias");
		String dir=element.attributeValue("dir");
		
		String fileName=templatePath +dir +File.separator +  alias+".ftl";
		String html= FileUtil.readFile(fileName);
		String contextPath="";
		try {
			contextPath=AppUtil.getServletContext().getContextPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		html=html.replaceAll("\\{\\{ctx\\}\\}",contextPath);

		BpmFormTemplate template=new BpmFormTemplate();
		template.setTemplateId(UniqueIdUtil.genId());
		template.setMacroTemplateAlias(macroAlias);
		template.setHtml(html);
		template.setTemplateName(name);
		template.setAlias(alias);
		template.setCanEdit(0);
		template.setTemplateType(type);
		template.setTemplateDesc(templateDesc);
		dao.add(template);
	}
	
	/**
	 * 检查模板别名是否唯一
	 * @param alias
	 * @return
	 */
	public boolean isExistAlias(String alias){
		List<BpmFormTemplate>list=dao.getAll();
		for(BpmFormTemplate bpmFormTemplate:list){
			if(bpmFormTemplate.getAlias().equals(alias)){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	/**
	 * 将用户自定义模板备份
	 * @param id
	 */
	public void backUpTemplate(Long id){
		BpmFormTemplate bpmFormTemplate=dao.getById(id);
		String alias=bpmFormTemplate.getAlias();
		String name=bpmFormTemplate.getTemplateName();
		String desc=bpmFormTemplate.getTemplateDesc();
		String html=bpmFormTemplate.getHtml();
		String type=bpmFormTemplate.getTemplateType();
		String macroAlias=bpmFormTemplate.getMacroTemplateAlias();
		
		String templatePath=getFormTemplatePath();
		
		String xmlPath=templatePath +"templates.xml";
		String xml= FileUtil.readFile(xmlPath);
		
		Document document=Dom4jUtil.loadXml(xml);
		Element root=document.getRootElement();
		
		Element e=root.addElement("template");
		e.addAttribute("alias", alias);
		e.addAttribute("name", name);
		e.addAttribute("type", type);
		e.addAttribute("templateDesc", desc);
		e.addAttribute("macroAlias",macroAlias);
		String content=document.asXML();
		
		FileUtil.writeFile(xmlPath, content);
		FileUtil.writeFile(templatePath +alias+".ftl", html);
		
		bpmFormTemplate.setCanEdit(0);
		dao.update(bpmFormTemplate);
		
	}
	
	/**
	 * 根据模版类型取得模版列表。
	 * @param type
	 * @return
	 */
	public List<BpmFormTemplate> getTemplateType(String type){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("templateType",type);
		return dao.getAll( params);
	}
	
	/**
	 * 获取主表模版
	 * @return
	 */
	public List<BpmFormTemplate> getAllMainTableTemplate() {
		return getTemplateType(BpmFormTemplate.MAIN_TABLE);
	}
	
	/**
	 * 获取子表模版。
	 * @return
	 */
	public List<BpmFormTemplate> getAllSubTableTemplate() {
		return getTemplateType(BpmFormTemplate.SUB_TABLE);
	}

	/**
	 * 获取宏模版。
	 * @return
	 */
	public List<BpmFormTemplate> getAllMacroTemplate() {
		return getTemplateType(BpmFormTemplate.MACRO);
	}
	
	/**
	 * 获取表管理模版。
	 * @return
	 */
	public List<BpmFormTemplate> getAllTableManageTemplate() {
		return getTemplateType(BpmFormTemplate.TABLE_MANAGE);
	}
	
	/**
	 * 获取列表模版。
	 * @return
	 */
	public List< BpmFormTemplate> getListTemplate() {
		return getTemplateType(BpmFormTemplate.LIST);
	}
	
	/**
	 * 获取明细模版。
	 * @return
	 */
	public List< BpmFormTemplate> getDetailTemplate() {
		return getTemplateType(BpmFormTemplate.DETAIL);
	}
	
	/**
	 * 获取数据模版。
	 * @return
	 */
	public List< BpmFormTemplate> getDataTemplate() {
		return getTemplateType(BpmFormTemplate.DATA_TEMPLATE);
	}
	/**
	 * 获取查询数据模版。
	 * @return
	 */
	public List< BpmFormTemplate> getQueryDataTemplate() {
		return getTemplateType(BpmFormTemplate.QUERY_DATA_TEMPLATE);
	}
	
	
}
