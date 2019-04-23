
<%@page pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<%@include file="/commons/include/form.jsp" %>
	<style>
	#divTaskContainer{transform-origin: 0 0;}
#divTaskContainerBox{cursor:move; 
    top:0;
    left:0;
    background-color: white;float:left;clear:both;position: relative;}
.divTaskgn{z-index: 11;position: relative;padding: 5px; cursor: pointer;}
</style>
	<script type="text/javascript">
	var _height=${shapeMeta.height};
	function setIframeHeight(){
		var mainIFrame = window.parent.document.getElementById("flowchart");
		if(!mainIFrame)return;
		mainIFrame.style.height=_height+100;
	};
	
	$(function(){
		if(self!=top){
			setIframeHeight();
		}
	});
	
	
	$(function(){	
		var $dibTask= $("#divTaskContainer");
		var TaskContHeight = $dibTask.height();
		var TaskContWidth = $dibTask.width();
		var frameHeight = $(window).height()-110;
		var frameWidth = $(window).width()-40;
		//debugger;
		 console.log("ddd",frameHeight,TaskContHeight,frameHeight/TaskContHeight)
		 console.log(frameWidth,TaskContWidth,frameWidth/TaskContWidth)
		 if(frameWidth/TaskContWidth>1){
			 var scaleVal="1";
		 }
		 else{
			 var scaleVal = frameWidth/TaskContWidth;
		 }
		 $dibTask.attr("scale",scaleVal);
		 $dibTask.css({"transform":"scale("+scaleVal+","+scaleVal+")"});
		 $("#divTaskContainerBox").height( scaleVal*TaskContHeight  );
		 $("#divTaskContainerBox").width( scaleVal*TaskContWidth  );
		 
			/* <button class="divTasksmall">缩小</button>
			<button class="divTaskconst">还原</button>
			<button class="divTaskbig">放大</button> */
			
			
			$(".divTaskimg").click(function(){
				var scale = 1;
				var nub=1;
				$dibTask.attr("scale",scale*nub);
				$dibTask.css({"transform":"scale("+scale*nub+","+scale*nub+")"});
				 $("#divTaskContainerBox").height( frameWidth/TaskContWidth*TaskContHeight*nub  );
				 $("#divTaskContainerBox").width( frameWidth/TaskContWidth*TaskContWidth*nub  );
			});
			
			
			$(".divTasksmall").click(function(){
				var scale = parseFloat($dibTask.attr("scale"));
				var nub=0.7;
				$dibTask.attr("scale",scale*nub);
				$dibTask.css({"transform":"scale("+scale*nub+","+scale*nub+")"});
				 $("#divTaskContainerBox").height( frameWidth/TaskContWidth*TaskContHeight*nub  );
				 $("#divTaskContainerBox").width( frameWidth/TaskContWidth*TaskContWidth*nub  );
			});
			
			$(".divTaskconst").click(function(){
				if(frameWidth/TaskContWidth>1){
					 var scaleVal="1";
				 }
				 else{
					 var scaleVal = frameWidth/TaskContWidth;
				 }
				var scale = parseFloat(scaleVal);
				var nub=1;
				$dibTask.attr("scale",scale*nub);
				$dibTask.css({"transform":"scale("+scale*nub+","+scale*nub+")"});
				 $("#divTaskContainerBox").height( frameWidth/TaskContWidth*TaskContHeight*nub  );
				 $("#divTaskContainerBox").width( frameWidth/TaskContWidth*TaskContWidth*nub  );
				 $("#divTaskContainerBox").css({"left":"0"}).css({"top":"0"});
			});
			
			$(".divTaskbig").click(function(){
				var scale = parseFloat($dibTask.attr("scale"));
				var nub=1.3;
				$dibTask.attr("scale",scale*nub);
				$dibTask.css({"transform":"scale("+scale*nub+","+scale*nub+")"});
				 $("#divTaskContainerBox").height( frameWidth/TaskContWidth*TaskContHeight*nub  );
				 $("#divTaskContainerBox").width( frameWidth/TaskContWidth*TaskContWidth*nub  );
			});

			  })

	$(document).ready(function(){
	    var $div = $("#divTaskContainerBox");
	    /* 绑定鼠标左键按住事件 */
	    $div.bind("mousedown",function(event){
	      /* 获取需要拖动节点的坐标 */
	      var offset_x = $(this)[0].offsetLeft;//x坐标
	      var offset_y = $(this)[0].offsetTop;//y坐标
	      /* 获取当前鼠标的坐标 */
	      var mouse_x = event.pageX;
	      var mouse_y = event.pageY;
	      /* 绑定拖动事件 */
	      /* 由于拖动时，可能鼠标会移出元素，所以应该使用全局（document）元素 */
	      $(document).bind("mousemove",function(ev){
	        /* 计算鼠标移动了的位置 */
	        var _x = ev.pageX - mouse_x;
	        var _y = ev.pageY - mouse_y;
	        /* 设置移动后的元素坐标 */
	        var now_x = (offset_x + _x ) + "px";
	        var now_y = (offset_y + _y ) + "px";
	        /* 改变目标元素的位置 */
	        $div.css({
	          top:now_y,
	          left:now_x
	        });
	      });
	    });
	    /* 当鼠标左键松开，接触事件绑定 */
	    $(document).bind("mouseup",function(){
	      $(this).unbind("mousemove");
	    });
	  })
	  
	</script>
	<title>流程示意图</title>
</head>
<body >
<div style="padding-top:20px;padding-left:20px;">
<b>流程图缩放：</b>
		<button class="divTaskgn divTaskconst">还原</button>
		<button class="divTaskgn divTaskimg">实际大小</button>
		<button class="divTaskgn divTasksmall">缩小</button>
		<button class="divTaskgn divTaskbig">放大</button>
		</div>
	<div id="divTaskContainerBox"  style="padding-top:20px;background-color: white;">
		<div id="divTaskContainer" scale=0.5 style="position: relative;background:url('${ctx}/bpmImage?definitionId=${actDefId}') no-repeat;width:${shapeMeta.width}px;height:${shapeMeta.height}px;">	 
		</div>
	</div>
</body>
</html>