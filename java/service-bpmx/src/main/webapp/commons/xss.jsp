<%@page import="com.hotent.core.web.util.RequestUtil" isErrorPage="true" pageEncoding="UTF-8"%>
<%
	String basePath=request.getContextPath();
%>
<%@include file="/commons/include/get.jsp"%>
<html>
	<head>
		<title>该页面输入了HTML标签</title>
			<style type="text/css">
			<!--
			.STYLE10 {
				font-family: "黑体";
				font-size: 36px;
			}
			-->  
	.link {
    border: 1px solid #999999;
    border-radius: 3px 3px 3px 3px;
    cursor: pointer;
    line-height: 23px;
    padding: 1px 7px 7px 8px;
    text-align: center;
}
			</style>
	</head>
	<body>
	 <table border="0" align="center" cellpadding="0" cellspacing="0">
	  <tr>
    	<td><img src="<%=basePath%>/styles/default/images/error/error_top.jpg" /></td>
  	  </tr>
	  <tr>
	    <td height="170" align="center" valign="top" background="<%=basePath%>/styles/default/images/error/error_bg.jpg">
	    	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	        <tr>
	          <td width="100%" valign="top" align="center">
	          	<table width="100%"  align="center">
	          		<tr height="25" align="center">
	          			<td >
	          			<span style="font-family: fantasy;font-size: 72px;font-weight: bold;">XSS</span>
	          			</td>
	          		</tr>
	          		<tr height="40" align="center">
	          			<td>
	          				当前检测到了XSS攻击,请检查页面是否输入了HTML标签!
	          			</td>
	          		</tr>
	          		
	          	</table>
	          	
	     	 </td>
	      </table>
	      </td>
	  </tr>    	 
	  <tr>
    	<td><img src="<%=basePath%>/styles/default/images/error/error_bootom.jpg" /></td>
      </tr>
	</table>
	</body>
</html>