function IconDialog(conf){
	if(!conf) conf={};	
	var url=__ctx + "/platform/system/resources/dialog.ht";
	if(conf.params)
		url += "?" + conf.params;
	var dialogWidth=850;
	var dialogHeight=600;
	conf=$.extend({},{dialogWidth:dialogWidth ,dialogHeight:dialogHeight ,help:0,status:0,scroll:0,center:1},conf);
	url=url.getNewUrl();
	DialogUtil.open({
		height:conf.dialogHeight,
		width: conf.dialogWidth,
		title : '选择图标',
		url: url, 
		isResize: true,
		//自定义参数
		sucCall:function(rtn){
			if(conf.callback){
				conf.callback.call(this,rtn.srcValue); 
			}
		}
	});
};

