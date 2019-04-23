var  app = angular.module("startFlow", ['flowServiceModule','formDirective']);
app.controller('ctrl', ['$rootScope','$scope','flowService','baseService',function($rootScope,$scope,flowService,baseService){
	var tableId = $("#tableId").val();
	var tableHtml = $("#formTemplate").val();
	
	tableHtml =tableHtml.replace(/&lt;/g,"<").replace(/&gt;/g,">");
	
	$("#formMsg").hide(); 
	//添加规则。
	$scope.getFormPreviewData = function(){
		var def=baseService.postForm(__ctx +"/weixin/bpm/getFormPreviewData.ht",{tableId:tableId});
		def.then(function(data){
			var template = Handlebars.compile(tableHtml);
			var html = template(data.permission);
			$scope.formHtml=html;
			$scope.data=data.data;
			$scope.permission=data.permission;
		},function(status){});
	}
	flowService.addRule();
	$scope.getFormPreviewData(); 
	
	/**
	 * 添加子表行
	 */
	$scope.addRow=function(tableName){
		flowService.addRow($scope,tableName);
		return false;
	};
	/**弹出框编辑子表行*/
	$scope.editRow=function(tableName,index){
		flowService.editRow($scope,tableName,index);
		return false;
	};
	/**将弹出框子表清空**/
	$scope.cleansubTempData=function(tableName){
		$scope.subTempData[tableName] ={};
	}
	/**将弹出框子表数据填充至子表**/ 
	$scope.pushTempDataToForm=function(tableName){
		var tempData =$.extend($scope.data.sub[tableName].rows[index],$scope.subTempData[tableName]);
		var index = tempData.$index;
		if(!index)index=0;
		$scope.data.sub[tableName].rows[index]=tempData;
		$('#'+tableName+'_editDialog').modal('close');
		//$rootScope.$digest();
	}
	
	/**
	 * 删除行
	 */
	$scope.removeRow=function(tableName,idx){
		flowService.removeRow($scope,tableName,idx);
	};
}]);