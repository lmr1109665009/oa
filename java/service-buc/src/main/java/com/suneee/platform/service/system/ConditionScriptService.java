package com.suneee.platform.service.system;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.platform.dao.system.ConditionScriptDao;
import com.suneee.platform.model.system.ConditionScript;
import com.suneee.platform.xml.system.ConditionScriptXml;
import com.suneee.platform.xml.system.ConditionScriptXmlList;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.platform.xml.util.XmlUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.platform.dao.system.ConditionScriptDao;
import com.suneee.platform.model.system.ConditionScript;
import com.suneee.platform.model.system.Identity;
import com.suneee.platform.xml.system.ConditionScriptXml;
import com.suneee.platform.xml.system.ConditionScriptXmlList;
import com.suneee.platform.xml.system.IdentityXml;
import com.suneee.platform.xml.system.IdentityXmlList;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.platform.xml.util.XmlUtil;

/**
 *<pre>
 * 对象功能:系统条件脚本 Service类
 * 开发公司:hotent
 * 开发人员:heyifan
 * 创建时间:2013-04-05 11:34:56
 *</pre>
 */
@Service
public class ConditionScriptService extends BaseService<ConditionScript>
{
	@Resource
	private ConditionScriptDao dao;
	@Resource
	private GroovyScriptEngine groovyScriptEngine;
	
	public ConditionScriptService()
	{
	}
	
	@Override
	protected IEntityDao<ConditionScript, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 获取所有有效的条件脚本
	 * @return
	 */
	public List<ConditionScript> getConditionScript(){
		return dao.getConditionScript();
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
			if(!"boolean".equals(returnType)&&!"java.lang.Boolean".equals(returnType))continue;
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
	 * 验证脚本是否有效
	 * @throws Exception
	 */
	public String validScriptIsEnable() throws Exception{
		String message = "";
		Boolean hasUnEnable = false;
		List<ConditionScript> list = dao.getAll();
		if(BeanUtils.isEmpty(list)){
			message = "没有脚本记录。";
		}
		else{
			Class<?> t = null;
			for(ConditionScript conditionScript : list){
				try{
					t = Class.forName(conditionScript.getClassName());
					String paraStr = conditionScript.getArgument();
					JSONArray jarry = JSONArray.fromObject(paraStr);
					Integer size = jarry.size();
					Class<?>[] paras = new Class<?>[size];
					for(int i=0;i<size;i++){
						JSONObject jobject = jarry.getJSONObject(i);
						paras[i] = Class.forName(jobject.getString("paraType"));
					}
					Method method = t.getMethod(conditionScript.getMethodName(), paras);
					String returnTypeStr = method.getReturnType().getCanonicalName();
					if(!"boolean".equals(returnTypeStr)&&!"java.lang.Boolean".equals(returnTypeStr))
						throw new Exception("返回值不是Boolean类型");
					if(method.getModifiers()!=1)
						throw new Exception("方法不是public类型");
				}
				catch(Exception ex){
					hasUnEnable = true;
					conditionScript.setEnable(1L);
					dao.update(conditionScript);
				}
			}
			if(hasUnEnable)
				message = "验证完成，无效的脚本已被设置为无效。";
			else
				message = "验证完成，未发现无效脚本";
		}
		return message;
	}
	
	
	public Boolean execMethod(Long id,Object[] args){
		ConditionScript conditionScript = getById(id);
		if(conditionScript==null){
			return false;
		}
		
		String classInstName = conditionScript.getClassInsName();
		String methodName = conditionScript.getMethodName();
		String argStr = "";
		Map<String,Object> map = new HashMap<String, Object>();
		for(int i=0;i<args.length;i++){
			String argName = "_arg_"+i;
			map.put(argName, args[i]);
			argStr+=argName+",";
			
		}
		argStr = argStr.substring(0,argStr.length()-1);
		String script = classInstName+"."+methodName+"("+argStr+");";
		return groovyScriptEngine.executeBoolean(script, map);
		
//		return Void.TYPE;
	}
	
	/**
	 * 导出全部条件脚本
	 * @param conditionScripts
	 * @return
	 * @throws Exception
	 */
	public String exportXml(List<ConditionScript> conditionScripts)
			throws Exception {
		ConditionScriptXmlList conditionScriptXmlList = new ConditionScriptXmlList();
		List<ConditionScriptXml> list = new ArrayList<ConditionScriptXml>();
		for (ConditionScript conditionScript : conditionScripts) {

			ConditionScriptXml conditionScriptXml = this
					.exportConditionScriptXml(conditionScript);
			list.add(conditionScriptXml);
		}
		conditionScriptXmlList.setConditionScriptXmlList(list);
		return XmlBeanUtil.marshall(conditionScriptXmlList,
				ConditionScriptXmlList.class);
	}
	/**
	 * 导出条件脚本XML
	 * @param tableIds
	 * 
	 * @return
	 * @throws Exception
	 */
	public String exportXml(Long[] tableIds) throws Exception {
		ConditionScriptXmlList conditionScriptXmlList = new ConditionScriptXmlList();
		List<ConditionScriptXml> list = new ArrayList<ConditionScriptXml>();
		for (int i = 0; i < tableIds.length; i++) {
			ConditionScript conditionScript = dao.getById(tableIds[i]);
			ConditionScriptXml conditionScriptXml = this
					.exportConditionScriptXml(conditionScript);
			list.add(conditionScriptXml);
		}
		conditionScriptXmlList.setConditionScriptXmlList(list);
		return XmlBeanUtil.marshall(conditionScriptXmlList,
				ConditionScriptXmlList.class);
	}
	/**
	 * 导出表的信息
	 * @param conditionScript
	 * @param map
	 * @return
	 */
	private ConditionScriptXml exportConditionScriptXml(ConditionScript conditionScript)throws Exception
	{
		ConditionScriptXml conditionScriptXml=new ConditionScriptXml();
		Long id=conditionScript.getId();
		if(BeanUtils.isNotIncZeroEmpty(id)){
			//导出条件脚本
			conditionScriptXml.setConditionScript(conditionScript);
		}
		return conditionScriptXml;
	}
	
	/**
	 * 导入条件脚本XML
	 * @param inputStream
	 * @throws Exception
	 */
	public void importXml(InputStream inputStream) throws Exception {
		Document doc = Dom4jUtil.loadXml(inputStream);
		Element root = doc.getRootElement();
		// 验证格式是否正确
		XmlUtil.checkXmlFormat(root, "system", "conditionScripts");

		String xmlStr = root.asXML();
		ConditionScriptXmlList conditionScriptXmlList = (ConditionScriptXmlList) XmlBeanUtil
				.unmarshall(xmlStr, ConditionScriptXmlList.class);

		List<ConditionScriptXml> list = conditionScriptXmlList
				.getConditionScriptXmlList();

		for (ConditionScriptXml conditionScriptXml : list) {
			// 导入表，并解析相关信息
			this.importConditionScriptXml(conditionScriptXml);

		}

	}
	
	/**
	 * 导入时生成条件脚本
	 * @param conditionScriptXml
	 * @throws Exception
	 */
	private void importConditionScriptXml(ConditionScriptXml conditionScriptXml)
			throws Exception {
		Long conditionScriptId = UniqueIdUtil.genId();
		ConditionScript conditionScript = conditionScriptXml
				.getConditionScript();
		if (BeanUtils.isEmpty(conditionScript)) {
			throw new Exception();

		}
		String methodName = conditionScript.getMethodName();
		conditionScript.setId(conditionScriptId);
		dao.add(conditionScript);
		MsgUtil.addMsg(MsgUtil.SUCCESS, "方法名为" + methodName + "的条件脚本导入成功！");
	}
	
	
}
