package com.suneee.oa.controller.document;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.util.CookieUitl;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.platform.service.system.SysOrgService;
import com.suneee.platform.service.system.UserPositionService;
import com.suneee.oa.model.docFile.DocFile;
import com.suneee.oa.service.docFile.DocFileService;
import com.suneee.ucp.base.controller.UcpBaseController;
import com.suneee.ucp.base.vo.ResultVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Properties;
@Controller
@RequestMapping("/me/newDocFile/")
public class DocFileMoveOrCopyController extends UcpBaseController{

	@Resource
	private DocFileService docFileService;

	@Resource
	private SysOrgService orgService;
	@Resource
	private UserPositionService uerPositionService;
	@Resource
	private Properties configProperties;
	
	@RequestMapping("toMoveOrCopy")
	@ResponseBody
	@Action(description = "移动文件至其他目录文件夹下")
	public ResultVo tomove(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long[] docFileIds = RequestUtil.getLongAryByStr(request, "docFileIds");
		ResultVo message = null;

		Long id = RequestUtil.getLong(request, "id");
		int isMove = RequestUtil.getInt(request, "isMove");
		SysUser user = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
		String eid = CookieUitl.getCurrentEnterpriseCode();
		//获取要移动到的文件夹是属于哪个文件柜
		int classify=docFileService.getClassify(id,0l);
		Long depmentId = null;
		// 获取当前用户所在的部门id
		List<UserPosition> userPositionList = uerPositionService.getByUserId(user.getUserId());
		if (userPositionList != null && userPositionList.size() > 0) {
			depmentId = userPositionList.get(0).getOrgId();
		}
		try {
			if((long)classify == Long.valueOf(configProperties.getProperty("groupId"))){
				eid=docFileService.getGroupCode();
			}
			Double countSize = 0.0;
			if (1 == isMove) {
				//原来的文件夹size大小需要减去
				Long oddParent = docFileService.getById(docFileIds[0]).getParentId();
				docFileService.move(docFileIds,id,classify,eid,user.getUserId(),depmentId);
				List<DocFile> doclist = docFileService.getByIds(docFileIds);
				for (DocFile docFile : doclist) {
					docFileService.renameDocFile(docFile);
					String si = docFile.getSize();
					Double size = docFileService.getDoubleSize(si);
					countSize += size;
				}
				docFileService.setSubParentSize(oddParent,countSize);
				docFileService.setAddParentSize(id,countSize);
				return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"移动成功！");
			} else {
				List<DocFile> oldDocFiles = docFileService.getByIds(docFileIds);
					DocFile newDocFile = new DocFile();
					newDocFile.setUper(user.getUserId());
					newDocFile.setUperName(user.getFullname());
					newDocFile.setEid(eid);
					newDocFile.setClassify(classify);

					newDocFile.setDepartmentId(depmentId);
					for (DocFile docFile : oldDocFiles) {
						docFile.setParentId(id);
						docFileService.renameDocFile(docFile);
						this.copy(docFile,newDocFile, id);
						String si = docFile.getSize();
						Double size =docFileService.getDoubleSize(si);
						countSize += size;
				}
				docFileService.setAddParentSize(id,countSize);
				return new ResultVo(ResultVo.COMMON_STATUS_SUCCESS,"复制成功！");
			}
			
		} catch (Exception ex) {
			return new ResultVo(ResultVo.COMMON_STATUS_FAILED,"操作文件失败！");
		}
	}

	/**
	 * 递归复制文件
	 * @param docFile
	 * @param newDocFile
	 * @param parentId
	 */
	public void copy(DocFile docFile, DocFile newDocFile, Long parentId) {
		// 如果是文档则直接复制
		newDocFile.setName(docFile.getName());
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		newDocFile.setUpTime(ts);
		newDocFile.setDescribe(docFile.getDescribe());
		newDocFile.setParentId(parentId);
		Long id = UniqueIdUtil.genId();
		newDocFile.setId(id);
		newDocFile.setTypes(docFile.getTypes());
		newDocFile.setPath(docFile.getPath());
		newDocFile.setSize(docFile.getSize());
		newDocFile.setRank(docFile.getRank());
		newDocFile.setIsPrivate(docFile.getIsPrivate());
		newDocFile.setOwner(docFile.getOwner());
		//newDocFile.setDepartmentId(docFile.getDepartmentId());
		// 如果是文件夹则复制文件夹并且继续往里层复制
		newDocFile.setIsPrivate(docFile.getIsPrivate());
		newDocFile.setOwner(docFile.getOwner());
		newDocFile.setDownNumber(0l);
		newDocFile.setFileSaveId(docFile.getFileSaveId());
		newDocFile.setIsDocType(docFile.getIsDocType());
		docFileService.add(newDocFile);
		//以要复制的文件夹id作为parentId查找子文件。如果有则遍历。没有则结束单个循环。
		List<DocFile> tempDocFileList = docFileService.getByParentId(docFile.getId());
		if(tempDocFileList.size()!= 0){
			for (DocFile docFile2 : tempDocFileList) {
				this.copy(docFile2,newDocFile, newDocFile.getId());
				newDocFile.setId(id);
			}
		}
	}



}
