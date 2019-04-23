var app = angular.module('app', [ 'baseServices', 'commonListService', 'arrayToolService' ]);
app.controller("ctrl", [ '$scope', 'BaseService', 'CommonListService', 'ArrayToolService', function($scope, BaseService, CommonListService, ArrayToolService) {
	$scope.CommonList = CommonListService;
	$scope.ArrayTool = ArrayToolService;

	// external对象
	$scope.external = {};
	$scope.external.tablerows = "3";
	$scope.external.show="1";
	$scope.external.modetype="inlinemodel";
	
	$scope.init = function(){
		if(targetEl){//编辑模式
			var external=$(targetEl).attr("external").replace(new RegExp(/(&#39;)/g),"'");
			$scope.external = eval("("+external+")");
		}
	};
} ]);