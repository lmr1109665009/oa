/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: newIdentityController
 * Author:   lmr
 * Date:     2018/4/3 15:26
 * Description: 流水号管理
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.oa.controller.Identity;

import com.suneee.core.page.PageList;
import com.suneee.core.util.*;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.annotion.ActionExecOrder;
import com.suneee.platform.model.system.Identity;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysProperty;
import com.suneee.platform.service.system.IdentityService;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.weixin.model.ListModel;
import com.suneee.weixin.util.CommonUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈流水号管理〉
 *
 * @author lmr
 * @create 2018/4/3
 * @since 1.0.0
 */
@Controller
@RequestMapping("/platform/system/newIdentity/")
@Action(ownermodel = SysAuditModelType.PROCESS_AUXILIARY)
public class IdentityOaController extends BaseController {
    @Resource
    private IdentityService identityService;

    /**
     * 取得流水号生成分页列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("list")
    @Action(description = "查看流水号生成分页列表")
    @ResponseBody
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Identity> list = identityService.getAll(new QueryFilter(request,true));
        ListModel model = new ListModel();
        if (list != null) {
            model = CommonUtil.getListModel((PageList) list);
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取流水号列表成功！", model);
    }

    /**
     * 取得流水号生成分页列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("showlist")
    @Action(description = "查看流水号生成分页列表", detail = "查看流水号生成分页列表")
    public ModelAndView showlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Identity> list = identityService.getAll(new QueryFilter(request, "identityItem"));
        ModelAndView mv = this.getAutoView().addObject("identityList", list);
        return mv;
    }

    /**
     * 删除流水号生成
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("delete")
    @ResponseBody
    @Action(description = "删除流水号",
            execOrder = ActionExecOrder.AFTER,
            detail = "删除流水号:" +
                    "<#list StringUtils.split(id,\",\") as item>" +
                    "<#assign entity=identityService.getById(Long.valueOf(item))/>" +
                    "【${entity.name}】" +
                    "</#list>")
    public ResultVo delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultVo message = null;
            Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
            for(int i=0;i<lAryId.length;i++){
                Identity identity = identityService.getById(lAryId[i]);
                    if(identity!=null&&identity.getAlias().equals(SysProperty.GlobalFlowNo)){
                        message=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"您不能删除"+identity.getName()+"，此流水号为全局流水号！");
                        return new ResultVo(message);
                    }
            }
        try {
            identityService.delByIds(lAryId);
            message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除流水号成功!");
        } catch (Exception ex) {
            message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除流水号失败，请重试。");
        }
        return new ResultVo(message);
    }

    @RequestMapping("save")
    @ResponseBody
    @Action(description = "添加或更新流水号生成",
            detail = "<#if isAdd>添加<#else>更新</#if>流水号生成：" +
                    "【${SysAuditLinkService.getIdentityLink(Long.valueOf(id))}】")
    public ResultVo save(HttpServletRequest request, HttpServletResponse response, Identity identity) throws Exception {
        boolean isadd = true;
        String resultMsg = null;
        if (identity.getId()==null||identity.getId() == 0L) {
            String aliasName = PinyinUtil.getPinYinHeadChar(identity.getName());
            identity.setAlias(this.addAlias(aliasName,1));
                identity.setId(UniqueIdUtil.genId());
                identityService.add(identity);
                resultMsg = "添加流水号生成成功";
        } else {
            identityService.update(identity);
            resultMsg = "更新流水号生成成功";
       }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, resultMsg);
    }

    /**
     * 添加新的流水号别名
     * @param name
     * @param count
     * @return
     */
    public String addAlias(String name,int count){
        boolean rtn = identityService.isAliasExisted(name);
        String newName=name ;
        if(rtn){
            newName=name+count;
            count++;
            this.addAlias(newName,count);
        }
        return newName;
    }
    /**
     * 更新的流水号别名
     * @param identity
     * @param count
     * @return
     */
    public String updateAlias(Identity identity,int count){
        boolean rtn = identityService.isAliasExistedByUpdate(identity);
        String newName=identity.getAlias() ;
        if(rtn){
            newName=identity.getAlias()+count;
            count++;
            identity.setAlias(newName);
            this.updateAlias(identity,count);
        }
        return newName;
    }
    /**
     * 获取所有的流水号
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("getAllIdentity")
    @ResponseBody
    public List<Identity> getAllIdentity(HttpServletRequest request) throws Exception {
        return identityService.getAll();
    }

    /**
     * 取得流水号生成明细
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("get")
    @ResponseBody
    @Action(description = "查看流水号生成明细", detail = "查看流水号生成明细")
    public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        long id = RequestUtil.getLong(request, "id");
        long canReturn = RequestUtil.getLong(request, "canReturn", 0);
        Identity identity = identityService.getById(id);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "查看详情成功", identity);
    }

    /**
     * 根据alias取得流水号生成明细
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("getById")
    @ResponseBody
    public ResultVo getById(HttpServletRequest request) throws Exception {
        String alias = RequestUtil.getString(request, "alias");
        Identity identity = identityService.getByAlias(alias);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,identity.getName());
    }

    /**
     * 导出选择导出xml
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("export")
    @Action(description = "导出选择导出xml")
    public ModelAndView export(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tableIds = RequestUtil.getString(request, "tableIds");
        ModelAndView mv = this.getAutoView();
        mv.addObject("tableIds", tableIds);
        return mv;
    }

    /**
     * 导出自定义查询xml。
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("exportXml")
    @Action(description = "导出流水号", detail = "导出流水号:" + "<#list StringUtils.split(tableIds,\",\") as item>" + "<#assign entity=bpmFormTableService.getById(Long.valueOf(item))/>" + "【${entity.tableDesc}(${entity.tableName})】" + "</#list>")
    public void exportXml(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String strXml = null;
        String fileName = null;
        Long[] tableIds = RequestUtil.getLongAryByStr(request, "tableIds");
        List<Identity> list = identityService.getAll();
        try {
            if (BeanUtils.isEmpty(tableIds)) {
                if (BeanUtils.isNotEmpty(list)) {
                    strXml = identityService.exportXml(list);
                    fileName = "全部流水号记录_"
                            + DateFormatUtil.getNowByString("yyyyMMddHHmmdd")
                            + ".xml";
                }
            } else {
                strXml = identityService.exportXml(tableIds);
                fileName = DateFormatUtil.getNowByString("yyyyMMddHHmmdd")
                        + ".xml";
                if (tableIds.length == 1) {
                    Identity identity = identityService.getById(tableIds[0]);
                    fileName = identity.getName() + "_" + fileName;
                } else if (tableIds.length == list.size()) {
                    fileName = "全部流水号记录_" + fileName;
                } else {
                    fileName = "多条流水号记录_" + fileName;
                }
            }
            FileUtil.downLoad(request, response, strXml, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 导入流水号的XML
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("importXml")
    @Action(description = "导入流水号")
    public ResultVo importXml(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipartFile fileLoad = request.getFile("xmlFile");
        ResultVo message = null;
        try {
            identityService.importXml(fileLoad.getInputStream());
            message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,
                    MsgUtil.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "导入文件异常，请检查文件格式！");
        }
        return message;
    }


}