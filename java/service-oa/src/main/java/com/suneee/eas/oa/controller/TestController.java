package com.suneee.eas.oa.controller;

import com.suneee.eas.common.component.Pager;
import com.suneee.eas.common.component.QueryFilter;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.uploader.UploaderHandler;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.model.Demo;
import com.suneee.eas.oa.service.DemoService;
import com.suneee.eas.oa.service.user.UserService;
import io.minio.errors.*;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @user 子华
 * @created 2018/7/30
 */
@RestController
public class TestController {
    @Autowired
    private DemoService demoService;
    @Autowired
    private UploaderHandler uploaderHandler;

    @RequestMapping("/test")
    public ResponseMessage test(){
        Map<String,Object> data=new HashMap<String, Object>();
        data.put("id", IdGeneratorUtil.getNextId());
        data.put("enterpriseCode", ContextSupportUtil.getCurrentEnterpriseCode());
        data.put("userId", ContextSupportUtil.getCurrentUserId());
        data.put("username", ContextSupportUtil.getCurrentUsername());
        return ResponseMessage.success("获取ID成功！",data);
    }

    @RequestMapping("/testId")
    public ResponseMessage testId(){
        Map<String,Object> data=new HashMap<String, Object>();
        Demo demo=new Demo();
        demo.setId(IdGeneratorUtil.getNextId());
        demo.setUsername("test");
        demo.setPassword("123456");
        return ResponseMessage.success("获取ID成功！",demo);
    }

    @RequestMapping("/testUpload")
    public ResponseMessage testUpload(MultipartFile file) throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, InvalidArgumentException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException, RegionConflictException {
        if (file==null){
            ResponseMessage.fail("请选择上传文件！");
        }
        String path="/temp/"+file.getOriginalFilename();
        uploaderHandler.upload(path,file.getInputStream());
        return ResponseMessage.success("上传成功！",path);
    }

    @RequestMapping("/testDownload")
    public void testDownload(String path) throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException {
        uploaderHandler.download(path,true);
    }

    @RequestMapping("/testDel")
    public ResponseMessage testDel(String path) throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException {
        uploaderHandler.delete(path);
        return ResponseMessage.success("删除成功!");
    }


    @RequestMapping("/testCopy")
    public ResponseMessage testCopy(String srcPath,String destPath) throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException {
        uploaderHandler.copy(srcPath,destPath);
        return ResponseMessage.success("复制成功!");
    }



    @RequestMapping("/testTx")
    public ResponseMessage testTx(){
        Demo demo=new Demo();
        demo.setUsername("zihua");
        demo.setPassword("123456");
        demo.setId(IdGeneratorUtil.getNextId());
        demoService.testTx(demo);
        return ResponseMessage.success("事务处理!");
    }

    @RequestMapping("/saveDemo")
    public ResponseMessage saveDemo(String username){
        Demo demo=new Demo();
        demo.setUsername(username);
        demo.setPassword("123456");
        demo.setId(IdGeneratorUtil.getNextId());
        demoService.saveDemo(demo);
        return ResponseMessage.success("添加成功!");
    }

    /**
     * filter使用
     * @param request
     * @return
     */
    @RequestMapping("/testFilter")
    public ResponseMessage testFilter(HttpServletRequest request){
        QueryFilter filter=new QueryFilter("listAll",request);
        List<Demo> demoList=demoService.listFilter(filter);
        return ResponseMessage.success("filter查询成功!",demoList);
    }
    /**
     * filter使用
     * @param request
     * @return
     */
    @RequestMapping("/testCount")
    public ResponseMessage testCount(HttpServletRequest request){
        QueryFilter filter=new QueryFilter("listCount",request);
        Integer count=demoService.getCountBySqlKey(filter);
        return ResponseMessage.success("filter查询到"+count+"条记录!",count);
    }

    @RequestMapping("/testPage")
    public ResponseMessage testPage(HttpServletRequest request){
        QueryFilter filter=new QueryFilter("listAll",request);
        Pager<Demo> demoPage=demoService.getPageBySqlKey(filter);
        return ResponseMessage.success("filter查询成功!",demoPage);
    }

    @RequestMapping("/testList")
    public ResponseMessage testList(){
        return ResponseMessage.success("查询成功!",demoService.listAll());
    }


    @Autowired
    private UserService userService;
    @RequestMapping("/testUser")
    public ResponseMessage testUser() throws UnsupportedEncodingException {
        return ResponseMessage.success("查询用户信息成功",userService.getUserDetails(1L));
    }

    @RequestMapping("/batchUser")
    public ResponseMessage batchUser() throws UnsupportedEncodingException {
        List<Long> ids=new ArrayList<>();
        ids.add(1L);
        ids.add(10000000000151L);
        return ResponseMessage.success("查询用户信息成功",userService.batchFindUser(ids));
    }

    @Autowired
    private RuntimeService runtimeService;
    @RequestMapping("/processList")
    public ResponseMessage processList(){
        List<ProcessInstance> instanceList = runtimeService.createProcessInstanceQuery().list();
        List<Map<String,Object>> dataList=new ArrayList<>();
        for (ProcessInstance instance:instanceList){
            Map<String,Object> data=new HashMap<>();
            data.put("instanceId",instance.getProcessInstanceId());
            data.put("defId",instance.getProcessDefinitionId());
            data.put("defKey",instance.getProcessDefinitionKey());
            data.put("name",instance.getProcessDefinitionName());
            data.put("desc",instance.getDescription());
            data.put("id",instance.getId());
            data.put("isEnd",instance.isEnded());
            data.put("isSuspend",instance.isSuspended());
            dataList.add(data);
        }
        return ResponseMessage.success("获取流程列表成功",dataList);
    }

    @RequestMapping("/delProcess")
    public ResponseMessage delProcess(String instanceId){
        runtimeService.deleteProcessInstance(instanceId,"无效数据");
        return ResponseMessage.success("删除成功");
    }



}
