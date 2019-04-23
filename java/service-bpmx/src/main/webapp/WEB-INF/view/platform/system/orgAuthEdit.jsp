<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>编辑 分级管理员</title>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
	<script type="text/javascript" src="${ctx }/js/hotent/platform/system/SysDialog.js"></script>
	<script type="text/javascript">
		$(function() {
			$("a.save").click(function() {
				$("#orgAuthForm").attr("action","save.ht");
				submitForm();
			});
			addOrgAuth("posPerms");
			addOrgAuth("userPerms");
			addOrgAuth("orgPerms");
			addOrgAuth("orgauthPerms");
		});
		//提交表单
		function submitForm(){
			var options={};
			if(showResponse){
				options.success=showResponse;
			}
			var frm=$('#orgAuthForm').form();
			frm.ajaxForm(options);
			if(frm.valid()){
				frm.submit();
			}
		}
		
		function showResponse(responseText) {
			var obj = new com.hotent.form.ResultMessage(responseText);
			if (obj.isSuccess()) {
				$.ligerDialog.success(obj.getMessage(),"提示信息", function(rtn) {
					if(rtn){
						if(window.opener){
							window.opener.location.reload();
							window.close();
						}else{
							this.close();
							window.location.href="${isGrade==true ? 'gradeList':'list'}.ht?orgId=${orgId}";
						}
					}
				});
			} else {
				$.ligerDialog.err("提示信息","分级管理员保存失败!",obj.getMessage());
			}
		}
		
		function editUser(){
			if('${isGrade}'!='true'){  
				UserDialog({callback:fullUserMsg,isSingle:true});
			}else{
				GradeUserDialog({callback : fullUserMsg,isSingle:true});
			}
		}; 
		function fullUserMsg(userIds,fullNames){
			$("#userId").val(userIds);
			$("#userName").val(fullNames);	
		} 
		
		function resetUser(){
			$("#userId").val("");
			$("#userName").val("");	
		}
		function editRole(isGrade){
			if(isGrade) isGrade = true;  else isGrade = false;
			RoleDialog({callback:function(userIds,fullnames){
				$("#roleIds").val(userIds);
				$("#roleNames").val(fullnames);	
			},isSingle:false,isGrade:isGrade}); 
		};
		function resetRole(){
			$("#roleIds").val("");
			$("#roleNames").val("");	
		}
		
		//分级管理员权限下的添加分级管理员设置各个权限（用户、岗位、组织和分级管理员权限）
		function addOrgAuth(obj){
			var isGrade="${param.isGrade}";
			   if(isGrade == "")
				   return;
			   var strObj = $("#orgAuth").children('option:selected').attr(obj);//分级管理员权限
			   
			   if(strObj.indexOf("add")==-1) {
					$("#"+obj).find(".add").hide();
				}
				if(strObj.indexOf("edit")==-1) {
					$("#"+obj).find(".edit").hide();
				}
				if(strObj.indexOf("del")==-1) {
					$("#"+obj).find(".del").hide();
				} 
		};
		
	</script>
</head>
<body>
<div class="panel">
    <div style="display: none;" >
		<select id="orgAuth" style="width:99.8% !important;" >
			<option value="${userAuth.orgId}" authId="${userAuth.id}" dimId="${userAuth.dimId}" orgPerms="${userAuth.orgPerms}" userPerms="${userAuth.userPerms}" orgauthPerms="${userAuth.orgauthPerms}" posPerms="${userAuth.posPerms}" ></option>  
	    </select>
	</div>
	<div class="panel-top">
		<div class="tbar-title">
		    <c:choose>
			    <c:when test="${orgAuth.id !=null}">
			        <span class="tbar-label"><span></span>编辑分级管理员</span>
			    </c:when>
			    <c:otherwise>
			        <span class="tbar-label"><span></span>添加分级管理员</span>
			    </c:otherwise>			   
		    </c:choose>
		</div>
		<div class="panel-toolbar">
			<div class="toolBar">
				<div class="group"><a class="link save" id="dataFormSave" href="javascript:;"><span></span>保存</a></div>
				<div class="l-bar-separator"></div>
				<div class="group">
				<a class="link back" href="${isGrade==true ? 'gradeList':'list'}.ht?orgId=${orgId}&isGrade=${param.isGrade}&topOrgId=${param.topOrgId}">
				<span></span>返回</a></div>
			</div>
		</div>
	</div>
	<div class="panel-body">
		<form id="orgAuthForm" method="post" action="save.ht">
			<table class="table-detail" cellpadding="0" cellspacing="0" border="0" type="main">
				<tr>
					<th width="20%">管理员: <span class="required">*</span></th>
					<td>
					 	<input type="text" class="inputText" readonly="readonly"id="userName" value="${orgAuth.userName}" validate="{required:true}">
						<input type="hidden" id="userId" name="userId" value="${orgAuth.userId}"  class="inputText" />
					    <a href="javascript:;" onclick="editUser()" class="link get">选择</a>
					    <a href="javascript:;" onclick="resetUser()" class="link clean">清空</a>
					</td>
				</tr>
				<tr>
					<th width="20%">组织:<span class="required">*</span></th>
					<td>
					${empty sysOrg ? orgAuth.orgName : sysOrg.orgName}
					<input type="hidden" id="orgId" name="orgId" value="${empty sysOrg ? orgAuth.orgId : sysOrg.orgId}" />
					<input type="hidden" id="dimId" name="dimId" value="${empty sysOrg ? orgAuth.dimId : sysOrg.demId}" /></td> 
				</tr>
				<tr>
					<th width="20%">子组织权限: </th>
					<td id="orgPerms">
						<input type="checkbox" class="add" name="orgPerms" value="add" id="orgAdd" <c:if test="${fn:contains(orgAuth.orgPerms, 'add')}">checked="checked" </c:if> /> 
						<label for="orgAdd" class="add">增加</label>　
						<input type="checkbox"  class="del" name="orgPerms" value="delete" id="orgDelete" <c:if test="${fn:contains(orgAuth.orgPerms, 'delete')}">checked="checked" </c:if> /> 
						<label for="orgDelete" class="del">删除</label>　
						<input type="checkbox" class="edit" name="orgPerms" value="edit" id="orgEdit" <c:if test="${fn:contains(orgAuth.orgPerms, 'edit')}">checked="checked" </c:if> /> 
						<label for="orgEdit" class="edit">修改</label>　
					</td>
				</tr>
				<tr>
					<th width="20%">用户权限: </th>
					<td id="userPerms">
						<input type="checkbox"  class="add" name="userPerms" value="add" id="userAdd" <c:if test="${fn:contains(orgAuth.userPerms, 'add')}">checked="checked" </c:if> />
						<label for="userAdd"  class="add">增加</label>
						<input type="checkbox"  class="del" name="userPerms" value="delete" id="userDelete" <c:if test="${fn:contains(orgAuth.userPerms, 'delete')}">checked="checked" </c:if> /> 
						<label for="userDelete"  class="del">删除</label>
						<input type="checkbox"   class="edit" name="userPerms" value="edit" id="userEdit" <c:if test="${fn:contains(orgAuth.userPerms, 'edit')}">checked="checked" </c:if> />
						<label for="userEdit"   class="edit">修改</label>
		        </tr>
		        
		        <tr>
		           <th width="20%">分级管理员权限：</th>
		           <td id="orgauthPerms">
		               <input type="checkbox" class="add" name="orgauthPerms" value="add" id="orgauthAdd" <c:if test="${fn:contains(orgAuth.orgauthPerms, 'add')}">checked="checked" </c:if> />
		               <label for="orgauthAdd" class="add">增加</label>
		               <input type="checkbox" class="del" name="orgauthPerms" value="delete" id="orgauthDelete" <c:if test="${fn:contains(orgAuth.orgauthPerms, 'delete')}">checked="checked" </c:if> />
		               <label for="orgauthDelete" class="del">删除</label>
		               <input type="checkbox"  class="edit" name="orgauthPerms" value="edit" id="orgauthEdit" <c:if test="${fn:contains(orgAuth.orgauthPerms, 'edit')}">checked="checked" </c:if> />
		               <label for="orgauthEdit"  class="edit">修改</label>
		           </td>
		        </tr>
		        <tr>
		           <th width="20%">岗位管理权限：</th>
		           <td id="posPerms">
		               <input type="checkbox" class="add" name="posPerms" value="add" id="posAdd" <c:if test="${fn:contains(orgAuth.posPerms, 'add')}">checked="checked" </c:if> />
		               <label for="posAdd" class="add" >增加</label>
		               <input type="checkbox" class="del" name="posPerms" value="delete" id="posDelete" <c:if test="${fn:contains(orgAuth.posPerms, 'delete')}">checked="checked" </c:if> />
		               <label for="posDelete" class="del">删除</label>
		               <input type="checkbox" class="edit" name="posPerms" value="edit" id="posEdit" <c:if test="${fn:contains(orgAuth.posPerms, 'edit')}">checked="checked" </c:if> />
		               <label for="posEdit" class="edit">修改</label>
		           </td>
		        </tr>
		        
		        <tr>
					<th width="20%">可分配角色: </th>
					<td>
					<textarea id="roleNames" rows="1" cols="1" readonly="readonly"><c:forEach items="${roleList}" var="role">${role.roleName},</c:forEach></textarea>
					<input type="hidden" name="roleIds" id="roleIds" value="<c:forEach items="${roleList}" var="role">${role.roleId},</c:forEach>" /> 
					 <a href="javascript:;" onclick="editRole(${isGrade})" class="link get">选择</a>
					 <a href="javascript:;" onclick="resetRole()" class="link clean">清空</a>
					</td>
		        </tr>
			</table>
			<input type="hidden" name="id" value="${orgAuth.id}" />
		</form>
		
	</div>
</div>
</body>
</html>
