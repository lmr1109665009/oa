/**
 * 
 */
package com.suneee.ucp.base.vo;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Administrator
 *
 */
public class PageVo<E>{
	/**
	 * 当前页
	 */
	private int currentPage;
	
	/**
	 * 每页数据条数
	 */
	private int pageSize;
	
	/**
	 * 总页数
	 */
	private int totalPage;
	
	/**
	 * 数据总条数
	 */
	private int totalCount;
	
	/**
	 * 数据列表
	 */
	private List<E> rowList;

	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the totalPage
	 */
	public int getTotalPage() {
		return totalPage;
	}

	/**
	 * @param totalPage the totalPage to set
	 */
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
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

	/**
	 * @return the rowList
	 */
	public List<E> getRowList() {
		return rowList;
	}

	/**
	 * @param rowList the rowList to set
	 */
	public void setRowList(List<E> rowList) {
		this.rowList = rowList;
	}
	
	/** (non-Javadoc)
	 * @see Object#toString()
	 */
	public String toString(){
		return new ToStringBuilder(this)
			.append("currentPage", currentPage)
			.append("pageSize", pageSize)
			.append("totalPage", totalPage)
			.append("totalCount", totalCount)
			.append("rowList", rowList)
			.toString();
	}
}
