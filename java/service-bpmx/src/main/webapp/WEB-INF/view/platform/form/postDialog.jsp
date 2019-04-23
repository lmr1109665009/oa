<%@page pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<%@include file="/commons/include/form.jsp"%>
<title></title>
<script type="text/javascript">
	$(function() {
		var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)
		var param = dialog.get("param");
		var url = dialog.get("postUrl");
		
		var frm = new com.hotent.form.Form();
		frm.creatForm("form", url);
		for(var key in param){
			frm.addFormEl(key, param[key]) ;
		}
		frm.setMethod("post");
		frm.submit();
	});
</script>
</head>
<body>
</body>
</html>