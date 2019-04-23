package com.suneee.platform.model.index;

import java.util.List;

import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageBean;

public class IndexTabList {

	protected String curTab;

	private PageBean pageBean = new PageBean();

	protected List<IndexTab> indexTabList;

	public List<IndexTab> getIndexTabList() {
		return indexTabList;
	}

	public void setIndexTabList(List<IndexTab> indexTabList) {
		this.indexTabList = indexTabList;
	}

	public String getCurTab() {
		return curTab;
	}

	public void setCurTab(String curTab) {
		this.curTab = curTab;
	}

	public PageBean getPageBean() {
		return pageBean;
	}

	public void setPageBean(PageBean pageBean) {
		this.pageBean = pageBean;
	}

}
