var base = angular.module("base", []);
base.controller('CustomDialogController', ['$scope', function(scope){
	//param打开对话框传递的参数
	scope.param = dialogConf.param||{},scope.returnData =[];
	if(isCombine)dialogConf.dialog_alias_ =listDialogalias;
	
	//确定按钮
	scope.dialogOk=function(){
		//将选择的值更新到data{[],[],}中
		var returnJson = [];
		for(var i=0,data;data=scope.returnData[i++];){
			var tempData={};
			for (var j = 0,field;field=returnField[j++];)
				tempData[field.field]=data[field.field];
			returnJson.push(tempData); 
		 }
		
		if(isSingle) returnJson =returnJson[0];
		dialogConf.callBack(returnJson);
		scope.closeDialog();
	}
    	 
	//重新加载对话框数据isSearch，是否搜索,treeNode 只在树查找。
	scope.reload = function(isSearch,treeNode){
		//初始化page
		var pageParam =scope.pageParam;
		//如果是查询则当前页面初始化为1 ,查询的时候，忽略树形参数
		if(!pageParam)pageParam ={"p":1,"z":5,"placeholder":"","dialog_alias_":dialogConf.dialog_alias_};
		if(isSearch){
			pageParam.p=1;
			pageParam[pageParam._queryName]=pageParam._queryData;
		}
		pageParam = $.extend(pageParam,scope.param);
		 
		$.post( __ctx+'/platform/form/bpmFormDialog/getDialogData.ht',pageParam,function(data){
   			//列表数据
			scope.dataList = data.list;
			//查询参数
			$.extend(true,pageParam,data.pageParam);
			
   			 //如果树搜索，且有数据，或者是搜索。
   			 if(isSearch && data.list.length>0){
   				 $("#tabs").tabs('open',isCombine?1:0);
   			 }
   			 else if(treeNode && data.list.length==0){
   				$('#'+treeNode.tId+'_a').popover({
   					 trigger:'hover',
 					 content:'没有查到数据.',
 					 theme:'warning sm'
 				 });
   			 }
   			 scope.handelChecked();
  			 if(isSearch)pageParam.placeholder =pageParam.placeholder +' ('+pageParam.count+')'
  			scope.pageParam = pageParam;
  			 scope.$digest();
	 	});
		
	}
    	
	//ListTree选择一个
	scope.selectOne = function(index){
		var selectData = scope.dataList[index];
		if(!selectData.checked) scope.pushToReturnData(selectData);
	}
	/**
	 * 树点击
	 * 组合对话框情况下，为list提供参数，
	 * 普通情况向returnData 填充数据
	 */
	scope.treeClick = function(event, treeId,treeNode){
		if(isCombine){
			for(var i=0,p;p=scope.tree4ListParamKey[i++];){
				scope.pageParam[p.listField] =treeNode[p.treeField];
			}
			scope.reload(true,treeNode);
			scope.pageParam.placeholder='查找['+treeNode[scope.treeDisplayName]+']';
		}else{
			scope.pushToReturnData(treeNode);
		}
		scope.$digest();
	}
	//将选择数据添加到返回列表中
	scope.pushToReturnData = function(data){
		if(isSingle)  scope.returnData[0] =data;
		else if (scope.returnData.length ==0)scope.returnData.push(data);
		else{
			var isSame = false;
			for (var i = 0; i < scope.returnData.length; i++) {
				var thisData =scope.returnData[i];
				var curentOneIsSame =true;
				for (var j = 0,field;field=returnField[j++];) {
					if(thisData[field.field] != data[field.field]){
						curentOneIsSame = false; 
						continue;
					}
				}
				if(curentOneIsSame){
					isSame = true;
					break;
				}
			}
			if(!isSame){
				scope.returnData.push(data);
			}
		}
		scope.handelChecked();
	}
		
	scope.loadOrgTree = function(){
		// 树形参数
		var treeParam = eval("("+treeParamStr+")");
		
		var orgTree = new ZtreeCreator("customerTree",treeUrl)
		.setDataKey({idKey:treeParam.id,pIdKey:treeParam.pid,name:treeParam.displayName})
		.setCallback({onClick:scope.treeClick})
		.setOutLookStyle()
		.initZtree({},1,function(treeObj,treeId){
			
			});
		//组合对话框情况下，树作为列表参数传入
		if(isCombine){
			var combineParam = eval("(" + combineField + ")");
			scope.tree4ListParamKey =[];
			for (var int = 0; int < combineParam.length; int++) {
				var temp ={} ;
				temp.treeField =combineParam[int].list.fieldName;
				temp.treeDisplayName =treeParam.displayName;
				temp.listField =combineParam[int].tree.fieldName;
				temp.defaultValue =combineParam[int].list.defaultValue;
				scope.tree4ListParamKey.push(temp);
			}
			scope.treeDisplayName =treeParam.displayName;
		}
		
		
	}
		
	//删除选择
	scope.unChoiceOne = function(index){
		scope.returnData.splice(index,1);
		scope.handelChecked();
	}
	//前一页
	scope.prewPage = function (){
		if(scope.pageParam.p>1){
			scope.pageParam.p--;
			scope.reload();
		}
	}
	//后一页
	scope.nextPage = function (){
		if(scope.pageParam.p<scope.pageParam.pageCount){
			scope.pageParam.p++;
			scope.reload();
		}
	}
	// 关闭弹框
	scope.closeDialog = function(){
		dialogConf.closeDialog();
	}
	scope.cleanSelect=function(){
		scope.returnData.length=0;
	}
	//选中已选的checkBox
	scope.handelChecked = function(){
	 if(!scope.dataList) return;
	 for(var i=0,data;data=scope.dataList[i++];){
	    data.checked = false;
		for (var k = 0,rd;rd=scope.returnData[k++];){
			var isSame =true;
			for (var j = 0,field;field=returnField[j++];) {
				if(rd[field.field] != data[field.field]){
					isSame = false; 
				}
			}
			if(isSame)data.checked=true;
		}
	 }
	}
	
	if(needList) scope.reload();
	if(needTree) scope.loadOrgTree();
}
]);
		
		