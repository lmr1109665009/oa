package com.suneee.oa.controller.mh.codeTable;

import com.suneee.core.page.PageBean;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.PageVo;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.me.model.conference.ConferenceRoom;
import com.suneee.ucp.mh.model.codeTable.CodeTable;
import com.suneee.ucp.mh.service.codeTable.CodeTableService;
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
 * 对象功能:区域相关设置 API控制器类
 * 开发公司:深圳象翌
 * 开发人员:pengfeng
 * 创建时间:2017-08-30
 *</pre>
 */
@Controller
@RequestMapping("/mh/codeTable/typeManegeApi/")
public class TypeManageApiController extends UcpBaseController {

    //流程审批类型
    public static final String TYPE_QUYU = "qyzwxg";

    @Resource
    private CodeTableService codeTableService;
    @Resource
    private SysUserService userService;
    @Resource
    private GlobalTypeService globalService;
    @Resource
    private DictionaryService dictionaryService;
    @Resource
    private SysUserService sysUserService;

    /**
     * 查看区域相关设置
     * @return
     * @throws Exception
     */
    @RequestMapping("list")
    @ResponseBody
    @Action(description = "查看区域相关设置列表")
    public ResultVo list(HttpServletRequest request, HttpServletResponse response)throws Exception{
        try {
            QueryFilter filter = new QueryFilter(request, "codeTableItem", true);
            int page = RequestUtil.getInt(request,"page", 1);
            int pageSize = RequestUtil.getInt(request, "pageSize", PageBean.DEFAULT_PAGE_SIZE);

            filter.getPageBean().setCurrentPage(page);
            filter.getPageBean().setPagesize(pageSize);

            if (null != filter.getFilters().get("fullname") || null != filter.getFilters().get("aliasname")) {
                PageBean pageBean = filter.getPageBean();
                filter.setPageBean(null);
                List<SysUser> sysUserList = sysUserService.getUsersByQuery(filter);
                if (sysUserList.size() > 0) {
                    List<Long> userIdList = sysUserService.getUserIdList(sysUserList);
                    filter.addFilter("itemId", userIdList);
                } else {
                    PageVo pageVo = PageUtil.getPageVo(new ArrayList<>(), pageBean);
                    return new ResultVo(pageVo);
                }
                filter.setPageBean(pageBean);
            }
            filter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
            PageVo<CodeTable> pageVo = codeTableService.getTypeListApi(filter);
            List<CodeTable> lists = pageVo.getRowList();
            if(null != lists) {
                for (CodeTable list : lists) {
                    //将用户的信息进行填充
                    Long userId = Long.parseLong(list.getItemId());
                    SysUser user = userService.getByUserId(userId);
                    list.setUserAccount(user.getAliasName());
                    list.setUserName(user.getFullname());
                    //将区域的key进行翻译
                    List<Dictionary> types = dictionaryService.getByItemValue(list.getSettingType());
                    list.setSettingType(types.get(0).getItemName());
                    //将类型的key进行翻译并填充
                    String nodekey = list.getItemValue();
                    String typename = dictionaryService.getByItemValue(nodekey).get(0).getItemName();
                    list.setItemValue(typename);
                }
            }
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取列表信息成功", pageVo);
        }catch (Exception e){
            logger.error("获取列表信息失败", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取列表信息失败");
        }
    }

    @RequestMapping("/getRegionAndTypeList")
    @ResponseBody
    @Action(description = "获取地区列表及流程审批类型列表")
    public ResultVo getRegionAndTypeList(){
        try {
            String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
            // 从数据字典中获取地区列表
            List<Dictionary> regionList = dictionaryService.getByNodeKeyAndEid(ConferenceRoom.REGION_NODE_KEY,enterpriseCode);
            // 从数据字典中获取流程审批类型
            List<Dictionary> typeList = dictionaryService.getByNodeKeyAndEid(this.TYPE_QUYU,enterpriseCode);
            Map<String, Object> maplist = new HashMap<String, Object>();//构建数据的整合对象
            maplist.put("regionList", regionList);
            maplist.put("typeList", typeList);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取分类排序成功", maplist);
        }catch(Exception e){
            logger.error("getRegionAndTypeList 异常,", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取地区列表及及流程审批类型列表异常");
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
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        Long settingId= RequestUtil.getLong(request, "settingId", 0L);
        CodeTable codeTable = codeTableService.getById(settingId);
        //根据itemId获取用户姓名
        if(codeTable!=null){
            SysUser user = userService.getByUserId(Long.parseLong(codeTable.getItemId()));
            codeTable.setUserName(user.getFullname());
        }
        // 从数据字典中获取地区列表
        List<Dictionary> regionList = dictionaryService.getByNodeKeyAndEid(ConferenceRoom.REGION_NODE_KEY,enterpriseCode);
        // 从数据字典中获取流程审批类型
        List<Dictionary> typeList = dictionaryService.getByNodeKeyAndEid(this.TYPE_QUYU,enterpriseCode);

        Map<String,Object> maplist=new HashMap<String,Object>();//构建数据的整合对象
        maplist.put("codeTable",codeTable);
        maplist.put("regionList",regionList);
        maplist.put("typeList",typeList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据成功",maplist);
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
           return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除成功!");
        }catch(Exception ex){
            logger.error("删除失败", ex);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败");
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
       // Long settingId = codeTable.getSettingId();
        try {
            String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
            if(StringUtil.isEmpty(enterpriseCode)){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"您没有设置组织，不能执行此操作");
            }
            codeTable.setEnterpriseCode(enterpriseCode);
            codeTableService.save(codeTable);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "保存成功");
            //writeResultMessage(response.getWriter(), resultMsg, );
        } catch (Exception e) {
            logger.error("保存失败", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存失败");
            //writeResultMessage(response.getWriter(), resultMsg + "," + e.getMessage(), ResultMessage.Fail);
        }
    }



}
