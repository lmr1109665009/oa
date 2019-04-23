/**
 * 弹出框的弹出调用类
 */
var SysPopupRemindUtil = {
	/**
	 * userId：弹出框，不传userId默认拿当前用户;
	 * emptyFuc:当没有数据时调用的方法，是一个fucntion;
	 * refreshTime:当弹出框不关闭的时候，多少秒刷新一次弹出框
	 * sprsIds:提示id
	 * isTest：是否是演示
	 */
	show : function(userId,emptyFuc,refreshTime,sprsIds,isTest){
		//先判断是否有数据
		$.post(__ctx+"/platform/system/sysPopupRemind/showSize.ht",{userId:userId,isTest:isTest,sprsIds:sprsIds},
		function (data, textStatus){
			if(data.size<=0){
				if(!emptyFuc) return;
				emptyFuc(data);
			}else{
				var url=__ctx+"/platform/system/sysPopupRemind/show.ht?";
				if(sprsIds){
					url+="&sprsIds="+sprsIds;
				}
				if(userId){
					url+="&userId="+userId;
				}
				if(isTest){
					url+="&isTest="+isTest;
				}
				if(refreshTime){//传到目标页面，让其自己操作
					url+="&refreshTime="+refreshTime;
				}
				var tip=$.ligerDialog.open({showToggle:true,fixedType:'se',modal: false,showType:'slide',isDrag: false,isHidden:false,title: '提示信息', url:url,width:"300",height:"250"});
			}
		}, 
		"json");
	}
}