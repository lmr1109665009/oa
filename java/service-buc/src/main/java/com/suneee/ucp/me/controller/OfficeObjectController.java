package com.suneee.ucp.me.controller;

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
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.SysPropertyConstants;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.me.model.OfficeObject;
import com.suneee.ucp.me.model.OfficeObjectStorageRecord;
import com.suneee.ucp.me.model.OfficeObjectType;
import com.suneee.ucp.me.service.OfficeObjectService;
import com.suneee.ucp.me.service.OfficeObjectStorageRecordService;
import com.suneee.ucp.me.service.OfficeObjectTypeService;


@Controller
@RequestMapping("/me/officeObjectStorage/")
public class OfficeObjectController extends UcpBaseController{
	
	@Resource
	private OfficeObjectService service;
	
	@Resource
	private OfficeObjectTypeService typeService;
	
	@Resource
	private OfficeObjectStorageRecordService recordService;
	
	@Resource
	private DictionaryService dictionaryService;
	
	/**
	 * 入库物品列表
	 * @Title: list
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ModelAndView    返回类型
	 * @throws
	 */
	@RequestMapping("list")
	public ModelAndView list(HttpServletRequest request) throws Exception{
		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT, false);
		QueryFilter queryFilter = new QueryFilter(request, "officeObjectItem");
		List<OfficeObject> list = service.getAll(queryFilter);
		ModelAndView mv = this.getAutoView().addObject("officeObjectList", list).addObject("isSupportWeixin",
				isSupportWeixin);
		List<OfficeObject> typeList = service.getTypeList();
		List<OfficeObject> areaList = service.getAreaList();
		
		mv.addObject("typeList", typeList);
		mv.addObject("areaList", areaList);
		return mv;
	}
	
	/**
	 * 入库或编辑入库
	 * @Title: save
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @param officeObject
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ResultVo    返回类型
	 * @throws
	 */
	@RequestMapping("save")
	@ResponseBody
	public ResultVo save(HttpServletRequest request, HttpServletResponse response, OfficeObject officeObject) throws Exception{
		ResultVo result = new ResultVo();
		String resultMsg = "";
		try {
			SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
			if(officeObject!=null && (officeObject.getId()==null || officeObject.getId()==0)){//入库操作
				Long storageId = UniqueIdUtil.genId();
				//查询数据库中是否有该入库，如果有则合并只修改库存
				List<OfficeObject> objectList = service.query(officeObject);
				if(objectList!=null && objectList.size()>0){
					OfficeObject object = objectList.get(0);
					int oldStore = object.getStore();
					int curStor = oldStore + officeObject.getStore();
					object.setStore(curStor);
					object.setUpdateBy(user.getUserId());
					//object.setUpdatetime(updatetime);
					service.update(object);
					storageId  = object.getId();
				}else{
					//增加
					officeObject.setId(storageId);
					officeObject.setCreator(user.getUsername());
					officeObject.setCreateBy(user.getUserId());
					//officeObject.setCreatetime(new Date());
					service.add(officeObject);
				}
				
				//增加入库记录
				OfficeObjectStorageRecord record = new OfficeObjectStorageRecord();
				record.setId(UniqueIdUtil.genId());
				record.setStorageId(storageId);
				record.setAction("0");//增加库存
				record.setNumber(""+officeObject.getStore());
				record.setCreateBy(user.getUserId());
				record.setCreator(user.getUsername());
				record.setType(officeObject.getType());
				record.setObjectName(officeObject.getObjectName());
				recordService.add(record);
				
				resultMsg = "添加成功!";
				result.setSuccess();
			}else{//编辑操作
				
				officeObject.setUpdateBy(user.getUserId());
				//officeObject.setUpdatetime(new Date());
				service.update(officeObject);
				//增加修改库存记录
				OfficeObjectStorageRecord record = new OfficeObjectStorageRecord();
				record.setId(UniqueIdUtil.genId());
				record.setStorageId(officeObject.getId());
				record.setAction("1");//修改库存
				record.setNumber(""+officeObject.getStore());
				record.setCreateBy(user.getUserId());
				record.setCreator(user.getUsername());
				record.setType(officeObject.getType());
				record.setObjectName(officeObject.getObjectName());
				recordService.add(record);
				
				resultMsg = "更新成功!";
				result.setSuccess();
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
	
	@RequestMapping("storageRecordList")
	@ResponseBody
	public ResultVo storageRecordList(OfficeObjectStorageRecord record){
		ResultVo result = new ResultVo();
		String resultMsg = "";
		try {
			List<OfficeObjectStorageRecord> recordList = recordService.getRecordList(record);
			resultMsg = "获取成功";
			result.setData(recordList);
		} catch (Exception e) {
			resultMsg = "获取异常";
			result.setFaile();
		}
		result.setMessage(resultMsg);
		return result;
	}
	
	/**
	 * 
	 * @Title: del
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("del")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] ids = RequestUtil.getLongAryByStr(request, "id");
			if (0 < service.deleteAll(ids)) {
				message = new ResultMessage(ResultMessage.Success, "删除成功!");
			}
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}
	
	/**
	 * 详情
	 * @Title: detail
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ModelAndView    返回类型
	 * @throws
	 */
	@RequestMapping("detail")
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		Long id = RequestUtil.getLong(request, "id");
		String returnUrl = RequestUtil.getPrePage(request);
		ModelAndView mv = getAutoView().addObject("returnUrl", returnUrl);
		if (0 != id) {
			// 编辑，获取原信息
			OfficeObject officeObject = service.getById(id);
			mv.addObject("officeObject",officeObject);
			
			QueryFilter queryFilter = new QueryFilter(request, "storageRecordList");
			List<OfficeObjectStorageRecord> recordList = recordService.getAll(queryFilter);
			mv.addObject("recordList",recordList);
		}

		return mv;
	}
	
	/**
	 * 调整到创建入库界面
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
		ModelAndView mv = this.getAutoView();
		List<OfficeObjectType> typeList = typeService.getTypeList();
		//List<OfficeObject> areaList = service.getAreaList();
		List<Dictionary> areaList = dictionaryService.getByNodeKey("dq");
		mv.addObject("typeList", typeList);
		mv.addObject("areaList", areaList);
		return mv;
	}
	
	/**
	 * 调整到编辑界面
	 * @Title: edit
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param id
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ModelAndView    返回类型
	 * @throws
	 */
	@RequestMapping("edit")
	public ModelAndView edit(HttpServletRequest request, Long id) throws Exception{
		
		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT, false);
		OfficeObject data = service.getById(id);
		ModelAndView mv = this.getAutoView().addObject("officeObject", data).addObject("isSupportWeixin",
				isSupportWeixin);
		return mv;
	}
	
	
	
	
	//===================================
	
	/**
	 * 前端 办公用品列表接口
	 * @Title: flist
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response    参数
	 * @return void    返回类型
	 * @throws
	 */
//	@RequestMapping("flist")
//	public void flist(HttpServletRequest request, HttpServletResponse response){
//		
//		String message;
//		try {
//			QueryFilter queryFilter = new QueryFilter(request, "officeObjectItem");
//			List<OfficeObject> list = service.getAll(queryFilter);
//			message = "查询成功";
//			addMessage(ResultMessage.Success, message, "", list, response);
//		} catch (Exception e) {
//			message = "查询出错";
//			addMessage(ResultMessage.Fail, message, "", response);
//		}
//	}
	
	/**
	 * 前端 获取办公用品类型列表
	 * @Title: ftypeList
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return    参数
	 * @return ResultVo    返回类型
	 * @throws
	 */
//	@RequestMapping("ftypeList")
//	@ResponseBody
//	public ResultVo ftypeList(){
//		
//		ResultVo result = new ResultVo();
//		try {
//			List<OfficeObjectType> typeList = typeService.getAll();
//			result.setData(typeList);
//			result.setSuccess();
//			result.setMessage("获取办公用品类型成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.setFaile();
//			result.setMessage("获取办公用品类型失败");
//		}
//		
//		return result;
//	}
	
//	@RequestMapping("saveType")
//	public void saveType(HttpServletRequest request, HttpServletResponse response, OfficeObjectType type) throws Exception{
//		
//		String resultMsg = "";
//		try {
//				SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
//				
//				if(type!=null && (type.getId()==null) || type.getId() == 0 ){
//					type.setCreateBy(user.getUserId());
//					type.setCreator(user.getUsername());
//					type.setId(UniqueIdUtil.genId());
//					typeService.add(type);
//				}else{
//					type.setUpdateBy(user.getUserId());
//					typeService.update(type);
//				}
//				resultMsg = "添加成功!";
//			
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
	
//	@RequestMapping("delType")
//	@ResponseBody
//	public ResultVo delType(HttpServletRequest request){
//		
//		ResultVo result = new ResultVo();
//		try {
//			//RequestUtil.getLongAryByStr(request, "id");
//			long id = RequestUtil.getLong(request, "id");
//			typeService.delById(id);
//			result.setSuccess();
//			result.setMessage("删除成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.setFaile();
//			result.setMessage("删除失败");
//		}
//		
//		return result;
//	}
	
	/**
	 * 后台 办公用品列表接口
	 * @Title: list
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ModelAndView    返回类型
	 * @throws
	 */
//	@RequestMapping("list")
//	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception{
//		
//		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT, false);
//		QueryFilter queryFilter = new QueryFilter(request, "officeObjectItem");
//		List<OfficeObject> list = service.getAll(queryFilter);
//
//		ModelAndView mv = this.getAutoView().addObject("officeObjectList", list).addObject("isSupportWeixin",
//				isSupportWeixin);
//		
//		List<OfficeObjectType> typeList = typeService.getAll();
//		mv.addObject("typeList", typeList);
//		return mv;
//		
//	}
	
	/**
	 * 获取信息，跳转至新增或编辑页面
	 * @Title: edit
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ModelAndView    返回类型
	 * @throws
	 */
//	@RequestMapping("edit")
//	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception{
//		
//		Long id = RequestUtil.getLong(request, "id");
//		String returnUrl = RequestUtil.getPrePage(request);
//		ModelAndView mv = getAutoView().addObject("returnUrl", returnUrl);
//		if (0 != id) {
//			// 编辑，获取原信息
//			OfficeObject officeObject = service.getById(id);
//			mv.addObject("officeObject",officeObject);
//		}
//
//		// 所有目录
//		List<OfficeObjectType> types = typeService.getAll();
//		mv.addObject("typeList", types);
//		return mv;
//	}
	
	/**
	 * 获取信息，跳转至新增或编辑页面
	 * @Title: edit
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ModelAndView    返回类型
	 * @throws
	 */
//	@RequestMapping("detail")
//	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception{
//		
//		Long id = RequestUtil.getLong(request, "id");
//		String returnUrl = RequestUtil.getPrePage(request);
//		ModelAndView mv = getAutoView().addObject("returnUrl", returnUrl);
//		if (0 != id) {
//			// 编辑，获取原信息
//			OfficeObject officeObject = service.getById(id);
//			mv.addObject("officeObject",officeObject);
//		}
//
//		// 所有目录
//		List<OfficeObjectType> types = typeService.getAll();
//		mv.addObject("typeList", types);
//		return mv;
//	}
	
	/**
	 * 获取信息并跳至领用界面
	 * @Title: possess
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @return
	 * @param @throws Exception    参数
	 * @return ModelAndView    返回类型
	 * @throws
	 */
//	@RequestMapping("possess")
//	public ModelAndView possess(HttpServletRequest request, HttpServletResponse response) throws Exception{
//		
//		Long id = RequestUtil.getLong(request, "id");
//		String returnUrl = RequestUtil.getPrePage(request);
//		ModelAndView mv = getAutoView().addObject("returnUrl", returnUrl);
//		if (0 != id) {
//			// 编辑，获取原信息
//			OfficeObject officeObject = service.getById(id);
//			mv.addObject("officeObject",officeObject);
//		}
//
////		// 所有目录
////		List<OfficeObjectType> types = typeService.getAll();
////		mv.addObject("typeList", types);
//		return mv;
//	}
	
//	@RequestMapping("del")
//	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String preUrl = RequestUtil.getPrePage(request);
//		ResultMessage message = null;
//		try {
//			Long[] ids = RequestUtil.getLongAryByStr(request, "id");
//			if (0 < service.deleteAll(ids)) {
//				message = new ResultMessage(ResultMessage.Success, "删除成功!");
//			}
//		} catch (Exception ex) {
//			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
//		}
//		addMessage(message, request);
//		response.sendRedirect(preUrl);
//	}
	
	/**
	 * 新增或编辑  将数据存入数据库
	 * @Title: save
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param request
	 * @param @param response
	 * @param @param officeObject
	 * @param @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
//	@RequestMapping("save")
//	public void save(HttpServletRequest request, HttpServletResponse response, OfficeObject officeObject) throws Exception{
//		String resultMsg = "";
//		
//		try {
//			SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
//			
//			if(officeObject!=null && (officeObject.getId()==null || officeObject.getId()==0)){
//				
//				officeObject.setId(UniqueIdUtil.genId());
//				officeObject.setCreator(user.getUsername());
//				officeObject.setCreateBy(user.getUserId());
//				
//				officeObject.setId(UniqueIdUtil.genId());
//				//officeObject.setCreatetime(new Date());
//				service.add(officeObject);
//				
//				resultMsg = "添加成功!";
//			}else{
//				
//				officeObject.setUpdateBy(user.getUserId());
//				service.update(officeObject);
//				resultMsg = "更新成功!";
//			}
//			
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
	
//	@RequestMapping("applyList")
//	public ModelAndView applyList(HttpServletRequest request, HttpServletResponse response) throws Exception{
//		
//		boolean isSupportWeixin = PropertyUtil.getBooleanByAlias(SysPropertyConstants.WX_IS_SUPPORT, false);
//		QueryFilter queryFilter = new QueryFilter(request, "officeObjectApplyItem");
//		List<OfficeObjectApply> list = applyService.getAll(queryFilter);
//
//		ModelAndView mv = this.getAutoView().addObject("officeObjectApplyList", list).addObject("isSupportWeixin",
//				isSupportWeixin);
//		
//		List<OfficeObjectType> typeList = typeService.getAll();
//		mv.addObject("typeList", typeList);
//		mv.setViewName("me/officeObjectApplyList");
//		return mv;
//	}
	
}
