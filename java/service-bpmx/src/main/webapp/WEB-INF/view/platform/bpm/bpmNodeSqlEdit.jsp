
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
	<title>流程节点SQL设置</title>
	<%@include file="/commons/include/get.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/angular.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/service/baseServices.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/service/arrayToolService.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/service/commonListService.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/sysDataSource/SysDataSourceService.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/bpmNodeSql/BpmNodeSqlService.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/bpmNodeSql/EditController.js"></script>
	<script type="text/javascript" src="${ctx}/js/util/sqlUtil.js"></script>
	<script type="text/javascript">
		var actdefId = "${param.actdefId}";
		var nodeId="${param.nodeId}";
		var id="${param.id}";
		
		var dialog =frameElement!=null?frameElement.dialog:null;
		function closeWin(){
			if(dialog){//弹出框打开
				dialog.close();
			}else{
				window.location.href="list.ht?defId=${param.defId}";
			}
		}
		
		//在鼠标停留的地方插入text
		function insertText(obj,str) {
		    if (document.selection) {
		    	obj.focus();
		        var sel = document.selection.createRange();
		        sel.text = str;
		    } else if (typeof obj.selectionStart === 'number' && typeof obj.selectionEnd === 'number') {
		        var startPos = obj.selectionStart,
		            endPos = obj.selectionEnd,
		            cursorPos = startPos,
		            tmpStr = obj.value;
		        obj.value = tmpStr.substring(0, startPos) + str + tmpStr.substring(endPos, tmpStr.length);
		        cursorPos += str.length;
		        obj.selectionStart = obj.selectionEnd = cursorPos;
		    } else {
		        obj.value += str;
		    }
		}
	</script>
</head>
<body ng-app="app" ng-controller="EditController" style="overflow-y: auto;">
	<form id="frmSubmit">
		<div class="panel">
			<div class="panel-top">
				<div class="tbar-title">
			        <span class="tbar-label">编辑节点SQL</span>
				</div>
				<div class="panel-toolbar">
					<div class="toolBar">
						<div class="group">
							<a class="link save" ng-click="save()"><span></span>保存</a>
						</div>
						<div class="group">
							<a class="link back" href="javaScript:closeWin()"><span></span>返回</a>
						</div>
					</div>
				</div>
			</div>
			<div class="panel-body">
				<table class="table-detail" cellpadding="0" cellspacing="0" border="0" type="main">
					<tr>
						<th width="20%">名称: </th>
						<td><input type="text" ng-model="prop.name" validate="{required:true}" class="inputText"/></td>
					</tr>
					<tr>
						<th width="20%">节点ID：</th>
						<td>{{prop.nodeId}}</td>
					</tr>
					<tr>
						<th width="20%">数据源别名: </th>
						<td><select ng-model="prop.dsAlias" ng-options="m.alias as m.name for m in allSysDS"/></td>
					</tr>
					<tr>
						<th width="20%">触发时机：</th>
						<td><select ng-model="prop.action" ng-options="m.value as m.key for m in actionList"/></td>
					</tr>
					<tr>
						<th width="20%">表单字段：</th>
						<td>
							<input ng-repeat="field in table.fieldList" type="button" value="{{field.fieldDesc}}" ng-click="appendSql(field)"/>
						</td>
					</tr>
					<tr>
						<th width="20%">流程字段：</th>
						<td>
							<input ng-repeat="field in flowFieldList" type="button" value="{{field.key}}" ng-click="appendSql(field)"/>
						</td>
					</tr>
					<tr>
						<th width="20%">SQL语句: </th>
						<td>
							<textarea id="sqlText" ng-model="prop.sql" rows="12" validate="{required:true}"></textarea>
							<div>
								 事务回滚:<input type="checkbox" ng-model="rollback"/>
								<a class="button" ng-click="checkSqlValidity()"><span class="icon valid"></span><span>验证查询语句</span></a>
							</div> 
						</td>
					</tr>
					<tr>
						<th width="20%">描叙: </th>
						<td>
							<textarea ng-model="prop.desc" rows="3"></textarea>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</form>
</body>
</html>


