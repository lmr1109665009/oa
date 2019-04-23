package com.suneee.weixin.model;

import java.util.ArrayList;
import java.util.List;

public class ListModel {
	
	private String title="";
	
	private String key="";
	
	// 总页数
	private int total=0;
	
	// 总记录数
	private int totalCount = 0;
	
	private List rowList=new ArrayList();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List getRowList() {
		return rowList;
	}
	
	public void addRow(Object model){
		rowList.add(model);
	}

	public void setRowList(List rowList) {
		this.rowList = rowList;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
