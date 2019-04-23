package com.suneee.ucp.me.controller;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysUser;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.me.model.SysNews;
import com.suneee.ucp.me.service.SysNewsServcie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/me/sysNews/")
public class SysNewsFormController  extends UcpBaseController{
	
	@Resource
	private SysNewsServcie sysNewsServcie;

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
	
	
	@RequestMapping("save")
	@Action(description = "添加或更新新闻动态明细")
	public void save(MultipartHttpServletRequest request, HttpServletResponse response,
					 SysNews sysNews) throws Exception {
		String resultMsg = null;
		MultipartFile file=request.getFile("file");
		try {
			processUploadFile(sysNews,file);
			if (sysNews.getId() == null) {
				SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
				sysNews.setCreatorId(sysUser.getUserId());
				sysNews.setCreator(sysUser.getFullname());
				sysNews.setId(UniqueIdUtil.genId());
				sysNewsServcie.add(sysNews);
				resultMsg = getText("添加成功", "公告表");
			} else {
				sysNewsServcie.update(sysNews);
				resultMsg = getText("更新成功", "公告表");
			}
			
			writeResultMessage(response.getWriter(), resultMsg,
					ResultMessage.Success);
		} catch (Exception e) {
			e.printStackTrace();
			writeResultMessage(response.getWriter(),
					resultMsg + "," + e.getMessage(), ResultMessage.Fail);
		}
	}

	/**
	 * 处理上传文件
	 * @param news
	 * @param multipartFile
	 */
	private void processUploadFile(SysNews news,MultipartFile multipartFile) throws IOException {
		if (multipartFile==null||multipartFile.isEmpty()){
			return;
		}
		String filepath= FileUtil.createDateFilePath(basePath,contextPath,multipartFile.getOriginalFilename());
		multipartFile.transferTo(new File(basePath+filepath));
		if (StringUtil.isNotEmpty(news.getImgUrl())){
			File delFile=new File(basePath+news.getImgUrl());
			if (delFile.exists()){
				delFile.delete();
			}
		}
		news.setImgUrl(filepath);
	}
	/**
	 * 在实体对象进行封装前，从对应源获取原实体
	 * @param acceptId
	 * @param model
	 * @return
	 * @throws Exception
	 */
    @ModelAttribute
    protected SysNews getFormObject(@RequestParam("id") Long id,Model model) throws Exception {
		logger.debug("enter SysBulletin getFormObject here....");
		SysNews sysNews=null;
		if(id!=null){
			sysNews=sysNewsServcie.getById(id);
		}else{
			sysNews= new SysNews();
		}
		return sysNews;
    }
}
