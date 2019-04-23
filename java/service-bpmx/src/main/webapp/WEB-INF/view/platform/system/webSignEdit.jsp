<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<%@include file="/commons/include/form.jsp"%>
<title>用户签章图片编辑</title>
<style type="text/css">
	.table-detail .link, .btnlink, .table-detail input[type="button"]{
		padding-top: 8px;
		padding-bottom: 8px;
	}
</style>
<script type="text/javascript" src="${ctx }/js/hotent/platform/system/SysDialog.js"></script>
<script type="text/javascript">
    function dlgCallBack(userId,fullname,email,phone,userObj) {
        if(!userId){
            return;
        }
        $("#userId").val(userId);
        $("#username").val(fullname);
        $("#relativeInfo").html('手机号码：'+phone+'，邮箱：'+email+'，账号：'+userObj.accounts);
        $("#department").html(userObj.orgNames);
    };
    //添加、保存签章文件
    function save(){
        if($("#file").val()==""){
            if ($("#imgBox").length>0){
                $.ligerDialog.warn("请上传新的签章图片!", '提示信息')
                return;
            }else {
                $.ligerDialog.warn("请上传签章图片!", '提示信息')
                return;
            }
		}
		if($("#userId").val()==""){
            $.ligerDialog.warn("请选择用户!", '提示信息')
            return;
		}
		$("#userEdit").submit();

	}
</script>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">签章图片配置</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group">
						<a class="link save" onclick="save()"><span></span>保存</a>
					</div>
					<div class="l-bar-separator"></div>
					<div class="group">
						<a class="link back" href="list.ht"><span></span>返回</a>
					</div>
				</div>
			</div>
		</div>
		<div class="panel-body">
		<form id="userEdit" method="post" action="save.ht" enctype="multipart/form-data" target="hideFrame">
			<div class="panel-detail">
			<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<th width="20%">选择用户:</th>
					<td>
						<input id="userId" name="userId" type="hidden" class="hidden" value="${user.userId}">
						<input id="username" type="text" class="inputText" value="${user.fullname}" readonly="readonly">
						<c:if test="${user==null}">
							<a class="link user" href="javascript:UserDialog({callback:dlgCallBack,isSingle:true});">选择</a>
						</c:if>
					</td>
				</tr>
				<tr>
					<th width="20%">所属部门:</th>
					<td>
						<span id="department">${user.orgName}</span>
					</td>
				</tr>
				<tr>
					<th width="20%">相关信息:</th>
					<td>
						<span id="relativeInfo">
							<c:if test="${user !=null}">
								手机号码：${user.mobile}，邮箱：${user.email}，账号：${user.account}
							</c:if>
						</span>
					</td>
				</tr>
				<tr>
					<th width="20%">签章图片:</th>
					<td>
						<input type="file" id="file" name="file" value=""/>
						<c:if test="${user!=null && user.webSignUrl!=null}">
							<div class="imgBox">
								<img style="max-width: 200px;height: auto;margin-top: 20px;" src="${staticUrl}${user.webSignUrl}">
							</div>
						</c:if>
					</td>
				</tr>
			</table>
			</div>
		</form>
		</div>
	</div>
	<iframe name="hideFrame" style="display: none;"></iframe>
</body>
</html>

