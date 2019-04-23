/**
 * angular js 校验
 * $.fn.validRules
 * $.fn.addRule
 * 
 */
(function($) {
	$.extend($.fn, {
		// 手动扩展验证规则
		addRule : function(name,rule) {
			this.rules[name.toLowerCase()]=rule;
		},
		// ng-js 验证单个控件。
		validRules : function(value,validRule,element){
			
			var rules = this.rules;
			var name = element.attr("name");
			value = value == null ? "" : value+"";
			if(typeof value == 'string')value.trim();
			// 获取json。
			var json = validRule;
			if(typeof json == 'string') {
				json = eval('(' + validRule + ')');
			}
			var isRequired = json.required;
			
			// 非必填的字段且值为空 那么直接返回成功。
			if ((isRequired == false || isRequired == undefined) && value == "")
				return true;
			
			//number：true将number取消格式化
			if(json.number){
				value =$.fn.toNumber(value)+"";
			}
			
			// 遍历json规则。
			var validates=[] ;
			for (var name in json) {
				var validValue = json[name];
				validates.push(
					{
						rules:rules,//规则json
						ruleName:name,// 规则名称
						validValue:validValue,//验证的值
						value:value,//实际的值
						errormsgtips:element.errormsgtips,
						element:element
					}
				);
			}
			//验证规则
			for (var i=0,v;v=validates[i++];) {
				if(i==validates.length)v.isLast=true;
				var _valid = this._validRules(v);
				//如果当前规则验证不通过则直接返回false
				if(!_valid) return false;
			}
			
			//全部验证通过则返回true
			return true;
		},
		/***
		 * 将格式化数字转换成number
		 */
		toNumber: function(x){
			if(x === null || x === undefined ||  x === '')
				return '';
			if(typeof x == "string"){
				x = x.replace(/,/g, "");
			}
			var match = x.toString().match(/([$|￥])\d+\.?\d*/);
			if(match){
				x = x.replace(match[1],'');
			}
			var val =Number(x);
			if(Number.isNaN(val))return '';
			return val;
		},
		/**
		 * 验证规则
		 **/
		_validRules :function(conf){
			var  _valid = true,
				rules = conf.rules,//规则json
				ruleName = conf.ruleName.toLowerCase(),// 规则名称
				validValue = conf.validValue,//验证的值
				value =conf.value,//实际的值
				element = conf.element;//当前对象
			
			if(element.showtype){
				try{
					//处理当element为货币时，value会类似：￥10.00 但这事实上应该是数字
					// 所以需要处理一下value，让其变成 10.00
					var str=$(element).attr('showtype').replace(new RegExp("'","gm"),"\"");
					var cv=JSON.parse(str).coinValue;
					if(value.startWith("+"+cv)||value.startWith("-"+cv)||value.startWith(cv)){
						value=value.replace(cv,"");
					}
				}catch(e){}
			}
			
			
			
				// 取得验证规则。
				var rule = rules[ruleName];
				if(!rule) return true;
				// 验证规则如下：
				// email:true,url:true.
				//验证规则是boolean类型
				if ($.type(validValue)  === "boolean") 
					_valid = (!rule.rule(value) && validValue == true) ? false:true; 	
				else 
					_valid = rule.rule(value, validValue,element);
				//错误 提示， qtip 会有
				//var qtipApi = $(element).qtip("api"); 
				var amui_pop = element.data('amui.popover')
				if (!_valid){ //验证不通过返回消息
					var errorMsg=rule.msg;
					if(conf.errormsgtips){
						var errormsgtips=eval("("+conf.errormsgtips.replaceAll("'","\"")+")")
						for(var i in errormsgtips){
							if(i==ruleName){
								errorMsg=errormsgtips[i];
								break;
							}
						}
					}
					var errMsg =this.format(errorMsg, validValue);
					
					if(!amui_pop){ //qtipApi  
						var defaultSetting = {
							//	hide: { event: "unfocus"},
								style: {classes: 'qtip-default  qtip qtip-bootstrap qtip-shadow' },
								position: { my: 'top left', at: 'bottom center'},
								content : errMsg
						};
						//$(element).qtip(defaultSetting);
						element.popover({ trigger: "focus,hover", content: errMsg, theme: "warning lg" })
					}else{
						amui_pop.setContent(errMsg);
						/*if(qtipApi.disabled)qtipApi.enable();
						qtipApi._updateContent(errMsg);
						qtipApi.show()*/
					}
					return _valid;
				}
				//置为不可用，隐藏
				/*if(qtipApi &&!qtipApi.disabled && conf.isLast){
					qtipApi.hide();
					qtipApi.destroy();
					//qtipApi.disable();
				}*/
				if(amui_pop && conf.isLast){
					amui_pop.close();
					amui_pop.$popover.remove()
					element.removeData('amui.popover'); 
				}
				
			return _valid;
		},
		/**
		 * 消息格式化
		 **/
		format:function(msg,args){
			//boolean类型的直接返回
			if ($.type(args)  === "boolean") 
				return  msg;
			if (!$.isArray(args)) //不是数组类型的
				args = [args];
			//数组类型的
			$.each(args,function(d, e) {
				msg = msg.replace(RegExp("\\{" + d + "\\}", "g"), e)
			});
			return msg;		
		},

		// 内置的规则。
		rules : {
			"required":{
						rule : function(v) {
							if (v == "" || v.length == 0)
								return false;
							return true;
						},
						msg : $lang_js.customValid.rules.required
					  },
			"number":{
					rule : function(v) {
						return /^-?((\d{1,3}(,\d{3})+?|\d+)(\.\d{1,5})?)$/
								.test(v.trim());
					},
					msg : $lang_js.customValid.rules.number
				},
			"variable":{
					rule : function(v) {
						return /^[A-Za-z_0-9]*$/gi.test(v.trim());
					},
					msg : $lang_js.customValid.rules.variable
				},
			"fields":{
					name : "",
					rule : function(v){
						return /^[A-Za-z]{1}([a-zA-Z0-9_]{1,17})?$/gi.test(v.trim());
					},
					msg : $lang_js.customValid.rules.fields
				},
			"minlength":{
					rule : function(v, b) {
						return (v.length >= b);
					},
					msg : $lang_js.customValid.rules.minLength
				}, 
			"maxlength":{
					rule : function(v, b) {
						return (v.trim().length <= b);
					},
					msg : $lang_js.customValid.rules.maxLength
				},
			"rangelength":{
					rule : function(v, args) {
						return (v.trim().length >= args[0] && v.trim().length <= args[1]);
					},
					msg : $lang_js.customValid.rules.rangeLength
				},
			"email":{
					rule : function(v) {
						return /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i
								.test(v.trim());
					},
					msg : $lang_js.customValid.rules.email
				},
			"url":{
					rule : function(v) {
						return /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i
								.test(v.trim());
					},
					msg : $lang_js.customValid.rules.url
				},
			 "date":{
					rule : function(v) {
						var re = /^[\d]{4}-[\d]{1,2}-[\d]{1,2}\s*[\d]{1,2}:[\d]{1,2}:[\d]{1,2}|[\d]{4}-[\d]{1,2}-[\d]{1,2}|[\d]{1,2}:[\d]{1,2}:[\d]{1,2}$/g
								.test(v.trim());
						return re;
					},
					msg : $lang_js.customValid.rules.date
				},
			"digits":{
					rule : function(v) {
						return /^\d+$/.test(v.trim());
					},
					msg : $lang_js.customValid.rules.digits
				},
			"equalto":{
					rule : function(v, b) {
						var a = $("#" + b).val();
						return (v.trim() == a.trim());
					},
					msg : $lang_js.customValid.rules.equalTo
				}, 
			"range":{
					rule : function(v, args) {
						return v <= args[1] && v >= args[0];
					},
					msg : $lang_js.customValid.rules.range
				},
			"maxvalue":{
					rule : function(v, max) {
						return v <= max;
					},
					msg : $lang_js.customValid.rules.maxvalue
				},
			"minvalue":{
					rule : function(v, min) {
						return v >= min;
					},
					msg :$lang_js.customValid.rules.minvalue
				},
			"maxintlen":{
					// 判断数字整数位
					rule : function(v, b) {
						return (v + '').split(".")[0].replace("/,/g","").length <= b;
					},
					msg : $lang_js.customValid.rules.maxIntLen
				}, 
			"maxdecimallen":{
					// 判断数字小数位
					rule : function(v, b) {
						return (v + '').replace(/^[^.]*[.]*/, '').length <= b;
					},
					msg : $lang_js.customValid.rules.maxDecimalLen
				},
			"daterangestart":{
					rule : function(v, b,e) {
						return daysBetween(b.targetVal, v); 
					},
					msg : $lang_js.customValid.rules.dateRangeStart
				}, 
			"daterangeend":{
					rule : function(v,b,e) {
						return daysBetween(v, b.targetVal);
					},
					msg : $lang_js.customValid.rules.dateRangeEnd
				},
			"empty":{
					// 空的字段（永远通过验证,返回true）  防止在验证JSON中出现有多余的逗号
					rule : function(v, b) {
					//	var start = $("#" + b).val();
						return true;
					},
					msg : ""
				},
			"nodigitsstart":{
					// 不能以数字开头
					rule : function(v) {
						return !/^(\d+)(.*)$/.test(v.trim());
					},
					msg : $lang_js.customValid.rules.noDigitsStart
				},
			"varirule":{
					name : "varirule",
					rule : function(v) {

						return /^[a-zA-Z]\w*$/.test(v.trim());
					},
					msg : "只能为字母开头,允许字母、数字和下划线"
				}
		}
	});

})(jQuery);
						
function daysBetween(DateOne,DateTwo)
{
	 var date1 =  new Date(DateOne).getTime();
	 var date2 = new Date(DateTwo).getTime();;
       if(date1>date2){
           return false;
       }else{
           return true;
       }
};
