<%@page language="java" pageEncoding="UTF-8"%>
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

<table class="table-detail" cellpadding="0" cellspacing="0" border="0" type="main">
	<tr>
		<th width="20%">版本: </th>
		<td><input type="text" id="version" name="version" value="${bpmMobileFormDef.version}"  class="inputText" validate="{required:false,number:true }"  /></td>
	</tr>
	<tr>
		<th width="20%">表单KEY: </th>
		<td><input type="text" id="formKey" name="formKey" value="${bpmMobileFormDef.formKey}"  class="inputText" validate="{required:false,maxlength:50}"  /></td>
	</tr>
	<tr>
		<th width="20%">表单HTML: </th>
		<td><input type="text" id="formHtml" name="formHtml" value="${bpmMobileFormDef.formHtml}"  class="inputText" validate="{required:false}"  /></td>
	</tr>
	<tr>
		<th width="20%">表ID: </th>
		<td><input type="text" id="tableId" name="tableId" value="${bpmMobileFormDef.tableId}"  class="inputText" validate="{required:false,number:true }"  /></td>
	</tr>
	<tr>
		<th width="20%">表名: </th>
		<td><input type="text" id="tableName" name="tableName" value="${bpmMobileFormDef.tableName}"  class="inputText" validate="{required:false,maxlength:50}"  /></td>
	</tr>
	<tr>
		<th width="20%">表单主题: </th>
		<td><input type="text" id="subject" name="subject" value="${bpmMobileFormDef.subject}"  class="inputText" validate="{required:false,maxlength:255}"  /></td>
	</tr>
	<tr>
		<th width="20%">创建人ID: </th>
		<td><input type="text" id="createBy" name="createBy" value="${bpmMobileFormDef.createBy}"  class="inputText" validate="{required:false,number:true }"  /></td>
	</tr>
	<tr>
		<th width="20%">创建人: </th>
		<td><input type="text" id="creator" name="creator" value="${bpmMobileFormDef.creator}"  class="inputText" validate="{required:false,maxlength:50}"  /></td>
	</tr>
	<tr>
		<th width="20%">创建时间: </th>
		<td><input type="text" id="createTime" name="createTime" value="<fmt:formatDate value='${bpmMobileFormDef.createTime}' pattern='yyyy-MM-dd'/>" class="inputText date" validate="{date:true}" /></td>
	</tr>
	<tr>
		<th width="20%">是否默认版本: </th>
		<td><input type="text" id="isDefault" name="isDefault" value="${bpmMobileFormDef.isDefault}"  class="inputText" validate="{required:false,number:true }"  /></td>
	</tr>
	<tr>
		<th width="20%">是否发布: </th>
		<td><input type="text" id="isPublished" name="isPublished" value="${bpmMobileFormDef.isPublished}"  class="inputText" validate="{required:false,number:true }"  /></td>
	</tr>
	<tr>
		<th width="20%">发布人: </th>
		<td><input type="text" id="publisher" name="publisher" value="${bpmMobileFormDef.publisher}"  class="inputText" validate="{required:false,maxlength:50}"  /></td>
	</tr>
	<tr>
		<th width="20%">发布人ID: </th>
		<td><input type="text" id="publishBy" name="publishBy" value="${bpmMobileFormDef.publishBy}"  class="inputText" validate="{required:false,number:true }"  /></td>
	</tr>
	<tr>
		<th width="20%">发布时间: </th>
		<td><input type="text" id="publishTime" name="publishTime" value="<fmt:formatDate value='${bpmMobileFormDef.publishTime}' pattern='yyyy-MM-dd'/>" class="inputText date" validate="{date:true}" /></td>
	</tr>
	<tr>
		<th width="20%">更新人: </th>
		<td><input type="text" id="updator" name="updator" value="${bpmMobileFormDef.updator}"  class="inputText" validate="{required:false,maxlength:50}"  /></td>
	</tr>
	<tr>
		<th width="20%">更新人ID: </th>
		<td><input type="text" id="updateBy" name="updateBy" value="${bpmMobileFormDef.updateBy}"  class="inputText" validate="{required:false,number:true }"  /></td>
	</tr>
	<tr>
		<th width="20%">最后更新时间: </th>
		<td><input type="text" id="updateTime" name="updateTime" value="<fmt:formatDate value='${bpmMobileFormDef.updateTime}' pattern='yyyy-MM-dd'/>" class="inputText date" validate="{date:true}" /></td>
	</tr>
	<tr>
		<th width="20%">分类: </th>
		<td><input type="text" id="categoryId" name="categoryId" value="${bpmMobileFormDef.categoryId}"  class="inputText" validate="{required:false,number:true }"  /></td>
	</tr>
</table>
<input type="hidden" name="id" value="${bpmMobileFormDef.id}" />
