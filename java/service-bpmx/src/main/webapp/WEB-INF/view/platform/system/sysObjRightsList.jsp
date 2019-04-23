<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>对象权限表管理</title>
<%@include file="/commons/include/get.jsp" %>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">对象权限表管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link add" href="edit.ht"><span></span>添加</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link update update-hide" id="btnUpd" action="edit.ht"><span></span>修改</a></div>
				    <div class="l-bar-separator"></div>
					<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
				</div>	
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht">
					<ul class="row">
						<li><span class="label">对象类型:</span><input type="text" name="Q_objType_SL"  class="inputText" /></li>
						<li><span class="label">权限对象ID:</span><input type="text" name="Q_objectId_SL"  class="inputText" /></li>
						<li><span class="label">授权人ID:</span><input type="text" name="Q_ownerId_SL"  class="inputText" /></li>
						<li><span class="label">授权人:</span><input type="text" name="Q_owner_SL"  class="inputText" /></li>
						<li><span class="label">权限类型:</span><input type="text" name="Q_rightType_SL"  class="inputText" /></li>
					<li><button class="btn">查询</button></li></ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="sysObjRightsList" id="sysObjRightsItem" requestURI="list.ht" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<display:column title="${checkAll}" media="html" style="width:30px;">
			  		<input type="checkbox" class="pk" name="id" value="${sysObjRightsItem.id}">
				</display:column>
				<display:column property="objType" title="对象类型" sortable="true" sortName="OBJ_TYPE"></display:column>
				<display:column property="objectId" title="权限对象ID" sortable="true" sortName="OBJECT_ID"></display:column>
				<display:column property="ownerId" title="授权人ID" sortable="true" sortName="OWNER_ID"></display:column>
				<display:column property="owner" title="授权人" sortable="true" sortName="OWNER"></display:column>
				<display:column property="rightType" title="权限类型" sortable="true" sortName="RIGHT_TYPE"></display:column>
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
					<a href="edit.ht?id=${sysObjRightsItem.id}" class="link edit">编辑</a> |
					<a href="get.ht?id=${sysObjRightsItem.id}" class="link detail">详情</a>  | 
					<a href="del.ht?id=${sysObjRightsItem.id}" class="link del">删除</a>
				</display:column>
			</display:table>
			<hotent:paging tableId="sysObjRightsItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


