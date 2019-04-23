<%@page import="com.hotent.platform.model.system.GlobalType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>知识库管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bus/BusQueryRuleUtil.js" ></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerComboBox.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/htCatCombo.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.ztree.js"></script>
<script type="text/javascript">
		var dialogCategory
		$(function(){
			dialogCategory = $("#dialogCategory").html();
		});
		var dialog=null;
		//重设分类
		function setCategory(){
			$("#dialogCategory").html(dialogCategory);
			$('.catComBo').each(function() {
				$(this).htCatCombo();
			});
			
			var knowKeys=getKnowKey();
			if(knowKeys==""){
				$.ligerDialog.warn('还没有选择,请选择一项记录!','提示');
				return;
			}
			if(dialog==null){
				dialog=$.ligerDialog.open({title:"设置分类",target:$("#dialogCategory"),width:400,height:250,buttons:
					[ {text : '确定',onclick: setCategoryPost},
					{text : '取消',onclick: function (item, dialog) {
							dialog.hide();
						}
					}]
				});	
			}
			dialog.show();
		}
		//设置分类想后台发送请求
		function setCategoryPost(item, dialog){
			var typeId=$("#typeId").val();
			if(typeId==""){
				$.ligerDialog.warn('请选择分类','提示');
				return;
			}
			var knowKeys=getKnowKey();
			var params={knowKeys:knowKeys,typeId:typeId};
			var url="${ctx}/platform/system/sysKnowledge/setCategory.ht";
			$.post(url,params,function(responseText){
				var obj=new com.hotent.form.ResultMessage(responseText);
				if(obj.isSuccess()){
					$.ligerDialog.success('操作成功!','提示',function(){
						var url=location.href.getNewUrl();
						location.href=url;
						dialog.close();
					});
				}
				else{
					$.ligerDialog.err('提示','操作失败!',obj.getMessage());
				}
			});
		}
		//获取id
		function getKnowKey(){
			var aryChk=$("input:checkbox[name='id']:checked");
			if(aryChk.length==0) return "";
			var aryKowKey=[];
			aryChk.each(function(){
				aryKowKey.push($(this).val());
			});
			return aryKowKey.join(",");
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
				<span class="tbar-label">知识库管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<c:if test="${isSuperAdmin == true}">
						<div class="group"><a class="link add" onclick="$.openFullWindow('edit.ht?typeId=${typeId}')"  href="#"><span></span>添加</a></div>
						<div class="l-bar-separator"></div>
						<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
						<div class="l-bar-separator"></div>
					</c:if>
				    <c:if test="${isSuperAdmin == false}">
						<c:forEach var="perList" items="${permissionList}">
						    <c:if test="${perList=='add'}">
								<div class="group"><a class="link add" onclick="$.openFullWindow('edit.ht?typeId=${typeId}')"  href="#"><span></span>添加</a></div>
								<div class="l-bar-separator"></div>
							</c:if>
							<c:if test="${perList == 'del'}">
								<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
								<div class="l-bar-separator"></div>
							</c:if>
						</c:forEach>
					</c:if>
						<div class="l-bar-separator"></div>
					    <div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
				</div>	
			</div>
			<div class="panel-search">
				<form id="searchForm" method="post" action="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__IS_QUERY__=0&typeId=${typeId}">
					<ul class="row">
						<li><span class="label">主题:</span><input type="text" name="Q_subject_SL"  class="inputText" value="${param['Q_subject_SL']}"/></li>
						<li><span class="label">创建人:</span><input type="text" name="Q_creator_SL"  class="inputText" value="${param['Q_creator_SL']}"/></li>
						<li><span class="label">创建时间:</span> <input  name="Q_begincreatetime_DL"  class="inputText date" value="${param['Q_begincreatetime_DL']}" />
						<span class="label label_line">_ </span><input  name="Q_endcreatetime_DG" class="inputText date" value="${param['Q_endcreatetime_DG']}" /></li>
						<li><button class="btn">查询</button></li></ul>
				</form>
			</div>
		</div>
		<div class="panel-body">
			<input type="hidden" name="typeId" value="${typeId}" title="分类ID"></input>
	    	<c:set var="checkAll">
				<input type="checkbox" id="chkall"/>
			</c:set>
		    <display:table name="sysKnowledgeList" id="sysKnowledgeItem" requestURI="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__FILTER_FLAG__=${busQueryRule.filterFlag}" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<f:col name="id">
					<display:column title="${checkAll}" media="html" style="width:30px;">
				  		<input type="checkbox" class="pk" name="id" value="${sysKnowledgeItem.id}">
					</display:column>
				</f:col>
				<f:col name="subject">
					<display:column property="subject" title="主题" sortable="true" sortName="SUBJECT"></display:column>
				</f:col>
				<f:col name="creator">
					<display:column property="creator" title="创建人" sortable="true" sortName="CREATOR"></display:column>
				</f:col>
				<f:col name="createtime">
					<display:column  title="创建时间" sortable="true"  sortName="CREATETIME">
						<fmt:formatDate value="${sysKnowledgeItem.createtime}" pattern="yyyy-MM-dd"/>
					</display:column>
				</f:col>
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
				<c:if test="${isSuperAdmin == true}">
					<a href="#" onclick="$.openFullWindow('edit.ht?id=${sysKnowledgeItem.id}&typeId=${typeId}')" class="link edit">编辑</a> |
					<a onclick="$.openFullWindow('get.ht?id=${sysKnowledgeItem.id}')"  class="link detail">详情</a>  | 
					<a href="del.ht?id=${sysKnowledgeItem.id}" class="link del">删除</a>
			    </c:if>
			    <c:if test="${isSuperAdmin == false}">
					<c:forEach var="perList" items="${permissionList}">
					    <c:if test="${perList=='edit'}">
							<a href="#" onclick="$.openFullWindow('edit.ht?id=${sysKnowledgeItem.id}&typeId=${typeId}')" class="link edit">编辑</a> |
						</c:if>
						<a onclick="$.openFullWindow('get.ht?id=${sysKnowledgeItem.id}')"  class="link detail">详情</a>  | 
						<c:if test="${perList == 'del'}">
							<a href="del.ht?id=${sysKnowledgeItem.id}" class="link del">删除</a>
						</c:if>
					</c:forEach>
				</c:if>
				
				</display:column>
			</display:table>
			<hotent:paging tableId="sysKnowledgeItem"/>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
	<div id="dialogCategory" style="width: 380px;display: none;">
	<div class="panel">
			<div class="panel-body">
				<form id="frmDel">
					<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<th style="width:150px;">设置分类:</th>
							<td>
								<input class="catComBo" catKey="KNOWLEDGE_TYPE" valueField="typeId" catValue="" name="typeName" height="150" width="150"/>
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
</div>
</body>
</html>


