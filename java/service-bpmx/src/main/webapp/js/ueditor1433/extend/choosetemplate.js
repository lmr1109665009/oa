UE.registerUI('choosetemplate', function(editor, uiName) {
	// 创建一个button
	var btn = new UE.ui.Button({
		// 按钮的名字
		name : uiName,
		// 提示
		title : "选择模板",
		// 需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
		cssRules : 'background-position: -400px -40px;',
		// 点击时执行的命令
		onclick : function() {
			if(editor.type=="mb"){
				FormDef.selectTemplate(editor);
			}else if(typeof FormDef == 'undefined' || typeof tableId  == 'undefined'){//编辑器设计表单模式
				//重新选择模板（该方法的定义在bpmFormDefDesignEdit.jsp页面）
				showSelectTemplate(__ctx+'/platform/form/bpmFormDef/chooseDesignTemplate.ht?&isSimple=1');	
			}else{//表模式
				FormDef.showSelectTemplate(__ctx+'/platform/form/bpmFormDef/selectTemplate.ht?tableId=' + tableId +"&isSimple=1");
			}
		}
	});

	// 因为你是添加button,所以需要返回这个button
	return btn;
}/*
	 * index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId
	 * 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮
	 */);