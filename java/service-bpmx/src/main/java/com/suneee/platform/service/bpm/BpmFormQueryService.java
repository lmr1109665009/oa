package com.suneee.platform.service.bpm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.*;
import com.suneee.platform.dao.bpm.BpmFormQueryDao;
import com.suneee.platform.model.bpm.BpmFormQuery;
import com.suneee.platform.model.form.DialogField;
import com.suneee.platform.model.form.QueryResult;
import com.suneee.platform.xml.form.BpmFormQueryXml;
import com.suneee.platform.xml.form.BpmFormQueryXmlList;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.platform.xml.util.XmlUtil;
import net.sf.json.JSONObject;

//import org.apache.cxf.binding.corba.wsdl.Exception;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.platform.dao.bpm.BpmFormQueryDao;
import com.suneee.platform.model.bpm.BpmFormQuery;
import com.suneee.platform.model.form.BpmFormDialog;
import com.suneee.platform.model.form.DialogField;
import com.suneee.platform.model.form.QueryResult;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.platform.xml.form.BpmFormDialogXml;
import com.suneee.platform.xml.form.BpmFormDialogXmlList;
import com.suneee.platform.xml.form.BpmFormQueryXml;
import com.suneee.platform.xml.form.BpmFormQueryXmlList;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.platform.xml.util.XmlUtil;

/**
 * 对象功能:通用表单查询 Service类 开发公司:广州宏天软件有限公司 开发人员:ray 创建时间:2012-11-27 10:37:12
 */
@Service
public class BpmFormQueryService extends BaseService<BpmFormQuery> {
	@Resource
	private BpmFormQueryDao dao;

	public BpmFormQueryService() {
	}

	@Override
	protected IEntityDao<BpmFormQuery, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 根据别名获取对话框对象。
	 * 
	 * @param alias
	 * @return
	 */
	public BpmFormQuery getByAlias(String alias) {
		return dao.getByAlias(alias);
	}

	/**
	 * 检查别名是否唯一
	 * 
	 * @param alias
	 * @return
	 */
	public boolean isExistAlias(String alias) {
		return dao.isExistAlias(alias) > 0;
	}

	/**
	 * 检查别名是否唯一。
	 * 
	 * @param alias
	 * @return
	 */
	public boolean isExistAliasForUpd(Long id, String alias) {
		return dao.isExistAliasForUpd(id, alias) > 0;
	}

	/**
	 * 根据别名获取对应查询的数据
	 * 
	 * @param bpmFormQuery
	 *            表单查询对象
	 * @param params
	 *            参数
	 * @param page
	 *            页码
	 * @param pageSize
	 *            每页记录条数
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public QueryResult getData(BpmFormQuery bpmFormQuery, String queryData, Integer page, Integer pageSize) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtil.isNotEmpty(queryData)) {
			JSONObject jsonObj = JSONObject.fromObject(queryData);
			Iterator<?> it = jsonObj.keys();
			while (it.hasNext()) {
				String key = it.next().toString();
				String value = jsonObj.getString(key);
				params.put(key, value);
			}
		}


		
		List<DialogField> resultList = bpmFormQuery.getReturnList();
		List<DialogField> conditionList = bpmFormQuery.getConditionList();
		String objectName = bpmFormQuery.getObjName();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("objectName", objectName);
		map.put("returnList", resultList);
		map.put("conditionList", conditionList);
		map.put("sortList", bpmFormQuery.getSortList());
		QueryResult queryResult = new QueryResult();
		// 是否需要分页。
		if (page > 0 && pageSize > 0) {
			String sql = ServiceUtil.getSql(map, params);
			
			PageList pageList= JdbcTemplateUtil.getPage(bpmFormQuery.getDsalias(), page, pageSize, sql, params);
			List list =  handList(pageList);
			queryResult.setList(list);
			queryResult.setIsPage(1);
			queryResult.setPage(page);
			queryResult.setPageSize(pageSize);
			PageBean pageBean=pageList.getPageBean();
			int totalCount = pageBean.getTotalCount();
			int totalPage = pageBean.getTotalPage();
			queryResult.setTotalCount(totalCount);
			queryResult.setTotalPage(totalPage);
		} else {
			String sql = ServiceUtil.getSql(map, params);

			List<Map<String, Object>> list=JdbcTemplateUtil.getNamedParameterJdbcTemplate(bpmFormQuery.getDsalias()).queryForList(sql, params);
			list = handList(list);
			queryResult.setList(list);
			queryResult.setTotalCount(list.size());
		}
		
		return queryResult;
	}

	/**
	 * 处理list
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> handList(List<Map<String, Object>> list) {
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = (Map<String, Object>) list.get(i);
			Map<String, Object> rtnMap = handMap(map);
			rtnList.add(rtnMap);
		}
		return rtnList;
	}

	/**
	 * 处理Map
	 * 
	 * @param map
	 * @return
	 */
	private Map<String, Object> handMap(Map<String, Object> map) {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			// 把数据转换成小写
			String key = entry.getKey().toLowerCase();
			Object obj = entry.getValue();
			if (obj == null) {
				rtnMap.put(key, "");
				continue;
			}
			// 对时间字段单独处理。
			if (obj instanceof Date) {
				String format = "yyyy-MM-dd HH:mm:ss";
				String str = TimeUtil.getDateTimeString((Date) obj, format);
				rtnMap.put(key, str);
			} else {
				rtnMap.put(key, obj);
			}
		}
		return rtnMap;
	}
	/**
	 * 导出全部
	 * @param bpmFormQueries
	 * @return
	 * @throws Exception
	 */
	public String exportXml(List<BpmFormQuery> bpmFormQueries)throws Exception
	{
		BpmFormQueryXmlList bpmFormQueryXmlList = new BpmFormQueryXmlList();
		List<BpmFormQueryXml> list = new ArrayList<BpmFormQueryXml>();
		for(BpmFormQuery bpmFormQuery :bpmFormQueries)
		{
			BpmFormQueryXml bpmFormQueryXml =this.exportBpmFormQueryXml(bpmFormQuery);
			list.add(bpmFormQueryXml);
		}
		bpmFormQueryXmlList.setBpmFormQueryXmlList(list);
		return XmlBeanUtil.marshall(bpmFormQueryXmlList, BpmFormQueryXmlList.class);
	}

	/**
	 * 导出自定义查询XML
	 * @param tableIds
	 * 
	 * @return
	 * @throws Exception
	 */
	public String exportXml(Long[] tableIds) throws Exception {
		BpmFormQueryXmlList bpmFormQueryXmlList = new BpmFormQueryXmlList();
		List<BpmFormQueryXml> list = new ArrayList<BpmFormQueryXml>();
		for (int i = 0; i < tableIds.length; i++) {
			BpmFormQuery bpmFormQuery = dao.getById(tableIds[i]);
			BpmFormQueryXml bpmFormQueryXml =this.exportBpmFormQueryXml(bpmFormQuery);
			list.add(bpmFormQueryXml);
		}
		bpmFormQueryXmlList.setBpmFormQueryXmlList(list);
		return XmlBeanUtil.marshall(bpmFormQueryXmlList, BpmFormQueryXmlList.class);
	}

	/**
	 * 导出表的信息
	 * @param bpmFormQuery
	 * @param map
	 * @return
	 */
	private BpmFormQueryXml exportBpmFormQueryXml(BpmFormQuery bpmFormQuery
			)throws Exception
	{
		BpmFormQueryXml bpmFormQueryXml=new BpmFormQueryXml();
		Long id=bpmFormQuery.getId();
		if(BeanUtils.isNotIncZeroEmpty(id) )
		{
			//导出自定义查询
			
			bpmFormQueryXml.setBpmFormQuery(bpmFormQuery);
			
		}
		return bpmFormQueryXml;
	}
	
	/**
	 * 导入自定义查询XML
	 * @param inputStream
	 * @throws Exception
	 */
	public void importXml(InputStream inputStream) throws Exception {
		Document doc = Dom4jUtil.loadXml(inputStream);
		Element root = doc.getRootElement();
		// 验证格式是否正确
		XmlUtil.checkXmlFormat(root, "bpm", "formQuerys");

		String xmlStr = root.asXML();
		BpmFormQueryXmlList bpmFormQueryXmlList = (BpmFormQueryXmlList) XmlBeanUtil.unmarshall(xmlStr, BpmFormQueryXmlList.class);

		List<BpmFormQueryXml> list = bpmFormQueryXmlList.getBpmFormQueryXmlList();
		
		for (BpmFormQueryXml bpmFormQueryXml : list) {
			// 导入表，并解析相关信息
			this.importBpmFormQueryXml(bpmFormQueryXml);
			
		}
		

	}
	/**
	 * 导入时生成自定义查询
	 * @param bpmFormQueryXml
	 * @return 
	 */
	private void importBpmFormQueryXml(BpmFormQueryXml bpmFormQueryXml)throws Exception
	{
		Long queryId = UniqueIdUtil.genId();
		BpmFormQuery bpmFormQuery = bpmFormQueryXml.getBpmFormQuery();
		if (BeanUtils.isEmpty(bpmFormQuery)) {
			// MsgUtil.addMsg(MsgUtil.WARN, "什么内容也没有，请检查你的Xml文件！");
			// return;
			throw new Exception();
		}
		String alias = bpmFormQuery.getAlias();
		BpmFormQuery query = dao.getByAlias(alias);
		if (BeanUtils.isNotEmpty(query)) {
			MsgUtil.addMsg(MsgUtil.WARN, "别名为‘" + alias
					+ "’的自定义查询已经存在，请检查你的xml文件！");
			return;
		}
		bpmFormQuery.setId(queryId);
		dao.add(bpmFormQuery);
		MsgUtil.addMsg(MsgUtil.SUCCESS, "别名为" + alias + "的自定义查询导入成功！");
	}
	
	

	
}
