<%@page pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
	<head>
		<title>表单明细</title>
		<%@include file="/commons/include/customForm.jsp" %>
		<script type="text/javascript">
			$().ready(function (){
				hideExtend();
			});
			
			function hideExtend(){
				$("a.extend").remove();
			};
			
			//自定义打印
			function customPrint(printAlias){
				var url="${ctx}/platform/form/bpmPrintTemplate/printForm.ht?formKey=${formKey}&businessKey=${id}&printAlias="+printAlias;
				jQuery.openFullWindow(url);
			}
			
		</script>
	</head>
	<body >
		<div class="panel">
			<div class="l-layout-header">表单明细</div>
		</div>
		<div class="panel-toolbar">
			<div class="buttons">
				<c:forEach items="${printTemplateList}" var="ptList">
						<a href="javascript:void(0);" onclick="customPrint('${ptList.alias}')" class="link print"><span></span>${ptList.temapalteName }</a>
				</c:forEach>
			</div>
		</div>
		<div class="panel-body" style="overflow:auto;width: 80%;margin: auto;">
			${form}
		</div>

	</body>
</html>