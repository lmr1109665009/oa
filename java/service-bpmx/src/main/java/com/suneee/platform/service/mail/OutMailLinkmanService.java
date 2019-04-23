package com.suneee.platform.service.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.mail.OutMailLinkmanDao;
import com.suneee.platform.model.mail.OutMailLinkman;
import org.springframework.stereotype.Service;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.platform.dao.mail.OutMailLinkmanDao;
import com.suneee.platform.model.mail.OutMailLinkman;

/**
 * 对象功能:最近联系人 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zyp
 * 创建时间:2012-04-13 11:11:56
 */
@Service
public class OutMailLinkmanService extends BaseService<OutMailLinkman>
{
	@Resource
	private OutMailLinkmanDao dao;
	

	
	public OutMailLinkmanService()
	{
	}
	
	@Override
	protected IEntityDao<OutMailLinkman, Long> getEntityDao()
	{
		return dao;
	}
	
	
	
	/**
	 * 根据邮箱地址找到联系人
	 * @param address
	 * @return
	 * @throws Exception
	 */
	public OutMailLinkman findLinkMan(String address, long userId)throws Exception
	{
		return dao.findLinkMan(address,userId);
	}
	
	/**
	 * 找到当前用户下的最近联系人
	 * @param userId
	 * @return
	 */
	public List<OutMailLinkman> getAllByUserId(Long userId,String condition) {
		Map params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("condition", condition);
		return dao.getAllByUserId(params);
	}
}
