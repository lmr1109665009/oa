/**
 *
 */
package com.suneee.ucp.base.service.script;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.engine.IScript;
import com.suneee.core.model.TaskExecutor;
import com.suneee.platform.dao.system.SysOrgDao;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.bpm.impl.ScriptImpl;
import com.suneee.platform.service.system.*;
import com.suneee.ucp.mh.model.codeTable.CodeTable;
import com.suneee.ucp.mh.service.codeTable.CodeTableService;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Administrator
 *
 */
@Service
public class UserScriptImpl implements IScript {
	@Resource
	private JobService jobService;
	@Resource
	private UserPositionService userPositionService;
	@Resource
	private ScriptImpl scriptImpl;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private CodeTableService codeTableService;
	@Resource
	private UserUnderService userUnderService;
	@Resource
	private SysOrgDao sysOrgDao;
	@Resource
	private UserPositionService userPosService;
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private SysOrgParamService sysOrgParamService;
	@Resource
	private GlobalTypeService globalTypeService;

	@Resource
	private SysOrgService sysOrgService;

	@Resource
	private SysParamService sysParamService;
	private static short gjfzcGrade = 20;
	private static short fzcGrade = 30;
	private static short zjlGrade = 40;
	private static short fzjlGrade = 50;
	private static short directorGrade = 60;
	private static short basicStaffGrade = 90;
	private static short managementPersonGrade  = 80;
	private static short managerGrade = 70;


	public Set getByUseId(String userId){
		Set<String> result = new HashSet<>();
		if(userId == null||userId==""){
			return result;
		}
		result.add(userId);
		return result;
	}
	public Set getByUseId(Long userId){
		Set<String> result = new HashSet<>();
		if(userId == null){
			return result;
		}
		result.add(userId.toString());
		return result;
	}
	/**
	 * 根据部门id获取相应标识的userId（set集合）
	 *
	 * <pre>
	 * 脚本中使用方法:
	 * </pre>
	 *
	 * pengfeng
	 *
	 * @return
	 */

	public Set<String> getCodeByDepartIdAndType(String departId,String type) {
		Set<String> result = new HashSet<>();
		if(StringUtils.isBlank(departId)){
			return result;
		}
		SysOrg org = sysOrgDao.getById(Long.parseLong(departId));
		if(org == null){
			return result;
		}
		String path = org.getPath();
		if(path==null){
			return result;
		}

		List<CodeTable> codeTablelist= codeTableService.getByType(type);
		if(codeTablelist!=null){
			for (int i=0;i< codeTablelist.size(); i++){
				if(path.contains(codeTablelist.get(i).getItemValue())){
					result.add(codeTablelist.get(i).getItemId());
				}
			}
		}
		return result;
	}
	/**
	 * 根据Userid获取相应标识的userId（set集合）
	 *
	 * <pre>
	 * 脚本中使用方法:
	 * </pre>
	 *
	 * liumaorong
	 *
	 * @return
	 */
	public Set<String> getCodeByUserIdAndType(Long userId,String type){
		Set<String> result = new HashSet<>();
		if(userId==null){
			return result;
		}
		List<SysOrg> sysOrglist = sysOrgDao.getOrgsByUserId(userId);
		if(sysOrglist != null){
			for (SysOrg sysOrg : sysOrglist) {
				Set<String> temp =  new HashSet<>();
				temp = this.getCodeByDepartIdAndType(sysOrg.getOrgId().toString(),type);
				result.addAll(temp);
			}
		}
		return result;
	}

	/**
	 * 根据部门id获取政委id
	 * @param departId
	 * @return
	 */
	public Set<String> getZwByDepartId(String departId) {
		return getCodeByDepartIdAndType(departId, CodeTable.DEPART_ZW);
	}

	public Set<String> getZwByDepartId(Long departId) {
		if(departId==null){
			return new HashSet<>();
		}
		String departIdStr = departId.toString();
		return getCodeByDepartIdAndType(departIdStr, CodeTable.DEPART_ZW);
	}
	/**
	 * 根据用户ID获取政委
	 * @param userId
	 * @return
	 */
	public Set<String> getZwByUserId(String userId) {
		if(StringUtils.isBlank(userId)){
			return new HashSet<>();
		}
		Long userIdLong = Long.parseLong(userId);
		return getZwByUserId(userIdLong);

	}

	public Set<String> getZwByUserId(Long userId) {
		return getCodeByUserIdAndType(userId, CodeTable.DEPART_ZW);
	}
	/**
	 * 脚本 根据userid获取逐级部门的所有HRBP
	 *
	 * @param userId
	 * @return
	 * @author LiuMaoRong
	 */
	public Set<String> getHRBPByUserId(String userId) {
		if(StringUtils.isBlank(userId)){
			return new HashSet<>();
		}
		Long userIdLong = Long.parseLong(userId);
		return getCodeByUserIdAndType(userIdLong, CodeTable.DEPART_HRBP);
	}
	public Set<String> getHRBPByUserId(Long userId) {
		return getCodeByUserIdAndType(userId,CodeTable.DEPART_HRBP);
	}
	/**
	 * 脚本 根据部门Id获取逐级部门的所有HRBP
	 *
	 * @param orgId
	 * @return
	 * @author LiuMaoRong
	 */
	public Set<String> getHRBPByOrgId(String orgId){
		return getCodeByDepartIdAndType(orgId, CodeTable.DEPART_HRBP);
	}
	public Set<String> getHRBPByOrgId(Long orgId){
		if(orgId==null){
			return new HashSet<>();
		}
		String orgIdStr = orgId.toString();
		return getCodeByDepartIdAndType(orgIdStr, CodeTable.DEPART_HRBP);
	}

	/**
	 * 脚本 根据用户Id获取部门的所有副总经理
	 *
	 * @param userId
	 * @return
	 * @author LiuMaoRong
	 */
	public Set<String> getFZJLByUserId(String userId) {
		if(StringUtils.isBlank(userId)){
			return new HashSet<>();
		}
		Long userIdLong = Long.parseLong(userId);
		return getCodeByUserIdAndType(userIdLong, CodeTable.DEPART_FZJL);
	}
	public Set<String> getFZJLByUserId(Long userId) {
		return getCodeByUserIdAndType(userId,CodeTable.DEPART_FZJL);
	}
	/**
	 * 脚本 根据部门Id获取部门的所有副总经理
	 *
	 * @param orgId
	 * @return
	 * @author LiuMaoRong
	 */
	public Set<String> getFZJLByOrgId(String orgId){
		return getCodeByDepartIdAndType(orgId, CodeTable.DEPART_FZJL);
	}
	public Set<String> getFZJLByOrgId(Long orgId){
		if(orgId==null){
			return new HashSet<>();
		}
		String orgIdStr = orgId.toString();
		return getCodeByDepartIdAndType(orgIdStr, CodeTable.DEPART_FZJL);
	}
	/**
	 * 脚本 根据部门Id获取部门的所有总经理
	 *
	 * @param userId
	 * @return
	 * @author LiuMaoRong
	 */
	public Set<String> getZJLByUserId(String userId) {
		if(StringUtils.isBlank(userId)){
			return new HashSet<>();
		}
		Long userIdLong = Long.parseLong(userId);
		return getCodeByUserIdAndType(userIdLong, CodeTable.DEPART_ZJL);
	}
	public Set<String> getZJLByUserId(Long userId) {
		return getCodeByUserIdAndType(userId,CodeTable.DEPART_ZJL);
	}
	/**
	 * 脚本 根据部门Id获取部门的所有总经理
	 *
	 * @param orgId
	 * @return
	 * @author LiuMaoRong
	 */
	public Set<String> getZJLByOrgId(String orgId){
		return getCodeByDepartIdAndType(orgId, CodeTable.DEPART_ZJL);
	}
	public Set<String> getZJLByOrgId(Long orgId){
		if(orgId==null){
			return new HashSet<>();
		}
		String orgIdStr = orgId.toString();
		return getCodeByDepartIdAndType(orgIdStr, CodeTable.DEPART_ZJL);
	}

	/**
	 * 根据部门id获取分管领导
	 * @param departId
	 * @return
	 */
	public Set<String> getFGLDByDepartId(String departId) {
		return getCodeByDepartIdAndType(departId, CodeTable.DEPART_FGLD);
	}

	public Set<String> getFGLDByDepartId(Long departId) {
		if(departId==null){
			return new HashSet<>();
		}
		String departIdStr = departId.toString();
		return getCodeByDepartIdAndType(departIdStr, CodeTable.DEPART_FGLD);
	}
	/**
	 * 根据用户ID获取分管领导
	 * @param userId
	 * @return
	 */
	public Set<String> getFGLDByUserId(String userId) {
		if(StringUtils.isBlank(userId)){
			return new HashSet<>();
		}
		Long userIdLong = Long.parseLong(userId);
		return getFGLDByUserId(userIdLong);

	}

	public Set<String> getFGLDByUserId(Long userId) {
		return getCodeByUserIdAndType(userId, CodeTable.DEPART_FGLD);
	}
	/**
	 * 获取用户的主岗位职务信息
	 *
	 * <pre>
	 * userScriptImpl.getUserJob(String userId);
	 * </pre>
	 *
	 * @param userId
	 * @return
	 */
	public Job getUserJob(String userId) {
		UserPosition userPosition = userPositionService.getChargeByUserId(Long.parseLong(userId));
		if (userPosition == null || userPosition.getJobId() == null) {
			return null;
		}
		Job job = jobService.getById(userPosition.getJobId());
		return job;
	}

	/**
	 * 获取用户主岗位职务级别
	 *
	 * <pre>
	 * userScriptImpl.getUserJob(String userId);
	 * </pre>
	 *
	 * @param userId
	 * @return
	 */
	public short getUserJobGrade(String userId) {
		Job job = this.getUserJob(userId);
		if (job == null || job.getGrade() == null) {
			return -1;
		}
		return job.getGrade();
	}

	/**
	 * 获取用户最高职务级别
	 *
	 * @param userId
	 * @return
	 */
	public short getUserMaxLevelJobGrade(String userId) {
		if (StringUtils.isBlank(userId)) {
			return -1;
		}
		UserPosition userPos = userPositionService.getMaxLevelByUserId(Long.parseLong(userId));
		if (userPos == null || userPos.getGrade() == null) {
			return -1;
		}
		return userPos.getGrade();
	}

	/**
	 * 获取用户上级最高职务级别
	 *
	 * @param userId
	 * @return
	 */
	public short getLeaderMaxLevelJobGrade(String userId) {
		if (StringUtils.isBlank(userId)) {
			return -1;
		}
		Set<String> userIds = scriptImpl.getLeaderByUserId(Long.parseLong(userId));
		if (userIds.isEmpty()) {
			return -1;
		}
		return this.getUserMaxLevelJobGrade(userIds.iterator().next());
	}

	/**
	 * 判断用户上级的最高职务级别是否大于指定级别
	 *
	 * @param userId
	 * @param specifyGrade
	 * @param isContains
	 * @return
	 */
	public boolean isLeaderMaxLevelGtSpecifyGrade(String userId, short specifyGrade, boolean isContains) {
		short jobGrade = this.getLeaderMaxLevelJobGrade(userId);
		if (isContains) {
			if (jobGrade == specifyGrade) {
				return true;
			}
		}
		if (jobGrade > specifyGrade) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 判断是否为副总裁级及以上
	 *  @param userId
	 * @return
	 */
	public boolean isGreaterThanFZC(String userId){
		short jobGrade = this.getUserMaxLevelJobGrade(userId);
		if (jobGrade <= fzcGrade) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isGreaterThanFZC(Long userId){
		if(userId==null){
			return false;
		}
		String userIdStr = userId.toString();
		return this.isGreaterThanFZC(userIdStr);
	}

	/**
	 * 判断是否为副总裁
	 *  @param userId
	 * @return
	 */
	public boolean isFZC(String userId){
		short jobGrade = this.getUserMaxLevelJobGrade(userId);
		if (jobGrade == fzcGrade) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isFZC(Long userId){
		if(userId==null){
			return false;
		}
		String userIdStr = userId.toString();
		return this.isFZC(userIdStr);
	}

	/**
	 * 判断是否为高级副总裁级及以上
	 *  @param userId
	 * @return
	 */
	public boolean isGreaterThanGJFZC(String userId){
		short jobGrade = this.getUserMaxLevelJobGrade(userId);
		if (jobGrade <= gjfzcGrade) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isGreaterThanGJFZC(Long userId){
		if(userId==null){
			return false;
		}
		String userIdStr = userId.toString();
		return this.isGreaterThanGJFZC(userIdStr);
	}
	/**
	 * 判断是否为副总裁级以下
	 *  @param userId
	 * @return
	 */
	public boolean isLessThanFZC(String userId){
		short jobGrade = this.getUserMaxLevelJobGrade(userId);
		if (jobGrade > fzcGrade) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isLessThanFZC(Long userId){
		if(userId==null){
			return false;
		}
		String userIdStr = userId.toString();
		return this.isLessThanFZC(userIdStr);
	}
	/**
	 * 判断是否为副总经理级以下
	 *  @param userId
	 * @return
	 */
	public boolean isLessThanFZJL(String userId){
		short jobGrade = this.getUserMaxLevelJobGrade(userId);
		if (jobGrade > fzjlGrade) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isLessThanFZJL(Long userId){
		if(userId==null){
			return false;
		}
		String userIdStr = userId.toString();
		return this.isLessThanFZJL(userIdStr);
	}

	/**
	 * 判断是否为副总经理级及以上总经理级以下
	 *  @param userId
	 * @return
	 */
	public boolean isGThanFZJLAndLThanZJL(String userId){
		short jobGrade = this.getUserMaxLevelJobGrade(userId);
		if (jobGrade > zjlGrade&&jobGrade<=fzjlGrade) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isGThanFZJLAndLThanZJL(Long userId){
		if(userId==null){
			return false;
		}
		String userIdStr = userId.toString();
		return this.isGThanFZJLAndLThanZJL(userIdStr);
	}
	/**
	 * 判断是否为总经理级及以上
	 *  @param userId
	 * @return
	 */
	public boolean isGreaterThanZJL(String userId){
		short jobGrade = this.getUserMaxLevelJobGrade(userId);
		if (jobGrade <= zjlGrade) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isGreaterThanZJL(Long userId){
		if(userId==null){
			return false;
		}
		String userIdStr = userId.toString();
		return this.isGreaterThanZJL(userIdStr);
	}
	/**
	 * 判断用户的级别是否为总监级以上，参数为String userId
	 * @param userId
	 * @return
	 */
	//总监级以上 :<60
	public boolean isGreaterThanDirector(String userId) {
		short jobGrade = this.getUserMaxLevelJobGrade(userId);
		if (jobGrade < directorGrade) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断用户的级别是否为总监级以上，参数为Long userId
	 * @param userId
	 * @return
	 */
	public boolean isGreaterThanDirector(Long userId) {
		if(userId==null){
			return false;
		}
		String userIdStr = userId.toString();
		return this.isGreaterThanDirector(userIdStr);
	}

	/**
	 * 判断用户的级别是否为总监级别，参数为String userId
	 * @param userId
	 * @return
	 */
	//总监级：=60
	public boolean isDirector(String userId) {
		short jobGrade = this.getUserMaxLevelJobGrade(userId);
		if (jobGrade  == directorGrade) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断用户的级别是否为总监级别，参数为Long userId
	 * @param userId
	 * @return
	 */
	public boolean isDirector(Long userId) {
		if(userId==null){
			return false;
		}
		String userIdStr = userId.toString();
		return this.isDirector(userIdStr);
	}

	/**
	 * 判断用户的级别是否为总监级以下，参数为String userId
	 * @param userId
	 * @return
	 */
	//总监级以下：60<x<90
	public boolean isLessThanDirector(String userId) {
		short jobGrade = this.getUserMaxLevelJobGrade(userId);
		if (jobGrade > directorGrade&&jobGrade<basicStaffGrade) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断用户的级别是否为总监级以下，参数为Long userId
	 * @param userId
	 * @return
	 */
	public boolean isLessThanDirector(Long userId) {
		if(userId==null){
			return false;
		}
		String userIdStr = userId.toString();
		return this.isLessThanDirector(userIdStr);
	}

	/**
	 * 判断用户的级别是否为基层人员，参数为String userId
	 * @param userId
	 * @return
	 */
	//基层人员：>=90
	public boolean isBasicStaff(String userId) {
		short jobGrade = this.getUserMaxLevelJobGrade(userId);
		if (jobGrade >= basicStaffGrade) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断用户的级别是否为基层人员，参数为Long userId
	 * @param userId
	 * @return
	 */
	public boolean isBasicStaff(Long userId) {
		if(userId==null){
			return false;
		}
		String userIdStr = userId.toString();
		return this.isBasicStaff(userIdStr);
	}

	/**
	 * 判断用户上级的最高职务级别是否小于指定级别
	 *
	 * @param userId
	 * @param specifyGrade
	 * @param isContains
	 * @return
	 */
	public boolean isLeaderMaxLevelLtSpecifyGrade(String userId, short specifyGrade, boolean isContains) {
		short jobGrade = this.getLeaderMaxLevelJobGrade(userId);
		if (isContains) {
			if (jobGrade == specifyGrade) {
				return true;
			}
		}
		if (jobGrade < specifyGrade) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断用户的上级是否是指定的职务级别
	 *
	 * @param userId
	 *            用户ID
	 * @param specifyGrade
	 *            指定职务级别
	 * @return
	 */
	public boolean isLeaderSpecifyGrade(String userId, short specifyGrade) {
		Set<String> userIds = scriptImpl.getLeaderByUserId(Long.parseLong(userId));
		if (userIds.isEmpty()) {
			return false;
		}
		return this.isSpecifyGrade(userIds.iterator().next(), specifyGrade);
	}

	/**
	 * 判断用户是否是指定职务级别
	 *
	 * @param userId
	 *            用户ID
	 * @param specifyGrade
	 *            指定级别
	 * @return
	 */
	public boolean isSpecifyGrade(String userId, short specifyGrade) {
		short jobGrade = this.getUserJobGrade(userId);
		if (jobGrade == specifyGrade) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断用户的主岗位职务级别是否小于指定级别
	 *
	 * @param userId
	 *            用户ID
	 * @param specifyGrade
	 *            指定级别
	 * @param isContains
	 *            是否包含指定级别
	 * @return
	 */
	public boolean isLtSpecifyGrade(String userId, short specifyGrade, boolean isContains) {
		short jobGrade = this.getUserJobGrade(userId);
		if (isContains) {
			if (jobGrade == specifyGrade) {
				return true;
			}
		}
		if (jobGrade < specifyGrade) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 判断用户的主岗位职务级别是否大于指定级别
	 *
	 * @param userId
	 *            用户ID
	 * @param specifyGrade
	 *            指定级别
	 * @param isContains
	 *            是否包含指定级别
	 * @return
	 */
	public boolean isGtSpecifyGrade(String userId, short specifyGrade, boolean isContains) {
		short jobGrade = this.getUserJobGrade(userId);
		if (isContains) {
			if (jobGrade == specifyGrade) {
				return true;
			}
		}
		if (jobGrade > specifyGrade) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断用户的地区与其上级地区是否相同
	 *
	 * @param userId
	 * @return
	 */
	public boolean isSameRegion(String userId) {
		Long userIdLong = Long.parseLong(userId);
		// 获取用户的上级
		Set<String> userIds = scriptImpl.getLeaderByUserId(userIdLong);
		// 用户未设置上级
		if (userIds.isEmpty()) {
			return false;
		}
		// 获取用户信息
		SysUser sysUser = sysUserService.getById(userIdLong);
		// 获取用户上级的用户信息
		SysUser leaderUser = sysUserService.getById(Long.parseLong(userIds.iterator().next()));
		// 用户的地区为空或者用户上级的地区为空或者用户的地区与用户上级地区相同，此三种情况视为用户与用户上级在同一地区
		if (sysUser.getRegion() == null || leaderUser.getRegion() == null
				|| sysUser.getRegion().equals(leaderUser.getRegion())) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 获取用户指定的部门考勤复核人
	 *
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public Set<String> getUserOrgChecker(String userId) {
		return this.getUserIdFromCodeTable(userId, CodeTable.DEPART_HR);
	}

	/**
	 * 获取用户所在区域的区域HR负责人
	 *
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public Set<String> getUserRegionHR(String userId) {
		return this.getUserIdFromCodeTable(userId, CodeTable.AREA_HR);
	}

	/**
	 * 获取用户的HR考勤复核人
	 *
	 * @param userId
	 * @return
	 */
	public Set<String> getUserHRChecker(String userId) {
		return this.getUserIdFromCodeTable(userId, CodeTable.HR_CHECKER);
	}

	/**
	 * 获取用户所在区域的地区行政副总
	 *
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public Set<String> getUserRegionManager(String userId) {
		return this.getUserIdFromCodeTable(userId, CodeTable.AREA_MANAGER);
	}

	/**
	 * 查询用户所在区域指定类型的审批人
	 *
	 * @param userId
	 * @param settingType
	 * @return
	 */
	public Set<String> getUserIdFromCodeTable(String userId, String settingType) {
		Set<String> userIds = new HashSet<String>();
		if (StringUtils.isBlank(userId)) {
			return userIds;
		}
		// 获取用户所在地区
		SysUser user = this.getUserInfo(userId);
		if (user == null || StringUtils.isBlank(user.getRegion())) {
			return userIds;
		}

		return this.getChargerByCondition(user.getRegion(), settingType);
	}

	/**
	 * @param itemValue
	 * @param settingType
	 * @return
	 */
	public Set<String> getChargerByCondition(String itemValue, String settingType) {
		Set<String> userIds = new HashSet<String>();
		if(StringUtils.isBlank(settingType)||StringUtils.isBlank(itemValue)){
			return userIds;
		}
		// 获取指定类型指定条件下的审批人
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("itemValue", itemValue);
		params.put("settingType", settingType);
		List<CodeTable> codeTableList = codeTableService.getByCondition(params);
		for (CodeTable codeTable : codeTableList) {
			String Id = codeTable.getItemId();
			if(null==Id){
				continue;
			}else{
				userIds.add(Id);
			}
		}
		return userIds;
	}

	/**
	 * 获取当前用户所在部门指定的考勤复核人
	 *
	 * @return
	 */
	public Set<String> getCurrentOrgHRCharger() {
		Long userId = ContextUtil.getCurrentUserId();
		return this.getUserOrgChecker(userId.toString());
	}

	/**
	 * 获取当前用户所在区域的区域HR负责人
	 *
	 * @return
	 */
	public Set<String> getCurrentRegionHRCharger() {
		Long userId = ContextUtil.getCurrentUserId();
		return this.getUserRegionHR(userId.toString());
	}

	/**
	 * 获取当前用户所在区域的地区行政副总
	 *
	 * @return
	 */
	public Set<String> getCurrentRegionViceGeneral() {
		Long userId = ContextUtil.getCurrentUserId();
		return this.getUserRegionManager(userId.toString());
	}

	/**
	 * 获取当前用户的用户信息
	 *
	 * @return
	 */
	public SysUser getCurrentUserInfo() {
		Long userId = ContextUtil.getCurrentUserId();
		return this.getUserInfo(userId.toString());

	}

	/**
	 * 获取用户的用户信息
	 *
	 * @param userId
	 * @return
	 */
	public SysUser getUserInfo(String userId) {
		if (StringUtils.isBlank(userId)) {
			return null;
		}
		return sysUserService.getById(Long.parseLong(userId));
	}

	/**
	 * 获取指定职务级别的上级，如果没有返回空的set集合
	 *
	 * @param userId
	 *            用户id
	 * @param grade
	 *            等级
	 * @return
	 */
	public Set<String> getUserGtByGrade(Long userId, Long grade) {
		Set<String> result = new HashSet<>();
		if (null == userId) {
			return null;
		}
		getByGrade(grade, result, userId);
		return result;
	}

	/**
	 * 获取指定职务级别的上级，如果没有返回空的set集合
	 *
	 * @param userId
	 *            用户id
	 * @param grade
	 *            等级
	 * @return
	 * @author youren
	 */
	public Set<String> getUserGtByGrade(String userId, Long grade) {
		Set<String> result = new HashSet<>();
		if (null == userId) {
			return null;
		}
		Long userIdL = Long.parseLong(userId);
		getByGrade(grade, result, userIdL);
		return result;
	}

	/**
	 * 获取指定用户的，指定职级，用于递归
	 *
	 * @param grade
	 * @param result
	 * @param luserId
	 * @author youren
	 */
	private void getByGrade(Long grade, Set<String> result, Long luserId) {
		Set<TaskExecutor> set = userUnderService.getMyLeader(luserId);
		if (null != set && set.size() > 0) {
			// 循环所有领导，获取指定级别的用户
			for (Iterator<TaskExecutor> it = set.iterator(); it.hasNext();) {
				String aLeaderId = it.next().getExecuteId();
				Long aluserId = Long.parseLong(aLeaderId);

				// 符合条件的集合
				List<UserPosition> someResult = userPositionService.getByGradeAndUserId(aluserId, grade);
				if (null != someResult && someResult.size() > 0) {
					for (UserPosition userPosition : someResult) {
						result.add(userPosition.getUserId().toString());
					}
				}

				// 目前的逻辑是找到了就都结束，不找了
				if (result.size() < 1) {
					getByGrade(grade, result, aluserId);
				}
			}

		}
	}

	/**
	 * 获取用户上级的用户信息
	 *
	 * @param userId
	 * @return
	 */
	public SysUser getUserInfoOfLeader(String userId) {
		if (StringUtils.isBlank(userId)) {
			return null;
		}
		Set<String> userIds = scriptImpl.getLeaderByUserId(Long.parseLong(userId));
		return this.getUserInfo(userIds.iterator().next());
	}

	/**
	 * 取得当前用户主组织等级
	 *
	 * <pre>
	 * 脚本中使用方法: scriptImpl.getCurrentPrimaryOrgName();
	 * </pre>
	 *
	 * pengfeng
	 *
	 * @return
	 */
	public int getCurrentPrimaryOrgGrade() {
		Long userId = ContextUtil.getCurrentUserId();
		SysOrg sysOrg = sysOrgDao.getPrimaryOrgByUserId(userId);
		if (sysOrg == null) {
			return 0;
		}
		String[] path = sysOrg.getOrgPathname().split("/");
		int grade = path.length;
		if (grade >= 3) {
			return 3;
		} else if (grade == 2) {
			return 2;
		} else {
			return 1;
		}
	}

	/**
	 * 根据类型、地区获取用户信息（set集合）
	 *
	 * <pre>
	 * 脚本中使用方法:
	 *  userScriptImpl.getUsersByTypeAndArea(String type,String area);
	 * </pre>
	 *
	 * pengfeng
	 *
	 * @return
	 */
	public Set<String> getUsersByTypeAndArea(String type, String area) {
		Set<String> result = new HashSet<>();
		Map<String, Object> params = new HashMap<>();
		params.put("settingType", type);
		params.put("itemValue", area);
		List<CodeTable> set = codeTableService.getByCondition(params);
		if (set.size() > 0) {
			for (CodeTable codeTable : set) {
				result.add(codeTable.getItemId());
			}
			return result;
		} else {
			return null;
		}
	}

//	/**
//	 * 根据部门id获取政委的userId（set集合）
//	 *
//	 * <pre>
//	 * 脚本中使用方法:
//	 * </pre>
//	 *
//	 * pengfeng
//	 *
//	 * @return
//	 */
//	public Set<String> getZwByDepartId(String departId) {
//		Set<String> result = new HashSet<>();
//		SysOrg org = sysOrgDao.getById(Long.parseLong(departId));
//		if(org == null){
//			return result;
//		}
//		String[] ids = org.getPath().split("\\.");
//		// 因为数组的第一个id为维度ID，不需要参与遍历查找
//		for (int i = ids.length - 1; i > 0; i--) {
//			result = getChargerByCondition(ids[i], CodeTable.DEPART_ZW);
//			if(result.size()==0){
//				continue;
//			}else{
//				return result;
//			}
//		}
//		return result;
//	}
//
//	public Set<String> getZwByDepartId(Long departId) {
//		String depId = departId.toString();
//		return getZwByDepartId(depId);
//	}
//
//	/**
//	 * 根据用户id获取他的一级部门的政委的userId（set集合）
//	 *
//	 * <pre>
//	 * 脚本中使用方法:
//	 *  userScriptImpl.getZwByUserId(String userId)
//	 * </pre>
//	 *
//	 * pengfeng
//	 *
//	 * @return
//	 */
//	public Set<String> getZwByUserId(String userId) {
//		Set<String> result = new HashSet<>();
//		SysOrg sysOrg = sysOrgDao.getPrimaryOrgByUserId(Long.parseLong(userId));
//		if(sysOrg != null){
//			result = this.getZwByDepartId(sysOrg.getOrgId());
//		}
//		return result;
//	}
//
//	public Set<String> getZwByUserId(Long userId) {
//		String Id = userId.toString();
//		return getZwByUserId(Id);
//	}

	/**
	 * 根据用户id判断其逐级部门的是否含有政委（boolean）
	 *
	 * <pre>
	 * 脚本中使用方法:
	 *  userScriptImpl.isExitZwByUserId(String userId)
	 * </pre>
	 *
	 * pengfeng
	 *
	 * @return
	 */
	public boolean isExitZwByUserId(String userId) {
		Set<String> result = getZwByUserId(userId);
		if (result.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isExitZwByUserId(Long userId) {
		if (null == userId) {
			return false;
		}
		String Id = userId.toString();
		return isExitZwByUserId(Id);
	}

	/**
	 * 根据部门id判断其逐级部门的是否含有政委（boolean）
	 *
	 * <pre>
	 * 脚本中使用方法:
	 *  userScriptImpl.isExitZwByDepartId(String userId)
	 * </pre>
	 *
	 * pengfeng
	 *
	 * @return
	 */
	public boolean isExitZwByDepartId(String departId) {
		Set<String> result = getZwByDepartId(departId);
		if (result.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isExitZwByDepartId(Long departId) {
		if (null == departId) {
			return false;
		}
		String Id = departId.toString();
		return isExitZwByUserId(Id);
	}

//	/**
//	 * 脚本 根据userid获取逐级部门的所有HRBP
//	 *
//	 * @param userId
//	 * @return
//	 * @author youren
//	 */
//	public Set<String> getHRBPByUserId(String userId) {
//		Set<String> result = new HashSet<>();
//		SysOrg sysOrg = sysOrgDao.getPrimaryOrgByUserId(Long.parseLong(userId));
//		String path = sysOrg.getPath();
//		String ids[] = path.split("\\.");
//		for (String string : ids) {
//			result.addAll(getSTByDepartId(string, "HRBP"));
//		}
//		return result;
//	}
//	public Set<String> getHRBPByUserId(Long userId) {
//		String strUserId = userId.toString();
//		return getHRBPByUserId(strUserId);
//	}
//	/**
//	 * 脚本 根据部门Id获取逐级部门的所有HRBP
//	 *
//	 * @param orgId
//	 * @return
//	 * @author pengfeng
//	 */
//	public Set<String> getHRBPByOrgId(String orgId){
//		Set<String> result = new HashSet<>();
//		SysOrg sysOrg = sysOrgDao.getById(Long.parseLong(orgId));
//		String path = sysOrg.getPath();
//		String ids[] = path.split("\\.");
//		for (String string : ids) {
//			result.addAll(getSTByDepartId(string, "HRBP"));
//		}
//		return result;
//	}
//	public Set<String> getHRBPByOrgId(Long orgId){
//		String strOrgId = orgId.toString();
//		return getHRBPByOrgId(strOrgId);
//	}
	/**
	 * 脚本 根据组织id 和码表职务类型 获取 该职务的用户id getSTByDepartId("123123","HRBP")
	 *
	 * @param departId
	 * @return
	 * @author youren
	 */
	public Set<String> getSTByDepartId(String departId, String settingType) {
		Set<String> result = new HashSet<>();
		result = getChargerByCondition(departId, settingType);
		return result;
	}

	/**
	 * 脚本 userId的部门是否拥有 拥有某码表职务 isThereSomeT("123123","HRBP")
	 *
	 * @param userId
	 * @param settingType
	 * @return
	 * @author youren
	 */
	public boolean isThereSomeT(String userId, String settingType) {

		SysOrg sysOrg = sysOrgDao.getPrimaryOrgByUserId(Long.parseLong(userId));
		String path = sysOrg.getPath();
		String ids[] = path.split("\\.");
		for (String string : ids) {
			Set<String> s = getSTByDepartId(string, settingType);
			if (null != s && s.size() > 0) {
				return true;
			}
		}
		return false;

	}

	/**
	 * 脚本 根据字典类型 和字典key 获取值 getByDictTypeAndKey("人事职级","总监级")
	 *
	 * @param typeName
	 * @param itemKey
	 * @return
	 * @author youren
	 */
	public String getByDictTypeAndKey(String typeName, String itemKey) {
		Map<String, String> map = new HashMap<>();
		map.put("typeName", typeName);
		map.put("itemKey", itemKey);

		List<Dictionary> dicts = dictionaryService.getByDictTypeAndKey(map);
		if (null != dicts && dicts.size() > 0) {
			return dicts.get(0).getItemValue();
		}
		return null;
	}

	/**
	 * 用户的逐级 部门中(到一级为止) 属性orgParam 值为 orgParamValue 的部门 是否拥有职级为grade的用户 有就返回
	 * eg:getUsersByUOpZj("1","部门标识","综合部",30)
	 *
	 * @param userId
	 * @param orgParam
	 * @param orgParamValue
	 * @param grade
	 * @return
	 * @author youren
	 */
	public Set<String> getUsersByUOpZj(String userId, String orgParam, String orgParamValue, Long grade) {
		Set<String> result = new HashSet<>();

		SysOrg sysOrg = sysOrgDao.getPrimaryOrgByUserId(Long.parseLong(userId));
		String path = sysOrg.getPath();
		return zhusds(orgParam, orgParamValue, grade, result, path);
	}
	public Set<String> getUsersByUOpZj(Long userId, String orgParam, String orgParamValue, Long grade){
		String strUserId = userId.toString();
		return getUsersByUOpZj(strUserId,orgParam,orgParamValue,grade);
	}
	/**
	 * sysOrgId的逐级 部门中(到二级) 属性orgParamKey 值为 orgParamKey 的部门 是否拥有职级为grade的用户
	 * 有就返回 eg:getUsersByUOpZj("1","部门标识","综合部",30)
	 *
	 * @param sysOrgId
	 * @param orgParam
	 * @param orgParamValue
	 * @param grade
	 * @return
	 * @author youren
	 */
	public Set<String> getUsersByOOpZj(String sysOrgId, String orgParam, String orgParamKey, Long grade) {
		Set<String> result = new HashSet<>();
		if(null==sysOrgId||null==grade||null==orgParam||null==orgParamKey){
			return result;
		}
		SysOrg sysOrg = sysOrgDao.getById(Long.parseLong(sysOrgId));
		String path = sysOrg.getPath();
		return zhusds(orgParam, orgParamKey, grade, result, path);
	}

	/**
	 * 只找到3级部门
	 * @param orgParam
	 * @param orgParamKey
	 * @param grade
	 * @param result
	 * @param path
	 * @return
	 */
	private Set<String> zhusds(String orgParam, String orgParamKey, Long grade, Set<String> result, String path) {
		String ids[] = path.split("\\.");
		/*
		 * 根据属性key找到sys_param的id和 对应orgParamKey的 value 如果没有就跳出整个循环
		 * 如果有就记住这个value。 start
		 */
		Long sysParamId = null;
		String orgParamValue = null;
		JSONObject js = null;

		SysParam sysParam = sysParamService.getByParamKey(orgParam);
		if (null != sysParam) {
			sysParamId = sysParam.getParamId();
			js = JSONObject.fromObject(sysParam.getSourceKey());
		}

		Map<String, String> params = js;

		if (null != params) {
			// 目前确定中 {"总经理":" zjl"} 传入的是zjl
			for (Entry<String, String> entry : params.entrySet()) {
				if (orgParamKey.equals(entry.getValue().trim())) {
					orgParamValue = entry.getKey().trim();
					break;
				}
			}
		}

		if (null == orgParamValue) {
			return result;
		}

		/*
		 * 根据属性key找到sys_param的id和 对应orgParamKey的 value 如果没有就跳出整个循环
		 * 如果有就记住这个value。 end
		 */
		for (String childOrg : ids) {
			try {
				SysOrg sysOrg = sysOrgDao.getById(Long.parseLong(childOrg));
				//只找部门
				if(sysOrg.getOrgType()!=3){
					continue;
				}
				findZj(childOrg,sysParamId,orgParamValue,grade,result);
				//还要找这个一级部门的子部门(二级部门)
				List<SysOrg> sysOrgs = sysOrgDao.getOrgByorgSupId(childOrg);
				if(null!=sysOrgs&&sysOrgs.size()>0){
					for (SysOrg sysOrg2 : sysOrgs) {
						findZj(sysOrg2.getOrgId().toString(),sysParamId,orgParamValue,grade,result);
						//如果二级部门还有子部门继续找三级部门
						List<SysOrg> sysOrgs3 = sysOrgDao.getOrgByorgSupId(sysOrg2.getOrgId().toString());
						if(null!=sysOrgs3&&sysOrgs3.size()>0){
							for (SysOrg sysOrg3 : sysOrgs3) {
								findZj(sysOrg3.getOrgId().toString(),sysParamId,orgParamValue,grade,result);
							}
						}
					}
				}
				break;
				// orgid、sys_param的id、value一起查询SYS_ORG_PARAM,有值 则这个部门有这个人
//				HashMap map = new HashMap();
//				map.put("orgId", childOrg);
//				map.put("paramId", sysParamId);
//				map.put("paramValue", orgParamValue);
//				List<SysOrgParam> sysOrgParams = sysOrgParamService.getAll(map);
//				if (null != sysOrgParams && sysOrgParams.size() > 0) {
//					/*
//					 * 找这个部门指定职级的人
//					 */
//					List<UserPosition> someResult = userPositionService.getBydepartAndGrade(Long.parseLong(childOrg),
//							grade.intValue());
//
//					if (null != someResult && someResult.size() > 0) {
//						for (UserPosition userPosition : someResult) {
//							result.add(userPosition.getUserId().toString());
//						}
//					}
//				}
			} catch (Exception e) {
				continue;
			}

		}
		return result;
	}


	public void findZj(String childOrg,Long sysParamId,String orgParamValue,Long grade,Set<String> result){
		// orgid、sys_param的id、value一起查询SYS_ORG_PARAM,有值 则这个部门有这个人
		HashMap map = new HashMap();
		map.put("orgId", childOrg);
		map.put("paramId", sysParamId);
		map.put("paramValue", orgParamValue);
		List<SysOrgParam> sysOrgParams = sysOrgParamService.getAll(map);
		if (null != sysOrgParams && sysOrgParams.size() > 0) {
			/*
			 * 找这个部门指定职级的人
			 */
			List<UserPosition> someResult = userPositionService.getBydepartAndGrade(Long.parseLong(childOrg),
					grade.intValue());

			if (null != someResult && someResult.size() > 0) {
				for (UserPosition userPosition : someResult) {
					result.add(userPosition.getUserId().toString());
				}
			}
		}
	}

	/**
	 * 根据数据字典中itemValue获取其itemName eg:itemValue="glry" itemName="管理人员"
	 *
	 * <pre>
	 * 脚本中使用方法:
	 *  userScriptImpl.getNameByDicValue(String itemValue)
	 * </pre>
	 *
	 * pengfeng
	 *
	 * @return
	 */
	public String getNameByDicValue(String nodekey, String itemValue) {
		GlobalType global = globalTypeService.getByDictNodeKey(nodekey);
		if (null != global.getTypeId()) {
			Dictionary dictionary = dictionaryService.getByTypeIdAndItemValue(global.getTypeId(), itemValue);
			return dictionary.getItemName();
		} else {
			return "";
		}
	}
	/**
	 * 根据流程变量添加流程标题
	 * @param paramColumn（条件判断的字段）
	 * @param titleColumn（需要显示在标题的字段）
	 * pengfeng
	 */
	public void changeTitle(String paramColumn, String titleColumn){
		ProcessCmd cmd = scriptImpl.getProcessCmd();
		//获取流程变量
		try {
			Map<String,Object> map = cmd.getVariables();
			String str = null;
			// 修改流程变量

			if(StringUtils.isBlank(paramColumn)){
				str ="基层人员";
			}else if(this.isGtSpecifyGrade(paramColumn,(short)60,false)){
				str = "管理人员";
			}else if(this.isLtSpecifyGrade(paramColumn,(short)60,false)){
				str ="总监以上";
			}else{
				str="总监级";
			}
			//将流程变量重新赋值
			map.put(titleColumn,str);
			cmd.setVariables(map);
		} catch (Exception e) {
			// TODO: handle exception
			return;
		}

	}
	public void changeTitle(Long paramColumn, String titleColumn){
		String strParam = null;
		if(paramColumn != null){
			strParam=paramColumn.toString();
		}
		changeTitle(strParam,titleColumn);
	}
}
