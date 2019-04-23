<%--
	time:2012-01-05 12:01:21
	desc:edit the 其它参数
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>其他参数设置</title>
	<%@include file="/commons/include/get.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/FlowVarWindow.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/FlexUploadDialog.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor_default.config.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor.all.min.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/lang/zh-cn/zh-cn.js"></script>
	<script type="text/javascript">
	var defId=${bpmDefinition.defId };
	var ortherParamUeditor;//百度编辑器
	$(function(){
		ueditorInit();//初始化编辑器
		handFlowVars();
		//状态改变
		handleStatusChange();
	});
	
	function ueditorInit(){
		ortherParamUeditor = new baidu.editor.ui.Editor
			
			({minFrameHeight:100,initialFrameWidth:'100%',lang:'zh_cn',enterTag:'',maximumWords:500,
				toolbars:[['source','undo','redo','bold','italic', 'underline',
				           'subscript','superscript', 'removeformat', 
				           'selectall', 'forecolor','fontsize', 'backcolor','justifyleft',
				           'justifyright', 'justifycenter', 'justifyjustify', 
				           'subject','startuser', 'startdate','starttime','businesskey']]
			});
		ortherParamUeditor.render("taskNameRule"); 
	}
	function handleStatusChange(){
		$("#status").change(function(){
			var v=$(this).val();
			if(v=="2" || v=="3"){
				$("#spanMessage").hide();
			}
			else{
				$("#spanMessage").show();
			}
			if(v=="4"){
				$("#testTag").show();
			}else{
				$("#testTag").hide();
			}
		});
	}
	
	function handFlowVars(){
		var objConditionCode=$("#taskNameRule");
		$("select[name='selFlowVar']").change(function(){		
			var val=$(this).val();
			var text=$(this).find("option:selected").text();
			if(val.length==0) return;
			if(text=="发起人(长整型)")
				text=text.replace("(长整型)","");			
			var inStr="{"+text+":"+val+"}";
			InsertText(inStr);
		});
	}
	
	function InsertText(val){
		var html = ortherParamUeditor.getContent();
		ortherParamUeditor.setContent(html+val);
	}
	
	function getCheckedValue(id){
		
		var checked=$(id).attr("checked");
		if(checked==undefined){
			return 0;
		}else if(checked){
			return 1;
		}else{
			return 0;
		}
	}
	function getMsgTypeList(id){
		var msgTypeList=[];
		$("input[name='"+id+"']").each(function(){
			var me = $(this),
				val = me.val(),
				state = me.attr("checked");
			if(state)
				msgTypeList.push(val);
		});
		return msgTypeList;
	}
	function saveParam(){
		var taskNameRule=ortherParamUeditor.getContent();
		var toFirstNode=getCheckedValue("#toFirstNode");
		var showFirstAssignee=getCheckedValue("#showFirstAssignee");
		var submitConfirm=getCheckedValue("#submitConfirm");
		var allowDivert=getCheckedValue("#allowDivert");
		var allowFinishedDivert=getCheckedValue("#allowFinishedDivert");
		var informStart = getMsgTypeList("informStart");
		var informType = getMsgTypeList("informType");
		var allowFinishedCc=getCheckedValue("#allowFinishedCc");
		var isPrintForm=getCheckedValue("#isPrintForm");
		var formDetailUrl=$('#formDetailUrl').val();
		var attachment=$('#attachment').val();
		var status=$('#status').val();
		
		var isUseOutForm=getCheckedValue("#isUseOutForm");
		var isUseOutForm=getCheckedValue("#isUseOutForm");
		var allowRefer=getCheckedValue("#allowRefer");
		var instanceAmount=$('#instanceAmount').val();
		var testStatusTag=$('#testStatusTag').val();
		
		var directstart=getCheckedValue("#directstart");
		var ccMessageType = $("input[name='ccMessageType']").val();
		
		var allowMobile=getCheckedValue("#allowMobile");
		var skipSetting=getMsgTypeList("skipSetting");
		var informShowInFront=getMsgTypeList("informShowInFront");
		
		var params={defId:defId,taskNameRule:taskNameRule,toFirstNode:toFirstNode,
				showFirstAssignee:showFirstAssignee,
				submitConfirm:submitConfirm,allowDivert:allowDivert,
				allowFinishedDivert:allowFinishedDivert,informStart:informStart.join(','),
				informType:informType.join(','),allowFinishedCc:allowFinishedCc,
				isPrintForm:isPrintForm,attachment:attachment,
				status:status,isUseOutForm:isUseOutForm,allowRefer:allowRefer,
				instanceAmount:instanceAmount,directstart:directstart,ccMessageType:ccMessageType,
				testStatusTag:testStatusTag,allowMobile:allowMobile,
				skipSetting:skipSetting.join(','), informShowInFront:informShowInFront.join(',')};
		
		$.post("saveParam.ht",params,function(msg){
			var obj=new com.hotent.form.ResultMessage(msg);
			if(obj.isSuccess()){
				$.ligerDialog.success(obj.getMessage(),"操作成功");
			}else{
				$.ligerDialog.err('出错信息',"流程定义其他参数设置失败",obj.getMessage());
			}
		});
	}
	
	function openCcUserList(){
		var url=__ctx +'/platform/bpm/bpmDefinition/copyUserList.ht?defId=${bpmDefinition.defId}&parentActDefId=${parentActDefId}';
	 	var winArgs="height=450,width=750,status=no,toolbar=no,menubar=no,location=no,resizable=1,scrollbars=1";
		url=url.getNewUrl();
	 	window.open(url,"",winArgs);
	 	$("#allowFinishedCc").attr("checked","checked");
	};
	
	//添加附件
	function addFile(){
		FlexUploadDialog({isSingle:true,callback:fileCallback});
	};
	
	function fileCallback(fileIds,fileNames,filePaths){
		if(fileIds==undefined || fileIds=="") return ;
		var url=__ctx+"/platform/system/sysFile/file_"+fileIds +".ht";
		$("#attachment").val(fileIds);
		if($("#file").length>0){
			$("#file").attr("href",url).text(fileNames);
		}else{
			var node='<a href="'+url+'" target="_blank" id="file">'+fileNames+' </a><a  class="link del" onclick="del(this)" style="cursor:pointer;"> 删除</a>';
			$("#attachment").after(node);
		}
	}
	function del(obj){
		$("#attachment").val("");
		$("#file").remove();
		$(obj).remove();
	}
	var referDef;
	function referDefinition(){
		var url=__ctx +'/platform/bpm/bpmDefinition/defReferSelector.ht?defId=${bpmDefinition.defId}';
		referDef = $.ligerDialog.open({
			title:'流程引用',
			mask:true,
			isResize:true,
			height: 500,
			url:url,
			width:700,
			buttons:[
			         { text: '确定', onclick: function (item, dialog) {
			        	 var contents=$("iframe",dialog.dialog).contents()
			        	 var chKeys=contents.find("input.pk[name=defKey]:checked");
			        	 var defId=contents.find("#bpmDefId").val();
			        	 var aryDefKey = [];
			        	 
				     		$.each(chKeys,function(i,ch){
				     			aryDefKey.push($(ch).val());
				     		});
				     		var params={defId:defId,refers:aryDefKey.join(",")};
				     		if(aryDefKey.length > 0){
				    			$.post("saveReferDef.ht",params,function(map){
				    				var spanHtml='';
				    				for (var key in map) {  
			    						spanHtml=spanHtml+"<span id='ref_"+key+"'>"+map[key]+"<a href='javascript:void(0);' onclick='delRefer("+key+")'>删除</a>&nbsp;&nbsp;&nbsp;&nbsp;</span>"
				    				} 
				    				$("span#refDefArray").html(spanHtml);
				    				$.ligerDialog.success('流程引用成功！','提示'); 
				    			});
				    		}else{
				    			$.ligerDialog.warn('请选择引用流程!','提示');
				    		}
							dialog.close();
					}
			         },
					{ text: '取消', onclick: function (item, dialog) { dialog.close(); } }
					]});
	};
	
	function clickAllowRefer(obj){
		var $obj=$(obj);
		if($obj.attr("checked")){
			$("#spanInstanceAmount").show();	
		}
		else{
			$("#spanInstanceAmount").hide();
		}
		
	}
	
	function delRefer(refId){
		$.post("delReferDef.ht",{refId:refId},function(msg){
			var obj=new com.hotent.form.ResultMessage(msg);
			if(obj.isSuccess()){
				$.ligerDialog.success(obj.getMessage(),'提示');
				$('#ref_'+refId).remove();
			}else{
				$.ligerDialog.err("提示信息","删除流程引用失败!",obj.getMessage());
			}
		});
	}
	
	</script>
</head>
<body> 

            <jsp:include page="incDefinitionHead.jsp">
		   		<jsp:param value="其他参数" name="title"/>
			</jsp:include>
			 <div class="panel-container">
            <f:tab curTab="otherParam" tabName="flow"/>
            <div class="panel-top">
	            <div class="tbar-title">
					<span class="tbar-label">流程定义其他参数设置</span>
				</div>
				<div class="panel-toolbar">
					<div class="toolBar">
						<div class="group"><a class="link save" onclick="saveParam()"><span></span>保存</a></div>
					</div>	
				</div>

			</div>
            <div class="panel-detail">
			<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<th width="15%">流程标题规则定义</th>
					<td>
						表单变量:<f:flowVar defId="${defId}" controlName="selFlowVar"></f:flowVar>
						<textarea id="taskNameRule" row="6" name="taskNameRule" >${bpmDefinition.taskNameRule }</textarea>
					</td>	
				</tr>
				
				<tr>
					<th width="15%">跳过第一个任务:</th>
					<td>
						<input id="toFirstNode" type="checkbox"  name="toFirstNode" value="0"  <c:if test="${bpmDefinition.toFirstNode==1 }">checked="checked"</c:if> />
						<div class="tipbox"><a href="javascript:;" class="tipinfo"><span>流程启动后直接完成第一个节点的任务。</span></a></div>
					</td>	
				</tr>
				<c:if test="${!isStartMultipleNode}">
				<tr>
					<th width="15%">直接启动流程:</th>
					<td>
						<input id="directstart" type="checkbox" name="directstart" value="0"  <c:if test="${bpmDefinition.directstart==1 }">checked="checked"</c:if> />
						<div class="tipbox"><a href="javascript:;" class="tipinfo"><span>不使用表单直接启动流程，启动流程时不传入主键。</span></a></div>
					</td>	
				</tr>
				</c:if>
				<tr>
					<th width="15%">流程启动选择执行人:</th>
					<td>
						<input id="showFirstAssignee" type="checkbox" name="showFirstAssignee" value="1"  <c:if test="${bpmDefinition.showFirstAssignee==1 }">checked="checked"</c:if> />
						<div class="tipbox"><a href="javascript:;" class="tipinfo"><span>如果勾选，那么流程启动时可以改变下一步的执行人，默认不可以。</span></a></div>
					</td>	
				</tr>
				<tr>
					<th width="15%">允许API调用:</th>
					<td>
						<input id="isUseOutForm" type="checkbox" name="isUseOutForm" value="1"  <c:if test="${bpmDefinition.isUseOutForm==1 }">checked="checked"</c:if> />
						
						<div class="tipbox"><a href="javascript:;" class="tipinfo"><span>如果勾选，那么流程执行时会转向设置的url业务表单上去。</span></a></div>
					</td>	
				</tr>
				
				<tr>
					<th width="20%">提交是否需要确认:</th>
					<td>
						<input id="submitConfirm" type="checkbox" name="submitConfirm" value="${bpmDefinition.submitConfirm}" <c:if test="${bpmDefinition.submitConfirm==1 }">checked="checked"</c:if>  />
						<div class="tipbox"><a href="javascript:;" class="tipinfo"><span>如果勾选,在每一次提交提示确认对话框，默认关闭。</span></a></div>
					</td>	
				</tr>
				
				<tr>
					<th width="20%">是否允许转办:</th>
					<td>
						<input id="allowDivert" type="checkbox" name="allowDivert" value="${bpmDefinition.allowDivert}" <c:if test="${bpmDefinition.allowDivert==1 }">checked="checked"</c:if> />
						<div class="tipbox"><a href="javascript:;" class="tipinfo"><span>如果勾选,则允许转办，默认不允许转办。</span></a></div>
					</td>	
				</tr>
				<tr>
					<th width="20%">是否允许我的办结转发:</th>
					<td>
						<input id="allowFinishedDivert" type="checkbox" name="allowFinishedDivert"   <c:if test="${bpmDefinition.allowFinishedDivert==1 }">checked="checked"</c:if>  />
						<div class="tipbox"><a href="javascript:;" class="tipinfo"><span>如果勾选，则允许转发，默认不允许。</span></a></div>
					</td>	
				</tr>
				<tr>
					<th width="20%">归档时发送消息给发起人:</th>
					<td>
					    <c:forEach items="${handlersMap}" var="item">
		                   <input type="checkbox" name="informStart" value="${item.key }"  <c:if test="${fn:contains(bpmDefinition.informStart,item.key)}">checked="checked"</c:if> />
                            ${item.value.title }
		                </c:forEach>
					</td>	
				</tr>
				<tr>
					<th width="20%">审批节点消息类型显示设置:</th>
					<td>
					    <c:forEach items="${handlersMap}" var="item">
		                   <input type="checkbox" name="informShowInFront" value="${item.key }"  <c:if test="${fn:contains(bpmDefinition.informShowInFront,item.key)}">checked="checked"</c:if> />
                            ${item.value.title }
		                </c:forEach>
					</td>	
				</tr>
				<tr>
					<th width="20%">是否允许办结抄送:</th>
					<td>
						<input id="allowFinishedCc" type="checkbox" name="allowFinishedCc" value="${bpmDefinition.allowFinishedCc}"  <c:if test="${bpmDefinition.allowFinishedCc==1 }">checked="checked"</c:if>  />
						<!-- 抄送消息提醒方式 -->
						<input class="send_type" name="ccMessageType" type="hidden" value='${bpmDefinition.ccMessageType}' />
						<a href="###" onclick="openCcUserList()">抄送人员设置</a>
						<div class="tipbox"><a class="tipinfo"><span>如果勾选，则允许在流程结束时抄送，默认不抄送。</span></a></div>
					</td>
				</tr>
				<tr>
					<th width="20%">流程实例归档后是否允许打印表单:</th>
					<td>
						<input id="isPrintForm" type="checkbox" name="isPrintForm" value="${bpmDefinition.isPrintForm}"   <c:if test="${bpmDefinition.isPrintForm==1 }">checked="checked"</c:if> />
						<div class="tipbox"><a href="javascript:;" class="tipinfo"><span>如果勾选，则流程实例归档后将提供打印表单功能。</span></a></div>
					</td>	
				</tr>
				
				<tr>
					<th width="20%">流程帮助:</th>
					<td>
						<input id="attachment" type="hidden" name="attachment" value="${bpmDefinition.attachment}"  />
						
						<c:if test="${sysFile !=null }">
							<a href="${ctx}/platform/system/sysFile/file_${sysFile.fileId}.ht" target="_blank" id="file">${sysFile.fileName }</a><a  class="link del" onclick="del(this)" style="cursor:pointer;">删除</span>
						</c:if>
						<a href="javascript:void(0);" class="link selectFile"  onclick="addFile()">添加附件</a>
						<div class="tipbox"><a href="javascript:;" class="tipinfo"><span>流程帮助附件</span></a></div>
					</td>	
				</tr>
				<tr>
					<th width="20%">引用的流程:</th>
					<td>
						<span id="refDefArray">
							<c:forEach items="${referList}" var="refer">
								<span id="ref_${refer.id}"><c:out value="${refer.subject}"></c:out><a href="javascript:void(0);" onclick="delRefer(${refer.id})">删除</a>&nbsp;&nbsp;&nbsp;&nbsp;</span>
							</c:forEach>
						</span><span id="refDefs"></span>
						<a class="link search" href="javascript:void(0);" onclick="referDefinition()">引用</a>
						<div class="tipbox"><a href="javascript:;" class="tipinfo"><span>维护该流程可以进行引用的流程。</span></a></div>
					</td>	
				</tr>
				<tr>
					<th width="20%">流程参考:</th>
					<td>
						<input type="checkbox" value="${bpmDefinition.allowRefer}" name="allowRefer" id="allowRefer" onclick="clickAllowRefer(this)"  <c:if test="${bpmDefinition.allowRefer==1 }">checked="checked"</c:if> >允许参考
						<span id="spanInstanceAmount" <c:if test="${bpmDefinition.allowRefer==0 }">style="display:none;"</c:if> >
							参考条数:<input type="text" value="${bpmDefinition.instanceAmount }" name="instanceAmount" id="instanceAmount" style="width:30px;" >
						</span>
					</td>	
				</tr>
				
				<tr>
					<th width="20%">状态:</th>
					<td>
						<select id="status" name="status">
							<option value="1" <c:if test="${bpmDefinition.status==1}">selected='selected'</c:if>>启用</option>
							<option value="2" <c:if test="${bpmDefinition.status==2}">selected='selected' </c:if>>禁用</option>
							<option value="3" <c:if test="${bpmDefinition.status==3}">selected='selected' </c:if>>禁用(实例)</option>
							<option value="4" <c:if test="${bpmDefinition.status==4}">selected='selected' </c:if>>测试</option>
						</select>
						<span id="spanMessage" <c:if test="${bpmDefinition.status==2||bpmDefinition.status==3}">style="display:none;"</c:if>>
						   <c:forEach items="${handlersMap}" var="item">
		                   <input type="checkbox" name="informType" value="${item.key }"  <c:if test="${fn:contains(bpmDefinition.informType,item.key)}">checked="checked"</c:if> />
                            ${item.value.title }
		                   </c:forEach>
						</span>
						<span id="testTag"<c:if test="${bpmDefinition.status!=4}">style="display:none;"</c:if>>
							&nbsp;&nbsp;测试标签：<input type="text" id="testStatusTag" name="testStatusTag" value="${bpmDefinition.testStatusTag}" >
						</span>
					</td>	
				</tr>
				<tr>
					<th width="20%">是否手机审批:</th>
					<td>
					 	<c:choose>
					 		<c:when test="${bpmDefinition.allowMobile==0}">是</c:when>
					 		<c:otherwise>否</c:otherwise>
					 	</c:choose>
					</td>	
				</tr>
				
				<tr>
					<th width="20%">节点跳过设定:</th>
					<td>
						<c:forEach items="${skipMap}" var="item">
				        	<label><input type="checkbox" name="skipSetting" value="${item.key}" <c:if test="${fn:contains(bpmDefinition.skipSetting,item.key)}">checked="checked"</c:if>/>${item.value.title }</label>
				        </c:forEach>
					</td>	
				</tr>
				
			</table>
			</div>
			</div>
			<input type="hidden" id="defId" name="defId" value="${bpmDefinition.defId }">
</body>
</html>
