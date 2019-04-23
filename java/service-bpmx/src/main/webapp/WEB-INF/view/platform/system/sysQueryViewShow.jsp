<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
	<title>${queryView.name}视图预览</title>
	<%@include file="/commons/include/getJqGrid.jsp" %>
	<script>
		var currentViewAlias = '${currentView}';
		var currentViewId = '${queryView.id}';
		var sqlAlias = '${sqlAlias}';
		var postData = eval('(' + '${postData}' + ')');
		
	</script>
</head>
<body>
<div class="panel">
		<!-- 导航TAB -->
		<c:if test="${hasTab eq true  &&  fn:length(viewList)>1}">
			<div class='panel-nav'>
				<div class="l-tab-links">
					<ul style="left: 0px; ">
						<c:forEach items="${viewList}" var="item">
							<li tabid="${item.alias}" <c:if test="${item.alias ==currentView }">class="l-selected"</c:if> >
								<a href="${ctx }/platform/system/sysQueryView/${sqlAlias}.ht?view=${item.alias}" title="${item.alias}">${item.name}</a>
							</li>
						</c:forEach>
					</ul>
				</div>
			</div>
		</c:if>
		${queryView.template}
	</div>
</body>
</html>
