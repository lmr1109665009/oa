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
<script type="text/javascript" src="websigncontrolDialogController.js"></script>

<script type="text/javascript">
	var scope;
	var element = editor.selection.getStart();//当前对象
	var targetEl = dialog.targetEl;//编辑模式会传的对象

	//不知道为什么有时候会选择到一个在html中都不存在的<br>标签，先手动找其父节点吧
	if ($(editor.selection.getStart()).is("br")) {
		var p= element.parentElement;
		$(element).remove();
		element=p;
	}

	$(function() {
		scope = getScope();

		initName();

		//return true 关闭窗口，return false 不关闭窗口		
		dialog.onok = function() {
			var frm = $('#frmSubmit').form();
			if (!frm.valid())
				return false;

			var span;
			if (!targetEl) {//新增模式
				span = $("<span class='websigncontrol' parser='websignedit'></span>");//TODO web印章的解释器还没写
				$(element).append(span);
			} else {//编辑
				span = $(targetEl);
				span.empty();
			}
			
			span.attr("external", externalEncode(JSON.stringify(scope.external)));
			var input =$("<input type='text'/>");
			input.css("width",scope.external.width+scope.external.widthUnit);
			input.css("height",scope.external.height+scope.external.heightUnit);
			span.append(input);
			
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
						<input ng-model="external.comment" type="text" validate="{required:true}">
					</td>
					<th>字段名称:</th>
					<td>
						<input ng-model="external.name" ht-pinyin="external.comment" type="text" validate="{required:true,varirule:true}">
					</td>
				</tr>

				<tr>
					<th>数据类型:</th>
					<td>
						<select ng-model="external.dbType.type">
							<option value="varchar">文字</option>
							<option value="clob">大文本</option>
						</select>
					</td>

					<th>数据格式:</th>
					<!-- 字符串类型 -->
					<td class="dbformat_td" ng-if="external.dbType.type=='varchar'">
						长度:
						<input ng-model="external.dbType.length" style="width: 60px;" type="text">
					</td>
				</tr>

				<tr>
					<th>选项:</th>
					<td colspan="4">
						<label>
							<input type="checkbox" ng-model="external.isRequired" ng-true-value="1" ng-false-value="0" />
							必填&nbsp;
						</label>
						<label>
							<input type="checkbox" ng-model="external.isList" ng-true-value="1" ng-false-value="0" />
							显示到列表&nbsp;
						</label>
						<label>
							<input type="checkbox" ng-model="external.isQuery" ng-true-value="1" ng-false-value="0" />
							作为查询条件&nbsp;
						</label>
						<label>
							<input type="checkbox" ng-model="external.isFlowVar" ng-true-value="1" ng-false-value="0" />
							是否流程变量&nbsp;
						</label>
						<label>
							<input type="checkbox" ng-model="external.isReference" ng-true-value="1" ng-false-value="0" />
							作为超连接&nbsp;
						</label>
						<label>
							<input type="checkbox" ng-model="external.isWebSign" ng-true-value="1" ng-false-value="0" />
							是否支持Web印章验证
						</label>
					</td>
				</tr>
				
				<tr>
					<th>值来源:</th>
					<td colspan="3">
						<select ng-model="external.valueFrom.value">
							<option value="0">表单输入</option>
						</select>
					</td>
				</tr>
				<!-- 表单输入 -->
				<tr class="valuefrom0" ng-if="external.valueFrom.value=='0'">
					<th>验证规则:</th>
					<td colspan="3">
						<select ng-model="external.valueFrom.content" ng-options="m.name as m.name for m in ruleList">
							<option value="">无</option>
						</select>
					</td>
				</tr>

				<tr class="style_tr">
					<th>控件宽度:</th>
					<td>
						<input type="text" ng-model="external.width" style="width: 120px;">
						<select style="width: 40px;" ng-model="external.widthUnit">
							<option value="px">px</option>
							<option value="%">%</option>
						</select>
					</td>
					<th>控件高度:</th>
					<td>
						<input type="text" ng-model="external.height" style="width: 120px;">
						<select style="width: 40px;" ng-model="external.heightUnit">
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