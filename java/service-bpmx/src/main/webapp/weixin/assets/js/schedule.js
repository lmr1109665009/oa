//判断加载Android或iOS的cordova文件
var u = navigator.userAgent;
var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
///集合取交集  
				    Array.intersect = function () {  
				        var result = new Array();  
				        var obj = {};  
				        for (var i = 0; i < arguments.length; i++) {  
				            for (var j = 0; j < arguments[i].length; j++) {  
				                var str = arguments[i][j];  
				                if (!obj[str]) {  
				                    obj[str] = 1;  
				                }  
				                else {  
				                    obj[str]++;  
				                    if (obj[str] == arguments.length)  
				                    {  
				                        result.push(str);  
				                    }  
				                }//end else  
				            }//end for j  
				        }//end for i  
				        return result;  
				    }  
					Date.prototype.format = function(format){
					    var o = {
					        "M+" : this.getMonth()+1,
					        "d+" : this.getDate(),
					        "h+" : this.getHours(),
					        "m+" : this.getMinutes(),
					        "s+" : this.getSeconds(),
					        "q+" : Math.floor((this.getMonth()+3)/3),
					        "S" : this.getMilliseconds()
					    }
					    
					    if(/(y+)/.test(format))
					    {
					        format=format.replace(RegExp.$1,(this.getFullYear()+"").substr(4 - RegExp.$1.length));
					    }
					    for(var k in o)if(new RegExp("("+ k +")").test(format))
					        format = format.replace(RegExp.$1,RegExp.$1.length==1 ? o[k] :("00"+ o[k]).substr((""+ o[k]).length));
					    return format;
					}
					function StringToDate(DateStr)
					{
						var arr = DateStr.split(/[- : \/]/);
					    var date = new Date(arr[0], arr[1]-1, arr[2], arr[3], arr[4], arr[5]);
					return date;
					} 
					//计算两个日期天数差的函数，通用
						function DateDiff(sDate1, sDate2) {  //sDate1和sDate2日期和时间格式yyyy-MM-dd yyyy-MM-dd HH:ss
						var aDate, oDate1, oDate2, iDays;

							sDate1=new Date(sDate1).format("yyyy/MM/dd");
							sDate2=new Date(sDate2).format("yyyy/MM/dd");
							aDate = sDate1.split("/");
							oDate1 = new Date(aDate[1] + '/' + aDate[2] + '/' + aDate[0]);  //转换为yyyy-MM-dd格式
							aDate = sDate2.split("/");
							oDate2 = new Date(aDate[1] + '/' + aDate[2] + '/' + aDate[0]);
						
						
						if(sDate1>sDate2){
							iDays = -parseInt(Math.abs(oDate2 - oDate1) / 1000 / 60 / 60 / 24);
							//console.log(sDate1,sDate2);
						}
						else iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 / 24); //把相差的毫秒数转换为天数
						return iDays;  //返回相差天数
						}
						//数组去重
						Array.prototype.unique3 = function(){
							 var res = [];
							 var json = {};
							 for(var i = 0; i < this.length; i++){
							  if(!json[this[i]]){
							   res.push(this[i]);
							   json[this[i]] = 1;
							  }
							 }
							 return res;
							}
						
						var schedule = {
				init:function () {
					var windowNumber=window.localStorage.getItem('Number');
					if(windowNumber=='1'){
					//$('.burger').removeClass("menu-opened");
					}
				 }
}
					$(function() {
				    	$('#ca').calendar({
				            data: [
				    		],
				            onSelected: function (view, date, data) {
				                /* var selectedDate = date.Format("yyyy-MM-dd"); */
				                /* getData(date.Format("yyyy-MM-dd")); */
				            },
				            onMonthChanged: function(){
				            	//$('.days li div').css("visibility","hidden");
				            	//getAbnormalDay();
				            }
				        });				   
				/*         $('#dd').calendar({
				            trigger: '#dt',
				            zIndex: 999,
				    		format: 'yyyy-mm-dd',
				            onSelected: function (view, date, data) {
				                //console.log('event: onSelected')
				            },
				            onClose: function (view, date, data) {
				                //console.log('event: onClose')
				                //console.log('view:' + view)
				                //console.log('date:' + date)
				                //console.log('data:' + (data || 'None'));
				            }
				        }); */
				    	getAbnormalDay();
				    	//setAbnormalState();
				    	//点击左右刷新当月数据
				    	 $(document).on("click",".prev,.next", function(){
				    		 $('.days li div').css("visibility","hidden");getAbnormalDay();})
				    		 
					    $(document).on("click",".days li", function(){    
					    	 if($(this).hasClass("now")){
					    		 $(".days li").removeClass('activea');
					    		$(this).addClass("activea");
					    		$(".days .now>i").css({"background":"#0ba9f9","border":"1px solid #0ba9f9","color":"#fff"});
					    		//alert("aa");
					    		getSomeDayData(); 
					    	}else{
					    		if ($(this).hasClass("new") || $(this).hasClass("old")){
					    			$('.days li div').css("visibility","hidden");
					    			getAbnormalDay();
					    		}else{
							    	$(".days li").removeClass('activea');
									$(".days .now>i").css({"background":"#f6f6f6","border":"1px solid #0ba9f9","color":"#333"});
									$(this).addClass("activea");
									getSomeDayData(); 
					    		}
							}	  	 
						})
					})
					var GY={
					goGY: function(n,year,month){	
						  	var dayArr=[];
						  	if(n==-1){	
							if(parseInt(month)==1||month=="01"||month=="1"){
							  		year = (parseInt(year)-1) + "";
							  		month="12"
							  	}
							else { month=(parseInt(month)-1)+ "";};
							
							if (parseInt(month) < 10) {
								month = "0" + month;
							}
							for(var i=23;i<42;i++){
								var day = year+'-'+month+'-'+i;
								 ////console.log("month:",month);
							     dayArr.push(day);
							   }
						  	}
						  	if(n==0){
						  		if (parseInt(month) < 10) {
									month = "0" + month;
								}
								for(var i=1;i<42;i++){
									if (parseInt(i) < 10) {
										i = "0" + i;
									}
									var day = year+'-'+month+'-'+i;
								    dayArr.push(day);
								   }
							  	}
							 else if(n==1){
								 if(parseInt(month)==12||month=="12"||month=="12"){
								  		year = (parseInt(year)+1) + "";
								  		month="1"
								  	}
								 else month=(parseInt(month)+1)+ "";
								 
								 if (parseInt(month) < 10) {
										month = "0" + month;
									}
								 for(var i=1;i<10;i++){
										var day = year+'-'+month+'-0'+i;
									     dayArr.push(day);
									   }
								 for(var i=10;i<14;i++){
										var day = year+'-'+month+'-'+i;
									     dayArr.push(day);
									   } 
							 }
						  	////console.log(dayArr)
						     return dayArr;
						   	}
				    }
				  
					//根据根据当月的日程打点
					function setAbnormalState(day){
				    	var items = $('.days li');
				    	//var day = day;
				    	//console.log(day);
				    	var dayall = [];
				    	if (day!= null){
			        		for(var i=0; i< day.length; i++){
			        				////console.log(DateDiff(day[i].start,day[i].end));
									for(var j=0; j<=DateDiff(day[i].startTime,day[i].endTime); j++){
										dayall.push(new Date(day[i].startTime + 86400000*j).format("yyyy-MM-dd"))
									};	
			        		}
				    	}
				    	dayall=dayall.unique3();
				    	console.log(dayall);
				    	if( dayall && typeof(dayall)!="undefined"){
						    var year = $('.calendar-display').text();
						  	year = year.substr(0,4);
						  	var month = $('.m').text();
						  	month = month.replace("月","");
							var shangDate = Array.intersect(dayall,GY.goGY(-1,year,month));
							var dangDate = Array.intersect(dayall,GY.goGY(0,year,month));
							var xiaDate = Array.intersect(dayall,GY.goGY(1,year,month));
							////console.log(shangDate,dangDate,xiaDate)
						    ////console.log(GY.goGY(-1,year,month),dayall);
							
				    	 $.each( items, function(i, item){	
				    		 if ($(item).hasClass('old')){
				    			 $.each(shangDate, function(i, it){
				   		     		var day = new Date(it).getDate();  
				    			if ($(item).text() == day){
				     				$(item).children("div").css("visibility","visible");
				     			}
				    			 }); 
				    		}
				    		 else if($(item).hasClass('new')){
				    			 $.each(xiaDate, function(i, it){
				    		     		var day = new Date(it).getDate();  
				    		     		 ////console.log($(item).text())
				     			if ($(item).text() == day){
				      				$(item).children("div").css("visibility","visible");
				      			}
				     			 }); 
				    		 }
				    		 else {
				    			
				    			 $.each(dangDate, function(i, it){
				    		     		var day = new Date(it).getDate();  
				    		     		// //console.log($(item).text())
				     			if ($(item).text() == day){
				      				$(item).children("div").css("visibility","visible");
				      			}
				     			 }); 
				    		 }
				    		 
						}); 
				    	}
				    }
					var moonData=[];
					//获取当月的日程列表
					function getAbnormalDay(){
				    	var year = $('.calendar-display').text();
				    	year = year.substr(0,4);
				    	var month = $('.m').text();
				    	month = month.replace("月","");
				    	var daya ='30';
				    	var startDate = year+'-'+month+'-01';
				    	if(['1','3','5','7','8','10','12'].indexOf(month)!=-1){
				    		daya='31'
				    	}
				    	else if(month=='2'){daya ='28';}
				    	else{daya ='30';}
				    	var endDate= year+'-'+month+'-'+daya;

				    	var url = _ctx+"/mh/schedulecalendar/scheduleCalendar/list.ht";
				    	var param="startDate="+startDate+"&endDate="+endDate;
				    	$.get(url,param,function(data){
				    		////console.log("benGY",data);
				    		/* $.each( data, function(i, item){
				        		//var day = new Date(item).getDate();    
				        		setAbnormalState(item);
				    		}); */
				    		/*data=[
				    			{
									title: '临时会议',
									start: '2017-11-01 16:00:00',
									end: '2017-11-11 19:00:00',
									allDay:false
								},
								{
									title: '临时会议',
									start: '2017-12-01 16:00:00',
									end: '2017-12-12 19:00:00',
									allDay:false
								},
								{
									title: '总部会议',
									start: '2017-10-20',
									end: '2017-10-20',
									allDay:true
								},
								{
									title: '总部会gg议',
									start: '2017-10-09',
									end: '2017-10-11',
									allDay:true
								},
								{
									title: '临时会议fff',
									start: '2017-10-09 06:00:00',
									end: '2017-10-09 19:00:00',
									allDay:false
								},{
									title: '临时会议101010fff',
									start: '2017-10-10 06:00:00',
									end: '2017-10-10 19:00:00',
									allDay:false
								},
								{
									title: '临时会议16',
									start: '2017-10-16 06:00:00',
									end: '2017-10-16 19:00:00',
									allDay:false
								},
								{
									title: '临时会议',
									start: '2017-10-13 16:00:00',
									end: '2017-10-13 19:00:00',
									allDay:false
								}
								]*/
				    		moonData = data.data;
				    		setAbnormalState(data.data);
				    		getNowData();
				    	});
				    	
				    }
					//获取当天的日程
					function getNowData(){
				    	//var year = $('.calendar-display').text();
				   	 	//year = year.replace("年", "/");
				   	 	//year = year.replace("月", "/");
				   	 	//var day = $('.now').text();
				   	   // var day2 = $('.activea').text();
				   	    //if(day2){day = year+day2;}
				   	    //else{day = year+day;}
						//alert("date"+date+" "+new Date());
				   	 	getData(moonData,new Date().format("yyyy/MM/dd"));
				   	 	
				    }
					
					//获取某天日程数据
					function getSomeDayData(){
						 var year = $('.calendar-display').text();
				    	 year = year.replace("年", "/");
				    	 year = year.replace("月", "/");
				    	 var day = $('.activea').text();
				    	 day = year+day;
				    	 getData(moonData,day);
				    	 //alert("aa6");
				    	 ////console.log(new Date());
					}
				    
				    //获取某一天的日程数据
				    function getData(data,date){

				    	//先清楚原有数据
				        var today = date;
				        var data = moonData;
				        	var contentHtml = "";
				        	//console.log(data);
				        		for(var i=0; i< data.length; i++){
				        			
				        			//console.log(i,DateDiff(data[i].start,today),DateDiff(data[i].end,today));
				      				
				        			if(DateDiff(data[i].startTime,today)>=0 && DateDiff(data[i].endTime,today)<=0){
				                    
				        			if (data[i].allDay==1){
				        				contentHtml += '<div class="info-div-list" onclick="openEditLayer({startTime:\''+data[i].startTime+'\',id:\''+data[i].id+'\',title:\''+data[i].title+'\',start:\''+data[i].start+'\',end:\''+data[i].end+'\',allDay:\''+data[i].allDay+'\'})"><span class="info-time">全天</span><span class="info-nr">'+data[i].title+'</span></div>';	
				        			}
				        			else if (data[i].allDay==0){
				        				var infotime='';
				        				
				        				//data[i].start=(new Date(data[i].startTime)).format("yyyy-MM-dd hh:mm:ss");
				        				//console.log(data[i].start);
				        				//data[i].end=(new Date(data[i].endTime)).format("yyyy-MM-dd hh:mm:ss");
				        				if(DateDiff(data[i].startTime,today)==0&&DateDiff(data[i].endTime,today)==0){
				        				 contentHtml+= '<div class="info-div-list" onclick="openEditLayer({startTime:\''+data[i].startTime+'\',id:\''+data[i].id+'\',title:\''+data[i].title+'\',start:\''+data[i].start+'\',end:\''+data[i].end+'\',allDay:\''+data[i].allDay+'\'})"><span class="info-time">'+new Date(data[i].startTime).format("hh:mm")+'</span><span class="info-nr">'+data[i].title+'</span></div>';
				        				}
				        				else{
				        				if(DateDiff(data[i].startTime,today)==0){
				        					infotime=(new Date(data[i].startTime)).format("hh:mm");
				        				}
				        				else if(DateDiff(data[i].startTime,today)>0&&DateDiff(data[i].endTime,today)!=0){
				        					infotime='全天';
				        				}
				        				else if(DateDiff(data[i].endTime,today)==0){
				        					infotime=(new Date(data[i].endTime)).format("hh:mm");
				        				}
				        				contentHtml+= '<div class="info-div-list" onclick="openEditLayer({startTime:\''+data[i].startTime+'\',id:\''+data[i].id+'\',title:\''+data[i].title+'\',start:\''+data[i].start+'\',end:\''+data[i].end+'\',allDay:\''+data[i].allDay+'\'})"><span class="info-time">'+infotime+'</span><span class="info-nr">'+data[i].title+'</span></div>';
				        				}
				        			}
				        		} 
				        		}
				        		$('.info-div').html(contentHtml);
				        		
				        		
				    } 
				
$.fn.serializeObject = function()
{
   var o = {};
   var a = this.serializeArray();
   $.each(a, function() {
       if (o[this.name]) {
           if (!o[this.name].push) {
               o[this.name] = [o[this.name]];
           }
           o[this.name].push(this.value || '');
       } else {
           o[this.name] = this.value || '';
       }
   });
   return o;
};

var date = new Date();
var d = date.getDate();
var m = date.getMonth();
var y = date.getFullYear();
function GetDateDiff(diffTime) {  
    //将xxxx-xx-xx的时间格式，转换为 xxxx/xx/xx的格式   
    var time = diffTime.replace(/\-/g, "/");  
    return time;
};

//打开弹出层
function openLayer(date,allDay){
	$("#addScheduleBox .am-header-title span").html("新增日程");
	$('#content,#startTime,#endTime,#allDayChk,button').removeAttr("disabled");
    $('button').removeClass("gray");
    $('button.btn-danger').css("display","inline-block");
    $("#id").val(null);
    $("#del").hide();
    $('#Form')[0].reset();
    //初始化日期控件：
    //$('input[data-role="datebox"]').mobiscroll().date();
    if(allDay){
    	$('#allDayChk').prop("checked",true);      
        $('#startTime').val(new Date(date).format("yyyy/MM/dd"));
        $('input[data-role="datebox"]').mobiscroll(DTPickerOpt.date);
        }
        else
        	{
            $('#allDayChk').prop("checked",false);
            $('#startTime').val(new Date(date).format("yyyy/MM/dd hh:mm"));
            $('input[data-role="datebox"]').mobiscroll(DTPickerOpt.datetime);
        	}
    
    $(".error").empty();
    $('#addScheduleBox').show();
    changeState();
    //initForm();    
}

/**默认配置*/
var DTPickerOpt ={};
var currYear = (new Date()).getFullYear();	
//yyyy-MM-dd 年月日
//初始化日期控件
DTPickerOpt.date = {
		dateFormat: 'yyyy/mm/dd',
		preset:'date',
	//	dateFormat:'yyyy-mm-dd',
		theme: 'android-ics light', //皮肤样式
		display: 'modal', //显示方式 
		mode: 'scroller', //日期选择模式
		lang:'zh',
		startYear:currYear, //开始年份
		minDate: new Date(),//加上这句话会导致startYear:currYear失效，去掉就可以激活startYear:currYear
		endYear:currYear + 5, //结束年份
        
};
//yyyy-MM-dd HH:mm:ss 年月日时分秒
DTPickerOpt.datetime = {
		preset:'datetime',
		dateFormat: 'yyyy/mm/dd',
		timeFormat: 'HH:ii:ss',
        timeWheels: 'HHiiss',
		//	dateFormat:'yyyy-MM-dd HH:mm:ss',
		theme: 'android-ics light', //皮肤样式
		display: 'modal', //显示方式 
		mode: 'scroller', //日期选择模式
		lang:'zh',
		startYear:currYear, //开始年份
		minDate: new Date(),//加上这句话会导致startYear:currYear失效，去掉就可以激活startYear:currYear
		endYear:currYear + 5 //结束年份
};
function autofill(data){
	$('#content').val(data.title);
    $('#id').val(data.id);
    $('#startTime').val(GetDateDiff(data.start));
    $('#endTime').val(GetDateDiff(data.end));
    console.log(data.startTime<new Date().getTime(),data.startTime,new Date().getTime());
    if(data.startTime<new Date().getTime()){
    	$('#content,#startTime,#endTime,#allDayChk,button').attr("disabled","disabled");	
    	$('button').addClass("gray");
    	$('button.btn-danger').css("display","none");
    	return
    }
    else{
    $('#content,#startTime,#endTime,#allDayChk,button').removeAttr("disabled");
    $('button').removeClass("gray");
    $('button.btn-danger').css("display","inline-block");
    if(data.allDay==0){
    $('#allDayChk').prop("checked",false);
    $('input[data-role="datebox"]').mobiscroll(DTPickerOpt.datetime);
    }
    else
    	{
    	$('#allDayChk').prop("checked",true);
    	$('input[data-role="datebox"]').mobiscroll(DTPickerOpt.date);
    	}
    }
}
function resetData(){
	$('#content,#startTime,#endTime').val(null);
}
function changeState(){
	$("#allDayChk").change(function(){
		   //$('#startTime,#endTime').val(null);
		    if( $(this).prop("checked")){//alert("d");
		    	$('input[data-role="datebox"]').mobiscroll(DTPickerOpt.date);
		    }else{
		    	   //alert("df");
		    	$('input[data-role="datebox"]').mobiscroll(DTPickerOpt.datetime);
		    }    
		    
		});  
}
function openEditLayer(data){
    $("#del").show();
    $("#addScheduleBox .am-header-title span").html("日程详情");
    $('#Form')[0].reset();
    autofill(data);
    $('#addScheduleBox').show();
    changeState();
    //initForm();    
}

//关闭日程弹出层
function closeLayer(){
	$('#addScheduleBox').hide();
}
//新建日程弹出层
function addLayer(){
	 var year = $('.calendar-display').text();
	 year = year.replace("年", "/");
	 year = year.replace("月", "/");
	 var day = $('.activea').text();
	 if(day){
		if(new Date(year+day)<new Date(date)){
			day = date;
		} 
		else day = year+day;
		 }
	 else{ day = date;}	 
 
	openLayer(day,false)
    //alert(day)
}


/**
 * 把日期转为字符；(yyyy-mm-dd)
 * 
 */
var _self=this;
var queryDataUrl = _ctx+"/mh/schedulecalendar/scheduleCalendar/list.ht";

this.changeDateToStr=function(date){
	var dateStr = ""
	if(date){
		dateStr = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
	}
	return dateStr
};
//提交表单
function initForm(){ 
	    	if($('#allDayChk').prop("checked")){
                $("#allDay").val(1) ;  
                $("#startTime1").val(new Date($("#startTime").val()).format("yyyy-MM-dd"));
                $("#endTime1").val(new Date($("#endTime").val()).format("yyyy-MM-dd"));
                //alert($("#startTime").val())
            }else{
            	$("#allDay").val(0) ;
                $("#startTime1").val(new Date($("#startTime").val()).format("yyyy-MM-dd hh:mm"));
                $("#endTime1").val(new Date($("#endTime").val()).format("yyyy-MM-dd hh:mm"));
                //alert($("#startTime").val())
            }
	    	
	    	var obj=$('#Form').serialize();
	    	console.log(obj);
	    	if($("#content").val()==''){
	    	$("#content").addClass("error");
	    	//toast('top-right','warning','输入日程内容 ','1');
	    	Alert("提示信息","请输入日程内容");
	    	return
	    	}
	    	if($("#startTime").val()==''){
		    	$("#startTime").addClass("error");
		    	//toast('top-right','warning','输入开始时间 ','2');
		    	Alert("提示信息","请输入开始时间");
		    	return
		    	}
	    	
	    	if($("#endTime").val()==''){
		    	$("#endTime").addClass("error");
		    	//toast('top-right','warning','输入结束时间  ','3');
		    	Alert("提示信息","请输入结束时间");
		    	return
		    	}
	    	if(checkEndTime()){
	    		//alert("aa");
	    		console.log(new Date($("#startTime").val()),new Date($("#endTime").val()),(new Date($("#startTime").val()) >= new Date($("#endTime").val())));
	    		$("#endTime").addClass("error");
		    	//toast('top-right','warning','结束时间要大于开始时间 ','4');
		    	Alert("提示信息","结束时间要大于开始时间");
		    	return
	    	}
            save(obj);	
}
function checkEndTime(){
	var startTime=$("#startTime1").val();
	var start=new Date(startTime.replace("-", "/").replace("-", "/"));
	var endTime=$("#endTime1").val();
	var end=new Date(endTime.replace("-", "/").replace("-", "/"));
	if(end<=start){
	return true;
	}
	return false;
	}
//保存日程信息
function save(obj){	
	$.ajax({  
        type : "POST",  //提交方式  
        url : _ctx+"/mh/schedulecalendar/scheduleCalendar/save.ht",
        data : obj,
        success : function(result) {
            if ( result.status==0 ) {    
            	 ////console.log(result.data);
            	$('.days li div').css("visibility","hidden");
            	getAbnormalDay();
            	closeLayer();
            	  //window.location.reload();  	  
            } else {  
            	console.log(result.message)
            }
           
        }  
    }); 
}

function delData(){
    var id= $("#id").val();//alert("delid",id);
    Alert('确认删除','<br/>您确定删除该日程吗?', function(){ 
        $.ajax({//获取数据
            type : "GET",
            url : _ctx + '/mh/schedulecalendar/scheduleCalendar/del.ht?scheduleId='+id,
            success : function(result) {
            	 if ( result.status==0 ) {  
            		 $('.days li div').css("visibility","hidden");
            		 getAbnormalDay();
                 	 closeLayer();
                 	 //window.location.reload();  	
                 } else {  
                	 console.log(result.message)
                 } 
            }
        });
    });
}
$(function(){
	//初始化
	 schedule.init();
	 $(".am-u-sm-9 input").focus(function(){
			$(this).removeClass("error");
	});
	/* $(window).resize(function(){
 		$('#ca').calendar({
 			
 		});
 	});*/
 })