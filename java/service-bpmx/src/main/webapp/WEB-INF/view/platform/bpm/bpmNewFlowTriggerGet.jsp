
<%--
	time:2015-05-28 11:20:59
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<title>触发新流程配置明细</title>
<%@include file="/commons/include/get.jsp"%>
<script type="text/javascript">
	//放置脚本
</script>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">触发新流程配置详细信息</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group">
						<a class="link back" href="list.ht"><span></span>返回</a>
					</div>
				</div>
			</div>
		</div>
		<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<th width="20%">名子:</th>
				<td>${bpmNewFlowTrigger.name}</td>
			</tr>
			<tr>
				<th width="20%">节点:</th>
				<td>${bpmNewFlowTrigger.nodeid}</td>
			</tr>
			<tr>
				<th width="20%">触发动作:</th>
				<td>${bpmNewFlowTrigger.action}</td>
			</tr>
			<tr>
				<th width="20%">流程key:</th>
				<td>${bpmNewFlowTrigger.flowkey}</td>
			</tr>
			<tr>
				<th width="20%">被触发流程:</th>
				<td>${bpmNewFlowTrigger.triggerflowkey}</td>
			</tr>
			<tr>
				<th width="20%">映射配置:</th>
				<td>${bpmNewFlowTrigger.jsonmaping}</td>
			</tr>
			<tr>
				<th width="20%">发起人配置:</th>
				<td>${bpmNewFlowTrigger.userseting}</td>
			</tr>
			<tr>
				<th width="20%">描述:</th>
				<td>${bpmNewFlowTrigger.desc}</td>
			</tr>
		</table>
		</div>
	</div>
</body>
</html>

