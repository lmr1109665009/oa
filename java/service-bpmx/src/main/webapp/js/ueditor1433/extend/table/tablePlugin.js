var domUtils = UE.dom.domUtils;
/**
 * 前插入行（完全复制当前行的）
 */
UE.registerUI('copytr2insertrow',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
    	queryCommandState : function() {
    		 var tableItems = getTableItemsByRange(this),
             cell = tableItems.cell;
    		 return cell && (cell.tagName == "TD" || (cell.tagName == 'TH' && tableItems.tr !== tableItems.table.rows[0]))? 0 : -1;
    	},
    	execCommand : function() {
            var rng = this.selection.getRange(),
            bk = rng.createBookmark(true);
	        var tableItems = getTableItemsByRange(this),
	            cell = tableItems.cell,
	            table = tableItems.table,
	            ut = UE.UETable.getUETable(table),
	            cellInfo = ut.getCellInfo(cell);
	        if (!ut.selectedTds.length) {
	            ut.insertRow(cellInfo.rowIndex, cell,tableItems.tr.children);
	        } else {
	            var range = ut.cellsRange;
	            for (var i = 0, len = range.endRowIndex - range.beginRowIndex + 1; i < len; i++) {
	                ut.insertRow(range.beginRowIndex, cell,tableItems.tr.children);
	            }
	        }
	        rng.moveToBookmark(bk).select();
	        if (table.getAttribute("interlaced") === "enabled")this.fireEvent("interlacetable", table);
	    }
    });
    
    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title:"前插入行",
    	className:'edui-for-insertrow',
    	index:50,
    	//点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });
	return btn;
},63/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/);


/**
 * 前插入行（完全复制当前行的）
 */
UE.registerUI('copytr2insertrownext',function(editor,uiName){
    //注册按钮执行时的command命令，使用命令默认就会带有回退操作
    editor.registerCommand(uiName,{
    	queryCommandState : function() {
    		   var tableItems = getTableItemsByRange(this),
               cell = tableItems.cell;
           return cell && (cell.tagName == "TD")? 0 : -1;
    	},
    	execCommand : function() {
	        var rng = this.selection.getRange(),
	            bk = rng.createBookmark(true);
	        var tableItems = getTableItemsByRange(this),
	            cell = tableItems.cell,
	            table = tableItems.table,
	            ut = UE.UETable.getUETable(table),
	            cellInfo = ut.getCellInfo(cell);
	        //ut.insertRow(!ut.selectedTds.length? cellInfo.rowIndex + cellInfo.rowSpan : ut.cellsRange.endRowIndex + 1,'');
	        if (!ut.selectedTds.length) {
	            ut.insertRow(cellInfo.rowIndex + cellInfo.rowSpan, cell,tableItems.tr.children);
	        } else {
	            var range = ut.cellsRange;
	            for (var i = 0, len = range.endRowIndex - range.beginRowIndex + 1; i < len; i++) {
	                ut.insertRow(range.endRowIndex + 1, cell,tableItems.tr.children);
	            }
	        }
	        rng.moveToBookmark(bk).select();
	        if (table.getAttribute("interlaced") === "enabled")this.fireEvent("interlacetable", table);
	    }
    });
    
    //创建一个button
    var btn = new UE.ui.Button({
        //按钮的名字
        name:uiName,
        //提示
        title:"后插入行",
    	className:'edui-for-insertrownext',
    	index:50,
    	//点击时执行的命令
        onclick:function () {
            //这里可以不用执行命令,做你自己的操作也可
           editor.execCommand(uiName);
        }
    });
	return btn;
},65/*index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮*/);
