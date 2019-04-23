/**
 * 表单编辑器的uedtor相关js
 */
var FormDefUeditor = {
	// 初始化editor
	init : function(conf) {
		var h = $(window).height(), w = $(window).width(), lang = conf.lang ? conf.lang : 'zh_cn';
		h = conf.height ? (h - conf.height) : h;
		w = conf.width ? (w - conf.width) : w;
		var editor = new baidu.editor.ui.Editor({
			minFrameHeight : h,
			initialFrameWidth : w,
			lang : conf.lang
		});
		editor.addListener("sourceModeChanged", function(t, m) {
			FormDef.isSourceMode = m;
		});

		initMenu(editor);
		return editor;
	}
};

// 初始化组件的属性菜单
function initMenu(editor) {
	var popup = initPopup(editor);

	// 为editor添加鼠标经过某些元素时需要弹出的菜单
	editor.addListener('mouseover', function(t, evt) {
		var el = evt.target || evt.srcElement;
		// 表单设计编辑模式下parser通常都在父级span下
		if (!$(el).is("[external]") && $(el).parent().is("[external]")) {
			el = el.parentElement;
		}

		// 是否手机端表单
		var isNgInput = el.hasAttribute("ht-input") || el.hasAttribute("ht-date") || el.hasAttribute("ht-selector") || el.hasAttribute("ht-textarea") || el.hasAttribute("ht-customerdialog");
		if (!$(el).is("[parser]") && !isNgInput) {// 没有解释器，说明不是字段
			if (!$(el).is("a.extend")) {// 这个是自定义按钮，目前其他都不显示菜单了，可能有漏网之鱼
				return;
			}
		}

		var str = "";
		// 可以到这边说明可以显示菜单了，下面是菜单内容的拼装过程
		var menuHtml = "";
		if (!isNgInput) {// PC端表单
			menuHtml += '<span onclick="$$._onInputDelButtonClick()" class="edui-clickable">删除</span>&nbsp;&nbsp;';
			
			//解析器类型
			var parser = $(el).attr("parser");
			
			if ($(el).is("[external]")) {
				// 添加一个编辑按钮
				menuHtml += '<span onclick="$$._onInputEditButtonClick(event, this);" class="edui-clickable">编辑</span>&nbsp;';
			}

			if ($(el).is("a.extend")) {// 自定义按钮配置 添加 查询设置和导入导出Excel
				menuHtml += '<span onclick="$$._onInputDialogButtonClick(event, this);" class="edui-clickable">查询设置</span>&nbsp;&nbsp;';
				menuHtml += '<span onclick="$$._onInputImportExcelButtonClick(event, this);" class="edui-clickable">导入Excel</span>&nbsp;&nbsp;';
				menuHtml += '<span onclick="$$._onInputExportExcelButtonClick(event, this);" class="edui-clickable">导出Excel</span>&nbsp;&nbsp;';
			}

			if ($(el).is("select") || ($(el).is("[external]") && $(el).children().is("select"))) {// 下拉框
				// 级联菜单
				menuHtml += '<span onclick="$$._onCascadeQueryClick(event, this);" class="edui-clickable">级联设置</span>&nbsp;&nbsp;';
			}

			//单行文本和多行文本可以设置是否只读
			if(typeof  parser != 'undefined' && (parser == 'inputedit' || parser == 'textareaedit' || parser == 'decimaltopercentedit')){
				var hasReadOnly=(el.childNodes[0].tagName=="INPUT"||el.childNodes[0].tagName=="TEXTAREA")&&el.childNodes[0].getAttribute("readonly")?true:false
				if(hasReadOnly){
					menuHtml += '<span onclick="$$._unReadOnlyClick(event, this);" class="edui-clickable">删除只读</span>&nbsp;&nbsp;';
				}else{
					menuHtml += '<span onclick="$$._onReadOnlyClick(event, this);" class="edui-clickable">设为只读</span>&nbsp;&nbsp;';
				}
			}else if(typeof  parser != 'undefined' && (parser == 'inputtable' || parser == 'textareatable' || parser == 'decimaltopercenttable')){
				var hasReadOnly=(el.tagName=="INPUT"||el.tagName=="TEXTAREA")&&el.getAttribute("readonly")?true:false
				if(hasReadOnly){
					menuHtml += '<span onclick="$$._unReadOnlyClick(event, this);" class="edui-clickable">删除只读</span>&nbsp;&nbsp;';
				}else{
					menuHtml += '<span onclick="$$._onReadOnlyClick(event, this);" class="edui-clickable">设为只读</span>&nbsp;&nbsp;';
				}
			}
		} else {// 手机端表单
			if ($(el).is("[ht-customerDialog]")) {// 手机端才用到
				menuHtml += '<span onclick="$$._onCustomerDialogClick(event, this);" class="edui-clickable">设置自定义对话框</span>&nbsp;&nbsp;';
				menuHtml += '<span onclick="$$._onUnCustomerDialogClick(event, this);" class="edui-clickable">移除自定义对话框</span>&nbsp;&nbsp;';
			} else {
				// 默认移除按钮
				menuHtml += '<span onclick="$$._onRemoveInputClick(event, this);" class="edui-clickable">移除</span>&nbsp';
			}

		}

		html = popup.formatHtml('<nobr>' + menuHtml + '</nobr>');
		if (popup.getDom("content")) {// 已生成过菜单，要改变其dom元素
			popup.getDom("content").innerHTML = html;
		} else {// 第一次dom元素还不存在的，直接设置content则可
			popup.content = html;
		}
		popup.anchorEl = el;
		popup.showAnchor(popup.anchorEl);
	});
}

function initPopup(editor) {
	var utils = baidu.editor.utils, uiUtils = baidu.editor.ui.uiUtils, UIBase = baidu.editor.ui.UIBase, domUtils = baidu.editor.dom.domUtils;

	var popup = new baidu.editor.ui.Popup({
		editor : editor,
		content : '',
		className : 'edui-bubble',
		// ⑨扩展
		// 处理字段的剪切和粘贴
		_onInputCutButtonClick : function() {
			this.hide();
			var child = popup.anchorEl;
			editor.curCutInput = child;
			domUtils.remove(child);
		},
		// ⑦扩展
		_onInputDelButtonClick : function() {
			this.hide();
			if (popup.anchorEl) {
				popup.anchorEl.parentNode.removeChild(popup.anchorEl);
			}
		},
		// ⑤扩展
		// 处理输入框的修改
		_onInputEditButtonClick : function() {
			this.hide();
			editor.execCommand(popup.anchorEl.className, popup.anchorEl);// 把编辑中的对象传到指令中
		},
		// ⑥扩展
		// 处理输入框的自定义对话框
		_onInputDialogButtonClick : function() {
			this.hide();
			editor.curInput = popup.anchorEl;
			editor.execCommand("customdialog");
		},
		// 扩展
		// 处理导入Excel的自定义对话框
		_onInputImportExcelButtonClick : function() {
			this.hide();
			editor.curInput = popup.anchorEl;
			editor.execCommand("importexceldialog");
		},
		// 扩展
		// 处理导出Excel的自定义对话框
		_onInputExportExcelButtonClick : function() {
			this.hide();
			editor.curInput = popup.anchorEl;
			editor.execCommand("exportexceldialog");
		},
		// ⑩扩展
		// 级联设置
		_onCascadeQueryClick : function() {
			this.hide();
			var el = popup.anchorEl;
			if ($(el).is("[external]")) {
				el = el.children[0];
			}
			editor.curInput = el;
			editor.execCommand("cascadequery");
		},

		// 删除级联
		_onUnCascadeQueryClick : function() {
			this.hide();
			var child = popup.anchorEl;
			if (child) {
				editor.curInput = child;
				editor.execCommand("uncascadequery");
			}
		},
		// 设为只读
		_onReadOnlyClick : function() {
			this.hide();
			var child = popup.anchorEl;
			if (child) {
				editor.curInput = child;
				editor.execCommand("setreadonly");
			}
		},
		// 删除只读
		_unReadOnlyClick : function() {
			this.hide();
			var child = popup.anchorEl;
			if (child) {
				editor.curInput = child;
				editor.execCommand("delreadonly");
			}
		},
		_onCustomerDialogClick : function() {
			this.hide();
			var child = popup.anchorEl;
			if (child) {
				editor.curInput = child;
				editor.execCommand("customdialog");
			}
		},
		_onUnCustomerDialogClick : function() {
			this.hide();
			var child = popup.anchorEl;
			if (child) {
				editor.curInput = child;
				var parent = $(child).closest("[type='customerButton']");
				if (parent.length != 1)
					$(child).remove();
				else {
					parent.parent().append($("input", parent));
					parent.remove();
				}
			}
		},
		_onRemoveInputClick : function() {
			this.hide();
			var el = popup.anchorEl;
			$(el).remove();
		}
	});
	return popup;
}