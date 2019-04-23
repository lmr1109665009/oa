UE.registerUI('flowchart', function(editor, uiName) {
	// 创建一个button
	var btn = new UE.ui.Button({
		// 按钮的名字
		name : uiName,
		// 提示
		title : "流程示意图",
		// 需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
		cssRules : 'background-position: -1000px 0;',
		// 点击时执行的命令
		onclick : function() {
			var element = editor.selection.getStart();//当前对象
			//不知道为什么有时候会选择到一个在html中都不存在的<br>标签，先手动找其父节点吧
			if ($(editor.selection.getStart()).is("br")) {
				element = element.parentElement;//
			}
			
			var div = $('<div class="flowchart" parser="flowchartedit"><input type="text"/></div>');
			$(element).append(div);
		}
	});

	// 因为你是添加button,所以需要返回这个button
	return btn;
}/*
	 * index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId
	 * 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮
	 */);