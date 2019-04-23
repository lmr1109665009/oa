<%--
	time:2015-03-18 11:36:25
	desc:edit the 密码策略
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<%@ taglib prefix="ht" tagdir="/WEB-INF/tags/wf"%>
<html>
<head>
	<title>编辑 密码策略</title>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/form/rule.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/angular.min.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/service/baseServices.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/service/arrayToolService.js"></script>
	<script type="text/javascript" src="${ctx}/js/angular/service/commonListService.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/pwdStrategy/PwdStrategyService.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/pwdStrategy/EditController.js"></script>
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
					        <span class="tbar-label">编辑密码策略</span>
					    </c:when>
					    <c:otherwise>
					        <span class="tbar-label">添加密码策略</span>
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
						<th width="20%">初始化密码: </th>
						<td>
							<input type="text" style="width: 200px" validate="{required:false,maxlength:600}" class="inputText" ng-model="prop.initPwd"/>
							<span style="color: green;">(不填则为没有初始化密码)</span>
						</td>
					</tr>
					<tr>
						<th width="20%">强制修改初始化密码: </th>
						<td>
							<input type="radio" ng-model="prop.forceChangeInitPwd" ng-value="0">否
							<input type="radio" ng-model="prop.forceChangeInitPwd" ng-value="1">是
						</td>
					</tr>
					<tr>
						<th width="20%">密码规则: </th>
						<td>
							<input type="radio" ng-model="prop.pwdRule" ng-value="0">无限制
							<input type="radio" ng-model="prop.pwdRule" ng-value="1">数字加字母
							<input type="radio" ng-model="prop.pwdRule" ng-value="2">数字加字母加特殊字符
						</td>
					</tr>
					<tr>
						<th width="20%">密码长度: </th>
						<td>
							<input type="text" validate="{required:true,非负整数:true}" class="inputText" ng-model="prop.pwdLength"/>
							<span style="color: green;">(0代表无限制)</span>
						</td>
					</tr>
					<tr >
						<th width="20%">密码过期处理: </th>
						<td>
							<input type="radio" ng-model="prop.handleOverdue" ng-value="0">不处理
							<input type="radio" ng-model="prop.handleOverdue" ng-value="1">锁定账号
						</td>
					</tr>
					<tr ng-if="prop.handleOverdue!=0">
						<th width="20%">密码有效期: </th>
						<td>
							<input type="text" validate="{required:true,非负整数:true}" class="inputText" ng-model="prop.validity"/>
							个月  <span style="color: green;">(0代表无限期)</span>
						</td>
					</tr>
					<tr ng-if="prop.handleOverdue!=0">
						<th width="20%">密码过期提醒: </th>
						<td>
							<input type="text" validate="{required:true,非负整数:true}" class="inputText" ng-model="prop.overdueRemind"/>
							个星期  <span style="color: green;">(0代表不提醒)</span>
						</td>
					</tr>
					<tr>
						<th width="20%">密码输错多少次出现验证码: </th>
						<td>
							<input type="text" validate="{required:true,非负整数:true}" class="inputText" ng-model="prop.verifyCodeAppear"/>
							次  <span style="color: green;">(0代表不出现)</span>
						</td>
					</tr>
					<tr>
						<th width="20%">密码错误多少次锁住账号: </th>
						<td>
							<input type="text" validate="{required:true,非负整数:true}" class="inputText" ng-model="prop.errLockAccount"/>
							次  <span style="color: green;">(0代表不出现)</span>
						</td>
					</tr>
					<tr>
						<th width="20%">是否启用该规则: </th>
						<td>
							<input type="radio" ng-model="prop.enable" ng-value="1">启用
							<input type="radio" ng-model="prop.enable" ng-value="0">禁用
						</td>
					</tr>
					<tr>
						<th width="20%">描叙: </th>
						<td>
							<textarea ng-model="prop.desc" rows="5" cols=""></textarea>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</form>
</body>
</html>
