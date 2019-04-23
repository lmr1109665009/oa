package com.suneee.oa.service.user;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.oa.dao.user.UserPositionExtendDao;
import com.suneee.platform.dao.system.UserPositionDao;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.model.system.Position;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.service.system.PositionService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.ucp.base.event.def.PositionEvent;
import com.suneee.ucp.base.event.def.UserPositionEvent;
import com.suneee.ucp.base.service.UcpBaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户岗位关系UserPosition扩展Service类
 * @author xiongxianyun
 *
 */
@Service
public class UserPositionExtendService extends UcpBaseService<UserPosition> {

	@Resource
	private UserPositionExtendDao userPositionExtendDao;
	
	@Resource
	private UserPositionDao userPositionDao;
	
	@Resource
	private PositionExtendService positionExtendService;

	@Resource
	private PositionService positionService;

	@Resource
	private UserPositionService userPositionService;

	@Override
	protected IEntityDao<UserPosition, Long> getEntityDao() {
		return null;
	}

	/**
	 * 保存用户岗位关系信息
	 * @param userId
	 * @param positionStrs
	 */
	public void saveUserPos(Long userId, String positionStrs) {
		if (StringUtils.isBlank(positionStrs)) {
			return;
		}

		// 获取用户在当前企业下的用户岗位关系
		List<UserPosition> userPosList = this.userPositionService.getByUserId(userId);
		// 岗位新增列表
		List<Position> posAddList = new ArrayList<Position>();
		// 用户岗位关系更新列表
		List<UserPosition> userPosUpdList = new ArrayList<UserPosition>();
		// 用户岗位关系新增列表
		List<UserPosition> userPosAddList = new ArrayList<UserPosition>();
		// 已经存在的用户岗位关系ID集合
		List<Long> updUserPositionId = new ArrayList<Long>();
		JSONArray positionJsonArr = JSONArray.fromObject(positionStrs);
		Long primaryPosId = null;
		for (Object positionJsonObj : positionJsonArr) {
			JSONObject positionObj = (JSONObject)positionJsonObj;
			Long orgId = Long.valueOf(positionObj.getLong("orgId"));
			Long jobId = Long.valueOf(positionObj.getLong("jobId"));
			Short isPrimary = Short.valueOf(positionObj.getString("isPrimary"));
			Short isCharge = Short.valueOf(positionObj.getString("isCharge"));
			
			// 根据组织ID和职务ID获取岗位信息
			Position position = this.positionExtendService.getByOrgIdAndJobId(orgId, jobId);
			// 岗位信息不存在则新建岗位
			if (position == null) {
				position = this.positionExtendService.addPosition(orgId, jobId);
				posAddList.add(position);
			}
			// 获取主岗位的岗位ID
			if(UserPosition.PRIMARY_YES == isPrimary){
				primaryPosId = position.getPosId();
			}
			// 查询用户岗位关系信息
			UserPosition userPosition = getByIds(userId, position.getPosId(), jobId, orgId);
			// 用户岗位关系不存在，则新建用户岗位关系
			if (userPosition == null) {
				userPosition = new UserPosition();
		        userPosition.setUserPosId(Long.valueOf(UniqueIdUtil.genId()));
		        userPosition.setOrgId(orgId);
		        userPosition.setPosId(position.getPosId());
		        userPosition.setJobId(jobId);
		        userPosition.setUserId(userId);
		        userPosition.setIsCharge(isCharge);
		        userPosition.setIsPrimary(isPrimary);
		        this.userPositionExtendDao.add(userPosition);
		        userPosAddList.add(userPosition);
			}  
			// 用户岗位关系存在，则更新用户岗位关系信息
			else {
				// 如果更新为非主岗位，需要判断用户的主岗位与当前更新的岗位是不是同一个，如果是，则不能将主岗位更新为非主岗位
				if(UserPosition.PRIMARY_NO == isPrimary && userPosition.getPosId().equals(primaryPosId)){
					userPosition.setIsPrimary(UserPosition.PRIMARY_YES);
				} else {
			        userPosition.setIsPrimary(isPrimary);
				}
		        userPosition.setIsCharge(isCharge);
		        this.userPositionExtendDao.update(userPosition);
		        updUserPositionId.add(userPosition.getUserPosId());
		        userPosUpdList.add(userPosition);
			}
		}

		// 删除在已存在的用户岗位关系ID集合中不存在的用户岗位关系
	    for (UserPosition userPos : userPosList) {
	    	if (!(updUserPositionId.contains(userPos.getUserPosId()))) {
	    		userPos.setIsDelete(UserPosition.DELETE_YES);
	    		userPos.setDelFrom(UserPosition.DELFROM_USER_EDIT);
	    		userPositionExtendDao.update(userPos);
	    		userPosUpdList.add(userPos);
	    	}
	    }

	   /* // 发布用户岗位关系新增事件
	    if (!(userPosAddList.isEmpty())) {
	    	EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_ADD, userPosAddList);

	    	//新增将数据同步到内部消息中心
	    }
	    // 发布用户岗位关系更新事件
	    if (!(userPosUpdList.isEmpty())) {
	    	EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_UPD, userPosUpdList);

			//新增将数据同步到内部消息中心
	    }
	    // 发布岗位新增事件
	    if (!(posAddList.isEmpty()))
	    	EventUtil.publishPositionEvent(PositionEvent.ACTION_ADD, posAddList);
*/
		//新增将数据同步到内部消息中心
	}

	/**
	 * 获取用户岗位关系信息
	 * @param userId 用户ID
	 * @param posId 岗位ID
	 * @param jobId 职务ID
	 * @param orgId 组织ID
	 * @return
	 */
	public UserPosition getByIds(Long userId, Long posId, Long jobId, Long orgId) {
		return this.userPositionExtendDao.getByIds(userId, posId, jobId, orgId);
	}
	
	/**
	 * 根据组织ID获取用户岗位关系
	 * @param orgId
	 * @return
	 */
	public List<UserPosition> getOrgCharger(Long orgId){
		return userPositionDao.getChargeByOrgId(orgId);
	}
	
	/** 
	 * 获取已删除的用户岗位关系
	 * @param userId
	 * @return
	 */
	public List<UserPosition> getDelByUserId(Long userId){
		return this.userPositionExtendDao.getDelByUserId(userId);
	}
	
	/** 
	 * 根据用户ID查询岗位信息
	 * (该方法用于外部系统查询使用，不用实现多企业查询)
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> getPositonByUserId(Long userId){
		return this.userPositionExtendDao.getPositonByUserId(userId);
	}
	
	/** 
	 * 删除用户在指定企业下的用户岗位关系
	 * @param userId
	 * @param enterpriseCode 
	 * @param delFrom 删除源头
	 */
	public void delByUserIdAndEnterpriseCode(Long userId, String enterpriseCode, String delFrom){
		// 查询用户在指定企业下的用户岗位关系
		List<UserPosition> userPositionList = userPositionService.getByUserIdAndEnterpriseCode(userId, enterpriseCode);
		userPositionExtendDao.delByUserIdAndEnterpriseCode(userId, enterpriseCode, delFrom);
		if(!userPositionList.isEmpty()){
			for(UserPosition userPos : userPositionList){
				userPos.setIsDelete(UserPosition.DELETE_YES);
				userPos.setDelFrom(delFrom);
				userPos.setUpdateBy(ContextUtil.getCurrentUserId());
				userPos.setUpdatetime(new Date());
			}
			// 同步用户岗位变更消息到权限中心
			EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_DEL, userPositionList);
		}
	}
}