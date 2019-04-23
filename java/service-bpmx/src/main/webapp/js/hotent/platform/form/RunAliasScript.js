/**
 * 调用别名脚本。
 * @param param	传入参数，参数格式 {alias:"别名必须传入",name:"abc","age":20}
 * @returns 返回数据格式如下：
 * {"result":"4","isSuccess":0,"msg":"别名脚本执行成功！"}
 * result:脚本返回的结果
 * isSuccess：0表示成果能够，1，失败
 * msg ：具体的出错信息
 */
function RunAliasScript(param){
	if(!param) param={};
	var url=__ctx + '/platform/system/aliasScript/executeAliasScript.ht';
	var rtn="";
	$.ajaxSetup({async:false});  //同步
	$.post(url,param,function(data){
		rtn=eval("("+ data +")");
	});
	$.ajaxSetup({async:true}); //异步
	return rtn;

}