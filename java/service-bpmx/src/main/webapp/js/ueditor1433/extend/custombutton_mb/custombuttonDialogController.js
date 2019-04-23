var app = angular.module('app', [ 'baseServices', 'commonListService', 'arrayToolService' ]);
app.controller("ctrl", [ '$scope', 'BaseService', 'CommonListService', 'ArrayToolService', function($scope, BaseService, CommonListService, ArrayToolService) {
	$scope.CommonList = CommonListService;
	$scope.ArrayTool = ArrayToolService;

	$scope.comment = "选择";
} ]);