package com.suneee.oa.controller.office;

import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.uploader.UploaderHandler;
import com.suneee.oa.dto.OfficeFileDto;
import com.suneee.oa.service.office.SysOfficeService;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Resource
	private SysOfficeService sysOfficeService;

	@Resource
	private UploaderHandler uploaderHandler;

	/**
	 * 获取拥有web签名用户列表
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@ResponseBody
	@Action(description="查看office控件下载列表(api)")
	public ResultVo list(HttpServletRequest request) {
		ResultVo resultVo = null;
		try {
			List<OfficeFileDto> officeList = sysOfficeService.getAll();
			resultVo = new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"获取office控件列表成功");
			resultVo.setData(officeList);
		} catch (Exception e) {
			resultVo.setStatus(ResultVo.COMMON_STATUS_FAILED);
			resultVo.setMessage("获取office控件列表失败：" + e.getMessage());
		}
		return resultVo;
	}


	@RequestMapping("save")
	@ResponseBody
	@Action(description="添加/保存office文件")
	public ResultVo save(MultipartHttpServletRequest request) throws Exception
	{
		MultipartFile file=request.getFile("file");
		ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		Long id = RequestUtil.getLong(request,"id",0);
		String name = RequestUtil.getString(request,"name");
		try {
			sysOfficeService.saveOfficeFile(id, name, file);
			if(id == 0){
				resultVo.setMessage("添加office文件成功!");
			}else{
				resultVo.setMessage("更新office文件成功!");
			}
			resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
		} catch (Exception e){
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
	public ResultVo del(HttpServletRequest request, HttpServletResponse response) {
		ResultVo resultVo=new ResultVo(ResultVo.COMMON_STATUS_FAILED,"");
		Long id = RequestUtil.getLong(request,"id",0);
		if (id == 0){
			resultVo.setMessage("参数不正确！");
			return resultVo;
		}
		try {
			OfficeFileDto officeFileDto = sysOfficeService.getById(id);
			String path = officeFileDto.getPath();
			sysOfficeService.delById(id);
			uploaderHandler.delete(path);
			resultVo.setStatus(ResultVo.COMMON_STATUS_SUCCESS);
			resultVo.setMessage("删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			resultVo.setMessage("删除失败：" + e.getMessage());
		}
		return resultVo;
	}

	/**
	 * office下载文件
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("download")
	@Action(description = "office下载文件")
	@ResponseBody
	public ResponseMessage download(HttpServletRequest request) {
		try {
			Long id = RequestUtil.getLong(request,"id",0);
			OfficeFileDto officeFileDto = sysOfficeService.getById(id);
			String path = officeFileDto.getPath();
			String name = officeFileDto.getFilename();
			String url = uploaderHandler.getFileUrl(path, null, 60);
			Map<String, Object> param = new HashMap<>();
			param.put("fileName", name);
			param.put("url", url);
			return ResponseMessage.success("获取office下载文件链接成功。", param);
		} catch (Exception e) {
			return ResponseMessage.fail("获取附件链接失败!"+e.getMessage(), e);
		}
	}

}
