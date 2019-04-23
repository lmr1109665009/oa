var HtUtil={};
/**
 * 发送消息给父页面
 */
HtUtil.postM=function(opts,n){
	//console.log("发送消息给父页面",opts,n);
	var openwindow;
	if(n==1){
        openwindow=window.parent
	}
    else if(n==2){
        openwindow=window.parent.parent
    }
    else if(n==3){
        openwindow=window.parent.parent.parent
    }
    else{
        openwindow=window.parent
	}
    openwindow.postMessage(opts, '*');
};

/**
 * 设置缓存。
 */
HtUtil.set=function(key,value){
	localStorage[key]=value;
}

/**
 * 获取缓存
 */
HtUtil.get=function(key){
	return localStorage[key];
}

/**
 * 删除缓存
 */
HtUtil.clean=function(key){
	localStorage.rmStorage(key);
}

/**
 * 设置缓存，value 为JSON对象。
 */
HtUtil.setJSON=function(key,value){
	var json=JSON.stringify(value)
	localStorage[key]=json;
}

/**
 * 根据键获取json对象。
 */
HtUtil.getJSON=function(key){
	var json=localStorage[key];
	if(json==undefined) return null;
	return JSON.parse(json);
}

HtUtil.getParameters=function(){
	var locUrl = window.location.search.substr(1);
	var aryParams=locUrl.split("&");
	var json={};
	for(var i=0;i<aryParams.length;i++){
		var pair=aryParams[i];
		var aryEnt=pair.split("=");
		var key=aryEnt[0];
		var val=aryEnt[1];
		if(json[key]){
			json[key]=json[key] +"," + val;
		}
		else{
			json[key]=val;
		}
		return json;
	}
}

/**
 * 根据参数名称，获取上下文中的参数。
 */
HtUtil.getParameter=function(name){
	var locUrl = window.location.search.substr(1);
	var aryParams=locUrl.split("&");
	var rtn="";
	for(var i=0;i<aryParams.length;i++){
		var pair=aryParams[i];
		var aryEnt=pair.split("=");
		var key=aryEnt[0];
		var val=aryEnt[1];
		if(key!=name) continue;
		if(rtn==""){
			rtn=val;
		}
		else{
			rtn+="," + val;
		}
	}
	return rtn;
}




/**
 * 构造popupDialog
 * @param url 远程访问地址
 * @param dialogId dialog类型 eg:orgDialog,UserDialog
 */
function createPopupDialog(conf,dialogId){
	var title=conf.title || "对话框";
	var iframeId ="dialogId_"+Math.random(1000);
	conf.iframeId = iframeId;
	
	var dialog =$("#"+dialogId);
	 /*已经存在重新创建。手机端不考虑弹出框再弹出*/
	/*var scope = dialog.jiframe[0].contentWindow.getData(); 或者不消除拿到scope恢复回显数据**/
	if(dialog.length>0){
		dialog.remove();
	}
	//将html 添加至页面中
	var w=window.innerWidth;
	var h=window.innerHeight-50;
	var iframe ='<iframe  src="'+conf.url+'" id="'+iframeId+'" style="height:'+h+'px;width:'+w+'px"></iframe>';
	var aryDiv=['<div class="am-modal am-modal-no-btn" tabindex="-1" id="'+dialogId+'">',
	  '<div class="am-modal-dialog">',
	    '<div class="am-modal-hd">',
	    	title,
	      '<a href="javascript: void(0)" class="am-close am-close-spin" data-am-modal-close>&times;</a>',
	    '</div>',
	    '<div class="am-modal-bd">',
	    	iframe ,
	    '</div>',
	  '</div>',
	'</div>']
	
	$("body").append(aryDiv.join(""));
	
	var popupDialog = $("#"+dialogId);
	//赋值
	conf.closeDialog=function(){popupDialog.modal('close')};
	document.getElementById(iframeId).contentWindow.dialogConf=conf;
	popupDialog.modal({width:window.innerWidth,height:window.innerHeight});
}


function Alert(title,content,clickFn){
	var aryHtml=['<div class="am-modal am-modal-alert" tabindex="-1" id="alertDialog">',
	             '<div class="am-modal-dialog">' ,
	             	'<div class="am-modal-hd">'+title+'</div>',
	             		'<div class="am-modal-bd">',
	             		content,
	             		'</div>',
	             		'<div class="am-modal-footer">',
	             			'<span class="am-modal-btn">确定</span>',
	             		'</div>',
	             	'</div>',
	             '</div>']
	
	var html= aryHtml.join("");
	
	var dialogObj=$("#alertDialog");
	if(dialogObj.length>0){
		dialogObj.remove();
	}
	var body=$("body");
	body.append(html)
	var $modal = $('#alertDialog');
	$modal.modal({onClose:function(){
		if(clickFn) clickFn();
	}});
}
/*确认框html*/
var aryHtml=['<div class="am-modal am-modal-prompt" tabindex="-1" id="my-prompt">',
          	  '<div class="am-modal-dialog">',
          	    '<div class="am-modal-hd" id="confirmDialog_Title">',
          	    //	title ,
          	    '</div>',
          	    '<div class="am-modal-bd" name="divConfirm" >',
          	    '<div id="confirmDialog_Content"></div>',
          	    '</div>',
          	    '<div class="am-modal-footer">',
          	      '<span class="am-modal-btn" data-am-modal-cancel>取消</span>',
          	      '<span class="am-modal-btn" data-am-modal-confirm>提交</span>',
          	    '</div>',
          	  '</div>',
          	'</div>'];

//询问框 有input框
function Prompt(title,content,confirmFn,cancelFn){
	var html =content+'<input type="text" class="am-modal-prompt-input">';
	Confirm(title,html,confirmFn,cancelFn);
}
//提示框
function Confirm(title,content,confirmFn,cancelFn){
	var dialogObj=$("#my-prompt");
	if(dialogObj.length==0){
		var body=$("body");
		body.append(aryHtml.join(""));
	}
	if(content){ 
		$("#confirmDialog_Content").html(content);
	}
	
	$("#confirmDialog_Title").text(title);
	
	var $modal = $('#my-prompt');
	$modal.modal({
		relatedTarget: this,
	      onConfirm: function(e) {
	    	  if(confirmFn){
	    		  confirmFn(e);
	    	  }
	      },
	      onCancel: function(e) {
	    	  if(cancelFn){
	    		  cancelFn(e);
	    	  }
	      }
	});
}

$(function(){
	$.ajaxSetup({complete:function(xhr, status){
		if(xhr&&xhr.responseText  ){
			var obj=eval("("+xhr.responseText+")");
			if(obj!=undefined && obj.cause=="nologin"){
				var url=__ctx +"/weixin/login.html";
				
				createPopupDialog({url:url},"loginFormDialog");
			}
		}
	}
	});
});

Date.prototype.Format = function(fmt){ //author: meizz   
  var o = {   
    "M+" : this.getMonth()+1,                 //月份   
    "d+" : this.getDate(),                    //日   
    "h+" : this.getHours(),                   //小时   
    "m+" : this.getMinutes(),                 //分   
    "s+" : this.getSeconds(),                 //秒   
    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
    "S"  : this.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
} 

/**
 * 格式化日期方法。
 */
Handlebars.registerHelper("formatDate",function(value,format){
	format=format || "yyyy-MM-dd";
	var temp=new Date(value) ;
	return temp.Format(format);
});

/**
 * burger
 */
$(function() {
	var obj = {}; 
	obj.firstPage = false;
	$(this).removeClass("menu-opened");
	window.parent.postMessage(obj,'*');
	$('.tab_2,.tab_3,.tab_4 ').on("click", function(){
		window.localStorage.setItem('Number',"1");
	});
	$('.burger').on("click", function(){ 
		window.localStorage.setItem('Number',"2");
	    obj.firstPage = true; 
	    window.parent.postMessage(obj,'*');
	    if($(this).hasClass("menu-opened")){$(this).removeClass("menu-opened");}
	    else{ $(this).addClass("menu-opened");}
	    //resultUserInfo()
	    //console.log("resultUserInfo():",resultUserInfo());
	    });
})
		
function resultUserInfo(){
			parent.postMessage({'request':'userInfo'}, '*');	
		    var resultdata;
			window.addEventListener('message',function(e){
				resultdata = e.data;
				return resultdata
			})
		}

