<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>知识库权限管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bus/BusQueryRuleUtil.js" ></script>
<script type="text/javascript">
//打开dialog
function searchDialog(){
	var url=__ctx +"/platform/system/sysKnowledgePer/search.ht?num=1";
	url=url.getNewUrl();
	DialogUtil.open({
        height:350,
        width: 650,
        title : '用户权限查询',
        url: url, 
        isResize: true
    });
};

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
				<span class="tbar-label">知识库权限管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link update" id="btnUpd" action="edit.ht?typeId=${typeId}"><span></span>修改</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link add" href="edit.ht?id=${id}"><span></span>添加</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link preview" href="#" onclick="searchDialog()"><span></span>人员权限查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
				    <div class="l-bar-separator"></div>
				</div>	
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__IS_QUERY__=0">
					<ul class="row">
						<li><span class="label">名称:</span><input type="text" name="Q_name_SL"  class="inputText"  value="${param['Q_name_SL']}"/></li>
						<li><span class="label">人员组织:</span><input type="text" name="Q_creator_SL"  class="inputText"  value="${param['Q_creator_SL']}"/></li>
						<li><span class="label">创建时间:</span> <input  name="Q_begincreatetime_DL"  class="inputText date" value="${param['Q_begincreatetime_DL']}" />
						<span class="label label_line">_ </span><input  name="Q_endcreatetime_DL" class="inputText date" value="${param['Q_endcreatetime_DG']}" /></li>
					<li><button class="btn">查询</button></li></ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="sysKnowPerRefList" id="sysKnowPerRefItem" requestURI="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__FILTER_FLAG__=${busQueryRule.filterFlag}" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<f:col name="id">
					<display:column title="${checkAll}" media="html" style="width:30px;">
				  		<input type="checkbox" class="pk" name="id" value="${sysKnowPerRefItem.id}">
					</display:column>
				</f:col>
				<f:col name="name">
					<display:column  style="width:130px;" property="name" title="名称" sortable="true" sortName="name"></display:column>
				</f:col>
				<f:col name="creator">
					<display:column  style="width:130px;" property="creator" title="人员组织" sortable="true" sortName="owner"></display:column>
				</f:col>
				<f:col name="createtime">
					<display:column  style="width:130px;" title="创建时间" sortable="true"  sortName="CREATETIME">
						<fmt:formatDate value="${sysKnowPerRefItem.createtime}" pattern="yyyy-MM-dd"/>
					</display:column>
				</f:col>
				<display:column title="管理"  media="html" style="width:150px;" class="opsBtnb">
					<a href="edit.ht?id=${sysKnowPerRefItem.id}&id=${id}" class="link edit">编辑</a> |
					<a href="get.ht?id=${sysKnowPerRefItem.id}&id=${id}" class="link detail">详情</a>  |
					<a href="del.ht?id=${sysKnowPerRefItem.id}" class="link del">删除</a> 
				</display:column>
			</display:table>
			<hotent:paging tableId="sysKnowPerRefItem"/>
			<input type="hidden" name="id" value="${id}">
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


