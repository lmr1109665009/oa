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
</head>
<body ng-controller="sysQueryViewExportsCtrl">
	<div >
		<div class="panel-top">
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group">
						<a class="link export" href="javascript:;" ng-class="{disabled:!hasChecked()}" ng-click="hasChecked()&&toExports();"> <span></span>
							导出
						</a>
					</div>
					<div class="group">
						<a class="link del" href="javascript:;" ng-click="close()"> <span></span>
							关闭
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	<table class="table-grid" style="margin: 10px 0 0 20px;width: 95%;">
		<thead>
			<tr>
				<th width="8%">导出类型</th>
				<td style="text-align: left;">
					<select ng-model="exports.type" class="ht-input">
						<option value="1">全部数据</option>
						<option value="2">当前页数据</option>
					</select>
				</td>
			</tr>
			<tr>
				<th width="8%">选择</th>
				<td style="text-align: left;">
					<div class="plus_button_warning pull-left" ng-click="selectAll(1)" style="width: 65px;margin-left:5px;">全选</div>
					<div class="plus_button_warning pull-left" ng-click="selectAll(2)" style="width: 65px;margin-left:5px;">反选</div>
					<div class="plus_button_warning pull-left" ng-click="selectAll(3)" style="width: 65px;margin-left:5px;">全不选</div>
				</td>
			</tr>
			<tr>
				<th width="8%">导出排序</th>
				<td style="text-align: left;">
					<table class="table-grid fieldSetting">
							<thead>
								<tr>
									<th width="5%">序号</th>
									<th width="5%">字段名称</th>
									<th width="15%">字段描述</th>
									<th width="15%">是否导出</th>
									<th width="20%">排序</th>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="f in colModels track by $index" ng-if="f.label">
									<td >{{$index+1}}</td>
									<td >{{f.name}}</td>
									<td >{{f.label}}</td>
									<td ><input type="checkbox" ng-model="f.gchecked" /></td>
									<td>
										<tool-buttons list="colModels" index="{{$index}}" ></tool-buttons>
									</td>
								</tr>
							</tbody>
					</table>
				</td>
			</tr>
		</thead>
	</table>
	<form action="saveExports.ht" method="post"  class="hidden" id="form"></form>
	<div id="exportDiv"></div>
	
</body>
</html>