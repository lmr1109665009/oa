package com.suneee.ucp.me.controller;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.me.model.DocType;
import com.suneee.ucp.me.service.DocTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: DocTypeController
 * @Description: 文档目录管理控制器
 * @author 游刃
 * @date 2017年4月12日 下午8:22:45
 *
 */
@Controller
@RequestMapping("/me/docType/")
@Action(ownermodel = SysAuditModelType.XINGZHENG_MANAGEMENT)
public class DocTypeFormController extends UcpBaseController {
	
	@Resource
	private DocTypeService docTypeService;
	@Resource
	private SysOrgService orgService;
	
	@RequestMapping(value = "save")
	@Action(description = "保存或更新文档目录", detail = "保存或更新文档目录")
	public void save(HttpServletRequest request, HttpServletResponse response, DocType docType) throws IOException {
		String resultMsg = "";
		boolean isFront =  RequestUtil.getBoolean(request, "isFront");
		Map<String,Object> params=new HashMap<String, Object>();
		try {
			SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
			SysOrg sysOrg = orgService.getByUserId(user.getUserId());
			String eid = sysOrg.getOrgCode();
			if(eid!=null){
				params.put("eid",eid);
				params.put("parentId",docType.getParentId());
				params.put("name",docType.getTypeName());
				params.put("privateFlag",0);
				if (docType.getDepartmentId()!=null){
					params.put("departmentId",docType.getDepartmentId());
				}
			if (null == docType.getId()||0 == docType.getId()) {// 新增
				// 设置创建者
				docType.setPromulgator(user.getUserId());
				docType.setPromulgatorName(user.getFullname());
				docType.setId(UniqueIdUtil.genId());
				docType.setIsPrivate(0);
				docType.setEid(sysOrg.getOrgCode());
				if(isFront){
					docType.setIsPrivate(1);
					docType.setOwner(user.getUserId());
					params.put("privateFlag",1);
					params.put("owner",user.getUserId());
				}
				docTypeService.renameDirtory(docType,params);
				docTypeService.add(docType);
				resultMsg = "添加文档目录成功!";

			} else {// 更新
				params.put("id",docType.getId());
				docTypeService.renameDirtory(docType,params);
				docTypeService.update(docType);
				resultMsg = "更新文档目录成功!";
			}
			if(isFront){
				addMessage(ResultMessage.Success, resultMsg, "", response);
				return;
			 }
			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
			}else{
				resultMsg="您没有设置组织，请设置后再操作";
				writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Fail);
			}			
		} catch (Exception e) {
			String str = MessageUtil.getMessage();
			ResultMessage resultMessage;
			String message;
			if (StringUtil.isNotEmpty(str)) {
				message="操作文档目录失败:" + str;
			} else {
				message = ExceptionUtil.getExceptionMessage(e);
			}
			if(isFront){
				addMessage(ResultMessage.Fail, resultMsg, "", response);
				return;
			}
			resultMessage = new ResultMessage(ResultMessage.Fail, message);
			response.getWriter().print(resultMessage);
		}
	}
	
	/**
	 * 在实体对象进行封装前，从对应源获取原实体
	 * @param acceptId
	 * @param model
	 * @return
	 * @throws Exception
	 */
    @ModelAttribute
    protected DocType getFormObject(@RequestParam(value="id",required=false) Long id,Model model) throws Exception {
		logger.debug("enter SysBulletin getFormObject here....");
		DocType docType=null;
		if(id!=null){
			docType=docTypeService.getById(id);
		}else{
			docType= new DocType();
		}
		return docType;
    }
	
}
