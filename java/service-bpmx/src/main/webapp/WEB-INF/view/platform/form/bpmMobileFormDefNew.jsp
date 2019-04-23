
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<title>新建表单</title>
<%@include file="/commons/include/form.jsp" %>
<f:link href="tree/zTreeStyle.css"></f:link>
<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerComboBox.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.ztree.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/htCatCombo.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/FormTableDialog.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/Share.js"></script>


<style type="text/css">
	html,body{height:100%;width:100%;}
</style>

<script type="text/javascript">
	window.name="frmEdit";
	var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)
	var dialogData = dialog.get('data');
	
	
	
	function closeWin(){
		dialog.close();
	}
	
	$(function(){
		$("#dataFormSave").click(function(){
			var tableId=$("#tableId").val();
			if(!tableId){
				$.ligerDialog.error("请您选择要生成的表","提示信息");
				return;
			}
			
			var formKey=$("#formKey").val();
			
			validExist(formKey,function(rtn){
				if(rtn>0){
					$.ligerDialog.error("您填写的别名系统中已存在!","提示信息");
				}
				else{
					var str=$("#bpmFormDefForm").serialize() ;
					var url="${ctx}/platform/form/bpmMobileFormDef/edit.ht?"+str;
					jQuery.openFullWindow(url);
				}
			});
			
			
		});
		
		
		$("#subject").blur( function () {
			Share.setPingyin($(this),$("#formKey"));
		});

	});
	
	function validExist(formKey,callBack){
		var url= __ctx + "/platform/form/bpmMobileFormDef/getCountByFormKey.ht";
		var obj={};
		obj.formKey=formKey;
		$.post(url, obj,function(data){
			callBack(data) ;
		});
	}
	
	function  selectTable(){
		var callBack=function(tableId,tableName){
			$("#tableId").val(tableId);
			$("#tableName").val(tableName);
			getTableTemplates(tableId);
		}
		FormTableDialog({callBack:callBack});
	}
	//主子表模板选择
	var mainTableTemplate,subTableTemplate;
	function getTableTemplates(tableId){
		$(".template_tr").remove();
		
		mainTableTemplate =$("#mainTableTemplate").val();
		subTableTemplate =$("#subTableTemplate").val();
		$.post("getMainSubTables.ht",{tableId:tableId},function(data){
			$("table").append(mainTableTemplate.replaceAll("tableDesc",data.tableDesc).replaceAll("tableId",data.tableId));
			for(var i=0,subTable;subTable=data.subTableList[i++];){
				$("table").append(subTableTemplate.replaceAll("tableDesc",subTable.tableDesc).replaceAll("tableId",subTable.tableId));
			}
		});
		
	}
	
	function resetTable(){
		$("#tableId").val('');
		$("#tableName").val('');
	};
	
	
</script>

</head>
<body >
	<div class="panel">
	<div class="hide-panel">
		<div class="panel-top">
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group">
						<a class="link run" id="dataFormSave" href="javascript:;"><span></span>下一步</a>
						<div class="l-bar-separator"></div>
						<a class="link back"  onclick="javascript:dialog.close();"><span></span>取消</a>
					</div>
				</div>
				</div>
			</div>
		</div>
		</div>
		<div class="panel-detail">
			<form  id="bpmFormDefForm" method="post" action="save.ht" >
				 
				 <table cellpadding="1" cellspacing="1" class="table-detail">
					<tr>
						<th width="150">表单标题:</th> 
						<td><input id="subject" type="text" name="subject" class="inputText" style="width:200px" value=""  /></td>
					</tr>
					<tr>
						<th width="150">表单别名:</th> 
						<td><input id="formKey" type="text" name="formKey" class="inputText" size="30" value="" /></td>
					</tr>
					<tr>
						<th width="150">表单类型:</th>
						<td>
							<input class="catComBo" catKey="FORM_TYPE" valueField="categoryId" catValue="${categoryId}" name="typeName" height="150" width="200"/>
						</td>
					</tr>
					<tr>
						<th width="150">表:</th>
						<td style="padding-top: 5px;">
							<input type="text" id="tableName" class="inputText" name="tableName" value="" readonly="readonly">
							<input type="hidden" id="tableId" name="tableId" value="">
							<a href='#' class='link search'  onclick="selectTable()" ></a>
							<a href='#' class='link redo' style='margin-left:10px;' onclick="resetTable()"><span>重选</span></a>
						</td>
					</tr>
				</table>
				
			</form>
			<textarea id="subTableTemplate" style="display: none;">
				<tr class="template_tr">
					<th width="150">tableDesc模板:</th>
					<td>
						<select name="template">
							<c:forEach items="${subTableTemplateList}" var="template">
								<option value="tableId,${template.alias}">${template.templateName}</option>
							</c:forEach>
						</select>
					</td>
				</tr>	
			</textarea>
			<textarea id="mainTableTemplate" style="display: none;">
				<tr class="template_tr">
					<th width="150">tableDesc模板:</th>
					<td>
						<select name="template">
							<c:forEach items="${mainTableTemplateList}" var="template">
								<option value="tableId,${template.alias}">${template.templateName}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
			</textarea>
		</div><!-- end of panel-body -->				
	</div> <!-- end of panel -->
</body>
</html>


