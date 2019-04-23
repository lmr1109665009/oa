package com.suneee.eas.common.uploader;

import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.eas.common.utils.StringUtil;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.xmlpull.v1.XmlPullParserException;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @user 子华
 * @created 2018/8/2
 */
public class MinioUploaderHandler implements UploaderHandler {
    private Integer expire;
    @Autowired
    private MinioClient client;

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
        this.expire=env.getProperty("uploader.minio.urlExpire",Integer.class);
    }

    @Override
    public void upload(String filepath, InputStream inputStream, Map<String, Object> extraParams) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, RegionConflictException, InvalidArgumentException {
        String bucket=getBucket(extraParams);
        boolean isExist = client.bucketExists(bucket);
        if(!isExist){
            client.makeBucket(bucket);
        }
        String contentType="application/octet-stream";
        if (extraParams!=null&&extraParams.get("contentType")!=null){
            contentType= (String) extraParams.get("contentType");
        }
        client.putObject(bucket,filepath,inputStream,contentType);
        if (inputStream!=null){
            inputStream.close();
        }
    }

    @Override
    public void upload(String filepath, InputStream inputStream) throws IOException, XmlPullParserException, NoSuchAlgorithmException, RegionConflictException, InvalidKeyException, InvalidArgumentException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException {
        upload(filepath,inputStream,null);
    }

    @Override
    public void upload(String filename, String dir, InputStream inputStream) throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, InvalidArgumentException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException, RegionConflictException {
        this.upload(dir+ File.separator+filename,inputStream);
    }

    @Override
    public void upload(String filename, String dir, InputStream inputStream, Map<String, Object> extraParams) throws IOException, XmlPullParserException, NoSuchAlgorithmException, RegionConflictException, InvalidKeyException, InvalidArgumentException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException {
        this.upload(dir+ File.separator+filename,inputStream,extraParams);
    }

    @Override
    public String getFileUrl(String path) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidExpiresRangeException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException {
        return this.getFileUrl(path,null);
    }

    @Override
    public String getFileUrl(String path, Map<String, Object> extraParams) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidExpiresRangeException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        String bucket=getBucket(extraParams);
        boolean isCanExpire=true;
        if (extraParams != null && extraParams.get(UploaderHandler.KEY_FILE_CAN_EXPIRE)!=null){
            isCanExpire= (boolean) extraParams.get(UploaderHandler.KEY_FILE_CAN_EXPIRE);
        }
        if (isCanExpire){
            return client.presignedGetObject(bucket, path, expire);
        }else {
            return client.getObjectUrl(bucket, path);
        }
    }

    @Override
    public String getFileUrl(String path, String bucket, Integer expireTime) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidExpiresRangeException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException{
        bucket = getBucket(bucket);
        if(null == expireTime){
            return client.presignedGetObject(bucket, path, expire);
        }
        return client.presignedGetObject(bucket, path, expireTime);
    }

    @Override
    public void download(String path) throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException {
        this.download(path,false);
    }

    @Override
    public void download(String path, Map<String, Object> extraParams) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidArgumentException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException {
        this.download(path,false,extraParams);
    }

    @Override
    public void download(String path, boolean isDownload) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidArgumentException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException {
        this.download(path,isDownload,null);
    }

    @Override
    public void download(String path, boolean isDownload, Map<String, Object> extraParams) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        response.setCharacterEncoding("utf-8");
        String bucket=getBucket(extraParams);
        InputStream inputStream=client.getObject(bucket,path);
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
    public void move(String srcPath, String destPath, Map<String, Object> extraParams) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException {
        if (srcPath.equals(destPath)){
            return;
        }
        String srcBucket=getBucket(extraParams);
        String destBucket=getBucket(extraParams);
        if (extraParams!=null&&extraParams.get("srcBucket")!=null){
            srcBucket= (String) extraParams.get("srcBucket");
        }
        if (extraParams!=null&&extraParams.get("destBucket")!=null){
            destBucket= (String) extraParams.get("destBucket");
        }
        client.copyObject(srcBucket,srcPath,destBucket,destPath);
        client.removeObject(srcBucket,srcPath);
    }

    @Override
    public void move(String srcPath, String destPath) throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, InvalidArgumentException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException {
        this.move(srcPath,destPath,null);
    }

    @Override
    public void copy(String srcPath, String destPath, Map<String, Object> extraParams) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException {
        if (srcPath.equals(destPath)){
            return;
        }
        String srcBucket=getBucket(extraParams);
        String destBucket=getBucket(extraParams);
        if (extraParams!=null&&extraParams.get("srcBucket")!=null){
            srcBucket= (String) extraParams.get("srcBucket");
        }
        if (extraParams!=null&&extraParams.get("destBucket")!=null){
            destBucket= (String) extraParams.get("destBucket");
        }
        client.copyObject(srcBucket,srcPath,destBucket,destPath);
    }

    @Override
    public void copy(String srcPath, String destPath) throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, InvalidArgumentException, ErrorResponseException, NoResponseException, InvalidBucketNameException, InsufficientDataException, InternalException {
        this.copy(srcPath,destPath,null);
    }

    @Override
    public void delete(String path, Map<String, Object> extraParams) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        String bucket=getBucket(extraParams);
        client.removeObject(bucket,path);
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
        String bucket=getBucket(extraParams);
        try {
            if (client.statObject(bucket,path)!=null){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 获取存储桶
     * @param extraParams
     * @return
     */
    private String getBucket(Map<String, Object> extraParams){
        if (extraParams!=null&&extraParams.get("bucket")!=null){
            return (String) extraParams.get("bucket");
        }
        if (StringUtil.isEmpty(ContextUtil.getRuntimeEnv())){
            return env.getProperty("uploader.minio.bucket","suneee");
        }
        return env.getProperty("uploader.minio.bucket."+ ContextUtil.getRuntimeEnv());
    }

    @Override
    public InputStream getInputStream(String path, Map<String, Object> extraParams) throws Exception{
        String bucket=getBucket(extraParams);
        return client.getObject(bucket, path);
    }

    private String getBucket(String bucket){
        if(StringUtil.isNotEmpty(bucket)){
            return bucket;
        }
        if (StringUtil.isEmpty(ContextUtil.getRuntimeEnv())){
            return env.getProperty("uploader.minio.bucket","suneee");
        }
        return env.getProperty("uploader.minio.bucket."+ ContextUtil.getRuntimeEnv());
    }
}
