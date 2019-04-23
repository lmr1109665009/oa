<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>

<html>
<head>
	<title>历史明细</title>
	<%@include file="/commons/include/form.jsp" %>
	<%-- <link  rel="stylesheet" type="text/css" href="${ctx}/js/codemirror/lib/codemirror.css" > --%>
	<f:link href="codemirror/lib/codemirror.css"></f:link>
	<script type="text/javascript" src="${ctx}/js/codemirror/lib/codemirror.js"></script>
	<script type="text/javascript" src="${ctx}/js/codemirror/mode/xml/xml.js"></script>
	<script type="text/javascript" src="${ctx}/js/codemirror/mode/javascript/javascript.js"></script>
    <script type="text/javascript" src="${ctx}/js/codemirror/mode/css/css.js"></script>
    <script type="text/javascript" src="${ctx}/js/codemirror/mode/htmlmixed/htmlmixed.js"></script>
	<script type="text/javascript">
	var editor=null;
	$(function() {
	
		var width = $("#template").width();
		var height = $("#template").height();
		editor = CodeMirror.fromTextArea(document.getElementById("template"), {
			mode: "text/html",
			tabMode: "indent",
			lineNumbers: true
		 });
		editor.setSize(width,height);
	});
	</script>
</head>
<body>
<div class="panel">
		<div class="panel-top">
			
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group">历史明细</div>
				</div>
			</div>
		</div>
		<div class="panel-body">
			<form id="historyTemplateForm" method="post" action="saveTemplate.ht" >
				<table class="table-detail">
					
					<tr>
						<th width="5%" nowrap="nowrap">模板</th>
						<td >
							<textarea id="template" name="template" style="width: 99%;height: 650px;">${sysHistoryData.content }</textarea>
						</td>
					</tr>
				</table>
				
			</form>
		</div>
</div>
</body>
</html>
