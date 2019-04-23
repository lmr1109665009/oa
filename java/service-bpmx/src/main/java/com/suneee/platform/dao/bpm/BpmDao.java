package com.suneee.platform.dao.bpm;

import com.suneee.core.api.bpm.IBpmDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.core.support.AbstractLobStreamingResultSetExtractor;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * BPMN的数据层直接操作类，所有需要绕开Activiti API访问数据层的处理均需要写在该类下。
 * @author csx
 *
 */
@Repository
public class BpmDao implements IBpmDao
{
	private Logger logger=LoggerFactory.getLogger(BpmDao.class);
	
	@Resource
	protected JdbcTemplate jdbcTemplate;
	
	public BpmDao()
	{
		
	}
	/**
	 * 取得流程定义的XML
	 * 
	 * @param deployId
	 * @return
	 */
	public String getActDefIdByDeployId(String deployId){
		String sql = "select id_ from ACT_RE_PROCDEF where deployment_id_=? ";
		String actDefId = jdbcTemplate.queryForObject(sql, new Object[]{deployId },String.class); 
		return actDefId;
	}
	
	/**
	 * 取得流程定义的XML
	 * 
	 * @param deployId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getDefXmlByDeployId(String deployId)
	{
		String sql = "select a.* from ACT_GE_BYTEARRAY a where NAME_ LIKE '%bpmn20.xml' and DEPLOYMENT_ID_= ? ";

		final LobHandler lobHandler = new DefaultLobHandler(); // reusable
																// object new
																// OracleLobHandler();
																// //
		final ByteArrayOutputStream contentOs = new ByteArrayOutputStream();
		String defXml = null;
		try
		{
			jdbcTemplate.query(sql, new Object[]{deployId },new AbstractLobStreamingResultSetExtractor()
				{
							public void streamData(ResultSet rs) throws SQLException, IOException
							{
								FileCopyUtils.copy(lobHandler.getBlobAsBinaryStream(rs, "BYTES_"), contentOs);
							}
				}
			);
			defXml = new String(contentOs.toByteArray(), "UTF-8");
		} catch (Exception ex)
		{
			logger.debug(ex.getMessage());
		}
		return defXml;
	}

	/**
	 * 把修改过的xml更新至回流程定义中
	 * 
	 * @param deployId
	 * @param defXml
	 */
	public void wirteDefXml(final String deployId, String defXml)
	{
		try
		{
			LobHandler lobHandler = new DefaultLobHandler();
			final byte[] btyesXml = defXml.getBytes("UTF-8");
			String sql = "update ACT_GE_BYTEARRAY set BYTES_=? where NAME_ LIKE '%bpmn20.xml' and DEPLOYMENT_ID_= ? ";
			jdbcTemplate.execute(sql, new AbstractLobCreatingPreparedStatementCallback
					(lobHandler)
					{
						@Override
						protected void setValues(PreparedStatement ps, LobCreator lobCreator)
								throws SQLException, DataAccessException
						{
							lobCreator.setBlobAsBytes(ps, 1, btyesXml);
							ps.setString(2, deployId);
						}
					});
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	@Override
	public String getDeployIdByActdefId(String actDefId) {
		String sql = "select DEPLOYMENT_ID_ from ACT_RE_PROCDEF where ID_=? ";
		String deployId = jdbcTemplate.queryForObject(sql, new Object[]{actDefId },String.class); 
		return deployId;
	}
	@Override
	public String getXmlByDefKey(String actDefkey) {
		String sql = "select deployment_id_ from ACT_RE_PROCDEF where key_=? and version_ =(select max(VERSION_) from ACT_RE_PROCDEF where key_=? ) ";
		String deployId = jdbcTemplate.queryForObject(sql, new Object[]{actDefkey,actDefkey },String.class); 
		
		String xml=getDefXmlByDeployId(deployId);
		return xml;
	}
}
