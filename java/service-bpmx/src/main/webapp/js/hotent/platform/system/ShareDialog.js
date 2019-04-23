// 属性及函数
 ShareDialog = {
	/**
	 * 显示历史对话框。
	 * objectId:关联数据ID
	 * callBack:回调函数
	 */
	showHis:function (objectId,callBack){
		DialogUtil.open({
	        height:540,
	        width: 800,
	        title : '历史操作记录',
	        url: __ctx +"/platform/system/sysHistoryData/list.ht?objId="+objectId, 
	        isResize: true,
	        //自定义参数
	        sucCall:function(rtn){
	        	if(rtn != null){
	        		callBack(rtn);// 恢复数据
	        	}
	        }
		});
	}
}

