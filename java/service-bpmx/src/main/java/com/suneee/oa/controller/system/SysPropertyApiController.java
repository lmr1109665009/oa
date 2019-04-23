package com.suneee.oa.controller.system;

import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysProperty;
import com.suneee.platform.service.system.SysPropertyService;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <pre>
 * 对象功能:系统配置参数表 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ouxb
 * 创建时间:2015-04-16 11:20:41
 * </pre>
 */
@Controller
@RequestMapping("/platform/system/sysPropertyApi/")
public class SysPropertyApiController extends BaseController {
    @Resource
    private SysPropertyService sysPropertyService;

    /**
     * 添加或更新系统配置参数表。
     *
     * @param request
     * @param response
     * @param sysProperty
     *            添加或更新的实体
     * @param bindResult
     * @param viewName
     * @return
     * @throws Exception
     */
    @RequestMapping("save")
    @ResponseBody
    @Action(description = "添加或更新系统配置参数表")
    public ResultVo save(HttpServletRequest request, HttpServletResponse response, SysProperty sysProperty) throws Exception {
        ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"");
        try {
            sysPropertyService.save(sysProperty);
            result.setMessage(getText("更新", "系统配置参数表"));
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            result.setStatus(ResultVo.COMMON_STATUS_FAILED);
        }
        return result;
    }

    /**
     * 取得系统配置参数表分页列表
     *
     * @param request
     * @param response
     * @param page
     * @return
     * @throws Exception
     */
    @RequestMapping("list")
    @Action(description = "查看系统配置参数表分页列表")
    @ResponseBody
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<SysProperty> list = sysPropertyService.getAll(new QueryFilter(request, "sysPropertyItem"));
//        ModelAndView mv = this.getAutoView().addObject("sysPropertyList", list);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据成功！",list);
    }

    /**
     * 编辑系统配置参数表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("edit")
    @ResponseBody
    @Action(description = "编辑系统配置参数表")
    public ResultVo edit(HttpServletRequest request) throws Exception {
        Long id = RequestUtil.getLong(request, "id", 0L);
        String returnUrl = RequestUtil.getPrePage(request);
        SysProperty sysProperty = sysPropertyService.getById(id);

        Map<String,Object> dataMap=new HashMap<String,Object>();
        dataMap.put("sysProperty",sysProperty);
//        dataMap.put("returnUrl",returnUrl);

//        return getAutoView().addObject("sysProperty", sysProperty).addObject("returnUrl", returnUrl);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据成功！",dataMap);
    }

    /**
     * 取得系统配置参数表明细
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("get")
    @ResponseBody
    @Action(description = "查看系统配置参数表明细")
    public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long id = RequestUtil.getLong(request, "id");
        SysProperty sysProperty = sysPropertyService.getById(id);
//        return getAutoView().addObject("sysProperty", sysProperty);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据成功！",sysProperty);
    }

}
