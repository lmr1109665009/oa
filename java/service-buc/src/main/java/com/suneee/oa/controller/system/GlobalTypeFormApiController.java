package com.suneee.oa.controller.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.log.SysAuditThreadLocalHolder;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.system.GlobalTypeService;
import com.suneee.platform.service.system.IdentityService;
import com.suneee.platform.service.system.ShareService;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * 对象功能:系统分类接口
 * 开发公司:深圳象羿
 * 开发人员:子华
 * 创建时间:2018-01-09 09:28:34
 */
@Controller
@RequestMapping("/api/system/globalType/")
@Action(ownermodel=SysAuditModelType.SYSTEM_SETTING)
public class GlobalTypeFormApiController extends BaseFormController
{
	@Resource
	private GlobalTypeService globalTypeService;
	@Resource
	private IdentityService identityService;
	@Resource
	private ShareService shareService;
	
	/**
	 * 添加或更新系统分类。
	 * @param request
	 * @param response
	 * @param globalType 添加或更新的实体
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("save")
	@ResponseBody
	@Action(description="添加或更新系统分类",
			detail="<#if isAdd>添加<#else>更新</#if>系统分类" +
					"【${SysAuditLinkService.getGlobalTypeLink(Long.valueOf(typeId))}】")
	public ResultVo save(HttpServletRequest request, HttpServletResponse response, GlobalType globalType, BindingResult bindResult) throws Exception
	{
		ResultMessage resultMessage=validForm("globalType", globalType, bindResult, request);
		if(resultMessage.getResult()== ResultMessage.Fail){
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,resultMessage.getMessage());
		}
		boolean isadd=globalType.getTypeId()==0;
		//父节点
		long parentId=RequestUtil.getLong(request, "parentId",0);
		//是否根节点
		int isRoot=RequestUtil.getInt(request, "isRoot");
		int isPrivate=RequestUtil.getInt(request, "isPrivate",0);
		Long userId=ContextUtil.getCurrentUserId();

		String resultMsg=null;
		if(globalType.getTypeId()==0){
			if(parentId!=0){
				GlobalType parentGlobal=globalTypeService.getById(parentId);
				if(parentGlobal!=null){
					parentGlobal.setIsLeaf(1);
					globalTypeService.update(parentGlobal);
				}
			}
			GlobalType tmpGlobalType=globalTypeService.getInitGlobalType(isRoot,parentId);
			String catKey=tmpGlobalType.getCatKey();
			String nodeKey=getUniqueKeyAdd(0,globalType.getTypeName(),catKey,globalType.getEnterpriseCode());
			globalType.setNodeKey(nodeKey);
			//分类key不为数据字典的情况
			if(!catKey.equals(GlobalType.CAT_DIC)){
				globalType.setType(tmpGlobalType.getType());
			}
			//设置用户ID
			if(isPrivate==1){
				globalType.setUserId(userId);
			}
			globalType.setCatKey(catKey);
			globalType.setNodePath(tmpGlobalType.getNodePath());
			globalType.setTypeId(tmpGlobalType.getTypeId());
			globalType.setDepth(1);
			globalType.setSn(0L);
			globalType.setIsLeaf(0);
			if(globalType.getNodeCodeType().equals(GlobalType.NODE_CODE_TYPE_AUTO_Y)){
				globalType.setNodeCode(identityService.nextId(globalType.getNodeCode()));
			}
			globalType.setEnterpriseCode(CookieUitl.getCurrentEnterpriseCode());
			globalTypeService.add(globalType);
			resultMsg="添加分类成功";
		}else{
			if(isRoot==1){
				globalType.setNodePath(parentId +"." + globalType.getTypeId() +".");
			}
			else{
				GlobalType parentGlobalType=globalTypeService.getById(parentId);
				if (parentGlobalType==null){
					globalType.setNodePath(parentId +"." + globalType.getTypeId() +".");
				}else {
					globalType.setNodePath(parentGlobalType.getNodePath() +globalType.getTypeId() +".");
					globalType.setEnterpriseCode(parentGlobalType.getEnterpriseCode());
				}
			}
			Long typeId=globalType.getTypeId();
			String catKey=globalType.getCatKey();
			String nodeKey=getUniqueKeyUpdate(0,globalType.getTypeName(),typeId,catKey,globalType.getEnterpriseCode());
			globalType.setNodeKey(nodeKey);
			globalTypeService.updateAndSubPath(globalType);
			resultMsg="更新分类成功";
		}

		try {
			SysAuditThreadLocalHolder.putParamerter("isAdd", isadd);
			SysAuditThreadLocalHolder.putParamerter("typeId", globalType.getTypeId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		int status=resultMessage.getResult()==ResultMessage.Success?ResultVo.COMMON_STATUS_SUCCESS:ResultVo.COMMON_STATUS_FAILED;
		return new ResultVo(status,resultMsg,resultMessage.getData());
	}

	/**
	 * 在实体对象进行封装前，从对应源获取原实体
	 * @param typeId
	 * @param model
	 * @return
	 * @throws Exception
	 */
    @ModelAttribute
    protected GlobalType getFormObject(@RequestParam("typeId") Long typeId,Model model) throws Exception {
		GlobalType globalType=null;
		
		if(typeId!=0){
			globalType=globalTypeService.getById(typeId);
		}else{
			globalType= new GlobalType();
		}
		return globalType;
    }

	/**
	 * 获取唯一key
	 * @param pos
	 * @param name
	 * @param catKey
	 * @param enterpriseCode
	 * @return
	 * @throws UnsupportedEncodingException
	 */
    private String getUniqueKeyAdd(int pos,String name,String catKey,String enterpriseCode) throws UnsupportedEncodingException {
		String nodeKey="";
    	if (pos>0){
			nodeKey=shareService.getPingyin(name+pos);
		}else {
    		nodeKey=shareService.getPingyin(name);
		}
		boolean isExistEnterprise=globalTypeService.isNodeKeyExistsForEnterprise(catKey, nodeKey,enterpriseCode);
    	boolean isExistNode=globalTypeService.isNodeKeyExists(catKey,nodeKey,enterpriseCode);
    	if (isExistEnterprise||isExistNode){
    		return getUniqueKeyAdd(++pos,name,catKey,enterpriseCode);
		}else {
    		return nodeKey;
		}
	}
	private String getUniqueKeyUpdate(int pos,String name,Long typeId,String catKey,String ecodes) throws UnsupportedEncodingException {
		String nodeKey="";
		if (pos>0){
			nodeKey=shareService.getPingyin(name+pos);
		}else {
			nodeKey=shareService.getPingyin(name);
		}
		boolean isExistEnterprise=globalTypeService.isNodeKeyExistsForEnterprise(catKey, nodeKey,ecodes);
		boolean isExistNode=globalTypeService.isNodeKeyExistsForUpdate(typeId,catKey,nodeKey, CookieUitl.getCurrentEnterpriseCode());
		if (isExistEnterprise||isExistNode){
			return getUniqueKeyUpdate(++pos,name,typeId,catKey,ecodes);
		}else {
			return nodeKey;
		}
	}

}
