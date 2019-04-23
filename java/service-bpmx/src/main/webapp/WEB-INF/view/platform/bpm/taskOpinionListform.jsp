<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="f" uri="http://www.jee-soft.cn/functions" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="hotent" uri="http://www.jee-soft.cn/paging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<table class="table-grid">
	<tr>
		<th>任务节点</th>
		<th>开始时间</th>
		<th>结束时间</th>
		<th>经历时间</th>
		<th>执行人</th>
		<th>审批意见</th>
		<th>审批状态</th>
	</tr>
	<c:forEach var="item" items="${taskOpinionList}">
		<c:forEach var="tem" items="${item.list}" varStatus="var">
			<tr>
				<c:if test="${var.index==0}">
					<td style="width: 80px !important;" <c:if test="${fn:length(item.list)>1}">rowspan="${fn:length(item.list)}"</c:if>>${item.taskName}</td>
				</c:if>
				<td style="width: 120px !important; height: 30px">${tem.startTimeStr}</td>
				<td style="width: 120px !important; height: 30px">${tem.endTimeStr}</td>
				<td>
					<c:if test="${tem.endTimeStr!=''}">
		              			${tem.durTimeStr==''?'0分钟':tem.durTimeStr}
		              		</c:if>
				</td>
				<td style="width: 120px !important;">
					<c:choose>
						<c:when test="${tem.exeUserId ne null }">
							${tem.exeFullname}
						</c:when>
						<c:otherwise>
							<c:forEach items="${tem.candidateUsers }" var="user">
								${user.fullname}
								<br />
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</td>
				<td style="width: 120px !important;">
					${f:parseText(tem.opinion)}
					<c:if test="${tem.checkStatus==15}">
						<a href="javascript:;" onclick="return false;" opinionId="${tem.opinionId }" checkStatus="15">接收人</a>
					</c:if>
					<c:if test="${tem.checkStatus==38 || tem.checkStatus==43}">
						<a href="javascript:;" onclick="return false;" opinionId="${tem.opinionId }" checkStatus="40">接收人</a>
					</c:if>
				</td>
				<td style="width: 80px !important;">
					<f:taskStatus status="${tem.checkStatus}" flag="0"></f:taskStatus>
				</td>
			</tr>
		</c:forEach>
	</c:forEach>
</table>
