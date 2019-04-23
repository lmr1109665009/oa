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
<script type="text/javascript" src="decimaltopercentDialogController.js"></script>

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
			var input;
			
			if(scope.external.dbType.minValue==0&&scope.external.dbType.maxValue==0){
				delete scope.external.dbType.minValue;
				delete scope.external.dbType.maxValue;
			}
			
			if (!targetEl) {//新增模式
				var span = $("<span class='decimaltopercentedit' parser='decimaltopercentedit'></span>");
				input =$("<input type='text'/>");
				span.append(input);
				span.attr("external", externalEncode(JSON.stringify(scope.external)));
				$(element).find("br").remove();
				$(element).append(span);
			} else {//编辑
				input =$(targetEl).find("input");
				$(targetEl).attr("external", externalEncode(JSON.stringify(scope.external)));
			}
			input.css("width",scope.external.width+scope.external.widthUnit);
			input.css("height",scope.external.height+scope.external.heightUnit);
			
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
					<th>数据格式:</th>
					<!-- 数字类型 -->
					<td class="dbformat_td" colspan="4" ng-if="external.dbType.type=='number'">
						<label>
							整数位:
							<input ng-model="external.dbType.intLen" style="width: 30px;" type="text" />
							小数位:
							<input ng-model="external.dbType.decimalLen" style="width: 25px;" type="text" />
						</label>
						<label>
							数字范围:<input ng-model="external.dbType.isRand" type="checkbox" ng-true-value="1" ng-false-value="0">
							<span ng-show="external.dbType.isRand" >
								<input ng-model="external.dbType.minValue" style="width: 30px;" type="text" />
								,
								<input ng-model="external.dbType.maxValue" style="width: 25px;" type="text" />
							</span>
						</label>
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
				<tr class="style_tr">
					<td colspan="4">
						这个输入控件在编辑状态可以输入数字，但是在只读状态会转化成百分比
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>