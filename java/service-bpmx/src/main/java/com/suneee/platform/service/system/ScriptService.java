package com.suneee.platform.service.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;


import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.ScriptDao;
import com.suneee.platform.model.system.Script;
import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.Script;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.dao.system.ScriptDao;
import com.suneee.platform.dao.system.SysFileDao;

/**
 * 对象功能:脚本管理 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-01-05 12:01:20
 */
@Service
public class ScriptService extends BaseService<Script>
{
	@Resource
	private ScriptDao dao;
	

		
	
	public ScriptService()
	{
	}
	
	@Override
	protected IEntityDao<Script, Long> getEntityDao()
	{
		return dao;
	}
	
	public List<String> getDistinctCategory(){
		return dao.getDistinctCategory();
	}
	
	
	/**
	 * 导入自定义表XML
	 * @param fileStr
	 * @throws Exception
	 */
	public void importXml(InputStream inputStream) throws Exception
	{
		Document doc= Dom4jUtil.loadXml(inputStream);
        Element root = doc.getRootElement();
        List<Element> itemLists = root.elements();
        if(BeanUtils.isNotEmpty(itemLists)){
    		for(Element elm : itemLists) {
    			String itemName=elm.element("name").getText();   			
    				Long scriptId = UniqueIdUtil.genId(); //产生id
    				String scriptText=elm.element("content").getText();
    				String memoText=elm.element("Memo").getText();
    				String scriptType=elm.element("type").getText();
    				Script script=new Script();
    				script.setId(scriptId);
    				script.setName(itemName);
    				script.setScript(scriptText);
    				script.setMemo(memoText);
    				script.setCategory(scriptType);
    				dao.add(script);		
    		}
        }
	}
	
	/**
	 * 导出自定义表XML
	 * @param Long[] tableId
	 * @return
	 */
	public String exportXml(Long[] aryScriptId) throws FileNotFoundException, IOException{		
		String strXml="";
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("List");
		for(int i=0;i<aryScriptId.length;i++){
			Script script=dao.getById(aryScriptId[i]);
			if(BeanUtils.isNotEmpty(script)){	
				// 生成主表
				exportTable(script, root, "Script");
			}
		}
		strXml=doc.asXML();
		return strXml;
		
	}
	
	/**
	 * 生成表代码
	 * @param bpmFormTable
	 * @param root
	 * @param nodeName
	 */
	private void exportTable(Script script, Element root, String nodeName) 
	{		
		Element elScript = root.addElement(nodeName);
		
		if(StringUtil.isNotEmpty(script.getName())){
			elScript.addElement("name").addText(script.getName());
		}
		if(StringUtil.isNotEmpty(script.getScript())){
			Element elContent=elScript.addElement("content");
			elContent.addCDATA(script.getScript());
		}
		if(BeanUtils.isNotEmpty(script.getCategory())){
			elScript.addElement("type").addText(script.getCategory());
		}
		if(BeanUtils.isNotEmpty(script.getMemo())){
			elScript.addElement("Memo").addText(script.getMemo());
		}
	}
}
