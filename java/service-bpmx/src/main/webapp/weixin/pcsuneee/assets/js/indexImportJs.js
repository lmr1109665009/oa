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
//平台、设备和操作系统
  var sUserAgent = navigator.userAgent.toLowerCase();
  var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
  var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
  var bIsMidp = sUserAgent.match(/midp/i) == "midp";
  var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
  var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
  var bIsAndroid = sUserAgent.match(/android/i) == "android";
  var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
  var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
  var iVersion= window.sessionStorage.getItem("version");//获取缓存浏览器version
  if (!(bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM)||bIsIpad||iVersion == 'iPad' ){
	// console.log("pc")
	  var aryJs__=[
			"/weixin/pcsuneee/assets/js/jquery.validate.min.js",
			"/weixin/assets/js/headers.js",
			"/weixin/pcsuneee/assets/js/messages_zh.js",
			"/weixin/pcsuneee/assets/js/fullcalendar/moment.min.js",
			"/weixin/pcsuneee/assets/js/fullcalendar/fullcalendar.min.js",
			'/weixin/pcsuneee/assets/js/fullcalendar/zh-cn.js',
			"/weixin/pcsuneee/assets/js/fullcalendar/indexFullcalendar.js",
			"/weixin/pcsuneee/assets/js/fullcalendar/setCalendarPc.js",
			'/weixin/pcsuneee/assets/js/layer/layer.js',
			"/weixin/pcsuneee/assets/js/calendar/My97DatePicker/WdatePicker.js"];	
	$(document).ready(function() {	
		$(".panel_wapper_body .am-header-fixed").hide();
		$(".panel_wapper_body").css("padding-top","10px");		
});	
} else {  
	var aryJs__=[
		"/weixin/pcsuneee/assets/js/jquery.validate.min.js",
		"/weixin/assets/js/headers.js",
		"/weixin/pcsuneee/assets/js/messages_zh.js",
		"/weixin/pcsuneee/assets/js/fullcalendar/moment.min.js",
		"/weixin/pcsuneee/assets/js/fullcalendar/fullcalendar.min.js",
		'/weixin/pcsuneee/assets/js/fullcalendar/zh-cn.js',
		"/weixin/pcsuneee/assets/js/fullcalendar/indexFullcalendar.js",
		"/weixin/pcsuneee/assets/js/fullcalendar/setCalendarMap.js",
		'/weixin/pcsuneee/assets/js/layer/layer.js',
		"/weixin/pcsuneee/assets/js/calendar/My97DatePicker/WdatePicker.js"];	
	$(document).ready(function() {
	$(".panel_wapper_body").css({"padding-top":"70px","padding-bottom":"0"});	
	});
}

  
var aryCss__=["/weixin/pcsuneee/assets/js/fullcalendar/fullcalendar.css"];


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




