package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.dao.system.PositionDao;
import com.suneee.platform.dao.system.SysOrgDao;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.dao.system.UserPositionDao;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.SysUserOrg;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.ucp.base.event.def.UserPositionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 对象功能:用户所属组织或部门 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:pkq
 * 创建时间:2011-12-07 18:23:24
 */
@Service
public class SysUserOrgService extends BaseService<UserPosition>
{

	@Resource
	private UserPositionDao dao;
	@Resource
	private SysOrgDao sysOrgDao;
	@Resource
	private SysUserDao sysUserDao;
	@Resource
	private PositionDao positionDao;

	protected static Logger logger = LoggerFactory.getLogger(SysUserOrgService.class);
	
	public SysUserOrgService()
	{
	}
	
	@Override
	protected IEntityDao<UserPosition, Long> getEntityDao()
	{
		return dao;
		
		
	}
	
	/**
	 * 对象功能：查找该条件的用户组织的实体
     * 创建时间:2011-11-08 12:04:22 
	 */
	@Deprecated
	public SysUserOrg getUserOrgModel(Long userId , Long orgId)
	{
		return new SysUserOrg();
		//return dao.getUserOrgModel(userId,orgId);
	}
	
	/**
	 * 根据用户查询用户所在的组织。
	 * 同时查询出负责人。
	 * @param userId	用户ID
	 * @return
	 */
	public List<SysUserOrg> getOrgByUserId(Long userId){
		List<SysUserOrg> list=new ArrayList<SysUserOrg>();
		List<UserPosition> upList=dao.getByUserId(userId);
		
		for(SysUserOrg sysUserOrg:upList){
			Long orgId=sysUserOrg.getOrgId();
			SysOrg sysOrg=getChageNameByOrgId(orgId);
			sysUserOrg.setChargeName(sysOrg.getOwnUserName());
		}
		return list;
	}
	
	
	
	/**
	 * 根据组织id获取负责人。
	 * <br>
	 * 返回组织对象，对象有负责人id和负责人名字。<br>
	 * 多个负责人则返回ID串
	 * @param orgId		组织ID
	 * @return
	 */
	public SysOrg getChageNameByOrgId(Long orgId){
		SysOrg sysOrg=new SysOrg();      
		List<UserPosition> chargeList= dao.getChargeByOrgId(orgId);
		
		String chargeId="";
		String chargeName="";
		
		for(SysUserOrg charge:chargeList){
			chargeId+=charge.getUserId() +",";
			chargeName+=charge.getUserName() +",";
		}
		
		if(chargeName.length()>0){
			chargeId=chargeId.substring(0,chargeId.length()-1);
			chargeName=chargeName.substring(0,chargeName.length()-1);
		}
		sysOrg.setOwnUser(chargeId);
		sysOrg.setOwnUserName(chargeName);//多个负责人则返回ID串
		return sysOrg;
	}
	

	



	/** 设置为非主岗位。
	 *@param userPosId
	 *主岗位ID
	 */
	public void setIsPrimary(Long userPosId){
		UserPosition userPosition =  dao.getById(userPosId);
		boolean userCount = dao.getCountByUserId(userPosition.getUserId());
		if(userCount ==true){
			if( userPosition.getIsPrimary()==SysUserOrg.PRIMARY_NO){
				userPosition.setIsPrimary(SysUserOrg.PRIMARY_YES);
				dao.updNotPrimaryByUserId(userPosition.getUserId());
			}
			else{
				userPosition.setIsPrimary(UserPosition.PRIMARY_NO);
			}
		}
		else{
			userPosition.setIsPrimary(SysUserOrg.PRIMARY_YES);
		}
		
		dao.update(userPosition);
		
		List<UserPosition> list = new ArrayList<UserPosition>();
		list.add(userPosition);
		EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_UPD, list);
	}

/** 如果userId只有一个岗位将不能将此岗位设置为非主岗位
 *
 *
 */
public boolean getUserCount(Long userPosId){
	UserPosition userPosition =  dao.getById(userPosId);
	boolean userCount = dao.getCountByUserId(userPosition.getUserId());
	return userCount;
}
	

	/**
	 * 设置是否组织负责人。
	 * @param userPosId
	 */
	public void setIsCharge(Long userPosId){
		UserPosition userPosition =  dao.getById(userPosId);
		
		if( userPosition.getIsCharge()==SysUserOrg.CHARRGE_NO){
			userPosition.setIsCharge(SysUserOrg.CHARRGE_YES);
		}
		else{
			userPosition.setIsCharge(SysUserOrg.CHARRGE_NO);
		}
		dao.update(userPosition);
		
		
		List<UserPosition> list = new ArrayList<UserPosition>();
		list.add(userPosition);
		EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_UPD, list);
	}
	
	
	/**
	 * 设置是否组织管理员。
	 * 这个管理员负责管理此组织中的人员分配，授权等操作。
	 * <pre>
	 * 1.如果原来是管理员，就设置为非管理员。
	 * 2.如果原来不是管理员，就设置为管理员。
	 * </pre>
	 * @param userPosId
	 */
	public void setIsManage(Long userPosId){
		UserPosition userPosition =  dao.getById(userPosId);
		if( userPosition.getIsGradeManage()==SysUserOrg.IS_NOT_GRADE_MANAGE){
			userPosition.setIsGradeManage(SysUserOrg.IS_GRADE_MANAGE);
		}
		else{
			userPosition.setIsGradeManage(SysUserOrg.IS_NOT_GRADE_MANAGE);
		}
		dao.update(userPosition);
		
	}
	

	
	public List<SysUser> getLeaderUserByOrgId(Long orgId, boolean upslope) {
		List<UserPosition> list= dao.getChargeByOrgId(orgId);
		if(BeanUtils.isNotEmpty(list)){
			List<SysUser> users=new ArrayList<SysUser>();
			for(SysUserOrg sysUserOrg:list){
				SysUser user = sysUserDao.getById(sysUserOrg.getUserId());
				users.add(user);
			}
			return users;
		} else {
			SysOrg sysOrg=sysOrgDao.getById(orgId);
			if(sysOrg==null)
				return new ArrayList<SysUser>();
			Long parentOrgId=sysOrg.getOrgSupId();
			SysOrg sysOrgParent=sysOrgDao.getById(parentOrgId);
			if (sysOrgParent == null) {
				return new ArrayList<SysUser>();
			} else {
				if(upslope){
					return getLeaderUserByOrgId(parentOrgId,true);
				}else{
					return new ArrayList<SysUser>();
				}
			}
		}
	}

	/**
	 * 获取用户的组织领导。
	 * <pre>
	 * 1.当前人是普通员工，则获取部门负责人，如果找不到，往上级查询负责人。
	 * 2.当前人员是部门负责人，则获取上级部门负责人，如果找不到则往上级查询负责人。
	 * </pre>
	 * @param userId	用户ID
	 * @return
	 */
	public List<TaskExecutor> getLeaderByUserId(Long userId){
		//获取当前用户ID
		Long startUserId= ContextUtil.getCurrentUserId();
		SysOrg sysOrg=null;
		//判断传送的userID是否发起人ID等同于当前用户ID
		if(userId!=startUserId){
			//根据传入用户ID的获取主组织信息
			sysOrg=sysOrgDao.getPrimaryOrgByUserId(userId);
		}else{
			sysOrg=(SysOrg) ContextUtil.getCurrentOrg();
		}
		List<TaskExecutor> list=new ArrayList<TaskExecutor>();
		if(sysOrg==null){
			return list;
		}
		else{
			Long orgId=sysOrg.getOrgId();
			//根据用户获取的主岗位
			UserPosition up=dao.getPrimaryUserPositionByUserId(userId);
			if(up.getIsCharge()==UserPosition.CHARRGE_YES){
				SysOrg sysOrgParent=sysOrgDao.getById(orgId);
				//调用上级组织查询
				return getLeaderByOrgId(sysOrgParent.getOrgSupId());
			}
			else{
				return getLeaderByOrgId(orgId);
			}
		}
	
	}
	
	/**
	 * 获取用户的组织领导岗位。
	 * <pre>
	 * 1.当前人是普通员工，则获取部门负责人，如果找不到，往上级查询负责人岗位。
	 * 2.当前人员是部门负责人，则获取上级部门负责人，如果找不到则往上级查询负责人岗位。
	 * 这个方法写的有些问题，不是太严谨。
	 * </pre>
	 * @param userId	用户ID
	 * @return
	 */
	public String getLeaderPosByUserId(Long userId){
		//根据用户获取的主岗位
		UserPosition up=dao.getPrimaryUserPositionByUserId(userId);
		Long uId=0L;
		if(up==null)
			return null;
		else{
              SysOrg sysOrg=sysOrgDao.getById(up.getOrgId());
			if(up.getIsCharge()==SysUserOrg.CHARRGE_YES){//是负责人
				
				List<TaskExecutor> list=getLeaderByOrgId(sysOrg.getOrgSupId());
				//调用上级组织查询
				if(BeanUtils.isNotEmpty(list)){
					String tmpUserId=list.get(0).getExecuteId();
					uId=Long.parseLong(tmpUserId);
					return positionDao.getPosByUserId(uId).getPosName();
				}
			}
			else{
				List<TaskExecutor> list=getLeaderByOrgId(up.getOrgId());
				if(BeanUtils.isNotEmpty(list)){
					String tmpUserId=list.get(0).getExecuteId();
					uId=Long.parseLong(tmpUserId);
					return positionDao.getPosByUserId(uId).getPosName();
				}	
			}
		}
		return null;
	}
	
	/**
	 * 根据组织ID返回组织负责人。
	 * <pre>
	 * 1.根据组织id获取组织负责人。
	 * 2.如果组织负责人为空。
	 * 		则往上级查询查询领导负责人，直到找到为止。
	 * </pre>
	 * @param orgId
	 * @return
	 */
	public List<TaskExecutor> getLeaderByOrgId(Long orgId){
		List<UserPosition> list=dao.getChargeByOrgId(orgId);
		if(BeanUtils.isNotEmpty(list)){
			List<TaskExecutor> users=new ArrayList<TaskExecutor>();
			for(SysUserOrg sysUserOrg:list){
				TaskExecutor taskExecutor=TaskExecutor.getTaskUser(sysUserOrg.getUserId().toString(),sysUserOrg.getUserName());
				users.add(taskExecutor);
			}
			return users;
		} else {
			SysOrg sysOrg=sysOrgDao.getById(orgId);
			if(sysOrg==null)
				return new ArrayList<TaskExecutor>();
			Long parentOrgId=sysOrg.getOrgSupId();
			SysOrg sysOrgParent=sysOrgDao.getById(parentOrgId);
			if(sysOrgParent==null){
				return new ArrayList<TaskExecutor>();
			}
			else{
				return getLeaderByOrgId(parentOrgId);
			}
		}
	}
	
	/**
	 * 
	 */
	public String getOrgJsonByAuthOrgId(Long orgId){
		//根据授权组ID获取下级组
		SysOrg org = sysOrgDao.getById(orgId);
		if(org == null) return"";
		String json=getJson(org);
		return json;
	}
	
	
	private String getJson(SysOrg sysOrg){
		StringBuffer sb=new StringBuffer();
		List<SysOrg> list= sysOrgDao.getByOrgPath(sysOrg.getPath());
		//设置顶级组织。
		for(SysOrg org:list){
			org.setTopOrgId(sysOrg.getOrgId());
		}
		
		Long orgId=sysOrg.getOrgId();
		Long demId= sysOrg.getDemId();
		sb.append("{orgId:\""+ orgId +"\", orgName:\"" + sysOrg.getOrgName() +"\",demId:\""+ demId +"\",isRoot:\"0\",orgSupId:\""
					+sysOrg.getOrgSupId()+ "\",path:\""+sysOrg.getPath()+"\",topOrgId:" +orgId );
		List<SysOrg> tmpList=getByParentId(orgId,list);
		if(tmpList.size()>0){
			sb.append(",children:[");
			getChildren(sb,tmpList,list);
			
			sb.append("]");
		}
		sb.append("}");
		return sb.toString();
	}
	
	
	
	private void getChildren(StringBuffer sb, List<SysOrg> list,List<SysOrg> allList){
		for(int i=0;i<list.size();i++){
			SysOrg sysOrg=list.get(i);
			Long orgId=sysOrg.getOrgId();
			Long demId= sysOrg.getDemId() ;
			sb.append("{orgId:\""+ orgId +"\",orgName:\"" + sysOrg.getOrgName()  +"\",demId:\""+ demId +"\",isRoot:0,orgSupId:\""
					+sysOrg.getOrgSupId()+ "\",path:\""+sysOrg.getPath()+"\",topOrgId:" +sysOrg.getTopOrgId());
			List<SysOrg> tmpList=getByParentId(orgId,allList);
			if(tmpList.size()>0){
				sb.append(",children:[");
				getChildren(sb,tmpList,allList);
				sb.append("]");
			}
			sb.append("}");
			if(i<list.size()-1){
				sb.append(",");
			}
		}
	}
	
	/**
	 * 根据父级Id查询下级的组织列表。
	 * @param parentId
	 * @param list
	 * @return
	 */
	private List<SysOrg> getByParentId(Long parentId,List<SysOrg> list){
		List<SysOrg> rtnList=new ArrayList<SysOrg>();
		for(Iterator<SysOrg> it=list.iterator();it.hasNext();){
			SysOrg org=it.next();
			if(parentId.equals(org.getOrgSupId())){
				rtnList.add(org);
				it.remove();
			}
		}
		return rtnList;
	}
	
	public static void main(String[] args) {
		String a="1,111,222,333";
	
		String c="1,111,222,333,445";
		String d="1,112";
		String e="1,112,223";
		
		String f="1,112,225";
		String g="1,113";
		String h="1,114";
		String k="1,115";
		List<String> list=new ArrayList<String>();
		list.add(a);
		
		list.add(c);
		list.add(d);
		list.add(e);
		list.add(f);
		list.add(g);
		list.add(h);
		list.add(k);
			
		List<String> out=new ArrayList<String>();
		String cur=list.get(0);
		out.add(list.get(0));
		for(int i=0;i<list.size();i++){
			String next=list.get(i);
			if(next.indexOf(cur)==-1){
				cur=next;
				out.add(cur);
			}
			
		}
		for(int i=0;i<out.size();i++){
			String str=out.get(i);
			logger.info(str);
		}
	}

	/**
	 *根据组织id，查询用户组织关系列表
	 * @param orgId 组织ID
	 * @return 
	 */
	public List<SysUserOrg> getByOrgId(Long orgId) {
		List<UserPosition> userPositionList = dao.getByOrgId(orgId);
		List<SysUserOrg> sysUserOrgList = new ArrayList<SysUserOrg> ();
		for (UserPosition up : userPositionList) {
			sysUserOrgList.add(up);
		}
		return sysUserOrgList;
	}
	
	public List<SysUserOrg> getUserByOrgIds(String orgIds){
		 List<SysUserOrg> sysUserOrgList=new ArrayList<SysUserOrg>();
		 List<UserPosition> userPositionList =dao.getUserByOrgIds(orgIds);
		 for(UserPosition up:userPositionList){
			 sysUserOrgList.add(up);
		 }
		 return sysUserOrgList;
	}

	/**
	 * 根据组织id获取组织负责人。
	 * @param orgId
	 * @return
	 */
	public List<UserPosition> getChargeByOrgId(Long orgId) {
		List<UserPosition> userPositionList=dao.getChargeByOrgId(orgId);
		return userPositionList;
	}
	
	/**
	 * 获取用户的主组织(没有主组织则返回任一组织)
	 * 
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public SysUserOrg getPrimaryByUserId(Long userId) {
		UserPosition userPosition=dao.getPrimaryUserPositionByUserId(userId);
		 return userPosition;
	}
	
	/**
	 * 根据用户ID删除用户组织关系
	 * @param userId
	 *            用户ID
	 * @param delFrom
	 * @return
	 */
	public void delByUserId(Long userId, String delFrom){
		List<UserPosition> list = dao.getByUserId(userId);
		dao.delByUserId(userId, delFrom);
		
		if(!list.isEmpty()){
			for(UserPosition userPos : list){
				userPos.setIsDelete(UserPosition.DELETE_YES);
			}
			
			EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_DEL, list);
		}
	}
	
	/**
	 * 根据组织ID删除用户组织关系
	 * 实际是删除岗位关系
	 * @author hjx
	 * @version 创建时间：2013-11-27  下午5:40:52
	 * @param orgId
	 */
	public void delByOrgId(Long orgId){
		List<UserPosition> list = dao.getByOrgId(orgId);
		dao.delByOrgId(orgId);
		for(UserPosition userPos : list){
			userPos.setIsDelete(UserPosition.DELETE_YES);
		}
		EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_DEL, list);
	}
	
	/**
	 * 根据组织路径，删除岗位关系
	 */
	public void delByOrgPath(String path){
		// 获取需要删除的用户组织关系
		List<UserPosition> list = dao.getByOrgPath(path);
		// 删除用户组织关系
		dao.delByOrgPath(path);
		// 更改用户组织关系的状态（更改为删除状态）
		for(UserPosition userPos : list){
			userPos.setIsDelete(UserPosition.DELETE_YES);
			userPos.setUpdateBy(ContextUtil.getCurrentUserId());
			userPos.setUpdatetime(new Date());
			userPos.setDelFrom(UserPosition.DELFROM_ORG_DEL);
		}
		EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_DEL, list);
	}
	
	
	/**
	 * 根据用户ID获取可以负责的组织列表。
	 * @param userId
	 * @return
	 */
	public List<SysUserOrg> getChargeOrgByUserId(Long userId){
		List<UserPosition> userPositionList=dao.getChargeOrgByUserId(userId);
		List<SysUserOrg> sysUserOrgList=new ArrayList<SysUserOrg>();
		if(userPositionList==null||userPositionList.size()<=0)return null;
		for(UserPosition up:userPositionList){
			sysUserOrgList.add(up);
		}
		return sysUserOrgList;
	}






}    

