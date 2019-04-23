<%--
	time:2012-01-14 15:10:58
	desc:edit the 发送消息
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<%@page import="com.hotent.core.api.util.PropertyUtil"%>
<html>
<head>
<title>编辑 发送消息</title>
<%@include file="/commons/include/form.jsp"%>
<f:link href="form.css"></f:link>
<script type="text/javascript" src="${ctx}/servlet/ValidJs?form=messageSend"></script>
<script type="text/javascript" src="${ctx }/js/hotent/platform/system/SysDialog.js"></script>
<script type="text/javascript" src="${ctx }/js/hotent/platform/system/MsgDialog.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/AttachMent.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/HtmlUploadDialog.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/FlexUploadDialog.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor_default.config.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/ueditor.all.min.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/lang/zh-cn/zh-cn.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/ueditor1433/themes/default/css/ueditor.css"></script>
<script type="text/javascript">
var uploadType = '<%=PropertyUtil.getByAlias("uploadType")%>';
		$(function() {
			var messageEditor = new baidu.editor.ui.Editor({minFrameHeight:300,initialFrameWidth:800,lang:'zh_cn'});
			messageEditor.render("txtHtml");
			function showRequest(formData, jqForm, options) { 
				return true;
			} 
			if(${messageSend.canReply==null}){
				valid(showRequest,showResponse,1);
			}else{
				valid(showRequest,showResponse);
			}
			$("a.sendMessage").click(function() {
				 if($("#receiverName").val()==""&&$('#receiverOrgName').val()=="")
				 {
			        $.ligerDialog.warn('请选择收信人或收信组织！','提示信息');
			        return;
				 }
				$('#content').val(messageEditor.getContent());
				$('#messageSendForm').submit(); 
			});
			AttachMent.init("w");
			
			
		});
		
		function dlgCallBack(userIds,fullnames,emails,mobiles,retypes)
		{								
			$("#receiverName").val(fullnames);	
			$("#receiverId").val(userIds);	
			$("#receiveType").val(retypes);	
			
		};
		function addClick(oName)
		{
			var selectUserIds = $("#receiverId").val();
			var selectUserNames = $("#receiverName").val();
			UserDialog({
				callback : dlgCallBack,
				selectUserIds:selectUserIds,
				selectUserNames:selectUserNames,
				isSingle : false
			});	
		};
		//清空
		function reSet(obj)
		{
			$("#receiverName").val("");	
			$("#receiverId").val("");	
			$("#receiveType").val("");				
		}
		
		// 弹出组织框
		function showOrgDialog(){
			OrgDialog({callback:dlgOrgCallBack,isSingle:false});
		};
		
		// 组织框返回数据   
		function dlgOrgCallBack(orgIds, orgNames)
		{
			$("#receiverOrgName").val(orgNames);	
			$("#receiverOrgId").val(orgIds);	
			//$("#receiveType").val(retypes);	
		}
		
		// 清空所选组织
		function reSetOrg(obj)
		{
			$("#receiverOrgName").val("");	
			$("#receiverOrgId").val("");				
		}
		
		function addLinkman(){
			LinkmanGroupDialog({callback:linkmanCallBack});
		}
		
		function linkmanCallBack(obj){
			$("#receiverId").val(obj.id);	
			$("#receiverName").val(obj.name);	
		}
		
	</script>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">编辑发送消息</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group">
						<a class="link sendMessage" id="dataFormSave" href="javascript:;">
							<span></span>
							发送
						</a>
					</div>
					<div class="l-bar-separator"></div>
					<div class="group">
						<a class="link back" href="list.ht?userId=${userId}">
							<span></span>
							返回
						</a>
					</div>
				</div>
			</div>
		</div>
		<div class="panel-body">
			<form id="messageSendForm" method="post" action="save.ht">

				<table class="table-detail" cellpadding="0" cellspacing="0" border="0">

					<tr>
						<th width="20%">标题:</th>
						<td>
							<input type="text" id="subject" name="subject" value="${messageSend.subject}" class="inputText" style="width: 308px !important;" />
						</td>
					</tr>
					<tr>
						<th width="20%">收信人:</th>
						<td>
							<input id="receiverName" name="receiverName" size="80" readonly="readonly" value="${messageSend.receiverName}" />
							<a href="javascript:;" onclick="addClick()" class="link get">选择</a>
							<a href="javascript:;" onclick="addLinkman()" class="link get">常用联系人</a>

							<a href="javascript:;" onclick="reSet()" class="link clean">清空</a>
							<input type="hidden" id="receiverId" name="receiverId" value="" class="inputText" />
							<input type="hidden" id="receiveType" name="receiveType" value="" class="inputText" />
						</td>
					</tr>
					<tr>
						<th width="20%">收信组织:</th>
						<td>
							<input id="receiverOrgName" name="receiverOrgName" size="80" readonly="readonly" value="${messageSend.receiverOrgName}" />
							<a href="javascript:;" onclick="showOrgDialog()" class="link get">选择</a>
							<a href="javascript:;" onclick="reSetOrg()" class="link clean">清空</a>
							<input type="hidden" id="receiverOrgId" name="receiverOrgId" value="" class="inputText" />
						</td>
					</tr>
					<tr>
						<th width="20%">需要回复:</th>
						<td>
							<input type="hidden" name="messageType" value="1" />
							<input type="radio" name="canReply" value="1" <c:if test='${messageSend.canReply==1||messageSend.canReply==null}'>checked</c:if> />
							是
							<input type="radio" name="canReply" value="0" <c:if test='${messageSend.canReply==0}'>checked</c:if> />
							否
						</td>
					</tr>
					<tr>
						<th width="15%">附件:</th>
						<td>
							<div name="div_attachment_container">
								<div class="attachement"></div>
								<textarea style="display: none" controltype="attachment" name="attachment" lablename="附件" validate="{}">${messageSend.attachment}</textarea>
								<a href="javascript:;" field="attachment" class="link selectFile" atype="select" onclick="AttachMent.addFile(this);">选择</a>
							</div>
						</td>
					</tr>
					<tr>
						<td align="right" nowrap="nowarp" style="width: 20%;" class="formTitle">内容</td>
						<td class="formInput">
							<div id="editor" position="center" style="overflow: hidden; height: 100%;">
								<textarea id="txtHtml" name="html">${messageSend.content}</textarea>
								<textarea id="content" name="content" style="display: none;"></textarea>
							</div>
						</td>
					</tr>

				</table>
				<input type="hidden" name="id" value="${messageSend.id}" />
			</form>
		</div>
	</div>
</body>
</html>
