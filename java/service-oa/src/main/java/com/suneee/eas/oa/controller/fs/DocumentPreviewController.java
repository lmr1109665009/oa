/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: DocumentPreviewController
 * Author:   lmr
 * Date:     2018/10/19 15:49
 * Description: 预览 下载
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.suneee.eas.oa.controller.fs;

import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FsConstant;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.uploader.UploaderHandler;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.FileUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.fs.DocFile;
import com.suneee.eas.oa.model.fs.Document;
import com.suneee.eas.oa.model.system.SysFile;
import com.suneee.eas.oa.service.fs.DocumentService;
import io.minio.MinioClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 〈一句话功能简述〉<br> 
 * 〈预览 下载〉
 *
 * @author lmr
 * @create 2018/10/19
 * @since 1.0.0
 */
@RestController
@RequestMapping(ModuleConstant.FS_MODULE + FunctionConstant.PUBLIC_DOCUMENT)
public class DocumentPreviewController {
    @Autowired
    DocumentService documentService;
    @Autowired
    private UploaderHandler uploaderHandler;
    private static final Logger LOGGER = LogManager.getLogger(DocumentPreviewController.class);
    private static final String PREFIX_TEMPLATE="fs/document/";
    /**
     * 上传文件
     *
     * @param request
     * @param file
     * @return
     */
    @RequestMapping("upload")
    public ResponseMessage upload(HttpServletRequest request, MultipartFile file) {
        if (file == null || file.getSize() == 0) {
            return ResponseMessage.fail("您没有上传文件");
        }
        String enterpriseCode = ContextSupportUtil.getCurrentEnterpriseCode();
        Document document = new Document();
        document.setContentType(file.getContentType());
        document.setExt(FileUtil.getFileExt(file.getOriginalFilename()));
        document.setSize(getsize(file.getSize()));
        String path = enterpriseCode + "/" + FsConstant.PATHTYPE_FILING_CABINET + "/" + file.getOriginalFilename();
        document.setPath(path);
        try {
            uploaderHandler.upload(document.getPath(), file.getInputStream());

        } catch (Exception e) {
            LOGGER.error("上传失败：" + e);
            return ResponseMessage.fail("文件上传失败,请联系管理员");
        }
        return ResponseMessage.success("上传成功！", document);
    }

    @RequestMapping("download/{id}")
    public void download(@PathVariable Long id){
        Document document = documentService.findById(id);
        if (document==null){
            LOGGER.error("下载文件不存在，ID："+id);
            return;
        }
        document.setReadNum(document.getReadNum()+1);
        documentService.update(document);
        String fileName = document.getName();
        Map<String,Object> params=new HashMap<>();
        params.put("attachName",fileName);
        String filePath = document.getPath();
        try {
            uploaderHandler.download(filePath,true,params);
        } catch (Exception e) {
            LOGGER.error("下载文件异常，ID："+id);
            e.printStackTrace();
        }

    }

    @RequestMapping("preview")
    public String viewDoc(@RequestParam Long id, HttpServletRequest request, Model model){
        Document document=documentService.findById(id);
//        document.setReadNum(document.getReadNum()+1);
//        documentService.update(document);
        if (document==null){
            return PREFIX_TEMPLATE+"notFound";
        }
        model.addAttribute("path",request.getContextPath()+ModuleConstant.FS_MODULE+ FunctionConstant.PUBLIC_DOCUMENT+"download/"+id);
        model.addAttribute("ext", FileUtil.getFileExt(document.getName()));
        return PREFIX_TEMPLATE+"viewFile";
    }




    /**
     * 计算文件的大小（默认是M）
     *
     * @param size
     * @return
     */
    public String getsize(Long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String s = df.format(size/1000);
        return s + "M";
    }

//    public void downloadsource(HttpServletResponse response,String billname,String filename) throws FileNotFoundException, IOException{
//        //响应头的设置
//        response.reset();
//        response.setCharacterEncoding("utf-8");
//        Map<String,Object> extraParams=new HashMap<>();
//        extraParams.put("attachName",fileName);
//        String bucket=getBucket(extraParams);
//        InputStream inputStream=client.getObject(bucket,path);
//        String contentType="application/octet-stream";
//        if (extraParams!=null&&extraParams.get("contentType")!=null){
//            contentType= (String) extraParams.get("contentType");
//        }
//        response.setContentType(contentType);
//        if (isDownload){
//            String attachName=path.substring(path.lastIndexOf('/')+1);
//            if (extraParams!=null&&extraParams.get("attachName")!=null){
//                attachName= (String) extraParams.get("attachName");
//            }
//            String agent=request.getHeader("User-Agent");
//            if (agent.contains("MSIE")) {
//                // IE浏览器
//                attachName = URLEncoder.encode(attachName, "utf-8");
//                attachName = attachName.replace("+", " ");
//            } else if (agent.contains("Firefox")) {
//                // 火狐浏览器
//                BASE64Encoder base64Encoder = new BASE64Encoder();
//                attachName = "=?utf-8?B?" + base64Encoder.encode(attachName.getBytes("utf-8")) + "?=";
//            } else if(agent.contains("Chrome")){
//                attachName=new String(attachName.getBytes("utf-8"),"ISO8859-1");
//            } else {
//                // 其它浏览器
//                attachName = URLEncoder.encode(attachName, "utf-8");
//            }
//
//            response.setHeader("Content-Disposition", "attachment;filename=\"" + attachName+"\"");
//        }
//        byte[] buff = new byte[1024];
//
//        //设置压缩包的名字
//        //解决不同浏览器压缩包名字含有中文时乱码的问题
//        String downloadName = billname+".zip";
//        //返回客户端浏览器的版本号、类型
//        String agent = request.getHeader("USER-AGENT");
//        try {
//            //针对IE或者以IE为内核的浏览器：
//            if (agent.contains("MSIE")||agent.contains("Trident")) {
//                downloadName = java.net.URLEncoder.encode(downloadName, "UTF-8");
//            } else {
//                //非IE浏览器的处理：
//                downloadName = new String(downloadName.getBytes("UTF-8"),"ISO-8859-1");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        response.setHeader("Content-Disposition", "attachment;fileName=\"" + downloadName + "\"");
//
//        //设置压缩流：直接写入response，实现边压缩边下载
//        ZipOutputStream zipos = null;
//        try {
//            zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
//            zipos.setMethod(ZipOutputStream.DEFLATED); //设置压缩方法
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //循环将文件写入压缩流
//        DataOutputStream os = null;
//
//        String modipath = request.getSession().getServletContext().getRealPath("/vod/mp4/"+filename);
//        File file = new File(modipath);
//        if(file.exists()){
//
//
//            try {
//                //添加ZipEntry，并ZipEntry中写入文件流
//                //这里，加上i是防止要下载的文件有重名的导致下载失败
//                zipos.putNextEntry(new ZipEntry(filename));
//                os = new DataOutputStream(zipos);
//                InputStream is = new FileInputStream(file);
//                byte[] b = new byte[100];
//                int length = 0;
//                while((length = is.read(b))!= -1){
//                    os.write(b, 0, length);
//                }
//                is.close();
//                zipos.closeEntry();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        //关闭流
//        try {
//            os.flush();
//            os.close();
//            zipos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
}