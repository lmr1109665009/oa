<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor_default.config.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor.all.min.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/lang/zh-cn/zh-cn.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/extend/picture.js"></script>
<script type="text/javascript">
	$(function() {
		$("textarea.myeditor").each(function(num) {
			var ue =new baidu.editor.ui.Editor({minFrameHeight:300,initialFrameWidth:800,lang:'zh_cn'});
			ue.render(this);
		});
	});
	function getEditorData(obj){
		return obj.getContent();
	}
</script>
