<%--
	time:2011-11-16 16:34:16
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<title>office</title>
<%@include file="/commons/include/customForm.jsp" %>
<f:link href="tree/zTreeStyle.css"></f:link>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerLayout.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.ztree.core.min.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerWindow.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/OfficePlugin.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/OfficeControl.js"></script>

<style type="text/css">
	.panel-body{
		overflow:hidden;
	}
	.l-layout-left{
		overflow:auto;
	}
	.doc-rename-wrap{
		text-align: center;
	}
	.doc-rename-wrap .label{
		width: 40px;
	}
	.doc-rename-wrap input[type="text"]{
		padding: 2px 10px 2px 10px;
		height: 28px;
		text-align: left;
		width: 175px;
	}
	.l-dialog-win .l-dialog-content{
		line-height: normal;
	}
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
	}
	.toolBar .link label{
		cursor: pointer;
	}
</style>
<script type="text/javascript">
	//var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)
	var fileId = $.getParameter("fileId");
	var menuRight = $.getParameter("menuRight");
	var right = $.getParameter("right");
	var isBinding = $.getParameter("isBinding");
	var bindingName = $.getParameter("bindingName");
	$(window).bind("load",function(){
		this.fileObjs=$("input[controltype='office']");
		for ( var i = 0; i < this.fileObjs.length; i++) {
			var fileObj = this.fileObjs.get(i);
			if(fileId.length>0){
				$(fileObj).val(fileId);
			}
			if(menuRight != "undefined"){
				$(fileObj).attr("menuRight",decodeURIComponent(menuRight));
			}
		}
		//Office控件初始化。
		OfficePlugin.init();
	});

	$(function() {
		//验证代码
		if(right != 'b' && right != 'w'){
			$("#dataSave").remove();
			$("#dataComplete").remove();
		}
		var menuRightObj=eval("("+menuRight+")");
		var isShowHideLhBtn=true;
		for (var prop in  menuRightObj){
		    if(!menuRightObj.hasOwnProperty(prop)){
		        continue;
			}
			if(prop=='showHideLhRight'&&menuRightObj[prop]=='y'){
		        isShowHideLhBtn=false;
			}
		}
		if (!isShowHideLhBtn){
		    $("#showHideLhBtn").remove();
		}

        $(document).on("click",".office-download-close .l-dialog-close",function () {
            OfficePlugin.hiddenObj(false);
        });
	});

	function submitData(isNeedClose) {
	    OfficePlugin.isNeedClose=isNeedClose;
	    OfficePlugin.isEditAttach=false;
		if(fileId.length != 0){
			var path = __ctx +'/platform/system/sysFile/getFile.ht';
			$.post(path,{fileId:fileId},function(data){
				   OfficePlugin.submit(data.fileName,"");
			 });
		}else{
			if(isBinding.length != 0){
				var finame = window.opener.getfiname(bindingName);
				OfficePlugin.submit(finame,"");
			}else{
                OfficePlugin.hiddenObj(true);
                renameDocFile("请输入文件名",function (isOk,val,dialog) {
                    if(isOk){
                        if(val&&val.trim()!=""){
                            if(OfficePlugin.submitNum>0){
                                dialog.close();
                                OfficePlugin.submit(val,"");//火狐和谷歌 的文档提交包括了  业务提交代码部分（完成  OfficePlugin.submit()后面的回调 函数 有 业务提交代码），所以 后面就不用加上业务提交代码
                            }
						}else {
                            $.ligerDialog.warn("请正确输入文件名!");
						}
					}else {
                        dialog.close();
                        OfficePlugin.hiddenObj(false);
					}
                });
			}
		}
	}

	function renameDocFile(title,callback) {
        var target = $($("#renameFileForm").html());
        var btnclick = function (item, Dialog, index)
        {
            if (callback)
            {
                callback(item.type == 'yes', $("input[type='text']",target).val(),Dialog);
            }
        }
        p = {
            title: title,
            target: target,
            width: 320,
            showMax: false,                             //是否显示最大化按钮
            showMin: false,
            allowClose: false,
            buttons: [{ text: $.ligerDefaults.DialogString.ok, onclick: btnclick, type: 'yes' }, { text: $.ligerDefaults.DialogString.cancel, onclick: btnclick, type: 'cancel'}]
        };
        return $.ligerDialog(p);
    }

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

    /**
	 * 打印操作
     */
    function printOperation() {
        var obj=OfficePlugin.officeObjs[0];
        obj.controlObj.ExitPrintPreview();
        obj.controlObj.PrintPreview();
    }
</script>

<%-- IE浏览器下office控件自定义菜单和工具栏--%>
<script language="JScript" for="office_div" event="OnDocumentOpened(file, doc)">
	addCustomMenu(0);
</script>

<%-- 自定义工具栏按钮事件配置
<script language="javascript" for="office_div" event="OnCustomToolBarCommand(btnIdx)">
	ntkocustomtoolbarcommand(btnIdx);
</script> --%>
<%-- 自定义菜单按钮事件配置 --%>
<script language="javascript" for="office_div" event="OnCustomButtonOnMenuCmd(btnPos,btnCaption,btnCmdid)">
	customButtonOnMenuCmd(btnPos,btnCaption,btnCmdid)
</script>
<%-- 自定义二级菜单按钮事件配置 --%>
<script language="javascript" for="office_div" event="OnCustomMenuCmd2(menuPos, submenuPos, subsubmenuPos, menuCaption, myMenuID)">
	customMenuCmd(menuPos, submenuPos, subsubmenuPos, menuCaption, myMenuID);
</script>

</head>
<body style="overflow:hidden">
	<div position="center">
		<input type="hidden" name="fileId" controltype="office"  value="${sysWordTemplate.fileId}"  right="w" fileType="wordTemplate"/>
	</div>
	<div>
		<div class="tbar-title">
			<span class="tbar-label">office文件</span>
		</div>
		<div class="panel-toolbar office-toolbar">
			<div class="toolBar">
				<div class="group menu-group">
					<a class="link update" id="dataSave" href="javascript:;" onclick="submitData(false);"><span></span>保存</a>
					<a class="link save" id="dataComplete" href="javascript:;" onclick="submitData(true);"><span></span>完成</a>
					<a class="link wodedingdan" id="showHideLhBtn" href="javascript:;" onclick="showHideLhOperation(this);"><span></span><label>隐藏留痕</label></a>
					<a class="link print" id="printBtn" href="javascript:;" onclick="printOperation();"><span></span>打印</a>
				</div>
				<div class="group" style="float: right;">
					<a class="link gengxin2" id="downloadCtl" href="javascript:;" onclick="downloadControl();"><span></span>下载控件</a>
				</div>
			</div>
		</div>
	</div>
	<div id="renameFileForm" style="display: none;">
		<div class="doc-rename-wrap">
			<span class="label">文件名:</span>
			<input size="18" type="text" name="Q_globalFlowNo_SL" class="inputText" value="">
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

