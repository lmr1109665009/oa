function changeWrapperC(a){
setTimeout(function () { 
	$('div[type="custform"] .l-text-wrapper').each(function(){
		if($(this).parent().css("text-align")=="center"){
			$(this).css({"margin-left":"auto","margin-right":"auto"});
		}
	})
}, a);
}
$(function(){
	//初始化表单tab
	FormUtil.initTab();
	//初始化日期控件。
	FormUtil.initCalendar();
	//附件初始化
	AttachMent.init();
	//
	ReadOnlyQuery.init();
	
	FormUtil.InitMathfunction();
	//绑定对话框。
	FormUtil.initCommonDialog();
	FormUtil.initCommonQuery();
	FormUtil.initEventBtn();
	
	FormUtil.initExtLink();
	
	QueryUI.init();
	
	// 绑定word套打按钮
	FormUtil.initWordTemplate();
	
	FormUtil.initTableSort();
	
	FormUtil.linkageInit();
	
	// 初始化表单导入导出excel
	FormUtil.importExcel();
	// 初始化表单导入导出excel
	FormUtil.exportExcel();
	// Office按钮初始化
	FormUtil.initOfficeButton();
	//数据字典居中， 延时只执行一次
	changeWrapperC(800);
	$('div[type="custform"] .link.add').live('click',function(){
		changeWrapperC(800);
	});
	// 初始化textarea 之后随着内容增加自动延伸文本框高度.
	//FormUtil.autosizeTextarea();
	autosize(document.querySelectorAll('textarea'));
	
});
$(window).bind("load",function(){
	//Office控件初始化。
	//OfficePlugin.init();
	//WebSign控件初始化。
	WebSignPlugin.init();
	//PictureShow 图片展示控件
	PictureShowPlugin.init();	
	//初始化第一个表单tab 中部分样式问题
	FormUtil.initTabStyle();
});