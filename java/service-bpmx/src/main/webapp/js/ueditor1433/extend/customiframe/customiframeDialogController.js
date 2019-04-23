var app = angular.module('app',['baseServices','commonListService','arrayToolService']);
app.controller("ctrl",['$scope','BaseService','CommonListService','ArrayToolService',function($scope,BaseService,CommonListService,ArrayToolService){
	
	console.log(editor);
	
	$scope.CommonList=CommonListService;
	$scope.ArrayTool=ArrayToolService;
	
	//external对象
	$scope.external={};
	
	$scope.external.name="iframeUrl";
	
	//选项值的数组
	$scope.external.options=[];
	
	$scope.external.dbType={};
	$scope.external.dbType.type="varchar";
	$scope.external.dbType.length="50";
	
	$scope.init = function(){
		initFields();
		if(targetEl){//编辑模式
			var external=$(targetEl).attr("external").replace(new RegExp(/(&#39;)/g),"'");
			$scope.external = eval("("+external+")");
		}
	};
	
	$scope.isDefClick = function(){
		$($scope.external.options).each(function(){
			this.isDefault="0";
		});
	};
	
	//初始化规则列表
	function initFields(){
		if (editor.tableId) {
			var url = __ctx + '/platform/form/bpmFormTable/getTableById.ht?tableId=' + editor.tableId + '&incHide=true';
			BaseService.post(url,{},function(data){
				$scope.maintable = data.table;
			});
		} else { //编辑器设计表单时获取字段并验证字段
			var html = editor.getContent();
			var params = {};
			params.html = html;
			params.formDefId = editor.formDefId;
			var url = __ctx + '/platform/form/bpmFormDef/validDesign.ht?incHide=true';
			BaseService.post(url,params,function(data){
				console.log(data);
				$scope.maintable = data.table;
			});
		}
	}
}]);