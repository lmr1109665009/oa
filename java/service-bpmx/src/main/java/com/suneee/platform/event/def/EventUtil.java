package com.suneee.platform.event.def;

import com.suneee.core.util.UniqueIdUtil;
import com.suneee.eas.common.utils.ContextUtil;
import com.suneee.platform.model.system.Position;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.oa.model.user.UserEnterprise;
import com.suneee.platform.model.system.Position;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.model.system.UserPosition;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.event.def.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 描述：为事件生成的工具类
 * 构建组：bpm33
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2015-3-10-下午5:41:55
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
@Component
public class EventUtil {

	private static Environment env;

	@Autowired
	public void setEnv(Environment env) {
		EventUtil.env = env;
	}

	/**
	 * 发布一个NodeSqlEvent
	 * @param actDefId	：流程ID
	 * @param nodeId	:节点ID
	 * @param action	：事件
	 * @param dataMap 	：主表数据Map
	 * void
	 */
	public static void publishNodeSqlEvent(String actDefId, String nodeId, String action, Map<String, Object> dataMap) {
		NodeSqlContext context = new NodeSqlContext();
		context.setActDefId(actDefId);
		context.setNodeId(nodeId);
		context.setAction(action);
		context.setDataMap(dataMap);
		NodeSqlEvent event = new NodeSqlEvent(context);
		AppUtil.publishEvent(event);
	}
	
	/**
	 *  发布用户事件。
	 * @param user	用户ID
	 * @param action 动作。
	 * @param needSyncToUserCenter 是否需要同步到用户中心
	 */
	public static void publishUserEvent(SysUser user, int action, boolean needSyncToUserCenter){
		UserEvent ev=new UserEvent(user.getUserId());
		ev.setAction(action);
		ev.setUser(user);
		ev.setNeedSyncToUserCenter(needSyncToUserCenter);
		AppUtil.publishEvent(ev);
	}
	/**
	 * 发布新流程启动事件
	 * @param action
	 * @param nodeId 
	 * @param processCmd
	 */
	public static void publishTriggerNewFlowEvent(String action, String nodeId, ProcessCmd processCmd) {
		TriggerNewFlowEvent tiNewFlowEvent = new TriggerNewFlowEvent(new TriggerNewFlowModel(action,nodeId, processCmd));
		AppUtil.publishEvent(tiNewFlowEvent);
	}
	
	/**
	 * 发布组织事件
	 * @param action
	 * @param sysOrgList
	 */
	public static final void publishOrgEvent(int action, List<SysOrg> sysOrgList){
		OrgEvent orgEvent = new OrgEvent(sysOrgList);
		orgEvent.setAction(action);
		AppUtil.publishEvent(orgEvent);
	}
	
	/**
	 * 发布用户岗位（组织）关系事件
	 * @param action
	 * @param userPosList
	 */
	public static final void publishUserPositionEvent(int action, List<UserPosition> userPosList){
		UserPositionEvent userPosEvent = new UserPositionEvent(userPosList);
		userPosEvent.setAction(action);
		AppUtil.publishEvent(userPosEvent);
	}
	
	/**
	 * 发布岗位事件
	 * @param action
	 * @param positionList
	 */
	public static final void publishPositionEvent(int action, List<Position> positionList){
		PositionEvent positionEvent = new PositionEvent(positionList);
		positionEvent.setAction(action);
		AppUtil.publishEvent(positionEvent);
	}
	
	/**
	 * 发布定子链消息事件
	 * @param msgType
	 * @param target
	 * @param targetType
	 * @param status
	 * @param data
	 */
	public static final void publishApolloMessageEvent(int msgType, List<Map<String, Object>> target, int targetType, int status, Object data){
		ApolloMessage message = new ApolloMessage();
		message.setMsgType(msgType);
		message.setTarget(target);
		message.setTargetType(targetType);
		message.setStatus(status);
		message.setData(data);
		//企业编码动态获取
		String compCode = env.getProperty("compCode." + target.get(1).get("runtimeEnv"));
		message.setCompCode(compCode);
//		message.setCompCode(AppConfigUtil.get(Constants.MESSAGE_ENTERPRISE_CODE));
		ApolloMessageEvent apolloMessageEvent = new ApolloMessageEvent(message);
		AppUtil.publishEvent(apolloMessageEvent);
	}
	
	/** 
	 * 发布用户企业关系事件
	 * @param action 操作类型
	 * @param sysUser 用户信息
	 * @param userEnterpriseList 用户企业关系集合
	 */
	public static final void publishUserEnterpriseEvent(int action, SysUser sysUser, List<UserEnterprise> userEnterpriseList){
		UserEnterpriseEvent userEnterpriseEvent = new UserEnterpriseEvent(userEnterpriseList);
		userEnterpriseEvent.setAction(action);
		userEnterpriseEvent.setSysUser(sysUser);
		AppUtil.publishEvent(userEnterpriseEvent);
	}
}
