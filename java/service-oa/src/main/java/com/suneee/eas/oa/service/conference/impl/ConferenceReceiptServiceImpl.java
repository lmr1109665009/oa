/**
 * @Title: ConferenceReceiptServiceImpl.java 
 * @Package com.suneee.eas.oa.service.conference.impl 
 * @Description: TODO(用一句话描述该文件做什么) 
 */ 
package com.suneee.eas.oa.service.conference.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.eas.common.service.impl.BaseServiceImpl;
import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.eas.common.utils.IdGeneratorUtil;
import com.suneee.eas.oa.dao.conference.ConferenceReceiptDao;
import com.suneee.eas.oa.exception.common.NotExistInDatabaseException;
import com.suneee.eas.oa.exception.conference.AlreadyHandledException;
import com.suneee.eas.oa.exception.conference.IllegalStatusException;
import com.suneee.eas.oa.exception.conference.NotParticipantException;
import com.suneee.eas.oa.exception.conference.UserPermissionException;
import com.suneee.eas.oa.model.conference.ConferenceReceipt;
import com.suneee.eas.oa.model.conference.ConferenceReserve;
import com.suneee.eas.oa.model.conference.ConferenceReserveParticipant;
import com.suneee.eas.oa.service.conference.ConferenceReceiptService;
import com.suneee.eas.oa.service.conference.ConferenceReserveParticipantService;
import com.suneee.eas.oa.service.conference.ConferenceReserveService;

/**
 * @ClassName: ConferenceReceiptServiceImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @Company: 深圳象翌
 * @author xiongxianyun
 * @date 2018-08-09 11:19:54 
 *
 */
@Service
public class ConferenceReceiptServiceImpl extends BaseServiceImpl<ConferenceReceipt> implements ConferenceReceiptService{
	private ConferenceReceiptDao conferenceReceiptDao;
	@Autowired
	private ConferenceReserveParticipantService conferenceReserveParticipantService;
	@Autowired
	private ConferenceReserveService conferenceReserveService;
	@Autowired
	public void setConferenceReceiptDao(ConferenceReceiptDao conferenceReceiptDao){
		this.conferenceReceiptDao = conferenceReceiptDao;
		setBaseDao(conferenceReceiptDao);
	}
	
	/** 
	 * @Title: save 
	 * @Description: 保存会议回执信息
	 * @param conferenceId
	 * @param status
	 * @param remark 
	 * @throws NotParticipantException 
	 * @throws AlreadyHandledException
	 * @throws IllegalStatusException 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReceiptService#save(java.lang.Long, java.lang.Byte, java.lang.String)
	 */
	@Override
	public void save(Long conferenceId, Byte status, String remark) throws UserPermissionException, NotParticipantException, AlreadyHandledException, IllegalStatusException {
		if(conferenceId == null || status == null){
			throw new IllegalArgumentException("conferenceId and status must not be null.");
		}
		// 判断回执状态是否合法
		if(status != ConferenceReceipt.STATUS_PARTICIPANT && status != ConferenceReceipt.STATUS_NOT_PARTICIPANT 
				&&  status != ConferenceReceipt.STATUS_UNDETERMINED){
			throw new IllegalStatusException("receipt status " + status + " is not support.");
		}
		
		// 查询会议信息
		ConferenceReserve conferenceReserve = conferenceReserveService.findById(conferenceId);
		if(conferenceReserve == null){
			throw new NotExistInDatabaseException("ConferenceReserve record of " + conferenceId + " is not exist in database.");
		}
		// 如果是会议发起人不用发送会议回执
		if(conferenceReserve.getCreateBy().equals(ContextSupportUtil.getCurrentUserId())){
			throw new UserPermissionException("the user " + ContextSupportUtil.getCurrentUserId() + " is started user");
		}
		// 当前用户ID
		Long userId = ContextSupportUtil.getCurrentUserId();
		// 判断当前用户是否是参会人员
		boolean isParticipant = conferenceReserveParticipantService.isParticipant(conferenceId, userId, ConferenceReserveParticipant.TYPE_PENDING);
		if(!isParticipant){
			throw new NotParticipantException("the user " + userId + " is not the participant of conference " + conferenceId);
		}
		
		// 判断用户是否已经发送过回执
		ConferenceReceipt receiptDb = this.getConferenceReceipt(conferenceId, userId);
		if(receiptDb != null){
			throw new AlreadyHandledException("the user " + userId + " already sent a receipt of cponference " + conferenceId);
		}
		
		// 保存会议回执
		ConferenceReceipt receipt = new ConferenceReceipt();
		receipt.setReceiptId(IdGeneratorUtil.getNextId());
		receipt.setConferenceId(conferenceId);
		receipt.setUserId(userId);
		receipt.setUserName(ContextSupportUtil.getCurrentUsername());
		receipt.setStatus(status);
		receipt.setRemark(remark);
		receipt.setReceiptTime(new Date());
		this.save(receipt);
	}

	/** 
	 * @Title: getByConferenceId 
	 * @Description:获取会议的所有回执信息
	 * @param conferenceId
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReceiptService#getByConferenceId(java.lang.Long)
	 */
	@Override
	public List<ConferenceReceipt> getByConferenceId(Long conferenceId) {
		if(conferenceId == null){
			throw new IllegalArgumentException("conferenceId must not be null.");
		}
		return conferenceReceiptDao.getByConferenceId(conferenceId);
	}
	
	/** 
	 * @Title: getConferenceReceipt 
	 * @Description: 获取会议回执信息
	 * @param conferenceId
	 * @param userId
	 * @return 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReceiptService#getConferenceReceipt(java.lang.Long, java.lang.Long)
	 */
	public ConferenceReceipt getConferenceReceipt(Long conferenceId, Long userId){
		return this.conferenceReceiptDao.getConferenceReceipt(conferenceId, userId);
	}

	/** 
	 * @Title: deleteByConferenceId 
	 * @Description: 删除会议回执信息
	 * @param conferenceId 
	 * @see com.suneee.eas.oa.service.conference.ConferenceReceiptService#deleteByConferenceId(java.lang.Long)
	 */
	public void deleteByConferenceId(Long conferenceId){
		this.conferenceReceiptDao.deleteByConferenceId(conferenceId);
	}
}
