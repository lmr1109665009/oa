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
<script type="text/javascript" src="custombuttonDialogController.js"></script>

<script type="text/javascript">
	var scope;
	var element = editor.selection.getStart();//当前对象
	var targetEl = dialog.targetEl;//编辑模式会传的对象
	
	//不知道为什么有时候会选择到一个在html中都不存在的<br>标签，先手动找其父节点吧
	if($(editor.selection.getStart()).is("br")){
		var p= element.parentElement;
		$(element).remove();
		element=p;
	}

	$(function() {
		scope = getScope();

		//return true 关闭窗口，return false 不关闭窗口		
		dialog.onok = function() {
			var frm = $('#frmSubmit').form();
			if (!frm.valid())
				return false;
			if(!scope.icon){
				$.ligerDialog.error("请选择图标","错误提示");
				return false;
			}
			
			var a = $("<a parser='custombtntable' href='javaScript:void(0)'>"+scope.comment+"</a>");
			a.attr("class",scope.icon);
			if($(element).is("span")){
				$(element).after(a);
			}else{
				$(element).append(a);
			}
			
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
					<th>按钮标签:</th>
					<td>
						<input ng-model="comment" type="text" validate="{required:true}">
					</td>
				</tr>
				
				<tr>
					<th>按钮图标:</th>
					<td>
						<a href="javascript:void(0);" ng-click="openIconDialog()" class="{{icon}}">选择图标</a>
					</td>
				</tr>

			</tbody>
		</table>
	</form>
</body>
</html>