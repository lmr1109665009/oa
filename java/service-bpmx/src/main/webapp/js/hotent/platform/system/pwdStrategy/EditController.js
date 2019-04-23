var app = angular.module('app',['baseServices','PwdStrategyService','commonListService','arrayToolService']);
app.controller("EditController",['$scope','BaseService','PwdStrategy','CommonListService','ArrayToolService',function($scope,BaseService,PwdStrategy,CommonListService,ArrayToolService){
	$scope.CommonList=CommonListService;
	$scope.ArrayTool=ArrayToolService;
	
	$scope.prop={};//prop 等于一个数据源初始化
	$scope.prop.forceChangeInitPwd=1;
	$scope.prop.pwdRule=1;
	$scope.prop.pwdLength=6;
	$scope.prop.validity=3;
	$scope.prop.handleOverdue=0;
	$scope.prop.overdueRemind=0;
	$scope.prop.verifyCodeAppear=0;
	$scope.prop.errLockAccount=0;
	$scope.prop.enable=1;
	
	//初始化数据
	if(id!=""){
		PwdStrategy.ngjs({action:"getById",id:id},function(data){
			$scope.prop=data;
		});
	}
	
	//保存
	$scope.save=function(){
		var frm=$('#frmSubmit').form({rules:com.hotent.form.rule.CustomRules});
		if(!frm.valid()) return;
		
		PwdStrategy.save($scope.prop,function(data){
			if(data.result==1){
				$.ligerDialog.confirm(data.message+",是否继续操作", "提示信息",
					function(rtn) {
						if (rtn) {
							window.location.reload();
						} else {
							window.location.href="list.ht";
						}
					});
			}else{
				$.ligerDialog.err("提示信息","保存失败",data.message);
			}
		});
	}
}]);