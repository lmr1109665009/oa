<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
    <%@include file="/commons/include/form.jsp"%>
    <title>编辑office下载文件</title>
    <style type="text/css">
        .table-detail .link, .btnlink, .table-detail input[type="button"]{
            padding-top: 8px;
            padding-bottom: 8px;
        }
    </style>
    <script type="text/javascript" src="${ctx }/js/hotent/platform/system/SysDialog.js"></script>
    <script type="text/javascript">
        function dlgCallBack(userId,fullname,email,phone,userObj) {
            if(!userId){
                return;
            }
            $("#userId").val(userId);
            $("#username").val(fullname);
            $("#relativeInfo").html('手机号码：'+phone+'，邮箱：'+email+'，账号：'+userObj.accounts);
            $("#department").html(userObj.orgNames);
        };
        //添加、保存office下载文件
        function save(){
            if($("#file").val()==""&&$("#id").val()==""){
                if ($("#imgBox").length>0){
                    $.ligerDialog.warn("请上传新的office文件!", '提示信息')
                    return;
                }else {
                    $.ligerDialog.warn("请上传office文件!", '提示信息')
                    return;
                }
            }
            $("#officeEdit").submit();
        }
    </script>
</head>
<body>
<div class="panel">
    <div class="panel-top">
        <div class="tbar-title">
            <span class="tbar-label">签章图片配置</span>
        </div>
        <div class="panel-toolbar">
            <div class="toolBar">
                <div class="group">
                    <a class="link save" onclick="save()"><span></span>保存</a>
                </div>
                <div class="l-bar-separator"></div>
                <div class="group">
                    <a class="link back" href="list.ht"><span></span>返回</a>
                </div>
            </div>
        </div>
    </div>
    <div class="panel-body">
        <form id="officeEdit" method="post" action="save.ht" enctype="multipart/form-data" target="hideFrame">
            <div class="panel-detail">
                <table class="table-detail" cellpadding="0" cellspacing="0" border="0">
                    <tr>
                        <th width="20%">显示名称:</th>
                        <td>
                            <input id="id" name="id" type="hidden" class="hidden" value="${officeObj.id}">
                            <input id="name" name="name" type="text" class="inputText" value="${officeObj.name}" style="width: 218px;">
                        </td>
                    </tr>
                    <tr>
                        <th width="20%">office文件:</th>
                        <td>
                            <input type="file" id="file" name="file" value=""/>
                            <c:if test="${officeObj!=null && officeObj.path!=null}">
                                &nbsp;&nbsp;
                                <a href="${staticUrl}${officeObj.path}">${officeObj.filename}</a>
                            </c:if>
                        </td>
                    </tr>
                </table>
            </div>
        </form>
    </div>
</div>
<iframe name="hideFrame" style="display: none;"></iframe>
</body>
</html>