if (typeof FlowUtil == 'undefined') {
	FlowUtil = {};
}

/**
 * 提交。
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
						$.ligerDialog.success("提交成功!",'提示信息');
					}
					else{
						$.ligerDialog.error("提交失败!",'出错了');
					}
				});
			};
			$.ligerDialog.confirm("确定提交吗?",'提示信息',callBack);
		}else{
			var url=__ctx +"/platform/bpm/task/startFlowForm.ht?defId="+defId;
			jQuery.openFullWindow(url);
		}
	});
	return false;
};



/**
 * 流程撤回。
 * @param runId
 * @param memo
 */
FlowUtil.recover=function(conf){
	if(!conf) conf={backToStart: 0};	
	var url= __ctx +"/platform/bpm/processRun/checkRecover.ht";
	if(conf.backToStart==1){
		url= __ctx +"/platform/bpm/processRun/checkRedo.ht";
	}	
	var params={runId:conf.runId };
	$.post(url,params,function(data){
		 var obj=new com.hotent.form.ResultMessage(data);
		 if(obj.isSuccess()){
				var url=__ctx + '/platform/bpm/processRun/redoDialog.ht?runId=' + conf.runId +'&backToStart=' + conf.backToStart;
				var dialogWidth=600;
				var dialogHeight=450;
				conf=$.extend({},{dialogWidth:dialogWidth ,dialogHeight:dialogHeight ,help:0,status:0,scroll:0,center:1,reload:true},conf);
				url=url.getNewUrl();
		
				/*KILLDIALOG*/
				DialogUtil.open({
					height:conf.dialogHeight,
					width: conf.dialogWidth,
					title : '流程撤回',
					url: url, 
					isResize: true,
					//自定义参数
					sucCall:function(rtn){
						if(rtn && conf.callback){
							conf.callback(this);
						}
					}
				});
		 }
		 else{
			 //$.ligerDialog.err("提示","撤回失败!",obj.getMessage());
			$.ligerDialog.error(obj.data.message,"提示",function(){
				return;
			});
		 }
	});
};

