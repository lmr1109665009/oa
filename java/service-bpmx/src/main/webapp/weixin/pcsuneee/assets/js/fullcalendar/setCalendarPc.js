var setCalendar={
		width:'500px',
	    height:'400px',
	    selectDate:""       //默认是没有的
			};

$(function() {
	var cheight=$(window).height()-$('header').height()-20;
	console.log(cheight);
    $('#calendar').fullCalendar('option', 'height', cheight);
	
})