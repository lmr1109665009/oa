var domUtils = UE.dom.domUtils;
/**
 * 单元格格式刷
 */
UE.registerUI('tableformat',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
    	queryCommandState : function() {
    		return getTableItemsByRange(this).table ? 0 : -1;
    	},
    	execCommand : function() {
    		var ut = getUETableBySelected(this),
    			cell = null;
    		
    		if (!ut) {
    			var me = this;
                var start = me.selection.getStart(),
                    cell = start && domUtils.findParentByTagName(start, ["td", "th", "caption"], true),
                    ut = domUtils.findParentByTagName(cell, 'table');
            } else {
            	cell = ut.selectedTds[0];
            }
    		
            if(cell&&cell.className){
    			editor.tableFormatState=ut.className;
    			editor.cellFormatState=cell.className;
    		}
    	}
    });
    
    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title:"单元格格式刷",
        //icon : "icon-mini-csscopy",
        //需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
    	cssRules : 'background-image:url("../../../js/ueditor1433/themes/default/images/css.png")!important;background-repeat: no-repeat;margin-left:5px;',
        //点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });
	return btn;
}/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/);


/**
 *  应用单元格样式（在选择一个单元格以后，点击样式刷按钮后，再点击其他的单元格会触发，而且只触发一次） 
 */
UE.registerUI('applytableformat',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
    	queryCommandState : function() {
    		return getTableItemsByRange(this).table ? 0 : -1;
    	},
    	execCommand : function(t) {
			if(!editor.tableFormatState)return;
			var ut = getUETableBySelected(this);
	    	
	  	    if (ut && ut.selectedTds.length) {
	  	    	if(ut.table.className!=editor.tableFormatState){
	  	    		ut.table.className=editor.tableFormatState;
	  	    		ut.table.style=undefined;
				}
	  		    utils.each(ut.selectedTds, function (td) {
	    			if(td.className!=editor.cellFormatState){		    				
	    				td.className=editor.cellFormatState;
	    			}
	            });
	  		  editor.tableFormatState=null;
	  		  editor.cellFormatState=null;
	        }
	  	    else{
	  	    	var range = this.selection.getRange(),
		  	    	start = range.startContainer,
		  	    	td = domUtils.findParentByTagName(start, ['td','th'], true),
		  	    	table = domUtils.findParentByTagName(td, 'table');			
	  	    	if(td.className==editor.cellFormatState){
	  	    		return;
	  	    	}else{
	  	    		if(table.className!=editor.tableFormatState){
	  	    			table.className=editor.tableFormatState;
	  	    			table.style=undefined;
	  	    		}
	  	    		td.className=editor.cellFormatState;
	  	    		editor.tableFormatState=null;
	  	    		editor.cellFormatState=null;
	  	    	}
	  	    }
		}
    });

    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title:"应用单元格样式",
        //icon : "glyphicon glyphicon-th-list",
        //需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
    	cssRules : 'background-image:url("../../../js/ueditor1433/themes/default/images/css2.png")!important;background-repeat: no-repeat;margin-left:5px;',
        //点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });

    //因为你是添加button,所以需要返回这个button
    return btn;
}/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/);

/**
 * 根据当前框选的td来获取ueTable对象
 */
function getUETableBySelected(editor) {
    var table = getTableItemsByRange(editor).table;
    if (table && table.ueTable && table.ueTable.selectedTds.length) {
        return table.ueTable;
    }
    return null;
}

/**
 * 根据当前选区获取相关的table信息
 * @return {Object}
 */
function getTableItemsByRange(editor) {
    var start = editor.selection.getStart(),
    //在table或者td边缘有可能存在选中tr的情况
        cell = start && domUtils.findParentByTagName(start, ["td", "th"], true),
        tr = cell && cell.parentNode,
        caption = start && domUtils.findParentByTagName(start, 'caption', true),
        table = caption ? caption.parentNode : tr && tr.parentNode.parentNode;

    return {
        cell:cell,
        tr:tr,
        table:table,
        caption:caption
    }
}
