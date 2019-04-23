package com.suneee.platform.service.system;

import com.suneee.core.api.system.ISysDataSourceService;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.*;
import com.suneee.platform.dao.system.SysDataSourceDao;
import com.suneee.platform.model.system.SysDataSource;
import com.suneee.platform.xml.system.SysDataSourceXml;
import com.suneee.platform.xml.system.SysDataSourceXmlList;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.platform.xml.util.XmlUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 对象功能:SYS_DATA_SOURCE Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:Aschs
 * 创建时间:2014-08-21 10:50:18
 * </pre>
 */
@Service
public class SysDataSourceService extends BaseService<SysDataSource> implements ISysDataSourceService {
	protected static final Logger LOGGER = LoggerFactory.getLogger(SysDataSourceService.class);
	@Resource
	private SysDataSourceDao dao;

	public SysDataSourceService() {

	}

	public SysDataSource getByAlias(String alias) {
		return dao.getUnique("getByAlias", alias);
	}

	@Override
	protected IEntityDao<SysDataSource, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 验证连接。
	 * 
	 * @param sysDataSource
	 * @return
	 */
	public boolean checkConnection(SysDataSource sysDataSource) {
		return checkConnection(getDsFromSysSource(sysDataSource), sysDataSource.getCloseMethod());
	}

	private boolean checkConnection(DataSource dataSource, String closeMethod) {
		boolean b = false;
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			b = true;
		} catch (Exception e) {
			// 调用关闭
			if (!StringUtil.isEmpty(closeMethod)) {
				String cp = closeMethod.split("\\|")[0];
				String mn = closeMethod.split("\\|")[1];
				try {
					Class<?> _class = Class.forName(cp);
					Method method = _class.getMethod(mn, null);
					method.invoke(null, null);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		return b;
	}

	/**
	 * 
	 * 利用Java反射机制把dataSource成javax.sql.DataSource对象
	 * 
	 * @param sysDataSource
	 * @param dataSourcePool
	 * @return javax.sql.DataSource
	 */
	public DataSource getDsFromSysSource(SysDataSource sysDataSource) {

		try {
			// 获取对象
			Class<?> _class = null;
			_class = Class.forName(sysDataSource.getClassPath());
			DataSource sqldataSource = null;
			sqldataSource = (DataSource) _class.newInstance();// 初始化对象

			// 开始set它的属性
			String settingJson = sysDataSource.getSettingJson();
			JSONArray ja = JSONArray.fromObject(settingJson);

			for (int i = 0; i < ja.size(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				Object value = BeanUtils.convertByActType(jo.getString("type"), jo.getString("value"));
				BeanUtils.setProperty(sqldataSource, jo.getString("name"), value);
			}

			// 如果有初始化方法，需要调用，必须是没参数的
			String initMethodStr = sysDataSource.getInitMethod();
			if (!StringUtil.isEmpty(initMethodStr)) {
				Method method = _class.getMethod(initMethodStr);
				method.invoke(sqldataSource);
			}

			return sqldataSource;
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}

		return null;
	}

	/**
	 * 获取全部的同事默认添加一个本地数据源
	 */
	public List<SysDataSource> getAllAndDefault() {
		List<SysDataSource> list = super.getAll();
		//添加本地默认数据源
		SysDataSource defaultSysDataSource = new SysDataSource();
		defaultSysDataSource.setAlias(BpmConst.LOCAL_DATASOURCE);
		defaultSysDataSource.setName("本地数据源");
		list.add(0,defaultSysDataSource);
		return list;
	}

	/**
	 * 导出全部系统数据源
	 * 
	 * @param sysDataSources
	 * @return
	 * @throws Exception
	 */
	public String exportXml(List<SysDataSource> sysDataSources) throws Exception {
		SysDataSourceXmlList sysDataSourceXmlList = new SysDataSourceXmlList();
		List<SysDataSourceXml> list = new ArrayList<SysDataSourceXml>();
		for (SysDataSource sysDataSource : sysDataSources) {
			SysDataSourceXml sysDataSourceXml = this.exportsysDataSource(sysDataSource);
			list.add(sysDataSourceXml);
		}
		sysDataSourceXmlList.setSysDataSourceXmlList(list);
		return XmlBeanUtil.marshall(sysDataSourceXmlList, SysDataSourceXmlList.class);
	}

	/**
	 * 导出系统数据源Xml
	 * 
	 * @param tableIds
	 * 
	 * @return
	 * @throws Exception
	 */
	public String exportXml(Long[] tableIds) throws Exception {
		SysDataSourceXmlList sysDataSourceXmlList = new SysDataSourceXmlList();
		List<SysDataSourceXml> list = new ArrayList<SysDataSourceXml>();
		for (int i = 0; i < tableIds.length; i++) {
			SysDataSource sysDataSource = dao.getById(tableIds[i]);
			SysDataSourceXml sysDataSourceXml = this.exportsysDataSource(sysDataSource);
			list.add(sysDataSourceXml);
		}
		sysDataSourceXmlList.setSysDataSourceXmlList(list);
		return XmlBeanUtil.marshall(sysDataSourceXmlList, SysDataSourceXmlList.class);
	}

	/**
	 * 导出表的信息
	 * 
	 * @param sysDataSource
	 * @param map
	 * @return
	 * @throws Exception
	 */
	private SysDataSourceXml exportsysDataSource(SysDataSource sysDataSource) throws Exception {
		SysDataSourceXml sysDataSourceXml = new SysDataSourceXml();
		Long id = sysDataSource.getId();
		if (BeanUtils.isNotIncZeroEmpty(id)) {
			//导出系统数据源
			sysDataSourceXml.setDataSource(sysDataSource);
		}
		return sysDataSourceXml;
	}

	/**
	 * 导入系统数据源Xml
	 * 
	 * @param inputStream
	 * @throws Exception
	 */
	public void importXml(InputStream inputStream) throws Exception {
		Document doc = Dom4jUtil.loadXml(inputStream);
		Element root = doc.getRootElement();
		// 验证格式是否正确
		XmlUtil.checkXmlFormat(root, "system", "dataSources");
		String xmlStr = root.asXML();
		SysDataSourceXmlList sysDataSourceXmlList = (SysDataSourceXmlList) XmlBeanUtil.unmarshall(xmlStr, SysDataSourceXmlList.class);
		List<SysDataSourceXml> list = sysDataSourceXmlList.getSysDataSourceXmlList();
		for (SysDataSourceXml sysDataSourceXml : list) {
			// 导入表 并解析相关信息
			this.importSysDataSourceXml(sysDataSourceXml);
		}
	}

	/**
	 * 导入时生成系统数据源
	 * 
	 * @param sysDataSourceXml
	 * @throws Exception
	 */
	private void importSysDataSourceXml(SysDataSourceXml sysDataSourceXml) throws Exception {
		Long id = UniqueIdUtil.genId();
		SysDataSource sysDataSource = sysDataSourceXml.getDataSource();
		if (BeanUtils.isEmpty(sysDataSource)) {
			throw new Exception();
		}

		String alias = sysDataSource.getAlias();

		sysDataSource.setId(id);
		dao.add(sysDataSource);
		MsgUtil.addMsg(MsgUtil.SUCCESS, "别名为" + alias + "的数据源导入成功！");
	}

}
