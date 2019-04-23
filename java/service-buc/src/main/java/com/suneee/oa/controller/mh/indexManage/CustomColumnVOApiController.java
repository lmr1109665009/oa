/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ColumnApiController
 * Author:   lmr
 * Date:     2018/5/4 15:47
 * Description: 栏目
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.oa.controller.mh.indexManage;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.model.CurrentUser;
import com.suneee.core.util.PinyinUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.system.Identity;
import com.suneee.platform.service.system.IdentityService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.model.customColumn.CustomColumn;
import com.suneee.ucp.mh.model.customColumn.CustomColumnVO;
import com.suneee.ucp.mh.model.gatewayManage.GatewaySetting;
import com.suneee.ucp.mh.service.customColumn.CustomColumnService;
import com.suneee.ucp.mh.service.customColumn.CustomColumnVOService;
import com.suneee.ucp.mh.service.gatewayManage.GatewaySettingService;
import org.apache.taglibs.standard.tag.el.sql.QueryTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈栏目〉
 *
 * @author lmr
 * @create 2018/5/4
 * @since 1.0.0
 */
@Controller
@RequestMapping("/mh/index/column")
public class CustomColumnVOApiController extends UcpBaseController {
    @Resource
    private CustomColumnVOService customColumnVOService;
    @Resource
    private CustomColumnService customColumnService;
    @Resource
    private GatewaySettingService gatewaySettingService;
    private static final String SOURCE = "sys";

    enum Aliases{
        dbsy,ybsy,gsxfwj,ptwj,xttz,lbt,kjrk,rcgl,zxxx,wjgl,wjg
    }
    @RequestMapping("/list")
    @ResponseBody
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = new QueryFilter(request);
        List<CustomColumnVO> columnList = customColumnVOService.getAll(queryFilter);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取栏目列表成功！", columnList);
    }

    /**
     * 首页保存栏目的设置
     * @param column
     * @return
     * @throws Exception
     */
    @RequestMapping("/save")
    @ResponseBody
    public ResultVo save(@RequestBody CustomColumnVO column) throws Exception {
        Long id = column.getId();
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        String msg = "";
        try {
            if (id == null||id == 0 ) {
                //如果id==0则为添加
                column.setId(UniqueIdUtil.genId());
                String columnAlias = PinyinUtil.getPinYinHeadChar(column.getColumnName());
                column.setColumnAlias(this.addAlias(columnAlias, 1));
                column.setCreateBy(ContextUtil.getCurrentUserId());
                column.setOwner(ContextUtil.getCurrentUserId());
                column.setSource("def");
                column.setEnterpriseCode(enterpriseCode);
                customColumnVOService.add(column);
                msg = "添加栏目成功！";
            } else {
                //id不等于0则为编辑
                //如果是默认的栏目。不能修改。应为添加新一个新的数据

                column.setOwner(ContextUtil.getCurrentUserId());
                if (SOURCE.equals(column.getSource())) {
                    //以默认的栏目为模板进行添加
                    column.setId(UniqueIdUtil.genId());
                    column.setCustomTabList(column.getCustomTabList());
                    column.setSource("def");
                    column.setEnterpriseCode(enterpriseCode);
                    customColumnVOService.add(column);
                    customColumnService.saveCustomTab(column);
                } else {
                    customColumnVOService.update(column);
                    customColumnService.saveCustomTab(column);
                }
                msg = "编辑栏目成功！";
            }
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, msg);
        } catch (Exception e) {
            if (id == 0 || id == null) {
                msg = "添加栏目失败！";
            } else {
                msg = "编辑栏目失败！";
            }
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, msg);
        }
    }

    /**
     * 获取不重复的别名
     * @param columnAlias
     * @param count
     * @return
     */
    public String addAlias(String columnAlias, int count) {
        boolean rtn = customColumnVOService.isAliasExisted(columnAlias);
        String newName = columnAlias;
        if (rtn) {
            newName = columnAlias + count;
            count++;
            this.addAlias(newName, count);
        }
        return newName;
    }

    /**
     * 获取栏目的信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/get")
    @ResponseBody
    public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long columnId = RequestUtil.getLong(request, "columnId");
        try {
            CustomColumnVO customColumnVO = customColumnVOService.getById(columnId);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "查看栏目详情成功！", customColumnVO);
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "查看栏目详情失败！");
        }
    }

    /**
     * 通过别名获取栏目
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/getByAlias")
    @ResponseBody
    public ResultVo getByAlias(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = new QueryFilter(request);
        try {
            List<CustomColumnVO> columnList = customColumnVOService.getAll(queryFilter);
            CustomColumnVO customColumnVO = null;
            if (columnList != null && columnList.size() != 0) {
                customColumnVO = columnList.get(0);
            }
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取栏目成功！", customColumnVO);
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取栏目成功！");
        }
    }

    /**
     * 首页中获取全部的栏目
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/getAllCustom")
    @ResponseBody
    public ResultVo getAllCustom(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long owner = ContextUtil.getCurrentUserId();
        QueryFilter queryFilter = new QueryFilter(request);
        queryFilter.addFilter("owner", owner);
        queryFilter.addFilter("enterpriseCode",CookieUitl.getCurrentEnterpriseCode());
        List<String> aliases = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        //别名和栏目类型的对应map,别名暂时写死。后期用id可以更改
        map.put("dbsy",customColumnService.LCGL_TYPE);
        map.put("ybsy",customColumnService.LCGL_TYPE);
        map.put("lbt",customColumnService.LBT_TYPE);
        map.put("kjrk",customColumnService.KJRK_TYPE);
        map.put("rcgl",customColumnService.RCGL_TYPE);
        map.put("zxxx",customColumnService.ZXXX_TYPE);
        map.put("wjgl",customColumnService.WJGL_TYPE);
        map.put("wjg",customColumnService.WJG_TYPE);
        map.put("ptwj",customColumnService.WJG_TYPE);
        map.put("xttz",customColumnService.WJG_TYPE);
        map.put("gsxfwj",customColumnService.WJG_TYPE);
        try {
            List<CustomColumnVO> columnList = new ArrayList<>();
            for (Aliases alias : Aliases.values()) {
                CustomColumnVO temp = null;
                queryFilter.addFilter("columnAlias", alias.toString());
                queryFilter.addFilter("source","def");
                List<CustomColumnVO> list = customColumnVOService.getAll(queryFilter);
                if (list == null||list.size()==0) {
                    temp = customColumnVOService.getDefaultByType(alias.toString(),map.get(alias.toString()));
                    if(temp!=null){
                        temp.setColumnAlias(alias.toString());
                    }
                } else {
                    temp = list.get(0);
                }
                if(temp!=null){
                    columnList.add(temp);
                }
            }
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取栏目成功！", columnList);
        } catch (
                Exception e)
        {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取栏目失败！");
        }

    }

    /**
     * 删除
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/del")
    @ResponseBody
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long[] ids = RequestUtil.getLongAryByStr(request, "id");
        try {
            customColumnVOService.delByIds(ids);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "查看栏目详情成功！");
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "查看栏目详情失败！");
        }
    }

    /**
     * 恢复默认
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/revert")
    @ResponseBody
    public ResultVo revert(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long owner = ContextUtil.getCurrentUserId();
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        String columnAlias = RequestUtil.getString(request,"columnAlias");
        try {
            customColumnVOService.delByOwner(owner,columnAlias,enterpriseCode);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "恢复默认成功！");
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "恢复默认失败！");
        }
    }

}