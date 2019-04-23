<%--
	time:2015-04-09 17:19:23
	desc:edit the 对象权限表
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<%@ taglib prefix="ht" tagdir="/WEB-INF/tags/wf"%>
<html>
<head>
	<title>表单树结构设置</title>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/angular.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/service/baseServices.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/service/arrayToolService.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/service/commonListService.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/formDefTree/FormDefTreeService.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/formDefTree/EditController.js"></script>
	<script type="text/javascript">
		var formKey="${param.formKey}";
		var dialog = frameElement.dialog;
	</script>
</head>
<body  ng-app="app" ng-controller="EditController">
<form id="frmSubmit">
<div class="panel">
	<div class="panel-top">
		<div class="tbar-title">
	        <span class="tbar-label">表单树结构设置</span>
		</div>
		<div class="panel-toolbar">
			<div class="toolBar">
				<div class="group"><a class="link save" ng-click="save()"><span></span>保存</a></div>
				<div class="group" ng-if="prop.id"><a class="link search" target="_blank" href="show_${param.formKey}.ht"><span></span>预览</a></div>
				<div class="group"><a class="link back" href="javaScript:dialog.close()"><span></span>关闭</a></div>
			</div>
		</div>
	</div>
	<div class="panel-body">
		<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<th width="20%">名称: </th>
				<td colspan="3">
					<input type="text" ng-model="prop.name">
					<input type="hidden" ng-model="prop.formKey">
				</td>
			
			</tr>
			
			<tr>
				<th width="20%">树主键字段: </th>
				<td><select ng-model="prop.treeId" ng-options="m.fieldName +'' as m.fieldDesc for m in param.table.fieldList"></select></td>
				<th width="20%">树父主键字段: </th>
				<td><select ng-model="prop.parentId" ng-options="m.fieldName +'' as m.fieldDesc for m in param.table.fieldList"></select></td>
			</tr>
			
			<tr>
				<th width="20%">显示字段: </th>
				<td><select ng-model="prop.displayField" ng-options="m.fieldName+'' as m.fieldDesc for m in param.table.fieldList"></select></td>
				<th width="20%">加载方式: </th>
				<td>
					<select ng-model="prop.loadType" ng-options="m.value as m.key for m in loadTypeList"></select>
					<span ng-if="prop.loadType==1">根节点父ID:<input ng-model="prop.rootId" type="text"/></span>
				</td>
			</tr>
		</table>
	</div>
</div>
</form>
</body>
</html>
