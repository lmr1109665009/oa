package com.suneee.oa.controller.document;

import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.attachment.AttachmentHandler;
import com.suneee.platform.attachment.AttachmentHandlerFactory;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.oa.model.docFile.DocFile;
import com.suneee.oa.service.docFile.DocFileService;
import com.suneee.ucp.base.controller.UcpBaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
@Controller
@RequestMapping("/me/newDocFile/")
public class DocFileDownLoadController extends UcpBaseController{

	@Resource
	private DocFileService docFileService;

	@Resource
	private SysOrgService orgService;
	@Resource
	private UserPositionService uerPositionService;
	
	private AttachmentHandler currentmentHander;

	// 初始化当前处理器
	private void initCurrentmentHander() throws Exception {
		if (BeanUtils.isEmpty(currentmentHander)) {
			AttachmentHandlerFactory attachmentHandlerFactory = AppUtil.getBean(AttachmentHandlerFactory.class);
			currentmentHander = attachmentHandlerFactory.getCurrentHandler();
		}
	}
	@RequestMapping(value = "download")
	public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {

		OutputStream outStream = response.getOutputStream();
		try {

			response.reset();
			String vers = request.getHeader("USER-AGENT");
			String openIt = request.getParameter("openIt");
			Long id = RequestUtil.getLong(request, "id");
			DocFile docFile = docFileService.getById(id);
			String fileName = docFile.getName();
			String filePath = docFile.getPath();
			filePath = URLDecoder.decode(filePath, "UTF-8");
			// 如果是Linux,所有反斜杠转成斜杠
			filePath = filePath.replace("\\", System.getProperty("file.separator"));

			if (StringUtils.isBlank(fileName)) {
				fileName = filePath.substring(filePath.lastIndexOf(System.getProperty("file.separator")) + 1);
			} else {
				// 判断名称是否有文件类型，没有就加上
				if (fileName.split("\\.").length < 2) {
					String[] ds = filePath.split("\\.");
					fileName = fileName + "." + ds[ds.length - 1];
				}
			}

			String contextType = fileName.substring(fileName.lastIndexOf(".") + 1);
			response.setContentType(contextType);
			response.setCharacterEncoding("utf-8");
			/*
			 * if (vers.indexOf("Chrome") != -1 && vers.indexOf("Mobile") != -1)
			 * { fileName = fileName.toString(); } else {
			 */
			fileName = StringUtil.encodingString(fileName, "GB2312", "ISO-8859-1");
			// }
			if (!"application/octet-stream".equals(contextType) && StringUtils.isNotBlank(openIt)) {
				fileName = URLEncoder.encode(fileName, "utf-8");
				response.addHeader("Content-Disposition", "filename=" + fileName);
			} else {
				response.setContentType("application/force-download");// 设置强制下载不打开
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			}
			response.addHeader("Content-Transfer-Encoding", "binary");
			initCurrentmentHander();
			SysFile sysFile = new SysFile();
			sysFile.setFilePath(filePath);
			currentmentHander.download(sysFile, outStream);
			// response.setContentLength(download(filePath, outStream));
			docFileService.updateDownNumber(id);
		} catch (Exception e) {
			outStream.write("获取文件失败".getBytes("utf-8"));
		} finally {
			if (outStream != null) {
				outStream.close();
				outStream = null;
			}
			response.flushBuffer();
		}

	}

	/**
	 * 根据文件id取得文件柜数据。
	 *
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("getFileById")
	public void getFileById(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long id = RequestUtil.getLong(request, "id");
        response.setContentType("application/octet-stream");
		DocFile docFile = docFileService.getById(id);
		String fileName = docFile.getName();
		response.setHeader("Content-Disposition","filename=" + fileName);
        response.setHeader("Content-Length",docFile.getSize());
		String filePath = docFile.getPath();
		filePath = URLDecoder.decode(filePath, "UTF-8");
		// 如果是Linux,所有反斜杠转成斜杠
		filePath = filePath.replace("\\", System.getProperty("file.separator"));
		OutputStream outStream = response.getOutputStream();
		try{
			initCurrentmentHander();
			SysFile sysFile = new SysFile();
			sysFile.setFilePath(filePath);
			currentmentHander.download(sysFile, outStream);
		} catch(Exception e){
			outStream.write("获取文件失败".getBytes("utf-8"));
		}
		finally{
			if (outStream != null) {
				outStream.close();
				outStream = null;
			}
			response.flushBuffer();
		}
	}

	public int download(String filePath, OutputStream outStream) throws Exception {
		String fullPath = filePath;
		File file = new File(fullPath);
		if (file.exists()) {
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(fullPath);
				byte[] b = new byte[1024];
				int i = 0;
				while ((i = inputStream.read(b)) > 0) {
					outStream.write(b, 0, i);
				}
				outStream.flush();
			} catch (Exception e) {
				throw e;
			} finally {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				if (outStream != null) {
					outStream.close();
					outStream = null;
				}
			}
			return (int) file.length();
		} else {
			throw new RuntimeException("该附件不存在");
		}
	}
}
