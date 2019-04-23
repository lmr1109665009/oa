package com.suneee.platform.event.listener;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.MapUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.event.def.NodeSqlContext;
import com.suneee.platform.event.def.NodeSqlEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.suneee.core.db.datasource.JdbcTemplateUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.MapUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.platform.event.def.NodeSqlContext;
import com.suneee.platform.event.def.NodeSqlEvent;
import com.suneee.platform.model.bpm.BpmNodeSql;
import com.suneee.platform.model.form.BpmFormData;
import com.suneee.platform.service.bpm.BpmNodeSqlService;
import com.suneee.platform.service.form.BpmFormHandlerService;

/**
 * 流程结束监听事件。
 * 
 * @author ray
 * 
 */
@Service
public class NodeSqlEventListener implements ApplicationListener<NodeSqlEvent>, Ordered {
	
	@Resource
	BpmNodeSqlService bpmNodeSqlService;
	@Resource
	BpmFormHandlerService bpmFormHandlerService;

	@Override
	public void onApplicationEvent(NodeSqlEvent event) {
		NodeSqlContext source = (NodeSqlContext) event.getSource();
		Map<String, Object> dataMap = (Map<String, Object>) source.getDataMap();// 主表数据
		String actdefId = source.getActdefId();// 流程id
		String nodeId =  source.getNodeId();// 节点id
		String action = source.getAction();// 执行时机
		
		// 确保获取到的事件是正确的	
		if(StringUtil.isEmpty(actdefId)) return ;
		
		List<BpmNodeSql> nodeSqls = bpmNodeSqlService.getByNodeIdAndActdefIdAndAction(nodeId, actdefId, action);
		if(nodeSqls.isEmpty()) return;
		
		try {
			if(BeanUtils.isNotEmpty(dataMap)){
				BpmFormData data = bpmFormHandlerService.getBpmFormData(dataMap.get("id").toString(),nodeId);//这才是拿到全部主数据
				dataMap.putAll(data.getMainFields());
			}
			
			for (BpmNodeSql nodeSql : nodeSqls) {
				String sql = nodeSql.getSql();
				if (dataMap != null && !dataMap.isEmpty()) {
					// 1.先根据主表数据拼装sql
					Pattern pattern = Pattern.compile("<#(.*?)#>");
					Matcher matcher = pattern.matcher(sql);
					while (matcher.find()) {
						String str = matcher.group();
						String key = matcher.group(1);
						String val = MapUtil.getString(dataMap, key);
						sql = sql.replace(str, val);
					}
				}
				// 2.开始执行sql
				JdbcTemplate jdbcTemplate = JdbcTemplateUtil.getNewJdbcTemplate(nodeSql.getDsAlias());
				jdbcTemplate.execute(sql);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public int getOrder() {
		return 2;
	}

}
