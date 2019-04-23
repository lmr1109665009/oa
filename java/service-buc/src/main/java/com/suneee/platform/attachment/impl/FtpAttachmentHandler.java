package com.suneee.platform.attachment.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.suneee.core.util.AppUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.service.util.ServiceUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.suneee.core.util.AppUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.attachment.AttachmentHandler;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.weixin.encode.StringUtils;

public class FtpAttachmentHandler implements AttachmentHandler{
	private String url;					/*服务器地址*/
	private int port = 21;				/*端口*/
	private String username;			/*账号*/
	private String password;			/*密码*/
	private String separator = "/";		/*FTP服务器的分隔符*/
	
	private FTPClient ftp = new FTPClient();
	/** 本地字符编码 */
	private String LOCAL_CHARSET = "GBK";
	// FTP协议里面，规定文件名编码为iso-8859-1
	private String SERVER_CHARSET = "ISO-8859-1";
	
	private String attachPath = ServiceUtil.getBasePath("document.folder.root").replace("/", File.separator);
	
	public FtpAttachmentHandler(){
		if (StringUtil.isEmpty(attachPath)) {
			attachPath = AppUtil.getRealPath("/attachFiles/temp");
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return "ftp";
	}
	
	//连接FTP服务器
	private void connect(){
		try {
			int reply;
			ftp.connect(url, port);
			FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
			conf.setServerLanguageCode("zh");
			boolean loginResult = ftp.login(username, password);
			if(loginResult){
				// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
				if (FTPReply.isPositiveCompletion(ftp.sendCommand("OPTS UTF8", "ON"))) { 
                    LOCAL_CHARSET = "UTF-8";
                }
				ftp.setControlEncoding(LOCAL_CHARSET);
				ftp.enterLocalPassiveMode(); // 设置被动模式
			}
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void upload(SysFile sysFile, InputStream inputStream) throws Exception {
		String path = sysFile.getFilePath();
		String fileName1 = sysFile.getFileName();
		//sysFile.getTypeId()==6 流程相关附件
		if(fileName1==null||fileName1==""||sysFile.getTypeId()==6){
			fileName1 =org.apache.commons.lang3.StringUtils.isBlank(sysFile.getExt())?sysFile.getFilePath():(sysFile.getFileId()+"."+sysFile.getExt());
		}
		int idx = fileName1.lastIndexOf("/");
		// 文件后缀
		String fileName = fileName1.substring(idx+1);
		path = replaceFileSeparator(attachPath+File.separator+ path);
		// 编码转换，避免上传到服务器上时出现中文乱码
		path = new String(path.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
		fileName = new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
		validConnection();
		// 转到指定上传目录,没有该目录则创建
		changeDirectory(path);
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		boolean result = ftp.storeFile(fileName, inputStream);
		if (!result) {
			throw new RuntimeException("上传文件失败");
		}
		inputStream.close();
		ftp.logout();
		ftp.disconnect();
	}

	@Override
	public void download(SysFile sysFile, OutputStream outStream,boolean isCloseOutStream) throws Exception{
		try {
			validConnection();
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			String path = sysFile.getFilePath();
			path = replaceFileSeparator(attachPath+File.separator+ path);
			String fileName="";
			if(sysFile.getFileName()!=null&&sysFile.getTypeId()==6){
			 fileName =sysFile.getFileName();
			}else{
			String fileName1 =org.apache.commons.lang3.StringUtils.isBlank(sysFile.getExt())?sysFile.getFilePath():(sysFile.getFileId()+"."+sysFile.getExt());
			int idx = fileName1.lastIndexOf("/");
			// 文件后缀
			 fileName = fileName1.substring(idx+1);
			}
			path = new String(path.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
			boolean changeResult = ftp.changeWorkingDirectory(path);// 转移到FTP服务器目录
			if(!changeResult){
				throw new RuntimeException("要下载的文件路径不存在");
			}
			ftp.enterLocalPassiveMode();
			String[] fs = null;
			try {
				fs=ftp.listNames();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			boolean tag = false;
			if(sysFile.getTypeId() != null && sysFile.getTypeId() ==6){
				fileName =org.apache.commons.lang3.StringUtils.isBlank(sysFile.getExt())?sysFile.getFilePath():(sysFile.getFileId()+"."+sysFile.getExt());
			}
			for (String ff : fs) {
				
				if (ff.equals(fileName)) {
					tag = true;
					fileName = new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
					ftp.retrieveFile(fileName, outStream);
					if (isCloseOutStream){
						outStream.close();
					}
					break;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("要下载的文件不存在");
		}finally {
			if (isCloseOutStream){
				outStream.close();
			}
			ftp.logout();
			ftp.disconnect();
		}
	}

	@Override
	public void download(SysFile sysFile, OutputStream outStream) throws Exception {
		download(sysFile,outStream,true);
	}

	@Override
	public void remove(SysFile sysFile) throws Exception {
		validConnection();
		String path = sysFile.getFilePath();
		//ftp.changeWorkingDirectory(attachPath);
		String attachPath = ServiceUtil.getBasePath("document.folder.root");
		path = attachPath + "/" + path;	
		path=path.replace(File.separator, "/");		  
        ftp.deleteFile( path);  
	}
	//验证连接
	private void validConnection(){
		try{
			if(!ftp.isConnected()||!ftp.isRemoteVerificationEnabled()||!ftp.sendNoOp()){
				connect();
			}
			// 重置当前目录到根目录
			ftp.changeWorkingDirectory(separator);
		}
		catch(Exception e){
			connect();
		}
	}
	
	//转移FTP文件目录位置(不存在的目录会自动创建)	
	private void changeDirectory(String path) throws Exception{
		boolean changeResult = ftp.changeWorkingDirectory(path);
		//转移失败，则该目录不存在
		if(!changeResult){
			String errorMsg = "上传文件时，创建文件路径失败";
			//创建目录
			int mkd = ftp.mkd(path);
			if(mkd!=257){
				throw new RuntimeException(errorMsg);
			}
			//转移到该目录
			changeResult = ftp.changeWorkingDirectory(path);
			if(!changeResult){
				throw new RuntimeException(errorMsg);
			}
		}
	}
	
	private String replaceFileSeparator(String path){
		// 替换路径中的分隔符
		String ftpFormatPath = regReplace(path, separator);
		// 替换文件名，只保留路径
		ftpFormatPath = ftpFormatPath.replaceAll("/\\w+\\.?(\\w+)?(\\s+)?$", "");
		return ftpFormatPath;
	}
	
	private String regReplace(String str, String replaceChar){
		StringBuffer resultString = new StringBuffer();
        try {
            Pattern regex = Pattern.compile("[\\\\|/]");
            Matcher regexMatcher = regex.matcher(str);
            while (regexMatcher.find()) {
                regexMatcher.appendReplacement(resultString, replaceChar);
            }
            regexMatcher.appendTail(resultString);
        } catch (PatternSyntaxException ex) {
            ex.printStackTrace();
        }
        return resultString.toString();
	}
}
