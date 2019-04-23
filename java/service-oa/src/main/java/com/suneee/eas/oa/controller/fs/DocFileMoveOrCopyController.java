package com.suneee.eas.oa.controller.fs;

import com.suneee.eas.common.component.ResponseMessage;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.common.utils.RequestUtil;
import com.suneee.eas.oa.model.fs.DocFile;
import com.suneee.eas.oa.service.fs.DocFileService;
import com.suneee.eas.oa.service.user.UserPositionService;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.UserPosition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/me/newDocFile/")
public class DocFileMoveOrCopyController{
	@Value("${fs.docFile.groupId}")
	private Long groupId;

	@Resource
	private DocFileService docFileService;

	@Resource
	private UserPositionService uerPositionService;
	
	@RequestMapping("toMoveOrCopy")
	@ResponseBody
	public ResponseMessage tomove(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long[] docFileIds = RequestUtil.getLongAryByStr(request, "docFileIds");
		Long id = RequestUtil.getLong(request, "id");
		int isMove = RequestUtil.getInt(request, "isMove");
		SysUser user = (SysUser) ContextSupportUtil.getCurrentUser();// 获取当前用户
		String eid = ContextSupportUtil.getCurrentEnterpriseCode();
		//获取要移动到的文件夹是属于哪个文件柜
		int classify=docFileService.getClassify(id,0l);
		Long depmentId = null;
		// 获取当前用户所在的部门id
		List<UserPosition> userPositionList = uerPositionService.getByAccount(user.getAccount());
		if (userPositionList != null && userPositionList.size() > 0) {
			depmentId = userPositionList.get(0).getOrgId();
		}
		try {
			if((long)classify == groupId){
				eid=docFileService.getGroupCode();
			}
			Double countSize = 0.0;
			if (1 == isMove) {
				//原来的文件夹size大小需要减去
				Long oddParent = docFileService.findById(docFileIds[0]).getParentId();
				docFileService.move(docFileIds,id,classify,eid,user.getUserId(),depmentId);
				List<DocFile> doclist = docFileService.getByIds(docFileIds);
				for (DocFile docFile : doclist) {
					docFileService.renameDocFile(docFile,true);
					String si = docFile.getSize();
					Double size = docFileService.getDoubleSize(si);
					countSize += size;
				}
				docFileService.setSubParentSize(oddParent,countSize);
				docFileService.setAddParentSize(id,countSize);
				return ResponseMessage.success("移动成功！");
			} else {
				List<DocFile> oldDocFiles = docFileService.getByIds(docFileIds);
					DocFile newDocFile = new DocFile();
					newDocFile.setUper(user.getUserId());
					newDocFile.setUperName(ContextSupportUtil.getCurrentUsername());
					newDocFile.setEid(eid);
					newDocFile.setClassify(classify);

					newDocFile.setDepartmentId(depmentId);
					for (DocFile docFile : oldDocFiles) {
						docFile.setParentId(id);
						docFileService.renameDocFile(docFile,true);
						this.copy(docFile,newDocFile, id);
						String si = docFile.getSize();
						Double size =docFileService.getDoubleSize(si);
						countSize += size;
				}
				docFileService.setAddParentSize(id,countSize);
				return ResponseMessage.success("复制成功！");
			}
			
		} catch (Exception ex) {
			return ResponseMessage.fail("操作文件失败！");
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
		Long id = IdGeneratorUtil.getNextId();
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
		docFileService.save(newDocFile);
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
