<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>重置密码</title>
	<%@include file="/commons/include/form.jsp" %>
</head>
<body>
<div class="panel">
	<div class="panel-body">
		<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<th width="20%" colspan="2" style="text-align: center;">${sessionScope.remindMsg}</th>
			</tr>
			<tr>
				<th><a class="link save" href="${ctx}/platform/console/main.ht"><span></span>继续访问</a></th>
				<td><a class="link edit" href="${ctx}/platform/system/sysUser/commonResetPwdView.ht"><span></span>修改密码</a></td>
			</tr>
		</table>
	</div>
</div>
</body>
</html>
