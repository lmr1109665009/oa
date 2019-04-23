package com.suneee.oa.controller.office;

import com.suneee.core.util.StringUtil;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.oa.dto.OfficeFileDto;
import com.suneee.oa.utils.OfficeFileUtils;
import com.suneee.ucp.base.util.StringUtils;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
@Action(ownermodel=SysAuditModelType.PROCESS_AUXILIARY)
@RequestMapping("/api/system/office/")
public class SysOfficeApiController extends BaseController {
	//文件存储基本路径
	@Value("#{configProperties['common.file.basePath']}")
	private String basePath;
	//office数据保存路径，绝对路径=basePath+contextPath
	@Value("#{configProperties['file.office.context']}")
	private String contextPath;
	//文件访问URL地址
	@Value("#{configProperties['file.office.url']}")
	private String staticUrl;

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
	@RequestMapping("list")
	@ResponseBody
	@Action(description="查看office控件下载列表(api)")
	public ResultVo list(HttpServletRequest request) throws Exception {
		List<OfficeFileDto> officeList=loadOffices();
		String staticUrl=getStaticUrl();
		for (OfficeFileDto office:officeList){
			office.setPath(staticUrl+office.getPath());
		}
		ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取office控件列表成功");
		resultVo.setData(officeList);
		return resultVo;
	}

	/**
	 * 加载office下载内容
	 * @return
	 */
	private List<OfficeFileDto> loadOffices(){
		return OfficeFileUtils.loadOfficeFile(basePath+"/"+SysOfficeController.CONFIG_FILE);
	}

	/**
	 * 编辑office下载文件
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@ResponseBody
	@Action(description="编辑office下载文件")
	public ResultVo edit(HttpServletRequest request) throws Exception
	{
		ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取office控件编辑内容成功");
		Integer id= RequestUtil.getInt(request,"id",-1);
		List<OfficeFileDto> officeList=loadOffices();
		for (OfficeFileDto office:officeList){
			if (office.getId().equals(id)){
				office.setPath(getStaticUrl()+office.getPath());
				resultVo.setData(office);
				return resultVo;
			}
		}
		resultVo.setStatus(ResultVo.COMMON_STATUS_FAILED);
		resultVo.setMessage("找不到Office控件文件！");
		return resultVo;
	}

	/**
	 * 添加/保存office下载文件
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@ResponseBody
	@Action(description="添加/保存office文件")
	public ResultVo save(MultipartHttpServletRequest request) throws Exception
	{
		MultipartFile file=request.getFile("file");
		ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		Integer id=RequestUtil.getInt(request,"id",-1);
		String name=RequestUtil.getString(request,"name");
		if ((file==null||file.getSize()==0)&&id==-1){
			resultVo.setMessage("请上传office文件！");
			return resultVo;
		}
		List<OfficeFileDto> officeList=loadOffices();
		OfficeFileDto office=null;
		boolean isAdd=true;
		if (id>0){
			for (OfficeFileDto item:officeList){//比较id，找到对应更新的文件
				if (item.getId().equals(id)){
					isAdd=false;
					office=item;
					break;
				}
			}
			for (OfficeFileDto item:officeList){//比较文件名
				if(!item.getId().equals(id) && item.getName().equals(name)){//文件id不相等，比较文件名是否相等
					//如果id不相等，但文件名等于传入的名称，即改名字被其他文件所使用
					resultVo.setMessage("显示名已经存在！");
					return resultVo;
				}
			}
			if (isAdd==true){
				resultVo.setMessage("office文件不存在！");
				return resultVo;
			}
		}
		if (isAdd){
			for (OfficeFileDto item:officeList){//如果是新增，比较文件名
				if(item.getName().equals(name)){//比较文件名是否相等，如果文件名已被使用，return
					resultVo.setMessage("显示名已经存在！");
					return resultVo;
				}
			}
			office=new OfficeFileDto();
		}
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
				resultVo.setMessage("你上传的文件已存在，请不要重复上传!");
				return resultVo;
			}else if (isAdd){
				for (OfficeFileDto item:officeList){
					if (filepath.equals(item.getPath())){
						resultVo.setMessage("你上传的文件已存在，请不要重复上传!");
						return resultVo;
					}
				}
			}
			file.transferTo(destFile);
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
			OfficeFileUtils.persistenceData(officeList,basePath+"/"+SysOfficeController.CONFIG_FILE);
			if (isAdd){
				resultVo.setMessage("添加office文件成功!");
			}else {
				resultVo.setMessage("更新office文件成功!");
			}
			resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
		} catch (IOException e){
			resultVo.setMessage("系统出错，无法更新配置文件!");
		}
		return resultVo;
	}

	/**
	 * 删除office下载文件
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@ResponseBody
	@Action(description = "删除office下载文件")
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		int id=RequestUtil.getInt(request,"id",-1);
		if (id==-1){
			resultVo.setMessage("参数不正确！");
			return resultVo;
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
			officeList.remove(office);
			File targetFile=new File(basePath+office.getPath());
			if (targetFile.exists()){
				targetFile.delete();
			}
		}
		OfficeFileUtils.persistenceData(officeList,basePath+"/"+SysOfficeController.CONFIG_FILE);
		resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
		resultVo.setMessage("删除成功！");
		return resultVo;
	}

	/**
	 * 排序
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("sort")
	@ResponseBody
	public ResultVo sort(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		String flag=RequestUtil.getString(request,"flag","up");
		int id=RequestUtil.getInt(request,"id",-1);
		if (id==-1){
			resultVo.setMessage("参数不正确！");
			return resultVo;
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
		OfficeFileUtils.persistenceData(officeList,basePath+"/"+SysOfficeController.CONFIG_FILE);
		resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
		resultVo.setMessage("排序成功！");
		return resultVo;
	}


}
