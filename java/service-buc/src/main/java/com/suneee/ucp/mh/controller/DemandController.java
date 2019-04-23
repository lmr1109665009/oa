package com.suneee.ucp.mh.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.mh.model.Archive;
import com.suneee.ucp.mh.model.Demand;
import com.suneee.ucp.mh.model.Talent;
import com.suneee.ucp.mh.service.DemandService;

@Controller
@RequestMapping("/mh/demand/")
public class DemandController extends UcpBaseController{
	@Resource
	private DemandService demandService;

	/**
	 * 获取档案分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<Demand> archiveList = demandService.getArchiveList(new QueryFilter(request, "demandTable"));
		ModelAndView mv = this.getAutoView();
		return mv.addObject("demandList", archiveList);
	}
	
	/**
	 * 获取档案分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("edit")
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		/*List<Demand> archiveList = demandService.getArchiveList(new QueryFilter(request, "demandTable"));
		ModelAndView mv = this.getAutoView();
		return mv.addObject("demandList", archiveList);*/
		
		ModelAndView mv =new ModelAndView();
		mv.setViewName("/mh/demandedit");
		return mv;
	}
	/**
	 * 获取档案分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception{
		/*List<Demand> archiveList = demandService.getArchiveList(new QueryFilter(request, "demandTable"));
		ModelAndView mv = this.getAutoView();
		return mv.addObject("demandList", archiveList);*/
		
		ModelAndView mv =new ModelAndView();
		mv.setViewName("/mh/demandGet");
		return mv;
	}
	
	/**
	 * 获取招聘筛选列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("filtratelist")
	public ModelAndView filtratelist(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mv =new ModelAndView();
		mv.setViewName("/mh/filtrateList");
		return mv;
	}
	
	/**
	 *筛选编辑
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("filtrateedit")
	public ModelAndView filtrateedit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mv =new ModelAndView();
		mv.setViewName("/mh/filtrateedit");
		return mv;
	}
	
	/**
	 * 人才库列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("talentlist")
	public ModelAndView talentlist(HttpServletRequest request, HttpServletResponse response) throws Exception{
	/*	ModelAndView mv =new ModelAndView();
		mv.setViewName("/mh/talentList");
		return mv;*/
		
		List<Talent> talentList = new ArrayList<Talent>();
	
		Talent talent=new Talent();
		talent.setName("张三");
		talent.setPhone("18000000000");
		talentList.add(talent);
		
		ModelAndView mv = this.getAutoView();
		mv.setViewName("/mh/talentList");
		return mv.addObject("talentList", talentList);
	}
	
	/**
	 *筛选编辑
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("talentedit")
	public ModelAndView talentedit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mv =new ModelAndView();
		mv.setViewName("/mh/talentedit");
		return mv;
	}
	
	/**
	 * 人才库列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("employlist")
	public ModelAndView employlist(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mv =new ModelAndView();
		mv.setViewName("/mh/employList");
		return mv;
	}
	
	/**
	 *筛选编辑
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("employedit")
	public ModelAndView employedit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mv =new ModelAndView();
		mv.setViewName("/mh/employedit");
		return mv;
	}
}
