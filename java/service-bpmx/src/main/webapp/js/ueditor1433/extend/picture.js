//上传图片按钮
var pictureFile = null;
UE.registerUI('picture', function(editor, uiName) {
	// 创建一个button
	var btn = new UE.ui.Button({
		// 按钮的名字
		name : uiName,
		// 提示
		title : "上传图片",
		// 需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
		cssRules : 'background-position: -380px 0;',
		// 点击时执行的命令
		onclick : function() {
			var url = __ctx + "/platform/picture/pictureHandler/upload.ht";
			var formHtml = '<form action="'+url+'" method="post" style="display:none" enctype="multipart/form-data" id="formPicture">\
							<input type="file" name="file" id="PictureUpload" ></form>';
			if(pictureFile){
				pictureFile.click();
				return;
			}
			$("body").append($(formHtml));
			pictureFile = $("#PictureUpload");
			pictureFile.click();
			pictureFile.bind("change",function(){
				var fileName = pictureFile.val();
				if(/([^\x00-\xff]|\w)+.(png|gif|jpg|jpeg)/gi.test(fileName)){
					var frm=$('#formPicture');
					frm.ajaxForm({success:function(rtn){
						//图片上传处理结果
						var html = "<img  src='"+rtn+"' />";
						editor.execCommand('insertHtml',html);
					} }); 
					frm.submit();
				}else{
					$.ligerDialog.warn("请选择图片类型文件",'提示');
				}
			});
		}
	});

	// 因为你是添加button,所以需要返回这个button
	return btn;
}/*
	 * index 指定添加到工具栏上的那个位置，默认时追加到最后,editorId
	 * 指定这个UI是那个编辑器实例上的，默认是页面上所有的编辑器都会添加这个按钮
	 */);