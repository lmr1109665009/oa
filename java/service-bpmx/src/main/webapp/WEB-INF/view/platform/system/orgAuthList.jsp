<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>组织管理员</title>
<%@include file="/commons/include/get.jsp" %>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">组织管理员列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
				<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
				<div class="l-bar-separator" id="${action }"></div>
				<c:choose>
					<c:when test="${param.isGrade eq true }">
					    <c:if test="${not empty userAuth && fn:contains(userAuth.orgauthPerms, 'add')}">
							<div class="group"><a class="link add" href="edit.ht?orgId=${orgId}&isGrade=${grade}&topOrgId=${topOrgId}"><span></span>添加</a></div>
							<div class="l-bar-separator"></div>
						</c:if>
						<c:if test="${not empty userAuth && fn:contains(userAuth.orgauthPerms, 'edit')}">
							<div class="group"><a class="link update update-hide" id="btnUpd" action="edit.ht?orgId=${orgId}&isGrade=${grade}&topOrgId=${topOrgId}"><span></span>修改</a></div>
							<div class="l-bar-separator"></div>
						</c:if>
						<c:if test="${not empty userAuth && fn:contains(userAuth.orgauthPerms, 'del')}">
							<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
						</c:if>
					</c:when>
					<c:otherwise>
					<div class="group"><a class="link add" href="edit.ht?orgId=${orgId}&isGrade=${grade}&topOrgId=${topOrgId}"><span></span>添加</a></div>
						<div class="l-bar-separator"></div>
						<div class="group"><a class="link update update-hide" id="btnUpd" action="edit.ht?orgId=${orgId}&isGrade=${grade}&topOrgId=${topOrgId}"><span></span>修改</a></div>
						<div class="l-bar-separator"></div>
						<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
						
					</c:otherwise>
				</c:choose>
				<div class="l-bar-separator"></div>
					<div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
			</div>
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht">
					<ul class="row">
						<li><span class="label">管理员:</span><input type="text" name="Q_userName_SL"  class="inputText" value="${param['Q_userName_SL']}"/></li>
						<li><span class="label">管理组织:</span><input type="text" name="Q_orgName_SL"  class="inputText" value="${param['Q_orgName_SL']}"/></li>
					<li><button class="btn">查询</button></li></ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="orgAuthList" id="orgAuthItem" requestURI="list.ht" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<display:column title="${checkAll}" media="html" style="width:30px;">
			  		<input type="checkbox" class="pk" name="id" value="${orgAuthItem.id}">
				</display:column>
				<display:column property="userName" title="管理员" sortable="true" sortName="USER_ID"></display:column>
				<display:column property="orgName" title="管理组织" sortable="true" sortName="ORG_ID"></display:column>
				<display:column property="orgPerms" title="子组织操作权限" sortable="true" sortName="ORG_PERMS"></display:column>
				<display:column property="userPerms" title="用户操作权限" sortable="true" sortName="USER_PERMS"></display:column>
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
					<c:choose>
						<c:when test="${param.isGrade eq true}">
						  <c:if test="${not empty userAuth && fn:contains(userAuth.orgauthPerms, 'edit')}">
						  	<a href="edit.ht?id=${orgAuthItem.id}&orgId=${orgId}&isGrade=${grade}&topOrgId=${topOrgId}" class="link edit">编辑</a> |
						  </c:if>
						  <c:if test="${not empty userAuth && fn:contains(userAuth.orgauthPerms, 'del')}">
						  	<a href="del.ht?id=${orgAuthItem.id}" class="link del">删除</a>
						  </c:if>
						</c:when>
						<c:otherwise>
							<a href="edit.ht?id=${orgAuthItem.id}&orgId=${orgId}&isGrade=${grade}&topOrgId=${topOrgId}" class="link edit">编辑</a> |
						    <a href="del.ht?id=${orgAuthItem.id}" class="link del">删除</a>
						</c:otherwise>
					</c:choose>
				</display:column>
			</display:table>
		<hotent:paging tableId="orgAuthItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


