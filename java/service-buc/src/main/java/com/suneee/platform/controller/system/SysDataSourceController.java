package com.suneee.platform.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.db.RollbackJdbcTemplate;
import com.suneee.core.db.datasource.DataSourceUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.suneee.platform.annotion.Action;
import com.suneee.core.db.IRollBack;
import com.suneee.core.db.RollbackJdbcTemplate;
import com.suneee.core.db.datasource.DataSourceUtil;
import com.suneee.core.db.datasource.DbContextHolder;
import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.system.PersonScript;
import com.suneee.platform.model.system.SysDataSource;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.service.system.SysDataSourceService;
import com.suneee.platform.xml.util.MsgUtil;

/**
 * <pre>
 * 对象功能:SYS_DATA_SOURCE 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:Aschs
 * 创建时间:2014-08-21 10:50:18
 * </pre>
 */
@Controller
@RequestMapping("/platform/system/sysDataSource/")
public class SysDataSourceController extends BaseController {
	@Resource
	private SysDataSourceService sysDataSourceService;
	@Resource
	private ProcessRunService processRunService;
	@Resource
    RollbackJdbcTemplate rollbackJdbcTemplate;
	

	/**
	 * 添加或更新SYS_DATA_SOURCE。
	 * 
	 * @param request
	 * @param response
	 * @param sysDataSource
	 *            添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@Action(description = "添加或更新SYS_DATA_SOURCE")
	public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String json = FileUtil.inputStream2String(request.getInputStream());
		SysDataSource sysDataSource = JSONObjectUtil.toBean(json, SysDataSource.class);

		try {
			if (sysDataSource.getId() == null) {
				sysDataSource.setId(UniqueIdUtil.genId());
				sysDataSourceService.add(sysDataSource);
				writeResultMessage(response.getWriter(), "添加成功", ResultMessage.Success);

			} else {
				sysDataSourceService.update(sysDataSource);
				writeResultMessage(response.getWriter(), "更新成功", ResultMessage.Success);
			}
			
			// 加入系统数据源
			if (sysDataSource.getEnabled()) {
				try {
					DataSourceUtil.addDataSource(sysDataSource.getAlias(), sysDataSourceService.getDsFromSysSource(sysDataSource));
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			writeResultMessage(response.getWriter(), e.getMessage(), ResultMessage.Fail);
		}
		
	}

	/**
	 * 取得SYS_DATA_SOURCE分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("list")
	@Action(description = "查看SYS_DATA_SOURCE分页列表")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<SysDataSource> list = sysDataSourceService.getAll(new QueryFilter(request, "sysDataSourceItem"));
		ModelAndView mv = this.getAutoView().addObject("sysDataSourceList", list);

		return mv;
	}

	/**
	 * 删除SYS_DATA_SOURCE
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("del")
	@Action(description = "删除SYS_DATA_SOURCE")
	public void del(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String preUrl = RequestUtil.getPrePage(request);
		ResultMessage message = null;
		try {
			Long[] lAryId = RequestUtil.getLongAryByStr(request, "id");
			if(lAryId.length>1){
				message = new ResultMessage(ResultMessage.Success, "删除SYS_DATA_SOURCE成功!");
			}else{
				message = new ResultMessage(ResultMessage.Success, "删除"+sysDataSourceService.getById(lAryId[0]).getName()+"成功!");
			}
			sysDataSourceService.delByIds(lAryId);
		} catch (Exception ex) {
			message = new ResultMessage(ResultMessage.Fail, "删除失败" + ex.getMessage());
		}
		addMessage(message, request);
		response.sendRedirect(preUrl);
	}

	/**
	 * 编辑SYS_DATA_SOURCE
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("edit")
	@Action(description = "编辑SYS_DATA_SOURCE")
	public ModelAndView edit(HttpServletRequest request) throws Exception {
		String returnUrl = RequestUtil.getPrePage(request);
		return getAutoView().addObject("returnUrl", returnUrl);
	}

	/**
	 * 取得SYS_DATA_SOURCE明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("get")
	@Action(description = "查看SYS_DATA_SOURCE明细")
	public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		SysDataSource sysDataSource = sysDataSourceService.getById(id);
		Long runId = 0L;
		ProcessRun processRun = processRunService.getByBusinessKey(id.toString());
		if (BeanUtils.isNotEmpty(processRun)) {
			runId = processRun.getRunId();
		}
		return getAutoView().addObject("sysDataSource", sysDataSource).addObject("runId", runId);
	}

	@RequestMapping("getById")
	@Action(description = "查看SYS_DATA_SOURCE明细")
	@ResponseBody
	public SysDataSource getById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = RequestUtil.getLong(request, "id");
		SysDataSource sysDataSource = sysDataSourceService.getById(id);
		return sysDataSource;
	}

	/**
	 * 改变当前的数据源
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 *             void
	 * @exception
	 * @since 1.0.0
	 */
	@RequestMapping("checkConnection")
	public void checkConnection(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String json = FileUtil.inputStream2String(request.getInputStream());
		SysDataSource sysDataSource = JSONObjectUtil.toBean(json, SysDataSource.class);
		boolean b = false;
		try {
			b = sysDataSourceService.checkConnection(sysDataSource);
		} catch (Exception e) {
			b=false;
		}

		String resultMsg = "";

		if (b) {
			resultMsg = sysDataSource.getName() + ":连接成功";
			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Success);
		} else {
			resultMsg = sysDataSource.getName() + ":连接失败";
			writeResultMessage(response.getWriter(), resultMsg, ResultMessage.Fail);
		}

	}
	
	/**
	 * 获取在容器的数据源，包含本地数据源
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 *             List<SysDataSource>
	 * @exception
	 * @since 1.0.0
	 */
	@RequestMapping("getAll")
	@ResponseBody
	public List<SysDataSource> getAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<SysDataSource> dsList = sysDataSourceService.getAllAndDefault();
		return dsList;
	}
	
	
	/**
	 * 导出系统数据源xml。
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("exportXml")
	@Action(description = "导出系统数据源", detail = "导出系统数据源:" + "<#list StringUtils.split(tableIds,\",\") as item>" + "<#assign entity=bpmFormTableService.getById(Long.valueOf(item))/>" + "【${entity.tableDesc}(${entity.tableName})】" + "</#list>")
	public void exportXml(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String strXml = "";
		String fileName = "";
		Long[] tableIds = RequestUtil.getLongAryByStr(request, "tableIds");
		
		
		if (BeanUtils.isEmpty(tableIds)) {
			List<SysDataSource> list = sysDataSourceService.getAll();
			if (BeanUtils.isNotEmpty(list)) {
				strXml = sysDataSourceService.exportXml(list);
				fileName = "全部数据源_"+ DateFormatUtil.getNowByString("yyyyMMddHHmm")+ ".xml";
			}

		} else {

			strXml = sysDataSourceService.exportXml(tableIds);
			fileName = DateFormatUtil.getNowByString("yyyyMMddHHmm")+ ".xml";
			if (tableIds.length == 1) {
				SysDataSource sysDataSource = sysDataSourceService.getById(tableIds[0]);
				fileName = sysDataSource.getName() + "_" + fileName;
			} else {
				fileName = "数据源_" + fileName;
			}
		}
		FileUtil.downLoad(request, response, strXml, fileName);
		

	}
	
	/**
	 * 导入系统数据源的XML
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("importXml")
	@Action(description = "导入系统数据源")
	public void importXml(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
		MultipartFile fileLoad = request.getFile("xmlFile");
		ResultMessage message = null;
		try {
			sysDataSourceService.importXml(fileLoad.getInputStream());
			message = new ResultMessage(ResultMessage.Success, MsgUtil.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message = new ResultMessage(ResultMessage.Fail, "导入文件异常，请检查文件格式！");
		}
		writeResultMessage(response.getWriter(), message);
	}
	
	
}
