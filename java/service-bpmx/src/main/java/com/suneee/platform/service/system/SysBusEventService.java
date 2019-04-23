package com.suneee.platform.service.system;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.SysBusEventDao;
import com.suneee.platform.model.system.SysBusEvent;
import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 *<pre>
 * 对象功能:sys_bus_event Service类
 * 开发公司:宏天软件
 * 开发人员:ray
 * 创建时间:2015-05-22 11:14:30
 *</pre>
 */
@Service
public class SysBusEventService extends BaseService<SysBusEvent>
{
	@Resource
	private SysBusEventDao dao;
	
	
	
	public SysBusEventService()
	{
	}
	
	@Override
	protected IEntityDao<SysBusEvent, Long> getEntityDao()
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
			SysBusEvent sysBusEvent=getSysBusEvent(json);
			if(StringUtil.isEmpty(cmd.getBusinessKey())){
				Long genId= UniqueIdUtil.genId();
				sysBusEvent.setId(genId);
				this.add(sysBusEvent);
			}else{
				sysBusEvent.setId(Long.parseLong(cmd.getBusinessKey()));
				this.update(sysBusEvent);
			}
			cmd.setBusinessKey(sysBusEvent.getId().toString());
		}
	}
	
	/**
	 * 根据json字符串获取SysBusEvent对象
	 * @param json
	 * @return
	 */
	public SysBusEvent getSysBusEvent(String json){
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher((new String[] { "yyyy-MM-dd" })));
		if(StringUtil.isEmpty(json))return null;
		JSONObject obj = JSONObject.fromObject(json);
		SysBusEvent sysBusEvent = (SysBusEvent)JSONObject.toBean(obj, SysBusEvent.class);
		return sysBusEvent;
	}
	
	/**
	 * 保存 sys_bus_event 信息
	 * @param sysBusEvent
	 */
	public void save(SysBusEvent sysBusEvent){
		Long id=sysBusEvent.getId();
		if(id==null || id==0){
			id=UniqueIdUtil.genId();
			sysBusEvent.setId(id);
			this.add(sysBusEvent);
		}
		else{
			this.update(sysBusEvent);
		}
	}
	
	/**
	 * 根据formKey获取脚本验证。
	 * @param formKey
	 * @return
	 */
	public SysBusEvent getByFormKey(String formKey){
		return dao.getByFormKey(formKey);
	}
	
	
}
