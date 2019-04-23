<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
	<%@include file="/commons/include/get.jsp" %>
	<title>${f:removeHTMLTag(processRun.subject)}--流程审批历史</title>
	<link href="${ctx}/styles/default/css/jquery.qtip.css" rel="stylesheet" />
	<script type="text/javascript" src="${ctx}/js/util/easyTemplate.js"></script>
	<script type="text/javascript" src="${ctx}/js/jquery/plugins/jquery.qtip.js" ></script>
	<script type="text/javascript" src="${ctx}/js/util/form.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/ProcessUrgeDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/FlowUtil.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/ShowExeInfo.js" ></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/UserInfo.js"></script>
	<script type="text/javascript">
		$(function(){
			$("a[opinionId]").each(showResult);	
		});
		function showResult(){
			var opinionId=$(this).attr("opinionId"),
				checkStatus= $(this).attr("checkStatus");
			var template=$("#txtReceiveTemplate").val();
			$(this).qtip({  
				content:{
					text:'加载中...',
					ajax:{
						url:__ctx +"/platform/bpm/commuReceiver/getByOpinionId.ht",
						type:"GET",
						data:{opinionId: opinionId },
						success:function(data,status){
							var html=easyTemplate(template,data).toString();
							this.set("content.text",html);
						}
					},
					title:{
						text: checkStatus==36?'重启审批人' :(checkStatus==15?'沟通接收人' : '流转接收人')			
					}
				},
			        position: {
			        	at:'top left',
			        	target:'event',
			        	
   					viewport:  $(window)
			        },
			        show:{
			        	event:"click",
	   			     	solo:true
			        },   			     	
			        hide: {
			        	event:'unfocus',
			        	fixed:true
			        },  
			        style: {
			       	  classes:'ui-tooltip-light ui-tooltip-shadow'
			        } 			    
		 	});	
		}
		
	</script>
</head>
<body>
	<div class="l-layout-header">流程实例-【<i>${processRun.subject}</i>】审批历史明细。</div>
	<div class="panel">
		<c:if test="${param.tab eq 1 }">
			<c:choose>
					<c:when test="${processRun.status==2  || processRun.status==3}">
						<f:tab curTab="taskOpinion" tabName="process" />
					</c:when>
					<c:otherwise>
						<f:tab curTab="taskOpinion" tabName="process" hideTabs="copyUser"/>
					</c:otherwise>
			</c:choose>
		</c:if>
		<div class="panel-body" style="height:120%;overflow:auto;">
  		    <table class="table-grid">
  		    <tr>
  		      <th>任务节点</th>
  		      <th>开始时间</th>
  		      <th>结束时间</th>
  		      <th>经历时间</th>
  		      <th>执行人</th>
  		      <th>审批意见</th>
  		      <th>审批状态</th>
  		    </tr>
	    	<c:forEach var="item" items="${taskOpinionList}">     
				<c:forEach var="tem" items="${item.list}" varStatus="var">
		           	<tr>
           			   <c:if test="${var.index==0}">
			               <td style="width: 80px !important;" <c:if test="${fn:length(item.list)>1}">rowspan="${fn:length(item.list)}"</c:if>>
			       	  			${item.taskName}
			          	   </td>
		          	   </c:if>
		          	   <td style="width: 120px !important;height: 30px">
		                	${tem.startTimeStr}
		               	</td>
		               	<td style="width: 120px !important;height: 30px">
		                  	${tem.endTimeStr}
		               	</td>
		              	<td>
	              		 	<c:if test="${tem.endTimeStr!=''}">
		              			${tem.durTimeStr==''?'0分钟':tem.durTimeStr}
		              		</c:if>
		              	</td>
		              	<td style="width: 120px !important;">
 		                 	<c:choose>
								<c:when test="${tem.exeUserId ne null }">
									<a href="javascript:userDetail('${tem.exeUserId}');">${tem.exeFullname}</a>
								</c:when>
								<c:otherwise>
									<c:forEach items="${tem.candidateUsers }" var="user">
										<a href="javascript:userDetail('${user.userId}');" >${user.fullname}</a>
										<br/>
									</c:forEach>
								</c:otherwise>
							</c:choose>
		              	</td>
		              	<td style="width: 120px !important;">
		              		 ${f:parseText(tem.opinion)}
							<c:if test="${tem.checkStatus==15}">
								<a  href="javascript:;" onclick="return false;" opinionId="${tem.opinionId }" checkStatus="15" >接收人</a>
							</c:if>
							<c:if test="${tem.checkStatus==38 || tem.checkStatus==43}">
								<a  href="javascript:;" onclick="return false;" opinionId="${tem.opinionId }" checkStatus="40" >接收人</a>
							</c:if>
	             		</td>
		              	<td style="width: 80px !important;">
		                	<f:taskStatus status="${tem.checkStatus}" flag="0"></f:taskStatus>
		              	</td>
		           	</tr>
	       	   	</c:forEach>  
			</c:forEach> 
  		 	</table>
		</div>
  </div>
  
  <textarea id="txtReceiveTemplate"  style="display: none;">
    <div  style="height:150px;width:150px;overflow:auto">
	  	
	  		<#list data as obj>
	  		<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
	  		<tr>
	  			<th>接收人</th>
	  			<td>\${obj.receivername }</td>
	  		</tr>
	  		<tr>
	  			<th>状态</th>
	  			<td>
	  				<#if (obj.status==0) >
	  					<span class="red">未读</span>
	  				<#elseif (obj.status==1)>
	  					<span class="green">已读</span>
	  				<#elseif (obj.status==2)>
	  					<span class="green">已反馈</span>
	  				<#elseif (obj.status==3)>
	  					<span class="red">已取消</span>
	  				</#if>
	  			</td>
	  		</tr>
	  		<#if (obj.status==0) >
		  		<tr>
		  			<th>创建时间</th>
		  			<td>\${obj.createtimeStr}</td>
		  		</tr>
		  	<#elseif (obj.status==1)>
			  	<tr>
		  			<th>接收时间</th>
		  			<td>\${obj.receivetimeStr}</td>
			  	</tr>
		  	<#elseif (obj.status==2)>
		  		<tr>
		  			<th>反馈时间</th>
		  			<td>\${obj.feedbacktimeStr}</td>
		  		</tr>
		  	<#elseif (obj.status==3)>
		  		<tr>
		  			<th>取消时间</th>
		  			<td>\${obj.feedbacktimeStr}</td>
		  		</tr>
	  		</#if>
	  		</table>
	  		</#list>
	  	
  	</div>
  </textarea>
</body>
</html>

