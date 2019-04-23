
var  formServiceModule = angular.module('formServiceModule',["base"]);

formServiceModule.service('formService',['baseService',function(baseService){
	return {
		/**
		 * 格式化数据 
		 * @param scope    
		 * @param modelName 目標modelName
		 * @param exp	 函数表达式
		 * subFormDiv 子表div
		 */
		doMath:function(scope,modelName,funcexp){
			var value = FormMath.replaceName2Value(funcexp,scope);
			try{
				value = eval("("+value+")");
			}
			catch(e){
				return true;
			}
			if(/^(Infinity|NaN)$/i.test(value))return true;
			
			//按字段的小数点设置处理小数点的问题（四舍五入）
			value = Number(value);
			eval("scope."+modelName+"=value");
		},
		/**
		 * 數字格式化
		 * @returns {String} 返回的数据
		 */
		dataFormat : function(value,formatorJson,nocomdify) {
			var coinvalue = formatorJson.coinValue, iscomdify = formatorJson.isShowComdify, 
				capital = formatorJson.capital,decimallen=formatorJson.decimalValue;
			
			if(!value||value=="undefined") return;
			value =$.fn.toNumber(value)+""; 
			if (capital) {
				value = $.convertCurrency(value);
				me.val(value);
				return;
			}

			// 小数处理
			if (decimallen > 0 && value != '') {
				if (value.indexOf('.') != -1) {
					var ary = value.split('.');
					var temp = ary[ary.length - 1];
					if (temp.length > 0 && temp.length < decimallen) {
						var zeroLen = '';
						for ( var i = 0; i < decimallen
								- temp.length; i++) {
							zeroLen = zeroLen + '0';
						}
						value = value + zeroLen;
					}else if(temp.length > 0 && temp.length > decimallen ){
						temp = temp.substring(0,decimallen);
						ary[ary.length - 1] =temp;
						value =ary.join(".");
					}
				} else {
					var zeroLen = '';
					for ( var i = 0; i < decimallen; i++) {
						zeroLen = zeroLen + '0';
					}
					value = value + '.' + zeroLen;
				}
			}
			// 添加货币标签
			if (coinvalue != null
					&& coinvalue != ''
					&& value != '') {
				value = coinvalue + value;
			}

			return value;
		},
		isValid:function (el){
			if($(el)[0]&&$(el)[0].querySelectorAll(".has-error").length>0) return false;
			return true;
		},
		/**
		 * 日期计算
		 * 日期开始，日期结束，计算类型 day,yeay,month
		 */
		doDateCalc:function(startTime,endTime,diffType){
			if(typeof startTime == "undefined" || startTime == "" 
				|| typeof endTime == "undefined" || endTime == ""){
				return "";
			}
			var result;
			var temptype = diffType;
			if (diffType == "hour"){
				diffType = "minute";
			}
			if(startTime.indexOf("-") == -1 && endTime.indexOf("-") == -1){
				result=FormMath.timeVal(startTime,endTime,diffType);//日期格式为 hh:mm:ss
			}else{
				result=FormMath.dateVal(startTime,endTime,diffType);//日期格式YYYY-MM-DD
			}
			if (isNaN(result)){
				result = "";
			} else if (temptype == "hour") {
				//精确到半小时
				temp = parseInt(result / 60);
				if (result % 60 >= 30){
					temp = temp + 0.5;
				}
				result = temp;
			}
			return result;
		}

	};
}])

/****************数学统计的扩展方法********************/

if (typeof FormMath == 'undefined') {
	FormMath = {};
}

FormMath.toNumber = function(x){
	if(x === null || x === undefined ||  x === '')
		return '';
	if(typeof x == "string"){
		x = x.replace(/,/g, "");
	}
	var match = x.toString().match(/([$|￥])\d+\.?\d*/);
	if(match){
		x = x.replace(match[1],'');
	}
	return Number(x);
};

/**
 * 返回x的绝对值
 * @param  {[type]} x [description]
 * @return {[type]}   [description]
 */
FormMath.abs = function(x){
    return Math.abs(x);
}

/**
 * 把x四舍五入为最接近的整数
 * @param  {[type]} x [description]
 * @return {[type]}   [description]
 */
FormMath.round = function(x){
	return Math.round(x);
}

/**
 * 对x进行上舍入，返回等于或者大于x,并且与x最接近的整数
 * @param  {[type]} x [description]
 * @return {[type]}   [description]
 */
FormMath.ceil = function(x){
	return Math.ceil(x);
}

/**
 * 对x进行下舍入，返回小于或者等于x，并且与x最接近的整数
 * @param  {[type]} x [description]
 * @return {[type]}   [description]
 */
FormMath.floor = function(x){
	return Math.floor(x);
}

/**
 * 返回集合ary中最大的数
 * @param  {[type]} ary [description]
 * @return {[type]}     [description]
 */
FormMath.max = function(ary){
	var tmp,
		x = 0,
		size = ary.length;
	for(var i=0;i<size;i++){
		x = FormMath.toNumber(ary[i]);
		if(isNaN(x))continue;
		if(tmp===undefined){
			tmp = x;
		}
		else{
			if(x>tmp)
				tmp = x;	
		}
	}
	tmp = FormMath.toNumber(tmp);
	return tmp;
}

/**
 * 返回集合ary中最小的数
 * @param  {[type]} ary [description]
 * @return {[type]}     [description]
 */
FormMath.min = function(ary){
	var tmp,
		x = 0,
		size = ary.length;
	for(var i=0;i<size;i++){
		x = FormMath.toNumber(ary[i]);
		if(isNaN(x))continue;
		if(tmp===undefined){
			tmp = x;
		}
		else{
			if(x<tmp)
				tmp = x;	
		}
	}
	tmp = FormMath.toNumber(tmp);
	return tmp;
}

/**
 * 返回x的平方根
 * @param  {[type]} x [description]
 * @return {[type]}   [description]
 */
FormMath.sqrt = function(x){
	return Math.sqrt(x);
}

/**
 * 获取ary的平均值
 * @param  {[type]} ary [description]
 * @return {[type]}     [description]
 */
FormMath.average = function(ary){
	var tmp,
		x = 0,
		size = ary.length;
	for(var i=0;i<size;i++){
		x = FormMath.toNumber(ary[i]);
		if(isNaN(x))continue;
		if(tmp===undefined){
			tmp = x;
		}
		else{
			tmp += x;
		}
	}
	tmp = FormMath.toNumber(tmp/size);
	return tmp;
};

/**
 * 求ary的和
 * @param  {[type]} ary [description]
 * @return {[type]}     [description]
 */
FormMath.sum = function(ary){
	var tmp,
		x = 0,
		size = ary.length;
	for(var i=0;i<size;i++){
		x = FormMath.toNumber(ary[i]);
		if(isNaN(x))continue;
		if(tmp===undefined){
			tmp = x;
		}
		else{
			tmp += x;
		}
	}
	tmp = FormMath.toNumber(tmp);
	return tmp;
};

/**
 * 返回保留小数点后b位的x的四舍五入值
 * @param  {[type]} x [description]
 * @param  {[type]} b [description]
 * @return {[type]}   [description]
 */
FormMath.tofixed = function(x,b){
	var tmp = FormMath.toNumber(x);
	b = FormMath.toNumber(b);
	if(isNaN(tmp)||isNaN(b))return x;
	return tmp.toFixed(b);
};




/****************数学统计的逻辑代码********************/
/*
 * 将函数表达式中的目标字段获取出来
 * **/
FormMath.replaceName2Value = function(exp,scope){
	
	//{手机数字(item.sj)}*2
	//FormMath.sum([合计(data.sub.zbcs.hj)])
	if(!exp)return 0;
	var reg = /\{.*?\(([data.main|data.sub|item].*?)\)\}/g;//
	exp = exp.replace(reg,function(){
		var name = arguments[1],
			value=0;
		var	object;
		//子表
		if(scope){
			//子表统计计算情况。多行数据
			if(name.indexOf("data.sub")==0){
				var valArray =[];
				var subMsg = name.split(".");
				var fieldName = subMsg[3];
				var subTableSrc =name.replace(fieldName,"");
				var rows = eval('scope.'+subTableSrc+'rows');
				for(var i=0,row;row=rows[i++];){
					valArray.push(row[fieldName]); 
				}
				value = valArray.join(",");
			}else{
				var val = eval('scope.'+name);
				val = FormMath.toNumber(val);
				if(!isNaN(val) && ""!=val) value = val;
			}
		}
		return value;
	});
	return exp;
};


/******
 * --------------------------日期计算--------------------------------
 */


//日期格式YYYY-MM-DD
FormMath.dateVal = function(startTime, endTime, diffType){
	startTime = startTime.replace(/\-/g, "/");
	endTime = endTime.replace(/\-/g, "/");
	diffType = diffType.toLowerCase();
	var sTime = new Date(startTime); //开始时间
	var eTime = new Date(endTime); //结束时间
	
	if(diffType == "month"){
		return FormMath.getMonthBetween(sTime,eTime);
	}
	
	var divNum = FormMath.getDiffType(diffType);
	var result = parseInt((eTime.getTime() - sTime.getTime()) / parseInt(divNum));
	//作为除数的数字        结果+1 如，1号到1号则为1天
	if("day" == diffType)result++;
	return result;
}

//日期格式为 hh:mm:ss
FormMath.timeVal = function(startTime, endTime, diffType){
	var temptype = diffType;
	if (diffType == "hour"){
		diffType = "minute";
	}
	var divNum = FormMath.getDiffType(diffType)
	var sTime = startTime.split(':', 3);
	var eTime = endTime.split(':', 3);
	var h=(parseInt(eTime[0])-parseInt(sTime[0]))*3600;
	var m=(parseInt(eTime[1])-parseInt(sTime[1]))*60;
	if(m<0){
		h=h-1;
		m=60+m;
	}
	var result =parseInt(((h+m)*1000/parseInt(divNum)));
	return result;
}

FormMath.getMonthBetween = function(startDate,endDate){
	var num=0;
	var year=endDate.getFullYear()-startDate.getFullYear();
		num+=year*12;
	var month=endDate.getMonth()-startDate.getMonth();
		num+=month;
	var day=endDate.getDate()-startDate.getDate();
	if(day>-1){ 
		num+=1;
	}
	return num;
}

FormMath.getDiffType=function(diffType){
	var divNum=1;
	switch (diffType) {
		case "second":
			divNum = 1000;
			break;
		case "minute":
			divNum = 1000 * 60;
			break;
		case "hour":
			divNum = 1000 * 3600;
			break;
		case "day":
			divNum = 1000 * 3600 * 24;
			break;
		default:
			break;
    }
	return divNum;
}
