package com.suneee.platform.service.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.form.FormDefCombinateDao;
import com.suneee.platform.model.form.FormDefCombinate;
import net.sf.json.util.JSONUtils;
import net.sf.ezmorph.object.DateMorpher;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.util.StringUtil;
import net.sf.json.JSONObject;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.form.FormDefCombinate;
import com.suneee.platform.dao.form.FormDefCombinateDao;
import com.suneee.core.service.BaseService;

/**
 * <pre>
 * 对象功能:form_def_combinate Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-05-20 09:55:36
 * </pre>
 */
@Service
public class FormDefCombinateService extends BaseService<FormDefCombinate> {
	@Resource
	private FormDefCombinateDao dao;

	public FormDefCombinateService() {
	}

	@Override
	protected IEntityDao<FormDefCombinate, Long> getEntityDao() {
		return dao;
	}


	/**
	 * 保存 form_def_combinate 信息
	 * 
	 * @param formDefCombinate
	 */
	public void save(FormDefCombinate formDefCombinate) {
		Long id = formDefCombinate.getId();
		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			formDefCombinate.setId(id);
			this.add(formDefCombinate);
		} else {
			this.update(formDefCombinate);
		}
	}
	
	public FormDefCombinate getByAlias(String alias){
		Map<String, Object> params= new HashMap<String, Object>();
		params.put("alias", alias);
		return dao.getUnique("getByAlias", params);
	}

}
