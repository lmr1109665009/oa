var setCalendar = {
	width : '500px',
	height : '400px',
	selectDate : "" // 默认是没有的
};
$(function() {
	// var windowHeight=window.sessionStorage.getItem("windowHeight")-60;
	// alert($(window).height())
	if (iPad || iVersion == 'iPad') {
		$('.tit4').remove();
	}
	$('.panelWapperBox').height($(window).height() - 10)
	$('.fc-left').html("我的日程");
	myToDoList();
	/*myRequestList();
	myAcceptedList();
	myCompletedList();*/
	/*newsData("0","个人");*/
	newsData("1","公共");
	/*newsData("2","部门");*/
	meetingData();
	getMessageList();

	//知识管理  默认 公共文件柜
    function newsData(num,title){
    var host = window.location.protocol+'//' + window.location.host ;
	var fileUrl = _ctx + '/me/document/searchFile.ht?sessionId='+sessionId;
	var downloadurl = host + _ctx + '/me/document/download.ht?sessionId='+sessionId;
	var page = 0;
	    // 每页展示5个
	var size = 10;
	$.ajax({
        type:'post',
        url: fileUrl,
        //flag 0为个人 2为部门  1 为公共
        data:{"Q_name_SL":"",flag:num,'page':page,'pageSize':size},
        //data:{id:3},
        dataType: 'json',
        success:function(result) {
            var data = result.results;
            //console.log(data+","+num+","+title);
            var str = '';
            if (data.length == 0) {
                str += "<center style='color:#f60;line-height:40px;'>"+title+"文件柜没有文件！</center>";
            } else {
            	for(var i=0;i<data.length;i++){
            		var upTime = fmtDate(data[i].upTime);
            		if(!(iPad||iVersion == 'iPad')){ 
            			str += '<li><a  target="_blank" href="' + downloadurl +'&path=' + data[i].path +'&id=' + data[i].id + '">'+
            		 '<div class="panel_tit">' + data[i].name + '</div>'+
		                    '<span class="c9">' + upTime + '</span>'+
	                    '</a>'+
	                '</li>'}
            		
            		else{ 
	                	str += '<li onclick=' + 'downloadWJ('+ '"' + downloadurl +'&path=' + data[i].path +'&id=' + data[i].id + '&openIt=true'+'"'+')>'+
	            		 '<div class="panel_tit">' + data[i].name + '</div>'+
			            '<span class="c9">' + upTime + '</span>'+
		                '</li>'
	                }   
            	}
            }
            $('#file_ul').html(str);
        }
    });
    }
    //flag 0为个人 2为部门  1 为公共 tab切换
    $(".titanew").on("click",function(){
    	var title = ["个人","公共","部门"];
    	index = $(this)[0].className.substr($(this)[0].className.length - 1);
		$('.titnew' + index).addClass("cur").siblings().removeClass("cur");
		newsData(index-1,title[index-1]);
    })
	$("#newsUpdate").on("click", function() {
		var arg = $(".tita.cur")[0].className.substring(8, 9);
		var title = ["个人","公共","部门"];
		newsData(arg-1,title[arg-1]);
	})
	// 如果为ios端，下载时进入浏览器
	downloadWJ = function downloadWJ(href) {
		event.stopPropagation();
		var obj = {};
		obj.download = href;
		// console.log(href)
		window.parent.postMessage(obj, '*');
	}
	// 知识箭头跳转
	$("#fileTo").on("click",function() {
				// http://172.19.5.248:8080/bpmx/weixin/pcsuneee/mroomFrame.html?sessionId=

				if (iPad || iVersion == 'iPad') {
					var obj = {};
					obj.pageHrefTitle = "知识管理";
					obj.pageHref = 'http://' + window.location.host
							+ "/bpmx/weixin/sea/fileCabinet.html?sessionId="
							+ sessionId;
					window.parent.postMessage(obj, '*');
					location.href = obj.pageHref;
					sessionStorage.setItem('title', obj.pageHrefTitle);
					sessionStorage.setItem('href', obj.pageHref);
				} else {
					var obj = {};
					obj.pageHrefTitle = "知识管理";
					window.parent.postMessage(obj, '*');
					location.href = "../tabFrame.html?sessionId=" + sessionId;
				}
			})

	// 时间戳转成 yy-mm-dd
	function fmtDate(obj) {
		var date = new Date(obj);
		var y = 1900 + date.getYear();
		var m = "0" + (date.getMonth() + 1);
		var d = "0" + date.getDate();
		return y + "-" + m.substring(m.length - 2, m.length) + "-"
				+ d.substring(d.length - 2, d.length);
	}

	// 会议管理 默认预定记录
	function meetingData() {
		var meetingUrl = _ctx + '/me/conference/conferenceInfo/list.ht';
		var Size = 10;
		var Page = 0;
		$.ajax({
					type : 'post',
					url : meetingUrl,
					data : {
						'currentPage' : Page,
						'pageSize' : Size
					},
					// data:{id:3},
					dataType : 'json',
					success : function(result) {
						var str = '';
						if (result.status == 1) {
							str += '<div style="text-align: center;color:#f60;line-height: 40px">'
									+ result.message + '</div>';
						} else {
							var data = result.data.rowList;
							var detailHref = "http://"
									+ window.location.host
									+ "/bpmx/weixin/pcsuneee/mroom/mRoomDetail_alert.html";
							if (data.length > 0) {
								if (iPad || iVersion == 'iPad') {

									detailHref = "http://"
											+ window.location.host
											+ "/bpmx/weixin/suneee/mRoomDetail_iPad.html";
									// obj.pageHref =
									// "http://"+window.location.host+"/bpmx/weixin/suneee/mRoomDetail_iPad.html?sessionId="
									// +
									// sessionId+"&conferenceId="+conferenceId;
								} else {
									// var obj = {};
									// obj.pageHrefTitle = "会议详情记录";
									detailHref = "http://"
											+ window.location.host
											+ "/bpmx/weixin/pcsuneee/mroom/mRoomDetail_alert.html";
									// window.parent.postMessage(obj,'*');
									// location.href =
									// "../mroom/mRoomDetail_alert.html?sessionId="+sessionId+"&conferenceId="+conferenceId;
								}
							}
							if (data.length == 1) {
								str += '<li>' + '<a class="goMroomD" href="'
										+ detailHref + '?sessionId='
										+ sessionId + '&tit=v14&conferenceId='
										+ data[0].conferenceId + '">'
										+ '<i class="round curg"></i>'
										+ '<div class="panel_tit"><span>'
										+ data[0].roomName + '</span></div>'
										+ '<div class="panel_tit"><span>'
										+ data[0].theme + '</span></div>'
										+ '<span class="c9">'
										+ data[0].convokeDateText + " "
										+ data[0].beginTime + "-"
										+ data[0].endTime + '</span>' + '</a>'
										+ '</li>';
							} else if (data.length == 0) {
								str = "<center style='color:#f60;line-height:40px;'>会议预定记录没有内容！</center>";
							} else {
								str += '<li>' + '<a class="goMroomD" href="'
										+ detailHref + '?sessionId='
										+ sessionId + '&tit=v14&conferenceId='
										+ data[0].conferenceId + '">'
										+ '<i class="round curg"></i>'
										+ '<div class="panel_tit"><span>'
										+ data[0].roomName + '</span></div>'
										+ '<div class="panel_tit"><span>'
										+ data[0].theme + '</span></div>'
										+ '<span class="c9">'
										+ data[0].convokeDateText + " "
										+ data[0].beginTime + "-"
										+ data[0].endTime + '</span>' + '</a>'
										+ '</li>';
								for (var i = 1; i < data.length; i++) {
									str += '<li>'
											+ '<a class="goMroomD" href="'
											+ detailHref
											+ '?sessionId='
											+ sessionId
											+ '&tit=v14&conferenceId='
											+ data[i].conferenceId
											+ '">'
											+ '<i class="round"></i>'
											+ '<div class="panel_tit"><span>'
											+ data[i].roomName
											+ '</span></div>'
											+ '<div class="panel_tit"><span>'
											+ data[i].theme
											+ '</span></div>'
											+ '<span class="c9">'
											+ data[i].convokeDateText
											+ " "
											+ data[i].beginTime
											+ "-"
											+ data[i].endTime
											+ '</span>'
											+ '</a>' + '</li>';
								}
							}
						}
						$('#meeting_ul').html(str);
						if (iPad || iVersion == 'iPad') {
							$("#meeting_ul .goMroomD").on("click", function(e) {
								alert("dd")
								e.preventDefault();
								var obj = {};
								obj.pageHrefTitle = "会议详情记录";
								window.parent.postMessage(obj, '*');
								location.href = $(this).attr('href');
								// console.log(obj)
							})
						}
					}
				})
	}
	
	$("#msgUpdate").on("click",function(){getMessageList();})

	$("#meetingUpdate").on("click", function() {
		meetingData();
	})
	
	// 会议箭头跳转
	$("#meetingTo").on("click",function() {
						// http://172.19.5.248:8080/bpmx/weixin/pcsuneee/mroomFrame.html?sessionId=
						if (iPad || iVersion == 'iPad') {
							var obj = {};
							obj.pageHrefTitle = "会议管理";
							obj.pageHref = 'http://'
									+ window.location.host
									+ "/bpmx/weixin/suneee/mRoomList.html?sessionId="
									+ sessionId;
							window.parent.postMessage(obj, '*');
							location.href = obj.pageHref;
							sessionStorage.setItem('title', obj.pageHrefTitle);
							sessionStorage.setItem('href', obj.pageHref);
						} else {
							var obj = {};
							obj.pageHrefTitle = "会议管理";
							window.parent.postMessage(obj, '*');
							location.href = "../mroomFrame.html?sessionId=" + sessionId;
						}
					})
/*	console.log($(window).width(), $(window).height(),
			($(window).height() - 40) / 2);*/
	if ($(window).width() < 600) {
		$('#calendar').fullCalendar('option', 'height', '');
		$('.panel_body').css("height", 'auto')
	} else if ($(window).width() > 1000) {
		$('#calendar').fullCalendar('option', 'height',
				($(window).height() - 40) / 2);
		$('.panel_body').height(($(window).height() - 40) / 2 - 60)
	} else {
		$('#calendar').fullCalendar('option', 'height',
				($(window).height() - 40) / 2);
		$('.panel_body').height(($(window).height() - 40) / 2 - 60)
	}
	$(window).resize(
			function() {
				//console.log($(window).width(), $(window).height());
				if ($(window).width() < 600) {
					$('#calendar').fullCalendar('option', 'height', '');
					$('.panel_body').css("height", 'auto')
				} else if ($(window).width() > 1000) {
					$('#calendar').fullCalendar('option', 'height',
							($(window).height() - 40) / 2);
					$('.panel_body').height(($(window).height() - 40) / 2 - 60)
				} else {
					$('#calendar').fullCalendar('option', 'height',
							($(window).height() - 40) / 2);
					$('.panel_body').height(($(window).height() - 40) / 2 - 60)
				}
			});

	var flowToHref = 'myApprovalFrame.html';
	var title = "我的审批";
	var cond=1;//用来记录默认的选项卡。之前的默认为第一个，若选择的为第二个（即index为3或4），就默认加载第二个。
	$('.tita').on("click", function() {
		index = $(this)[0].className.substr($(this)[0].className.length - 1);
		$('.tit' + index).addClass("cur").siblings().removeClass("cur");
		$('.panel_ul' + index).removeClass("dsn").siblings().addClass("dsn");
		if (index == 1 || index == 3) {
			if(index==3){
				cond=2;
			}else{cond=1;}
			flowToHref = "myApprovalFrame.html";
			title = "我的审批";
		} else {
			if(index==4){
				cond=2;
			}else{cond=1;}
			flowToHref = "myApplyFrame.html"
			title = "我的流程";
		}
		var tabFunc = [myToDoList, myRequestList,myAcceptedList, myCompletedList]
		tabFunc[index - 1]();
	});

	$("#flowTo").on("click",
			function() {
				if (iPad || iVersion == 'iPad') {
					var obj = {};
					obj.pageHrefTitle = title;
					// obj.pageHref =
					// 'http://'+window.location.host+"/bpmx/weixin/pcsuneee/myApprovalFrame.html?sessionId="
					// + sessionId;
					obj.pageHref = 'http://' + window.location.host
							+ "/bpmx/weixin/pcsuneee/" + flowToHref
							+ "?sessionId=" + sessionId;
					window.parent.postMessage(obj, '*');
					location.href = obj.pageHref;
					sessionStorage.setItem('title', obj.pageHrefTitle);
					sessionStorage.setItem('href', obj.pageHref);
				} else {
					var obj = {};
					obj.pageHrefTitle = title;
					window.parent.postMessage(obj, '*');
					location.href = 'http://' + window.location.host
							+ "/bpmx/weixin/pcsuneee/" + flowToHref
							+ "?sessionId=" + sessionId+"&cond="+cond;
				}

			})

	$("#titaUpdate").on("click", function() {
		var arg = $(".tita.cur")[0].className.substring(8, 9);
		var tabFunc = [ myToDoList, myRequestList,myAcceptedList, myCompletedList]
		tabFunc[arg - 1]();
	})

})

// 待我审批列表
function myToDoList() {
	var url = __ctx
			+ "/mh/sework/myTaskListFront.ht?startFromMobile=1&pageSize=10&page=1";
	$.ajax({
				type : "get",
				async : false,
				url : url,
				dataType : "json",
				success : function(data) {
					if (undefined != data.rowList && data.rowList != null
							&& data.rowList.length > 0) {
						var data = data.rowList;
						var htmlMidALL = '<ul>';
						for (var j = 0; j < data.length; j++) {
							if (iPad || iVersion == 'iPad') {
								htmlMidALL += "<li><a target= '_top' href='/bpmx/platform/bpm/task/toStart.ht?taskId="
										+ data[j].id
										+ "' title='"
										+ data[j].subject
										+ "'>"
										+ "<div class='panel_tit'><span class=''>"
										+ data[j].subject
										+ "</span></div>"
										+ "<span class='c9'>发起人："
										+ data[j].creator + "</span></a></li>";
							} else {
								htmlMidALL += "<li><a target= '_blank' href='/bpmx/platform/bpm/task/toStart.ht?taskId="
										+ data[j].id
										+ "' title='"
										+ data[j].subject
										+ "'>"
										+ "<div class='panel_tit'><span class=''>"
										+ data[j].subject
										+ "</span></div>"
										+ "<span class='c9'>发起人："
										+ data[j].creator + "</span></a></li>";
							}
						}
						$('.panel_ul1').html(htmlMidALL + '</ul>');
					} else {
						$('.panel_ul1').html("<center style='color:#f60;line-height:40px;'>暂无数据</center>");
					}
				},
				error : function() {
					console.log("没有获取待我审批列表");
				}
			});
}
// 我的申请
function myRequestList() {
	var url = __ctx
			+ "/weixin/bpm/myRequestListJson.ht?startFromMobile=1&pageSize=10&page=1";
	$.ajax({
				type : "get",
				async : false,
				url : url,
				dataType : "json",
				success : function(data) {
					if (undefined != data.rowList && data.rowList != null
							&& data.rowList.length > 0) {
						var data = data.rowList;
						var htmlMidALL = '<ul>';
						for (var j = 0; j < data.length; j++) {

							if (iPad || iVersion == 'iPad') {
								htmlMidALL += "<li><a target= '_top' href='/bpmx/mh/sework/info.ht?runId="
										+ data[j].runId
										+ "' title='"
										+ data[j].subject
										+ "'>"
										+ "<div class='panel_tit'><span class=''>"
										+ data[j].subject
										+ "</span></div>"
										+ "<span class='c9'>发起人："
										+ data[j].creator + "</span></a></li>";
							} else {
								htmlMidALL += "<li><a target= '_blank' href='/bpmx/mh/sework/info.ht?runId="
										+ data[j].runId
										+ "' title='"
										+ data[j].subject
										+ "'>"
										+ "<div class='panel_tit'><span class=''>"
										+ data[j].subject
										+ "</span></div>"
										+ "<span class='c9'>发起人："
										+ data[j].creator + "</span></a></li>";
							}
						}
						$('.panel_ul2').html(htmlMidALL + '</ul>');
					} else {
						$('.panel_ul2').html("<center style='color:#f60;line-height:40px;'>暂无数据</center>");
					}
				},
				error : function() {
					console.log("没有获取我的申请列表");
				}
			});
}

// 我已审批
function myAcceptedList() {
	var url = __ctx
	+ "/weixin/bpm/getAlreadyMattersList.ht?startFromMobile=1&pageSize=10&page=1";
$.ajax({
		type : "get",
		async : false,
		url : url,
		dataType : "json",
		success : function(data) {
			if (undefined != data.rowList && data.rowList != null
					&& data.rowList.length > 0) {
				var data = data.rowList;
				var htmlMidALL = '<ul>';
				for (var j = 0; j < data.length; j++) {
					if (iPad || iVersion == 'iPad') {
						htmlMidALL += "<li><a target= '_top' href='/bpmx/mh/sework/info.ht?runId="
								+ data[j].runId + "&prePage=myFinishedTask"
								+ "' title='"
								+ data[j].subject
								+ "'>"
								+ "<div class='panel_tit'><span class=''>"
								+ data[j].subject
								+ "</span></div>"
								+ "<span class='c9'>发起人："
								+ data[j].creator + "</span></a></li>";
					} else {
						htmlMidALL += "<li><a target= '_blank' href='/bpmx/mh/sework/info.ht?runId="
								+ data[j].runId + "&prePage=myFinishedTask"
								+ "' title='"
								+ data[j].subject
								+ "'>"
								+ "<div class='panel_tit'><span class=''>"
								+ data[j].subject
								+ "</span></div>"
								+ "<span class='c9'>发起人："
								+ data[j].creator + "</span></a></li>";
					}
				}
				$('.panel_ul3').html(htmlMidALL + '</ul>');
			} else {
				$('.panel_ul3').html("<center style='color:#f60;line-height:40px;'>暂无数据</center>");
			}
		},
		error : function() {
			console.log("没有获取我已审批列表");
		}
	});
}

//我的办结
function myCompletedList() {
	var url = __ctx
	+ "/weixin/bpm/getMyCompletedListJson.ht?startFromMobile=1&pageSize=10&page=1";
$.ajax({
		type : "get",
		async : false,
		url : url,
		dataType : "json",
		success : function(data) {
			if (undefined != data.rowList && data.rowList != null
					&& data.rowList.length > 0) {
				var data = data.rowList;
				var htmlMidALL = '<ul>';
				for (var j = 0; j < data.length; j++) {

					if (iPad || iVersion == 'iPad') {
						htmlMidALL += "<li><a target= '_top' href='/bpmx/mh/sework/info.ht?runId="
								+ data[j].runId
								+ "' title='"
								+ data[j].subject
								+ "'>"
								+ "<div class='panel_tit'><span class=''>"
								+ data[j].subject
								+ "</span></div>"
								+ "<span class='c9'>发起人："
								+ data[j].creator + "</span></a></li>";
					} else {
						htmlMidALL += "<li><a target= '_blank' href='/bpmx/mh/sework/info.ht?runId="
								+ data[j].runId
								+ "' title='"
								+ data[j].subject
								+ "'>"
								+ "<div class='panel_tit'><span class=''>"
								+ data[j].subject
								+ "</span></div>"
								+ "<span class='c9'>发起人："
								+ data[j].creator + "</span></a></li>";
					}
				}
				$('.panel_ul4').html(htmlMidALL + '</ul>');
			} else {
				$('.panel_ul4').html("<center style='color:#f60;line-height:40px;'>暂无数据</center>");
			}
		},
		error : function() {
			console.log("没有获取我的办结列表");
		}
	});
}



//未读信息列表
function getMessageList(){
	//http://10.11.85.59:8080/bpmx/platform/system/messageReceiver/getMessageList.ht?currentPage=1&pageSize=10&Auth_Session=zhaowen
	var url = __ctx
	+ "/platform/system/messageReceiver/getMessageList.ht?currentPage=1&pageSize=10&Auth_Session=zhaowen";
$.ajax({
		type : "get",
		async : false,
		url : url,
		dataType : "json",
		success : function(data) {
			data = data.data;
			if (undefined != data.rowList && data.rowList != null
					&& data.rowList.length > 0) {
				var data = data.rowList;
				var htmlMidALL = "";
				for (var j = 0; j < data.length; j++) {
					if (iPad || iVersion == 'iPad') {
						htmlMidALL += "<li onclick='javascript:window.top.showReadMsgDlg("+data[j].id+")'>"+data[j].subject+"</li>";
					} else {
						htmlMidALL += "<li onclick='javascript:window.top.showReadMsgDlg("+data[j].id+")'>"+data[j].subject+"</li>";
					}
				}
				$('#msg_ul').html(htmlMidALL);
			} else {
				$('#msg_ul').html("<center style='color:#f60;line-height:40px;'>暂无数据</center>");
			}
		},
		error : function() {
			console.log("没有获取最新消息列表");
		}
	});
}
