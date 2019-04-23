<%--
	time:2012-12-14 17:17:09
	desc:reset the 用户表
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>重置密码</title>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/servlet/ValidJs?form=sysUser"></script>
	<script type="text/javascript">
		$(function() {
			function showRequest(formData, jqForm, options) { 
				return true;
			} 
				
			$("#password").blur(function(){
		        if ( $(this).val() != $("#newPassword").val() ){
		            alert("你输入的密码不一致!");
		            return false;
		    }});

			valid(showRequest,function(text){
				var json =JSON.parse(text);
				if(json.state=="0"){
					$.ligerDialog.success(json.msg,"操作成功",function(){
						window.location.href="${ctx}/login.jsp";
					});
				}else{
					$.ligerDialog.error(json.msg);
				}
			});
			$("a.save").click(function() {
				$('#sysUserForm').submit(); 
			});
		});
	</script>
</head>
<body>
<div class="panel">
		<div class="panel-top">
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link save"><span></span>确定</a></div>
				</div>
				<div class="l-bar-separator"></div>
				<div class="group"><a class="link back" href="javaScript:window.history.go(-1);"><span></span>返回</a></div>
			</div>
		</div>
		<div class="panel-body">
			<form id="sysUserForm" method="post" action="${ctx}/platform/system/sysUser/commonResetPwd.ht">
				<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
					<tr>
						<th colspan="2" style="text-align: center;"><div class="confirm"><span style="color:#ff0000;">${sessionScope.SPRING_SECURITY_LAST_EXCEPTION}</span></div></th>
					</tr>
					<tr>
						<th width="20%">账号:  <span class="required">*</span></th>
						<td ><input type="text" name="account" style="width:255px !important" class="inputText"/></td>
					</tr>
					<tr>
						<th width="20%">旧密码:  <span class="required">*</span></th>
						<td ><input type="password" name="oldPassword" style="width:255px !important" class="inputText"/></td>
					</tr>
					<tr>
						<th width="20%">新密码:  <span class="required">*</span></th>
						<td ><input type="password" id="newPassword" name="newPassword" style="width:255px !important" class="inputText"/></td>
					</tr>
					<tr>
						<th width="20%">确认密码:  <span class="required">*</span></th>
						<td ><input type="password" id="password" name="password" style="width:255px !important" class="inputText"/></td>
					</tr>
				</table>
			</form>
		</div>
</div>
</body>
</html>
