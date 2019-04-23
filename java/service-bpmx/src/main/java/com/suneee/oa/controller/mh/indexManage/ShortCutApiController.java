package com.suneee.oa.controller.mh.indexManage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.vo.PageVo;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.model.shortcut.ShortCut;
import com.suneee.ucp.mh.service.gatewayManage.ShortCutService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/mh/index/shortCutApi")
public class ShortCutApiController extends UcpBaseController {

    private Logger logger = LoggerFactory.getLogger(ShortCutApiController.class);

    @Autowired
    private ShortCutService shortCutService;


    @RequestMapping("/list")
    @ResponseBody
    public ResultVo list(HttpServletRequest request){
        QueryFilter filter = null;
        try {
            filter = new QueryFilter(request, true);
            String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
            filter.addFilter("enterpriseCode", enterpriseCode);
            List<ShortCut> list = shortCutService.getAll(filter);
            PageVo pageVo = PageUtil.getPageVo(list, filter.getPageBean());
            return new ResultVo(pageVo);
        }catch (Exception e){
            logger.error("list exception, filter={}", filter, e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取数据失败");
        }
    }


    @RequestMapping("/getById")
    @ResponseBody
    public ResultVo getById(Long id){
        try {
            ShortCut shortCut = this.shortCutService.getByIdUseCache(id);
            return new ResultVo(shortCut);
        }catch (Exception e){
            logger.error("getById exception, id = {}", id, e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取数据失败");
        }
    }

    @RequestMapping("/save")
    @ResponseBody
    public ResultVo save(ShortCut shortCut){
        try {
            if (shortCut.getId() == null || shortCut.getId() == 0) {
                shortCut.setCreateBy(ContextUtil.getCurrentUserId());
                String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
                shortCut.setEnterpriseCode(enterpriseCode);
                this.shortCutService.add(shortCut);
            } else {
                shortCut.setUpdateBy(ContextUtil.getCurrentUserId());
                shortCut.setUpdatetime(new Date());
                this.shortCutService.update(shortCut);
            }
            return ResultVo.getSuccessInstance();
        }catch (Exception e){
            logger.error("save exception, shortCut={}", shortCut, e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存失败");
        }
    }

    @RequestMapping("/delByIds")
    @ResponseBody
    public  ResultVo delByIds(HttpServletRequest request){
        Long[] ids = null;
        try {
            ids = RequestUtil.getLongAryByStr(request, "ids");
            this.shortCutService.delByIds(ids);
            return ResultVo.getSuccessInstance();
        }catch (Exception e){
            logger.error("delByIds exception, ids={}", ids, e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除失败");
        }
    }



    @RequestMapping(value = "/getCustomShortCut", method = RequestMethod.GET)
    @ResponseBody
    @Action(description = "获取自定义快捷入口")
    public ResultVo getCustomShortCut(HttpServletRequest request){
        try {
            Long userId = ContextUtil.getCurrentUserId();
            String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
            ResultVo resultVo = shortCutService.getCustomShortCutByUserId(userId,enterpriseCode);
            //查询出来的为空，则初始化为默认的快捷入口
            if(resultVo.getStatus() == ResultVo.COMMON_STATUS_SUCCESS){
                List list = (List) resultVo.getData();
                if(CollectionUtils.isEmpty(list)){
                    try {
                        return this.shortCutService.initCustomShortCut(userId);
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
            return resultVo;
        }catch (Exception e){
            logger.error("getCustomShortCut expcetion", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取自定义快捷入口失败");
        }
    }


    @RequestMapping(value = "/saveCustomShortCut",method = RequestMethod.POST)
    @ResponseBody
    @Action(description = "保存自定义快捷入口")
    public ResultVo saveCustomShortCut(@RequestBody List<ShortCut> shortCutList){
        try {
            String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
            List<ShortCut> list = new ArrayList<ShortCut>();
            String str= JSON.toJSON(shortCutList).toString();
            list = JSONObject.parseArray(str, ShortCut.class);
            for(ShortCut shortCut:list){
                shortCut.setEnterpriseCode(enterpriseCode);
            }

            return this.shortCutService.saveCustomShortCut(list);
        }catch (Exception e){
            logger.error("saveCustomShortCut exception", e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存自定义快捷方式失败");
        }
    }

    /**
     * 获取编辑快捷入口页面展示的数据
     * */
    @RequestMapping("/getCustomEditInfo")
    @ResponseBody
    public ResultVo customEdit(){
       return this.shortCutService.getCustomEditInfo();
    }

    /**
     * 获取默认快捷入口
     * */
    @RequestMapping("/getDefault")
    @ResponseBody
    public ResultVo getDefault(){
        return this.shortCutService.getDefault();
    }


}
