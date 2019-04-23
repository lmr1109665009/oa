package com.suneee.platform.service.system;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.SysKnowMarkRelDao;
import com.suneee.platform.dao.system.SysKnowledgeMarkDao;
import com.suneee.platform.model.system.SysKnowMarkRel;
import com.suneee.platform.model.system.SysKnowledgeMark;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.springframework.stereotype.Service;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.SysKnowMarkRelDao;
import com.suneee.platform.dao.system.SysKnowledgeMarkDao;
import com.suneee.platform.model.system.SysKnowMarkRel;
import com.suneee.platform.model.system.SysKnowledgeMark;

/**
 *<pre>
 * 对象功能:SYS_ KNOWLEDGE_MARK Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2015-07-28 10:23:42
 *</pre>
 */
@Service
public class SysKnowledgeMarkService extends BaseService<SysKnowledgeMark>
{
	@Resource
	private SysKnowledgeMarkDao dao;
	@Resource
	private SysKnowMarkRelDao sysKnowMarkReldao;
	
	
	public SysKnowledgeMarkService()
	{
	}
	
	@Override
	protected IEntityDao<SysKnowledgeMark, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 流程处理器方法 用于处理业务数据
	 * @param cmd
	 * @throws Exception
	 */
	public void processHandler(ProcessCmd cmd)throws Exception{
		Map data=cmd.getFormDataMap();
		if(BeanUtils.isNotEmpty(data)){
			String json=data.get("json").toString();
			SysKnowledgeMark sysKnowledgeMark=getSysKnowledgeMark(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				sysKnowledgeMark.setId(genId);
				this.add(sysKnowledgeMark);
			}else{
				sysKnowledgeMark.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(sysKnowledgeMark);
			}
			cmd.setBusinessKey(sysKnowledgeMark.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取SysKnowledgeMark对象
	 * @param json
	 * @return
	 */
	public SysKnowledgeMark getSysKnowledgeMark(String json){
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher((new String[] { "yyyy-MM-dd" })));
		if(StringUtil.isEmpty(json))return null;
		JSONObject obj = JSONObject.fromObject(json);
		SysKnowledgeMark sysKnowledgeMark = (SysKnowledgeMark)JSONObject.toBean(obj, SysKnowledgeMark.class);
		return sysKnowledgeMark;
	}
	
	/**
	 * 保存 SYS_ KNOWLEDGE_MARK 信息
	 * @param sysKnowledgeMark
	 */
	public void save(SysKnowledgeMark sysKnowledgeMark){
		Long id=sysKnowledgeMark.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			sysKnowledgeMark.setId(id);
			this.add(sysKnowledgeMark);
		}
		else{
			this.update(sysKnowledgeMark);
		}
	}

	public List<SysKnowledgeMark> getByKnowId(Long knowledgeId) {
		Map<String,Long> map = new HashMap<String, Long>();
		map.put("knowledgeId", knowledgeId);
		List<SysKnowledgeMark> markList = dao.getBySqlKey("getByKnowId", map);
		return markList;
	}

	public List<SysKnowledgeMark> getListByUser(QueryFilter queryFilter) {
		Long userId = ContextUtil.getCurrentUserId();
		queryFilter.addFilter("userId", userId);
		List<SysKnowledgeMark> markList = dao.getBySqlKey("getListByUser", queryFilter);
		return markList;
	}

	/**
	 * 保存文章书签，使用LinkedList去掉重复的书签
	 * @param markStr
	 * @param knowId
	 */
	@SuppressWarnings("unchecked")
	public void saveMarkStr(String markStr, Long knowId) {
		sysKnowMarkReldao.deleteByKnow(knowId);//删除旧的书签
		markStr = markStr.replaceAll("，", ",");//去掉中文逗号
		List<String> list = new ArrayList<String>(Arrays.asList(markStr.split(",")));
		Set<String> newSet = new HashSet<String>();
		newSet.addAll(list);
		for(String str : newSet){
			Long markId = 0L;
			List<SysKnowledgeMark> existMarkList = dao.isExist(str);
			if(existMarkList.size() == 0){
				SysKnowledgeMark sysKnowMark = new SysKnowledgeMark(); //加入书签
				markId = UniqueIdUtil.genId();
				sysKnowMark.setId(markId);
				sysKnowMark.setBookmark(str);
				sysKnowMark.setCreatetime(new Date());
				sysKnowMark.setUserId(ContextUtil.getCurrentUserId());
				dao.add(sysKnowMark);
			}else{
				SysKnowledgeMark existMar =  existMarkList.get(0);
				markId = existMar.getId();
			}
			SysKnowMarkRel sysKnowMarkRel = new SysKnowMarkRel();//添加文章书签关系表
			sysKnowMarkRel.setId(UniqueIdUtil.genId());
			sysKnowMarkRel.setKnowledgeid(knowId);
			sysKnowMarkRel.setMarkid(markId);
			sysKnowMarkReldao.add(sysKnowMarkRel);
		}
	}
	public List<SysKnowledgeMark> getByMarkAlias(String bookMarkAlias){
		return dao.getByMarkAlias(bookMarkAlias);
	}

}
