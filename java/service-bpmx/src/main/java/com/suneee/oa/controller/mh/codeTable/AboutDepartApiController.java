package com.suneee.oa.controller.mh.codeTable;

import com.suneee.core.page.PageBean;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.PageVo;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.model.codeTable.CodeTable;
import com.suneee.ucp.mh.service.codeTable.CodeTableService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:区域HR设置 控制器类
 * 开发公司:深圳象翌
 * 开发人员:pengfeng
 * 创建时间:2017-08-30
 *</pre>
 */
@Controller
@RequestMapping("/mh/codeTable/aboutDepartApi/")
public class AboutDepartApiController extends UcpBaseController {

    private Logger logger = LoggerFactory.getLogger(AboutDepartApiController.class);

    //流程审批类型
    public static final String TYPE_CONST = "rszwxg";

    @Resource
    private CodeTableService codeTableService;
    @Resource
    private SysUserService userService;
    @Resource
    private GlobalTypeService globalService;
    @Resource
    private DictionaryService dictionaryService;
    @Resource
    private SysOrgService sysOrgService;
    @Resource
    private SysUserService sysUserService;
    /**
     * 查看区域HR设置列表信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("list")
    @ResponseBody
    @Action(description="查看区域HR设置列表")
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception{
        try {
            QueryFilter filter = new QueryFilter(request, "codeTableItem", true);
            if (null != filter.getFilters().get("fullname") || null != filter.getFilters().get("aliasname")) {
                PageBean pageBean = filter.getPageBean();
                filter.setPageBean(null);
                List<SysUser> list = sysUserService.getUsersByQuery(filter);
                if (list.size() > 0) {
                    List<Long> userIdList = this.sysUserService.getUserIdList(list);
                    filter.addFilter("itemId", userIdList);
                } else {
                    PageVo pageVo = PageUtil.getPageVo(new ArrayList<>(), pageBean);
                    return new ResultVo(pageVo);
                }
                filter.setPageBean(pageBean);
            }
            String page = request.getParameter("page");
            String pageSize = request.getParameter("pageSize");
            if(StringUtils.isEmpty(page)){
                page = "1";
            }
            if(StringUtils.isEmpty(pageSize)){
                pageSize = PageBean.DEFAULT_PAGE_SIZE.toString();
            }
            filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
            filter.getPageBean().setCurrentPage(Integer.parseInt(page));
            filter.getPageBean().setPagesize(Integer.parseInt(pageSize));
            PageVo<CodeTable> pageVo = codeTableService.getOtherListApi(filter);
            List<CodeTable> lists = pageVo.getRowList();
            if(null != lists) {
                for (CodeTable list : lists) {
                    Long userId = Long.parseLong(list.getItemId());
                    List<Dictionary> types = dictionaryService.getByItemValue(list.getSettingType());
                    Long orgId = Long.parseLong(list.getItemValue());
                    SysOrg sysOrg = sysOrgService.getById(orgId);
                    SysUser user = userService.getByUserId(userId);
                    if (user != null) {
                        list.setUserAccount(user.getAliasName());
                        list.setUserName(user.getFullname());
                    }
                    if (sysOrg != null)
                        list.setItemValue(sysOrg.getOrgPathname());//这里有个报空，猜测：sysOrg对象为空。
                    if (types.size() > 0)
                        list.setSettingType(types.get(0).getItemName());
                }
            }

            //Map<String,Object> dataMap=new HashMap<String,Object>();
            //dataMap.put("typeList",typeList);
            // dataMap.put("codeTableList",lists);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取数据成功！", pageVo);
        }catch (Exception e){
            logger.error("区域HR设置列表数据失败", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取数据失败！");

        }
    }

    @RequestMapping("/getTypeList")
    @ResponseBody
    @Action(description = "获取流程审批类型")
    public ResultVo getTypeList(){
        try {
            // 从数据字典中获取流程审批类型
            List<Dictionary> typeList = dictionaryService.getByNodeKeyAndEid(TYPE_CONST,ContextSupportUtil.getCurrentEnterpriseCode());
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取数据成功", typeList);
        }catch (Exception e){
            logger.error("获取流程审批类型失败", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取流程审批类型失败");
        }
    }
    /**
     * 编辑区域HR信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("edit")
    @ResponseBody
    @Action(description="编辑区域HR")
    public ResultVo edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
        try {
            String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
            if(StringUtil.isEmpty(enterpriseCode)){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"对不起，您还没有设置组织");
            }
            Long settingId = RequestUtil.getLong(request, "settingId", 0L);
            CodeTable codeTable = codeTableService.getById(settingId);
            //根据itemId获取用户姓名
            if (codeTable != null) {
                SysUser user = userService.getByUserId(Long.parseLong(codeTable.getItemId()));
                SysOrg sysOrg = sysOrgService.getById(Long.parseLong(codeTable.getItemValue()));
                if(null != user){
                    codeTable.setUserName(user.getFullname());
                }
                if(null != sysOrg){
                    codeTable.setUserAccount(sysOrg.getOrgPathname());

                }
            }
            // 从数据字典中获取流程审批类型
            List<Dictionary> typeList = dictionaryService.getByNodeKey(this.TYPE_CONST);
            typeList = dictionaryService.getByNodeKeyAndEid(this.TYPE_CONST,enterpriseCode);
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("codeTable", codeTable);
            dataMap.put("typeList", typeList);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取数据成功！", dataMap);
        }catch (Exception e){
            logger.error("获取数据失败", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取数据失败");
        }

    }
    /**
     * 删除区域HR信息
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("del")
    @ResponseBody
    @Action(description="删除区域HR")
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        //String preUrl = RequestUtil.getPrePage(request);
        //ResultMessage message = null;
        try{
            Long[] lAryId = RequestUtil.getLongAryByStr(request, "settingId");
            codeTableService.delByIds(lAryId);
            //message = new ResultMessage(ResultMessage.Success, "删除区域HR成功!");
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除成功");
        }catch(Exception ex){
            logger.error("删除失败", ex);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败");
            //message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
        }
        //addMessage(message, request);
        //response.sendRedirect(preUrl);
    }
    /**
     * 添加或更新区域HR信息
     * @param request
     * @param response
     * @param codeTable
     * @throws Exception
     */
    @RequestMapping("save")
    @ResponseBody
    @Action(description="保存区域HR")
    public ResultVo save(HttpServletRequest request,HttpServletResponse response,CodeTable codeTable) throws Exception{
        //String resultMsg = null;
        //Long settingId = RequestUtil.getLong(request, "settingId");
        try {
            String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
            if(StringUtil.isEmpty(enterpriseCode)){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"您没有设置组织，不能执行此操作");
            }
            codeTable.setEnterpriseCode(enterpriseCode);
            codeTableService.save(codeTable);
            /*if(0==settingId){
                resultMsg="添加类型人员成功";
            }else{
                resultMsg="更新类型人员成功";
            }*/
            //writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "保存成功");
        } catch (Exception e) {
            //writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
            logger.error("保存失败", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存失败");

        }
    }

    private List<Long> getUserIdList(List<SysUser> sysUserList){
        List<Long> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(sysUserList)){
            for(SysUser sysUser : sysUserList){
                list.add(sysUser.getUserId());
            }
        }
        return list;
    }
}
