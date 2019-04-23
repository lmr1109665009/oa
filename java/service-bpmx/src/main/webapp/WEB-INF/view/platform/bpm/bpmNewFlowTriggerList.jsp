<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>触发新流程配置管理</title>
<%@include file="/commons/include/get.jsp" %>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">触发新流程配置管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link add" href="edit.ht"><span></span>添加</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link update update-hide" id="btnUpd" action="edit.ht"><span></span>修改</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
				</div>	
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht">
					<ul class="row">
						<li><span class="label">名子:</span><input type="text" name="Q_name_SL"  class="inputText" /></li>
						<li><span class="label">节点:</span><input type="text" name="Q_nodeid_SL"  class="inputText" /></li>
						<li><span class="label">触发动作:</span><input type="text" name="Q_action_SL"  class="inputText" /></li>
						<li><span class="label">流程key:</span><input type="text" name="Q_flowkey_SL"  class="inputText" /></li>
						<li><span class="label">被触发流程:</span><input type="text" name="Q_triggerflowkey_SL"  class="inputText" /></li>
						<li><span class="label">映射配置:</span><input type="text" name="Q_jsonmaping_SL"  class="inputText" /></li>
						<li><span class="label">发起人配置:</span><input type="text" name="Q_userseting_SL"  class="inputText" /></li>
						<li><span class="label">描述:</span><input type="text" name="Q_desc_SL"  class="inputText" /></li>
					<li><button class="btn">查询</button></li></ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="bpmNewFlowTriggerList" id="bpmNewFlowTriggerItem" requestURI="list.ht" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<display:column title="${checkAll}" media="html" style="width:30px;">
			  		<input type="checkbox" class="pk" name="id" value="${bpmNewFlowTriggerItem.id}">
				</display:column>
				<display:column property="name" title="名子" sortable="true" sortName="name" maxLength="80"></display:column>
				<display:column property="nodeid" title="节点" sortable="true" sortName="nodeId" maxLength="80"></display:column>
				<display:column property="action" title="触发动作" sortable="true" sortName="action"></display:column>
				<display:column property="flowkey" title="流程key" sortable="true" sortName="flowkey" maxLength="80"></display:column>
				<display:column property="triggerflowkey" title="被触发流程" sortable="true" sortName="triggerFlowkey" maxLength="80"></display:column>
				<display:column property="jsonmaping" title="映射配置" sortable="true" sortName="jsonMaping" maxLength="80"></display:column>
				<display:column property="userseting" title="发起人配置" sortable="true" sortName="userSeting" maxLength="80"></display:column>
				<display:column property="desc" title="描述" sortable="true" sortName="desc" maxLength="80"></display:column>
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
					<a href="edit.ht?id=${bpmNewFlowTriggerItem.id}" class="link edit">编辑</a> |
					<a href="get.ht?id=${bpmNewFlowTriggerItem.id}" class="link detail">详情</a>  |
					<a href="del.ht?id=${bpmNewFlowTriggerItem.id}" class="link del">删除</a> 
				</display:column>
			</display:table>
			<hotent:paging tableId="bpmNewFlowTriggerItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


