package com.suneee.oa.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.oa.service.user.SysOrgExtendService;
import com.suneee.oa.service.user.SysUserExtendService;
import com.suneee.oa.service.user.UserPositionExtendService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.SysUserOrgService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.ucp.base.event.def.UserPositionEvent;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/system/sysUserOrg/")
@Action(ownermodel= SysAuditModelType.USER_MANAGEMENT)
public class SysUserOrgApiController extends BaseController {

    @Resource
    private SysUserOrgService sysUserOrgService;

    @Resource
    private SysOrgService sysOrgService;
    @Resource
    private UserPositionService userPositionService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysOrgExtendService sysOrgExtendService;
    @Resource
    private SysUserExtendService sysUserExtendService;
    @Resource
    private UserPositionExtendService userPositionExtendService;

    /**
     * 设置是主管。
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("setIsCharge")
    @Action(description="设置是主管",execOrder= ActionExecOrder.AFTER,
            detail="<#assign entity=SysAuditLinkService.getByUserPosId(Long.valueOf(userPosId))/>" +
                    "设置人员：【${entity.userName}】,为组织【${entity.orgName}】的主管  <#if isSuccess>成功<#else>失败</#if>")
    public void setIsCharge(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        Long userPosId=RequestUtil.getLong(request, "userPosId",0);
        ResultMessage message=null;
        Short isCharge = RequestUtil.getShort(request,"isCharge");
        boolean issuccess;
        try{
            sysUserOrgService.setIsCharge(userPosId);
            issuccess=true;
            if(isCharge==1 ){
                message=new ResultMessage(isCharge, "设置非负责人成功");
            }else{
                message=new ResultMessage(isCharge, "设置负责人成功");
            }
        }
        catch(Exception ex){
            message=new ResultMessage(ResultMessage.Fail, "设置失败");
            issuccess=false;
        }
        SysAuditThreadLocalHolder.putParamerter("isSuccess", issuccess);
        writeResultMessage(response.getWriter(),message);
    }

    /**
     * 设置是否主岗位。
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("setIsPrimary")
    @Action(description="设置主岗位",execOrder=ActionExecOrder.AFTER,
            detail="<#assign entity=SysAuditLinkService.getByUserPosId(Long.valueOf(userPosId))/>" +
                    "设置人员：【${entity.userName}】的主岗位为组织【${entity.orgName}】 <#if isSuccess>成功<#else>失败</#if>")
    public void setIsPrimary(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        Long userPosId=RequestUtil.getLong(request, "userPosId",0);
        ResultMessage message=null;
        Short isPrimary = RequestUtil.getShort(request,"isPrimary");
        boolean issuccess ;
        try{
            sysUserOrgService.setIsPrimary(userPosId);
            issuccess=true;
            if(isPrimary==1 ){
                if(sysUserOrgService.getUserCount(userPosId)==true){
                    message=new ResultMessage(isPrimary, "设置非主职务成功");
                }else{
                    message=new ResultMessage(isPrimary, "该用户只有一个职务，不能将该职务设置为非主职务");
                }

            }else{
                message=new ResultMessage(isPrimary, "设置主职务成功");
            }
        }
        catch(Exception ex){
            issuccess=false;
            message=new ResultMessage(ResultMessage.Fail, "设置失败");
        }
        SysAuditThreadLocalHolder.putParamerter("isSuccess", issuccess);
        writeResultMessage(response.getWriter(),message);
    }


    /**
     * 删除用户所属组织或部门
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("del")
    @ResponseBody
    @Action(description = "删除用户所属组织或部门", detail="<#if userPosId??><#list StringUtils.split(userPosId,\",\") as item>将用户"
    		+ "<#assign userPos=userPositionService.getById(Long.valueOf(item))/><#if userPos!=''>"
    		+ "【${SysAuditLinkService.getSysUserLink(Long.valueOf(userPos.userId))}】</#if>从组织<#if userPos!=''>"
    		+ "【${SysAuditLinkService.getSysOrgLink(Long.valueOf(userPos.orgId))}】</#if>下移除,</#list><#else>将用户从组织下移除</#if>",
    		execOrder=ActionExecOrder.BEFORE)
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long[] lAryId = RequestUtil.getLongAryByStr(request, "userPosId");
        sysUserOrgService.delByIds(lAryId);
        List<UserPosition> userPosList = new ArrayList<UserPosition>();
        for (Long userPosId : lAryId) {
            userPosList.add(sysUserOrgService.getById(userPosId));
        }
        try {
            EventUtil.publishUserPositionEvent(UserPositionEvent.ACTION_DEL, userPosList);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除用户所属组织或部门成功");
        } catch (Exception e) {
            logger.error("删除用户所属组织或部门失败", e.getMessage(), e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除用户所属组织或部门失败");
        }
    }


    /**
     * 取得组织下的所有用户
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("userList")
    @Action(description = "取得员工列表", detail = "取得员工列表")
    @ResponseBody
	public ResultVo userList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	QueryFilter queryFilter = new QueryFilter(request, true);
    	Long orgId = RequestUtil.getLong(request, "orgId");
    	// 组织ID不为空时，则根据组织ID查询组织及其子组织下的员工信息
        if(orgId != 0){
        	 SysOrg sysOrg = sysOrgService.getById(orgId);
        	 if(sysOrg == null){
        		 logger.error("获取员工信息失败：选择的组织【" + orgId + "】在系统中不存在！");
        		 return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取员工信息失败：选择的组织在系统中不存在！");
        	 }
        	 queryFilter.addFilter("path", sysOrg.getPath() + "%");
        	 queryFilter.addFilter("orgId", null);
        }

        // 获取当前登录用户当前所属的企业下的员工
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        queryFilter.addFilter("enterpriseCode", enterpriseCode);
        PageList<UserPosition> list = (PageList<UserPosition>)userPositionService.getAll(queryFilter);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取员工信息成功",PageUtil.getPageVo(list));
    }
	
    /** 
     * 根据账号查询用户的岗位信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getUserPosition")
    @ResponseBody
    public ResultVo getUserPosition(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	Long userId = 0L;
    	String account = RequestUtil.getString(request, "account");
    	try {
			// 账号参数为空时，默认查询当前登录用户的岗位信息
			if(StringUtils.isBlank(account)){
				userId = ContextUtil.getCurrentUserId();
			}
			// 账号参数不为空时，查询账号对应用户的岗位信息
			else {
				SysUser user = sysUserService.getByAccount(account);
				if(user == null){
					logger.error("获取用户岗位信息失败：账号【" + account + "】不存在");
					return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户岗位信息失败：账号【" + account + "】不存在！");
				}
				userId = user.getUserId();
			}
			
			// 查询用户岗位信息
			List<Map<String, Object>> list = this.userPositionExtendService.getPositonByUserId(userId);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户岗位信息成功！", list);
		} catch (Exception e) {
			logger.error("获取用户岗位信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户岗位信息失败：" + e.getMessage());
		}
    }

    @RequestMapping("getUserListByIds")
    @ResponseBody
    public ResultVo getUserListByIds(HttpServletRequest request){
        try {
            String params = RequestUtil.getMessage(request);
            if(StringUtil.isNotEmpty(params)){
               JSONObject obj = JSONObject.parseObject(params);
               String idsString = obj.getString("ids");
               if(StringUtil.isNotEmpty(idsString)){
                   String[] userIds = idsString.split(",");
                   Long[] ids = (Long[])ConvertUtils.convert(userIds, Long.class);
                   List<SysUser> users = sysUserExtendService.getUserListByIds(ids);
                   return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户信息成功！", users);
               }
            }
        } catch (Exception e) {
            logger.error("获取用户信息失败：" + e.getMessage(), e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户信息失败：" + e.getMessage());
        }
        return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取用户信息失败");
    }

    @RequestMapping("getOrgListByUserId")
    @ResponseBody
    public ResultVo getOrgListByUserId(HttpServletRequest request){
        try {
            String params = RequestUtil.getMessage(request);
            if(StringUtil.isNotEmpty(params)){
                JSONObject obj = JSONObject.parseObject(params);
                Long userId = obj.getLong("userId");
                String enterpriseCode = obj.getString("enterpriseCode");
                if(StringUtil.isEmpty(enterpriseCode)){
                    enterpriseCode = com.suneee.eas.common.utils.ContextSupportUtil.getCurrentEnterpriseCode();
                }
                if(null == userId || StringUtil.isEmpty(enterpriseCode)){
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织信息失败：userId或enterpriseCode为空！");
                }
                List<SysOrg> sysOrgs = sysOrgExtendService.getOrgListByUserId(userId, enterpriseCode);
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取组织信息成功！", sysOrgs);
            }
        } catch (Exception e) {
            logger.error("获取用户信息失败：" + e.getMessage(), e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织信息失败：" + e.getMessage());
        }
        return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织信息失败");
    }

	/**
	 * 根据orgId获取组织信息
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("getOrgListByIds")
	@ResponseBody
	public ResultVo getOrgListByIds(HttpServletRequest request) {
		try {
			String params = RequestUtil.getMessage(request);
			if (StringUtil.isNotEmpty(params)) {
				JSONObject obj = JSONObject.parseObject(params);
				String idsString = obj.getString("ids");
				if (StringUtil.isNotEmpty(idsString)) {
					List<SysOrg> orgList = sysOrgService.getByOrgIds(idsString);
					return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取组织信息成功!", orgList);
				}
			}
		}catch(Exception ex){
			logger.error("获取组织信息失败：" + ex.getMessage());
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织信息失败！");
		}
		return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取组织信息失败！");
	}
}
