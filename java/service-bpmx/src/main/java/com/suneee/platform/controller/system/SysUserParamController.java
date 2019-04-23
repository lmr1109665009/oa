package com.suneee.platform.controller.system;

import com.suneee.core.util.ExceptionUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysParam;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.SysUserParam;
import com.suneee.platform.service.bpm.thread.MessageUtil;
import com.suneee.platform.service.system.DictionaryService;
import com.suneee.platform.service.system.SysParamService;
import com.suneee.platform.service.system.SysUserParamService;
import com.suneee.platform.service.system.SysUserService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 对象功能:人员参数属性 控制器类 开发公司:广州宏天软件有限公司 开发人员:csx 创建时间:2012-02-24 10:04:50
 */
@Controller
@RequestMapping("/platform/system/sysUserParam/")
public class SysUserParamController extends BaseController {
	@Resource
	private SysUserParamService sysUserParamService;
	@Resource
	private SysParamService sysParamService;
	@Resource
	private SysUserService sysUserService;
	@Resource
	private DictionaryService dictionaryService;
	protected Logger logger = LoggerFactory
			.getLogger(SysUserParamController.class);

	/**
	 * 取得人员参数属性分页列表
	 * 
	 * @param request
	 * @param response
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("editByUserId")
	@Action(description = "修改人员参数属性分页列表")
	public ModelAndView editByUserId(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String returnUrl = RequestUtil.getPrePage(request);
		long userId = RequestUtil.getLong(request, "userId");
		String paramTypeFilter = RequestUtil.getString(request, "paramType");
		boolean isFirst = false;
		List<String> categoryList = sysParamService.getDistinctCategory(1,null);
		
		SysUser user = sysUserService.getById(userId);
		List<SysParam> sysParamList = sysParamService.getStatusParam();
		List<SysUserParam> list = sysUserParamService.getByUserId(userId);
		
		List< SysUserParam> userParam=convertByList(sysParamList,list,paramTypeFilter);
		ModelAndView mv = this.getAutoView()
				.addObject("paramList", userParam)
				.addObject("user", user)
				.addObject("isFirst",isFirst)
				.addObject("categoryList",categoryList)
				.addObject("paramType",paramTypeFilter)
				.addObject("returnUrl", returnUrl);
		return mv;
	}
	
	private List< SysUserParam> convertByList(List<SysParam> sysParamList,List<SysUserParam> userParaList, String paramTypeFilter ){
		List< SysUserParam> list=new ArrayList<SysUserParam>();
		if(userParaList.size()==0){
			for(SysParam sysParam:sysParamList){
				// 属性过滤
				if(StringUtil.isEmpty(paramTypeFilter)
						|| (StringUtil.isNotEmpty(sysParam.getCategory())&&paramTypeFilter.contains(sysParam.getCategory()+","))
						||paramTypeFilter.contains("all,")){
					list.add(new SysUserParam(sysParam));
				}
			}
			return list;
		}
		else{
			Set<String> paramKey = new java.util.HashSet<String>();
			convertToList(userParaList,paramKey);
			
			for(SysParam sysParam:sysParamList){
				if((StringUtil.isNotEmpty(sysParam.getCategory())&&paramTypeFilter.contains(sysParam.getCategory()+","))
						||paramTypeFilter.contains("all,")){
					
					if(!paramKey.contains(sysParam.getParamKey()))
						userParaList.add(new SysUserParam(sysParam));
				}
			}
			return userParaList;
		}
	}
	
	private void convertToList(List<SysUserParam> userParaList, Set<String> paramKey){
		for(SysUserParam param :userParaList){
			SysParam sysParam=sysParamService.getById(param.getParamId());
			param.setSourceType(sysParam.getSourceType());
			param.setSourceKey(sysParam.getSourceKey());
			param.setDescription(sysParam.getDescription());
			paramKey.add(sysParam.getParamKey());
		}
	}
	 
	@RequestMapping("saveByUserId")
	@Action(description = "编辑人员参数属性")
	public void saveByUserId(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		try {
			long userId = RequestUtil.getLong(request, "userId");
			String jsonParamData=request.getParameter("jsonParamData");
			
			List<SysUserParam> valueList = coverBean(userId, jsonParamData);
			sysUserParamService.add(userId, valueList);
			ResultMessage message = new ResultMessage(ResultMessage.Success,
					"编辑人员参数属性成功");
			out.print(message.toString());
		} catch (Exception e) {
			e.printStackTrace();
			String str = MessageUtil.getMessage();
			if (StringUtil.isNotEmpty(str)) {
				ResultMessage resultMessage = new ResultMessage(
						ResultMessage.Fail, "编辑人员参数属性失败:" + str);
				response.getWriter().print(resultMessage);
			} else {
				String message = ExceptionUtil.getExceptionMessage(e);
				ResultMessage resultMessage = new ResultMessage(
						ResultMessage.Fail, message);
				response.getWriter().print(resultMessage);
			}
		}
	}

	private List<SysUserParam> coverBean(long uesrId, String jsonData) throws Exception {
		Map<String,String> userParamMap = jsonToMap(jsonData);
		List<SysParam> paramList =sysParamService.getAll();
		List<SysUserParam> list = new ArrayList<SysUserParam>();
		for(SysParam sysParam:paramList){
			if(userParamMap.containsKey((sysParam.getParamId()).toString())){
				long paramId=sysParam.getParamId();
				SysUserParam userParam = new SysUserParam();
				userParam.setValueId(UniqueIdUtil.genId());
				userParam.setParamId(sysParam.getParamId());
				userParam.setParamValue(userParamMap.get((sysParam.getParamId()).toString()));
				userParam.setUserId(uesrId);
				String dataType = sysParamService.getById(paramId)
						.getDataType();
				String sourceType=sysParamService.getById(paramId).getSourceType();
				if (sourceType.equals("input")) {
					if (SysParam.DATA_TYPE_MAP.get(dataType) != null
							&& SysParam.DATA_TYPE_MAP.get(dataType).equals("数字")) {
						if(userParamMap.get(sysParam.getParamId())!=null){
							userParam.setParamIntValue(Long.parseLong(userParamMap.get((sysParam.getParamId()).toString())));
						}
					} else if (SysParam.DATA_TYPE_MAP.get(dataType) != null
							&& SysParam.DATA_TYPE_MAP.get(dataType).equals("日期")) {
						if(userParamMap.get(sysParam.getParamId())!=null){
							userParam.setParamDateValue(SysParam.PARAM_DATE_FORMAT.parse(userParamMap.get((sysParam.getParamId()).toString())));
						}
					}
				}
				list.add(userParam);
			}
		}
		return list;
	}
	
	private Map<String, String> jsonToMap(String jsonData) {
		Map<String, String> map = null ;
	    JSONObject json = null;
		json = JSONObject.fromObject(jsonData);
		Iterator<?> iter = json.keySet().iterator();
		map = new HashMap<String, String>();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = json.getString(key);
			map.put(key, value);
		}
		return map;
	}

	@RequestMapping("dialog")
	public ModelAndView dialog(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Enumeration attrNames = request.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			logger.info(attrNames.nextElement().toString());
		}

		ModelAndView mv = this.getAutoView();

		List<SysParam> sysParamList = sysParamService.getUserParam();
		mv.addObject("sysParamList", sysParamList);
		return mv;
	}

	@RequestMapping("getByParamKey")
	public ModelAndView getByParamKey(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = this.getAutoView();
		int postFlag = RequestUtil.getInt(request, "postflag");
		String userParam = RequestUtil.getString(request, "userParam");
		List<SysUser> userList = sysUserService.getByUserParam(userParam);
		mv.addObject("userList", userList);
		mv.addObject("postFlag", postFlag);
		return mv;
	}
}
