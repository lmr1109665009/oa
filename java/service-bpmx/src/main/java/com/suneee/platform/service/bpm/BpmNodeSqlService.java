package com.suneee.platform.service.bpm;

import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.dao.bpm.BpmNodeSqlDao;
import com.suneee.platform.model.bpm.BpmNodeSql;
import com.suneee.platform.model.form.BpmFormData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 对象功能:bpm_node_sql Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-03-05 09:59:29
 * </pre>
 */
@Service
public class BpmNodeSqlService extends BaseService<BpmNodeSql> {
	@Resource
	private BpmNodeSqlDao dao;

	public BpmNodeSqlService() {
	}

	@Override
	protected IEntityDao<BpmNodeSql, Long> getEntityDao() {
		return dao;
	}

	public BpmNodeSql getByNodeIdAndActdefId(String nodeId, String actdefId) {
		List<BpmNodeSql> bpmNodeSqls = getByNodeIdAndActdefIdAndAction(nodeId, actdefId, null);
		if (bpmNodeSqls.isEmpty())
			return null;
		return bpmNodeSqls.get(0);
	}

	public List<BpmNodeSql> getByNodeIdAndActdefIdAndAction(String nodeId, String actdefId, String action) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtil.isNotEmpty(action)) {
			map.put("action", action);
		}
		if (StringUtil.isNotEmpty(nodeId)) {
			map.put("nodeId", nodeId);
		}
		if (StringUtil.isNotEmpty(actdefId)) {
			map.put("actdefId", actdefId);
		}
		return dao.getList("getByNodeIdAndActdefIdAndAction", map);
	}
	
	
	
	/**
	 * 
	 * 处理流程时表单数据跟流程数据
	 * @param processCmd
	 * @param bpmFormData 
	 * void
	 */
	public static void handleData(ProcessCmd processCmd, BpmFormData bpmFormData){
		Map<String, Object> map = new HashMap<String, Object>();
		Map formDataMap= processCmd.getFormDataMap();
		Map data=new HashMap();
		data.putAll(formDataMap);
		data.putAll(bpmFormData.getMainFields());
		data.remove("formData");
		processCmd.addTransientVar("bpmFormData", bpmFormData);
		processCmd.addTransientVar("mainData", data);
		
		
		
	}
}
