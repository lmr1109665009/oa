/**
 * @Title: EnterpriseinfoController.java 
 * @Package com.suneee.oa.controller.user 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.oa.controller.user;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.eas.common.utils.ContextSupportUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.model.system.Enterpriseinfo;
import com.suneee.ucp.base.vo.ResultVo;

/**
 * @ClassName: EnterpriseinfoController 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-05-08 16:01:09 
 *
 */
@Controller
@RequestMapping("/oa/user/enterpriseinfo/")
public class EnterpriseinfoController extends UcpBaseController{
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	
	/** 
	 * 获取用户所属企业信息列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getByUserId")
	@ResponseBody
	public ResultVo getByUserId(HttpServletRequest request, HttpServletResponse response)throws Exception{
		Long userId = RequestUtil.getLong(request, "userId",null);
		if(userId == null){
			userId = ContextSupportUtil.getCurrentUserId();
		}
		try {
			List<Enterpriseinfo> enterpriseinfoList = enterpriseinfoService.getByUserId(userId);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户所属企业信息成功", enterpriseinfoList);
		} catch (Exception e) {
			logger.error("获取用户所属企业信息失败：" + e.getMessage(), e);
			return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户所属企业信息失败：" + e.getMessage());
		}
	}

	/**
	 * 根据企业编码获取企业信息
	 * @param enterpriseCode
	 * @return
	 */
	@RequestMapping("/getByCompCode")
	@ResponseBody
	public Enterpriseinfo getByCompCode(@RequestParam String enterpriseCode){
		return enterpriseinfoService.getByCompCode(enterpriseCode);
	}
}
