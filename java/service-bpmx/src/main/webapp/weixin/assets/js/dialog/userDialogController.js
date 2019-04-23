var base = angular.module("userDialog", ["base"]);
base.controller('UserDialogController', ['$scope','baseService', function(scope,baseService){
	var dialogConf = window.dialogConf;
	scope.single=dialogConf.single;
	scope.permision =dialogConf.scope ||{};//数据的权限范围
	scope.returnData = dialogConf.initData||[];/*回显数据*/  
	scope.placeholder = "查找 账号/用户名";
	
	//确定按钮
		//将选择的值更新到data{[],[],}中
	scope.dialogOk=function(){
		dialogConf.callBack(scope.returnData);
		scope.closeDialog();
	}
	 
	//重新加载对话框数据isSearch，是否搜索,treeNode 只在树查找。
	scope.reload = function(isSearch,treeNode){
		//初始化page
		if(!scope.pageParam) {
			scope.pageParam ={"page":1,"pageSize":5};
			// 数据权限
			scope.pageParam.permisionType =scope.permision.type;
			scope.pageParam.permisionTypeValue =scope.permision.value;
		}
		//如果是查询则当前页面初始化为1 
		if(isSearch)scope.pageParam.page=1;
		var url=__ctx+'/weixin/orgDialog/userList.ht';
		var def=baseService.postForm(url,scope.pageParam);
		
		def.then(function(data){
    			//列表数据
    			scope.dataList = data.list;
    			//查询参数
    			$.extend(true,scope.pageParam,data.pageParam);
       			 //如果树搜索，且有数据，或者是搜索。
       			 if(isSearch && data.list.length>0){
       				 $("#tabs").tabs('open',1);
       			 }
       			 else if(treeNode && data.list.length==0){
       				$('#'+treeNode.tId+'_a').popover({
       					 trigger:'hover',
     					 content:'没有查到数据.',
     					 theme:'warning sm'
     				 });
       			 }
      			// if(isSearch) // scope.placeholder =scope.placeholder +' ('+scope.pageParam.count+')'
			},
			function(status){}
		);
	}
	
	/**
	 * 判断是否已经选择。
	 * 如果选择则返回选择的索引。
	 */
	scope.isSelected=function(obj){
		var userId=obj.userId;
		for(var i=0,obj;obj=scope.returnData[i++];){
			if(userId==obj.userId){
				return {result:true,index:i-1};
			}
		}
		return false;
	}

	//选择一个
	scope.selectOne = function(index){
		var selectData = scope.dataList[index];
		//单选的情况
		if(scope.single){
			scope.returnData.length=0;
			scope.returnData[0] = selectData;
		}
		//多选的情况。
		else{
			var obj=scope.isSelected(selectData);
			if(obj) return;
			
			scope.returnData.push(selectData);
		}
	}
	
	//清除所选的用户。
	scope.cleanSelect=function(){
		scope.returnData.length=0;
		$("#tabs").tabs('open',2);
	}
	
	//树过滤
	scope.treeClick = function(event, treeId,treeNode){
		scope.pageParam.orgId=treeNode.orgId;
		scope.placeholder='查找['+treeNode.orgName+']';
		scope.$digest();
		scope.reload(true,treeNode);
	}
	
	//加载组织树
	scope.loadOrgTree = function(){
		if(!scope.pageParam.org_dimId){
			 scope.pageParam.org_dimId=1;
		 }
		var param = {dimId:scope.pageParam.org_dimId};
		// 数据权限
		param.permisionType =scope.permision.type;
		param.permisionTypeValue =scope.permision.value;
		var url = __ctx+'/weixin/orgDialog/getOrgListByDim.ht';
		var orgTree = new ZtreeCreator("orgTree",url)
		.setCallback({onClick:scope.treeClick})
		.setDataKey({idKey:"orgId",pIdKey:"orgSupId",name:"orgName"})
		.setOutLookStyle()
		.initZtree(param,1,function(treeObj,treeId){});
	}
	
	//删除选择
	scope.unChoiceOne = function(index){
		scope.returnData.splice(index,1);
	}
	//前一页
	scope.prewPage = function (){
		if(scope.pageParam.page<=1) return;
		scope.pageParam.page--;
		scope.reload();
	}
	//后一页
	scope.nextPage = function (){
		if(scope.pageParam.page<scope.pageParam.pageCount){
			scope.pageParam.page++;
			scope.reload();
		}
	}
	// 关闭弹框
    	scope.closeDialog = function(){
    		dialogConf.closeDialog();
    	}

    	scope.reload();
    	scope.loadOrgTree();
}
]);
	
		