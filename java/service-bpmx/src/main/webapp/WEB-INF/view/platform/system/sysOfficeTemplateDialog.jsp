<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>选择office模板</title>
<%@include file="/commons/include/form.jsp" %>
<script type="text/javascript">
	/*KILLDIALOG*/
	var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)
	function getTemplate(templateId,subject,path){
		var obj={templateId:templateId,subject:subject,path:path};
		//window.returnValue=obj;
		dialog.get('sucCall')(obj);
		dialog.close();
	}
	function selectTemplate(){
		var obj=$('#templateIframe').contents().find("input:radio[name='rtn']:checked");
		if(obj.length==0){
			$.ligerDialog.warn("请选择一个模版!");
			return;
		}
		var aryRtn=obj.val().split(",");
		getTemplate(aryRtn[0],aryRtn[1],aryRtn[2]);
	}
	
	function closeOfficeTemp(){
		var obj = "";
		dialog.get('sucCall')(obj);
		dialog.close();
	}
	
</script>
<style type="text/css">
	div.bottom{text-align: center;padding-top: 10px;}
	html,body{padding:0px; margin:0; width:100%;height:100%;overflow: hidden;}
</style>
</head>
<body>
	<div position="center" style="height:85%;">
	<iframe id="templateIframe" src="selector.ht?templatetype=${type}" width="100%" height="100%" frameborder="0"></iframe>
	</div>
	<div position="bottom"   class="bottom" style="margin-top:10px;">
			<a href='#' class='button'  onclick="selectTemplate()" ><span class="icon ok"></span><span>选择</span></a>
			<a href='#' class='button' style='margin-left:10px;' onclick="closeOfficeTemp()"><span class="icon cancel"></span><span >取消</span></a>
		</div>
</body>
</html>


