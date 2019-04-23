package com.suneee.eas.oa.controller.system;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.constant.FunctionConstant;
import com.suneee.eas.common.constant.ModuleConstant;
import com.suneee.eas.common.uploader.UploaderHandler;
import com.suneee.eas.common.utils.FileUtil;
import com.suneee.eas.common.utils.PinyinUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.system.SysFile;
import com.suneee.eas.oa.service.system.SysFileService;
import io.minio.errors.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 系统公共服务控制器
 * @Author: 子华
 * @Date: 2018/7/31 17:54
 */
@RestController
@RequestMapping(ModuleConstant.SYSTEM_MODULE+FunctionConstant.SYS_COMMON)
public class SysCommonApiController {
	private Logger log= LogManager.getLogger(SysCommonApiController.class);
	@Autowired
	private SysFileService sysFileService;
	@Autowired
	private UploaderHandler uploaderHandler;


	/**
	 * 获取中文拼音
	 * @param request
	 * @param name
	 * @return
	 */
	@RequestMapping("getPinyin")
	public ResponseMessage getPinyin(HttpServletRequest request,String name){
		boolean isUpperCase= RequestUtil.getBoolean(request,"isUpperCase",false);
		String pinyin=null;
		if (isUpperCase){
			pinyin=PinyinUtil.getPinyinToUpperCase(name);
		}else {
			pinyin=PinyinUtil.getPinyinToLowerCase(name);
		}
		return ResponseMessage.success("获取拼音成功",pinyin);
	}

	/**
	 * 获取中文首拼音
	 * @param request
	 * @param name
	 * @return
	 */
	@RequestMapping("getFirstPinyin")
	public ResponseMessage getFirstPinyin(HttpServletRequest request,String name){
		boolean isUpperCase= RequestUtil.getBoolean(request,"isUpperCase",false);
		String pinyin=PinyinUtil.getPinYinHeadChar(name);
		if (StringUtils.isNotEmpty(pinyin)&&isUpperCase){
			pinyin=pinyin.toUpperCase();
		}
		return ResponseMessage.success("获取拼音成功",pinyin);
	}

	/**
	 * 附件上传
	 * @param file
	 * @return
	 */
	@RequestMapping("attachment")
	public ResponseMessage attachment(MultipartFile file){
		if (file==null||file.getSize()==0){
			return ResponseMessage.fail("您没有上传文件");
		}
		SysFile sysFile=new SysFile();
		sysFile.setType(SysFile.TYPE_ATTACHMENT);
		sysFile.setIsDir(SysFile.ID_FILE);
		sysFile.setContentType(file.getContentType());
		sysFile.setSize(file.getSize());
		sysFile.setParentId(0L);
		sysFile.setName(file.getOriginalFilename());
		sysFile.setExt(FileUtil.getFileExt(sysFile.getName()));
		sysFile.setPath(FileUtil.getAttachmentFilePath(sysFile.getName()));

		try {
			uploaderHandler.upload(sysFile.getPath(),file.getInputStream());
			sysFile.setDownload(FileUtil.getDownloadUrl(sysFile.getPath(),false));
			sysFileService.save(sysFile);
		} catch (Exception e) {
			log.error("附件上传失败,请联系管理员", e);
			return ResponseMessage.fail("附件上传失败,请联系管理员:"+e.getMessage(), e);
		}
		return ResponseMessage.success("上传成功",sysFile);
	}


	/**
	 * 批量上传附件
	 * @param files
	 * @return
	 */
	@RequestMapping("attachments")
	public ResponseMessage attachments(MultipartFile[] files){
		if (files==null||files.length==0){
			return ResponseMessage.fail("您没有上传文件");
		}
		List<SysFile> fileList=new ArrayList<SysFile>();
		for (MultipartFile file:files){
			SysFile sysFile=new SysFile();
			sysFile.setType(SysFile.TYPE_ATTACHMENT);
			sysFile.setIsDir(SysFile.ID_FILE);
			sysFile.setContentType(file.getContentType());
			sysFile.setSize(file.getSize());
			sysFile.setParentId(0L);
			sysFile.setName(file.getOriginalFilename());
			sysFile.setExt(FileUtil.getFileExt(sysFile.getName()));
			sysFile.setPath(FileUtil.getAttachmentFilePath(sysFile.getName()));
			try {
				uploaderHandler.upload(sysFile.getPath(),file.getInputStream());
				sysFile.setDownload(FileUtil.getDownloadUrl(sysFile.getPath(),false));
				sysFileService.save(sysFile);
				fileList.add(sysFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ResponseMessage.success("上传成功",fileList);
	}

	/**
	 * 下载附件
	 * @param id
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws InvalidArgumentException
	 * @throws ErrorResponseException
	 * @throws NoResponseException
	 * @throws InvalidBucketNameException
	 * @throws InsufficientDataException
	 * @throws InternalException
	 */
	@RequestMapping("downloadFile")
	@ResponseBody
	public ResponseMessage downloadFile(@RequestParam Long id) {
		SysFile sysFile=sysFileService.findById(id);
		if (sysFile==null){
			return ResponseMessage.fail("附件不存在");
		}
		try {
			String url = uploaderHandler.getFileUrl(sysFile.getPath(), null, 60);
			Map<String,Object> result=new HashMap<>();
			result.put("fileName",sysFile.getName()+"."+sysFile.getExt());
			result.put("url", url);
			return ResponseMessage.success("获取附件链接成功。", result);
		} catch (Exception e) {
			log.error("获取附件链接失败!"+e.getMessage(), e);
			return ResponseMessage.fail("获取附件链接失败!"+e.getMessage(), e);
		}
	}


	/**
	 * 公共上传文件
	 * @param file
	 * @param savePath
	 * @return
	 */
	private Map<String,Object> uploadFile(MultipartFile file,String savePath){
		Map<String,Object> info=new HashMap<String, Object>();
		try {
			uploaderHandler.upload(savePath,file.getInputStream());
			info.put("path",savePath);
			info.put("size",file.getSize());
			info.put("name",file.getOriginalFilename());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("上传文件异常");
		}
		return info;
	}

	/**
	 * 编辑器文件上传
	 * @param files
	 * @return
	 */
	@RequestMapping("upload/editor")
	public ResponseMessage uploadEditor(MultipartFile[] files){
		if (files==null||files.length==0){
			return ResponseMessage.fail("您没有上传文件");
		}
		List<Map<String,Object>> fileList=new ArrayList<Map<String, Object>>();
		for (MultipartFile file:files){
			try {
				Map<String,Object> info=uploadFile(file,FileUtil.getUploadEditorPath(file.getOriginalFilename()));
				fileList.add(info);
			}catch (RuntimeException e){
				e.printStackTrace();
			}
		}
		return ResponseMessage.success("上传成功",fileList);
	}

	/**
	 * 轮播图文件上传
	 * @param files
	 * @return
	 */
	@RequestMapping("upload/carousel")
	public ResponseMessage uploadCarousel(MultipartFile[] files){
		if (files==null||files.length==0){
			return ResponseMessage.fail("您没有上传文件");
		}
		List<Map<String,Object>> fileList=new ArrayList<Map<String, Object>>();
		for (MultipartFile file:files){
			try {
				Map<String,Object> info=uploadFile(file,FileUtil.getUploadCarouselPath(file.getOriginalFilename()));
				fileList.add(info);
			}catch (RuntimeException e){
				e.printStackTrace();
			}
		}
		return ResponseMessage.success("上传成功",fileList);
	}
	
	/**
	 * 场景图标上传
	 * @param files
	 * @return
	 */
	@RequestMapping("upload/scene")
	public ResponseMessage uploadScene(MultipartFile[] files){
		if (files==null||files.length==0){
			return ResponseMessage.fail("您没有上传文件");
		}
		List<Map<String,Object>> fileList=new ArrayList<Map<String, Object>>();
		for (MultipartFile file:files){
			try {
				Map<String,Object> info=uploadFile(file,FileUtil.getUploadScenePath(file.getOriginalFilename()));
				fileList.add(info);
			}catch (RuntimeException e){
				e.printStackTrace();
			}
		}
		return ResponseMessage.success("上传成功",fileList);
	}

	/**
	 * 新闻公告图片上传
	 * @param files
	 * @return
	 */
	@RequestMapping("upload/bulletin")
	public ResponseMessage uploadBulletin(MultipartFile[] files){
		if (files==null||files.length==0){
			return ResponseMessage.fail("您没有上传文件");
		}
		List<Map<String,Object>> fileList=new ArrayList<Map<String, Object>>();
		for (MultipartFile file:files){
			try {
				Map<String,Object> info=uploadFile(file,FileUtil.getUploadBulletinPath(file.getOriginalFilename()));
				fileList.add(info);
			}catch (RuntimeException e){
				e.printStackTrace();
			}
		}
		return ResponseMessage.success("上传成功",fileList);
	}

}
