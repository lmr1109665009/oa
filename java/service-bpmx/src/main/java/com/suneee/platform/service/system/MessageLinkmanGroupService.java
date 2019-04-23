package com.suneee.platform.service.system;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.MessageLinkmanGroupDao;
import com.suneee.platform.model.system.MessageLinkmanGroup;
import net.sf.json.util.JSONUtils;
import net.sf.ezmorph.object.DateMorpher;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.util.StringUtil;
import net.sf.json.JSONObject;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.system.MessageLinkmanGroup;
import com.suneee.platform.dao.system.MessageLinkmanGroupDao;
import com.suneee.core.service.BaseService;

/**
 *<pre>
 * 对象功能:常用联系人组 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ouxb
 * 创建时间:2015-07-29 10:29:57
 *</pre>
 */
@Service
public class MessageLinkmanGroupService extends BaseService<MessageLinkmanGroup>
{
	@Resource
	private MessageLinkmanGroupDao dao;
	
	
	
	public MessageLinkmanGroupService()
	{
	}
	
	@Override
	protected IEntityDao<MessageLinkmanGroup, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 保存 常用联系人组 信息
	 * @param messageLinkmanGroup
	 */
	public void save(MessageLinkmanGroup messageLinkmanGroup){
		Long id=messageLinkmanGroup.getId();
		if(id==null || id==0){
			id= UniqueIdUtil.genId();
			messageLinkmanGroup.setId(id);
			messageLinkmanGroup.setCreatorId(ContextUtil.getCurrentUserId());
			messageLinkmanGroup.setCreateTime(new Date());
			this.add(messageLinkmanGroup);
		}
		else{
			this.update(messageLinkmanGroup);
		}
	}
	
}
