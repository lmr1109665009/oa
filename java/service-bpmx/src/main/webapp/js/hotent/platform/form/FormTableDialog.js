/**
 * 功能:选择表话框
 * 
 */
function FormTableDialog(conf){
	var url=__ctx + '/platform/form/bpmFormTable/selector.ht';
	if(conf.isExternal!=undefined){
		url+="?isExternal=" +conf.isExternal;
	}
	var winArgs="dialogWidth:900px;dialogHeight:600px;help:0;status:1;scroll:1;center:1;resizable:1";
	url=url.getNewUrl();

	/*var rtn= window.showModalDialog(url,"",winArgs);

	if(rtn!=undefined){
		if(conf.callBack)
			conf.callBack.call(this,rtn.tableId,rtn.tableName);
	}*/
	/*KILLDIALOG*/
	var that =this;
	DialogUtil.open({
		height:600,
		width: 900,
		title : '选择表对话框',
		url: url, 
		isResize: true,
		//自定义参数
		sucCall:function(rtn){
			if(rtn!=undefined){
				if(conf.callBack)
					conf.callBack.call(that,rtn.tableId,rtn.tableName);
			}
		}
	});
}