package com.suneee.oa.controller.bpm;

import com.suneee.core.web.ResultMessage;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.controller.mobile.MobileBaseController;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.form.BpmFormFieldService;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <pre>
 * 对象功能:流程实例表单自定义字段查询
 * 开发公司:深圳象翌
 * 开发人员:子华
 * 创建时间:2011-12-12 10:56:13
 * </pre>
 */
@Controller
@RequestMapping("/api/bpm/customFormQuery/")
@Action(ownermodel = SysAuditModelType.PROCESS_MANAGEMENT)
public class CustomFormQueryApiController extends MobileBaseController{
    @Autowired
    private BpmFormFieldService formFieldService;

    /**
     * 获取可查询自定义查询字段
	 * @param defId 流程定义ID
     * @return
     */
	@RequestMapping("queryFieldList")
    @ResponseBody
    @Action(description = "获取可查询自定义查询字段")
    public ResultVo queryFieldList(@RequestParam Long defId){
        ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
        List<BpmFormField> fieldList=formFieldService.getQueryVarByFlowDefId(defId);
        resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
        resultVo.setMessage("获取自定义查询字段成功");
        resultVo.setData(fieldList);
	    return resultVo;
    }
}
