package com.suneee.platform.service.bpm.util;

import com.suneee.core.bpm.WorkFlowException;
import com.suneee.core.bpm.graph.activiti.ProcessDiagramGenerator;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.bpm.util.BpmConst;
import com.suneee.core.model.TaskExecutor;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.ResultMessage;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.system.GlobalType;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmService;
import net.sf.json.JSONObject;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BPM 函数。
 * 
 * @author hotent
 * 
 */
public class BpmUtil {

	private static final Log logger = LogFactory.getLog(BpmUtil.class);

	/**
	 * 流程变量的前缀名,所有需要放入流程中运行的变量，在请求中的命名方式如：v_name_S或v_createtime_D 变量命名规范:v_变量名_变量类型缩写, 变量类型缩写有如下类型 String:S Date:D Long:L Float:F Double:DB BigDecimal:BD Integer:I Short:SH
	 */
	private final static String VAR_PRE_NAME = "v_";

	

	

	public static ProcessCmd getProcessCmd(JSONObject jsonObject) {
		// taskId
		ProcessCmd cmd = new ProcessCmd();

		String temp = jsonObject.get("taskId")!=null?jsonObject.getString("taskId"):"";
		if (StringUtil.isNotEmpty(temp)) {
			cmd.setTaskId(temp);
		}

		// 添加表单数据
		temp = jsonObject.get("formData")!=null?jsonObject.getString("formData"):"";
		if (StringUtil.isNotEmpty(temp)) {
			cmd.setFormData(temp);
		}

//		Map paraMap = RequestUtil.getParameterValueMap(request, false, false);
//		cmd.setFormDataMap(paraMap);

		BpmDefinitionService bpmDefinitionService = (BpmDefinitionService) AppUtil.getBean(BpmDefinitionService.class);
		BpmDefinition bpmDefinition = null;

		temp = jsonObject.get("actDefId")!=null?jsonObject.getString("actDefId"):"";
		if (StringUtil.isNotEmpty(temp)) {
			cmd.setActDefId(temp);
			bpmDefinition = (BpmDefinition) bpmDefinitionService.getByActDefId(temp);
		} else {
			temp = jsonObject.get("flowKey")!=null?jsonObject.getString("flowKey"):"";
			if (StringUtil.isNotEmpty(temp)) {
				cmd.setFlowKey(temp);
				bpmDefinition = bpmDefinitionService.getMainDefByActDefKey(temp);
			}
		}

		cmd.addTransientVar(BpmConst.BPM_DEF, bpmDefinition);

		if (BeanUtils.isNotEmpty(bpmDefinition)) {
			String informType = "";
			informType = bpmDefinition.getInformType();
			cmd.setInformType(informType);
		}

		temp = jsonObject.get("destTask")!=null?jsonObject.getString("destTask"):"";
		if (StringUtil.isNotEmpty(temp)) {
			cmd.setDestTask(temp);
		}
		// 可以根据主键启动流程。
		temp = jsonObject.get("businessKey")!=null?jsonObject.getString("businessKey"):"";
		if (StringUtil.isNotEmpty(temp)) {
			cmd.setBusinessKey(temp);
		}


		// 退回
		temp = jsonObject.get("back")!=null?jsonObject.getString("back"):"";
		if (StringUtil.isNotEmpty(temp)) {
			Integer rtn = Integer.parseInt(temp);
			cmd.setBack(rtn);
		}

		cmd.setVoteContent(jsonObject.get("voteContent")!=null?jsonObject.getString("voteContent"):"");

		temp = jsonObject.get("stackId")!=null?jsonObject.getString("stackId"):"";
		if (StringUtils.isNotEmpty(temp)) {
			cmd.setStackId(new Long(temp));
		}
		temp = jsonObject.get("voteAgree")!=null?jsonObject.getString("voteAgree"):"";
		if (StringUtil.isNotEmpty(temp)) {
			cmd.setVoteAgree(new Short(temp));
		}

		

		temp = jsonObject.get("isManage")!=null?jsonObject.getString("isManage"):"";
		if (StringUtil.isNotEmpty(temp)) {
			cmd.setIsManage(new Short(temp));
		}

		// 流程启动设置下一步执行人。
		temp = jsonObject.get("_executors_")!=null?jsonObject.getString("_executors_"):"";

		if (StringUtil.isNotEmpty(temp)) {
			List<TaskExecutor> executorList = com.suneee.core.bpm.util.BpmUtil.getTaskExecutors(temp);
			cmd.setTaskExecutors(executorList);
		}
		// 设置起始节点选择的下一个节点
		temp = jsonObject.get("startNode")!=null?jsonObject.getString("startNode"):"";
		if (StringUtil.isNotEmpty(temp)) {
			cmd.setStartNode(temp);
		}
		// 设置关联运行Id。
		Long relRunId =jsonObject.get("relRunId")!=null?jsonObject.getLong("relRunId"):0L;
		cmd.setRelRunId(relRunId);

		return cmd;
	}



	

	/**
	 * 根据deployId生成流程图。
	 * 
	 * @param deployId
	 *            流程deployid
	 * @param fileName
	 *            文件路径
	 * @param repositoryService
	 * @throws IOException
	 */
	public static ResultMessage genImageByDepolyId(String deployId, String fileName, RepositoryService repositoryService) throws IOException {
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
		ProcessDefinition def = query.deploymentId(deployId).singleResult();
		if (def == null) {
			ResultMessage result = new ResultMessage();
			result.setMessage("没有找到流程定义!");
			result.setResult(ResultMessage.Fail);
			return result;
		}
		String defId = def.getId();
		return genImageByDefId(defId, fileName, repositoryService);
	}

	/**
	 * 根据deployId生成生成节点可以高亮的流程图。
	 * 
	 * @param deployId
	 *            流程deployid
	 * @param fileName
	 *            图片文件路径
	 * @param repositoryService
	 * @param activitys
	 *            高亮节点名称
	 * @return
	 * @throws IOException
	 */
	public static ResultMessage genImageByDepolyId(String deployId, String fileName, RepositoryService repositoryService, String... activitys) throws IOException {
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
		ProcessDefinition def = query.deploymentId(deployId).singleResult();
		if (def == null) {
			ResultMessage result = new ResultMessage();
			result.setMessage("没有找到流程定义!");
			result.setResult(ResultMessage.Fail);
			return result;
		}
		String defId = def.getId();
		return genImageByDefId(defId, fileName, repositoryService, activitys);
	}

	/**
	 * 根据流程定义ID生成图片。
	 * 
	 * @param actDefId
	 *            流程定义ID。
	 * @param fileName
	 *            生成图片文件的名称。
	 * @param repositoryService
	 * @throws IOException
	 */
	public static ResultMessage genImageByDefId(String actDefId, String fileName, RepositoryService repositoryService) throws IOException {
		ResultMessage result = new ResultMessage();
	

		BpmService bpmService = (BpmService) AppUtil.getBean(BpmService.class);
		String bpmXml = bpmService.getDefXmlByProcessDefinitionId(actDefId);
		if (bpmXml == null) {
			result.setMessage("没有找到对应的流程定义!");
			result.setResult(ResultMessage.Fail);
			return result;
		}
		InputStream is = ProcessDiagramGenerator.generatePngDiagram(bpmXml);
		FileUtil.writeFile(fileName, is);

		result.setMessage("成功生成流程定义!");
		result.setResult(ResultMessage.Success);

		return result;
	}

	/**
	 * 根据流程定义ID生成节点可以高亮的流程图
	 * 
	 * @param defId
	 *            流程定义id
	 * @param fileName
	 *            生成的路径
	 * @param repositoryService
	 * @param activitys
	 *            指定节点需要高亮节点名称
	 * @return
	 * @throws IOException
	 */
	public static ResultMessage genImageByDefId(String defId, String fileName, RepositoryService repositoryService, String... activitys) throws IOException {

		ResultMessage result = new ResultMessage();
	

		BpmService bpmService = (BpmService) AppUtil.getBean(BpmService.class);
		String bpmXml = bpmService.getDefXmlByProcessDefinitionId(defId);
		if (bpmXml == null) {
			result.setMessage("没有找到对应的流程定义!");
			result.setResult(ResultMessage.Fail);
			return result;
		}

		List<String> list = new ArrayList<String>();
		for (String node : activitys) {
			list.add(node);
		}

		InputStream is = ProcessDiagramGenerator.generateDiagram(bpmXml, "png", list);
		FileUtil.writeFile(fileName, is);

		result.setMessage("成功生成流程定义!");
		result.setResult(ResultMessage.Success);

		return result;
	}
	/**
	 * 启动新流程（线程安全 主线程等待启动流程线程成功完成。）启动后无法回滚
	 * @param newFlowCmd 		
	 * 	newFlowCmd.setFormData(newFlowFormData);
		@required：newFlowCmd.setFlowKey(newFlowKey);
	 * @param startUser
	 * @return 
	 */
	public static ProcessRun startNewFlow(ProcessCmd newFlowCmd, SysUser startUser){
		StartNewFlow triggerNewFlow = AppUtil.getBean(StartNewFlow.class);
		ExecutorService executor = Executors.newCachedThreadPool();  
		
		try {
			CountDownLatch latch = new CountDownLatch(1);  
			triggerNewFlow.setLatch(latch);
			triggerNewFlow.setUser(startUser);
			newFlowCmd.getVariables().put("triggerFirstStep",true);
			triggerNewFlow.setCmd(newFlowCmd);
			
			//触发新线程处理流程 
			executor.execute(triggerNewFlow);
			//当前主线程等待触发新流程走完。
			//解决触发子流程死锁问题,by子华
//			latch.await();
			//如果新流程失败，则异常不为空。
			if(triggerNewFlow.getException() != null)throw new RuntimeException(triggerNewFlow.getException());
			return triggerNewFlow.getProcessRun();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkFlowException("触发新流程失败 ！ ："+ e.getMessage());
		}finally{
			executor.shutdown();
		}
	}
	
	/**
	 * 根据流程定义ID获取流程定义KEY。
	 * @param actDefId
	 * @return
	 */
	public static String getDefKeyByActDefId(String actDefId){
		String[] aryTemp=actDefId.split(":");
		return aryTemp[0];
	}

	/**
	 * 获取分类Id集合
	 * @param typeList
	 * @return
	 */
	public static Set<Long> getTypeIdList(List<GlobalType> typeList){
		Set<Long> typeIdList=new HashSet<Long>();
		for (GlobalType type:typeList){
			typeIdList.add(type.getTypeId());
		}
        if (typeIdList.size()==0){
            typeIdList.add(0L);
        }
		return typeIdList;
	}

	/**
	 * 分类ID过滤器，
	 * 如果为空就默认加一个值为0
	 */
	public static void typeIdFilter(List<Long> typeIdList){
		if (typeIdList.size()==0){
			typeIdList.add(0L);
		}
	}

}
