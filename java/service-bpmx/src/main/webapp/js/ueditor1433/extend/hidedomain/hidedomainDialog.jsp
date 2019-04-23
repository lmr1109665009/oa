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
<script type="text/javascript" src="hidedomainDialogController.js"></script>

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

		initName();

		//return true 关闭窗口，return false 不关闭窗口		
		dialog.onok = function() {
			var frm = $('#frmSubmit').form();
			if (!frm.valid())
				return false;
			if (!targetEl) {//新增模式
				var span = $("<span class='hidedomain' parser='hiddenedit'><input type='text'/></span>");
				span.attr("external", externalEncode(JSON.stringify(scope.external)));
				$(element).append(span);
			} else {//编辑
				$(targetEl).attr("external", externalEncode(JSON.stringify(scope.external)));
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
							<option value="1">脚本运算(显示)</option>
							<option value="2">脚本运算(不显示)</option>
							<option value="3">流水号</option>
						</select>
					</td>
				</tr>
				<!--脚本运算-->
				<tr class="valuefrom12" ng-if="external.valueFrom.value=='1'||external.valueFrom.value=='2'">
					<th>脚本(显示):</th>
					<td colspan="3">
						<div>
							<a href="javascript:void(0);" ng-click="addScript()" class="link var" title="常用脚本">常用脚本</a>
						</div>
						<textarea ng-model="external.valueFrom.content" cols="50" rows="5"></textarea>
					</td>
				</tr>

				<tr class="valuefrom3" ng-if="external.valueFrom.value=='3'">
					<th>流水号:</th>
					<td colspan="3">
						<select ng-model="external.valueFrom.content" ng-options="m.alias as m.name for m in serialnumList">
							<option value="">无</option>
						</select>
						<label>
							<input ng-model="external.isShowidentity" type="checkbox" ng-true-value="1" ng-false-value="0"/>
							是否显示在启动流程中
						</label>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>