<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>编辑 书签</title>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
</head>
<body>
<div class="panel">
	<div class="panel-top">
	</div>
	<div class="panel-body">
		<form id="sysKnowledgeMarkForm" method="post" action="save.ht">
			<table class="table-detail" cellpadding="0" cellspacing="0" border="0" type="main">
				<tr>
					<th width="20%">书签: </th>
					<td><input type="text" id="bookmark" name="bookmark" value="${sysKnowledgeMark.bookmark}"  class="inputText" validate="{required:false,maxlength:765}"  />
					</td>
				</tr>
			</table>
			<input type="hidden" name="id" value="${sysKnowledgeMark.id}" />
		</form>
	</div>
</div>
</body>
</html>
