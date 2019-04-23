package com.suneee.kaoqin.service.kaoqin;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.kaoqin.dao.kaoqin.VacationRemainDao;
import com.suneee.kaoqin.model.kaoqin.VacationRemain;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:假期结余 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2017-05-09 11:32:12
 *</pre>
 */
@Service
public class VacationRemainService extends BaseService<VacationRemain>
{
	@Resource
	private VacationRemainDao dao;
	
	
	
	public VacationRemainService()
	{
	}
	
	@Override
	protected IEntityDao<VacationRemain, Long> getEntityDao()
	{
		return dao;
	}
	
	
	
	/**
	 * 流程处理器方法 用于处理业务数据
	 * @param cmd
	 * @throws Exception
	 */
	public void processHandler(ProcessCmd cmd)throws Exception{
		Map data=cmd.getFormDataMap();
		if(BeanUtils.isNotEmpty(data)){
			String json=data.get("json").toString();
			VacationRemain vacationRemain=getVacationRemain(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				vacationRemain.setId(genId);
				this.add(vacationRemain);
			}else{
				vacationRemain.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(vacationRemain);
			}
			cmd.setBusinessKey(vacationRemain.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取VacationRemain对象
	 * @param json
	 * @return
	 */
	public VacationRemain getVacationRemain(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		VacationRemain vacationRemain = JSONObjectUtil.toBean(json, VacationRemain.class);
		return vacationRemain;
	}
	
	/**
	 * 保存 假期结余 信息
	 * @param vacationRemain
	 */
	public void save(VacationRemain vacationRemain){
		Long id=vacationRemain.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			vacationRemain.setId(id);
			this.add(vacationRemain);
		}
		else{
			this.update(vacationRemain);
		}
	}
	
	/**
	 * 获取假期结余
	 * @param userId
	 * @param vacationTypeId
	 * @return
	 */
	public VacationRemain getByUserIdAndType(long userId, long vacationTypeId) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("vacationType", vacationTypeId);
		return dao.getUnique("getByUserIdAndType", map);
	}
	
	/**
	 * 获取假期数据
	 * @param userId
	 * @return
	 */
	public List<VacationRemain> getByUserId(Long userId) {
		return dao.getBySqlKey("getByUserId", userId);
	}

	/**
	 * 获取指定用户的假期结余信息
	 * @param userId
	 * @return
	 */
	public List<VacationRemain> getUserRemainDetail(Long userId) {
		return dao.getBySqlKey("getUserRemainDetail", userId);
	}

	/**
	 * 获取假期截止时间在指定时间之前的结余列表
	 * @return
	 */
	public List<VacationRemain> getOverdueRemains(Date date) {
		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("endvalidDate", date);
		return dao.getAll(filter);
	}
	
}
