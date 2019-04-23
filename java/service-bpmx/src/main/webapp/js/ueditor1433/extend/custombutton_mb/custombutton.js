UE.registerUI('custombutton', function(editor, uiName) {

	// 创建dialog
	var dialog = new UE.ui.Dialog({
		// 指定弹出层中页面的路径，这里只能支持页面,因为跟addCustomizeDialog.js相同目录，所以无需加路径
		iframeUrl : __ctx + '/js/ueditor1433/extend/custombutton_mb/custombuttonDialog.jsp',
		// 需要指定当前的编辑器实例
		editor : editor,
		// 指定dialog的名字
		name : uiName,
		// dialog的标题
		title : "自定义按钮",

		// 指定dialog的外围样式
		cssRules : "width:400px;height:200px;",

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
		title : "自定义按钮",
		// 需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
		cssRules : 'background-position: -340px -40px;',
		onclick : function() {//这个按钮进入的肯定是新增的，新增就删除这个编辑才用的对象
			var el = editor.selection.getStart();
			dialog.targetEl=el;
			
			// 渲染dialog
			dialog.render();
			dialog.open();
		}
	});

	// 注册按钮执行时的command命令,用uiName作为command名字，使用命令默认就会带有回退操作
	editor.registerCommand(uiName, {
		execCommand : function(cmdName, targetEl) {//用指令进来的肯定编辑模式，需要把编辑的对象传到页面
			dialog.targetEl=targetEl;
			dialog.render();
			dialog.open();
		},
		queryCommandValue : function() {
			return 0;
		}
	});

	return btn;
}/*
	 * index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId
	 * 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮
	 */);