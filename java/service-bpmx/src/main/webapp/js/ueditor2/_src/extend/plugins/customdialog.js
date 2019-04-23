/**
 * 自定义对话框
 * @function
 * @name baidu.editor.execCommands
 * @param    {String}    cmdName     cmdName="customdialog"
 */
UE.commands['customdialog'] = {	
	execCommand : function(cmdName) {
		var me=this;
		
		if(!me.ui._dialogs['customdialogDialog']){
			baidu.editor.ui['customdialog'](me);
		}
		me.ui._dialogs['customdialogDialog'].open();
	},
	queryCommandState : function() {
		return 0;
	}
}

UE.commands['customdialog_mb'] = {
		execCommand : function(cmdName) {
			var me=this;
			if(!me.ui._dialogs['customdialog_mbDialog']){
				baidu.editor.ui['customdialog_mb'](me);
			}
			me.ui._dialogs['customdialog_mbDialog'].open();
		},
		queryCommandState : function() {
			return 0;
		}
	}