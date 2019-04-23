package com.suneee.kaoqin.service.kaoqin;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.jsonobject.JSONObjectUtil;
import com.suneee.kaoqin.dao.kaoqin.ShiftTimeDao;
import com.suneee.kaoqin.model.kaoqin.ShiftTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 *<pre>
 * 对象功能:班次时间段 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:mikel
 * 创建时间:2017-05-02 10:09:27
 *</pre>
 */
@Service
public class ShiftTimeService extends BaseService<ShiftTime>
{
	@Resource
	private ShiftTimeDao dao;
	
	
	
	public ShiftTimeService()
	{
	}
	
	@Override
	protected IEntityDao<ShiftTime, Long> getEntityDao()
	{
		return dao;
	}
	
	/**
	 * 根据班次ID获取班次时间段列表
	 * @param shiftId
	 * @return
	 */
	public List<ShiftTime> getTimesByShiftId(Long shiftId) {
		return dao.getBySqlKey("getTimesByShiftId", shiftId);
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
			ShiftTime shiftTime=getShiftTime(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				shiftTime.setId(genId);
				this.add(shiftTime);
			}else{
				shiftTime.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(shiftTime);
			}
			cmd.setBusinessKey(shiftTime.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取ShiftTime对象
	 * @param json
	 * @return
	 */
	public ShiftTime getShiftTime(String json){
		
		if(StringUtil.isEmpty(json))return null;
		
		ShiftTime shiftTime = JSONObjectUtil.toBean(json, ShiftTime.class);
		return shiftTime;
	}
	
	/**
	 * 保存 班次时间段 信息
	 * @param shiftTime
	 */
	public void save(ShiftTime shiftTime){
		Long id=shiftTime.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			shiftTime.setId(id);
			this.add(shiftTime);
		}
		else{
			this.update(shiftTime);
		}
	}
	
}
