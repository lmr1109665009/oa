<%@page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" >
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<%@include file="/commons/include/form.jsp"%>
<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
<script type="text/javascript" src="${ctx}/js/ueditor1433/dialogs/internal.js"></script>
<link rel="stylesheet" type="text/css" href="../edit.css">
<link rel="stylesheet" type="text/css" href="${ctx}/styles/default/css/form.css">
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/ScriptDialog.js"></script>
<script type="text/javascript" src="../util.js"></script>
<!-- ngjs -->
<script type="text/javascript" src="${ctx}/js/angular/angular.min.js"></script>
<script type="text/javascript" src="${ctx}/js/angular/service/baseServices.js"></script>
<script type="text/javascript" src="${ctx}/js/angular/service/arrayToolService.js"></script>
<script type="text/javascript" src="${ctx}/js/angular/service/commonListService.js"></script>
<script type="text/javascript" src="opinionDialogController.js"></script>

<script type="text/javascript">
	var scope;
	var element = editor.selection.getStart();//当前对象

	$(function() {
		scope = getScope();

		//return true 关闭窗口，return false 不关闭窗口		
		dialog.onok = function() {
			var frm = $('#frmSubmit').form();
			if (!frm.valid())
				return false;
			var textarea = $("<textarea></textarea>");
			textarea.attr("name",scope.name);
			textarea.attr("opinionname",scope.comment);
			textarea.attr("parser","taskopinionedit");
			textarea.attr("widthunit",scope.widthUnit);
			textarea.attr("heightunit",scope.heightunit);

			textarea.css("width",scope.width+scope.widthUnit);
			textarea.css("height",scope.height+scope.heightUnit);
			
			$(element).append(textarea);
			
			return true;
		};
	});
</script>
</head>
<body ng-app="app" ng-controller="ctrl" ng-init="init()">
	<form id="frmSubmit">
		<table class="edit_table">
			<tbody>
				<tr>
					<th>字段注释:</th>
					<td>
						<input ng-model="comment" type="text" validate="{required:true}">
					</td>
				</tr>
				<tr>
					<th>字段名称:</th>
					<td>
						<input ng-model="name" ht-pinyin="comment" type="text" validate="{required:true,varirule:true}">
					</td>
				</tr>

				<tr class="style_tr">
					<th>控件宽度:</th>
					<td>
						<input type="text" ng-model="width" style="width: 120px;">
						<select style="width: 40px;" ng-model="widthUnit">
							<option value="px">px</option>
							<option value="%">%</option>
						</select>
					</td>
				</tr>
				
				<tr class="style_tr">
					<th>控件高度:</th>
					<td>
						<input type="text" ng-model="height" style="width: 120px;">
						<select style="width: 40px;" ng-model="heightUnit">
							<option value="px">px</option>
							<option value="%">%</option>
						</select>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>