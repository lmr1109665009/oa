package com.suneee.platform.attachment.impl;

import java.io.InputStream;
import java.io.OutputStream;

import com.suneee.core.util.FileUtil;
import com.suneee.platform.attachment.AttachmentHandler;
import com.suneee.platform.model.system.SysFile;
import com.suneee.core.util.FileUtil;
import com.suneee.platform.model.system.SysFile;

public class DatabaseAttachmentHandler implements AttachmentHandler{

	public String getType() {
		return "database";
	}

	public void remove(SysFile sysFile) throws Exception {
		// 数据库存放附件模式，直接删除数据库中的记录即可
	}

	public void upload(SysFile sysFile, InputStream inputStream) throws Exception {
		// 上传附件时，将附件字节流设置到SysFile的FileBlob属性中
		sysFile.setFileBlob(FileUtil.readByte(inputStream));
	}

	public void download(SysFile sysFile, OutputStream outStream,boolean isCloseOutStream) throws Exception {
		//获取附件字节数组
		byte[] fileBlob = sysFile.getFileBlob();
		try{
			outStream.write(fileBlob);
		}
		catch(Exception e){
			throw e;
		}
		finally{
			if (outStream != null&&isCloseOutStream) {
				outStream.close();
				outStream = null;
			}
		}
	}

	@Override
	public void download(SysFile sysFile, OutputStream outStream) throws Exception {
		download(sysFile,outStream,true);
	}
}
