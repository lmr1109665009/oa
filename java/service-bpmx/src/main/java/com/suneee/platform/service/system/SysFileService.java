package com.suneee.platform.service.system;

import com.amazonaws.services.s3.model.CompleteMultipartUploadResult;
import com.suneee.core.api.org.model.ISysUser;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.eas.common.bean.upload.ChunkUpload;
import com.suneee.eas.common.uploader.UploaderHandler;
import com.suneee.eas.common.uploader.s3.CloudStorageHandler;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.dao.system.SysFileDao;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysFile;
import com.suneee.ucp.base.util.StringUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 对象功能:附件 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:csx
 * 创建时间:2011-11-26 18:19:16
 */
@Service
public class SysFileService extends BaseService<SysFile>
{
	public static final String WORD_TEMPLATE = "wordTemplate";
	@Resource
	private SysFileDao dao;
	@Resource
	private GlobalTypeService globalTypeService;
	@Resource
	private SysTypeKeyService sysTypeKeyService;

	@Resource
	private UploaderHandler uploaderHandler;

	@Resource
	private CloudStorageHandler cloudStorageHandler;

	public SysFileService()
	{
	}

	@Override
	protected IEntityDao<SysFile, Long> getEntityDao() {
		return dao;
	}

	public List<SysFile> getFileAttch(QueryFilter fileter){
		return dao.getFileAttch(fileter);
	}

	/**
	 * 删除附件及附件表中的记录
	 * @param ids
	 * @throws Exception
	 */
	public void delSysFileByIds(Long[] ids) throws Exception{
		for (Long id : ids) {
			SysFile sysFile = dao.getById(id);
			if(null != sysFile){
				uploaderHandler.delete(sysFile.getFilePath());
				dao.delById(id);
			}
		}
	}
	/**
	 * 创建文件目录
	 *
	 * @param tempPath
	 * @param fileName
	 *            文件名称
	 * @return 文件的完整目录
	 */
	private String createFilePath(String tempPath, String fileName) {
		File one = new File(tempPath);
		return one.getPath() + File.separator + fileName;
	}

	/**
	 * 创建文件目录
	 *
	 *
	 * @param fileName
	 *            文件名称
	 * @return 文件的完整目录
	 */
	private String createFilePath(String userAccount,String fileName,String fileType) {
		if(StringUtil.isEmpty(fileType) || !WORD_TEMPLATE.equalsIgnoreCase(fileType)){
			return this.createFilePath(userAccount, fileName);
		}
		return WORD_TEMPLATE + File.separator + fileName;
	}

	/**
	 * 上传附件
	 * @param typeId 类型ID
	 * @param fileId 附件ID
	 * @param uploadName 上传的附件名称
	 * @param uploadType 上传类型
	 * @param fileFormates 格式限定
	 * @param files 上传的文件流
	 * @return
	 * @throws Exception
	 */
	public JSONObject uploadFile(Long typeId, Long fileId, String fileType, String uploadName, String uploadType, String fileFormates, Map<String, MultipartFile> files) throws Exception{
		JSONObject jobject = new JSONObject();
		boolean mark = true;
		ISysUser currentUser = ContextSupportUtil.getCurrentUser();
		// 获取附件类型
		GlobalType globalType = null;
		if (typeId > 0) {
			globalType = globalTypeService.getById(typeId);
		}
		Iterator<MultipartFile> it = files.values().iterator();
		while (it.hasNext()) {
			Boolean isAdd = false;
			String oldFilePath = "";
			SysFile sysFile;
			if(BeanUtils.isNotIncZeroEmpty(fileId)){
				sysFile = dao.getById(fileId);
				oldFilePath = sysFile.getFilePath();
			}
			else{
				// 新增的上传文件
				isAdd = true;
				// 生成新的文件ID
				fileId = UniqueIdUtil.genId();
				sysFile = new SysFile();
				sysFile.setFileId(fileId);
			}
			MultipartFile f = it.next();
			if(StringUtil.isNotEmpty(uploadName)){
				if(!uploadName.equals(f.getName())){
					throw new RuntimeException("上传名称与文件名称不符");
				}
			}
			String oriFileName = f.getOriginalFilename();
			//去除文件名称特殊字符
			oriFileName= StringUtils.specialCharFilter(oriFileName);
			String extName = FileUtil.getFileExt(oriFileName);
			//文件格式要求
			if(StringUtil.isNotEmpty(fileFormates)){
				//不符合文件格式要求的就标志为false
				if(!( fileFormates.contains("*."+extName) ) ){
					mark = false;
				}
			}
			if(mark){
				String fileName = fileId + "." + extName;
				String filePath = "";

				// 当前用户的信息
				if (currentUser != null) {
					sysFile.setCreatorId(currentUser.getUserId());
					sysFile.setCreator(currentUser.getFullname());
				} else {
					sysFile.setCreator(SysFile.FILE_UPLOAD_UNKNOWN);
				}
				//获取文件保存路径
				filePath = com.suneee.eas.common.utils.FileUtil.getUploadFlowPath(fileName, ContextSupportUtil.getCurrentUser().getAccount());
				// 附件名称
				sysFile.setFileName(oriFileName.lastIndexOf('.')==-1 ? oriFileName:oriFileName.substring(0, oriFileName.lastIndexOf('.')));
				//保存相对路径
				sysFile.setFilePath(filePath);
				// 附件类型
				if (globalType != null) {
					sysFile.setTypeId(globalType.getTypeId());
					sysFile.setFileType(globalType.getTypeName());
				} else {
					sysFile.setTypeId(sysTypeKeyService.getByKey(GlobalType.CAT_FILE).getTypeId());
					sysFile.setFileType("-");
				}
				// 上传时间
				sysFile.setCreatetime(new java.util.Date());
				// 扩展名
				sysFile.setExt(extName);
				// 字节总数
				sysFile.setTotalBytes(f.getSize());
				// 说明
				sysFile.setNote(FileUtil.getSize(f.getSize()));
				// 总的字节数
				sysFile.setDelFlag(SysFile.FILE_NOT_DEL);

				if(isAdd){
					uploaderHandler.upload(filePath, f.getInputStream());
					dao.add(sysFile);
				}
				else{
					uploaderHandler.upload(filePath, f.getInputStream());
					dao.update(sysFile);
					boolean tag = true;
					String newFilePath = sysFile.getFilePath();
					if(StringUtil.isNotEmpty(newFilePath)&&StringUtil.isNotEmpty(oldFilePath)){
						if(newFilePath.trim().equals(oldFilePath.trim())){
							tag = false;
						}
					}
					// 修改了文件的存放路径，需要删除之前路径下的文件
					if(tag){
						sysFile.setFilePath(oldFilePath);
						uploaderHandler.delete(filePath);
					}
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				//下面一行意思：将原来的文件名 + 现在上传者+时间+文件大小
				long size = f.getSize()/1024;
				if (null == currentUser.getAliasName()) {
					oriFileName = oriFileName + " " + " " + ContextSupportUtil.getCurrentUser().getFullname() +" " + " "
							+sdf.format(new Date())+" " + " "+size+"K";
				}else {
					oriFileName = oriFileName + " " + " " + ContextSupportUtil.getCurrentUser().getAliasName() +" " + " "
							+sdf.format(new Date())+" " + " "+size+"K";
				}
				jobject.put("success", "true");
				jobject.put("fileId", fileId);
				jobject.put("fileName", oriFileName);

			}else{
				jobject.put("success", "false");
				jobject.put("message", "文件格式不符合要求!");
			}
		}
		return jobject;
	}

	/**
	 * 上传附件
	 * @param typeId 类型ID
	 * @param fileId 附件ID
	 * @param uploadName 上传的附件名称
	 * @param fileFormates 格式限定
	 * @param files 上传的文件流
	 * @return
	 * @throws Exception
	 */
	public JSONObject uploadFile(Long typeId, Long fileId, String uploadName, String fileFormates, Map<String, MultipartFile> files, ChunkUpload chunkUpload) throws Exception{
		CompleteMultipartUploadResult result = null;
		JSONObject jobject = new JSONObject();
		boolean mark = true;
		ISysUser currentUser = ContextSupportUtil.getCurrentUser();
		// 获取附件类型
		GlobalType globalType = null;
		if (typeId > 0) {
			globalType = globalTypeService.getById(typeId);
		}
		Iterator<MultipartFile> it = files.values().iterator();
		while (it.hasNext()) {
			Boolean isAdd = false;
			String oldFilePath = "";
			SysFile sysFile;
			if(BeanUtils.isNotIncZeroEmpty(fileId)){
				sysFile = dao.getById(fileId);
				oldFilePath = sysFile.getFilePath();
			}else{
				// 新增的上传文件
				isAdd = true;
				sysFile = new SysFile();
				// 生成新的文件ID
				fileId = UniqueIdUtil.genId();
				sysFile.setFileId(fileId);
			}
			MultipartFile f = it.next();
			if(StringUtil.isNotEmpty(uploadName)){
				if(!uploadName.equals(f.getName())){
					throw new RuntimeException("上传名称与文件名称不符");
				}
			}
			String oriFileName = f.getOriginalFilename();
			//去除文件名称特殊字符
			oriFileName= StringUtils.specialCharFilter(oriFileName);
			String extName = FileUtil.getFileExt(oriFileName);
			//文件格式要求
			if(StringUtil.isNotEmpty(fileFormates)){
				//不符合文件格式要求的就标志为false
				if(!( fileFormates.contains("*."+extName) ) ){
					mark = false;
				}
			}
			if(mark){
				String fileName = fileId + "." + extName;
				String filePath = "";

				// 当前用户的信息
				if (currentUser != null) {
					sysFile.setCreatorId(currentUser.getUserId());
					sysFile.setCreator(currentUser.getFullname());
				} else {
					sysFile.setCreator(SysFile.FILE_UPLOAD_UNKNOWN);
				}
				//获取文件保存路径
				filePath = com.suneee.eas.common.utils.FileUtil.getUploadFlowPath(fileName, ContextSupportUtil.getCurrentUser().getAccount());

				result = cloudStorageHandler.multiUpload(filePath, f.getInputStream(), chunkUpload);
				if(null != result){
					//新增的时候取最后上传成功分块路径上的id
					filePath = result.getKey();
					// 附件名称
					sysFile.setFileName(oriFileName.lastIndexOf('.')==-1 ? oriFileName:oriFileName.substring(0, oriFileName.lastIndexOf('.')));
					//保存相对路径
					sysFile.setFilePath(filePath);
					// 附件类型
					if (globalType != null) {
						sysFile.setTypeId(globalType.getTypeId());
						sysFile.setFileType(globalType.getTypeName());
					} else {
						sysFile.setTypeId(sysTypeKeyService.getByKey(GlobalType.CAT_FILE).getTypeId());
						sysFile.setFileType("-");
					}
					// 上传时间
					sysFile.setCreatetime(new java.util.Date());
					// 扩展名
					sysFile.setExt(extName);
					// 字节总数
					sysFile.setTotalBytes(f.getSize());
					// 说明
					sysFile.setNote(FileUtil.getSize(f.getSize()));
					// 总的字节数
					sysFile.setDelFlag(SysFile.FILE_NOT_DEL);

					if(isAdd){
						String id = filePath.substring(filePath.lastIndexOf("/")+1, filePath.lastIndexOf("."));
						fileId = Long.valueOf(id);
						sysFile.setFileId(fileId);
						dao.add(sysFile);
					}else{
						dao.update(sysFile);
						boolean tag = true;
						String newFilePath = sysFile.getFilePath();
						if(StringUtil.isNotEmpty(newFilePath)&&StringUtil.isNotEmpty(oldFilePath)){
							if(newFilePath.trim().equals(oldFilePath.trim())){
								tag = false;
							}
						}
						// 修改了文件的存放路径，需要删除之前路径下的文件
						if(tag){
							sysFile.setFilePath(oldFilePath);
							uploaderHandler.delete(filePath);
						}
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					//下面一行意思：将原来的文件名 + 现在上传者+时间+文件大小
					long size = f.getSize()/1024;
					if (null == currentUser.getAliasName()) {
						oriFileName = oriFileName + " " + " " + ContextSupportUtil.getCurrentUser().getFullname() +" " + " "
								+sdf.format(new Date())+" " + " "+size+"K";
					}else {
						oriFileName = oriFileName + " " + " " + ContextSupportUtil.getCurrentUser().getAliasName() +" " + " "
								+sdf.format(new Date())+" " + " "+size+"K";
					}

					jobject.put("fileId", fileId);
					jobject.put("fileName", oriFileName);
				}
				jobject.put("success", "true");
			}else{
				jobject.put("success", "false");
				jobject.put("message", "文件格式不符合要求!");
			}
		}
		return jobject;
	}

    public String downloadFile(String filePath) throws Exception{
        return uploaderHandler.getFileUrl(filePath, null, 60);
    }

	public SysFile downloadFile(SysFile sysFile, OutputStream outStream,boolean isCloseOutStream) throws Exception{
		InputStream in = uploaderHandler.getInputStream(sysFile.getFilePath(), null);
		byte[] buff = new byte[1024*2];
		int len = 0;
		while ((len = in.read(buff)) > 0){
            outStream.write(buff, 0, len);
        }
		return sysFile;
	}

	/**
	 * 获取内容类型。
	 *
	 * @param extName
	 * @return
	 */
	public String getContextType(String extName, boolean isRead) {
		String contentType = "application/octet-stream";
		if ("jpg".equalsIgnoreCase(extName) || "jpeg".equalsIgnoreCase(extName)) {
			contentType = "image/jpeg";
		} else if ("png".equalsIgnoreCase(extName)) {
			contentType = "image/png";
		} else if ("gif".equalsIgnoreCase(extName)) {
			contentType = "image/gif";
		} else if ("doc".equalsIgnoreCase(extName) || "docx".equalsIgnoreCase(extName)) {
			contentType = "application/msword";
		} else if ("xls".equalsIgnoreCase(extName) || "xlsx".equalsIgnoreCase(extName)) {
			contentType = "application/vnd.ms-excel";
		} else if ("ppt".equalsIgnoreCase(extName) || "pptx".equalsIgnoreCase(extName)) {
			contentType = "application/ms-powerpoint";
		} else if ("rtf".equalsIgnoreCase(extName)) {
			contentType = "application/rtf";
		} else if ("htm".equalsIgnoreCase(extName) || "html".equalsIgnoreCase(extName)) {
			contentType = "text/html";
		} else if ("swf".equalsIgnoreCase(extName)) {
			contentType = "application/x-shockwave-flash";
		} else if ("bmp".equalsIgnoreCase(extName)) {
			contentType = "image/bmp";
		} else if ("mp4".equalsIgnoreCase(extName)) {
			contentType = "video/mp4";
		} else if ("wmv".equalsIgnoreCase(extName)) {
			contentType = "video/x-ms-wmv";
		} else if ("wm".equalsIgnoreCase(extName)) {
			contentType = "video/x-ms-wm";
		} else if ("rv".equalsIgnoreCase(extName)) {
			contentType = "video/vnd.rn-realvideo";
		} else if ("mp3".equalsIgnoreCase(extName)) {
			contentType = "audio/mp3";
		} else if ("wma".equalsIgnoreCase(extName)) {
			contentType = "audio/x-ms-wma";
		} else if ("wav".equalsIgnoreCase(extName)) {
			contentType = "audio/wav";
		}
		if ("pdf".equalsIgnoreCase(extName) && isRead)// txt不下载文件，读取文件内容
		{
			contentType = "application/pdf";
		}
		if (("sql".equalsIgnoreCase(extName) || "txt".equalsIgnoreCase(extName)) && isRead)// pdf不下载文件，读取文件内容
		{
			contentType = "text/plain";
		}
		return contentType;
	}




}
