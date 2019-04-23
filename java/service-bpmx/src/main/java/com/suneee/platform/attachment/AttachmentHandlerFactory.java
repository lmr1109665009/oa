package com.suneee.platform.attachment;

import java.util.HashSet;
import java.util.Set;

import com.suneee.core.util.BeanUtils;
import com.suneee.platform.service.util.ServiceUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.platform.service.util.ServiceUtil;


public class AttachmentHandlerFactory {
	// 附件处理器集合
	private Set<AttachmentHandler> attachmentHandlers = new HashSet<AttachmentHandler>();
	// 当前附件处理器
	private AttachmentHandler currentHandler;
	//附件保存类型
	private static String saveType;
	private String getSaveType(){
		if (saveType!=null){
			return saveType;
		}
		saveType=ServiceUtil.getSaveType();
		return saveType;
	}

	public void setAttachmentHandlers(Set<AttachmentHandler> attachmentHandlers) {
		this.attachmentHandlers = attachmentHandlers;
	}
	
	public AttachmentHandler getCurrentHandler() throws Exception{
		if(BeanUtils.isEmpty(currentHandler)){
			for (AttachmentHandler attachmentHandler : attachmentHandlers) {
				if(attachmentHandler.getType().equals(getSaveType())){
					currentHandler = attachmentHandler;
					break;
				}
			}
			if(BeanUtils.isEmpty(currentHandler)){
				throw new RuntimeException("未找到对应的附件处理器，请检查系统属性中的file.saveType属性");
			}
		}
		return currentHandler;
	}
	
	
	public AttachmentHandler getCurrentHandler(String saveTypePath) throws Exception{
		String saveType = ServiceUtil.getSaveType(saveTypePath);
			for (AttachmentHandler attachmentHandler : attachmentHandlers) {
				if(attachmentHandler.getType().equals(saveType)){
					currentHandler = attachmentHandler;
					break;
				}
			}
			if(BeanUtils.isEmpty(currentHandler)){
				throw new RuntimeException("未找到对应的附件处理器");
			}
		return currentHandler;
	}
}
