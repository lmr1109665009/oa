package com.suneee.platform.service.system;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.platform.dao.system.AliasScriptDao;
import com.suneee.platform.model.system.AliasScript;
import com.suneee.platform.xml.system.AliasScriptXml;
import com.suneee.platform.xml.system.AliasScriptXmlList;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.platform.xml.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.platform.dao.system.AliasScriptDao;
import com.suneee.platform.model.system.AliasScript;
import com.suneee.platform.xml.system.AliasScriptXml;
import com.suneee.platform.xml.system.AliasScriptXmlList;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.platform.xml.util.XmlUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *<pre>
 * 对象功能:自定义别名脚本表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-12-19 11:26:03
 *</pre>
 */
@Service
public class AliasScriptService extends BaseService<AliasScript>
{
	@Resource
	private AliasScriptDao dao;
	
	
	
	public AliasScriptService(){
	}
	
	@Override
	protected IEntityDao<AliasScript, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 通过类名获取类的所有方法
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public JSONArray getMethodsByName(String name) throws Exception{
		JSONArray jarray = new JSONArray(); 
		Class<?> t = Class.forName(name);
		Method[] methods = t.getDeclaredMethods();
		for(Method method : methods){
			String returnType = method.getReturnType().getCanonicalName();
			//只要返回值为boolean的方法
			//if(!"boolean".equals(returnType)&&!"java.lang.Boolean".equals(returnType))continue;
			Integer modifirer = method.getModifiers();
			//只要public方法
			if(modifirer!=1)continue;
			JSONObject jobMethod = new JSONObject();
			JSONArray jaryPara = new JSONArray();
			Class<?>[] paraArr = method.getParameterTypes();
			for(int i=0;i<paraArr.length;i++){
				Class<?> para = paraArr[i];
				String paraName = "arg" + i;
				String paraType = para.getCanonicalName();
				jaryPara.add(new JSONObject().accumulate("paraName", paraName)
											 .accumulate("paraType", paraType)
											 .accumulate("paraDesc", ""));
			}
			jobMethod.accumulate("returnType", returnType)
					 .accumulate("methodName", method.getName())
					 .accumulate("para", jaryPara);
			jarray.add(jobMethod);
		}
		return jarray;
	}
	
	
	/**
	 * 检查别名是否存在
	 * @param userId
	 * @param map
	 * @return
	 */
	public AliasScript getAliasScriptByName(String alias){
		AliasScript as = dao.getAliasScriptByName(alias);
		return as;
		
	}
	
	/**
	 * 根据是否有返回值格式配置获取列表
	 * @param returnValue
	 * @return 
	 * AliasScript
	 * @exception 
	 * @since  1.0.0
	 */
	public List<AliasScript> getByReturnValue(Short returnValue){		
		return dao.getByReturnValue(returnValue);
	}
	
	/**
	 * 导出全部别名脚本
	 * @param aliasScripts
	 * @return
	 * @throws Exception
	 */
	public String exportXml(List<AliasScript> aliasScripts) throws Exception {
		AliasScriptXmlList aliasScriptXmlList = new AliasScriptXmlList();
		List<AliasScriptXml> list = new ArrayList<AliasScriptXml>();
		for (AliasScript aliasScript : aliasScripts) {

			AliasScriptXml aliasScriptXml = this
					.exportAliasScriptXml(aliasScript);
			list.add(aliasScriptXml);
		}
		aliasScriptXmlList.setAliasScriptXmlList(list);
		return XmlBeanUtil.marshall(aliasScriptXmlList,
				AliasScriptXmlList.class);
	}
	
	
	/**
	 * 导出别名脚本XML
	 * @param tableIds
	 * 
	 * @return
	 * @throws Exception
	 */
	public String exportXml(Long[] tableIds) throws Exception {
		AliasScriptXmlList aliasScriptXmlList = new AliasScriptXmlList();
		List<AliasScriptXml> list = new ArrayList<AliasScriptXml>();
		for (int i = 0; i < tableIds.length; i++) {
			AliasScript aliasScript = dao.getById(tableIds[i]);
			AliasScriptXml aliasScriptXml = this
					.exportAliasScriptXml(aliasScript);
			list.add(aliasScriptXml);
		}
		aliasScriptXmlList.setAliasScriptXmlList(list);
		return XmlBeanUtil.marshall(aliasScriptXmlList,
				AliasScriptXmlList.class);
	}
	/**
	 * 导出表的信息
	 * @param aliasScript
	 * @param map
	 * @return
	 */
	private AliasScriptXml exportAliasScriptXml(AliasScript aliasScript)throws Exception
	{
		AliasScriptXml aliasScriptXml=new AliasScriptXml();
		Long id=aliasScript.getId();
		if(BeanUtils.isNotIncZeroEmpty(id)){
			//导出别名脚本
			aliasScriptXml.setAliasScript(aliasScript);
		}
		return aliasScriptXml;
	}
	
	
	/**
	 * 导入别名脚本XML
	 * @param inputStream
	 * @throws Exception
	 */
	public void importXml(InputStream inputStream) throws Exception {
		Document doc = Dom4jUtil.loadXml(inputStream);
		Element root = doc.getRootElement();
		// 验证格式是否正确
		XmlUtil.checkXmlFormat(root, "system", "aliasScripts");

		String xmlStr = root.asXML();
		AliasScriptXmlList aliasScriptXmlList = (AliasScriptXmlList) XmlBeanUtil
				.unmarshall(xmlStr, AliasScriptXmlList.class);

		List<AliasScriptXml> list = aliasScriptXmlList.getAliasScriptXmlList();

		for (AliasScriptXml aliasScriptXml : list) {
			// 导入表，并解析相关信息
			this.importAliasScriptXml(aliasScriptXml);

		}

	}
	/**
	 * 导入时生成条件脚本
	 * @param personScriptXml
	 * @return
	 * @throws Exception
	 */
	private void importAliasScriptXml(AliasScriptXml aliasScriptXml)
			throws Exception {
		Long aliasId = UniqueIdUtil.genId();
		AliasScript aliasScript = aliasScriptXml.getAliasScript();
		if (BeanUtils.isEmpty(aliasScript)) {

			throw new Exception();
		}
		String aliasName = aliasScript.getAliasName();
		Map<String, String> map = new HashMap<String, String>();
		map.put("aliasName", aliasName);
		AliasScript script = this
				.getAliasScriptByName(aliasName);
		if (BeanUtils.isNotEmpty(script)) {
			MsgUtil.addMsg(MsgUtil.WARN, "别名为‘" + aliasName
					+ "’的别名脚本已经存在，请检查你的xml文件！");
			return;
		}
		aliasScript.setId(aliasId);
		dao.add(aliasScript);
		MsgUtil.addMsg(MsgUtil.SUCCESS, "别名为" + aliasName + "的别名脚本导入成功！");
	}
	
	
}
