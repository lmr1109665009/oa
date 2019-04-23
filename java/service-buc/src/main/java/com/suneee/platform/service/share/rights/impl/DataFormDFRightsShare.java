package com.suneee.platform.service.share.rights.impl;

import javax.annotation.Resource;

import com.suneee.platform.model.form.BpmDataTemplate;
import com.suneee.platform.service.share.rights.DataTemplateVO;
import com.suneee.platform.service.share.rights.FormDFRightShare;
import org.springframework.stereotype.Service;

import com.suneee.platform.model.form.BpmDataTemplate;
import com.suneee.platform.service.form.BpmDataTemplateService;
import com.suneee.platform.service.form.BpmFormDefService;
import com.suneee.platform.service.share.rights.DataTemplateVO;
import com.suneee.platform.service.share.rights.FormDFRightShare;

/**
 * @author as xianggang
 * 
 */
@Service
public class DataFormDFRightsShare extends FormDFRightShare {
	
	@Resource
	BpmDataTemplateService bpmDataTemplateService;
	
	@Resource
	BpmFormDefService bpmFormDefService ;
	
	
	BpmDataTemplate bpmDataTemplate;

	@Override
	public String getShareType() {
		return "formDTDF";
	}

	@Override
	public String getShareDesc() {
		return "业务数据模板数据权限";
	}

	@Override
	public DataTemplateVO getDataObject(String id) {
		DataTemplateVO vo = new DataTemplateVO();
		BpmDataTemplate bpmDataTemplate = bpmDataTemplateService.getByFormKey(id);
		if(bpmDataTemplate==null)
			bpmDataTemplate = bpmDataTemplateService.getByFormKey(bpmFormDefService.getById(Long.parseLong(id)).getFormKey());
		vo.setDisplayField(bpmDataTemplate.getDisplayField());
		vo.setExportField(bpmDataTemplate.getExportField());
		vo.setFilterField(bpmDataTemplate.getFilterField());
		vo.setManageField(bpmDataTemplate.getManageField());
		vo.setPrintField(bpmDataTemplate.getPrintField());
		return vo;
	}

	@Override
	public DataTemplateVO getDataTemplateVO(String ruleId) {
		bpmDataTemplate = bpmDataTemplateService.getByFormKey(ruleId);
		DataTemplateVO vo = new DataTemplateVO();
		vo.setDisplayField(bpmDataTemplate.getDisplayField());
		vo.setExportField(bpmDataTemplate.getExportField());
		vo.setFilterField(bpmDataTemplate.getFilterField());
		vo.setManageField(bpmDataTemplate.getManageField());
		vo.setPrintField(bpmDataTemplate.getPrintField());
		return vo;
	}

	@Override
	public void updateDataTemplateVO(DataTemplateVO vo) {
		bpmDataTemplate.setDisplayField(vo.getDisplayField());
		bpmDataTemplate.setExportField(vo.getExportField());
		bpmDataTemplate.setFilterField(vo.getFilterField());
		bpmDataTemplate.setManageField(vo.getManageField());
		bpmDataTemplate.setPrintField(vo.getPrintField());
		bpmDataTemplateService.update(bpmDataTemplate);
	}
}
