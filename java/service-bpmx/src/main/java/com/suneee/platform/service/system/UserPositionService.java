package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.*;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.model.system.Position;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.ucp.base.event.def.UserPositionEvent;
import com.suneee.ucp.base.service.system.SysOrgExtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 *<pre>
 * 对象功能:SYS_USER_POS Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-11-27 10:19:23
 *</pre>
 */
@Service
public class UserPositionService extends BaseService<UserPosition>
{
	protected static Logger logger = LoggerFactory.getLogger(SysUserOrgService.class);
	
	@Resource
	private UserPositionDao userPositionDao;
	@Resource
	private PositionService positionService;
	@Resource
	private SysOrgDao sysOrgDao;
	@Resource
	private SysUserDao sysUserDao;
	@Resource
	private PositionDao positionDao;
	@Resource
	private SysOrgTypeDao sysOrgTypeDao;
	@Resource
	private SysOrgExtService sysOrgExtService;
	public UserPositionService()
	{
	}
	
	@Override
	protected IEntityDao<UserPosition, Long> getEntityDao()
	{
		return userPositionDao;
	}
	
	/**
	 * 取得某个职位下的所有用户ID
	 * @param posId
	 * @return
	 */
	public List<Long> getUserIdsByPosId(Long posId)
	{
		return userPositionDao.getUserIdsByPosId(posId);
	}
	

	
	/**
	 * 根据userId获取用户在当前企业下的用户岗位关系信息
	 * @param param
	 * @return
	 */
	public List<UserPosition> getByUserId(Long userId)
	{ 
		return userPositionDao.getByUserId(userId);
	}
	
	/** 
	 * 获取用户在指定企业下的用户岗位关系
	 * @param userId
	 * @param enterpriseCode
	 * @return
	 */
	public List<UserPosition> getByUserIdAndEnterpriseCode(Long userId, String enterpriseCode)
	{ 
		return userPositionDao.getByUserIdAndEnterpriseCode(userId, enterpriseCode);
	}
	
	/** 
	 * 根据用户ID获取用户所有企业下的岗位信息
	 * @param userId
	 * @return
	 */
	public List<UserPosition> getAllByUserId(Long userId){
		return userPositionDao.getAllByUserId(userId);
	}
	
	/**
	 * 获取用户所在部门的企业编码集合
	 * @param userPosArr 用户组织关系数组（该数组对象中的用户ID必须相同）
	 * @return
	 */
	public Set<String> getOrgCodeSet(UserPosition[] userPosArr){
		Set<String> orgCodeSet = new HashSet<String>();
		if(userPosArr == null || userPosArr.length == 0){
			return orgCodeSet;
		}
		
		SysOrg org = null;
		for(UserPosition userPos : userPosArr){
			org = sysOrgExtService.getById(userPos.getOrgId());
			orgCodeSet.add(org.getOrgCode());
		}
		return orgCodeSet;
	}
	 

	/**
	 * 根据userId得用户组织列表，
	 * 组织不重复，但岗位ID和主键都为null
	 * @param param
	 * @return
	 */
	public List<UserPosition> getOrgListByUserId(Long userId)
	{ 
		
		return userPositionDao.getOrgListByUserId(userId);
	}
	
	/**
	 * 根据posId得用户组织列表，
	 * 
	 * @param param
	 * @return
	 */
	public List<UserPosition> getByPosId(Long posId)
	{ 
		return userPositionDao.getByPosId(posId);
	}
		
	
	/**
	 * 根据userId删除用户岗位关系
	 * @param userId
	 * @param delFrom 删除源头
	 * @return
	 */
	public void delByUserId(Long userId, String delFrom)
	{ 
		List<UserPosition> list = this.getAllByUserId(userId);
		userPositionDao.delByUserId(userId, delFrom);
		if(!list.isEmpty()){
			for(UserPosition userPos : list){
				userPos.setIsDelete(UserPosition.DELETE_YES);
				userPos.setUpdateBy(ContextUtil.getCurrentUserId());
				userPos.setUpdatetime(new Date());
				userPos.setDelFrom(delFrom);
			}
			
			EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_DEL, list);
		}
	}
	
	/**
	 * 根据岗位ID删除用户岗位关系
	 * @param param
	 * @return
	 */
	public void delByPosId(Long posId) { 
		List<UserPosition> list = this.getByPosId(posId);
		
		userPositionDao.delByPosId(posId);
		
		for(UserPosition userPos : list){
			userPos.setIsDelete(UserPosition.DELETE_YES);
			userPos.setUpdateBy(ContextUtil.getCurrentUserId());
			userPos.setUpdatetime(new Date());
			userPos.setDelFrom(UserPosition.DELFROM_POS_DEL);
		}
		EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_DEL, list);
	}
	

	/**
	 * 获取用户指定企业下的主岗位关系
	 * 主岗位对应的就是主组织
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public UserPosition getPrimaryUserPositionByUserId(Long userId, String enterpriseCode) {
		return userPositionDao.getPrimaryUserPositionByUserId(userId, enterpriseCode);
	}
	
	/**
	 * 根据组织id串得到用户和组织及岗位的关系
	 * @author hjx
	 * @version 创建时间：2013-11-27  下午3:16:17
	 * @param orgIds
	 * @return
	 */
	public List<UserPosition> getUserByOrgIds(String orgIds){
		return  userPositionDao.getUserByOrgIds(orgIds);
	}
	
	public List<SysUser> getLeaderUserByOrgId(Long orgId, boolean upslope) {
		List<UserPosition> list= userPositionDao.getChargeByOrgId(orgId);
		if(BeanUtils.isNotEmpty(list)){
			List<SysUser> users=new ArrayList<SysUser>();
			for(UserPosition userPosition:list){
				SysUser user = sysUserDao.getById(userPosition.getUserId());
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
	 * 根据用户ID获取可以负责的组织列表。
	 * @param userId
	 * @return
	 */
	public List<UserPosition> getChargeOrgByUserId(Long userId){
		return userPositionDao.getChargeOrgByUserId(userId);
	}
	
	/**
	 * 保存用户和岗位的映射关系。
	 * @param userId	用户ID
	 * @param posIds	岗位ID
	 * @param posIdPrimary	主岗位ID
	 * @param posIdCharge	负责人
	 * @throws Exception
	 */
	public void saveUserPos(Long userId ,Long[] posIds,Long posIdPrimary,Long[] posIdCharge) throws Exception
	 {
			
			//根据UserId获取数据库中没有标记删除的用户岗位信息
			List<UserPosition> userPositions = userPositionDao.getPosIdByUserId(userId);
			List<Long> posIdsTemp = new ArrayList<Long>();
			for (int i = 0; i < userPositions.size(); i++) {
				posIdsTemp.add(userPositions.get(i).getPosId());
			}
			
			if (BeanUtils.isEmpty(posIds)) {
				this.delByUserId(userId, UserPosition.DELFROM_USER_EDIT);
				return ;
			}
			
			List<Long> posIdList = Arrays.asList(posIds);
			// 新增岗位ID集合
			List<Long> posIdsAdd = new ArrayList<Long>();
			// 数据库中存在且此次操作中也存在的岗位ID集合
			List<Long> posIdsList = new ArrayList<Long>();
			List<UserPosition> userPosAdds = new ArrayList<UserPosition>();
			List<UserPosition> userPosUpdates = new ArrayList<UserPosition>();
			//循环从数据库获取的岗位Id数据posIdsTemp，如果不包含在需要更新的岗位数据posIdList中，则说明是需要删除的数据
			for (Long posId : posIdsTemp) {
				if (!posIdList.contains(posId)) {
					this.delByUserIdAndPosId(userId,posId, UserPosition.DELFROM_USER_EDIT);
				}else {
					posIdsList.add(posId);
				}
			}
			//循环需要更新的岗位数据posIdList，如果不包含在从数据库获取的岗位Id数据posIdsTemp中，则说明是需要添加的数据
			for (Long posId : posIdList) {
				if (!posIdsTemp.contains(posId)) {
					posIdsAdd.add(posId);
				}
			}
			
			Position position=new Position();
			for (int i = 0; i< posIdsAdd.size(); i++) {
				UserPosition userPosition = new UserPosition();
				userPosition.setUserPosId(UniqueIdUtil.genId());
				userPosition.setPosId(posIdsAdd.get(i));
				position=positionService.getById(posIdsAdd.get(i));
				userPosition.setJobId(position.getJobId());
				userPosition.setOrgId(position.getOrgId());
				userPosition.setUserId(userId);
				if (posIdPrimary != null && posIdsAdd.get(i).equals(posIdPrimary)) {
					userPosition.setIsPrimary(UserPosition.PRIMARY_YES);
				}
				if (posIdCharge != null && posIdCharge.length > 0) {
					for (int y = 0; y < posIdCharge.length; y++) {
						if (posIdCharge[y] != null && posIdCharge[y].longValue() ==  posIdsAdd.get(i).longValue()) {
							userPosition.setIsCharge(UserPosition.CHARRGE_YES);
						}
					}
				}
				userPositionDao.add(userPosition);
				userPosAdds.add(userPosition);
			}
			
			for (int i = 0; i < posIdsList.size(); i++) {
				UserPosition userPosition = userPositionDao.getUserPosModel(userId,posIdsList.get(i));
				userPosition.setIsPrimary(UserPosition.PRIMARY_NO);
				userPosition.setIsCharge(UserPosition.CHARRGE_NO);
				if (posIdPrimary != null && posIdsList.get(i).equals(posIdPrimary)) {
					userPosition.setIsPrimary(UserPosition.PRIMARY_YES);
				}
				if (posIdCharge != null && posIdCharge.length > 0) {
					for (int y = 0; y < posIdCharge.length; y++) {
						if (posIdCharge[y] != null && posIdCharge[y].longValue() ==  posIdsList.get(i).longValue()) {
							userPosition.setIsCharge(UserPosition.CHARRGE_YES);
						}
					}
				}
				userPositionDao.update(userPosition);
				userPosUpdates.add(userPosition);
			}
			
			EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_ADD, userPosAdds);
			EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_UPD, userPosUpdates);
		}

	
	/**
	 * 获取某用户的组织列表字符串（可能多个组织）
	 * @author hjx
	 * @version 创建时间：2013-12-4  下午4:09:36
	 * @param user
	 * @return
	 */
	public String getOrgnamesByUserId(Long userId){
		StringBuffer orgNames=new StringBuffer();
		//获取每个用户的所有组织，组织不重复
		List<UserPosition> userPositionList=this.getOrgListByUserId(userId);
		if(BeanUtils.isNotEmpty(userPositionList)){
			//循环组织，组织名拼接
			for(UserPosition userPosition:userPositionList){

				if(userPosition.getIsPrimary()!=null&&userPosition.getIsPrimary()==UserPosition.PRIMARY_YES){
					orgNames.append(userPosition.getOrgName()+"(主)  ");
				}else{
					orgNames.append(userPosition.getOrgName()+"  ");
				}
			}
		}
		return orgNames.toString();
	}
	
	

	/**
	 * 获取某岗位的用户列表字符串
	 * @author hjx
	 * @version 创建时间：2013-12-4  下午4:09:36
	 * @param user
	 * @return
	 */
	public String getUsernamesByPosId(Long posId){
		StringBuffer userNames=new StringBuffer();
		List<UserPosition> userPositionList=this.getByPosId(posId);
		if(BeanUtils.isNotEmpty(userPositionList)){
			//循环组织，组织名拼接
			for(UserPosition userPosition:userPositionList){
				//取得用户链接
				userNames.append(ServiceUtil.getUserLinkOpenWindow(userPosition.getUserId().toString(),userPosition.getUserName()));
			}
		}
		return userNames.toString();
	}
	
	
	
	
	/**
	 * 添加多个人员和一个岗位的关系
	 *  @param Long[] userIds
	 *  @param Long orgId
	 */
	public void addPosUser(Long[] userIds,Long posId) throws Exception
	{
		if(BeanUtils.isEmpty(userIds)|| StringUtil.isEmpty(posId.toString())) return;
		Position position=positionService.getById(posId);
		List<UserPosition> list = new ArrayList<UserPosition>();
		for (Long userId : userIds){
			UserPosition userPosition= userPositionDao.getUserPosModel(userId, posId);
			/*break 跳出语句块 执行下面的语句
			continue 跳出当前循环 不执行循环中continue下面的所有语句 
			开始下一次循环*/
			if(userPosition!=null) continue;
			//不存在才新增
			userPosition =new UserPosition();
			userPosition.setUserPosId(UniqueIdUtil.genId());
			userPosition.setOrgId(position.getOrgId());
			userPosition.setUserId(userId);
			userPosition.setPosId(posId);
			userPosition.setJobId(position.getJobId());
			userPositionDao.add(userPosition);
			list.add(userPosition);
		}
		EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_ADD, list);
	}

	public void delByUserIdAndPosId(Long userId, Long posId, String delFrom) {
		// 根据用户ID、岗位ID查询出用户岗位关系
		UserPosition userPos = userPositionDao.getUserPosModel(userId, posId);
		
		// 修改用户岗位信息
		userPos.setIsDelete(UserPosition.DELETE_YES);
		userPos.setDelFrom(delFrom);
		
		userPositionDao.update(userPos);
		
		// 发布用户岗位信息变更消息
		List<UserPosition> list = new ArrayList<UserPosition>();
		list.add(userPos);
		EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_DEL, list);
	}

	public void delByJobId(Long jobId) {
		// 获取需要删除的用户组织关系
		List<UserPosition> list = userPositionDao.getByJobId(jobId);
		// 删除用户组织关系
		userPositionDao.delByJobId(jobId);
		// 更改用户组织关系的状态（更改为删除状态）
		for(UserPosition userPos : list){
			userPos.setIsDelete(UserPosition.DELETE_YES);
			userPos.setUpdateBy(ContextUtil.getCurrentUserId());
			userPos.setUpdatetime(new Date());
			userPos.setDelFrom(UserPosition.DELFROM_JOB_DEL);
		}
		EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_DEL, list);
	}
	
	
	/**
	 * 获取用户主岗位关系
	 * @param userId
	 * @return
	 */
	public UserPosition getChargeByUserId(Long userId) {
		// TODO Auto-generated method stub
		return userPositionDao.getChargeByUserId(userId);
	}
	
	/**
	 * 获取用户最高职务级别岗位关系
	 * @param userId
	 * @return
	 */
	public UserPosition getMaxLevelByUserId(Long userId) {
		// TODO Auto-generated method stub
		return userPositionDao.getMaxLevelByUserId(userId);
	}
	
	/**
	 * 查找用户是否有某个级别的职务
	 * @param userId
	 * @param aGrade
	 * @return
	 */
	public List<UserPosition> getByGradeAndUserId(Long userId,Long aGrade) {
		return userPositionDao.getByGradeAndUserId(userId,aGrade);
	}
	/**
	 * 查找组织是否有某个级别的职务
	 * @param userId
	 * @param aGrade
	 * @return
	 */	
	public List<UserPosition> getBydepartAndGrade(long orgId, int grade){
		return userPositionDao.getBydepartAndGrade(orgId,grade);
	}
//	/**
//	 * 
//	 * 根据orgId判断组织下是否存在人员
//	 * @param orgId
//	 * @return 
//	 */
//	public boolean isUserExistFromOrg(Long orgId){
//		return userPositionDao.isUserExistFromOrg(orgId);
//	}
	
	/**
	 * 根据用户ID和组织路径获取岗位信息
	 * @param userId
	 * @param path
	 * @param orgCodes
	 * @return
	 */
	public List<UserPosition> getByUserIdAndOrgPath(Long userId, String path, List<String> orgCodes){
		return userPositionDao.getByUserIdAndOrgPath(userId, path, orgCodes);
	}
}
