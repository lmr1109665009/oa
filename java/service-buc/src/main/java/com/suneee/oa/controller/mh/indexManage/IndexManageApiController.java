package com.suneee.oa.controller.mh.indexManage;

import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.ucp.mh.model.customColumn.IndexCustomDTO;
import com.suneee.ucp.mh.service.indexManage.IndexManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 首页管理controller
 * @author ytw
 * date:2018.04.03
 * */

@RequestMapping("/mh/index/indexManageApi")
@Controller
public class IndexManageApiController {

    private Logger logger = LoggerFactory.getLogger(IndexManageApiController.class);

    @Autowired
    private IndexManageService indexManageService;

    /**
     * 保存首页自定义信息
     * */
    @RequestMapping(value = "/saveIndexCustom",method = RequestMethod.POST)
    @ResponseBody
    public ResultVo saveIndexCustom(@RequestBody IndexCustomDTO indexCustomDTO){
        try {
            return this.indexManageService.saveIndexCustom(indexCustomDTO);
        }catch (Exception e){
            logger.error("saveIndexCustom exception, params={}",indexCustomDTO, e);
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"保存失败");
        }
    }

}
