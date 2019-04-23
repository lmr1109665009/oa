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
			var pattern = /^[a-zA-Z0-9_]{6,20}$/;
			
			function showRequest(formData, jqForm, options) { 
				return true;
			} 
			
			$("#newPassword").blur(function(){
				// 密码格式校验
				if(!pattern.test($(this).val())){
					//$.ligerDialog.error("密码只能是6-20位字母、数字、下划线组合！", "出错了");
					$("#formaterror").text("密码只能是6-20位字母、数字、下划线组合！");
				} else {
					$("#formaterror").text("");
				}
				
			});
			$("#password").blur(function(){
				// 密码一致性校验
		        if ( $(this).val() != $("#newPassword").val() ){
		            alert("你输入的密码不一致!");
		            return false;
		    }});

			valid(showRequest,showResponse);
			$("a.save").click(function() {
				// 密码格式校验
				var newpassword = $("#newPassword").val();
				if(!pattern.test(newpassword)){
					//$.ligerDialog.error("密码只能是6-20位字母、数字、下划线组合！", "出错了");
					$("#formaterror").text("密码只能是6-20位字母、数字、下划线组合！");
					return;
				}
				// 密码一致性校验
				if ( $("#password").val() != newpassword ){
					//$.ligerDialog.error("你输入的密码不一致!",'出错了');
					$("#sameerror").text("你输入的密码不一致!");
		            return;
				}
				$('#sysUserForm').submit(); 
			});
		});
		
	</script>
</head>
<body>
<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">重置密码</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link save" id="dataFormSave"><span></span>保存</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link back" href="${returnUrl}"><span></span>返回</a></div>
				</div>
			</div>
		</div>
		<div class="panel-body">
			<form id="sysUserForm" method="post" action="resetPwd.ht">
				<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
					<tr>
						<th width="20%">重置密码:  <span class="required">*</span></th>
						<td >
							<input type="password" id="newPassword" name="newPassword" style="width:255px !important" class="inputText"/>
							<span id="formaterror" style="color:red"></span>
						</td>
					</tr>
					<tr>
						<th width="20%">确认密码:  <span class="required">*</span></th>
						<td >
							<input type="password" id="password" name="password" style="width:255px !important" class="inputText"/>
							<span id="sameerror" style="color:red"></span>
						</td>
					</tr>
				</table>
				<input type="hidden" name="userId" value="${sysUser.userId}" />
			</form>
		</div>
</div>
</body>
</html>
