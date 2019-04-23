<%@page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" >
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<%@include file="/commons/include/form.jsp" %>
<link rel="stylesheet" type="text/css" href="../input.css">
<link rel="stylesheet" type="text/css" href="${ctx}/styles/default/css/form.css">
<script type="text/javascript" src="${ctx}/js/ueditor2/dialogs/internal.js"></script>
<style type="text/css">
	body{
		overflow:hidden;
	}
	a.extend{
	  display:inline;
	}
</style>
<script type="text/javascript">
	var _element;
	$(".button-td").bind("mouseenter mouseleave",function(){
		$(this).toggleClass("button-td-hover");
	});
	$(function() {
		_element = null;
		_element = editor.curInput;
		if (_element) {
			bindData();
		}			
	});
	
	function openIconDialog() {
		var url= __ctx+"/js/ueditor2/dialogs/extend/input/icons.jsp";			
		var dialogWidth=500;
		var dialogHeight=400;
		conf=$.extend({},{dialogWidth:dialogWidth ,dialogHeight:dialogHeight ,help:0,status:0,scroll:0,center:1});

		var winArgs="dialogWidth="+conf.dialogWidth+"px;dialogHeight="+conf.dialogHeight
			+"px;help=" + conf.help +";status=" + conf.status +";scroll=" + conf.scroll +";center=" +conf.center;			
		DialogUtil.open({
			height:conf.dialogHeight,
			width: conf.dialogWidth,
			title : '',
			url: url, 
			isResize: true,
			//自定义参数
			sucCall:function(rtn){
				$("#preview-button").attr("class",rtn.cla);
			}
		});
		
	};

	function bindData() {
		var child = _element.childNodes[0];
		if (child) {
			var cla = $(child).attr("class"), label = $(child).text();
			$("#preview-button").attr("class", cla);
			$("#a-label").val(label);
		}
	};

	dialog.onok = function() {
		var label = $("#a-label").val();
		//如果是手机端
		if("${param.type}"=='mb'){
			var child = utils.parseDomByString('<span class="am-input-group-btn"> <button ht-customerDialog="" class="am-btn am-btn-default" type="button">'+label+'</button></span>');
			
			var start = editor.selection.getStart();
			if(!start||!child)return;
			if(start.tagName!= "INPUT"){
				start.appendChild(utils.parseDomByString('<button ht-customerDialog="[]" class="am-btn am-btn-default" type="button">'+label+'</button>'));
			}
			else{
				var div =$("<div class='am-input-group' type='customerButton'></div>");
				start = domUtils.findEditableInput(start);
				$(start.parentElement).append(div);
				div.append(start);
				div.append(child);
			}
			return;
		}
		
		
		
		var html = '';
		
		var cla = $("#preview-button").attr("class");
		html += '<a href="javascript:;" ';
		if(cla)html+='class="'+cla+'"';
		html +='>' + label + '</a>';
		html += '';
		if(_element){
			editor.curInput.outerHTML=html;
		}
		else{
			var child = utils.parseDomByString(html);
			var start = editor.selection.getStart();
			if(!start||!child)return;
			if(start.tagName=='TD'){
				start.appendChild(child);
			}
			else{
				start = domUtils.findEditableInput(start);
				utils.insertAfter(child, start);
			}
		}
	};
</script>
</head>
<body>
	<div id="inputPanel">
		<fieldset class="base">
			<legend><var id="lang_button_prop"></var></legend>
			<table>
				<tr>
					<th><var id="lang_button_label"></var>:</th>
					<td><input id="a-label" type="text" value="选择"/></td>
				</tr>
				<c:if test="${empty param.type}">
				<tr>
					<th><var id="lang_button_img"></var>:</th>
					<td>
						<a href="javascript:;" id="preview-button"></a>				
						<div class="button-td" onclick="openIconDialog()">
						   	<var id="lang_choose_img"></var>
						</div>
					</td>
				</tr>	
				</c:if>			
			</table>
		</fieldset>
	</div>
	
</body>
</html>