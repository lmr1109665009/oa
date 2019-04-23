<%@ page language="java" contentType="text/html; charset=utf-8"
       pageEncoding="utf-8"%>
<%@page import="com.hotent.core.util.FileUtil"%>
<%@page import="com.hotent.core.api.util.PropertyUtil"%>
<%@page import="java.io.*"%>
<%
  	String path = request.getParameter("path");
	String filePath = PropertyUtil.getByAlias("file.upload")+File.separator+ path.replace("/", File.separator);
	byte[] bytes = FileUtil.readByte(filePath);
	response.getOutputStream().write(bytes);
	response.getOutputStream().flush();
	response.getOutputStream().close();
%>
