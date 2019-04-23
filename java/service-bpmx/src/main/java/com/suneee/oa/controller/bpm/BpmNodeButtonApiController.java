package com.suneee.oa.controller.bpm;

import com.suneee.core.bpm.model.FlowNode;
import com.suneee.core.bpm.model.NodeCache;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmNodeButtonService;
import com.suneee.platform.service.bpm.BpmNodeSetService;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.form.FormUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象功能:审批按钮配置管理
 * 开发公司:深圳象翌
 * 开发人员:子华
 * 创建时间:2017-12-27 18:26:13
 */
@Controller
@RequestMapping("/api/bpm/bpmNodeButton/")
@Action(ownermodel = SysAuditModelType.PROCESS_MANAGEMENT)
public class BpmNodeButtonApiController extends BaseController {
    @Resource
    private BpmNodeButtonService bpmNodeButtonService;
    @Resource
    private BpmDefinitionService bpmDefinitionService;
    @Resource
    private BpmNodeSetService bpmNodeSetService;
    @Resource
    private BpmService bpmService;

    /**
     * 流程定义配置操作按钮列表
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("list")
    @ResponseBody
    @Action(description = "流程定义配置操作按钮列表")
    public ResultVo list(HttpServletRequest request) throws Exception {
        Long defId = RequestUtil.getLong(request, "defId");

        BpmDefinition bpmDefinition = bpmDefinitionService.getById(defId);

        String actDefId = bpmDefinition.getActDefId();

        List<BpmNodeSet> list = bpmNodeSetService.getByDefId(defId);

        Map<String, FlowNode> taskMap = NodeCache.getByActDefId(actDefId);

        Map<String, List<BpmNodeButton>> btnMap = bpmNodeButtonService.getMapByDefId(defId);

        //读按钮配置文件button.xml
        String buttonPath = FormUtil.getDesignButtonPath();
        String xml = FileUtil.readFile(buttonPath + "button.xml");
        Document document = Dom4jUtil.loadXml(xml);
        Element root = document.getRootElement();
        String xmlStr = root.asXML();
        BpmNodeButtonXml bpmButtonList = (BpmNodeButtonXml) XmlBeanUtil
                .unmarshall(xmlStr, BpmNodeButtonXml.class);
        List<BpmButton> btnList = bpmButtonList.getButtons();
        //对按钮进行分类
        List<BpmButton> startBtnList = new ArrayList<BpmButton>();//起始节点按钮
        List<BpmButton> firstNodeBtnList = new ArrayList<BpmButton>();//第一个节点按钮
        List<BpmButton> signBtnList = new ArrayList<BpmButton>();//会签节点按钮
        List<BpmButton> commonBtnList = new ArrayList<BpmButton>();//普通节点按钮
        for (BpmButton button : btnList) {
            if (button.getInit() == 0) continue;
            if (button.getType() == 0) {
                startBtnList.add(button);
            } else if (button.getType() == 1) {
                firstNodeBtnList.add(button);
            } else if (button.getType() == 2) {
                signBtnList.add(button);
            } else if (button.getType() == 3) {
                commonBtnList.add(button);
            }
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("btnMap", btnMap);
        data.put("taskMap", taskMap);
        data.put("bpmNodeSetList", list);
        data.put("bpmDefinition", bpmDefinition);
        data.put("startBtnList", startBtnList);
        data.put("firstNodeBtnList", firstNodeBtnList);
        data.put("signBtnList", signBtnList);
        data.put("commonBtnList", commonBtnList);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取操作按钮成功", data);

    }

    /**
     * 按钮初始化
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping("initAll")
    @ResponseBody
    @Action(description = "初始化操作按钮",
            detail = "初始化流程定义 【${SysAuditLinkService.getBpmDefinitionLink(Long.valueOf(defId))}】的全部操作按钮"
    )
    public ResultVo initAll(HttpServletRequest request) throws Exception {
        Long defId = RequestUtil.getLong(request, "defId", 0);
        ResultVo resultMessage = null;
        try {
            bpmNodeButtonService.initAll(defId);
            resultMessage = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "初始化按钮成功!");
        } catch (Exception ex) {
            resultMessage = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "初始化按钮失败:" + ex.getMessage());
        }
        return resultMessage;
    }


    /**
     * 清除按钮配置
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping("delByDefId")
    @ResponseBody
    @Action(description = "清除按钮配置",

            detail = "清除流程定义 【${SysAuditLinkService.getBpmDefinitionLink(Long.valueOf(defId))}】的表单按钮"
    )
    public ResultVo delByDefId(HttpServletRequest request) throws Exception {
        Long defId = RequestUtil.getLong(request, "defId", 0);

        ResultVo resultMessage = null;
        try {
            bpmNodeButtonService.delByDefId(defId);
            resultMessage = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "清除流程表单按钮成功!");
        } catch (Exception ex) {
            resultMessage = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "清除流程表单按钮失败:" + ex.getMessage());
        }
        return resultMessage;
    }

    /**
     * 初始化操作按钮
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("init")
    @ResponseBody
    @Action(description = "初始化操作按钮",
            detail = "初始化流程定义 【${SysAuditLinkService.getBpmDefinitionLink(Long.valueOf(defId))}】节点" +
                    "<#if !StringUtil.isEmpty(nodeId)>" +
                    "【${SysAuditLinkService.getNodeName(Long.valueOf(defId),nodeId)}】" +
                    "</#if>" +
                    "的操作按钮"
    )
    public ResultVo init(HttpServletRequest request) throws Exception {
        Long defId = RequestUtil.getLong(request, "defId", 0);
        String nodeId = RequestUtil.getString(request, "nodeId");
        ResultVo resultMessage = null;
        try {
            bpmNodeButtonService.init(defId, nodeId);
            resultMessage = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "初始化按钮成功!");
        } catch (Exception ex) {
            resultMessage = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "初始化按钮失败:" + ex.getMessage());
        }
        return resultMessage;
    }

    /**
     * 根据流程定义Id和节点ID删除按钮操作。
     *
     * @param request
     * @throws IOException
     */
    @RequestMapping("delByDefNodeId")
    @ResponseBody
    @Action(description = "清除按钮配置",
            detail = "删除流程定义 【${SysAuditLinkService.getBpmDefinitionLink(Long.valueOf(defId))}】的节点的按钮操作"
    )
    public ResultVo delByDefNodeId(HttpServletRequest request) throws IOException {
        Long defId = RequestUtil.getLong(request, "defId", 0);
        String nodeId = RequestUtil.getString(request, "nodeId");

        ResultVo resultMessage = null;
        try {
            bpmNodeButtonService.delByDefNodeId(defId, nodeId);
            resultMessage = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "删除流程表单按钮成功!");
        } catch (Exception ex) {
            resultMessage = new ResultVo(ResultVo.COMMON_STATUS_FAILED, "删除流程表单按钮失败:" + ex.getMessage());
        }
        return resultMessage;
    }

    /**
     * 编辑自定义按钮
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("edit")
    @ResponseBody
    @Action(description = "编辑自定义工具条")
    public ResultVo edit(HttpServletRequest request) throws Exception {
        Long id = RequestUtil.getLong(request, "id");
        BpmNodeButton bpmNodeButton = null;
        Map<String, Object> data = new HashMap<String, Object>();
        Long defId = RequestUtil.getLong(request, "defId", 0);
        String nodeId = RequestUtil.getString(request, "nodeId");

        if (id != 0) {
            bpmNodeButton = bpmNodeButtonService.getById(id);
            BpmDefinition bpmDefinition = bpmDefinitionService.getById(bpmNodeButton.getDefId());
            data.put("bpmDefinition", bpmDefinition);

        } else {
            BpmDefinition bpmDefinition = bpmDefinitionService.getById(defId);
            data.put("bpmDefinition", bpmDefinition);
            String actDefId = bpmDefinition.getActDefId();
            bpmNodeButton = new BpmNodeButton();
            bpmNodeButton.setDefId(defId);
            if (StringUtil.isNotEmpty(nodeId)) {
                boolean rtn = bpmService.isSignTask(actDefId, nodeId);
                bpmNodeButton.setNodetype(rtn ? 1 : 0);
                bpmNodeButton.setIsstartform(0);
            } else {
                bpmNodeButton.setIsstartform(1);
            }
            bpmNodeButton.setActdefid(actDefId);
            bpmNodeButton.setNodeid(nodeId);
        }
        //读按钮配置文件button.xml
        String buttonPath = FormUtil.getDesignButtonPath();
        String xml = FileUtil.readFile(buttonPath + "button.xml");
        Document document = Dom4jUtil.loadXml(xml);
        Element root = document.getRootElement();
        String xmlStr = root.asXML();
        BpmNodeButtonXml bpmButtonList = (BpmNodeButtonXml) XmlBeanUtil
                .unmarshall(xmlStr, BpmNodeButtonXml.class);
        List<BpmButton> list = bpmButtonList.getButtons();
        data.put("bpmNodeButton", bpmNodeButton);
        data.put("defId", defId);
        data.put("nodeId", nodeId);
        data.put("buttonSelectList", list);
        return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取编辑自定义按钮成功", data);
    }
}
