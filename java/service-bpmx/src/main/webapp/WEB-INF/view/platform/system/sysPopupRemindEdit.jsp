<%--
	time:2015-03-18 11:36:25
	desc:edit the sys_popup_remind
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<%@ taglib prefix="ht" tagdir="/WEB-INF/tags/wf"%>
<html>
<head>
	<title>编辑 sys_popup_remind</title>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/form/AttachMent.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/FlexUploadDialog.js" ></script>
	<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/angular.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/service/baseServices.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/service/arrayToolService.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/service/commonListService.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/sysDataSource/SysDataSourceService.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/sysPopupRemind/SysPopupRemindService.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/sysPopupRemind/EditController.js"></script>
	<script type="text/javascript" src="${ctx}/js/util/sqlUtil.js"></script>
	<script type="text/javascript">
		var id="${param.id}";
	</script>
</head>
<body ng-app="app" ng-controller="EditController">
	<form id="frmSubmit">
		<div class="panel">
			<div class="panel-top">
				<div class="tbar-title">
				    <c:choose>
					    <c:when test="${param.id !=null}">
					        <span class="tbar-label">编辑sys_popup_remind</span>
					    </c:when>
					    <c:otherwise>
					        <span class="tbar-label">添加sys_popup_remind</span>
					    </c:otherwise>			   
				    </c:choose>
				</div>
				<div class="panel-toolbar">
					<div class="toolBar">
						<div class="group"><a class="link save" ng-click="save()"><span></span>保存</a></div>
						<div class="group"><a class="link back" href="list.ht"><span></span>返回</a></div>
					</div>
				</div>
			</div>
			<div class="panel-body">
				<table class="table-detail" cellpadding="0" cellspacing="0" border="0" type="main">
					<tr>
						<th width="20%">主题: </th>
						<td>
							<input type="text" style="width: 200px" validate="{required:false,maxlength:600}" class="inputText" ng-model="prop.subject"/>
						</td>
					</tr>
					<tr>
						<th width="20%">跳转URL: </th>
						<td>
							<input type="text" style="width: 300px"  ng-model="prop.url" validate="{required:false,maxlength:600}" class="inputText"/>
							当用户点击提醒信息时的跳转地址
						</td>
					</tr>
					<tr>
						<th width="20%">排序大小: </th>
						<td><input type="text" ng-model="prop.sn" validate="{required:false,number:true,maxIntLen:18 }" class="inputText"/></td>
					</tr>
					<tr>
						<th width="20%">是否启用: </th>
						<td>
							<select class="inputText" ng-model="prop.enabled" style="width: 70px" ng-options="m.value as m.key for m in CommonList.yesOrNoList2">
							</select>
						</td>
					</tr>
					<tr>
						<th width="20%">数据源别名: </th>
						<td><select ng-model="prop.dsalias" ng-options="m.alias as m.name for m in allSysDS"/></td>
					</tr>
					<tr>
						<th width="20%">弹框方式: </th>
						<td>
							<input type="radio" name="popupType" value="tab" ng-model="prop.popupType">tab
							<input type="radio" name="popupType" value="newWin" ng-model="prop.popupType">新窗口
							<input type="radio" name="popupType" value="dialog" ng-model="prop.popupType">对话框
							<span ng-if="prop.popupType=='dialog'">（宽：<input type="text" style="width: 30px" ng-model="prop.reserve.dialogWidth"/>px</span>
							<span ng-if="prop.popupType=='dialog'">高：<input type="text" style="width: 30px" ng-model="prop.reserve.dialogHeight"/>px）</span>
						</td>
					</tr>
					<tr>
						<th width="20%">SQL语句: </th>
						<td>
							<div>常用数据：<input type="button" value="当前用户ID" ng-click="appendSql('{curUserId}')"/></div>
							<textarea id="sqlText" ng-model="prop.sql" rows="12" validate="{required:true}"></textarea>
							注意：此处的SQL语句要满足只返回一条记录且是number类型的场合
							<div>
								<a class="button" ng-click="checkSqlValidity()"><span class="icon valid"></span><span>验证查询语句</span></a>
							</div> 
						</td>
					</tr>
					<tr>
						<th width="20%">描叙: </th>
						<td>
							<textarea ng-model="prop.desc" rows="3"></textarea>
							注意：{count}为固定写法，表示SQL语句查询结果的记录总数
						</td>
					</tr>
				</table>
			</div>
		</div>
	</form>
</body>
</html>
