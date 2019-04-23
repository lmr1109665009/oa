function userDetail(userid){
	var url = __ctx+"/platform/system/sysUser/get.ht?canReturn=2&hasClose=1&userId="+userid;
	url=url.getNewUrl();
	DialogUtil.open({
		url:url,
		title:"用户信息",
		height:'600',
		width:'800'
	});
};


