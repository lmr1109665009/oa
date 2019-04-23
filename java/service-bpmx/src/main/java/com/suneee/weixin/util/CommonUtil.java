package com.suneee.weixin.util;

import com.suneee.core.page.PageList;
import com.suneee.weixin.model.ListModel;
import com.suneee.core.page.PageList;
import com.suneee.weixin.model.ListModel;

public class CommonUtil {
	
	public static ListModel getListModel(PageList list){
		ListModel model=new ListModel();
		
		model.setRowList(list);
		
		PageList pageList=(PageList)list;
		
		model.setTotal(pageList.getTotalPage());
		model.setTotalCount(pageList.getTotalCount());
		return model;
	}

}
