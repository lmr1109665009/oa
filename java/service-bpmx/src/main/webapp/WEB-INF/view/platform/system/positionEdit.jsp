<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>编辑 系统岗位表，实际是部门和职务的对应关系表</title>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/Share.js"></script>
	<script type="text/javascript" src="${ctx}/js/suneee/base/system/CommonDialog.js"></script>
	<script type="text/javascript">
	  
	  var authId = "${param.authId}";
	
		$(function() {
			$("a.save").click(function() {
				var jobOption = $("#jobId").val();
				if(!jobOption){
					$.ligerDialog.warn("请选择职务，如果没有，请先添加职务","提示");
					return;
				}
				 $("#positionForm").attr("action","save.ht");
				 submitForm();
			});
			/*自动填充岗位名称和岗位代码*/
		});
		
		//提交表单
		function submitForm(){
			var options={};
			if(showResponse){
				options.success=showResponse;
			}
			var frm=$('#positionForm').form();
			frm.ajaxForm(options);
			if(frm.valid()){
				frm.submit();
			}
		}
		
		function showResponse(responseText) {
			var self=this;
			var obj = new com.hotent.form.ResultMessage(responseText);
			if (obj.isSuccess()) {
				$.ligerDialog.confirm(obj.getMessage()+",是否继续操作","提示", function(rtn) {
				if(rtn){
					     window.location.reload(true);
				      }else{
				    	 //返回按钮的链接
				    	 if(authId==""){
				    	     window.location.href="${ctx}/platform/system/position/list.ht?orgId=${sysOrg.orgId}";
				    	 }else{
				    		 window.location.href="${ctx}/platform/system/position/gradeList.ht?orgId=${sysOrg.orgId}&authId=${param.authId}&topOrgId=${param.topOrgId}";
				    	 }
				      }
				});
			} else {
				$.ligerDialog.err("提示信息","岗位保存失败!",obj.getMessage());
			}
		}
		
		function autoPingin(obj){
			var value=$(obj).val();
			Share.getPingyin({
				input:value,
				postCallback:function(data){
					$("#posCode").val(data.output);
				}
			});
		}
		function selectJob(){
			var conf = {url:"/platform/system/job/selector.ht?enterpriseCode=${sysOrg.orgCode}", 
					dialogWidth:800, 
					dialogHeight:520, 
					title:"选择职务",
					callBack:function(result){
						$("#jobId").val(result.jobid);
						$("#jobName").text(result.jobname);
						$("#posName").val("${sysOrg.orgName}"+"_"+result.jobname).trigger("change");
					}
			};
			CommonDialog(conf);
			
			/* var paramValueString = "";
			CommonDialog("gwxz",function(data){
			  //data返回 Object { JOBID = "参数值", JOBNAME = "参数值"}，多个则返回 Object 数组
			  $("#jobId").val(data.JOBID);
			  $("#jobName").text(data.JOBNAME);
			  $("#posName").val("${sysOrg.orgName}"+"_"+data.JOBNAME).trigger("change");
			},paramValueString); */
		}
	</script>
</head>
<body>
<div class="panel">
	<div class="panel-top">
		<div class="tbar-title">
		    <c:choose>
			    <c:when test="${position.posId !=null}">
			        <span class="tbar-label">编辑系统岗位表，实际是部门和职务的对应关系表</span>
			    </c:when>
			    <c:otherwise>
			        <span class="tbar-label">添加系统岗位表，实际是部门和职务的对应关系表</span>
			    </c:otherwise>			   
		    </c:choose>
		</div>
		<div class="panel-toolbar">
			<div class="toolBar">
				<div class="group"><a class="link save" id="dataFormSave" href="javascript:;"><span></span>保存</a></div>
				<div class="l-bar-separator"></div>
				<div class="group"><a class="link back" href="javaScript:window.history.go(-1)"><span></span>返回</a></div>
			</div>
		</div>
	</div>
	<div class="panel-body">
		<form id="positionForm" method="post" action="save.ht">
			<table class="table-detail" cellpadding="0" cellspacing="0" border="0" type="main">
			
				<tr>
					<th width="20%">组织名称: </th>
					<td>
						<input type="hidden" id="orgId" name="orgId" value="${sysOrg.orgId}"/>
						<input type="hidden" id="orgCode" name="orgCode" value="${sysOrg.orgCode}" />
						${sysOrg.orgName} 
					</td>
				</tr>
				<tr>
					<th width="20%">职务: </th>
					<td>
						<span id="jobName">
						<c:forEach items="${jobList}" var="job" >							 				
			 				<c:if test="${position.jobId==job.jobid}">${job.jobname}</c:if>	
						</c:forEach></span>
						<input type="hidden" name="jobId" value="${position.jobId}" id="jobId">
						<a href="javascript:;" class="link get" onclick="selectJob()">选择</a> 
	                 </td>
				</tr>
				<tr>
					<th width="20%">岗位名称:  <span class="required">*</span></th>
					<td><input type="text" id="posName" name="posName"  value="${position.posName}" validate="{required:true,maxlength:128}" 
					           class="inputText" style="width:255px !important"/></td>
				</tr>
				<%-- <tr>
					<th width="20%">岗位代码:  <span class="required">*</span></th>
					<td><input type="text" id="posCode" name="posCode" value="${position.posCode}" validate="{required:false,maxlength:128}" class="inputText" style="width:255px !important"/></td>
				</tr> --%>
				<tr>
					<th width="20%">岗位描述: </th>
					<td><textarea id="posDesc" name="posDesc" cols="30" rows="4"  style="width:258px !important">${position.posDesc}</textarea></td>
				</tr>
			</table>
			<input type="hidden" name="posId" value="${position.posId}" />					
		</form>
	</div>
</div>
</body>
</html>
