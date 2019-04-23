package com.suneee.ucp.mh.service.gatewayManage;

import com.alibaba.fastjson.JSON;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.platform.dao.bpm.ProcessRunDao;
import com.suneee.platform.dao.system.JobDao;
import com.suneee.platform.dao.system.SysOrgDao;
import com.suneee.platform.model.bpm.BpmProCopyto;
import com.suneee.platform.model.bpm.BpmProTransTo;
import com.suneee.platform.model.bpm.BpmTaskExe;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.Job;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.ucp.base.common.ResultConst;
import com.suneee.ucp.base.enums.IsDelete;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.dao.gateway.GatewaySettingDao;
import com.suneee.ucp.mh.dao.shortcut.ShortCutDao;
import com.suneee.ucp.mh.model.gatewayManage.GatewaySetting;
import com.suneee.ucp.mh.model.shortcut.ShortCut;
import com.suneee.ucp.mh.model.shortcut.ShortCutCustomInfoVO;
import com.suneee.ucp.mh.model.shortcut.ShortCutVO;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ShortCutService extends UcpBaseService<ShortCut> {

    //缓存有效期
    private final int expireMilliseconds = 60 * 3 * 1000;

    private static final String USER_CACHE_PREFIX = "userId_";

    private static final String SHORT_CUT_CACHE_PREFIX = "shortcut_";

    @Autowired
    private ShortCutDao shortCutDao;

    @Resource
    private BpmService bpmService;

    @Resource
    private ProcessRunService processRunService;

    @Resource
    private ProcessRunDao processRunDao;

    @Resource
    private BpmTaskExeService bpmTaskExeService;

    @Resource
    private BpmProTransToService bpmProTransToService;

    @Resource
    private BpmProCopytoService bpmProCopytoService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GatewaySettingDao gatewaySettingDao;

    @Autowired
    private GatewaySettingService gatewaySettingService;

    @Autowired
    private SysOrgDao sysOrgDao;

    @Autowired
    private JobDao jobDao;

    @Autowired
    private GlobalTypeService globalTypeService;

    @Override
    protected IEntityDao<ShortCut, Long> getEntityDao() {
        return shortCutDao;
    }

    /**
     * 获取用户快捷入口缓存的key
     * */
    public String getUserShortCutCacheKey(Long userId){
        return USER_CACHE_PREFIX + userId+"enterpriseCode_"+ CookieUitl.getCurrentEnterpriseCode();
    }

    /**
     * 获取快捷入口缓存的key
     * */
    public String getShortCutCacheKey(Long shortcutId){
        return SHORT_CUT_CACHE_PREFIX + shortcutId;
    }

    /**
     * 获取用户自定义的快捷入口
     * */
    public ResultVo getCustomShortCutByUserId(Long userId,String enterpriseCode){
        String key = getUserShortCutCacheKey(userId);
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String shortCutVOJSON = operations.get(key);
       // String shortCutVOJSON="";
        List<ShortCutVO> showList;
        //缓存中没查到，重建缓存
        if(StringUtils.isEmpty(shortCutVOJSON)){
            logger.info("rebuild cache, shorcut");
            List<ShortCut> list = this.shortCutDao.getCustomShortCutByUserId(userId,enterpriseCode);
            showList = convert2ShorcutVo(list);
            //重建缓存
            operations.set(key, JSON.toJSONString(showList), expireMilliseconds, TimeUnit.MILLISECONDS);
        }else{
            showList = JSON.parseArray(shortCutVOJSON, ShortCutVO.class);
            for (ShortCutVO shortCutVO:showList) {
                shortCutVO.setNum(getUnReadCount(shortCutVO));
            }
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "成功", showList);
    }

    /**
     * 为用户初始化默认的快捷入口
     * */
    public ResultVo initCustomShortCut(Long userId){
        QueryFilter filter = new QueryFilter();
        filter.addFilter("isDelete", IsDelete.NOT_DEL.getCode());
        //默认快捷入口
        filter.addFilter("isDefault", 1);
        filter.addFilter("limit",8);
        //默认快捷入口list
        List<ShortCut> list = this.shortCutDao.getAll(filter);
        List<ShortCut> shortCuts = new ArrayList<>();
        for(int i =0;i<list.size();i++){
            ShortCut shortCut = list.get(i);
            shortCut.setOrder(i);
            shortCut.setEnterpriseCode(CookieUitl.getCurrentEnterpriseCode());
            shortCuts.add(shortCut);
        }
//        for(ShortCut shortCut : list){
//            shortCut.setOrder(1);
//            shortCut.setEnterpriseCode(CookieUitl.getCurrentEnterpriseCode());
//        }
        //初始化用户的快捷入口
        Map<String, Object> params = new HashMap<>();
        params.put("list", shortCuts);
        params.put("userId", userId);
        try{

            this.shortCutDao.saveCustomShortCut(params);
        }catch (Exception e){
            e.printStackTrace();
        }
        List<ShortCutVO> showList = convert2ShorcutVo(shortCuts);

        String key = getUserShortCutCacheKey(userId);
        //放入缓存
        this.redisTemplate.opsForValue().set(key, JSON.toJSONString(showList), expireMilliseconds, TimeUnit.MILLISECONDS);
        return new ResultVo(showList);
    }

    /**
     * 从门户模板中初始化快捷入口
     * */
    public ResultVo initCustomShortCut2(Long userId){
        Long jobId = ContextUtil.getCurrentPos().getJobId();
        Long orgId = ContextUtil.getCurrentOrgId();
        SysOrg sysOrg = this.sysOrgDao.getById(orgId);
        Assert.notNull(sysOrg, "初始化快捷入口失败，查询不到用户所属组织");
        Job job = this.jobDao.getById(userId);
        Assert.notNull(job, "初始化快捷入口失败，查询不到用户的职位");
        String orgCode = sysOrg.getOrgCode();
        Short grade = job.getGrade();
        Map<String, Object> params = new HashMap<>();
        params.put("orgCode", orgCode);
        params.put("grade", grade);
        GatewaySetting gatewaySetting = (GatewaySetting) this.gatewaySettingDao.getOne("getByOrgCodeAndGrade", params);
        if(gatewaySetting == null){
            return ResultVo.getSuccessInstance();
        }
        List<ShortCut> shortCutList = this.gatewaySettingService.extractShortCutLayout(gatewaySetting);
        List<ShortCutVO> showList = convert2ShorcutVo(shortCutList);

        String key = getUserShortCutCacheKey(userId);
        //放入缓存
        this.redisTemplate.opsForValue().set(key, JSON.toJSONString(showList), expireMilliseconds, TimeUnit.MILLISECONDS);
        return new ResultVo(showList);
    }


    /**
     * 转换为VO对象并查询对应的未读消息数
     * */
    private List<ShortCutVO> convert2ShorcutVo(List<ShortCut> shortCutList){
        List<ShortCutVO> showList = new ArrayList<>();
        for (ShortCut shortCut : shortCutList) {
            ShortCutVO vo = buildVO(shortCut);
            //获取未读消息数
            int unReadNum = getUnReadCount(shortCut);
            vo.setNum(unReadNum);
            showList.add(vo);
        }
        return showList;
    }
    /**
     * 保存用户自定义快捷方式
     * */
    public ResultVo saveCustomShortCut(List<ShortCut> shortCutList){
        Long userId = ContextUtil.getCurrentUserId();
        //先清缓存
        this.redisTemplate.delete(getUserShortCutCacheKey(userId));

        //先删除之前的自定义快捷方式
        String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("enterpriseCode",enterpriseCode);
        this.shortCutDao.deleteCustomShortCut(map);
        Map<String, Object> params = new HashMap<>();
        params.put("list", shortCutList);
        params.put("userId",userId);
        //保存自定义快捷方式
        if(!CollectionUtils.isEmpty(shortCutList)){
            this.shortCutDao.saveCustomShortCut(params);
        }else{
            //存入一条标识数据，标识用户删除了所有快捷入口
            this.shortCutDao.update("saveNoneCustomShortCut", map);
        }
        return ResultVo.getSuccessInstance();
    }

    /**
     * 构建ShortCutVO=对象
     * */
    public ShortCutVO buildVO(ShortCut shortCut){
        ShortCutVO vo = new ShortCutVO();
        vo.setId(shortCut.getId());
        vo.setIcon(shortCut.getIcon());
        vo.setName(shortCut.getName());
        vo.setRouter(shortCut.getRouter());
        vo.setColor(shortCut.getColor());
        vo.setMobileItem(shortCut.getMobileItem());
        return vo;
    }

    private int getUnReadCount(ShortCut shortCut){
        String flag = shortCut.getRouter();
        int unReadNum = 0;
        if(flag == null){
            return unReadNum;
        }
        Set<Long> typeIds=getFlowTypeIds();
        switch (flag){
            case "/bpm/myDraftList" :
                unReadNum = getDraftNum(typeIds);
                break;
            case "/bpm/accordingList" :
                unReadNum = getAccordingNum(typeIds);
                break;
           /* case "matterList" :
                unReadNum = getCompleteNum();
                break;*/
            case "/bpm/matterList" :
                unReadNum = getAlreadyNum(typeIds);
                break;
            case "/bpm/myCopyList" :
                unReadNum = getForwardNum(typeIds);
                break;
            case "/bpm/myGaveToList" :
                unReadNum = getGaveToNum(typeIds);
                break;
            case "/bpm/myRequestList" :
                unReadNum = getMyApplyNum(typeIds);
                break;
            case "/bpm/mattersList" :
                unReadNum = getPenddingNum(typeIds);
                break;
            case "/bpm/completedList" :
                unReadNum = getCompleteNum(typeIds);
             default: break;
        }
        return unReadNum;
    }

    /**
     * 流程分类条件过滤
     */
    private Set<Long> getFlowTypeIds() {
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, false);
        Set<Long> typeIdList = BpmUtil.getTypeIdList(globalTypeList);
        return typeIdList;
    }

    /**
     * 获得未读的代办任务的数量
     *
     * @param typeIds*/
    public int getPenddingNum(Set<Long> typeIds){
        QueryFilter filter = new QueryFilter();
        //待办数量
        filter.addFilter("typeIds",typeIds);
        List<TaskEntity> pendingCountList = bpmService.getMyTasks(filter);
        return pendingCountList == null ? 0 : pendingCountList.size();
    }

    /**
     * 获取未读的已办任务的数量
     *
     * @param typeIds*/
    public int getAlreadyNum(Set<Long> typeIds){
        Long userId = ContextUtil.getCurrentUserId();
        //我的已办
        QueryFilter filter=new QueryFilter();
        filter.addFilter("assignee",userId);
        filter.addFilter("typeIds",typeIds);
        List<ProcessRun> matterList = processRunDao.getAlreadyMattersList(filter);
        return matterList == null ? 0 : matterList.size();
    }

    /**
     * 获取未读的我的申请数量
     *
     * @param typeIds*/
    public int getMyApplyNum(Set<Long> typeIds){
        //我的申请
        QueryFilter myRequest = new QueryFilter();
        myRequest.addFilter("creatorId", ContextUtil.getCurrentUserId());
        myRequest.addFilter("typeIds",typeIds);
        List<ProcessRun> myRequestList = processRunService.getMyRequestList(myRequest);
        return myRequestList == null ? 0 : myRequestList.size();
    }
    /**
     * 获取未读的我的办结的数量
     * */
    public int getCompleteNum(Set<Long> typeIds){
        QueryFilter myRequest = new QueryFilter();
        myRequest.addFilter("typeIds",typeIds);
        //我的办结
        List<ProcessRun> completedList = processRunService.getMyCompletedList(myRequest);
        return completedList == null ? 0 : completedList.size();
    }

    /**
     * 获取未读的我的草稿的数量
     *
     * @param typeIds*/
    public int getDraftNum(Set<Long> typeIds){
        Long userId = ContextUtil.getCurrentUserId();
        //我的草稿
        QueryFilter filter=new QueryFilter();
        filter.addFilter("userId",userId);
        filter.addFilter("typeIds",typeIds);
        List<ProcessRun> myDraftList = processRunDao.getMyDraft(filter);
        return myDraftList == null ? 0 : myDraftList.size();
    }

    /**
     * 获取未读的我的交办的数量
     *
     * @param typeIds*/
    public int getAccordingNum(Set<Long> typeIds){
        Long userId = ContextUtil.getCurrentUserId();
        QueryFilter filter = new QueryFilter();
        //我的交办
        filter.addFilter("ownerId", userId);
        filter.addFilter("typeIds",typeIds);
        List<BpmTaskExe> accordingList = bpmTaskExeService.accordingMattersList(filter);
        return accordingList == null ? 0 : accordingList.size();
    }

    /**
     * 获取未读的我的加签的数量
     *
     * @param typeIds*/
    public int getGaveToNum(Set<Long> typeIds){
        Long userId = ContextUtil.getCurrentUserId();
        //我的加签
        QueryFilter myGaveTo = new QueryFilter();
        myGaveTo.addFilter("exceptDefStatus", 3);
        myGaveTo.addFilter("createUserId", userId);
        myGaveTo.addFilter("typeIds",typeIds);
        List<BpmProTransTo> myGaveTolist = bpmProTransToService.mattersList(myGaveTo);
        return myGaveTolist == null ? 0 : myGaveTolist.size();
    }

    /**
     * 获取未读的他人转发的数量
     *
     * @param typeIds*/
    public int getForwardNum(Set<Long> typeIds){
        //他人转发
        QueryFilter copyList = new QueryFilter();
        copyList.getFilters().put("ccUid", ContextUtil.getCurrentUserId());
        copyList.addFilter("typeIds",typeIds);
        List<BpmProCopyto> myCopyList=bpmProCopytoService.getMyList(copyList);
        return myCopyList == null ? 0 : myCopyList.size();
    }

    /**
     * 获取快捷入口自定义编辑页面数据
     * */
    public ResultVo getCustomEditInfo(){
        try {
            Long userId = ContextUtil.getCurrentUserId();
            String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
            Map param = new HashMap();
            param.put("userId",userId);
            param.put("enterpriseCode",enterpriseCode);
            //已选快捷入口
            List<ShortCut> selectedShortCutList = this.shortCutDao.getBySqlKey("getSelectedByUserId", param);

            //未选择的快捷入口
            List<ShortCut> unSelectedShortCutList = this.shortCutDao.getBySqlKey("getUnSelectedByUserId", param);
            ShortCutCustomInfoVO vo = new ShortCutCustomInfoVO(selectedShortCutList, unSelectedShortCutList);
            return new ResultVo(ResultConst.COMMON_STATUS_SUCCESS, "获取成功", vo);
        }catch (Exception e){
            logger.error("获取快捷入口自定义编辑页面数据异常", e);
            return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "获取数据失败");
        }
    }

    /**
     * 获取默认快捷入口
     * */
    public ResultVo getDefault(){
        try {
            QueryFilter filter = new QueryFilter();
            filter.addFilter("isDelete", IsDelete.NOT_DEL.getCode());
            //默认快捷入口list
            List<ShortCut> defaultShortCutList = this.shortCutDao.getAll(filter);

            //选择前8个快捷入口，后面2个作为备选择的入口

            List<ShortCut> selectedShortCutList = defaultShortCutList.subList(0,8);
            List<ShortCut> unselectedShortCutList = defaultShortCutList.subList(8,11);

            //filter.addFilter("isDefault", 0);
            //List<ShortCut> notDefaultShortCutList = this.shortCutDao.getAll(filter);

            ShortCutCustomInfoVO vo = new ShortCutCustomInfoVO(selectedShortCutList, unselectedShortCutList);
            return new ResultVo(ResultConst.COMMON_STATUS_SUCCESS, "获取成功", vo);
        }catch (Exception e){
            logger.error("获取默认快捷入口异常", e);
            return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "获取数据失败");
        }
    }

    /**
     * 尝试从缓存中根据id获取快捷入口，
     * */
    public ShortCut getByIdUseCache(Long id){
        ValueOperations<String, String> operations = this.redisTemplate.opsForValue();
        String cacheKey = getShortCutCacheKey(id);
        String cacheJSON = operations.get(cacheKey);
        if(!StringUtils.isEmpty(cacheJSON)){
            ShortCut shortCut = JSON.parseObject(cacheJSON, ShortCut.class);
            return shortCut;
        }
        ShortCut shortCut = this.shortCutDao.getById(id);
        operations.set(cacheKey, JSON.toJSONString(shortCut), 60, TimeUnit.MINUTES);
        return shortCut;
    }


    @Override
    public void update(ShortCut entity) {
        Long id = entity.getId();
        String cacheKey = getShortCutCacheKey(id);
        redisTemplate.delete(cacheKey);
        super.update(entity);
    }

    @Override
    public void delByIds(Long[] ids) throws IOException {
        Set<String> delKeys = new HashSet<>();
        for(Long id : ids){
            delKeys.add(getShortCutCacheKey(id));
        }
        //清除缓存
        this.redisTemplate.delete(delKeys);
        this.shortCutDao.delBySqlKey("deleteByShortCutId", ids);
        super.delByIds(ids);
    }

    public void delByGatewayId(Long id,Long userId) {
        Map<String,Long> param = new HashMap<>();
        param.put("gatewayId",id);
        param.put("createBy",userId);
        shortCutDao.delBySqlKey("delByGatewayId",param);
    }
}
