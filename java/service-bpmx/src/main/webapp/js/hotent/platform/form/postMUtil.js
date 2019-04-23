if (typeof postMUtil == 'undefined') {
	 postMUtil = {};
}
/**
 * 初始化opts。
 */
postMUtil.opts = {
	//待办事宜
	mattersList:{
	//提交
	agree:{node:'1',type:"agree",btn:true},
	//保存表单
	save:{node:'1',type:"save",btn:true},
	},
	//已办事宜
    matterList:{

    },
	//关闭本页面，刷新上级页面
    close:{node:'close',type:"close",btn:true},
}
//postMUtil.postM(postMUtil.opts.matterList.del);
postMUtil.postM=function(opts,n){
	console.log("发送消息给父页面",opts,n);
	var openwindow;
	if(n==1){
        openwindow=window.parent
	}
    else if(n==2){
        openwindow=window.parent.parent
    }
    else if(n==3){
        openwindow=window.parent.parent.parent
    }
    else{
        openwindow=window.parent
	}
    openwindow.postMessage(opts, '*');
};
//postMUtil.postM(postMUtil.opts.mattersList.agree,1);
//postMUtil.postM(postMUtil.opts.mattersList.agreem,2);



