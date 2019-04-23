<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>视图定义管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/sysObjRights/SysObjRightsUtil.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/AddResourceDialog.js"></script>
<script type="text/javascript">
function addResource(alias){
	var url="/platform/system/sysQueryView/"+alias+".ht";
	AddResourceDialog({addUrl:url});
}
function sortViewList(){
		var url=  __ctx + "/platform/system/sysQueryView/sortList.ht?Q_sqlAlias_S=${querySqlDef.alias}";
		var that = this;
		DialogUtil.open({
			height:400,
			width: 700,
			title : '视图排序',
			url: url, 
			isResize: true,
			sucCall:function(dialog){
				$.ligerDialog.success("保存成功！","提示信息");
				dialog.close();
				window.location.href =  location.href.getNewUrl();
			}
		});
}
</script>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">视图定义管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link add" href="edit.ht?sqlId=${param.sqlId}"><span></span>添加</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link update update-hide" id="btnUpd" action="edit.ht"><span></span>修改</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link export" onclick="sortViewList()"><span></span>排序</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link preview" href="${querySqlDef.alias}.ht" target="_blank"><span></span>预览</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link collapse" href="#" onclick="addResource('${querySqlDef.alias}')"><span></span>添加菜单</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link back" onclick="history.back(-1);"><span></span>返回</a></div>
				</div>	
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht">
					<ul class="row">
						<li><span class="label">名称:</span><input type="text" name="Q_name_SL"  class="inputText" /></li>
						<li><span class="label">视图别名:</span><input type="text" name="Q_alias_SL"  class="inputText" /></li>
						<input type="hidden" name="sqlId" value="${sqlId }">
					<li><button class="btn">查询</button></li></ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="sysQueryViewList" id="sysQueryViewItem" requestURI="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__FILTER_FLAG__=${busQueryRule.filterFlag}" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<f:col name="id">
					<display:column title="${checkAll}" media="html" style="width:30px;">
				  		<input type="checkbox" class="pk" name="id" value="${sysQueryViewItem.id}">
					</display:column>
				</f:col>
				<f:col name="sqlAlias">
					<display:column property="sqlAlias" title="SQL别名" sortable="true" sortName="SQL_ID"></display:column>
				</f:col>
				<f:col name="name">
					<display:column property="name" title="名称" sortable="true" sortName="NAME"></display:column>
				</f:col>
				<f:col name="alias">
					<display:column property="alias" title="视图别名" sortable="true" sortName="ALIAS"></display:column>
				</f:col>
				<f:col name="initQuery">
					<display:column  title="是否初始化查询" sortable="true" sortName="INIT_QUERY">
						<c:choose>
							<c:when test="${sysQueryViewItem.initQuery eq 1 }">是</c:when>
							<c:otherwise>否</c:otherwise>
						</c:choose>
					</display:column>
				</f:col>
				<f:col name="supportGroup">
					<display:column  title="是否支持分组" sortable="true" sortName="SUPPORT_GROUP">
						<c:choose>
							<c:when test="${sysQueryViewItem.supportGroup eq 1 }">是</c:when>
							<c:otherwise>否</c:otherwise>
						</c:choose>
					</display:column>
				</f:col>
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
					<a href="edit.ht?id=${sysQueryViewItem.id}&sqlId=${param.sqlId}" class="link edit">编辑</a> |
					<a onclick="SysObjRightsUtil.setRights('${sysQueryViewItem.id}','${sysQueryViewItem.sqlAlias}')" class="link auth">授权</a> |
					<a href="${sysQueryViewItem.sqlAlias}/${sysQueryViewItem.alias}.ht" class="link preview"  target="_blank">预览</a> |
					<a href="del.ht?id=${sysQueryViewItem.id}" class="link del">删除</a>
				</display:column>
			</display:table>
			<hotent:paging tableId="sysQueryViewItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


