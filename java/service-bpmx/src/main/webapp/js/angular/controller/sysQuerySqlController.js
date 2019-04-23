var sysQuerySqlApp = angular.module('sysQuerySqlApp', [ 'baseServices','DataRightsApp','sysQuerySqlServiceApp' ]);
sysQuerySqlApp.controller('sysQuerySqlCtrl',['$scope','BaseService','sysQuerySqlService','$timeout',function($scope,BaseService,sysQuerySqlService,$timeout){
	var service = sysQuerySqlService;
	$scope.buttonKey = 'urlParams';
	$scope.service = service;
	$timeout(function(){
		$scope.tab =$("#tab").ligerTab({contextmenu:false});	
		$scope.hasInitTab = 124;
		$scope.$digest();
	},100)
	service.init($scope);
	$scope.save = function(){
		var form = $('#sysQuerySqlForm');
		if (!form.valid()) return;
		if (service.validSql(false)) {
			service.customFormSubmit($scope.buttonKey);
		}
	}
}])
.controller('sysQuerySqlDefCtrl',['$scope','sysQuerySqlService','$timeout',function($scope,sysQuerySqlService,$timeout){
	var service = sysQuerySqlService;
	$scope.buttonKey = 'buttonDef';
	$scope.service = service;
	$timeout(function(){
		$scope.tab =$("#tab").ligerTab({contextmenu:false});	
		$scope.hasInitTab = 124;
		$scope.$digest();
	},100);
	service.init($scope);
	$scope.save = function(){
		var form = $('#sysQuerySqlForm');
		if (!form.valid()) return;
		if (service.validSql(false)) {
			service.customFormSubmit($scope.buttonKey);
		}
	}
	$scope.setVirtualField = function(isVirtual){
		if(!isVirtual) return;
	}
	//sysQueryFields
}])
.controller('sysQuerySqlUrlCtrl',['$scope','sysQuerySqlService','dataRightsService',function($scope,sysQuerySqlService,dataRightsService){
	var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)
	var conf =dialog.get('conf');
	var service = sysQuerySqlService;
	$scope.service = service;
	$scope.service.util = dataRightsService;
	$scope.sysQueryFields = conf.sysQueryFields;
	$scope.urlParams = conf.urlParams?parseToJson(conf.urlParams):[];
	$scope.save = function(){
		dialog.get('sucCall')(JSON2.stringify($scope.urlParams));
		dialog.close();
	}
	$scope.close = function(){
		dialog.close();
	}
	$scope.add = function(){
		$scope.urlParams.push({});
	}
}])
.directive('fieldSort', ['$timeout',function($timeout){
	function sortNumber(row1, row2){
		return row1.sn - row2.sn;
	}
	
	var link = function($scope, $element, $attrs, $ctrl){
		var field=$attrs.ngModel;
		var timeout;
	    $scope.$watch(field,function(newVal){
    		if (timeout) {
                 $timeout.cancel(timeout);
             }
             timeout = $timeout(function() {
            	 var rows=$scope.$parent.sysQueryFields;
 		    	 rows.sort(sortNumber)
             }, 500);
	    });
	}
	return {
		require:"ngModel",
		link:link
	}
   
}]);