<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>历史数据管理</title>
<%@include file="/commons/include/get.jsp" %>


<script type="text/javascript">
function preView(hisId){
	
	// 显示历史记录
	DialogUtil.open({
       height:800,
       width: 1000,
       showMax: true,
       title : '查看历史',
       url: __ctx+ "/platform/system/sysHistoryData/get.ht?id="+hisId, 
       isResize: true
   });
}

var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)
function restore(hisId){
	$.ligerDialog.confirm('确认要恢复数据吗？','提示信息',function(rtn) {
		if(rtn) {
			var content=getTemplate(hisId);
			var call=dialog.get("sucCall"); // 调用回调
			call(content);
			dialog.close();
		}
	});
}

function getTemplate(id){
	var data="";
	$.ajax({
		  url: __ctx+ "/platform/system/sysHistoryData/getContent.ht?id=" + id,
		  async:false,
		  success: function(html){
		    data=html;
		  }
	});
	return data;
}


</script>

</head>
<body>
	<div class="panel">
		
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">历史数据管理列表</span>
			</div>
			
		</div>
		<div class="panel-body">
	    
		    <display:table name="sysHistoryDataList" id="sysHistoryDataItem" requestURI="list.ht?__FILTERKEY__=${busQueryRule.filterKey}&__FILTER_FLAG__=${busQueryRule.filterFlag}" sort="external" cellpadding="1" cellspacing="1" class="table-grid">
				
				<display:column property="subject" title="标题" sortable="true" sortName="subject"></display:column>
				<display:column property="creator" title="创建人" sortable="true" sortName="creator"></display:column>
				<display:column  title="创建时间" sortable="true" sortName="createtime">
					<fmt:formatDate value="${sysHistoryDataItem.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</display:column>
				<display:column title="管理" media="html" style="width:150px;" class="opsBtnb">
					<a href="javascript:restore(${sysHistoryDataItem.id});" class="link edit">恢复</a> |
					<a href="javascript:preView(${sysHistoryDataItem.id});" class="link detail">详情</a> |
					<a href="del.ht?id=${sysHistoryDataItem.id}" class="link del">删除</a>
				</display:column>
			</display:table>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


