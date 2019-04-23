<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>附件管理</title>
<style type="text/css">
.file_name{
	text-decoration:none;
	color:black;
}
</style>
<%@include file="/commons/include/get.jsp" %>
<f:link href="jquery.qtip.css"></f:link>
<script type="text/javascript" src="${ctx}/js/jquery/plugins/jquery.qtip.js" ></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/ImageQtip.js" ></script>
<script type="text/javascript">
	function upload(){
		var url=__ctx+'/platform/system/sysFile/htmlDialog.ht';
		var winArgs="dialogWidth=840px;dialogHeight=600px;help=0;status=0;scroll=1;center=1";
		DialogUtil.open({
			height:600,
			width: 840,
			title : '上传附件',
			url: url, 
			isResize: true,
		});
	};
</script>
</head>
<body>
	<div class="panel">
	<div class="hide-panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">附件管理列表</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link search" id="btnSearch"><span></span>查询</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link upload" onclick="upload()"><span></span>上传</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link del"  action="del.ht"><span></span>批量删除</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a></div>
				</div>	
			</div>
			<div class="panel-search">
					<form id="searchForm" method="post" action="list.ht">
							<ul class="row">
										<li><span class="label">文件名:</span><input type="text" name="Q_fileName_SL"  class="inputText"  value="${param['Q_fileName_SL']}" />	</li>											
										<li><span class="label">上传者:</span><input type="text" name="Q_creator_SL"  class="inputText"  value="${param['Q_creator_SL']}"/></li>
										<li><span class="label">扩展名:</span><input type="text" name="Q_ext_SL"  class="inputText"   value="${param['Q_ext_SL']}"/></li>									

										<li><span class="label">创建时间:</span><input type="text"  name="Q_begincreatetime_DL"  class="inputText date" value="${param['Q_begincreatetime_DL']}"/>
										<span class="label label_line">_ </span><input type="text" name="Q_endcreatetime_DG" class="inputText date" value="${param['Q_endcreatetime_DG']}"/></li>
				
							<li><button class="btn">查询</button></li></ul>
					</form>
			</div>
		</div>
		</div>
		<div class="panel-body">
		    	<c:set var="checkAll">
					<input type="checkbox" id="chkall"/>
				</c:set>
			    <display:table name="sysFileList" id="sysFileItem" requestURI="list.ht" sort="external" cellpadding="1" cellspacing="1"   class="table-grid">
					<display:column title="${checkAll}" media="html" style="width:30px;">
						  	<input type="checkbox" class="pk" name="fileId" value="${sysFileItem.fileId}">
					</display:column>
					<display:column media="html" title="文件名" sortable="true" sortName="fileName">
						<a href="javascript:;" class="file_name" path="${sysFileItem.filePath}">${sysFileItem.fileName}</a>
					</display:column>
					<display:column title="创建时间" sortable="true" sortName="createtime">
						<fmt:formatDate value="${sysFileItem.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</display:column>
					<display:column property="ext" title="扩展名" sortName="ext" sortable="true"></display:column>							
				    <display:column property="note" title="大小" sortable="true" sortName="note" maxLength="80"></display:column>
					<display:column property="creator" title="上传者"  ></display:column>
					<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
						<a href="get.ht?fileId=${sysFileItem.fileId}" class="link detail">详情</a>  | 
		                <c:choose>
							<c:when test="${sysFile.delFlag eq 1}"><font color="red">已删除</font>  | </c:when>
							<c:otherwise>
							<a href="download.ht?fileId=${sysFileItem.fileId }" target="_blank" class="link download">下载</a>  | 
							</c:otherwise>
						</c:choose>
						<f:a alias="delFile" href="del.ht?fileId=${sysFileItem.fileId}" css="link del">删除</f:a>
					</display:column>
				</display:table>
				<hotent:paging tableId="sysFileItem"/>
			
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


