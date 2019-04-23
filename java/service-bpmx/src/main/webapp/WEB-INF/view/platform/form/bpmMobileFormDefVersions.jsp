
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看版本</title>
<%@include file="/commons/include/get.jsp" %>

<style type="text/css">
	.panel-data {
		margin-top: 5px ;
		margin-bottom: 5px ;
	}
	
	.floatBtn {
		font-weight: bold;
		color: red !important;
	}
	
</style>
<script type="text/javascript">
	$(function(){
		handPublish();
		handNewVersion();
	});
	
	function handPublish(){
		$.confirm("a.link.deploy",'确认发布吗？');
	}
	
	function handNewVersion(){
		$.confirm("a.link.newVersion",'确认新建版本吗？');
	}
	
	function openEdit(id){
		var url="edit.ht?id=" +id;
		jQuery.openFullWindow(url);
	}
	
</script>
</head>
<body>
	<div class="panel">
	<div class="hide-panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">${formName}--版本管理</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link back " href="list.ht"><span></span>返回</a></div>
				</div>
			</div>
		</div>
		</div>
		<div class="panel-body">
			<display:table name="bpmMobileFormDefList" id="mobileFormDef" requestURI="versions.ht" sort="external" cellpadding="1" cellspacing="1"  class="table-grid">
				<display:column property="version" title="版本号" sortable="true" sortName="versionNo"></display:column>
				<display:column property="creator" title="创建人" sortable="true" sortName="publisher"></display:column>
				<display:column title="发布时间" sortable="true" sortName="publishTime">
					<fmt:formatDate value="${mobileFormDef.publishTime}"/> 
				</display:column>
				<display:column title="是否默认" sortable="true" sortName="isDefault">
					<c:choose>
					 	<c:when test="${mobileFormDef.isDefault==1}"><span class="green">是</span></c:when>
					 	<c:otherwise>
					 		<span class="red">否</span>
					 	</c:otherwise>
					</c:choose>
				</display:column>
				<display:column title="是否发布" sortable="true" sortName="isDefault">
					<c:choose>
					 	<c:when test="${mobileFormDef.isPublished==1}"><span class="green">已发布</span></c:when>
					 	<c:otherwise>
					 		<span class="red">未发布</span>
					 	</c:otherwise>
					</c:choose>
				</display:column>
			
				<display:column title="管理" media="html" >
					<c:if test="${mobileFormDef.isPublished==0}">
							<a href="publish.ht?id=${mobileFormDef.id}" class="link deploy" >发布</a>
					</c:if>
					<c:if test="${mobileFormDef.isPublished== 1}">
						<a href="newVersion.ht?id=${mobileFormDef.id}"  class="link newVersion">新建版本</a>
					</c:if>	
					<a onclick="openEdit(${mobileFormDef.id})" class="link edit">编辑表单</a>
					<c:choose>
						<c:when test="${mobileFormDef.isDefault==1}">
						</c:when>
						<c:otherwise>
							<a  class="link setting" href="setDefaultVersion.ht?id=${mobileFormDef.id }"><span >设置默认</span></a>
							<a href="del.ht?id=${mobileFormDef.id}" class="link del">删除</a>
						</c:otherwise>
					</c:choose>
				</display:column>
			</display:table>
			
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


