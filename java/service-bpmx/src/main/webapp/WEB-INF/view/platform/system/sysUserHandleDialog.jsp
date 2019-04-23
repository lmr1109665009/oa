<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
	<title>用户批量导入</title>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/FlexUploadDialog.js"></script>
	<script type="text/javascript">
		var dialog = frameElement.dialog;
		$(function(){
			//导入文件
			$("#importForm").ajaxForm({success:showResponse});
			$("a.import").click(function(){
				if($("#userFile").val()==''){
					$.ligerDialog.warn("请选择导入excel文件","提示信息");
				}else{
					$("#importForm").submit();
					$.ligerDialog.waitting('正在导入用户信息，请等待...');
				}
			});
		});
		
		function showResponse(responseText){
			$.ligerDialog.closeWaitting();
			if(responseText.status == 0){//成功
				$.ligerDialog.success(responseText.message,"提示信息",function(){
					dialog.get('sucCall')();
					dialog.close();
				});
		    }else{//失败
		    	$.ligerDialog.err('出错信息',"批量导入用户信息失败",responseText.message);
		    }
		};
	</script>
</head>
<body>
<div class="panel">
	<div class="panel-top">
		<div class="tbar-title">
			<span class="tbar-label">用户批量导入</span>
		</div>		
	</div>
	<div class="panel-body">
		<form id="importForm" method="post" action="handleUser.ht">
			
			<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
				<tr height="40px">
					<th width="20%">选择导入文件:</th>
					<td>
						<div class="group"><input type="file"  id="userFile"  name="userFile"  value="导入"/></div>
					</td>
				</tr>
				<tr height="40px">
					<th width="20%">数据时间:</th>
					<td>
						<div class="group"><input type="text" class="inputText date"  id="compDate"  name="compDate"/></div>
					</td>
				</tr>
				<tr><td width="20%">&nbsp;</td>
					<td>
						<div class="group"><a class="link import" href="javascript:;"><span></span>导入</a></div>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
</body>
</html>
