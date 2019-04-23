UE.registerUI('importexceldialog', function(editor, uiName) {

	// 创建dialog
	var dialog = new UE.ui.Dialog({
		// 指定弹出层中页面的路径，这里只能支持页面,因为跟addCustomizeDialog.js相同目录，所以无需加路径
		iframeUrl : __ctx + '/js/ueditor1433/extend/importexceldialog/importexceldialogDialog.jsp',
		// 需要指定当前的编辑器实例
		editor : editor,
		// 指定dialog的名字
		name : uiName,
		// dialog的标题
		title : "导入Excel",

		// 指定dialog的外围样式
		cssRules : "width:600px;height:500px;",

		// 如果给出了buttons就代表dialog有确定和取消
		buttons : [ {
			className : 'edui-okbutton',
			label : '确定',
			onclick : function() {
				dialog.close(true);// true才触发dialog.onok
			}
		}, {
			className : 'edui-cancelbutton',
			label : '取消',
			onclick : function() {
				dialog.close(false);
			}
		}]
	});

	// 注册按钮执行时的command命令,用uiName作为command名字，使用命令默认就会带有回退操作
	editor.registerCommand(uiName, {
		execCommand : function(cmdName, targetEl) {// 用指令进来的肯定编辑模式，需要把编辑的对象传到页面
			dialog.render();
			dialog.open();
		},
		queryCommandValue : function() {
			return 0;
		}
	});
}/*
	 * index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId
	 * 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮
	 */);