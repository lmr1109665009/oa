/**
 * 附件管理。
 * @returns {AttachMent}
 */
if (typeof AttachMent == 'undefined') {
	AttachMent = {};
}
/**
 * 选择非直接上传附件时判断用flash还是html
 */
AttachMent.addFile=function(obj){
	AttachMent.htmlUpLoadFile(obj);
}

/**
 * 选择直接上传附件时判断用flash还是html
 */
AttachMent.directUpLoadFile=function(obj){
	AttachMent.htmlUpLoadFile(obj);
}

/**
 * flash附件非直接上传
 */
AttachMent.FlexAddFile=function(obj){	
	var inputObj=$(obj);
	var fieldName=inputObj.attr("field");
	var parent=inputObj.parent().parent();

	var rights="w";
	var divName="div.attachement";
	var inputName="input[name='" +fieldName +"'],textarea[name='" +fieldName +"']";
	//获取div对象。
	var divObj=$(divName,parent);
	var inputJson=$(inputName,parent);
	
	var aryJson=AttachMent.getFileJsonArray(divObj);
	//文件选择器
	FlexUploadDialog({isSingle:false,callback:function (fileIds,fileNames,filePaths,extPaths){
		if(fileIds==undefined || fileIds=="") return ;
		var aryFileId=fileIds.split(",");
		var aryName=fileNames.split(",");
		var aryExtPath=extPaths.split(",");
	
		for(var i=0;i<aryFileId.length;i++){
			var name=aryName[i];
			AttachMent.addJson(aryFileId[i],name,aryJson);
		}
		//获取json
		var json=JSON2.stringify(aryJson);		
		var html=AttachMent.getHtml(aryJson,rights);
		divObj.empty();
		divObj.append($(html));
		inputJson.text(json);
		if(typeof CustomForm != "undefined"){
			CustomForm.validate();
		}
		
	}});
};
/**
 * 直接附件上传
 */
AttachMent.directUpLoad=function(obj){
	var inputObj=$(obj);
	var fieldName=inputObj.attr("field");
	var parent=inputObj.parent().parent();
	var rights="w";
	var divName="div.attachement";
	var inputName="input[name='" +fieldName +"'],textarea[name='" +fieldName +"']";
	//获取div对象。
	var divObj=$(divName,parent);
	var inputJson=$(inputName,parent);
	
	var aryJson=AttachMent.getFileJsonArray(divObj);
	//文件上传
	DirectUploadDialog({callback:function (attachs){
		if(attachs==undefined || attachs==[]) return ;
		for(var i=0;i<attachs.length;i++){
			var fileId=attachs[i].fileId;
			var name=attachs[i].fileName;
			AttachMent.addJson(fileId,name,aryJson);
		}
		//获取json
		var json=JSON2.stringify(aryJson);		
		var html=AttachMent.getHtml(aryJson,rights);
		divObj.empty();
		divObj.append($(html));
		inputJson.val(json);
		if(typeof CustomForm != "undefined"){
			CustomForm.validate();
		}
	}});
};

/**
 * html附件上传dialog
 * @param conf
 */

AttachMent.htmlUpLoadFile=function(obj){	
	var inputObj=$(obj);
	var fieldName=inputObj.attr("field");
	var parent=inputObj.parent().parent();
	var divName="div.attachement";
	var rights="w";
	var inputName="input[name='" +fieldName +"'],textarea[name='" +fieldName +"']";
	//获取div对象。
	var divObj=$(divName,parent);
	var inputJson=inputObj.prev();//获取到textarea对象
	
	var aryJson=AttachMent.getFileJsonArray(divObj);
	//文件选择器
	HtmlUploadDialog({max:30,callback:function (attachs){
		if(attachs==undefined || attachs==[]) return ;
		for(var i=0;i<attachs.length;i++){
			var fileId=attachs[i].fileId;
			var name=attachs[i].fileName;
			AttachMent.addJson(fileId,name,aryJson);
		}
		var json=JSON2.stringify(aryJson);		
		var html=AttachMent.getHtml(aryJson,rights);
		divObj.empty();
		divObj.append($(html));
		inputJson.val(json);
		inputJson.text(json);
		if(typeof CustomForm != "undefined"){
			CustomForm.validate({form:divObj.parent()});
		}
		
	}});
};
/**
 * 附件排序
 * @param obj 附件排序。
 */
AttachMent.sort=function(obj,name,right){
    var inputObj=$(obj);
    var parent=inputObj.parent();
    var divName="div.attachement";
    var inputJson=$("textarea[name='"+name+"']")//获取到textarea对象
    //获取div对象。
    var divObj=$(divName,parent);
    var aryJson=AttachMent.getFileJsonArray(divObj);
    var arr =JSON.stringify(aryJson);
    var arrUrl = encodeURIComponent(arr);
    var url = __ctx +'/platform/system/sysFile/sysFileSort.ht?arr='+arrUrl;
    var dialog = $.ligerDialog.open({
        passConf : {dialog:dialog},
        height : 270,
        width : 600,
        title : '附件排序',
        url : url,
        modal : true,
        resizable : false,
        showMax: false,
        showToggle: false,
        showMin: false,
        buttons: [{
        	text: '确定',
			onclick: function(item, dialog){
        		var iframe=$("iframe", dialog.dialog);
                iframe.get(0).contentWindow.changeSort();
                var contents =iframe.contents();
            var aryStr = contents.find("#dataForm").val();
            var JsonAry = JSON.parse(aryStr);
                var html = AttachMent.getHtml(JsonAry, right);
            divObj.empty();
            divObj.append($(html));
                inputJson.val(aryStr);
                dialog.close();
            if(!contents){
                $.ligerDialog.alertExt("排序时出错");
                return;
            }
                $.ligerDialog.success("排序成功");
        } }],
        error: function(){alert('请求超时');},
    });
}
AttachMent.newHtmlFile=function (obj) {
    var inputObj=$(obj);
	history.back();
    var parent=inputObj.parent();
    var divName="div.attachement";
    var divObj=$(divName,parent);
    divObj.empty();
    divObj.append($(obj));
    if(typeof CustomForm != "undefined"){
        CustomForm.validate({form:divObj.parent()});
    }

}
/**
 * 删除附件
 * @param obj 删除按钮。
 */
AttachMent.delFile=function(obj){
	$.ligerDialog.confirm('确定要删除', function (rtn){
        var inputObj=$(obj);
        var parent=inputObj.parent();
        var divObj=parent.parent().parent().parent();
        var spanObj=$("span[name='attach']",parent);
        var fileId=spanObj.attr("fileId");
        var divContainer=divObj.parent();
        var aryJson=AttachMent.getFileJsonArray(divObj);
        AttachMent.delJson(fileId,aryJson);
        var json=JSON2.stringify(aryJson);
        var inputJsonObj=$("textarea",divContainer);
        if(aryJson.length == 0)
            json = "";
        //设置json
        inputJsonObj.val(json);
        //删除span
        parent.remove();
        if(typeof CustomForm != "undefined"){
            CustomForm.validate();
        }

        //设置删除文件的id
        var $delFileIdsVal = $('#delFileIds').val();
		if(!$delFileIdsVal){
            $('#delFileIds').val(fileId);
		}else{
            $('#delFileIds').val($delFileIdsVal + "," + fileId);
		}
        $.ligerDialog.success("删除成功");
		/**
		 * 将原有删除附件逻辑改到保存表单的时候进行处理
		 * 原因：在先删除附件但未保存表单的情况下会造成附件删除成功但表单中还显示有此附件
		 * */
		/*if(rtn){
			var url = __ctx + "/platform/system/sysFile/delByFileId.ht";
			var inputObj=$(obj);
			var parent=inputObj.parent();
			var divObj=parent.parent().parent().parent();
			var spanObj=$("span[name='attach']",parent);
			var divContainer=divObj.parent();
			var fileId=spanObj.attr("fileId");
			$.post(url,{ids:fileId},function(data){
				data = eval("("+data+")");
				if(data.success == "true"){
					var aryJson=AttachMent.getFileJsonArray(divObj);
					AttachMent.delJson(fileId,aryJson);
					var json=JSON2.stringify(aryJson);
					var inputJsonObj=$("textarea",divContainer);
					if(aryJson.length == 0)
						json = "";		
					//设置json
					inputJsonObj.val(json);
					//删除span
					parent.remove();
					if(typeof CustomForm != "undefined"){
						CustomForm.validate();
					}
					$.ligerDialog.success("删除成功");
				}else{
					$.ligerDialog.error('删除失败');
				}
			});
		}*/
	})
};

/**
 * 初始化表单的附件字段数据。
 */
AttachMent.init=function(subRights,parent){
	$("[ctltype='attachment']").each(function(){
		var right=$(this).attr("right")==null?"":$(this).attr("right");
		var val=$(this).val()==null?"":$(this).val();
		var div=$('<div name="div_attachment_container" right="'+right+'"></div>');
		div.append('<div class="attachement"></div>');
		var obj = $('<textarea style="display:none" controltype="attachment" name="'
				+$(this).attr("name")+'" lablename="附件" validatable="'+$(this).attr("validatable")+'">'+val+'</textarea>');
		obj.attr("validate",$(this).attr("validate"));
		div.append(obj);
		
		var onclick="AttachMent.addFile(this)";
		if($(this).attr("isdirectupload")=="1"){
			onclick="AttachMent.directUpLoadFile(this)";
		}
		div.append('<a href="javascript:;" field="'+$(this).attr("name")+'" class="link selectFile" atype="select" onclick="'+onclick+'">选择</a>');
        div.append('<a href="javascript:;" class="link selectFile" atype="select" onclick="AttachMent.sort(this,\''+$(this).attr("name")+'\',\''+$(this).attr("right")+'\');">排序</a>');
		$(this).after(div);
		$(this).remove();
		//前台js解析完页面后   再对附件必填进行处理
		TablePermission.handleMainTableRequest();
	});
	
	if(	$.isEmpty(parent)||parent.length==0){
		parent = $("div[name='div_attachment_container']");
	}
	parent.each(function(){
		var me=$(this),
			rights=me.attr("right");
		//如果没有权限属性，可能是子表中的附件
		if(!rights){
			rights=me.closest("[type='subtable']").attr("right");
		}
		//对于弹出框的处理
		if(!$.isEmpty(subRights))
			rights = subRights;	
		if(rights){
			rights=rights.toLowerCase();
		}
		
		if(rights=="r"||rights=="rp"){
			$("a.selectFile",me).remove();
		}
		var atta =$("textarea[controltype='attachment']",me);
		var jsonStr = atta.val();
		if(!$.isEmpty(jsonStr)){
			jsonStr = jsonStr.replaceAll("￥@@￥","\"").replaceAll("'","\"");
			atta.val(jsonStr);
		}
		var divAttachment=$("div.attachement",me);
		//json数据为空。
		AttachMent.insertHtml(divAttachment,jsonStr,rights);
	});
};

/**
 *  附件插入显示
 * @param {} div
 * @param {} jsonStr 
 * @param {} rights 权限 如果不传，默认是r
 */
AttachMent.insertHtml= function(div,jsonStr,rights){
	if($.isEmpty(jsonStr)) {
		div.empty();
		return ;
	}
	if($.isEmpty(rights)) rights ='r';
	var jsonObj=[];
	try {
		jsonStr = jsonStr.replaceAll("￥@@￥","\"");
		jsonObj =	jQuery.parseJSON(jsonStr);
	} catch (e) {
	}
	var html=AttachMent.getHtml(jsonObj,rights);
	div.empty();
	div.append($(html));
};

/**
 * 获取文件的html。
 * @param aryJson
 * @returns {String}
 */
AttachMent.getHtml=function(aryJson,rights){
	var str="";
	var template="";
	var templateW="<li style='overflow: hidden;'><span class='attachement-span'><span fileId='#fileId#' name='attach' file='#file#' ><a class='attachment' target='_blank' path='#path#' onclick='AttachMent.handleClickItem(this,\"w\")' title='#title#'>#name#</a></span><a href='javascript:;' onclick='AttachMent.download(this);' title='下载' class='download'>下载</a>&nbsp;<a href='javascript:;' onclick='AttachMent.delFile(this);' class='cancel'>删除</a></span></li>";
	var templateR="<li style='margin-bottom: 10px;margin-top: 10px;'><span class='attachement-span'><span fileId='#fileId#' name='attach' file='#file#' ><a class='attachment' target='_blank' path='#path#' onclick='AttachMent.handleClickItem(this,\"r\")' title='#title#'>#name#</a></span><a href='javascript:;' onclick='AttachMent.download(this);' title='下载' class='download'>下载</a></span></li>";
	if(rights=="w"||rights=="b"){
		template=templateW;
	}
	else{
		template=templateR;
	}
	for(var i=0;i<aryJson.length;i++){
		var obj=aryJson[i];
		var id=obj.id;
		var name=obj.name;
		var path =__ctx +"/platform/system/sysFile/file_" +obj.id+ ".ht";
			
		var file=id +"," + name ;
		var tmp=template.replace("#file#",file).replace("#path#",path).replace("#name#", AttachMent.parseName(name)).replace("#title#",name).replace("#fileId#", id);
		//附件如果是图片就显示到后面
		str+=tmp;
	}
	str = "<ul>"+str+"</ul>";	/*改为一个附件就占领一行*/
	return str;
};

AttachMent.parseName = function(name){
	/*if(name.length >10)
		return name.substr(0,6)+"...";*/	/*暂时去掉截取字段*/
	return name;
}

/**
 * 添加json。
 * @param fileId
 * @param name
 * @param path
 * @param aryJson
 */
AttachMent.addJson=function(fileId,name,aryJson){
	var rtn=AttachMent.isFileExist(aryJson,fileId);
	if(!rtn){
		var obj={id:fileId,name:name};
		aryJson.push(obj);
	}
};

/**
 * 删除json。
 * @param fileId 文件ID。
 * @param aryJson 文件的JSON。
 */
AttachMent.delJson=function(fileId,aryJson){
	for(var i=aryJson.length-1;i>=0;i--){
		var obj=aryJson[i];
		if(obj.id==fileId){
			aryJson.splice(i,1);
		}
	}
};

/**
 * 判断文件是否存在。
 * @param aryJson
 * @param fileId
 * @returns {Boolean}
 */
AttachMent.isFileExist=function(aryJson,fileId){
	for(var i=0;i<aryJson.length;i++){
		var obj=aryJson[i];
		if(obj.id==fileId){
			return true;
		}
	}
	return false;
};

/**
 * 取得文件json数组。
 * @param divObj
 * @returns {Array}
 */
AttachMent.getFileJsonArray=function(divObj){
	var aryJson=[];
	var arySpan=$("span[name='attach']",divObj);
	arySpan.each(function(i){
		var obj=$(this);
		var file=obj.attr("file");
		var aryFile=file.split(",");
		var obj={id:aryFile[0],name:aryFile[1]};
		aryJson.push(obj);
	});
	return aryJson;
};

AttachMent.handleClickItem = function(obj,rights){

	var _this = $(obj);
	var span = _this.closest("span");
	var mFileId = span.attr("fileId");
	
	var url =__ctx+"/platform/system/sysFile/getJson.ht";
	var sysFile;
	$.ajax({
		url:url,
		data:{
			fileId:mFileId
		},
		success:function(data){
			if(typeof(data)=="string"){
				$.ligerDialog.error('系统超时请重新登录!','提示');
				return ;
			}
			
			if(data.status!=1){
				$.ligerDialog.error(data.msg,'提示');
			}else{
				sysFile = data.sysFile;
				var path = _this.attr("path");
				if(/(doc)|(docx)|(xls)|(xlsx)|(ppt)|(pptx)|(pdf)/ig.test(sysFile.ext)){
					var showUrl = __ctx+"/platform/system/sysFile/office.ht?fileId=" + mFileId;
					if(/(doc)|(docx)/ig.test(sysFile.ext)){//word才支持在线编辑
						if(rights=='w'){
							showUrl+="&rights=w";
						}
					}else{
						showUrl+="&rights=r";
					}
                    $.openFullWindow(showUrl);
				}else if(/(jpg)|(png)|(bpm)|(gif)|(webp)/ig.test(sysFile.ext)){
                    var path =__ctx+"/platform/system/sysFile/file_"+mFileId+".ht";
                    $.openFullWindow(path);
				} else{
					// window.open(path,'_blank');
                    var path =__ctx+"/platform/system/sysFile/file_"+mFileId+".ht?download=true";
                    location.href=path;
				}
			}
		}
		
		
	});
};

/**
 * 下载
 */		
AttachMent.download	= function(obj){
	var me = $(obj);
	var	span = me.siblings("span");
	if(span.length >0)
	var	fileId = span.attr("fileId");
	
	var path =__ctx+"/platform/system/sysFile/file_"+fileId+".ht?download=true";
	location.href=path;
}
	


