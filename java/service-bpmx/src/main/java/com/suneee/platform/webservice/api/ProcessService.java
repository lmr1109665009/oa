package com.suneee.platform.webservice.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;

/**
 * 流程操作Web服务对外接口类
 * 
 * @author csx
 * 
 */
@SOAPBinding(style = Style.DOCUMENT, parameterStyle = ParameterStyle.WRAPPED)
@WebService(endpointInterface = "com.suneee.platform.webservice.api.ProcessService", targetNamespace = "http://impl.webservice.platform.hotent.com/")
public interface ProcessService {
	/**
	 * 增加会签人员
	 * 
	 * @param signUserIds
	 *            会签人员id，多个人员之间以逗号分隔
	 * @param taskId
	 *            其中一个会签任务的id
	 * @return <result result="true" message="成功增加会签人员"/>
	 */
	String addSignUsers(@WebParam(name = "signUserIds") String signUserIds, @WebParam(name = "taskId") String taskId);

	/**
	 * 是否允许出现选择人员
	 * 
	 * @param taskId
	 * @return
	 */
	String canSelectedUser(@WebParam(name = "taskId") String taskId);

	/**
	 * 客户端提交xml,执行流程往下跳转
	 * 
	 * <pre>
	 * 流程参数包括：
	 * taskId：任务ID 不能为空。
	 * userAccount：必须填写。
	 * voteAgree：1=同意 2=反对 0=弃权 3=退回  若设置为值，流程回退
	 * voteContent：备注信息
	 * 流程变量数可以是0..*个。
	 * 流程变量的数据类型为 int,long,double,date,string。
	 * &lt;req taskId="" account="" voteAgree="" voteContent=""  businessKey="" >
	 * 		&lt;var varName="" varVal="" varType="int,long,double,date,string" dateFormat="yyyy-MM-dd HH:mm:ss" />
	 * 		&lt;formData>&lt;/formData>
	 * &lt;/req> 。
	 * 
	 * 执行成功调用doNext方法进行下一步审批:
	 * @param xml
	 * @return
	 */
	@WebMethod
	String doNext(@WebParam(name = "xml") String xml);

	/**
	 * 按任务ID结束流程实例
	 * 
	 * @param taskId
	 *            当前任务Id
	 * @return
	 */
	@WebMethod
	String endProcessByTaskId(@WebParam(name = "taskId") String taskId);

	/**
	 * 根据taskId获取任务预先设置的审批用语列表
	 * 
	 * <pre>
	 * 参数包括：
	 * taskId：任务Id，必填。
	 * </pre>
	 * 
	 * @return xml(审批用语列表)
	 */
	String getApprovalItems(@WebParam(name = "taskId") String taskId);

	/**
	 * 根据用户账号获取流程定义列表
	 * 
	 * <pre>
	 * 获取流程定义列表的参数：
	 * account：用户账号
	 * pageSize：每页记录数
	 * currentPage：当前页
	 * &lt;req account=""  pageSize="" currentPage="" >
	 * &lt;/req> 。
	 * </pre>
	 * 
	 * @return：xml(流程定义列表)
	 * */
	String getBpmDefinition(@WebParam(name = "xml") String xml);

	/**
	 * 通过businessKey获取运行实例
	 * 
	 * @param businessKey
	 * @return
	 */
	String getByBusinessKey(@WebParam(name = "businessKey") String businessKey);

	/**
	 * 取到当前任务的某个任务对应的所有执行人员列表
	 * 
	 * @param taskId
	 *            当前任务id
	 * @param destNodeId
	 *            目标节点Id
	 * @return
	 */
	String getDestNodeHandleUsers(@WebParam(name = "taskId") String taskId, @WebParam(name = "destNodeId") String destNodeId);

	/**
	 * 取得某用户办完的任务
	 * 
	 * <pre>
	 * 待办任务参数包括：
	 * userId：用户ID。
	 * subject:任务标题 。
	 * taskName：任务名称。
	 * pagesize：页数
	 * currentPage：当前页
	 * &lt;req account="" taskName="" subject="" pageSize="" currentPage="">
	 * &lt;/req> 。
	 * </pre>
	 * 
	 * @return：xml(办完任务列表)。
	 */
	String getFinishTask(@WebParam(name = "xml") String xml);

	/**
	 * 返回某个任务审批的明细表单地址
	 * 
	 * @param actInstId
	 * @param nodeKey
	 * @return
	 */
	String getFinishTaskDetailUrl(@WebParam(name = "actInstId") String actInstId, @WebParam(name = "nodeKey") String nodeKey);

	/**
	 * 根据用户账号取得流程实例列表
	 * 
	 * <pre>
	 * 参数包括：
	 * account：用户账号
	 * subject 任务标题。
	 * status 流程实例状态  1=正在运行 ,2=运行结束。
	 * pageSize：页数。
	 * currentPage：当前页。
	 * &lt;req account="" subject="" status=""　pageSize="" currentPage="">
	 * &lt;/req> 。
	 * </pre>
	 * 
	 * @return：xml(某用户的流程实例列表)。
	 */
	String getMyProcessRunByXml(@WebParam(name = "xml") String xml);

	/**
	 * 获取当前任务的后续跳转节点
	 * 
	 * @param taskId
	 * @return
	 */
	String getNextFlowNodes(@WebParam(name = "taskId") String taskId, @WebParam(name = "account") String account);

	/**
	 * 按Activiti流程实例Id取得某个流程的审批历史
	 * 
	 * @param actInstId
	 * @return
	 */
	String getProcessOpinionByActInstId(@WebParam(name = "actInstId") String actInstId);

	/**
	 * 按运行实例Id取得某个流程的审批历史
	 * 
	 * @param runId
	 *            流程运行实例表BPM_PRO_RUN表主键
	 * @return
	 */
	String getProcessOpinionByRunId(@WebParam(name = "runId") String runId);

	/**
	 * 查询流程运行实例
	 * 
	 * <pre>
	 * 待办任务参数包括：
	 * defId:流程定义Id，actDefId：act流程定义ID，defKey：流程定义Key，三者必填一个。
	 * pageSize：页数
	 * currentPage：当前页
	 * &lt;req defId="" actDefId="" defKey="" pageSize="" currentPage="" >
	 * &lt;/req> 。
	 * </pre>
	 * 
	 * @return：xml(流程运行对象列表)。
	 * */
	String getProcessRun(@WebParam(name = "xml") String xml);

	/**
	 * 通过runId取得流程运行实例
	 * 
	 * @param runId
	 * @return
	 */
	String getProcessRunByRunId(@WebParam(name = "runId") String runId);

	/**
	 * 根据流程taskId获取对应的流程运行对象
	 * 
	 * <pre>
	 * 参数包括：
	 * taskId:任务Id，必填。
	 * </pre>
	 * 
	 * @return：xml(流程运行对象)。
	 * */
	String getProcessRunByTaskId(@WebParam(name = "taskId") String taskId);

	/**
	 * 根据runId和taskId获取任务的状态
	 * 
	 * @param runId
	 * @param taskId
	 * @return
	 */
	String getStatusByRunidNodeId(@WebParam(name = "runId") String runId, @WebParam(name = "nodeId") String nodeId);

	/**
	 * 根据任务ID获取流程任务实例
	 * 
	 * <pre>
	 * 参数包括：
	 * @param taskId：流程任务Id
	 * </pre>
	 * 
	 * @return xml(流程任务实例)。
	 **/
	String getTaskByTaskId(@WebParam(name = "taskId") String taskId);

	/**
	 * 返回用户任务对应的导航URL
	 * 
	 * @param taskId
	 * @return
	 */
	String getTaskFormUrl(@WebParam(name = "taskId") String taskId);

	/**
	 * 根据taskid获取taskName
	 * 
	 * @param taskId
	 * @return
	 */
	String getTaskNameByTaskId(@WebParam(name = "taskId") String taskId);

	/**
	 * 根据taskId获取当前任务节点信息
	 * 
	 * <pre>
	 * 参数包括：
	 * @param taskId：任务Id，必填。
	 * </pre>
	 * 
	 * @return xml(任务节点信息)。
	 */
	String getTaskNode(@WebParam(name = "taskId") String taskId);

	/**
	 * 通过taskId获取当前任务节点的后续节点
	 * 
	 * <pre>
	 * 参数包括：
	 * @param taskId 任务Id，必填。
	 * </pre>
	 * 
	 * @return xml(节点信息列表)。
	 */
	String getTaskOutNodes(@WebParam(name = "taskId") String taskId);

	/**
	 * 通过用户账号获取用户待办事项
	 * 
	 * <pre>
	 * &lt;req account="" taskNodeName="" subject="" processName="" orderField="" orderSeq="asc" pageSize="" currentPage="">
	 * &lt;/req>
	 * </pre>
	 * 
	 * @param xml
	 * @return
	 */
	String getTasksByAccount(@WebParam(name = "xml") String xml);

	/**
	 * 取得某个运行流程实例对应的任务列表
	 * 
	 * @param runId
	 * @return
	 */
	String getTasksByRunId(@WebParam(name = "runId") String runId);

	/**
	 * 根据runId获取流程变量
	 * 
	 * <pre>
	 * 参数包括：
	 * @param runId：运行实例Id。
	 * </pre>
	 * 
	 * @return xml(流程变量)。
	 */
	String getVariablesByRunId(@WebParam(name = "runId") String runId);

	/**
	 * 通过taskId获取该流程的变量
	 * 
	 * @param taskId
	 * @return
	 */
	String getVariablesByTaskId(@WebParam(name = "taskId") String taskId);

	/**
	 * 是否允许补签
	 * 
	 * @param account
	 * @param taskId
	 * @return
	 */
	String isAllowAddSign(@WebParam(name = "account") String account, @WebParam(name = "taskId") String taskId);

	/**
	 * 任务是否允许回退
	 * 
	 * @param taskId
	 * @return
	 */
	String isAllowBack(@WebParam(name = "taskId") String taskId);

	/**
	 * 获取该任务是否可以选择路径跳转
	 * 
	 * @param taskId
	 * @return
	 */
	String isSelectPath(@WebParam(name = "taskId") String taskId);
	
	/**
	 * 为某流程任务设置一组流程变量
	 * 
	 * <pre>
	 * 参数包括：
	 * @param runId 当前流程实例
	 * @param vars，流程变量 
	 * &lt;req runId="">
	 * &lt;var varName="" varVal="" varType="int,long,double,date,string" dateFormat="yyyy-MM-dd HH:mm:ss" />
	 * &lt;/req>。
	 * </pre>
	 */
	String setVariablesByRunId(@WebParam(name = "xml") String xml);
	
	/**
	 * 为某流程任务设置一组流程变量
	 * 
	 * <pre>
	 * 参数包括：
	 * @param taskId 当前任务ID
	 * @param vars，流程变量 
	 * &lt;req taskId="">
	 * &lt;var varName="" varVal="" varType="int,long,double,date,string" dateFormat="yyyy-MM-dd HH:mm:ss" />
	 * &lt;/req>。
	 * </pre>
	 */
	String setTaskVars(@WebParam(name = "xml") String xml);

	/**
	 * 客户端提交xml启动流程
	 * 
	 * <pre>
	 * 流程参数包括：
	 * actDefId：流程定义ID flowKey:流程key，两个参数可任意定义一个。
	 * startUser：必须填写。
	 * businessKey: 可以指定业务主键。
	 * 节点data：启动流程的自定义表单的数据。
	 * 流程变量数可以是0..*个。
	 * 流程变量的数据类型为 int,long,double,date,string。
	 * 构建的xml参数如下所示
	 * &lt;req actDefId="" flowKey="" subject="" account="" businessKey="">
	 *  &lt;data>
	 *  &lt;![CDATA[
	 *  {"main": {"fields": {"orderNo": "","orderName": ""}},
	 *  "sub": [{"tableName": "spmx",
	 * 	      "fields": [{"xj": "","ID": ""}]}],
	 *  "opinion": []}
	 *  ]]>
	 *  &lt;/data>
	 *  &lt;var varName="" varVal="" varType="int,long,double,date,string" dateFormat="yyyy-MM-dd HH:mm:ss" />
	 * &lt;/req>
	 * 
	 * 启动成功返回:
	 * &lt;result result="true" subject='' runId='' instanceId=''/>
	 * subject为主题。
	 * runId 为运行id和流程实例Id一一对应。
	 * instanceId：流程实例Id。
	 * 失败返回：
	 * &lt;result result="flase" message=''/>
	 * message:启动失败消息。
	 * </pre>
	 * 
	 * @param xml
	 * @return
	 */
	String start(@WebParam(name = "xml") String xml);
}