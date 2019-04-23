package com.suneee.ucp.mh.controller;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.kaoqin.model.kaoqin.VacationRemain;
import com.suneee.kaoqin.service.kaoqin.VacationRemainService;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.controller.UcpBaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 假期结余
 *  
 * @author harvey
 * @time 2017年5月10日 下午1:31:01
 *
 */
@Controller
@RequestMapping("/mh/vacationLeft/")
public class VacationLeftController extends UcpBaseController {
	
	@Resource
	private VacationRemainService remainService;
	
	
	/**
	 * 假期结余页面
	 *  
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();
		SysUser currentUser = (SysUser) ContextUtil.getCurrentUser();
		// 获取假期结余列表
		List<VacationRemain> remains = remainService.getUserRemainDetail(currentUser.getUserId());
		for (VacationRemain remain : remains) {
			if (remain.getRemained() == null) {
				remain.setRemained(0L);
			}
		}
		
		mv.addObject("vacationRemainList", remains);
		return mv;
	}
}
