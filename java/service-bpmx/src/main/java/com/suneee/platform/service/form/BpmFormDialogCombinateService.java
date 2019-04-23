package com.suneee.platform.service.form;

import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.form.BpmFormDialogCombinateDao;
import com.suneee.platform.model.form.BpmFormDialogCombinate;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <pre>
 * 对象功能:bpm_form_dialog_combinate Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-05-06 11:36:18
 * </pre>
 */
@Service
public class BpmFormDialogCombinateService extends BaseService<BpmFormDialogCombinate> {
	@Resource
	private BpmFormDialogCombinateDao dao;

	public BpmFormDialogCombinateService() {
	}

	@Override
	protected IEntityDao<BpmFormDialogCombinate, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 根据json字符串获取BpmFormDialogCombinate对象
	 * 
	 * @param json
	 * @return
	 */
	public BpmFormDialogCombinate getBpmFormDialogCombinate(String json) {
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher((new String[] { "yyyy-MM-dd" })));
		if (StringUtil.isEmpty(json))
			return null;
		JSONObject obj = JSONObject.fromObject(json);
		BpmFormDialogCombinate bpmFormDialogCombinate = (BpmFormDialogCombinate) JSONObject.toBean(obj, BpmFormDialogCombinate.class);
		return bpmFormDialogCombinate;
	}

	/**
	 * 保存 bpm_form_dialog_combinate 信息
	 * 
	 * @param bpmFormDialogCombinate
	 * @throws Exception
	 */
	public void save(BpmFormDialogCombinate bpmFormDialogCombinate) throws Exception {

		Long id = bpmFormDialogCombinate.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			bpmFormDialogCombinate.setId(id);
			this.add(bpmFormDialogCombinate);
		} else {
			this.update(bpmFormDialogCombinate);
		}
	}

	public BpmFormDialogCombinate getByAlias(String alias) {
		return dao.getByAlias(alias);
	}
}
