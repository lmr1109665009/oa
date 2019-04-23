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
package com.suneee.oa.controller.broadcast;

import com.suneee.core.page.PageList;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.model.broadcast.Broadcast;
import com.suneee.oa.service.broadcast.BroadcastService;
import com.suneee.platform.annotion.Action;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.weixin.model.ListModel;
import com.suneee.weixin.util.CommonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈轮播图管理〉
 *
 * @author lmr
 * @create 2018/5/2
 * @since 1.0.0
 */
@Controller
@RequestMapping("/oa/broadcast/")
public class BroadcastController extends BaseController {
    @Resource
    BroadcastService broadcastService;

    //文件存储基本路径
    @Value("#{configProperties['common.file.basePath']}")
    private String basePath;
    //签章路径，绝对路径=basePath+contextPath
    @Value("#{configProperties['user.webSign.context']}")
    private String contextPath;
    //文件访问URL地址
    @Value("#{configProperties['user.webSign.url']}")
    private String staticUrl;

    private String getStaticUrl() {
        if (staticUrl == null) {
            return "";
        }
        return staticUrl;
    }

    @RequestMapping("list")
    @ResponseBody
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = new QueryFilter(request,true);
        queryFilter.addFilter("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
        List<Broadcast> list = broadcastService.getAll(queryFilter);
        ListModel model = new ListModel();
        if (list != null) {
            model = CommonUtil.getListModel((PageList) list);
        }
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取轮播图列表成功！", model);
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultVo save(HttpServletRequest request, HttpServletResponse response, Broadcast broadcast) throws Exception {
        Long id = RequestUtil.getLong(request, "id");
        String message = "";
        //如果id=0或者为null则为添加
        try {
            if (id == 0 || id == null) {
                broadcast.setId(UniqueIdUtil.genId());
                broadcast.setEnterpriseCode(ContextSupportUtil.getCurrentEnterpriseCode());
                broadcastService.add(broadcast);
                message = "添加轮播图信息成功!";
            } else {
                broadcastService.update(broadcast);
                message = "编辑轮播图信息成功!";
            }

            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, message);
        } catch (Exception e) {
            if (id == 0 || id == null) {
                message = "添加轮播图信息失败!";
            } else {
                message = "编辑轮播图信息失败!";
            }
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, message);
        }
    }

    @RequestMapping("del")
    @ResponseBody
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long[] ids = RequestUtil.getLongAryByStr(request, "id");
        try {
            broadcastService.delByIds(ids);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除轮播图信息成功!");
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除轮播图信息失败!");
        }
    }

    @RequestMapping("details")
    @ResponseBody
    public ResultVo details(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long id = RequestUtil.getLong(request, "id");

        try {
            Broadcast broadcast = broadcastService.getById(id);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取轮播图详情成功!", broadcast);
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取轮播图详情失败!");
        }
    }

    @RequestMapping("get")
    @ResponseBody
    public ResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
            List<Broadcast> broadcastList = broadcastService.getByStatus(enterpriseCode);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取轮播图详情成功!", broadcastList.get(0));
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "获取轮播图详情失败!");
        }
    }

    @RequestMapping("upload")
    @Action(description = "保存图片")
    @ResponseBody
    public ResultVo save(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
//        JSONObject json = new JSONObject();
//        String originName = file.getOriginalFilename();
//        originName = StringUtils.specialCharFilter(originName);
//        String suffix = originName.substring(originName.lastIndexOf("."));
//        if (suffix != null) {
//            suffix = suffix.toLowerCase();
//        }
//        if (!(".png".equals(suffix) || ".jpg".equals(suffix) || ".bpm".equals(suffix) || ".jpeg".equals(suffix) || ".gif".equals(suffix))) {
//            //json.addObject("hasError",true);
//            //json.addObject("msg","请上传有效的图片格式文件！");
//            return new ResultVo(ResultMessage.Fail, "请上传有效的图片格式文件！");
//        }
//        String dir = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        String destPath = contextPath + "/" + dir + "/" + System.currentTimeMillis() + suffix;
//        File destFile = new File(basePath + destPath);
//        if (!destFile.getParentFile().exists()) {
//            destFile.getParentFile().mkdirs();
//        }
//        file.transferTo(destFile);
//        return new ResultVo(ResultMessage.Success, "上传成功！", destFile);
//    }
            String path = "";
    Map<String, MultipartFile> files = request.getFileMap();
    Iterator<MultipartFile> it = files.values().iterator();

		while (it.hasNext()) {
        MultipartFile f = it.next();
        path = FileUtil.createDateFilePath(basePath,contextPath,f.getOriginalFilename());
        FileUtil.writeByte(basePath+path, f.getBytes());
    }
		return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"成功上传轮播图！",getStaticUrl()+path);
}
}