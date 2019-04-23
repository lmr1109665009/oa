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
		//debugger;
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
var iPadBackFrame = {
		back : function(opts){
		var iPad = navigator.userAgent.indexOf('iPad') > -1; //是否iPad
		var iVersion= window.sessionStorage.getItem("version");//获取缓存浏览器version
		if(iPad||iVersion == 'iPad'){
			var href_href=window.sessionStorage.getItem("Hosthref");
			window.top.location.href=href_href;
		}
		else{
			if(opts==true){
			dialog.close();
			}
		}
}
}