package com.suneee.oa.controller.office;

import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.oa.dto.OfficeFileDto;
import com.suneee.oa.model.docFile.DocFile;
import com.suneee.oa.service.docFile.DocFileService;
import com.suneee.oa.service.office.SysOfficeService;
import com.suneee.oa.utils.OfficeFileUtils;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.model.system.SysFile;
import com.suneee.platform.service.system.SysFileService;
import com.suneee.ucp.base.util.StringUtils;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *<pre>
 * 对象功能:office控件 控制器类
 * 开发公司:深圳象翌
 * 开发人员:子华
 * 创建时间:2018-02-05 14:09:00
 *</pre>
 */
@Controller
@Action(ownermodel= SysAuditModelType.PROCESS_AUXILIARY)
public class SysOfficeController extends BaseController {
	public static final String CONFIG_FILE="config/office-config.xml";
	public static final String PREFIX_PATH="/platform/system/office/";

	@Resource
	private DocFileService docFileService;
	@Resource
	private SysFileService sysFileService;
	//文件存储基本路径
	@Value("#{configProperties['common.file.basePath']}")
	private String basePath;
	//office数据保存路径，绝对路径=basePath+contextPath
	@Value("#{configProperties['file.office.context']}")
	private String contextPath;
	//文件访问URL地址
	@Value("#{configProperties['file.office.url']}")
	private String staticUrl;

	@Resource
	private SysOfficeService sysOfficeService;

	private String getStaticUrl() {
		if (staticUrl==null){
			return "";
		}
		return staticUrl;
	}

	/**
	 * 获取拥有web签名用户列表
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(PREFIX_PATH+"list")
	@Action(description="查看office控件下载列表")
	public ModelAndView list(HttpServletRequest request) throws Exception {
		List<OfficeFileDto> officeList=loadOffices();
		ModelAndView mv=this.getAutoView().addObject("officeList",officeList).addObject("staticUrl",getStaticUrl());
		return mv;
	}

	/**
	 * 加载office下载内容
	 * @return
	 */
	private List<OfficeFileDto> loadOffices(){
		return OfficeFileUtils.loadOfficeFile(basePath+"/"+CONFIG_FILE);
	}

	/**
	 * 编辑office下载文件
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(PREFIX_PATH+"edit")
	@Action(description="编辑office下载文件")
	public ModelAndView edit(HttpServletRequest request) throws Exception
	{
		Integer id= RequestUtil.getInt(request,"id",-1);
		ModelAndView mv =getAutoView();
		List<OfficeFileDto> officeList=loadOffices();
		for (OfficeFileDto office:officeList){
			if (office.getId().equals(id)){
				mv.addObject("officeObj",office);
				break;
			}
		}
		return mv;
	}

	/**
	 * 添加/保存office下载文件
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(PREFIX_PATH+"save")
	@Action(description="添加/保存office下载文件")
	public ModelAndView save(MultipartHttpServletRequest request) throws Exception
	{
		MultipartFile file=request.getFile("file");
		ModelAndView mv=this.getAutoView();
		mv.addObject("ctx",request.getServletContext().getContextPath());
		Integer id= RequestUtil.getInt(request,"id",-1);
		if ((file==null||file.getSize()==0)&&id==-1){
			mv.addObject("msg","请上传office文件！");
			return mv;
		}
		List<OfficeFileDto> officeList=loadOffices();
		OfficeFileDto office=null;
		boolean isAdd=true;
		if (id>0){
			for (OfficeFileDto item:officeList){
				if (item.getId().equals(id)){
					isAdd=false;
					office=item;
					break;
				}
			}
			if (isAdd==true){
				mv.addObject("msg","office文件不存在！");
				return mv;
			}
		}
		if (isAdd){
			office=new OfficeFileDto();
		}
		String name=RequestUtil.getString(request,"name");
		if (StringUtil.isNotEmpty(name)){
			office.setName(name);
		}

		if (file!=null&&file.getSize()>0){
			String originName=file.getOriginalFilename();
			originName= StringUtils.specialCharFilter(originName);
			int pos=originName.lastIndexOf(".");
			String filename=null;
			if (pos>=0){
				filename=originName.substring(0,pos);
				office.setExt(originName.substring(pos));
			}else {
				filename=originName;
				office.setExt("");
			}
			if (StringUtil.isEmpty(name)){
				office.setName(filename);
			}

			office.setFilename(filename);
			String filepath=contextPath+"/"+originName;
			File destFile=new File(basePath+filepath);
			if(!destFile.getParentFile().exists()){
				destFile.getParentFile().mkdirs();
			}
			if (!isAdd&&destFile.exists()&&!filepath.equals(office.getPath())){
				mv.addObject("msg","你上传的文件已存在，请不要重复上传!");
				return mv;
			}else if (isAdd){
				for (OfficeFileDto item:officeList){
					if (filepath.equals(item.getPath())){
						mv.addObject("msg","你上传的文件已存在，请不要重复上传!");
						return mv;
					}
				}
			}
			file.transferTo(destFile);
			for (OfficeFileDto of:officeList){
				if(of.getName().equals(office.getName())){
					mv.addObject("msg","显示名字已存在，请修改！");
					return mv;
				}
			}
			if (!isAdd&&office.getPath()!=null){
				File delFile=new File(basePath+office.getPath());
				if (delFile.exists()){
					delFile.delete();
				}
			}
			office.setPath(filepath);
		}
		if (isAdd){
			officeList.add(office);
		}
		try {
			OfficeFileUtils.persistenceData(officeList,basePath+"/"+CONFIG_FILE);
			if (isAdd){
				mv.addObject("msg","添加office文件成功!");
			}else {
				mv.addObject("msg","更新office文件成功!");
			}
		} catch (IOException e){
			mv.addObject("msg","系统出错，无法更新配置文件!");
		}
		return mv;
	}

	/**
	 * 删除office下载文件
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(PREFIX_PATH+"del")
	@ResponseBody
	@Action(description = "删除office下载文件")
	public ResultVo del(HttpServletRequest request) throws Exception {
		ResultVo message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"删除成功！");
		int id=RequestUtil.getInt(request,"id",-1);
		if (id==-1){
			message.setStatus(ResultVo.COMMON_STATUS_FAILED);
			message.setMessage("参数不正确！");
			return message;
		}
		List<OfficeFileDto> officeList = sysOfficeService.getAll();
		OfficeFileDto office=null;
		for (OfficeFileDto officeItem:officeList){
			if (officeItem.getId()==id){
				office=officeItem;
				break;
			}
		}
		if (office!=null){
			officeList.remove(office);
			File targetFile=new File(basePath+office.getPath());
			if (targetFile.exists()){
				targetFile.delete();
			}
		}
		OfficeFileUtils.persistenceData(officeList,basePath+"/"+CONFIG_FILE);
		return message;
	}

	/**
	 * 排序
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(PREFIX_PATH+"sort")
	@ResponseBody
	public ResultVo sort(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String flag=RequestUtil.getString(request,"flag","up");
		ResultVo message = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取排序数据成功");
		int id=RequestUtil.getInt(request,"id",-1);
		if (id==-1){
			message.setStatus(ResultVo.COMMON_STATUS_FAILED);
			message.setMessage("参数不正确！");
			return message;
		}
		List<OfficeFileDto> officeList=loadOffices();
		OfficeFileDto office=null;
		for (OfficeFileDto officeItem:officeList){
			if (officeItem.getId()==id){
				office=officeItem;
				break;
			}
		}
		if (office!=null){
			int pos=officeList.indexOf(office);
			int pos2=pos;
			if ("up".equals(flag)){
				if (pos>0){
					pos2--;
				}
			}else {
				if (pos<officeList.size()-1){
					pos2++;
				}
			}
			OfficeFileDto office2=officeList.get(pos2);
			officeList.set(pos2,office);
			officeList.set(pos,office2);
		}
		OfficeFileUtils.persistenceData(officeList,basePath+"/"+CONFIG_FILE);
		return message;
	}

	/**
	 * 获取office控件模板
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/platform/form/OfficeTemplate")
	public ModelAndView template(HttpServletRequest request) throws Exception {
		List<OfficeFileDto> officeList = sysOfficeService.getAll();
		ModelAndView mv=new ModelAndView("platform/form/OfficeTemplate.jsp");
		mv.addObject("officeList",officeList).addObject("staticUrl",getStaticUrl());
		return mv;
	}

	/**
	 * 获取office附件预览
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/platform/system/sysFile/office")
	public ModelAndView office(HttpServletRequest request) throws Exception {
		List<OfficeFileDto> officeList = sysOfficeService.getAll();
		ModelAndView mv=new ModelAndView("platform/system/sysFileOffice");
		Long id=RequestUtil.getLong(request,"fileId");
		SysFile file=sysFileService.getById(id);
//		String path = request.getServletPath();
//		String url = request.getRequestURL().toString();
//		String tempContextUrl = url.replace(path, "");
		mv.addObject("officeList",officeList).addObject("staticUrl",getStaticUrl()).addObject("ext",file.getExt());
		return mv;
	}

	/**
	 * 获取文件柜在线文件预览
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/platform/system/sysFile/viewFile")
	public ModelAndView viewFile(HttpServletRequest request,@RequestParam Long id) throws Exception {
		List<OfficeFileDto> officeList = sysOfficeService.getAll();
		DocFile docFile = docFileService.getById(id);
		String fileName = docFile.getName();
		String ext="";
		if (StringUtil.isNotEmpty(fileName)){
			ext=fileName.substring(fileName.lastIndexOf(".")+1);
		}
		ModelAndView mv=new ModelAndView("platform/system/sysFileViewFile.jsp");
		mv.addObject("officeList",officeList).addObject("staticUrl",getStaticUrl()).addObject("ext",ext);
		return mv;
	}

}
