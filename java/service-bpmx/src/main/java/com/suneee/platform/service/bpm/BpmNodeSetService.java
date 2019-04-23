package com.suneee.platform.service.bpm;

import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.engine.GroovyScriptEngine;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.dao.bpm.BpmNodeSetDao;
import com.suneee.platform.dao.form.BpmFormTableDao;
import com.suneee.platform.model.bpm.BpmNodeSet;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.SysUserOrg;
import com.suneee.platform.model.system.UserRole;
import com.suneee.platform.service.form.BpmFormRightsService;
import com.suneee.platform.service.system.SysUserOrgService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.system.UserRoleService;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 对象功能:BpmNodeSetService类 
 * 开发公司:广州宏天软件有限公司 
 * 开发人员:cjj 
 * 创建时间:2011-12-06 13:41:44
 */
@Service
public class BpmNodeSetService extends BaseService<BpmNodeSet> {
	@Resource
	private BpmNodeSetDao dao;
	
	@Resource
	private UserRoleService userRoleService;
	
	@Resource
	private SysUserOrgService userOrgService;
	
	@Resource
	private SysUserService sysUserService;

	@Resource
	private BpmFormRightsService bpmFormRightsService;

	@Resource
	private BpmDefinitionService bpmDefinitionService;
	
	@Resource
    BpmFormTableDao bpmFormTableDao;

	public BpmNodeSetService() {
	}

	@Override
	protected IEntityDao<BpmNodeSet, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 保存节点配置。
	 * @param defId
	 * @param nodeList
	 * @param parentActDefId
	 * @param isOnlineForm 是否在在线表单
	 */
	public void save(Long defId, List<BpmNodeSet> nodeList, String parentActDefId) {
		String actDefId=nodeList.get(0).getActDefId();//随便拿一个nodeSet获取actDefId
		BpmNodeSet oldGloabal=null;//旧的全局nodeSet
		if (StringUtil.isEmpty(parentActDefId)) {
			oldGloabal = dao.getBySetType(actDefId, BpmNodeSet.SetType_GloabalForm);
			dao.delByStartGlobalDefId(defId);
		} else {
			oldGloabal = dao.getBySetTypeAndParentActDefId(actDefId, BpmNodeSet.SetType_GloabalForm, parentActDefId);
			dao.delByStartGlobalDefId(defId, parentActDefId);
		}
		
		//处理全局表单的权限
		BpmNodeSet newGloabal = null;//新全局nodeset
		for(BpmNodeSet bns:nodeList){
			if(bns.getSetType()==BpmNodeSet.SetType_GloabalForm){
				newGloabal=bns;
				break;
			}
		}
		//如果新nodeSet的formKey跟旧的不一样，需要删除旧的表单权限
		if(oldGloabal!=null&&newGloabal!=null&&!oldGloabal.getFormKey().equals(newGloabal.getFormKey())){
			bpmFormRightsService.delFormRights(oldGloabal.getFormKey(), actDefId, null, parentActDefId);
		}
		
		//下面处理nodeSet节点保存
		for (BpmNodeSet node : nodeList) {
			if (node.getSetId() == null) {
				long setId = UniqueIdUtil.genId();
				node.setSetId(setId);
				dao.add(node);
			} else {
				dao.update(node);
				// 表单类型
				if (node.getFormType() == 0) {
					if (StringUtil.isNotEmpty(node.getOldFormKey())) {
						if ((node.getFormKey().equals(node.getOldFormKey())))
							continue;
						// 原来定义过表单权限，并且新定义的在线表单和原定义的表单不一致。
						// 删除原节点的权限定义
						bpmFormRightsService.delFormRights("", node.getActDefId(), node.getNodeId(), parentActDefId);
					}
				} else {
					// 设置其他表单类型时，清空权限设置
					bpmFormRightsService.delFormRights("", node.getActDefId(), node.getNodeId(), parentActDefId);
				}
			}
		}

		bpmFormTableDao.judgeHasMoreThanOneTable(defId);
	}

	/**
	 * 根据流程设置ID取流程节点设置
	 * 
	 * @param defId
	 * @return
	 */
	public List<BpmNodeSet> getByDefId(Long defId) {
		return dao.getByDefId(defId);
	}
	
	/**
	 * 根据流程设置ID取流程节点设置
	 * 
	 * @param defId
	 * @param parentActDefId
	 * @return
	 */
	public List<BpmNodeSet> getByDefId(Long defId, String parentActDefId) {
		return dao.getByDefIdAndParentActDefId(defId, parentActDefId,false);
	}

	/**
	 * 根据流程设置ID取流程所有的节点设置
	 * 
	 * @param defId
	 * @return
	 */
	public List<BpmNodeSet> getAllByDefId(Long defId) {
		return dao.getAllByDefId(defId);
	}

	
	/**
	 * 根据流程定义获取流程节点设置对象。
	 * @param defId
	 * @return
	 */
	public Map<String, BpmNodeSet> getMapByDefId(Long defId) {
		return dao.getMapByDefId(defId);
		
	}
	
	
	/**
	 * 根据流程定义和父流程定义获取流程节点设置对象。
	 * @param defId
	 * @param parentActDefId
	 * @return
	 */
	public Map<String, BpmNodeSet> getMapByDefId(Long defId, String parentActDefId) {
		Map<String, BpmNodeSet> map=new HashMap<String, BpmNodeSet>();
		List<BpmNodeSet> list = dao.getByDefIdAndParentActDefId(defId, parentActDefId,false);
		for(BpmNodeSet bpmNodeSet:list){
			map.put(bpmNodeSet.getNodeId(), bpmNodeSet);
		}
		return map;
	}

	
	/**
	 * 通过流程发布ID及节点id获取流程设置节点实体
	 * 
	 * @param deployId
	 * @param nodeId
	 * @param parentActDefId
	 * @return
	 */
	public BpmNodeSet getByActDefIdNodeId(String actDefId, String nodeId, String parentActDefId) {
		BpmNodeSet bpmNodeSet = null;
		BpmNodeSet globalNodeSet = null;
		//父流程定义ID不为空。
		if(StringUtil.isNotEmpty(parentActDefId)){
			bpmNodeSet=dao.getByActDefIdNodeId(actDefId, nodeId, parentActDefId);
			//如果没有配置那么查询子流程配置。
			if(bpmNodeSet==null){
				bpmNodeSet=dao.getByActDefIdNodeId(actDefId, nodeId);
			}
			if (isFormEmpty(bpmNodeSet)){
				globalNodeSet = getBySetType(actDefId, BpmNodeSet.SetType_GloabalForm, parentActDefId);
			}
		}
		else{
			bpmNodeSet=dao.getByActDefIdNodeId(actDefId, nodeId);
			if (isFormEmpty(bpmNodeSet)){
				globalNodeSet = getBySetType(actDefId, BpmNodeSet.SetType_GloabalForm);
			}
		}
		if(BeanUtils.isEmpty(bpmNodeSet)){
			bpmNodeSet = globalNodeSet;
		}
		if(globalNodeSet != null){
			bpmNodeSet.setFormType(globalNodeSet.getFormType());
			bpmNodeSet.setFormUrl(globalNodeSet.getFormUrl());
			bpmNodeSet.setFormKey(globalNodeSet.getFormKey());
			bpmNodeSet.setFormDefName(globalNodeSet.getFormDefName());
			bpmNodeSet.setFormDefId(globalNodeSet.getFormDefId());
		}
		
		
		return bpmNodeSet;
	}
	
	/**
	 * 获取节点表单。
	 * @param actDefId
	 * @param nodeId
	 * @param isMobile
	 * @return
	 */
	private BpmNodeSet getFormByActDefIdNodeId(String actDefId, String nodeId, boolean isMobile){
		BpmNodeSet bpmNodeSet=dao.getByActDefIdNodeId(actDefId, nodeId);
		if(isMobile){
			String mobileFormKey=bpmNodeSet.getMobileFormKey();
			if(StringUtil.isEmpty(mobileFormKey)){
				BpmNodeSet globalNodeSet=dao.getBySetType(actDefId, BpmNodeSet.SetType_GloabalForm);
				setBpmNodeSet(globalNodeSet,bpmNodeSet);
			}
		}
		else{
			//pc表单是否为空
			if(isFormEmpty(bpmNodeSet)){
				BpmNodeSet globalNodeSet=dao.getBySetType(actDefId, BpmNodeSet.SetType_GloabalForm);
				setBpmNodeSet(globalNodeSet,bpmNodeSet);
			}
		}
		return bpmNodeSet;
	}
	
	/**
	 * 获取父子流程关联表单配置。
	 * 根据流程定义，节点id，父流程节点查询节点配置。
	 * 1.如果没有找到配置说明系统没有对主子流程进行配置。
	 * 		1.那么获取子流程在该节点上的配置。
	 * 		2.如果表单为空，则获取子流程的全局表单。
	 * 		3.仍然为空则根据父流程id获取父流程全局设置。
	 * 
	 * 2.获取到节点表单为空。
	 * 		则获取主子流程的全局表单配置。
	 *
	 * 
	 * @param actDefId
	 * @param nodeId
	 * @param parentActDefId
	 * @param isMobile
	 * @return
	 */
	private BpmNodeSet getParentFormByActDefIdNodeId(String actDefId, String nodeId, String parentActDefId,boolean isMobile){
		BpmNodeSet bpmNodeSet=dao.getByActDefIdNodeId(actDefId, nodeId, parentActDefId);
		//如果没有配置那么查询子流程配置。
		if(bpmNodeSet==null){
			//获取子流程配置
			bpmNodeSet=dao.getByActDefIdNodeId(actDefId, nodeId);
			if(isMobile){
				if(StringUtil.isEmpty(bpmNodeSet.getMobileFormKey())){
					//子流程全局配置
					BpmNodeSet globalNodeSet=dao.getBySetType(actDefId, BpmNodeSet.SetType_GloabalForm);
					if(StringUtil.isEmpty(globalNodeSet.getMobileFormKey())){
						//获取父流程全局设置
						BpmNodeSet parentNodeSet=dao.getBySetType(parentActDefId, BpmNodeSet.SetType_GloabalForm);
						setBpmNodeSet(parentNodeSet,bpmNodeSet);
					}
					else{
						setBpmNodeSet(globalNodeSet,bpmNodeSet);
					}
				}
			}
			else{
				if(isFormEmpty(bpmNodeSet)){
					BpmNodeSet globalNodeSet=dao.getBySetType(actDefId, BpmNodeSet.SetType_GloabalForm);
					if(isFormEmpty(globalNodeSet)){
						//获取父流程全局设置
						BpmNodeSet parentNodeSet=dao.getBySetType(parentActDefId, BpmNodeSet.SetType_GloabalForm);
						setBpmNodeSet(parentNodeSet,bpmNodeSet);
					}
					else{
						setBpmNodeSet(globalNodeSet,bpmNodeSet);
					}
				}
			}
		}
		//配置父子关联子流程
		else{
			if(isMobile){
				if(StringUtil.isEmpty(bpmNodeSet.getMobileFormKey())){
					BpmNodeSet globalNodeSet=dao.getBySetTypeAndParentActDefId(actDefId, BpmNodeSet.SetType_GloabalForm, parentActDefId);
					setBpmNodeSet(globalNodeSet,bpmNodeSet);
				}
			}
			else{
				if(isFormEmpty(bpmNodeSet)){
					BpmNodeSet globalNodeSet=dao.getBySetTypeAndParentActDefId(actDefId, BpmNodeSet.SetType_GloabalForm, parentActDefId);
					setBpmNodeSet(globalNodeSet,bpmNodeSet);
				}
			}
		}
		return bpmNodeSet;
	}
	
	/**
	 * 获取流程审批节点表单配置数据。
	 * 分为两种情况，在子流程中。
	 * 不再子流程中。
	 * @param actDefId
	 * @param nodeId
	 * @param parentActDefId
	 * @param isMobile
	 * @return
	 */
	public BpmNodeSet getFormByActDefIdNodeId(String actDefId, String nodeId, String parentActDefId,boolean isMobile) {
		//父流程定义ID不为空。
		if(StringUtil.isNotEmpty(parentActDefId)){
			BpmNodeSet bpmNodeSet =getParentFormByActDefIdNodeId(actDefId,nodeId,parentActDefId,isMobile);
			return bpmNodeSet;
		}
		//没有父流程的状态。
		else{
			BpmNodeSet bpmNodeSet=getFormByActDefIdNodeId(actDefId,nodeId,isMobile);
			return bpmNodeSet;
		}
	}
	
	
	
	/**
	 * 判断表单是否为空。
	 * @param bpmNodeSet
	 * @return
	 */
	private boolean isFormEmpty(BpmNodeSet bpmNodeSet) {
		if(BeanUtils.isEmpty(bpmNodeSet)){
			return true;
		}
		short formType = bpmNodeSet.getFormType();
		String formKey = bpmNodeSet.getFormKey();
		// 没有设置表单的情况
		if (formType == -1) {
			return true;
		}
		// 在线表单的情况
		if (formType == 0) {
			if (StringUtil.isEmpty(formKey)) {
				return true;
			}
		}
		// url表单的情况。
		else {
			String formUrl = bpmNodeSet.getFormUrl();
			if (StringUtil.isEmpty(formUrl)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 取得某个流程定义中对应的某个节点为汇总节点的配置
	 * 
	 * @param actDefId
	 * @param joinTaskKey
	 * @return
	 */
	public BpmNodeSet getByActDefIdJoinTaskKey(String actDefId,
			String joinTaskKey) {
		return dao.getByActDefIdJoinTaskKey(actDefId, joinTaskKey);
	}

	/**
	 * 根据流程定义Id和 表单类型查询 默认表单和起始表单。
	 * 
	 * @param defId
	 * @param setType
	 *            值为(2，全局表单,3，流程业务表单)
	 * @return
	 */
	public BpmNodeSet getBySetType(String actDefId, Short setType) {
		BpmNodeSet bpmNodeSet =dao.getBySetType(actDefId, setType);
		return bpmNodeSet;
	}
	
	/**
	 * 根据流程定义Id、父流程定义Id和 表单类型查询 默认表单和起始表单。
	 * 
	 * @param defId
	 * @param setType
	 *            值为(2，全局表单,3，流程业务表单)
	 * @param parentActDefId
	 * @return
	 * @developer helh
	 */
	public BpmNodeSet getBySetType(String actDefId, Short setType, String parentActDefId) {
		BpmNodeSet bpmNodeSet =dao.getBySetTypeAndParentActDefId(actDefId, setType, parentActDefId);
		return bpmNodeSet; 
	}

	/**
	 * 根据流程定义获取当前的表单数据。
	 * 
	 * @param actDefId
	 * @return
	 */
	public List<BpmNodeSet> getByActDefId(String actDefId) {
		return dao.getByActDefId(actDefId);
	}
	
	/**
	 * 根据流程定义获取当前的表单数据。
	 * 
	 * @param actDefId
	 * @return
	 */
	public List<BpmNodeSet> getByActDefId(String actDefId, String parentActDefId) {
		return dao.getByActDefId(actDefId,parentActDefId);
	}

	/**
	 * 通过定义ID及节点Id更新isJumpForDef字段的设置
	 * 
	 * @param nodeId
	 * @param actDefId
	 * @param isJumpForDef
	 */
	public void updateIsJumpForDef(String nodeId, String actDefId,
			Short isJumpForDef) {
		dao.updateIsJumpForDef(nodeId, actDefId, isJumpForDef);
	}
	
	
	/**
	 * 获取开始节点节点设置。
	 * @param actDefId
	 * @param isMobile
	 * @return
	 */
	public BpmNodeSet getStartBpmNodeSet(String actDefId, boolean isMobile){
		FlowNode flowNode = null;
		try {
			flowNode = NodeCache.getFirstNodeId(actDefId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String nodeId = "";
		if(flowNode!=null){
			nodeId = flowNode.getNodeId();
		}
		//1.查询节点配置。
		BpmNodeSet firstNodeSet=dao.getByActDefIdNodeId(actDefId, nodeId);
		if(isMobile){
			if( firstNodeSet.isMobileEnabled()){
				return firstNodeSet;
			}
		}
		else{
			if(!isFormEmpty(firstNodeSet) ){
				return firstNodeSet;
			}
		}
		/**
		 * 获取开始节点全局节点配置。
		 */
		BpmNodeSet globalNodeSet=dao.getBySetType(actDefId, BpmNodeSet.SetType_GloabalForm);
		if(isFormEmpty(globalNodeSet)) return firstNodeSet;
		
		setBpmNodeSet(globalNodeSet,firstNodeSet);
		return firstNodeSet;
	}
	
	/**
	 * 获取启动的是BpmNodeSet的设置。
	 * @param actDefId
	 * @param parentActDefId
	 * @param isMobile
	 * @return
	 */
	public BpmNodeSet getStartBpmNodeSet(String actDefId,String parentActDefId, boolean isMobile){
		FlowNode flowNode = null;
		try {
			flowNode = NodeCache.getFirstNodeId(actDefId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String nodeId = "";
		if(flowNode!=null){
			nodeId = flowNode.getNodeId();
		}
		//1.查询节点配置。
		BpmNodeSet bpmNodeSet=dao.getByActDefIdNodeId(actDefId,nodeId, parentActDefId);
		if(bpmNodeSet==null){
			bpmNodeSet=dao.getByActDefIdNodeId(actDefId, nodeId);
			if(isMobile){
				if(!bpmNodeSet.isMobileEnabled()){
					BpmNodeSet globalNodeSet=dao.getBySetType(actDefId,  BpmNodeSet.SetType_GloabalForm);
					setBpmNodeSet(globalNodeSet, bpmNodeSet);
				}
			}
			else{
				if(isFormEmpty(bpmNodeSet)){
					BpmNodeSet globalNodeSet=dao.getBySetType(actDefId,  BpmNodeSet.SetType_GloabalForm);
					if(!isFormEmpty(globalNodeSet)){
						setBpmNodeSet(globalNodeSet, bpmNodeSet);
					}
				}
			}
		}
		else{
			if(isMobile){
				if(!bpmNodeSet.isMobileEnabled()){
					BpmNodeSet globalNodeSet=dao.getBySetTypeAndParentActDefId(actDefId, BpmNodeSet.SetType_GloabalForm, parentActDefId);
					if(globalNodeSet.isMobileEnabled()){
						setBpmNodeSet(globalNodeSet, bpmNodeSet);
					}
				}
			}
			else{
				if(!isFormEmpty(bpmNodeSet)){
					BpmNodeSet globalNodeSet=dao.getBySetTypeAndParentActDefId(actDefId, BpmNodeSet.SetType_GloabalForm, parentActDefId);
					if(!isFormEmpty(globalNodeSet)){
						setBpmNodeSet(globalNodeSet, bpmNodeSet);
					}
				}
			}
		}
		return bpmNodeSet;
	}
	
	
	
	/**
	 * 设置节点数据表单数据。
	 * @param fromNodeSet
	 * @param toNodeSet
	 */
	private void setBpmNodeSet(BpmNodeSet fromNodeSet,BpmNodeSet toNodeSet){
		if(toNodeSet==null) toNodeSet = new BpmNodeSet();
		toNodeSet.setFormType(fromNodeSet.getFormType());
		toNodeSet.setFormKey(fromNodeSet.getFormKey());
		toNodeSet.setFormUrl(fromNodeSet.getFormUrl());
		toNodeSet.setDetailUrl(fromNodeSet.getDetailUrl());
		toNodeSet.setMobileFormKey(fromNodeSet.getMobileFormKey());
		toNodeSet.setEnableMobile(fromNodeSet.getEnableMobile());
	}
	
	/**
	 * 获取引用子流程的父流程Id
	 * @param defId
	 * @return
	 */
//	public List<BpmNodeSet> getParentIdByDefId(Long defId){
//		return dao.getParentIdByDefId(defId);
//	}

	public void updateScopeById(Long setId, String scope) {
		if (BeanUtils.isEmpty(setId)) return;
		dao.updateScopeById(setId,scope);
		
	}
	
	public void updateCommunicateById(Long setId, String communicate) {
		if (BeanUtils.isEmpty(setId)) return;
		dao.updateCommunicateById(setId,communicate);
		
	}
	
	public List<BpmNodeSet> getByDefIdOpinion(Long defId,String parentActDefId){
		return dao.getByDefIdOpinion( defId, parentActDefId);
	}

	/**
	 * 获取意见回填字段
	 * @param actDefId
	 * @return
	 */
	public List<String> getOpinionFields(String actDefId){
		return dao.getOpinionFields(actDefId);
	}
	/**
	 * 解析计算沟通人员
	 */
	public Map<String,String> getUserByCommunicate(String communicate){
		Map<String,String> mapCommunicate = new HashMap<String, String>();
		if(null==communicate){
			return new HashMap<String, String>();
		}
		JSONObject json = JSONObject.fromObject(communicate);
		String type = json.getString("type");
		String value= json.getString("value");
		Set<String> userIdSet =  new java.util.HashSet<String>();
		Set<String> userNameSet = new java.util.HashSet<String>();
		if(type.equals("script")){
			String script= JSONObject.fromObject(value).getString("value");
			GroovyScriptEngine engine = (GroovyScriptEngine) AppUtil
					.getBean(GroovyScriptEngine.class);
			Object obj = engine.executeObject(script, null);
			userIdSet.add(obj.toString());
			for(String userId : userIdSet){
				SysUser sysUser = sysUserService.getById(Long.parseLong(userId));
				userIdSet.add(sysUser.getUserId().toString());
//				userNameSet.add(sysUser.getFullname());
				userNameSet.add(ContextSupportUtil.getUsername(sysUser));
			}
		}else if(type.equals("user")){
			userIdSet.add(JSONObject.fromObject(value).getString("id"));
			userNameSet.add(JSONObject.fromObject(value).getString("value"));
		}else if(type.equals("org")){
			String orgValue= JSONObject.fromObject(value).getString("id");
			List<SysUserOrg> userList = userOrgService.getUserByOrgIds(orgValue);
			for(SysUserOrg sysUserOrg:userList){
				userIdSet.add(sysUserOrg.getUserId().toString());
				userNameSet.add(sysUserOrg.getUserName());
				}
		}else if(type.equals("role")){
			String roleValue= JSONObject.fromObject(value).getString("id");		
			List<UserRole> userRoleList = userRoleService.getUserByRoleIds(roleValue);
			for(UserRole userRole: userRoleList){
				userIdSet.add(userRole.getUserId().toString());
//				userNameSet.add(userRole.getFullname());
				SysUser temp=new SysUser();
				temp.setFullname(userRole.getFullname());
				temp.setAliasName(userRole.getAliasName());
				userNameSet.add(ContextSupportUtil.getUsername(temp));
			}
		}
		mapCommunicate.put("cmpIds", userIdSet.toString().replace("[", "").replace("]", ""));
		mapCommunicate.put("cmpNames", userNameSet.toString().replace("[", "").replace("]", ""));
		return mapCommunicate;
	}
}
