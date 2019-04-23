/**
 * 为了ligerUi的顶层dialog显示
 */
var DialogUtil = {
	open : function(opts){
		if(opts.type=="post"){
			opts.postUrl = opts.url;
			opts.url=__ctx+"/platform/form/post/dialog.ht";
		}
		//原来的代码
		//var outerWindow = window.top;
		var outerWindow = window.parent;
		return outerWindow.$.ligerDialog.open(opts);
	},

	openInWindow : function(opts) {
		if(opts.type=="post"){
			opts.postUrl = opts.url;
			opts.url=__ctx+"/platform/form/post/dialog.ht";
		}
		var outerWindow = window.parent;
		return outerWindow.$.ligerDialog.open(opts);
	}
}