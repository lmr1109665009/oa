<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>手机表单管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bus/BusQueryRuleUtil.js" ></script>
<script type="text/javascript">
function newFormDef(){
	var url=__ctx + '/platform/form/bpmMobileFormDef/new.ht?categoryId=${categoryId}';
	win= DialogUtil.open({title:"新建表单", url: url, height: 450,width:550 ,isResize: false,pwin:window });	
}

function openEdit(id){
	var url="edit.ht?id=" +id;
	jQuery.openFullWindow(url);
}

function preview(id){
	var url='${ctx}/platform/form/bpmMobileFormDef/preview.ht?mobileFormId='+id;
	win= DialogUtil.open({title:"预览表单", url: url, height:680,width:500,isResize:true,pwin:window});	
}

function closeWin(){
	if(win){
		win.hidden();
	}
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
				<span class="tbar-label">手机表单管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link add" id="btnUpd" onclick="newFormDef()"><span></span>添加</a></div>
					<div class="l-bar-separator"></div>
					<!-- <div class="group"><a class="link update update-hide" id="btnUpd" action="edit.ht"><span></span>修改</a></div> -->
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
				    <div class="l-bar-separator"></div>
					<div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
				</div>
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__IS_QUERY__=0">
					<ul class="row">
						<li><span class="label">表单key:</span><input type="text" name="Q_form_key_SL"  class="inputText" />
						</li><li><span class="label">表单标题:</span><input type="text" name="Q_subject_SL"  class="inputText" />
						</li><li><span class="label">表名:</span><input type="text" name="Q_table_Name_SL"  class="inputText" />
						</li>
						<li><button class="btn">查询</button></li></ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="bpmMobileFormDefList" id="bpmMobileFormDefItem" requestURI="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__FILTER_FLAG__=${busQueryRule.filterFlag}" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
					<display:column title="${checkAll}" media="html" style="width:30px;">
				  		<input type="checkbox" class="pk" name="id" value="${bpmMobileFormDefItem.id}">
					</display:column>
					<display:column property="formKey" title="表单key" sortable="true" sortName="form_key"></display:column>
					<display:column property="tableName" title="表名" sortable="true" sortName="TABLE_NAME"></display:column>
					<display:column property="subject" title="表单主题" sortable="true" sortName="subject"></display:column>
					<display:column property="categoryName" title="表单分类" sortable="false"></display:column>
					<display:column  title="创建时间" sortable="true" sortName="CREATE_TIME">
						<fmt:formatDate value="${bpmMobileFormDefItem.createTime}" pattern="yyyy-MM-dd"/>
					</display:column>
					<display:column title="是否发布" sortable="true" sortName="IS_PUBLISHED">
						<c:choose>
							<c:when test="${bpmMobileFormDefItem.isPublished==1}">
						    	<span class="green">发布</span>
						   	</c:when>
					       	<c:otherwise>
								<span class="red">未发布</span>
						   	</c:otherwise>
						</c:choose>
					</display:column>
				<display:column title="版本信息" sortable="true" sortName="version">
				 <span　style="font-weight: bold;">版本${bpmMobileFormDefItem.version}</span>
				<a href="versions.ht?formKey=${bpmMobileFormDefItem.formKey}">管理更多(${bpmMobileFormDefItem.versionCount})</a>
				
				</display:column>
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
					<c:if test="${bpmMobileFormDefItem.isPublished!=1}">
						<a href="publish.ht?id=${bpmMobileFormDefItem.id}" class="link deploy" >发布</a> | 
					</c:if>
					<a onclick="openEdit(${bpmMobileFormDefItem.id})" class="link edit">编辑表单</a> | 
					<a onclick="preview(${bpmMobileFormDefItem.id})" class="link  preview">预览</a>
				</display:column>
			</display:table>
			<hotent:paging tableId="bpmMobileFormDefItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>