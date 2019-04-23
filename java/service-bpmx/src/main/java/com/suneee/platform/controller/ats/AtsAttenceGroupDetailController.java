package com.suneee.platform.controller.ats;

import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.ats.AtsAttenceGroupDetail;
import com.suneee.platform.service.ats.AtsAttenceGroupDetailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 *<pre>
 * 对象功能:考勤组明细 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-26 10:07:59
 *</pre>
 */
@Controller
@RequestMapping("/platform/ats/atsAttenceGroupDetail/")
public class AtsAttenceGroupDetailController extends BaseController
{
	@Resource
	private AtsAttenceGroupDetailService atsAttenceGroupDetailService;
	
	
	/**
	 * 添加或更新考勤组明细。
	 * @param request
	 * @param response
	 * @param atsAttenceGroupDetail 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description="添加或更新考勤组明细")
	public void save(HttpServletRequest request, HttpServletResponse response,AtsAttenceGroupDetail atsAttenceGroupDetail) throws Exception
	{
		String resultMsg=null;		
		try{
			if(atsAttenceGroupDetail.getId()==null||atsAttenceGroupDetail.getId()==0){
				resultMsg=getText("添加","考勤组明细");
			}else{
				resultMsg=getText("更新","考勤组明细");
			}
		    atsAttenceGroupDetailService.save(atsAttenceGroupDetail);
			writeResultMessage(response.getWriter(),resultMsg, ResultMessage.Success);
		}catch(Exception e){
			writeResultMessage(response.getWriter(),resultMsg+","+e.getMessage(),ResultMessage.Fail);
		}
	}
	
	
	/**
	 * 取得考勤组明细分页列表
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description="查看考勤组明细分页列表")
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception
	{	
		List<AtsAttenceGroupDetail> list=atsAttenceGroupDetailService.getAll(new QueryFilter(request,"atsAttenceGroupDetailItem"));
		return this.getAutoView().addObject("atsAttenceGroupDetailList",list);
	}
	
	/**
	 * 删除考勤组明细
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description="删除考勤组明细")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String preUrl= RequestUtil.getPrePage(request);
		ResultMessage message=null;
		try{
			Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
			atsAttenceGroupDetailService.delByIds(lAryId);
			message=new ResultMessage(ResultMessage.Success, "删除考勤组明细成功!");
		}catch(Exception ex){
			message=new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 	编辑考勤组明细
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description="编辑考勤组明细")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id",0L);
		String returnUrl=RequestUtil.getPrePage(request);
		AtsAttenceGroupDetail atsAttenceGroupDetail=atsAttenceGroupDetailService.getById(id);
		
		return getAutoView().addObject("atsAttenceGroupDetail",atsAttenceGroupDetail)
							.addObject("returnUrl",returnUrl);
	}

	/**
	 * 取得考勤组明细明细
	 * @param request   
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description="查看考勤组明细明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Long id=RequestUtil.getLong(request,"id");
		AtsAttenceGroupDetail atsAttenceGroupDetail = atsAttenceGroupDetailService.getById(id);	
		return getAutoView().addObject("atsAttenceGroupDetail", atsAttenceGroupDetail);
	}
	
}

