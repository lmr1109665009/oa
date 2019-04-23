var __ctx="/bpmx";

document.write("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
document.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\">");
document.write("<meta name=\"renderer\" content=\"webkit\">");
document.write("<meta http-equiv=\"Cache-Control\" content=\"no-siteapp\" />");
document.write("<link rel=\"icon\" type=\"image/png\" href=\""+__ctx+"/weixin/assets/i/favicon.png\">");
document.write("<meta name=\"mobile-web-app-capable\" content=\"yes\">");
document.write("<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">");
document.write("<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\">");
document.write("<meta name=\"msapplication-TileColor\" content=\"#0e90d2\">");

var aryCss__=["/weixin/assets/css/amazeui.css",
              "/weixin/assets/css/font-awesome.min.css",
            "/weixin/assets/css/app.css"];

var aryJs__=["/weixin/assets/js/jquery.min.js",
            "/weixin/assets/js/amazeui.js",
            "/weixin/assets/js/handlebars.min.js",
            "/weixin/assets/js/WxUtil.js",
            "/weixin/assets/js/headers.js",
            "/weixin/assets/js/LABjs.js"];

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