<%--
	time:2012-05-25 10:16:17
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>系统模版明细</title>
	<%@include file="/commons/include/getById.jsp" %>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/form/AttachMent.js"></script>
	<script type="text/javascript">
		//放置脚本
		$(function() {
			var fileid=$("textarea[name='fileid']").attr("fileid");
			if(fileid){
				init(fileid);
			}
		});
		function init(fileId){
			var url = "${ctx}/platform/system/sysFile/getJson.ht";
			var params = {fileId:fileId};
			$.post(url,params,function(data){
				var divObj=$("div.attachement");
				var aryJson=[];
				var aryFileId = data.sysFile.fileId;
				var fileName = data.sysFile.fileName+"."+data.sysFile.ext;
				AttachMent.addJson(aryFileId,fileName,aryJson);
				var html=getHtml(aryJson);
				divObj.empty();
				divObj.append($(html));
			});
		}
		
		function getHtml(aryJson){
			var str="";
			var template="<span class='attachement-span'><span fileId='#fileId#' name='attach' file='#file#' ><input type='hidden' name='fileId' id='fileId' value='#fileId#'><a class='attachment' target='_blank' href='#path#'  title='#title#'>#name#</a></span></span>";
			for(var i=0;i<aryJson.length;i++){
				var obj=aryJson[i];
				var id=obj.id;
				var name=obj.name;
				var path =__ctx +"/platform/system/sysFile/file_" +obj.id+ ".ht";
				var file=id +"," + name ;
				var tmp=template.replace("#file#",file).replace("#path#",path).replace("#name#", name).replace("#title#",name).replaceAll("#fileId#", id);
				str+=tmp;
			}
			return str;
		}
	</script>
</head>
<body>
<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">系统模版详细信息</span>
			</div>
			
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link back" href="list.ht"><span></span>返回</a>
					</div>
					
				</div>
			</div>
			
		</div>
		<div class="panel-body">
					<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<th width="20%">主题:</th>
							<td>${sysOfficeTemplate.subject}</td>
						</tr>
						<tr>
							<th width="20%">模版类型:</th>
							<td>
								<c:if test="${sysOfficeTemplate.templatetype==1}">普通模版</c:if>
								<c:if test="${sysOfficeTemplate.templatetype==2}">套红模版</c:if>
							</td>
						</tr>
						<tr>
							<th width="20%">创建人:</th>
							<td>${sysOfficeTemplate.creator}</td>
						</tr>
						<tr>
							<th width="20%">创建时间:</th>
							<td>
								<fmt:formatDate value="${sysOfficeTemplate.createtime}"/>
							</td>
						</tr>
						<tr>
							<th width="20%">文件:</th>
							<td>
								<div right="w" name="div_attachment_container">
									<div class="attachement"></div>
									<textarea style="display:none;" controltype="attachment" lablename="附件ID" name="fileid" fileid=" ${sysOfficeTemplate.fileid}"></textarea>
								</div>
							</td>
						</tr>
						<tr>
							<th width="20%">备注:</th>
							<td>${sysOfficeTemplate.memo}</td>
						</tr>
					</table>
				</div>
		</div>

</body>
</html>
