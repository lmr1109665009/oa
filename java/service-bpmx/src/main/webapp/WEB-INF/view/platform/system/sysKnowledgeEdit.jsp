<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<%@page import="com.hotent.core.api.util.PropertyUtil"%>
<html>
<head>
	<title>编辑 知识库</title>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/form/AttachMent.js" ></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/HtmlUploadDialog.js" ></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/FlexUploadDialog.js" ></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor_default.config.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor.all.min.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/lang/zh-cn/zh-cn.js"></script>
	<script type="text/javascript">
	$(function() {
			var editor = new baidu.editor.ui.Editor({minFrameHeight:400,initialFrameWidth:'100%',lang:'zh_cn'});
			$("a.save").click(function() {
				$("#sysKnowledgeForm").attr("action","save.ht");
				$("#saveData").val(1);
				$("#txtHtml").val(editor.getContent());
				submitForm();
			});
			editor.render("txtHtml");
			AttachMent.init("w");
		});
		//提交表单
		function submitForm(){
			var options={};
			if(showResponse){
				options.success=showResponse;
			}
			var frm=$('#sysKnowledgeForm').form();
			frm.ajaxForm(options);
			if(frm.valid()){
				frm.submit();
			}
		}
		
		function showResponse(responseText) {
			var obj = new com.hotent.form.ResultMessage(responseText);
			if (obj.isSuccess()) {
				$.ligerDialog.confirm(obj.getMessage()+",是否继续操作","提示信息", function(rtn) {
					if(rtn){
						window.location.reload();
					}else{
						this.close();
						window.opener.location.reload();
					}
				});
			} else {
				$.ligerDialog.err("提示信息","知识库保存失败!",obj.getMessage());
			}
		}
	</script>
</head>
<body>
<div class="panel">
	<div class="panel-top">
		<div class="panel-toolbar">
			<div class="toolBar">
				<div class="group"><a class="link save" id="dataFormSave" href="#"><span></span>保存</a></div>
				<div class="l-bar-separator"></div>
			</div>
		</div>
	</div>
	<div  >
		<form id="sysKnowledgeForm" method="post" action="save.ht">
			<table class="table-detail" cellpadding="0" cellspacing="0" border="0" type="main">
				<input type="hidden" name="typeid"  value="${sysKnowledge.typeid}" />
				<tr>
					<th width="10%">主题: </th>
					<td ><input style="width: 300px"  type="text" id="subject" name="subject" value="${sysKnowledge.subject}"  class="inputText" validate="{required:true,maxlength:150}"  /></td>
				</tr>
				<tr>
				    <th width="10%">书签: </th>
						<td>
							<input class="inputText" id="markStr" style="width: 300px" name="markStr" type="text" value="${markStr}" validate="{maxlength:150}"><span style="margin-left: 10px;"><font style="color:red">*</font>多个书签以逗号隔开</span>
						</td>			
				<tr>
				<tr>
				    <th width="10%">内容: </th>
					<td align="center" class="formInput">
						<div id="editor" position="center"
							style="overflow: hidden; height: 100%;">
							<textarea id="txtHtml" name="content">${sysKnowledge.content}</textarea>
						</div>
					</td>			
					</tr> 
				<tr>
					<th width="10%">附件: </th>
					<td>
						<div name="div_attachment_container">
							<div class="attachement"></div>
							<textarea style="display: none" controltype="attachment"
								id="attachment" name="attachment" lablename="附件" validate="{}">${sysKnowledge.attachment}</textarea>
							<a href="javascript:;" field="attachment" class="link selectFile"
								atype="select" onclick="AttachMent.addFile(this);">选择</a>
						</div> 
					</td>
				   </tr>
			</table>
			<input type="hidden" name="id" value="${sysKnowledge.id}" />
		    <input type="hidden" name="saveData" id="saveData" />
		    <input type="hidden" name="executeType"  value="start" />
		</form>
	</div>
</div>
</body>
</html>
