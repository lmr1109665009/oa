/**
 * 
 */
package com.suneee.ucp.base.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.encrypt.EncryptUtil;
import com.suneee.core.excel.reader.DataEntity;
import com.suneee.core.excel.reader.ExcelReader;
import com.suneee.core.excel.reader.FieldEntity;
import com.suneee.core.excel.reader.TableEntity;
import com.suneee.core.util.*;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.oa.model.user.UserEnterprise;
import com.suneee.oa.model.user.UserSynclog;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.oa.service.user.PositionExtendService;
import com.suneee.oa.service.user.UserEnterpriseService;
import com.suneee.oa.service.user.UserPositionExtendService;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.event.def.UserEvent;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.*;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.dao.system.SysUserExtDao;
import com.suneee.ucp.base.event.def.OrgEvent;
import com.suneee.ucp.base.event.def.PositionEvent;
import com.suneee.ucp.base.event.def.UserPositionEvent;
import com.suneee.ucp.base.extentity.Result;
import com.suneee.ucp.base.extentity.UcUser;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.base.util.HttpUtils;
import com.suneee.ucp.base.util.SendMsgCenterUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Administrator
 *
 */
@Service
public class SysUserExtService extends UcpBaseService<SysUser>{
	
	@Resource
	private SysUserExtDao dao;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private UserRoleService userRoleService;
	@Resource
	private JobService jobService;
	@Resource
	private SysOrgExtService sysOrgExtService;
	@Resource
	private PositionService positionService;
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private UserPositionService userPositionService;
	@Resource
	private SysUserOrgService sysUserOrgService;
	@Resource
	private SysRoleService sysRoleService;
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private OrgAuthService orgAuthService;
	@Resource
	private UserUnderService userUnderService;
	@Resource
	private PositionExtendService positionExtendService;
	@Resource
	private UserPositionExtendService userPositionExtendService;
	@Resource
	private MessageService msgService;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	@Resource
	private UserEnterpriseService userEnterpriseService;

	@Override
	protected IEntityDao<SysUser, Long> getEntityDao() {
		return dao;
	}
	@Transactional(propagation=Propagation.NOT_SUPPORTED)
	public String importUserOrg(MultipartFile mFile) throws IOException, Exception{
		// 读取excel文件内容
		ExcelReader reader = new ExcelReader();
		InputStream inputStream = mFile.getInputStream();
		TableEntity tableEntity = reader.readFile(inputStream);
		List<DataEntity> dataEntityList = tableEntity.getDataEntityList();
		if(dataEntityList == null || dataEntityList.isEmpty()){
			return null;
		}
		
		// 默认角色名称
		String defaultRoleName = AppConfigUtil.get(Constants.USER_DEFAULT_ROLENAME, "普通员工");
		// 默认密码
		String defaultPassword = AppConfigUtil.get(Constants.USER_DEFAULT_PASSWORD, "111111");
		// 员工状态
		String defaultUserStatus = AppConfigUtil.get(Constants.USER_DEFAULT_USERSTATUS, "正式员工");
		// 是否过期
		Short defaultIsExpired = Short.valueOf(AppConfigUtil.get(Constants.USER_DEFAULT_ISEXPIRED, "0"));
		// 是否锁定
		Short defaultIsLock = Short.valueOf(AppConfigUtil.get(Constants.USER_DEFAULT_ISLOCK, "0"));
		// 当前状态
		Short defaultStatus = Short.valueOf(AppConfigUtil.get(Constants.USER_DEFAULT_STATUS, "1"));
		// 默认组织维度：行政维度
		Long demId = 1L;
		
		// 被忽略的记录信息
		StringBuilder content = new StringBuilder();
		// 新增的组织列表
		List<SysOrg> addOrgList = new ArrayList<SysOrg>();
		// 更新的组织列表
		List<SysOrg> updateOrgList = new ArrayList<SysOrg>();
		// 新增的用户组织关系列表
		List<UserPosition> userPosList = new ArrayList<UserPosition>();
		// 新增的岗位列表
		List<Position> posAddList = new ArrayList<Position>();
		// 导入失败的用户
		StringBuilder failedUser = new StringBuilder();
		Pattern mobileRegex = Pattern.compile(Constants.REGEX_MOBILE);
		Pattern emailRegex = Pattern.compile(Constants.REGEX_EMAIL);
		row:
		for(DataEntity dataEntity : dataEntityList){
			// 解析文件中的记录
			List<FieldEntity> fieldEntityList = dataEntity.getFieldEntityList();
			if(fieldEntityList == null){
				continue row;
			}
			// 总列数
			int totalColumn = fieldEntityList.size();
			// 文件中字段信息
			String staffNo = null;
			String fullName = null;
			String aliasName = null;
			String account = null;
			String mobile = null;
			String email = null;
			String sex = null;
			String identification = null;
			Long attendNo = null;
			Date workDate = null;
			Date entryDate = null;
			String orgName = null;
			String orgPathName = null;
			String orgCode = null;
			String jobName = null;
			Short isCharge = null;
			//String superior = null;
			String region = null;
			String grade = null;
			String jobCategory = null;
			String phone = null;
			String [] roleCodes =null;
			int action = 0; // 操作类型：0-新增，1-更新，3-删除
			boolean needSync = false; // 用户企业关系是否需要同步到用户中心
			// 值为空的列数
			int blankColumn = 0;
			for(FieldEntity fieldEntity : fieldEntityList){
				if(StringUtils.isBlank(fieldEntity.getValue())){
					blankColumn++;
					// 如果所有列都为空，则忽略此空行，处理下一行数据
					if(blankColumn >= totalColumn){
						continue row;
					} 
					// 继续列循环
					continue;
				}
				// 获取的值去除空格
				String tempValue = fieldEntity.getValue().replaceAll("[" + Constants.SEPARATOR_BLANK 
						+ Constants.SEPARATOR_FULL_BLANK + "]", "");
				String trimName = fieldEntity.getName().replaceAll("\\*", "").trim();

				switch(fieldEntity.getName().replaceAll("\\*", "").trim()){
					case "工号":
						staffNo = tempValue;
						break;
					case "用户姓名":
					case "姓名":
						fullName = tempValue;
						break;
					case "字号":
					case "别名":
						aliasName = tempValue;
						break;
					case "帐号":
					case "用户名":
						account = tempValue;
						break;
					case "部门名称":
						orgName = tempValue;
						break;
					case "部门名称路径":
						orgPathName = tempValue.replaceAll("[∕／]", Constants.SEPARATOR_BACK_SLANT);
						if(!orgPathName.startsWith(Constants.SEPARATOR_BACK_SLANT)){
							orgPathName = Constants.SEPARATOR_BACK_SLANT + orgPathName;
						}
						// 出现连续的“/”时，使用一个“/”替换
						orgPathName = orgPathName.replaceAll("/{2,}", Constants.SEPARATOR_BACK_SLANT);
						break;
					case "组织代码":
					case "企业编码":
						orgCode = tempValue;
						break;
					case "职位名称":
						jobName = tempValue;
						break;
					case "是否负责人":
						if("是".equals(tempValue)){
							isCharge = 1;
						}else{
							isCharge = 0;
						}
						break;
					case "手机":
						mobile = tempValue;
						break;
					case "邮箱":
					case "邮箱地址":
						email = tempValue;
						break;
					case "性别": 
						if("女".equals(tempValue)){
							sex = "0";
						} else {
							sex = "1";
						}
						break;
					case "身份证号":
						identification = tempValue;
						break;
					case "考勤编号":
						attendNo = Double.valueOf(tempValue).longValue();
						break;
					case "入职日期":
						entryDate = DateUtil.parseDate(tempValue);
						break;
					case "工作日期":
						workDate = DateUtil.parseDate(tempValue);
						break;
//					case "上级主管":
//						superior = fieldEntity.getValue();
//						break;
					case "地区":
						region = tempValue;
						break;
					case "职务级别":
						grade = tempValue;
						break;
					case "":
						jobCategory = tempValue;
						break;
					case "电话":
					case "工作号码":
						phone = tempValue;
						break;
					case "角色编号":
						roleCodes = tempValue.split(",");
						break;
						
				}
			}
			
			// 必填字段（用户名（账号）、姓名、邮箱、手机号、组织名称、组织路径、组织代码、职务名称（职级80））为空的用户记录跳过，需要记录失败日志
			if(StringUtils.isBlank(account) || StringUtils.isBlank(fullName) || StringUtils.isBlank(email) 
					|| StringUtils.isBlank(mobile)|| StringUtils.isBlank(orgName) || StringUtils.isBlank(orgCode) 
					|| StringUtils.isBlank(orgPathName) || StringUtils.isBlank(jobName)){
				for(FieldEntity fieldEntity : fieldEntityList){
					content.append(fieldEntity.getName()).append(Constants.SEPARATOR_COLON)
						.append(fieldEntity.getValue()).append(Constants.SEPARATOR_COMMA);
				}
				content.append("errorinfo:请完善用户模板中标*的字段信息").append(Constants.SEPARATOR_LINE_BREAK);
				failedUser.append(fullName).append("\t").append(account).append("\t")
					.append("请完善用户模板中标*的字段信息").append(Constants.SEPARATOR_LINE_BREAK);
				continue;
			}
			
			// 校验手机号格式错误的记录，忽略
			if(!mobileRegex.matcher(mobile).matches()){
				for(FieldEntity fieldEntity : fieldEntityList){
					content.append(fieldEntity.getName()).append(Constants.SEPARATOR_COLON)
						.append(fieldEntity.getValue()).append(Constants.SEPARATOR_COMMA);
				}
				content.append("errorinfo:手机号码格式错误").append(Constants.SEPARATOR_LINE_BREAK);
				failedUser.append(fullName).append("\t").append(account).append("\t")
					.append("手机号码格式错误").append(Constants.SEPARATOR_LINE_BREAK);
				continue;
			}
			
			// 校验邮箱格式的记录，忽略
			if(!emailRegex.matcher(email).matches()){
				for(FieldEntity fieldEntity : fieldEntityList){
					content.append(fieldEntity.getName()).append(Constants.SEPARATOR_COLON)
						.append(fieldEntity.getValue()).append(Constants.SEPARATOR_COMMA);
				}
				content.append("errorinfo:邮箱格式错误").append(Constants.SEPARATOR_LINE_BREAK);
				failedUser.append(fullName).append("\t").append(account).append("\t")
					.append("邮箱格式错误").append(Constants.SEPARATOR_LINE_BREAK);
				continue;
			}
			// 组织名称路径验证
			if(orgPathName.replaceAll("/", "").length() <= 0){
				for(FieldEntity entity : fieldEntityList){
					content.append(entity.getName()).append(":").append(entity.getValue()).append(",");
				}
				content.append(Constants.SEPARATOR_LINE_BREAK);
				failedUser.append(fullName).append("\t").append(account).append("\t")
					.append("组织名称路径格式错误").append(Constants.SEPARATOR_LINE_BREAK);
				continue;
			}
			// 企业编码验证
			Enterpriseinfo enterpriseInfo = enterpriseinfoService.getByCompCode(orgCode);
			if(enterpriseInfo == null){
				for(FieldEntity entity : fieldEntityList){
					content.append(entity.getName()).append(":").append(entity.getValue()).append(",");
				}
				content.append(Constants.SEPARATOR_LINE_BREAK);
				failedUser.append(fullName).append("\t").append(account).append("\t")
					.append("企业编码在系统中不存在").append(Constants.SEPARATOR_LINE_BREAK);
				continue;
			}
			// 根据角色别名查询角色信息
			/*String roleAlias = "bpm_" + orgCode + Constants.SEPARATOR_UNDERLINE + PinyinUtil.getPinYinHeadChar(defaultRoleName);
			SysRole sysRole = sysRoleService.getByRoleAlias(roleAlias);*/
			/*// 角色信息不存在时则新建角色
			if(sysRole == null){
				sysRole = new SysRole();
				sysRole.setRoleId(UniqueIdUtil.genId());
				sysRole.setRoleName(defaultRoleName);
				sysRole.setAlias(roleAlias);
				sysRole.setSystemId(1L);
				sysRole.setAllowDel(Short.valueOf("1"));
				sysRole.setAllowEdit(Short.valueOf("1"));
				sysRole.setEnabled(Short.valueOf("1"));
				sysRole.setEnterpriseCode(orgCode);
				sysRoleService.add(sysRole);
			}*/
			
			// 根据职务code查询职务信息
			String jobCode = orgCode.toLowerCase() + Constants.SEPARATOR_UNDERLINE + PinyinUtil.getPinYinHeadCharFilter(jobName);
			Job job = jobService.generateJob(jobCode, orgCode, jobName, grade, jobCategory, 0);
			
			// 导入模板中组织名称与组织名称路径中的组织名称不同时，组织名称修改为“组织名称路径/组织名称”（防止导入模板中组织名称与组织名称路径中的组织名称不同时，导致组织重复创建）
			String orgNameSplit = orgPathName.substring(orgPathName.lastIndexOf(Constants.SEPARATOR_BACK_SLANT) + 1);
			if(!orgName.equals(orgNameSplit)){
				orgPathName = orgPathName + Constants.SEPARATOR_BACK_SLANT + orgName;
			}
			// 根据部门名称路径查询部门信息
			SysOrg sysOrg = sysOrgExtService.getByOrgPathName(orgPathName, demId, null);
			String status="";
			// 当部门信息不存在时就创建部门
			if(sysOrg == null){
				sysOrg = sysOrgExtService.addSysOrg(orgName, orgCode, "部门", orgPathName, null, demId, addOrgList);
				status=Constants.MESSAGE_STATUS_ADD;
			}else {
				// 组织信息存在，则更新组织编码
				sysOrg.setOrgCode(orgCode);
				sysOrgService.update(sysOrg);
				status=Constants.MESSAGE_STATUS_UPDATE;
			}
			SendMsgCenterUtils sendMsgCenterUtils = new SendMsgCenterUtils();
			sendMsgCenterUtils.sendToUserInfoCenter(sysOrg,status,AppConfigUtil.get(Constants.MESSAGE_ORG_TOPIC));
			
			// 根据组织ID和职务ID查询岗位信息
			Position position = positionExtendService.getByOrgIdAndJobId(sysOrg.getOrgId(), job.getJobid());
			// 岗位信息不存在时就新建
			if(position == null){
				//根据岗位code查询岗位信息
				String positionName = orgName + Constants.SEPARATOR_UNDERLINE + jobName;
				String positionCode = orgCode.toLowerCase() + Constants.SEPARATOR_UNDERLINE + PinyinUtil.getPinYinHeadCharFilter(positionName);
				position = positionService.generatePosition(positionCode, positionName, sysOrg.getOrgId(), job.getJobid(), 0);
				posAddList.add(position);
			}
			
			// 当用户名为空时，获取字号全拼作为账号
//			if(StringUtils.isBlank(account)){
//				account = PinyinUtil.getPinyinToLowerCase(aliasName).replaceAll(Constants.SEPARATOR_COLON, "");
//				if(account.contains(Constants.SEPARATOR_COMMA)){
//					account = account.substring(0, account.indexOf(Constants.SEPARATOR_COMMA));
//				}
//			}
			
			// 获取唯一账号
			SysUser sysUser = getUniqueAccount(account, 0, email);
			
			// 判断邮箱唯一性
			SysUser user = sysUserService.getByMail(email);
			if(user != null && !sysUser.getEmail().equals(user.getEmail())){
				// 日志文件信息
				for(FieldEntity fieldEntity : fieldEntityList){
					content.append(fieldEntity.getName()).append(Constants.SEPARATOR_COLON)
						.append(fieldEntity.getValue()).append(Constants.SEPARATOR_COMMA);
				}
				content.append("errorinfo:邮箱在系统中已经存在").append(Constants.SEPARATOR_LINE_BREAK);
				// 返回给页面的提示信息，账号使用原始账号
				failedUser.append(fullName).append("\t").append(account).append("\t")
					.append("邮箱在系统中已经存在").append(Constants.SEPARATOR_LINE_BREAK);
				// 忽略当前用户数据，处理下一行用户数据
				continue row;
			}
			
			// 判断手机号的唯一性
			user = sysUserService.getByMobile(mobile);
			if(user != null && !sysUser.getMobile().equals(user.getMobile())){
				// 日志文件信息
				for(FieldEntity fieldEntity : fieldEntityList){
					content.append(fieldEntity.getName()).append(Constants.SEPARATOR_COLON)
						.append(fieldEntity.getValue()).append(Constants.SEPARATOR_COMMA);
				}
				content.append("errorinfo:手机号在系统中已经存在").append(Constants.SEPARATOR_LINE_BREAK);
				// 页面提示信息
				failedUser.append(fullName).append("\t").append(account).append("\t")
					.append("手机号在系统中已经存在").append(Constants.SEPARATOR_LINE_BREAK);
				// 忽略当前用户数据，处理下一行用户数据
				continue row;
				
			}
			
			sysUser.setFullname(fullName);
			sysUser.setAliasName(aliasName);
			sysUser.setMobile(mobile);
			sysUser.setEmail(email);
			sysUser.setSex(sex);
			sysUser.setStaffNo(staffNo);
			sysUser.setAttendNo(attendNo);
			sysUser.setIdentification(identification);
			sysUser.setEntryDate(entryDate);
			sysUser.setWorkDate(workDate);
			sysUser.setPhone(phone);
			sysUser.setLoginAccount(enterpriseInfo.getGroupCode() + Constants.SEPARATOR_UNDERLINE + account);
			sysUser.setSyncToUc(SysUser.SYNC_UC_NO);
			// 查询地区
			Dictionary regionDic = dictionaryService.getByNodeKeyAndItemName(Constants.DIC_NODEKEY_DQ, region, orgCode);
			if(regionDic != null){
				sysUser.setRegion(regionDic.getItemValue());
			}
			if(sysUser.isAdd()){
				action = UserEvent.ACTION_ADD;
				needSync = false;
				sysUser.setUserId(UniqueIdUtil.genId());
				sysUser.setPassword(EncryptUtil.encrypt32MD5(defaultPassword));
				sysUser.setIsExpired(defaultIsExpired);
				sysUser.setIsLock(defaultIsLock);
				sysUser.setUserStatus(defaultUserStatus);
				sysUser.setStatus(defaultStatus);
				sysUser.setSn(sysUser.getUserId());
				dao.add(sysUser);
				/*
				// 保存用户角色信息
				UserRole userRole = new UserRole();
				userRole.setUserRoleId(UniqueIdUtil.genId());
				userRole.setUserId(sysUser.getUserId());
				//userRole.setRoleId(sysRole.getRoleId());
				userRoleService.add(userRole);*/
			}else{
				if(sysUser.getSn() == null){
					sysUser.setSn(sysUser.getUserId());
				}
				sysUser.setUpdatetime(new Date());
				sysUser.setUpdateBy(ContextUtil.getCurrentUserId());
				this.importUserUpdate(sysUser);
				action = UserEvent.ACTION_UPD;
				needSync = true;
			}
			
			// 设置用户企业关系信息，用于企业关系数据同步
			String userEnterpriseinfoJson = this.genEnterpriseCodeJsonString(sysUser.getUserId(), orgCode, enterpriseInfo.getGroupCode(), job.getJobCategory());
			sysUser.setUserEnterpriseinfoJson(userEnterpriseinfoJson);
						
			// 保存用户企业关系
			userEnterpriseService.saveUserEnterprise(sysUser, orgCode, needSync);
			// 根据用户Id、组织ID、岗位ID、职务ID查询用户岗位信息
			UserPosition userPosition = userPositionExtendService.getByIds(sysUser.getUserId(), position.getPosId(), job.getJobid(), sysOrg.getOrgId());
			// 用户岗位关系不存在时创建用户岗位关系
			if(userPosition == null){
				userPosition = new UserPosition();
				userPosition.setUserPosId(UniqueIdUtil.genId());
				userPosition.setOrgId(sysOrg.getOrgId());
				userPosition.setPosId(position.getPosId());
				userPosition.setJobId(job.getJobid());
				userPosition.setUserId(sysUser.getUserId());
				userPosition.setIsCharge(isCharge);
				
				// 判断用户在导入组织对应的企业下是否已经有主岗位
				UserPosition userPositionDb = userPositionService.getPrimaryUserPositionByUserId(sysUser.getUserId(), orgCode);
				if(userPositionDb == null){
					userPosition.setIsPrimary(UserPosition.PRIMARY_YES);
				}else {
					userPosition.setIsPrimary(UserPosition.PRIMARY_NO);
				}
				
				userPositionService.add(userPosition);
				userPosList.add(userPosition);
			}
			// 用户岗位关系存在，则判断是否需要将用户设置为组织负责人
			else{
				if(UserPosition.CHARRGE_YES.equals(isCharge) && UserPosition.CHARRGE_NO.equals(userPosition.getIsCharge())){
					sysUserOrgService.setIsCharge(userPosition.getUserPosId());
				}
			}
			// 发布用户变更消息=-=
			EventUtil.publishUserEvent(sysUser, action,true);
			List<Map<String, Object>> positonByUserId = userPositionExtendService.getPositonByUserId(sysUser.getUserId());
			sysUser.setDeptRole(positonByUserId);
			sendMsgCenterUtils.sendToUserInfoCenter(sysUser,Constants.MESSAGE_STATUS_ADD,AppConfigUtil.get(Constants.MESSAGE_USER_TOPIC));
		}
		
		// 发布组织变更、用户组织关系变更消息
		EventUtil.publishOrgEvent(OrgEvent.ACTION_ADD, addOrgList);
		EventUtil.publishOrgEvent(OrgEvent.ACTION_UPD, updateOrgList);
		EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_ADD, userPosList);
		EventUtil.publishPositionEvent(PositionEvent.ACTION_ADD, posAddList);
		if(content.length() > 0){
			String fileName = AppConfigUtil.get(Constants.IMPORT_ERRORDATA_FILEPATH) + mFile.getName() 
				+ DateFormatUtil.format(new Date(), "yyyyMMddHHmmssSSS") + ".txt";
			FileUtil.writeFile(fileName, content.toString());
		}
		return failedUser.toString();
	}
	
	/**
	 * 同步用户到用户中心
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public String syncUserToUserCenter() throws Exception{
		// 查询需要同步的用户信息
		Map<String, Object> params = new HashMap<String, Object>();
    	params.put("syncToUc", SysUser.SYNC_UC_NO);
    	List<SysUser> sysUserList = dao.getAll(params);
    	
    	// 没有需要同步的信息时直接返回
    	if(sysUserList == null || sysUserList.isEmpty()){
    		return Constants.UC_API_SUCCESS;
    	}
    	
    	// 存放同步的用户
    	List<SysUser> syncUserList = new ArrayList<SysUser>();
    	// 将需要同步的用户信息转换为用户中心的用户信息结构
    	List<UcUser> ucUserList = new ArrayList<UcUser>();
    	UcUser ucUser = null;
    	for(SysUser sysUser : sysUserList){
    		// 手机号为空的用户不同步到用户中心
    		if(StringUtils.isNotBlank(sysUser.getMobile())){
    			// 构造用户中心用户信息对象
        		ucUser = new UcUser();
        		ucUser.setUseName(sysUser.getAliasName());
//    			ucUser.setPassword(defaultPassword);
        		ucUser.setMobile(sysUser.getMobile());
        		ucUser.setEmail(sysUser.getEmail());
        		ucUser.setAccount(sysUser.getAccount());
        		ucUser.setName(sysUser.getFullname());
        		if("0".equals(sysUser.getSex()))
        		{
        			ucUser.setSex(false);
        		}else{
        			ucUser.setSex(true);
        		}
        		ucUserList.add(ucUser);
        		
        		// 添加需要同步的用户
        		syncUserList.add(sysUser);
    		}
    	}
    	
    	// 构建用户中心批量导入请求参数
    	Map<String, Object> reqParams = new HashMap<String, Object>();
    	reqParams.put("account", AppConfigUtil.get(Constants.UC_LOGIN_ACCOUNT));
    	reqParams.put("password", AppConfigUtil.get(Constants.UC_LOGIN_PASSWORD));
    	reqParams.put("clientIp", AppConfigUtil.get(Constants.UC_CLIENTIP));
    	reqParams.put("appCode", AppConfigUtil.get(Constants.UC_APPCODE));
    	reqParams.put("users", ucUserList);
    	
    	// 同步数据
    	Result result = HttpUtils.sendToUserCenter(Constants.UC_BATCHIMPORT_API, reqParams, false);
    	//如果同步成功，更新用户信息为已同步
    	SysUser sysUser = null;
    	if(Constants.UC_API_SUCCESS.equals(result.getStatus())){
    		List<Double> ucUserIdList = (List<Double>)result.getData();
        	for(int i = 0, j = syncUserList.size(); i < j; i++){
        		sysUser = syncUserList.get(i);
        		// 系统用户与用户中心用户关联
        		sysUser.setUcUserid(ucUserIdList.get(i).longValue());
        		// 将同步到用户中心标识更改为已同步
        		sysUser.setSyncToUc(SysUser.SYNC_UC_OK);
        		this.updateSyncInfo(sysUser);
        	}
        	return Constants.UC_API_SUCCESS;
    	}
    	return result.getMessage();
	}

	/**
	 * 根据用户ID获取用户信息
	 * @param sysUserIds
	 * @return
	 */
	public List<SysUser> getSysUserByIds(String sysUserIds){
		String[] sysUserIdArr = sysUserIds.split(Constants.SEPARATOR_COMMA);
		List<Long> userIds = new ArrayList<Long>();
		for(String userId : sysUserIdArr){
			userIds.add(Long.parseLong(userId));
		}
		return this.getSysUserByIds(userIds);
	}
	
	/**
	 * 根据用户ID获取用户信息
	 * @param sysUserIds
	 * @return
	 */
	public List<SysUser> getSysUserByIds(List<Long> sysUserIds){
		return dao.getSysUserByIds(sysUserIds);
	}
	
	/**
	 * 获取用户信息
	 * @return
	 */
	public List<SysUser> getUserList(){
		List<SysUser> userList = dao.getAll();
		for(SysUser sysUser : userList){
			sysUser.setPassword("");
		}
		return userList;
	}
	
	/**
	 * 通过用户中心用户ID获取用户信息
	 * @param ucUserid
	 * @return
	 */
	public SysUser getByUcUserid(Long ucUserid){
		return dao.getByUcUserid(ucUserid);
	}
	
	/**
	 * 从用户中心获取用户信息
	 * @param mobile
	 * @param email
	 * @param account
	 * @param aliasName
	 * @return
	 * @throws Exception
	 */
	public SysUser getUserFromUserCenter(String mobile, String email, String account,String aliasName) throws Exception{
		// 构建用户中心查询接口请求参数
		Map<String, Object> reqParams = new HashMap<String, Object>();
    	reqParams.put("account", AppConfigUtil.get(Constants.UC_LOGIN_ACCOUNT));
    	reqParams.put("password", AppConfigUtil.get(Constants.UC_LOGIN_PASSWORD));
    	reqParams.put("clientIp", AppConfigUtil.get(Constants.UC_CLIENTIP));
    	reqParams.put("appCode", AppConfigUtil.get(Constants.UC_APPCODE));
    	reqParams.put("mobile", mobile);
    	reqParams.put("email", email);
    	reqParams.put("oaAccount", account);
    	reqParams.put("aliasName", aliasName);
    	
    	// 根据手机号邮箱查询用户信息
    	Result result = HttpUtils.sendToUserCenter(Constants.UC_GETUSERBYCONDITION_API, reqParams, false);
    	SysUser sysUser = null;
    	if(Constants.UC_API_SUCCESS.equals(result.getStatus())){
    		if(result.getData() != null){
    			sysUser = new SysUser();
    			JSONObject jsonObj = JSONObject.fromObject(result.getData());
        		sysUser.setUcUserid(jsonObj.getLong("userId"));
        		sysUser.setAliasName("null".equals(jsonObj.getString("aliasName")) ? "" : jsonObj.getString("aliasName"));
        		sysUser.setAccount("null".equals(jsonObj.getString("account")) ? "" : jsonObj.getString("account"));
        		sysUser.setMobile("null".equals(jsonObj.getString("mobile")) ? "" : jsonObj.getString("mobile"));
        		sysUser.setEmail("null".equals(jsonObj.getString("email")) ? "" : jsonObj.getString("email"));
        		sysUser.setFullname("null".equals(jsonObj.getString("name"))? "" : jsonObj.getString("name"));
        		sysUser.setSex("false".equals(jsonObj.getString("sex"))? "0" : "1");
        		sysUser.setPassword("null".equals(jsonObj.getString("password")) ? "" : jsonObj.getString("password"));
        		sysUser.setPhone("null".equals(jsonObj.getString("phone")) ? "" : jsonObj.getString("phone"));
    		}
    	}
    	return sysUser;
	}
	
	/**
	 * 在用户中心更新系统用户类型
	 * @param sysUser
	 * @throws Exception
	 */
	public void updateToUserCenter(SysUser sysUser, Map<String, String> enterpriseCodes) throws Exception{
		if(sysUser == null){
			return;
		}
		
		if(sysUser.getUcUserid() == null){
			throw new RuntimeException("更新用户企业关系失败：该用户还未同步到用户中心！");
		}
		String userEnterpriseinfoJson = sysUser.getUserEnterpriseinfoJson();
		if(StringUtils.isBlank(userEnterpriseinfoJson)){
			if(enterpriseCodes == null || enterpriseCodes.isEmpty()){
				throw new RuntimeException("更新用户企业关系失败：用户未设置关联企业！");
			}
			userEnterpriseinfoJson = this.genEnterpriseCodeJsonString(enterpriseCodes, sysUser.getUserId());
		}
		
		// 获取集团编码
		String groupCode = AppConfigUtil.get(Constants.MESSAGE_ENTERPRISE_CODE);
			
		// 构建用户中心查询接口请求参数
		Map<String, Object> reqParams = new HashMap<String, Object>();
    	reqParams.put("account", AppConfigUtil.get(Constants.UC_LOGIN_ACCOUNT));
    	reqParams.put("password", AppConfigUtil.get(Constants.UC_LOGIN_PASSWORD));
    	reqParams.put("clientIp", AppConfigUtil.get(Constants.UC_CLIENTIP));
    	reqParams.put("appCode", AppConfigUtil.get(Constants.UC_APPCODE));
		reqParams.put("enterpriseCode", userEnterpriseinfoJson);
		reqParams.put("ucUserid", sysUser.getUcUserid());
		reqParams.put("compaycode", groupCode);
		reqParams.put("system", AppConfigUtil.get(Constants.UC_SYSTEM, "OA"));
		reqParams.put("modular", "更新用户企业关系");
		
    	// 根据手机号邮箱查询用户信息
		Result result = null;
    	try {
    		result = HttpUtils.sendToUserCenter(Constants.UC_UPDUSERORG_API_NEW, reqParams, false);
		} catch (Exception e) {
			throw new RuntimeException("更新用户企业关系失败：" + e.getMessage(), e);
		}
    	
    	if(Constants.UC_API_SUCCESS.equals(result.getStatus())){
    		logger.debug("更新用户企业关系成功！");
    	} else {
			throw new RuntimeException("更新用户企业关系失败：" + result.getMessage());
		}
	}

	/**
	 * 在用户中心新增B用户
	 * @param sysUser
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void addToUserCenter(SysUser sysUser) throws Exception{
		if(sysUser == null){
			return;
		}
		
		// 获取用户企业关系信息
		String userEnterpriseinfoJson = sysUser.getUserEnterpriseinfoJson();
		if(StringUtils.isBlank(userEnterpriseinfoJson)){
			// 根据用户ID获取用户所属企业信息
			Map<String, String> enterpriseCodes = enterpriseinfoService.getCodeMapByUserId(sysUser.getUserId());
			if(enterpriseCodes == null || enterpriseCodes.isEmpty()){
				throw new RuntimeException("添加企业B用户失败：用户未设置所属企业！");
			}
			userEnterpriseinfoJson = this.genEnterpriseCodeJsonString(enterpriseCodes, sysUser.getUserId());
		}
		
		// 获取集团编码
		String groupCode = AppConfigUtil.get(Constants.MESSAGE_ENTERPRISE_CODE);
				
		// 构建用户中心查询接口请求参数
		JSONObject jsonObj = SysUser.toJsonObject(sysUser);
		Map<String, Object> reqParams = new HashMap<String, Object>();
		for(Iterator<String> keyStr = jsonObj.keys(); keyStr.hasNext();){
			String key =  keyStr.next();
			reqParams.put(key, jsonObj.get(key));
		}
		reqParams.put("enterpriseCode", userEnterpriseinfoJson);
		reqParams.put("oaAccount", sysUser.getAccount());
		reqParams.put("sex", "0".equals(sysUser.getSex()) ? false : true);
		reqParams.put("userPassword", sysUser.getPassword());
    	reqParams.put("account", AppConfigUtil.get(Constants.UC_LOGIN_ACCOUNT));
    	reqParams.put("password", AppConfigUtil.get(Constants.UC_LOGIN_PASSWORD));
    	reqParams.put("clientIp", AppConfigUtil.get(Constants.UC_CLIENTIP));
    	reqParams.put("appCode", AppConfigUtil.get(Constants.UC_APPCODE));
		reqParams.put("compaycode", groupCode);
		reqParams.put("system", AppConfigUtil.get(Constants.UC_SYSTEM, "OA"));
		reqParams.put("modular", "添加企业B用户");

    	// 根据手机号邮箱查询用户信息
    	Result result = null;
    	try {
			result = HttpUtils.sendToUserCenter(Constants.UC_ADDUSER_API_NEW, reqParams, false);
		} catch (Exception e) {
			throw new RuntimeException("添加企业B用户失败：" + e.getMessage(), e);
		}
    	if(Constants.UC_API_SUCCESS.equals(result.getStatus())){
    		sysUser.setSyncToUc(SysUser.SYNC_UC_OK);
    		JSONObject user = JSONObject.fromObject(result.getData());
    		sysUser.setUcUserid(user.getLong("userId"));
    		this.updateSyncInfo(sysUser);
    		logger.error("添加企业B用户成功！");
    	}else{
			throw new RuntimeException("添加企业B用户失败：" + result.getMessage());
    	}
	}
	
	/**
	 * 删除用户中心用户企业关联关系
	 * @param sysUser
	 * @param enterpriseCodes
	 */
	public void delUserOrgFromUserCenter(SysUser sysUser, Set<String> enterpriseCodes) throws Exception{
		this.delFromUserCenter(sysUser, enterpriseCodes, Constants.UC_DELUSERORG_API_NEW, UserSynclog.OPTYPE_DEL_USER_ORG);
	}
	
	/**
	 * 删除用户中心用户或用户企业关系
	 * 
	 * @param sysUser 用户信息
	 * @param enterpriseCodes 用户所在组织的企业编码
	 * @param apiMethod 用户中心接口名称
	 * @param opType 操作类型：3-删除用户，6-删除用户企业关系
	 * @throws Exception
	 */
	public void delFromUserCenter(SysUser sysUser, Set<String> enterpriseCodes, String apiMethod, Short opType) throws Exception{
		if(sysUser == null){
			return;
		}
		String message = "删除用户";
		if(opType == UserSynclog.OPTYPE_DEL_USER_ORG){
			message = "删除用户企业关系";
		}
		
		// 获取集团编码
		String groupCode = AppConfigUtil.get(Constants.MESSAGE_ENTERPRISE_CODE);
				
		// 构建用户中心查询接口请求参数
		Map<String, Object> reqParams = new HashMap<String, Object>();
    	reqParams.put("account", AppConfigUtil.get(Constants.UC_LOGIN_ACCOUNT));
    	reqParams.put("password", AppConfigUtil.get(Constants.UC_LOGIN_PASSWORD));
    	reqParams.put("clientIp", AppConfigUtil.get(Constants.UC_CLIENTIP));
    	reqParams.put("appCode", AppConfigUtil.get(Constants.UC_APPCODE));
    	reqParams.put("enterpriseCode", StringUtils.join(enterpriseCodes.toArray(), Constants.SEPARATOR_COMMA));
    	reqParams.put("email", sysUser.getEmail());
    	reqParams.put("mobile",sysUser.getMobile());
    	reqParams.put("compaycode", groupCode);
		reqParams.put("system", AppConfigUtil.get(Constants.UC_SYSTEM, "OA"));
		reqParams.put("modular", message);
		
		// 根据手机号邮箱查询用户信息
		Result result = null;
		try {
			result = HttpUtils.sendToUserCenter(apiMethod, reqParams, false);
		} catch (Exception e) {
			throw new RuntimeException(message + "失败：" + e.getMessage(), e);
		}
		
		if(Constants.UC_API_SUCCESS.equals(result.getStatus())){
			logger.debug(message + "成功！");
		} else {
			throw new RuntimeException(message + "失败：" + result.getMessage());
		}
	}
	
	/**
	 * 更新用户中心用户信息
	 * @param sysUser
	 * @param syncPassword
	 */
	@SuppressWarnings("unchecked")
	public void updateUserToUserCenter(SysUser sysUser, boolean syncPassword) throws Exception{
		if(sysUser == null){
			return;
		}
		
		if(sysUser.getUcUserid() == null){
			throw new RuntimeException("更新用户信息失败：该用户未同步到用户中心");
		}
		
		// 获取集团编码
		String groupCode = AppConfigUtil.get(Constants.MESSAGE_ENTERPRISE_CODE);
				
		// 构建用户中心查询接口请求参数
		Map<String, Object> reqParams = new HashMap<String, Object>();
		// 修改密码或重置密码操作时同步用户密码
		if(syncPassword){
			reqParams.put("userPassword", sysUser.getPassword());
			reqParams.put("ucUserid", sysUser.getUcUserid());
		}
		// 用户信息更新不同步密码
		else{
			JSONObject jsonObj = SysUser.toJsonObject(sysUser);
			for(Iterator<String> keyStr = jsonObj.keys(); keyStr.hasNext();){
				String key =  keyStr.next();
				reqParams.put(key, jsonObj.get(key));
			}
			reqParams.put("oaAccount", sysUser.getAccount());
			reqParams.put("sex", "0".equals(sysUser.getSex()) ? false : true);
		}
		
		reqParams.put("account", AppConfigUtil.get(Constants.UC_LOGIN_ACCOUNT));
		reqParams.put("password", AppConfigUtil.get(Constants.UC_LOGIN_PASSWORD));
		reqParams.put("clientIp", AppConfigUtil.get(Constants.UC_CLIENTIP));
		reqParams.put("appCode", AppConfigUtil.get(Constants.UC_APPCODE));
		reqParams.put("compaycode", groupCode);
		reqParams.put("system", AppConfigUtil.get(Constants.UC_SYSTEM, "OA"));
		reqParams.put("modular", "更新用户信息");
		
    	// 根据手机号邮箱查询用户信息
    	Result result = null;
    	try {
			result = HttpUtils.sendToUserCenter(Constants.UC_UPDUSER_API, reqParams, false);
		} catch (Exception e) {
			throw new RuntimeException("更新用户信息失败：" + e.getMessage(), e);
		}
    	if(Constants.UC_API_SUCCESS.equals(result.getStatus())){
    		sysUser.setSyncToUc(SysUser.SYNC_UC_OK);
    		this.updateSyncInfo(sysUser);
    		logger.debug("更新用户信息成功！");
    	}else{
    		throw new RuntimeException("更新用户信息失败：" + result.getMessage());
    	}
	}
	
	/**
	 * 设置地区
	 * @param userIds
	 * @param region
	 */
	public void setRegion(String userIds, String region){
		String[] userIdArr = userIds.split(Constants.SEPARATOR_COMMA);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userIds", userIdArr);
		params.put("region", region);
		dao.updateRegion(params);
	}
	
	/**
	 * 根据组织ID获取用户信息（包括子组织用户）
	 * @param orgId
	 * @return
	 */
	public List<SysUser> getByOrgId(Long orgId, String enterpriseCodes){
		String orgPath = null;
		if(orgId != null){
			// 根据组织ID获取组织信息
			SysOrg sysOrg = sysOrgService.getById(orgId);
			if(sysOrg != null){
				// 取出组织的组织路径
				orgPath = sysOrg.getPath();
			}
		}
		List<String> orgCodes = null;
		if(StringUtils.isNotBlank(enterpriseCodes)){
			orgCodes = Arrays.asList(enterpriseCodes.split(Constants.SEPARATOR_COMMA));
		}
		
		List<SysUser> userList = this.getUserByOrgPath(orgPath, orgCodes);
		for(SysUser user : userList){
			user.setUserPositions(userPositionService.getByUserIdAndOrgPath(user.getUserId(), orgPath, orgCodes));
		}
		return userList;
	}

	/**
	 * 用户导入更新用户信息
	 * @param sysUser
	 */
	public void importUserUpdate(SysUser sysUser){
		dao.update("importUserUpdate", sysUser);
	}
	
	/**
	 * 删除用户
	 * @param userId
	 * @return
	 */
	public String deleteUser(Long userId){
		String message = "";
		SysUser sysUser = this.getById(userId);
		// 查询用户所属的所有企业
		Set<String> enterpriseCodes = enterpriseinfoService.getCodeSetByUserId(userId);
		// 登录用户的当前企业编码
		String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
		try {
			// 如果用户仅属于登录用户的当前企业，则更新本系统的用户为删除状态
			if(enterpriseCodes.size() == 1 && enterpriseCodes.contains(enterpriseCode)){
				sysUserService.updStatus(userId, SysUser.STATUS_Del, null);
			}

			// 删除在当前企业下的用户企业关系，用户角色关系，以及用户岗位关系
			// 删除用户中心的用户企业关系
			enterpriseCodes.clear();
			enterpriseCodes.add(enterpriseCode);
			//this.delUserOrgFromUserCenter(sysUser, enterpriseCodes);

			// 删除当前企业下的用户企业关系（逻辑删除）
			userEnterpriseService.delByUserIdAndEnterpriseCode(userId, enterpriseCode, false);
			// 删除当前企业下的用户角色关系（物理删除）
			userRoleService.delByUserIdAndEnterpriseCode(userId, enterpriseCode);
			// 删除当前企业下的用户岗位关系（逻辑删除）
			userPositionExtendService.delByUserIdAndEnterpriseCode(userId, enterpriseCode, UserPosition.DELFROM_USER_DEL);
		} catch (Exception e) {
			message = sysUser.getFullname() + "\t" + e.getMessage();
		}
		return message;
	}

	/**
	 * 根据组织路径获取用户信息
	 * @param orgPath
	 * @return
	 */
	public List<SysUser> getUserByOrgPath(String orgPath, List<String> orgCodes){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orgPath", orgPath);
		params.put("orgCodes", orgCodes);
		return dao.getUserByOrgPath(params);
	}
	/**
	 * 恢复用户:恢复用户中心的状态，成功后修改本系统的状态
	 * @param userId
	 * @throws Exception 
	 */
	public String revertUser(Long userId){
		String message = null;
		SysUser sysUser = this.getById(userId);
		try {
			if(sysUser == null){
				return "用户不存在";
			}
			// 查询用户在当前企业下的用户企业关系
			UserEnterprise userEnterprise = userEnterpriseService.getByUserIdAndCode(userId, CookieUitl.getCurrentEnterpriseCode());
			if(userEnterprise == null){
				return "用户【" + sysUser.getAccount() + "】在当前企业下的用户企业关系不存在！";
			}
			
			// 如果用户为删除状态，则需要先恢复用户的状态
			if(SysUser.STATUS_Del.equals(sysUser.getStatus())){
				// 恢复本系统用户信息（修改用户状态为激活）
				sysUserService.updStatus(sysUser.getUserId(), SysUser.STATUS_OK, null);
			}
			
			// 如果用户已同步到用户中心，恢复用户的用户企业关系
			/*if(sysUser.getUcUserid() != null){
				Map<String, String> enterpriseCodes = new HashMap<String, String>();
				enterpriseCodes.put(userEnterprise.getEnterpriseCode(), userEnterprise.getGroupCode());
				this.updateToUserCenter(sysUser, enterpriseCodes);
			}*/
			
			// 恢复本系统的用户企业关系
			userEnterprise.setIsDelete(UserEnterprise.DELETE_NO);
			userEnterpriseService.update(userEnterprise, false);
		} catch (Exception e) {
			logger.error("恢复用户失败：" + e.getMessage(), e);
			message = sysUser.getFullname() + "\t" + e.getMessage();
		}
		return message;
	}
	
	/**
	 * 更新用户密码。
	 * 
	 * @param
	 *
	 * @param
	 *
	 * @param pwd
	 *            明文密码。
	 */
	public void updPwd(SysUser user, String pwd) throws Exception{
		try {
			String enPassword = EncryptUtil.encrypt32MD5(pwd);
			// 如果是管理员账号就不同步用户中心
			if(!"admin".equalsIgnoreCase(user.getAccount())){
				user.setPassword(enPassword);
//				this.updateUserToUserCenter(user, true);
			}
			
			sysUserService.updPwd(user.getUserId(), pwd);
		} catch (Exception e) {
			MessageUtil.addMsg(e.getMessage());
			throw new Exception(e);
		}
	}
	
	/**
	 * 通过用户中心用户ID更新用户信息
	 * @param sysUser
	 */
	public void updateByUcUserid(SysUser sysUser){
		dao.updateByUcUserid(sysUser);
	}
	
	/**
	 * 获取唯一账号：
	 *   1.当账号在系统中存在时，新生成的账号在原始账号后追加递增序列数，递增序列从1开始。
	 *   2.当index为0时，新账号就是原始账号，无需追加序列数
	 * @param account 原始账号
	 * @param index 账号自增序列
	 * @param email
	 * @return
	 */
	private SysUser getUniqueAccount(String account, int index, String email){
		String tmpAccount = account;
		if(index > 0){
			tmpAccount = account + index;
		}
		SysUser sysUser = sysUserService.getByAccount(tmpAccount);
		// 账号在系统中不存在时，需要创建该账号，所以返回账号
		if(null == sysUser){
			sysUser = new SysUser();
			sysUser.setAccount(tmpAccount);
			sysUser.setAdd(true);
		}else{
			// 账号在系统中存在，并且邮箱相同，说明用户信息已经在系统中存在不需要再创建
			if(email.equals(sysUser.getEmail())){
				sysUser.setAdd(false);
			}
			// 账号在系统中存在，但邮箱与系统中的邮箱不相同，则需要重新获取账号
			else{
				index++;
				sysUser = getUniqueAccount(account, index, email);
			}
		}
		return sysUser;
	}
	
	/** 
	 * 组装用户的用户企业关系
	 * @param enterpriseCodes
	 * @param userId
	 * @return
	 */
	private String genEnterpriseCodeJsonString(Map<String, String> enterpriseCodes, Long userId){
		List<Map<String, Object>> codeList = new ArrayList<Map<String, Object>>();
		Map<String, Object> code = null;
		for(Map.Entry<String, String> entry : enterpriseCodes.entrySet()){
			code = new HashMap<String, Object>();
			// 集团编码
			code.put("parentEnterpriseCode", entry.getValue());
			// 企业编码
			code.put("inenterpriseCode", entry.getKey());
			// 根据企业编码查询用户在该企业下的职务类别
			Long jobCategory = jobService.getMaxCategoryByUidAndEcode(userId, entry.getKey());
			if(jobCategory == null){
				jobCategory = Long.valueOf(AppConfigUtil.get(Constants.DEFAULT_JOB_CATEGORY, "30"));
			}
			code.put("role", jobCategory);
			codeList.add(code);
		}
		return JSONArray.fromObject(codeList).toString().replaceAll("[\\[\\]]", "");
	}
	
	/** 
	 * 组装用户的用户企业关系
	 * @param userId
	 * @param enterpriseCode
	 * @param groupCode
	 * @param jobCategory
	 * @return
	 */
	private String genEnterpriseCodeJsonString(Long userId, String enterpriseCode, String groupCode, Long jobCategory){
		List<Map<String, Object>> codeList = new ArrayList<Map<String, Object>>();
		Map<String, Object> code = new HashMap<String, Object>();
		// 集团编码
		code.put("parentEnterpriseCode", groupCode);
		// 企业编码
		code.put("inenterpriseCode", enterpriseCode);
		// 根据企业编码查询用户在该企业下的职务类别
		Long jobCategoryTmp = jobService.getMaxCategoryByUidAndEcode(userId, enterpriseCode);
		if(jobCategoryTmp != null && jobCategoryTmp < jobCategory){
			jobCategory = jobCategoryTmp;
		}
		code.put("role", jobCategory);
		codeList.add(code);
		return JSONArray.fromObject(codeList).toString().replaceAll("[\\[\\]]", "");
	}

	/**
	 * 根据行政等级获取用户信息
	 * @return
	 */
	public List<SysUser> getListByLevel(QueryFilter filter){
		return dao.getListBySqlKey("getListByLevel",filter);
	}
	
	/** 
	 * 更新用户的同步信息
	 * @param sysUser
	 */
	public void updateSyncInfo(SysUser sysUser){
		dao.updateSyncInfo(sysUser);
		EventUtil.publishUserEvent(sysUser, UserEvent.ACTION_UPD, false);
	}
}
