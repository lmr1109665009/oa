<@override name="content">
    <div class="office-container">
        <div class="office-body"></div>
        <div class="office-toolbar">
            <div class="group">
                <#--<a href="#" class="office-button"><span class="glyphicon glyphicon-tags"></span>&nbsp;&nbsp;显示留痕</a>-->
                <a href="${path}" class="office-button"><span class="glyphicon glyphicon-file"></span>&nbsp;下载</a>
            </div>
            <#--<div class="group pull-right">
                <a href="javascript:loadOfficeApp()" class="office-button"><span class="glyphicon glyphicon-download-alt"></span>&nbsp;下载控件</a>
            </div>-->
        </div>
    </div>
    <div id="myModal" class="modal fade" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">

            </div>
        </div>
    </div>
</@override>

<@override name="customCss">
    <link href="${request.contextPath}/css/office.css" rel="stylesheet">
</@override>

<@override name="customJs">
    <script src="${request.contextPath}/js/office/jquery.office.js"></script>
    <script>
        var viewer=null;
        $(function () {
            var options={
                "contextPath":"${request.contextPath}",
                "file":{
                    "path":"${path}",
                    "ext":"${ext}"
                }
            };
            viewer=$(".office-body").viewFile(options);
        });
        function loadOfficeApp() {
            $(".office-body").hideFile();
           $.get("getOfficeList",function (data,status) {
             if (status!="success"){
                 alert("请求网络失败！");
                 $(".office-body").showFile();
                 return;
             }
             $("#myModal .modal-content").html(data);
             $("#myModal").modal();
           });
        }
        function closeMyModal() {
            $(".office-body").showFile();
            $("#myModal").modal('hide');
        }
    </script>
</@override>

<@extends name="/baseTemplate.ftl"/>