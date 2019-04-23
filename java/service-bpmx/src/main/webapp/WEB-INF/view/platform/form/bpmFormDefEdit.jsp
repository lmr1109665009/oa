<%--
	time:2011-11-16 16:34:16
--%>
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
<script type="text/javascript" src="${ctx}/servlet/ValidJs?form=bpmFormDef"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerWindow.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerTab.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/FormDef.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/PageTab.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/FormContainer.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/CommonDialog.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/FormDefUeditor.js"></script>
<script type="text/javascript" src="${ctx}/js/util/base64.js"></script>

<!-- ueditor -->
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor_formDef.config.js"></script>
<!-- 
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor.all.min.js"></script>
 -->
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor.all.js?version=01"> </script>

<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/lang/zh-cn/zh-cn.js"></script>
<!-- 下面是表生成和编辑器公用的组件 -->
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/opinion/opinion.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/choosetemplate.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/custombutton/custombutton.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/taskopinion.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/flowchart.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/insertfunction/insertfunction.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/customquery/customquery.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/daterange/daterange.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/datecalculate/datecalculate.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/picture.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/instanceqrcode.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/customdialog/customdialog.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/importexceldialog/importexceldialog.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/exportexceldialog/exportexceldialog.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/cascadequery/cascadequery.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/setreadonly/setreadonly.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/tableformat/tableformat.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/table/tablePlugin.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/customiframe/customiframe.js"></script>
<style type="text/css">
.panel-body {
	overflow: hidden;
}
</style>
<script type="text/javascript">
	var locale = 'zh_cn';
	var tabTitle = "${bpmFormDef.tabTitle}";
	var tableId = "${bpmFormDef.tableId}";
	var tab;
	var editor;
	var flag = false;

	function showRequest(formData, jqForm, options) {
		return true;
	}

	function closeParent() {
		if (window.opener && window.opener.closeWin) {
			window.opener.closeWin();
		}
	}

	$(function() {
		//关闭父窗口。
		closeParent();
		//验证代码
		valid(showRequest, showResponse);
		$("a.save").click(save);
		var winHeight = $(window).height() - 90;
		$("#frmDefLayout").ligerLayout({
			leftWidth : 200,
			height : winHeight,
			onHeightChanged : function(layoutHeight, diffHeight, middleHeight) {
			}
		});
		var height = $(".l-layout-center").height();
		$("#colstree").css({"height": height- 100 , "overflow": "auto"});

		tab = $("#tab").ligerGetTabManager();
		editor = FormDefUeditor.init({
			height : 240,
			width : 227,
			lang : locale
		});
		editor.addListener('ready', function() {
            var content=$("<div>"+editor.getContent()+"</div>");
            $("[parser]",content).each(function (pos,item) {
                var label=$(item).attr("lablename");
                if (!label||label==""){
                    label=$(item).attr("opinionname");
                }
                if (!label||label==""){
                    label=$(item).attr("tabledesc");
                }
                if(!label){
                    label="";
                }
                $(this).attr("placeholder",label);
            });
            editor.setContent(content.html());
			initTab();
		});
		//ueditor渲染textarea
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
			return '你确定吗？';
		};
		if (height)
			$('body').height(height);
	});

	//预览
	function preview() {
		saveChange();
		window.onbeforeunload = null;
		var objForm = formContainer.getResult();
		var frm = new com.hotent.form.Form();
		frm.creatForm("bpmPreview", __ctx + "/platform/form/bpmFormHandler/edit.ht");
		frm.addFormEl("name", $("#subject").val());
		frm.addFormEl("title", objForm.title);
		frm.addFormEl("html", Base64.encode(objForm.form));
		frm.addFormEl("comment", $("#formDesc").val());
		frm.addFormEl("tableId", tableId);
		frm.setTarget("_blank");
		frm.setMethod("post");
		frm.submit();
		frm.clearFormEl();
	};

	//终极预览
	function finalPreview() {
		saveChange();
		window.onbeforeunload = null;
		var objForm = formContainer.getResult();
		var frm = new com.hotent.form.Form();
		frm.creatForm("bpmPreview", __ctx + "/platform/form/bpmFormDef/parse.ht");
		frm.addFormEl("name", $("#subject").val());
		frm.addFormEl("title", objForm.title);
		frm.addFormEl("html", Base64.encode(objForm.form));
		frm.addFormEl("comment", $("#formDesc").val());
		frm.addFormEl("tableId", tableId);
		frm.setTarget("_blank");
		frm.setMethod("post");
		frm.submit();
		frm.clearFormEl();
	};

	//保存表单数据。
	function save() {
		if(flag){return false;}
		flag = true;
		if (FormDef.isSourceMode) {
			$.ligerDialog.warn('不能在源代码模式下保存表单', "提示信息");
			return;
		}
		saveChange();
		var rtn = $("#bpmFormDefForm").valid();
		if (!rtn)
			return;
		//syncOpinion();
		var data = {};
		var arr = $('#bpmFormDefForm').serializeArray();
		$.each(arr, function(i, d) {
			data[d.name] = d.value;
		});

		//保存当前tab的数据。
		var idx = tabControl.getCurrentIndex() - 1;
		saveTabChange(idx);
		var objForm = formContainer.getResult();

		data['tabTitle'] = objForm.title;

		var content=$("<div>"+objForm.form+"</div>");
        $("[parser]",content).each(function (pos,item) {
            $(this).removeAttr("placeholder");
        });
		data['html'] = content.html();

		while (data['html'].indexOf('？') != -1) {
			data['html'] = data['html'].replace('？', '');
		}
		$.post('save.ht', {
			data : JSON2.stringify(data),
			tableName : $('#tableName').val()
		}, FormDef.showResponse);
	}

	function getAllFields() {
		FormDef.getFieldsByTableId(tableId);
	}

	//tab控件
	var tabControl;
	//存储数据控件。
	var formContainer;
	//添加tab页面
	function addCallBack() {
		var curPage = tabControl.getCurrentIndex();
		var str = tabControl.pageTitle;
		var idx = curPage - 1;
		formContainer.insertForm(str, "", idx);
		saveTabChange(idx - 1, idx);
	}
	//切换tab之前，返回false即中止切换
	function beforeActive(prePage) {
		if (FormDef.isSourceMode) {
			$.ligerDialog.warn('不能在源代码模式下切换页面', "提示信息");
			return 0;
		}
		return 1;
	}
	//点击激活tab时执行。
	function activeCallBack(prePage, currentPage) {
		if (prePage == currentPage)
			return;
		//保存上一个数据。
		saveTabChange(prePage - 1, currentPage - 1);
	}
	//根据索引设置数据
	function setDataByIndex(idx) {
		if (idx == undefined)
			return;
		var obj = formContainer.getForm(idx);
		editor.setContent(obj.form);
		$("b", tabControl.currentTab).text(obj.title);
	}
	//在删除页面之前的事件，返回false即中止删除操作
	function beforeDell(curPage) {
		if (FormDef.isSourceMode) {
			$.ligerDialog.warn('不能在源代码模式下删除页面', "提示信息");
			return 0;
		}
		return 1;
	}
	//点击删除时回调执行。
	function delCallBack(curPage) {
		formContainer.removeForm(curPage - 1);
		var tabPage = tabControl.getCurrentIndex();
		setDataByIndex(tabPage - 1);
	}
	//文本返回
	function txtCallBack() {
		var curPage = tabControl.getCurrentIndex();
		var idx = curPage - 1;
		var title = tabControl.currentTab.text();
		//设置标题
		formContainer.setFormTitle(title, idx);
	}
	//tab切换时保存数据
	function saveTabChange(index, curIndex) {
		var data = editor.getContent();
		formContainer.setFormHtml(data, index);
		setDataByIndex(curIndex);
	}
	//表单或者标题发生变化是保存数据。
	function saveChange() {
		var index = tabControl.getCurrentIndex() - 1;
		var title = tabControl.currentTab.text();
		var data = editor.getContent();
		formContainer.setForm(title, data, index);
	}
	//初始化TAB
	function initTab() {
		var formData = editor.getContent();
		if (tabTitle == "")
			tabTitle = "页面1";
		formContainer = new FormContainer();
		var aryTitle = tabTitle.split(formContainer.splitor);
		var aryForm = formData.split(formContainer.splitor);
		var aryLen = aryTitle.length;
		//初始化
		formContainer.init(tabTitle, formData);

		tabControl = new PageTab("pageTabContainer", aryLen, {
			addCallBack : addCallBack,
			beforeActive : beforeActive,
			activeCallBack : activeCallBack,
			beforeDell : beforeDell,
			delCallBack : delCallBack,
			txtCallBack : txtCallBack
		});
		tabControl.init(aryTitle);
		if (aryLen > 1) {
			editor.setContent(aryForm[0]);
		}
		;
	};

	// 显示历史记录
	function showHis() {
		var formDefId = $("#formDefId").val();
		DialogUtil.open({
			height : 540,
			width : 800,
			title : '历史操作记录',
			url : "${ctx}/platform/system/sysHistoryData/list.ht?type=formDefTemplate&objId=" + formDefId,
			isResize : true,
			//自定义参数
			sucCall : function(rtn) {
				if (rtn != null) {
					editor.setContent(rtn);
				}
			}
		});
	}
	
	//关闭页面提示
	function closeConfirm(){
		var callback = function(rtn) {
			if(!rtn) return;
			else {
				window.onbeforeunload = null;
				window.close()
			}
		};
		$.ligerDialog.confirm('确认要退出吗？','提示信息',callback);
		
	}
	
</script>
</head>
<body style="overflow: hidden">
	<div>
		<div class="tbar-title">
			<span class="tbar-label">在线表单编辑</span>
		</div>
		<div class="panel-toolbar">
			<div class="toolBar">
				<div class="group">
					<a class="link save" id="dataFormSave" href="javaScript:void(0)">
						<span></span>
						保存
					</a>
				</div>
				<div class="l-bar-separator"></div>
				<div class="group">
					<a class="link preview" id="btnPreView" href="javaScript:void(0)">
						<span></span>
						预览
					</a>
					<a class="link preview" href="javaScript:void(0)" onclick="finalPreview()">
						<span></span>
						终极预览
					</a>
				</div>
				<div class="group">
					<a class="link" id="btnHis" href="javaScript:void(0)">
						<span></span>
						查看历史记录
					</a>
				</div>
				<div class="group">
					<a class="link  del" href="javascript:closeConfirm();">
						<span></span>
						关闭
					</a>
				</div>
			</div>
		</div>
	</div>
	<div class="panel-body">
		<form id="bpmFormDefForm" method="post">
			<input id="formDefId" type="hidden" name="formDefId" value="${bpmFormDef.formDefId}" />
			<input id="tableId" type="hidden" name="tableId" value="${bpmFormDef.tableId}" />
			<input id="categoryId" type="hidden" name="categoryId" value="${bpmFormDef.categoryId}" />
			<input type="hidden" name="isDefault" value="${bpmFormDef.isDefault}" />
			<input type="hidden" name="versionNo" value="${bpmFormDef.versionNo}" />
			<input type="hidden" name="isPublished" value="${bpmFormDef.isPublished}" />
			<input type="hidden" name="publishedBy" value="${bpmFormDef.publishedBy}" />
			<input type="hidden" name="publishTime" value="<fmt:formatDate value="${bpmFormDef.publishTime}" pattern="yyyy-MM-dd HH:mm:ss"/>" />
			<input type="hidden" id="templatesId" name="templatesId" value="${bpmFormDef.templatesId}" />
			<div class="panel-nav">
				<table cellpadding="0" cellspacing="0" border="0" class="table-detail">
					<tr>
						<%--<th width="80">表单别名:&nbsp;</th>--%>
						<%--<td style="padding: 4px;">--%>
							<input id="formKey" type="hidden" name="formKey" value="${bpmFormDef.formKey}" <c:if test="${bpmFormDef.formDefId>0 }">readonly='readonly'</c:if> class="inputText" style="width: 100px" />
						<%--</td>--%>
						<th width="80">表单标题:&nbsp;</th>
						<td width="20%" style="padding: 4px;">
							<input id="subject" type="text" name="subject" value="${bpmFormDef.subject}" class="inputText" style="width: 200px" />
						</td>
						<th width="80">描述:&nbsp;</th>
						<td style="padding: 4px;">
							<input id="formDesc" type="text" name="formDesc" value="${bpmFormDef.formDesc}" class="inputText" style="width: 200px" />
						</td>
					</tr>
				</table>
			</div>
		</form>
		<div id="tab" class="panel-nav">
			<div id="frmDefLayout">
				<div position="left" title="表字段" style="overflow: auto;">
					<div class="tree-toolbar tree-title">
						<span class="toolBar">
							<div class="group">
								<a class="link reload" onclick="getAllFields()">刷新</a>
							</div>
						</span>
					</div>
					<ul id="colstree" class="ztree" style="overflow: auto;"></ul>
				</div>
				<div id="editor" position="center" style="overflow: hidden; height: 100%;">
					<textarea id="html" name="html">${fn:escapeXml(bpmFormDef.html) }</textarea>
					<div id="pageTabContainer"></div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>

