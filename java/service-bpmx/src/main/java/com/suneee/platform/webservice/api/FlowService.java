package com.suneee.platform.webservice.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;

/**
 * 流程的相关接口
 * 
 */
@SOAPBinding(style = Style.DOCUMENT, parameterStyle = ParameterStyle.WRAPPED)
@WebService(endpointInterface = "com.suneee.platform.webservice.api.FlowService", targetNamespace = "http://impl.webservice.platform.hotent.com/")
public interface FlowService {

	/**
	 * 获取当前用户可使用的流程数据
	 * 
	 * @param json
	 *            :查询参数
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	@WebMethod(operationName = "getMyFlowListJson")
	public String getMyFlowListJson(@WebParam(name = "json") String json);
	
	/**
	 * 获取某用户的待办任务
	 * @param json
	 * @return
	 */
	@WebMethod(operationName = "getMyTaskByAccount")
	public String getMyTaskByAccount(@WebParam(name = "json") String json);
	

	/**
	 * 获取某用户的已办事宜
	 * @param json
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	@WebMethod(operationName = "getAlreadyMattersListJson")
	public String getAlreadyMattersListJson(@WebParam(name = "json") String json);

	/**
	 * 获取指定某用户的请求
	 * @param json
	 *            :查询参数
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	@WebMethod(operationName = "getMyRequestListJson")
	public String getMyRequestListJson(@WebParam(name = "json") String json);

	/**
	 * 获取某用户的办结事宜
	 * @param json
	 *            :查询参数
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	@WebMethod(operationName = "getMyCompletedListJson")
	public String getMyCompletedListJson(@WebParam(name = "json") String json);

	/**
	 * 获取指定某用户的草稿列表
	 * @param json
	 *            :查询参数
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	@WebMethod(operationName = "getMyDraftListJson")
	public String getMyDraftListJson(@WebParam(name = "json") String json);

	/**
	 * 通过表单保存草稿
	 */
	@WebMethod(operationName = "saveDraftByForm")
	public String saveDraftByForm(@WebParam(name = "json") String json) throws Exception;
	
	/**
	 * 根据runId删除草稿
	 * @param runIds
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	@WebMethod(operationName = "delDraftByRunIds")
	public String delDraftByRunIds(@WebParam(name = "runIds") String runIds);
	
	/**
	 * 指定流程的所有节点信息
	 * 
	 * @param defId
	 *            :传入流程ID
	 * @return :String
	 * 
	 *         <pre>
	 * 返回一段JSON格式的字符串，将其转化成json后
	 * json.state：返回结果，-1:失败 ; 0:成功
	 * json.msg：返回结果内容
	 * json.nodeSetList：流程的节点信息，是一个json数组格式的，这里不包含开始和结束节点，是BpmNodeSet对象，用户自行取内容
	 * json.startFlowNode：开始节点，是一个FlowNode对象的json数据
	 * json.endFlowNodeList：结束节点，是一个FlowNode数组对象的json数组数据格式
	 * ps:注意是数据是单个数据还是数组
	 * </pre>
	 * @exception
	 * @since 1.0.0
	 */
	@WebMethod(operationName = "getBpmAllNode")
	public String getBpmAllNode(@WebParam(name = "defId") String defId);

	/**
	 * 保存流程定义节点人员设置
	 * 
	 * @param json
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String saveNode(@WebParam(name = "json") String json);

	
	/**
	 * 获取指定account用户的待办事宜
	 * 
	 * @param account
	 *            :用户account
	 * @param json
	 *            :查询参数
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String getPendingMattersList(@WebParam(name = "account") String account, @WebParam(name = "json") String json);

	/**
	 * 获取指定account用户的转办代理事宜
	 * 
	 * @param account
	 *            :用户account
	 * @param json
	 *            :查询参数
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String getAccordingMattersList(@WebParam(name = "account") String account, @WebParam(name = "json") String json);

	/**
	 * 获取指定account用户的抄送转发事宜
	 * 
	 * @param account
	 *            :用户account
	 * @param json
	 *            :查询参数
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String getProCopyList(@WebParam(name = "account") String account, @WebParam(name = "json") String json);

	/**
	 * 获取指定account用户的加签流转事宜
	 * 
	 * @param account
	 *            :用户account
	 * @param json
	 *            :查询参数
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String getProTransMattersList(@WebParam(name = "account") String account, @WebParam(name = "json") String json);

	/**
	 * 设置代理
	 * 
	 * @param json
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String setAgent(@WebParam(name = "json") String json);

	/**
	 * 获取特定流程信息
	 * 
	 * @param defId
	 *            流程Id
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String getBpmDefinitionByDefId(@WebParam(name = "defId") String defId);

	/**
	 * 
	 * 设置参数
	 * @param defId
	 * @param json
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	String setDefOtherParam(@WebParam(name = "defId") String defId, @WebParam(name = "json") String json);

	/**
	 * 任务转办
	 * 
	 * @param json
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String taskAssign(@WebParam(name = "json") String json);

	/**
	 * 
	 * 流程任务加签
	 * 
	 * @param account
	 *            操作人account
	 * @param json
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String taskCountersign(@WebParam(name = "account") String account, @WebParam(name = "json") String json);

	/**
	 * 
	 * 终止任务
	 * 
	 * @param account
	 *            :操作人account
	 * @param json
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String taskEndProcess(@WebParam(name = "account") String account, @WebParam(name = "json") String json);

	/**
	 * 获取任务的跳转节点
	 * 
	 * @param taskId
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	String getFreeJump(@WebParam(name = "taskId") String taskId);

	/**
	 * 
	 * 撤销流程
	 * @param account	:执行人account
	 * @param json	:参数
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	String defRecover(@WebParam(name = "account") String account, @WebParam(name = "json") String json);
	
	
	/**
	 * 根据流程定义key获取对应的节点
	 * @param defKey
	 * @return json格式  [{nodeId:"",nodeName:"",nodeType:"",preNode:[nodeId1,nodeId2],outNode:[node3,node4]}] 数组
	 * nodeId：节点ID
	 * nodeName:节点名称
	 * nodeType：节点类型
	 * preNode：前置节点
	 * outNode：后置节点
	 */
	String getNodesByDefKey(@WebParam(name = "defKey") String defKey);
	
	
	/**
	 * 启动时添加执行人员
	 * @param json  [{defKey :"defkey",nodeId:"node1",userId:"id1,id2",startTime:"",endTime:""}] 数组格式
	 * defKey：流程定义Key 
	 * nodeId：节点ID
	 * userId：执行人ID（可以多个，逗号分隔）
	 * startTime：开始时间
	 * endTime：结束时间
	 * @return
	 */
	String assignUsers(@WebParam(name = "json") String json);
	
	/**
	 * 获取下一环节处理人
	 * @param taskId 任务ID
	 * @param nodeId 节点ID
	 * @return
	 */
	String getNextTaskUsers(@WebParam(name = "taskId") String taskId, @WebParam(name = "nodeId") String nodeId);
	
	/**
	 * 修改任务的执行人
	 * @param json {taskId:10035820313,assignee:"zhangsan",account:"admin",voteContent:"原处理人已离职",informType:"1,2,3"}
	 * @return
	 */
	String changeAssignee(@WebParam(name = "json") String json);
}
