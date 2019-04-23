<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>常用联系人组管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript">
	$(function(){
		$("tr.odd,tr.even").unbind("hover");
		$("tr.odd,tr.even").click(function(){
			$(this).siblings().removeClass("over").end().addClass("over");
		});
	}); 
</script>
<style type="text/css">
	div.bottom{text-align: center;padding-top: 10px;}
	body {overflow: hidden;}
</style>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
				</div>	
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="selector.ht?__FILTERKEY__=${busQueryRule.filterKey}&__IS_QUERY__=0">
					<div class="row">
						<span class="label">组名:</span><input type="text" name="Q_groupName_SL"  class="inputText" />
					</div>
				</form>
			</div>
		</div>
		<div class="panel-body">
		    <display:table name="messageLinkmanGroupList" id="messageLinkmanGroupItem" requestURI="selector.ht?__FILTERKEY__=${busQueryRule.filterKey}&__FILTER_FLAG__=${busQueryRule.filterFlag}" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<display:column title="选择" media="html" style="width:30px;">
					<input type="radio" class="pk" name="data" value="${messageLinkmanGroupItem.userIds}#${messageLinkmanGroupItem.users}">
				</display:column>
				<display:column property="groupName" title="组名" sortable="true" sortName="GROUP_NAME"></display:column>
				<display:column property="users" title="常用联系人" sortable="true" sortName="USERS" maxLength="80"></display:column>
				<display:column  title="创建时间" sortable="true" sortName="CREATE_TIME">
					<fmt:formatDate value="${messageLinkmanGroupItem.createTime}" pattern="yyyy-MM-dd"/>
				</display:column>
			</display:table>
			<hotent:paging tableId="messageLinkmanGroupItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


