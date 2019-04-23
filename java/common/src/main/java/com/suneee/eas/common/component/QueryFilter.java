package com.suneee.eas.common.component;

import com.suneee.eas.common.utils.RequestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能：对http请求进行封装，用于查询。<br>
 * 
 * 具体功能如下：<br>
 * 1.将查询条件封装成Map对象。<br>
 * 2.封装分页参数构建PageBean 。<br>
 * 3.封装排序条件。<br>
 * 
 * @author csx
 * 
 */
public class QueryFilter {
	private Logger log = LogManager.getLogger(QueryFilter.class);
	private String sqlKey;

	public String getSqlKey() {
		return sqlKey;
	}

	public void setSqlKey(String sqlKey) {
		this.sqlKey = sqlKey;
	}

	/**
	 * 过滤参数
	 */
	private Map<String, Object> filters = new HashMap<String, Object>();

	public final static String ORDER_ASC = "1";
	public final static String ORDER_DESC = "2";

	public QueryFilter() {
	}

	public QueryFilter(String sqlKey) {
		this.sqlKey=sqlKey;
	}


	/**
	 * 解析当前请求所带参数
	 * @param sqlKey
	 * @param request
	 */
	public QueryFilter(String sqlKey,HttpServletRequest request) {
		this.sqlKey=sqlKey;
		try {
			Map<String, Object> map = RequestUtil.getQueryMap(request);
			filters = map;
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

	/**
	 * 取得查询条件。
	 * 
	 * @return
	 */
	public Map<String, Object> getFilters() {
		return filters;
	}

	/**
	 * 添加过滤字段条件
	 * 
	 * @param filterName
	 *            参数名
	 * @param params
	 *            参数值
	 */
	public void addFilter(String filterName, Object params) {
		this.filters.put(filterName, params);
	}

	/**
	 * 设置查询条件。
	 * 
	 * @param filters
	 */
	public void setFilters(Map<String, Object> filters) {
		this.filters = filters;
	}

}
