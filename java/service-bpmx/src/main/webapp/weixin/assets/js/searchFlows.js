
importJs(["/weixin/assets/js/pageScrollSearch.js"]);
importCss(["/weixin/assets/css/page.css"]);
var searchUrl=__ctx+"/platform/mobile/mobileTask/searchFlows.ht";

document.writeln(" <div class=\'LC_search\' id=\'mallHead\' style=\'display: ;\'>");
document.writeln("<div class=\'LC_Head\'>");
document.writeln("    <form action=\'\' method=\'get\' class=\'LC_search_frm\' onsubmit=\'return false;\'><!--焦点LC_search_frm_focus-->");
document.writeln("        <input name=\'key\' class=\'LC_search_txt\' placeholder=\'请输入流程名称\' type=\'search\' id=\'topSearchTxt\'>");
document.writeln("        <a class=\'LC_search_clear\' href=\'javascript:;\' id=\'topSearchClear\' style=\'display:none;\'>x</a>");
document.writeln("    </form>");
document.writeln("    <div class=\'LC_qx\'>        ");
document.writeln("        <a href=\'javascript:\' class=\'LC_search_btn\' style=\'display:;\'>取消</a>");
document.writeln("    </div>");
document.writeln("    </div>");
document.writeln("    <div class=\'LC_boxBlock\' style=\'display:;\'>");
document.writeln("    <div>");
document.writeln("<div class=\'LC_row LC_rowS\' id=\'wrapperSearch\' data-am-widget=\'list_news\'");
document.writeln("       class=\'am-list-news am-list-news-default\'>");
document.writeln("    <div class=\'am-list-news-bd\'>");
document.writeln("    	");
document.writeln("      <div class=\'pull-action loading\' id=\'pull-downs\'>");
document.writeln("        <span class=\'am-icon-arrow-down pull-label\'");
document.writeln("              id=\'pull-down-labels\'> 下拉刷新</span>");
document.writeln("        <span class=\'am-icon-spinner am-icon-spin\'></span>");
document.writeln("      </div>");
document.writeln("      <ul class=\'am-list\' id=\'events-lists\'>");
document.writeln("        <li class=\'am-list-item-desced\' style=\'text-align: center;\'>");
document.writeln("          <div class=\'am-list-item-text\'>正在加载内容... </div>");
document.writeln("        </li>");
document.writeln("      </ul>");
document.writeln("      <div class=\'pull-action\' id=\'pull-ups\'>");
document.writeln("        <span class=\'am-icon-arrow-up pull-label\'    id=\'pull-up-labels\'> 上拉加载更多</span>");
document.writeln("        <span class=\'am-icon-spinner am-icon-spin\'></span>");
document.writeln("      </div>");
document.writeln("    </div>");
document.writeln("  </div>");
document.writeln("    </div></div>");
document.writeln("</div>");

$(document).ready(function(){
	$("#wrapperSearch").hide();
	$('#lcListInput').click(function(e){
	$(".LC_search").show() ;
	$(".LC_Head #topSearchTxt").focus();
	$(".LC_Head #topSearchTxt").keyup(function (e) {
		   $(".lists").empty();
		   var key = $(".LC_Head #topSearchTxt").val();
		   if(key==''){return false;}
		   else{
			   var callBack=function(data){
				}
			var appS = new EventsListS(null, {
		        url: searchUrl,
		        dataS: 1,
		        params: {
		          page: 1,
		          startpage: 1,
		          Q_subject_SUPL:decodeURI(key),
		          pageSize: 20,
		          callBack:callBack
		        }
		    });
			$("#wrapperSearch").show();
			appS.init();
			document.addEventListener('touchmove', function(e) {
		          e.preventDefault();
		    }, false);
		   }
	});
	 $('#topSearchTxt').bind('search', function (e) { 
        //var keycode = e.keyCode;  
       // var searchName = $(this).val();  
       // if(keycode=='13') {  
		 e.preventDefault();   
            //请求搜索接口  
        //alert("dd4");
           var key = $(this).val();
  		   if(key==''){return false;}
  		   else{
  			   var callBack=function(data){
  				}
  			var appS = new EventsListS(null, {
  		        url: searchUrl,
  		        dataS: 1,
  		        params: {
  		          page: 1,
  		          startpage: 1,
  		          Q_subject_SUPL:decodeURI(key),
  		          pageSize: 20,
  		          callBack:callBack
  		        }
  		    });
  			$("#wrapperSearch").show();
  			appS.init();
  			document.addEventListener('touchmove', function(e) {
  		          e.preventDefault();
  		    }, false);
  			document.activeElement.blur();//软键盘收起
  			$(this).blur();
  		   }
       // }  
 });  
	
	$('.LC_qx').click(function(e){
	$(".LC_search").hide();
	$(".LC_Head #topSearchTxt").val("")
	$("#events-lists").html("");
	});
	});
})