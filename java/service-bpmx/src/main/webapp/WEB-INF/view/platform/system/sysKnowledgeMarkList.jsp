<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>SYS_ KNOWLEDGE_MARK管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bus/BusQueryRuleUtil.js" ></script>
<script type="text/javascript" >
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
</head>
<body>
	<div class="panel">
		<c:if test="${!empty busQueryRule.filterList && fn:length(busQueryRule.filterList) >1}">
			<div class="l-tab-links">
				<ul style="left: 0px; ">
					<c:forEach items="${busQueryRule.filterList}" var="filter">
						<li tabid="${filter.key}" <c:if test="${busQueryRule.filterKey ==filter.key}"> class="l-selected"</c:if>>
							<a href="list.ht?__FILTERKEY__=${filter.key}" title="${filter.name}">${filter.desc}</a>
						</li>
					</c:forEach>
				</ul>
			</div>
		</c:if>
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">书签管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
				    <div class="l-bar-separator"></div>
					<div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
				</div>
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__IS_QUERY__=0">
					<ul class="row">
						<li><span class="label">书签:</span><input type="text" name="Q_bookmark_SL"  class="inputText" value="${param['Q_bookmark_SL']}"/></li>
						<li><span class="label">创建时间从:</span> <input  name="Q_begincreatetime_DL" value="${param['Q_begincreatetime_DL']}"  class="inputText date" />
						<span class="label label_line">_ </span><input  name="Q_endcreatetime_DG" value="${param['Q_endcreatetime_DG']}"  class="inputText date" /></li>
					<li><button class="btn">查询</button></li></ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="sysKnowledgeMarkList" id="sysKnowledgeMarkItem" requestURI="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__FILTER_FLAG__=${busQueryRule.filterFlag}" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<f:col name="id">
					<display:column title="${checkAll}" media="html" style="width:30px;text-align:center">
				  		<input type="checkbox" class="pk" name="id" value="${sysKnowledgeMarkItem.id}">
					</display:column>
				</f:col>
				<f:col name="bookmark">
					<display:column property="bookmark" title="书签名称" sortable="true" sortName="BOOKMARK" style="text-align:center" maxLength="80"></display:column>
				</f:col>
				<display:column  style="text-align:center" title="创建时间" sortable="true" sortName="CREATETIME">
					<fmt:formatDate value="${sysKnowledgeMarkItem.createtime}" pattern="yyyy-MM-dd"/>
				</display:column>
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
					<a href="#" onclick="showKnowList('${sysKnowledgeMarkItem.id}')" class="link">打开文章列表</a> |
					<c:if test="${isSuperAdmin == true}">
						<a href="del.ht?id=${sysKnowledgeMarkItem.id}" class="link del">删除</a>
					</c:if>
				</display:column>
			</display:table>
			<hotent:paging tableId="sysKnowledgeMarkItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


