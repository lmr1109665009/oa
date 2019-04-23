package com.suneee.platform.controller.system;

import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysTypeKey;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysTypeKeyService;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;



/**
 * 对象功能:系统分类表 控制器类
 *zousiyu
 * 2018-1-18
 */
@Controller
@RequestMapping("/oaApi/system/globalType/")
@Action(ownermodel = SysAuditModelType.SYSTEM_SETTING)
public class GlobalTypeOaApiController extends BaseController {


    @Resource
    private GlobalTypeService globalTypeService;
    @Resource
    private SysTypeKeyService sysTypeKeyService;

    /**
     * 取得系统分类表用于显示树层次结构的分类可以分页列表
     *
     * @param request
     * @param response
     * @param page
     * @return
     * @throws Exception
     */
    @RequestMapping("getByParentId")
    @ResponseBody
    public ResultVo getByParentId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long parentId = RequestUtil.getLong(request, "parentId", -1);
        Long catId = RequestUtil.getLong(request, "catId", 0);
        String catKey = RequestUtil.getString(request, "catKey");
        List<GlobalType> list = globalTypeService.getByParentId(parentId == -1 ? parentId = catId : parentId, catKey);
        //如果分类的ID和父类ID一致的情况表明，是根节点
        //为根节点的情况需要添加一个跟节点。
        if (catId.equals(parentId)) {
            SysTypeKey sysTypeKey = sysTypeKeyService.getById(catId);
            GlobalType globalType = new GlobalType();
            globalType.setTypeName(sysTypeKey.getTypeName());
            globalType.setCatKey(sysTypeKey.getTypeKey());

            globalType.setTypeId(sysTypeKey.getTypeId());
            globalType.setParentId(0L);
            globalType.setType(sysTypeKey.getType());
            if (list.size() == 0) {
                globalType.setIsParent("false");
            } else {
                globalType.setIsParent("true");
                globalType.setOpen("true");
            }
            globalType.setNodePath(sysTypeKey.getTypeId() + ".");
            list.add(0, globalType);
        }

        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取分类列表成功",list);
    }
}
