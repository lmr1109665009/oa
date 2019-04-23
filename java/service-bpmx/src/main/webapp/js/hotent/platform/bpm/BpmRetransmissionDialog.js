/**
 *  功能:流程转办对话框
 * 
 * @param conf:配置为一个JSON
 * 
 * dialogWidth:对话框的宽度。
 * dialogHeight：对话框高度。
 * 
 * taskId:任务ID。
 * taskName 任务名称
 */
function BpmRetransmissionDialog(conf){

	if(!conf) conf={};	
	var url=__ctx + '/platform/bpm/bpmProCopyto/forward.ht?runId=' + conf.runId;
	var dialogWidth=600;
	var dialogHeight=400;
	conf=$.extend({},{dialogWidth:dialogWidth ,dialogHeight:dialogHeight ,help:0,status:0,scroll:0,center:1,reload:true},conf);

	var winArgs="dialogWidth="+conf.dialogWidth+"px;dialogHeight="+conf.dialogHeight
		+"px;help=" + conf.help +";status=" + conf.status +";scroll=" + conf.scroll +";center=" +conf.center;
	url=url.getNewUrl();
	
	/*KILLDIALOG*/
	DialogUtil.open({
		height:conf.dialogHeight, 
		width: conf.dialogWidth,
		title : '转发窗口',
		url: url, 
		isResize: true,
		sucCall:function(rtn){
		}
	});
	

}