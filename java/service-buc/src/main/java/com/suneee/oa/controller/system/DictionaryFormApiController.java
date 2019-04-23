
package com.suneee.oa.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.oa.Exception.TipInfoException;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 对象功能:数据字典 控制器类
 * 开发公司:深圳象翌
 * 开发人员:kaize
 * 创建时间:2018-03-19 13:18:34
 */
@Controller
@RequestMapping("/api/system/dictionary/")
@Action(ownermodel=SysAuditModelType.SYSTEM_SETTING)
public class DictionaryFormApiController extends BaseFormController
{
	@Resource
	private DictionaryService dictionaryService;

	
	/**
	 * 添加或更新数据字典.(新版)
	 * @param request  HttpServletRequest对象
	 * @param response HttpServletResponse对象
	 * @param globalType 字典分类，Web表单对象
	 * @param bindResult 命令/表单对象的验证结果对象
	 * @throws Exception
	 */
	@RequestMapping("saveGlobaltypeAndDictionary")
	@ResponseBody
	@Action(description="添加或更新数据字典",
			execOrder=ActionExecOrder.AFTER,
			detail="<#if isAdd>添加<#else>更新</#if>新数据字典" +
					"【${SysAuditLinkService.getGlobalTypeLink(Long.valueOf(typeId))}】")
	public ResultVo saveGlobaltypeAndDictionary(HttpServletRequest request, HttpServletResponse response, GlobalType globalType, BindingResult bindResult) throws Exception
	{
		ResultMessage resultMessage = null;
		try {
			String jsonData = RequestUtil.getString(request, "dictionarysJsonData");
			List<Dictionary> dicList = JSONObject.parseArray(jsonData, Dictionary.class);
			//表单输入内容验证（GlobalType）
			resultMessage = validForm("globalType", globalType, bindResult, request);
			if (resultMessage.getResult() == ResultMessage.Fail) {
				return new ResultVo(ResultVo.COMMON_STATUS_FAILED,resultMessage.getMessage());
			}
			//表单输入内容验证（Dictionary）
			for (Dictionary dic : dicList) {
				resultMessage = validForm("dictionary", dic, bindResult, request);
				if (resultMessage.getResult() == ResultMessage.Fail) {
					return new ResultVo(ResultVo.COMMON_STATUS_FAILED,resultMessage.getMessage());
				}
			}
			if(globalType.getTypeId() == 0L){
				resultMessage = new ResultMessage(ResultMessage.Success, "数据字典新增成功！");
			}else{
				resultMessage = new ResultMessage(ResultMessage.Success, "数据字典编辑成功！");
			}
			dictionaryService.saveGlobaltypeAndDictionary(request,response,globalType, dicList);
		}catch (TipInfoException te){
			resultMessage = new ResultMessage(ResultMessage.Fail, te.getMessage());
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			resultMessage = new ResultMessage(ResultMessage.Fail, "数据字典保存失败！");
		}
		int status=resultMessage.getResult()==ResultMessage.Success?ResultVo.COMMON_STATUS_SUCCESS:ResultVo.COMMON_STATUS_FAILED;
		return new ResultVo(status,resultMessage.getMessage(),resultMessage.getData());
	}
}
