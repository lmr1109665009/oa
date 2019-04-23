package com.suneee.ucp.me.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.me.model.DocType;
import com.suneee.ucp.me.model.FileTankVo;
import com.suneee.ucp.me.service.DocTypeService;
import com.suneee.ucp.me.service.DocumentService;
import com.suneee.ucp.me.service.FileTankVoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class DocTypeController extends UcpBaseController {
	@Resource
	private DocTypeService docTypeService;

	@Resource
	private DocumentService documentService;

	@Resource
	private FileTankVoService fileTankVoService;
	
	@Resource
	private UserPositionService uerPositionService;
	
	@Resource
	private SysOrgService orgService;

	@RequestMapping("list")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<DocType> list = docTypeService.getSuperS();
		ModelAndView mv = this.getAutoView().addObject("docTypeList", list);

		return mv;
	}

	@RequestMapping("list2")
	public ModelAndView list2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter queryFilter = new QueryFilter(request, "docTypeItem");
		List<DocType> list = docTypeService.getAll(queryFilter);

		ModelAndView mv = this.getAutoView().addObject("docTypeList", list);

		return mv;
	}

	@RequestMapping("getTreeData")
	@ResponseBody
	public List<DocType> getTreeData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<DocType> docTypeList = null;
		Long demId = RequestUtil.getLong(request, "demId", 0);
		long orgId = RequestUtil.getLong(request, "id", 0);
		Long curUserId = ContextUtil.getCurrentUserId();
		SysOrg sysOrg = orgService.getByUserId(curUserId);
	    String eid = sysOrg.getOrgCode();
		if (orgId == 0) {
			docTypeList = docTypeService.getDocByParentId(demId);
			// 不显示个人文件柜
			if (1 == demId) {
				for (DocType docType : docTypeList) {
					if (4 == docType.getId()) {
						docTypeList.remove(docType);
						break;
					}
				}
			}
			DocType docType = docTypeService.getById(demId);
			docType.setIsRoot((short) 1);
			docTypeList.add(docType);

		} else {
			Long depmentId = null;
			if(orgId == 2 ){//如果是部门目录
				//获取当前用户所在的部门id			
			    List<UserPosition> userPositionList =  uerPositionService.getByUserId(curUserId);
				if(userPositionList!=null && userPositionList.size()>0){ 
					depmentId = userPositionList.get(0).getOrgId();
				}
				//depmentId=null为超级管理员，可查看所有部门文件柜
				if(depmentId!=null){
				//获取文件目录，过滤没有权限的目录
				docTypeList = docTypeService.getDocByParentIdAndDepartmentIdAndEid(orgId, depmentId,eid);
				}else if(eid==null){
				docTypeList = docTypeService.getDocByParentId(orgId);	
				}else{
				docTypeList = docTypeService.getDocByParentIdAndEid(orgId,eid);	
				}
			}else if(orgId==3){
				docTypeList = docTypeService.getDocByParentIdAndEid(orgId,eid);
			}else{
				docTypeList = docTypeService.getDocByParentId(orgId);
			}
		}

		return docTypeList;
	}

	@RequestMapping("frontList")
	public void frontList(HttpServletRequest request, HttpServletResponse response) {
		JSONObject data = new JSONObject();
		String message;
		try {
			List<DocType> list = docTypeService.getAllName(null);
			data.put("varList", list);
			message = "查询成功";
			addMessage(ResultMessage.Success, message, "", data, response);
		} catch (Exception e) {
			message = "查询出错";
			addMessage(ResultMessage.Fail, message, "", response);
		}
	}

	@RequestMapping("newFrontList")
	public void newFrontList(HttpServletRequest request, HttpServletResponse response) {
		JSONObject data = new JSONObject();
		String message;
		try {
			Long id = RequestUtil.getLong(request, "id");

			// 是否个人文件柜
			boolean isPrivate = RequestUtil.getBoolean(request, "isPrivate");
			Long userId = null;
			if (isPrivate) {
				userId = ContextUtil.getCurrentUserId();				
			}
			
			Long depmentId = null;
			//目录
			List<FileTankVo> results = null;
			// 文件
			List<FileTankVo> results2 = new ArrayList<FileTankVo>();
			//获取当前用户所在的部门id
			Long curUserId = ContextUtil.getCurrentUserId();
			SysOrg sysOrg = orgService.getByUserId(curUserId);
		    String eid = sysOrg.getOrgCode();
			if(id == 2 ){				
				List<UserPosition> userPositionList =  uerPositionService.getByUserId(curUserId);
				if(userPositionList!=null && userPositionList.size()>0){
					depmentId = userPositionList.get(0).getOrgId();
				}
				if(depmentId!=null){
				results = fileTankVoService.getVoByParentIdAndDepartmentIdAndEid(id, userId, depmentId,eid);
				}else{
				results = fileTankVoService.getVoByParentIdAndEid(id,eid);	
				}
			}else{
				// 目录
				results = fileTankVoService.getVoByParentId(id, userId, depmentId,eid);
				//文件
				results2 = fileTankVoService.getDocument(id, userId,eid);
				//FileTankVo doc = results2.get(0);
				//doc.setDocumentIPath(null);
			}

			// 如果直接查询1，不显示个人文件柜
			if (1 == id) {
				for (FileTankVo fileTankVo : results) {
					if ("4".equals(fileTankVo.getId())) {
						results.remove(fileTankVo);
						break;
					}
				}
			}
			
			results.addAll(results2);
			data.put("varList", results);
			message = "查询成功";
			addMessage(ResultMessage.Success, message, "", data, response);
		} catch (Exception e) {
			message = "查询出错";
			addMessage(ResultMessage.Fail, message, "", response);
		}
	}

	@RequestMapping("get")
	public void get(HttpServletRequest request, HttpServletResponse response) {
		JSONObject data = new JSONObject();
		String message;
		Long id = RequestUtil.getLong(request, "id");
		try {
			DocType aDocType = docTypeService.getById(id);
			data.put("aDocType", aDocType);
			message = "查询成功";
			addMessage(ResultMessage.Success, message, "", data, response);
		} catch (Exception e) {
			message = "查询出错";
			addMessage(ResultMessage.Fail, message, "", response);
		}
	}

	@RequestMapping("edit")
	@Action(description = "编辑文档目录")
	public ModelAndView edit(HttpServletRequest request) throws Exception {

		Long id = RequestUtil.getLong(request, "id");
		String isnew = RequestUtil.getString(request, "isnew");
		String returnUrl = RequestUtil.getPrePage(request);

		ModelAndView mv = getAutoView().addObject("returnUrl", returnUrl);
		// 上级文件夹名称
		DocType now = docTypeService.getById(id);
		String parentName = now.getTypeName();
		Long parentId = now.getId();
		if (null == isnew || !"1".equals(isnew)) {
			// 编辑，获取原信息
			DocType superNow = docTypeService.getById(now.getParentId());
			parentName = superNow.getTypeName();
			parentId = superNow.getId();
			mv.addObject("id", id);
			mv.addObject("typeName", now.getTypeName());
		}
		mv.addObject("parentId", parentId);
		mv.addObject("parentName", parentName);
		return mv;
	}

	/**
	 * 删除文档目录
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除文档目录")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String message;
		try {
			Long id = RequestUtil.getLong(request, "id");
			// 如果是父节点，则要求先删除子节点
			if (0 < docTypeService.getChildNumber(id)) {
				message = "删除失败,请先删除其子节点";
				addMessage(ResultMessage.Fail, message, "", response);
			} else {
				docTypeService.delById(id);

				message = "删除文档目录成功!";
				addMessage(ResultMessage.Success, message, "", response);
			}

		} catch (Exception ex) {
			message = "删除失败,请检查该目录下是否有文档";
			addMessage(ResultMessage.Fail, message, "", response);
		}
	}

	/**
	 * 前端删除文档目录
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("frontDel")
	@Action(description = "前端删除文档目录")
	public void frontDel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StringBuffer message = new StringBuffer();
		try {
			// 0目录，1文件
			String ds = RequestUtil.getString(request, "ds");
			JSONArray jsonArray = JSONArray.parseArray(ds);
			JSONObject jsonOne;
			Map<String, String> map = null;
			for (int i = 0; i < jsonArray.size(); i++) {
				map = new HashMap<String, String>();
				jsonOne = jsonArray.getJSONObject(i);
				Long id = (Long) jsonOne.get("id");
				// 文件还是目录
				if (1==(int)jsonOne.get("type")) {
					documentService.delById(id);
				} else {
					String failName = docTypeService.getById(id).getTypeName();
					if(!docTypeService.delAllchilds(id)){
						message.append("删除‘"+failName+"’的子文件夹或文件失败!");
					}
				}
			}

			if (message.length()==0) {
				message.append("删除成功");
				addMessage(ResultMessage.Success, message.toString(), "", response);
			}else{
				addMessage(ResultMessage.Fail, message.toString(), "", response);
			}
			
		} catch (Exception ex) {
			addMessage(ResultMessage.Fail, "删除失败,请与管理员联系", "", response);
		}
	}

	/**
	 * 查询所有目录
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("search")
	public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QueryFilter filter = new QueryFilter(request, "docTypeItem");
		filter.getPageBean().setPagesize(10);
		List<DocType> docTypeList = docTypeService.getAll(filter);
		ModelAndView mv = this.getAutoView().addObject("docTypeList", docTypeList);

		return mv;
	}

	/**
	 * 取得空目录明细，防止js冲突
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getEmpty")
	public ModelAndView getEmpty(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getAutoView();
	}
}
