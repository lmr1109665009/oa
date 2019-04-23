package com.suneee.platform.model.form;

import com.suneee.core.util.StringUtil;

import java.util.*;

/**
 * 子表数据
 * 
 * @author ray
 * 
 */
public class SubTable {

	/**
	 * 表名
	 */
	private String tableName = "";

	/**
	 * 主键名称。
	 */
	private String pkName = "";

	/**
	 * 外键名称。
	 */
	private String fkName = "";

	/**
	 * 子表数据。
	 */
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

	private Map<String, Map<String, Object>> dataMap = new LinkedHashMap<String, Map<String, Object>>();

	/**
	 * 子表数据初始化数据。
	 */
	private Map<String, Object> row = new HashMap<String, Object>();

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<Map<String, Object>> getDataList() {
		return dataList;
	}

	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
		String idName = this.getPkName();
		if(StringUtil.isEmpty(idName)){
			return;
		}
		//倒序是为了保持原来的顺序
		for (int i = dataList.size() - 1; i >= 0; i--) {
			Map<String, Object> m =dataList.get(i);
			String pk = m.get(idName).toString();
			dataMap.put(pk, m);
		}
	}

	public void addRow(Map<String, Object> row) {
		this.dataList.add(row);
	}

	public String getPkName() {
		return pkName;
	}

	public void setPkName(String pkName) {
		this.pkName = pkName;
	}

	public String getFkName() {
		return fkName;
	}

	public void setFkName(String fkName) {
		this.fkName = fkName;
	}

	public Map<String, Object> getRow() {
		return this.row;
	}

	public void setInitData(Map<String, Object> row) {
		this.row = row;
	}

	/**
	 * dataMap
	 * 
	 * @return the dataMap
	 * @since 1.0.0
	 */

	public Map<String, Map<String, Object>> getDataMap() {
		return dataMap;
	}

}