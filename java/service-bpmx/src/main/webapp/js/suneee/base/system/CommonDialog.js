/**
 * 功能:弹出框
 * 
 */
function CommonDialog(conf){
	if(!conf.url){
		return;
	}
	var url=__ctx + conf.url;
	var dialogWidth = conf.dialogWidth ? conf.dialogWidth : 600;
	var dialogHeight= conf.dialogHeight ? conf.dialogHeight : 400;
	var title = conf.title ? conf.title : "通用对话框";
	//conf=$.extend({},{dialogWidth:dialogWidth ,dialogHeight:dialogHeight ,help:0,status:0,scroll:0,center:1},conf);
	DialogUtil.open({
		height:dialogHeight,
		width: dialogWidth,
		title: title,
		url: url, 
		isResize: true,
		buttons:[
			{text:"确定", onclick:function(item, dialog){
				var result = window.top.__resultData__;
				if(result == -1){
					alert("还没有选择数据项！");
					return;
				}
				if(conf.callBack){
					conf.callBack(result);
				}
				dialog.close();
			}},
			{text:"取消", onclick:function(item, dialog){dialog.close();}}
		]
	});
}