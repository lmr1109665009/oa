package com.suneee.core.mybatis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;


/**
 * mybatis 操作基类，实现mybatis 基本操作。
 * @author hotent
 *
 */
public abstract class BaseMyBatisDao extends SqlSessionDaoSupport
{
	protected final Log log = LogFactory.getLog(getClass());
	/**
	 *  注入sqlSessionFactory。
	 */
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	/**
	 * 注入sqlSessionTemplate。
	 */
	 @Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	protected void checkDaoConfig() throws IllegalArgumentException
	{
		
		Assert.notNull(sqlSessionFactory, "sqlSessionFactory must be not null");
	}

	/**
	 * 返回SqlSessionFactory
	 * @return
	 */
	public SqlSessionFactory getSqlSessionFactory()
	{
		return sqlSessionFactory;
	}

	/**
	 * 注入sqlSessionFactory
	 * @param sqlSessionFactory
	 */
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory)
	{
		this.sqlSessionFactory = sqlSessionFactory;
		this.sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
	}

	/**
	 * 返回sqlSessionTemplate
	 * @return
	 */
	public SqlSessionTemplate getSqlSessionTemplate()
	{
		return sqlSessionTemplate;
	}
	
	/**
	 * 根据mapper文件的Id 和参数对象获取IbatisSql对象。<br>
	 * 用于获取SQL对象。
	 * @param id
	 * @param parameterObject
	 * @return
	 */
	@SuppressWarnings("unused")
	public IbatisSql getIbatisSql(String id, Object parameterObject) {    
	    IbatisSql ibatisSql = new IbatisSql();    
	    Configuration configuration = sqlSessionFactory.getConfiguration();
	    Collection<String> coll= configuration.getMappedStatementNames();
	    
	    MappedStatement ms = configuration.getMappedStatement(id);    
	    BoundSql boundSql = ms.getBoundSql(parameterObject);    
	    
	    TypeHandlerRegistry typeHandlerRegistry= configuration.getTypeHandlerRegistry();
	        
	    List<ResultMap> ResultMaps=ms.getResultMaps();    
	    if(ResultMaps!=null&&ResultMaps.size()>0){    
	        ResultMap ResultMap = ms.getResultMaps().get(0);    
	        ibatisSql.setResultClass(ResultMap.getType());    
	    }    
	    ibatisSql.setSql(boundSql.getSql());    
	    
	    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();    
	    if (parameterMappings != null) {    
	        Object[] parameterArray = new Object[parameterMappings.size()];    
	        MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
	          
	        for (int i = 0; i < parameterMappings.size(); i++) {    
	          ParameterMapping parameterMapping = parameterMappings.get(i);    
	          if (parameterMapping.getMode() != ParameterMode.OUT) {    
	            Object value;    
	            
	            String propertyName = parameterMapping.getProperty();
                if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (parameterObject == null) {
                    value = null;
                } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    value = parameterObject;
                } else {
                    value = metaObject == null ? null : metaObject.getValue(propertyName);
                }
	            parameterArray[i] = value;    
	          }    
	        }    
	        ibatisSql.setParameters(parameterArray);    
	    }    
	   
	    return ibatisSql;    
	}   
	
	

	
}
