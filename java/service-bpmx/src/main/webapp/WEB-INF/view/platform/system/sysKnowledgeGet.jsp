
<%--
	time:2015-07-28 10:15:59
--%>
<%@page import="com.hotent.core.api.util.PropertyUtil"%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<%
	String appName=PropertyUtil.getByAlias("appName");
%>
<head>
<title>知识库明细</title>
<%@include file="/commons/include/get.jsp"%>
<f:link href="article.css"></f:link>
<f:link href="form.css" ></f:link>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/AttachMent.js" ></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/HtmlUploadDialog.js" ></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/FlexUploadDialog.js" ></script>
<script type="text/javascript">
	//放置脚本
	$().ready(function (){
		AttachMent.init("r");
	});
	function showKnowList(markId){
		var url=__ctx +"/platform/system/sysKnowledge/knowDialog.ht?markId="+markId;
    	url=url.getNewUrl();
    	DialogUtil.open({
            height:350,
            width: 500,
            title : '书签文章列表',
            url: url, 
            isResize: true
            //自定义参数
        });
	}
</script>
<style type="text/css">
.footer{border: hidden !important;}
.read{border: hidden !important;}
</style>
</head>
<body>
	<div id="main" align="center">
		<!--网页正文开始-->
		<div class="content">
			<div class="read">
				<h1 class="title">${sysKnowledge.subject}</h1>
				<input type="hidden" id="knowledgeId" name="knowledgeId" value="${sysKnowledge.id}"/>
				<div class="read-info">
					发布时间:
					<fmt:formatDate value="${sysKnowledge.createtime}"
						pattern="yyyy-MM-dd" />
					<span>更新时间:<fmt:formatDate value="${sysKnowledge.createtime}"
						pattern="yyyy-MM-dd" /></span>
				</div>
				<div align="left" id = "markDiv">
				<font style="margin-left: 15px">分类书签：</font>
				<c:forEach var="knowMarkList" items="${knowMarkList}">
					<span style="margin: 5px 5px 5px 5px"><a onclick="showKnowList('${knowMarkList.id}');" href="#"> ${knowMarkList.bookmark}</a></span>
				</c:forEach>
				</div>
				<!--文章块开始-->
				<div id="read-con">
					<div style="padding-bottom: 10px;" align="left">${sysKnowledge.content}</div>
				</div>
				<!-- 附件 -->
				<div style="padding-bottom: 10px;" align="left">
					<div name="div_attachment_container">
						<div class="attachement" title="bulletinGet"></div>
						<textarea style="display: none" controltype="attachment"
							name="attachment" lablename="附件" validate="{}">${sysKnowledge.attachment}</textarea>
					</div>
				</div>
				<!--文章块结束-->
			</div>
		</div>
	</div>
</body>
</html>

