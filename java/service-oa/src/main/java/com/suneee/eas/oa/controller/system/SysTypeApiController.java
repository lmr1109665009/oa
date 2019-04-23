package com.suneee.eas.oa.controller.system;

import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.system.SysType;
import com.suneee.eas.oa.service.system.SysTypeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 系统分类控制器
 * @Author: 子华
 * @Date: 2018/7/31 17:54
 */
@RestController
@RequestMapping(ModuleConstant.SYSTEM_MODULE+FunctionConstant.SYS_TYPE)
public class SysTypeApiController {
	private Logger log= LogManager.getLogger(SysTypeApiController.class);

	@Autowired
	private SysTypeService sysTypeService;

	/**
	 * 获取分类列表
	 * @param request
	 * @return
	 */
	@RequestMapping("list")
	public ResponseMessage list(HttpServletRequest request){
		QueryFilter filter=new QueryFilter("listAll",request);
		filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		List<SysType> typeList=sysTypeService.getListBySqlKey(filter);
		return ResponseMessage.success("获取分类列表成功",typeList);
	}

	/**
	 * 添加系统分类
	 * @param sysType
	 * @return
	 */
	@RequestMapping("save")
	public ResponseMessage save(@Valid SysType sysType){
		ResponseMessage result=sysTypeService.saveType(sysType);
		if (result==null){
			result=ResponseMessage.success("添加成功");
		}
		return result;
	}

	/**
	 * 更新分类
	 * @param sysType
	 * @return
	 */
	@RequestMapping("update")
	public ResponseMessage update(SysType sysType){
		if (StringUtils.isEmpty(sysType.getName())){
			return ResponseMessage.fail("请填写分类名称");
		}
		if (sysType.getTypeId()==null){
			return ResponseMessage.fail("参数不正确，缺少分类ID");
		}
		SysType oldType=sysTypeService.findById(sysType.getTypeId());
		if (oldType==null){
			return ResponseMessage.fail("分类不存在");
		}
		ResponseMessage result=sysTypeService.updateType(sysType,oldType);
		if (result==null){
			result=ResponseMessage.success("更新成功");
		}
		return result;
	}

	/**
	 * 删除分类ID
	 * @param request
	 * @return
	 */
	@RequestMapping("delete")
	public ResponseMessage delete(HttpServletRequest request){
		Long[] ids=RequestUtil.getLongAryByStr(request,"ids");
		if (ids==null||ids.length==0){
			return ResponseMessage.fail("参数错误，无法进行删除");
		}
		List<String> errorList=new ArrayList<>();
		for (Long id:ids){
			SysType type=sysTypeService.findById(id);
			if (type==null){
				continue;
			}
			QueryFilter filter=new QueryFilter("countAll");
			filter.addFilter("nodePath",type.getNodePath()+type.getTypeId()+".%");
			int count=sysTypeService.getCountBySqlKey(filter);
			if (count>0){
				errorList.add(type.getName());
				continue;
			}
			sysTypeService.deleteById(id);
		}
		if (ids.length!=errorList.size()&&errorList.size()>0){
			return ResponseMessage.success(StringUtils.join(errorList,"、")+"等分类包含子分类，无法删除");
		}else if (ids.length==errorList.size()){
			return ResponseMessage.fail("全部删除失败");
		}
		return ResponseMessage.success("删除成功");
	}

	/**
	 * 分类排序更新
	 * @param request
	 * @return
	 */
	@RequestMapping("updateSort")
	public ResponseMessage updateSort(HttpServletRequest request){
		Long[] ids= RequestUtil.getLongAryByStr(request,"ids");
		sysTypeService.updateSort(ids);
		return ResponseMessage.success("排序已更新");
	}

}
