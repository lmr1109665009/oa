/**  
 * @Title: OfficeObjectTypeController.java
 * @Package com.suneee.ucp.me.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @author yiwei
 * @date 2017年5月5日
 * @version V1.0  
 */
package com.suneee.ucp.me.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.api.util.PropertyUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.system.SysPropertyConstants;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.me.model.OfficeObjectType;
import com.suneee.ucp.me.service.OfficeObjectTypeService;
import com.suneee.ucp.me.utils.CharacterUtil;

/**
 * @ClassName: OfficeObjectTypeController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author yiwei
 * @date 2017年5月5日
 *
 */
@Controller
@RequestMapping("/me/officeObjectType/")
public class OfficeObjectTypeController extends UcpBaseController{
	@Resource
	private OfficeObjectTypeService typeService;
	
	/**
	 * 类目列表
	 * @Title: list
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ModelAndView    返回类型
	 * @throws
	 */
	@RequestMapping("list")
	public ModelAndView list(HttpServletRequest request) throws Exception{//列表
		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT, false);
		QueryFilter queryFilter = new QueryFilter(request, "officeObjectTypeItem");
		List<OfficeObjectType> list = typeService.getAll(queryFilter);
		List<OfficeObjectType> typeList = typeService.getTypeList();
		
		ModelAndView mv = this.getAutoView().addObject("officeObjectTypeList", list).addObject("isSupportWeixin",
				isSupportWeixin);
		
		mv.addObject("typeList", typeList);
		
		return mv;
	}
	
	/**
	 * 类目的增加和更新
	 * @Title: save
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @param officeObjectType
	 * @param @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
//	@RequestMapping("save")
//	public void save(HttpServletRequest request, HttpServletResponse response, OfficeObjectType officeObjectType) throws Exception{//新增和更新
//		String resultMsg = "";
//		try {
//			SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
//			
//			if(officeObjectType.getId()>0){
//				
//				officeObjectType.setUpdateBy(user.getUserId());
//				typeService.add(officeObjectType);
//				resultMsg = "类目更新成功";
//			}else{
//				//先去查找有没有相同的
//				List<OfficeObjectType> typeList = typeService.queryType(officeObjectType);
//				if(typeList!=null && typeList.size()>0){//更新
//					resultMsg = "类目已存在";
//					writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Fail);
//				}else{//增加
//					officeObjectType.setId(UniqueIdUtil.genId());
//					officeObjectType.setCreator(user.getUsername());
//					officeObjectType.setCreateBy(user.getUserId());
//					resultMsg = "类目添加成功";
//				}
//			}
//			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
//		} catch (Exception e) {
//			
//			String str = MessageUtil.getMessage();
//			if (StringUtil.isNotEmpty(str)) {
//				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, "操作失败:" + str);
//				response.getWriter().print(resultMessage);
//			} else {
//				String message = ExceptionUtil.getExceptionMessage(e);
//				ResultMessage resultMessage = new ResultMessage(ResultMessage.Fail, message);
//				response.getWriter().print(resultMessage);
//			}
//		}
//	}
	
	/**
	 * 创建类目或编辑类目
	 * @Title: save
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @param officeObjectType
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ResultVo    返回类型
	 * @throws
	 */
	@RequestMapping("save")
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response, OfficeObjectType officeObjectType) throws Exception{//新增和更新
		ResultVo result = new ResultVo();
		String resultMsg = "";
		try {
			SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
			
			if(officeObjectType.getId()!=null && officeObjectType.getId()>0){
				officeObjectType.setUpdateBy(user.getUserId());
				typeService.update(officeObjectType);
				resultMsg = "类目更新成功";
				result.setSuccess();
			}else {
				List<OfficeObjectType> typeList = typeService.queryType(officeObjectType);
				if(typeList!=null && typeList.size()>0){
					OfficeObjectType oldData =  typeList.get(0);
					String oldSpecification = oldData.getSpecification();
					String[] sArr = oldSpecification.split("_");
					String curSpecification = officeObjectType.getSpecification();
					for(int i=0; i<sArr.length; i++){
						if(!curSpecification.contains(sArr[i])){
							curSpecification = curSpecification +"_"+sArr[i];
						}
					}
					
					oldData.setSpecification(curSpecification);
					oldData.setUpdateBy(user.getUserId());
					typeService.update(oldData);
					resultMsg = "类目添加成功";
					result.setSuccess();
				}else{
					//add
					officeObjectType.setId(UniqueIdUtil.genId());
					officeObjectType.setCreator(user.getUsername());
					officeObjectType.setCreateBy(user.getUserId());
					typeService.add(officeObjectType);
					resultMsg = "类目添加成功";
					result.setSuccess();
				}
			}
			result.setMessage(resultMsg);
			return result;
		} catch (Exception e) {
			String str = MessageUtil.getMessage();
			if(StringUtil.isEmail(str)){
				str = "出现异常";
			}
			result.setMessage(str);
			result.setFaile();
			return result;
		}
	}
	
	/**
	 * 删除类目
	 * @Title: delete
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("delete")
	public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {//类目列表
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] ids = RequestUtil.getLongAryByStr(request, "id");
			if (0 < typeService.deleteAll(ids)) {
				message = new ResultMessage(ResultMessage.Success, "删除成功!");
			}
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 获取类目类型
	 * @Title: typeList
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ResultVo    返回类型
	 * @throws
	 */
	@RequestMapping("typeList")
	@ResponseBody
	public ResultVo typeList()throws Exception{//类目列表
		ResultVo result = new ResultVo();
		
		try {
			List<OfficeObjectType> typeList = typeService.getTypeList();
			List<String> types = new ArrayList<String>();
			if(typeList!=null && typeList.size()>0){
				for(OfficeObjectType item : typeList){
					types.add(item.getType());
				}
			}
			result.setData(types);

			result.setSuccess();
			result.setMessage("获取类目成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.setFaile();
			result.setMessage("获取类目失败");
		}
		return result;
	}
	
	/**
	 * 获取物品
	 * @Title: nameList
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param type
	 * @param @return    参数
	 * @return ResultVo    返回类型
	 * @throws
	 */
	@RequestMapping("nameList")
	@ResponseBody
	public ResultVo nameList(String type){//类目下物品列表
		
		ResultVo result = new ResultVo();
		try {
			type =  CharacterUtil.toChinese(type);
			List<OfficeObjectType> nameList = typeService.getNameList(type);
			List<String> names = new ArrayList<String>();
			if(nameList!=null && nameList.size()>0){
				for(OfficeObjectType item : nameList){
					names.add(item.getObjectName());
				}
			}
			result.setData(names);
			result.setSuccess();
			result.setMessage("获取类目成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.setFaile();
			result.setMessage("获取类目失败");
		}
		return result;
	}
	
	/**
	 * 获取规格列表
	 * @Title: specificationList
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param officeObjectType
	 * @param @return    参数
	 * @return ResultVo    返回类型
	 * @throws
	 */
	@RequestMapping("specificationList")
	@ResponseBody
	public ResultVo specificationList(OfficeObjectType officeObjectType){//规格列表
		
		ResultVo result = new ResultVo();
		try {
			List<OfficeObjectType> specificationList = typeService.getSpecificationList(officeObjectType);
			List<String> specifications = new ArrayList<String>();
			if(specificationList!=null && specificationList.size()>0){
				String specification = specificationList.get(0).getSpecification();
				String[] specificationArr = specification.split("_");
				specifications = Arrays.asList(specificationArr);
			}
			result.setData(specifications);
			result.setSuccess();
			result.setMessage("获取规格成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.setFaile();
			result.setMessage("获取规格失败");
		}
		return result;
	}
	
	/**
	 * 跳转到类目入库
	 * @Title: add
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param id
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ModelAndView    返回类型
	 * @throws
	 */
	@RequestMapping("add")
	public ModelAndView add(HttpServletRequest request, Long id) throws Exception{
		return this.getAutoView();
	}

	@RequestMapping("edit")
	public ModelAndView edit(HttpServletRequest request, Long id) throws Exception{
		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT, false);

		OfficeObjectType type = typeService.getById(id);
		List<String> specificationList = OfficeObjectType.getSpecificationList(type.getSpecification());
		ModelAndView mv = this.getAutoView().addObject("officeObjectType", type).addObject("isSupportWeixin",
				isSupportWeixin);
		mv.addObject("specificationList", specificationList);
		return mv;
		
	}
}
