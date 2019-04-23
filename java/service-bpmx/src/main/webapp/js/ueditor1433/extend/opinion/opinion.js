UE.registerUI('opinion', function(editor, uiName) {

	// 创建dialog
	var dialog = new UE.ui.Dialog({
		// 指定弹出层中页面的路径，这里只能支持页面,因为跟addCustomizeDialog.js相同目录，所以无需加路径
		iframeUrl : __ctx + '/js/ueditor1433/extend/opinion/opinionDialog.jsp',
		// 需要指定当前的编辑器实例
		editor : editor,
		// 指定dialog的名字
		name : uiName,
		// dialog的标题
		title : "意见",

		// 指定dialog的外围样式
		cssRules : "width:600px;height:150px;",

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
		} ]
	});

	// 参考addCustomizeButton.js
	var btn = new UE.ui.Button({
		name : "dialogbutton" + uiName,
		title : "意见",
		// 需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
		cssRules : 'background-position: -700px 0;',
		onclick : function() {//这个按钮进入的肯定是新增的，新增就删除这个编辑才用的对象
			delete dialog.targetEl;
			// 渲染dialog
			dialog.render();
			dialog.open();
		}
	});

	return btn;
});