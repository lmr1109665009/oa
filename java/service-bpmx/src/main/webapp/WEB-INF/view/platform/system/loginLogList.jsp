<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>sys_login_log管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bus/BusQueryRuleUtil.js" ></script>
</head>
<body>
	<div class="panel">
		<c:if test="${!empty busQueryRule.filterList && fn:length(busQueryRule.filterList) >1}">
			<div class="l-tab-links">
				<ul style="left: 0px; ">
					<c:forEach items="${busQueryRule.filterList}" var="filter">
						<li tabid="${filter.key}" <c:if test="${busQueryRule.filterKey ==filter.key}"> class="l-selected"</c:if>>
							<a href="list.ht?__FILTERKEY__=${filter.key}" title="${filter.name}">${filter.desc}</a>
						</li>
					</c:forEach>
				</ul>
			</div>
		</c:if>
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">sys_login_log管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
					
					<div class="group" style="float: right;">
						<f:a onclick="BusQueryRuleUtil.saveFilter({tableName:'${busQueryRule.tableName}',filterKey:'${busQueryRule.filterKey}',filterFlag:'${busQueryRule.filterFlag}'})" alias="saveFilter_${busQueryRule.tableName}" css="link save"  showNoRight="false"><span></span>保存条件</f:a>
						<f:a onclick="BusQueryRuleUtil.myFilter({tableName:'${busQueryRule.tableName}',url:'${busQueryRule.url}'})" alias="myFilter_${busQueryRule.tableName}" css="link fiter"  showNoRight="false"><span></span>过滤器</f:a>
						<f:a onclick="BusQueryRuleUtil.eidtDialog({tableName:'${busQueryRule.tableName}'})" alias="customQuery_${busQueryRule.tableName}" css="link setting" showNoRight="false" ><span></span>高级查询</f:a>
					</div>
				</div>
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__IS_QUERY__=0">
					<ul class="row">
					<li><span class="label">账号:</span><input type="text" name="Q_account_S"  class="inputText" /></li>
					<li><button class="btn">查询</button></li></ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="loginLogList" id="loginLogItem" requestURI="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__FILTER_FLAG__=${busQueryRule.filterFlag}" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<f:col name="id">
					<display:column title="${checkAll}" media="html" style="width:30px;">
				  		<input type="checkbox" class="pk" name="id" value="${loginLogItem.id}">
					</display:column>
				</f:col>
				<f:col name="account">
					<display:column property="account" title="用户账号" sortable="true" sortName="ACCOUNT" maxLength="80"></display:column>
				</f:col>
				<f:col name="logintime">
					<display:column  title="登录时间" sortable="true" sortName="LOGINTIME">
						<fmt:formatDate value="${loginLogItem.loginTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</display:column>
				</f:col>
				<f:col name="ip">
					<display:column property="ip" title="IP地址" sortable="true" sortName="IP" maxLength="80"></display:column>
				</f:col>
				<f:col name="status">
					<display:column property="status" title="登录状态" sortable="true" sortName="STATUS"></display:column>
				</f:col>
				<f:col name="desc">
					<display:column property="desc" title="描叙" sortable="true" sortName="DESC_" maxLength="80"></display:column>
				</f:col>
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
					<a href="get.ht?id=${loginLogItem.id}" class="link detail">详情</a>  | 
					<a href="del.ht?id=${loginLogItem.id}" class="link del">删除</a>
				</display:column>
			</display:table>
			<hotent:paging tableId="loginLogItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


