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
<script type="text/javascript" src="subtableDialogController.js"></script>

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

		//return true 关闭窗口，return false 不关闭窗口		
		dialog.onok = function() {
			var frm = $('#frmSubmit').form();
			if (!frm.valid())
				return false;

			var parser = "";
			if (scope.external.modetype == 'inlinemodel') {//行模式
				parser = 'rowmodeedit';
			}
			if (scope.external.modetype == 'blockmodel') {//块模式
				parser = 'piecemodeedit';
			}
			if (scope.external.modetype == 'windowmodel') {//弹窗模式
				parser = 'windowdialogmodeedit';
			}
			if (scope.external.modetype == 'inlinemodelNO') {//（序号）行模式
				parser = 'rowmodeedit';
			}
			if (scope.external.modetype == 'windowmodelNO') {//（序号）弹窗模式
				parser = 'windowmodeedit';
			}
			
			var div = $("<div type='subtable' class='subtable'></div>");
			div.attr("tablename",scope.external.tablename);
			div.attr("tabledesc",scope.external.tablememo);
			div.attr("show",scope.external.show=="1"?"true":"false");
			div.attr("external",externalEncode(JSON.stringify(scope.external)));
			div.attr("parser",parser);
			div.append(getSubtableContent());
			
			if(targetEl){//编辑模式
				$(targetEl).after(div);
				$(targetEl).remove();
			}else{
				$(element).append(div);				
			}
			
			return true;
		};
	});
	
	//获取对应模式子表内容
	function getSubtableContent(){
		var num = Number(scope.external.tablerows);
		var text=[''];
		switch(scope.external.modetype){
			case "inlinemodel":
				//行模式
				text.push('<table class="listTable" border="0" cellpadding="2" cellspacing="0"><tbody>');
				text.push('<tr class="toolBar"><td colspan="'+num+'"><a class="link add" href="javascript:;">添加</a></td></tr>');
				text.push('<tr class="headRow">');
				for(var i=0;i<num;i++){
					text.push('<th>列'+(i+1)+'</th>');
				}
				text.push('</tr><tr class="listRow" formtype="edit">');
				for(var i=0;i<num;i++){
					text.push('<td></td>');
				}
				text.push('</tr></table>');
				break;
			case "blockmodel":
				//块模式
				text.push('<div class="subTableToolBar l-tab-links"><a class="link add">添加</a></div><div formtype="edit"><table class="blocktable"><tr>');
				for(var i=0;i<num;i++){
					text.push('<th style="width:'+(100*0.3)/num+'%;">列'+(i+1)+'</th><td style="width:'+(100*0.7)/num+'%;"></td>');
				}
				text.push('</tr></table></div>');
				break;
			case "windowmodel":
				//窗口模式
				text.push('<table class="listTable" border="0" cellpadding="2" cellspacing="0"><tbody>');
				text.push('<tr class="toolBar"><td colspan="'+num+'"><a class="link add" href="javascript:;">添加</a></td></tr>');
				text.push('<tr class="headRow">');
				for(var i=0;i<num;i++){
					text.push('<th>列'+(i+1)+'</th>');
				}
				text.push('</tr><tr class="listRow" formtype="form">');
				for(var i=0;i<num;i++){
					text.push('<td></td>');
				}
				text.push('</tr></table><div formtype="window"><table class="window-table">');
				for(var i=0;i<num;i++){
					text.push('<tr><th>列'+(i+1)+'</th><td></td></tr>');
				}
				text.push('</table></div>');
				break;
			case "inlinemodelNO":
				//(序号)行模式
				var colNum=Number(num)+1;
				text.push('<table class="listTable" border="0" cellpadding="2" cellspacing="0"><tbody>');
				text.push('<tr class="toolBar"><td colspan="'+colNum+'"><a class="link add" href="javascript:;">添加</a></td></tr>');
				text.push('<tr class="headRow">');
				text.push('<th>序号</th>');
				for(var i=0;i<num;i++){
					text.push('<th>列'+(i+1)+'</th>');
				}
				text.push('</tr><tr class="listRow" formtype="edit">');
				text.push('<td class="tdNo"></td>');
				for(var i=0;i<num;i++){
					text.push('<td></td>');
				}
				text.push('</tr></table>');
				break;
			case "windowmodelNO":
				//(序号)窗口模式
				var colNum=Number(num)+1;
				text.push('<table class="listTable" border="0" cellpadding="2" cellspacing="0"><tbody>');
				text.push('<tr class="toolBar"><td colspan="'+colNum+'"><a class="link add" href="javascript:;">添加</a></td></tr>');
				text.push('<tr class="headRow">');
				text.push('<th>序号</th>');
				for(var i=0;i<num;i++){
					text.push('<th>列'+(i+1)+'</th>');
				}
				text.push('</tr><tr class="listRow" formtype="form">');
				text.push('<td class="tdNo"></td>');
				for(var i=0;i<num;i++){
					text.push('<td></td>');
				}
				text.push('</tr></table><div formtype="window"><table class="window-table">');
				for(var i=0;i<num;i++){
					text.push('<tr><th>列'+(i+1)+'</th><td></td></tr>');
				}
				text.push('</table></div>');
				break;
		}
		return text.join('');		
	};
</script>
</head>
<body ng-app="app" ng-controller="ctrl" ng-init="init()">
	<form id="frmSubmit">
		<table class="edit_table">
			<tbody>
				<tr>
					<th>表描述:</th>
					<td>
						<input ng-model="external.tablememo" type="text" validate="{required:true}">
					</td>
					<th>表名称:</th>
					<td>
						<input ng-model="external.tablename" ht-pinyin="external.tablememo" type="text" validate="{required:true,varirule:true}">
					</td>
				</tr>

				<tr>
					<th>列数:</th>
					<td>
						<input ng-model="external.tablerows" type="text" validate="{required:true}">
					</td>
					<th>默认显示子表:</th>
					<td>
						<input type="checkbox" ng-model="external.show" ng-true-value="1" ng-false-value="0" />
					</td>
				</tr>

				<tr>
					<th>添加数据模式:</th>
					<td colspan="3">
						<label>
							<input ng-model="external.modetype" type="radio" value="inlinemodel">
							行模式
						</label>
						<label>
							<input ng-model="external.modetype" type="radio" value="inlinemodelNO">
							(序号)行模式
						</label>
						<label>
							<input ng-model="external.modetype" type="radio" value="blockmodel">
							块模式
						</label>
						<br>
						<label>
							<input ng-model="external.modetype" type="radio" value="windowmodel">
							弹窗模式
						</label>
						<label>
							<input ng-model="external.modetype" type="radio" value="windowmodelNO">
							(序号)弹窗模式
						</label>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>