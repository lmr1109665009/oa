package com.suneee.ucp.mh.service.gatewayManage;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.page.PageBean;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.dao.system.UserPositionDao;
import com.suneee.platform.model.system.Dictionary;
import com.suneee.platform.model.system.Job;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.JobService;
import com.suneee.ucp.base.common.ResultConst;
import com.suneee.ucp.base.enums.GatewayFlag;
import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.dao.customColumn.CustomColumnDao;
import com.suneee.ucp.mh.dao.customColumn.CustomColumnVODao;
import com.suneee.ucp.mh.dao.gateway.GatewaySettingDao;
import com.suneee.ucp.mh.dao.rowTemp.SysRowTempDao;
import com.suneee.ucp.mh.dao.shortcut.ShortCutDao;
import com.suneee.ucp.mh.model.customColumn.CustomColumnVO;
import com.suneee.ucp.mh.model.gatewayManage.*;
import com.suneee.ucp.mh.model.rowTemp.SysRowTemp;
import com.suneee.ucp.mh.model.shortcut.ShortCut;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 *  门户设置service
 *  @author ytw
 * */
@Service
public class GatewaySettingService extends UcpBaseService{

    private Logger logger = LoggerFactory.getLogger(GatewaySettingService.class);

    //数据字典-门户类型的key
    public static final String GATEWAY_TYPE = "mhlx";

    @Autowired
    private SysRowTempDao sysRowTempDao;

    @Autowired
    private ShortCutDao shortCutDao;

    @Autowired
    private CustomColumnDao customColumnDao;

    @Autowired
    private GatewaySettingDao gatewaySettingDao;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private ShortCutService shortCutService;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserPositionDao userPositionDao;

    @Autowired
    private CustomColumnVODao customColumnVODao;

    @Override
    protected IEntityDao getEntityDao() {
        return gatewaySettingDao;
    }

    /**
     * 保存门户设置
     * */
    public ResultVo save(GatewaySetting gatewaySetting){
        ResultVo resultVo = saveValidate(gatewaySetting);
        if (resultVo.getStatus() != ResultVo.COMMON_STATUS_SUCCESS) {
            return resultVo;
        }
        logger.info("gatewaySetting={}", gatewaySetting);
        //保存前对数据处理
        resultVo = saveBeforeHandle(gatewaySetting);
        if (resultVo.getStatus() != ResultVo.COMMON_STATUS_SUCCESS) {
            return resultVo;
        }
        this.gatewaySettingDao.add(gatewaySetting);
        return ResultVo.getSuccessInstance();
    }

    /**
     * 保存前处理操作
     * */
    private ResultVo saveBeforeHandle(GatewaySetting gatewaySetting) {
        String contentJSON = gatewaySetting.getContentJSON();
        List<LayoutRowVO> rows = JSON.parseArray(contentJSON, LayoutRowVO.class);
        StringBuilder tempNameBuilder = new StringBuilder();
        StringBuilder contentViewBuilder = new StringBuilder();
        //处理每行数据
        for(LayoutRowVO rowVO : rows){
            //模板id
            Long tempId = rowVO.getTempId();
            SysRowTemp sysRowTemp = sysRowTempDao.getById(tempId);
            if(sysRowTemp == null){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "参数tempId错误");
            }
            //一列（12），三列（4，4，4）
            tempNameBuilder.append(sysRowTemp.getTempName() + "(" + sysRowTemp.getWidth() + "),");
            List<LayoutColVO> colList = rowVO.getItems();
            for(LayoutColVO col : colList){
                //快捷入口，待办事宜
                contentViewBuilder.append(col.getName() + ",");
            }
        }
        if(tempNameBuilder.length() > 0){
            tempNameBuilder.deleteCharAt(tempNameBuilder.length()-1);
        }
        if(contentViewBuilder.length() > 0){
            contentViewBuilder.append(contentViewBuilder.deleteCharAt(contentViewBuilder.length()-1));
        }
        gatewaySetting.setTemplet(tempNameBuilder.toString());
        gatewaySetting.setContentView(contentViewBuilder.toString());
        return ResultVo.getSuccessInstance();
    }

    public ResultVo update(GatewaySetting gatewaySetting){
        ResultVo resultVo = saveValidate(gatewaySetting);
        if(resultVo.getStatus() != ResultVo.COMMON_STATUS_SUCCESS){
            return resultVo;
        }
        resultVo = saveBeforeHandle(gatewaySetting);
        if(resultVo.getStatus() != ResultVo.COMMON_STATUS_SUCCESS){
            return resultVo;
        }
        this.gatewaySettingDao.update(gatewaySetting);
        return ResultVo.getSuccessInstance();
    }

    /**
     * 处理快捷入口模板,根据id查询出快捷入口的具体信息
     * @param shortcutColVO 快捷入口布局列
     * @param onlyResolve true-只解析，不序列化;false-解析并序列化
     * */
    private List<ShortCut> processShortCut(LayoutColVO shortcutColVO, boolean onlyResolve){
        List<ShortCut> list = new ArrayList<>();
        List<Object> shortCutIdList = shortcutColVO.getExtra();
        if(CollectionUtils.isEmpty(shortCutIdList)){
            return list;
        }
        for(Object shortCutId : shortCutIdList){
            ShortCut shortCut = shortCutService.getByIdUseCache(Long.parseLong(shortCutId.toString()));
            list.add(shortCut);
        }
        if(!onlyResolve){
            shortcutColVO.setExtraJSON(JSON.toJSONString(list));
        }
        return list;
    }

    /**
     * 获取编辑页面左侧菜单信息
     * */
    public ResultVo getLeftMenuInfo(){
        // 从数据字典中获取门户类型
        List<Dictionary> gatewayTypeList = dictionaryService.getByNodeKey(GATEWAY_TYPE);
        //获取职务级别
        List<Dictionary> jobGrade = dictionaryService.getByNodeKey(Job.NODE_KEY);
        //门户模板
        List<SysRowTemp> rowTempList = this.sysRowTempDao.getAll();
        //快捷入口
        List<ShortCut> shortCutList = this.shortCutDao.getAll();
        QueryFilter queryF = new QueryFilter();
        queryF.addFilter("createBy",0);
        List<CustomColumnVO> customColumnVOList = this.customColumnVODao.getAll(queryF);

        QueryFilter filter = new QueryFilter();
        filter.setSortColumns("heat DESC");
        PageBean pageBean = new PageBean();
        pageBean.setCurrentPage(1);
        pageBean.setPagesize(5);
        filter.setPageBean(pageBean);
        pageBean.setShowTotal(false);
        //常用模板
        List<SysRowTemp> hotRowTempList = this.sysRowTempDao.getAll(filter);

        GatewaySettingMenuVO gatewaySettingMenuVO = new GatewaySettingMenuVO(ContextUtil.getCurrentOrg().getOrgSupName(), gatewayTypeList,rowTempList ,shortCutList,customColumnVOList,hotRowTempList,jobGrade);

        return new ResultVo(gatewaySettingMenuVO);
    }


    /**
     * 获取布局信息
     * @param id 门户设置id
     * */
    public ResultVo getGatewaySettingInfo(Long id){
        GatewaySetting gatewaySetting = this.gatewaySettingDao.getById(id);
        String contentJSON = gatewaySetting.getContentJSON();
        List<LayoutRowVO> rows = JSON.parseArray(contentJSON, LayoutRowVO.class);
        for(LayoutRowVO row : rows){
            for(LayoutColVO col : row.getItems()){
                //快捷入口需要特殊处理
                if(isShortCutLayout(col)){
                    processShortCut(col, false);
                }
            }
        }

        GatewaySettingVO VO = convert2VO(gatewaySetting, ContextUtil.getCurrentOrg().getOrgSupName());
        //替换为处理后的JSON
        VO.setLayoutJSON(JSON.toJSONString(rows));
        return new ResultVo(VO);
    }


    /**
     * 保存前的校验
     * */
    private ResultVo saveValidate(GatewaySetting gatewaySetting){
        ResultVo resultVo = new ResultVo(ResultConst.COMMON_STATUS_FAILED, null);
        if(StringUtils.isEmpty(gatewaySetting.getGatewayName())){
            resultVo.setMessage("门户名称为空");
            return resultVo;
        }else if(gatewaySetting.getGatewayType() == null){
            resultVo.setMessage("没有选择门户类型");
            return resultVo;
        }
        resultVo.setStatus(ResultConst.COMMON_STATUS_SUCCESS);
        return resultVo;

    }

    /**
     * 强制更新
     * */
    public ResultVo forceUpdate(Long gatewaySettingId){
        GatewaySetting gatewaySetting = this.gatewaySettingDao.getById(gatewaySettingId);
        if(gatewaySetting == null){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "参数异常");
        }
        //企业编码
        String orgCode = gatewaySetting.getOrgCode();
        Set<String> enterpriseCodes = new HashSet<>();
        enterpriseCodes.add(orgCode);
        QueryFilter filter = new QueryFilter();
        filter.addFilter("enterpriseCodes", enterpriseCodes);
        filter.addFilter("grade", gatewaySetting.getType());
        //获取职位级别对应的所有职位
        List<Job> jobList = jobService.getAll(filter);
        if(CollectionUtils.isEmpty(jobList)){
            return ResultVo.getSuccessInstance();
        }
        //获取职位下所有的用户
        List<Long> userIdlist = this.userPositionDao.getUserIdByJobIds(jobList);

        //删除用户自定义的快捷方式，用户登录时读取此模板中的默认快捷方式
        this.shortCutDao.deleteCustomShortCutByUserIds(userIdlist);

        Map map = new HashMap<String, Object>();
        map.put("list", userIdlist);
        map.put("updatetime", new Date());
        map.put("updateBy", ContextUtil.getCurrentUserId());
        //删除用户自定义的栏目
        this.customColumnDao.delBySqlKey("deleteCustomTabByUserIds", map);

        return ResultVo.getSuccessInstance();
    }

    /**
     * 判断布局中是否有快捷方式
     * @param col 布局中的列
     * */
    private boolean isShortCutLayout(LayoutColVO col){
        return col.getFlag() != null && col.getFlag().equals(GatewayFlag.SHORT_CUT.getCode());
    }


    /**
     * 提取布局中的快捷入口信息
     * @param gatewaySetting 布局对象
     * */
    public List<ShortCut> extractShortCutLayout(GatewaySetting gatewaySetting){
        List<ShortCut> shortCutList = Lists.newArrayList();
        if(gatewaySetting == null || StringUtils.isEmpty(gatewaySetting.getContentJSON())){
            return shortCutList;
        }
        //布局JSON格式数据
        String layoutJSON = gatewaySetting.getContentJSON();
        //解析
        List<LayoutRowVO> rows = JSON.parseArray(layoutJSON, LayoutRowVO.class);
        //查找快捷入口布局
        for(LayoutRowVO row : rows){
            List<LayoutColVO> cols = row.getItems();
            for(LayoutColVO col :cols){
                if(isShortCutLayout(col)){
                    shortCutList = processShortCut(col, true);
                }
            }
        }
        return shortCutList;
    }

    public List<GatewaySettingVO> convert2VO(List<GatewaySetting> gatewaySettingList, String compnayName){
        List<GatewaySettingVO> list = new ArrayList<>();
        List<Dictionary> gatewayTypeList = this.dictionaryService.getByNodeKey(GATEWAY_TYPE);
        for(GatewaySetting gatewaySetting :gatewaySettingList){
            GatewaySettingVO gatewaySettingVO = new GatewaySettingVO();
            gatewaySettingVO.setId(gatewaySetting.getId());
            gatewaySettingVO.setCompanyName(compnayName);
            gatewaySettingVO.setContent(gatewaySetting.getContentView());
            gatewaySettingVO.setTemplet(gatewaySetting.getTemplet());
            gatewaySettingVO.setGatewayName(gatewaySetting.getGatewayName());
            gatewaySettingVO.setGatewayTypeName(findGatewayType(gatewayTypeList, gatewaySetting.getGatewayType()));
            list.add(gatewaySettingVO);
        }
        return list;
    }

    public GatewaySettingVO convert2VO(GatewaySetting gatewaySetting, String compnayName){
        GatewaySettingVO gatewaySettingVO = new GatewaySettingVO();
        gatewaySettingVO.setId(gatewaySetting.getId());
        gatewaySettingVO.setCompanyName(compnayName);
        gatewaySettingVO.setContent(gatewaySetting.getContentView());
        gatewaySettingVO.setTemplet(gatewaySetting.getTemplet());
        gatewaySettingVO.setGatewayName(gatewaySetting.getGatewayName());
        gatewaySettingVO.setType(gatewaySetting.getType());
        gatewaySettingVO.setGatewayType(gatewaySetting.getGatewayType());
        return gatewaySettingVO;
    }

    /**
     * 根据gatewayType查找门户name
     * */
    private String findGatewayType(List<Dictionary> gatewayTypeList, Long gatewayType){
        for(Dictionary dictionary : gatewayTypeList){
            if(dictionary.getItemValue().equals(gatewayType.toString())){
                return dictionary.getItemName();
            }
        }
        return "";
    }
}
