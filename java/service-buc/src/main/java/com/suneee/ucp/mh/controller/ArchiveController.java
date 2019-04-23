package com.suneee.ucp.mh.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.Job;
import com.suneee.platform.model.system.JobParam;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.mh.model.Archive;
import com.suneee.ucp.mh.service.ArchiveService;

@Controller
@RequestMapping("/mh/archive/")
public class ArchiveController extends UcpBaseController{
	@Resource
	private ArchiveService archiveService;

	/**
	 * 获取档案分页列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<Archive> archiveList = archiveService.getArchiveList(new QueryFilter(request, "archiveTable"));
		ModelAndView mv = this.getAutoView();
		return mv.addObject("archiveList", archiveList);
	}
	
	/**
	 * 编辑或新增页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("edit")
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long archiveId=RequestUtil.getLong(request, "archive_id");
		Archive archive=archiveService.getById(archiveId);		
		return getAutoView().addObject("archive",archive);
	}
	
	/**
	 * 添加或更新档案表。
	 * @param request
	 * @param response
	 * @param job 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新档案")
	public void save(HttpServletRequest request, HttpServletResponse response,Archive archive) throws Exception
	{
		String resultMsg=null;		
		try{
			boolean isExist=false;
			archiveService.save(archive);
			resultMsg="添加职务成功";
				
			if (isExist) {
				writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Fail);
			}else{
				writeResultMessage(response.getWriter(),resultMsg,ResultMessage.Success);
			}
		}
		catch(DuplicateKeyException ex){
			writeResultMessage(response.getWriter(),"该职务代码已存在.",ResultMessage.Fail);
		}
		catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	/**
	 * 取得职务表明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看职务表明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long archiveId=RequestUtil.getLong(request,"archive_id");
		Archive archive = archiveService.getById(archiveId);	
		return getAutoView().addObject("archive", archive);
	}
}
