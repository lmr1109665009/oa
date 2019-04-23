package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.encrypt.EncryptUtil;
import com.suneee.core.model.OnlineUser;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.oa.dao.user.SysUserExtendDao;
import com.suneee.oa.service.user.UserPositionExtendService;
import com.suneee.platform.dao.system.SysOrgDao;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.event.def.UserEvent;
import com.suneee.platform.model.bpm.BpmNodeUser;
import com.suneee.platform.model.bpm.BpmNodeUserUplow;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.bpm.BpmNodeUserService;
import com.suneee.platform.web.listener.UserSessionListener;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.util.SendMsgCenterUtils;
import com.suneee.weixin.api.IUserService;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * 对象功能:用户表 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:heyifan
 * 创建时间:2012-12-17 16:02:46
 */
@Service
public class SysUserService extends BaseService<SysUser> {
    @Resource
    private UserPositionExtendService userPositionExtendService;
    @Resource
    private SysUserDao dao;
    @Resource
    private BpmNodeUserService bpmNodeUserService;
    @Resource
    private PositionService positionService;
    @Resource
    private SysOrgDao sysOrgDao;
    @Resource
    private UserPositionService userPositionService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private SysUserOrgService sysUserOrgService;
    @Resource
    private UserUnderService userUnderService;
    @Resource
    private IUserService wxUserService;
    @Resource
    private SysOrgService sysOrgService;
    @Resource
    private GlobalTypeService globalTypeService;
    @Resource
    private SysUserExtendDao sysUserExtendDao;

    @Override
    protected IEntityDao<SysUser, Long> getEntityDao() {
        return dao;
    }

    public SysUserService() {

    }

    public SysUser getByAccount(String account) {
        return dao.getByAccount(account);
    }

    /**
     * 对象功能：根据查询条件查询用户(用于人员选择器)
     */
    public List<SysUser> getUserByQuery(QueryFilter queryFilter) {
        List<SysUser> userByQuery = dao.getUserByQuery(queryFilter);
        return userByQuery;
    }

    /**
     * 对象功能：根据查询条件查询用户(用于用户管理)
     */
    public List<SysUser> getUsersByQuery(QueryFilter queryFilter) {
        GlobalType globalType = globalTypeService.getByDictNodeKey(Constants.DIC_NODEKEY_DQ);
        Long typeId = 0L;
        if (globalType != null) {
            typeId = globalType.getTypeId();
        }

        queryFilter.addFilter("typeId", typeId);
        return dao.getUsersByQuery(queryFilter);
    }

    /**
     * 对象功能：根据查询条件查询用户(用于码表管理)
     */
    public List<SysUser> getUsersByQueryFilterForCodeManage(QueryFilter queryFilter) {
        GlobalType globalType = globalTypeService.getByDictNodeKey(Constants.DIC_NODEKEY_DQ);
        Long typeId = 0L;
        if (globalType != null) {
            typeId = globalType.getTypeId();
        }
        queryFilter.addFilter("typeId", typeId);
        return dao.getUsersByQueryForCodeManage(queryFilter);
    }


    /**
     * 对象功能：根据查询条件查询用户(用于员工管理)
     */
    public List<SysUser> getUsersByQueryFilter(QueryFilter queryFilter) {
        GlobalType globalType = globalTypeService.getByDictNodeKey(Constants.DIC_NODEKEY_DQ);
        Long typeId = 0L;
        if (globalType != null) {
            typeId = globalType.getTypeId();
        }

        queryFilter.addFilter("typeId", typeId);
        return dao.getUsersByQueryFilter(queryFilter);
    }


    /**
     * 返回某个角色的所有用户Id
     *
     * @param roleId
     * @return
     */
    public List<Long> getUserIdsByRoleId(Long roleId) {
        List<Long> ids = new ArrayList<Long>();
        List<SysUser> users = dao.getByRoleId(roleId);
        for (SysUser user : users) {
            ids.add(user.getUserId());
        }
        return ids;
    }

    /**
     * 对象功能：根据角色id查询员工
     */
    public List<SysUser> getByRoleId(Long roleId) {
        return dao.getByRoleId(roleId);
    }

    /**
     * 获取没有分配组织的用户
     *
     * @return
     */
    public List<SysUser> getUserNoOrg(QueryFilter queryFilter) {
        return dao.getUserNoOrg(queryFilter);
    }

    /**
     * 根据多个工号字符串获取对应用户列表， 注：该方法不适用于读取大量用户
     *
     * @param accounts 用户工号，以逗号隔开
     * @return 用户例表
     */
    public List<SysUser> getByAccounts(String accounts) {
        List<SysUser> users = new ArrayList<SysUser>();
        if (accounts != null) {
            String[] aAccount = accounts.split(",");
            SysUser u;
            for (String a : aAccount) {
                u = getByAccount(a);
                if (u != null) {
                    users.add(u);
                }
            }
        }
        return users;
    }

    /**
     * 对象功能：根据岗位路径查询用户
     */
    public List<SysUser> getDistinctUserByPosPath(QueryFilter queryFilter) {
        return dao.getDistinctUserByPosPath(queryFilter);
    }

    /**
     * 对象功能：根据组织path查询用户
     */
    public List<SysUser> getDistinctUserByOrgPath(QueryFilter queryFilter) {
        //如果不含组织path 传递orgId 亦可
        String path = (String) queryFilter.getFilters().get("path");
        String orgId = (String) queryFilter.getFilters().get("orgId");
        if (StringUtil.isEmpty(path) && StringUtil.isNotEmpty(orgId)) {
            SysOrg org = sysOrgDao.getById(Long.parseLong(orgId));
            if (org != null)
                queryFilter.addFilter("path", org.getPath());
        }

        return dao.getDistinctUserByOrgPath(queryFilter);
    }

    /**
     * 根据组织路径获取用户信息
     *
     * @param queryFilter
     * @return
     */
    public List<SysUser> getByOrgPath(QueryFilter queryFilter) {
        return dao.getBySqlKey("getByOrgPath", queryFilter);
    }

    /**
     * 对象功能：判断是否存在该账号
     */
    public boolean isAccountExist(String account, Long userId) {
        return dao.isAccountExist(account, userId);
    }

    /**
     * 对象功能：判断是否存在该账号
     */
    public boolean isEmailExist(String email, Long userId) {
        return dao.isEmailExist(email, userId);
    }

    /**
     * 对象功能：判断是否存在该账号
     */
    public boolean isMobileExist(String mobile, Long userId) {
        return dao.isMobileExist(mobile, userId);
    }

    /**
     * 判定帐号是否存在，在更新时使用。
     *
     * @param userId
     * @param account
     * @return
     */
    public boolean isAccountExistForUpd(Long userId, String account) {
        return dao.isAccountExistForUpd(userId, account);
    }

    /**
     * 查询用户属性
     *
     * @param userParam
     * @return
     * @throws Exception
     */
    public List<SysUser> getByUserParam(String userParam) throws Exception {
        ParamSearch search = new ParamSearch<SysUser>() {
            @Override
            /**
             * 实现查找数据的接口。
             */
            public List<SysUser> getFromDataBase(Map<String, String> property) {
                return dao.getByUserOrParam(property);
            }
        };
        return search.getByParamCollect(userParam);
    }

    /**
     * 查询组织属性
     *
     * @param userParam
     * @return
     * @throws Exception
     */
    public List<SysUser> getByOrgParam(String userParam) throws Exception {
        ParamSearch search = new ParamSearch<SysUser>() {
            @Override
            /**
             * 实现查找数据的接口。
             */
            public List<SysUser> getFromDataBase(Map<String, String> property) {
                return dao.getByOrgOrParam(property);
            }
        };
        return search.getByParamCollect(userParam);
    }

    /**
     * 通过用户属性、组织属性获取用户
     *
     * @param nodeUserId
     * @return
     * @throws Exception
     */
    public List<SysUser> getByParam(long nodeUserId) throws Exception {
        List<SysUser> list = null;
        BpmNodeUser bpmNodeUser = bpmNodeUserService.getById(nodeUserId);
        String assignType = bpmNodeUser.getAssignType();
        String param = bpmNodeUser.getCmpIds();
        if (BpmNodeUser.ASSIGN_TYPE_USER_ATTR.equals(assignType)) {
            list = getByUserParam(param);
        } else if (BpmNodeUser.ASSIGN_TYPE_ORG_ATTR.equals(assignType)) {
            list = getByOrgParam(param);
        }
        return list;
    }

    /**
     * 根据当前登陆用户与上下级关系取得当前登陆用户的上下级用户
     *
     * @param userId
     * @param nodeUserId
     * @return
     */
    public List<SysUser> getByUserIdAndUplow(long userId, BpmNodeUser bpmNodeUser) {
        String cmpIds = bpmNodeUser.getCmpIds();
        JSONArray ja = JSONArray.fromObject(cmpIds);
        List<BpmNodeUserUplow> uplowList = (List) JSONArray.toCollection(ja, BpmNodeUserUplow.class);
        return getByUserIdAndUplow(userId, uplowList);
    }

    /**
     * 根据 当前登陆用户与上下级关系取得上下级用户
     *
     * @param userId
     * @param uplowList
     * @return
     */
    public List<SysUser> getByUserIdAndUplow(long userId, List<BpmNodeUserUplow> uplowList) {
        if (uplowList == null)
            return null;
        List<SysUser> list = new ArrayList<SysUser>();
        // 当前登陆用户的主岗位
        Position pl = null;
        // 当前登陆用户的主组织
        SysOrg ol = null;
        for (BpmNodeUserUplow uplow : uplowList) {
            short upLowType = uplow.getUpLowType();
            int upLowLevel = uplow.getUpLowLevel();
            int isCharge = uplow.getIsCharge();
            Long demensionId = uplow.getDemensionId();
            if (uplow.getDemensionId().longValue() == Demension.positionDem.getDemId().longValue()) {
                // 目前岗位没有上下级
                return list;

            } else {
                // 组织
                if (ol == null)
                    // 获取主组织
                    ol = sysOrgDao.getPrimaryOrgByUserId(userId);
                // 此处需要再次判断主组织是否为空，因为当前用户可能没有设置主组织，例如：管理员
                if (ol == null || !demensionId.equals(ol.getDemId()))
                    return list;
                String currentPath = ol.getPath();
                Map<String, Object> param = handlerCondition(currentPath, upLowType, upLowLevel, isCharge);
                List<SysUser> l = dao.getUpLowOrg(param);
                // list去重
                for (SysUser user : l) {
                    if (!list.contains(user)) {
                        list.add(user);
                    }
                }

            }
        }
        return list;
    }

    /**
     * 根据 发起人与上下级关系取得上下级用户
     *
     * @param userId
     * @param
     * @return
     */
    public List<SysUser> getByUserIdAndUplow(long userId) {
        List<SysUser> list = new ArrayList<SysUser>();
        // 当前登陆用户的组织
        SysOrg ol = sysOrgDao.getPrimaryOrgByUserId(userId);
        if (ol != null) {
            String currentPath = ol.getPath();
            int currentDepth = currentPath.split("\\.").length;
            //查找上下级
            for (int depth = currentDepth; depth > 1; depth--) {
                Map<String, Object> param = handlerCondition(currentPath, depth);
                List<SysUser> l = dao.getUpLowOrg(param);
                for (SysUser user : l) {//list去重
                    if (!list.contains(user)) {
                        list.add(user);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 根据当前用户所在路径组织查询条件 //此段代码的前提是upLowType:1为上级,0为平级,-1为下级;如此方能计算正确
     *
     * @param currentPath
     * @param upLowType   1为上级,0为平级,-1为下级
     * @param upLowLevel
     * @param isCharge    判断是否为组织负责人 1为是,0为否
     * @return
     */
    private static Map<String, Object> handlerCondition(String currentPath, short upLowType, int upLowLevel, int isCharge) {

        String pathArr[] = currentPath.split("\\.");
        int currentDepth = pathArr.length;
        String path = null;
        Integer depth = null;
        String pathCondition = null;
        int depthCondition = 0;

        switch (upLowType) {
            case 1://上级
                depth = currentDepth - upLowLevel;
                pathCondition = "=";
                path = coverArray2Str(pathArr, depth);
                path += ".";
                break;
            case -1://下级
                depth = currentDepth + upLowLevel;
                pathCondition = "like";
                depthCondition = depth;
                path = currentPath + "%";
                break;
            case 0://同级
                depth = currentDepth;
                pathCondition = "like";
                path = coverArray2Str(pathArr, depth - 1) + "._%";
                depthCondition = depth;
                break;
        }

        Map<String, Object> returnMap = new HashMap<String, Object>();
        //查找路径
        returnMap.put("path", path);
        //depthCondition用于判断是否要按组织、岗位的层次进行具体层次的查找，主要用于上几级或下几级的查找，0为不进行层次查找
        returnMap.put("depthCondition", depthCondition);
        //isCharge用于判断是否要查找负责人，0为不查找，1为查找
        returnMap.put("isCharge", isCharge);
        //路径条件，或为‘=’，或为‘like’
        returnMap.put("pathCondition", pathCondition);
        return returnMap;
    }

    /**
     * 根据当前用户所在路径组织查询条件
     *
     * @param currentPath
     * @param depth
     * @return
     */
    private static Map<String, Object> handlerCondition(String currentPath, int depth) {

        String pathArr[] = currentPath.split("\\.");
        String path = null;
        String pathCondition = "=";
        if (depth == pathArr.length) {
            path = coverArray2Str(pathArr, depth - 1) + "._%";
            pathCondition = "like";
        } else {
            path = coverArray2Str(pathArr, depth);
            path += ".";
        }
        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.put("path", path);
        //isCharge用于判断是否要查找负责人，0为不查找，1为查找
        returnMap.put("isCharge", 0);
        returnMap.put("pathCondition", pathCondition);
        //depthCondition用于判断是否要按组织、岗位的层次进行具体层次的查找，主要用于上几级或下几级的查找，0为不进行层次查找
        returnMap.put("depthCondition", 0);
        return returnMap;
    }

    /**
     * 将数组路径转化为字符串
     *
     * @param pathArr
     * @param len
     * @return
     */
    private static String coverArray2Str(String pathArr[], int len) {
        if (len < 0)
            return pathArr[0];
        if (len > pathArr.length)
            len = pathArr.length;

        StringBuilder sb = new StringBuilder();
        if (pathArr.length > 1) {
            int i = 0;
            do {
                sb.append(pathArr[i]);
                sb.append(".");
                i++;
            } while (i < len);

            sb = sb.delete(sb.length() - 1, sb.length());
        } else if (pathArr.length > 0)
            sb = sb.append(pathArr[0]);
        return sb.toString();
    }

    /**
     * 获取在线用户
     *
     * @param list
     * @return
     */
    public List<SysUser> getOnlineUser(List<SysUser> list) {
        List<SysUser> listOnl = new ArrayList<SysUser>();
        Map<Long, OnlineUser> onlineUsers = UserSessionListener.getOnLineUsers();
        List<OnlineUser> onlineList = new ArrayList<OnlineUser>();
        for (Long userId : onlineUsers.keySet()) {
            OnlineUser onlineUser = onlineUsers.get(userId);
            onlineList.add(onlineUser);
        }
        for (SysUser sysUser : list) {
            for (OnlineUser onlineUser : onlineList) {
                Long sysUserId = sysUser.getUserId();
                Long onlineUserId = onlineUser.getUserId();
                if (sysUserId.toString().equals(onlineUserId.toString())) {
                    listOnl.add(sysUser);
                }
            }
        }
        return listOnl;
    }

    /**
     * 按用户Id组取到该用户列表
     *
     * @param uIds
     * @return
     */
    public List<SysUser> getByIdSet(Set uIds) {
        return dao.getByIdSet(uIds);
    }

    public SysUser getByMail(String address) {
        return dao.getByMail(address);
    }

    /**
     * 获取所有用户（包含用户的组织ID）
     *
     * @return
     */
    public List<SysUser> getAllIncludeOrg() {
        return dao.getAll();
    }

    /**
     * 更新用户密码。
     *
     * @param userId 用户id
     * @param pwd    明文密码。
     * @throws Exception
     */
    public void updPwd(Long userId, String pwd) throws Exception {
        String enPassword = EncryptUtil.encrypt32MD5(pwd);
        dao.updPwd(userId, enPassword);
    }

    /**
     * 更新用户的状态。
     *
     * @param userId 用户id
     * @param status 1，激活，0，禁用，-1，删除
     * @param isLock 0，未锁定，1，锁定
     */
    public void updStatus(Long userId, Short status, Short isLock) throws IOException {
        SysUser sysUser = this.getById(userId);
        dao.updStatus(userId, status, isLock);
        sysUser.setStatus(status);
        if (isLock != null) {
            sysUser.setIsLock(isLock);
        }

        EventUtil.publishUserEvent(sysUser, UserEvent.ACTION_UPD, false);

        SendMsgCenterUtils sendMsgCenterUtils = new SendMsgCenterUtils();
        //同步到消息中心(本部)

        List<Map<String, Object>> positonByUserId = userPositionExtendService.getPositonByUserId(userId);
        sysUser.setDeptRole(positonByUserId);
        sendMsgCenterUtils.sendToUserInfoCenter(sysUser, Constants.MESSAGE_STATUS_UPDATE, AppConfigUtil.get(Constants.MESSAGE_USER_TOPIC));


    }

    /**
     * 更新用户的状态。
     *
     * @param account 用户账号
     * @param status  1，激活，0，禁用，-1，删除
     * @param isLock  0，未锁定，1，锁定
     */
    public void updStatus(String account, Short status, Short isLock) throws IOException {
        SysUser sysUser = dao.getByAccount(account);
        if (sysUser != null) {
            this.updStatus(sysUser.getUserId(), status, isLock);
        }
    }

    /**
     * 保存用户对象
     *
     * @param bySelf
     * @param sysUser      用户对象
     * @param
     * @param posIds       岗位ID
     * @param posIdPrimary 主岗位
     * @param roleIds      用户角色
     * @throws Exception
     */
    public void saveUser(Integer bySelf, SysUser sysUser, Long[] posIdCharge, Long[] posIds, Long posIdPrimary, Long[] roleIds) throws Exception {
        int event = UserEvent.ACTION_ADD;
        sysUser.setSyncToUc(SysUser.SYNC_UC_NO);
        if (sysUser.getUserId() == null) {
            sysUser.setUserId(UniqueIdUtil.genId());
            sysUser.setIsExpired(SysUser.UN_EXPIRED);
            sysUser.setIsLock(SysUser.UN_LOCKED);
        } else {
            event = UserEvent.ACTION_UPD;
        }
        if (bySelf == 0) {
            Long userId = sysUser.getUserId();
            //保存用户 和岗位的对应关系
            userPositionService.saveUserPos(userId, posIds, posIdPrimary, posIdCharge);
            // 保存与角色的映射关系。
            userRoleService.saveUserRole(userId, roleIds);
            //保存上级数据
            userUnderService.saveSuperior(userId, sysUser.getFullname(), sysUser.getSuperiorIds());

        }
        // 用户所属组织集团编码与账号的组合值：集团编码_account，主要用于解决定子链多企业账号重复问题
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        String loginAccount = sysUser.getAccount();
        if (StringUtils.isNotBlank(enterpriseCode)) {
            loginAccount = enterpriseCode + Constants.SEPARATOR_UNDERLINE + sysUser.getAccount();
        }
        sysUser.setLoginAccount(loginAccount);

        // 保存用户信息
        if (event == UserEvent.ACTION_ADD) {
            dao.add(sysUser);
        } else {
            dao.update(sysUser);
        }
        EventUtil.publishUserEvent(sysUser, event, true);
    }

    /**
     * 通过用户来源类型获取用户列表
     *
     * @param type
     * @return
     */
    public List<SysUser> getByFromType(Short type) {
        return dao.getByFromType(type);
    }

    /**
     * 根据角色id查询员
     */
    public List<SysUser> getUserByRoleId(QueryFilter queryFilter) {
        return dao.getBySqlKey("getUserByRoleId", queryFilter);
    }

    /**
     * 手机用户的查询
     *
     * @param filter
     * @return
     */
    public List<SysUser> getAllMobile(QueryFilter filter) {
        return dao.getAllMobile(filter);
    }

    /**
     * 取得某个部门下有某个角色的人员列表
     *
     * @param roleId
     * @param orgId
     * @return
     */
    public List<SysUser> getUserByRoleIdOrgId(Long roleId, Long orgId) {
        return dao.getUserByRoleIdOrgId(roleId, orgId);
    }

    /**
     * 取得某个部门下有某个岗位的人员列表
     *
     * @param orgId
     * @param posId
     * @return
     */
    public List<SysUser> getByOrgIdPosId(Long orgId, Long posId) {
        return dao.getByOrgIdPosId(orgId, posId);
    }

    public List<SysUser> getDistinctUserByOrgId(QueryFilter queryFilter) {
        // TODO Auto-generated method stub
        return dao.getDistinctUserByOrgId(queryFilter);
    }

    public List<SysUser> getDistinctUserByPosId(QueryFilter queryFilter) {
        // TODO Auto-generated method stub
        return dao.getDistinctUserByPosId(queryFilter);
    }

    /**
     * 根据职务列表获取人员。
     *
     * @param
     * @return
     */
    public List<SysUser> getUserListByJobId(Long jobId) {
        List<Long> list = new ArrayList<Long>();
        list.add(jobId);
        return dao.getByJobIds(list);
    }

    /**
     * 根据职务列表获取人员。
     *
     * @param
     * @return
     */
    public List<SysUser> getUserListByPosId(Long posId) {
        return dao.getByPosId(posId);
    }

    /**
     * 普通用户修改个人信息
     *
     * @param sysUser
     */
    public void updateCommon(SysUser sysUser) {
        dao.updateCommon(sysUser);
        EventUtil.publishUserEvent(sysUser, UserEvent.ACTION_UPD, true);
    }

    /**
     * 更新签章图片
     *
     * @param params
     */
    public void updateWebSign(Map<String, Object> params) {
        dao.update("updateWebSign", params);
    }

    /**
     * 联系人根据邮件地址和userid找是否存在用户
     *
     * @param email
     * @param
     * @return
     */
    public List<SysUser> findLinkMan(String email) {
//		List<SysUser> userList = new ArrayList<SysUser>();
//		Map<String,Object> param= new HashMap<String,Object>();
//		param.put("email", email);
//		param.put("userId", userId);
//		userList=dao.getBySqlKey("findLinkMan", param);
//		return userList;
        return dao.getBySqlKey("findLinkMan", email);
    }

    /**
     * 同步指定用户到微信通讯录
     * 如果人员为空则同步所有用户
     *
     * @param lAryId
     */
    public void syncUserToWx(Long[] lAryId) {
        List<SysUser> userList = new ArrayList<SysUser>();
        sysOrgService.syncAllOrg();
        if (BeanUtils.isNotEmpty(lAryId)) {
            for (int i = 0; i < lAryId.length; i++) {
                SysUser user = dao.getById(lAryId[i]);
                if (user != null) userList.add(user);
            }
        } else {
            userList = dao.getAll();
        }
        wxUserService.addAll(userList);
    }

    /**
     * 根据传入的组织获取用户列表。
     *
     * @param list
     * @return
     */
    public List<SysUser> getByOrgIds(List<Long> list) {
        return dao.getByOrgIds(list);
    }

    /**
     * 根据传入的用户ID获取用户列表。
     *
     * @param list
     * @return
     */
    public List<SysUser> getByUserIdsWithOrgName(List<Long> list) {
        return dao.getBySqlKey("getByUserIdsWithOrgName", list);
    }

    public SysUser getByUserId(Long supId) {

        return dao.getByUserId(supId);
    }

    public SysUser getSupUserByUserId(Long orgId) {
        // TODO Auto-generated method stub
        return dao.getSupUserByUserId(orgId);
    }

    public List<SysUser> getByFullName(String fullname) {

        return dao.getByFullName(fullname);
    }

    /**
     * 根据手机号获取用户信息
     *
     * @param mobile
     * @return
     */
    public SysUser getByMobile(String mobile) {
        return dao.getByMobile(mobile);
    }

    /**
     * 获取list中的所有userId
     */
    public List<Long> getUserIdList(List<SysUser> sysUserList) {
        List<Long> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sysUserList)) {
            for (SysUser sysUser : sysUserList) {
                list.add(sysUser.getUserId());
            }
        }
        return list;
    }

    /**
     * 添加图片路径
     *
     * @param userId
     * @param path
     */
    public void addHeadImg(Long userId, String path) {
        dao.addheadImg(userId, path);
    }

    /**
     * 修改用户信息
     *
     * @param user
     */
    public int updateSysUser(SysUser user) {
        return sysUserExtendDao.updateUser(user);
    }

    /**
     * 根据ucUserId查找用户
     *
     * @param
     */
    public SysUser getByUcUserId(Long ucUserId) {

        return dao.getByUcUserId(ucUserId);
    }

    /**
     * 大行政修改用户信息
     */
    public Boolean updateBySysUser(SysUser sysUser) throws IOException {
        Long userId = ContextUtil.getCurrentUserId();
        sysUser.setUpdateBy(userId);
        Boolean aBoolean = dao.updateBySysUser(sysUser);
        if(aBoolean){
            SendMsgCenterUtils sendMsgCenterUtils=new SendMsgCenterUtils();
            SysUser byUserId = this.getByUserId(sysUser.getUserId());
            List<Map<String, Object>> positonByUserId = userPositionExtendService.getPositonByUserId(sysUser.getUserId());
            byUserId.setDeptRole(positonByUserId);
            //同步到消息中心
            sendMsgCenterUtils.sendToUserInfoCenter(byUserId,Constants.MESSAGE_STATUS_UPDATE,AppConfigUtil.get(Constants.MESSAGE_USER_TOPIC));
        }
        return aBoolean;
    }

    /**
     * 用户中心获取到没有同步的用户关联职位组织
     *
     * @param
     * @return
     */
    public Boolean setUserPosOrg(Map param) {
        return dao.setUserPosOrg(param);
    }

    /**
     * 通过用户来源类型获取用户列表
     *
     * @param type
     * @return
     */
    public void saveUserAndEnterprise(Map param) {


    }
}
