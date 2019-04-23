package com.suneee.eas.oa.controller.fs;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.uploader.UploaderHandler;
import com.suneee.eas.common.utils.FileUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.fs.DocFile;
import com.suneee.eas.oa.service.fs.DocFileService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/me/newDocFile/")
public class DocFileDownLoadController{

	private static final Logger log = LogManager.getLogger(DocFileDownLoadController.class);

	@Resource
	private DocFileService docFileService;

	@Autowired
	private UploaderHandler uploaderHandler;

	@RequestMapping(value = "download")
	@ResponseBody
	public ResponseMessage download(HttpServletRequest request){
		try {
			Long id = RequestUtil.getLong(request, "id");
			DocFile docFile = docFileService.findById(id);
			String fileName = docFile.getName();
			String filePath = docFile.getPath();
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
			String url = uploaderHandler.getFileUrl(filePath, null, 60);
			Map<String,Object> params=new HashMap<>();
			params.put("fileName", fileName);
			params.put("url", url);
			docFileService.updateDownNumber(id);
			return ResponseMessage.success("获取文件链接成功。", params);
		} catch (Exception e) {
			log.error("获取文件链接失败:"+e.getMessage(), e);
			return ResponseMessage.fail("获取文件链接失败:"+e.getMessage(), e);
		}
	}
		/**
	 * 根据文件id取得文件柜数据。
	 *
	 * @param request
	 * @throws IOException
	 */
	@RequestMapping("getFileById")
	@ResponseBody
	public ResponseMessage getFileById(HttpServletRequest request){
		try {
			Long id = RequestUtil.getLong(request, "id");
			DocFile docFile = docFileService.findById(id);
			String fileName = docFile.getName();
			String filePath = docFile.getPath();
			filePath = URLDecoder.decode(filePath, "UTF-8");
			// 如果是Linux,所有反斜杠转成斜杠
			filePath = filePath.replace("\\", System.getProperty("file.separator"));
			String url = uploaderHandler.getFileUrl(filePath, null, 60);
			Map<String,Object> params=new HashMap<>();
			params.put("fileName",fileName);
			params.put("url",url);
			return ResponseMessage.success("获取文件链接成功。", params);
		} catch (Exception e) {
			log.error("获取文件链接失败:"+e.getMessage(), e);
			return ResponseMessage.fail("获取文件链接失败:"+e.getMessage(), e);
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

	@RequestMapping("/getDocFileUrl")
	@ResponseBody
	public String getDocFileUrl(HttpServletRequest request) throws Exception{
		Long id = RequestUtil.getLong(request, "id");
		DocFile docFile = docFileService.findById(id);
		String filePath = docFile.getPath();
		try {
			filePath = URLDecoder.decode(filePath, "UTF-8");
			// 如果是Linux,所有反斜杠转成斜杠
			filePath = filePath.replace("\\", System.getProperty("file.separator"));
			return FileUtil.getDownloadUrl(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
