var u = navigator.userAgent;
var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
if (isAndroid) {
    $LAB
        .script("./../assets/js/cordova/android/cordova.js");
} else if (isiOS) {
    $LAB
        .script("./../assets/js/cordova/ios/cordova.js");
}
//与app通信函数
function dispatchWithApp(arg, callback) {
    cordova.exec((function (data) {
            callback(data);
        }), (function (err) {
            console.log(err);
        }),
        'UCPCommandPlugin', 'sendMessage', ['', '', arg]
    );
}
//与app通信函数下载//如果为ios端，下载时进入浏览器
function downloadApp(href) {
    cordova.exec((function (data) {}), (function (err) {
            console.log(err);
        }),"OpenUrlPlugin", "sendUrl", ['', '', href]
    );
}
var formDirective = angular.module("formDirective", ["base","formServiceModule"]);
/**
 * 功能说明：
 * 选择器
 * <input type="text"   permission="'+permission+
			'" ht-selector="main.name"   selectorconfig="{type:\'UserDialog\',display:\'fullName\', single:true,
			bind:{userId:\''+dataName+'id\',fullName:\''+dataName+'\'}}" />
 permission：w 可选择，r 显示选择的数据。
 ht-selector : scope 数据表达式。
 	主表：data.main.name
 	子表：item.name
 
 selectorconfig:对话框配置
 single:true 或 false 是否单选
 type：对话框类型
 display ：对话框显示的字段。
 bind：key 为对话框返回字段
 	  val 为scope数据表达式
 */
formDirective.directive('htSelector',['$rootScope', function($rootScope) {
    return {
    	restrict : 'AE',
    	templateUrl:__ctx +'/weixin/bpm/selector.html',
    	scope: {
    		htSelector:'='
    	},
        link: function($scope, element, attrs) {
        	element.removeClass("am-form-field");
        	$scope.permission=attrs.permission;
        	//绑定配置
        	var selector=eval("("+ attrs.selectorconfig+")");
        	//绑定配置
        	var bind=selector.bind;
        	//显示字段。
        	var display=selector.display;
        	//是否单选
        	
        	$scope.data={};
        	
        	//将控件数据初始化到指定本地域中。
        	$scope.initData=function(){
        		for(var key in bind){
            		var val=eval("($scope.$parent." + bind[key] +")");
            		var aryVal=[];
            		if(val){
            			aryVal=val.split(","); 
            		}
            		//数据存放local变量data中。
            		$scope.data[key]=aryVal;
        		}
        	}
        	//对值进行双向绑定
        	$scope.$watch('htSelector',  function(newValue, oldValue) {
        		if(newValue!=oldValue){
        			$scope.initData();
        			$scope.render();
        		}
        	});
        	
        	//数据展示
        	$scope.render=function(){
        		$scope.names=$scope.data[display];
        	}
        	/**展示对话框*/
        	$scope.showDialog = function(){
        		var initData=[];
        		for(var i=0;i<$scope.data[display].length;i++){
        			var obj={};
        			for(var key in bind){
        				obj[key]=$scope.data[key][i];
        			}
        			initData.push(obj);
        		}
        		//打开对话框，scope.dialogOk则为处理同意数据
        		var conf = $.extend(selector,{callBack:$scope.dialogOk,initData:initData}); 
        		var dialog = eval(selector.type+'(conf)');
        	}
        	//删除数据信息
        	$scope.remove = function(index){
				for(var key in $scope.data){
					var ary=$scope.data[key];
					ary.splice(index,1);
				}
				$scope.updScope();
			}// 选中数据点击事件
        	$scope.selectedOnClick = function(index){
        		for(var key in bind){
        			if(key !=selector.display){
        				var id=$scope.data[key][index];
        			}
        		}
        		//不同对话框选择的数据进行展示个体弹框，这个将来可以拓展到 各自对话框的自身来实现
        		if(selector.type =='UploadDialog'){
        			window.location.href=__ctx+"/platform/system/sysFile/download.ht?fileId="+id;
        		}
			}
        	
        	//更新值到父容器
        	$scope.updScope =function(){
        		for(var key in bind){
    				var dataStr = $scope.data[key]?$scope.data[key].join(","):"";//以逗号分隔数据
        			var tmp='$scope.$parent.'+bind[key]+'="'+dataStr +'"';
        			eval(tmp);
        		}
        		$scope.render();
        		!$rootScope.$$phase && $rootScope.$digest();
        	}
        	
        	//选择完成
        	$scope.dialogOk=function(returnData){
        		//将选择的值更新到data{[],[],}中
        		//先初始化防止报空
        		if(!returnData)returnData=[];
        		$scope.data = {};
        		for(var key in bind) $scope.data[key]=[];
        		
        		for(var i=0,object;object=returnData[i++];){
        			for(var key in bind){
            			$scope.data[key].push(object[key])
            		}
        		}
        		$scope.updScope();
        	}
        	//初始化数据
        	$scope.initData();
        	//显示数据。
        	$scope.render();	
        }
    }
}])
/*
 * 功能输入框指令。
 * 使用方法:
 * <div >
 * 	<input ht-input="data.main.name" permission='w' />
 * </div>
 * 	ht-input对应的属性值为scope对应的数据值路径。
 * 	permission:
 * 		取值有两种：
 * 			r:只读
 *  		w:可输入
 */
.directive('htInput', function() {
	return {
    	restrict : 'AE',
    	template : "",
    	compile: function(element, attrs){
    		var permission = attrs.permission;

            //console.log(attrs)
    		if(permission=='r')	{

    			element.hide();
    			element.after("${"+attrs.htInput+"}");
    		}
    		if(permission=='n'){
    			element.parent().parent().hide();
    		}
    		
    		//$("#formHtml input[ht-input]").wrap('<div class="inputPostR"></div>');
    		element.on('focus', function () {
    			  var target = this;
    			  setTimeout(function(){
    			        target.scrollIntoView();
    			        //console.log('htInput-scrollIntoViewIfNeeded');
    			      },400);
    			});
    		
    		return link;
    		function link(scope, element, attrs,ctrl){
    			
    		}
    	}
}})
/*
 * 上传指令。
 * 使用方法:
 * 	<input ht-upload="data.main.name" permission='w' />
 * 	ht-input对应的属性值为scope对应的数据值路径。
 * 	permission:
 * 		取值有两种：
 * 			r:只读
 *  		w:可输入
 */
.directive('htUpload',["$rootScope",function($rootScope) {
	return {
    	restrict : 'A',
    	scope: {
    		htUpload:"="
    	},
    	template : '<div class="am-u-sm-12 am-u-md-12" ng-if="permission==\'w\' || permission==\'b\'">\
						<span class="am-u-sm-6 m-u-md-4" ng-repeat="file in files"> 	\
							<button class="am-btn am-btn-secondary am-btn-uploadCtrl" ng-click="onClick(file.id)">${file.name}</button> \
							<a class="btn btn-xs  fa-remove"  title="移除该项" ng-click="remove($index)"></a> 	\
    					</span>				\
    					<a class="am-btn am-btn-default am-radius fa-upload" ng-click="showDialog()"></a>			\
    				</div>\
    				<div ng-if="permission==\'r\'" class="am-u-sm-12 am-u-md-12" >\
						<span ng-repeat="file in files" class="am-u-sm-6 m-u-md-4" >\
							<button  class="am-btn am-btn-secondary am-btn-uploadCtrl" ng-click="onClick(file.id)">${file.name}</button>\
						</span>\
    				</div>',
    				
		link: function (scope,element,attrs,ctrl){
			element.removeClass("am-form-field");
			scope.permission = attrs.permission;
        	var jsonStr = scope.htUpload?scope.htUpload.replace(/￥@@￥/g,"\""):"[]";
        	scope.files = eval("(" +jsonStr +")");
        	
        	scope.dialogOk = function(data){
        		scope.files = data;
        		scope.htUpload=JSON.stringify(data);
        		!$rootScope.$$phase&&$rootScope.$digest();
        	}
        	
    		scope.showDialog = function(){
    			var conf ={callBack:scope.dialogOk}; 
    			UploadDialog(conf);
    		}
    		scope.onClick = function(id){
                downloadApp(__ctx+"/platform/system/sysFile/download.ht?fileId="+id);
    			//window.location.href=__ctx+"/platform/system/sysFile/download.ht?fileId="+id;
    		}
    		
    		scope.$watch('htUpload',  function(newValue, oldValue) {
    			if(newValue==oldValue) return ;
    			
    			if(!newValue) scope.files=[];
    			else scope.files = eval("(" +newValue +")");
    			
    			!$rootScope.$$phase&&$rootScope.$digest();
        	});
    		scope.remove = function(index){
				scope.files.splice(index,1);
				//更新字段值
				if(!scope.files ||scope.files.length==0) scope.htUpload="";
				else　scope.htUpload=JSON.stringify(scope.files);
			}
			
		}
	}}])
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
    	compile: function(element, attrs){
    		var permission = attrs.permission;
    		
    		if(permission=='r' || permission=='rp')	{ 
    			element.hide();
    			element.after("${"+attrs.htTextarea+"}");
    		}
    		else if(permission=='n') { $(element).remove(); }
    		
    		element.on('focus', function () {
  			  var target = this;
  			  setTimeout(function(){
  			        target.scrollIntoView();
  			        //console.log('htTextarea-scrollIntoViewIfNeeded');
  			      },400);
  			});
    		
    		return link;
    		function link(scope, element, attrs,ctrl){
    			
    		}
    	}
}})
/*
 * 多行文本框html指令。
 * 使用方法:
 * 	<textarea ht-textarea="data.main.name" permission='w' />
 * 	ht-textarea 对应的属性值为scope对应的数据值路径。
 * 	permission:
 * 		取值有两种：
 * 			r:只读
 *  		w:可输入
 */
    .directive('htHtmledittable', function() {
        return {
            restrict : 'AE',
            scope: {
                htHtmledittable:"=",
                placeholder:'@',
            },
            compile: function(element, attrs){
               // console.log(element,attrs)
                return link;
                function link(scope, element, attrs,ctrl){
                	var content=scope.htHtmledittable;
                	var staticUrl=window.localStorage.staticUrl||"";
                	content=content.replace(/\{\{staticUrl\}\}/g,staticUrl);
                    element.after(content);
                    $(element).remove();
                }
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
        	jqEl.children().remove();
        	if(permission=="w" || permission=="b"){
        		for(var i=0;i<valJson.length;i++){
        			var jsonObj=valJson[i];
        			var id=attrs.htCheckboxs+Math.random(1000000);
        			var str='<div class="am-checkbox-inline"><input type="checkbox"  ht-checkbox="'+attrs.htCheckboxs+'"  value="'+jsonObj.val+'" id="'+id+'"/><label for="'+id+'">' + jsonObj.text +'</label></div>';
        			jqEl.append(str);
        		}
        	}
        	return link;
        	
        	function link(scope, element, attrs){
        		var permission=attrs.permission;
        		if(permission=='w' || permission=="b") return;
        		
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
.directive('htCheckbox',['commonService',"$rootScope",  function(commonService,$rootScope) {
	//checkbox 反生变化时处理
	handChange__=function(event){
		var scope=event.data.scope;
		var aryChecked=event.data.aryChecked;
		var isArray=event.data.isArray;
		commonService.operatorAry(this.value,this.checked,aryChecked);
		scope.htCheckbox=isArray?aryChecked:aryChecked.join(",");
		$rootScope.$digest();
	}
	/**
	 * 
	 */
	this.preChecked=function(){
		//点击的元素
		var obj=$(window.event.srcElement);
		var pre=obj.prev()[0];
		pre.checked=!pre.checked;
	}
	
    return {
    	restrict : 'A',
    	scope: {
    		htCheckbox: '='
    	},
        link: function(scope, element, attrs) {
        	//这里做了一次硬编码，目的是向上两级获取父容器对象。
        	var parentObj=element.parent().parent() ;
        	var name=attrs.htCheckbox;
        	//碰到在父域具有相同名称的checkbox只处理一次。
        	var flag=parentObj.data("chk_" + name );
        	if(flag) return;
        	//选中的值
        	var aryChecked=[];
        	var isArray=true;
        	        	
    		parentObj.data("chk_" +name,1);
    		var val=scope.htCheckbox;
    		if(typeof val == "string"){
    			if(val!=""){
    				aryChecked=val.split(",");
    			}
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
				jqEl.hide();
			}
			//可读现在下拉框中
			else if(attrs.options){
				console.log(attrs.options)
				var options=eval(attrs.options);console.log(options)
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
	    			scope.$watch("htSelect",function(newValue, oldValue) {
	    				var jqEl=element;
	 	    			var parent=jqEl.parent();
	 	    			var jqSpan=$('[name="'+attrs.htSelect+'"]', parent);
	 	    			var aryVal=(typeof newValue =="string") ?newValue.split(",") : newValue;
	 	    			var aryDesc=[];
	 	    			if(typeof(aryVal) != "undefined"){ //alert(newValue,aryVal)
	 	    			for(var i=0;i<aryVal.length;i++){
	 	    				if(!aryVal[i]) continue;
	 	    				var text = $("option[value='"+aryVal[i]+"']",element).text();
	 	    				text && aryDesc.push(text);
	 	    			}
	 	    			jqSpan.text(aryDesc.join(","));
	 	    			}

	    			 });
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
			jqEl.html("");
			if(permission=="w" || permission=="b"){
				//子表情况
				for(var i=0;i<valJson.length;i++){
	        		var jsonObj=valJson[i];
	        		var id=attrs.htRadios +"_" + i;
	        		jqEl.append('<div class="am-radio-inline"><input type="radio" id="'+id+'" name="" ng-model="'+attrs.htRadios+'"  value="'+jsonObj.val+'" /><label for="'+id+'">' + jsonObj.text + "</label></div>");
//	        		if(i==0){
//	        			jqEl.append('<input type="radio" id="'+id+'" name="" ng-checked="true" ng-model="'+attrs.htRadios+'"  value="'+jsonObj.val+'" /><label for="'+id+'">' + jsonObj.text + "</label>");}
//	        		else{jqEl.append('<input type="radio" id="'+id+'" name="" ng-model="'+attrs.htRadios+'"  value="'+jsonObj.val+'" /><label for="'+id+'">' + jsonObj.text + "</label>");}
				}
			}
   		 //$("input:radio").prop("checked") ;
			return link;
			//link函数
        	function link(scope,element,attrs){
        		//子表radio bug，为什么不在编译器处理呢？因为啊，forEach 会拿编译好的的模板直接执行link函数，不再进行编译。
        		$("input",element).attr("name", Math.random(1000000));
        		var permission=attrs.permission;
        		if(permission=="w" || permission=="b"){
        			scope[attrs.htRadios]= scope.htRadios;
        		}
        		else{
        			var val=scope.htRadios;
        			if(val){
        				var valJson=eval(attrs.values);
        				var json=commonService.getJson(val,valJson)
        				$(element).text(json.text);
        			}
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
    		var options=eval("(" +attrs.options +")");
    		var name=attrs.htDate;
    		var permission=attrs.permission;
    		var jqEl=$(element);
    		var jqParent=jqEl.parent();
    		var format='yyyy-mm-dd';
    		var preset = "date";
    		if(options){
    			format=options.format;
    			switch(format){
    				case 'yyyy-MM-dd HH:mm:ss':
    					preset = 'datetime';
    					break;
    				case 'yyyy-MM-dd HH:mm:00':
    					preset = 'datetimeZeroSecond';
    					break;
    				case 'HH:mm:ss':
    					preset = 'time';
    					break;
					default:preset = 'date';
    			}
    		}
    		var dateConfig = DTPickerOpt[preset];
    		var str="";
    		jqEl.remove();
    		if(permission=="w" || permission=="b"){
    			var icon =preset== 'time' ?"am-icon-clock-o":"am-icon-calendar"; 
    			str='<div class="am-form-icon am-form-feedback am-form-date-icon">\
	    		     	<input id="" class="am-form-date-icon" type="text"  ng-model="'+name+'" ht-validate="'+(attrs.htValidate?attrs.htValidate:"{}")+'"  permission="'+permission+'">	\
\
	    		  	 </div>'; 
    		}
    		else{
    			str='<div  ng-bind="'+name+'"></div>';
    		}
    		jqParent.append(str);
    		$compile(jqParent)(scope);
    		
    		if(permission=="w" || permission=="b"){
	    		var inputObj= $("input[type='text']",jqParent);
	    		dateConfig.onSelect = function (valueText, inst) { };
	    		eval("inputObj.mobiscroll(dateConfig)."+preset+"(dateConfig)");
    		}
    	}
    }
}])
/**
 * 数据字典指令。
 * dictype：数据字典别名。
 */
.directive('htDic', function($injector){
	return {
		restrict: 'A',
		scope:{
			htDic:'='
    	},
		templateUrl:__ctx +'/weixin/bpm/htDic.html',
		link: function(scope, element, attrs) { 
			element.removeClass("am-form-field");
			//scope.validate = attrs.validate;
			scope.permission =attrs.permission;
			var dictype =attrs["dictype"]; //数据字典的类型
			var url=attrs["url"]|| __ctx +"/platform/system/dictionary/getByNodeKey.ht?nodeKey="+dictype;
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
        		!scope.$parent.$$phase&&scope.$parent.$digest();

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
 			        		!scope.$parent.$$phase&&scope.$parent.$digest();

						}
 					}
				});
        	}
        	scope.loadOrgTree();
		}
	};
})
/**
 * 校验入口.
 * 校验指令，指定必须有ngModel属性。
 */
.directive('htValidate', [function () {
      return {
          require: "ngModel",
          link: function (scope, element, attr, ctrl) {
        	  var validate = attr.htValidate;
        	  var permission=attr.permission;
        	  //如果不必填且没有其他校验返回
        	  if(permission!=="b" && validate=="{}")return; 
        	  try {
        		  var validateJson = eval('(' + validate + ')');
        	  } catch (e) {
        		  console.info("表单ngModel:"+attr.ngModel+"validate出现异常！请检查："+validate );
				return ;
        	  }
        	  
        	  if(permission=="b")validateJson.required = true;
        	  
              var customValidator = function (value) {
            	  if(!validate) return true;
            	  handlRangeData(validateJson);
                  var validity =  $.fn.validRules(value,validateJson,element);
                  ctrl.$setValidity("customValidate", validity);
                  return validity ? value : undefined;
              };
              
              ctrl.$formatters.push(customValidator);
              ctrl.$parsers.push(customValidator);
              
              
              //处理比较目标字段
              var handlRangeData = function(validateJson){
            	  var ranges =[];
            	  if(validateJson.dateRangeEnd)ranges.push(validateJson.dateRangeEnd);
            	  if(validateJson.dateRangeStart)ranges.push(validateJson.dateRangeStart);
            	  
            	  for (var i = 0,dateRange;dateRange =ranges[i++];) {
            		  if(dateRange.range == ""){
            			  dateRange.targetVal = eval("scope."+dateRange.target);
            		  }else{
            			  dateRange.targetVal = scope.item[dateRange.target.spilt(".")[2]];
            		  }
            	  }
              }
          }
      };
  }])
  /**
   * 格式化数字/日期/string
   * {type:number/date/string}
   * {type='number','coinValue':'￥','isShowComdify':1,'decimalValue':2}
   * {type='date',dateformat:'yyyy-mm-dd'}
   * 
   */
  .directive('htFormator', ['formService','$filter',function (formService,$filter) {
      return {
          require: "ngModel",
          link: function (scope, element, attr, ctrl) {
        	  var formater = attr.htFormator;
        	  if(!formater) return;
        	  try {
        		  var formaterJson = eval('(' + formater + ')');
        	  } catch (e) {
        		  console.info("表单ngModel:"+attr.ngModel+" formater出现异常！请检查："+formater);
				return ;
        	  }
        	  var input = element,inputCtrl=ctrl;
        	  if($(element)[0].nodeName != "INPUT")  {
        		  input = $("input",element);
        		  inputCtrl = $(input).data("$ngModelController");
        	  }
        	 // 光标移入时，将modelvalue值（非格式化状态）展示到前端
        	/* input.focus(function(){
        		 inputCtrl.$setViewValue(inputCtrl.$modelValue);
        		 inputCtrl.$render();
        	 });*/
        	 //光标移出展示格式化数据
        	 input.blur(function(a){
        		 inputCtrl.$setViewValue(formService.dataFormat(inputCtrl.$modelValue,formaterJson));
        		 inputCtrl.$render();
        	 });
        	 // view to model
        	 inputCtrl.$parsers.unshift(function(value){
            	 return $.fn.toNumber(value);
             }); 
             //  model to view
        	 /**
        	  *  <div ng-model="data.main.name">
        	  *  	<input ng-model="inputName">
        	  *  </div>
        	  *  我们指令又产生了新的input框，他们有不同的ngModelController 
        	  *  当input框键入值时候，修改当前input的modelValue,从而触发修改 div 的ModelValue值
        	  *  div modelValue会触发它的formatters 当然如果界面存在{{data.main.name}} 的时候才有意义。
        	  *  当div modelValue 发生变化，会致使input modelValue发生变化、最终调用该方法：
        	  *  input model的formatter，会让第一次初始化数据进行格式化显示在页面。
        	  * */
        	 inputCtrl.$formatters.push(function(value){
        		 return formService.dataFormat(value,formaterJson); 
              }); 
          }
      };
  }])
  
  
  /**
   * 字段函数计算
   * 日期比较、函数计算、等
   */
  .directive('htFuncexp',['formService',function(formService){
	  
	var link = function($scope, element, attrs, $ctrl) {
		var modelName = attrs.ngModel;
		var funcexp=attrs.htFuncexp,
			watchField=getWatchField(funcexp);
		if(watchField.length>0){
			for(var i=0,f;f=watchField[i++];){
				//子表字段的监控
				if(f.indexOf("data.sub")==0){
					var subMsg = f.split(".");
					var fieldName = subMsg[3];
					var subTableSrc =f.replace(fieldName,"");
					var watched = $scope.$watch(subTableSrc+"rows.length",function(newValue,oldValue,scope) {
		        		if(newValue>oldValue || newValue==1){//子表必填默认一行数据
		        			if(newValue<1) alert("出错了，^_^………");
		        			//增加子表监控
		        			formService.doMath(scope,modelName,funcexp);
		        			$scope.$watch(subTableSrc+"rows["+(newValue-1)+"]."+fieldName,function(newValue, oldValue,scope) {
				        		if(newValue!=oldValue){
				        			formService.doMath(scope,modelName,funcexp);
				        		}
				        	});
		        		}else if(newValue<oldValue){
		        			watched(); //取消监控 
		        		}
		        	});
				}
				else{
					//主表和子表单条运算
					var aa = $scope.$watch(f,function(newValue, oldValue,scope) {
	        			formService.doMath(scope,modelName,funcexp);
		        	});
				} 
				
			}
			
		}
		function getWatchField(statFun){
			var myregexp = /\(([data.main|data.sub|item].*?)\)/g;
			var match = myregexp.exec(statFun);
			var arrs=[];
			while (match != null) {
				var str =match[1];
				var has=false;
				for(var i=0,v;v=arrs[i++];){
					if(v == str)has =true;
				}
				if(!has)arrs.push(str);
				match = myregexp.exec(statFun);
			}
			return arrs;
		}
	};
	return {
		restrict : 'A',
		require : "ngModel",
		compile : function() {
			return link;
		}
	};
}])
/**
 *  计算日期间隔
 */ 
.directive('htDatecalc', ['formService','$filter',function (formService,$filter) {
      return {
          require: "ngModel",
          link: function ($scope, element, attr, ctrl) {
        	  if(!attr.htDatecalc) return;
    		  var dateCalc = eval('(' + attr.htDatecalc + ')');
    		  var startSrc =dateCalc.dateCalculateStart;
    		  var endSrc = dateCalc.dateCalculateEnd;
    		  var diffType =dateCalc.diffType;
    		  if(!dateCalc.isMain){
    			  startSrc ="item."+startSrc.split(".")[3];
    			  endSrc ="item."+endSrc.split(".")[3];
    		  }
			  $scope.$watch(startSrc,function(newValue, oldValue) {
	        	   if(newValue!=oldValue){
	        		   var endDate =eval("$scope."+endSrc);//scope.data.main.field , item.field
	        		   var int =formService.doDateCalc(newValue,endDate,diffType);
	        		   ctrl.$setViewValue(int);
	        	   }
    		   });
			  $scope.$watch(endSrc,function(newValue, oldValue) {
	        	   if(newValue!=oldValue){
	        		   var start =eval("$scope."+startSrc);//scope.data.main.field , item.field
	        		   var int = formService.doDateCalc(start,newValue,diffType);
	        		   ctrl.$setViewValue(int);
	        	   }
    		   });
    			  
          }
      };
  }])
/**
 * 富文本框指令：
 * <ht-editor config="editorConfig" ng-model="content" height="100"></ht-editor>
 * ng-model：scope 数据表达式
 * config:编辑器配置
 * height:文本框高度
 * 
 * 使用方法：
 * 
 * <body ng-controller="ctrl">
 *	<ht-editor config="editorConfig" ng-model="content1">测试</ht-editor>
 *	<script>
 *		angular.module('example',['formDirective']).controller('ctrl', function ($scope,$sce) {
 *    	$scope.content1="hello world";
 * 	});
 *  </script>
 *</body>
 */
.directive('htEditor', function () {
  return {
    restrict: 'AE',
    transclude: true,
    template: '',
    require: '?ngModel',
    scope: {
      config: '='
    },
    link: function (scope, element, attrs, ngModel) {
    	var permission=attrs.permission;
    	//permission="r";
    	if(permission=="w" || permission=="b"){
    		 	var editor = new UE.ui.Editor(scope.config || 
    		   		  {focus:true ,toolbars: [ ['source', 'undo', 'redo', 'bold', 'italic',  'removeformat', 'formatmatch',  'autotypeset', 'blockquote',
    		                                     'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist']  ],
    		                                     initialFrameHeight:attrs.height || 150
    		                                     });
    		    editor.render(element[0]);
    		    if (ngModel) {
	    		    //Model数据更新时，更新百度UEditor
	    		    ngModel.$render = function () {
	    		          try {
	    		        	editor.setContent(ngModel.$viewValue);
	    		          } catch (e) {
	    		          }
	    		     };
			        //设置内容。
			        editor.ready(function () {
						editor.setContent(ngModel.$viewValue);
			 		});
			        //百度UEditor数据更新时，更新Model
			        editor.addListener('contentChange', function () {
			          setTimeout(function () {
			            scope.$apply(function () {
			              ngModel.$setViewValue(editor.getContent());
			            })
			          }, 0);
			        });
		      }
    	}
    	else{
 		        ngModel.$render = function () {
 		        	element[0].innerHTML=ngModel.$viewValue;
 		        }
    	}
    }
  }
})
//省市级联 
.directive('selectquery',['commonService', function(commonService) {
	 return {
		restrict : 'AE',
		scope:{},
		link: function(scope, element, attrs){
			var permission =attrs.permission;
			if(permission=='n') element.remove()
			
			if(!attrs.selectquery)return;
			var selectquery = attrs.selectquery.replace(/'/g,"\"");
			var selectqueryCof =angular.fromJson(selectquery);
			
			var toQuery = function(){
				// 获取查询参数
				var querydata = {};
				for (var i = 0,query;query=selectqueryCof.query[i++];){
					if(query.trigger=='-1') continue;
					var fieldValue = "";
					
					if (query.isMain=="true") {
						fieldValue = scope.$parent.data.main[query.trigger];
					} else {
						fieldValue = scope.$parent.item[query.trigger];
					}
					if(fieldValue != "" && fieldValue!=null)
						querydata[query.condition] = fieldValue;
					else if(query.initValue!= "")
						querydata[query.condition] = query.initValue;
				}
				//查询
				var condition = {
						alias:selectqueryCof.name,
						querydata:angular.toJson(querydata),
						page : 0,
						pagesize : 0}
				
				DoQuery(condition,function(data){
					if (data.errors || data.list.length < 1) {
						return;
					}
					element.empty();
					element.append("<option value=''>请选择</option>");
					for (var i = 0,item;item=data.list[i++];){
						var datavalue = item[selectqueryCof.binding.value.toLowerCase()];
						var datakey = item[selectqueryCof.binding.key.toLowerCase()];
						element.append("<option value='"+datakey+"'>"+datavalue+"</option>");	
					}
					
				},true)
			}
			toQuery();
			if(permission=='r'){
				var value = eval("scope.$parent."+attrs.ngModel);
				var text = value?$("option[value='"+value+"']",element).text():"";
				
				 element.hide();
				 element.after(text);
			}
			
			var initWatch = function(){
				//初始化监听对象
				for (var i = 0,query;query=selectqueryCof.query[i++];){
					var field;
					if(query.trigger=='-1') continue;
					// isMain是true 就是绑定主表的字段
					if (query.isMain=="true") {
						scope.$parent.$watch("data.main."+query.trigger,function(newVal,oldVal){
							if(newVal && newVal!=oldVal)toQuery();
						})
					} else {
						scope.$parent.$watch("item."+query.trigger,function(newVal,oldVal){
							if(newVal&&newVal!=oldVal)toQuery();
						})
					}
				}
			}
			initWatch();
			
		}
}}])

formDirective.directive('htCustomerdialog',['$rootScope', function($rootScope) {
    return {
    	restrict : 'AE',
        link: function($scope, element, attrs) {
        	var permission=attrs.permission;
        	// 没有权限
        	if(permission && permission !='w' && permission != 'b'){
        		element.hide();return;
        	}
        	//是否位于子表。位于子表的填充方式为当前行覆盖
        	var isInSub = (element.parents("[type='subtable']").length >0);
			
        	var confJson=eval("("+ attrs.htCustomerdialog+")");
        	//绑定配置
        	var dialogMappingConf=confJson[0].fields;
        	var dialogAlias = confJson[0].name;
        	var isCombine = confJson[0].type =='combiDialog';
        	//对话框初始化监听
        	$(element).click(function(){
        		$scope.showDialog();
        	});
        	
        	/**展示对话框*/
        	$scope.showDialog = function(){
        		var param =$scope.getQueryParam(confJson[0].query);
        		CustomerDialog({dialog_alias_:dialogAlias,param:param,isCombine:isCombine,callBack:$scope.dialogOk});
        	}
        	$scope.dialogOk = function(returnData){
        		$scope.pushDataToForm(returnData,dialogMappingConf);
        	}
        	
        	$scope.custQueryOk = function(returnData){
        		$scope.pushDataToForm(returnData,dialogMappingConf);
        	}
        	//自定义查询，自定义对话框
        	$scope.initCustQuery = function(){
        		
        		
        		if(confJson.length<2) return ;
        		for(var i = 0,custQuery;custQuery=confJson[i++];){
        			if(custQuery.type != 'aliasbtn' && custQuery.type != 'querybtn') continue;
        			//通过触发字段进行初始化
        			var linkMessage =custQuery.linkMessage;
        			if(linkMessage.isMain){
            			var target =$("[ng-model='data.main."+linkMessage.linkTarget +"']");
        			}else{
        				var target = $("[ng-model='item."+linkMessage.linkTarget +"']",$(element).closest("[type='subTable']"));
        			}
        			
					// 回车联动
        			$(target).data("autoQueryData",custQuery);
        			if(linkMessage.linkType == 1){
        				$(target).bind("keydown", function(event){ 
        					if(event.keyCode != 13) return;
        					var custQuery =$(this).data("autoQueryData");
        					$scope.executeQuery(custQuery);
        				});
        			}else{
        			//值改变联动
        				var targetStr = linkMessage.isMain?'data.main.'+linkMessage.linkTarget :'item.'+linkMessage.linkTarget;
        				if(!$scope.autoQueryData)$scope.autoQueryData ={};
        				$scope.autoQueryData[targetStr]=custQuery;
        				$scope.$watch(targetStr,function(newValue, oldValue) {
        	        		if(newValue!=oldValue && newValue){ // 如果为空 则不查了
        	        			var custQuery = $scope.autoQueryData[targetStr];
        	        			$scope.executeQuery(custQuery);
        	        		}
        	        	});
        			}
    				
        			}
        		}
        	/** 执行别名脚本查询，自定义查询*/
        	$scope.executeQuery = function(custQuery){
        		var queryData =$scope.getQueryParam(custQuery.query),param={alias:custQuery.name};
				 param.querydata = JSON.stringify(queryData);
				if(custQuery.type == 'querybtn')
					DoQuery(param,function(data){
						$scope.pushDataToForm(data.list,custQuery.fields);
					})
				else if(custQuery.type == 'aliasbtn'){
					var data = RunAliasScript(param)
					$scope.pushDataToForm(data,custQuery.fields);
				}
        	}
        
    		//通过参数条件配置获取参数 条件
        	$scope.getQueryParam = function(querys){
        		var param ={};
        		for (var i = 0,query;query=querys[i++];) {
        			var value = query.initValue
					if(query.isMain){
						value =$scope.data.main[query.trigger] || value;
					}else{
						value =$scope.item[query.trigger] || value;
					}
        			param[query.condition] = value;
				}
        		return param;
        	}
        	$scope.initCustQuery();
        	
        	
        	//填充数据到数据库
        	$scope.pushDataToForm=function(returnData,mappingConf){
        		//将选择的值更新到data{[],[],}中
        		//先初始化防止报空
        		if(!returnData)returnData=[];
        		if(!$.isArray(returnData)){
        			returnData=new Array(returnData);
        		}
        		// 循环所有的返回值
        		for (var i = 0; i < returnData.length; i++) {
        			var hasSub = false,subDatas={};
        			// 循环所有mapping，将返回值，插入指定字段
					for (var int = 0,mapping;mapping= mappingConf[int++];){ 
						if(!mapping.src) continue; 
						var value =returnData[i][mapping.src] || returnData[i][mapping.src.toLowerCase()];
						var targets =mapping.target;
						// 返回值单个字段可以映射表单多个字段
						for (var j = 0,target;target= targets[j++];){
							/**target格式 data-mian-字段  sub-table-字段**/
							target =target.toLowerCase();
							var fieldMsg= target.split("-");
							if(fieldMsg.length != 3) continue;
							
							//如果是主表
							if(target.indexOf("data-main-")!= -1){
								$scope.data.main[fieldMsg[2]]=value; 
							}
							//如果是位于子表
							else if(isInSub){
								$scope.item[fieldMsg[2]]=value;
							}//位于主表填充子表，添加行的形式
							else{
								hasSub = true;
								if(!subDatas[fieldMsg[1]])subDatas[fieldMsg[1]]={}
								subDatas[fieldMsg[1]][fieldMsg[2]] = value;
							}
							
						}
					}
					// 如果有子表
					if(hasSub){
						for(tableName in subDatas){
							var tempData =$.extend({},$scope.data.sub[tableName].row);
							var data = $.extend(tempData,subDatas[tableName]);  
							$scope.data.sub[tableName].rows.push(data);
						}
					}
				}
        		!$scope.$parent.$$phase&&$scope.$parent.$digest();
        	}
        	
        	
        	
        }
    }
}]);


/** 
 * 执行查询
 * @param alias 查询别名
 * @param condition 查询条件
 * @param callback 查询完成后的回调
 * @param isSync 是否同步执行。
 */
function DoQuery(condition,callback,isSync){
	var url = __ctx + "/platform/bpm/bpmFormQuery/doQuery.ht";
	
	var isAsync=true;
	if(isSync!=undefined && isSync==true){
		isAsync=false;
	}
	
	$.ajax({
		   type: "POST",
		   url: url,
		   async:isAsync,
		   data: condition,
		   success: function(data){
			   if(callback)
					callback(data);
		   }
	});
	
};

/**
 * 调用别名脚本。
 * @param param	传入参数，参数格式 {alias:"别名必须传入",name:"abc","age":20}
 * @returns 返回数据格式如下：
 * {"result":"4","isSuccess":0,"msg":"别名脚本执行成功！"}
 * result:脚本返回的结果
 * isSuccess：0表示成果能够，1，失败
 * msg ：具体的出错信息
 */
function RunAliasScript(param){
	if(!param) param={};
	var url=__ctx + '/platform/system/aliasScript/executeAliasScript.ht';
	var rtn="";
	$.ajaxSetup({async:false});  //同步
	$.post(url,param,function(data){
		rtn=eval("("+ data +")");
	});
	$.ajaxSetup({async:true}); //异步
	return rtn;

}
