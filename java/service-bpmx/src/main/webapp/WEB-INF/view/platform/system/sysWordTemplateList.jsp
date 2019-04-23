<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>word套打模板管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
<script type="text/javascript">
function openWindow(operation, id){
	var url = operation+".ht";
	if(id) url += "?id="+id;
	$.openFullWindow(url.getNewUrl());
}
</script>

<style>
.label1 {
    display: inline-block;
    vertical-align: middle;
    white-space: nowrap;
    padding-bottom: 7px;
    padding-right: 7px;
    width: 30px;
    text-align: right;
    margin-left: -10px;
}

.group1{
   margin-left: 10px;
   
}
.separator_line {

 text-align: right;
}
</style>

</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">word套打模板管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link add" href="javascript:;" onclick="openWindow('edit');"><span></span>添加</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
				</div>	
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht?Q_tableId_L=${param.Q_tableId_L}">
					<ul class="row">
						<li><span class="label">报表名称:</span><input type="text" name="Q_name_SL"  class="inputText" value="${param.Q_name_SL}"/>
						</li>
						<li><span class="label">创建时间:</span><input  name="Q_begincreatetime_DL"  class="inputText date" value="${param.Q_begincreatetime_DL}"/>
						<span class="label label_line">_ </span><input  name="Q_endcreatetime_DG" class="inputText date" value="${param.Q_endcreatetime_DG}"/>
						</li>
						<li><span class="label">类型:</span>
						<select name="Q_type_SL"  class="inputText">
							<option value="">请选择……</option>
							<option value="0" <c:if test="${param.Q_type_SL eq '0'}">selected="selected"</c:if>>自定义表</option>
							<option value="1" <c:if test="${param.Q_type_SL eq '1'}">selected="selected"</c:if>>SQL</option>
						</select>
						</li>
						<li><button class="btn">查询</button></li></ul>
					</div>
					
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="sysWordTemplateList" id="sysWordTemplateItem" requestURI="list.ht" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<display:column title="${checkAll}" media="html" style="width:30px;">
			  		<input type="checkbox" class="pk" name="id" value="${sysWordTemplateItem.id}">
				</display:column>
				<display:column property="name" title="报表名称" sortable="true" sortName="NAME_" maxLength="80"></display:column>
				<display:column property="alias" title="报表别名" sortable="true" sortName="ALIAS_" maxLength="80"></display:column>
				<display:column  title="创建时间" sortable="true" sortName="CREATETIME_">
					<fmt:formatDate value="${sysWordTemplateItem.createtime}" pattern="yyyy-MM-dd"/>
				</display:column>
				<display:column title="类型" sortable="true" sortName="TYPE_">
					<c:if test="${sysWordTemplateItem.type eq '0'}">自定义表,表名:<span style="color: red;font-size: 16px;">${sysWordTemplateItem.tableName}</span></c:if>
					<c:if test="${sysWordTemplateItem.type eq '1'}">SQL</c:if>
				</display:column>
				<display:column title="管理" media="html" style="width:200px;" class="opsBtnb">
					<a href="javascript:;" class="link edit" onclick="openWindow('edit', '${sysWordTemplateItem.id}');">编辑</a>  | 
					<a href="javascript:;" class="link detail" onclick="openWindow('get', '${sysWordTemplateItem.id}');">详情</a>  | 
					<c:if test="${sysWordTemplateItem.fileId >0}">
					<a href="javascript:;" class="link preview" onclick="preview('${sysWordTemplateItem.alias}');">预览</a>  | 
					</c:if>
					<a href="del.ht?id=${sysWordTemplateItem.id}" class="link del">删除</a>
				</display:column>
			</display:table>
			<hotent:paging tableId="sysWordTemplateItem"/>
		</div><!-- end of panel-body -->				
		
		
	</div> <!-- end of panel -->
	<jsp:include page="sysWordTemplateInc.jsp"></jsp:include>
</body>
</html>


