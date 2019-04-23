var app = angular.module('app',['baseServices','commonListService','arrayToolService']);
app.controller("ctrl",['$scope','BaseService','CommonListService','ArrayToolService',function($scope,BaseService,CommonListService,ArrayToolService){
	$scope.CommonList=CommonListService;
	$scope.ArrayTool=ArrayToolService;
	
	//external对象
	$scope.external={};
	
	//初始化字段类型
	$scope.external.dbType={};
	$scope.external.dbType.type="varchar";
	$scope.external.dbType.length="50";
	
	//初始化字段来源
	$scope.external.valueFrom={};
	$scope.external.valueFrom.value="0";
	$scope.external.valueFrom.content="";
	
	//复选框值的数组
	$scope.external.options=[];
	
	$scope.init = function(){
		initRule();
		
		if(targetEl){//编辑模式
			var external=$(targetEl).attr("external").replace(new RegExp(/(&#39;)/g),"'");
			$scope.external = eval("("+external+")");
			//将数字转为字符串，如果数字类型，页面的选项字段反选不上
			if(typeof($scope.external.isRequired) == "number"){
				$scope.external.isRequired = ""+$scope.external.isRequired;
			}
			if(typeof($scope.external.isWebSign) == "number"){
				$scope.external.isWebSign = ""+$scope.external.isWebSign;
			}
			if(typeof($scope.external.isList) == "number"){
				$scope.external.isList = ""+$scope.external.isList;
			}
			if(typeof($scope.external.isFlowVar) == "number"){
				$scope.external.isFlowVar = ""+$scope.external.isFlowVar;
			}
		}
	};
	
	//初始化规则列表
	function initRule(){
		var url = __ctx + "/platform/form/bpmFormRule/getAllRules.ht";
		BaseService.post(url,{},function(data){
			$scope.ruleList=data;
		});
		
	}
}]);