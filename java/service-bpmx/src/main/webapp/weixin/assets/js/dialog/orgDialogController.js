		var base = angular.module("orgDialog", ["base"]);
		base.controller('OrgDialogController', ['$scope','baseService', function(scope,baseService) {
			var dialogConf = window.dialogConf;
			scope.permision =dialogConf.scope ||{};//数据的权限范围
			scope.returnData = dialogConf.initData||[];/*回显数据*/
			//确定按钮
        	scope.dialogOk=function(){
        		//将选择的值更新到data{[],[],}中
        		dialogConf.callBack(scope.returnData);
        		scope.closeDialog();
        	}
        	 
        	//重新加载对话框数据
        	scope.reload = function(isSearch){
        		//初始化page
        		/*if(!scope.pageParam) scope.pageParam ={"page":1,"pageSize":5};
        		//如果是查询则当前页面初始化为1 
        		if(isSearch)scope.pageParam.page=1;
        		//查询参数
        		var _queryName = scope.pageParam._queryName;
        		var _queryData =scope.pageParam._queryData;
        		if(_queryName&&_queryData)scope.pageParam[_queryName] =_queryData;
        		
        		$.post('/bpmx'+'/weixin/orgDialog/orgList.ht',scope.pageParam,function(data){
          			 scope.dataList = data.list;
          			 scope.pageParam = data.pageParam;
          			 //默认值设置
          			 if(!scope.pageParam._queryName){
          				 scope.pageParam.org_dimId=1;
          				 scope.pageParam._queryName='Q_orgName_SL';
          				 scope.loadOrgTree('orgTree');
          			 }
          			 scope.$digest();
          		 });*/
        		
        	}
        	//树过滤
        	scope.treeClick = function(event, treeId,treeNode){
    			scope.selectOne(false,treeNode);
    			scope.$digest();
        	}
        	//刷新左侧树
        	scope.loadOrgTree = function(treeId){
        		if(!scope.pageParam)scope.pageParam = {org_dimId:1};
        		// 权限
        		scope.pageParam.permisionType =scope.permision.type;
        		scope.pageParam.permisionTypeValue =scope.permision.value;
        		
        		var url = __ctx +'/weixin/orgDialog/getOrgListByDim.ht';
        		var orgTree = new ZtreeCreator(treeId,url)
 				.setCallback({onClick:scope.treeClick})
 				.setDataKey({idKey:"orgId",pIdKey:"orgSupId",name:"orgName"})
 				.setOutLookStyle()
 				.initZtree(scope.pageParam,1,function(treeObj,treeId){
 					});
        	}
    	   
			//选择一个
			scope.selectOne = function(index,data){
				var selectData = index===false?data:scope.dataList[index];
        		//页面列表选择情况
        		if(dialogConf.single){
        			scope.returnData[0] = selectData;
        			return;
        		}
        		for(var i=0,obj;obj=scope.returnData[i++];){
        			//判断是否重复,如果已经存在则返回。
					if(obj.orgId ==selectData.orgId) return;
        		}
        		scope.returnData.push(selectData);
        	}
			
			
			//删除选择
			scope.unChoiceOne = function(index){
        		scope.returnData.splice(index,1);
        	}
			scope.cleanSelect =function(){
				scope.returnData=[];
			}
			//前一页
        	scope.prewPage = function (){
        		if(scope.pageParam.page>1){
        			scope.pageParam.page--;
        			scope.reload();
        		}
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
        	 scope.loadOrgTree('orgTree');
	}
	]);
		
