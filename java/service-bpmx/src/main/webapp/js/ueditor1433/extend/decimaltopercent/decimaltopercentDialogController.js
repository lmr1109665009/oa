var app = angular.module('app',['baseServices','commonListService','arrayToolService']);
app.controller("ctrl",['$scope','BaseService','CommonListService','ArrayToolService',function($scope,BaseService,CommonListService,ArrayToolService){
	$scope.CommonList=CommonListService;
	$scope.ArrayTool=ArrayToolService;
	
	//external对象
	$scope.external={};
	
	//初始化字段类型
	$scope.external.dbType={};
	$scope.external.dbType.type="number";
	$scope.external.dbType.length="50";
	$scope.external.dbType.intLen="14";
	$scope.external.dbType.decimalLen="0";
	$scope.external.dbType.minValue="0";
	$scope.external.dbType.maxValue="0";
	
	//初始化字段来源
	$scope.external.valueFrom={};
	$scope.external.valueFrom.value="0";
	$scope.external.valueFrom.content="";
	
	//初始化控件宽高
	$scope.external.width="150";
	$scope.external.widthUnit="px";
	$scope.external.height="18";
	$scope.external.heightUnit="px";
		
	
	$scope.init = function(){
		initSerialnum();
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
	
	//是否货币
	$scope.eIsCoinChange = function(){
		if($scope.external.dbType.isCoin=='1'){
			$scope.external.dbType.isShowComdify="1";
			$scope.external.dbType.coinValue="￥";
		}else{
			$scope.external.dbType.coinValue="";
		}
	};
	
	//选择脚本
	$scope.addScript = function(){
		ScriptDialog({
			callback : function(data) {
				$scope.$apply(function() {
					$scope.external.valueFrom.content+=data;
				});
			}
		});
	};
	
	//初始化流水号
	function initSerialnum(){
		var url = __ctx + "/platform/system/identity/getAllIdentity.ht";
		BaseService.post(url,{},function(data){
			$scope.serialnumList=data;
		});
	}
	
	//初始化规则列表
	function initRule(){
		var url = __ctx + "/platform/form/bpmFormRule/getAllRules.ht";
		BaseService.post(url,{},function(data){
			$scope.ruleList=data;
		});
		
	}
}]);