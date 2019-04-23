var app = angular.module('app',['baseServices','commonListService','arrayToolService']);
app.controller("ctrl",['$scope','BaseService','CommonListService','ArrayToolService',function($scope,BaseService,CommonListService,ArrayToolService){
	$scope.CommonList=CommonListService;
	$scope.ArrayTool=ArrayToolService;
	
	//初始化控件宽高
	$scope.width="150";
	$scope.widthUnit="px";
	$scope.height="55";
	$scope.heightUnit="px";
}]);