var app = angular.module('app', [ 'baseServices', 'commonListService', 'arrayToolService' ]);
app.controller("ctrl", [ '$scope', 'BaseService', 'CommonListService', 'ArrayToolService', function($scope, BaseService, CommonListService, ArrayToolService) {
	$scope.CommonList = CommonListService;
	$scope.ArrayTool = ArrayToolService;

	$scope.comment = "选择";

	$scope.openIconDialog = function() {
		var url= __ctx+"/js/ueditor1433/extend/custombutton/icons.jsp";
		DialogUtil.open({
			height : "380",
			width : "400",
			title : '选择图标',
			url : url,
			isResize : true,
			// 自定义参数
			sucCall : function(rtn) {
				$scope.$apply(function() {
					$scope.icon=rtn.cla;
				});
			}
		});
	};
} ]);