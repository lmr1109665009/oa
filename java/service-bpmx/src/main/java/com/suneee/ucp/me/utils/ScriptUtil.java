/**  
 * @Title: ScriptUtil.java
 * @Package com.suneee.ucp.me.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author yiwei
 * @date 2017年5月2日
 * @version V1.0  
 */
package com.suneee.ucp.me.utils;


import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.system.SysUserService;
import com.suneee.ucp.me.model.OfficeObject;
import com.suneee.ucp.me.model.OfficeObjectApply;
import com.suneee.ucp.me.service.OfficeObjectApplyService;
import com.suneee.ucp.me.service.OfficeObjectService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @ClassName: ScriptUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author yiwei
 * @date 2017年5月2日
 *
 */
public class ScriptUtil{
	private static final Logger log= LogManager.getLogger(ScriptUtil.class);

	/**
	 * 
	 * @Title: updateOfficeObjecr
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param 商品id
	 * @param @param applyNum
	 * @param @param processId    流程id
	 * @return void    返回类型
	 * @throws
	 */
	public void updateOfficeObjecr(String id, String applyNum, Long processId, String approverId){
		
		try {
			OfficeObjectService officeService = AppUtil.getBean(OfficeObjectService.class);
			OfficeObjectApplyService officeObjectApplyService = AppUtil.getBean(OfficeObjectApplyService.class);
			SysUserService userService = AppUtil.getBean(SysUserService.class);
			
			
			Long idL = Long.valueOf(id);
			Long approverIdL = Long.valueOf(approverId);
			int applyNumInt = Integer.valueOf(applyNum);
			
			SysUser approverUser = userService.getById(approverIdL);//审核人信息
			
			if(officeService!=null){
				OfficeObject object = officeService.getById(idL);
				int newStore = object.getStore() - applyNumInt;
				object.setStore(newStore);
				officeService.update(object);
				//插入领用记录
				if(officeObjectApplyService != null ){
					
					OfficeObjectApply apply = new OfficeObjectApply();
					
					SysUser curUser = (SysUser) ContextUtil.getCurrentUser();// 获取当前用户
					apply.setId(UniqueIdUtil.genId());
					apply.setCreateBy(curUser.getUserId());
					apply.setCreator(curUser.getUsername());//领用人
					
					apply.setApplyNumber(applyNumInt);
					if(approverUser!=null){
						apply.setApprover(approverUser.getUsername());//审核人
					}
					apply.setApproverId(approverId);
					apply.setType(object.getType());
					apply.setSpecification(object.getSpecification());
					apply.setObjectName(object.getObjectName());
					apply.setProcessId(processId);
					apply.setArea(object.getArea());
					officeObjectApplyService.add(apply);
				}
				
				log.error("updateOfficeObjecr: newStore = " + newStore);
			}else{
				log.error("updateOfficeObjecr error...");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
