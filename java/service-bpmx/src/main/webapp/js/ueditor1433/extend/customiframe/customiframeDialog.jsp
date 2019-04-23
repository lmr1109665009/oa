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
<script type="text/javascript" src="customiframeDialogController.js"></script>

<script type="text/javascript">
	var scope;
	var element = editor.selection.getStart();//当前对象
	var targetEl = dialog.targetEl;//编辑模式会传的对象

	//不知道为什么有时候会选择到一个在html中都不存在的<br>标签，先手动找其父节点吧
	if ($(editor.selection.getStart()).is("br")) {
		element = element.parentElement;//
	}

	$(function() {
		scope = getScope();

		initName();

		//return true 关闭窗口，return false 不关闭窗口		
		dialog.onok = function() {
			scope.external.iframeUrl = __ctx+scope.external.url;
			var frm = $('#frmSubmit').form();
			if (!frm.valid())
				return false;
			var div;
			var img = $("<img id='tempCode' src='"+__ctx+"/styles/default/images/iframe.png'/>");
			if (!targetEl) {//新增模式
				div = $("<div class='customiframe' style='width:250px;height:80px' parser='customiframe'></div>");
				div.attr("external", externalEncode(JSON.stringify(scope.external)));
				$(element).append(div);
			} else {//编辑
				div = $(targetEl);
				div.empty();
				div.attr("external", externalEncode(JSON.stringify(scope.external)));
			}
			div.append(img);
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
					<th>url地址:<span style="color:red">*</span></th>
					<td colspan="3">
						<input ng-model="external.url" validate="{required:true}" value="">
						<div class="tipbox">
							<a class="tipinfo">
								<span>填入url，如："http://www.hotent.com/"</span>
							</a>
						</div>
					</td>
				</tr>
				<tr class="valuefrom0">
					<th>样式类名:</th>
					<td colspan="3">
						<input ng-model="external.iframeClass" value="">
						<div class="tipbox">
							<a class="tipinfo">
								<span>填入类名，如："class1 class2"</span>
							</a>
						</div>
					</td>
				</tr>
				<tr class="valuefrom0">
					<th>样式:</th>
					<td colspan="3">
						<table style="width: 100%">
							<tr class="editable-tr">
								<td>
									高度:
									<input ng-model="external.height" type="text" style="width: 60px"/>
									&nbsp
									宽度:
									<input ng-model="external.width" type="text"  style="width: 60px"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<th>选项:</th>
					<td colspan="3">
						<table style="width: 100%">
							<tr>
								<td colspan="2">
									<a href="javaScript:void(0)" class="link add" ng-click="external.options.push({'source':'fixed'})">添加</a>
								</td>
							</tr>
							<tr class="editable-tr" ng-repeat="item in external.options track by $index">
								<td>
									参数key:
									<input ng-model="item.key" type="text" style="width: 100px"/>
									参数值:
									<input ng-model="item.value" type="text" ng-if="item.source == 'fixed'"/>
									<select ng-model="item.value" ng-if="item.source == 'field'">
										<option  value="pkField">主键</option>
										<option ng-repeat="field in maintable.fieldList" value="{{field.fieldName}}">{{field.fieldDesc}}</option>
									</select>
									值来源:
									<select ng-model="item.source" ng-change="item.value = ''" style="width: 100px">
										<option value="fixed" selected="selected">固定值</option>
										<option value="field">主表字段</option>
									</select>
								</td>
								<td>
									<a href="javaScript:void(0)" class="link del" ng-click="ArrayTool.del($index,external.options)">移除</a>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>