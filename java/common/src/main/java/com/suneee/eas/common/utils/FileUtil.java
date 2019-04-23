package com.suneee.eas.common.utils;

import com.suneee.eas.common.uploader.UploaderHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件工具类
 * @user 子华
 * @created 2018/8/17
 */
@Component
public class FileUtil {
    //附件保存路径前缀
    public static final String PREFIX_ATTACHMENT= File.separator+"attachment";
    //轮播图保存路径前缀
    public static final String PREFIX_UPLOAD_CAROUSEL = File.separator+"upload"+File.separator+"carousel";
    //编辑器保存路径前缀
    public static final String PREFIX_UPLOAD_EDITOR = File.separator+"upload"+File.separator+"editor";
    // 场景图标保存路径前缀
    public static final String PREFIX_UPLOAD_SCENE = File.separator + "upload" + File.separator + "scene";
    //车辆图片保存路径前缀
    public static final String PREFIX_UPLOAD_CARINFO = File.separator + "upload" + File.separator + "carInfo";
    //签章图片保存路径前缀
    public static final String PREFIX_UPLOAD_WEBSIGN = File.separator + "upload" + File.separator + "webSign";
    //新闻公告图片保存路径前缀
    public static final String PREFIX_UPLOAD_BULLETIN = File.separator + "upload" + File.separator + "bulletin";
    //流程保存路径前缀
    public static final String PREFIX_UPLOAD_FLOW = File.separator + "flow" + File.separator + "attachment";
    //office控件保存路径
    public static final String PREFIX_UPLOAD_OFFICE = File.separator + "office";

    private static UploaderHandler handler;

    @Autowired
    public void setHandler(UploaderHandler handler) {
        FileUtil.handler = handler;
    }

    /**
     * 获取文件名后缀
     * @param filename
     * @return
     */
    public static String getFileExt(String filename){
        if (StringUtils.isEmpty(filename)){
            return "";
        }
        int pos=filename.lastIndexOf(".");
        if (pos<0){
            return "";
        }
        return filename.substring(pos+1);
    }

    /**
     * 创建月份目录
     * @return
     */
    public static String buildMonthPath(){
        Calendar calendar=Calendar.getInstance();
        return File.separator+ calendar.get(Calendar.YEAR)+File.separator+(calendar.get(Calendar.MONTH)+1);
    }

    /**
     * 创建日月目录
     * @return
     */
    public static String buildMonthDayPath(){
        Calendar calendar=Calendar.getInstance();
        return File.separator+ calendar.get(Calendar.YEAR)+File.separator+(calendar.get(Calendar.MONTH)+1)+File.separator+calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 创建UUID名字
     * @param filename
     * @return
     */
    public static String createUuidName(String filename){
        return UUID.randomUUID().toString().replace("-", "").toLowerCase()+"."+getFileExt(filename);
    }

    /**
     * 按照月份存储文件文件
     * @param filename
     * @return
     */
    public static String getMonthFilePath(String filename){
        return buildMonthPath()+File.separator+createUuidName(filename);
    }

    /**
     * 按照月日来存储文件
     * @param filename
     * @return
     */
    public static String getMonthDayFilePath(String filename){
        return buildMonthDayPath()+File.separator+createUuidName(filename);
    }

    /**
     * 获取文件下载路径
     * @param filepath
     * @return
     */
    public static String getDownloadUrl(String filepath){
        return getDownloadUrl(filepath,null);
    }

    /**
     * 获取文件下载路径
     * @param filepath
     * @param extParams
     * @return
     */
    public static String getDownloadUrl(String filepath,Map<String,Object> extParams){
        try {
            return handler.getFileUrl(filepath,extParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取文件下载路径
     * @param filepath
     * @param isCanExpire 是允许过期
     * @return
     */
    public static String getDownloadUrl(String filepath,boolean isCanExpire){
        Map<String,Object> params=new HashMap<String, Object>();
        params.put(UploaderHandler.KEY_FILE_CAN_EXPIRE,isCanExpire);
        return getDownloadUrl(filepath,params);
    }

    /**
     * 获取附件保存路径
     * @param filename
     * @return
     */
    public static String getAttachmentFilePath(String filename){
        String path=PREFIX_ATTACHMENT+FileUtil.getMonthDayFilePath(filename);
        return path.replace(File.separator,"/");
    }

    /**
     * 获取轮播图上传保存路径
     * @param filename
     * @return
     */
    public static String getUploadCarouselPath(String filename){
        String path=PREFIX_UPLOAD_CAROUSEL+FileUtil.getMonthDayFilePath(filename);
        return path.replace(File.separator,"/");
    }

    /**
     * 获取编辑器文件上传保存路径
     * @param filename
     * @return
     */
    public static String getUploadEditorPath(String filename){
        String path=PREFIX_UPLOAD_EDITOR+FileUtil.getMonthDayFilePath(filename);
        return path.replace(File.separator,"/");
    }

    /** 获取场景图标上传保存路径
     * @param filename
     * @return
     */
    public static String getUploadScenePath(String filename){
    	String path = PREFIX_UPLOAD_SCENE + FileUtil.getMonthDayFilePath(filename);
    	return path.replace(File.separator, "/");
    }

    /**
     * 获取车辆信息图片保存路径
     * @param filename
     * @return
     */
    public static String getUploadCarInfoPath(String filename){
        String path = PREFIX_UPLOAD_CARINFO + FileUtil.getMonthDayFilePath(filename);
        return path.replace(File.separator, "/");
    }

    /**
     * 获取签章图片保存路径
     * @param filename
     * @return
     */
    public static String getUploadWebSignPath(String filename){
        String path = PREFIX_UPLOAD_WEBSIGN + FileUtil.getMonthDayFilePath(filename);
        return path.replace(File.separator, "/");
    }

    /**
     * 获取流程保存路径
     * @param filename
     * @return
     */
    public static String getUploadFlowPath(String filename, String account){
        String path = PREFIX_UPLOAD_FLOW + File.separator + account + File.separator + filename;
        return path.replace(File.separator, "/");
    }

    /**
     * 获取office控件保存路径
     * @param filename
     * @return
     */
    public static String getUploadOfficePath(String filename){
        String path = PREFIX_UPLOAD_FLOW + FileUtil.getMonthDayFilePath(filename);
        return path.replace(File.separator, "/");
    }

	/**
	 * 获取新闻公告图片上传路径
	 * @param filename
	 * @return
	 */
	public static String getUploadBulletinPath(String filename){
        String path = PREFIX_UPLOAD_BULLETIN + FileUtil.getMonthDayFilePath(filename);
		return path.replace(File.separator, "/");
    }

}
