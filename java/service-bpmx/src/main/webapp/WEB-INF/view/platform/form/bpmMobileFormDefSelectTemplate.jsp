
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="/commons/include/get.jsp" %>
<title>选择模板</title>
<style type="text/css">
	html,body{height:100%;width:100%; overflow: auto;overflow-x:hidden;}
</style>
<script type="text/javascript">
</script>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">选择表单模版</span>
			</div>
		
		</div>
		<div class="panel-body">
			<form id="frmDefInfo">
				
					<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
						<c:if test="${mainTable != null}">
						<tr>
							<th width="30%">主表模板:</th>
							<td>
								<select id="main" templateId="templateId" tableId="${mainTable.tableId }">
									<c:forEach items="${mainTableTemplates}" var="bpmFormTemplate">
										<option value="${bpmFormTemplate.alias}">${bpmFormTemplate.templateName}</option>
									</c:forEach>
								</select>
							</td>
						</tr>
						</c:if>
						<c:forEach items="${subTables}" var="subTable">
						<tr>
							<th width="30%">${subTable.tableDesc }模板:</th>
							<td>
								<select templateId="templateId" tableId="${subTable.tableId }">
									<c:forEach items="${subTableTemplates}" var="bpmFormTemplate">
										<option value="${bpmFormTemplate.alias}">${bpmFormTemplate.templateName}</option>
									</c:forEach>
								</select>
							</td>
						</tr>					
						</c:forEach>					
					</table>
				</div>
				
			</form>
				
	</div> <!-- end of panel -->
</body>
</html>


