
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>选择自定义表</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript">
	var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)
	function getResult(){
		var obj = $(":radio:checked");
		if(obj.length < 1){
			return -1;
		}
		return {jobname:obj.attr("jobname"), jobid: obj.attr("jobid")};
	}
	
	$(function(){
		window.top.__resultData__= -1;
		$("body").bind("click",function(){
			window.top.__resultData__= getResult();
		});
	});
	 
</script>
<style type="text/css">
	div.bottom{text-align: center;padding-top: 10px;}
	.l-panel-bbar-inner {position: relative;left:0;rigth:0;margin-top: -1px;padding: 10px;}
</style>
</head>
<body>
	<div id="defLayout">
		<div id="centerLayout" position="center"  style="overflow: auto;">
			<div class="panel-top" usesclflw="true">
				<div class="panel-toolbar">
					<div class="toolBar">
						<div class="group"><a class="link search search-show" id="btnSearch"><span></span>查询</a></div>
					</div>	
				</div>
				<div class="panel-search">
					<form id="searchForm" method="post" action="selector.ht">
						<ul class="row">
								<li><span class="label">职务名称:</span><input type="text" name="Q_jobname_SL"  class="inputText" size="15" value="${param['Q_jobname_SL']}"/></li>
								<li><input type="hidden" name="enterpriseCode" value="${enterpriseCode}"></li>
						</ul>		
					</form>
				</div>
			</div>
		
	    	<display:table name="jobList" id="jobItem" requestURI="getEnterpriseinfoList.ht" sort="external"  export="false"  class="table-grid">
				<display:column title="">
					<input type="radio" name="rtn" class="pk" jobname="${jobItem.jobname}" jobId="${jobItem.jobid}"/>
				</display:column>
				<display:column property="jobname" title="职务名称"> </display:column>
				<display:column property="enterpriseCode" title="企业编码"></display:column>
				<display:column property="grade" title="级别"></display:column>
			</display:table>
			<hotent:paging tableId="jobItem"/>
		</div>
	</div> 
</body>
</html>
