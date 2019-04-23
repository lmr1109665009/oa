var  app = angular.module("app", ['flowServiceModule','formDirective']);
app.controller('ctrl', ['$scope','flowService',function($scope,flowService){
	
	
	var runId=HtUtil.getParameter("runId");
	var defId=HtUtil.getParameter("defId");
	var id=HtUtil.getParameter("id");
	var status=HtUtil.getParameter("status");
	
	
	
	/**
	 * 显示审批历史。
	 */
	$scope.showOpinion=function(){
		flowService.showOpinion(runId);
	};
	
	/**
	 * 手机流程图
	 */
	$scope.showFlowPic=function(){
		flowService.showFlowPic(runId);
	};
	
	var confirmFn=function(e){
		var ary=[];
		var parent=$("[name='divConfirm']");
		$("[name='informType']",parent).each(function(){
			if(this.checked){
				ary.push(this.value);
			}
		});
		var informType=ary.join("");
		var opinion=$("[name='txtCause']",parent).val();
		
		
		var params= {id:id,opinion:opinion,informType:informType};
		var def=flowService.cancelAgent(params);
		def.then(function(data){
			if(data.result==1){
				Alert("提示信息","取回任务成功!",function(){
					location.href="myTurnOutList.html";
				});
			}
			else{
				Alert("提示信息","取回任务失败:"+ data.message);
			}
		},function(status){
			
		});
	}
	
	$scope.cancel=function(){
		var html=$("#myTable").html();
		Confirm("取消转办(代理)",html,confirmFn);
	};
	
	$scope.getInformType=function(){
		var def=flowService.getInformType();
		def.then(function(data){
			$scope.informTypes=data;
			
		},function(status){})
	},
	
	/**
	 * 初始化表单。
	 */
	$scope.initForm=function(){
		var json=HtUtil.getJSON("form_" +defId);
		var defer=null;
		if(json==null){
			defer=flowService.getInstForm(runId);
		}
		else{
			defer=flowService.getInstForm(runId,json.formKey,json.version);
		}
		defer.then(function(data){
			flowService.handForm($scope,data,defId);
		},
		function(status){
			console.info(status);
		});
	};
	//初始化表单。
	$scope.initForm();
	//状态
	$scope.status=status;
	//获取通知类型
	$scope.getInformType();
}]);