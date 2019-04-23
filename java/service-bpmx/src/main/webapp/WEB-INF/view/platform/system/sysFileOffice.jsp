<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://www.jee-soft.cn/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html>
<head>
<title>文件上传管理</title>

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
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/rule.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/TablePermission.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/CustomForm.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/OfficeControl.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/OfficePlugin.js"></script>
<c:set var="rights" value="${ param.rights}" />

<c:if test="${rights!='w' }">
	<c:set var="rights" value="r"></c:set>
</c:if>
<script type="text/javascript">
	$().ready(function (){
		getOffice();
        $(document).on("click",".office-download-close .l-dialog-close",function () {
            OfficePlugin.hiddenObj(false);
        });
	});
	// 获取Office 控件
	function getOffice(){
		OfficePlugin.init();
		/* var path= "${ctx}/platform/system/sysFile/file_${param.fileId}.ht";
		try{
			OfficePlugin.officeObjs[0].controlObj.OpenFromURL(path);
			OfficePlugin.officeObjs[0].setFileReadOnly(false);
		}
		catch(err){
			alert(err);
		} */
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
    /**
     * 显示留痕/隐藏留痕
     */
    var isShowLh=true;
    function showHideLhOperation(obj) {
        isShowLh=!isShowLh;
        var officeObj=OfficePlugin.officeObjs[0];
        officeObj.controlObj.ActiveDocument.ShowRevisions=isShowLh;
        if(isShowLh){
            $("label",obj).html("隐藏留痕");
        }else {
            $("label",obj).html("显示留痕");
        }
    }
    function submitData(isNeedClose) {
        OfficePlugin.isNeedClose=isNeedClose;
        OfficePlugin.isEditAttach=true;
        var path = __ctx +'/platform/system/sysFile/getFile.ht';
        $.post(path,{fileId:"${param.fileId}"},function(data){
            OfficePlugin.submit(data.fileName,"",data.ext);
        });
    }
</script>

<%-- IE浏览器下office控件自定义菜单和工具栏--%>
<script language="JScript" for="office_div" event="OnDocumentOpened(file, doc)">
	addCustomMenu(0);
</script>

<%--  自定义工具栏按钮事件配置
<script language="javascript" for="office_div" event="OnCustomToolBarCommand(btnIdx)">
	ntkocustomtoolbarcommand(btnIdx);
</script> --%>

<script language="javascript" for="office_div" event="OnCustomButtonOnMenuCmd(btnPos,btnCaption,btnCmdid)">
	customButtonOnMenuCmd(btnPos,btnCaption,btnCmdid)
</script>

<script language="javascript" for="office_div" event="OnCustomMenuCmd2(menuPos, submenuPos, subsubmenuPos, menuCaption, myMenuID)">
	customMenuCmd(menuPos, submenuPos, subsubmenuPos, menuCaption, myMenuID);
</script>


</head>
<body>
<div class="attach-container">
	<c:choose>
		<c:when test="${ rights=='w'}">
			<input type="hidden" class="hidden" name="${param.fileId}" value="${param.fileId}" lablename="filePreview" controltype="office" right="w" doctype="${ext }"
				   menuRight="{wjRight:'y',lhRight:'y',blhRight:'y',qchjRight:'y',mbthRight:'y',xzmbRight:'y',sxqmRight:'n',gzRight:'n',qpRight:'n',zcpdfRight:'n',ekeygzRight:'n',pdfgzRight:'n'}"
			/>
		</c:when>
		<c:otherwise>
			<input type="hidden" class="hidden" name="${param.fileId}" value="${param.fileId}" lablename="filePreview" controltype="office" right="r" doctype="${ext }"
				   menuRight="{wjRight:'y',lhRight:'n',blhRight:'n',qchjRight:'n',mbthRight:'n',xzmbRight:'n',sxqmRight:'n',gzRight:'n',qpRight:'n',zcpdfRight:'n',ekeygzRight:'n',pdfgzRight:'n'}"
			/>
		</c:otherwise>

	</c:choose>
</div>
<div class="panel-toolbar office-toolbar">
	<div class="toolBar">
		<div class="group menu-group">
			<c:if test="${ rights=='w'}">
				<a id="saveOffice" class="link update" style="width: auto;" href="javascript:;" onclick="submitData(false);"><span></span><label>保存</label></a>
				<a id="completeOffice" class="link save" style="width: auto;" href="javascript:;" onclick="submitData(true);"><span></span><label>完成</label></a>
				<a id="showHideLhBtn" class="link wodedingdan" style="width: auto;" href="javascript:;" onclick="showHideLhOperation(this)"><span></span><label>隐藏留痕</label></a>
			</c:if>
			<c:if test="${param.rights != 'rp'}">
				<a id="downLoad" class="link saoyisao" style="width: auto;" href="${ctx}/platform/system/sysFile/file_${param.fileId}.ht"><span></span><label>下载</label></a>
			</c:if>
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
					<th><a href="${ctx}/api/system/office/download.ht?id=${officeItem.id}">下载</a></th>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>

</body>
</html>
