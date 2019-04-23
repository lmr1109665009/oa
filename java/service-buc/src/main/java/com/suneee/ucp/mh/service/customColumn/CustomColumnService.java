package com.suneee.ucp.mh.service.customColumn;

import com.alibaba.fastjson.JSON;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.page.PageBean;
import com.suneee.core.util.LongUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.oa.dao.docFile.DocFileDao;
import com.suneee.oa.model.docFile.DocFile;
import com.suneee.oa.service.docFile.DocFileService;
import com.suneee.platform.dao.bpm.*;
import com.suneee.platform.dao.form.BpmFormHandlerDao;
import com.suneee.platform.dao.system.SysUserDao;
import com.suneee.platform.model.bpm.BpmProCopyto;
import com.suneee.platform.model.bpm.BpmProTransTo;
import com.suneee.platform.model.bpm.BpmTaskExe;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.bpm.TaskOpinionService;
import com.suneee.platform.service.bpm.util.BpmUtil;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.PageVo;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.dao.customColumn.CustomColumnDao;
import com.suneee.ucp.mh.model.customColumn.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 首页栏目自定义tab的service
 *
 * @author ytw
 */
@Service
public class CustomColumnService extends UcpBaseService<CustomColumn> {

    //已办事宜栏目标识
    public static final String ALREADY = "ybsy";

    //待办事宜栏目标识
    public static final String PENDDING = "dbsy";

    //我的申请
    public static final String MYREQUEST = "wdsq";

    //我的办结
    public static final String MYCOMPLETE = "wdbj";

    //我的交办
    public static final String ACCORDING = "wdjb";

    //我的加签
    public static final String MATTERS = "wdjq";

    //他人转发
    public static final String COPYLIST = "trzf";

    public static final String MYDRAFT = "wdcg";

    public static final String LBT_TYPE = "lbt";
    public static final String KJRK_TYPE = "kjrk";
    public static final String RCGL_TYPE = "rcgl";
    public static final String LCGL_TYPE = "lcgl";
    public static final String ZXXX_TYPE = "zxxx";
    public static final String WJGL_TYPE = "wjgl";
    public static final String WJG_TYPE = "wjg";


    @Autowired
    private CustomColumnDao customColumnDao;

    @Autowired
    private CustomColumnVOService customColumnVOService;

    @Autowired
    private DocFileService docFileService;

    @Autowired
    private ProcessRunDao processRunDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private BpmTaskExeDao bpmTaskExeDao;

    @Autowired
    private BpmProTransToDao bpmProTransToDao;

    @Autowired
    private BpmProCopytoDao bpmProCopytoDao;

    @Resource
    private SysUserDao sysUserDao;

    @Resource
    private BpmNodeSetService bpmNodeSetService;

    @Resource
    private BpmFormDefService bpmFormDefService;

    @Resource
    private TaskOpinionService taskOpinionService;

    @Resource
    private DocFileDao docFileDao;

    @Autowired
    private BpmDefinitionService bpmDefinitionService;

    @Resource
    private BpmFormTableService bpmFormTableService;

    @Resource
    private ProcessRunService processRunService;

    @Resource
    private BpmFormHandlerDao bpmFormHandlerDao;

    @Resource
    private GlobalTypeService globalTypeService;

    private final String ALREADY_URL_PREFIX = "/platform/bpm/processRun/info.ht?runId=";

    private final String PEDDING_URL_PREFIX = "/platform/bpm/task/toStart.ht?taskId=";

    private final String MYCOMPLETE_URL_PREFIX = "/platform/bpm/processRun/info.ht?link=1&runId=";

    private final String MYDRAFT_URL_PREFIX = "/platform/bpm/task/startFlowForm.ht?runId=";

    private final String MYREQUEST_URL_PREFIX = "/platform/bpm/processRun/info.ht?prePage=myRequest&link=1&runId=";

    private final String ACCORDING_URL_PREFIX = "/platform/bpm/processRun/info.ht?link=1&runId=";

    private final String COPYLIST_URL_PREFIX = "/platform/bpm/processRun/info.ht?link=1&runId=";

    private final String MATTERS_URL_PREFIX = "/platform/bpm/processRun/info.ht?link=1&runId=";

    private final String COLUMN_PREFIX = "column_";

    //缓存有效期
    private final int expireMilliseconds = 60 * 3 * 1000;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    protected IEntityDao<CustomColumn, Long> getEntityDao() {
        return customColumnDao;
    }

    /**
     * 根据userId和栏目id获取自定义的tab
     */
    public List<CustomColumn> getCustomTab(final CustomColumn param) throws ExecutionException {
        Long userId = param.getCreateBy();
        String cacheKey = COLUMN_PREFIX + "_" + userId + "_" + param.getColumnId();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String tabListJSON = operations.get(cacheKey);
        List<CustomColumn> list;
        //重建缓存
        if (StringUtils.isEmpty(tabListJSON)) {
            logger.info("rebuild cache, column_tab");
            list = getCustomTabData(param);
            //重建缓存
            operations.set(cacheKey, JSON.toJSONString(list), expireMilliseconds, TimeUnit.MILLISECONDS);
        } else {
            list = JSON.parseArray(tabListJSON, CustomColumn.class);
        }
        return list;
    }

    /**
     * 重建自定义tab缓存
     */
    private List<CustomColumn> getCustomTabData(CustomColumn param) {
        List<CustomColumn> list = null;
        QueryFilter queryFilter = new QueryFilter();
        Long columnId = param.getColumnId();
        queryFilter.addFilter("columnId", columnId);
        queryFilter.addFilter("createBy", param.getCreateBy());
        CustomColumnVO customColumnVO = customColumnVOService.getById(columnId);
        if (customColumnVO == null) {
            return new ArrayList<>();
        }
        String columnType = customColumnVO.getColumnType();
        try {
            list = customColumnDao.getAll(queryFilter);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取每个tab对应流程的name
        for (CustomColumn customColumn : list) {
            String defId = customColumn.getData();
            if (StringUtils.isNotEmpty(defId)) {
                List<String> dataNameList = new ArrayList<>();
                if (LCGL_TYPE.equals(columnType)) {
                    dataNameList = bpmDefinitionService.getByDefIds(defId.split(","));
                } else if (WJGL_TYPE.equals(columnType) || WJG_TYPE.equals(columnType)) {
                    dataNameList = docFileService.getNamesByIds(LongUtil.transStrsToLongs(defId));
                }
                customColumn.setDataName(StringUtils.join(dataNameList.toArray(), ","));
            }
        }
        return list;
    }

    /**
     * 保存用户的栏目自定义tab
     */
    public ResultVo saveCustomTab(CustomColumnVO customColumnVO) {
        Long userId = ContextUtil.getCurrentUserId();
        customColumnVO.setCreateBy(userId);
        customColumnVO.setUpdateBy(userId);
        customColumnVO.setUpdatetime(new Date());
        String cacheKey = COLUMN_PREFIX + "_" + userId + "_" + customColumnVO.getId();
        //清除缓存
        this.redisTemplate.delete(cacheKey);

        this.customColumnDao.delByColumnId(customColumnVO.getId());
        if (CollectionUtils.isNotEmpty(customColumnVO.getCustomTabList())) {
            try {
                this.customColumnDao.insert("saveCustomTab", customColumnVO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "保存成功");
    }


    /**
     * 根据columnId获取已办事宜下所有tab的列表数据
     */
    public ResultVo getAlreadyDataByColumnId(CustomColumn param) throws ExecutionException {
        List<CustomColumn> list = this.getCustomTab(param);
        if (CollectionUtils.isEmpty(list)) {
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "没有自定义tab");
        }
        //结果数据集
        List<CustomLCTabVO> retList = new ArrayList<>();
        for (CustomColumn customColumn : list) {
            //流程定义id
            String defIds = customColumn.getData();
            //构造VO对象
            CustomLCTabVO vo = buildCustomLCTabVO(customColumn);
            retList.add(vo);
            if (StringUtils.isBlank(defIds)) {
                continue;
            }
            String[] defIdArr = StringUtils.split(defIds, ",");
            PageBean pb = new PageBean();
            pb.setCurrentPage(1);
            pb.setPagesize(15);
            // 去掉进行分页的总记录数的查询
            pb.setShowTotal(false);
            //根据流程定义id获取已办事宜数据
            List<ProcessRun> alreadyList = getAlreadyByDefIds(defIdArr, pb);
            //转换成tab列表数据VO对象
            List<TabDataVO> tabDataVOList = convertProcessRun2TabDataVO(alreadyList, ALREADY_URL_PREFIX);
            vo.setData(tabDataVOList);
        }
        return new ResultVo(retList);
    }

    /**
     * 根据columnId获取待办事宜下所有tab的列表数据
     */
    public ResultVo getPenddingDataByColumnId(CustomColumn param) throws ExecutionException {
        List<CustomColumn> list = this.getCustomTab(param);
        if (CollectionUtils.isEmpty(list)) {
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "没有定义tab");
        }
        //结果数据集
        List<CustomLCTabVO> retList = new ArrayList<>();
        for (CustomColumn customColumn : list) {
            //流程定义id
            String defIds = customColumn.getData();
            //构造VO对象
            CustomLCTabVO vo = buildCustomLCTabVO(customColumn);
            retList.add(vo);
            if (StringUtils.isBlank(defIds)) {
                continue;
            }
            String[] defIdArr = StringUtils.split(defIds, ",");
            PageBean pb = new PageBean();
            pb.setCurrentPage(1);
            pb.setPagesize(15);
            // 去掉进行分页的总记录数的查询
            pb.setShowTotal(false);
            //根据流程定义id获取待办事宜数据
            List<ProcessTask> penddingList = getPenddingByDefIds(defIdArr, pb);
            //转换成tab列表数据VO对象
            List<TabDataVO> tabDataVOList = convertProcessTask2TabDataVO(penddingList, customColumn.getType());
            vo.setData(tabDataVOList);
        }
        return new ResultVo(retList);
    }


    /**
     * 获取已办事宜，可根据多个defId过滤
     *
     * @param defIds   多个defId
     * @param pageBean 分页参数
     */
    public List<ProcessRun> getAlreadyByDefIds(Object[] defIds, PageBean pageBean) {
        Long userId = ContextUtil.getCurrentUserId();
        Map<String, Object> params = new HashMap<>();
        //如果defId为null，表示为该类型全部的流程
        params.put("assignee", userId);
        params.put("typeIds", getFlowTypeIds());
        if (defIds != null && defIds.length != 0) {
            params.put("defIds", defIds);
        }
        return processRunDao.getBySqlKey("getAlreadyMattersListByDefIds", params, pageBean);

    }

    /**
     * 流程分类条件过滤
     */
    private Set<Long> getFlowTypeIds() {
        List<GlobalType> globalTypeList = globalTypeService.getByCatKey(GlobalType.CAT_FLOW, false);
        Set<Long> typeIdList = BpmUtil.getTypeIdList(globalTypeList);
        if (typeIdList.size() == 0) {
            typeIdList.add(0L);
        }
        return typeIdList;
    }

    /**
     * 获取待办事宜，可根据多个defId过滤
     *
     * @param defIds   多个defId
     * @param pageBean 分页参数
     */
    public List getPenddingByDefIds(Object[] defIds, PageBean pageBean) {
        Map<String, Object> params = new HashMap<>();
        Long userId = ContextUtil.getCurrentUserId();
        params.put("userId", userId);
        params.put("typeIds", getFlowTypeIds());
        if (defIds != null && defIds.length != 0) {
            params.put("defIds", defIds);
        }
        return taskDao.getAllMyTaskByDefIds(params, pageBean);
    }

    /**
     * 获取我的草稿，可根据多个defId过滤
     *
     * @param defIds 多个defId
     */
    public List<ProcessRun> getMyDraftByDefIds(Object[] defIds, PageBean pageBean) {
        Map<String, Object> params = new HashMap<>();
        Long userId = ContextUtil.getCurrentUserId();
        params.put("userId", userId);
        params.put("typeIds", getFlowTypeIds());
        if (defIds != null && defIds.length != 0) {
            params.put("defIds", defIds);
        }
        return processRunDao.getBySqlKey("getMyDraft", params, pageBean);
    }

    /**
     * 获取我的申请，可根据多个defId过滤
     *
     * @param defIds 多个defId
     */
    public List<ProcessRun> getMyRequestByDefIds(Object[] defIds, PageBean pageBean) {
        Long userId = ContextUtil.getCurrentUserId();
        Map<String, Object> params = new HashMap<>();
        params.put("creatorId", userId);
        params.put("startFromMobile", 1);
        params.put("typeIds", getFlowTypeIds());
        if (defIds != null && defIds.length != 0) {
            params.put("defIds", defIds);
        }
        return processRunDao.getBySqlKey("getMyRequestList", params, pageBean);
    }

    /**
     * 获取我的办结，可根据多个defId过滤
     *
     * @param defIds 多个defId
     */
    public List<ProcessRun> getMyCompleteByDefIds(Object[] defIds, PageBean pageBean) {
        Map<String, Object> params = new HashMap<>();
        Long userId = ContextUtil.getCurrentUserId();
        params.put("creatorId", userId);
        params.put("typeIds", getFlowTypeIds());
        if (defIds != null && defIds.length != 0) {
            params.put("defIds", defIds);
        }
        return processRunDao.getBySqlKey("getMyCompletedList", params, pageBean);
    }

    /**
     * 获取我的交办，可根据多个defId过滤
     *
     * @param defIds 多个defId
     */
    public List getAccordingByDefIds(Object[] defIds, PageBean pageBean) {
        Map<String, Object> params = new HashMap<>();
        Long userId = ContextUtil.getCurrentUserId();
        params.put("ownerId", userId);
        params.put("typeIds", getFlowTypeIds());
        if (defIds != null && defIds.length != 0) {
            params.put("defIds", defIds);
        }
        return bpmTaskExeDao.getBySqlKey("accordingMattersList", params, pageBean);
    }

    /**
     * 获取我的加签，可根据多个defId过滤
     *
     * @param defIds 多个defId
     */
    public List<BpmProTransTo> getMattersByDefIds(Object[] defIds, PageBean pageBean) {
        Map<String, Object> params = new HashMap<>();
        Long userId = ContextUtil.getCurrentUserId();
        params.put("exceptDefStatus", 3);
        params.put("createUserId", userId);
        params.put("typeIds", getFlowTypeIds());
        if (defIds != null && defIds.length != 0) {
            params.put("defIds", defIds);
        }
        return bpmProTransToDao.getBySqlKey("mattersList", params, pageBean);
    }

    /**
     * 获取我的加签，可根据多个defId过滤
     *
     * @param defIds 多个defId
     */
    public List<BpmProCopyto> getCopeByDefIds(Object[] defIds, PageBean pageBean) {
        Map<String, Object> params = new HashMap<>();
        params.put("ccUid", ContextUtil.getCurrentUserId());
        params.put("typeIds", getFlowTypeIds());
        if (defIds != null && defIds.length != 0) {
            params.put("defIds", defIds);
        }
        return bpmProCopytoDao.getBySqlKey("getMyList", params, pageBean);
    }


    /**
     * 把BpmTaskExe对象转换成通用的tab数据VO对象
     */
    private List<TabDataVO> convertBpmTaskExe2TabDataVO(List<BpmTaskExe> list, String type) {
        List<TabDataVO> retList = new ArrayList<>();
        for (BpmTaskExe bpmTaskExe : list) {
            TabDataVO vo = new TabDataVO();

            vo.setWorkNo(bpmTaskExe.getGlobalFlowNo());
            vo.setSubject(bpmTaskExe.getSubject());
            vo.setId(bpmTaskExe.getRunId());
            vo.setType(type);
            ProcessRun processRun = processRunService.getById(bpmTaskExe.getRunId());
            if (processRun != null) {
                vo.setProcessName(processRun.getProcessName());
            }
            String extend = processRun.getExtend();
            if(StringUtil.isNotEmpty(extend)){
                vo.setExtend(extend);
            }
            vo.setDuration(processRun.getDuration());
            vo.setHasRead(true);
            vo.setCreator(bpmTaskExe.getCreator());
            vo.setCreateTime(bpmTaskExe.getCreatetime());
            retList.add(vo);
        }
        return retList;
    }

    /**
     * 把BpmProCopyto对象转换成通用的tab数据VO对象
     */
    private List<TabDataVO> convertBpmProCopyto2TabDataVO(List<BpmProCopyto> list, String type) {
        List<TabDataVO> retList = new ArrayList<>();
        for (BpmProCopyto bpmProCopyto : list) {
            TabDataVO vo = new TabDataVO();
            vo.setWorkNo(bpmProCopyto.getGlobalFlowNo());
            vo.setSubject(bpmProCopyto.getSubject());
            vo.setId(bpmProCopyto.getRunId());
            vo.setCreator(bpmProCopyto.getCreator());
            vo.setType(type);
            vo.setCopyId(bpmProCopyto.getCopyId());
            ProcessRun processRun = processRunService.getById(bpmProCopyto.getRunId());
            if (processRun != null) {
                vo.setProcessName(processRun.getProcessName());
            }
            String extend = processRun.getExtend();
            if(StringUtil.isNotEmpty(extend)){
               vo.setExtend(extend);
            }
            vo.setDuration(processRun.getDuration());
            vo.setHasRead(bpmProCopyto.getIsReaded() == 1);
            vo.setCreateTime(bpmProCopyto.getCreatetime());
            retList.add(vo);
        }
        return retList;
    }

    /**
     * 把ProcessRun对象转换成通用的tab数据VO对象
     */
    private List<TabDataVO> convertProcessRun2TabDataVO(List<ProcessRun> processRunList, String type) {
        List<TabDataVO> retList = new ArrayList<>();
        for (ProcessRun processRun : processRunList) {
            TabDataVO vo = new TabDataVO();
            vo.setWorkNo(processRun.getGlobalFlowNo());
            vo.setSubject(processRun.getSubject());
            vo.setId(processRun.getRunId());
            vo.setCreator(processRun.getCreator());
            vo.setCreateTime(processRun.getCreatetime());
            vo.setProcessName(processRun.getProcessName());
            vo.setType(type);
            String extend = processRun.getExtend();
            if(StringUtil.isNotEmpty(extend)){
               vo.setExtend(extend);
            }

            if(processRun.getStatus() != 2 && processRun.getStatus() != 3 && processRun.getStatus() != 10){
                Long durationTime = TimeUtil.getTime(processRun.getCreatetime(),new Date());
                processRun.setDuration(durationTime);
            }
            vo.setDuration(processRun.getDuration());
            vo.setHasRead(true);
            retList.add(vo);
        }
        return retList;
    }


    /**
     * 把ProcessTask对象转换成通用的tab数据VO对象
     */
    private List<TabDataVO> convertProcessTask2TabDataVO(List<ProcessTask> taskEntityList, String type) {
        List<TabDataVO> retList = new ArrayList<>();
        for (ProcessTask processTask : taskEntityList) {
            TabDataVO vo = new TabDataVO();
            vo.setId(Long.parseLong(processTask.getId()));
            vo.setWorkNo(processTask.getGlobalFlowNo());
            vo.setSubject(processTask.getSubject());
            vo.setCreator(processTask.getCreator());
            vo.setCreateTime(processTask.getCreateTime());
            vo.setType(type);
            String instanceId = processTask.getProcessInstanceId();
            ProcessRun processRun = processRunService.getByActInstanceId(new Long(instanceId));
            if (processRun != null) {
                vo.setProcessName(processRun.getProcessName());
            }
            String extend = processRun.getExtend();
            if(StringUtil.isNotEmpty(extend)){
                vo.setExtend(extend);
            }
            Date date = new Date();
            vo.setDuration(date.getTime() - processTask.getCreateTime().getTime());
            vo.setHasRead(processTask.getHasRead() == 1);
            retList.add(vo);
        }
        return retList;
    }

    /**
     * 把BpmProTransTo对象转换成通用的tab数据VO对象
     */
    private List<TabDataVO> convertBpmProTransTo2TabDataVO(List<BpmProTransTo> list, String type) {
        List<TabDataVO> retList = new ArrayList<>();
        for (BpmProTransTo bpmProTransTo : list) {
            TabDataVO vo = new TabDataVO();
            vo.setId(bpmProTransTo.getId());
            vo.setWorkNo(bpmProTransTo.getGlobalFlowNo());
            vo.setSubject(bpmProTransTo.getSubject());
            vo.setType(type);
            String userName = sysUserDao.getById(bpmProTransTo.getCreateUserId()).getUserName();
            ProcessRun processRun = processRunService.getById(bpmProTransTo.getRunId());
            if (processRun != null) {
                vo.setProcessName(processRun.getProcessName());
            }
            String extend = processRun.getExtend();
            if(StringUtil.isNotEmpty(extend)){
                vo.setExtend(extend);
            }
            vo.setDuration(processRun.getDuration());
            vo.setHasRead(true);
            vo.setCreator(userName);
            vo.setCreateTime(bpmProTransTo.getCreatetime());
            retList.add(vo);
        }
        return retList;
    }


    private CustomLCTabVO buildCustomLCTabVO(CustomColumn customColumn) {
        CustomLCTabVO vo = new CustomLCTabVO();
        vo.setColumnId(customColumn.getColumnId());
        vo.setTabId(customColumn.getId());
        vo.setTabName(customColumn.getTabName());

        return vo;
    }

    private CustomWJTabVO buildCustomWJTabVO(CustomColumn customColumn) {
        CustomWJTabVO vo = new CustomWJTabVO();
        vo.setColumnId(customColumn.getColumnId());
        vo.setTabId(customColumn.getId());
        vo.setTabName(customColumn.getTabName());
        return vo;
    }

    /**
     * 分页查询tab下的数据
     */
    public ResultVo getTabDataByTabId(Long tabId, PageBean pageBean) {
        try {
            CustomColumn customColumn;
            customColumn = this.customColumnDao.getById(tabId);
            if (customColumn == null) {
                logger.error("无法根据tabId查询到自定义tab,tabId={}", tabId);
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "参数tabId错误");
            }
            String defIds = customColumn.getData();
            String[] defIdArr = StringUtils.split(defIds, ",");
            String columnType = customColumn.getColumnType();
            List<DocFile> docFileList = new ArrayList<>();
            PageVo pageVo = new PageVo();
            switch (columnType) {
                case LCGL_TYPE:
                    List<TabDataVO> tabDataVOList = this.getLCGL(customColumn, pageBean);
                    pageVo = PageUtil.getPageVo(tabDataVOList, pageBean);
                    break;
                case WJGL_TYPE:
                    docFileList = this.getDocFileData(customColumn, pageBean);
                    pageVo = PageUtil.getPageVo(docFileList, pageBean);
                    break;
                case WJG_TYPE:
                    docFileList = this.getDocFileData(customColumn, pageBean);
                    pageVo = PageUtil.getPageVo(docFileList, pageBean);
                    break;
                default:
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "找不到tab所属的栏目");
            }
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取tab数据成功！", pageVo);
        } catch (Exception e) {
            logger.error("getListByTabId exception, tabId={}", tabId, e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取数据失败");
        }
    }

    /**
     * 获取流程类的数据
     *
     * @param customColumn
     * @param pageBean
     * @return
     */
    public List<TabDataVO> getLCGL(CustomColumn customColumn, PageBean pageBean) {
        String defIds = customColumn.getData();
        String[] defIdArr = StringUtils.split(defIds, ",");
        List<TabDataVO> tabDataVOList = null;
        if (ALREADY.equals(customColumn.getType())) {
            //根据流程定义id获取已办事宜数据
            List<ProcessRun> list = getAlreadyByDefIds(defIdArr, pageBean);
            //转换成tab列表数据VO对象
            tabDataVOList = convertProcessRun2TabDataVO(list, customColumn.getType());
        } else if (PENDDING.equals(customColumn.getType())) {
            List<ProcessTask> list = getPenddingByDefIds(defIdArr, pageBean);
            tabDataVOList = convertProcessTask2TabDataVO(list, customColumn.getType());
            //我的草稿
        } else if (MYDRAFT.equals(customColumn.getType())) {
            List<ProcessRun> list = getMyDraftByDefIds(defIdArr, pageBean);
            tabDataVOList = convertProcessRun2TabDataVO(list, customColumn.getType());
            //我的申请
        } else if (MYREQUEST.equals(customColumn.getType())) {
            List<ProcessRun> list = getMyRequestByDefIds(defIdArr, pageBean);
            tabDataVOList = convertProcessRun2TabDataVO(list, customColumn.getType());
            //我的已办
        } else if (MYCOMPLETE.equals(customColumn.getType())) {
            List<ProcessRun> list = getMyCompleteByDefIds(defIdArr, pageBean);
            tabDataVOList = convertProcessRun2TabDataVO(list, customColumn.getType());
            //我的交办
        } else if (ACCORDING.equals(customColumn.getType())) {
            List<BpmTaskExe> list = getAccordingByDefIds(defIdArr, pageBean);
            tabDataVOList = convertBpmTaskExe2TabDataVO(list, customColumn.getType());
            //我的加签
        } else if (MATTERS.equals(customColumn.getType())) {
            List<BpmProTransTo> list = getMattersByDefIds(defIdArr, pageBean);
            tabDataVOList = convertBpmProTransTo2TabDataVO(list, customColumn.getType());
            //他人转发
        } else if (COPYLIST.equals(customColumn.getType())) {
            List<BpmProCopyto> list = getCopeByDefIds(defIdArr, pageBean);
            tabDataVOList = convertBpmProCopyto2TabDataVO(list, customColumn.getType());
        }
        return tabDataVOList;

    }

    /**
     * 获取文件类栏目的数据
     *
     * @param customColumn
     * @param pageBean
     * @return
     */
    public List<DocFile> getDocFileData(CustomColumn customColumn, PageBean pageBean) {
        String data = customColumn.getData();
        if (StringUtil.isEmpty(data)) {
            return null;
        } else {
            return this.getAllDocFile(customColumn);
        }
    }

    /**
     * 根据columnId获取流程类的数据。(初始数据)
     *
     * @param param
     * @return
     */
    public List<CustomLCTabVO> getLCDataByColumnId(CustomColumn param) throws Exception {
        List<CustomLCTabVO> retList = new ArrayList<>();
        try {

            List<CustomColumn> list = this.getCustomTab(param);
            //结果数据集
            for (CustomColumn customColumn : list) {
                CustomLCTabVO vo = buildCustomLCTabVO(customColumn);
                PageBean pb = new PageBean();
                pb.setCurrentPage(1);
                pb.setPagesize(15);
                // 去掉进行分页的总记录数的查询
                pb.setShowTotal(false);
                List<TabDataVO> tabDataVOList = this.getLCGL(customColumn, pb);
                if (tabDataVOList == null) {
                    tabDataVOList = new ArrayList<>();
                }
                vo.setData(tabDataVOList);
                retList.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retList;
    }

    /**
     * 根据columnId获取文件类的数据。(初始数据)
     *
     * @param param
     * @return
     */
    public List<CustomWJTabVO> getWJDataByColumnId(CustomColumn param, HttpServletRequest request) throws Exception {
        List<CustomWJTabVO> retList = new ArrayList<>();
        List<CustomColumn> list = this.getCustomTab(param);
        //结果数据集
        for (CustomColumn customColumn : list) {
            CustomWJTabVO vo = buildCustomWJTabVO(customColumn);
            PageBean pb = new PageBean();
            pb.setCurrentPage(1);
            pb.setPagesize(15);
            // 去掉进行分页的总记录数的查询
            pb.setShowTotal(false);
            //根据流程定义id获取已办事宜数据
            String columnType = customColumn.getColumnType();
            String defIds = customColumn.getData();
            Long[] docFileIds = LongUtil.transStrsToLongs(defIds);
            List<DocFile> docFiles = new ArrayList<>();
            QueryFilter queryFilter = new QueryFilter();
            if (docFileIds != null) {
                for (int i = 0; i < docFileIds.length; i++) {
                    queryFilter = docFileService.getFilter(queryFilter, docFileIds[i]);
                    queryFilter.addFilter("id", docFileIds[i]);
                    queryFilter.addFilter("isDocType", 0);
                    List<DocFile> temp = docFileService.getAll(queryFilter);
                    docFiles.addAll(temp);
                }
            }
            if (docFiles == null) {
                docFiles = new ArrayList<>();
            }
            vo.setData(docFiles);
            retList.add(vo);
        }
        return retList;
    }

    public List<DocFile> getAllDocFile(CustomColumn customColumn) {
        String defIds = customColumn.getData();
        Long[] docFileIds = LongUtil.transStrsToLongs(defIds);
        List<DocFile> docFiles = new ArrayList<>();
        for (int i = 0; i < docFileIds.length; i++) {
            QueryFilter queryFilter = new QueryFilter();
            queryFilter = docFileService.getFilter(queryFilter, docFileIds[i]);
            List<DocFile> temp = docFileService.getList(docFileIds[i], new ArrayList<DocFile>(), 0);
            docFiles.addAll(temp);
        }
        if (docFiles == null) {
            docFiles = new ArrayList<>();
        }
        return docFiles;
    }


//    /**
//     * 获取自定义首页编辑模式下预览的数据
//     * @param columnId 栏目id
//     * @param defIds 流程定义id数组
//     * @param pageBean 分页参数
//     * */
//    public ResultVo getPreviewDataByDefIds(Long columnId, String[] defIds, PageBean pageBean){
//        List<TabDataVO> tabDataVOList;
//        CustomColumn customColumn = this.customColumnDao.getById(columnId);
//        if(columnId == ALREADY){
//            List<ProcessRun> alreadyList = this.getAlreadyByDefIds(defIds, pageBean);
//            tabDataVOList = convertProcessRun2TabDataVO(alreadyList);
//        }else if(columnId == PENDDING){
//            List<ProcessTask> pendingList = this.getPenddingByDefIds(defIds, pageBean);
//            tabDataVOList = convertProcessTask2TabDataVO(pendingList);
//        }else{
//            return new ResultVo(ResultConst.COMMON_STATUS_FAILED, "参数columnId无效");
//        }
//        return new ResultVo(tabDataVOList);
//    }
}
