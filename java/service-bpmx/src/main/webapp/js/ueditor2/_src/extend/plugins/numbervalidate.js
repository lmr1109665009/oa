/**
 * 字段验证
 * @function
 * @name baidu.editor.execCommands
 * @param    {String}    cmdName     cmdName="numbervalidate"
 */
UE.commands['numbervalidate'] = {	
	execCommand : function(cmdName) {
		
	},
	queryCommandState : function() {
		
		var node = this.selection.getStart();
		
		if(node && "INPUT"==node.nodeName.toUpperCase()){
			var valid=node.getAttribute("validate")
			if(valid){
				var validObj=eval("(" + valid +")");
				if(validObj && validObj.number){
					return 0;
				}
			}
		}
		return -1;
	}
}