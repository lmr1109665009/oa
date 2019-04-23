
<%--
	time:2015-07-01 14:10:56
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<title>登录日志明细</title>
<%@include file="/commons/include/get.jsp"%>
<script type="text/javascript">
	//放置脚本
</script>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">登录日志详细信息</span>
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
				<th width="20%">账号:</th>
				<td>${loginLog.account}</td>
			</tr>
			<tr>
				<th width="20%">登录时间:</th>
				<td>
				<fmt:formatDate value="${loginLog.loginTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
			<tr>
				<th width="20%">IP地址:</th>
				<td>${loginLog.ip}</td>
			</tr>
			<tr>
				<th width="20%">登录状态:</th>
				<td>
					${loginLog.status}
					<%-- <c:if test="${loginLog.status==0}">
						登录成功
					</c:if>
					<c:if test="${loginLog.status==-1}">
						验证码错误
					</c:if>
					<c:if test="${loginLog.status==-2}">
						账号密码为空
					</c:if>
					<c:if test="${loginLog.status==-3}">
						用户名密码输入错误
					</c:if>
					<c:if test="${loginLog.status==-4}">
						用户被锁定
					</c:if>
					<c:if test="${loginLog.status==-5}">
						用户被禁用
					</c:if>
					<c:if test="${loginLog.status==-6}">
						用户已过期
					</c:if>
					<c:if test="${loginLog.status==-7}">
						密码策略没通过
					</c:if> --%>
				</td>
			</tr>
			<tr>
				<th width="20%">描叙:</th>
				<td>${loginLog.desc}</td>
			</tr>
		</table>
		</div>
	</div>
</body>
</html>

