/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: BroadcastController
 * Author:   lmr
 * Date:     2018/5/2 16:06
 * Description: 轮播图管理
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.controller.broadcast;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.broadcast.Broadcast;
import com.suneee.eas.oa.service.broadcast.BroadcastService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈轮播图管理〉
 *
 * @author lmr
 * @create 2018/5/2
 * @since 1.0.0
 */
@RestController
@RequestMapping("/oa/broadcast/")
public class BroadcastApiController{
	private static final Logger LOGGER = LogManager.getLogger(BroadcastApiController.class);
    @Resource
    BroadcastService broadcastService;
    
    @RequestMapping("list")
    public ResponseMessage list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
			QueryFilter queryFilter = new QueryFilter("getAll", request);
			queryFilter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
			Pager<Broadcast> list = broadcastService.getPageBySqlKey(queryFilter);
			return ResponseMessage.success("获取轮播图列表成功！", list);
		} catch (Exception e) {
			LOGGER.error("获取轮播图列表失败：" + e.getMessage(), e);
			return ResponseMessage.fail("获取轮播图列表失败：系统内部错误！");
		}
    }

    @RequestMapping("save")
    public ResponseMessage save(HttpServletRequest request, HttpServletResponse response, Broadcast broadcast) throws Exception {
        Long id = RequestUtil.getLong(request, "id");
        String message = "";
        //如果id=0或者为null则为添加
        try {
            if (id == 0 || id == null) {
                broadcast.setId(IdGeneratorUtil.getNextId());
                broadcast.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
                broadcastService.save(broadcast);
                message = "添加轮播图信息成功!";
            } else {
                broadcastService.update(broadcast);
                message = "编辑轮播图信息成功!";
            }

            return ResponseMessage.success(message);
        } catch (Exception e) {
            if (id == 0 || id == null) {
                message = "添加轮播图信息失败：";
            } else {
                message = "编辑轮播图信息失败：";
            }
            LOGGER.error(message + e.getMessage(), e);
            return ResponseMessage.fail(message + "系统内部错误！");
        }
    }

    @RequestMapping("del")
    public ResponseMessage del(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long[] ids = RequestUtil.getLongAryByStr(request, "id");
        try {
            broadcastService.delByIds(ids);
            return ResponseMessage.success("删除轮播图信息成功!");
        } catch (Exception e) {
        	LOGGER.error("删除轮播图信息失败：" + e.getMessage(), e);
            return ResponseMessage.fail("删除轮播图信息失败：系统内部错误！");
        }
    }

    @RequestMapping("details")
    public ResponseMessage details(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long id = RequestUtil.getLong(request, "id");

        try {
            Broadcast broadcast = broadcastService.findById(id);
            return ResponseMessage.success("获取轮播图详情成功!", broadcast);
        } catch (Exception e) {
        	LOGGER.error("获取轮播图详情失败：" + e.getMessage(), e);
            return ResponseMessage.fail("获取轮播图详情失败：系统内部错误！");
        }
    }

    @RequestMapping("get")
    public ResponseMessage get(HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
            List<Broadcast> broadcastList = broadcastService.getByStatus(enterpriseCode);
            return ResponseMessage.success("获取轮播图详情成功!", broadcastList.get(0));
        } catch (Exception e) {
        	LOGGER.error("获取轮播图详情失败：" + e.getMessage(), e);
            return ResponseMessage.fail("获取轮播图详情失败：系统内部错误！");
        }
    }
}