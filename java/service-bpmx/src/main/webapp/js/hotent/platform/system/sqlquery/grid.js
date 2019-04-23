var t=document.documentElement.clientWidth;  
window.onresize = function(){  
	if(t != document.documentElement.clientWidth){ 
		t = document.documentElement.clientWidth; 
		doResize(); 
	} 
} 

function doResize() { 
	var objSize = $.getPageSize(); 
	var panelHeight=$("#panelTop").height();
	$("#gridList").jqGrid('setGridWidth', objSize.width-40).jqGrid('setGridHeight', objSize.height-panelHeight-160); 
} 

function callBackFunc(){
	doResize();
}

function search(){
	var scope=$("#searchForm");
	var params={};
	var search = $("#gridList").jqGrid("getGridParam", "search");
	var postData = $("#gridList").jqGrid("getGridParam", "postData");
	$("[name^='Q_']",scope).each(function(){
		var obj=$(this);
		var val=obj.val();
		var key=obj.attr("name");
		delete postData[key];
		if(val && val!=""){
			params[key]=obj.val();
		}
	});
    $("#gridList").jqGrid('setGridParam',{postData:params,search:true}).trigger("reloadGrid"); //重新载入 
}

/**
 * 替换格式如类似格式的URL.
 * var url="/list.ht?id={id}&name={name}"
 * @param url
 * @param rowObject
 * @returns
 */
function replaceUrl(url,rowObject){
	var myregexp = /\{(.*?)\}/g;
	var match = myregexp.exec(url);
	while (match != null) {
		url=url.replace(match[0],rowObject[match[1]])
		match = myregexp.exec(url);
	}
	return url;
}

/**
 * 显示对话框。
 * @param dialogName	对话框名称
 * @param resultField	返回字段
 * @param targetCtl		
 */
function showCustomDialog(dialogName,resultField,targetCtl){
	CommonDialog(dialogName,function(data){
		var targetObj=$("#" + targetCtl);
		var v=[];
		if($.isArray (data)){
           for(var i=0;i<data.length;i++){
				var row=data[i];
				v.push(row[resultField]);
			}
		}
		else{
			v.push(data[resultField]);
		}
		targetObj.val( v.join(","));
	},""); 
}

/**
 * 导出对话框。
 */
function exports(){
	var param = $("#gridList").jqGrid("getGridParam");
	param.viewId = currentViewId;
	var url=  __ctx + "/platform/system/sysQueryView/exports.ht";
	var that = this;
	DialogUtil.open({
		height:500,
		width: 900,
		title : '导出数据',
		url: url, 
		conf : {
			param : param,
			searchParams : serializeObject($("#searchForm"))
		},
		isResize: true,
		sucCall:function(dialog){
			$.ligerDialog.success("导出数据成功！","提示信息");
			dialog.close();
		}
	});
}
