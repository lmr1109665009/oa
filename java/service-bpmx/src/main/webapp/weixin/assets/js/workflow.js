var index = {
				init:function () {
					index.loadMyWFNub();//条数
		            //轮播图
					 index.touchSlider();
					 //index.flowListALL();
					 index.changeUser(sessionId);
					 
				var windowNumber=window.localStorage.getItem('Number');
				if(windowNumber=='1'){
				$('.burger').removeClass("menu-opened");
				}
				$('.tb_7 a').on("click", function(){ 
				   window.localStorage.setItem('burger',"black");
			     });  
			   
				},
				
				//切换用户
				 changeUser: function(str) {
				  [].forEach.call($('.tb_7 a,#tabBox2 a'), function (ele) {
				    var href = ele.href;
				    if (/sessionId=/.test(href)) {
				      ele.href = href.split("sessionId=")[0] + "sessionId=" + str;
				    }

				  });
				},
				
				loadMyWFNub :function() {
					//var url = __ctx +"/platform/mobile/mobileTask/processTaskInfo.ht";
					var url = __ctx +"/oa/oaTask/getMyCount.ht";
					$.post(url, function(data){
						/*myCompleteCount	我的办结
						pendingCount	待我审批
						myRequestCount	我的申请
						myGaveToCount	我的加签*/
						//console.log(data.data);
						$('.my-todo em').text("" + data.data.pendingCount + "");
						$('.my-request em').text("" + data.data.myRequestCount + "");
						$('.my-flow-tome em').text("" + data.data.accordingCount + "");
						$('.my-complete-req em').text("" + data.data.myGaveToCount + "");
					});
					$('.my-todo a').attr('href',"../bpm/myToDo.html?sessionId="+sessionId+"");
					$('.my-request a').attr('href',"../bpm/myRequestList.html?sessionId="+sessionId+"");
					$('.my-flow-tome a').attr('href',"../bpm/myTurnOutList.html?sessionId="+sessionId+"");
					$('.my-complete-req a').attr('href',"../bpm/myGaveToList.html?sessionId="+sessionId+"");
				},
				
					
				touchSlider : function (){
		            //轮播图
		            //TouchSlider
					//console.log("dd")
		      		var active=0,
		      		as=$('#pagenavi a');
		      		for(var i=0;i<as.length;i++){
		      			(function(){
		      				var j=i;
		      				as[i].onclick=function(){
		      					t2.slide(j);
		      					return false;
		      				}
		      			})();
		      		}
		      		var t2=new TouchSlider({id:'slider', speed:600, timeout:6000, before:function(index){
		      				as[active].className='';
		      				active=index;
		      				as[active].className='active';
		      			}});
		            //end轮播图
				},
				//一级流程列表
				flowListALL: function(){
				    var url = __ctx + "/platform/system/globalType/getByParentId.ht?parentId=3&catKey=FLOW_TYPE";
				    $.ajax({
				      type: "get",
				      async: false,
				      url: url,
				      dataType: "json",
				     success: function(data){
				    	 //console.log(data);
				    	   var htmlMidALL = '<ul>';
				   			for (var j = 0; j < data.length; j++) {
				   				
				   				if(j%3==0){
				   					htmlMidALL += "</ul><ul><li class='tb_c"+j+"' typeAId="+ data[j].typeId+" typeAName="+ data[j].typeName+"><a href='../suneee/myFlowList.html?typeId="+ data[j].typeId +"&typeName="+ encodeURI(data[j].typeName) +"'><div class='otb'><i></i><span>" +data[j].typeName +"</span></div></a></li>";
				   				}
				   				else
				   					{
				   					htmlMidALL += "<li class='tb_c"+j+"' typeAId="+ data[j].typeId+" typeAName="+ data[j].typeName+"><a href='../suneee/myFlowList.html?typeId="+ data[j].typeId +"&typeName="+ encodeURI(data[j].typeName) +"'><div class='otb'><i></i><span>" +data[j].typeName +"</span></div></a></li>";
				   					}
				   			} 
				   			if(data.length%3==1){
				   				htmlMidALL += "<li></li><li></li>"
				   			}
				   			if(data.length%3==2){
				   				htmlMidALL += "<li></li>"
				   			}
				   			$('#oneWorkFlow').html(htmlMidALL+'</ul>');
				      },
				      error: function(){
				        console.log("没有获取到一级流程列表");
				      }
				    });
				  }
        };
		 $(function(){
    		//初始化
    		index.init();
		 })