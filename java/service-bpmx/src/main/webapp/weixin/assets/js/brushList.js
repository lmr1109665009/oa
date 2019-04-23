document.writeln("   <div class=\'brushListBox\'>   ");
document.writeln("   <div class=\'brushList\'>职位类型</div>");
document.writeln("   <ul class=\'item-ul\'>");
document.writeln("   <li class=\'item-li\'><div class=\'a\'><span>总监以上</span></div></li>");
document.writeln("   <li class=\'item-li\'><div class=\'a\'><span>总监级</span></div></li>");
document.writeln("   <li class=\'item-li\'><div class=\'a\'><span>管理人员</span></div></li>");
document.writeln("   <li class=\'item-li\'><div class=\'a\'><span>基层人员</span></div></li>");
document.writeln("   </ul>");
//document.writeln("   <div class=\'bigButtonBox\'>	    ");
//document.writeln("	<button type=\'button\' class=\'bigButton\' id=\'brushGet\'>确定</button>");
//document.writeln("	</div>");
document.writeln("   </div>");

//获取列表	
	function getBrushData(key){
		var url= searchUrl;
		var data = {
		          page: 1,
		          pageSize: 20,
		          Q_subject_SUPL:key
		};
		var appB = new EventsList(null, {
		    ajax:"post",
	        url: url,
	        params: data
	    });
		appB.init();
	}
	
	$(document).ready(function(){
		var brushArr;
		$('#brushList').click(function(e){
		$(".brushListBox").show();
		
		$(".brushListBox .a").click(function (e) {
			$(".brushListBox .a").removeClass("hover");
			$(this).addClass("hover");
			brushArr=$(this).text();
			getBrushData(brushArr);
			$(".brushListBox").hide() ;
		});	
		
		})
	})