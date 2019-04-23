/**
 * 
 */
package com.suneee.ucp.base.controller.system;

import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.ucp.base.common.ResultConst;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 数据字典 控制器类 扩展
 * @author xiongxianyun
 *
 */
@RequestMapping("/platform/system/dictionary/")
@Controller
public class DictionaryExtController extends UcpBaseController{
	@Resource
	private DictionaryService dictionaryService;
	@Resource
	private SysOrgService sysOrgService;
	/**
	 * 根据分类表中的nodekey获取数据字典数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getDicByNodeKey")
	@ResponseBody
	public ResultVo getDicByNodeKey(HttpServletRequest request, HttpServletResponse response){
		// 获取请求参数
		String nodeKey = RequestUtil.getString(request, "nodeKey");
		if(StringUtils.isBlank(nodeKey)){
			return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "请求参数错误！");
		}		
		//获取当前用户企业编码
		String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
		try {
			List<Dictionary> dicList = dictionaryService.getByNodeKeyAndEid(nodeKey, enterpriseCode);
			return new ResultVo(ResultConst.COMMON_STATUS_SUCCESS, "获取数据字典成功！", dicList);
		} catch (Exception e) {
			logger.error("获取nodekey为[" + nodeKey + "]的数据字典失败！", e);
			return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "获取数据字典失败！");
		}
	}
}
