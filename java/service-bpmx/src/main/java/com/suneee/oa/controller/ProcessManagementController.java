package com.suneee.oa.controller;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import com.suneee.eas.common.utils.ContextSupportUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.model.shortcut.ShortCut;
import com.suneee.ucp.mh.service.gatewayManage.ShortCutService;
@Controller
@RequestMapping("/platform/bpm/process")
public class ProcessManagementController {
    private Logger logger = LoggerFactory.getLogger(ProcessManagementController.class);
    @Autowired
    private ShortCutService shortCutService;
    @Resource
    private ProcessRunService processRunService;
    @RequestMapping(value = "/processmanagement", method = RequestMethod.GET)
    @ResponseBody
    @Action(description = "获取自定义快捷入口")
    public ResultVo getCustomShortCut(HttpServletRequest request){
        try {

            Long userId = ContextSupportUtil.getCurrentUserId();
            String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
            //String enterpriseCode = CookieUitl.getCurrentEnterpriseCode();
            ResultVo resultVo = shortCutService.getCustomShortCutByUserIds(userId,enterpriseCode);
            //查询出来的为空，则初始化为默认的快捷入口
            if(resultVo.getStatus() == ResultVo.COMMON_STATUS_SUCCESS){
                List list = (List) resultVo.getData();
                if(CollectionUtils.isEmpty(list)){
                    try {
                        return this.shortCutService.initCustomShortCuts(userId);
                    }catch (DuplicateKeyException e){
                        logger.error("重复初始化快捷入口, userId={}", userId, e);
                    }
                }else if(list.size() == 1){
                    //判断是否没有设置快捷入口(删掉了所有自定义快捷入口)
                    ShortCut shortCut = (ShortCut) list.get(0);
                    //关联表中的short_id=0表示是一条标识数据，表示用户自己删除了所有快捷入口
                    if(shortCut.getId() == 0){
                        resultVo.setData(new ArrayList<>());
                    }
                }
            }

//            暂时屏蔽掉新建流程和我的草稿
            List<ShortCut> list = (List) resultVo.getData();
            ArrayList<ShortCut> shortCuts = new ArrayList<>();
            for (ShortCut shortCut : list) {
                if(!"新建流程".equals(shortCut.getName()) && !"我的草稿".equals(shortCut.getName())){
                    shortCuts.add(shortCut);
                }
            }
            resultVo.setData(shortCuts);

            return resultVo;
        }catch (Exception e){
            logger.error("getCustomShortCut expcetion", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取自定义快捷入口失败");
        }
    }


}