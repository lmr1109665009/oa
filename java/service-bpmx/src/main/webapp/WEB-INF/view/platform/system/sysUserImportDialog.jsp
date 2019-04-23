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
					//if (!this.windowMask)
			        //{
			        //    this.windowMask = $("<div class='l-window-mask l-window-mask2' style='z-index:9200;display: block;'></div>").appendTo(window.parent.$("body"));
			       // }
			    	//$(".l-window-mask2").show();
					$("#importForm").submit();
					$.ligerDialog.waitting('正在导入用户信息，请等待...');
				}
			});
		});
		
		function showResponse(responseText){
			$.ligerDialog.closeWaitting();
			//window.parent.$(".l-window-mask2").hide();
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
		<!-- <div class="panel-toolbar">
			<form id="importForm" method="post" action="import.ht">
				<div class="group"><input type="file"  id="userFile"  name="userFile"  value="导入"/></div>
				<div class="l-bar-separator"></div>
				<div class="group"><a class="link upload" href="javascript:;"><span></span>导入</a></div>
				<div class="l-bar-separator"></div>
				<div class="l-bar-separator"></div>
				<div class="l-bar-separator"></div>
				<div class="l-bar-separator"></div>
				<div class="group"><a class="link del" href="javascript:;"><span></span>删除</a></div>
			</form>
		</div> -->
	</div>
	<div class="panel-body">
		<form id="importForm" method="post" action="import.ht">
			
			<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<th width="25%">下载导入模板:</th>
					<td>
						<div class="group">
							<f:a alias="download" css="link download" href="download.ht"><span></span>用户导入模板下载</f:a>
						</div>
					</td>
				</tr>
				<tr height="40px">
					<th width="20%">选择导入文件:</th>
					<td>
						<div class="group"><input type="file"  id="userFile"  name="userFile"  value="导入"/></div>
					</td>
				</tr>
				<tr height="40px">
					<th width="20%">说明:</th>
					<td>
						<span>
						    1、用户导入模板中，标*的列（用户姓名、帐号、部门名称、部门名称路径、企业编码、职位名称、邮箱、手机）必填，任意一列为空的记录都会被忽略；<br />
						    2、帐号在本系统中已经存在，但邮箱与系统已存在用户邮箱不同的记录将会被作为新用户，系统将根据原帐号生成系统唯一帐号；<br/>
						    3、系统生成新帐号的规则为：在帐号后追加依次递增的数字；<br/>
						    4、邮箱、手机号在本系统中已经存在，但帐号与系统已存在用户帐号不同的记录将会被忽略；<br/>
						    5、邮箱、手机号格式错误的记录将会被忽略；<br/>
						    6、用户信息在系统中已存在（账号和邮箱与系统中用户一致）的记录将会被更新；<br />
						    7、导入文件仅支持“.xls”格式。
						</span>
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
