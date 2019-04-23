package com.suneee.platform.controller.word;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.suneee.core.util.AppUtil;
import com.suneee.core.util.FileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.suneee.core.util.AppUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.platform.service.util.WordUtil;

import freemarker.template.TemplateException;

@Controller
@RequestMapping("/platform/word/wordHandler/")
public class WordHandlerController
{
	/**
	 * 
	 * 根据word路径获取表格的html
	 * @param request
	 * @param response
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TemplateException 
	 * String
	 */
	@RequestMapping("getHtmlByPath")
	public void getHtmlByPath(MultipartHttpServletRequest request,HttpServletResponse response) throws FileNotFoundException, IOException, TemplateException {
		String rtn="";
		Map<String, MultipartFile> files = request.getFileMap();
		Iterator<MultipartFile> it = files.values().iterator();
		String attachPath = AppUtil.getRealPath("/attachFiles/temp");
		while (it.hasNext()) {
			MultipartFile f = it.next();
			String path = attachPath + File.separator + f.getOriginalFilename();
			File file = new File(path);
			FileUtil.writeByte(path, f.getBytes());
			rtn = WordUtil.getHtmlByPath(path);
			FileUtil.deleteDir(file);
		}
		response.getWriter().print(rtn);
	}
	
}
