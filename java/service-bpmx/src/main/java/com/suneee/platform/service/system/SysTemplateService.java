package com.suneee.platform.service.system;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.platform.dao.system.SysTemplateDao;
import com.suneee.platform.model.system.SysTemplate;
import com.suneee.platform.xml.system.SysTemplateObjectFactory;
import com.suneee.platform.xml.system.SysTemplates;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象功能:模版管理 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:pkq
 * 创建时间:2011-12-28 14:04:30
 */
@Service
public class SysTemplateService extends BaseService<SysTemplate>
{
	@Resource
	private SysTemplateDao dao;
	
	public SysTemplateService()
	{
	}
	
	@Override
	protected IEntityDao<SysTemplate, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 设置默认模板
	 * @param id
	 */
	public void setDefault(Long id){
		SysTemplate sysTemplate = dao.getById(id);
		Integer useType = sysTemplate.getUseType();
		dao.setNotDefaultByUseType(useType);
		dao.setDefault(id);
	}
	
	/**
	 * 删除指定类型模板
	 * @param id
	 */
	public void delByUseType(Integer useType){
		dao.delByUseType(useType);
	}
	
	/**
	 * 获取信息类型、用途类型的默认模板
	 * @param tempType
	 * @return
	 */
	public SysTemplate getDefaultByUseType(Integer useType)
	{
		return dao.getDefaultByUseType(useType);
	}
	
	

	/**
	 * 
	 * 获取对应的消息模板字符串
	 * 
	 * @param useType 信息类型 （查看SysTemplate定义类型）
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getTempByFun(Integer useType) throws Exception {
		SysTemplate sysTemplate = this.getDefaultByUseType(useType);

		String title = "";
		String html = "";
		String plain = "";
		if (BeanUtils.isNotEmpty(sysTemplate)) {
			title = sysTemplate.getTitle();
			html =  sysTemplate.getHtmlContent();
			plain = sysTemplate.getPlainContent();
		} else {
			throw new Exception("未找系统模板");
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put(SysTemplate.TEMPLATE_TITLE, title);
		map.put(SysTemplate.TEMPLATE_TYPE_HTML, html);
		map.put(SysTemplate.TEMPLATE_TYPE_PLAIN, plain);
		// map.put(SysTemplate.TEMPLATE_USETYPE, useType);
		return map;
	}

	/**
	 * 获取默认的消息模板字符串
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getDefaultTemp() throws Exception {
		return this.getTempByFun(SysTemplate.USE_TYPE_NOTIFY);
	
	}
	
	
	public static String marshal(List<SysTemplate> templateList) throws JAXBException{
		SysTemplates sysTemplates = new SysTemplates();
		sysTemplates.setSysTemplates(templateList);
		return XmlBeanUtil.marshall(sysTemplates, SysTemplateObjectFactory.class);
	}
	
	
	public List<SysTemplate> unmarshal(String xml) throws JAXBException{
		SysTemplates sysTemplates = (SysTemplates) XmlBeanUtil.unmarshall(xml, SysTemplateObjectFactory.class);
		return sysTemplates.getSysTemplates();
	}
	
	public List<SysTemplate> unmarshal(InputStream is) throws JAXBException{
		SysTemplates sysTemplates = (SysTemplates) XmlBeanUtil.unmarshall(is, SysTemplateObjectFactory.class);
		return sysTemplates.getSysTemplates();
	}
	
	public List<SysTemplate> getByIds(Long[] ids){
		List<SysTemplate> sysTemplates = new ArrayList<SysTemplate>();
		for(Long id:ids){
			SysTemplate sysTemplate = dao.getById(id);
			sysTemplates.add(sysTemplate);
		}
		return sysTemplates;
	}
	
	public String exportXml(Long[] ids) throws JAXBException{
		List<SysTemplate> sysTemplates = getByIds(ids);
		return exportXml(sysTemplates);
	}
	
	public String exportXml(List<SysTemplate> sysTemplates) throws JAXBException{
		return marshal(sysTemplates);
	}
	
	public void importXml(String xml) throws JAXBException{
		importXml(new ByteArrayInputStream(xml.getBytes()));
	}
	
	public void importXml(InputStream is) throws JAXBException{
		List<SysTemplate> sysTemplates = unmarshal(is);
		for(SysTemplate sysTemplate:sysTemplates){
			dao.add(sysTemplate);
		}
	}
}
