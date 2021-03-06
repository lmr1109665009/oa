<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>SYS_DATA_SOURCE管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/ImportExportXmlUtil.js" charset="UTF-8"></script>	
<script type="text/javascript">

//导出系统数据源
function exportXml(){
	
	var tableIds = ImportExportXml.getChkValue('pk');
	var url=__ctx + "/platform/system/sysDataSource/exportXml.ht?tableIds="+tableIds;
	if (tableIds  ==''){
		$.ligerDialog.confirm('还没有选择，是否导出全部？','提示信息',function(rtn) {
			if(rtn) {
				var form=new com.hotent.form.Form();
				form.creatForm("form", url);
				form.submit();
			}
		});
		
	}else{
	$.ligerDialog.confirm('确认导出吗？','提示信息',function(rtn) {
		if(rtn) {
			var form=new com.hotent.form.Form();
			form.creatForm("form", url);
			form.submit();
		}
	});
	}
}
//导入系统数据源
function importXml(){
	var url=__ctx + "/platform/system/sysDataSource/import.ht";
	ImportExportXml.showModalDialog({url:url,title:'导入系统数据源'});
}
</script>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">SYS_DATA_SOURCE管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link add" href="edit.ht"><span></span>添加</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
					<div class="l-bar-separator"></div>
					<div class="group">
					<a onclick="exportXml()"  class="link export"><span></span>导出</a>
					</div>
			   		<div class="l-bar-separator"></div>
					<div class="group">
					<a onclick="importXml()"  class="link import"><span></span>导入</a>
					</div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
				</div>	
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht">
					<ul class="row">
						<li>
						<span class="label">名称:</span><input type="text" name="Q_name_SL"  class="inputText" value="${param['Q_name_SL'] }" />
						</li><li><span class="label">别名:</span><input type="text" name="Q_alias_SL"  class="inputText" value="${param['Q_alias_SL'] }" />
					</li><li><button class="btn">查询</button></li></ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="sysDataSourceList" id="sysDataSourceItem" requestURI="list.ht" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<display:column title="${checkAll}" media="html" style="width:30px;">
			  		<input type="checkbox" class="pk" name="id" value="${sysDataSourceItem.id}">
				</display:column>
				<display:column property="name" title="名称" sortable="true" sortName="NAME_"></display:column>
				<display:column property="alias" title="别名" sortable="true" sortName="ALIAS_"></display:column>
				<display:column property="dbType" title="数据库类型" sortable="true" sortName="DB_TYPE_"></display:column>
				<display:column property="initOnStart" title="初始化容器" sortable="true" sortName="INIT_ON_START_"></display:column>
				<display:column property="enabled" title="是否生效" sortable="true" sortName="ENABLED_"></display:column>
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
					<a href="edit.ht?id=${sysDataSourceItem.id}" class="link edit">编辑</a> |
					<a href="del.ht?id=${sysDataSourceItem.id}" class="link del">删除</a>
				</display:column>
			</display:table>
			<hotent:paging tableId="sysDataSourceItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


