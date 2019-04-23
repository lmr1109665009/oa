package com.suneee.platform.service.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.form.BpmMobileFormDefDao;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.form.BpmFormTemplate;
import com.suneee.platform.model.form.BpmMobileFormDef;
import com.suneee.platform.model.form.TeamModel;
import com.suneee.platform.service.bpm.BpmDefinitionService;

import freemarker.template.TemplateException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *<pre>
 * 对象功能:手机表单 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:miao
 * 创建时间:2015-10-28 10:52:49
 *</pre>
 */
@Service
public class BpmMobileFormDefService extends BaseService<BpmMobileFormDef>
{
	@Resource
	private BpmMobileFormDefDao dao;
	@Resource
	private BpmFormTableService bpmFormTableService;
	@Resource
	private BpmFormFieldService bpmFormFieldService;
	@Resource
	private FreemarkEngine freemarkEngine;
	@Resource
	private BpmFormTemplateService bpmFormTemplateService;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	
	public BpmMobileFormDefService()
	{
	}
	
	@Override
	protected IEntityDao<BpmMobileFormDef, Long> getEntityDao()
	{
		return dao;
	}
	

	public Integer getCountByFormKey(String formKey) {
		return (Integer) dao.getOne("getCountByFormKey", formKey);
	}	
	
	
	/**
	 * 通过分组获取默认的分组
	 * @param formDef
	 * @param request 
	 * @param template 模板名称
	 * @return
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	public String getDefaultMobileHtml(BpmMobileFormDef formDef, String[] templates) throws TemplateException, IOException {
			Long tableId=formDef.getTableId();
			BpmFormTable bpmFormTable = bpmFormTableService.getTableById(tableId);
			
			
			Map<String,String> tableTmpMap=getTableTemplateMap(templates);
			//保存模版和表的映射关系
			setBpmMobileFormDef(formDef,templates);

			List<BpmFormField> fields = bpmFormTable.getFieldList();
			Map<String, Object> map = new HashMap<String, Object>();
			removeField(bpmFormTable,fields);
			if(StringUtil.isNotEmpty(bpmFormTable.getTeam())){
				this.setTeamFields(map, bpmFormTable, fields);
			}
			map.put("title", bpmFormTable.getTableDesc());
			map.put("fields", fields);
			
			//主表
			StringBuffer sb=new StringBuffer();
			String template=tableTmpMap.get(tableId.toString());
			
			BpmFormTemplate mainTemplate=bpmFormTemplateService.getByTemplateAlias(template);
			String macroAlias=mainTemplate.getMacroTemplateAlias();
			BpmFormTemplate macroTemplate=bpmFormTemplateService.getByTemplateAlias(macroAlias);
			String html = freemarkEngine.parseByStringTemplate(map,macroTemplate.getHtml() + mainTemplate.getHtml() );
			
			sb.append("<form name='myform' class='am-form am-form-horizontal' novalidate>");
			sb.append(html);
			//子表处理
			if(BeanUtils.isNotEmpty(bpmFormTable.getSubTableList())){
				// 子表模板
				for(BpmFormTable subTable : bpmFormTable.getSubTableList()) {
					Map<String, Object> subMap = new HashMap<String, Object>();
					if(StringUtil.isNotEmpty(subTable.getTeam())){
						this.setTeamFields(subMap, subTable, subTable.getFieldList());
					}
					subMap.put("subTable", subTable);
					subMap.put("fields", subTable.getFieldList());
					
					String subAlias=tableTmpMap.get(subTable.getTableId().toString());
					
					BpmFormTemplate subTemplate=bpmFormTemplateService.getByTemplateAlias(subAlias);
					BpmFormTemplate macroSubTemplate=bpmFormTemplateService.getByTemplateAlias(subTemplate.getMacroTemplateAlias());
					
					String subHtml = freemarkEngine.parseByStringTemplate(subMap,macroSubTemplate.getHtml() + subTemplate.getHtml() );
					
					sb.append(subHtml);
				}
			}
			sb.append("</form> ");

			return sb.toString();
	}
	
	/**
	 * 根据表和对应的模版生成html
	 * @param tableIds
	 * @param templates
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	public String genHtml(Long[] tableIds, String[] templates) throws TemplateException, IOException {
		
		StringBuffer sb=new StringBuffer();
		sb.append("<form name='myform' class='am-form am-form-horizontal' novalidate>");
		
		for(int i=0;i<tableIds.length;i++){
		
			long lTableId=tableIds[i];
			String template=templates[i];
			BpmFormTable bpmFormTable= bpmFormTableService.getById(lTableId);
			List<BpmFormField> fields =bpmFormFieldService.getByTableId(lTableId);
			//主表的情况
			if(bpmFormTable.getIsMain()==BpmFormTable.IS_MAIN){
				Map<String, Object> map = new HashMap<String, Object>();
				removeField(bpmFormTable,fields);
				if(StringUtil.isNotEmpty(bpmFormTable.getTeam())){
					this.setTeamFields(map, bpmFormTable, fields);
				}
				map.put("title", bpmFormTable.getTableDesc());
				map.put("fields", fields);
				
				BpmFormTemplate mainTemplate=bpmFormTemplateService.getByTemplateAlias(template);
				String macroAlias=mainTemplate.getMacroTemplateAlias();
				BpmFormTemplate macroTemplate=bpmFormTemplateService.getByTemplateAlias(macroAlias);
				String html = freemarkEngine.parseByStringTemplate(map,macroTemplate.getHtml() + mainTemplate.getHtml() );
				
				sb.append(html);
			}
			//子表的情况
			else{
				Map<String, Object> subMap = new HashMap<String, Object>();
				if(StringUtil.isNotEmpty(bpmFormTable.getTeam())){
					this.setTeamFields(subMap, bpmFormTable, fields);
				}
				subMap.put("subTable", bpmFormTable);
				subMap.put("fields", fields);
				
				BpmFormTemplate subTemplate=bpmFormTemplateService.getByTemplateAlias(template);
				BpmFormTemplate macroSubTemplate=bpmFormTemplateService.getByTemplateAlias(subTemplate.getMacroTemplateAlias());
				
				String subHtml = freemarkEngine.parseByStringTemplate(subMap,macroSubTemplate.getHtml() + subTemplate.getHtml() );
				
				sb.append(subHtml);
			}
		}

		 sb.append("</form> ");
		return sb.toString();
	}
	
	private void setBpmMobileFormDef(BpmMobileFormDef formDef,String[] templates){
		String str="";
		for(int i=0;i<templates.length;i++){
			if(i==0){
				str+=templates[i];
			}
			else{
				str+=";" + templates[i];
			}
		}
		formDef.setTemplatesId(str);
	}
	
	/**
	 * 根据模版返回表和模版的键值对。
	 * @param templates
	 * @return
	 */
	private Map<String,String> getTableTemplateMap(String[] templates){
		Map<String,String> map=new HashMap<String,String>();
		for(String str : templates){
			String[] ary=str.split(",");
			map.put(ary[0], ary[1]);
		}
		return map;
	}

	/**
	 * 处理将字段赋予分组
	 * 
	 * @param fields
	 * @param fieldName
	 * @return
	 */
	private List<TeamModel> setTeamFields(Map<String, Object> fieldsMap,
                                          BpmFormTable table, List<BpmFormField> fields) {
		if (StringUtil.isEmpty(table.getTeam()))
			return null;
		List<TeamModel> list = new ArrayList<TeamModel>();
		JSONObject json = JSONObject.fromObject(table.getTeam());
		fieldsMap.put("isShow", json.get("isShow"));
		fieldsMap.put("showPosition", json.get("showPosition"));

		JSONArray teamJson = JSONArray.fromObject(json.get("team"));
		for (Object obj : teamJson) {
			TeamModel teamModel = new TeamModel();
			JSONObject jsonObj = (JSONObject) obj;
			String teamName = (String) jsonObj.get("teamName");
			teamModel.setTeamName(teamName);
			// 获取字段
			JSONArray jArray = (JSONArray) jsonObj.get("teamField");
			List<BpmFormField> teamFields = new ArrayList<BpmFormField>();
			for (Object object : jArray) {
				JSONObject fieldObj = (JSONObject) object;
				String fieldName = (String) fieldObj.get("fieldName");
				BpmFormField bpmFormField = this
						.getTeamField(fields, fieldName);
				if (BeanUtils.isNotEmpty(bpmFormField)) {
					fields.remove(bpmFormField);
					teamFields.add(bpmFormField);
				}
			}
			//this.setBpmFormFieldName(table, teamFields);
			teamModel.setTeamFields(teamFields);
			list.add(teamModel);
		}
		fieldsMap.put("teamFields", list);
		return list;
	}
	/**
	 * 删除外部表的主键外键字段。
	 * @param table
	 * @param fields
	 */
	private void removeField(BpmFormTable table ,List<BpmFormField> fields){
		if(!table.isExtTable()) return ;
		String pk=table.getPkField();
		String fk=table.getRelation();
		for(Iterator<BpmFormField> it=fields.iterator();it.hasNext();){
			BpmFormField field=it.next();
			if(field.getFieldName().equalsIgnoreCase(fk)){
				it.remove();
				continue;
			}
			if(field.getFieldName().equalsIgnoreCase(pk)){
				it.remove();
			}
		}
	}
	/**
	 * 获取分组字段
	 * 
	 * @param fields
	 * @param fieldName
	 * @return
	 */
	private BpmFormField getTeamField(List<BpmFormField> fields,
			String fieldName) {
		for (BpmFormField bpmFormField : fields) {
			if (bpmFormField.getFieldName().equals(fieldName))
				return bpmFormField;
		}
		return null;
	}

	/**
	 * 获取默认的手机表单定义。
	 * @param formKey
	 * @return
	 */
	public 	BpmMobileFormDef getDefaultByFormKey(String formKey){
		return dao.getDefaultByFormKey(formKey);
	}
	/***
	 * 将手机表单设置默认
	 * @param id
	 */
	public void setDefaultVersion(Long id) {
		BpmMobileFormDef def = dao.getById(id);
		String formKey = def.getFormKey();
		dao.update("setNotDefaultByFromKey",formKey);
		def.setIsDefault(1);
		dao.update(def);
	}

	public List<BpmMobileFormDef> getByFormKey(String formKey) {
		return dao.getBySqlKeyGenericity("getByFormKey", formKey);
	}
	
	/** 批量删除手机表单定义
	 * @param formKeys
	 * @return
	 */
	public List<String> delByFormKeys(String[] formKeys){
		List<String> formNames = new ArrayList<>();
		if(formKeys != null){
			for(String formKey : formKeys){
				// 根据手机表单key查询默认手机表单
				BpmMobileFormDef mobileFormDef = this.getDefaultByFormKey(formKey);
				// 如果默认手机表单不存在，则进行下次循环
				if(mobileFormDef == null){
					continue;
				}
				
				// 根据手机表单key查询绑定的流程
				List<BpmDefinition> bpmDefinitionList = bpmDefinitionService.getByMobileFormKey(formKey);
				// 如果手机表单未绑定流程，则直接根据表单key删除表单数据
				if(bpmDefinitionList.size() <= 0){
					this.delByFormKey(formKey);
				}
				// 如果手机表单绑定了流程，则记录该手机表单的名称用于接口返回提示
				else {
					formNames.add(mobileFormDef.getSubject());
				}
			}
		}
		return formNames;
	}
	
	/** 根据表单key删除手机表单定义
	 * @param formKey
	 */
	public void delByFormKey(String formKey){
		dao.delByFormKey(formKey);
	}
}
