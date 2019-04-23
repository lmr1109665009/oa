package com.suneee.platform.service.form;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.*;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.oa.component.BpmImportHolder;
import com.suneee.oa.component.GlobalTypeHolder;
import com.suneee.oa.model.bpm.GlobalTypeAmount;
import com.suneee.platform.dao.bpm.BpmNodeSetDao;
import com.suneee.platform.dao.bpm.FormDefTreeDao;
import com.suneee.platform.dao.form.BpmDataTemplateDao;
import com.suneee.platform.dao.form.BpmFormDefDao;
import com.suneee.platform.dao.form.BpmFormRightsDao;
import com.suneee.platform.dao.form.BpmFormTableDao;
import com.suneee.platform.dao.system.*;
import com.suneee.platform.model.bpm.*;
import com.suneee.platform.model.form.*;
import com.suneee.platform.model.system.*;
import com.suneee.platform.service.bpm.*;
import com.suneee.platform.service.form.parser.BpmFormDefHtmlParser;
import com.suneee.platform.service.form.parser.util.ParserParam;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.SysHistoryDataService;
import com.suneee.platform.xml.form.BpmFormDefXml;
import com.suneee.platform.xml.form.BpmFormDefXmlList;
import com.suneee.platform.xml.table.BpmFormTableXml;
import com.suneee.platform.xml.util.MsgUtil;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.apache.cxf.common.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对象功能:BPM_FORM_DEF Service类 开发公司:广州宏天软件有限公司 开发人员:xwy 创建时间:2011-12-22 11:07:56
 */
@Service
public class BpmFormDefService extends BaseService<BpmFormDef> {
	@Resource
	private BpmFormDefDao dao;
	@Resource
	private BpmFormRightsService bpmFormRightsService;

	@Resource
	private BpmFormHandlerService bpmFormHandlerService;

	@Resource
	private BpmFormRunService bpmFormRunService;

	@Resource
	private BpmFormTableService bpmFormTableService;

	@Resource
	private BpmFormRightsDao bpmFormRightsDao;

	@Resource
	private BpmNodeSetService bpmNodeSetService;

	@Resource
	private BpmDefinitionService bpmDefinitionService;

	@Resource
	private BpmNodeSetDao bpmNodeSetDao;

	@Resource
	private GlobalTypeDao globalTypeDao;

	@Resource
	private SysUserDao sysUserDao;
	@Resource
	private SysRoleDao sysRoleDao;
	@Resource
	private SysOrgDao sysOrgDao;
	@Resource
	private PositionDao positionDao;

	@Resource
	private BpmDataTemplateDao bpmDataTemplateDao;
	@Resource
	private SysBusEventDao sysBusEventDao;
	@Resource
	private FormDefTreeDao formDefTreeDao;
	@Resource
	private BpmFormTableDao bpmFormTableDao;
	@Resource
	private ProcessRunService processRunService;
	@Resource
	private BpmFormFieldService bpmFormFieldService;

	@Resource
	private SysHistoryDataService historyDataService;

	@Resource
	private TaskOpinionService taskOpinionService;
	
	public BpmFormDefService() {
	}

	@Override
	protected IEntityDao<BpmFormDef, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 获得已发布版本数量
	 * 
	 * @param formKey
	 *            在表单版本中使用
	 * @return
	 */
	public Integer getCountByFormKey(String formKey) {
		return dao.getCountByFormKey(formKey);
	}

	/**
	 * 获得默认版本
	 * 
	 * @param formKey
	 *            在表单版本中使用
	 * @return
	 */
	public BpmFormDef getDefaultVersionByFormKey(String formKey) {
		return dao.getDefaultVersionByFormKey(formKey);
	}

	/**
	 * 处理权限的人员
	 * 
	 * @param permission
	 * @param flag
	 *            标记是导出（true）还是导入(false)
	 * @return
	 */
	public String parsePermission(String permission, boolean flag) {
		JSONObject json = JSONObject.fromObject(permission);
		Object read = json.get("read");
		Object write = json.get("write");
		if (read == null)
			read = "{}";
		if (write == null)
			write = "{}";
		json.element("read", parseUseInfo(read.toString(), flag));
		json.element("write", parseUseInfo(write.toString(), flag));
		return json.toString();
	}

	/**
	 * 处理权限的人员
	 * 
	 * @param mode
	 *            处理json
	 * @param flag
	 *            标记是导出（true）还是导入(false)
	 * @return
	 */
	private String parseUseInfo(String mode, boolean flag) {
		JSONObject node = JSONObject.fromObject(mode);
		if (JSONUtils.isNull(node))
			return "";
		if (JSONUtils.isNull(node.get("type")))
			return mode;
		if (JSONUtils.isNull(node.get("id")))
			return mode;
		String type = node.get("type").toString();
		String id = (String) node.get("id");
		String tempFullname = (String) node.get("fullname");
		if (BpmFormRights.TYPE_NONE.equals(type) || BpmFormRights.TYPE_EVERYONE.equals(type))
			return mode;
		if (flag) {// 导出只对人员处理
			if (BpmFormRights.TYPE_USER.equals(type)) {
				SysUser sysUser = (SysUser) sysUserDao.getById(Long.parseLong(id));
				if (BeanUtils.isNotEmpty(sysUser))
					node.element("fullname", sysUser.getAccount());
			}
		}
		// 导入
		else {
			if (StringUtil.isEmpty(tempFullname))
				return cleanPermission(node);
			// 因传入的fullname可能为“用户1,用户2”，因此将传入的fullname进行分割再处理
			String[] fullnameArr = tempFullname.split(",");
			int count = 0;// 用于统计找不到记录的次数
			StringBuffer ids = new StringBuffer();// 存放过滤后的用户ID
			StringBuffer names = new StringBuffer();// 存放过滤后的用户工号
			for (String fullname : fullnameArr) {
				// 用户
				if (BpmFormRights.TYPE_USER.equals(type)) {
					SysUser sysUser = (SysUser) sysUserDao.getByAccount(fullname);
					if (BeanUtils.isEmpty(sysUser)) {
						MsgUtil.addMsg(MsgUtil.ERROR, "表单权限的中的用户工号：" + fullname + ",不存在!请检查!");
						count++;
					} else {
						ids.append(sysUser.getUserId()).append(",");
//						names.append(sysUser.getFullname()).append(",");
						names.append(ContextSupportUtil.getUsername(sysUser)).append(",");

					}
				}
				// 角色
				else if (BpmFormRights.TYPE_ROLE.equals(type)) {
					List<SysRole> sysRoleList = sysRoleDao.getByRoleName(fullname);
					if (BeanUtils.isEmpty(sysRoleList)) {
						MsgUtil.addMsg(MsgUtil.ERROR, "表单权限的中的角色名称：" + fullname + ",不存在!请检查!");
						count++;
					} else if (sysRoleList.size() > 1) {
						MsgUtil.addMsg(MsgUtil.ERROR, "表单权限的中的角色名称：" + fullname + ",多于一条记录!请检查!");
						count++;
					} else {
						SysRole sysRole = (SysRole) sysRoleList.get(0);
						ids.append(sysRole.getRoleId()).append(",");
						names.append(sysRole.getRoleName()).append(",");
					}
				}
				// 组织或组织负责人
				else if (BpmFormRights.TYPE_ORG.equals(type) || BpmFormRights.TYPE_ORGMGR.equals(type)) {
					List<SysOrg> sysOrgList = sysOrgDao.getByOrgName(fullname);
					if (BeanUtils.isEmpty(sysOrgList)) {
						MsgUtil.addMsg(MsgUtil.ERROR, "表单权限的中的组织名称：" + fullname + ",不存在!请检查!");
						count++;
					} else if (sysOrgList.size() > 1) {
						MsgUtil.addMsg(MsgUtil.ERROR, "表单权限的中的组织名称：" + fullname + ",多于一条记录!请检查!");
						count++;
					} else {
						SysOrg sysOrg = (SysOrg) sysOrgList.get(0);
						ids.append(sysOrg.getOrgId()).append(",");
						names.append(sysOrg.getOrgName()).append(",");
					}
				}
				// 岗位
				else if (BpmFormRights.TYPE_POS.equals(type)) {
					List<Position> positionList = positionDao.getByPosName(fullname);
					if (BeanUtils.isEmpty(positionList)) {
						MsgUtil.addMsg(MsgUtil.ERROR, "表单权限的中的岗位名称：" + fullname + ",不存在!请检查!");
						count++;
					} else if (positionList.size() > 1) {
						MsgUtil.addMsg(MsgUtil.ERROR, "表单权限的中的岗位名称：" + fullname + ",多于一条记录!请检查!");
						count++;
					} else {
						Position position = positionList.get(0);
						ids.append(position.getPosId()).append(",");
						names.append(position.getPosName()).append(",");
					}
				}
			}
			// 若数组中所有的名称在数据库中都无对应记录
			if (count == fullnameArr.length) {
				return cleanPermission(node);
			} else {
				node.element("id", ids.deleteCharAt(ids.length() - 1).toString());
				node.element("fullname", names.deleteCharAt(names.length() - 1).toString());
			}
		}
		return node.toString();

	}

	/**
	 * 清理权限
	 * 
	 * @param node
	 *            权限
	 * @return
	 */
	private String cleanPermission(JSONObject permission) {
		permission.element("id", "");
		permission.element("fullname", "");
		return permission.toString();
	}

	/**
	 * 根据formkey查询所有的表单定义版本。
	 * 
	 * @param formKey
	 *            在表单版本中使用
	 * @return
	 */
	public List<BpmFormDef> getByFormKey(String formKey) {
		return dao.getByFormKey(formKey);
	}

	/**
	 * 增加表单定义。
	 * 
	 * @param bpmFormDef
	 *            自定义表单对象
	 * @throws Exception
	 */
	public void addForm(BpmFormDef bpmFormDef) throws Exception {
		long id = UniqueIdUtil.genId();
		bpmFormDef.setFormDefId(id);
		bpmFormDef.setVersionNo(1);
		bpmFormDef.setIsDefault((short) 1);
		bpmFormDef.setIsPublished((short) 0);
		dao.add(bpmFormDef);

		historyDataService.add(SysHistoryData.SYS_FORMDEF_TEMPLATE, bpmFormDef.getSubject(), bpmFormDef.getHtml(), bpmFormDef.getFormDefId());
	}

	/**
	 * 更新表单及权限。
	 * 
	 * @param bpmFormDef
	 *            自定义表单对象
	 * @throws Exception
	 */
	public void updateForm(BpmFormDef bpmFormDef) throws Exception {

		// 更新table
		dao.update(bpmFormDef);

		historyDataService.add(SysHistoryData.SYS_FORMDEF_TEMPLATE, bpmFormDef.getSubject(), bpmFormDef.getHtml(), bpmFormDef.getFormDefId());
	}

	/**
	 * 发布
	 * 
	 * @param formDefId
	 *            自定义表单Id
	 * @param operator
	 *            发布人
	 * @throws Exception
	 */
	public void publish(Long formDefId, String operator) throws Exception {
		// 设为已发布
		BpmFormDef formDef = dao.getById(formDefId);

		formDef.setIsPublished((short) 1);
		formDef.setPublishedBy(operator);
		formDef.setPublishTime(new Date());
		dao.update(formDef);

	}

	/**
	 * 设为默认版本。
	 * 
	 * @param formDefId
	 *            自定义表单Id
	 * @param formKey
	 *            在表单版本使用
	 */
	public void setDefaultVersion(Long formDefId, String formKey) {
		dao.setDefaultVersion(formKey, formDefId);
	}

	/**
	 * 根据表单定义id创建新的表单版本。 表单定义ID
	 * 
	 * @param formDefId
	 *            自定义表单Id
	 * @throws Exception
	 */
	public void newVersion(Long formDefId) throws Exception {
		BpmFormDef formDef = dao.getById(formDefId);
		Integer rtn = dao.getMaxVersionByFormKey(formDef.getFormKey());
		Long newFormDefId = UniqueIdUtil.genId();
		// 创建新的版本
		BpmFormDef newVersion = (BpmFormDef) formDef.clone();
		newVersion.setFormDefId(newFormDefId);
		newVersion.setIsDefault((short) 0);
		newVersion.setIsPublished((short) 0);
		newVersion.setPublishedBy("");

		newVersion.setVersionNo(rtn + 1);
		dao.add(newVersion);
		// 拷贝表单权限

	}

	/**
	 * 添加复制的表单，包括表单权限信息
	 * 
	 * @param bpmFormDef
	 * @param oldFormkey
	 */
	public void copyForm(BpmFormDef bpmFormDef, String oldFormkey) {
		dao.add(bpmFormDef);
		String formKey = bpmFormDef.getFormKey();
		if (bpmFormDef.getDesignType() == 0) {
			List<BpmFormRights> list = bpmFormRightsDao.getByFormKey(oldFormkey, true);
			for (BpmFormRights bpmFormRights : list) {
				Long newId = UniqueIdUtil.genId();
				bpmFormRights.setId(newId);
				bpmFormRights.setFormKey(formKey);
				bpmFormRightsDao.add(bpmFormRights);
			}
		}
	}

	/**
	 * 根据BpmFormRun取得表单。 表单分为： 1.在线表单。 2.url表单。
	 * 
	 * <pre>
	 * 1.首先去bpmformrun中获取表单数据。
	 * 2.没有获取到则获取当前节点的表单设置。
	 * 3.获取全局表单设置。
	 * </pre>
	 * 
	 * @param processRun
	 * @param nodeId
	 * @param userId
	 * @param ctxPath
	 * @param variables
	 * @return
	 * @throws Exception
	 */
	public FormModel getNodeForm(ProcessRun processRun, String nodeId, Long userId, String ctxPath, Map<String, Object> variables, boolean isReadonly) throws Exception {
		String instanceId = processRun.getActInstId();
		String actDefId = processRun.getActDefId();
		String businessKey = processRun.getBusinessKey();

		BpmFormRun bpmFormRun = bpmFormRunService.getByInstanceAndNode(instanceId, nodeId);

		String parentActDefId = "";
		if (variables.containsKey(BpmConst.FLOW_PARENT_ACTDEFID)) {// 判断当前是否属于子流程任务
			parentActDefId = (String) variables.get(BpmConst.FLOW_PARENT_ACTDEFID);
		}
		BpmNodeSet bpmNodeSet = bpmNodeSetService.getFormByActDefIdNodeId(actDefId, nodeId, parentActDefId, false);

		FormModel formModel = new FormModel();
		// 运行时存在。
		if (bpmFormRun != null) {
			Long formDefId = bpmFormRun.getFormdefId();
			BpmFormDef bpmFormDef = dao.getById(formDefId);
			String formKey = bpmNodeSet.getFormKey();
			// 判断当前是否 更换了表单
			if (bpmFormDef != null && bpmFormDef.getFormKey().equals(formKey)) {
				Long tableId = bpmFormDef.getTableId();
				if (tableId > 0) {
					BpmFormTable bpmFormTable = bpmFormDef.getBpmFormTable();
					if (BeanUtils.isNotEmpty(bpmFormTable)) {
						String tablename = bpmFormTable.getTableName();
						// 判断数据是否有效。,
						isDataValid(formModel, businessKey, tablename);
						//String formHtml = bpmFormHandlerService.obtainHtml(bpmFormDef, businessKey, instanceId, actDefId, nodeId, ctxPath, parentActDefId, false, false, isReadonly);

						//--->测试新解释器
						String formHtml = parseHtml(bpmFormDef, businessKey, instanceId, actDefId, nodeId, parentActDefId, false, true, isReadonly,false,(short) 0);
						//<---测试新解释器

						formModel.setFormHtml(formHtml);
						return formModel;
					}
				}

			}
		}

		if (bpmNodeSet == null)
			return formModel;
		// 获取在线表单
		if (BpmConst.OnLineForm.equals(bpmNodeSet.getFormType())) {
			BpmFormDef bpmFormDef = dao.getDefaultPublishedByFormKey(bpmNodeSet.getFormKey());
			String bussinessKey = processRun.getBusinessKey();
			String tablename = bpmFormDef.getBpmFormTable().getTableName();

			// 验证表单数据是否有效。
			isDataValid(formModel, bussinessKey, tablename);
			//String formHtml = bpmFormHandlerService.obtainHtml(bpmFormDef, businessKey, processRun.getActInstId(), processRun.getActDefId(), "", ctxPath, parentActDefId, false, false, isReadonly);
			
			//--->测试新解释器
			String formHtml = parseHtml(bpmFormDef, bussinessKey, instanceId, actDefId, nodeId, parentActDefId, false, false, isReadonly,false,(short) 0);
			//<---测试新解释器
			
			formModel.setFormHtml(formHtml);
		} else {
			// 获取流程实例ID
			String bussinessKey = processRun.getBusinessKey();
			String formUrl = bpmNodeSet.getFormUrl();
			String detailUrl = bpmNodeSet.getDetailUrl();
			if (StringUtil.isNotEmpty(formUrl)) {

				formUrl = getFormUrl(formUrl, bussinessKey, variables, ctxPath);
				formModel.setFormUrl(formUrl);
				formModel.setFormType(BpmConst.UrlForm);
			}
			if (StringUtil.isNotEmpty(detailUrl)) {
				detailUrl = getFormUrl(detailUrl, bussinessKey, variables, ctxPath);
				formModel.setDetailUrl(detailUrl);
			}
		}
		return formModel;
	}

	/**
	 * 获取表单URL。
	 * 
	 * @param formUrl
	 * @param bussinessKey
	 * @param variables
	 * @param ctxPath
	 * @return
	 */
	private String getFormUrl(String formUrl, String bussinessKey, Map<String, Object> variables, String ctxPath) {
		String url = formUrl;
		if (StringUtil.isNotEmpty(bussinessKey)) {
			url = formUrl.replaceFirst(BpmConst.FORM_PK_REGEX, bussinessKey);
		}

		if (variables != null)
			url = getUrlByVariables(url, variables);
		if (!formUrl.startsWith("http")) {
			url = ctxPath + url;
		}
		return url;
	}

	/**
	 * 根据任务id 获取API调用的跳转URL 。
	 * 
	 * @param taskId
	 * @param defId
	 * @param nodeId
	 * @param businessKey
	 * @param ctxPath
	 * @return
	 */
	public String getFormUrl(String taskId, String actDefId, String nodeId, String businessKey, String ctxPath) {
		String formUrl = "";

		BpmNodeSet nodeSet = bpmNodeSetService.getByActDefIdNodeId(actDefId, nodeId, "");
		if (nodeSet != null) {
			formUrl = nodeSet.getFormUrl();
		}
		if (StringUtil.isEmpty(formUrl)) {
			BpmNodeSet node = bpmNodeSetService.getBySetType(actDefId, BpmNodeSet.SetType_GloabalForm);
			formUrl = node.getFormUrl();
		}
		if (StringUtils.isEmpty(formUrl))
			return formUrl;
		formUrl = formUrl.replaceFirst(BpmConst.FORM_PK_REGEX, businessKey).replaceFirst("\\{taskId\\}", taskId);
		if (!formUrl.startsWith("http")) {
			formUrl = ctxPath + formUrl;
		}
		return formUrl;

	}

	/**
	 * 替换地址 orderNo={orderNo}
	 * 
	 * @param url
	 * @param variables
	 * @return
	 */
	private String getUrlByVariables(String url, Map<String, Object> variables) {
		Pattern regex = Pattern.compile("\\{(.*?)\\}");
		Matcher regexMatcher = regex.matcher(url);
		while (regexMatcher.find()) {
			String toreplace = regexMatcher.group(0);
			String varName = regexMatcher.group(1);
			if (!variables.containsKey(varName))
				continue;
			url = url.replace(toreplace, variables.get(varName).toString());
		}
		return url;
	}

	/**
	 * 取得发布的表单。
	 * 
	 * @param queryFilter
	 * @return
	 */
	public List<BpmFormDef> getPublished(QueryFilter queryFilter) {
		return dao.getPublished(queryFilter);
	}

	public List<BpmFormDef> getAllPublished(String formDesc) {
		return dao.getAllPublished(formDesc);
	}

	/**
	 * 根据表单key获取默认的表单定义。
	 * 
	 * @param formKey
	 * @return
	 */
	public BpmFormDef getDefaultPublishedByFormKey(String formKey) {
		return dao.getDefaultPublishedByFormKey(formKey);
	}

	/**
	 * 判断表单是否已经被使用。
	 * 
	 * @param formKey
	 *            表单key
	 * @return
	 */
	public int getFlowUsed(String formKey) {
		int rtn = dao.getFlowUsed(formKey);
		return rtn;
	}

	/**
	 * 根据formkey删除数据。
	 * 
	 * <pre>
	 * 	如果表已经生成并且表单是通过设计器进行设计的那么将删除所创建的表。
	 * </pre>
	 * 
	 * @param formKey
	 * @throws SQLException
	 */
	public void delByFormKey(String formKey) throws SQLException {
		BpmFormDef bpmFormDef = dao.getDefaultVersionByFormKey(formKey);
		Long tableId = bpmFormDef.getTableId();
		// 删除表单权限
		bpmFormRightsService.delByFormKey(formKey, true);
		// 先删除表单，后判断是否还有表单使用该表
		dao.delByFormKey(formKey);
		// 删除数据模版。
		bpmDataTemplateDao.delByFormKey(formKey);
		// tableId大于零并且有表单生成。
		if (tableId > 0 && bpmFormDef.getDesignType() == 1) {
			BpmFormTable bpmFormTable = bpmFormTableService.getTableById(tableId);
			// 是否还有表单使用该表
			boolean tableHasForm = dao.isTableHasFormDef(tableId);
			if (bpmFormTable != null && !tableHasForm) {
				bpmFormTableService.dropTable(bpmFormTable);
				bpmFormTableService.delTable(bpmFormTable);
			}
		}

		// 删除树结构设置
		formDefTreeDao.delByFormDefKey(bpmFormDef.getFormKey());
	}

	/**
	 * 保存表单。
	 * 
	 * <pre>
	 * 	1.表单输入新创建的表单。
	 * 		1.保存表单。
	 * 		
	 *  2.表单未发布。
	 *  	1.保存表单。
	 *  	
	 *  3.表单已经发布的情况，表单已经发布，数据库表已经创建。
	 *  	1.保存表单。
	 *  	2.表单是否有其他的表单定义情况。
	 *  		1.相同的表不止对应一个表单的情况，对表做更新处理。
	 *  		2.没有数据的情况，表删除重建。
	 * </pre>
	 * 
	 * @param bpmFormdef
	 * @param bpmFormTable
	 * @throws Exception
	 */
	public void saveForm(BpmFormDef bpmFormdef, BpmFormTable bpmFormTable, boolean isPublish) throws Exception {
		if (bpmFormdef.getFormDefId() == 0) {
			Long formDefId = UniqueIdUtil.genId();
			bpmFormdef.setFormDefId(formDefId);
			bpmFormdef.setDesignType(BpmFormDef.DesignType_CustomDesign);
			bpmFormdef.setIsDefault((short) 1);
			bpmFormdef.setVersionNo(1);
			Long tableId = 0L;
			if (isPublish) {
				tableId = bpmFormTableService.saveTable(bpmFormTable);
				bpmFormdef.setIsPublished((short) 1);
				bpmFormdef.setPublishTime(new Date());
			} else {
				bpmFormdef.setIsPublished((short) 0);
				bpmFormdef.setPublishedBy("");
			}
			bpmFormdef.setTableId(tableId);
			dao.add(bpmFormdef);
		} else {
			// 当前为发布或者表单已经分布。
			if (isPublish || bpmFormdef.getIsPublished() == 1) {
				Long tableId = bpmFormdef.getTableId();
				bpmFormTable.setTableId(tableId);
				tableId = bpmFormTableService.saveTable(bpmFormTable);
				bpmFormdef.setTableId(tableId);
				bpmFormdef.setIsPublished((short) 1);
				bpmFormdef.setPublishTime(new Date());
			}
			dao.update(bpmFormdef);
		}
		historyDataService.add(SysHistoryData.SYS_FORMDEF_TEMPLATE, bpmFormdef.getSubject(), bpmFormdef.getHtml(), bpmFormdef.getFormDefId());

	}

	/**
	 * 获取现有表单Id函数
	 * 
	 * @param nodeSet
	 * @return
	 */
	public Long getCurrentTableId(BpmNodeSet nodeSet) {
		Long formId = 0L;
		BpmFormDef bpmFormDef;
		// 节点挂钩表单不为空时取节点表单
		if (nodeSet.getFormType().equals(Short.parseShort("0"))) {
			bpmFormDef = dao.getDefaultVersionByFormKey(nodeSet.getFormKey());
			if (bpmFormDef != null) {
				formId = bpmFormDef.getFormDefId();
			}
		} else { // 节点表单为空时取全局表单
			BpmNodeSet globalForm = bpmNodeSetDao.getBySetType(nodeSet.getActDefId(), BpmNodeSet.SetType_GloabalForm);
			if (globalForm != null) {
				bpmFormDef = dao.getDefaultVersionByFormKey(globalForm.getFormKey());
				if (bpmFormDef != null) {
					formId = bpmFormDef.getFormDefId();
				}
			}
		}
		return formId;
	}

	/**
	 * 
	 * 导出表单XML
	 * 
	 * <pre>
	 * 1.导出流程定义
	 * 2.导出流程定义权限
	 * 3.导出数据模板
	 * </pre>
	 * 
	 * @param formDefIds
	 * @param map
	 *            是否导出的Map列表
	 * @return
	 * @throws Exception
	 */
	public String exportXml(Long[] formDefIds, Map<String, Boolean> map) throws Exception {
		BpmFormDefXmlList bpmFormDefXmls = new BpmFormDefXmlList();
		List<BpmFormDefXml> list = new ArrayList<BpmFormDefXml>();
		for (int i = 0; i < formDefIds.length; i++) {
			BpmFormDef bpmFormDef = dao.getById(formDefIds[i]);
			BpmFormDefXml bpmFormDefXml = exportBpmFormDef(bpmFormDef, BpmFormDef.IS_DEFAULT, map);
			list.add(bpmFormDefXml);
		}
		bpmFormDefXmls.setBpmFormDefXmlList(list);
		return XmlBeanUtil.marshall(bpmFormDefXmls, BpmFormDefXmlList.class);
	}

	public Map<String, Boolean> getDefaultExportMap(Map<String, Boolean> map) {
		if (BeanUtils.isEmpty(map)) {
			map = new HashMap<String, Boolean>();
			map.put("bpmFormDef", true);
			map.put("bpmFormTable", false);
			map.put("bpmFormDefOther", true);
			map.put("bpmFormRights", true);
			map.put("bpmTableTemplate", true);
			map.put("sysBusEvent", true);
			map.put("formDefTree", true);
		}
		return map;
	}

	/**
	 * 导出表单的信息
	 * 
	 * @param bpmFormDef
	 *            表单
	 * @param isDefault
	 *            是否是默认 默认则要导出其它表单和模板
	 * @param map
	 *            是否导出的Map列表
	 * @return
	 */
	public BpmFormDefXml exportBpmFormDef(BpmFormDef bpmFormDef, Short isDefault, Map<String, Boolean> map) {
		BpmFormDefXml bpmFormDefXml = new BpmFormDefXml();
		// 表单
		bpmFormDefXml.setBpmFormDef(bpmFormDef);
		Long formDefId = bpmFormDef.getFormDefId();
		String formKey = bpmFormDef.getFormKey();

		if (isDefault.shortValue() == BpmFormDef.IS_DEFAULT.shortValue()) {
			// 导出对应的表
			if (map.get("bpmFormTable")) {
				exportBpmFormTableXml(bpmFormDef, bpmFormDefXml);
			}

			if (BeanUtils.isNotEmpty(formKey)) {
				// 导出自定义表单 非默认版本
				if (map.get("bpmFormDefOther")) {
					exportBpmFormDefOther(formKey, map, bpmFormDefXml);
				}
				// 数据模板
				if (map.get("bpmTableTemplate")) {
					exportBpmDataTemplate(formKey, bpmFormDefXml);
				}
			}
		}

		// 表单权限
		if (map.get("bpmFormRights")) {
			exportBpmFormRights(formKey, bpmFormDefXml);
		}

		//业务数据
		if (map.get("sysBusEvent")) {
			exportSysBusEvent(formKey, bpmFormDefXml);
		}

		//表单树形配置
		if (map.get("formDefTree")) {
			exportFormDefTree(formKey, bpmFormDefXml);
		}

		return bpmFormDefXml;
	}

	/**
	 * 导出对于的表
	 * 
	 * @param bpmFormDef
	 * @param bpmFormDefXml
	 */
	private void exportBpmFormTableXml(BpmFormDef bpmFormDef, BpmFormDefXml bpmFormDefXml) {
		if (BeanUtils.isEmpty(bpmFormDef.getTableId()))
			return;
		if (bpmFormDef.getTableId() == 0 && bpmFormDef.getDesignType() == BpmFormDef.DesignType_CustomDesign)
			return;

		BpmFormTable formTable = bpmFormTableService.getById(bpmFormDef.getTableId());
		if (formTable!=null){
			BpmFormTableXml bpmFormTableXml = bpmFormTableService.exportTable(formTable, null);
			bpmFormDefXml.setBpmFormTableXml(bpmFormTableXml);
		}
	}

	/**
	 * 导出其它版本的自定义表单
	 * 
	 * @param formKey
	 * @param map
	 * @param bpmFormDefXml
	 */
	private void exportBpmFormDefOther(String formKey, Map<String, Boolean> map, BpmFormDefXml bpmFormDefXml) {
		List<BpmFormDef> formDefList = dao.getByFormKeyIsDefault(formKey, BpmFormDef.IS_NOT_DEFAULT);
		if (BeanUtils.isEmpty(formDefList))
			return;

		List<BpmFormDefXml> list = new ArrayList<BpmFormDefXml>();
		for (BpmFormDef formDef : formDefList) {
			BpmFormDefXml formDefXml = exportBpmFormDef(formDef, BpmFormDef.IS_NOT_DEFAULT, map);
			list.add(formDefXml);
		}
		bpmFormDefXml.setBpmFormDefXmlList(list);
	}

	/**
	 * 导出表单权限
	 * 
	 * @param formDefId
	 * @param bpmFormDefXml
	 */
	private void exportBpmFormRights(String formKey, BpmFormDefXml bpmFormDefXml) {
		List<BpmFormRights> bpmFormRightsList = bpmFormRightsDao.getByFormKey(formKey, true);
		if (BeanUtils.isNotEmpty(bpmFormRightsList))
			bpmFormDefXml.setBpmFormRightsList(bpmFormRightsList);
	}

	/**
	 * 处理人员
	 * 
	 * @param bpmFormRightsList
	 * @return
	 */
	public List<BpmFormRights> exportBpmFormRightsUser(List<BpmFormRights> bpmFormRightsList) {
		List<BpmFormRights> formRightsList = new ArrayList<BpmFormRights>();
		// 处理人员
		for (BpmFormRights bpmFormRights : bpmFormRightsList) {
			String permission = bpmFormRights.getPermission();
			bpmFormRights.setPermission(parsePermission(permission, true));
			formRightsList.add(bpmFormRights);
		}
		return formRightsList;
	}

	/**
	 * 导出数据模板
	 * 
	 * @param formKey
	 * @param bpmFormDefXml
	 */
	private void exportBpmDataTemplate(String formKey, BpmFormDefXml bpmFormDefXml) {
		BpmDataTemplate bpmDataTemplate = bpmDataTemplateDao.getByFormKey(formKey);
		if (bpmDataTemplate != null)
			bpmFormDefXml.setBpmDataTemplate(bpmDataTemplate);

	}

	/**
	 * 导出业务保存设置
	 * 
	 * @param formKey
	 * @param bpmFormDefXml
	 */
	private void exportSysBusEvent(String formKey, BpmFormDefXml bpmFormDefXml) {
		SysBusEvent sysBusEvent = sysBusEventDao.getByFormKey(formKey);
		if (sysBusEvent != null) {
			bpmFormDefXml.setSysBusEvent(sysBusEvent);
		}
	}

	private void exportFormDefTree(String formKey, BpmFormDefXml bpmFormDefXml) {
		FormDefTree formDefTree = formDefTreeDao.getByFormKey(formKey);
		if (formDefTree != null) {
			bpmFormDefXml.setFormDefTree(formDefTree);
		}
	}

	/**
	 * 导入xml
	 * 
	 * <pre>
	 * 1.导入流程定义
	 * 2.导入流程定义权限
	 * 3.导入数据模板
	 * </pre>
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public void importXml(InputStream inputStream) throws Exception {
		Document doc = Dom4jUtil.loadXml(inputStream);
		Element root = doc.getRootElement();
		// 验证格式是否正确
		this.checkXMLFormat(root);

		String xmlStr = root.asXML();
		BpmFormDefXmlList bpmFormDefXmlList = (BpmFormDefXmlList) XmlBeanUtil.unmarshall(xmlStr, BpmFormDefXmlList.class);
		List<BpmFormDefXml> list = bpmFormDefXmlList.getBpmFormDefXmlList();
		for (BpmFormDefXml bpmFormDefXml : list) {
			this.importBpmFormDef(bpmFormDefXml);
			BpmImportHolder.getTableIdHolder().remove();
			MsgUtil.addSplit();
		}
	}

	/**
	 * 检查XML格式是否正确
	 * 
	 * @param root
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void checkXMLFormat(Element root) throws Exception {
		String msg = "导入文件格式不对";
		if (!root.getName().equals("form"))
			throw new Exception(msg);
		List<Element> itemLists = root.elements();
		for (Element elm : itemLists) {
			if (!elm.getName().equals("formDefs"))
				throw new Exception(msg);
		}
	}


	/**
	 * 导入表单信息
	 * 
	 * @param bpmFormDefXml
	 * @return
	 * @throws Exception
	 */
	public Map<String,Map> importBpmFormDef(BpmFormDefXml bpmFormDefXml) throws Exception {
		Set<Identity> identitySet = new HashSet<Identity>();
		// 导入自定义表
		Map<Long,Long> map = this.importBpmFormTable(bpmFormDefXml.getBpmFormTableXml(), identitySet);
		return importBpmFormDef(bpmFormDefXml,identitySet,map);
	}
	/**
	 * 导入表单信息
	 *
	 * @param bpmFormDefXml
	 * @param identitySet
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Map> importBpmFormDef(BpmFormDefXml bpmFormDefXml,Set<Identity> identitySet,Map<Long,Long> map) throws Exception {
		// 流水号
		bpmFormTableService.importIdentity(identitySet);

		BpmFormDef bpmFormDef = bpmFormDefXml.getBpmFormDef();
		String origFormKey = bpmFormDef.getFormKey();
		Long origFormDefId = bpmFormDef.getFormDefId();
		//先导入历史表单
		importBpmFormDefHis(bpmFormDefXml.getBpmFormDefXmlList(),map);
		// 导入表单信息
		bpmFormDef = this.importBpmFormDef(bpmFormDef, map);
		String formKey = bpmFormDef.getFormKey();
		Long formDefId = bpmFormDef.getFormDefId();

		// // 导入 表单模板
		BpmDataTemplate bpmDataTemplate = bpmFormDefXml.getBpmDataTemplate();
		if (BeanUtils.isNotEmpty(bpmDataTemplate)) {
			this.importBpmDataTemplate(bpmDataTemplate, formKey);
		}
		// 表单权限
		List<BpmFormRights> bpmFormRightsList = bpmFormDefXml.getBpmFormRightsList();
		if (BeanUtils.isNotEmpty(bpmFormRightsList)) {
			for (BpmFormRights bpmFormRights : bpmFormRightsList) {
				this.importBpmFormRights(bpmFormRights, formKey);
			}
		}
		SysBusEvent sysBusEvent = bpmFormDefXml.getSysBusEvent();
		//
		if (BeanUtils.isNotEmpty(sysBusEvent)) {
			importSysBusEvent(sysBusEvent, formKey);
		}
		FormDefTree defTree = bpmFormDefXml.getFormDefTree();
		if (BeanUtils.isNotEmpty(defTree)) {
			importFormDefTree(defTree);
		}

		Map<String, Map> defMap = new HashMap<String, Map>();
		Map<String, String> keyMap = new HashMap<String, String>();
		Map idMap = new HashMap();

		idMap.put(origFormDefId, formDefId);
		keyMap.put(origFormKey, formKey);
		defMap.put("id", idMap);
		defMap.put("key", keyMap);
		return defMap;

	}

	/**
	 * 导入流程定义历史记录
	 * @param bpmFormDefXmlList
	 * @param map
	 */
	private void importBpmFormDefHis(List<BpmFormDefXml> bpmFormDefXmlList, Map map) {
		if (bpmFormDefXmlList==null||bpmFormDefXmlList.size()==0){
			return;
		}
		Collections.sort(bpmFormDefXmlList, new Comparator<BpmFormDefXml>() {
			@Override
			public int compare(BpmFormDefXml o1, BpmFormDefXml o2) {
				return o1.getBpmFormDef().getVersionNo()-o2.getBpmFormDef().getVersionNo();
			}
		});
		for (BpmFormDefXml bpmFormDefXml:bpmFormDefXmlList){
			BpmFormDef bpmFormDef = bpmFormDefXml.getBpmFormDef();
			importFormDefData(map, bpmFormDef);
		}
	}

	/**
	 *  导入自定义表单数据
	 * @param map
	 * @param bpmFormDef
	 */
	private void importFormDefData(Map map, BpmFormDef bpmFormDef){
		importFormDefData(map,bpmFormDef,BpmFormDef.IS_NOT_DEFAULT);
	}
	private void importFormDefData(Map map, BpmFormDef bpmFormDef,Short isDefault) {
		BpmFormDef transFormDef=bpmFormDef;
		BpmFormDef formDefTemp=dao.getById(bpmFormDef.getFormDefId());
		if (formDefTemp!=null){
			if (bpmFormDef.getTableId()!=null){
				formDefTemp.setTableId(bpmFormDef.getTableId());
			}
            bpmFormDef=formDefTemp;
        }
		SysUser sysUser = (SysUser) ContextUtil.getCurrentUser();
		bpmFormDef.setHtml(StringUtil.convertScriptLine(transFormDef.getHtml(), false));
		bpmFormDef.setTemplate(StringUtil.convertScriptLine(transFormDef.getTemplate(), false));
		bpmFormDef.setCategoryId(transFormDef.getCategoryId());
		// 设置tableId
		this.setTableId(bpmFormDef, map);
		// 设置分类
		this.setCategoryId(bpmFormDef);
		if (formDefTemp==null){
            bpmFormDef.setPublishTime(new Date());
            bpmFormDef.setCreatetime(new Date());
            Integer maxVersion = dao.getMaxVersionByFormKey(bpmFormDef.getFormKey());
            if (BeanUtils.isEmpty(maxVersion)){
                maxVersion=0;
            }
            bpmFormDef.setVersionNo(maxVersion + 1);
        }
//		bpmFormDef.setPublishedBy(sysUser.getFullname());
		bpmFormDef.setPublishedBy(ContextSupportUtil.getUsername(sysUser));
		bpmFormDef.setCreateBy(sysUser.getUserId());
//		bpmFormDef.setCreator(sysUser.getFullname());
		bpmFormDef.setCreator(ContextSupportUtil.getUsername(sysUser));
		bpmFormDef.setIsDefault(isDefault);

		//设置分类
		GlobalTypeHolder holder= BpmImportHolder.getFormDefaultTypeHolder().get();
		GlobalTypeService typeService= holder.getTypeService();
		GlobalType category=null;
		if (bpmFormDef.getCategoryId()!=null){
			category=typeService.getById(bpmFormDef.getCategoryId());
		}
		if (category==null){
			category=typeService.getDefaultType(holder.getCatKey(),holder.getEnterpriseCode());
		}
		if(category==null){
			category=holder.getDefaultType();
		}
		bpmFormDef.setCategoryId(category.getTypeId());
		bpmFormDef.setCategoryName(category.getTypeName());

		if (formDefTemp==null){
            dao.add(bpmFormDef);
        }else {
            dao.update(bpmFormDef);
        }
	}

	/**
	 * 保存数据模版
	 * 
	 * @param sysBusEvent
	 * @param formKey
	 */
	private void importSysBusEvent(SysBusEvent sysBusEvent, String formKey) {

		SysBusEvent sysBusEventTemplet = sysBusEventDao.getById(sysBusEvent.getId());
		if (BeanUtils.isNotEmpty(sysBusEventTemplet)) {
			sysBusEventDao.update(sysBusEvent);
			MsgUtil.addMsg(MsgUtil.WARN, " 数据模板已经存在,数据模板ID：" + sysBusEventTemplet.getId() + ",已经存在，该数据模板进行更新!");
			return;
		}
		sysBusEvent.setFormkey(formKey);
		sysBusEventDao.add(sysBusEvent);
		MsgUtil.addMsg(MsgUtil.SUCCESS, " 数据模板成功导入!");
	}

	private void importFormDefTree(FormDefTree formDefTree) {
		FormDefTree defTree = formDefTreeDao.getById(formDefTree.getId());
		if (BeanUtils.isNotEmpty(defTree)) {
			formDefTreeDao.update(defTree);
			MsgUtil.addMsg(MsgUtil.WARN, " 数据模板已经存在,数据模板ID：" + defTree.getName() + ",已经存在，该数据模板进行更新!");
			return;
		}
		formDefTreeDao.add(defTree);
		MsgUtil.addMsg(MsgUtil.SUCCESS, defTree.getName() + " 数据模板成功导入!");
	}

	/**
	 * 导入自定义表
	 * 
	 * @param bpmFormTableXml
	 * @param identitySet
	 * @throws Exception
	 */
	private Map<Long, Long> importBpmFormTable(BpmFormTableXml bpmFormTableXml, Set<Identity> identitySet) throws Exception {
		Map map = new HashMap();

		if (BeanUtils.isNotEmpty(bpmFormTableXml)) {
			map = bpmFormTableService.importBpmFormTableXml(bpmFormTableXml);
			bpmFormTableService.setIdentity(bpmFormTableXml.getIdentityList(), identitySet);
		}
		return map;
	}

	/**
	 * 导入的表单信息保存
	 * 
	 * @param bpmFormDef
	 * @param map
	 * @return
	 */
	private BpmFormDef importBpmFormDef(BpmFormDef bpmFormDef, Map<Long, Long> map) throws Exception {
		BpmFormDef temp=dao.getDefaultPublishedByFormKey(bpmFormDef.getFormKey());
		boolean isHasDefault=false;
		if (temp!=null){
			isHasDefault=true;
		}
		if (isHasDefault&&temp.getFormDefId().equals(bpmFormDef.getFormDefId())){
			importFormDefData(map,bpmFormDef);
			//更新设置默认模板
			dao.setDefaultVersion(bpmFormDef.getFormKey(),bpmFormDef.getFormDefId());
			return bpmFormDef;
		}
		importFormDefData(map,bpmFormDef,BpmFormDef.IS_DEFAULT);
		return bpmFormDef;
	}

	/**
	 * 设置表ID
	 * 
	 * @param bpmFormDef
	 * @param map
	 * @param tableMap
	 * @return
	 */
	private void setTableId(BpmFormDef bpmFormDef, Map<Long, Long> map) {
		if (BeanUtils.isEmpty(bpmFormDef.getTableId()))
			return;
		Long tableId = bpmFormDef.getTableId();
		if (BeanUtils.isNotEmpty(map)) {
			Long origTableId = map.get(tableId);
			if (BeanUtils.isNotIncZeroEmpty(origTableId)) {
				tableId = origTableId;
			}
		}

		BpmFormTable bpmFormTable = bpmFormTableService.getById(tableId);
		if (BeanUtils.isEmpty(bpmFormTable)&&BpmImportHolder.getTableIdHolder().get()!=null){
			tableId=BpmImportHolder.getTableIdHolder().get();
		}else if (BeanUtils.isEmpty(bpmFormTable)){
			bpmFormDef.setTableId(null);
			return;
		}
		bpmFormDef.setTableId(tableId);

	}

	/**
	 * 导入的表单信息保存
	 * 
	 * @param bpmFormDef
	 * @param tableMap
	 * @param msg
	 * @return
	 */
	@SuppressWarnings("unused")
	private void importBpmFormDef(BpmFormDef bpmFormDef) throws Exception {
		// 设置分类
		this.setCategoryId(bpmFormDef);
		// 设置tableId
		this.setTableId(bpmFormDef);
		bpmFormDef.setIsPublished(BpmFormDef.IS_NOT_PUBLISHED);
		bpmFormDef.setPublishedBy(null);
		bpmFormDef.setPublishTime(null);
		dao.add(bpmFormDef);
		MsgUtil.addMsg(MsgUtil.SUCCESS, "自定义表单:" + bpmFormDef.getSubject() + ",该记录成功导入!");
	}

	/**
	 * 设置表ID
	 * 
	 * @param bpmFormDef
	 * @param tableMap
	 * @return
	 */
	private void setTableId(BpmFormDef bpmFormDef) {
		if (BeanUtils.isEmpty(bpmFormDef.getTableId()))
			return;

		BpmFormTable bpmFormTable = bpmFormTableService.getById(bpmFormDef.getTableId());
		if (BeanUtils.isEmpty(bpmFormTable))
			bpmFormDef.setTableId(null);

	}

	/**
	 * 设置分类
	 * 
	 * @param bpmFormDef
	 * @return
	 */
	private void setCategoryId(BpmFormDef bpmFormDef) {
		if (BeanUtils.isEmpty(bpmFormDef.getCategoryId()))
			return;
		GlobalType globalType = globalTypeDao.getById(bpmFormDef.getCategoryId());
		if (BeanUtils.isEmpty(globalType))
			bpmFormDef.setCategoryId(null);

	}

	/**
	 * 保存 表单权限
	 * 
	 * @param bpmFormRights
	 * @param formDefId
	 * @param msg
	 * @return
	 */
	private void importBpmFormRights(BpmFormRights bpmFormRights, String formKey) throws Exception {
		BpmFormRights formRights = bpmFormRightsDao.getById(bpmFormRights.getId());
		if (BeanUtils.isNotEmpty(formRights)) {
			bpmFormRightsDao.update(bpmFormRights);
			MsgUtil.addMsg(MsgUtil.SUCCESS, " 表单权限已经存在:" + bpmFormRights.getName() + ",该记录更新!");
			// MsgUtil.addMsg(MsgUtil.WARN, "表单权限已经存在,表单权限ID："+bpmFormRights.getId()+",该记录终止导入!");
			return;
		}
		bpmFormRights.setFormKey(formKey);
		bpmFormRightsDao.add(bpmFormRights);
		MsgUtil.addMsg(MsgUtil.SUCCESS, " 表单权限:" + bpmFormRights.getName() + ",该记录成功导入!");
	}

	/**
	 * 保存 数据模板
	 * 
	 * @param bpmTableTemplate
	 * @param long1
	 * @param msg
	 * @return
	 */
	private void importBpmDataTemplate(BpmDataTemplate bpmTableTemplate, String formKey) throws Exception {
		BpmDataTemplate tableTemplate = bpmDataTemplateDao.getById(bpmTableTemplate.getId());
		if (BeanUtils.isNotEmpty(tableTemplate)) {
			bpmDataTemplateDao.update(bpmTableTemplate);
			MsgUtil.addMsg(MsgUtil.WARN, " 数据模板已经存在,数据模板ID：" + tableTemplate.getId() + ",已经存在，该数据模板进行更新!");
			return;
		}
		bpmTableTemplate.setFormKey(formKey);
		bpmDataTemplateDao.add(bpmTableTemplate);
		MsgUtil.addMsg(MsgUtil.SUCCESS, " 数据模板成功导入!");
	}

	public void updCategory(Long categoryId, List<String> formKeyList) {
		dao.updCategory(categoryId, formKeyList);
	}

	/**
	 * 根据流程定义ID，取得Table ID
	 * 
	 * @param defId
	 *            流程定义id
	 * @return
	 */
	public Long getTableIdByDefId(Long defId) {
		List<BpmFormDef> bpmFormDefs = dao.getByDefId(defId);
		if (BeanUtils.isNotEmpty(bpmFormDefs)) {
			return bpmFormDefs.get(0).getTableId();
		} else {
			return null;
		}
	}

	/**
	 * 根据流程定义ID，节点ID，取得流程开始表单定义。 在节点没有设置表单时，如果cascade为true，则会查询全局表单和开始表单
	 * 
	 * @param actDefId
	 * @param nodeId
	 * @param cascade
	 *            是否向上查找标志
	 * @return
	 */
	public BpmFormDef getNodeFormDef(String actDefId, String nodeId, boolean cascade) {
		List<BpmFormDef> defs = dao.getByActDefIdAndNodeId(actDefId, nodeId);
		if (BeanUtils.isNotEmpty(defs)) {
			return defs.get(0);
		}

		if (!cascade) {
			return null;
		}

		BpmFormDef def = this.getGlobalFormDef(actDefId);
		if (def != null) {
			return def;
		}

		return def;

	}

	/**
	 * 根据流程定义ID，取得流程全局表单定义
	 * 
	 * @param actDefId
	 * @return
	 */
	public BpmFormDef getGlobalFormDef(String actDefId) {
		List<BpmFormDef> defs = dao.getByActDefIdAndSetType(actDefId, BpmNodeSet.SetType_GloabalForm);
		if (BeanUtils.isNotEmpty(defs)) {
			return defs.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 提交的主键和表名确定数据表单是否有效。
	 * 
	 * <pre>
	 * 1.获取当前节点的表单数据对应的表。
	 * 2.如果主键存在的情况。
	 * 3.那么根据主键去这个表中获取数据。
	 * 如果能够获取到数据说明表单没有更换。否则说明表单已经变更。
	 * </pre>
	 * 
	 * @param formModel
	 * @param pkValue
	 *            主键
	 * @param tableName
	 *            表名
	 */
	private void isDataValid(FormModel formModel, String pkValue, String tableName) {
		// 判断业务主键是否为空。
		if (StringUtil.isEmpty(pkValue) || StringUtil.isEmpty(tableName))
			return;
		long pk = Long.parseLong(pkValue);
		//boolean rtn = bpmFormHandlerService.isExistsData(dsName, tableName, pkName, pkValue)
		formModel.setValid(true);

	}

	/**
	 * 判断自定义表是否绑定了表单
	 * 
	 * @param tableId
	 *            自定义表ID
	 * @return <code>true</code> 绑定了表单<br/>
	 *         <code>false</code> 末绑定表单
	 */
	public boolean isTableHasFormDef(Long tableId) {
		return dao.isTableHasFormDef(tableId);
	}

	/**
	 * 根据tableId构建数据，键值如下：
	 * 
	 * <pre>
	 * mainname：主表描述，若描述为空，则取表名
	 * mainid：主表tableId
	 * tablename：表名
	 * mainfields：主表字段列表，List<BpmFormField> mainTableFields = bpmFormFieldService.getByTableId(tableId);
	 * 
	 * subtables：子表列表
	 * 	name：子表描述，若描述为空，则取表名
	 * 	id：子表tableId
	 * 	tablename：子表表名
	 * 	subfields：子表字段列表，List<BpmFormField> subFields = bpmFormFieldService.getByTableId(subTableId);
	 * </pre>
	 * 
	 * @param tableId
	 * @return String
	 * @see BpmFormField
	 */
	public Map<String, Object> getAllFieldsByTableId(Long tableId) {
		BpmFormTable mainTable = bpmFormTableService.getById(tableId);
		List<BpmFormField> mainTableFields = bpmFormFieldService.getByTableId(tableId);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("mainname", StringUtil.isEmpty(mainTable.getTableDesc()) ? mainTable.getTableName() : mainTable.getTableDesc());
		result.put("mainid", tableId);
		result.put("tablename", mainTable.getTableName());
		result.put("mainfields", mainTableFields);

		List<BpmFormTable> subTables = bpmFormTableService.getSubTableByMainTableId(tableId);
		List<Map<String, Object>> subTableList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < subTables.size(); i++) {
			BpmFormTable subTable = subTables.get(i);
			Long subTableId = subTable.getTableId();
			List<BpmFormField> subFields = bpmFormFieldService.getByTableId(subTableId);
			Map<String, Object> subMap = new HashMap<String, Object>();
			subMap.put("name", StringUtil.isEmpty(subTable.getTableDesc()) ? subTable.getTableName() : subTable.getTableDesc());
			subMap.put("id", subTableId);
			subMap.put("tablename", subTable.getTableName());
			subMap.put("subfields", subFields);
			subTableList.add(subMap);
		}

		result.put("subtables", subTableList);
		return result;
	}
	
	/**
	 * 
	 * @param bpmFormDef
	 * @param pkValue
	 * @param instanceId
	 * @param actDefId
	 * @param nodeId
	 * @param parentActDefId
	 * @param isReCalcute
	 * @param isPreView
	 * @param isReadOnly
	 * @param isCopy
	 * @return
	 * @throws Exception 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public String parseHtml(BpmFormDef bpmFormDef, String pkValue, String instanceId, String actDefId, String nodeId, String parentActDefId, boolean isReCalcute, boolean isPreView, boolean isReadOnly,boolean isCopy,short status) throws Exception {
		String html = "";
		
		Map<String, Object> permission = bpmFormRightsService.getByFormKeyAndUserId(bpmFormDef, actDefId, nodeId, parentActDefId, isReadOnly);
		
		BpmFormData data = bpmFormHandlerService.getBpmFormData(bpmFormDef.getBpmFormTable(), pkValue, instanceId, actDefId, nodeId, isReCalcute, isPreView,isCopy, status);
		List<TaskOpinion> opinionList = new ArrayList<>();
		if(StringUtil.isNotEmpty(instanceId)){
			opinionList = taskOpinionService.getByActInstId(instanceId);
		}
		BpmDefinition bpmDefinition = bpmDefinitionService.getByActDefId(actDefId);
		ParserParam param = new ParserParam(data, permission,opinionList, bpmDefinition);
		param.putVar("instanceId", instanceId);
		param.putVar("actDefId", actDefId);
		
		if(bpmFormDef.getDesignType()==BpmFormDef.DesignType_CustomDesign){
			bpmFormDef.setHtml(StringUtil.unescapeHtml(bpmFormDef.getHtml()));
		}
		html = BpmFormDefHtmlParser.parse(bpmFormDef.getHtml(), param);
		
		html = bpmFormHandlerService.getTabHtml(bpmFormDef.getTabTitle(), html);
		return html;
	}

	/**
	 * 根据分类来统计各个分类数据个数
	 * @param queryFilter
	 * @return
	 */
	public List<GlobalTypeAmount> getAllCount(QueryFilter queryFilter){
		return (List<GlobalTypeAmount>) dao.getAllCount(queryFilter);
	}
}
