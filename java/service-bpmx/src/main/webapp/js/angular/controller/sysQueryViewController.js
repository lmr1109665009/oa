var sysQueryViewApp = angular.module('sysQueryViewApp', [ 'baseServices','DataRightsApp' ]);
sysQueryViewApp.controller('sysQueryViewCtrl',['$scope','BaseService','dataRightsService',function($scope,BaseService,dataRightsService){
	var service = dataRightsService;
	service.noRights = true;
	$scope.service = service;
	$scope.type = "sysQueryView";
	window.setTimeout(function(){
		$scope.tab =$("#tab").ligerTab({});
		$scope.hasInitTab = true;
		$scope.$digest();
		$scope.tab.selectTabItem("filterSetting");
		var width = $('div[tabid="filterSetting"]').width()- 50;
		var height = $("#sql").height();
		editor = CodeMirror.fromTextArea(document.getElementById("sql"), {
			mode: "text/x-mariadb",
			lineWrapping:true,
			lineNumbers: true
		 });
		editor.setSize(width,height);
		$scope.tab.selectTabItem("baseSetting");
//		//初始化日期控件
		FormUtil.initCalendar();	
		$("#ruleDiv").linkdiv({data:$scope.filterFields.conditions,
							updateContent:updateContent,
							rule2json:rule2json});
		
		$("#sqlTip").qtip({
			content:{
				text:'<div>该脚本为 groovyScript脚本 ，返回值为可执行的sql语句,' +
					'<p>例：String sql ="select ID,field from table where 1=1";<br/>' +
					
					'if(params.containsKey("CATEGORY"))<br/>' +
					'&nbsp;&nbsp;sql += " and CATEGORY like \'%"+CATEGORY+"%\'";<br/>' +
					'return sql; </p>' +
					'其中的params为系统所封装的一个Map对象,他封装了上下文查询条件。' +
					'</div>'
			}
		});

		$("#sqlTip2").qtip({
			content:{
				text:'<div>该脚本为sql语句 ，所填写语句会在where 1=1 后面添加,' +
				'<p>例：AND USERID = [CUR_USER]' +
				'</p>' +
				'</div>'
			}
		});
		
		$scope.$digest();
	},500);
	service.init($scope);
	$scope.save = function(){
		if ($scope._validForm()) {
			if ($scope.dataRightsJson.id) {
				$.ligerDialog.confirm("保存会覆盖模板，如果修改了模板请手动保存模板后再进行保存业务数据模板，是否继续保存？","提示信息",function(rtn) {
						if (rtn) service.customFormSubmit(showViewResponse);
					});
			} else {
				service.customFormSubmit(showViewResponse);
			}
		}
	},
	
	showViewResponse =function(responseText){
		$.ligerDialog.closeWaitting();
		var obj = new com.hotent.form.ResultMessage(responseText);
		if (obj.isSuccess()) {
			$.ligerDialog.confirm( obj.getMessage()+",是否继续操作","提示信息", function(rtn) {
				if(rtn){
					window.location.reload();
				}else{
					var url="list.ht?sqlId=" +sqlId;
					window.location.href =  url.getNewUrl();
				}
			});
		} else {
			$.ligerDialog.err("提示信息","保存视图失败!",obj.getMessage());
		}
	},
	
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
		if($scope.dataRightsJson.templateAlias=="") {
			$scope.tab.selectTabItem("baseSetting");
			form.valid();
			return false;	
		}
		return true;
	}
	//预览
	$scope.preview = function (){
		var alias = $scope.dataRightsJson.sqlAlias;
		var view = $scope.dataRightsJson.alias;
		

		if($.isEmpty(alias)){
			$.ligerDialog.error("请设置完信息保存后预览!","提示信息");
			return ;
		}
		var url=__ctx+ "/platform/system/sysQueryView/"+alias+"/"+view+".ht";
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
		var url=__ctx+ "/platform/system/sysQueryView/editTemplate.ht?id="+id;
		url=url.getNewUrl();
		$.openFullWindow(url);
	}
	//添加菜单
	$scope.addToResource = function () {
		var alias = $scope.dataRightsJson.sqlAlias;
		var url="/platform/system/sysQueryView/"+alias+"/"+$scope.dataRightsJson.alias+".ht";
		AddResourceDialog({addUrl:url});
	}
	function setGroupText(){
		var txt = "";
		for(var i = 0 ; i < $scope.groupingView.length ; i++){
			txt+=" {"+(i)+"} ";
		}
		for(var i = 0 ; i < $scope.groupingView.length ; i++){
			if(!$scope.groupingView[i].txtChange)
				$scope.groupingView[i].groupText = "<b> "+ $scope.groupingView[i].groupDesc +" : {0},数量{1} </b>"
		}
	}
	$scope.selectFieldToGroup = function(field){
		$scope.groupingView = $scope.groupingView || [];
		if(!field.gchecked){
			for(var i = 0 ; i < $scope.groupingView.length ; i++){
				if($scope.groupingView[i].groupField == field.name){
					$scope.groupingView.splice(i,1);
					setGroupText();
					return;
				}
			}
			 return;
		}
		
		for(var i = 0 ; i < $scope.groupingView.length ; i++){
			if($scope.groupingView[i].groupField == field.name){
				 return;
			}
		}
		$scope.groupingView.push({
			groupField:field.name,
			groupDesc:field.fieldDesc,
			groupColumnShow : 1,
			groupSummary:1,
			groupOrder : "asc"
		})
		setGroupText();
	}
}])
.controller('sysQueryViewSortListCtrl',['$scope','BaseService','dataRightsService',function($scope,BaseService,dataRightsService){
	$scope.sysQueryViewList=sysQueryViewList;
	$scope.moveTr = dataRightsService.moveTr;
	$scope.save = function(){
		var params = {},list = [];
		for ( var i = 0; i < $scope.sysQueryViewList.length; i++) {
			list.push({
				id:$scope.sysQueryViewList[i].id,
				sn:i
			})
		}
		params.list = JSON.stringify(list);
		$.post(__ctx+"/platform/system/sysQueryView/saveSortList.ht", params,function(data){
			if(data.success){
				frameElement.dialog.get('sucCall')(frameElement.dialog);
			}
			else
				$.ligerDialog.error("保存失败！","提示信息");
        });
		
	}
	$scope.close = function(){
		frameElement.dialog.close();
	}
}])
.controller('sysQueryViewExportsCtrl',['$scope','BaseService','dataRightsService',function($scope,BaseService,dataRightsService){
	var params = frameElement.dialog.get('conf').param;
	$scope.colModels = $.extend(true,[],params.colModel);
	$scope.exports = {
		type:1
	};
	$scope.selectAll = function(flag){
		for ( var i = 0; i < $scope.colModels.length; i++) {
			switch(flag){
				case 1:
					$scope.colModels[i].gchecked = true;
				break;
				case 2:
					$scope.colModels[i].gchecked = !$scope.colModels[i].gchecked;
					break;
				case 3:
					$scope.colModels[i].gchecked = false;
					break;
			}
			
		}
	}
	$scope.close = function(){
		frameElement.dialog.close();
	}
	$scope.toExports = function(){
		var exportNames = [];
		for ( var i = 0; i < $scope.colModels.length; i++) {
			if($scope.colModels[i].gchecked&&$scope.colModels[i].label)
				exportNames.push($scope.colModels[i].name)
		}
		var param = {
				viewId : params.viewId,
				isAll:$scope.exports.type==1?1:0,
				exportNames:exportNames.join(","),
				orderSeq:params.sortorder,
				sortField:params.sortname
		};
		param=$.extend({},param,frameElement.dialog.get('conf').searchParams);
		
		switch($scope.exports.type){
			case 2:
			case "2":
				param.paseSize = params.rowNum;
				param.page = params.page;
				break;
		}

		var form = $('#form'); 
		form.empty();
		for(var key in param){
			var input = $("<input type='hidden' name='"+key+"' value='"+param[key]+"'/>");
			form.append(input);
	    }
		$.ligerDialog.waitting("正在导出，请稍等！");
		form.submit();
		
		
		window.setTimeout(function(){
			frameElement.dialog.close();
		},2000);
	}
	$scope.hasChecked = function(){
		for ( var i = 0; i < $scope.colModels.length; i++) {
			if($scope.colModels[i].gchecked) return true;
		}
	}
	$scope.selectAll(1);
	
}])
//过滤条件隐藏属性<filter-hidden></filter-hidden>
.directive('filterHidden', function() {
	return {
		restrict : 'E',
		replace : true,
		template :  '<div class="hidden">'+
						'<!-- 数字的判断 -->'+
						'<span  id="judgeCon-1" class="judge-condition" >'+
							'<select  name="judgeCondition" class="ht-input" style="width:80px;  height: 30px;" onchange="judgeConditionChange.apply(this)">'+
								'<option value="1">等于</option>'+
								'<option value="2">不等于</option>'+
								'<option value="3">大于</option>'+
								'<option value="4">大于等于</option>'+
								'<option value="5">小于</option>'+
								'<option value="6">小于等于</option>'+
								'<option value="7">等于变量</option>'+
								'<option value="8">不等于变量</option>'+
							'</select>'+
						'</span>'+
						'<!-- 字符串的判断 -->'+
						'<span  id="judgeCon-2"  class="judge-condition">'+
							'<select name="judgeCondition" class="ht-input" style="width:80px;  height: 30px;" onchange="judgeConditionChange.apply(this)">'+
								'<option value="1">等于</option>'+
								'<option value="3">等于(忽略大小写)</option>'+
								'<option value="2">不等于</option>'+
								'<option value="4">like</option>'+
								'<option value="5">like左</option>'+
								'<option value="6">like右</option>'+
								'<option value="7">等于变量</option>'+
								'<option value="8">不等于变量</option>'+
							'</select>'+
						'</span>'+
						'<!-- 字典的判断 -->'+
						'<span  id="judgeCon-4"  class="judge-condition">'+
							'<select name="judgeCondition" class="ht-input" style="width:80px;  height: 30px;">'+
								'<option value="1">等于</option>'+
								'<option value="2">不等于</option>'+
							'</select>'+
						'</span>'+
						'<!-- 选择器的判断 -->'+
						'<span  id="judgeCon-5"   class="judge-condition">'+
							'<select  name="judgeCondition" onchange="judgeConditionChange.apply(this)" class="ht-input" style="width:80px;  height: 30px;">'+
								'<option value="1">包含</option>'+
								'<option value="2">不包含</option>'+
								'<option value="3">等于变量</option>'+
								'<option value="4">不等于变量</option>'+
							'</select>'+
						'</span>'+
						'<!-- 默认类型-->'+
						'<span id="normal-input" class="judge-value"  type="1">'+
							'<input class="short-input ht-input" name="judgeValue" type="text" style="width:100px;margin-left: 5px;"/>'+
						'</span>'+
						'<!-- 日期类型 -->'+
						'<span id="date-input" class="judge-value"  type="1">'+
							'<input id="date-input" type="text" class="Wdate ht-input" style="width:180px;"/>'+
						'</span>'+
					''+
						'<!-- 用户选择器 -->'+
						'<div id="user-div">'+
							'<span  class="judge-value" type="1">'+
								'<input type="hidden" value="" />'+
								'<input type="text" readonly="readonly" class="ht-input" style="width:130px;margin-left:5px;" />'+
								'<a href="javascript:;" class="link users">选择</a>'+
							'</span>'+
						'</div>'+
					''+
						'<!-- 角色选择器 -->'+
						'<div id="role-div">'+
							'<span  class="judge-value"  type="1" >'+
								'<input type="hidden" value="" />'+
								'<input type="text" readonly="readonly" class="ht-input" style="width:130px;margin-left:5px;" />'+
								'<a href="javascript:;" class="link roles">选择</a>'+
							'</span>'+
						'</div>'+
						'<!-- 组织选择器 -->'+
						'<div id="org-div">'+
							'<span  class="judge-value"  type="1">'+
								'<input type="hidden" value="">'+
								'<input type="text" readonly="readonly" class="ht-input" style="width:130px;margin-left:5px;" />'+
								'<a href="javascript:;" class="link orgs">选择</a>'+
							'</span>'+
						'</div>'+
						'<!-- 岗位选择器 -->'+
						'<div id="position-div">'+
							'<span  class="judge-value"  type="1">'+
								'<input type="hidden" value="">'+
								'<input type="text" readonly="readonly" class="ht-input" style="width:130px;margin-left:5px;" />'+
								'<a href="javascript:;" class="link positions">选择</a>'+
							'</span>'+
						'</div>'+
						'<!--常用变量-->'+
						'<span id="commonVar" class="judge-value"  type="2">'+
							'<select class="ht-input" style="width:100px;height: 30px;margin-left: 5px;">'+
								'<option value="{{co.alias}}" ng-repeat="co in commonVars">{{co.name}}[{{co.number?"数字":"字符串"}}]</option>'+
							'</select>'+
						'</span>'+
						'<select id="flowVarsSelect" class="left margin-set ht-input" name="flowVars" onchange="flowVarChange.apply(this)" style="height: 30px; ">'+
							'<option value="">--请选择--</option>'+
							'<option value="{{f.fieldName}}" datefmt="{{f.dateFormat}}" ctltype="{{f.controlType}}"  ftype="{{f.dataType}}" ng-repeat="f in sysQueryMetaFields">{{f.fieldDesc}}</option>'+
						'</select>'+
					'</div>'
	};
})
//过滤条件 ， 嵌入式 <filter-include-setting></filter-include-setting>
.directive('filterIncludeSetting', function() {
	return {
		restrict : 'E',
		replace : true,
		template :  '<table style="margin: auto;width:100%;margin-top: 1px;" class="table-detail" cellpadding="0" cellspacing="0" border="0">'+
						'<thead>'+
							'<tr style="height: 50px;">'+
								'<td colspan="4">'+
									'脚本类型：'+
									'<select ng-model="filterFields.type" class="ht-input">'+
										'<option value="0" ></option>'+
										'<option value="1" >条件脚本</option>'+
										'<option value="2" >SQL</option>'+
										'<option value="3" >追加SQL</option>'+
									'</select>'+
								'</td>'+
							'</tr>'+
						'</thead>'+
						'<tbody>'+
							'<tr>'+
								'<td colspan="4" >'+
								
									'<fieldset style="margin: 5px 0px 5px 0px;" id="filterSetting" ng-show="filterFields.type==1" >'+
										'<legend>'+
											'<span>条件设置</span>'+
										'</legend>'+
										'<div class="table-top">'+
											'<div class="table-top-right">'+
												'<div class="toolBar" style="margin:0;">'+
													'<div class="group">'+
														'<a class="link add" onclick="addDiv(1)">添加条件</a>'+
													'</div>'+
													'<div class="l-bar-separator"></div>'+
												
													'<div class="group">'+
														'<a class="link switchuser" onclick="assembleDiv()">组合规则</a>'+
													'</div>'+
													'<div class="l-bar-separator"></div>'+
													'<div class="group">'+
														'<a class="link switchuser" onclick="splitDiv()">拆分规则</a>'+
													'</div>'+
													'<div class="l-bar-separator"></div>'+
													'<div class="group">'+
														'<a class="link del" onclick="removeDiv()">删除</a>'+
													'</div>'+
												'</div>'+
											'</div>'+
										'</div>'+
										'<div id="ruleDiv" style="border:2px solid #ccc;margin:5px 0 0 0;"></div>'+
									'</fieldset>'+
									'<fieldset style="margin: 5px 0px 5px 0px;" id="sqlSetting" ng-show="filterFields.type==2||filterFields.type==3" >'+
										'<legend ng-show="filterFields.type==2" >'+
											'<span>SQL设置</span>'+
										'</legend>'+
										'<legend ng-show="filterFields.type==3">'+
											'<span>追加SQL过滤</span>'+
										'</legend>'+
										'<table  cellpadding="0" cellspacing="0" border="0" style="width: 100%;"  class="table-detail" >'+
											'<tr>'+
												'<td width="5%">'+
													'<div id="sqlTip" ng-show="filterFields.type==2">'+
														'<a href="javascript:;" class="tipinfo"></a>'+
													'</div>'+
													
													'<div id="sqlTip2" ng-show="filterFields.type==3" >'+
														'<a href="javascript:;" class="tipinfo"></a>'+
													'</div>'+
													
													'<td width="10%">常用变量：</td>'+
													'<td>'+
														'<select id="varFieldSelect" class="ht-input" name="varFields" onchange="varsChange.apply(this)">'+
															'<option value="">--请选择--</option>'+
															'<optgroup class="main-table-item" label="sql字段" ></optgroup>'+
																'<option class="field-item"  value="{{c.name}}" ng-repeat="c in conditionFields">{{c.name}}</option>'+
															'<optgroup class="main-table-item" label="常用变量" ></optgroup>'+
																'<option value="{{co.alias}}" ng-repeat="co in commonVars">{{co.name}}[{{co.number?"数字":"字符串"}}]</option>'+
														'</select>'+
													'</td>'+
												'</td>'+
											'</tr>'+
											'<tr>'+
												'<td colspan="7">'+
													'<textarea  id="sql" ng-model="filterFields.sql" style="height: 300px;width:1000px;display:none !important;" class="ht-input  border-none margin-none "></textarea>'+
												'</td>'+
											'</tr>'+
										'</table>'+
									'</fieldset>'+
								'</td>'+
							'</tr>'+
						'</tbody>'+
					'</table>',
					link:function(scope){
					
						scope.commonVars = commonVars;
					}
	};
})
