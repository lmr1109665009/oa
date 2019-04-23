/**
 * 
 */
package com.suneee.ucp.base.util;

import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.ucp.base.vo.PageVo;

import java.util.List;

/**
 * 分页工具类
 * @author xiongxianyun
 *
 */
public class PageUtil {
	/**
	 * 获取分页信息对象
	 * @param pageList
	 * @return
	 */
	public static <E> PageVo<E> getPageVo(PageList<E> pageList){
		PageVo<E> pageVo = new PageVo<E>();
		// 列表信息
		pageVo.setRowList(pageList);
		
		// 分页信息
		PageBean pageBean = pageList.getPageBean();
		if(null != pageBean){
			pageVo.setCurrentPage(pageBean.getCurrentPage());
			pageVo.setPageSize(pageBean.getPageSize());
			pageVo.setTotalCount(pageBean.getTotalCount());
			pageVo.setTotalPage(pageBean.getTotalPage());
		}
		return pageVo;
	}

	public static <T> PageVo<T> getPageVo(List<T> data, PageBean pageBean){
		PageVo<T> pageVo = new PageVo<>();
		pageVo.setRowList(data);
		// 分页信息
		if(null != pageBean){
			pageVo.setCurrentPage(pageBean.getCurrentPage());
			pageVo.setPageSize(pageBean.getPageSize());
			pageVo.setTotalCount(pageBean.getTotalCount());
			pageVo.setTotalPage(pageBean.getTotalPage());
		}
		return pageVo;
	}
}
