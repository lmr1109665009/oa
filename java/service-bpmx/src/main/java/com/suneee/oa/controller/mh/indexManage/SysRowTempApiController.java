package com.suneee.oa.controller.mh.indexManage;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.PageVo;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.model.rowTemp.SysRowTemp;
import com.suneee.ucp.mh.service.gatewayManage.SysRowTempService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 模板管理controller
 * @author ytw
 * */
@Controller
@RequestMapping("/platform/system/sysRowTempApi/")
public class SysRowTempApiController {

    private Logger logger = LoggerFactory.getLogger(SysRowTempApiController.class);

    @Resource
    private SysRowTempService sysRowTempService;

    /**
     * 添加或更新模板。
     */
    @RequestMapping("save")
    @Action(description="添加或更新模板")
    @ResponseBody
    public ResultVo save(SysRowTemp sysRowTemp)
    {
        try{
            this.sysRowTempService.save(sysRowTemp);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"操作成功");
        }catch(Exception e){
            logger.error("添加或更新模板失败, sysIndexLayout={}",sysRowTemp, e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "操作失败");
        }
    }


    /**
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("list")
    @ResponseBody
    @Action(description="获取模板列表数据")
    public ResultVo list(HttpServletRequest request) throws Exception
    {
        QueryFilter queryFilter = new QueryFilter(request);
        List<SysRowTemp> list=sysRowTempService.getAll(queryFilter);
        PageVo pageVo = PageUtil.getPageVo(list, queryFilter.getPageBean());
        return new ResultVo(pageVo);
    }

    /**
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping("del")
    @Action(description="删除模板")
    @ResponseBody
    public ResultVo del(HttpServletRequest request) throws Exception
    {
        Long[] ids =RequestUtil.getLongAryByStr(request, "ids");
        try{
            sysRowTempService.delByIds(ids);
            return ResultVo.getSuccessInstance();
        }catch(Exception e){
            logger.error("del exception, ids={}", ids, e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败");
        }
    }


    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("get")
    @Action(description="查看模板明细")
    @ResponseBody
    public ResultVo get(HttpServletRequest request, HttpServletResponse response)
    {
        Long id= RequestUtil.getLong(request,"id");
        SysRowTemp sysRowTemp = sysRowTempService.getById(id);
        return new ResultVo(sysRowTemp);
    }

}
