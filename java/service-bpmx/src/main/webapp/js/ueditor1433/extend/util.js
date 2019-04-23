/**
 * 在表单设计模式上那些组件用的
 * 获取前一个td上的名称初始化到窗口中
 */
function initName(){
	if (!targetEl) {//新增模式
		//初始化字段描述
		if ($(element).is("td")) {
			var comment = $(element).prev().text().replace(':', '').trim();
			if (comment) {
				scope.external.comment = comment;
				Share.getPingyin({
					input : comment,
					postCallback : function(data) {
						scope.external.name = data.output;
					}
				});
			}
		}
	}
}

//转化external属性
function externalEncode(str) {
	return str.replace(new RegExp(/(")/g),"'");
}
