var setCalendar={
		width:'100%',
		height:'100%',
		selectDate:""       //默认是没有的
};

$(function() {
	$(".layui-form").addClass("layui-form-map"); 
	var cheight=$(window).height()-$('header').height()-20;
	console.log(cheight);
    $('#calendar').fullCalendar('option', 'height', cheight);
	
})

/**
 * burger
 */
$(function() {
	var obj = {}; 
	obj.firstPage = false;
	$(this).removeClass("menu-opened");
	window.parent.postMessage(obj,'*');
	$('.burger').on("click", function(){ 
	    obj.firstPage = true; 
	    window.parent.postMessage(obj,'*');
	    if($(this).hasClass("menu-opened")){$(this).removeClass("menu-opened");}
	    else{ $(this).addClass("menu-opened");}
	    console.log(window.parent.postMessage(obj,'*'))
	    //resultUserInfo()
	    //console.log("resultUserInfo():",resultUserInfo());
	    });
})