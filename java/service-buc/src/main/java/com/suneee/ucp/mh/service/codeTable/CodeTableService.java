package com.suneee.ucp.mh.service.codeTable;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.PageVo;
import com.suneee.ucp.mh.dao.codeTable.CodeTableDao;
import com.suneee.ucp.mh.model.codeTable.CodeTable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
@Service
public class CodeTableService extends UcpBaseService<CodeTable> {

    @Resource
	private CodeTableDao dao;
	@Override
	protected IEntityDao<CodeTable, Long> getEntityDao() {
		// TODO Auto-generated method stub
		return dao;
	}
	public void save(CodeTable codeTable) {
	  Long settingId =codeTable.getSettingId();
	  if(null == settingId){
		  settingId = UniqueIdUtil.genId();
		  codeTable.setSettingId(settingId);
		  this.add(codeTable);
	  }else{		
		  this.update(codeTable);
	  }		
	}	
	public boolean isExist(String itemId, String itemValue) {
		// TODO Auto-generated method stub
		return dao.isExist(itemId,itemValue);
	}	
	
	/**
	 * 根据条件获取码表信息列表
	 * @param params
	 * @return
	 */
	public List<CodeTable> getByCondition(Map<String, Object> params){
		return dao.getByCondition(params);
	}
	/**
	 * 根据itemId和settingType获取信息列表
	 * @param itemId
	 * @param settingType
	 * @return
	 */
	public List<CodeTable> getByItemId(String itemId, String settingType) {
		// TODO Auto-generated method stub
		return dao.getByItemId(itemId,settingType);
	}
	public List<CodeTable> getTypeList(QueryFilter filter) {
		// TODO Auto-generated method stub
		return dao.getTypeList(filter);
	}

	public List<CodeTable> getOtherList(QueryFilter filter) {
		// TODO Auto-generated method stub
		return dao.getOtherList(filter);
	}
	public List<CodeTable> getByType(String type) {
		// TODO Auto-generated method stub
		return dao.getBySqlKey("getByType", type);
	}

	public int getTypeListCount(QueryFilter filter){
	    return dao.getTypeListCount(filter);
    };

	/**
	 * TypeManageApiController 使用
	 * */
	public PageVo<CodeTable> getTypeListApi(QueryFilter filter) {
      /*  //如果mistiness字段不为空则代表不指定具体过滤字段
        String mistiness = (String) filter.getFilters().get("mistiness");
        int count;
        List<CodeTable> list;
        if(StringUtils.isNotEmpty(mistiness)){
            count = dao.getTypeListCountMistiness(filter);
        }else{
            count = dao.getTypeListCount(filter);
        }

		if(count == 0){
			return new PageVo<>();
		}
		if(StringUtils.isNotEmpty(mistiness)){
            list = dao.getTypeListMistiness(filter);
        }else{
            list = dao.getTypeList(filter);
        }*/

		int count = dao.getTypeListCount(filter);
		if(count == 0){
			return new PageVo<>();
		}
		List<CodeTable> list = dao.getTypeList(filter);
		PageList pageList = new PageList(list);

		PageBean pageBean = new PageBean();
		pageBean.setTotalCount(count);
		pageBean.setPagesize(filter.getPageBean().getPageSize());
		pageBean.setCurrentPage(filter.getPageBean().getCurrentPage());

		pageList.setPageBean(pageBean);

		return PageUtil.getPageVo(pageList);
	}


	/**
	 * AboutDepartApiController 使用
	 * */
	public PageVo<CodeTable> getOtherListApi(QueryFilter filter) {
		/*//如果mistiness字段不为空则代表不指定具体过滤字段
		String mistiness = (String) filter.getFilters().get("mistiness");
		int count = 0;
		List<CodeTable> list;
		if(StringUtils.isNotEmpty(mistiness)){
			count  = this.dao.getOtherListCountOr(filter);
		}else{
			count = this.dao.getOtherListCount(filter);
		}
		if(count == 0){
			return new PageVo<>();
		}

		if(StringUtils.isNotEmpty(mistiness)){
			list = dao.getOtherListOr(filter);
		}else{
			list = dao.getOtherList(filter);
		}*/
		int count = this.dao.getOtherListCount(filter);
		if(count == 0){
			return new PageVo<>();
		}
		List<CodeTable> list = dao.getOtherList(filter);
		PageList pageList = new PageList(list);

		PageBean pageBean = new PageBean();
		pageBean.setTotalCount(count);
		pageBean.setPagesize(filter.getPageBean().getPageSize());
		pageBean.setCurrentPage(filter.getPageBean().getCurrentPage());

		pageList.setPageBean(pageBean);

		return PageUtil.getPageVo(pageList);
	}
}
