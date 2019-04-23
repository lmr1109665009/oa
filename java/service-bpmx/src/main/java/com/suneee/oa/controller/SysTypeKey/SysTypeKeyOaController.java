/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: sysTypeKeyController
 * Author:   lmr
 * Date:     2018/3/27 15:41
 * Description: 分类标识管理
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.oa.controller.SysTypeKey;

import com.suneee.core.page.PageList;
import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysTypeKey;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysTypeKeyService;
import com.suneee.weixin.model.ListModel;
import com.suneee.weixin.util.CommonUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈分类标识管理〉
 *
 * @author lmr
 * @create 2018/3/27
 * @since 1.0.0
 */
@Controller
@RequestMapping("/platform/system/newSysTypeKey/")
@Action(ownermodel= SysAuditModelType.SYSTEM_SETTING)
public class SysTypeKeyOaController extends BaseController
{
    @Resource
    private SysTypeKeyService sysTypeKeyService;
    @Resource
    private GlobalTypeService globalTypeService;
    /**
     * 取得系统分类键定义分页列表
     * @param request
     * @param response
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("list")
    @Action(description="查看系统分类键定义分页列表")
    @ResponseBody
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        boolean needPage = RequestUtil.getBoolean(request,"needPage");
        QueryFilter queryFilter=new QueryFilter(request,needPage);
        List<SysTypeKey> list=sysTypeKeyService.getAll(queryFilter);
        if(needPage){
            ListModel model=null;
            if(list!=null){
                model = CommonUtil.getListModel((PageList) list);
            }
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取分类标识列表成功！",model);
        }else{
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取分类标识列表成功！",list);
        }
    }




    @RequestMapping("getPingyinByName")
    @Action(description="根据汉字获取对应的拼音")
    public void getPingyinByName(HttpServletRequest request,HttpServletResponse response) throws Exception{
        PrintWriter out=response.getWriter();
        String typeName=RequestUtil.getString(request, "typeName");
        String str= PinyinUtil.getPinYinHeadCharFilter(typeName);
        out.print(str);
    }

    /**
     * 删除系统分类键定义
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("delete")
    @Action(description="删除系统分类键定义",
            execOrder= ActionExecOrder.BEFORE,
            detail="删除系统数据源"+
                    "<#list StringUtils.split(typeId,\",\") as item>" +
                    "<#assign entity=sysTypeKeyService.getById(Long.valueOf(item))/>" +
                    "【${entity.typeName}】"+
                    "</#list>"
    )
    @ResponseBody
    public ResultVo delete(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        try{
            Long[] lAryId =RequestUtil.getLongAryByStr(request, "typeId");
            for(int i =0;i<lAryId.length;i++) {
                SysTypeKey sysTypeKey = sysTypeKeyService.getById(lAryId[i]);
                List<GlobalType> list = globalTypeService.getByParentId(lAryId[i],sysTypeKey.getTypeKey());
                if(list.size()!=0){
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"请删除"+sysTypeKey.getTypeName()+"所关联的数据！");
                }
            }
            sysTypeKeyService.delByIds(lAryId);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除分类标识成功!");
        }
        catch(Exception ex){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败:" + ex.getMessage());
        }
    }

    @RequestMapping("edit")
    @Action(description="编辑系统分类键定义",
            execOrder=ActionExecOrder.AFTER,
            detail="<#if isAdd>添加系统分类键定义<#else>编辑系统分类键定义:" +
                    "<#assign entity=sysTypeKeyService.getById(Long.valueOf(typeId))/>" +
                    "【${entity.name}】</#if>"
    )
    @ResponseBody
    public ResultVo edit(HttpServletRequest request,SysTypeKey sysTypeKey) throws Exception
    {
        String typeKey=sysTypeKey.getTypeKey();
        String message="";
        //如果id为null,或者为0，则为添加
        if(sysTypeKey.getTypeId()==0){

                try{
                    String pinKey = PinyinUtil.getPinYinHeadChar(sysTypeKey.getTypeName());
                    String newTypeKey = this.addTypeKey(pinKey, 1);
                    sysTypeKey.setTypeKey(newTypeKey);
                    sysTypeKey.setTypeId(UniqueIdUtil.genId());
                    sysTypeKey.setFlag(0);
                    sysTypeKey.setSn(0);
                    sysTypeKeyService.add(sysTypeKey);
                     message="添加分类标识成功";
                    return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,message);
                }
                catch(Exception ex){
                    String str = MessageUtil.getMessage();
                    if (StringUtil.isNotEmpty(str)) {
                        message="添加分类标识失败:" + str;

                    } else {
                        message="添加分类标识失败:" + ExceptionUtil.getExceptionMessage(ex);
                    }
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED,message);

            }
        }else{
                try{
//                    sysTypeKey.setTypeKey(this.updateNodeKey(sysTypeKey.getTypeName(),1,sysTypeKey.getTypeId()));
                    boolean isExist = sysTypeKeyService.isKeyExistForUpdate(sysTypeKey.getTypeKey(),sysTypeKey.getTypeId());
                    if(isExist){
                        return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"键值已经存在!");
                    }
                    sysTypeKeyService.update(sysTypeKey);
                     message="更新分类标识成功";
                    return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, message);
                }
                catch(Exception ex){
                    String str = MessageUtil.getMessage();
                    if (StringUtil.isNotEmpty(str)) {
                        message="修改分类标识失败:" + str;
                    } else {
                        message="修改分类标识失败:" + ExceptionUtil.getExceptionMessage(ex);
                    }
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED,message);
                }

        }
    }

    /**
     * 添加不重复的nodeKey
     * @param pinyinKey
     * @param count

     * @return
     */
    public String addTypeKey(String pinyinKey, int count) {
        boolean isExist = sysTypeKeyService.isExistKey(pinyinKey);
        String newKey = pinyinKey;
        if (isExist) {
            newKey = pinyinKey + count;
            count++;
            this.addTypeKey(newKey, count);
        }
        return newKey;
    }

    /**
     * 更新不重复的key
     * @param pinyinKey
     * @param count
     * @param typeId

     * @return
     */
    public String updateNodeKey(String pinyinKey, int count, Long typeId) {
        boolean isExist = sysTypeKeyService.isKeyExistForUpdate(pinyinKey,typeId);
        String newKey = pinyinKey;
        if (isExist) {
            newKey = pinyinKey + count;
            count++;
            this.updateNodeKey(newKey, count, typeId);
        }
        return newKey;
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
        long id= RequestUtil.getLong(request,"typeId");
        long canReturn=RequestUtil.getLong(request, "canReturn",0);
        SysTypeKey sysTypeKey = sysTypeKeyService.getById(id);
      //  return getAutoView().addObject("sysTypeKey", sysTypeKey).addObject("canReturn", canReturn);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"查看详情成功",sysTypeKey);
    }

    @RequestMapping("sort")
    @ResponseBody
    @Action(description="排序操作",detail="排序操作")
    public ResultVo saveSequence(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ResultVo message;
        try{
            Long[] typeIds=RequestUtil.getLongAryByStr(request, "typeIds");
            if(typeIds==null){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"没有排序的数据！");
            }
            sysTypeKeyService.saveSequence(typeIds);
            message=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "分类排序成功!");
        }
        catch(Exception ex){
            message=new ResultVo(ResultVo.COMMON_STATUS_FAILED, "分类排序失败!");
        }
        return message;
    }

}