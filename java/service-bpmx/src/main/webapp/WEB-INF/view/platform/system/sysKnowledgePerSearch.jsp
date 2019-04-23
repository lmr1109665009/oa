<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" import="com.hotent.platform.model.system.Resources"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
	<title>人员权限查询</title>
	<%@include file="/commons/include/customForm.jsp" %>
	<base target="_self"/> 
	<%-- <link href="${ctx}/styles/ligerUI/ligerui-all.css" rel="stylesheet"	type="text/css" /> --%>
	<f:link href="Aqua/css/ligerui-all.css"></f:link>
	<link rel="stylesheet" href="${ctx }/js/tree/zTreeStyle.css" type="text/css" />
	<script type="text/javascript" src="${ctx}/js/tree/jquery.ztree.js"></script>
	<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerComboBox.js"></script>
	<script type="text/javascript">
	</script>
	<script type="text/javascript">
	var oldUserId;
	function perSearch(){
		var userId = $("#userID").val();
		if(userId == "" || oldUserId == userId){
			return;
		}
		oldUserId = userId;
		var url=__ctx + "/platform/system/sysKnowledgePer/searchUserPer.ht?userId="+userId;
		$.post(url,function(result){
			var html = "";
			for(var i = 0 ; i < result.length ; i++){
				var permissionJson = result[i].permissionJson;
				if(permissionJson==null || permissionJson==""){
					continue;
				}
				var perArray = permissionJson.split(",");
				html += '<tr name="oldDetailTr"><td style="width:20%" class="formTitle" align="right">'+result[i].typeName+':</td>';
				html +='<td style="width:80%" class="formInput">';
				for(var j = 0 ; j < perArray.length ; j++){
					var perName = ""
					if(perArray[j] == "readDir"){
						perName = "分类可读取";
					}else if(perArray[j] == "del"){
						perName = "文章可删除";
					}else if(perArray[j] == "edit"){
						perName = "文章可编辑";
					}else if(perArray[j] == "add"){
						perName = "可添加文章";
					}else if(perArray[j] == "read"){
						perName = "文章可读";
					}
					html += '<span class="owner-span">'+perName+'</span>';
				}
				html += '</td></tr>';
			}
			if(html == ""){//如果没有授权
				html += '<tr name="oldDetailTr"><td style="width:20%" class="formTitle" align="right"><td style="width:80%" class="formInput"><font style="color:red">没有授权信息</font></td></tr>';
			}
			$("[name='oldDetailTr']").remove();
			$("#detailTr").after(html);
		})
	}
	</script>
</head>
<body>
	<div class="panel">
		<div class="hide-panel">
			<div class="panel-top">
				<div class="panel-toolbar">
					<div class="toolBar">
						<div class="group">
							<a class="link preview" href="#" onclick="perSearch()" ><span></span>查询</a>
						</div>
						<div class="l-bar-separator"></div>
					</div>
				</div>
			</div>
		</div>
		<div>
			<table class="table-detail" cellpadding="0" cellspacing="0"
						border="0" >
				<tr id = "detailTr">
					<td style="width:20%" class="formTitle" align="right">用户选择:</td>
					<td style="width:80%" class="formInput"><span> <input id ="userID" name="userID" type="hidden" class="hidden" value="" />  <input type="text" name="user"  validate="{'maxlength':50}" readonly="readonly" scope="{'type':'system','value':'all'}" value="" />  <a href="javascript:;" class="link user" name="user">选择</a> </span></td>
				</tr>
			</table>
		</div>
		
	</div>
</body>
</html>
