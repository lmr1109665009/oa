<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
    <%@include file="/commons/include/get.jsp" %>
    <title>office控件下载管理</title>
    <style type="text/css">
        html,body{
            overflow:auto;
        }
    </style>
</head>
<body>
<div class="panel">
    <div class="panel-top">
        <div class="tbar-title">
            <span class="tbar-label">office控件下载列表</span>
        </div>
        <div class="panel-toolbar">
            <div class="toolBar">
                <div class="group"><a class="link add" href="edit.ht"><span></span>上传</a></div>
            </div>
        </div>
    </div>
    <div class="panel-body">
        <display:table name="officeList" id="officeItem" requestURI="list.ht" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
            <display:column property="id" title="序号"></display:column>
            <display:column property="name" title="显示名称"></display:column>
            <display:column property="filename" title="文件名"></display:column>
            <display:column title="操作" media="html" class="opsBtnb">
                <a class="link" href="sort.ht?flag=up&id=${officeItem.id}" class="link edit">上移</a>&nbsp;
                <a class="link" href="sort.ht?flag=down&id=${officeItem.id}" class="link edit">下移</a>&nbsp;
                <a class="link" href="${staticUrl}${officeItem.path}" class="link edit">下载</a>&nbsp;
                <a class="link" href="edit.ht?id=${officeItem.id}" class="link edit">编辑</a>&nbsp;
                <a class="link" href="del.ht?id=${officeItem.id}" class="link edit" onclick="return confirm('是否要删除该文件？')">删除</a>
            </display:column>
        </display:table>
    </div>
</div>
</body>
</html>