var windowdq=window.localStorage.getItem('dq');
var windowroom=window.localStorage.getItem('room');
//console.log(windowdq,windowroom);
 //获取地区列表	
function getRegionData(){
	var $tpl = $('#tpi-option-item');
	var tpl = $tpl.text();
	var template = Handlebars.compile(tpl);
	var url=__ctx+"/platform/system/dictionary/getDicByNodeKey.ht?nodeKey=dq";
	$.get(url,function(result){
		if(result.status==0){
		var html = template(result.data);
		$("#doc-select-dq").html(html);
		if(!result.data && typeof(result.data)!="undefined" && result.data!=0){
			////console.log(result.message);  
			return 
			}
		else if(result.data.length!=0){
			var dicId=windowdq||result.data[0].dicId;
			checkRegion(dicId)}
		}
		else {
			//console.log(result.message); 
		}
	})
}

var meetRoomArr = [];
//根据地区获取会议室列表
function getMRoomData(dicId){
	var $tpl = $('#tpi-option-room');
	var tpl = $tpl.text();
	var template = Handlebars.compile(tpl);
	
	var $tpla = $('#doc-room-detail');
	var tpla = $tpla.text();
	var templatea = Handlebars.compile(tpla);
	var url=__ctx+"/me/conference/conferenceRoom/getRegionRoom.ht?region="+ dicId;
	$.get(url,function(result){
		if(result.status==0){
		var html = template(result.data);
		$("#doc-select-room").html(html);
		meetRoomArr = result.data;
		//console.log(result.data); 
		if(!result.data && typeof(result.data)!="undefined" && result.data!=0){
			////console.log(result.message); 
			return 
		}
		else if(result.data.length!=0){
			if(windowroom=="null"){
				var roomId=result.data[0].roomId;
			}
			else{
			var roomId=  windowroom || result.data[0].roomId;}
			
			//console.log(windowroom,roomId);
			//console.log(roomId);
			checkRoom(roomId);
			//console.log(dicId,roomId); 
			var htmla = templatea(result.data[0]);
			$("#doc-room-detailbox").html(htmla);
		}
		}
		else {
			//console.log(result.message); 
		}
	})
}


//获取会议室已预定列表
function getMeetingData(roomId){
		var url=__ctx+"/me/conference/conferenceInfo/getReserveList.ht?roomId="+ roomId;
		$.get(url,function(result){
			if(result.status==0){
				////console.log(JSON.stringify(result.data));
			if(!result.data && typeof(result.data)!="undefined" && result.data!=0){}
				else{ 

					initMeetings(result.data);
					//lunBoDateY();
					
				}
			}
			else {
				//console.log(result.message);  
				return
			}
		})
	}
//获取可用设备列表：
function getSbData(){  
	    checkboxArrText=[];checkboxArr=[];$("#doc-ipt-sb").html("选择你的会议设备");
	    var beginTime=$("#timestart").val();
	 	var endTime=$("#timeend").val();
	    var convokeDate=new Date($("#date").val()).format("yyyy-MM-dd");
	    var region = $("#doc-select-dq").val();
	    var $tpl = $('#tpi-checkbox-sb');
		var tpl = $tpl.text();
		var template = Handlebars.compile(tpl);
		var url=__ctx+"/me/conference/conferenceInfo/getAvailableDevice.ht?convokeDate="+convokeDate+"&beginTime="+beginTime+"&endTime="+endTime+"&region="+region;
		$.get(url,function(result){
			if(result.status==0){
				var html = template(result.data);
			$("#tpi-checkbox-sb-box").html(html);	
			}
			else {
			$("#tpi-checkbox-sb-box").html('<div style="border: 1px solid #eaeaea;color:#f00;padding-left:10px">请先选择你的会议时间!</div>'); 
			}
		})
	}
//获取系统用户信息
  function getUserData(){  
	    var $tpl = $('#tpi-option-user');
		var tpl = $tpl.text();
		var template = Handlebars.compile(tpl);
		var url=__ctx+"/platform/system/sysUser/userList.ht";
		$.get(url,function(result){
			if(result.status==0){
				////console.log(JSON.stringify(result.data));
			var html = template(result.data);
			$(".chosen-select").html(html);	
			$(".chosen-select").chosen({
				  no_results_text: "无法搜索",  
			      });		
			}
			else {
				//console.log(result.message); 
			}
		})
	}
  
var weekdays='一二三四五六日';
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

function weekday(){
            var currentDate = new Date().format("yyyy-MM-dd");
            var currentTime = currentDate.toLocaleString().substring(11);
            var now = new Date();
            now = new Date(now.getTime() + 86400000*6);
            var yyyy = now.getFullYear(), mm = (now.getMonth() + 1).toString(), dd = now
                    .getDate().toString();
            if (mm.length == 1) {
                mm = '0' + mm;
            }
            if (dd.length == 1) {
                dd = '0' + dd;
            }
            var week = now.format("yyyy-MM-dd");
         return week
}
var bookerDate = initDate(new Date().format("yyyy-MM-dd"),weekday());
var bookerIntDate = initIntDate(new Date().format("yyyy-MM-dd"),weekday());

function lowerCaseToUpper(num)
{
    var upperWeekNum;
    if (parseInt(num) == 0)
    {
        upperWeekNum = "日";
    }
    else if (parseInt(num) == 1)
    {
        upperWeekNum = "一";
    }
    else if (parseInt(num) == 2)
    {
        upperWeekNum = "二";
    }
    else if (parseInt(num) == 3)
    {
        upperWeekNum = "三";
    }
    else if (parseInt(num) == 4)
    {
        upperWeekNum = "四";
    }
    else if (parseInt(num) == 5)
    {
        upperWeekNum = "五";
    }
    else if (parseInt(num) == 6)
    {
        upperWeekNum = "六";
    }
    
    return upperWeekNum;
}

//计算天数差的函数，通用 sDate1和sDate2是2002-12-18格式
function DateDiff(sDate1, sDate2)
{ 
    var aDate, oDate1, oDate2, iDays;
    aDate = sDate1.split("-");
    oDate1 = new Date(aDate[0],aDate[1],aDate[2]);//转换为12-18-2002格式 
    aDate = sDate2.split("-");
    oDate2 = new Date(aDate[0],aDate[1],aDate[2]);
    iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 86400);//把相差的毫秒数转换为天数 
    return iDays;
}
function js_strto_time(str_time){
      var new_str = str_time.replace(/:/g,"-");
      new_str = new_str.replace(/ /g,"-");
      var arr = new_str.split("-");
      var datum = new Date(Date.UTC(arr[0],arr[1]-1,arr[2],arr[3]-8,arr[4],arr[5]));
      return strtotime = datum.getTime()/1000;
    }
function ifStepMonth(startTimeStr,endTimeStr)
{
    var startTime = startTimeStr + " 00:00:00";
    var endTime = endTimeStr + " 23:59:59";
    var start = js_strto_time(startTime);
    var end   = js_strto_time(endTime);
    var days = (end - start)/86400;
    return Math.abs(days);
}
function initDate(startTimeStr,endTimeStr)
{
    var differ = ifStepMonth(startTimeStr,endTimeStr);
    var nextDate;
    var localDate= new Date(Date.parse(startTimeStr.replace(/-/g,   "/"))); 
    var currentWeek;
    var dateArr=new Array();
    for(var i=0;i<differ;i++)
    {   
        nextDate=localDate.getTime() + i*24*60*60*1000;
        var declareNextDate=new Date(nextDate);
        currentWeek=declareNextDate.getDay();
        var formatDate=declareNextDate.format("yyyy-MM-dd")+" 周"+lowerCaseToUpper(currentWeek)+"";
//        if(lowerCaseToUpper(currentWeek)== "日"||lowerCaseToUpper(currentWeek)== "六"){ 
//            formatDate=null;
//            }
      dateArr[i]=formatDate;
    }
    dateArr = dateArr.filter(function(n) { return n; });
    return dateArr;
}
function initIntDate(startTimeStr,endTimeStr)
{
    var differ = ifStepMonth(startTimeStr,endTimeStr);
    var nextDate;
    var localDate= new Date(Date.parse(startTimeStr.replace(/-/g,   "/"))); 
    var currentWeek;
    var dateArr=new Array();
    for(var i=0;i<differ;i++)
    {   
        nextDate=localDate.getTime() + i*24*60*60*1000;
        var declareNextDate=new Date(nextDate);
        currentWeek=declareNextDate.getDay();
        var formatDate=declareNextDate.format("yyyy/MM/dd");
//        if(lowerCaseToUpper(currentWeek)== "日"||lowerCaseToUpper(currentWeek)== "六"){ 
//            formatDate=null;
//            }
      dateArr[i]=formatDate;
    }
    dateArr = dateArr.filter(function(n) { return n; });
    return dateArr;
}
function   getDateFromString(strDate)
{
    var  arrYmd = strDate.split("-");
    var  numYear = parseInt(arrYmd[0],10);
    var  numMonth = parseInt(arrYmd[1],10)-1;
    var  numDay= parseInt(arrYmd[2],10);
    var  leavetime=new Date(numYear,numMonth,numDay);
    return leavetime;
}
/**
 * 初始化x轴
 */
function initAxisX()
{
    function number2Time(n)
    {
        var h = Math.floor(n);
        return (h < 10 ? ("0" + h) : h) + ":" + (n - h ? "30" : "00")
    }
    $(".axis-x div").each(function(i, e){
        $(e).html(number2Time(i/2 + 9) + "<br>" + number2Time(i/2 + 9.5));
    });
}
/**
 * 初始化Y轴
 */
function initDateY()
{
   //console.log(bookerDate);
     function gettdItem(i){
        var str='<div class=item>'+i+'</div>';  
        return str
      }
  var cltr=$(".axis-y-date"); 
  for(var i=0;i<bookerIntDate.length;i++){
    var clhtml=gettdItem(bookerDate[i]);
    cltr.append(clhtml);
  };
}

//function lunBoDateY()
//{
// var width = $(window).width();
// var itembox = $('.week-meeting');
// var items = $('.week-meeting').children();
//                var len = items.length;
//                itembox.width(width*len); 
//                $(".container,#meetingBox").width(width);   
//                var index = 0;
//                var str = 0;
//                /*var dotsChild = $('.dot span');*/
//                //点击左侧按钮的函数
//                $(".prev").click(function(){
//                    if(str == 0){
//                        itembox.css({"left": -width*str});
//                        str = len-1;
//                        itembox.css({"left": -width*str});
//                        index = str;
//                        
//                    }else{
//                        itembox.css({"left": -width*str});
//                        str --;
//                        itembox.css({"left": -width*str});
//                        index = str;
//                    }
//                });
//                //点击右侧按钮的函数
//                $(".next").click(function(){
//                    if(str == len-1){
//                        itembox.css({"left": -width*str});
//                        str = 0;
//                        itembox.css({"left": -width*str});
//                        index = str;
//                    }else{
//                        itembox.css({"left": -width*str});
//                        str ++;
//                        itembox.css({"left": -width*str});
//                        index = str;
//                    }
//                });
//}

/**
 * 时间差
 */
function TimeDiffO(sDate1, sDate2){
var reg = /^\s*(\d{1,2})\s*(\:\s*(\d{0,2}))?\s*$/;
var sd = sDate1, ed = sDate2;
                var sh = sd.replace(reg, "$1");
                var sm = sd.replace(reg, "$3");
                var ss = new Date("1111/1/1," + sh + ":" + sm + ":0");
 
                var eh = ed.replace(reg, "$1");
                var em = ed.replace(reg, "$3");
                var es = new Date("1111/1/1," + eh + ":" + em + ":0");
 
                var gaph = Math.floor((es - ss) / 1000 / 60 / 60);
                var gapm = Math.floor((es - ss) / 1000 / 60 % 60);
 
                if(gapm > 0 && gapm <= 30) gapm = 0.5;
                else if(gapm > 30 && gapm < 60) gapm = 1;
                var r = gaph * 1 + gapm * 1;
                r = isNaN(r) ? "" : r;
                return r 
}
/**
 * 时间差
 */
function TimeDiff(sDate1, sDate2){
var date1 = new Date(sDate1);
var date2 = new Date(sDate2);
var s1 = date1.getTime(),s2 = date2.getTime();
var total = (s2 - s1)/1000/3600;
//console.log(total);
return  total
}
  //分钟转化为时间的方法
  function minsTime(i) {
    var h = parseInt((i / 60 ) ^ 0 ) +9;
    var m = (i % 60) ^ 0;
    m = m < 10 ? "0" + m : m;
    return " "+h + ":" + m;
  }
  
var checkboxArr = [];
var checkboxArrText = [];
var arrUser = [];
var arrUserid = [];
var bookers = [];
var  cls = 'cltd'; 
function initMeetings(meetings)
{
	  //var meetings = //meetings  || [] ;
    	//[{"createBy":222,"createtime":1494559092000,"updatetime":1494559092000,"updateBy":1,"conferenceId":10000001370013,"roomId":10000001060019,"convokeDate":1494518400000,"beginTime":"12:00","endTime":"13:00","theme":"ceeghhhs","description":"ggaghfr","devices":"10000001080002,10000001200001","participants":"10000000980003","roomName":"会议室一","convokeDateText":"2017/05/12","devicesText":null,"participantsText":null,"createByName":"超级管理员"}]
    	//JSON.stringify(roomMeetings);
    //console.log(meetings);
    	/* 
    	[{"createBy":1,"createtime":1494259200000,"updatetime":1494259200000,"updateBy":1,"conferenceId":10000001240003,"roomId":10000001060019,"convokeD

ate":1494259200000,"beginTime":"09:00","endTime":"12:00","theme":"会议模块需求评审","description":"会议模块原型图、需求评

审","devices":"10000001080002,10000001200001,","participants":"10000000980001,10000000980005,","devicesList":null,"participantsList":null,"roomNa

me":"会议室一","convoketDateText":null,"createByName":"超级管理员"}]
    	*/

  var opts = {
    start: "9:00",
    limit: 9 * 60,
    renderTo: $('#meeting'),
    ppm: 2,
  }
  $('#meeting').html('');
  var c = $.extend(true, opts);
  var height = bookerIntDate.length * 41;
  var el = $("<div class='week-booker'></div>").height(height);
  var table = $("<div class='week-meeting'></div>").appendTo(c.renderTo);
  c.width = (c.limit) * c.ppm;
  //window.meetings = window.meetings || []; 
  //window.meetings.push(meetings);
  for(var j=0;j<bookerIntDate.length;j++)
    {   
    var ela = $('<div class=container style=z-index:1'+j+'>').addClass(c.line % 2 ? "odd" : "even");
      c.el && ela.appendTo(c.el);
      var clheight = parseInt((c.limit)/60 * c.ppm);
      //console.log(typeof(clheight)); 
      var cltr=$('<div class="booker booker'+j+'"></div>');  
      var clul=$('<div class=bookerdbox date='+bookerIntDate[j]+'></div>'); 
      var cls = ''; 
            for(var i=0;i<clheight;i++){   
            var date1 = minsTime(i*30); 
            var date2 = minsTime(i*30+30);
	            if(j==0){
	           	var todayTime =  new Date();
	           	var todayTimea = todayTime.getHours() +':'+todayTime.getMinutes();   
	            	var todayTimeDiff = parseFloat(TimeDiffO(c.start, todayTimea))* c.ppm;
	           	    console.log(todayTimea,c.start,todayTimeDiff);
	            	if(i<todayTimeDiff){ 
	            		 cls = 'disabled'; 
	            	}
	            	else cls = ''; 
	            }
            var clhtml='<span date1='+date1+' date2='+date2+' i='+i+'  class="'+cls+' cltd" ></span>';
            clul.append(clhtml);
          };
    cltr.append(clul);
  if(meetings.length>0){
        for(var b=0;b<meetings.length;b++){
            var day = meetings[b].convokeDateText;
            ////console.log(":"+day);  
            var m_proposer= meetings[b].createByName;
            var intday = bookerIntDate.indexOf(day);
            var ul=$("<div class=bookerdul id=bookerd"+ j +b+"></div>");  
            var clhtml;
            var tipbox=$('<span class="disabled disabledd cltd" title='+ m_proposer+' >'+ m_proposer+'</span>');
            if(intday==j){
            var meettimeS=meetings[b].beginTime;
            var meettimeE=meetings[b].endTime;
            //console.log(meettimeS,meettimeE)
 
            var daystart = c.start;
            var intTimeD =parseFloat(TimeDiffO(daystart, meettimeS))* c.ppm;
            var intTimeDiff =parseFloat(TimeDiffO(daystart, meettimeE))* c.ppm;
            //console.log(' j:'+intday+' b:'+b+' day:'+day+" intTimeD"+intTimeD+" intTimeDiff"+intTimeDiff);
              for(var i=0;i<clheight;i++){   
               if(intTimeD > i){
               cls = 'cltd'; 
               clhtml='<span class="'+ cls +' cltd"></span>';ul.append(clhtml);}
                if(intTimeD <= i && i < intTimeDiff){  
//                	  cls = 'disabled';
//                    var date1 = minsTime(i*30); 
//                    var date2 = minsTime(i*30+30);
//                    clhtml='<span class="'+ cls +' cltd"></span>'
//                    tipbox.append(clhtml);  
//                    ul.append(tipbox);   
                	if(i == intTimeDiff-1) {
                    	//console.log(50*(parseFloat(intTimeDiff-intTimeD))-1);
                    	tipbox.width(50*(parseFloat(intTimeDiff-intTimeD))-1); 
                        ul.append(tipbox);    
                        }
                	if (intTimeDiff==18){
                		tipbox.css("border-right","0");              		
                	}
              }
            else { }            
          };
          cltr.append(ul);
        }
        }
        }
       
      // $("<div class='axis-y-date'></div>").appendTo(ela)
      var tablea = $("<div class='cltable'></div>").appendTo(ela);
      tablea.append($(cltr));
      table.append(ela);
}
 
  }
 


$(function() {
	// $(".mroom-meeting").height($(window).height()-60);
	initAxisX();initDateY();
	getRegionData();checkRegion(windowdq);getUserData();
    $(document).on("click",".bookerdbox .cltd:not(.disabled)", function()
    {
    var activeNArr=new Array();
    var activeArr=new Array();  
    var activeArrend=new Array();  

    this.activeArrendSE = function()
        { 
    var active = $(this).parent().find(".active");
    $(this).parent().find(".active").each(function(i){
    activeArr[i] =$(this).attr('date1');
    activeArr[active.length] =$(this).attr('date2');
    })
    activeArr = activeArr.filter(function(n) { return n; });
    activeArrend.push(activeArr.shift(),activeArr.pop());
    $("#date").val($(this).parent().attr('date'));   
    $("#timestart").val(activeArrend[0]);
    $("#timeend").val(activeArrend[1]) ;
    return activeArrend;
        }
    this.activeNArrSE = function()
        {  
    var active = $(this).parent().find(".active");
    $(this).parent().find(".active").each(function(i){
    activeNArr[i]=$(this).attr('i');
    })
    return activeNArr;
        }
if($(this).hasClass("active")){    
            {     
            this.activeNArrSE(); 
            var aaa= parseInt($(this).attr('i')) +"";           
            if(aaa==this.activeNArrSE()[0] || aaa ==this.activeNArrSE()[this.activeNArrSE().length-1] ){ 

             $(this).removeClass("active");

             if(aaa==this.activeNArrSE()[0]){var bbb= this.activeNArrSE().shift();
                this.activeArrendSE()}
             else{
                var bbb= this.activeNArrSE().pop();
                this.activeArrendSE();
             }   
             getSbData();
             }   
            }

         }
        else{           
                         
            $(this).parent().parent().parent().parent().siblings().find(".cltd").removeClass('active');
           // $(this).addClass("active");
            this.activeNArrSE();
            var leftN = parseInt($(this).attr('i'))-1+"";
            var rightN = parseInt($(this).attr('i'))+1+"";

            ////console.log(leftN,rightN);
            var aa=this.activeNArrSE().indexOf(leftN);
            var bb= this.activeNArrSE().indexOf(rightN);
            // //console.log(aa,bb);
            
            if(aa>=0 || bb >=0 || this.activeNArrSE().length==0){  
                 $(this).addClass("active");
                this.activeArrendSE();getSbData();
            }
            else { 
              
              $(".cltd").removeClass('active');
              $(this).addClass("active");
              this.activeArrendSE();getSbData();
            }         
          }  
          	
});
      
    $('#doc-ipt-sb').on("click", function(){ 
        $(".mroom-checkbox").show();
        getSbData();
         });  
    
    $('.chosen-select').on("click", function(){ 
      getUserData()
         }); 
    
    $(document).on("click",".am-checkboxli", function(){
    	var text =$(this).find('span').html();
    	var sbId =$(this).attr("id");
    	if($(this).hasClass("active")){
    		$(this).removeClass('active'); 
    		 checkboxArrText.splice($.inArray(text,checkboxArrText),1);
    		 checkboxArr.splice($.inArray(sbId,checkboxArr),1);
    	} else{
    		$(this).addClass("active");
    		checkboxArrText.push(text);
    		checkboxArr.push(sbId);
    	}
    	//"#am-checkboxli"
    	//console.log(checkboxArr,checkboxArrText);
    	
    	getSbD();
})

	$(document).bind("click",function(e){ 
	var target = $(e.target); 
	if(target.closest("#meetingSb").length == 0){ 
	$(".mroom-checkbox").hide(); 
	} 
	}) 
	$("#doc-ipt-them").focus(function(){
		$(this).removeClass("am-field-error");
		});
	    $("#doc-ipt-sb").click(function(){
	    	$(this).removeClass("am-field-error");
	    	});      
}); 

function getSbD(){	 
	var str = '';
	for(var i=0;i<checkboxArrText.length;i++){
	 if(i==checkboxArrText.length-1){ str += checkboxArrText[i];} else {str += checkboxArrText[i] + '、';}
	}
	if(checkboxArrText.length==0){
		 $("#doc-ipt-sb").html("选择你的会议设备");
		 $("#doc-ipt-sb").addClass("c9");
	 }
	 else {
		 $("#doc-ipt-sb").html(str);
		 $("#doc-ipt-sb").removeClass("c9");	 
	 }
	}
	
	
  
 function checkRegion(dicId){
		var dicId = dicId || $("#doc-select-dq").val();
		$("#doc-select-dq").val(dicId);
		if(!dicId && typeof(dicId)!="undefined" && dicId!=0){
			 return 
			}
			else{getMRoomData(dicId);}
		
	}
function checkRoom(roomId){
		var roomId= roomId||$("#doc-select-room").val();
		 $("#doc-select-room").val(roomId);
		//console.log("checkRoom:",roomId,$("#doc-select-room").val())
		//console.log(meetRoomArr);
		for(var r=0;r<meetRoomArr.length;r++){  
			if( roomId == meetRoomArr[r].roomId){
				var $tpla = $('#doc-room-detail');
				var tpla = $tpla.text();
				var templatea = Handlebars.compile(tpla);
				
				var htmla = templatea(meetRoomArr[r]);
				$("#doc-room-detailbox").html(htmla);
			}	
		}
		$("#timestart,#date,#participants").val('');
		getMeetingData(roomId);
		//console.log("room:",$("#doc-select-dq").val(),$("#doc-select-room").val())
	}	

function saveData(){	
	$("#participants").val(arrUserid) ;
	$("#devices").val(checkboxArr) ;
	$("#date").val(new Date($("#date").val()).format("yyyy-MM-dd"));
	//console.log($('#form1').serialize());
	////console.log($("#date").val());
	if($("#doc-ipt-them").val()==''){$("#doc-ipt-them").addClass("am-field-error"); toast('top-right','error','输入你的会议主题','1');return}
	if($("#timestart").val()==''){toast('top-right','error','选择你的会议时间','2'); return  }
	//if(checkboxArr.length==0){$("#doc-ipt-sb").addClass("am-field-error");toast('top-right','error','选择你的会议设备','4'); return  }
	//if(arrUserid.length==0){toast('top-right','error','选择你的参会人员','3'); return  }
	$.ajax({  
        type : "POST",  //提交方式  
        url : __ctx+"/me/conference/conferenceInfo/save.ht",
        data : $('#form1').serialize(),
        success : function(result) {
            if ( status==0 ) {  
            	//alert(JSON.stringify(result.message));
            	//alert(result.data);
            	toast('top-right','success',JSON.stringify(result.message),'success');
            	$("#doc-ipt-them").val('');
            	$("#doc-ta-hyms").val('');
            	setTimeout(function(){window.location.reload();},2000);
            } else {  
            	toast('top-right','error',JSON.stringify(result.message),'error');
            } 
            window.localStorage.setItem('dq',$("#doc-select-dq").val());
            window.localStorage.setItem('room',$("#doc-select-room").val());
        }  
    });  
}	
