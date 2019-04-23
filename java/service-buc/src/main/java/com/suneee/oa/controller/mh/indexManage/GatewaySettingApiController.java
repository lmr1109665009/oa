package com.suneee.oa.controller.mh.indexManage;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.model.customColumn.CustomColumnVO;
import com.suneee.ucp.mh.model.gatewayManage.GatewaySetting;
import com.suneee.ucp.mh.model.gatewayManage.GatewaySettingVO;
import com.suneee.ucp.mh.model.shortcut.ShortCut;
import com.suneee.ucp.mh.service.customColumn.CustomColumnVOService;
import com.suneee.ucp.mh.service.gatewayManage.GatewaySettingService;
import com.suneee.ucp.mh.service.gatewayManage.ShortCutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 门户设置controller
 * @author ytw
 * */
@RequestMapping("/mh/index/gatewaySettingApi")
@Controller
public class GatewaySettingApiController {

    private Logger logger = LoggerFactory.getLogger(GatewaySettingApiController.class);

    @Autowired
    private GatewaySettingService gatewaySettingService;
    @Autowired
    private ShortCutService shortCutService;
    @Autowired
    private CustomColumnVOService customColumnVOService;

    /**
     * 获取门户设置列表信息
     * */
    @RequestMapping("/list")
    @ResponseBody
    public ResultVo list(HttpServletRequest request){
        QueryFilter filter = new QueryFilter(request);
        PageList<GatewaySetting> list = (PageList<GatewaySetting>) this.gatewaySettingService.getAll(filter);
        String companyName = ContextUtil.getCurrentOrg().getOrgSupName();
        List<GatewaySettingVO> voList = this.gatewaySettingService.convert2VO(list, companyName);
        return new ResultVo(PageUtil.getPageVo(voList, list.getPageBean()));
    }

    @RequestMapping("/deleteByIds")
    @ResponseBody
    public ResultVo deleteByIds(HttpServletRequest request){
        Long[] ids = RequestUtil.getLongAryByStr(request, "ids");
        try {
            this.gatewaySettingService.delById(ids);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultVo.getSuccessInstance();
    }

    /**
     * 获取左侧列表信息
     * */
    @RequestMapping("/getLeftMenuInfo")
    @ResponseBody
    public ResultVo getLeftMenuInfo(){
        try{
            return gatewaySettingService.getLeftMenuInfo();
        }catch (Exception e){
            logger.error("getGatewaySettingById exception", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取左侧列表信息失败");
        }
    }

    @RequestMapping("/getById")
    @ResponseBody
    public ResultVo getById(Long id){
        return this.gatewaySettingService.getGatewaySettingInfo(id);
    }


    @RequestMapping("/save")
    @ResponseBody
    public ResultVo save(GatewaySetting gatewaySetting, List<ShortCut> shortCutList, List<CustomColumnVO> customColumnVOList){
        try {
            SysOrg sysOrg = (SysOrg) ContextUtil.getCurrentOrg();
            gatewaySetting.setOrgCode(sysOrg.getOrgCode());
            if(gatewaySetting.getId() == null){
                return this.gatewaySettingService.save(gatewaySetting);
            }else{
                //删除旧的数据。
                shortCutService.delByGatewayId(gatewaySetting.getId(), ContextUtil.getCurrentUserId());
                //将快捷入口保存到数据库中。
                for (ShortCut shortCut:shortCutList) {
                    shortCut.setGatewayId(gatewaySetting.getId());
                    shortCut.setId(UniqueIdUtil.genId());
                    shortCutService.add(shortCut);
                }
                //将栏目保存到数据库中。
                for (CustomColumnVO customCloumnVo: customColumnVOList) {
                    customCloumnVo.setColumnId(UniqueIdUtil.genId());
                    customCloumnVo.setGatewayId(gatewaySetting.getId());
                    customColumnVOService.add(customCloumnVo);
                }
                return this.gatewaySettingService.update(gatewaySetting);
            }
        }catch (Exception e){
            logger.error("保存失败", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存失败");
        }
    }

    /**
     * 强制更新
     * */
    @RequestMapping("/forceUpdate")
    @ResponseBody
    public ResultVo forceUpdate(Long id){
        return this.gatewaySettingService.forceUpdate(id);
    }


}
