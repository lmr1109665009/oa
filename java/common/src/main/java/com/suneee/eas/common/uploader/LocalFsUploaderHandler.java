package com.suneee.eas.common.uploader;

import io.minio.errors.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.xmlpull.v1.XmlPullParserException;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * 服务器本地存储
 * @user 子华
 * @created 2018/8/2
 */
public class LocalFsUploaderHandler implements UploaderHandler {
    private String storePath;
    private String url;

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
        this.storePath=env.getProperty("uploader.localFs.storePath");
        this.url=env.getProperty("uploader.localFs.url");
    }

    @Override
    public void upload(String filepath, InputStream inputStream, Map<String, Object> extraParams) throws IOException {
        File destFile=new File(storePath+File.separator+filepath);
        if (!destFile.getParentFile().exists()){
            destFile.getParentFile().mkdirs();
        }
        FileOutputStream outputStream=new FileOutputStream(destFile);
        IOUtils.copy(inputStream,outputStream);
        if (inputStream!=null){
            inputStream.close();
        }
        if (outputStream!=null){
            outputStream.close();
        }
    }

    @Override
    public void upload(String filepath, InputStream inputStream) throws IOException {
        upload(filepath,inputStream,null);
    }

    @Override
    public void upload(String filename, String dir, InputStream inputStream) throws IOException {
        this.upload(dir+ File.separator+filename,inputStream);
    }

    @Override
    public void upload(String filename, String dir, InputStream inputStream, Map<String, Object> extraParams) throws IOException {
        this.upload(dir+ File.separator+filename,inputStream,extraParams);
    }

    @Override
    public String getFileUrl(String path) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidExpiresRangeException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException {
        return getFileUrl(path,null);
    }

    @Override
    public String getFileUrl(String path, Map<String, Object> extraParams) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidExpiresRangeException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        return url+path;
    }

    @Override
    public String getFileUrl(String path, String bucket, Integer expireTime) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidExpiresRangeException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        return url+path;
    }

    @Override
    public void download(String path, boolean isDownload) throws IOException {
       this.download(path,isDownload,null);
    }

    @Override
    public void download(String path, boolean isDownload, Map<String, Object> extraParams) throws IOException {
        HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        response.setCharacterEncoding("utf-8");
        File srcFile=new File(storePath+path);
        String contentType="application/octet-stream";
        if (extraParams!=null&&extraParams.get("contentType")!=null){
            contentType= (String) extraParams.get("contentType");
        }
        response.setContentType(contentType);
        if (isDownload){
            String attachName=path.substring(path.lastIndexOf('/')+1);
            if (extraParams!=null&&extraParams.get("attachName")!=null){
                attachName= (String) extraParams.get("attachName");
            }
            String agent=request.getHeader("User-Agent");
            if (agent.contains("MSIE")) {
                // IE浏览器
                attachName = URLEncoder.encode(attachName, "utf-8");
                attachName = attachName.replace("+", " ");
            } else if (agent.contains("Firefox")) {
                // 火狐浏览器
                BASE64Encoder base64Encoder = new BASE64Encoder();
                attachName = "=?utf-8?B?" + base64Encoder.encode(attachName.getBytes("utf-8")) + "?=";
            } else if(agent.contains("Chrome")){
                attachName=new String(attachName.getBytes("utf-8"),"ISO8859-1");
            } else {
                // 其它浏览器
                attachName = URLEncoder.encode(attachName, "utf-8");
            }
            response.setHeader("Content-Disposition", "attachment;filename=\"" + attachName+"\"");
        }
        FileInputStream inputStream=new FileInputStream(srcFile);
        byte[] buff = new byte[1024];
        BufferedInputStream bufferedInputStream=new BufferedInputStream(inputStream);
        ServletOutputStream outputStream =response.getOutputStream();
        int len;
        while ((len=bufferedInputStream.read(buff))!=-1){
            outputStream.write(buff,0,len);
        }
        bufferedInputStream.close();
        inputStream.close();
        outputStream.close();
    }

    @Override
    public void download(String path) throws IOException {
        this.download(path,false);
    }

    @Override
    public void download(String path, Map<String, Object> extraParams) throws IOException {
        this.download(path,false,extraParams);
    }

    @Override
    public void move(String srcPath, String destPath, Map<String, Object> extraParams) throws IOException {
        if (srcPath.equals(destPath)){
            return;
        }
        FileUtils.moveFile(new File(storePath+File.separator+srcPath),new File(storePath+File.separator+destPath));
    }

    @Override
    public void move(String srcPath, String destPath) throws IOException {
        this.move(srcPath,destPath,null);
    }

    @Override
    public void copy(String srcPath, String destPath, Map<String, Object> extraParams) throws IOException {
        if (srcPath.equals(destPath)){
            return;
        }
        FileUtils.copyFile(new File(storePath+File.separator+srcPath),new File(storePath+File.separator+destPath));
    }

    @Override
    public void copy(String srcPath, String destPath) throws IOException {
        this.copy(srcPath,destPath,null);
    }

    @Override
    public void delete(String path, Map<String, Object> extraParams) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        FileUtils.forceDelete(new File(storePath+File.separator+path));
    }

    @Override
    public void delete(String path) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidArgumentException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException {
        this.delete(path,null);
    }

    @Override
    public boolean isExist(String path) {
        return this.isExist(path,null);
    }

    @Override
    public boolean isExist(String path, Map<String, Object> extraParams) {
        File file=new File(storePath+File.separator+path);
        return file.exists();
    }

    @Override
    public InputStream getInputStream(String path, Map<String, Object> extraParams) throws Exception {
        return null;
    }
}
