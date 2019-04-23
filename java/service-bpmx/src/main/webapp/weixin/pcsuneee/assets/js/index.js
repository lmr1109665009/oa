//平台、设备和操作系统
var request = 
{ 
QueryString : function(val) 
{ 
var uri = window.location.search; 
var re = new RegExp("" +val+ "=([^&?]*)", "ig"); 
return ((uri.match(re))?(uri.match(re)[0].substr(val.length+1)):null); 
} 
}
var sessionId = request.QueryString("sessionId") == null ? '' : request.QueryString("sessionId");

    var sUserAgent = navigator.userAgent.toLowerCase();
    var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
    var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
    var bIsMidp = sUserAgent.match(/midp/i) == "midp";
    var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
    var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
    var bIsAndroid = sUserAgent.match(/android/i) == "android";
    var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
    var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
    if (!(bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM) ){
	
	
	// console.log("pc")
	$(document).ready(function() {
		$(".panel_wapper_body .am-header-fixed").hide();
		$(".panel_wapper_body").css({"padding-top":"10px","padding-bottom":"0"});
		$(".panel_wapper").css({"top":"10px","padding-bottom":"70px"});
	
	    $('.panel a').on('click', function (e) {
		  var href = $(this).attr('datapc');
		  $(this).attr('href', __ctx + href.split("sessionId=")[0] + "sessionId=" + sessionId);
		  location.href = __ctx + href.split("sessionId=")[0] + "sessionId=" + sessionId;
		 });
	    
	});
} else {  
	$(document).ready(function() {
	$(".panel_wapper_body").css({"padding-top":"70px","padding-bottom":"10px"});
	$(".panel_wapper").css({"top":"70px","bottom":"0px"});
	
	
    $('.panel a').on('click', function (e) {
		  var href = $(this).attr('datawap');
		  $(this).attr('href', __ctx + href.split("sessionId=")[0] + "sessionId=" + sessionId);
		  location.href = __ctx + href.split("sessionId=")[0] + "sessionId=" + sessionId;
		 });
    
	});
}


