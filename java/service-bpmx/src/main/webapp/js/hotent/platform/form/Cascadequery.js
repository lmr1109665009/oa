/**
 * 说明
 * [formtype="window"]主要是处理子表弹出框模式，
 * table.listTable  处理子表的块模式和表内模式
 */

if (typeof QueryUI == 'undefined') {
	QueryUI = {};
}

/**
 * queryedCacheData保存当前页面的已经查询出来的数据
 * 数据格式：
 * queryedCacheData{
 * 		
 * }
 */
QueryUI.queryedCacheData={};
/**
 * 加上targetObj，针对该对象下的级联进行初始化，
 * 在CustomForm.js中初始化子表级联时使用
 */
QueryUI.init = function(targetObj){
	var querys;
	var filters = "select[selectquery][selectqueryed!='selectqueryed']";
	if(targetObj){//绑定指定对象下的级联
		querys = $(targetObj).find(filters);
	}else {
		querys = $(filters);
	}
	querys.each(function(){
		var me = $(this),
			selectquery = me.attr("selectquery");
			
		if (!selectquery)
			return true;
		selectquery = selectquery.replaceAll("'", "\"");
		var queryJson = JSON2.parse(selectquery);
		var query = queryJson.query;
		
		for (var i = 0; i < query.length; i++) {
				var field;
				if(query[i].trigger=='-1')
				continue;
				// isMain是true 就是绑定主表的字段
				if (query[i].isMain=="true") {
					field = $(".formTable [name$=':" + query[i].trigger + "']");
				} else {
					field = me.closest('tr').find("[name='" + query[i].trigger + "']");
					if(field.length==0){
						field = me.parents('table,[formtype="window"]').find("[name='" + query[i].trigger + "']");
					}
					field.unbind("change");
				}
				if (field != ""){
					QueryUI.change(field, me, queryJson);
				}
		}
		QueryUI.getvalue(me, queryJson, true);
	});
};

QueryUI.change = function(field, SelectObj, queryJson) {
		field.bind("change", function() {
				QueryUI.getvalue(SelectObj, queryJson);
		});
	};

QueryUI.replaceValue = function(objValue) {
	return objValue.replaceAll(",", "#,");
}

var cachedJson={};
QueryUI.getCachedHtml = function(queryJson) {
	return cachedJson[queryJson.name+'_'+queryJson.binding.key+'_'+queryJson.binding.value];
}
QueryUI.setCachedHtml = function(queryJson, html) {
	cachedJson[queryJson.name+'_'+queryJson.binding.key+'_'+queryJson.binding.value] = html;
}
/**
 * isInit，表示是否为表单初始化设置级联
 * 表单初始化时，如果B级联需要的查询条件为A级联的值，则不初始化B级联下拉框，在A级联的值改变时，才会触发B级联的初始化
 */
QueryUI.getvalue = function(SelectObj, queryJson, isInit) {
	var query = queryJson.query;
	if(query.length==0){// 对没有查询条件的级联进行缓存
		var cachedHtml = QueryUI.getCachedHtml(queryJson);
		
		if(typeof(cachedHtml)=='undefined'){// 如果没有缓存
			QueryUI.setCachedHtml(queryJson, 1);// 标识正在建立缓存
		}else {// 如果有缓存
			// cachedHtml==1时，表示正在初始化所有级联，但缓存未完全建立，在建立缓存时，会填充下拉框数据
			if(cachedHtml != 1) {// cachedHtml!=1时，可直接填充缓存数据
				SelectObj.html(cachedHtml);
				//添加一个属性，标识已经处理过，
				SelectObj.attr("selectqueryed","selectqueryed");
			}
			return;
		}
	}
	var fieldValue, subfield, checkfield,field;
	var querydataStr = "{";
	for (var i = 0; i < query.length; i++) {
		var triggerField = query[i].trigger;
		if (query[i].isMain=="true") {
			//输入框，下拉框，数据字典
			subfield = $(".formTable [name$=':" + triggerField + "']");
			// 选择器
			field = $(".formTable [name$=':" + triggerField + "ID']");
			// 单选、复选框
			checkfield = $(":checked[name$=':" + triggerField + "']");
		} else {
			subfield = SelectObj.closest('tr').find("[name='" + triggerField + "']");
			if(subfield && subfield.length>0){
				field = SelectObj.closest('tr').find("input[name='" + triggerField + "ID']");
				checkfield = SelectObj.closest('tr').find(":checked[name='" + triggerField + "']");
			}else {
				subfield = SelectObj.parents('table,[formtype="window"]').find("[name='" + triggerField + "']");
				field = SelectObj.parents('table,[formtype="window"]').find("input[name='" + triggerField + "ID']");
				checkfield = SelectObj.parents('table,[formtype="window"]').find(":checked[name='" + triggerField + "']");
			}
			
		}
		if(isInit && subfield.is('select')) {
			return false;
		}
		
		//级联参数值为空时，将子级联清空
		if((typeof subfield.val() == "undefined" || subfield.val() =="") && query[i].initValue==""){
			SelectObj.html('');
			SelectObj.trigger("change").trigger('blur');
			return false;
		}
		switch (query[i].controlType) {
			//没有绑定任何表字段controlType=-1而有预设值时
			case '-1':break;
			// 输入框，下拉框，数据字典是一类直接获取值
			case '1' :
			case '2' :
			case '3' :
			case '11' :
			case '15' :
				fieldValue = subfield.val();
				break;
			// 选择器
			case '4' :
			case '5' :
			case '6' :
			case '7' :
			case '8' :
			case '17' :
			case '18' :
			case '19' :
				fieldValue =field.val();
				fieldValue = QueryUI.replaceValue(fieldValue);
				break;
			// 单选、复选框
			case '13' :
			case '14' :
				var tempvalue;
				checkfield.each(function() {
					var obj = $(this);
					if (typeof(tempvalue) != "undefined"
							&& tempvalue != "") {
						tempvalue += "#," + obj.val();
					} else {
						tempvalue = obj.val();
					}
				});
				fieldValue = tempvalue;
				tempvalue = "";
				break;
		}
		//如果对应的绑定字段有值则使用该值，没有值则判断设计时有没有输入预设值，有预设值则使用，没有则不做输入条件
		var condition = query[i].condition;
		var initValue = query[i].initValue;
		if (fieldValue && fieldValue!==0) {
			querydataStr += condition + ":\"" + fieldValue + "\",";
		}else if(initValue!='' && initValue!==0){
			querydataStr += condition + ":\"" + initValue + "\",";
		}
	}
	if (querydataStr.length > 1){
		querydataStr = querydataStr.substring(0, querydataStr.length - 1);
	}
	querydataStr += "}";
	
	SelectObj.empty();
	queryCond = {
		alias : queryJson.name,
		querydata : querydataStr,
		page : 0,
		pagesize : 0
	};
	DoQuery(queryCond, function(data) {
		var list = data.list;
		if (data.errors || list.length==0) return;
		QueryUI.handData(list,queryJson,SelectObj);
		//处理只读情况
		if($(SelectObj).attr("right")=="r"||$(SelectObj).attr("right")=="rp"){
			var text = $(SelectObj).find("option:selected").text();
			$(SelectObj).after(text);
			//当级联时，不要将SelectObj下拉框remove掉，因为有些级联的查询条件是根据其他级联的值来查询的，如果remove掉将可能取不到完整的查询条件
			$(SelectObj).addClass("hidden");
		}
	},false);
};

/**
 * 获取数据以后经过处理，显示多下拉列表中
 * 修改时间：2015-05-18 14:05
 */
QueryUI.handData=function(data,queryJson,currSelectObj){
	var selectObj=$(currSelectObj);
	var queryValue = queryJson.binding.value.toLowerCase();
	var queryKey = queryJson.binding.key.toLowerCase();
	selectObj.html("<option value=''>请选择……</option>");
	for (var i = 0; i < data.length; i++) {
		var dataobj = data[i];
		var datavalue = dataobj[queryValue];
		var datakey = dataobj[queryKey];
		var opt = $("<option>").val(datakey).text(datavalue);
		selectObj.append(opt);
	}
	selectObj.attr("selectqueryed","selectqueryed");
	if(queryJson.query.length == 0){
		var html = selectObj.html();
		// 设置缓存html
		QueryUI.setCachedHtml(queryJson, html);
		//填充所有相同查询的级联
		$('select[selectqueryed!="selectqueryed"][selectquery="{\'name\':\''
				+queryJson.name+'\',\'binding\':{\'key\':\''
				+queryJson.binding.key+'\',\'value\':\''
				+queryJson.binding.value+'\'},\'query\':[]}"]')
				.html(html)
				.each(function(){
					QueryUI.triggerChange(this);
				})
				.attr("selectqueryed","selectqueryed");
	}
	QueryUI.triggerChange(currSelectObj);
};

/**
 * 给下拉框设置值，如果值发生改变，则触发change事件
 */
QueryUI.triggerChange=function(currSelectObj){
	var selectObj=$(currSelectObj);
	var tempValue = selectObj.attr("val");
	selectObj.val(tempValue);
	if(tempValue || tempValue === 0) selectObj.trigger("change").trigger('blur');
};
/**
 * 获取缓存数据
 */
QueryUI.getCascaData = function(alias,querydata){
	if(!QueryUI.queryedCacheData)return;
	var tempAlias=QueryUI.queryedCacheData[alias];
	if(!tempAlias)return ;
	var tempData=tempAlias[querydata];
	return tempData;
};
/**
 * 设置缓存数据
 */
QueryUI.setCascaData = function(alias,querydata,data){
	var aliasObj = QueryUI.queryedCacheData[alias] || {};
	aliasObj[querydata] = data;
	QueryUI.queryedCacheData[alias] = aliasObj;
};