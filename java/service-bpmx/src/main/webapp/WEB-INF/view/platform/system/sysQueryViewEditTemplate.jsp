<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>

<html>
<head>
	<title>编辑 数据模板的模板</title>
	<%@include file="/commons/include/form.jsp" %>
	<%-- <link  rel="stylesheet" type="text/css" href="${ctx}/js/codemirror/lib/codemirror.css" > --%>
	<f:link href="codemirror/lib/codemirror.css"></f:link>
	<script type="text/javascript" src="${ctx}/js/codemirror/lib/codemirror.js"></script>
	<script type="text/javascript" src="${ctx}/js/codemirror/mode/xml/xml.js"></script>
	<script type="text/javascript" src="${ctx}/js/codemirror/mode/javascript/javascript.js"></script>
    <script type="text/javascript" src="${ctx}/js/codemirror/mode/css/css.js"></script>
    <script type="text/javascript" src="${ctx}/js/codemirror/mode/htmlmixed/htmlmixed.js"></script>
    <script type="text/javascript" src="${ctx}/js/hotent/platform/system/ShareDialog.js"></script>
    
	<script type="text/javascript">
		var editor=null;
		$(function() {
			var options={};
			if(showResponse){
				options.success=showResponse;
			}
			$('#sysQueryViewForm').ajaxForm(options);
			$("a.save").click(function() {
				editor.save();
				$('#sysQueryViewForm').submit();
			});
			var width = $("#template").width();
			var height = $("#template").height();
			editor = CodeMirror.fromTextArea(document.getElementById("template"), {
				mode: "text/html",
				tabMode: "indent",
				lineNumbers: true
			 });
			editor.setSize(width,height);
			
			$("#btnSearch").click(function(){
				ShareDialog.showHis(${sysQueryView.id },restoreData)
			})
		});
		
		function showResponse(responseText) {
			var obj = new com.hotent.form.ResultMessage(responseText);
			if (obj.isSuccess()) {
				$.ligerDialog.confirm( obj.getMessage()+",是否继续操作","提示信息", function(rtn) {
					if(rtn){
						/* this.close(); */
					}else{
						this.close();
					}
				});
			} else {
				$.ligerDialog.err('出错信息',"编辑自定义表管理显示模板失败",obj.getMessage());
			}
		}
		
		function restoreData(data){
			editor.setValue(data);
		}
	</script>
</head>
<body>
<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">编辑自定义表管理显示模板</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link save" href="javascript:;"><span></span>保存</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link search" id="btnSearch" href="javascript:;"><span></span>选择历史模版</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link close" href="javascript:;" onclick="window.close();"><span></span>关闭</a></div>
				</div>
			</div>
		</div>
		<div class="panel-body">
			<form id="sysQueryViewForm" method="post" action="saveTemplate.ht" >
				<table class="table-detail">
					
					<tr>
						<th width="5%" nowrap="nowrap">模板</th>
						<td >
							<textarea id="template" name="template" style="width: 99%;height: 700px;">${sysQueryView.template }</textarea>
						</td>
					</tr>
				</table>
				<input name="id" type="hidden" value="${sysQueryView.id }"/>
			</form>
		</div>
</div>
</body>
</html>
