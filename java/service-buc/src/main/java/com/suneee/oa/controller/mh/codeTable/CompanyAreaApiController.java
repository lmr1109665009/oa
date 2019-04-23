package com.suneee.oa.controller.mh.codeTable;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.controller.UcpBaseController;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:企业地区设置 控制器类
 * 开发公司:深圳象翌
 * 开发人员:pengfeng
 * 创建时间:2017-08-30
 *</pre>
 */
@Controller
@RequestMapping("/mh/codeTable/companyAreaApi/")
public class CompanyAreaApiController extends UcpBaseController {


    @Resource
    private CodeTableService codeTableService;
    @Resource
    private SysUserService userService;
    @Resource
    private GlobalTypeService globalService;
    @Resource
    private DictionaryService dictionaryService;
    /**
     * 查看企业地区列表信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("list")
    @ResponseBody
    @Action(description="查看企业地区列表")
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception{
        QueryFilter filter = new QueryFilter(request,"codeTableItem",true);
        filter.addFilter("settingType", 4);
        List<CodeTable> lists =null;
        try {
            lists=codeTableService.getAll(filter);
            for (CodeTable list : lists) {
                //根据地区key获取地区名
                String nodekey = list.getItemValue();
                String itemId = list.getItemId();
                String items[] = itemId.split("-");
                String typename = dictionaryService.getByItemValue(nodekey).get(0).getItemName();
                list.setItemValue(typename);
                list.setUserAccount(items[1]);
                list.setUserName(items[0]);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取信息失败！",lists);
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取信息成功！",lists);

    }
    /**
     * 编辑企业地区信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("edit")
    @ResponseBody
    @Action(description="编辑企业地区信息")
    public ResultVo edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String returnUrl = RequestUtil.getPrePage(request);
        Long settingId= RequestUtil.getLong(request, "settingId", 0L);
        CodeTable codeTable = codeTableService.getById(settingId);
        //根据itemId获取用户姓名
        // 从数据字典中获取地区列表
        List<Dictionary> regionList = dictionaryService.getByNodeKey(ConferenceRoom.REGION_NODE_KEY);

        Map<String,Object> dataMap=new HashMap<String, Object>();
        dataMap.put("codeTable",codeTable);
        dataMap.put("regionList",regionList);
//        dataMap.put("returnUrl",returnUrl);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取信息成功！",dataMap);

//        return getAutoView().addObject("codeTable", codeTable)
//                .addObject("regionList", regionList)
//                .addObject("returnUrl", returnUrl);
    }
    /**
     * 删除企业地区信息
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("del")
    @ResponseBody
    @Action(description="删除企业地区")
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        ResultVo message = null;
        try{
            Long[] lAryId = RequestUtil.getLongAryByStr(request, "settingId");
            codeTableService.delByIds(lAryId);
            message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除企业地区成功!");
        }catch(Exception ex){
            message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败" + ex.getMessage());
        }
        return message;
    }
    /**
     * 添加或更新企业地区信息
     * @param request
     * @param response
     * @param codeTable
     * @throws Exception
     */
    @RequestMapping("save")
    @ResponseBody
    @Action(description="保存企业地区")
    public ResultVo save(HttpServletRequest request,HttpServletResponse response,CodeTable codeTable) throws Exception{
        ResultVo result=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"");
        Long settingId = RequestUtil.getLong(request, "settingId");
        try {
            codeTableService.save(codeTable);
            if(0==settingId){
                result.setMessage("添加企业地区成功");
            }else{
                result.setMessage("更新企业地区成功");
            }
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            result.setStatus(ResultVo.COMMON_STATUS_FAILED);
        }
        return result;
    }


}
