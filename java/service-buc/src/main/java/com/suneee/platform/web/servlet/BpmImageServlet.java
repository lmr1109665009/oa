package com.suneee.platform.web.servlet;

import com.suneee.core.bpm.graph.activiti.ProcessDiagramGenerator;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.util.RequestUtil;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.bpm.IFlowStatus;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.core.bpm.graph.activiti.ProcessDiagramGenerator;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.web.util.RequestUtil;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * 产生流程图的servlet。<br>
 * 
 * <pre>
 * 要求传入名称为deployId参数。
 * 在web.xml中配置：
 * &lt;servlet>
 *       &lt;servlet-name>bpmImageServlet&lt;/servlet-name>
 *       &lt;servlet-class>com.suneee.core.bpm.servlet.BpmImageServlet&lt;/servlet-class>
 *    &lt;/servlet>
 * &lt;servlet-mapping>
 *   	&lt;servlet-name>bpmImageServlet&lt;/servlet-name>
 *   	&lt;url-pattern>/bpmImage&lt;/url-pattern>
 *   &lt;/servlet-mapping>
 *   
 *   页面使用方法如下：
 *   &lt;img src="${ctx}/bpmImage?deployId=**" />
 *   &lt;img src="${ctx}/bpmImage?taskId=**" />
 *   &lt;img src="${ctx}/bpmImage?processInstanceId=**" />
 *   &lt;img src="${ctx}/bpmImage?definitionId=**" />
 * </pre>
 * 
 * @author csx.
 * 
 */
@SuppressWarnings("serial")
public class BpmImageServlet extends HttpServlet {
	private BpmService bpmService = (BpmService) AppUtil.getBean("bpmService");
	private BpmDefinitionService bpmDefinitionService= (BpmDefinitionService) AppUtil.getBean("bpmDefinitionService");
	private IFlowStatus iFlowStatus = (IFlowStatus) AppUtil.getBean("iFlowStatus");
	public static final String DIAGRAM_DIR="/diagrams";
	public static final String DEFAULT_FILENAME="default";

	@Override
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		String deployId = RequestUtil.getString(request, "deployId");
		String taskId = RequestUtil.getString(request, "taskId");
		String processInstanceId = RequestUtil.getString(request,"processInstanceId");
		String definitionId = RequestUtil.getString(request, "definitionId");
		Long defId=RequestUtil.getLong(request,"defId");
		String runId = request.getParameter("runId");
		String download=request.getParameter("download");
		// 生成图片
		InputStream is = null;
		//根据流程deployId产生图片
		if (StringUtil.isNotEmpty(deployId)) {
			is=getInputStreamByDeployId(deployId);
		}
		//流程定义id
		else if (StringUtils.isNotEmpty(definitionId)) {
			BpmDefinition bpmDefinition=bpmDefinitionService.getByActDefId(definitionId);
			is=getInputStreamByDefinitionId(bpmDefinition.getDefId().toString(),definitionId);
		}
		//流程任务id
		else if (StringUtil.isNotEmpty(taskId)) {
			is=getInputStreamByTaskId(taskId);
		}
		//流程实例ID
		else if (StringUtils.isNotEmpty(processInstanceId)) {
			is=getInputStreamByProcessInstanceId(processInstanceId);
		}
		//流程运行id
		else if (StringUtils.isNotEmpty(runId)) {
			is=getInputStreamByRunId(runId);
		}

		if (is != null) {
			if(StringUtil.isEmpty(download)){
				response.setContentType("image/png");
			}
			else{
				response.setContentType("APPLICATION/OCTET-STREAM");
				String fileName="flowImage.png";
				if(StringUtil.isNotEmpty(definitionId)){
					BpmDefinitionService bpmDefinitionService=AppUtil.getBean(BpmDefinitionService.class);
					BpmDefinition bpmDefinition= bpmDefinitionService.getByActDefId( definitionId);
					fileName=bpmDefinition.getDefKey() +".png";
				}
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);  
			}
			OutputStream out = response.getOutputStream();

			try {
				byte[] bs = new byte[1024];
				int n = 0;
				while ((n = is.read(bs)) != -1) {
					out.write(bs, 0, n);
				}
				out.flush();
			} catch (Exception ex) {
			} finally {
				is.close();
				out.close();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	/**
	 * 根据deployId来获取输入流
	 * @param deployId
	 * @return
	 */
	private InputStream getInputStreamByDeployId(String deployId){
		String actInstanceId=bpmService.getActDefIdByDeployId(deployId);
		BpmDefinition bpmDef=bpmDefinitionService.getByActDefId(actInstanceId);
		InputStream is=readDiagramImage(bpmDef.getDefId().toString(),DEFAULT_FILENAME);
		if (is!=null){
			return is;
		}
		String bpmnXml = bpmService.getDefXmlByDeployId(deployId);
		is = ProcessDiagramGenerator.generatePngDiagram(bpmnXml);
		saveFlowDiagram(is,bpmDef.getDefId().toString(),DEFAULT_FILENAME);
		return is;
	}
	/**
	 * 根据definitionId来获取输入流
	 * @param definitionId
	 * @return
	 */
	private InputStream getInputStreamByDefinitionId(String definitionId,String actInstanceId){
		InputStream is=readDiagramImage(definitionId,DEFAULT_FILENAME);
		if (is!=null){
			return is;
		}
		String bpmnXml = bpmService.getDefXmlByProcessDefinitionId(actInstanceId);
		is = ProcessDiagramGenerator.generatePngDiagram(bpmnXml);
		saveFlowDiagram(is,definitionId,DEFAULT_FILENAME);
		return is;
	}
	/**
	 * 根据taskId来获取输入流
	 * @param taskId
	 * @return
	 */
	private InputStream getInputStreamByTaskId(String taskId){
		TaskEntity taskEntity = bpmService.getTask(taskId);
		String definitionId=taskEntity.getProcessDefinitionId();
		Map<String,String> highLightList = iFlowStatus.getStatusByInstanceId(new Long( taskEntity.getProcessInstanceId()));
		String highLightKey= com.suneee.ucp.base.util.StringUtils.computeHighLightKey(highLightList);
		InputStream is=readDiagramImage(definitionId,highLightKey);
		if (is!=null){
			return is;
		}
		String bpmnXml = bpmService.getDefXmlByProcessTaskId(taskId);
		is = ProcessDiagramGenerator.generateDiagram(bpmnXml,highLightList, "png");
		saveFlowDiagram(is,definitionId,highLightKey);
		return is;
	}
	/**
	 * 根据processInstanceId来获取输入流
	 * @param processInstanceId
	 * @return
	 */
	private InputStream getInputStreamByProcessInstanceId(String processInstanceId){
		IFlowStatus flowStatus=(IFlowStatus) AppUtil.getBean(IFlowStatus.class);
		Map<String,String>  highLightMap = flowStatus.getStatusByInstanceId(Long.parseLong(processInstanceId));
		String highLightKey= com.suneee.ucp.base.util.StringUtils.computeHighLightKey(highLightMap);
		ProcessRunService processRunService = (ProcessRunService) AppUtil.getBean(ProcessRunService.class);
		ProcessRun processRun = processRunService.getByActInstanceId(new Long(processInstanceId));
		InputStream is=null;
		String definitionId=String.valueOf(processRun.getDefId());
		is=readDiagramImage(definitionId,highLightKey);
		if (is!=null){
			return is;
		}
		String bpmnXml = bpmService.getDefXmlByProcessProcessInanceId(processInstanceId);
		if (bpmnXml == null) {
			bpmnXml = bpmService.getDefXmlByDeployId(processRun.getActDefId());
		}
		is = ProcessDiagramGenerator.generateDiagram(bpmnXml,highLightMap, "png");
		saveFlowDiagram(is,definitionId,highLightKey);
		return is;
	}
	/**
	 * 根据runId来获取输入流
	 * @param runId
	 * @return
	 */
	private InputStream getInputStreamByRunId(String runId){
		ProcessRunService processRunService = (ProcessRunService) AppUtil.getBean(ProcessRunService.class);
		ProcessRun processRun = processRunService.getById(new Long(runId));
		String processInstanceId=processRun.getActInstId();
		String definitionId=bpmService.getProcessInstance(processInstanceId).getProcessDefinitionId();
		IFlowStatus flowStatus=(IFlowStatus) AppUtil.getBean(IFlowStatus.class);
		Map<String,String>  highLightMap = flowStatus.getStatusByInstanceId(Long.parseLong(processInstanceId));
		String highLightKey= com.suneee.ucp.base.util.StringUtils.computeHighLightKey(highLightMap);
		InputStream is=readDiagramImage(definitionId,highLightKey);
		if (is!=null){
			return is;
		}
		String bpmnXml = bpmService.getDefXmlByProcessProcessInanceId(processRun.getActInstId());
		if (bpmnXml == null) {
			bpmnXml = bpmService.getDefXmlByDeployId(processRun.getActDefId());
		}
		is = ProcessDiagramGenerator.generateDiagram(bpmnXml,highLightMap, "png");
		saveFlowDiagram(is,definitionId,highLightKey);
		return is;
	}

	private void saveFlowDiagram(InputStream is,String defId,String filename){
		File dir=new File(AppUtil.getRealPath(DIAGRAM_DIR+File.separator+defId));
		if (!dir.exists()){
			dir.mkdirs();
		}
		filename=filename.replace(":","_")+".png";
		try {
			File destFile=new File(dir,filename);
			writeFile(destFile.getAbsolutePath(),is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取服务器上流程图片文件
	 * @param defId
	 */
	private InputStream readDiagramImage(String defId,String filename){
		InputStream is=null;
		File dir=new File(AppUtil.getRealPath(DIAGRAM_DIR+File.separator+defId));
		filename=filename+".png";
		File destFile=new File(dir,filename);
		if (!destFile.exists()){
			return null;
		}
		try {
			is=new FileInputStream(destFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return is;
	}

	/**
	 * 生成图片文件并保存在服务器上
	 * @param fileName
	 * @param is
	 * @throws IOException
	 */
	public static void writeFile(String fileName, InputStream is)
			throws IOException {
		FileOutputStream fos;
		if(!FileUtil.isExistFile(fileName)){
			boolean createFile = FileUtil.createFile(fileName);
			if(!createFile){
				throw new IOException("创建文件失败");
			}
		}
		fos = new FileOutputStream(fileName);
		byte[] bs = new byte[512];
		int n = 0;
		while ((n = is.read(bs)) != -1) {
			fos.write(bs, 0, n);
		}
		fos.close();
		is.reset();
	}


}
