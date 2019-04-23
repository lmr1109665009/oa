package com.suneee.eas.oa.service.car.impl;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.car.OilCardDao;
import com.suneee.eas.oa.model.car.OilCard;
import com.suneee.eas.oa.service.car.OilCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @user zousiyu
 * @date 2018/8/20 13:11
 */
@Service
public class OilCardServiceImpl extends BaseServiceImpl<OilCard> implements OilCardService {
    
    private OilCardDao oilCardDao;
    
    @Autowired
    public void setOilCardDao(OilCardDao oilCardDao) {
        this.oilCardDao = oilCardDao;
        setBaseDao(oilCardDao);
    }

   

    @Override
    public void save(OilCard oilCard,String date) {
        //获取油卡新增id
        Long cardId = IdGeneratorUtil.getNextId();
        //获取当前企业编码
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        //获取当前用户id
        Long currrentId = ContextSupportUtil.getCurrentUserId();
        //获取当前时间
        Date date1 = new Date();
        //将油卡发卡日期字符串转化为date类型
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //判断油卡编号是否存在
        String cardNum = oilCard.getCardNum();
        boolean count = this.checkCardNumIsExist(cardNum);
        if (count) {
           throw  new  IllegalArgumentException("该油卡编号已存在!");
        }

        Date date2 = null;
        try {
            date2 = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        oilCard.setCardId(cardId);
        oilCard.setEnterpriseCode(enterpriseCode);
        oilCard.setCreateBy(currrentId);
        oilCard.setCreateTime(date1);
        oilCard.setDate(date2);
        
        oilCardDao.save(oilCard);
        
    }

    @Override
    public void update(OilCard oilCard,String date) {

        //获取当前用户id
        Long currrentId = ContextSupportUtil.getCurrentUserId();
        //获取当前时间
        Date date1 = new Date();
        //将油卡发卡日期字符串转化为date类型
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前企业编码
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        Date date2 = null;
        try {
            date2 = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        oilCard.setUpdateTime(date1);
        oilCard.setUpdateBy(currrentId);
        oilCard.setDate(date2);
        oilCard.setEnterpriseCode(enterpriseCode);
        oilCardDao.update(oilCard);
        
    }
    

    //查询油卡编号是否存在
    @Override
    public boolean checkCardNumIsExist(String cardNum){
        Map<String,Object> params = new HashMap<>();
        params.put("cardNum", cardNum);
        int count = oilCardDao.checkCardNumIsExist(params);
        if (count>0) {
            return true;
        } else {
            return false;
        }
    }
}
