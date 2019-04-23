<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html ng-app="sysQueryViewApp">
<head>
<%@include file="/commons/include/form.jsp" %>
	<link href="${ctx}/styles/default/css/hotent/dataRights.css" rel="stylesheet" />
	<script type="text/javascript" src="${ctx}/js/angular/angular.min.js"></script>
	<script type="text/javascript"	src="${ctx}/js/angular/service/baseServices.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/module/DataRightsApp.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/controller/sysQueryViewController.js"></script>
	<title>设置字段信息 </title>
	<script>
		var sysQueryViewList=${sysQueryViewList};
	</script>
</head>
<body ng-controller="sysQueryViewSortListCtrl">
	<dialog-buttons></dialog-buttons>
	<table id="displayFieldTbl"  class="table-grid fieldSetting">
		<thead>
			<tr>
				<th width="5%">序号</th>
				<th width="15%">sql别名</th>
				<th width="15%">视图名称</th>
				<th width="15%">视图别名</th>
				<th width="20%">排序</th>
			</tr>
		</thead>
		<tbody>
			<tr ng-repeat="sq in sysQueryViewList">
				<td >{{$index+1}}</td>
				<td >{{sq.sqlAlias}}</td>
				<td >{{sq.name}}</td>
				<td >{{sq.alias}}</td>
				<td>
					<a class="link moveup" href="javascript:;" ng-click="moveTr(sysQueryViewList,$index,true)"></a>
					<a class="link movedown" href="javascript:;" ng-click="moveTr(sysQueryViewList,$index,false)"></a>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>