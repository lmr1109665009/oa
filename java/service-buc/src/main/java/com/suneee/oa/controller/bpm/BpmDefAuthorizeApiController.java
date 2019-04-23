package com.suneee.oa.controller.bpm;

import com.suneee.core.util.StringUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.controller.mobile.MobileBaseController;
import com.suneee.platform.model.bpm.BpmDefAuthorize;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.BpmDefAuthorizeService;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;




/**
 * <pre>
 * 对象功能:流程授权接口控制器类
 * 开发公司:深圳象翌
 * 开发人员:子华
 * 创建时间:2018-01-11 10:56:13
 * </pre>
 */
@Controller
@RequestMapping("/api/bpm/bpmDefAuthorize/")
@Action(ownermodel=SysAuditModelType.PROCESS_MANAGEMENT)
public class BpmDefAuthorizeApiController extends MobileBaseController
{
	@Resource
	private BpmDefAuthorizeService bpmDefAuthorizeService;
	/**
	 * 取得流程定义权限列表
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	@Action(description="查看流程分管权限分页列表")
	public ResultVo list(HttpServletRequest request) throws Exception {
		QueryFilter filter = new QueryFilter(request, true);
		String flag=RequestUtil.getString(request,"flag");
		String keyword=RequestUtil.getString(request,"keyword");
		if ("multi".equals(flag)&& StringUtil.isNotEmpty(keyword)){
			if(keyword.equals("所有")||keyword.equals("所有人")||keyword.equals("所有用户")){
				filter.addFilter("ownerName","all");
			}
			filter.addFilter("keyword","%"+keyword+"%");
		}else {
			String ownerName= (String) filter.getFilters().get("ownerName");
			if(ownerName!=null&&(ownerName.equals("%所有%")||ownerName.equals("%所有人%")||ownerName.equals("%所有用户%"))){
				filter.addFilter("keyword","all");
			}
		}
		filter.addFilter("enterpriseCode", CookieUitl.getCurrentEnterpriseCode());
		List<BpmDefAuthorize> list = bpmDefAuthorizeService.getAuthorizeListByFilter(filter);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取流程数据列表成功",getPageList(list,filter));
	}

	/**
	 * 修改授权信息
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Action(description="修改授权信息")
	public ResultVo edit(HttpServletRequest request) throws Exception {
		Long id= RequestUtil.getLong(request,"id");
		BpmDefAuthorize bpmDefAuthorize = null;
		if(id==0){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"授权信息不存在");
		}
		bpmDefAuthorize = bpmDefAuthorizeService.getAuthorizeById(id);
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取授权信息成功",bpmDefAuthorize);
	}

    /**
     * 删除流程分管授权信息
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("del")
    @ResponseBody
    @Action(description="删除流程分管授权信息")
    public ResultVo del(HttpServletRequest request) throws Exception {
		ResultVo message=null;
        try{
            Long[] lAryId =RequestUtil.getLongAryByStr(request, "id");
            bpmDefAuthorizeService.deleteAuthorizeByIds(lAryId);
            message=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除成功！");
        }catch(Exception ex){
            message=new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败,请联系管理员！" + ex.getMessage());
        }
        return message;
    }

    /**
     * 获得流程分管授权详情
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("get")
    @ResponseBody
    @Action(description="获得流程分管授权详情")
    public ResultVo get(HttpServletRequest request) throws Exception {
        Long id=RequestUtil.getLong(request,"id");
        BpmDefAuthorize bpmDefAuthorize = null;
        if(id!=0){
            bpmDefAuthorize = bpmDefAuthorizeService.getAuthorizeById(id);
        }else{
            bpmDefAuthorize =new BpmDefAuthorize();
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获得流程分管授权详情",bpmDefAuthorize);
    }
}
