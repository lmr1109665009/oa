package com.suneee.ucp.base.controller.system;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.ucp.base.common.ResultConst;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;

import net.sf.json.JSONObject;
@Controller
@RequestMapping("/platform/system/sysUserFind/")
public class SysUserFindController  extends UcpBaseController{
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private UserPositionService userPositionService;
	
	@RequestMapping("getUserByOrg")
	@ResponseBody
	@Action(description="根据组织编码获取用户列表")
	public ResultVo getUserByOrg(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String orgCode = RequestUtil.getString(request, "orgCode");
		if(orgCode==null){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"请求参数错误");
		}
		SysOrg org = sysOrgService.getByCode(orgCode);
		if(org==null){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"该代码的组织不存在");
		}
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("path", org.getPath());		
		try {
			List<SysUser> users = sysUserService.getByOrgPath(filter);
			for(SysUser user : users){
				List<UserPosition> userPos = userPositionService.getByUserId(user.getUserId());
				if(userPos == null || userPos.isEmpty()){
					user.setPosName("");
				}else{
					user.setPosName(userPos.get(0).getPosName());
				}
			}
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"根据组织编码获取用户列表成功",users);
		} catch (Exception e) {
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"根据组织编码获取用户列表失败");
		}
	}
	
	@RequestMapping("getOrgByOrgSupId")
	@ResponseBody
	@Action(description="根据父组织ID获取子组织列表")
	public ResultVo getOrgByOrgSupId(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Long orgSupId = RequestUtil.getLong(request, "orgSupId");
		if(orgSupId == 0){
			orgSupId = 1L;
		}
		try {
			List<SysOrg> sysOrgList = sysOrgService.getOrgByOrgSupId(orgSupId);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"根据父组织ID获取子组织列表成功",sysOrgList);
		} catch (Exception e) {
		    return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "根据父组织ID获取子组织列表失败");
		}
	}
}
