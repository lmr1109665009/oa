var __ctx="/bpmx";
var _ctx="/bpmx";
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
$.ajaxSetup({  
    url: "" , // 默认URL  
    aysnc: false , // 默认同步加载  
    type: "POST" , // 默认使用POST方式  
    headers: { // 默认添加请求头  
        "sessionId": sessionId,
    }  
 });
var iPad = navigator.userAgent.indexOf('iPad') > -1; //是否iPad
var iVersion= window.sessionStorage.getItem("version");//获取缓存浏览器version
var wapHTtitArr=["我的", "知识管理","会议管理",'会议详情记录'];
var wapHTtitArrAll=["我的","制度管理","新建流程","我的审批","我的流程","知识管理","会议管理","我的日程","假期结余","个人考勤","公司总部","翌捷公司","翌方公司",'会议详情记录'];
var wapHerfArrAll=["/weixin/sea/my.html","/weixin/pcsuneee/index/index.html","/weixin/pcsuneee/startFrame.html",
	"/weixin/pcsuneee/myApprovalFrame.html","/weixin/pcsuneee/myApplyFrame.html?",
	"/weixin/sea/fileCabinet.html","/weixin/suneee/mRoomList.html","/weixin/pcsuneee/flow/calendar.html",
	"/mh/vacationLeft/list.ht","/mh/punched/list.ht",
	"http://suneee.oa.weilian.cn/lizilian/quanjing/jituan/index.html?tit=v11",
	"http://suneee.oa.weilian.cn/lizilian/quanjing/yijie/index.html?tit=v12",
	"http://suneee.oa.weilian.cn/lizilian/quanjing/yifang/index.html?tit=v13",
	'/weixin/suneee/mRoomList.html'];
var iPadTit = request.QueryString("tit") == null ? 'v2' : request.QueryString("tit");
//alert("sessionId:"+request.QueryString("sessionId"));
$(function() {
	//console.log(iPadTit)
if(iPad||iVersion == 'iPad'){
	for(var i=1;i<=wapHTtitArrAll.length;i++){  
		if(iPadTit=="v"+i){
			 var href_title=wapHTtitArrAll[i-1];
			 var href_href=wapHerfArrAll[i-1];
			 sessionStorage.setItem('title',href_title);
			 //console.log(i,wapHTtitArrAll[i-1]);
			    var obj = {}; 
				obj.pageHrefTitle = href_title;
				if(i in [11,12,13]){
					obj.pageHref= href_href;
				}
				else{
				obj.pageHref = 'http://'+window.location.host+"/bpmx/"+href_href+"?sessionId=" + sessionId+'&tit=v'+i;
				}
				window.parent.postMessage(obj,'*');
		}
		else{
			var href_title=window.sessionStorage.getItem("title");	
		}
	} 
		  if(wapHTtitArr.indexOf(href_title)>-1){
			    $(".am-header").addClass("header-type");
			  }else{
			    $(".am-header").removeClass("header-type");
			  }
}
});