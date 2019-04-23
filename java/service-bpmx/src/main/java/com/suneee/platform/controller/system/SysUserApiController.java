package com.suneee.platform.controller.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.ldap.LdapUserService;
import com.suneee.platform.service.ldap.SysOrgSyncService;
import com.suneee.platform.service.ldap.SysUserSyncService;
import com.suneee.platform.service.system.*;
import com.suneee.platform.service.system.impl.OrgServiceImpl;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.ucp.base.service.system.SysUserExtService;
import com.suneee.ucp.base.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * 前端接口controller
 * @author ytw
 * date:2018-03-23
 * */
@Controller
@RequestMapping("/api/system/sysUser/")
public class SysUserApiController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(SysUserApiController.class);

    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysOrgService sysOrgService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private DemensionService demensionService;
    @Resource
    private SubSystemService subSystemService;
    @Resource
    private SysUserParamService sysUserParamService;
    @Resource
    private SysUserOrgService sysUserOrgService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private UserPositionService userPositionService;
    @Resource
    private LdapUserService ldapUserService;
    @Resource
    private SysOrgSyncService sysOrgSyncService;
    @Resource
    private SysUserSyncService sysUserSyncService;
    @Resource
    private UserSyncService userSyncService;
    @Resource
    private OrgAuthService orgAuthService;
    @Resource
    private OrgServiceImpl orgServiceImpl;
    @Resource
    private UserUnderService userUnderService;
    @Resource
    private PwdStrategyService pwdStrategyService;
    @Resource
    Properties configproperties;
    @Resource
    private DictionaryService dictionaryService;
    @Resource
    private SysUserExtService sysUserExtService;

    @RequestMapping("/getDemensionList")
    @ResponseBody
    @Action(description = "获取维度及子系统列表")
    public ResultVo getDemensionList(){
        List<Demension> demensionList = demensionService.getAll();
        List<SubSystem> subSystemList = subSystemService.getAll();

        Map<String, Object> map = new HashMap<>();
        map.put("demensionList", demensionList);
        map.put("subSystemList", subSystemList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "成功", map);
    }

    @RequestMapping("/getUserList")
    @ResponseBody
    @Action(description = "获取用户列表")
    public ResultVo getUserList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<SysUser> list = null;
        ModelAndView result = getAutoView();
        String searchBy = RequestUtil.getString(request, "searchBy");
        String type = RequestUtil.getString(request, "type");
        String typeVal = RequestUtil.getString(request, "typeVal");
        int includSub = RequestUtil.getInt(request, "includSub", 0);
        //String isSingle = RequestUtil.getString(request, "isSingle", "false");
        QueryFilter queryFilter = new QueryFilter(request, "sysUserItem");
        //姓名字号条件筛选时修改姓名样式
        Map<String, Object> filter = queryFilter.getFilters();
        if(StringUtil.isNotEmpty((String) filter.get("fullname"))){
            String fullname = changceFilter((String) filter.get("fullname"));
            filter.put("fullname", fullname);
        }
        if(StringUtil.isNotEmpty((String) filter.get("aliasname"))){
            String aliasname = changceFilter((String) filter.get("aliasname"));
            filter.put("aliasname", aliasname);
        }
        if (SystemConst.SEARCH_BY_ONL.equals(searchBy)) {
            String demId = RequestUtil.getString(request, "path");
            if (demId.equals("-1")) {//未分配组织的或者没有主组织的用户
                list = sysUserService.getUserNoOrg(queryFilter);
            } else {
                queryFilter.addFilter("isPrimary", 1);
                list = sysUserService.getDistinctUserByOrgPath(queryFilter);
            }
            list = sysUserService.getOnlineUser(list);
            //按组织
        } else if (SystemConst.SEARCH_BY_ORG.equals(searchBy)) {
            if (includSub == 0) {
                list = sysUserService.getDistinctUserByOrgId(queryFilter);
            } else {
                list = sysUserService.getDistinctUserByOrgPath(queryFilter);
            }
            //按岗位
        } else if (SystemConst.SEARCH_BY_POS.equals(searchBy)) {
            if (includSub == 0) {
                list = sysUserService.getDistinctUserByPosId(queryFilter);
            } else {
                list = sysUserService.getDistinctUserByPosPath(queryFilter);
            }
            //按角色
        } else if (SystemConst.SEARCH_BY_ROL.equals(searchBy)) {
            list = sysUserService.getUserByRoleId(queryFilter);
        } else {
            SysOrg sysOrg = (SysOrg) ContextUtil.getCurrentOrg();
            if (StringUtil.isNotEmpty(type) && !"all".equals(typeVal) && BeanUtils.isNotEmpty(sysOrg)) {
                String path = orgServiceImpl.getSysOrgByScope(type, typeVal).getPath();
                queryFilter.addFilter("path", path + "%");
                list = sysUserService.getDistinctUserByOrgPath(queryFilter);
            } else {
                list = sysUserService.getUserByQuery(queryFilter);
            }

        }
        List<SysUser> userList = new ArrayList<SysUser>();
        String orgNames = "";
        //循环用户
        for (SysUser user : list) {
            //获取某用户的组织列表字符串（可能多个组织）
            orgNames = userPositionService.getOrgnamesByUserId(user.getUserId());
            user.setOrgName(orgNames.toString());
            userList.add(user);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("sysUserList", userList);
        //map.put("isSingle", isSingle);
        map.put("type", type);
        map.put("typeVal", typeVal);

        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取用户列表成功", map);
    }

    /**
     * 更改查询字段(如：某某某->某%某%某)
     * @param param
     * @return
     */
    public String changceFilter(String param){
        String paramNew = "";
        String[] arr = new String[param.length()];
        for(int i = 1; i < param.length(); i++){
            arr[i] = param.substring(i, i+1);

            if(i>0&&i<param.length()-1){
                paramNew +="%"+arr[i];
            }else{
                paramNew+=arr[i];
            }
        }
        return paramNew;
    }
}
