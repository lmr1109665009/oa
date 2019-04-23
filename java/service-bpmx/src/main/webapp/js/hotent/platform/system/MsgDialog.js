/**
 * 常用联系人组
 * @param conf
 */
function LinkmanGroupDialog(conf)
{
	if(!conf) conf={};
	var url=__ctx + '/platform/system/messageLinkmanGroup/dialog.ht';
	url=url.getNewUrl();
	DialogUtil.open({
		height:600,
		width: 800,
		title : '选择常用联系人组',
		url: url, 
		isResize: true,
	   //自定义参数
		params: conf.params,
		 //回调函数
        sucCall:function(rtn){
        	conf.callback.call(this,rtn);
        }
	});
}