var Namespace = new Object();

/**
 * 动态增加包。
 * 比如：Namespace.register("com.hotent.form");  
 * 然后：
 * var obj=new com.hotent.form.Form();
 */
Namespace.register = function(path) {
	var arr = path.split(".");
	var ns = "";
	for ( var i = 0; i < arr.length; i++) {
		if (i > 0)
			ns += ".";
		ns += arr[i];
		eval("if(typeof(" + ns + ") == 'undefined') " + ns + " = new Object();");
	}
};

var base = angular.module("base", []);

/**
 * 修改angularjs绑定显示{{}} 为 ${}方式。
 */
base.config(function($interpolateProvider) {
	  $interpolateProvider.startSymbol('${');
	  $interpolateProvider.endSymbol('}');
	});

/**
 * 表单提交，将json转成 name=abc&age=19这种格式。
 */
base.factory("$jsonToFormData",function() {
	function transformRequest( data, getHeaders ){
		var headers = getHeaders();
		headers["Content-Type"] = "application/x-www-form-urlencoded; charset=utf-8";
		if(typeof( data )=="string"){
			return data;
		}
		return $.param(data);
	}
	return( transformRequest );
})
.service('baseService', ['$http','$q','$jsonToFormData', function($http,$q,$jsonToFormData) {
    var service = {
    		/**
    		 * get请求，输入url。
    		 * 调用方法：
    		 * var def=baseService.get(url);
    		 * def.then(
    		 * 	function(data){},
    		 * 	function(){});
    		 */
    		get:function(url){
    			var deferred = $q.defer();
    			$http.get(url).success(function(data,status){
    				deferred.resolve(data);
        		})
        		.error(function(data,status){
        			deferred.reject(status);
        		});
    			return deferred.promise;
    		},
    		/**
    		 * 数据使用表单键值对的方式提交。
    		 * 在post数据时使用www-form-urlencoded方式提交。
    		 * 发送数据方式：
    		 * 1.构建键值对
    		 * 	var str="name=tony&age=19";
    		 *  postForm(user,str);
    		 * 2.构建简单json对象
    		 *  var obj={name:"tony",age:19};
    		 *  postForm(user,obj);
    		 *  
    		 *  数据处理方式：
    		 *  var obj=baseService.postForm(url,params);
    		 *  obj.then(
    		 *  	//调用成功时处理，服务器返回状态为200时。
    		 *  	function(data){
    		 *  	},
    		 *  	//调用失败时处理，服务器返回状态 为400或500.
    		 * 		function(status){
    		 *  });
    		 *  
    		 */
    		postForm:function(url,param){
    			var deferred = $q.defer();
    			$http.post(url,param,{transformRequest:$jsonToFormData})
				 .success(function(data,status){
					 deferred.resolve(data);
				 })
				 .error(function(data,status){
					 deferred.reject(status);
				 });
    			 return deferred.promise;
    		},
    		/**
    		 * 将数据直接post到指定的URL。
    		 * 服务端接收方法：
    		 * 1.复杂对象。
    		 * var  obj={name:"zyg",age:"19",friends:[{name:"laoli",sex:"male"},{name:"tony",sex:"male"}]};
    		 * 使用：
    		 * 	public void demo1(HttpServletRequest request, HttpServletResponse response,@RequestBody User user) throws IOException 
    		 * 2.直接读取流的方式。
    		 * 	String str=FileUtil.inputStream2String(request.getInputStream());
			 *	User user= JSONObject.parseObject(str, User.class);
    		 */
    		post:function(url,param){
    			var deferred = $q.defer();
    			$http.post(url,param)
				 .success(function(data,status){
					 deferred.resolve(data);
				 })
				 .error(function(data,status){
					 deferred.reject(status);
				 });
    			 return deferred.promise;
    		}
        }
    return service;
}])
/**
 * 数组操作。
 */
.factory("commonService",function() {
		return {
			/**
			 * 判断数组中是否包含指定的值。
			 * 判定aryChecked数组中是否val。
			 */
	 		isChecked:function(val,aryChecked){
	 			for(var i=0;i<aryChecked.length;i++){
	 				if(val==aryChecked[i])	return true;
	 			}
	    		return false;
	    	},

	    	/**
	    	 * 数组操作。
	    	 * val:当前的值。
	    	 * checked:当前的值是否选中。
	    	 * aryChecked:选中的数据。
	    	 */
	    	operatorAry:function(val,checked,aryChecked){
	    		//判断指定的值在数组中存在。
	    		var isExist=this.isChecked(val,aryChecked);
	    		//如果当前数据选中，并且不存在，那么在数组中添加这个值。
	    		if(checked && !isExist){
	    			aryChecked.push(val);
	    		}
	    		//如果当前值没有选中，并且这个值在数组中存在，那么删除此值。
	    		else if(!checked && isExist){
					for(var i=0;i<aryChecked.length;i++){
						val==aryChecked[i] && aryChecked.splice(i,1);
		 			}
	    		}
	    	},
	    	/**
	    	 * 根据指定的值在json数组中查找对应的json对象。
	    	 * val ：指定的值。
	    	 * aryJson ：数据如下 
	    	 * [{val:1,name:""},{val:2,name:""}]
	    	 */
	    	getJson:function (val,aryJson){
	    		for(var i=0;i<aryJson.length;i++){
	    			var obj=aryJson[i];
	    			if(obj.val==val){
	    				return obj;
	    			}
	    		}
	    		return null;
	    	}
 		}
})
/**
 * 绑定angularjs模版html指令。
 * 这个指定用于将angularjs字符串模版，绑定到dom中，方便数据绑定。
 */
.directive('htBindHtml', function($compile) {
	return {
		restrict : 'A',
		link : function(scope, elm, attrs) {
			scope.unWatch = scope.$watch(attrs.htBindHtml, function(newVal, oldVal) {

                if (!elm.data('unbindWatchTag')) {
                    if(newVal){
                        elm.data('unbindWatchTag',true);
                        elm.html(newVal);
						scope.htmlFn&&scope.htmlFn.call();
                        $compile(elm)(scope);

                    }
                    else{
                        //避免重复添加监视
                        elm.data('unbindWatchTag')&&scope.unWatch();
                    }
                }
            });
		}
	};
})
/**
 * ht-select-ajax  动态加载select的options数据  
 *  列如： <select ng-model="form.typeId" ng-options="(m.id) as m.text for m in formTypeList"
 *			  ht-select-ajax="{url:'${ctx}/platform/system/sysType/getByGroupKey.ht?groupKey=FORM_TYPE',field:'formTypeList'}">
 *		 		 <option value="">请选择</option>
 *		 </select>
 *	传入参数 
 *		url ： 请求地址 
 *		field ： formTypeList 对应于 ng-options 中的 formTypeList （两者必须相同）
 */
.directive('htSelectAjax',['baseService', function(baseService){
	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
			var option=attrs["htSelectAjax"];
			option=eval("("+option+")");
			if(scope.$root.$$childHead[option.field]) return;
			var url=__ctx +option.url;
			var def=baseService.get(url);
			def.then(function(data){
				if(option.dataRoot){
					data = data[option.dataRoot];
				}
				scope[option.field] = data;
				scope.$root.$$childHead[option.field] = scope[option.field];
			},
			function(status){
				
			});
		}
	};
}])


/**
 * 外部JS和angular 交互类。
 * 主要包括两个方法：
 * 1.获取当前上下文的scope。
 * 2.设置修改后的scope。
 */
var AngularUtil={};

/**
 * 获取当前Angularjs scope 。
 */
AngularUtil.getScope=function(){
	return angular.element($("[ng-controller]")[0]).scope();
}

/**
 * 保存外部js对scope的修改。
 */
AngularUtil.setData=function(scope){
	!scope.$$phase && scope.$digest();
};

/**
 * 获取当前环境中的 service
 * serviceName：指定的服务名称。
 * 这里需要注意的是，只能获取当前ng-controller注入模块中的service。
 */
AngularUtil.getService = function(serviceName){
	if(!this.$injector){
		this.$injector =angular.element($("[ng-controller]")).injector();
	}
	if(this.$injector.has(serviceName)) {
		return this.$injector.get(serviceName);
	}
	else {
		alert(serviceName+"angular环境中没有找到该service！");
	}
};


Date.prototype.format = function(format){ 
	var o = { 
	"M+" : this.getMonth()+1, //month 
	"d+" : this.getDate(), //day 
	"h+" : this.getHours(), //hour 
	"m+" : this.getMinutes(), //minute 
	"s+" : this.getSeconds(), //second 
	"q+" : Math.floor((this.getMonth()+3)/3), //quarter 
	"S" : this.getMilliseconds() //millisecond 
	}

	if(/(y+)/i.test(format)) { 
	format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	}

	for(var k in o) { 
	if(new RegExp("("+ k +")").test(format)) { 
	format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length));
	} 
	} 
	return format; 
	}