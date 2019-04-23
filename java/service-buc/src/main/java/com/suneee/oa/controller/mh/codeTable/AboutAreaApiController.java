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
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:公司地区关联设置 控制器类
 * 开发公司:深圳象翌
 * 开发人员:pengfeng
 * 创建时间:2017-08-30
 *</pre>
 */
@Controller
@RequestMapping("/mh/codeTable/aboutAreaApi/")
public class AboutAreaApiController extends UcpBaseController {

    @Resource
    private CodeTableService codeTableService;
    @Resource
    private SysUserService userService;
    @Resource
    private GlobalTypeService globalService;
    @Resource
    private DictionaryService dictionaryService;
    /**
     * 查看公司地区关联设置列表信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("list")
    @ResponseBody
    @Action(description="查看公司地区关联列表")
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception{
        QueryFilter filter = new QueryFilter(request,"codeTableItem",true);
        filter.addFilter("settingType", 6);
        List<CodeTable> lists=null;
        try {
           lists = codeTableService.getAll(filter);
            for (CodeTable list : lists) {
                String nodekey = list.getItemValue();//值的类型key
                List<Dictionary> listdic = dictionaryService.getByItemValue(nodekey);//根据类型获得字典对象
                String typename = listdic.get(0).getItemName();//获得类型的名称
                list.setItemValue(typename);
                Dictionary dictionay = dictionaryService.getByItemValue(list.getItemId()).get(0);//item的主值，根据不同类型，使用不同Service
                list.setItemId(dictionay.getItemName());//将itemId翻译成中文
            }
        }catch (Exception e){
            e.printStackTrace();
            return  new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取数据失败！",lists);
        }
        return  new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取数据成功！",lists);
    }
    /**
     * 编辑公司地区关联信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("edit")
    @ResponseBody
    @Action(description="编辑公司地区关联")
    public ResultVo edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String returnUrl = RequestUtil.getPrePage(request);
        Long settingId= RequestUtil.getLong(request, "settingId", 0L);
        CodeTable codeTable = codeTableService.getById(settingId);
        //根据itemId获取用户姓名
        if(codeTable!=null){

        }
        // 从数据字典中获取地区列表
        List<Dictionary> regionList = dictionaryService.getByNodeKey(ConferenceRoom.REGION_NODE_KEY);
        //从数据字典中获取公司名称
        List<Dictionary> companyList = dictionaryService.getByNodeKey("gsmc");
//        return getAutoView().addObject("codeTable", codeTable)
//                .addObject("regionList", regionList)
//                .addObject("companyList", companyList)
//                .addObject("returnUrl", returnUrl);
        Map<String,Object> dataMap=new HashMap<String, Object>();
        dataMap.put("codeTable",codeTable);
        dataMap.put("regionList",regionList);
        dataMap.put("companyList",companyList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取信息成功！",dataMap);
    }
    /**
     * 删除公司地区关联信息
     * @param request
     * @throws Exception
     */
    @RequestMapping("del")
    @Action(description="删除公司地区关联")
    @ResponseBody
    public ResultVo del(HttpServletRequest request) throws Exception
    {
        ResultVo message = null;
        try{
            Long[] lAryId = RequestUtil.getLongAryByStr(request, "settingId");
            codeTableService.delByIds(lAryId);
            message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除公司地区关联!");
        }catch(Exception ex){
            message = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败" + ex.getMessage());
        }
       return message;
    }
    /**
     * 添加或更新公司地区关联信息
     * @param request
     * @param response
     * @param codeTable
     * @throws Exception
     */
    @RequestMapping("save")
    @Action(description="保存公司地区关联")
    @ResponseBody
    public ResultVo save(HttpServletRequest request,HttpServletResponse response,CodeTable codeTable) throws Exception{
        String resultMsg = null;
        Long settingId = RequestUtil.getLong(request, "settingId");
        ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"");
        try {
            codeTableService.save(codeTable);
            if(0==settingId){
                resultMsg="添加公司地区关联成功";
            }else{
                resultMsg="更新公司地区关联成功";
            }
            resultVo.setMessage(resultMsg);
            return resultVo;
        } catch (Exception e) {
            resultVo.setMessage(resultMsg + "," + e.getMessage());
            resultVo.setStatus(ResultVo.COMMON_STATUS_FAILED);
            return resultVo;
        }
    }


}
