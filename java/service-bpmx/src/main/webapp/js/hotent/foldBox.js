/**
 * jquery.scrollFollow.js
 * Copyright (c) 2008 Net Perspective (http://kitchen.net-perspective.com/)
 * Licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
 * 
 * @author R.A. Ray
 *
 * @projectDescription	jQuery plugin for allowing an element to animate down as the user scrolls the page.
 * 
 * @version 0.4.0
 * 
 * @requires jquery.js (tested with 1.2.6)
 * @requires ui.core.js (tested with 1.5.2)
 * 
 * @optional jquery.cookie.js (http://www.stilbuero.de/2006/09/17/cookie-plugin-for-jquery/)
 * @optional jquery.easing.js (http://gsgd.co.uk/sandbox/jquery/easing/ - tested with 1.3)
 * 
 * @param speed		int - Duration of animation (in milliseconds)
 * 								default: 500
 * @param offset			int - Number of pixels box should remain from top of viewport
 * 								default: 0
 * @param easing		string - Any one of the easing options from the easing plugin - Requires jQuery Easing Plugin < http://gsgd.co.uk/sandbox/jquery/easing/ >
 * 								default: 'linear'
 * @param container	string - ID of the containing div
 * 								default: box's immediate parent
 * @param killSwitch	string - ID of the On/Off toggle element
 * 								default: 'killSwitch'
 * @param onText		string - killSwitch text to be displayed if sliding is enabled
 * 								default: 'Turn Slide Off'
 * @param offText		string - killSwitch text to be displayed if sliding is disabled
 * 								default: 'Turn Slide On'
 * @param relativeTo	string - Scroll animation can be relative to either the 'top' or 'bottom' of the viewport
 * 								default: 'top'
 * @param delay			int - Time between the end of the scroll and the beginning of the animation in milliseconds
 * 								default: 0
 */

( function( $ ) {
	$.foldBox = function ( box, options ){ 		
		   var $content=$('.content',$(box));
		   var $button=	$('.drop',$(box));
			if($(box).hasClass(options.searchBox)){ //搜索框
				$content=$('#searchForm',$(box));
				var  insertHtml='<div class="title">查询条件</div><div class="drop"><a>展开</a></div>'
				$(insertHtml).insertBefore( $content ) ;
				var afterShowfn=options.afterShow;
				options.afterShow=function(){afterShowfn();
				$('.drop',$(box)).find('a').text('收起');
				$('.drop',$(box)).find('a').addClass('activi')}
				var afterHidefn=options.afterHide;
				options.afterHide=function(){afterHidefn();
				$('.drop',$(box)).find('a').text('展开');
				$('.drop',$(box)).find('a').removeClass('activi')}
				$button =$('.drop,.title',$(box));
				
				if($.isFunction($.fn.select2)){
					//初始化复选框查询
					$(".js-example-basic-multiple").select2({
						width:"auto"
					});
					$(".js-example-basic-multiple").each(function(){
						var obj = $(this);
						var initData = obj.attr("initData");
						if(initData && initData != ""){
							var valueArray = initData.split(",");
							obj.val(valueArray).trigger('change');
						}
					});
				}
			}
			
			$button.live('click',function(){
				 if( $content.is(':hidden')){
					 options.beforeShow($(box));
					 $content.show();
					 options.afterShow($(box));
					 $.setCookie("isLocked", true);
						if (window.navigator.userAgent.indexOf("MSIE")>=1) 
						{}
						else
						{
					 changeScrollHeight($.getCookie("noNeedFoldHeight"));
						}
				 }else{
					options.beforeHide($(box));
					$content.hide();
					options.afterHide($(box));
					$.setCookie("isLocked", false);
					if (window.navigator.userAgent.indexOf("MSIE")>=1) 
					{}
					else
					{
					changeScrollHeight($.getCookie("noNeedFoldHeight"));
					}
				 }
			});
			if($.getCookie("isLocked")=="true"){
				 $('.drop',$(box)).find('a').text('收起');
				 $('.drop',$(box)).find('a').addClass('activi');
				 $content.show();	
			}
			
	};
	
	$.fn.foldBox = function ( options ){
		options = options || {};
		options.searchBox = options.searchBox || 'panel-search';
		options.beforeShow = options.beforeShow || function($box){};
		options.afterShow = options.afterShow || function($box){};
		options.beforeHide = options.beforeHide || function($box){};
		options.afterHide = options.afterHide || function($box){};
		this.each( function() 
			{
				new $.foldBox( this, options );
			}
		);
		
		return this;
	};
})( jQuery );
function resizeAchangeScrollHeight(){
	$(function(){
		  $(window).resize(function(){
			  var currentThead=$(".table-grid.table-list thead");
			  var currentTable=$(".table-grid.table-list tbody");
			  if (window.navigator.userAgent.indexOf("MSIE")>=1) 
				{//console.log("<ie10");
					}
				
				else{
			  if($(".table-grid.table-list tbody .empty").length){

				}
				else
					{
					$(".table-grid.table-list thead tr th").each(function(index,element){
						$(this).width("");$(this).attr("style",'');
						$(this).width(currentTable.find("tr td:eq("+index+")").width());
						$(this).attr("style",currentTable.find("tr td:eq("+index+")").attr("style"));
					  });
					}
			  }
	    	});
		//判断加载ipad滚动条changeIpadScrollHeight
		  var iPadC = navigator.userAgent.indexOf('iPad') > -1; //是否iPad
		  if (iPadC) {changeIpadScrollHeight();
		  $(".l-panel-bbar-inner").css({
				"position": "relative",
			    "background": "#fff",
			    "padding": "10px",
			    "left": "0",
			    "right": "0"
				  })
		  }
		/*if($(".panel-container").length){}else{
		if($(".panel-search").length){}else{
			if($(".panel-top").length){
				$(".panel-top").append('<div class="panel-search"></div>');
				
			}else
				{
			$(".main_site").prepend('<div class="panel-search"></div>');
				}
		}
		}*/
		$.extend({initFoldBox:function(){
			/**
			 * 初始化更新页面
			 */
			try{
				
				var updateHeight = function(){
				$("div.hide-panel").height($("div.panel-top").height());
				};
				if($(".panel-search .row").length){}else{$(".panel-search").css({
					"position": "relative",
				    "background": "#fff",
				    "margin-top": "0",
				    "padding-top": "0",
				    "padding-bottom": "0px",
				    "border-top": "0"
					  })}
				$('.panel-search').foldBox({afterShow:updateHeight,afterHide:updateHeight});
				$('.foldBox').foldBox();
			if (window.navigator.userAgent.indexOf("MSIE")>=1) 
			{//console.log("<ie10");
				}
			
			else
			{
			//console.log(">ie10");	
				if (iPadC){}else{
				changeScrollHeight(($('.panel-search')[0]&&$('.panel-search').attr("hasScroll")!="true"||$('.foldBox')[0]&&$('.foldBox').attr("hasScroll")!="true")?"":true);
			}}
				}catch(e){}
		}});
		$.initFoldBox();	
	});
}
$(function(){
	resizeAchangeScrollHeight();
});
