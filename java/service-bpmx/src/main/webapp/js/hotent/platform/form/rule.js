Namespace.register("com.hotent.form.rule");  
com.hotent.form.rule.CustomRules=[
	{
		name:"数字验证",
		rule:function(v){
				return /^\d{6}$/.test(v);
		},
		msg:"日期格式不对"
	},

	{
		name:"IP地址",
		rule:function(v){
				return /^([0-9]{1,3}\.{1}){3}[0-9]{1,3}$/.test(v);
		},
		msg:"IP地址不正确"
	},

	{
		name:"英文字母",
		rule:function(v){
				return /^[a-zA-Z]+$/.test(v);
		},
		msg:"请输入英文字母"
	},

	{
		name:"非负整数",
		rule:function(v){
				return /^(0|[1-9]\d*)$/.test(v);
		},
		msg:"请输入非负整数"
	},

	{
		name:"英数字",
		rule:function(v){
				return /^[a-zA-Z0-9]+$/.test(v);
		},
		msg:"请输入英文字母和数字"
	},

	{
		name:"汉字",
		rule:function(v){
				return /^[\u4E00-\u9FA5]+$/.test(v);
		},
		msg:"请输入汉字"
	},

	{
		name:"负整数",
		rule:function(v){
				return /^-{1}\d+$/.test(v);
		},
		msg:"请输入负整数"
	},

	{
		name:"正整数",
		rule:function(v){
				return /^[1-9]+\d*$/.test(v);
		},
		msg:"请输入正整数"
	},

	{
		name:"整数",
		rule:function(v){
				return /^-?\d+$/.test(v);
		},
		msg:"请输入整数"
	},

	{
		name:"QQ号码",
		rule:function(v){
				return /^[1-9]*[1-9][0-9]*$/.test(v);
		},
		msg:"请输入有效的QQ号码"
	},

	{
		name:"email",
		rule:function(v){
				return /^\w+([-+.]\w+)*@\w+([-.]\w+)*.\w+([-.]\w+)*$/.test(v);
		},
		msg:"email格式输入有误"
	},

	{
		name:"手机号码",
		rule:function(v){
            return /^(((13[0-9]{1})|(18[0-9]{1})|(17[0-9]{1})|(14[0-9]{1})|(15[0-9]{1}))+\d{8})$/.test(v);
		},
		msg:"手机号码输入有误"
	}
];
