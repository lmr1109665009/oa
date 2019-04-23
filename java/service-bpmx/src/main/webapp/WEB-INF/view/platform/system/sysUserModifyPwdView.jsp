<%--
	time:2011-11-28 10:17:09
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@include file="/commons/include/html_doctype.html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>修改密码</title>
	<%@include file="/commons/include/form.jsp" %>

	<style>
		/* "变灰"效果*/
		.disableHref{
			cursor:default;
			color:#E5E0E0;
		}
	</style>

	<script type="text/javascript" src="${ctx}/servlet/ValidJs?form=sysUser"></script>
	<script type="text/javascript">
		$(function() {
			var pattern = /^[a-zA-Z0-9_]{6,20}$/;
			// 密码格式校验
			$("#newPassword").blur(function(){
				if(!pattern.test($(this).val())){
					// $.ligerDialog.error("密码只能是6-20位字母、数字、下划线组合！", "出错了");
					$("#formaterror").text("密码只能是6-20位字母、数字、下划线组合！");
				} else {
					$("#formaterror").text("");
				}
			});
			
			$("#repeatPassword").blur(function(){
				if($(this).val() != $("#newPassword").val()){
					//$.ligerDialog.error("两次输入的密码不一致",'出错了');
					$("#sameerror").text("两次输入的密码不一致!");
		    	} else {
		    		 $("#sameerror").text("");
		    	}
			});
			function showRequest(formData, jqForm, options) {
				/* var newPassword=$("#newPassword").val();
				var repeatPassword=$("#repeatPassword").val();
				if(newPassword!=repeatPassword){
					$.ligerDialog.error("两次输入的密码不一致",'出错了');
					return false;
				} */
				return true;

			}			
			
			function showResponse(responseText, statusText)  { 
				var obj=new com.hotent.form.ResultMessage(responseText);
				if(obj.isSuccess()){//成功
					$.ligerDialog.success(obj.getMessage(),'提示信息');
                    clearForm();//提示成功后，表单密码清空
                    enableHref();
			    }else{//失败
			    	$.ligerDialog.err('出错信息',"修改密码失败",obj.getMessage());
                    enableHref();
			    }
			} 
			function clearForm() {//用来清空密码
				$("#primitivePassword").val("");
                $("#newPassword").val("");
                $("#repeatPassword").val("");
            }
			valid(showRequest,showResponse);
			$("a.save").click(function() {
				// 密码格式校验
				var newPassword=$("#newPassword").val();
				if(!pattern.test(newPassword)){
					// $.ligerDialog.error("密码只能是6-20位字母、数字、下划线组合！", "出错了");
					$("#formaterror").text("密码只能是6-20位字母、数字、下划线组合！");
					return;
				}
				
				// 密码一致性校验
				var repeatPassword=$("#repeatPassword").val();
				if(newPassword!=repeatPassword){
					//$.ligerDialog.error("两次输入的密码不一致",'出错了');
					$("#sameerror").text("两次输入的密码不一致!");
					return;
				}
                hrefClick();//点击判断，如果有不可用样式，返回false，就不会产生第二次提交。
				$('#sysUserForm').submit(); 
			});

            // 禁用超链接-"变灰"
            function  disableHref(){
                var hrefDom = document.getElementById("dataFormSave");
                hrefDom.className+=" disableHref";
            }
            // 启用超链接-"正常"
            function  enableHref(){
                var hrefDom = document.getElementById("dataFormSave");
                hrefDom.className=hrefDom.className.replace(" disableHref","");
            }
            // 超链接点击事件
            function hrefClick(){
                var target=event.target;
                if(target.className.indexOf("disableHref")>-1){
                    // 加入判断,有"变灰"时返回
                    return false;
                }
            }
		});
    </script>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">修改密码</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link save" id="dataFormSave"><span></span>保存</a>
					<a class="link back"  onclick="history.back()" ><span></span>返回</a></div>
				</div>
			</div>
		</div>
		<div class="panel-body">
				<form id="sysUserForm" method="post" action="modifyPwd.ht">
					<div class="panel-detail">
						<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
							<tr>
								<th width="20%">帐号:<span> </span></th>
								<td>${sysUser.account}</td>
							</tr>
							<tr>
								<th width="20%">用户名:<span> </span></th>
								<td>${sysUser.fullname}</td>
							</tr>
							<tr>
								<th width="20%">原始密码:  <span class="required">*</span></th>
								<td ><input type="password" id="primitivePassword" autocomplete="off" name="primitivePassword" style="width:255px !important" class="inputText"/></td>
							</tr> 
							<tr>
								<th width="20%">修改密码:  <span class="required">*</span></th>
								<td >
									<input type="password" autocomplete="off" id="newPassword" name="newPassword" style="width:255px !important" class="inputText"/>
									<span id="formaterror" style="color:red"></span>
								</td>
							</tr>
							<tr>
								<th width="20%">重复密码:  <span class="required">*</span></th>
								<td >
									<input type="password" autocomplete="off" id="repeatPassword" name="repeatPassword" style="width:255px !important" class="inputText"/>
									<span id="sameerror" style="color:red"></span>
								</td>
							</tr>
						</table>
						<input type="hidden" name="userId" value="${userId}" />
					</div>
				</form>
		</div>
</div>
</body>
</html>
