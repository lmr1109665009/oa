<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
	<title>组织架构批量导入</title>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/FlexUploadDialog.js"></script>
	<script type="text/javascript">
		var dialog = frameElement.dialog;
		$(function(){
			//导入文件
			$("#importForm").ajaxForm({success:showResponse});
			$("a.import").click(function(){
				if($("#orgFile").val()==''){
					$.ligerDialog.warn("请选择导入excel文件","提示信息");
				}else{
					$("#importForm").submit();
				}
			});
		});
		
		function showResponse(responseText){
			if(responseText.status == 0){//成功
				$.ligerDialog.success(responseText.message,"提示信息",function(){
					dialog.get('sucCall')();
					dialog.close();
				});
		    }else{//失败
		    	$.ligerDialog.err('出错信息',"批量导入组织架构失败", responseText.message);
		    }
		};
	</script>
</head>
<body>
<div class="panel">
	<div class="panel-top">
		<div class="tbar-title">
			<span class="tbar-label">组织架构导入</span>
		</div>		
	</div>
	<div class="panel-body">
		<form id="importForm" method="post" action="import.ht">
			
			<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<th width="25%">下载导入模板:</th>
					<td>
						<div class="group">
							<f:a alias="download" css="link download" href="download.ht"><span></span>组织架构导入模板下载</f:a>
						</div>
					</td>
				</tr>
				<tr height="40px">
					<th width="20%">选择导入文件:</th>
					<td>
						<div class="group"><input type="file"  id="orgFile"  name="orgFile"  value="导入"/></div>
					</td>
				</tr>
				<tr height="40px">
					<th width="20%">说明:</th>
					<td>
						<span>
						    1、组织导入模板中，标红的列（组织名称、企业编码、组织类型、组织名称路径）必填，任意一列为空的记录都会被忽略；<br />
						    2、组织信息在系统中已经存在（组织名称路径与系统中的组织信息一致）会更新企业编码和组织类型信息；<br />
						    3、导入文件中，组织的上级组织不存在时，则根据子组织的“组织名称路径”逐级创建；根据“组织名称路径”创建的上级组织，企业编码和组织类型跟该组织相同；<br/>
						    4、导入文件仅支持“.xls”格式。
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