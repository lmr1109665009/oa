<%@ page language="java" contentType="text/html; charset=utf-8"
       pageEncoding="utf-8"%>
<%@page import="com.hotent.platform.model.file.Uploader"%>
<%@page import="java.io.*"%>


    <%
    request.setCharacterEncoding("utf-8");
	response.setCharacterEncoding("utf-8");
	Uploader up = new Uploader(request);
    String[] fileType = {".gif" , ".png" , ".jpg" , ".jpeg" , ".bmp"};
    up.setSavePath("imageUpload");
    up.setAllowFiles(fileType);
    up.setMaxSize(10000); //单位KB
    up.upload();
    String rtPath = up.getUrl();
    rtPath = rtPath.replace('\\', '/');
    response.getWriter().print("{'original':'"+up.getOriginalName()+"','url':'?path="+rtPath+"','title':'"+up.getTitle()+"','state':'"+up.getState()+"'}");
    %>
