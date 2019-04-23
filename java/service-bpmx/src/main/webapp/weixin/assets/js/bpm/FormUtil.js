
/**
 *  @description 
 *  手机表单开发工具类
 *  开发过程中可能需要如下类 flowService，baseService
 *  
 *  @http请求：
 *  def=FormUtil.post(url,data);
 *  def.then(function(data){},function(status){alert(errorCode:status)})
 *  
 *  @表单数据结构 $scope.data
 *   data.main
 *   data.sub[tableName].rows ={row:子表数据模板,rows:子表行}
 *  @表单数据权限$scope.permission
 *  主表权限结构：	 permission.mian[fieldName] ="b/w/r/n";
 *  子表字段权限：permission.fields[tableName][fieldName] ="b/w/r/n";
 *  子表权限结构：	permission[tableName] = {add:true/false,del:true,hidden:true,require:true}
 */ 
var FormUtil = {};
/**
 * 发送请求
 */
FormUtil.post = function(url,data){
	var flowService = this.getService("baseService");
	return flowService.postForm(url,data);
}
/**
 * 修改主表的数据 data为主表数据{name:name,sex:1}
 */
FormUtil.setMainData = function(data){
	if(!data) return;
	var mainData = this.getScope().data.main; //获取主表数据
	for(key in data) {
		mainData[key] =data[key];
	}
	this.$scope.$apply();
}

/**
 * 获取表单json对象。
 */
FormUtil.getJson=function(){
	return  this.getScope().data;
}

/**
 * 添加行 rowData 可为空
 */
FormUtil.addRow = function(tableName,rowData){
	var row= this.getScope().data.sub[tableName].row;
	var obj=$.extend(rowData ||{}, row) ;
	$scope.data.sub[tableName].rows.push(obj);
	this.$scope.$apply();
},

/**
 * 编辑行。
 * tableName ：子表名称
 * index： 索引
 * rowData：行数据
 */
FormUtil.saveRow = function(tableName,index,rowData){
	this.getScope().data.sub[tableName].rows[index]=rowData;
	this.$scope.$apply();
}

/**
 * 删除行
 * tableName :子表名称
 * index ：第几行
 */
FormUtil.removeRow = function(tableName,index){
	var flowService = this.getService("flowService");
	flowService.removeRow(this.getScope(),tableName,index);
	this.$scope.$apply();
}

/**
 * 获取表单的scope对象。
 */
FormUtil.getScope = function(){
	if(this.$scope) return this.$scope;
	var controllerElement =$("[ng-controller]");
	if(controllerElement.length!=1){
		alert("scope 获取失败！");
		return;
	} 
	this.$scope = angular.element(controllerElement).scope(); 
	return this.$scope;
}
/**
 * 获取当前环境中的 service
 * serviceName：指定的服务名称。
 */
FormUtil.getService = function(serviceName){
	if(!this.$injector){
		this.$injector =angular.element($("[ng-controller]")).injector();
	}
	if(this.$injector.has(serviceName)) {
		return this.$injector.get(serviceName);
	}
	else {
		alert(serviceName+"angular环境中没有找到该service！");
	}
}