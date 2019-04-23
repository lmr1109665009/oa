package com.suneee.platform.service.ats;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.*;
import com.suneee.platform.dao.ats.AtsCardRecordDao;
import com.suneee.platform.model.ats.AtsCardRecord;
import com.suneee.platform.service.ats.impl.AtsTakeCardRecordService;
import com.suneee.platform.service.util.ServiceUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * <pre>
 * 对象功能:打卡记录 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:zxh
 * 创建时间:2015-05-26 11:21:21
 * </pre>
 */
@Service
public class AtsCardRecordService extends BaseService<AtsCardRecord> {
	@Resource
	private AtsCardRecordDao dao;
	@Resource
	private AtsTakeCardRecordService atsTakeCardRecordService;

	public AtsCardRecordService() {
	}

	@Override
	protected IEntityDao<AtsCardRecord, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 保存 打卡记录 信息
	 * 
	 * @param atsCardRecord
	 */
	public void save(AtsCardRecord atsCardRecord) {
		Long id = atsCardRecord.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			atsCardRecord.setId(id);
			this.add(atsCardRecord);
		} else {
			this.update(atsCardRecord);
		}
	}

	public void importText(InputStream is, String startDate, String endDate) throws Exception {
		atsTakeCardRecordService.getCardRecordByText(is, startDate, endDate);
	}

	public void importExcel(InputStream is, String startDate, String endDate) throws Exception {
		atsTakeCardRecordService.getCardRecordByExcel(is, startDate, endDate);
	}

	public void importAccess(MultipartFile multipartFile, String startDate, String endDate) throws Exception {
		String realFilePath = StringUtil.trimSufffix(ServiceUtil.getBasePath().toString(), File.separator) + File.separator + "attachFiles" + File.separator + "importAccess" + File.separator;
		String originalFilename = multipartFile.getOriginalFilename();
		String destPath = realFilePath + originalFilename;

		ZipUtil.createFilePath(destPath, originalFilename);
		File file = new File(destPath);
		if (file.exists())
			file.delete();
		multipartFile.transferTo(file);

		if (BeanUtils.isEmpty(startDate) || BeanUtils.isEmpty(endDate)) {
			atsTakeCardRecordService.getCardRecordByAccess(destPath);
		} else {
			atsTakeCardRecordService.getCardRecordByAccess(destPath, startDate, endDate);
		}
	}

	public List<AtsCardRecord> getByCardNumber(String cardNumber) {
		return dao.getByCardNumber(cardNumber);
	}

	public Map<String, Set<Date>> getByCardNumberMap(String cardNumber, Date startTime, Date endTime) {
		List<AtsCardRecord> list = dao.getByCardNumberCardDate(cardNumber, startTime, endTime);
		Map<String, Set<Date>> map = new HashMap<String, Set<Date>>();
		for (AtsCardRecord atsCardRecord : list) {
			String dateStr = DateFormatUtil.formatDate(atsCardRecord.getCardDate());
			Set<Date> list1 = map.get(dateStr);
			if (BeanUtils.isEmpty(list1))
				list1 = new LinkedHashSet<Date>();
			list1.add(atsCardRecord.getCardDate());
			map.put(dateStr, list1);
		}
		return map;
	}

	public Set<Date> getByCardNumberSet(String cardNumber, Date startTime, Date endTime) {
		List<AtsCardRecord> list = dao.getByCardNumberCardDate(cardNumber, startTime, endTime);
		Set<Date> set = new TreeSet<Date>();
		for (AtsCardRecord atsCardRecord : list) {
			set.add(atsCardRecord.getCardDate());
		}
		return set;
	}

}
