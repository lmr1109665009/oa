package com.suneee.platform.controller.picture;

import com.suneee.core.util.FileUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.util.FileUtil;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 
 * <pre> 
 * 描述：表单编辑器中的上传图片按钮对应的图片处理
 * 构建组：bpmx33
 * 作者：lqf
 * 邮箱:13507732754@189.cn
 * 日期:2016-7-27-下午4:21:39
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Controller
@RequestMapping("/platform/picture/pictureHandler/")
public class PictureHandlerController
{
	//文件存储基本路径
	@Value("#{configProperties['common.file.basePath']}")
	private String basePath;
	//文件路径，绝对路径=basePath+contextPath
	@Value("#{configProperties['file.picture.context']}")
	private String contextPath;
	//文件访问URL地址
	@Value("#{configProperties['file.picture.url']}")
	private String staticUrl;

	private String getStaticUrl() {
		if (staticUrl==null){
			return "";
		}
		return staticUrl;
	}

	/**
	 * 
	 * 上传图片
	 * @param request
	 * @param response
	 * @throws IOException 
	 * void
	 */
	@RequestMapping("upload")
	@ResponseBody
	public ResultVo uploadPicture(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException{
		ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		Map<String, MultipartFile> files = request.getFileMap();
		if (files==null||files.isEmpty()){
			resultVo.setMessage("您没有上传任何文件！");
			return resultVo;
		}
		List<Map<String,Object>> fileList=new ArrayList<Map<String,Object>>();
		Iterator<MultipartFile> it = files.values().iterator();

		while (it.hasNext()) {
			MultipartFile f = it.next();
			String path = FileUtil.createDateFilePath(basePath,contextPath,f.getOriginalFilename());
			FileUtil.writeByte(basePath+path, f.getBytes());
			Map<String,Object> fileInfo=new HashMap<String, Object>();
			fileInfo.put("filename",f.getOriginalFilename());
			fileInfo.put("size",f.getSize());
			fileInfo.put("path",path);
			fileList.add(fileInfo);
		}
		resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
		resultVo.setMessage("上传成功！");
		resultVo.setData(fileList);
		return resultVo;
	}
	

}
