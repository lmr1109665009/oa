if (typeof FlowUtil == 'undefined') {
	FlowUtil = {};
}

/**
 * 启动流程。
 * @param defId 流程定义ID。
 */
FlowUtil.startFlow=function(defId,actDefId){
	var url= __ctx +"/platform/bpm/bpmDefinition/getCanDirectStart.ht";
	var params={defId:defId};
	$.post(url,params,function(data){
		if(data){
			var callBack=function(rtn){
				if(!rtn) return;
				var flowUrl= __ctx +"/platform/bpm/task/startFlow.ht";
				var parameters={actDefId:actDefId};
				$.post(flowUrl,parameters,function(responseText){
					var obj=new com.hotent.form.ResultMessage(responseText);
					if(obj.isSuccess()){//成功
						$.ligerDialog.success("启动流程成功!",'提示信息');
					}
					else{
						$.ligerDialog.error("启动流程失败!",'出错了');
					}
				});
			};
			$.ligerDialog.confirm("需要启动流程吗?",'提示信息',callBack);
		}else{
			var url=__ctx +"/platform/bpm/task/startFlowFormForSe.ht?defId="+defId;
//			jQuery.openFullWindow(url);
			DialogUtil.open({
				height:600,
				width: 900,
				title : '启动流程',
				url: url, 
				isResize: true,
			});
		}
	});
	return false;
};


/**
 * 执行任务。
 * @param taskId
 */
FlowUtil.executeTask=function(taskId){
	 var url= __ctx +"/platform/bpm/task/toStartForSe.ht?taskId="+taskId;
	 DialogUtil.open({
			height:600,
			width: 800,
			title : '流程审批',
			url: url, 
			isResize: true,
		});
}
