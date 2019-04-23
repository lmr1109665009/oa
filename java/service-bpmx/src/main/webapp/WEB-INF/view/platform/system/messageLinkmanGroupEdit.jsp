<%--
	time:2015-07-29 10:29:57
	desc:edit the 常用联系人组
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<title>编辑 常用联系人组</title>
<%@include file="/commons/include/form.jsp"%>
<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
<script type="text/javascript"
	src="${ctx }/js/hotent/platform/system/SysDialog.js"></script>
	
<script type="text/javascript">
	$(function() {
		$("a.save").click(function() {
			$("#messageLinkmanGroupForm").attr("action", "save.ht");
			submitForm();
		});
	});
	//提交表单
	function submitForm() {
		var options = {};
		if (showResponse) {
			options.success = showResponse;
		}
		var frm = $('#messageLinkmanGroupForm').form();
		frm.ajaxForm(options);
		if (frm.valid()) {
			frm.submit();
		}
	}

	function showResponse(responseText) {
		var obj = new com.hotent.form.ResultMessage(responseText);
		if (obj.isSuccess()) {
			$.ligerDialog
					.confirm(
							obj.getMessage() + ",是否继续操作",
							"提示信息",
							function(rtn) {
								if (rtn) {
									window.location.reload();
								} else {
									window.location.href = "${ctx}/platform/system/messageLinkmanGroup/list.ht";
								}
							});
		} else {
			$.ligerDialog.err("提示信息","常用联系人组保存失败!",obj.getMessage());
		}
	}

	function dlgCallBack(userIds, fullnames, emails, mobiles, retypes) {
		$("#users").val(fullnames);
		$("#userIds").val(userIds);
	};
	function addClick() {
		var selectUserIds = $("#userIds").val();
		var selectUserNames = $("#users").val();
		UserDialog({
			callback : dlgCallBack,
			selectUserIds:selectUserIds,
			selectUserNames:selectUserNames,
			isSingle : false
		});
	};
	//清空
	function reSet(){
		$("#users").val("");
		$("#userIds").val("");			
	};
</script>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<c:choose>
					<c:when test="${messageLinkmanGroup.id !=null}">
						<span class="tbar-label"><span></span>编辑常用联系人组</span>
					</c:when>
					<c:otherwise>
						<span class="tbar-label"><span></span>添加常用联系人组</span>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group">
						<a class="link save" id="dataFormSave" href="#"><span></span>保存</a>
					</div>
					<div class="l-bar-separator"></div>
					<div class="group">
						<a class="link back" href="list.ht"><span></span>返回</a>
					</div>
				</div>
			</div>
		</div>
		<div class="panel-body">
			<form id="messageLinkmanGroupForm" method="post" action="save.ht">
				<table class="table-detail" cellpadding="0" cellspacing="0"
					border="0" type="main">
					<tr>
						<th width="20%">组名:</th>
						<td><input type="text" id="groupName" name="groupName"
							value="${messageLinkmanGroup.groupName}" class="inputText"
							validate="{required:false,maxlength:200}" style="width: 200px;"/>
						</td>
					</tr>
					<tr>
						<th width="20%">常用联系人:</th>
						<td>
						<input type="hidden" id="userIds" name="userIds"
							value="${messageLinkmanGroup.userIds}"  />
						<textarea rows="5" cols="40" id="users" name="users" readonly="readonly">${messageLinkmanGroup.users}</textarea>	
							
						 <a href="javascript:;" onclick="addClick()" class="link get">选择</a>
						<a href="javascript:;" onclick="reSet()" class="link clean">清空</a>	
						</td>
					</tr>
				</table>
				<input type="hidden" name="id" value="${messageLinkmanGroup.id}" />
			</form>
		</div>
	</div>
</body>
</html>
