package com.suneee.platform.service.bpm;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.platform.dao.bpm.BpmCommonWsParamsDao;
import com.suneee.platform.dao.bpm.BpmCommonWsSetDao;
import com.suneee.platform.dao.system.WsDataTemplateDao;
import com.suneee.platform.model.bpm.BpmCommonWsParams;
import com.suneee.platform.model.bpm.BpmCommonWsSet;
import com.suneee.platform.model.system.WsDataTemplate;
import com.suneee.platform.xml.bpm.BpmCommonWsSetXml;
import com.suneee.platform.xml.bpm.BpmCommonWsSetXmlList;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.platform.xml.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *<pre>
 * 对象功能:通用webservice调用设置 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-10-17 10:09:20
 *</pre>
 */
@Service
public class BpmCommonWsSetService extends BaseService<BpmCommonWsSet>
{
	@Resource
	private BpmCommonWsSetDao dao;
	
	@Resource
	private BpmCommonWsParamsDao bpmCommonWsParamsDao;
	@Resource
	private WsDataTemplateDao wsDataTemplateDao;
	
	public BpmCommonWsSetService()
	{
	}
	
	@Override
	protected IEntityDao<BpmCommonWsSet, Long> getEntityDao()
	{
		return dao;
	}
	
	private void delByPk(Long id){
		bpmCommonWsParamsDao.delByMainId(id);
	}
	
	public void delAll(Long[] lAryId) {
		for(Long id:lAryId){	
			List<WsDataTemplate> list = wsDataTemplateDao.getByWsSetId(id);
			if(BeanUtils.isNotEmpty(list)){
				throw new RuntimeException("webservice调用设置已被引用，无法删除！");
			}
			delByPk(id);
			dao.delById(id);	
		}	
	}
	
	public void addAll(BpmCommonWsSet bpmCommonWsSet) throws Exception{
		add(bpmCommonWsSet);
		addSubList(bpmCommonWsSet);
	}
	
	public void updateAll(BpmCommonWsSet bpmCommonWsSet) throws Exception{
		update(bpmCommonWsSet);
		delByPk(bpmCommonWsSet.getId());
		addSubList(bpmCommonWsSet);
	}
	
	public void addSubList(BpmCommonWsSet bpmCommonWsSet) throws Exception{
		List<BpmCommonWsParams> bpmCommonWsParamsList=bpmCommonWsSet.getBpmCommonWsParamsList();
		if(BeanUtils.isNotEmpty(bpmCommonWsParamsList)){
			for(BpmCommonWsParams bpmCommonWsParams:bpmCommonWsParamsList){
				bpmCommonWsParams.setSetid(bpmCommonWsSet.getId());
				bpmCommonWsParams.setId(UniqueIdUtil.genId());
				bpmCommonWsParamsDao.add(bpmCommonWsParams);
			}
		}
	}
	
	public BpmCommonWsSet getByAlias(String alias){
		return dao.getByAlias(alias);
	}
	
	public List<BpmCommonWsParams> getBpmCommonWsParamsList(Long id) {
		return bpmCommonWsParamsDao.getByMainId(id);
	}
	
	/**
	 * 导出全部Web服务调用配置
	 * @param bpmCommonWsSets
	 * @return
	 * @throws Exception
	 */
	public String exportXml(List<BpmCommonWsSet> bpmCommonWsSets)
			throws Exception {
		BpmCommonWsSetXmlList bpmCommonWsSetXmlList = new BpmCommonWsSetXmlList();
		List<BpmCommonWsSetXml> list = new ArrayList<BpmCommonWsSetXml>();
		for (BpmCommonWsSet bpmCommonWsSet : bpmCommonWsSets) {
			BpmCommonWsSetXml bpmCommonWsSetXml = this
					.exportBpmCommonWsSetXml(bpmCommonWsSet);
			list.add(bpmCommonWsSetXml);
		}
		bpmCommonWsSetXmlList.setBpmCommonWsSetXmlList(list);
		return XmlBeanUtil.marshall(bpmCommonWsSetXmlList,
				BpmCommonWsSetXmlList.class);
	}
	
	/**
	 * 
	 * 导出Web服务调用配置XML
	 * 
	 * @return
	 * @throws Exception
	 */
	public String exportXml(Long[] tableIds) throws Exception {
		BpmCommonWsSetXmlList bpmCommonWsSetXmlList = new BpmCommonWsSetXmlList();
		List<BpmCommonWsSetXml> list = new ArrayList<BpmCommonWsSetXml>();
		for (int i = 0; i < tableIds.length; i++) {
			BpmCommonWsSet bpmCommonWsSet = dao.getById(tableIds[i]);
			BpmCommonWsSetXml bpmCommonWsSetXml = this
					.exportBpmCommonWsSetXml(bpmCommonWsSet);
			list.add(bpmCommonWsSetXml);
		}
		bpmCommonWsSetXmlList.setBpmCommonWsSetXmlList(list);
		return XmlBeanUtil.marshall(bpmCommonWsSetXmlList,
				BpmCommonWsSetXmlList.class);
	}

	/**
	 * 导出表的信息
	 * @param bpmCommonWsSet
	 * 
	 * @return
	 * @throws Exception
	 */
	private BpmCommonWsSetXml exportBpmCommonWsSetXml(
			BpmCommonWsSet bpmCommonWsSet)throws Exception 
	{
		BpmCommonWsSetXml bpmCommonWsSetXml=new BpmCommonWsSetXml();
		//参数列表
		List<BpmCommonWsParams> list=new ArrayList<BpmCommonWsParams>();
		Long id=bpmCommonWsSet.getId();
		if(BeanUtils.isNotIncZeroEmpty(id))
		{
			//参数
			List<BpmCommonWsParams> paramslist=new ArrayList<BpmCommonWsParams>();
			
				paramslist=bpmCommonWsParamsDao.getByMainId(id);
				
				this.exportBpmCommonWsParams(paramslist,list);
			
		}
		bpmCommonWsSetXml.setBpmCommonWsSet(bpmCommonWsSet);
		if (BeanUtils.isNotEmpty(list)) {
			bpmCommonWsSetXml.setBpmCommonWsParamsList(list);
		}
		return bpmCommonWsSetXml;
	}

	/**
	 * 导出参数
	 * @param paramslist
	 * @param list
	 * @throws Exception
	 */
	private void exportBpmCommonWsParams(List<BpmCommonWsParams> paramslist,
			List<BpmCommonWsParams> list) throws Exception {
		for (BpmCommonWsParams bpmCommonWsParams : paramslist) {
			list.add(bpmCommonWsParams);
		}

	}
	
	
	/**
	 * 导入Web服务调用配置XML
	 * @param inputStream
	 * @throws Exception
	 */
	public void importXml(InputStream inputStream) throws Exception {
		Document doc = Dom4jUtil.loadXml(inputStream);
		Element root = doc.getRootElement();
		// 验证格式是否正确
		XmlUtil.checkXmlFormat(root, "bpm", "commonWsSets");

		String xmlStr = root.asXML();
		BpmCommonWsSetXmlList bpmCommonWsSetXmlList = (BpmCommonWsSetXmlList) XmlBeanUtil
				.unmarshall(xmlStr, BpmCommonWsSetXmlList.class);

		List<BpmCommonWsSetXml> list = bpmCommonWsSetXmlList
				.getBpmCommonWsSetXmlList();

		for (BpmCommonWsSetXml bpmCommonWsSetXml : list) {
			// 导入表，并解析相关信息
			this.importBpmCommonWsSetXml(bpmCommonWsSetXml);

		}

	}

	/**
	 * 导入时生成Web服务调用配置
	 * @param bpmCommonWsSetXml
	 * @throws Exception
	 */
	private void importBpmCommonWsSetXml(BpmCommonWsSetXml bpmCommonWsSetXml)
			throws Exception {
		Long id = UniqueIdUtil.genId();
		BpmCommonWsSet bpmCommonWsSet = bpmCommonWsSetXml.getBpmCommonWsSet();
		if (BeanUtils.isEmpty(bpmCommonWsSet)) {

			throw new Exception();
		}
		String alias = bpmCommonWsSet.getAlias();

		bpmCommonWsSet.setId(id);

		// 参数列表
		List<BpmCommonWsParams> params = bpmCommonWsSetXml
				.getBpmCommonWsParamsList();
		if (BeanUtils.isNotEmpty(params)) {
			for (BpmCommonWsParams param : params) {
				Long paramsId = UniqueIdUtil.genId();
				param.setId(paramsId);
				param.setSetid(id);
				bpmCommonWsParamsDao.add(param);
			}
			bpmCommonWsSet.setBpmCommonWsParamsList(params);
		}

		dao.add(bpmCommonWsSet);
		MsgUtil.addMsg(MsgUtil.SUCCESS, "别名为" + alias + "的Web服务导入成功！");

	}

	
	
}
