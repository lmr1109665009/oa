<%
	//作用:查看某一流程所有版本历史
	//作者:csx
%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>流程定义扩展管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript">
	function openEditPage(id,nodeId,actdefId,defId){
		
		var url=__ctx+"/platform/bpm/bpmNodeSql/edit.ht?id="+id+"&nodeId="+nodeId+"&actdefId="+actdefId+"&defId="+defId;
		DialogUtil.open({
			height:650,
			width: 800,
			title : "节点sql设置",
			url: url,
			isResize: true
		});
	}
</script>
</head>
<body>
     <jsp:include page="incDefinitionHead.jsp">
	 	<jsp:param value="流程节点sql" name="title"/>
	 </jsp:include>
	 <div class="panel-container">
     	<f:tab curTab="nodeSql" tabName="flow"/>
		<div class="panel">
			<display:table name="bpmNodeSqlList" id="bpmNodeSqlItem" requestURI="list.ht" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<display:column title="${checkAll}" media="html" style="width:30px;">
			  		<input type="checkbox" class="pk" name="id" value="${bpmNodeSqlItem.id}">
				</display:column>
				<display:column property="name" title="名称" sortable="true" sortName="NAME" maxLength="80"></display:column>
				<display:column property="nodeName" title="节点ID" sortable="true" sortName="NODEID" maxLength="80"></display:column>
				<display:column property="dsAlias" title="数据源别名" sortable="true" sortName="DSALIAS" maxLength="80"></display:column>
				<display:column property="action" title="触发时机" sortable="true" sortName="ACTION"></display:column>
				<display:column property="sql" title="SQL语句" sortable="true" sortName="SQL" maxLength="80"></display:column>
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
					<a href="javaScript:openEditPage('${bpmNodeSqlItem.id}','${bpmNodeSqlItem.nodeId}','${bpmNodeSqlItem.actdefId}','${param.defId}');" class="link edit">编辑</a> |
					<a href="del.ht?id=${bpmNodeSqlItem.id}" class="link del">删除</a>
				</display:column>
			</display:table>
		</div>	
	</div> <!-- end of panel -->
</body>
</html>


