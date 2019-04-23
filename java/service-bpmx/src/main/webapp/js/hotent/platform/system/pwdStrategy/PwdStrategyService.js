angular.module('PwdStrategyService', ['baseServices'])
.service('PwdStrategy', ['$rootScope','BaseService', '$http',function($rootScope,BaseService,$http) {
    var service = {    		
    		//保存
    		save:function(json,callback){
     			$http.post(__ctx +'/platform/system/pwdStrategy/save.ht',json).success(function(data, status, headers, config){
    				if(callback){
	    				callback(data);
	    			 }
    			});
    		},
    		//ngjs请求
    		ngjs : function(json,callback){
    			BaseService.post(__ctx +'/platform/system/pwdStrategy/ngjs.ht',json,function(data){
    				if(callback){
	    				callback(data);
	    			 }
    			});
    		},
        }
    return service;
}]);