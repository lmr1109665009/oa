<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<title>流程引用</title>
<%@include file="/commons/include/get.jsp"%>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/ProcessUrgeDialog.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/FlowUtil.js" ></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/CheckVersion.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/SelectUtil.js" ></script>
<script type="text/javascript">
	$(function(){
		$("a.link.search").unbind("click");

		$("#processRunItem>tbody").find("tr").bind('click', function() {
			var ch=$(this).find(":checkbox");
			window.parent.selectMulti(ch);
		});
		
		$("#chkall").click(function(){
			var checkAll=false;
			if($(this).attr("checked")){
				checkAll=true;	
			}
			var checkboxs=$(":checkbox",  $("#processRunItem>tbody"));
			checkboxs.each(function(){
				if(checkAll){
					window.parent.selectMulti(this);
				}
			})
		});
	});

	function onSearch(){
		var referDefKey = $("#selectRefer",window.parent.document).val();
		$("#referDefKey").val(referDefKey);
		$("#searchForm").submit();
	};

	function showDetail(obj){
		var url = $(obj).attr("action");
		DialogUtil.open({
			height:700,
			width: 900,
			title : '',
			url: url, 
			isResize: true,
		});
	};
</script>
</head>
<body>
	<div class="panel">
			<input type="hidden" id="isSingle" value="${isSingle}"/>
			<div class="panel-top">
				<div class="toolBar">
				<div class="panel-toolbar">
						<div class="group">
							<a class="link search"  onclick="onSearch()" id="btnSearch"><span></span>查询</a>
						</div>
						<div class="l-bar-separator"></div>
					    <div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
					    <div class="l-bar-separator"></div>
						<div class="group"><a href="javascript:;" class="link reset" onclick="$.clearQueryForm();"><span></span>重置</a></div>
					</div>
				</div>
				<div class="panel-search">
					<form id="searchForm" method="post" action="${ctx}/platform/system/sysPlan/processRunList.ht?isSingle=${isSingle}">
						<ul class="row">
							<li><span class="label">工单号:</span>
							<input size="18" type="text" name="Q_globalFlowNo_SL" class="inputText"  value="${param['Q_globalFlowNo_SL']}" />
							</li>
							<li><span class="label">标题:</span>
							<input size="18" type="text" name="Q_subject_SL" class="inputText"  value="${param['Q_subject_SL']}" />
							</li>
							<li><span class="label">创建日期:</span>
							<input size="18" name="Q_begincreatetime_DL" class="inputText datePicker" datetype="1"  value="${param['Q_begincreatetime_DL']}" />
							<span class="label label_line">_ </span>
							<input size="18" name="Q_endcreatetime_DG" class="inputText datePicker" datetype="2" value="${param['Q_endcreatetime_DG']}" />
						    </li>
						<li><button class="btn">查询</button></li></ul>
						<div style="display: none;">
							<input id="referDefKey" type="hidden" name="referDefKey" />
						</div>
					</form>
				</div>
			</div>
			<div class="panel-body">
				<c:set var="checkAll">
					<input type="checkbox" id="chkall" />
				</c:set>
				<display:table name="processRunList" id="processRunItem" requestURI="${ctx}/platform/system/sysPlan/processRunList.ht?isSingle=${isSingle}" sort="external" cellpadding="1"
					cellspacing="1" class="table-grid">
					<display:column title="${checkAll}" media="html" style="width:30px;">
						<input type="checkbox" class="pk" name="runId" value='${processRunItem.runId}#${processRunItem.subject }' />
					</display:column>
					<display:column title="工单号" sortable="true" property="globalFlowNo"></display:column>
					<display:column title="请求标题" sortable="true" sortName="subject" style="text-align:left">
						${f:subString(processRunItem.subject)}
					</display:column>
					<display:column title="创建日期" sortable="true" sortName="createtime">
						<fmt:formatDate value="${processRunItem.createtime}"
							pattern="yyyy-MM-dd HH:mm:ss" />
					</display:column>
					<display:column title="结束日期" sortable="true" sortName="endTime">
						<fmt:formatDate value="${processRunItem.endTime}"
							pattern="yyyy-MM-dd HH:mm:ss" />
					</display:column>
					<display:column title="持续时间" sortable="true" sortName="duration">
								${f:getTime(processRunItem.duration)}
					</display:column>
					<display:column title="管理" media="html"  style="width:100px" class="opsBtnb">
					 <a name="processDetail" onclick="showDetail(this)" href="javascript:;"  action="${ctx}/platform/bpm/processRun/info.ht?link=1&runId=${processRunItem.runId}" title="${processRunItem.subject}" class="link detail">详情</a>
					</display:column>
				</display:table>
				<hotent:paging tableId="processRunItem"></hotent:paging>
		</div>
	</div>
</body>
</html>


