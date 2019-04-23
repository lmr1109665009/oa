/**
 * 
 */
package com.suneee.ucp.base.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.excel.reader.DataEntity;
import com.suneee.core.excel.reader.ExcelReader;
import com.suneee.core.excel.reader.FieldEntity;
import com.suneee.core.excel.reader.TableEntity;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.util.*;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.oa.service.user.EnterpriseinfoService;
import com.suneee.platform.dao.system.SysOrgDao;
import com.suneee.platform.event.def.EventUtil;
import com.suneee.platform.model.system.Position;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.dao.system.SysOrgExtDao;
import com.suneee.ucp.base.event.def.OrgEvent;
import com.suneee.ucp.base.model.system.Enterpriseinfo;

import com.suneee.ucp.base.service.UcpBaseService;
import com.suneee.ucp.base.util.HttpUtils;
import com.suneee.ucp.base.util.JsonUtils;
import com.suneee.ucp.base.util.SendMsgCenterUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author xiongxianyun
 *
 */
@Service
public class SysOrgExtService extends UcpBaseService<SysOrg>{
	@Resource
	private SysOrgDao sysOrgDao;
	@Resource 
	private SysOrgService sysOrgService;
	@Resource
	private SysOrgTypeExtService sysOrgTypeExtService;
	@Resource
	private SysOrgExtDao sysOrgExtDao;
	@Resource
	private EnterpriseinfoService enterpriseinfoService;
	
	@Override
	protected IEntityDao<SysOrg, Long> getEntityDao() {
		return sysOrgExtDao;
	}

	/**
	 * 解析组织架构excel并入库
	 * @param file
	 * @throws Exception
	 */
	public String importOrg(MultipartFile file) throws Exception{
		// 读取excel文件内容
		ExcelReader reader = new ExcelReader();
		TableEntity tableEntity = reader.readFile(file.getInputStream());
		List<DataEntity> dataEntityList = tableEntity.getDataEntityList();
		if(dataEntityList == null || dataEntityList.isEmpty()){
			return null;
		}
		
		StringBuilder failedOrg = new StringBuilder();
		// excel中存在数据记录则解析数据入库
		if(0 < dataEntityList.size()){
			SysOrg sysOrg = null;
			Long demId = 1L;
			
			// 被忽略的记录信息
			StringBuilder content = new StringBuilder();
			List<SysOrg> sysOrgList = new ArrayList<SysOrg>();
			List<SysOrg> updateOrgList = new ArrayList<SysOrg>();
			row:
			for(DataEntity dataEntity : dataEntityList){
				List<FieldEntity> fieldEntityList = dataEntity.getFieldEntityList();
				if(fieldEntityList == null){
					continue row;
				}
				// 导入记录字段
				String orgName = null;
				String orgType = null;
				String orgPathName = null;
				String orgDesc = null;
				String orgCode = null;
				String tmpValue = null;
				// 记录总列数
				int totalColumn = fieldEntityList.size();
				// 列值为空的数目
				int blankColumn = 0;
				for(FieldEntity fieldEntity : fieldEntityList){
					if(StringUtils.isBlank(fieldEntity.getValue())){
						blankColumn++;
						// 如果所有列都为空，则忽略此空行，处理下一行数据
						if(blankColumn >= totalColumn){
							continue row;
						} 
						// 继续列循环
						continue;
					}
					tmpValue = fieldEntity.getValue().replaceAll(Constants.SEPARATOR_BLANK, "");
					switch(fieldEntity.getName().trim()){
						case "组织名称":
							orgName = tmpValue;
							break;
						case "组织代码":
						case "企业编码":
							orgCode = tmpValue;
							break;
						case "组织类型":
							orgType = tmpValue;
							break;
						case "组织名称路径":
							orgPathName = tmpValue;
							if(!orgPathName.startsWith(Constants.SEPARATOR_BACK_SLANT)){
								orgPathName = Constants.SEPARATOR_BACK_SLANT + orgPathName;
							}
							// 出现连续的“/”时，使用一个“/”替换
							orgPathName = orgPathName.replaceAll("/{2,}", Constants.SEPARATOR_BACK_SLANT);
							break;
						case "组织描述":
							orgDesc = tmpValue;
							break;
					}
				}
				// 必填字段为空的记录保存到错误文件中
				if(StringUtils.isBlank(orgName) || StringUtils.isBlank(orgType) || StringUtils.isBlank(orgPathName) 
						|| StringUtils.isBlank(orgCode)){
					for(FieldEntity entity : fieldEntityList){
						content.append(entity.getName()).append(":").append(entity.getValue()).append(",");
					}
					content.append(Constants.SEPARATOR_LINE_BREAK);
					failedOrg.append(orgPathName).append("\t").append("请将组织导入模板中的标红信息补充完整").append(Constants.SEPARATOR_LINE_BREAK);
					continue;
				}
				// 组织名称路径验证
				if(orgPathName.replaceAll("/", "").length() <= 0){
					for(FieldEntity entity : fieldEntityList){
						content.append(entity.getName()).append(":").append(entity.getValue()).append(",");
					}
					content.append(Constants.SEPARATOR_LINE_BREAK);
					failedOrg.append(orgPathName).append("\t").append("组织名称路径格式错误").append(Constants.SEPARATOR_LINE_BREAK);
					continue;
				}
				
				// 企业编码验证
				Enterpriseinfo enterpriseInfo = enterpriseinfoService.getByCompCode(orgCode);
				if(enterpriseInfo == null){
					for(FieldEntity entity : fieldEntityList){
						content.append(entity.getName()).append(":").append(entity.getValue()).append(",");
					}
					content.append(Constants.SEPARATOR_LINE_BREAK);
					failedOrg.append(orgPathName).append("\t").append("企业编码在系统中不存在").append(Constants.SEPARATOR_LINE_BREAK);
					continue;
				}
				// 导入模板中组织名称与组织名称路径中的组织名称不同时，组织名称修改为“组织名称路径/组织名称”（防止导入模板中组织名称与组织名称路径中的组织名称不同时，导致组织重复创建）
				String orgNameSplit = orgPathName.substring(orgPathName.lastIndexOf(Constants.SEPARATOR_BACK_SLANT) + 1);
				if(!orgName.equals(orgNameSplit)){
					orgPathName = orgPathName + Constants.SEPARATOR_BACK_SLANT + orgName;
				}
				
				// 当前组织在数据库中存在，则更新组织的企业编码及组织类型
				sysOrg = sysOrgExtDao.getByOrgPathName(orgPathName, demId, null);
				if(null != sysOrg){
					sysOrg.setOrgCode(orgCode);
					sysOrg.setOrgType(sysOrgTypeExtService.getOrgTypeIdByNameAndDemId(orgType, demId));
					if(StringUtils.isNotBlank(orgDesc)){
						sysOrg.setOrgDesc(orgDesc);
					}
					this.update(sysOrg);
					updateOrgList.add(sysOrg);
					continue;
				}
				
				// 将该组织信息加入数据库
				addSysOrg(orgName, orgCode, orgType, orgPathName, orgDesc, demId, sysOrgList);
			}
			// 发布组织变更消息
			EventUtil.publishOrgEvent(OrgEvent.ACTION_ADD, sysOrgList);
			for (SysOrg org : sysOrgList) {//往消息中心发送消息
				SendMsgCenterUtils sendMsgCenterUtils = new SendMsgCenterUtils();
				sendMsgCenterUtils.sendToUserInfoCenter(org,Constants.MESSAGE_STATUS_ADD,AppConfigUtil.get(Constants.MESSAGE_ORG_TOPIC));
			}
			EventUtil.publishOrgEvent(OrgEvent.ACTION_UPD, updateOrgList);
			for (SysOrg org : updateOrgList) {
				SendMsgCenterUtils sendMsgCenterUtils = new SendMsgCenterUtils();
				sendMsgCenterUtils.sendToUserInfoCenter(org,Constants.MESSAGE_STATUS_UPDATE,AppConfigUtil.get(Constants.MESSAGE_ORG_TOPIC));
			}
			// 存在异常数据，则将异常保存到错误文件中
			if(0 != content.length()){
				String fileName = AppConfigUtil.get(Constants.IMPORT_ERRORDATA_FILEPATH) + file.getName()
				+ DateFormatUtil.format(new Date(), "yyyyMMddHHmmssSSS") + ".txt";
				FileUtil.writeFile(fileName, content.toString());
			}
		}
		return failedOrg.toString();
	}
	
	/**
	 * 获取组织详细信息列表
	 * @param queryFilter
	 * @return
	 */
	public List<Map<String, String>> getOrgDetailsList(QueryFilter queryFilter){
		return sysOrgExtDao.getOrgDetailsList(queryFilter);
	}
	
	/**
	 * 根据组织路径名称获取组织信息
	 * @param orgPathName
	 * @param demId
	 * @param orgId
	 * @return
	 */
	public SysOrg getByOrgPathName(String orgPathName, Long demId, Long orgId){
		return sysOrgExtDao.getByOrgPathName(orgPathName, demId, orgId);
	}
	
	/**
	 * @param orgName
	 * @param orgCode
	 * @param orgType
	 * @param orgPathName
	 * @param orgDesc
	 * @param demId
	 * @param list
	 * @return
	 */
	public SysOrg addSysOrg(String orgName, String orgCode, String orgType, String orgPathName, String orgDesc, Long demId, List<SysOrg> list){
		SysOrg sysOrg = new SysOrg();
		Long orgId = UniqueIdUtil.genId();
		// 组织ID
		sysOrg.setOrgId(orgId);
		// 维度ID
		sysOrg.setDemId(demId);
		// 组织名称
		sysOrg.setOrgName(orgName);
		// 组织描述
		sysOrg.setOrgDesc(orgDesc);
		// 组织类型ID：根据组织类型名称和维度ID查询组织类型ID，若不存在，则创建
		sysOrg.setOrgType(sysOrgTypeExtService.getOrgTypeIdByNameAndDemId(orgType, demId));
		// 创建人
		sysOrg.setCreatorId(ContextUtil.getCurrentUserId());
		// 创建时间
		sysOrg.setCreatetime(new Date());
		sysOrg.setSn(orgId);
		sysOrg.setFromType(SysOrg.FROMTYPE_DB);
		sysOrg.setOrgCode(orgCode);
		// 微信编码
		String code = this.generateCode(orgCode, orgName, null);
		sysOrg.setCode(code);
		
		// 获取上级组织信息
		int lastIndex = orgPathName.lastIndexOf(Constants.SEPARATOR_BACK_SLANT);
		// 没有上级组织
		if(lastIndex <= 0){
			sysOrg.setOrgSupId(demId);
			sysOrg.setPath(demId + "." + orgId + ".");
			sysOrg.setOrgPathname("/" + orgName);
			sysOrg.setDepth(0);
		}
		// 存在上级组织
		else{
			SysOrg supSysOrg = getSupOrgInfo(orgPathName.substring(0, orgPathName.lastIndexOf("/")), orgCode, demId, orgType, list);
			sysOrg.setOrgSupId(supSysOrg.getOrgId());
			sysOrg.setPath(supSysOrg.getPath() + orgId + ".");
			sysOrg.setOrgPathname(supSysOrg.getOrgPathname() + "/" + orgName);
			
			sysOrg.setDepth(sysOrg.getOrgPathname().split(Constants.SEPARATOR_BACK_SLANT).length - 2);
		}
		this.add(sysOrg);
		list.add(sysOrg);
		return sysOrg;
	}
	
	public String generateCode(String orgCode, String orgName, Long orgId){
		// 微信编码
		String code = orgCode.toLowerCase() + Constants.SEPARATOR_UNDERLINE + PinyinUtil.getPinYinHeadCharFilter(orgName);
		String tmpCode = code;
		int index = 1;
		// 如果微信编码已经存在，
		while(sysOrgService.getCountByCode(tmpCode, orgId) > 0){
			tmpCode = code + index;
			index++;
		}
		return tmpCode;
	}
	
	/**
	 * 获取组织的集团编码（集团编码为一级组织编码）
	 * @param orgId
	 * @return
	 */
	public String getEnterpriseCodeById(Long orgId){
		String enterpriseCode = null;
		SysOrg org = sysOrgDao.getById(orgId);
		if(org == null){
			return enterpriseCode;
		}
		if(org.getDemId() == org.getOrgSupId()){
			enterpriseCode = org.getOrgCode();
		}else{
			enterpriseCode = this.getEnterpriseCodeById(org.getOrgSupId());
		}
		
		return enterpriseCode;
	}
	
	/**
	 * 从权限中心获取企业信息
	 * @param filter
	 * @return
	 * @throws IOException
	 */
	public List<Enterpriseinfo> getEnterpirseinfoList(QueryFilter filter) throws IOException{
		// 获取filter中的请求参数
		Map<String, Object> params = filter.getFilters();
		
		// 组建权限中心的请求参数
		Map<String, Object> reqParam = new HashMap<String, Object>();
		reqParam.put("compName", params.get("compName"));
		reqParam.put("groupCode", AppConfigUtil.get(Constants.AUTHORITY_PARAM_COMPCODE));
		
		// 组建权限中心请求地址
		PageBean pageBean = filter.getPageBean();
		String apiMethod = Constants.AUTHORITY_CENTER_ENTERPRISELIST_API + "?pageSize="+ pageBean.getPageSize() 
			+ "&currentPage=" + pageBean.getCurrentPage();
		JSONObject result = HttpUtils.sendToAuthorityCenter(apiMethod, JSONObject.fromObject(reqParam).toString());
		PageList<Enterpriseinfo> enterpriseinfoList = new PageList<Enterpriseinfo>();
		if(result.getBoolean("success")){
			// 获取数据总条数
			JSONObject data = result.getJSONObject("data");
			pageBean.setTotalCount(data.getInt("totalCount"));
			filter.setPageBean(pageBean);
			
			//设置回请求对象，控制分页信息显示
			filter.setForWeb();
			
			// 获取数据列表
			String infoListStr = data.getString("list");
			List<Enterpriseinfo> infoList = JsonUtils.jsonToList(infoListStr, Enterpriseinfo[].class);
			enterpriseinfoList.addAll(infoList);
			enterpriseinfoList.setPageBean(pageBean);
		}
		return enterpriseinfoList;
	}
	
	public List<SysOrg> getByOrgCodes(String enterpriseCodes){
		List<String> orgCodes = null;
		if(StringUtils.isNotBlank(enterpriseCodes)){
			orgCodes = Arrays.asList(enterpriseCodes.split(Constants.SEPARATOR_COMMA));
		}
		
		
		return sysOrgExtDao.getByOrgCodes(orgCodes);
	}
	
	/** 
	 * 根据路径得到组织集合 
	 * @param path
	 * @return
	 */
	public List<SysOrg> getByOrgPath(String path){
		return sysOrgService.getByOrgPath(path);
	}
	
	/**
	 * 获取上级组织信息
	 * @param orgPathName 上级组织的组织名称路径
	 * @param demId 维度ID
	 * @return
	 */
	private SysOrg getSupOrgInfo(String orgPathName, String orgCode, Long demId, String orgType, List<SysOrg> list){
		// 数据库中存在则直接返回上级组织信息
		SysOrg sysOrg = sysOrgExtDao.getByOrgPathName(orgPathName, demId, null);
		if(null == sysOrg){
			String supOrgName = orgPathName.substring(orgPathName.lastIndexOf("/") + 1, orgPathName.length());
			sysOrg = addSysOrg(supOrgName, orgCode, orgType, orgPathName, supOrgName, demId, list);
		}
		return sysOrg;
	}
	/**
	 * 根据用户id获取获取组织及岗位信息
	 * @param
	 * @param
	 * @return
	 */
	public List<SysOrg> getToUIdByOrg(Long userId){
		// 数据库中存在则直接返回上级组织信息
		List<SysOrg> list = sysOrgExtDao.getToUIdByOrg(userId);

		return list;
	}
	public Position getToUIdByPosition(Long userId){

		return sysOrgDao.getToUIdByPosition(userId);
	}
}
