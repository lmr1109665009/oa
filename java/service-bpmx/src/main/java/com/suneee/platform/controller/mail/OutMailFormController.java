package com.suneee.platform.controller.mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.core.web.util.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.core.web.controller.BaseFormController;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.annotion.Action;
import com.suneee.platform.model.mail.OutMail;
import com.suneee.platform.model.mail.OutMailLinkman;
import com.suneee.platform.model.mail.OutMailUserSeting;
import com.suneee.platform.model.system.SysAuditModelType;
import com.suneee.platform.service.mail.OutMailLinkmanService;
import com.suneee.platform.service.mail.OutMailService;
import com.suneee.platform.service.mail.OutMailUserSetingService;
import com.suneee.platform.service.oa.OaLinkmanService;
import com.suneee.platform.service.util.ServiceUtil;
/**
 * 对象功能:邮件 控制器类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zyp
 * 创建时间:2012-04-09 14:16:18
 */
@Controller
@RequestMapping("/platform/mail/outMail/")
@Action(ownermodel=SysAuditModelType.USER_MANAGEMENT)
public class OutMailFormController extends BaseFormController
{
	@Resource
	private OutMailService outMailService;
	@Resource
	private OaLinkmanService oaLinkmanService;
	@Resource
	private OutMailUserSetingService outMailUserSetingService;
	
	@Resource 
	private OutMailLinkmanService outMailLinkmanService;
	
	
	/**
	 * 添加或更新邮件。
	 * @param request
	 * @param response
	 * @param outMail 添加或更新的实体
	 * @param bindResult
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("send")
	@Action(description="发送邮件",detail="发送邮件")
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response, OutMail outMail) throws Exception
	{
		ModelAndView mv=new ModelAndView("/platform/mail/outMailSuccess.jsp");
		int type=outMail.getTypes();
		long userId= ContextUtil.getCurrentUserId();
		long mailId= RequestUtil.getLong(request, "mailId",0);
		int isReply=RequestUtil.getInt(request, "isReply",0);
		OutMailUserSeting outMailUserSeting=outMailUserSetingService.getMailByAddress(outMail.getSenderAddresses());
		outMail.setMailDate(new Date());
		outMail.setIsReply(isReply);
		outMail.setUserId(userId);
		outMail.setSenderName(outMailUserSeting.getUserName());
		outMail.setSetId(outMailUserSeting.getId());
		String context=request.getContextPath();
		String basePath=ServiceUtil.getBasePath();
		ResultMessage message=null;
		
		try{
			//获取邮件地址
			Set<String> list= getMailAddress(outMail);
			//发送邮件
			if(type==2){
				outMailService.sendMail(outMail,userId,mailId,isReply,context,basePath);
				handLinkMan(userId, list);
				message=new ResultMessage(ResultMessage.Success, "发送邮件成功!");
			}
			//草稿
			else{
				if(mailId==0){
					outMail.setMailId(UniqueIdUtil.genId());
					//添加发出邮件
					outMailService.add(outMail);
				}else{
					outMailService.update(outMail);
				}
				message=new ResultMessage(ResultMessage.Success, "保存邮件成功!");
			}
			Set<String> set = checkAddress(list);
			mv.addObject("addrList", set);
			
		}catch (Exception e) {
			e.printStackTrace();
			message=new ResultMessage(ResultMessage.Fail, e.getMessage());
		}
		mv.addObject("message", message);
		return mv;
	}

	/**
	 * 处理联系人
	 * @param userId
	 * @param list
	 * @throws Exception 
	 * void
	 */
	private void handLinkMan(long userId, Set<String> list) throws Exception{
		OutMailLinkman man =null;
		//向最近联系人中增加记录或更新记录
		for(String address:list){
			man = outMailLinkmanService.findLinkMan(address, userId);
			String linkName = outMailService.getNameByEmail(address);
			if(man!=null){//更新
				man.setSendTimes(man.getSendTimes()+1);
				man.setLinkName(linkName);
				Date date = new Date(System.currentTimeMillis());
				man.setSendTime(date);
				outMailLinkmanService.update(man);
			}else{//添加
				man=new OutMailLinkman();
				man.setSendTimes(1);
				man.setUserId(userId);
				man.setLinkName(linkName);
				man.setSendTime(new Date());
				man.setLinkAddress(address);
				man.setLinkId(UniqueIdUtil.genId());
				outMailLinkmanService.add(man);
			}
		}
	}
	
	private Set<String> getMailAddress(OutMail outMail){
		String toAddess=outMail.getReceiverAddresses();
		String ccAddress=outMail.getCcAddresses();
		String bccAddress=outMail.getBcCAddresses();
		List<String> list=new ArrayList<String>();
		addAddress(toAddess,list);
		addAddress(ccAddress,list);
		addAddress(bccAddress,list);
		Set<String> set = new HashSet(list);
		return set;
	}
	
	
	private void addAddress(String address,List<String> list){
		if(StringUtil.isEmpty(address)) return;
		address=StringUtil.trim(address, ",");
		String[] aryAddress=address.split(",");
		for(String addr:aryAddress){
			list.add(addr);
		}
	}
	
	/**
	 * 检查地址是否存在于联系人列表
	 * @param set
	 * @return 
	 * Set&lt;String>
	 */
	private Set<String> checkAddress(Set<String> set){
		Long currentUserId=ContextUtil.getCurrentUserId();
		Set<String> rtnset = new HashSet<String>();
		for(String addr:set){
			//判断地址是否存在于联系人列表当中
			boolean flag = oaLinkmanService.isOaLinkExist(currentUserId, addr);
			if(flag) continue;
			rtnset.add(addr);
		}
		return rtnset;
	}

	/**
	 * 在实体对象进行封装前，从对应源获取原实体
	 * @param mailId
	 * @param model
	 * @return
	 * @throws Exception
	 */
    @ModelAttribute
    protected OutMail getFormObject(@RequestParam("mailId") Long mailId,Model model) throws Exception {
		logger.debug("enter OutMail getFormObject here....");
		OutMail outMail=null;
		if(mailId!=null){
			outMail=outMailService.getById(mailId);
		}else{
			outMail= new OutMail();
		}
		return outMail;
    }
   
}
