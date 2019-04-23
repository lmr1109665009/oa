$(function() { 	
	var windowBurger=window.localStorage.getItem('burger');
	if(windowBurger=='black'){
	$('.burger').removeClass("burger").addClass("burger-arr");
	$('.burger-arr').attr('onclick',"history.back();");
	}
	$('.burger-arr').click(function(){
		window.localStorage.setItem('Number',"1");
		var obj = {}; 
		obj.firstPage = true;
		window.parent.postMessage(obj,'*');
		}
	)
})