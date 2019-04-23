<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://www.jee-soft.cn/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html>
<head>
<title>文件预览</title>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<jsp:include page="/commons/include/iconfont.jsp"></jsp:include>
<f:link href="web.css" ></f:link>
<f:link href="form.css" ></f:link>
<f:js pre="js/lang/common" ></f:js>
<f:js pre="js/lang/js" ></f:js>
<f:link href="Aqua/css/ligerui-all.css" ></f:link>
<script type="text/javascript" src="${ctx}/js/dynamic.jsp"></script>
<style type="text/css">
    .office-control-list{
        padding: 15px;
    }
    .office-control-list table{
        width: 100%;
        border-collapse: collapse;
    }
    .office-control-list table tr{
        height: 30px;
        vertical-align: middle;
    }
    .office-control-list table tr td,.office-control-list table tr th{
        border: 1px solid #e6e6e6;
        padding: 0px 10px;
        line-height: normal;
    }
</style>
<script type="text/javascript" src="${ctx}/js/jquery/jquery.js"></script>
<script type="text/javascript" src="${ctx}/js/jquery/jquery.form.js"></script>
<script type="text/javascript" src="${ctx}/js/util/util.js"></script>
<script type="text/javascript" src="${ctx}/js/util/json2.js"></script>
<script type="text/javascript" src="${ctx}/js/util/form.js"></script>

<script type="text/javascript" src="${ctx}/js/lg/base.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerMenu.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerMenuBar.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerDialog.js" ></script>

<script type="text/javascript" src="${ctx}/js/lg/util/DialogUtil.js" ></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/OfficeControl.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/OfficePlugin.js"></script>
<script type="text/javascript">
	$().ready(function (){
		getOffice();
        $(document).on("click",".office-download-close .l-dialog-close",function () {
            OfficePlugin.hiddenObj(false);
        });
	});
	// 获取Office 控件
	function getOffice(){
		OfficePlugin.init({
			downloadPath:"${ctx}/me/newDocFile/getFileById.ht?id=",
            onlyRead:true
		});
	};
    function downloadControl() {
        OfficePlugin.hiddenObj(true);
        var p = {
            title:"Office相关控件下载",
            content: $("#officeCtrInfo").html(),
            buttons: [],
            showMax: false,
            showToggle: false,
            showMin: false,
            allowClose:true,
            width:600,
            height:300,
            cls:"office-download-close"
        };
        return $.ligerDialog(p);
    }
</script>

<%-- IE浏览器下office控件自定义菜单和工具栏--%>
<script language="JScript" for="office_div" event="OnDocumentOpened(file, doc)">
	addCustomMenu(0);
</script>

<script language="javascript" for="office_div" event="OnCustomButtonOnMenuCmd(btnPos,btnCaption,btnCmdid)">
	customButtonOnMenuCmd(btnPos,btnCaption,btnCmdid)
</script>

<script language="javascript" for="office_div" event="OnCustomMenuCmd2(menuPos, submenuPos, subsubmenuPos, menuCaption, myMenuID)">
	customMenuCmd(menuPos, submenuPos, subsubmenuPos, menuCaption, myMenuID);
</script>


</head>
<body>
<div class="attach-container">
	<input type="hidden" class="hidden" name="${param.id}" value="${param.id}" lablename="filePreview" controltype="office" right="r" doctype="${ext }"
		   menuRight="{wjRight:'y',lhRight:'n',blhRight:'n',qchjRight:'n',mbthRight:'n',xzmbRight:'n',sxqmRight:'n',gzRight:'n',qpRight:'n',zcpdfRight:'n',ekeygzRight:'n',pdfgzRight:'n'}"
	/>
</div>
<div class="panel-toolbar office-toolbar">
	<div class="toolBar">
		<div class="group menu-group">
			<a id="downLoad" class="link saoyisao" style="width: auto;" href="${ctx}/me/newDocFile/download.ht?id=${param.id}"><span></span><label>下载</label></a>
		</div>
		<div class="group" style="float: right;">
			<a class="link gengxin2" id="downloadCtl" href="javascript:;" onclick="downloadControl();"><span></span><label>下载控件</label></a>
		</div>
	</div>
</div>
<div id="officeCtrInfo" style="display: none">
	<div class="office-control-list">
		<table>
			<c:forEach var="officeItem" items="${officeList}">
				<tr>
					<th>${officeItem.name}</th>
					<th><a href="${staticUrl}${officeItem.path}">下载</a></th>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>
	
</body>
</html>
