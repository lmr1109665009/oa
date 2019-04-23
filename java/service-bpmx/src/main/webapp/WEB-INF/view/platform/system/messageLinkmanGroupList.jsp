<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>常用联系人组管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bus/BusQueryRuleUtil.js" ></script>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">常用联系人组管理列表</span>
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
				<form id="searchForm" method="post" action="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__IS_QUERY__=0">
					<ul class="row">
						<li>
						<span class="label">组名:</span><input type="text" name="Q_groupName_SL"  class="inputText" />
					</li>
					<li><button class="btn">查询</button></li>
					</ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="messageLinkmanGroupList" id="messageLinkmanGroupItem" requestURI="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__FILTER_FLAG__=${busQueryRule.filterFlag}" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<f:col name="id">
					<display:column title="${checkAll}" media="html" style="width:30px;">
				  		<input type="checkbox" class="pk" name="id" value="${messageLinkmanGroupItem.id}">
					</display:column>
				</f:col>
				<f:col name="groupName">
					<display:column property="groupName" title="组名" sortable="true" sortName="GROUP_NAME"></display:column>
				</f:col>
				<f:col name="users">
					<display:column property="users" title="常用联系人" sortable="true" sortName="USERS" maxLength="80"></display:column>
				</f:col>
				<f:col name="createTime">
					<display:column  title="创建时间" sortable="true" sortName="CREATE_TIME">
						<fmt:formatDate value="${messageLinkmanGroupItem.createTime}" pattern="yyyy-MM-dd"/>
					</display:column>
				</f:col>
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
					<a href="edit.ht?id=${messageLinkmanGroupItem.id}" class="link edit">编辑</a> |
					<a href="get.ht?id=${messageLinkmanGroupItem.id}" class="link detail">详情</a>  | 
					<a href="del.ht?id=${messageLinkmanGroupItem.id}" class="link del">删除</a>
				</display:column>
			</display:table>
			<hotent:paging tableId="messageLinkmanGroupItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


