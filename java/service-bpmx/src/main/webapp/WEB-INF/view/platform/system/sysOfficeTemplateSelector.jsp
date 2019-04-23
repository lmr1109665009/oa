<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统模版管理</title>
<%@include file="/commons/include/get.jsp" %>
<style type="text/css">
	html,body{ padding:0px; margin:0; width:100%;height:100%;overflow: hidden;}
</style>	
<script type="text/javascript">
/* var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)

$(function(){
	$("#defLayout").ligerLayout({ topHeight: 110,bottomHeight:40,allowTopResize:false,allowBottomResize:false});
	
	$("tr.odd,tr.even").unbind("hover");
	$("tr.odd,tr.even").click(function(){
		var obj=$(this);
		$("input[name='rtn']",obj).attr("checked","checked");
		$(this).siblings().removeClass("over").end().addClass("over");
	});

});
 */
</script>
</head>
<body>
		<div class="panel-toolbar">
			<div class="group">
				<a class="link search" id="btnSearch">查询</a>
			</div>
		</div>
		<div class="panel-search">
			<form id="searchForm" method="post" action="selector.ht?templatetype=${templatetype}">
				<ul class="row">
					<li><span class="label">主题:</span><input type="text" name="Q_subject_SL"  class="inputText" value="${param['Q_subject_SL']}"/></li>
					<!-- <li><span class="label">模版类型:</span>
					<select name="Q_templatetype_S">
						<option value="">请选择</option>
						<option value="1">普通模版</option>
						<option value="2">套红模版</option>
					</select> </li> -->
				</ul>
			</form>
		</div>
		<div position="center"  style="overflow: auto;">
	    	 <display:table name="sysOfficeTemplateList" id="sysOfficeTemplateItem" requestURI="selector.ht" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				<display:column  media="html" style="width:30px;">
					  <input type="radio" class="rtn" name="rtn" value="${sysOfficeTemplateItem.id},${sysOfficeTemplateItem.subject},${sysOfficeTemplateItem.fileid}">
				</display:column>
				<display:column property="subject" title="主题" sortable="true" sortName="subject"></display:column>
				<display:column title="模版类型" sortable="true" sortName="templatetype">
					<c:choose>
						<c:when test="${sysOfficeTemplateItem.templatetype==1 }">普通模版</c:when>
						<c:otherwise>
							套红模版
						</c:otherwise>
					</c:choose>
				</display:column>
				
			</display:table>
			<hotent:paging tableId="sysOfficeTemplateItem"/>
		</div>
</body>
</html>


