function toastB(weizhi){
	if($("#toast-container").length > 0){} else {
		var body = $('body');
		var box = $("<div id='toast-container' class='toast-"+weizhi+"'></div>"); 
		box.appendTo(body);}
}
function toast(weizhi,type,txt,id){
	toastB(weizhi); 
	if($("#toast"+id+"").length > 0){} else {
	var div = $("<div class='toast toast-"+type+"' id=toast"+id+"><div class='toast-message'>"+txt+"</div>"); 
	div.appendTo($("#toast-container"));
	}
	setTimeout(function(){toastHide(id)},3000);
}
function toastHide(id){
	$("#toast"+id+"").remove();
}

function salert(txt,id,conferenceId){
	if($("#salert"+id+"").length > 0){} else {
	var div = $('<div  id="salert'+id+'" class="sweet-alert"><h2>'+txt+'</h2>'+
			'<div class="sa-button-container"><button class="cancel" tabindex="2">取消</button>'+
			'<div class="sa-confirm-button-container"><button class="confirm" tabindex="1">确定</button>'+
			'</div></div></div><div class="sweet-overlay"></div>'); 
	div.appendTo($("body"));
	}
	$("#salert"+id+",.sweet-overlay").show();
	$("#salert"+id+" .cancel").on("click", function(){ 
		salertHide(id);
         });
	$("#salert"+id+" .confirm").on("click", function(){ 
		delDataS(conferenceId);salertHide(id);
         });
}
function salertHide(id){
	$("#salert"+id+" ,.sweet-overlay").hide();
}