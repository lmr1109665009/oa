<%@page import="com.hotent.platform.model.system.GlobalType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>知识库管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bus/BusQueryRuleUtil.js" ></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerComboBox.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/htCatCombo.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.ztree.js"></script>
<script type="text/javascript">
</script>
</head>
<body>
	<div class="panel">
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="knowList" id="knowItem"   sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<display:column media="html" title="主题"  sortable="false" sortName="SUBJECT">
					<a href="#" onclick="$.openFullWindow('get.ht?id=${knowItem.id}')" >${knowItem.subject}</a>
				</display:column>
				<display:column property="typeName" title="分类名称" sortable="false" sortName="typeName"></display:column>
				<display:column property="creator" title="创建人" sortable="false" sortName="CREATOR"></display:column>
			</display:table>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</div>
</body>
</html>


