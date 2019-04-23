package com.suneee.eas.oa.controller.car;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.DateUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.common.utils.StringUtil;
import com.suneee.eas.oa.model.car.OilCard;
import com.suneee.eas.oa.service.car.OilCardService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @user zousiyu
 * @date 2018/8/20 13:34
 */
@RestController
@RequestMapping(ModuleConstant.CAR_MODULE + FunctionConstant.OIL_INFO)
public class OilCardApiController {
    private static final Logger LOGGER = LogManager.getLogger(OilCardApiController.class);
    
    @Autowired
    private OilCardService oilCardService;

    /**
     * 获取油卡信息列表
     * @param request
     * @return
     */
    @RequestMapping("listPage")
    public ResponseMessage listPage(HttpServletRequest request){
//        String startTime = RequestUtil.getString(request, "Q_begindate_DL");
        String endTime = RequestUtil.getString(request, "Q_enddate_DG");
        QueryFilter queryFilter = new QueryFilter("listAll", request);
        if(StringUtil.isNotEmpty(endTime)){
            queryFilter.addFilter("enddate",DateUtil.getDate(endTime, DateUtil.FORMAT_DATE));
        }
//        queryFilter.addFilter("begindate", DateUtil.getDate(startTime, DateUtil.FORMAT_DATE));
       
        queryFilter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
        try {
            Pager<OilCard> page = oilCardService.getPageBySqlKey(queryFilter);
            return ResponseMessage.success("获取列表成功!", page);
        } catch (Exception e) {
            LOGGER.error("获取列表失败!" + e.getMessage());
            return ResponseMessage.fail("获取列表失败!");
        }
    }

    /**
     * 新增/修改油卡信息
     * @param request
     * @param oilCard
     * @return
     */
    @RequestMapping("save")
    public ResponseMessage save(HttpServletRequest request,OilCard oilCard){
        Long cardId = oilCard.getCardId();
        String date = RequestUtil.getString(request,"date");
        try {
            if (cardId == 0) {
                oilCardService.save(oilCard,date);
                return ResponseMessage.success("新增油卡信息成功!");
            } else {
                oilCardService.update(oilCard,date);
                return ResponseMessage.success("修改油卡信息成功!");
            }
        }catch (IllegalArgumentException ie){
            LOGGER.error("该油卡编号已存在!",ie.getMessage());
            return ResponseMessage.fail(ie.getMessage());
        }catch (Exception e) {
            if (cardId == 0) {
                LOGGER.error("新增油卡信息失败!",e.getMessage());
               return ResponseMessage.fail("新增油卡信息失败!");
            } else {
                LOGGER.error("修改油卡信息失败!",e.getMessage());
               return ResponseMessage.fail("修改油卡信息失败!");
            }
        }
         
    }

    /**
     * 根据油卡id删除油卡信息
     * @param request
     * @return
     */
    @RequestMapping("delByCardId")
    public ResponseMessage delByCardId(HttpServletRequest request){
        Long[] ids = RequestUtil.getLongAryByStr(request,"cardId");
        if (ids ==null||ids.length==0) {
            return  ResponseMessage.fail("删除失败!参数不能为空!");
        }
        try {
            for (Long id :ids){
                oilCardService.deleteById(id);
            }
            return ResponseMessage.success("删除油卡信息成功!");
        } catch (Exception e) {
            LOGGER.error("删除油卡失败!",e.getMessage());
            return ResponseMessage.fail("删除油卡失败!");
        }
    }

    /**
     * 获取油卡详情
     * @param request
     * @return
     */
    @RequestMapping("edit")
    public ResponseMessage edit(HttpServletRequest request){
        Long cardId = RequestUtil.getLong(request,"cardId");
        if(cardId==0){
            return ResponseMessage.fail("获取失败,参数不能为空!");
        }
        try {
            OilCard oilCard = oilCardService.findById(cardId);
            return ResponseMessage.success("获取油卡详情成功!", oilCard);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("获取油卡详情失败!",e.getMessage());
            return ResponseMessage.fail("获取油卡详情失败!");
        }
    }
}

