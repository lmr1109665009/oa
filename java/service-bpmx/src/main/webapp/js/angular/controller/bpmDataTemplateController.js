var bpmDataTemplateApp = angular.module('bpmDataTemplateApp', [ 'baseServices','DataRightsApp' ]);

bpmDataTemplateApp.controller('bpmDataTemplateCtrl',['$scope','BaseService','dataRightsService','$timeout',function($scope,BaseService,dataRightsService,$timeout){
	var service = dataRightsService;
	$scope.service = service;
	$timeout(function(){
		$scope.tab =$("#tab").ligerTab({});	
		$scope.hasInitTab = 124;
	},100)
	service.init($scope);
	$scope.filterUrl = __ctx + "/platform/form/bpmDataTemplate/filterDialog.ht?tableId=";
	$scope.save = function(){
		if ($scope._validForm()) {
			/*if ($scope.dataRightsJson.id) {
				$scope.dataRightsJson.resetTemp="1";
				$.ligerDialog.confirm("保存会覆盖模板，如果修改了模板请手动保存模板后再进行保存业务数据模板，是否继续保存？","提示信息",function(rtn) {
						if (rtn) service.customFormSubmit(showResponse);
					});
			} else {
				service.customFormSubmit(showResponse);
			}*/
			service.customFormSubmit(showResponse);
		}
	}
	
	showResponse = function(responseText){
		$.ligerDialog.closeWaitting();
		var obj = new com.hotent.form.ResultMessage(responseText);
		var resp = JSON.parse(obj.data);
		// if (obj.isSuccess()) {
		if (resp.result == 1){
			$.ligerDialog.confirm( resp.message+",是否继续操作","提示信息", function(rtn) {
				if(rtn){
					var url=location.href.getNewUrl();
					window.location.href =  location.href.getNewUrl();
				}else{
					$.closeWindow();
				}
			});
		} else {
			$.ligerDialog.error(resp.message,"提示信息");
		}
	}
	
	$scope._validForm = function (){
		var form=$('#dataRightsForm');
		if(!form.valid()) return false;
		//判断排序字段太多报错问题
		if($scope.sortFields&&$scope.sortFields.length>3){
			$.ligerDialog.error("排序字段不能设置超过3个，请检查！","提示信息");
			$scope.tab.selectTabItem("sortSetting");
			return false;
		}
		//判断管理字段
		if(service.manageFieldValid($scope.manageFields)){
			$.ligerDialog.error("功能按钮出现重复的类型，请检查！","提示信息");
			$scope.tab.selectTabItem("manageSetting");
			return false;
		}
		if($scope.dataRightsJson.templateAlias=="" || $scope.dataRightsJson.needPage ==""){
			$scope.tab.selectTabItem("baseSetting");
			$scope.form.valid();
			return false;	
		}
		return true;
	}
	//预览
	$scope.preview = function (){
		var alias = $scope.dataRightsJson.formKey;
		if($.isEmpty(alias)){
			$.ligerDialog.error("请设置完信息保存后预览!","提示信息");
			return ;
		}
		var url=__ctx+ "/platform/form/bpmDataTemplate/dataList_"+ alias+".ht";
		url=url.getNewUrl();
		$.openFullWindow(url);
	}
	//编辑模板
	$scope.editTemplate = function (){
		var id = $scope.dataRightsJson.id;
		if($.isEmpty(id)){
			$.ligerDialog.error("请设置完信息保存后编辑模板!","提示信息");
			return ;
		}
		var url=__ctx+ "/platform/form/bpmDataTemplate/editTemplate.ht?id="+id;
		url=url.getNewUrl();
		$.openFullWindow(url);
	}
	//添加菜单
	$scope.addToResource = function (){
		var formKey = $scope.dataRightsJson.formKey;
		var url="/platform/form/bpmDataTemplate/dataList_"+ formKey+".ht";
		AddResourceDialog({addUrl:url});
	}
	
	/**
	* 选择流程
	*/
	$scope.selectFlow = function (){
		BpmDefinitionDialog({isSingle:true,callback:function(defIds,subjects){
			$scope.dataRightsJson.defId = defIds;
			$scope.$apply(function(){
				$scope.dataRightsJson.subject = subjects;
			});
//			$scope.dataRightsJson.subject = subjects;
		}});
	};
	$scope.cancel = function (){
		$scope.dataRightsJson.defId = "";
		$scope.dataRightsJson.subject = "";
	}
}]);
