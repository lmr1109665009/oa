package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.PositionDao;
import com.suneee.platform.dao.system.UserPositionDao;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.model.system.Position;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.ucp.base.event.def.PositionEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *<pre>
 * 对象功能:系统岗位表，实际是部门和职务的对应关系表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2013-11-27 10:19:23
 *</pre>
 */
@Service
public class PositionService extends BaseService<Position>
{
	@Resource
	private PositionDao dao;
	@Resource
    UserPositionDao userPositionDao;
	@Resource 
	UserPositionService userPositionService;
	public PositionService()
	{
	}
	
	@Override
	protected IEntityDao<Position, Long> getEntityDao()
	{
		return dao;
	}
	
	 /**
	 * 根据用户id获取主岗位。
	 * @param userId
	 * @return
	 */
    public Position getPrimaryPositionByUserId(Long userId) {
    	return this.dao.getPrimaryPositionByUserId(userId);
    }

	/**
	 * 根据用户id获取岗位列表。
	 * @param userId
	 * @return
	 */
	public List<Position> getByUserId(Long userId){
		return dao.getByUserId(userId);
	}
	
	/**
	 * 根据岗位名称获得岗位信息
	 * @param posName
	 * @return
	 */
	public List<Position> getByPosName(String posName) {
		return dao.getByPosName(posName);
	}
	
	/**
     * 取得某个用户的岗位id
     * 
     * @param userId 用户id
     * @return
     */
    public List<Long> getPositonIdsByUserId(Long userId) {
    	List<Long> list=new ArrayList<Long>();
        List<Position> positionList = this.dao.getByUserId(userId);
        for (Position pos : positionList) {
            list.add(pos.getPosId());
        }
        return list;
    }
    
    /**
	 * 添加岗位和人员。
	 * 	1.如果当前人员是主岗位，则将该人员的之前的岗位修改为非主岗位。
	 * @param position
	 * @param upList
	 * @throws Exception 
	 */
	public void add(Position position,List<UserPosition> upList) throws Exception{
		this.add(position);
		if(BeanUtils.isEmpty(upList)) return;
		for(UserPosition up:upList){
			Long posId=position.getPosId();
			Long userId=position.getPosId();
			
			boolean isPrimary=up.getIsPrimary()==UserPosition.PRIMARY_YES;
			if(isPrimary){
				userPositionDao.updNotPrimaryByUserId(userId);
			}
			up.setPosId(posId);
			up.setUserPosId(UniqueIdUtil.genId());
			userPositionDao.add(up);
		}
	}
	
	/**
	 * 根据组织id串得到 组织及岗位的关系
	 * @author hjx
	 * @version 创建时间：2013-11-27  下午3:16:17
	 * @param orgIds
	 * @return
	 */
	public List<Position> getOrgPosListByOrgIds(String orgIds){
		return  dao.getOrgPosListByOrgIds(orgIds);
	}
	

	/**
	 * 根据组织id串得到 组织集合
	 * 只用于 组织岗位树
	 * @author hjx
	 * @version 创建时间：2013-11-27  下午3:16:17
	 * @param orgIds
	 * @return
	 */
	public List<Position> getOrgListByOrgIds(String orgIds){
		return  dao.getOrgListByOrgIds(orgIds);
	}
	
	/**
	 * 删除岗位，实际是修改标志位<br>
	 * 需要顺带将岗位下的人员标志删除
	 * @author hjx
	 * @version 创建时间：2013-12-4  上午10:50:27
	 * @param posId
	 */
	public void deleteByUpdateFlag(Long posId){
		  dao.deleteByUpdateFlag(posId);
		  userPositionService.delByPosId(posId);
	}
	
	/**
	 * 根据用户ID获取默认的的岗位。
	 * @param userId
	 * @return
	 */
	public Position getDefaultPosByUserId(Long userId) {

		List<UserPosition> list = userPositionDao.getByUserId(userId);
		Long posId = 0L;
		// 个人不属于任何一个部门。
		if (BeanUtils.isEmpty(list))
			return null;
		if (list.size() == 1) {
			posId = list.get(0).getPosId();
		} else {
			// 获取主要的组织。
			for (UserPosition userPosition : list) {
				if (userPosition.getIsPrimary().equals(UserPosition.PRIMARY_YES)) {
					posId = userPosition.getPosId();
					break;
				}
			}
			// 没有获取到主组织，从列表中获取一个组织作为当前组织。
			if (posId == 0L)
				posId = list.get(0).getPosId();
		}
		return dao.getById(posId);
	}
	
	/**
	 * 通过岗位代码获取岗位
	 * @param posCode 岗位代码
	 * @return
	 */
	public Position getByPosCode(String posCode){
		return dao.getByPosCode(posCode);
	}
	
	public Position getByPosCodeForUpd(String posCode, Long posId){
		return dao.getByPosCodeForUpd(posCode, posId);
	}
		
	public List<Position> getBySupOrgId(QueryFilter filter) {
		return dao.getBySupOrgId(filter);
	}

	/**
	 * 根据职务id删除
	 * @param jobId
	 */
	public void deleByJobId(Long jobId) {
		// 根据职务ID获取岗位信息
		List<Position> posList = this.getByJobId(jobId);
		// 删除职务下的岗位信息
		dao.deleByJobId(jobId);
		// 修改岗位信息
		for(Position pos : posList){
			pos.setIsDelete(Position.DELETE_YES);
			pos.setUpdateBy(ContextUtil.getCurrentUserId());
			pos.setUpdatetime(new Date());
			pos.setDelFrom(Position.DELFROM_JOB_DEL);
		}
		
		// 发布岗位信息变更事件
		EventUtil.publishPositionEvent(PositionEvent.ACTION_UPD, posList);
	}

	/**
	 * 根据组织id删除关系表
	 * @param lAryId
	 */
	public void delByOrgId(List<SysOrg> orgs) {
		List<Position> posList = new ArrayList<Position>();
		List<Position> tempList = null;
		for(int i = 0 ; i<orgs.size() ; i++){
			Long orgId = orgs.get(i).getOrgId();
			// 根据组织ID查询该组织下的岗位信息
			tempList = this.getByOrgId(orgId);
			// 删除组织下的岗位信息
			dao.delByOrgId(orgId);
			posList.addAll(tempList);
		}
		
		// 修改岗位信息
		for(Position pos : posList){
			pos.setIsDelete(Position.DELETE_YES);
			pos.setUpdateBy(ContextUtil.getCurrentUserId());
			pos.setUpdatetime(new Date());
			pos.setDelFrom(Position.DELFROM_ORG_DEL);
		}
		
		// 发布岗位信息变更事件
		EventUtil.publishPositionEvent(PositionEvent.ACTION_UPD, posList);
	}
	
	
	/**
	 * 
	 * 判断职务是否被岗位使用
	 * @param jobId
	 * @return 
	 */
	public boolean isJobUsedByPos(Long jobId){
		return dao.isJobUsedByPos(jobId);
	}
	
	/**
	 * 
	 * 获取岗位的poscode
	 * @param posId
	 * @return 
	 */
	public String getPosCode(Long posId){
		return dao.getPosCode(posId);
	}
	
	/**
	 * 
	 * 判断poscode是否被没有被删除的岗位所使用
	 * @param posCode
	 * @return 
	 */
	public boolean isPoscodeUsed(String posCode){
		return dao.isPoscodeUsed(posCode);
	}

	public Position getPosNameByUserId(Long userId) {
		// TODO Auto-generated method stub
		return dao.getPosNameByUserId(userId);
	}
	
	/**
	 * 获取岗位信息
	 * @param positionCode
	 * @param positionName
	 * @param orgId
	 * @param jobId
	 * @param index
	 * @return
	 */
	public Position generatePosition(String positionCode, String positionName, Long orgId, Long jobId, int index){
		String temCode = positionCode;
		if(index > 0){
			temCode = positionCode + index;
		}
		// 根据岗位code查询岗位信息
		Position position = this.getByPosCode(temCode);
		// 当岗位信息不存在时，新增岗位信息
		if(position == null){
			position = this.addPosition(temCode, positionName, orgId, jobId);
		}
		// 岗位信息存在
		else{
			// 组织Id不同或者职务ID不同，重新获取岗位信息
			if(!position.getOrgId().equals(orgId) || !position.getJobId().equals(jobId)){
				index++;
				position = this.generatePosition(positionCode, positionName, orgId, jobId, index);
			}
		}
		return position;	
	}
	
	/**
	 * 添加岗位
	 * @param positionCode
	 * @param positionName
	 * @param orgId
	 * @param jobId
	 * @return
	 */
	public Position addPosition(String positionCode, String positionName, Long orgId, Long jobId){
		Position position = new Position();
		position.setPosId(UniqueIdUtil.genId());
		position.setPosCode(positionCode);
		position.setPosName(positionName);
		position.setJobId(jobId);
		position.setOrgId(orgId);
		this.add(position);
		return position;
	}
	
	/**
	 * 根据职务ID获取岗位信息 
	 * @param jobId
	 * @return
	 */
	public List<Position> getByJobId(Long jobId){
		return dao.getByJobId(jobId);
	}
	
	/** 
	 * 根据组织ID获取岗位信息
	 * @param orgId
	 * @return
	 */
	public List<Position> getByOrgId(Long orgId){
		return dao.getByOrgId(orgId);
	}
}
