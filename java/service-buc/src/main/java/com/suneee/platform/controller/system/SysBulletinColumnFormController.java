package com.suneee.platform.controller.system;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysBulletin;
import com.suneee.platform.model.system.SysBulletinColumn;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysBulletinColumnService;
import com.suneee.platform.service.system.SysBulletinService;
import com.suneee.platform.service.system.SysOrgService;

/**
 * 对象功能:公告栏目 控制器类
 */
@Controller
@RequestMapping("/platform/system/sysBulletinColumn/")
public class SysBulletinColumnFormController extends BaseController {
	@Resource
	private SysBulletinColumnService sysBulletinColumnService;
	
	
	/**
	 * 添加或更新公告栏目。
	 * 
	 * @param request
	 * @param response
	 * @param column
	 *            添加或更新的实体
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新公告栏目")
	public void save(HttpServletRequest request, HttpServletResponse response,
			SysBulletinColumn column) throws Exception {
		String resultMsg = null;
		try {
			int rtn=sysBulletinColumnService. getAmountByAlias(column);
			if(rtn>0){
				writeResultMessage(response.getWriter(),"别名已经存在!" , ResultMessage.Fail);
				return;
			}
			if (column.getId() == null) {
				initData(column);
				sysBulletinColumnService.add(column);
				resultMsg = getText("添加成功", "公告栏目");
			}else{
				sysBulletinColumnService.update(column);
				resultMsg = getText("更新成功", "公告栏目");
			}
			writeResultMessage(response.getWriter(),resultMsg + "", ResultMessage.Success);
		}catch(DuplicateKeyException ex){
			writeResultMessage(response.getWriter(),"该栏目别名已存在.",ResultMessage.Fail);
		} 
		catch (Exception e) {
			e.printStackTrace();
			writeResultMessage(response.getWriter(),
					resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}

	/**
	 * 设置保存的数据
	 * @param request
	 * @param column
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private void initData(SysBulletinColumn column) throws Exception {
		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
		column.setId(UniqueIdUtil.genId());
		column.setCreatetime(new Date());
		column.setCreator(sysUser.getFullname());
		column.setCreatorid(sysUser.getUserId());
		boolean isSuperAdmin=ContextUtil.isSuperAdmin();
		Long companyId=ContextUtil.getCurrentCompanyId();
		if(!isSuperAdmin && companyId>0){
			column.setTenantid(companyId);
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
    protected SysBulletinColumn getFormObject(@RequestParam("id") Long id,Model model) throws Exception {
		logger.debug("enter SysBulletin getFormObject here....");
		SysBulletinColumn column=null;
		if(id!=null){
			column=sysBulletinColumnService.getById(id);
		}else{
			column= new SysBulletinColumn();
		}
		return column;
    }
}