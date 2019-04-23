package com.suneee.platform.attachment.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.suneee.core.util.AppUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.attachment.AttachmentHandler;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.service.util.ServiceUtil;

public class FolderAttachmentHandler implements AttachmentHandler{
	//当前存放附件的路径
	private String attachPath = ServiceUtil.getBasePath().replace("/", File.separator);
	
	public FolderAttachmentHandler(){
		if (StringUtil.isEmpty(attachPath)) {
			attachPath = AppUtil.getRealPath("/attachFiles/temp");
		}
	}
	
	public String getType() {
		return "folder";
	}
    
	public void remove(SysFile sysFile) throws Exception {
		String filePath = sysFile.getFilePath();
		String fullPath = attachPath + File.separator + filePath;
		// 删除文件
		FileUtil.deleteFile(fullPath);
	}
	
	@Override
	public void upload(SysFile sysFile, InputStream inputStream) throws Exception {
		String filePath = sysFile.getFilePath();
		String fullPath = attachPath + File.separator + filePath;
		
		String folderPath = fullPath.substring(0, fullPath.lastIndexOf(System.getProperty("file.separator")));
		File dir = new File(folderPath);
		if(!dir.exists()){//判断文件夹是否存在硬盘中，不存在则创建
			dir.mkdirs();
		}
		FileUtil.writeFile(fullPath, inputStream);
	}

	public void download(SysFile sysFile, OutputStream outStream,boolean isCloseOutStream)
			throws Exception {
		String filePath = sysFile.getFilePath();
		String fullPath = StringUtil.trimSufffix(attachPath, File.separator) + File.separator 
												+ filePath.replace("/", File.separator);
		File file = new File(fullPath);
		if (file.exists()) {
			FileInputStream inputStream = null;
			try{
				inputStream = new FileInputStream(fullPath);
				byte[] b = new byte[1024];
				int i = 0;
				while ((i = inputStream.read(b)) > 0) {
					outStream.write(b, 0, i);
				}
				outStream.flush();
			}
			catch(Exception e){
				throw e;
			}
			finally{
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				if (outStream != null&&isCloseOutStream) {
					outStream.close();
					outStream = null;
				}
			}
		}
		else {
			throw new RuntimeException("该附件不存在");
		}
	}

	@Override
	public void download(SysFile sysFile, OutputStream outStream) throws Exception {
		download(sysFile,outStream,true);
	}
}
