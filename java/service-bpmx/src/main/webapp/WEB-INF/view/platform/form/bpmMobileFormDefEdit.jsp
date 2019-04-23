<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<title>编辑自定义表单</title>
<%@include file="/commons/include/form.jsp"%>
<f:link href="tree/zTreeStyle.css"></f:link>
<f:link href="tab/tab.css"></f:link>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerLayout.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.ztree.core.min.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerWindow.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerTab.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/FormMobileDef.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/CommonDialog.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/FormDefUeditor.js"></script>

<!-- ueditor -->
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor_weixin.config.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor.all.min.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/lang/zh-cn/zh-cn.js"></script>
<!-- 下面是表生成和编辑器公用的组件 -->
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/choosetemplate.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/custombutton_mb/custombutton.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/insertfunction/insertfunction.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/daterange/daterange.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/datecalculate/datecalculate.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/customdialog/customdialog.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/cascadequery/cascadequery.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/setreadonly/setreadonly.js"></script>


<style type="text/css">
.panel-body {
	overflow: hidden;
}
</style>
<script type="text/javascript">
	var locale = 'zh_cn';
	
	var tableId = "${bpmMobileFormDef.tableId}";
	var editor;

	$(function() {
		//关闭父窗口
		//验证代码
		$("a.save").click(save);
		var winHeight = $(window).height()-120;
		$("#frmDefLayout").ligerLayout({leftWidth : 200,height:winHeight,
				onHeightChanged:function(layoutHeight, diffHeight, middleHeight){}
		});
		var height = $(".l-layout-center").height();
        $("#colstree").height(height-66);

        editor=FormDefUeditor.init({
			height : 240,
			width:227,
			lang : locale
		});
        
        editor.type="mb";//用于通知某些公用的拓展这次是mb模式
        
		//ueditor渲染textarea
		editor.isMoble_=true;
		editor.render("html");
		editor.tableId = tableId;
		//获取字段显示到左边的字段树中
		getAllFields();
		$("#btnPreView").click(function() {
			preview();
		});

		$("#btnHis").click(function() { // 绑定历史记录按钮
			showHis();
		});

		window.onbeforeunload = function() {
			return '将会重置已修改的表单数据！';
		};
		if (winHeight)
			$('body').height(winHeight);
		
	});

	//预览
	function preview() {
		window.onbeforeunload = null;
		var tableId = $("#tableId").val();
		
		var html = editor.getContent();
	
		var url="${ctx}/platform/form/bpmMobileFormDef/preview.ht";
		DialogUtil.open({
	        height:"600",
	        width: "800",
	        title : '手机表单预览',
	        url: url, 
	        isResize: true,
	        type:"post",
	        param:{
	        	formHtml:html,
	        	tableId:tableId
	        }
	    });
	};

	//保存表单数据。
	function save() {
		if (FormDef.isSourceMode) {
			$.ligerDialog.warn('不能在源代码模式下保存表单', "提示信息");
			return;
		}
		var rtn = $("#bpmFormDefForm").valid();
		if (!rtn) return;
		var data = {};
		var arr = $('#bpmFormDefForm').serializeArray();
		
		$.each(arr, function(i, d) {
			data[d.name] = d.value;
		});

		//保存当前tab的数据。
		var html = editor.getContent();
		data['formHtml'] =html.replace(/？/g, '');
	
		$.post('save.ht', data, FormDef.showResponse);
	}

	function getAllFields() {
		FormDef.getFieldsByTableId(tableId);
	}
	
	function closeParent(){
		if(window.opener && window.opener.closeWin){
			window.opener.closeWin();
		}
	}
</script>
<style type="text/css">
.myColor {
	color: red
}
</style>
</head>
<body style="overflow: hidden">
	<div>
		<div class="tbar-title"></div>
		<div class="panel-toolbar">
			<div class="toolBar">
				<div class="group">
					<a class="link save" id="dataFormSave" href="#"><span></span>保存</a>
				</div>
				<div class="l-bar-separator"></div>
				<div class="group">
					<a class="link preview" id="btnPreView" href="#"><span></span>预览</a>
				</div>
				<div class="group">
					<a class="link del" href="javascript:window.onbeforeunload = null;window.close()"><span></span>关闭</a>
				</div>
			</div>
		</div>
	</div>
	<div class="panel-body">
		<form id="bpmFormDefForm" method="post">
			<div class="panel-nav">
				<table cellpadding="0" cellspacing="0" border="0" class="table-detail">
					<tr>
						<th width="80">表单别名:&nbsp;</th>
						<td style="padding: 4px;"><input id="formKey" type="text" name="formKey" value="${bpmMobileFormDef.formKey}" <c:if test="${bpmMobileFormDef.id>0 }">readonly='readonly'</c:if> class="inputText" style="width: 100px" /></td>
						<th width="80">表单标题:&nbsp;</th>
						<td style="padding: 4px;"><input type="text" id="subject" name="subject" value="${bpmMobileFormDef.subject}" class="inputText" style="width: 200px" /></td>
					</tr>
				</table>
			</div>
			<input id="id" type="hidden" name="id" value="${bpmMobileFormDef.id}" /> 
			<input type="hidden" id="templatesId" name="templatesId" value="${bpmMobileFormDef.templatesId}" /> 
			<input type="hidden" id="tableId" name="tableId" value="${bpmMobileFormDef.tableId}" /> 
			<input type="hidden" id="tableName" name="tableName" value="${bpmMobileFormDef.tableName}" /> 
			<input type="hidden" id="categoryId" name="categoryId" value="${bpmMobileFormDef.categoryId}" />
		</form>
		<div id="tab" class="panel-nav">
			<div id="frmDefLayout">
				 <div position="left" title="表字段" style="overflow:auto;">
					<div class="tree-toolbar tree-title">
						<span class="toolBar">
							<div class="group">
								<a class="link reload" onclick="getAllFields()">刷新</a>
							</div>
						</span>
					</div>
					<ul id="colstree" class="ztree" ></ul>
				</div> 
				<div id="editor" position="center" style="overflow: hidden; height: 100%;">
					<textarea id="html" name=formHtml>${fn:escapeXml(bpmMobileFormDef.formHtml)}</textarea>
					
				</div>
			</div>
		</div>
	</div>
</body>
</html>

