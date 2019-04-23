package com.suneee.eas.oa.controller.system;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.system.DicItem;
import com.suneee.eas.oa.model.system.DicType;
import com.suneee.eas.oa.service.system.DicItemService;
import com.suneee.eas.oa.service.system.DicTypeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description: 数据字典控制器
 * @Author: 子华
 * @Date: 2018/7/31 17:54
 */
@RestController
@RequestMapping(ModuleConstant.SYSTEM_MODULE+FunctionConstant.SYS_DIC)
public class SysDicApiController {
	private Logger log= LogManager.getLogger(SysDicApiController.class);

	@Autowired
	private DicTypeService dicTypeService;
	@Autowired
	private DicItemService dicItemService;

	/**
	 * 获取字典列表
	 * @param request
	 * @return
	 */
	@RequestMapping("list")
	public ResponseMessage list(HttpServletRequest request){
		QueryFilter filter=new QueryFilter("listAll",request);
		filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		List<DicType> dicList=dicTypeService.getListBySqlKey(filter);
		return ResponseMessage.success("获取数据字典列表成功",dicList);
	}

	/**
	 * 分页获取字典列表数据
	 * @param request
	 * @return
	 */
	@RequestMapping("listPage")
	public ResponseMessage listPage(HttpServletRequest request){
		QueryFilter filter=new QueryFilter("listAll",request);
		filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		Pager<DicType> dicTypePager=dicTypeService.getPageBySqlKey(filter);
		return ResponseMessage.success("获取数据字典列表成功",dicTypePager);
	}

	/**
	 * 添加数据字典
	 * @param dicType
	 * @return
	 */
	@RequestMapping("save")
	public ResponseMessage save(@Valid DicType dicType){
		QueryFilter filter=new QueryFilter("countAll");
		filter.addFilter("code",dicType.getCode());
		filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		Integer count=dicTypeService.getCountBySqlKey(filter);
		if (count>0){
			return ResponseMessage.fail("字典已存在，请确认后再添加");
		}
		dicTypeService.save(dicType);
		return ResponseMessage.success("添加成功");
	}

	/**
	 * 字典整体添加
	 * @param dicType
	 * @return
	 */
	@RequestMapping("saveObj")
	public ResponseMessage saveObj(@RequestBody @Valid DicType dicType){
		QueryFilter filter=new QueryFilter("countAll");
		filter.addFilter("code",dicType.getCode());
		filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
		Integer count=dicTypeService.getCountBySqlKey(filter);
		if (count>0){
			return ResponseMessage.fail("字典已存在，请确认后再添加");
		}
		if (dicType.getList()!=null){
			Set<String> tempSet=new HashSet<String>();
			for (DicItem item:dicType.getList()){
				if (tempSet.contains(item.getVal())){
					return ResponseMessage.fail("字典项\""+item.getOption()+"("+item.getVal()+")\",值已存在");
				}
			}
		}
		dicTypeService.saveObj(dicType);
		return ResponseMessage.success("添加成功");
	}

	/**
	 * 更新数据字典
	 * @param dicType
	 * @return
	 */
	@RequestMapping("update")
	public ResponseMessage update(DicType dicType){
		if (StringUtils.isEmpty(dicType.getName())){
			return ResponseMessage.fail("请填写字典名称");
		}
		if (dicType.getDicId()==null){
			return ResponseMessage.fail("参数不正确，缺少数据字典ID");
		}
		dicTypeService.update(dicType);
		return ResponseMessage.success("更新成功");
	}

	/**
	 *
	 * @param dicType
	 * @return
	 */
	@RequestMapping("updateObj")
	public ResponseMessage updateObj(@RequestBody DicType dicType){
		if (StringUtils.isEmpty(dicType.getName())){
			return ResponseMessage.fail("请填写字典名称");
		}
		if (dicType.getDicId()==null){
			return ResponseMessage.fail("参数不正确，缺少数据字典ID");
		}
		if (dicType.getList()!=null){
			Set<String> tempSet=new HashSet<String>();
			for (DicItem item:dicType.getList()){
				if (tempSet.contains(item.getVal())){
					return ResponseMessage.fail("字典项\""+item.getOption()+"("+item.getVal()+")\",值已存在");
				}
			}
		}
		dicTypeService.updateObj(dicType);
		return ResponseMessage.success("更新成功");
	}

	/**
	 * 删除分数据字典
	 * @param request
	 * @return
	 */
	@RequestMapping("delete")
	public ResponseMessage delete(HttpServletRequest request){
		Long[] ids=RequestUtil.getLongAryByStr(request,"ids");
		if (ids==null||ids.length==0){
			return ResponseMessage.fail("参数错误，无法进行删除");
		}
		for (Long id:ids){
			DicType dicType=dicTypeService.findById(id);
			if (dicType==null){
				continue;
			}
			dicTypeService.deleteById(id);
			QueryFilter filter=new QueryFilter("delByDicId");
			filter.addFilter("dicId",id);
			dicItemService.deleteBySqlKey(filter);
		}
		return ResponseMessage.success("删除成功");
	}

	/**
	 * 添加字典项
	 * @param dicItem
	 * @return
	 */
	@RequestMapping("saveOption")
	public ResponseMessage saveOption(@Valid DicItem dicItem){
		if (dicItem.getDicId()==null){
			return ResponseMessage.fail("参数不正确，缺少数据字典ID");
		}
		QueryFilter filter=new QueryFilter("countAll");
		filter.addFilter("val",dicItem.getVal());
		filter.addFilter("dicId", dicItem.getDicId());
		Integer count=dicItemService.getCountBySqlKey(filter);
		if (count>0){
			return ResponseMessage.fail("字典值已存在，请确认后再添加");
		}
		dicItemService.save(dicItem);
		return ResponseMessage.success("添加成功");
	}

	/**
	 * 更新字典项
	 * @param dicItem
	 * @return
	 */
	@RequestMapping("updateOption")
	public ResponseMessage updateOption(@Valid DicItem dicItem){
		if (dicItem.getId()==null){
			return ResponseMessage.fail("参数不正确，缺少字典项ID");
		}
		QueryFilter filter=new QueryFilter("countAll");
		filter.addFilter("val",dicItem.getVal());
		filter.addFilter("dicId", dicItem.getDicId());
		filter.addFilter("notSelf",dicItem.getId());
		Integer count=dicTypeService.getCountBySqlKey(filter);
		if (count>0){
			ResponseMessage.fail("字典值已存在，请确认后再修改");
		}
		dicItemService.update(dicItem);
		return ResponseMessage.success("更新成功");
	}

	/**
	 * 删除字典项
	 * @param id
	 * @return
	 */
	@RequestMapping("delOption")
	public ResponseMessage delOption(Long id){
		dicItemService.deleteById(id);
		return ResponseMessage.success("删除成功");
	}

	/**
	 * 根据字典ID获取字典项列表
	 * @param dicId
	 * @return
	 */
	@RequestMapping("listByDicId")
	public ResponseMessage listByDicId(Long dicId){
		return ResponseMessage.success("获取字典项列表成功",dicItemService.listByDicId(dicId));
	}

	/**
	 * 根据ID来获取字典
	 * @param id
	 * @return
	 */
	@RequestMapping("getById")
	public ResponseMessage getById(Long id){
		DicType dicType=dicTypeService.findById(id);
		dicType.setList(dicItemService.listByDicId(dicType.getDicId()));
		return ResponseMessage.success("获取数据字典成功",dicType);
	}

	/**
	 * 根据字典码来获取字典
	 * @param code
	 * @return
	 */
	@RequestMapping("listByCode")
	public ResponseMessage listByCode(String code){
		DicType dicType=dicTypeService.getByCode(code);
		if(dicType == null){
			return ResponseMessage.fail("请先添加【" + code + "】数据字典分类及字典项！");
		}
		dicType.setList(dicItemService.listByDicId(dicType.getDicId()));
		return ResponseMessage.success("获取数据字典成功",dicType);
	}

	/**
	 * 更新字典项排序
	 * @param request
	 * @return
	 */
	@RequestMapping("updateSort")
	public ResponseMessage updateSort(HttpServletRequest request){
		Long[] ids= RequestUtil.getLongAryByStr(request,"ids");
		dicItemService.updateSort(ids);
		return ResponseMessage.success("排序已更新");
	}

}
