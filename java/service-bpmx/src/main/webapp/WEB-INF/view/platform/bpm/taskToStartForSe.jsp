<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="f" uri="http://www.jee-soft.cn/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@include file="/commons/include/html_doctype.html"%>
<%@page import="com.suneee.core.api.util.PropertyUtil"%>
<html>
	<head>
		<title>流程任务-[${task.name}]执行</title>
		<%@include file="/commons/include/customForm.jsp" %>
		<%@include file="/commons/include/ueditor.jsp" %>
		<f:link href="model.css" ></f:link>
		<link rel="stylesheet" type="text/css" href="${ctx}/styles/default/css/hotent/task.css"></link>
		<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/TaskAddSignWindow.js"></script>
		<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/TaskBackWindow.js"></script>
		<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/TaskImageUserDialog.js"></script>
		<script type="text/javascript" src="${ctx}/js/hotent/platform/tabOperator.js"></script>
		<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/BpmTaskExeAssignDialog.js"></script>
		<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/BpmRetransmissionDialog.js"></script>
		<script type="text/javascript">
		
		var taskId="${task.id}";
		var nodeId = "${task.taskDefinitionKey}";
		var nodeName = "${task.name}";
		var runId="${processRun.runId}";
		var isExtForm=${isExtForm};
		var isEmptyForm=${isEmptyForm};
		var isSignTask=${isSignTask};
		var isHidePath=${isHidePath};
		var isManage=${isManage};
		var isNeedSubmitConfirm=${bpmDefinition.submitConfirm==1};
		var isHandChoolse=${isHandChoolse};
		var bpmGangedSets=[];
		var dialog = frameElement.dialog;
		
		var form;
		//操作类型
		//1.完成任务
		//2.保存数据
		var operatorType=1;
		
		
		function isTaskEnd(callBack){
			var url="${ctx}/platform/bpm/task/isTaskExsit.ht";
			var params={taskId:"${task.id}"};
			
			$.post(url,params,function(responseText){
				var obj=new com.hotent.form.ResultMessage(responseText);
				if(obj.isSuccess()){
					callBack.apply(this);
				}
				else{
					$.ligerDialog.warn("这个任务已经完成或被终止了!",'提示');
				}
			});
		}
		
		function handFormJson(json){}
		
		function submitForm(action,button){
		
			var ignoreRequired=false;
			if(button=="#btnSave" || button=="#btnReject" || button=="#btnRejectToStart"){
				ignoreRequired=true;
			}
			
			if($(button).hasClass("disabled"))return;
			if(isEmptyForm){
				$.ligerDialog.error("还没有设置表单!",'提示信息');
				return;
			}
			
			var frmWorkFlow=$('#frmWorkFlow');
			frmWorkFlow.attr("action",action);
			if(isExtForm){
				var rtn = true;
				if(button!="a.save"){
					rtn=form.valid()
				}
				if(!rtn) return;
				
				$(button).addClass("disabled");
				frmWorkFlow.ajaxForm({success:showResponse }); //terry add
				if(frmWorkFlow.handleFieldName){//url表单清除命名
					frmWorkFlow.handleFieldName();
				}
				if(frmWorkFlow.setData){
					frmWorkFlow.setData();
				}
				frmWorkFlow.submit();
				
			}else{
				var rtn=CustomForm.validate({ignoreRequired:ignoreRequired});
				if(!rtn){
					$.ligerDialog.warn("表单验证不成功,请检查表单是否正确填写!","提示信息");
					return;
				}
		
				if(button!="#btnSave" || button=="#btnReject" || button=="#btnRejectToStart" ){
					//子表必填检查(兼容新旧版本)					
					rtn = SubtablePermission.subRequired();
					if(!rtn){	
						$.ligerDialog.warn("请填写子表表单数据！","提示信息");
						return;
					}
				}
				
				var data=CustomForm.getData();
				//WebSign控件提交。 有控件时才提交   xcx
				if(WebSignPlugin.hasWebSignField){
					WebSignPlugin.submit();
				}				
				
				$(button).addClass("disabled");
				
				var uaName=navigator.userAgent.toLowerCase();				
				if(uaName.indexOf("firefox")>=0||uaName.indexOf("chrome")>=0){  //异步处理
					//Office控件提交。 有可以提交的文档
					if(OfficePlugin.submitNum>0){
						OfficePlugin.submit();             //火狐和谷歌 的文档提交包括了  业务提交代码部分（完成  OfficePlugin.submit()后面的回调 函数 有 业务提交代码），所以 后面就不用加上业务提交代码
					}else{   //没有可提交的文档时 直接做 业务提交代码
						data=CustomForm.getData();
						//设置表单数据
						$("#formData").val(data);
						FormSubmitUtil.submitFormAjax(frmWorkFlow,showResponse,handFormJson);
					}					
				}else{   //同步处理
					//Office控件提交。 有可以提交的文档
					if(OfficePlugin.submitNum>0){
						OfficePlugin.submit();
						//当提交问题 等于 提交数量的变量 时 表示所有文档 都提交了  然后做 业务相关的提交
						if(OfficePlugin.submitNum == OfficePlugin.submitNewNum){    
							//获取自定义表单的数据
							data=CustomForm.getData();
							//设置表单数据
							$("#formData").val(data);
							
							FormSubmitUtil.submitFormAjax(frmWorkFlow,showResponse,handFormJson);
							OfficePlugin.submitNewNum = 0; //重置  提交数量的变量
						}else{
							$.ligerDialog.warn("提交失败,OFFICE控件没能正常使用，请重新安装 ！！！","提示");
						}
					}else{
						//获取自定义表单的数据
						data=CustomForm.getData();
						//设置表单数据
						$("#formData").val(data);
						
						FormSubmitUtil.submitFormAjax(frmWorkFlow,showResponse,handFormJson);
					}		
				}
				
			}
		}
		
		function saveData(){
			var operatorObj=getByOperatorType();
			var button="#" +operatorObj.btnId;
			
			var rtn=beforeClick(operatorType);
			
			if( rtn==false){
				return;
			}
			if(isExtForm){//提交第三方表单时检查该表单的参数
				var frm=$('#frmWorkFlow');
				if(!frm.valid()) return ;
				if(frm.setData)frm.setData();
			}
			
			var action="${ctx}/platform/bpm/task/saveData.ht";
			submitForm(action,button);
		}
		
		// 在完成任务之前，加上判断是否需要弹出意见对话框
		function completeTaskBefore(t){
			// 加上判断是否需要弹出意见对话框
			var isPopup = '${bpmNodeSet.isPopup}',
				gatherNode = '${gatherNode}';
			// 如果为汇总节点退回，必须通过弹窗模式
			if(isPopup == 1 || (t&&t=='reject'&&gatherNode)){
				opintionDialog(t);
			} else {
				completeTask();
			}
		}
		

		//完成当前任务。
		function completeTask(){
			
			if($("input[name='nextPathId']").length>0){
				var b=false;//是否需要判断同步条件
				var destTask=$("input[name='destTask']:checked");
				if(destTask.length==0){
					b=true;
				}else{
					var nextPathId=destTask.parents("tr").find("[name=nextPathId]");
					if(nextPathId.length>0){
						b=true;
					}
				}
				if(b){
					var v=$("input[name='nextPathId']:checked").val();
					if(!v){
						$.ligerDialog.error("在同步条件后的执行路径中，您至少需要选择一条路径!",'提示信息');
						return;	
					}
					
				}
			}
			//判断每条执行路径是否已选择用户
			var flag = false;
			var spanSelcUser = $("span[name='spanSelectUser'],#jumpUserDiv");
			var spanSelcUserLen = spanSelcUser.length;
			var tag = 0;
			spanSelcUser.each(function(index){
				var lenChil = $(this).children("input[name$='_userId']:checked").length;
				if(lenChil>0){
					tag++;
				}
				if(spanSelcUserLen==tag){
					flag = true;
				}
			});
			var lastDestTaskId = $("input[name='lastDestTaskId']").val();
			if(lastDestTaskId){
				if(lastDestTaskId.indexOf("EndEvent")==-1 && !flag){
					$.ligerDialog.warn("每条执行路径至少要选择一个用户!",'提示信息');
					return;
				}
			}
					
			var operatorObj=getByOperatorType();
			var button="#" +operatorObj.btnId;
			var action="${ctx}/platform/bpm/task/complete.ht";
			//执行前置脚本
			var rtn=beforeClick(operatorType);
			if( rtn==false){
				return;
			}
			//确认执行任务。
			if(isNeedSubmitConfirm){
				$.ligerDialog.confirm("您确定执行此操作吗？","提示",function(rtn){
					if(rtn){
						submitForm(action,button);
					}
				});
			}
			else{
				submitForm(action,button);	
			}
		}
		
		
		function getByOperatorType(){
			var obj={};
			switch(operatorType){
				//同意
				case 1:
					obj.btnId="btnAgree";
					obj.msg="执行任务成功!";
					break;
				//反对
				case 2:
					obj.btnId="btnNotAgree";
					obj.msg="执行任务成功!";
					break;
				//弃权
				case 3:
					obj.btnId="btnAbandon";
					obj.msg="执行任务成功!";
					break;
					//退回
				case 4:
					obj.btnId="btnReject";
					obj.msg="退回任务成功!";
					break;
				//退回到发起人
				case 5:
					obj.btnId="btnRejectToStart";
					obj.msg="退回到发起人成功!";
					break;
				//保存表单
				case 8:
					obj.btnId="btnSave";
					obj.msg="保存表单数据成功!";
					break;
				
			}
			return obj;
		}
		
		function getErrorByOperatorType(){
			var rtn="";
			switch(operatorType){
				//同意
				case 1:
				//反对
				case 2:
				//弃权
				case 3:
					rtn="执行任务失败!";
					break;
				//退回
					//退回
				case 4:
					rtn="退回任务失败!";
					break;
				//退回到发起人
				case 5:
					rtn="退回到发起人失败!";
					break;
				//保存表单
				case 8:
					rtn="bcvbvc保存表单数据失败!";
					break;
			}
			return rtn;
		}
		
		function showResponse(responseText){
			var operatorObj=getByOperatorType();
			$("#" +operatorObj.btnId).removeClass("disabled");
			var obj=new com.hotent.form.ResultMessage(responseText);
			if(obj.isSuccess()){
				$.ligerDialog.success(operatorObj.msg,'提示信息',function(){
					var rtn=afterClick(operatorType);
					if( rtn==false){
						return;
					}
					handJumpOrClose();
				});
			}else{
				var title=getErrorByOperatorType();
				$.ligerDialog.err('出错信息',title,obj.getMessage());
			}
		}
		
		//弹出回退窗口 TODO 去除
		function showBackWindow(){
			new TaskBackWindow({taskId:taskId}).show();
		}
		
		$(function(){
			initForm();
			resizeIframe();
			//初始化联动设置
			<c:if test="${!empty bpmGangedSets}">
				bpmGangedSets = ${bpmGangedSets};
				FormUtil.InitGangedSet(bpmGangedSets);
			</c:if>			
		});	
		
		//获取是否允许直接结束。
		function getIsDirectComplete(){
			var isDirectComlete = false;
			if($("#chkDirectComplete").length>0){
				if($("#chkDirectComplete").attr("checked")=="checked"){
					isDirectComlete = true;
				}
			}
			return isDirectComlete;
		}
		
		//提交第三方表单时检查该表单的参数
		function setExtFormData(){
			if(isExtForm){
				var frm=$('#frmWorkFlow');
				if(!frm.valid()) return ;
				if(frm.setData)frm.setData();
			}
		}
		
		function initBtnEvent(){
			//0，弃权，1，同意，2反对。
			var objVoteAgree=$("#voteAgree");
			var objBack=$("#back");
			
			//同意
			if($("#btnAgree").length>0){
				$("#btnAgree").click(function(){
					setDestTaskInclude(true);
					var voteContent = $('#voteContent'),
					content = voteContent.val();
					
					setExtFormData();
					
					var isDirectComlete = getIsDirectComplete();
					
					operatorType=1;
					//同意:5，一票通过。
					var tmp =isDirectComlete?"5":"1";
					
					objVoteAgree.val(tmp);
					objBack.val("0");
					
					// 提前校验表单 
					var beforeScript=beforeClick(getByOperatorType());
					if(beforeScript==false) return; 
					var rtn=CustomForm.validate({ignoreRequired:false});
					if(!rtn){
						$.ligerDialog.warn("表单验证不成功,请检查表单是否正确填写!","提示信息");
						return;
					}
					completeTaskBefore();
				});
			}
			//反对
			if($("#btnNotAgree").length>0){
				$("#btnNotAgree").click(function(){
					setExtFormData();
					setDestTaskInclude(true);
					var isDirectComlete = getIsDirectComplete();
					operatorType=2;
					////直接一票通过
					var tmp =isDirectComlete?'6':'2';
					objVoteAgree.val(tmp);
					objBack.val("0");
					completeTaskBefore();
				});
			}
			//弃权
			if($("#btnAbandon").length>0){
				$("#btnAbandon").click(function(){
					setExtFormData();
					setDestTaskInclude(true);
					operatorType=3;
					objVoteAgree.val(0);
					objBack.val("");
					completeTaskBefore();
				})
			}
			//退回
			if($("#btnReject").length>0){
				$("#btnReject").click(function(){
						setExtFormData();
						
						setDestTaskInclude(false);
						operatorType=4;
						objVoteAgree.val(3);
						objBack.val(1);
						completeTaskBefore("reject"); 
				})
			}
			//退回到发起人
			if($("#btnRejectToStart").length>0){
				$("#btnRejectToStart").click(function(){
					var voteContent = $('#voteContent'),
					content = voteContent.val();
						setExtFormData();
						setDestTaskInclude(false);
						
						operatorType=5;
						//退回到发起人
						objVoteAgree.val(3);
						objBack.val(2);
						completeTaskBefore();
				})
			}
			//保存表单
			if($("#btnSave").length>0){
				$("#btnSave").click(function(){
					setExtFormData();
					operatorType=8;
					saveData();
				})
			}
			
			//终止流程
			$("#btnEnd").click(function(){
				isTaskEnd(endTask);
			});
		}
		
		
		//退回时设置是否提交目标路径。
		function setDestTaskInclude(isNeed){
			$("[name='destTask']").each(function(){
				if(isNeed){
					$(this).attr("include","1");
				}
				else{
					$(this).removeAttr("include");
				}
			})
		}
		
		// 弹出意见对话框
		function opintionDialog(reject){
			var isRequired = '${bpmNodeSet.isRequired}';
			var actDefId = $("#actDefId").val();
			var url= __ctx + '/platform/bpm/task/opDialog.ht?taskId=${task.id}&isRequired=' +isRequired+'&actDefId='+actDefId + '&reject=' + reject;
			var voteContent = $("#voteContent").val();
			DialogUtil.open({
				height:350,
				width: 500,
				title : '填写意见',
				url: url, 
				isResize: true,
				//自定义参数
				voteContent:voteContent,
				sucCall:function(rtn){
					if(typeof rtn=="string"){
						// 填写到系统的意见
						$("#voteContent").val(rtn);
					}
					else if(typeof rtn=="object"){
						// 填写到系统的意见
						$("#voteContent").val(rtn.opinion);
						$("#forkTask2Reject").val(rtn.forkTask2Reject);
					}
					// 完成当前任务
					completeTask();
				}
			});
		}
		
		
		
		function handJumpOrClose(){
			//如果按钮类型为保存则不关闭窗口。
			if(operatorType==8) return;
			if(window.opener){
				try{
					window.opener.location.href=window.opener.location.href.getNewUrl();
				}
				catch(e){}
			}	
			dialog.close();
			window.close();
		}
		
		function initForm(){
			//初始化按钮事件。
			initBtnEvent();
			
			if(isEmptyForm) return;
		
			if(isExtForm){		
				form=$('#frmWorkFlow').form({excludes:"[type=append]"});
				var formUrl=$('#divExternalForm').attr("formUrl");
				$('#divExternalForm').load(formUrl, function() {
					hasLoadComplete=true;
					//动态执行第三方表单指定执行的js
					try{
						afterOnload();
					}catch(e){}
					initSubForm();
					OfficePlugin.init();
				});
			}else{
				$(".taskopinion").each(function(){
					$(this).removeClass("taskopinion");
					var actInstId=$(this).attr("instanceId");
					$(this).load("${ctx}/platform/bpm/taskOpinion/listform.ht?actInstId="+actInstId);
				});
				
			}
		}
		
		function initSubForm(){
			$('#frmWorkFlow').ajaxForm({success:showResponse }); 
		}
		
		function showRoleDlg(){
			RoleDialog({callback:function(roleId,roleName){$('#forkUserUids').val(roleId);}}); 
		}
		
		
		function isTaskEnd(callBack){
			var url="${ctx}/platform/bpm/task/isTaskExsit.ht";
			var params={taskId:"${task.id}"};
			
			$.post(url,params,function(responseText){
				var obj=new com.hotent.form.ResultMessage(responseText);
				if(obj.isSuccess()){
					callBack.apply(this);
				}
				else{
					$.ligerDialog.warn("当前任务已经完成或被终止","提示信息");
				}
			});
		}
		
		
		
		function resizeIframe(){
			if($("#frameDetail").length==0) return;
			$("#frameDetail").load(function() { 
				$(this).height($(this).contents().height()+20); 
			}) ;
		}
		
		
		// 选择常用语
		function addComment(){
			var objContent=document.getElementById("voteContent");
			var selItem = $('#selTaskAppItem').val();
			jQuery.insertText(objContent,selItem);
		}
		
		function openForm(formUrl){
			var winArgs="dialogWidth=500px;dialogHeight=400px;help=0;status=0;scroll=0;center=1";
			var url=formUrl.getNewUrl();
			window.open(url,"",winArgs);
		}
		
		function reference(){
			var defId=${bpmDefinition.defId};
			
			var url="${ctx}/platform/bpm/processRun/getRefList.ht?defId=" +defId;
			
			var params="height=400,width=700,status=no,toolbar=no,menubar=no,location=no,resizable=1,scrollbars=1";
			window.open(url);
		}
		
		
		function beforeClick(operatorType){<c:if test="${not empty mapButton.button}">
			switch(operatorType){<c:forEach items="${mapButton.button }" var="btn"  ><c:if test="${not empty btn.prevscript}">
					case ${btn.operatortype}:
					${btn.prevscript}
					break;</c:if></c:forEach>
				}</c:if>
		}
	
	function afterClick(operatorType){<c:if test="${not empty mapButton.button}">
		switch(operatorType){<c:forEach items="${mapButton.button }" var="btn" ><c:if test="${not empty btn.afterscript}">
			case ${btn.operatortype}:
				${btn.afterscript}
				break;</c:if></c:forEach>
			}</c:if>
	}
	
	</script>	
	
</head>
<body>

	<form id="frmWorkFlow"  method="post" >
		 <div class="panel">
		
				<div class="panel-body">
					<c:choose>
						<c:when test="${bpmNodeSet.isHideOption ==0 && bpmNodeSet.isPopup==0  && task.description!='15' &&  task.description!='38' && task.description!='39'}">
							<div class="noprint">
								<jsp:include page="incTaskOpinion.jsp"></jsp:include>
							</div>
						</c:when>
						<c:when test="${  bpmNodeSet.isPopup == 1 && task.description!='15' &&  task.description!='38' && task.description!='39'}">
		            		<div class="hidden">
		            		<textarea class="hidden" include="1" id="voteContent" name="voteContent" >${taskOpinion.opinion}</textarea>
		            		</div>
		            	</c:when>
					</c:choose>
					<div class="printForm panel-detail" >
							<c:choose>
								<c:when test="${isEmptyForm==true}">
									<div class="noForm">没有设置流程表单。</div>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${isExtForm}">
											<c:choose>
												<c:when test="${!empty detailUrl}">
													<iframe id="frameDetail" src="${detailUrl}" scrolling="no"  frameborder="no"  width="100%" height="100%"></iframe>
												</c:when>
												<c:otherwise>
													<div class="printForm" id="divExternalForm" formUrl="${form}"></div>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<div id="custformDiv" class="printForm" type="custform">
											${form}
											</div>
											<input type="hidden" include="1" name="formData" id="formData" />
										</c:otherwise>
									</c:choose>	
								</c:otherwise>
							</c:choose>
					</div>
					<input type="hidden" id="taskId" include="1" name="taskId" value="${task.id}"/> 
					<%--退回和撤销投票为再次提交 --%>
					<c:choose>
						<c:when test="${processRun.status eq 5 or processRun.status eq 6}">
							<input type="hidden" include="1" id="voteAgree" name="voteAgree" value="34"/>	
						</c:when>
						<c:otherwise>
							<input type="hidden" include="1" id="voteAgree" name="voteAgree" value="1"/>
						</c:otherwise>
					</c:choose>
					<input type="hidden" include="1" id="back" name="back" value=""/>
					<input type="hidden" include="1" id="actDefId" name="actDefId" value="${bpmDefinition.actDefId}"/>
					<input type="hidden" include="1" name="defId" value="${bpmDefinition.defId}"/>
					<input type="hidden" include="1" id="isManage" name="isManage" value="${isManage}"/>
					<input type="hidden" include="1" id="businessKey" name="businessKey" value="${processRun.businessKey}"/>
					<input type="hidden" include="1" id="startNode" name="startNode" value="${toBackNodeId}"/>
					<input type="hidden" include="1" name="curUserId" value="${curUserId}"/>
		            <input type="hidden" include="1" name="curUserName" value="${curUserName}"/>
		            <input type="hidden" include="1" id="currentNode" name="currentNode" value="${task.taskDefinitionKey}"/>
		            <input type="hidden" include="1" name="formKey" value="${formKey}"/>
		            <input type="hidden" include="1" id="forkTask2Reject" name="forkTaskRejects" /> 
		            <div style="padding:6px 8px 3px 12px;" class="noprint">
						<%-- <div class="group"><a id="btnAgree" class="link agree"><span></span>同意</a></div>
						<c:if test="${isCanBack || gatherNode}">
						<div class="l-bar-separator"></div>
						<div class="group"><a id="btnReject" class="link reject" ><span></span>退回</a></div>
						<div class="l-bar-separator"></div>
						<div class="group"><a id="btnRejectToStart" class="link rejectToStart" ><span></span>退回到发起人</a></div>
						</c:if> --%>
						<div class="row" style="margin-left: 30%;">
							<button type="button" id="btnAgree" class="btn agree" ><span>同意</span></button>
							<c:if test="${isCanBack || gatherNode}">
								<button type="button" id="btnReject" class="btn reject" ><span>退回</span></button>
								<button type="button" id="btnRejectToStart" class="btn rejectToStart" ><span>退回发起人</span></button>
							</c:if>
						</div>
					</div>
				</div>
         </div>
         
    </form>
    
</body>
</html>