<%@ page pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
	<%@include file="/commons/include/customForm.jsp" %>
	<script>
		var authId="${authId}";
	</script>
	<title></title>
	<link rel="stylesheet" type="text/css" href="${ctx}/styles/default/css/hotent/task.css"></link>
	<style type="text/css">
		.formTable{
			width: auto!important;
		}
		.table-history{
			border-collapse: collapse;
			width: 100%;
			margin-top: 10px;
		}
		.table-history tr td,.table-history tr th{
			border: 1px solid #000000;
			padding: 5px 5px;
			text-align: center;
		}
		.formTable .dicComboTree{
			color: #555555;
			padding-left: 0px;
		}
		.printForm .formTable input.dicComboTree{
			margin-left: -2px;
		}
	</style>
	<script type="text/javascript" src="${ctx}/js/util/form.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/ProcessUrgeDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/FlowUtil.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/FlowRightDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/CheckVersion.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/SelectUtil.js" ></script>
	<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerDialog.js" ></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/FlowUtil.js"></script>
</head>
<body class="runinfoB">
      <div class="panel">
		<div class="panel-body panel-detail printForm" id="justPrintDiv">
			<c:if test="${hasGlobalFlowNo }">
				<div align="left" class="flownoL" style="display:none;"><span>工单号：</span><P>${processRun.globalFlowNo}</P></div>
			</c:if>
			<c:choose>
				<c:when test="${isExtForm==true }">
					<div id="divExternalForm" formUrl="${form}"></div>
				</c:when>
				<c:otherwise>
					${form}
				</c:otherwise>
			</c:choose>
			<br>
			<br>
			<div class="flow-history">
				<h2>审批历史</h2>
				<table class="table-history">
					<tr>
						<th width="20%">任务节点</th>
						<th width="14%">开始时间</th>
						<th width="14%">结束时间</th>
						<th width="10%">经历时间</th>
						<th width="10%">执行人</th>
						<th width="25%">审批意见</th>
						<th width="7%">审批状态</th>
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
     </div>
</body>
</html>