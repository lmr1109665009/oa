<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>自定义SQL定义管理</title>
<%@include file="/commons/include/get.jsp" %>
<f:link href="tree/zTreeStyle.css"></f:link>
<script type="text/javascript" src="${ctx }/js/lg/plugins/ligerWindow.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerComboBox.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.ztree.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/htCatCombo.js"></script>
<script type="text/javascript" src="${ctx}/js/jquery/plugins/jquery.qtip.js" ></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/ImportExportXmlUtil.js" charset="UTF-8"></script>
<script type="text/javascript">

var dialog=null;

function setCategory(){
	var ids=getIds();
	if(ids==""){
		$.ligerDialog.warn('还没有选择,请选择一项记录!','提示');
		return;
	}
	$.initCatComboBox();
	if(dialog==null){
		dialog=$.ligerDialog.open({title:'设置分类',target:$("#dialogCategory"),width:400,height:250,buttons:
			[ {text : '确定',onclick: setCategoryOk},
			{text : '取消',onclick: function (item, dialog) {
					dialog.hide();
				}
			}]});	
	}
	dialog.show();
	justifyMargin(10,3,1000);
}


//导出自定义Sql查询
function exportXml(){	
	var tableIds = ImportExportXml.getChkValue('pk');
	var url=__ctx + "/platform/system/sysQuerySqlDef/exportXml.ht?tableIds="+tableIds;
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
//导入自定义Sql查询
function importXml(){
	var url=__ctx + "/platform/system/sysQuerySqlDef/import.ht";
	ImportExportXml.showModalDialog({url:url,title:'导入自定义Sql查询'});
}


//设置分类
function setCategoryOk(item, dialog){
	var categoryId=$("#categoryId").val();
	if(categoryId==""){
		$.ligerDialog.warn('请选择分类','提示');
		return;
	}
	var ids = getIds();
	var params={ids:ids,categoryId:categoryId};
	var url="${ctx}/platform/system/sysQuerySqlDef/setCategory.ht";
	$.post(url,params,function(responseText){
		var obj=new com.hotent.form.ResultMessage(responseText);
		if(obj.isSuccess()){
			$.ligerDialog.success('保存成功!','提示',function(){
				dialog.hide();
				var url=location.href.getNewUrl();
				location.href=url;
			});
		}
		else{
			$.ligerDialog.err('提示','保存失败!',obj.getMessage());
		}
	});
}

function getIds(){
	var aryChk=$("input:checkbox[name='id']:checked");
	if(aryChk.size()==0) return "";
	var aryTableId=[];
	aryChk.each(function(){
		aryTableId.push($(this).val());
	});
	return aryTableId.join(",");
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
				<span class="tbar-label">自定义SQL定义管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link add" href="edit.ht?categoryId=${categoryId}"><span></span>添加</a></div>
					<div class="l-bar-separator"></div>
					<!-- <div class="group"><a class="link update update-hide" id="btnUpd" action="edit.ht"><span></span>修改</a></div> -->
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a onclick="setCategory()"  class="link category"><span></span>设置分类</a></div>
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
			
					<%-- <div class="l-bar-separator"></div>
					<div class="group"><a class="link init" href="${ctx }/platform/system/sysQueryView/updatAllTemplate.ht"><span></span>初始化所有视图(该按钮不开放)</a></div>
					 --%>
				</div>	
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__IS_QUERY__=0">
					<ul class="row">
						<li><span class="label">名称:</span><input type="text" name="Q_name_SL"  class="inputText" value="${param['Q_name_SL']}" />
						</li><li><span class="label" >sql语句:</span><input type="text" name="Q_sql_SL"  class="inputText" value="${param['Q_sql_SL']}" />
						</li><li><span class="label">数据源名称:</span><input type="text" name="Q_dsname_SL"  class="inputText" value="${param['Q_dsname_SL']}" />
						</li><li><span class="label" >按钮定义:</span><input type="text" name="Q_buttonDef_SL"  class="inputText" value="${param['Q_buttonDef_SL']}" />
						</li><li><button class="btn">查询</button>
					</li></ul>
				</form>
			</div>
		</div>
		
		<div class="panel-body">
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="sysQuerySqlDefList" id="sysQuerySqlDefItem" requestURI="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__FILTER_FLAG__=${busQueryRule.filterFlag}" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<f:col name="id">
					<display:column title="${checkAll}" media="html" style="width:30px;">
				  		<input type="checkbox" class="pk" name="id" value="${sysQuerySqlDefItem.id}">
					</display:column>
				</f:col>
				<f:col name="categoryName">
					<display:column property="categoryName" title="分类名称" sortable="false"></display:column>
				</f:col>
				<f:col name="name">
					<display:column property="name" title="名称" sortable="true" sortName="name"></display:column>
				</f:col>
				<f:col name="alias">
					<display:column property="alias" title="别名" sortable="true" sortName="alias"></display:column>
				</f:col>
				<f:col name="sql">
					<display:column property="sql" title="sql语句" sortable="true" sortName="sql_" maxLength="80"></display:column>
				</f:col>
				<f:col name="dsname">
					<display:column property="dsname" title="数据源名称" sortable="true" sortName="dsName"></display:column>
				</f:col>
				<display:column title="管理" media="html" style="width:200px;" class="opsBtnb">
					<a href="edit.ht?id=${sysQuerySqlDefItem.id}" class="link edit">编辑</a> |
					<a href="${ctx}/platform/system/sysQueryView/list.ht?sqlId=${sysQuerySqlDefItem.id}&Q_sqlAlias_S=${sysQuerySqlDefItem.alias}" class="link detail">视图列表</a> |
					<a href="del.ht?id=${sysQuerySqlDefItem.id}" class="link del">删除</a>
				</display:column>
			</display:table>
			<hotent:paging tableId="sysQuerySqlDefItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
	<div id="dialogCategory" style="width: 380px;display: none;">
		<div class="panel">
			<div class="panel-body">
				<form id="frmDel">
					<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<th style="width:113px;text-align:center;">设置分类:</th>
							<td>
								<input class="catComBo" catKey="FORM_TYPE" valueField="categoryId" catValue="" name="typeName" height="150" width="235"/>
							</td>
						</tr>
					</table>
				
				</form>
			</div>
		</div>
	</div>
</body>
</html>


