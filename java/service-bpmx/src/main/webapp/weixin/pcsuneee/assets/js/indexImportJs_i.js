var __ctx="/bpmx";
var _ctx="/bpmx";
document.write("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
document.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\">");
document.write("<meta name=\"renderer\" content=\"webkit\">");
document.write("<meta http-equiv=\"Cache-Control\" content=\"no-siteapp\" />");
document.write("<meta name=\"mobile-web-app-capable\" content=\"yes\">");
document.write("<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">");
document.write("<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\">");
document.write("<meta name=\"msapplication-TileColor\" content=\"#f1f2f7\">");

var aryCss__=["/weixin/pcsuneee/assets/css/index_i.css"];

var aryJs__=[
	"/weixin/pcsuneee/assets/js/jquery.min.js",
	"/weixin/pcsuneee/assets/js/jquery.validate.min.js",
	"/weixin/assets/js/headers.js",
	"/weixin/pcsuneee/assets/js/messages_zh.js",
	"/weixin/pcsuneee/assets/js/fullcalendar/moment.min.js",
	"/weixin/pcsuneee/assets/js/fullcalendar/fullcalendar.min.js",
	'/weixin/pcsuneee/assets/js/fullcalendar/zh-cn.js',
	"/weixin/pcsuneee/assets/js/fullcalendar/indexFullcalendar_i.js",
	"/weixin/pcsuneee/assets/js/fullcalendar/setindex_i.js",
	'/weixin/pcsuneee/assets/js/layer/layer.js',
	"/weixin/pcsuneee/assets/js/calendar/My97DatePicker/WdatePicker.js"];	

/**
 * js引入时导入必须的css样式。
 */
for(var i=0;i<aryCss__.length;i++){
	var str="<link rel=\"stylesheet\" href=\""+__ctx + aryCss__[i] +"\">";
	document.write(str);
}

/**
 * js引入时导入必须的js文件。
 */
for(var i=0;i<aryJs__.length;i++){
	var str="<script src=\""+__ctx + aryJs__[i] +"\"></script>";
	document.write(str);
}

/**
 * 外部导入的js文件。
 */
function importJs(aryJs){
	for(var i=0;i<aryJs.length;i++){
		var str="<script src=\""+__ctx + aryJs[i] +"\"></script>";
		document.write(str);
	}
}
/**
 * 外部导入css。
 */
function importCss(aryCss){
	for(var i=0;i<aryCss.length;i++){
		var str="<link rel=\"stylesheet\" href=\""+__ctx + aryCss[i] +"\">";
		document.write(str);
	}
}