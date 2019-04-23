
<%--
	time:2015-07-29 10:29:57
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<title>常用联系人组明细</title>
<%@include file="/commons/include/get.jsp"%>
<script type="text/javascript">
	//放置脚本
</script>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">常用联系人组详细信息</span>
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
				<th width="20%">组名:</th>
				<td>${messageLinkmanGroup.groupName}</td>
			</tr>
			<tr>
				<th width="20%">常用联系人:</th>
				<td>${messageLinkmanGroup.users}</td>
			</tr>
			<tr>
				<th width="20%">创建时间:</th>
				<td>
				<fmt:formatDate value="${messageLinkmanGroup.createTime}" pattern="yyyy-MM-dd"/>
				</td>
			</tr>
		</table>
		</div>
	</div>
</body>
</html>

