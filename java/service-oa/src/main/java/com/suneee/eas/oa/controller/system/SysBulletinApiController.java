package com.suneee.eas.oa.controller.system;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.*;
import com.suneee.eas.oa.model.system.SysBulletin;
import com.suneee.eas.oa.service.system.SysBulletinService;
import com.suneee.eas.oa.service.user.UserService;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 *<pre>
 * 对象功能:通知公告表 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2018-8-22 10:44:00
 *</pre>
 */
@RestController
@RequestMapping(ModuleConstant.SYSTEM_MODULE + FunctionConstant.BULLETIN)
public class SysBulletinApiController {
	private static final Logger LOGGER = LogManager.getLogger(SysBulletinApiController.class);

	@Autowired
	private SysBulletinService sysBulletinService;
	@Autowired
	private UserService userService;

	@RequestMapping("save")
	public ResponseMessage save(HttpServletRequest request, HttpServletResponse response,
								SysBulletin sysBulletin) {
		String resultMsg = null;
		try {
			if(sysBulletin.getStatus() == 1){
				sysBulletin.setPublishTime(new Date());
			}
			SysUser sysUser = ContextSupportUtil.getCurrentUser();
			//获取当前用户的企业编码
			String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
			sysBulletin.setEnterpriseCode(enterpriseCode);
			if (sysBulletin.getId() == null) {
				sysBulletin.setCreatorId(sysUser.getUserId());
				sysBulletin.setCreator(ContextSupportUtil.getCurrentUsername());
				sysBulletin.setId(IdGeneratorUtil.getNextId());
				sysBulletin.setCreateTime(new Date());
				sysBulletinService.save(sysBulletin);
				if(StringUtil.isNotEmpty(sysBulletin.getPublishRangeID_org())){
					//添加数据到公告组织中间表
					sysBulletinService.addToBulletinOrg(sysBulletin.getId(),sysBulletin.getPublishRangeID_org());
				}
				resultMsg = "公告新增成功！";
			} else {
				sysBulletinService.update(sysBulletin);
				if(StringUtil.isNotEmpty(sysBulletin.getPublishRangeID_org())){
					//先删除之前公告对应数据，再添加更新后的数据
					sysBulletinService.dellFromBulletinOrgByBulletin(sysBulletin.getId());
					sysBulletinService.addToBulletinOrg(sysBulletin.getId(),sysBulletin.getPublishRangeID_org());
				}
				resultMsg = "公告更新成功！";
			}
			return ResponseMessage.success(resultMsg);
		} catch (Exception e) {
			if(sysBulletin.getId() == null){
				resultMsg = "公告新增失败！";
			}else {
				resultMsg = "公告更新失败！";
			}
			e.printStackTrace();
			LOGGER.error(resultMsg+e.getMessage());
			return ResponseMessage.fail(resultMsg);
		}
	}

	/**
	 * 列表数据(有关我的公告)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("listAboutMe")
	public ResponseMessage listAboutMe(HttpServletRequest request, HttpServletResponse response) {
		Boolean isSuperAdmin = ContextSupportUtil.isSuperAdmin();
		Long userId = ContextSupportUtil.getCurrentUserId();
		//获取当前用户的企业编码
		String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
		QueryFilter filter = new QueryFilter("getAllBulletin",request);
		filter.addFilter("enterpriseCode", enterpriseCode);
		try {
			if(!isSuperAdmin){
				List<SysOrg> orgs = userService.getOrgListByUserId(userId);
				String orgPaths = "";
				for(SysOrg org:orgs){
					if(enterpriseCode.equals(org.getOrgCode())){
						orgPaths += org.getPath();
					}
				}
				filter.addFilter("orgPaths",orgPaths);
				filter.addFilter("userId",userId);
			}else{
				filter.addFilter("isSuperAdmin",true);
			}
			Pager<SysBulletin> page = sysBulletinService.getPageBySqlKey(filter);
			return ResponseMessage.success("获取公告表分页列表数据成功！",page);
		}catch (Exception ex){
			LOGGER.error("获取公告表分页列表数据失败:"+ex.getMessage());
			return ResponseMessage.fail("获取公告表分页列表数据失败!");
		}
	}

	/**
	 * 列表数据(我创建的的公告)
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("listCreateByMe")
	public ResponseMessage listCreateByMe(HttpServletRequest request, HttpServletResponse response) {
		Boolean isSuperAdmin = ContextSupportUtil.isSuperAdmin();
		Long userId = ContextSupportUtil.getCurrentUserId();
		//获取当前用户的企业编码
		String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
		QueryFilter filter = new QueryFilter("listCreateByMe",request);
		try {
			if(!isSuperAdmin){
				filter.addFilter("creatorId", userId);
				filter.addFilter("enterpriseCode", enterpriseCode);
			}
			Pager<SysBulletin> page = sysBulletinService.getPageBySqlKey(filter);
			return ResponseMessage.success("获取我创建的公告表分页列表数据成功！",page);
		}catch (Exception ex){
			LOGGER.error("获取我创建的公告表分页列表数据失败："+ex.getMessage());
			return ResponseMessage.fail("获取我创建的公告表分页列表数据失败！");
		}
	}

	/**
	 * 删除公告表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	public ResponseMessage del(HttpServletRequest request, HttpServletResponse response) {
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			if(BeanUtils.isEmpty(lAryId)){
				return ResponseMessage.fail("删除公告表失败,参数不能为空！");
			}
			sysBulletinService.delByIds(lAryId);
			//删除中间表对应数据
			for(Long id:lAryId){
				sysBulletinService.dellFromBulletinOrgByBulletin(id);
			}
			return ResponseMessage.success("删除公告表成功!");
		} catch (Exception ex) {
			LOGGER.error("删除公告表失败："+ex.getMessage());
			return ResponseMessage.fail("删除公告表失败！");
		}
	}

	/**
	 * 编辑公告
	 * 
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("edit")
	public ResponseMessage edit(HttpServletRequest request) {
		Long id = RequestUtil.getLong(request, "id");
		try {
			SysBulletin sysBulletin = sysBulletinService.findById(id);
			if(sysBulletin == null){
				return ResponseMessage.fail("公告不存在！");
			}
			//如果发布人员范围不为空，则返回对应人员信息（方便前端人员选择器回填人员）
			List<SysUser> userList = new ArrayList<>();
			if(StringUtil.isNotEmpty(sysBulletin.getPublishRangeID_user())){
				String[] userIds = sysBulletin.getPublishRangeID_user().split(",");
				List<Long> userIdList = new ArrayList<>();
				for(String userId:userIds){
					userIdList.add(Long.valueOf(userId));
				}
				userList = userService.batchFindUser(userIdList);
			}
			//如果发布组织范围不为空，则返回对应组织信息（方便前端人员选择器回填组织）
			List<SysOrg> orgList = new ArrayList<>();
			if(StringUtil.isNotEmpty(sysBulletin.getPublishRangeID_org())){
				String[] orgIds = sysBulletin.getPublishRangeID_org().split(",");
				List<Long> orgIdList = new ArrayList<>();
				for(String orgId:orgIds){
					orgIdList.add(Long.valueOf(orgId));
				}
				orgList = userService.batchFindOrg(orgIdList);
			}
			Map<String,Object> resultMap = new HashMap<>();
			resultMap.put("sysBulletin",sysBulletin);
			resultMap.put("userList",userList);
			resultMap.put("orgList",orgList);
			return ResponseMessage.success("获取公告信息成功！",resultMap);
		}catch (Exception ex){
			LOGGER.error("获取公告信息失败！"+ex.getMessage());
			return ResponseMessage.fail("获取公告信息失败！");
		}
	}

	/**
	 * 取得公告表明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	public ResponseMessage get(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtil.getLong(request, "id");
		try {
			SysBulletin sysBulletin = sysBulletinService.findById(id);
			return ResponseMessage.success("获取公告明细成功！",sysBulletin);
		}catch (Exception ex){
			LOGGER.error("获取公告明细失败："+ex.getMessage());
			return ResponseMessage.fail("获取公告明细失败！");
		}
	}
}