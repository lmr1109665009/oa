Handlebars.registerHelper("length",function(value){
	return value.length;
});

/**
 * 判断是否有权限。
 */
Handlebars.registerHelper("hasPermission",function(needRight,allRights,options){
	if(allRights.indexOf(needRight)>-1)
		return options.fn(this);
	else
		return options.inverse(this);
	   
});

Handlebars.registerHelper("control",function(obj,isMain){
	var dataName=isMain?'data.main.'+obj.name : 'item.'+obj.name;
	var permission=obj.permission;
	var validate = obj.validate||"";
	var fieldHtml ="";
	switch(obj.ctlType){
		case 'selector':
			fieldHtml = '<input type="text"   permission="'+permission+
			'" validate="'+validate+ '" ht-selector="{type:\'UserDialog\',display:\'fullName\', single:true,bind:{userId:\''+dataName+'ID\',fullName:\''+dataName+'\'}}" />';
			break;
		/**
		 * 复选框
		 */
		case 'checkbox':
			var json=obj.json;
			fieldHtml = '<div ht-checkboxs="'+dataName+'" validate="'+validate+ '" permission="'+permission+'" values="'+json+'"></div>';
		break;
			//单选框
		case 'radio':
			var json=obj.json;
			fieldHtml = '<div ht-radios="'+dataName+'" validate="'+validate+ '" permission="'+permission+'" values="'+json+'"></div>'
		break
		//文本框
		case 'textarea':
			fieldHtml = '<textarea ht-textarea="'+dataName+'" validate="'+validate+ '" permission="'+permission+'" ></textarea>';
		break
		case 'ht-dic':
			fieldHtml = '<div ht-dic="'+dataName+'" validate="'+validate+ '" dictype="'+obj.dictype+'" permission="'+permission+'" ></div>';
		break
		//下拉框
		case 'select':
			var json=obj.json;
			fieldHtml = '<select multiple validate="'+validate+ '" ht-select="'+dataName+'" permission="'+permission+'" options="'+json+'" data-am-selected="{btnStyle:\'success\'}"></select>';
		break
		case 'date':
			var json=obj.json;
			fieldHtml = '<input ht-date="'+dataName+'" validate="'+validate+ '" permission="'+permission+'" options="'+json+'" />';
		break
		default:
			fieldHtml = '<input ht-input="'+dataName+'" validate="'+validate+ '" placeholder="'+obj.comment+'" permission="'+permission+'" />';
	}
	fieldHtml = "<div ng-model='"+dataName+"' ht-validate='"+validate+"'>"+fieldHtml+"</div>"
	return fieldHtml;
});

var base = angular.module("base", []);
base.factory('dataService',['$http','$q',function($http,$q){
	return {
		doRequest:function(){
			var deferred = $q.defer();
			$http.get('../data/users.json')
				 .success(function(data){
					 deferred.resolve(data);
				 })
				 .error(function(){
					 deferred.reject();
				 });
			return deferred.promise;
		},
		getHtml:function(callBack){
			$http.get('formTemplate.html')
			 .success(function(data){
				 callBack(data)
			 })
			 .error(function(){
			 });
		}
	};
}])
.factory("commonService",function() {
		return {
			/**
			 * 判断数组中是否包含指定的值。
			 */
	 		isChecked:function(val,aryChecked){
	 			for(var i=0;i<aryChecked.length;i++){
	 				if(val==aryChecked[i])	return true;
	 			}
	    		return false;
	    	},
	    	
	    	/**
	    	 * 数组操作。
	    	 */
	    	operatorAry:function(val,checked,aryChecked){
	    		var isExist=this.isChecked(val,aryChecked);
	    		if(checked && !isExist){
	    			aryChecked.push(val);
	    		}
	    		else if(!checked && isExist){
					for(var i=0;i<aryChecked.length;i++){
						val==aryChecked[i] && aryChecked.splice(i,1);
		 			}
	    		}
	    	},
	    	/**
	    	 * 根据指定的值在json数组中查找对应的json对象。
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
 *ht-bind-html 
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
 * 功能说明：
 * 选择器
 * <input type="text"   permission="'+permission+
			'" ht-selector="{type:\'UserDialog\',display:\'fullName\', single:true,
			bind:{userId:\''+dataName+'ID\',fullName:\''+dataName+'\'}}" />
 */
.directive('htSelector', function() {
    return {
    	restrict : 'AE',
    	templateUrl:'selector.html',
    	scope: {
    		htSelector:'='
    	},
    	replace:true,
        link: function(scope, element, attrs) {
        	scope.permission=attrs.permission;
        	var selector=scope.htSelector;
        	var bind=selector.bind;
        	var display=selector.display;
        	var single = selector.single;
        	
        	scope.data={};
        	//数据展示
        	scope.render=function(){
        		for(var key in bind){
            		var val=eval("(scope.$parent." + bind[key] +")");
            		var aryVal=[];
            		if(val){
            			aryVal=val.split(","); 
            		}
            		//数据存放local变量data中。?
            		scope.data[key]=aryVal;
            		if(display==key){
            			scope.names=aryVal;
            		}
            	}
        	}
        	/**展示对话框*/
        	scope.showDialog = function(){
        		var initData=[];
        		for(var i=0;i<scope.data[display].length;i++){
        			var obj={};
        			for(var key in bind){
        				obj[key]=scope.data[key][i];
        			}
        			initData.push(obj);
        		}
        		//打开对话框，scope.dialogOk则为处理同意数据
        		var conf ={single:single,callBack:scope.dialogOk,initData:initData}; 
        		var dialog = eval('new '+selector.type+'(conf)');
        	}
        	
        	//删除数据信息
        	scope.remove = function(index){
				for(var key in scope.data){
					var ary=scope.data[key];
					ary.splice(index,1);
				}
				scope.updScope();
			}
        	//更新值到父容器
        	scope.updScope =function(){
        		for(var key in bind){
    				var dataStr = scope.data[key]?scope.data[key].join(","):"";//以逗号分隔数据
        			var tmp='scope.$parent.'+bind[key]+'="'+dataStr +'"';
        			eval(tmp);
        		}
        		scope.$parent.$$phase&&scope.$parent.$digest();
        	}
        	
        	//选择完成
        	scope.dialogOk=function(returnData){
        		//将选择的值更新到data{[],[],}中
        		if(!returnData)returnData=[];
        		scope.data = {};
        		for(var i=0,object;object=returnData[i++];){
        			for(var key in bind){
            			if(!scope.data[key])scope.data[key]=[];
            			scope.data[key].push(object[key])
            		}
        		}
        		scope.updScope();
        	}
        	scope.render();	
        }
    }
})
/*
 * 功能输入框指令。
 * 使用方法:
 * 	<input ht-input="data.main.name" permission='w' />
 * 	ht-input对应的属性值为scope对应的数据值路径。
 * 	permission:
 * 		取值有两种：
 * 			r:只读
 *  		w:可输入
 */
.directive('htInput', function() {
	return {
    	restrict : 'AE',
    	scope: {
    		htInput:"=",
    		placeholder:'@'
    	},
    	replace:true,
    	template:'<span>'
					+'<span ng-show="permission==\'w\'" >'
					+'<input ng-model="htInput" placeholder="{{placeholder}}" class="am-form-field"/>'
					+'</span>'
					+'<span ng-show="permission==\'r\'" ng-bind="htInput"></span>'
				 +'</span>',
        link: function(scope, element, attrs) {
        	scope.permission = attrs.permission;
        }
}})
/*
 * 多行文本框指令。
 * 使用方法:
 * 	<textarea ht-textarea="data.main.name" permission='w' />
 * 	ht-textarea 对应的属性值为scope对应的数据值路径。
 * 	permission:
 * 		取值有两种：
 * 			r:只读
 *  		w:可输入
 */
.directive('htTextarea', function() {
	return {
    	restrict : 'AE',
    	scope: {
    		htTextarea:"=",
    		placeholder:'@'
    	},
    	replace:true,
    	template:'<span>'
					+'<span ng-show="permission==\'w\'" >'
					+'<textarea type="text" ng-model="htTextarea" placeholder="{{placeholder}}" ></textarea>'
					+'</span>'
					+'<div ng-show="permission==\'r\'" ng-bind="htTextarea"></div>'
				 +'</span>',
        link: function(scope, element, attrs) {
        	scope.permission = attrs.permission;
        }
}})
/**
 * 表单使用checkbox：
 * 
 * 属性说明：
 * ht-checkboxs:对应scope中的数据。
 * permission：权限 r,w
 * values:选项数据为一个json
 * 使用示例:
 * <div ht-checkboxs="data.users" permission="w" values="[{val:1,text:'红'},{val:2,text:'绿'},{val:3,text:'蓝'}]"></div>
 */
.directive('htCheckboxs',['commonService', function(commonService) {
    return {
    	restrict : 'A',
    	scope:{
    		htCheckboxs:"="
    	},
    	compile: function(element, attrs) {
        	var permission=attrs.permission;
        	var valJson=eval(attrs.values);
        	var jqEl=$(element);
        	if(permission=="w"){
        		for(var i=0;i<valJson.length;i++){
        			var jsonObj=valJson[i];
        			var id=attrs.htCheckboxs +"_" +i;
        			jqEl.append('<input type="checkbox" id="'+id+'" ht-checkbox="'+attrs.htCheckboxs+'"  value="'+jsonObj.val+'" /><label for="'+id+'">' + jsonObj.text +'</label>');
        		}
        	}
        	return link;
        	
        	function link(scope, element, attrs){
        		var permission=attrs.permission;
        		if(permission=='w') return;
        		
        		var valJson=eval(attrs.values);
        		var values=scope.htCheckboxs;
        		var aryValues=(typeof values == "string")?values.split(","):values;
        		
        		var aryDisplay=[];
        		for(var i=0;i<aryValues.length;i++){
        			var json=commonService.getJson(aryValues[i],valJson);
        			if(json==null) continue;
        			aryDisplay.push(json.text);
        		}
        		$(element).text(aryDisplay.join(","))
        	}
        }
    }
}])
/**
 * 功能说明:
 * htCheckbox 指令用于收集checkbox数据。
 * 在页面中使用 
 * 	属性指令：ht-checkbox
 * 	对应的值为scope对应的数据data.users。
 * 示例:
  	<div >
        <input type="checkbox" ht-checkbox="data.users"  value="1" />红
		<input type="checkbox" ht-checkbox="data.users"   value="2" />绿
		<input type="checkbox" ht-checkbox="data.users"   value="3" />蓝
		<span>{{data.users}}</span>
   </div>
   <script>
       var app=angular.module("app",["directive"]);
		app.controller('ctrl', ['$scope',function($scope){
			$scope.data={users:"1,2"};
			$scope.getData=function(){
				console.info($scope.data.users)
			}
		}])
    </script>
 */
.directive('htCheckbox',['commonService',  function(commonService) {
	
	//checkbox 发生变化时处理
	handChange__=function(event){
		var scope=event.data.scope;
		var aryChecked=event.data.aryChecked;
		var isArray=event.data.isArray;
		commonService.operatorAry(this.value,this.checked,aryChecked);
		scope.htCheckbox=isArray?aryChecked:aryChecked.join(",");
		!scope.$parent.$$phase&&scope.$parent.$digest();
	}
	
    return {
    	restrict : 'A',
    	scope: {
    		htCheckbox: '='
    	},
        link: function(scope, element, attrs) {
        	var parentObj=element.parent() ;
        	var name=attrs.htCheckbox;
        	//碰到在父域具有相同名称的checkbox只处理一次。
        	var flag=parentObj.data("chk_" + name );
        	var aryChecked=[];
        	var isArray=true;
        	if(flag) return;
        	
    		parentObj.data("chk_" +name,1);
    		var val=scope.htCheckbox;
    		if(typeof val == "string"){
    			aryChecked=val.split(",");
    			isArray=false;
    		}
    		var chkObjs=$("[ht-checkbox='"+name+"']",parentObj );
    		//遍历相同名称的checkbox，初始化选中和绑定change事件。
    		for(var i=0;i<chkObjs.length;i++){
    			var obj=$(chkObjs[i]);
    			commonService.isChecked(obj.val(),aryChecked) && obj.attr("checked",true);
    			obj.bind("change",{scope:scope,isArray:isArray,aryChecked:aryChecked} ,handChange__)
    		}
        }
    }
}])
/**
 * 下拉选择框。
 * 属性说明：
 * 	ht-select：指令 属性值为scope的数据路径。
 *  permission：权限，值为r,w
 *  options:可选值
 * <select   ht-select="data.main.hobbys" permission="w" options="[{val:1,text:'篮球'},{val:2,text:'乒乓球'},{val:3,text:'足球'}]" ></select>
 */
.directive('htSelect',['commonService', function(commonService) {
	 return {
		restrict : 'AE',
		scope:{
			htSelect:"="
		},
		/**
		 * 重写编译方法。
		 */
		compile: function( element, attrs){
			var permission = attrs.permission;
			var jqEl=$(element);
			//只读增加一个span标签
			if(permission=='r'){
				var obj=$("<div name='"+attrs.htSelect+"'></div>");
				jqEl.after(obj);
			}
			//可读现在下拉框中
			else{
				var options=eval(attrs.options);
	    		jqEl.append("<option value=''>请选择</option>");	
	    		for(var i=0;i<options.length;i++){
	    			var obj=options[i];
	    			jqEl.append("<option value='"+obj.val+"'>"+obj.text+"</option>");		
	    		}
			}
			return link;
			//返回link函数。
			function link(scope, element, attrs){
				var permission = attrs.permission;
				var aryOptions=eval(attrs.options);
	    		if(permission=='r'){
	    			var jqEl=$(element);
	    			var parent=jqEl.parent();
	    			var jqSpan=$('[name="'+attrs.htSelect+'"]', parent);
	    			var val=scope.htSelect;
	    			var aryVal=(typeof val =="string") ?val.split(",") : val;
	    			var aryDesc=[];
	    			for(var i=0;i<aryVal.length;i++){
	    				var json=commonService.getJson(aryVal[i],aryOptions);
	    				json!=null  && aryDesc.push(json.text);
	    			}
	    			jqSpan.text(aryDesc.join(","));
	    			jqEl.remove();
	    		}
				else{
					var jqEl=$(element);
					//设置值
					var val=scope.htSelect;
    				if(typeof val =="string"){
    					jqEl.val(val.split(","));
    				}
    				else{
    					jqEl.val(val);
    				}
    				
    				jqEl.bind("change",function(){
    					var str='scope.$parent.'+attrs.htSelect+'="' + $(this).val() +'"';
	    				eval(str);
    					!scope.$parent.$$phase&&scope.$parent.$digest();
    				})
				}
			}
		}
			       
}}])
/**
 * 功能说明：
 *  单选按钮指令
 * 	ht-radios：指令名称，值为数据路径
 *  permission：权限 w,可写,r只读。
 *  values：单选框对应的值
 * <div ht-radios="data.color" permission="w" values="[{val:1,text:'红'},{val:2,text:'绿'},{val:3,text:'蓝'}]"></div>
 */
.directive('htRadios',['commonService', function(commonService) {
    return {
    	restrict : 'A',
    	scope:{
    		htRadios:'='
    	},
    	//编译阶段
        compile: function(element, attrs) {
        	var permission=attrs.permission;
        	var valJson=eval(attrs.values);
			var jqEl=$(element);
			if(permission=="w"){
				for(var i=0;i<valJson.length;i++){
	        		var jsonObj=valJson[i];
	        		var id=attrs.htRadios +"_" + i;
	        		jqEl.append('<input type="radio" id="'+id+'" name="'+attrs.htRadios+'" ng-model="'+attrs.htRadios+'"  value="'+jsonObj.val+'" /><label for="'+id+'">' + jsonObj.text + "</label>");
	        	}
			}
			return link;
			//link函数
        	function link(scope,element,attrs){
        		var permission=attrs.permission;
        		if(permission=="w"){
        			scope[attrs.htRadios]= scope.htRadios;
        		}
        		else{
        			var val=scope.htRadios;
        			var valJson=eval(attrs.values);
        			var json=commonService.getJson(val,valJson)
        			jqEl.text(json.text);
        		}
        	}
        	
        }
    }
}])
/**
 * 日期控件
 * 使用方法：
 * <input ht-date="data.main.date" permission="w" options="{format:'yyyy-mm-dd'}" />
 * ht-date ：指令名称 属性值为数据路径
 * permission：表示数据权限
 * options:选项，目前只有日期格式
 */
.directive('htDate',['$compile', function($compile) {
    return {
    	restrict : 'A',
    
    	link:function(scope,element,attrs){
    		var name=attrs.htDate;
    		var permission=attrs.permission;
    		var options=eval("(" +attrs.options +")");
    		var jqEl=$(element);
    		var jqParent=jqEl.parent();
    		var format=options.format;
    		
    		format=format || 'yyyy-mm-dd';
    		
    		var str="";
    		
    		jqEl.remove();
    		if(permission=="w"){
    			str='<div class="am-input-group am-datepicker-date" data-am-datepicker="{format: \''+format+'\',theme:\'success\'}">'+
	    		  		'<input   type="text"  class="am-form-field"  placeholder="日历组件"  ng-model="'+name+'"  >'+
	    		  			'<span class="am-input-group-btn am-datepicker-add-on">'+
	    		  				'<button class="am-btn am-btn-default" type="button"><span class="am-icon-calendar"></span> </button>'+
	    		  			'</span>'+
	    		  	'</div>';
    		}
    		else{
    			str='<div  ng-bind="'+name+'"></div>';
    		}
    		jqParent.append(str);
    		$compile(jqParent)(scope);
    		
    		if(permission=="w"){
	    		var inputObj= $("input[type='text']",jqParent);
	    		$('div.am-datepicker-date',jqParent).datepicker().on('changeDate.datepicker.amui', function(event) {
	    	    		eval("scope." +name +"='"+inputObj.val()+"'");
	    	    		!scope.$$phase&&scope.$digest();
	    	    });
    		}
    	}
    }
}])
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
.directive('htSelectAjax', function($injector){
	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
			//var BaseService = $injector.get("baseService");
			var option=attrs["htSelectAjax"];
			option=eval("("+option+")");
			if(scope.$root.$$childHead[option.field]) return;
			$.post('/bpmx'+option.url,function(data){
				if(option.dataRoot){
					data = data[option.dataRoot];
				}
				scope[option.field] = data;
				scope.$root.$$childHead[option.field] = scope[option.field];
			});
		}
	};
})

.directive('htDic', function($injector){
	return {
		restrict: 'A',
		scope:{
			htDic:'='
    	},
		replace:true,
		templateUrl:'htDic.html',
		link: function(scope, element, attrs) {
			//scope.validate = attrs.validate;
			scope.permission =attrs.permission||"w";
			var dictype =attrs["dictype"]; //数据字典的类型
			var url=attrs["url"]|| __ctx + "/platform/system/dictionary/getByNodeKey.ht?nodeKey="+dictype;
			var keyName =attrs["keyName"]||"itemValue";
			var valName=attrs["valName"]||"itemName";
			
			//var arr =attrs["htDic"].split(".");
			//var treeId = "dicTree_"+arr[arr.length-1];
			scope.treeId = parseInt(Math.random()*1000)
			scope.dicData={};
        	scope.treeClick = function(event,treeId,treeNode){
        		scope.dicData.key =treeNode[keyName];
        		scope.dicData.value =treeNode[valName];
        		scope.htDic =scope.dicData.key;
        		
        		$('#dropBody'+treeId).dropdown('close');
        		scope.$digest();
        	}
        	
        	scope.loadOrgTree = function(){
        		var orgTree = new ZtreeCreator(scope.treeId,url)
        		.setDataKey({idKey:"dicId",name:"itemName"})
 				.setCallback({onClick:scope.treeClick})
 				.setOutLookStyle()
 				.initZtree({},1,function(treeObj,treeId){
 					$('#dropBody'+treeId).dropdown({justify: '#dropDown'+treeId});
 					
 					// 通过key 回显Value
 					if(scope.htDic){
 						//获取key 的那个Value
 						var node = treeObj.getNodesByFilter( function(node){ if(node[keyName]==scope.htDic) return true; else return false; },true);
 						if(node){
 							scope.dicData.key =node[keyName];
 			        		scope.dicData.value =node[valName];
 			        		scope.$digest();
						}
 					}
				});
        	}
        	scope.loadOrgTree();
		}
	};
})
/**
 * 校验入口
 * **/
.directive('htValidate', [function () {
      return {
          require: "ngModel",
          link: function (scope, element, attr, ngModel) {
        	  var validate = attr.htValidate;
              var customValidator = function (value) {
            	  if(!validate) return true;
                  var validity =  $.fn.validRules(value,validate,element);
                  ngModel.$setValidity("customValidate", validity);
                  return validity ? value : undefined;
              };
              ngModel.$formatters.push(customValidator);
              ngModel.$parsers.push(customValidator);
          }
      };
  }])


;
var  app = angular.module("app", ['base']);
app.controller('ctrl', ['$scope','dataService',function($scope,dataService){
	$scope.test1='';   $scope.test2='';   $scope.test3='';   $scope.test4='';   $scope.test5='';   $scope.test6='';   $scope.test7='';
	//默认行对象。
	var subTableRows={};
	$scope.test={};
	$scope.test.dic={name:"小明",key:"xiaoming"};
	
	/**
	 * 添加子表行
	 */
	$scope.addRow=function(tableName){
		var row=subTableRows[tableName];
		if(!row){
			var subTables= $scope.metaData.sub;
			for(var i=0;i<subTables.length;i++){
				var table=subTables[i];
				if(table.tableName==tableName){
					subTableRows[tableName]=table.row;
				}
			}
		}
		var obj=$.extend( {}, subTableRows[tableName] ) ;
		$scope.data.sub[tableName].push(obj);
		return false;
	};
	
	//处理必填
	$scope.addRequireRow=function(){
		var subTables= $scope.metaData.sub;
		for(var i=0;i<subTables.length;i++){
			var table=subTables[i];
			var tableName=table.tableName;
			var permission=table.persmssion;
			if(permission.indexOf("require")==-1) return ;
			if($scope.data.sub[tableName].length>0) return;
			//添加必要的行
			var obj=$.extend( {}, table.row ) ;
			$scope.data.sub[tableName].push(obj);
		}
	}
	
	/**
	 * 删除行
	 */
	$scope.removeRow=function(tableName,idx){
		$scope.data.sub[tableName].splice(idx,1);
	}
	
	
	/**
	 * 模版渲染
	 */
	dataService.doRequest().then(function(data){
		var formData=data;
		$scope.data=formData.data;
		$scope.metaData=formData.form;
		//添加行
		$scope.addRequireRow();
		
		//获取模版数据
		dataService.getHtml(function(data){
			//编译模版
			var compiler = Handlebars.compile(data);
			var out=compiler(formData);
			$scope.formHtml=out;
			
		});
	});
}]);

