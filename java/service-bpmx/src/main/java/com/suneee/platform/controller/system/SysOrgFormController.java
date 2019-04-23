package com.suneee.platform.controller.system;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.service.system.SysOrgExtService;

/**
 * 对象功能:组织架构 控制器类 开发公司:广州宏天软件有限公司 开发人员:pkq 创建时间:2011-12-02 10:41:53
 */
@Controller
@RequestMapping("/platform/system/sysOrg/")
@Action(ownermodel=SysAuditModelType.USER_MANAGEMENT)
public class SysOrgFormController extends BaseFormController {
	@Resource
	private SysOrgService sysOrgService;
	@Resource
	private SysOrgExtService sysOrgExtService;

	/**
	 * 添加或更新组织架构。
	 * 
	 * @param request
	 * @param response
	 * @param sysOrg
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新组织架构",detail="<#if isAdd>添加<#else>更新</#if>组织【${SysAuditLinkService.getSysOrgLink(Long.valueOf(orgId))}】")
	public void save(HttpServletRequest request, HttpServletResponse response, SysOrg sysOrg, BindingResult bindResult) throws Exception {
		ResultMessage resultMessage = validForm("sysOrg", sysOrg, bindResult, request);
		if (resultMessage.getResult() == ResultMessage.Fail) {
			writeResultMessage(response.getWriter(), resultMessage);
			return;
		}
		//判断编码存在。
//		Integer rtn=sysOrgService.getCountByCode(sysOrg.getCode(), sysOrg.getOrgId());
//		if(rtn>0){
//			writeResultMessage(response.getWriter(), "组织代码:"+sysOrg.getCode()+"已被使用,请检查!", ResultMessage.Fail);
//			return;
//		}
		
		Long supOrgId= RequestUtil.getLong(request, "orgSupId");
		SysOrg supOrg = sysOrgService.getById(supOrgId);
		String orgPathName = null;
		if(supOrg == null){
			orgPathName = Constants.SEPARATOR_BACK_SLANT + sysOrg.getOrgName();
		}else{
			orgPathName = supOrg.getOrgPathname() + Constants.SEPARATOR_BACK_SLANT + sysOrg.getOrgName();
		}
		
		// 根据组织路径名称查询组织信息
		SysOrg orgDb = sysOrgExtService.getByOrgPathName(orgPathName, sysOrg.getDemId(), sysOrg.getOrgId());
		if(orgDb != null){
			writeResultMessage(response.getWriter(), "该组织已经存在，请勿重复创建！", ResultMessage.Fail);
			return;
		}
		
		boolean isAdd=false;
		Long orgId = sysOrg.getOrgId();
		if(BeanUtils.isZeroEmpty(orgId)){
			isAdd = true;
			orgId = UniqueIdUtil.genId();
		}
		
		// 若以维度为父节点新建，则设置其Path为维度ID+该组织ID
		if (supOrg == null) {
			sysOrg.setPath(supOrgId + "." + orgId + ".");
		} else {
			sysOrg.setPath(supOrg.getPath() + orgId + ".");
		}
		sysOrg.setOrgPathname(orgPathName);
		sysOrg.setDepth(sysOrg.getOrgPathname().split(Constants.SEPARATOR_BACK_SLANT).length - 2);
		
		String code = sysOrgExtService.generateCode(sysOrg.getOrgCode(), sysOrg.getOrgName(), sysOrg.getOrgId());
		sysOrg.setCode(code);
		if(isAdd){
			sysOrg.setOrgId(orgId);
			sysOrg.setCreatorId(ContextUtil.getCurrentUserId());
			sysOrgService.addOrg(sysOrg);
			writeResultMessage(response.getWriter(), "{\"orgId\":\""+ orgId +"\",\"action\":\"add\"}", ResultMessage.Success);
		}else{
			sysOrg.setUpdateId(ContextUtil.getCurrentUserId());
			sysOrgService.updOrg(sysOrg);
			this.upSysOrgBySupId(sysOrg.getOrgId(),sysOrg.getPath(),sysOrg.getOrgPathname());
			writeResultMessage(response.getWriter(), "{\"orgId\":\""+ sysOrg.getOrgId() +"\",\"action\":\"upd\"}", ResultMessage.Success);
		}
		
//		if (BeanUtils.isZeroEmpty(sysOrg.getOrgId())) {
////			Long orgId=UniqueIdUtil.genId();
//			
//			// 若以维度为父节点新建，则设置其Path为维度ID+该组织ID
//			if (supOrg == null) {
//				sysOrg.setPath(supOrgId + "." + orgId +".");
//				sysOrg.setOrgPathname("/" +sysOrg.getOrgName());
//			} else {
//				sysOrg.setPath(supOrg.getPath() + sysOrg.getOrgId() +".");
//				sysOrg.setOrgPathname(supOrg.getOrgPathname() +"/" + sysOrg.getOrgName());
//			}
//			sysOrgService.addOrg(sysOrg);
//			
//			writeResultMessage(response.getWriter(), "{\"orgId\":\""+ orgId +"\",\"action\":\"add\"}", ResultMessage.Success);
//		} else {
//			isAdd=false;
//			// 若以维度为父节点新建，则设置其Path为维度ID+该组织ID
//			if (supOrg == null) {
//				sysOrg.setPath(supOrgId + "." + sysOrg.getOrgId() +".");
//				sysOrg.setOrgPathname("/" +sysOrg.getOrgName());
//			} else {
//				sysOrg.setPath(supOrg.getPath() + sysOrg.getOrgId() +".");
//				sysOrg.setOrgPathname(supOrg.getOrgPathname() +"/" + sysOrg.getOrgName());
//			}
//			sysOrg.setUpdateId(ContextUtil.getCurrentUserId());
////			String pathName=getOrgPathName(sysOrg.getPath(),sysOrg.getOrgName());
////			sysOrg.setOrgPathname(pathName);
//			sysOrgService.updOrg(sysOrg);
//			this.upSysOrgBySupId(sysOrg.getOrgId(),sysOrg.getPath(),sysOrg.getOrgPathname());
//			writeResultMessage(response.getWriter(), "{\"orgId\":\""+ sysOrg.getOrgId() +"\",\"action\":\"upd\"}", ResultMessage.Success);
//		}
		SysAuditThreadLocalHolder.putParamerter("isAdd", isAdd);
		SysAuditThreadLocalHolder.putParamerter("orgId",sysOrg.getOrgId().toString());
	}
	
	/**
	 * 更新其下子组织的路径
	 * 
	 */
	private void upSysOrgBySupId(long orgSupId,String path,String supPathName) throws Exception{
		//根据id查找子组织
		List<SysOrg> sysOrgs=sysOrgService.getOrgByOrgSupId(orgSupId);
		if (sysOrgs!=null && sysOrgs.size()!=0) {
			SysOrg sysOrg=null;
			for (int i = 0; i < sysOrgs.size(); i++) {
				sysOrg=sysOrgs.get(i);
				String pathName= supPathName+"/"+sysOrg.getOrgName();
				sysOrg.setOrgPathname(pathName);
				sysOrg.setPath(path+sysOrg.getOrgId()+".");
				sysOrg.setDepth(pathName.split(Constants.SEPARATOR_BACK_SLANT).length - 2);
				sysOrgService.updOrg(sysOrg);
				//递归遍历是否存在子组织，存在就继续修改，不存在就结束
				this.upSysOrgBySupId(sysOrg.getOrgId(),sysOrg.getPath(),pathName);
			}
		}
	}
	
	private String getOrgPathName(String orgPath,String orgName){
		orgPath= StringUtil.trimSufffix(orgPath, ".");
		String[] aryPath=orgPath.split("\\.");
		String pathName="";
		for(int i=1;i<aryPath.length-1;i++){
			SysOrg sysOrg=sysOrgService.getById(new Long(aryPath[i]));
			pathName+="/" + sysOrg.getOrgName();
		}
		pathName+="/" + orgName;
		return pathName;
	}

	/**
	 * 在实体对象进行封装前，从对应源获取原实体
	 * 
	 * @param orgId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ModelAttribute
	protected SysOrg getFormObject(@RequestParam("orgId") Long orgId, Model model) throws Exception {
		logger.debug("enter SysOrg getFormObject here....");
		SysOrg sysOrg = null;
		if (orgId != null) {
			sysOrg = sysOrgService.getById(orgId);
		} else {
			sysOrg = new SysOrg();
		}
		return sysOrg;
	}

}
