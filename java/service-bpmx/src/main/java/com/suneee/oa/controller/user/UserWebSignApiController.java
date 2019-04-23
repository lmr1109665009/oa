package com.suneee.oa.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.page.PageList;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.uploader.UploaderHandler;
import com.suneee.eas.common.utils.FileUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.base.vo.ResultVo;
import com.suneee.weixin.model.ListModel;
import com.suneee.weixin.util.CommonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("api/user/webSign/")
public class UserWebSignApiController extends BaseController {

    @Resource
    private SysUserService userService;
    @Resource
    private SysOrgService orgService;
    //文件存储基本路径
    @Value("#{configProperties['common.file.basePath']}")
    private String basePath;
    //签章路径，绝对路径=basePath+contextPath
    @Value("#{configProperties['user.webSign.context']}")
    private String contextPath;
    //文件访问URL地址
    @Value("#{configProperties['user.webSign.url']}")
    private String staticUrl;
    @Resource
    private UploaderHandler uploaderHandler;

    private String getStaticUrl() {
        if (staticUrl == null) {
            return "";
        }
        return staticUrl;
    }

    /**
     * 获取拥有web签名用户列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("listJson")
    @ResponseBody
    @Action(description = "查看用户签章图片分页列表接口")
    public ResultVo list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = new QueryFilter(request, true);
        queryFilter.addFilter("hasWebSign", true);
        try {
            List<SysUser> list = userService.getUsersByQuery(queryFilter);
            for (SysUser user : list) {
                SysOrg sysOrg = orgService.getByUserId(user.getUserId());
                user.setOrgName(sysOrg.getOrgPathname());
            }
            ListModel model = CommonUtil.getListModel((PageList) list);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "查看用户签章图片分页列表接口成功", model);
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "查看用户签章图片分页列表接口失败", e);
        }
    }

    @RequestMapping("save")
    @ResponseBody
    @Action(description = "保存用户签章图片")
    public ResultVo save(MultipartHttpServletRequest request, @RequestParam Long userId) throws Exception {
        try {
            SysUser sysUser = userService.getByUserId(userId);
            MultipartFile file = request.getFile("file");
            if (sysUser.getWebSignUrl() != null && file == null) {
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "签章更新成功!");
            } else if (file == null) {
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "请上传签章图片!");
            }
            JSONObject json = new JSONObject();
            String originName = file.getOriginalFilename();
            String suffix = FileUtil.getFileExt(originName);
            if (suffix != null) {
                suffix = suffix.toLowerCase();
            }
            if (!(".png".equals(suffix) || !".jpg".equals(suffix) || !".bpm".equals(suffix) || !".jpeg".equals(suffix) || !".gif".equals(suffix))) {
                json.put("hasError", true);
                json.put("msg", "请上传有效的图片格式文件！");
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "保存用户签章图片失败", json);
            }
            String path = FileUtil.getUploadWebSignPath(originName);
            uploaderHandler.upload(path, file.getInputStream());

            if (sysUser.getWebSignUrl() == null) {
                json.put("msg", "签章添加成功!");
            } else {
                json.put("msg", "签章更新成功!");
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userIds", new Long[]{userId});
            params.put("webSignUrl", path);
            userService.updateWebSign(params);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "签章添加成功!", json);
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "签章添加失败!", e);
        }
    }

    @RequestMapping("edit")
    @ResponseBody
    @Action(description = "配置用户签章")
    public ResultVo edit(HttpServletRequest request) throws Exception {
        try {
            Long userId = RequestUtil.getLong(request, "id");
            JSONObject json = new JSONObject();
            json.put("user", null);
            if (userId != null && userId > 0) {
                SysUser user = userService.getByUserId(userId);
                SysOrg sysOrg = orgService.getByUserId(userId);
                user.setOrgName(sysOrg.getOrgName());
                json.put("user", user);
                json.put("staticUrl", user.getWebSignUrl());
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "配置用户签章接口调用成功", json);
            }
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "配置用户签章接口调用成功", json);
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "配置用户签章接口调用失败", e);
        }

    }

    @RequestMapping("del")
    @Action(description = "删除签章图片")
    @ResponseBody
    public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
            if (lAryId == null || lAryId.length == 0) {
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "没有选择记录");
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userIds", lAryId);
            params.put("webSignUrl", null);
            userService.updateWebSign(params);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除签章成功");
        } catch (Exception e) {
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除签章失败", e);
        }
    }
}
