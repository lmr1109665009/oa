<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<%@include file="/commons/include/get.jsp" %>
<title>用户签名图片管理</title>
<style type="text/css">
	html,body{
		overflow:auto;
	}
	.websign-wrap{
		padding: 5px;
		text-align: center;
	}
	.websign-img{
		display: inline-block;
		max-width:100%;
		height: auto;
	}
</style>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">用户签名图片列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link add" href="edit.ht"><span></span>添加</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
				</div>	
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht">
					<ul class="row">
					<li>
						<span class="label">用户名:</span><input type="text" name="Q_fullname_SL"  class="inputText" value="${param['Q_fullname_SL']}"/>
						</li>
					<%--<li><span class="label">字号:</span><input type="text" name="Q_aliasName_SL"  class="inputText" value="${param['Q_aliasName_SL']}"/>
						</li>--%>
					<li><span class="label">邮箱:</span><input type="text" name="Q_email_SL"  class="inputText" value="${param['Q_email_SL']}"/>
						</li>
					<li><button class="btn">查询</button></li></ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="userList" id="userItem" requestURI="list.ht" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<display:column title="${checkAll}" media="html" style="width:30px;">
			  		<input type="checkbox" class="pk" name="id" value="${userItem.userId}">
				</display:column>
				<display:column property="fullname" title="用户名" sortable="true" sortName="FULLNAME">
					${userItem.fullname}
				</display:column>
				<%--<display:column property="aliasName" title="字号" maxLength="80"></display:column>--%>
				<display:column title="签章" maxLength="80">
					<div class="websign-wrap"><img class="websign-img" src="${staticUrl}${userItem.webSignUrl}"></div>
				</display:column>
				<display:column property="account" title="帐号" sortable="true" sortName="ACCOUNT" maxLength="80"></display:column>
				<display:column property="email" title="邮箱"></display:column>
				<display:column title="状态" sortable="true" sortName="status">
					<c:choose>
						<c:when test="${userItem.status==1}">
							<span class="green">激活</span>
						</c:when>
						<c:when test="${sysUserItem.status==0}">
							<span class="red">禁用</span>
						</c:when>
						<c:otherwise>
							<span class="red">删除</span>
						</c:otherwise>
					</c:choose>
				</display:column>
				<display:column title="操作" media="html" style="width:100px" class="opsBtnb">
					<a class="link" href="edit.ht?id=${userItem.userId}" class="link edit">编辑</a>
				</display:column>
			</display:table>
			<hotent:paging tableId="userItem"/>
		</div>
	</div>
</body>
</html>


