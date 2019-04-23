package com.suneee.oa.controller.mh.codeTable;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.model.codeTable.CodeTable;
import com.suneee.ucp.mh.service.codeTable.CodeTableService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *<pre>
 * 对象功能:部门HR设置 控制器类
 * 开发公司:深圳象翌
 * 开发人员:pengfeng
 * 创建时间:2017-08-30
 *</pre>
 */

@Controller
@RequestMapping("/mh/codeTable/departHrApi/")
public class DepartHrApiController extends UcpBaseController {

    @Resource
    private CodeTableService codeTableService;
    @Resource
    private SysUserService userService;
    @Resource
    private GlobalTypeService globalService;

    /**
     * 查看部门HR列表信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("list")
    @ResponseBody
    @Action(description="查看部门HR设置列表")
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception{
        QueryFilter filter = new QueryFilter(request,"codeTableItem",true);
        filter.addFilter("settingType", 2);
        List<CodeTable> lists=null;
        try{
            lists = codeTableService.getAll(filter);
            for (CodeTable list : lists) {
                Long userId =Long.parseLong(list.getItemId());
                //获取部门人员ids字符串
                String value = list.getItemValue();
                SysUser user = userService.getByUserId(Long.parseLong(value));
                list.setItemValue(user.getFullname());
                list.setUserAccount(user.getAliasName());
                SysUser userValue = userService.getByUserId(userId);
                list.setUserName(userValue.getFullname());
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取数据失败！",lists);
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据成功！",lists);
    }



    /**
     * 编辑部门HR设置信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("edit")
    @ResponseBody
    @Action(description="编辑部门HR设置")
    public ResultVo edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
//        String returnUrl = RequestUtil.getPrePage(request);
        Long settingId= RequestUtil.getLong(request, "settingId", 0L);
        CodeTable codeTable = codeTableService.getById(settingId);
        //根据itemId获取用户姓名
        if(codeTable!=null){
            SysUser user = userService.getByUserId(Long.parseLong(codeTable.getItemId()));
            codeTable.setUserName(user.getFullname());
            SysUser user2 = userService.getByUserId(Long.parseLong(codeTable.getItemValue()));
            codeTable.setUserAccount(user2.getFullname());
        }
//        return getAutoView().addObject("codeTable", codeTable)
//                .addObject("returnUrl", returnUrl);
        return  new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获得数据成功！",codeTable);
    }
    /**
     * 删除部门HR信息
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("del")
    @ResponseBody
    @Action(description="删除部门HR")
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        ResultVo message = null;
        try{
            Long[] lAryId = RequestUtil.getLongAryByStr(request, "settingId");
            codeTableService.delByIds(lAryId);
            message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除部门HR设置成功!");
        }catch(Exception ex){
            message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败" + ex.getMessage());
        }
       return message;
    }
    /**
     * 添加或更新部门HR设置信息
     * @param request
     * @param response
     * @param codeTable
     * @throws Exception
     */
    @RequestMapping("save")
    @ResponseBody
    @Action(description="保存部门HR设置")
    public ResultVo save(HttpServletRequest request,HttpServletResponse response,CodeTable codeTable) throws Exception{
        ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"");
        Long settingId = RequestUtil.getLong(request, "settingId");
        try {
            String Values = codeTable.getItemValue();
            //如果是新增多个部门人员
            if(Values.contains(",")){
                String itemValues[] = Values.split(",");
                for (String itemValue : itemValues) {
                    //判断数据是否存在
                    boolean rtn =codeTableService.isExist(codeTable.getItemId(),itemValue);
                    if(rtn){
                        String userName = userService.getByUserId(Long.parseLong(itemValue)).getFullname();
                        result.setMessage(userName+"已经设置在该部门中了，请不要重复设置");
                        result.setStatus(ResultVo.COMMON_STATUS_FAILED);
                        return result;
                    }
                    codeTable.setSettingId(null);
                    codeTable.setItemValue(itemValue);
                    codeTableService.save(codeTable);
                }
            }else{
                if(codeTable.getSettingId()==null){
                    boolean rtn =codeTableService.isExist(codeTable.getItemId(),codeTable.getItemValue());
                    if(rtn){
                        String userName = userService.getByUserId(Long.parseLong(codeTable.getItemValue())).getFullname();
                        result.setMessage(userName+"已经设置在该部门中了，请不要重复设置");
                        result.setStatus(ResultVo.COMMON_STATUS_FAILED);
                        return result;
                    }
                }
                codeTableService.save(codeTable);
            }
            if(0==settingId){
                result.setMessage("添加部门HR成功");
            }else{
                result.setMessage("更新部门人员成功");
            }
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            result.setStatus(ResultVo.COMMON_STATUS_FAILED);
        }
        return result;
    }
    @RequestMapping("getEdit")
    @ResponseBody
    public ResultVo getEdit(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String returnUrl = RequestUtil.getPrePage(request);
        Long settingId= RequestUtil.getLong(request, "settingId", 0L);
        CodeTable codeTable = codeTableService.getById(settingId);
        String itemId = codeTable.getItemId();
        codeTable.setItemValue(itemId);
        SysUser user = userService.getByUserId(Long.parseLong(codeTable.getItemId()));
        codeTable.setUserName(user.getFullname());
        String userAcount ="";
        List<CodeTable> lists = codeTableService.getByItemId(itemId,codeTable.getSettingType());
        for (CodeTable list : lists) {
            SysUser userValue = userService.getByUserId(Long.parseLong(list.getItemValue()));
            userAcount+=userValue.getFullname()+",";
        }
        codeTable.setUserAccount(userAcount);
//        ModelAndView mv = new ModelAndView("/mh/codeTable/departHrEdit2.jsp");
//        mv.addObject("codeTable", codeTable)
//                .addObject("returnUrl", returnUrl);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获得数据成功！",codeTable);
    }
    @RequestMapping("saveHr")
    @ResponseBody
    public ResultVo saveHr(HttpServletRequest request,HttpServletResponse response,CodeTable codeTable) throws Exception{
        List<CodeTable> lists = codeTableService.getByItemId(codeTable.getItemValue(),codeTable.getSettingType());
        ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"");
        try {
            for (CodeTable list : lists) {
                list.setItemId(codeTable.getItemId());
                list.setDescription(codeTable.getDescription());
                codeTableService.save(list);
            }
            result.setMessage("更新部门HR成功");
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            result.setStatus(ResultVo.COMMON_STATUS_FAILED);
        }
        return result;
    }
}
