package com.suneee.platform.controller.system;

import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;

import com.suneee.core.util.PinyinUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysTypeKey;

import com.suneee.platform.service.system.SysTypeKeyService;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.page.PageList;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对象功能:系统分类键定义 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zousiyu
 * 创建时间:2018-01-18
 */@Controller
@RequestMapping("/api/system/sysTypeKey/")
@Action(ownermodel= SysAuditModelType.SYSTEM_SETTING)
public class SysTypeKeyApiController extends BaseController {

    @Resource
    private SysTypeKeyService sysTypeKeyService;
    /**
     * 取得系统分类键定义分页列表
     * @param request
     * @param response
     * @param page
     * @return
     * @throws Exception
     */
    @RequestMapping("list")
    @Action(description="查看系统分类键定义分页列表")
    @ResponseBody
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        QueryFilter queryFilter=new QueryFilter(request,true);
        PageList<SysTypeKey> list=(PageList<SysTypeKey>)sysTypeKeyService.getAll(queryFilter);

        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取定义分页列表成功", PageUtil.getPageVo(list));
    }


    @RequestMapping("getPingyinByName")
    @Action(description="根据汉字获取对应的拼音")
    @ResponseBody
    public ResultVo getPingyinByName(HttpServletRequest request, HttpServletResponse response) throws Exception{

        String typeName= RequestUtil.getString(request, "typeName");
        String str= PinyinUtil.getPinYinHeadCharFilter(typeName);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"已获取对应的拼音",str);
    }


    @RequestMapping("edit")
    @Action(description="编辑系统分类键定义",
            execOrder=ActionExecOrder.AFTER,
            detail="<#if isAdd>添加系统分类键定义<#else>编辑系统分类键定义:" +
                    "<#assign entity=sysTypeKeyService.getById(Long.valueOf(typeId))/>" +
                    "【${entity.name}】</#if>"
    )
    @ResponseBody
    public ResultVo edit(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Long typeId=RequestUtil.getLong(request,"typeId");
        SysTypeKey sysTypeKey=null;
        boolean isadd=true;
        if(typeId!=0){
            sysTypeKey= sysTypeKeyService.getById(typeId);
            isadd=false;
        }else{
            sysTypeKey=new SysTypeKey();
        }
        SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"编辑成功");

    }

    /**
     * 删除系统分类键定义
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("del")
    @Action(description="删除系统分类键定义",
            execOrder=ActionExecOrder.BEFORE,
            detail="删除系统数据源"+
                    "<#list StringUtils.split(typeId,\",\") as item>" +
                    "<#assign entity=sysTypeKeyService.getById(Long.valueOf(item))/>" +
                    "【${entity.typeName}】"+
                    "</#list>"
    )
    @ResponseBody
    public ResultVo del(HttpServletRequest request,HttpServletResponse response) throws Exception
    {
        try{
            Long[] lAryId =RequestUtil.getLongAryByStr(request, "typeId");
            sysTypeKeyService.delByIds(lAryId);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除系统分类键定义成功!" );
        }
        catch(Exception ex){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败:" + ex.getMessage());
        }

    }

    /**
     * 取得系统分类键定义明细
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("get")
    @Action(description="查看系统分类键定义明细",detail="查看系统分类键定义明细")
    @ResponseBody
    public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        long id=RequestUtil.getLong(request,"typeId");
//        long canReturn=RequestUtil.getLong(request, "canReturn",0);
        SysTypeKey sysTypeKey = sysTypeKeyService.getById(id);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获得系统分类键定义明细成功",sysTypeKey);
    }

    @RequestMapping("saveSequence")
    @Action(description="排序操作",detail="排序操作")
    @ResponseBody
    public ResultVo saveSequence(HttpServletRequest request, HttpServletResponse response) throws Exception{
        try{
            Long[] ids=RequestUtil.getLongAryByStr(request, "ids");
            sysTypeKeyService.saveSequence(ids);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "分类排序成功!" );
        }
        catch(Exception ex){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "分类排序失败!" );
        }

    }



}
