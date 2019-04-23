<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>职务表管理</title>
<%@include file="/commons/include/get.jsp" %>

</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">职务表管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link add" href="edit.ht"><span></span>添加</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link update update-hide" id="btnUpd" action="edit.ht"><span></span>修改</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
				</div>	
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht">
					<ul class="row">
							<li><span class="label">名称:</span><input type="text" name="Q_jobname_SL"  class="inputText" value="${param['Q_jobname_SL']}"/>
						</li>
						<%-- <li><span class="label">代码:</span><input type="text" name="Q_jobcode_SL"  class="inputText" value="${param['Q_jobcode_SL']}" />
						</li> --%>
						<li><span class="label">描述:</span><input type="text" name="Q_jobdesc_SL"  class="inputText" value="${param['Q_jobdesc_SL']}" />
					</li>
					<li><button class="btn">查询</button></li></ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="jobList" id="jobItem" requestURI="list.ht" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<display:column title="${checkAll}" media="html" style="width:30px;">
			  		<input type="checkbox" class="pk" name="jobid" value="${jobItem.jobid}">
				</display:column>
				<display:column property="jobname" title="职务名称" sortable="true" sortName="JOBNAME"></display:column>
				<%-- <display:column property="jobcode" title="代码" sortable="true" sortName="JOBCODE"></display:column> --%> 
				<display:column property="grade" title="级别" sortable="true" sortName="GRADE"></display:column>
				
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
					<a href="edit.ht?jobid=${jobItem.jobid}" class="link edit">编辑</a> |
					<a href="get.ht?jobid=${jobItem.jobid}" class="link detail">详情</a>  | 
					<a href="del.ht?jobid=${jobItem.jobid}" class="link del">删除</a>
				</display:column>
			</display:table>
			<hotent:paging tableId="jobItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


