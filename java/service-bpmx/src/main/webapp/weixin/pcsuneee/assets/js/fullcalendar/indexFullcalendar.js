//var _ctx="http://172.19.5.124:8081/bpmx";
//var _ctx="http://172.19.6.176:8280/bpmx";
function GetDateDiff(diffTime) {  
    //将xxxx-xx-xx的时间格式，转换为 xxxx/xx/xx的格式   
    var time = diffTime.replace(/\-/g, "/");  
    return time;
};
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

/**
 * burger
 */
$(function() {
	$('.layer-hidden-line').css('pointer-events', 'none');
	var obj = {}; 
	obj.firstPage = false;
	window.parent.postMessage(obj,'*');
	$('.burger').on("click", function(){ 
	    obj.firstPage = true;
	    window.parent.postMessage(obj,'*');
	    });
})


$(function(){
    initFullCalendar();
    //getList('2016-05-28','2017-09-28')
    $('#calendar .fc-left').html("");
    //initForm();
    initDate()
    //changeState();
    
    $('#calendar').fullCalendar('option', 'height', 300);
    $('textarea.layui-input').focus(function(){
    	$(this).removeClass("c9");
    	if($(this).val()=="请输入日程内容"){
    	$(this).val("");
    	$(this).removeClass("c9");
    	}
    	});
})
/*var dataSource=[
				{
					title: '总部会议',
					start: '2017-06-28',
					end: '2017-06-28',
					allDay:true
				},
				{
					title: '临时会议',
					start: '2017-06-09T16:00:00',
					end: '2017-06-11T16:00:00'
				},
				]*/
 //页面加载完初始化日历   
var date = new Date();
var d = date.getDate();
var m = date.getMonth();
var y = date.getFullYear();
//选中事件
var addIndex;
//打开弹出层
function openLayer(date,allDay){
    $("#id").val(null);
    $("#del").hide();
    $('#Form')[0].reset();
    $('textarea.layui-input').addClass("c9");
    $('textarea.layui-input').val("请输入日程内容");
    if(allDay==1){
    	
    	$('#allDayChk').prop("checked",true);
        $('#startTime,#endTime').attr('datefmt',"yyyy/MM/dd");
        $('#startTime').val(new Date(date._d).format("yyyy/MM/dd"));
        }
        else
        	{
            $('#allDayChk').prop("checked",false);
            $('#startTime,#endTime').attr('datefmt',"yyyy/MM/dd HH:mm:ss");
            $('#startTime').val(new Date(date._d).format("yyyy/MM/dd hh:mm:ss"));
        	}
    
    $("label.error").remove();
    	addIndex=layer.open({
        title : '<i class="fa fa-plus"></i>&nbsp;新增日程',
        type : 1,
        skin: 'layer-ext-moon',//样式类名
        
        fix : false,
        // 加上边框
        area : [ setCalendar.width,setCalendar.height ],
        // 宽高
        content : $('.layer-hidden-line'),
        end: function () {
        	$('.layui-layer-shade').hide();
        	$('.layer-hidden-line').css('pointer-events', 'none');
        }
    });
    $('.layui-layer-page').css('pointer-events', 'none');	
setTimeout(function () {
	$('.layer-hidden-line').css('pointer-events', 'auto');
	$('.layui-layer-page').css('pointer-events', 'auto');
},500);
    changeState();
    initForm();    
}
function autofill(data){
	$('#content').val(data.title);
    $('#id').val(data._id);
	console.log(data)
	if(data.end==null){data.end = data.start;}
    if(data.allDay==0){
    $('#allDayChk').prop("checked",false);
    //$('#startTime').val(GetDateDiff(data.start));
    //$('#endTime').val(GetDateDiff(data.end));
    
    $('#startTime').val(new Date(data.startTime).format("yyyy/MM/dd hh:mm:ss"));
    $('#endTime').val(new Date(data.endTime).format("yyyy/MM/dd hh:mm:ss"));
    $('#startTime,#endTime').attr('datefmt',"yyyy/MM/dd HH:mm:ss")
    }
    else
    	{
    	$('#allDayChk').prop("checked",true);
    	$('#startTime').val(new Date(data.startTime).format("yyyy/MM/dd"));
    	var dataend = new Date(data.endTime).format("yyyy/MM/dd");
    	//var dend = new Date(dataend.setDate(dataend.getDate()-1)).format("yyyy/MM/dd");
        $('#endTime').val(dataend);
        $('#startTime,#endTime').attr('datefmt',"yyyy/MM/dd")
    	}
   //console.log(data.allDay);//console.log($('#id').val())
}
function resetData(){
	$('#content,#startTime,#endTime').val(null);
}
function changeState(){
	$("#allDayChk").change(function(){
		   $('#startTime,#endTime').val(null);
		    if( $(this).prop("checked")){//alert("d");
		    	$('#startTime,#endTime').attr('datefmt',"yyyy/MM/dd")
		    }else{
		    	   //alert("df");
		    	   $('#startTime,#endTime').attr('datefmt',"yyyy/MM/dd HH:mm:ss")
		    }    
		    
		});  
}
function openEditLayer(data){
    $("#del").show();
    $('#Form')[0].reset();
    autofill(data);
    	addIndex=layer.open({
        title : '<i class="fa fa-plus"></i>&nbsp;编辑日程',
        type : 1,
        fix : false,
        skin: 'layer-ext-moon',
        
        // 加上边框
        area : [ setCalendar.width,setCalendar.height ],
        // 宽高
        content : $('.layer-hidden-line'),
        cancel: function(index, layero){ 
        	$("label.error").hide();
        	$('.layer-hidden-line').css('pointer-events', 'none');
        	},
        end: function () {
        	$('.layui-layer-shade').hide();
        }
    });	
        $('.layui-layer-page').css('pointer-events', 'none');	
        setTimeout(function () {
        	$('.layer-hidden-line').css('pointer-events', 'auto');
        	$('.layui-layer-page').css('pointer-events', 'auto');
        },500);
    changeState();
    $('textarea.layui-input').removeClass("c9");
    console.log("ddd")
    initForm();    
}

//关闭弹出层
function closeLayer(){
    layer.close(addIndex);
}
/**
 * 把日期转为字符；(yyyy/MM/dd)
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
//初始化日程视图
function initFullCalendar(){
	//console.log()
    $('#calendar').fullCalendar({
    	header: {
			left: '',
			center: 'prev,title,next',
			right: 'month,agendaWeek,agendaDay'
		},
        editable: false,//可以拖动   
/*      firstDay:9,
		navLinks: true, // can click day/week names to navigate views
		selectable: true,
		selectHelper: true,*/
		//aspectRatio:1.98,
		eventLimit: true, // allow "more" link when too many events
/*      timeFormat: 'H:mm',
        axisFormat: 'H:mm',*/
        events: //dataSource
        	function(start,end,timezone,callback){ 
	    	var startDate = _self.changeDateToStr(start._d);
	    	var endDate = _self.changeDateToStr(end._d);
	    	$.ajax({ 
		    	 type:"get", 
		    	 url:queryDataUrl+"?startDate="+startDate+"&endDate="+ endDate, 
		    	 success:function(data){ 
			    	 var event = []; 
			    	 if(data.data){
			    		 event = data.data;
			    		 for(var i=0;i<event.length;i++){
			            		if(event[i].allDay == 1){
			            			var end = event[i].end +" "+"24:00:00";
			            			event[i].end = end;
			            		}
			            	}
				     }
			    	//console.log(data);
			    	 callback(event); 
		    	 } 
	    	 }); 
	    } 
		,		
        dayClick: function(date, allDay, jsEvent, view) { //当单击日历中的某一天时，触发callback
            //var view = $('#calendar').fullCalendar('getView');
            //console.log(date);
            openLayer(date,allDay);
           // $(this).css('background-color', '#f6f6f6');
        },
        eventClick:function(event, jsEvent, view){//当点击日历中的某一日程（事件）时，触发此操作
            openEditLayer(event);
           //console.log(this);console.log(event);
        },
        eventMouseover:function (event, jsEvent, view){//鼠标划过的事件
          ////console.log(event);
        },
        eventMouseout:function( event, jsEvent, view ) { //鼠标离开的事件
            //console.log(event);
        }
        ,
        eventDrop:function( event, dayDelta, minuteDelta, allDay, jsEvent, view ) { //当拖拽完成并且时间改变时触发
           //console.log(event);
        }
    });
}
//进入下一个月视图
function next(){
    $('#calendar').fullCalendar('next'); 
}

//提交表单
function initForm(){
	$().ready(function() {
		$('textarea.layui-input').removeClass("error");
		$('#content-error').remove();
		console.log("erroe")
		// 在键盘按下并释放及提交后验证提交表单
		 $("#Form").validate({
			    debug:true,
			    rules: {
			      title: "required",
			      start: "required",
			      end: {
			        required: true,
			      }
			    },
			    messages: {
			      title: "请输入您的日程内容",
			      start: "请输入您的开始日期",
			      end: {
			        required: "请输入您的结束日期",
			       // equalTo: "您的结束日期要大于你的开始日期！"
			      }
			    },
			    submitHandler:function(){
		            //alert("提交事件!");  
			    	if($('#allDayChk').prop("checked")){
		                $("#allDay").val(1) ;
		                $("#startTime1").val(new Date($("#startTime").val()).format("yyyy-MM-dd"));
		                $("#endTime1").val(new Date($("#endTime").val()).format("yyyy-MM-dd"));
		            }else{
		            	$("#allDay").val(0) ;
		            	$("#startTime1").val(new Date($("#startTime").val()).format("yyyy-MM-dd hh:mm"));
		                $("#endTime1").val(new Date($("#endTime").val()).format("yyyy-MM-dd hh:mm"));
		            }			    	
			    	var obj=$('#Form').serialize();
		           //console.log(obj);
			    	if($('textarea.layui-input').val()=="请输入日程内容"){
			    		console.log($('textarea.layui-input').val())
			    		$('textarea.layui-input').val("").addClass("error");
			    		$('textarea.layui-input').parent().append('<label id="content-error" class="error" for="content">请输入您的日程内容</label>');
			    	    }
			    	else{
		            save(obj);
		            }
		           //console.log(obj);				
		            //alert(obj);  
		            ///form.submit();
		        } 
			    
			});
		});
	
}
//保存日程信息
function save(obj){	
	$.ajax({  
        type : "POST",  //提交方式  
        url : _ctx+"/mh/schedulecalendar/scheduleCalendar/save.ht",
        data : obj,
        success : function(result) {
            if ( result.status==0 ) {    
            	 //console.log(result.data);
            	  closeLayer();
            	  layer.msg(result.message, { time : 2000});
            	  $('#calendar').fullCalendar('refetchEvents'); 
            	  window.location.reload();  	  
            } else {  
            	layer.msg(result.message, { time : 2000});
            }
           
        }  
    }); 
}
//初始化WdatePicker日期控件
function initDate(){
	/**
	 * 前面的日期不能大于后面的日期*/
	
	$('#startTime').on('focus',function(){
		if(navigator.userAgent.indexOf('iPad') > -1){$(this).blur();}
		var me = $(this), dateFmt=  (me.attr('datefmt')?me.attr('datefmt'):'yyyy/MM/dd');
		WdatePicker({dateFmt:dateFmt,maxDate:'#F{$dp.$D(\'endTime\')}'})
		$(this).blur();
	});
	
	$('#endTime').on('focus',function(){
		if(navigator.userAgent.indexOf('iPad') > -1){$(this).blur();}
		var me = $(this), dateFmt=  (me.attr('datefmt')?me.attr('datefmt'):'yyyy/MM/dd');
		WdatePicker({dateFmt:dateFmt,minDate:'#F{$dp.$D(\'startTime\')}'})
		$(this).blur();
	});
}


function delData(){
	
    var id= $("#id").val();//alert("delid",id);
    layer.confirm('您确定删除该日程吗?', function(){ 
        $.ajax({//获取数据
            type : "GET",
            url : _ctx + '/mh/schedulecalendar/scheduleCalendar/del.ht?scheduleId='+id,
            success : function(result) {
            	 if ( result.status==0 ) {  
            		 closeLayer();
            		 layer.msg(result.message, { time : 2000});
                     //方法一
                     $('#calendar').fullCalendar('removeEvents', id)
                     //方法二
                     $('#calendar').fullCalendar('refetchEvents'); 
                 	 //window.location.reload();  	
                 } else {  
                	 layer.msg(result.message, { time : 2000});
                 } 
            }
        });
    });
}
