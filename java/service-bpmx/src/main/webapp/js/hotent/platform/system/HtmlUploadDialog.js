/**
 * 附件上传选择窗口。
 * dialogWidth：窗口宽度，默认值700
 * dialogWidth：窗口宽度，默认值500
 * callback：回调函数
 * 使用方法如下：
 * 
 * HtmlUploadDialog({conf,callback:picCallBack{
 *		//回调函数处理
 *	}});
 * @param conf
 */

function HtmlUploadDialog(conf) {
	if(!conf) conf={};
	var max = conf.max||0,
		type = conf.type||"";
	var url=__ctx + "/platform/system/sysFile/HtmlDialog.ht?max="+max+"&type="+type;
	var dialog = null;
	
	
	dialog = DialogUtil.openInWindow({
		passConf : {dialog:dialog},
		url:url,
		title : "附件上传",
		width : 800,
		height : 500,
		modal : true,
		resizable : true,
		buttons:[{
			text:'确定',
			onclick:function(e){
					var scope = dialog.jiframe[0].contentWindow.getData();
				    if(!scope){
				    	$.ligerDialog.alertExt("获取已上传文件信息时出错");
				    	return;
				    }
				    console.log(scope.uploader.getNotUploadedItems());
				    if(scope.uploader.getNotUploadedItems().length>0){
				    	$.ligerDialog.alertExt("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;有文件尚未上传，请上传该文件或删除该文件.","提示信息");
				    	return;
				    }
				    if(scope.uploader.queue.length==0){
				    	//$("<div class=l-window-mask style='z-index:9200;display: block'>").appendTo($("body"));
				    	$.ligerDialog.alertExt("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;至少需要上传一个文件.","提示信息");
				    	return;
				    }	
					if(conf.callback){
						var ary = [];
						$.each(scope.uploader.queue,function(item,obj){
							ary.push(obj.json);
						});
						conf.callback(ary);
						dialog.close();
					}else{
						dialog.close();
					}
			}
		},{
			text:'取消',
			onclick: function (item,dialog) { dialog.close(); }
		}]
	});
}