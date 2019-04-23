package com.suneee.oa.controller.scene;

import com.alibaba.fastjson.JSONObject;
import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.form.BpmFormField;
import com.suneee.platform.model.form.BpmFormTable;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.form.BpmFormTableService;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.oa.model.scene.MobileScene;
import com.suneee.oa.model.scene.SubProcess;
import com.suneee.oa.service.scene.MobileSceneService;
import com.suneee.oa.service.scene.SubProcessService;
import com.suneee.ucp.base.util.PageUtil;
import com.suneee.ucp.base.util.StringUtils;
import com.suneee.ucp.base.vo.ResultVo;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <pre>
 * 对象功能:手机端场景控制类
 * 开发公司:深圳象翌
 * 开发人员:pengfeng
 * 创建时间:2018-5-14
 * </pre>
 */
@Controller
@RequestMapping("/oa/mobileScene/")
public class MobileScenceController extends BaseController {
    @Resource
    private MobileSceneService mobileSceneService;
    @Resource
    private GlobalTypeService globalTypeService;
    @Resource
    private SubProcessService subProcessService;
    @Resource
    private BpmDefinitionService bpmDefinitionService;
    @Resource
    private BpmFormTableService bpmFormTableService;
    //文件存储基本路径
    @Value("#{configProperties['common.file.basePath']}")
    private String basePath;
    //签章路径，绝对路径=basePath+contextPath
    @Value("#{configProperties['user.webSign.context']}")
    private String contextPath;


    @RequestMapping("getList")
    @ResponseBody
    public ResultVo getList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        Long userId = ContextUtil.getCurrentUserId();
        JSONObject json = new JSONObject();
        QueryFilter filter = new QueryFilter(request);
        //通过当前人的组织编码获取列表
        String  enterpriseCode=CookieUitl.getCurrentEnterpriseCode();
        filter.addFilter("enterpriseCode",enterpriseCode);
        try {
            PageList<MobileScene> allList = (PageList<MobileScene>) mobileSceneService.getAll(filter);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取列表数据成功", PageUtil.getPageVo(allList));
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取列表信息失败",e.getMessage());
        }
    }

    /**
     * 列出所有场景分类
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("getSceneType")
    @ResponseBody
    public List<GlobalType> getSceneType(HttpServletRequest request) throws Exception{
        List<GlobalType> typeList = globalTypeService.getByCatKey(GlobalType.Mobile_SCENE, false);
        return typeList;
    }

    /**
     * 按分类列出所有
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getTypeList")
    @ResponseBody
    public ResultVo getTypeList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        JSONObject json = new JSONObject();
        List listAll = new ArrayList();
        Set set = new HashSet();
        //通过当前人的组织编码获取列表
        /*String  enterpriseCode=CookieUitl.getCurrentEnterpriseCode();
        filter.addFilter("enterpriseCode",enterpriseCode);*/
        try {
            List<GlobalType> typeList = globalTypeService.getByCatKey(GlobalType.Mobile_SCENE, false);
            for (GlobalType type:typeList){
               List<MobileScene> list = mobileSceneService.getByTypeId(type.getTypeId());
                Map<String,Object> map = new HashMap<>();
                if(list.size()==0){
                    MobileScene newMobile = new MobileScene();
                    newMobile.setTypeId(type.getTypeId());
                    newMobile.setTypeName(type.getTypeName());
                    list.add(newMobile);
                }
                map.put("list",list);
                listAll.add(map);
            }
            json.put("allList",listAll);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取列表数据成功",json);
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取列表信息失败",e.getMessage());
        }
    }
    @RequestMapping("save")
    @ResponseBody
    public ResultVo saveScene(HttpServletRequest request,@RequestBody MobileScene mobileScene) throws Exception{
        try {
            Long Id =mobileScene.getId();
            String message = "";
            if(StringUtil.isEmpty(CookieUitl.getCurrentEnterpriseCode())){
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"当前用户没有设置组织，不能保存");
            };
            //关联流程表单数据JsonStr
            if(null!=Id){
                message="更新场景成功";
            }else{
                message="新增场景成功";
                List<MobileScene> list = mobileSceneService.getByDefId(mobileScene.getDefId());
                if(list.size()>0){
                    return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"该流程已定义了场景");
                }
            }
            String mobileSceneJsonData = mobileScene.getMobileSceneJsonData();
            List<SubProcess> subProcessList =null;
            if(StringUtil.isNotEmpty(mobileSceneJsonData)) {
                 subProcessList = JSONObject.parseArray(mobileSceneJsonData, SubProcess.class);
            }
            mobileSceneService.save(mobileScene,subProcessList);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,message);
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"保存失败",e.getMessage());
        }
    }

    /**
     * 判断子流程是否关联场景
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("isCorrelation")
    @ResponseBody
    public ResultVo isCorrelation(HttpServletRequest request) throws Exception{
        long defId = RequestUtil.getLong(request, "defId");
        List<MobileScene> list = mobileSceneService.getByDefId(defId);
        if(list.size()>0){
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"已关联场景，可在推送中显示");
        }else{
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"未关联场景，不能在推送中显示");
        }
    }

    @RequestMapping("del")
    @ResponseBody
    public ResultVo del(HttpServletRequest request) throws Exception{
        try {
            Long[] IdAry = RequestUtil.getLongAryByStr(request,"Id");
            if(IdAry.length>0){
                mobileSceneService.delBySceneIds(IdAry);
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"删除场景成功");
            }else{
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"没有选择要删除的记录");
            }
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"删除记录失败",e.getMessage());
        }
    }

    @RequestMapping("edit")
    @ResponseBody
    public ResultVo edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
        Long Id = RequestUtil.getLong(request,"Id");
        JSONObject json = new JSONObject();
        try {
            List<GlobalType> typeList = globalTypeService.getByCatKey(GlobalType.Mobile_SCENE, false);
            if(Id>0){
                MobileScene scene = mobileSceneService.getById(Id);
                List<SubProcess> subProcesses = subProcessService.getBySceneId(Id);
                json.put("scene",scene);
                json.put("typeList",typeList);
                json.put("subProcesses",subProcesses);
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"编辑接口调用成功",json);
            }else{
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"编辑接口调用成功",typeList);
            }
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"编辑接口调用失败",e.getMessage());
        }
    }

    /**
     * 通过流程key 获取表单信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getTableTreeByDefkey")
    @ResponseBody
    @Action(description="通过流程key 获取表单信息")
    public String getTableTreeByDefkey(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String ctx = request.getContextPath();
        String defKey = RequestUtil.getString(request,"defKey");
        String type = RequestUtil.getString(request, "type","trigger");
        BpmDefinition def = bpmDefinitionService.getMainByDefKey(defKey);

        BpmFormTable table = bpmFormTableService.getByDefId(def.getDefId(),"");
        List<BpmFormField> fieldList = handelTableTOfieldTree(table);

        JSONArray array = new JSONArray();
        for(BpmFormField field : fieldList){
            String icon ;
            if(type.equals("trigger"))
                icon = ctx+"/styles/default/images/resicon/tree_file.png";
            else icon = ctx+"/styles/default/images/resicon/o_10.png";
            if("table".equals(field.getFieldType())) icon = ctx+"/styles/default/images/resicon/tree_folder.gif";

            JSONObject json = new JSONObject();

            json.put("style", type);
            json.put("fieldId", field.getFieldId());
            json.put("tableId", field.getTableId());
            json.put("fieldType", field.getFieldType());
            json.put("fieldName", field.getFieldName());
            json.put("fieldDesc", field.getFieldDesc()+"("+field.getFieldType()+")");
            json.put("icon", icon);
            array.add(json);
        }
        return array.toString();
    }

    @RequestMapping("uploadImg")
    @ResponseBody
    public ResultVo uploadImg(MultipartHttpServletRequest request,HttpServletResponse response) throws Exception{
        JSONObject json = new JSONObject();
        try {
            MultipartFile file = request.getFile("file");
            String originalFilename = file.getOriginalFilename();
            originalFilename = StringUtils.specialCharFilter(originalFilename);
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            if(suffix!=null){
                suffix=suffix.toLowerCase();
            }
            if (!(".png".equals(suffix)||".jpg".equals(suffix)||".bmp".equals(suffix)||".jpeg".equals(suffix)||".gif".equals(suffix))){
                json.put("hasError",true);
                json.put("msg","请上传有效的图片格式文件！");
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"保存场景图片失败",json);
            }
            String dir=new SimpleDateFormat("yyyyMMdd").format(new Date());
            String destPath=contextPath+"/"+dir+"/"+System.currentTimeMillis()+suffix;
            File destFile=new File(basePath+destPath);
            if (!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
            String contextPath = request.getServletContext().getContextPath();
            json.put("imgPath",destPath);
            return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"上传图标成功",json);
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"上传图标失败",e.getMessage());
        }
    }

    @RequestMapping("getSubProcess")
    @ResponseBody
    public ResultVo startSubProcess(HttpServletRequest request,HttpServletResponse response) throws Exception{
        long subProcessId = RequestUtil.getLong(request, "Id", 0L);
        long runId = RequestUtil.getLong(request, "runId");
        try {
            SubProcess subProcess = subProcessService.getById(subProcessId);
            Long newRunId = mobileSceneService.startSubProcess(subProcess, runId);
            if(newRunId>=0) {
                return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS, "获取子流程成功",newRunId);
            }else{
                return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取子流程失败");
            }
        }catch (Exception e){
            return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"获取子流程失败",e.getMessage());
        }
    }



    private List<BpmFormField> handelTableTOfieldTree(BpmFormTable table) {
        List<BpmFormField> fieldList = new ArrayList<BpmFormField>();
        BpmFormField mainField = getFieldByTable(table);
        fieldList.add(mainField);
        fieldList.addAll(table.getFieldList());

        if(BeanUtils.isNotEmpty(table.getSubTableList()))
            for(BpmFormTable subTable :table.getSubTableList()){
                fieldList.add(getFieldByTable(subTable));
                fieldList.addAll(subTable.getFieldList());
            }
        return fieldList;
    }


    private BpmFormField getFieldByTable(BpmFormTable table) {
        BpmFormField field = new BpmFormField();
        field.setTableId(table.getMainTableId());
        field.setFieldId(table.getTableId());
        field.setFieldName(table.getTableName());
        field.setFieldDesc(table.getTableDesc());
        field.setFieldType("table");
        field.setType(table.getIsMain());
        return field;
    }
}
