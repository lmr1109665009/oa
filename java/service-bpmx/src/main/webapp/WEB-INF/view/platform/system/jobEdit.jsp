<%--
	time:2013-11-28 16:17:48
	desc:edit the 职务表
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>编辑 职务表</title>
	<%@include file="/commons/include/form.jsp" %>
	<f:link href="Aqua/css/ligerui-all.css" ></f:link>
	<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/formdata.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/subform.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/Share.js"></script>
	<script type="text/javascript" src="${ctx}/js/tree/jquery.ztree.js"></script>
	<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerComboBox.js"></script>
	<script type="text/javascript" src="${ctx}/js/lg/plugins/htDicCombo.js"></script>
	<script type="text/javascript" src="${ctx}/js/suneee/base/system/CommonDialog.js"></script>
	<script type="text/javascript">
		$(function() {
			/* $("#jobname").blur(function(){
				var obj=$(this);
				autoPingin(obj);
			}); */
			
			$("a.save").click(function() {
				$("#jobForm").attr("action","save.ht");
				submitForm();
			});
		});
		//提交表单
		function submitForm(){
			var options={};
			if(showResponse){
				options.success=showResponse;
			}
			var frm=$('#jobForm').form();
			frm.ajaxForm(options);
			if(frm.valid()){
				frm.sortList();
				frm.submit();
			}
		}
		
		function showResponse(responseText) {
			var obj = new com.hotent.form.ResultMessage(responseText);
			if (obj.isSuccess()) {
				$.ligerDialog.success(obj.getMessage(),"提示信息", function(rtn) {
					if(rtn){
						if(window.opener){
							window.opener.location.reload();
							window.close();
						}else{
							this.close();
							window.location.href="list.ht";
						}
					}
				});
			} else {
				$.ligerDialog.err("提示信息","职务保存失败!",obj.getMessage());
			}
		}
		
		function autoPingin(obj){
			var value=obj.val();
			Share.getPingyin({
				input:value,
				postCallback:function(data){
					$("#jobcode").val(data.output);
				}
			});
		}
		
		function chooseEnterpriseinfo(){

			var callBack=function(result){
                $("#enterpriseName").val(result.companyName);
                $("#enterpriseCode").val(result.orgCode);
			}
			var conf = {url:"/platform/system/sysOrg/getEnterpriseinfoList.ht", 
					dialogWidth:800, 
					dialogHeight:520, 
					title:"选择企业编码",
					callBack:callBack
			};
			CommonDialog(conf);
		};
	</script>
</head>
<body>
<div class="panel">
	<div class="panel-top">
		<div class="tbar-title">
		    <c:choose>
			    <c:when test="${job.jobid !=null}">
			        <span class="tbar-label">编辑职务表</span>
			    </c:when>
			    <c:otherwise>
			        <span class="tbar-label">添加职务表</span>
			    </c:otherwise>			   
		    </c:choose>
		</div>
		<div class="panel-toolbar">
			<div class="toolBar">
				<div class="group"><a class="link save" id="dataFormSave" href="javascript:;"><span></span>保存</a></div>
				<div class="l-bar-separator"></div>
				<div class="group"><a class="link back" href="list.ht"><span></span>返回</a></div>
			</div>
		</div>
	</div>
	<div class="panel-body">
		<form id="jobForm" method="post" action="save.ht">
			<table class="table-detail" cellpadding="0" cellspacing="0" border="0" type="main">
				<tr>
					<th width="20%">职务名称: <span class="required">*</span></th>
					<td><input type="text" id="jobname" name="jobname" value="${job.jobname}" validate="{required:true,maxlength:100}" class="inputText"/></td>
				</tr>
				<%-- <tr>
					<th width="20%">职务代码: </th>
					<td><input type="text" id="jobcode" name="jobcode" value="${job.jobcode}" validate="{required:false,maxlength:100}" class="inputText"/></td>
				</tr> --%>
				<tr>
					<th width="20%">级别: </th>
					<td>
					 <c:choose>
					    <c:when test="${empty dicList}">
					        	请在数据字典定义一个nodeKey为zwjb的分类
					    </c:when>
					    <c:otherwise>
					    	<select name="grade" class="inputText">
					    		<option value="">--请选择--</option>
					    		<c:forEach items="${dicList }" var="dic">
					    			<option value="${dic.itemValue }" <c:if test="${dic.itemValue eq job.grade}">selected="selected"</c:if>>${dic.itemName }</option>
					    		</c:forEach>
					    	</select>
					    </c:otherwise>			   
				    </c:choose>
					</td>
				</tr>
				<tr>
					<th width="20%">企业编码: <span class="required">*</span></th>
					<td>
						<input type="text" id="enterpriseCode" name="enterpriseCode" value="${job.enterpriseCode}" validate="{required:true}" class="inputText"/>
						<input type="hidden" id="enterpriseName" name="enterpriseName">
						<a class="link ok" href="javascript:0;" onclick="chooseEnterpriseinfo()"><span></span>选择</a>
					</td>
				</tr>
				<tr>
					<th width="20%">职务描述: </th>
					<td><textarea  id="jobdesc" name="jobdesc" value="${job.jobdesc}" validate="{required:false}" class="inputText">${job.jobdesc}</textarea></td>
				</tr>
			</table>
			<table class="table-grid table-list mt20" cellpadding="1" cellspacing="1" type="subtable" formtype="page" id="jobParam">
				<tr>
					<td colspan="2">
				   		<a id="btnAdd" class="link add">添加职务参数</a>
		    		</td>
				</tr>
				<tr>
					<th>键</th>
					<th>值</th>
				</tr>
				<c:forEach items="${jobParamList}" var="jobParamItem" varStatus="status">
				    <tr type="subdata">
		    			<td style="text-align: center"><input type="text" name="key" value="${jobParamItem.key}" validate="{required:false,maxlength:255}" class="inputText"/></td>
		    			<td style="text-align: center"><input type="text" name="value" value="${jobParamItem.value}" validate="{required:false,maxlength:255}" class="inputText"/></td>
				    </tr>
				</c:forEach>
				<tr type="append" style="display:none;">
			    	<td style="text-align: center"><input type="text" name="key" value="" validate="{required:false,maxlength:255}" class="inputText"/></td>
			    	<td style="text-align: center"><input type="text" name="value" value="" validate="{required:false,maxlength:255}" class="inputText"/></td>
			    </tr>
		    </table>
			<input type="hidden" name="jobid" value="${job.jobid}" />	
			<input type="hidden" name="jobcode" value="${job.jobcode }" />				
		</form>
	</div>
</div>
</body>
</html>
