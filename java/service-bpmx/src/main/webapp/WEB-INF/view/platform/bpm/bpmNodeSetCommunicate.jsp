<%--
	time:2011-12-14 08:41:55
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>沟通人员设置</title>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/js/conf/orgUserConf.js"></script>
	<script type="text/javascript" src="${ctx}/js/javacode/codemirror.js"></script>
	<script type="text/javascript" src="${ctx}/js/javacode/InitMirror.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/ScriptDialog.js" ></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
	<script type="text/javascript">
	/*KILLDIALOG*/
	var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)
	$(function(){ 
		handOrgUser();
		$("a.save").click(function(){
			InitMirror.save();
			var opt=$("select#fromType").find('option:selected');
			var type = opt.attr("type");
			var typeVal = {};
			if(type == 'script'){
				typeVal.value = $("#script").val();
			}else if(type == 'user'){
				typeVal.id =$("#userIds").val();
				typeVal.value =$("#userNames").val();
			}else if(type == 'org'){
				typeVal.id =$("#orgIds").val();
				typeVal.value =$("#orgNames").val();
			}else if(type == 'role'){
				typeVal.id =$("#roleIds").val();
				typeVal.value =$("#roleNames").val();
			}
			var communicate ={};
			communicate.type = type;
			communicate.value = typeVal;
			$("#bpmNodeSetForm").ajaxForm({success:showResponse });
			$("#communicate").val(JSON2.stringify(communicate).replaceAll("\"","#@"));
			debugger;
			if(type==""){
				$("#communicate").val("");
			}
			$("#bpmNodeSetForm").submit(); 
		});
		init();
	})
	
	function showResponse(responseText) {
		var obj = new com.hotent.form.ResultMessage(responseText);
		if (obj.isSuccess()) {
			$.ligerDialog.success( obj.getMessage(),$lang.tip.confirm, function(rtn) {
				if(rtn){
					dialog.close();
				}
			});
		} else {
			$.ligerDialog.error("沟通人员设置保存失败!","提示信息");
		}
	}
	
	//初始化
	function init(){
		// debugger;
		var communicate = $("#communicate").val();
		if(communicate){
			communicate=communicate.replaceAll("#@","\"");
			var JsonObj = JSON2.parse(communicate);
			var type = JsonObj.type;
			var typeVal=JsonObj.value;
			if(type == 'script'){
				$("#fromType").val(type);
				$("#scriptTr").show();
				$("#script").val(typeVal.value);
			}else if(type=='user'){
				$("#fromType").val(type);
				$("#userTr").show();
				$("#userIds").val(typeVal.id);
				$("#userNames").val(typeVal.value);
			}else if(type=='org'){
				$("#fromType").val(type);
				$("#orgTr").show();
				$("#orgIds").val(typeVal.id);
				$("#orgNames").val(typeVal.value);
			}else if(type=='role'){
				$("#fromType").val(type);
				$("#roleTr").show();
				$("#roleIds").val(typeVal.id);
				$("#roleNames").val(typeVal.value);
			}
		}else{
			$("#fromType").val("user");
			$("#userTr").show();
		}
	}
	
	function changeValue(conf){
		var me = $(conf);
		var opt = me.find('option:selected');
		var type = opt.attr("type");
		if(type =='script'){
			$("#scriptTr").show();
		}else{
			$("#scriptTr").hide();
		}
		if(type=="user"){
			$("#userTr").show();
		}else{
			$("#userTr").hide();
		}
		if(type=="org"){
			$("#orgTr").show();
		}else{
			$("#orgTr").hide();
		}
		if(type=="role"){
			$("#roleTr").show();
		}else{
			$("#roleTr").hide();
		}
	}
	function handOrgUser(){
		 var selectObj = $("#fromType");
		 for(var i=0 ; i<communicateUserJson.length;i++){
			 var json = communicateUserJson[i];
			 if(json){
			 var opt = $("<option>").text(json.title).val(json.type);
			 opt.attr("type",json.type);
			 selectObj.append(opt);
			}
		 }
 	}
	function selectScript() {
	    ScriptDialog({
	        callback: function(script) {
	            InitMirror.editor.insertCode(script);
	        }
	    });
	}
	//关闭页面
	function closeDialog(){
		dialog.close();
	};
	
	function addUser() {
		UserDialog({
			selectUserIds:$("#userIds").val(),
		    selectUserNames:$("#userNames").val(),
			callback : userCallBack,
			isSingle : false
		});
	}
	function userCallBack(userIds, fullnames) {
		var userId=$("#userIds");
		var userName=$("#userNames");
		userId.val(userIds);
		userName.val(fullnames);
	}
	
	function addOrg() {
		OrgDialog({
			selectOrgIds:$("#orgIds").val(),
		    selectOrgNames:$("#orgNames").val(),
			callback : orgCallBack,
			isSingle : false
		});
	}
	function orgCallBack(orgId, orgName) {
		var orgIds=$("#orgIds");
		var orgNames=$("#orgNames");
		orgIds.val(orgId);
		orgNames.val(orgName);
	}
	
	
	function addRole() {
		RoleDialog({
			selectRoleIds:$("#roleIds").val(),
		    selectRoleNames:$("#roleNames").val(),
			callback : roleCallBack,
			isSingle : false
		});
	}
	function roleCallBack(roleId, roleName) {
		var roleIds=$("#roleIds");
		var roleNames=$("#roleNames");
		roleIds.val(roleId);
		roleNames.val(roleName);
	}
	
	function addScript() {
	    ScriptDialog({
	        callback: function(script) {
	            InitMirror.editor.insertCode(script);
	        }
	    });
	}
	
	
	
	</script>
</head>
<body>
<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">沟通人员设置</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group"><a class="link save" href="#"><span></span>保存</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link close"  onclick="closeDialog();"  href="#"><span></span>返回</a></div>
				</div>
			</div>
		</div>
		<div class="panel-body">
			<div class="panel-detail">
				<form id="bpmNodeSetForm" method="post" action="savaCommunicate.ht">
					<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
						<tr id="fromTypeTr" >
							<th width=120>
								配置类型:
							</th>
							<td colspan="3">
								<select id="fromType" name="fromType" onchange="changeValue(this)">
								<option val="none" type="">无</option>
								</select>
							</th>
						</tr>
						<tr id="scriptTr" style="display:none;">
							<th width=120>
								脚本:
							</th>
							<td colspan="3">
								<a href="javascript:;" onclick="selectScript()" id="btnScript" class="link var" title="常用脚本">常用脚本</a>
								<br/>表达式必须返回Set&lt;String&gt;集合类型的数据,数据项为用户ID。<br />
								<textarea id="script" codemirror="true" name="script" rows="6" cols="70"></textarea>
							</td>
							
						</tr>
						<tr id="userTr" style="display:none;">
							<th width=120>
								人员:
							</th>
							<td colspan="3">
								<input type="hidden" id="userIds" /> 
					<textarea id="userNames"  cols="50" style="width:300px"  rows="2" class="textarea" readonly="readonly" validate="{required:true}"></textarea>
					<br/><a class="link grant" onclick="addUser();"><span>选择人员</span></a>
							</td>
						</tr>
						<tr id="orgTr" style="display:none;">
							<th width=120>
								部门:
							</th>
							<td colspan="3">
								<input type="hidden" id="orgIds" /> 
					<textarea id="orgNames"  cols="50" style="width:300px"  rows="2" class="textarea" readonly="readonly" validate="{required:true}"></textarea>
					<br/><a class="link grant" onclick="addOrg();"><span>选择部门</span></a>
							</td>
						</tr>

						<tr id="roleTr" style="display:none;">
							<th width=120>
								角色:
							</th>
							<td colspan="3">
								<input type="hidden" id="roleIds" /> 
					<textarea id="roleNames"  cols="50" style="width:300px"  rows="2" class="textarea" readonly="readonly" validate="{required:true}"></textarea>
					<br/><a class="link grant" onclick="addRole();"><span>选择角色</span></a>
							</td>
						</tr>
						
						<input type="hidden" id="setId" name="setId" value="${bpmNodeSet.setId}" />
						<input type="hidden" id="communicate" name="communicate" value="${bpmNodeSet.communicate}" />
						<input type="hidden" id="defId" name="defId" value="${bpmNodeSet.defId}" />
						<input type="hidden" id="nodeId" name="nodeId" value="${bpmNodeSet.nodeId}" />
						<input type="hidden" id="parentActDefId" name="parentActDefId" value="${bpmNodeSet.parentActDefId}" />
						
					</table>
				</form>
			</div>
		</div>
</div>

</body>
</html>
