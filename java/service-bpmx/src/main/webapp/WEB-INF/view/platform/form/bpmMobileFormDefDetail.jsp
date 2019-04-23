<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script type="text/javascript" src="${ctx}/js/hotent/scriptMgr.js"></script>
<script type="text/javascript">
	function afterOnload(){
		var afterLoadJs=[
		     			'${ctx}/js/hotent/formdata.js',
		     			'${ctx}/js/hotent/subform.js'
		 ];
		ScriptMgr.load({
			scripts : afterLoadJs
		});
	}
</script>
<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<th width="20%">版本:</th>
		<td>${bpmMobileFormDef.version}</td>
	</tr>
	
	<tr>
		<th width="20%">表单KEY:</th>
		<td>${bpmMobileFormDef.formKey}</td>
	</tr>
	
	<tr>
		<th width="20%">表单HTML:</th>
		<td>${bpmMobileFormDef.formHtml}</td>
	</tr>
	
	<tr>
		<th width="20%">表ID:</th>
		<td>${bpmMobileFormDef.tableId}</td>
	</tr>
	
	<tr>
		<th width="20%">表名:</th>
		<td>${bpmMobileFormDef.tableName}</td>
	</tr>
	
	<tr>
		<th width="20%">表单主题:</th>
		<td>${bpmMobileFormDef.subject}</td>
	</tr>
	
	<tr>
		<th width="20%">创建人ID:</th>
		<td>${bpmMobileFormDef.createBy}</td>
	</tr>
	
	<tr>
		<th width="20%">创建人:</th>
		<td>${bpmMobileFormDef.creator}</td>
	</tr>
	
	<tr>
		<th width="20%">创建时间:</th>
		<td>
		<fmt:formatDate value="${bpmMobileFormDef.createTime}" pattern="yyyy-MM-dd"/>
		</td>
	</tr>
	
	<tr>
		<th width="20%">是否默认版本:</th>
		<td>${bpmMobileFormDef.isDefault}</td>
	</tr>
	
	<tr>
		<th width="20%">是否发布:</th>
		<td>${bpmMobileFormDef.isPublished}</td>
	</tr>
	
	<tr>
		<th width="20%">发布人:</th>
		<td>${bpmMobileFormDef.publisher}</td>
	</tr>
	
	<tr>
		<th width="20%">发布人ID:</th>
		<td>${bpmMobileFormDef.publishBy}</td>
	</tr>
	
	<tr>
		<th width="20%">发布时间:</th>
		<td>
		<fmt:formatDate value="${bpmMobileFormDef.publishTime}" pattern="yyyy-MM-dd"/>
		</td>
	</tr>
	
	<tr>
		<th width="20%">更新人:</th>
		<td>${bpmMobileFormDef.updator}</td>
	</tr>
	
	<tr>
		<th width="20%">更新人ID:</th>
		<td>${bpmMobileFormDef.updateBy}</td>
	</tr>
	
	<tr>
		<th width="20%">最后更新时间:</th>
		<td>
		<fmt:formatDate value="${bpmMobileFormDef.updateTime}" pattern="yyyy-MM-dd"/>
		</td>
	</tr>
	
	<tr>
		<th width="20%">分类:</th>
		<td>${bpmMobileFormDef.categoryId}</td>
	</tr>
</table>
