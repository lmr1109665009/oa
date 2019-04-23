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
<script type="text/javascript" src="textinputDialogController.js"></script>

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
				var span = $("<span class='textinput' parser='inputedit'></span>");
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
					<th>数据类型:</th>
					<td>
						<select ng-model="external.dbType.type">
							<option value="varchar">文字</option>
							<option value="number">数字</option>
						</select>
					</td>

					<th>数据格式:</th>
					<!-- 字符串类型 -->
					<td class="dbformat_td" ng-if="external.dbType.type=='varchar'">
						长度:
						<input ng-model="external.dbType.length" style="width: 60px;" type="text">
					</td>
					<!-- 数字类型 -->
					<td class="dbformat_td" ng-if="external.dbType.type=='number'">
						<label for="isShowComdify">
							千分位:
							<input id="isShowComdify" type="checkbox" ng-disabled="external.dbType.isCoin=='1'" ng-model="external.dbType.isShowComdify" ng-true-value="1" ng-false-value="0" />
						</label>
						<label for="isCoin">
							货币:
							<input id="isCoin" ng-model="external.dbType.isCoin" type="checkbox" ng-true-value="1" ng-false-value="0" ng-change="eIsCoinChange()">
						</label>
						<select ng-model="external.dbType.coinValue" style="width: 80px;" ng-if="external.dbType.isCoin=='1'">
							<option value="￥">￥人民币</option>
							<option value="$">$美元</option>
						</select>
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
				
				<!-- 字符串类型，数据是日期格式 -->
				<tr class="defaultValue_tr" ng-if="external.dbType.type=='varchar'">
					<th>日期相关:</th>
					<td colspan="3">
						<label for="isDateString">
							<input type="checkbox" id="isDateString" ng-model="external.isDateString" ng-true-value="1" ng-false-value="0" />
							日期字符
						</label>
						<span ng-if="external.isDateString=='1'">
							格式:
							<select ng-model="external.dbType.dateStrFormat">
								<option value="yyyy-MM-dd">yyyy-MM-dd</option>
								<option value="yyyy-MM-dd HH:mm:ss">yyyy-MM-dd HH:mm:ss</option>
								<option value="yyyy-MM-dd HH:mm:00">yyyy-MM-dd HH:mm:00</option>
								<option value="HH:mm:ss">HH:mm:ss</option>
							</select>
							<label for="displayDate" class="displayDate">
								<input type="checkbox" id="displayDate" ng-model="external.dbType.displayDate" ng-true-value="1" ng-false-value="0" />
								当前日期字符
							</label>
						</span>
					</td>
				</tr>
				<tr>
					<th>值来源:</th>
					<td colspan="3">
						<select ng-model="external.valueFrom.value">
							<option value="0">表单输入</option>
							<option value="1">脚本运算(显示)</option>
							<option value="2">脚本运算(不显示)</option>
							<option value="3">流水号</option>
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
							<input ng-model="external.isShowidentity" type="checkbox" ng-true-value="1" ng-false-value="0" />
							是否显示在启动流程中
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
			</tbody>
		</table>
	</form>
</body>
</html>