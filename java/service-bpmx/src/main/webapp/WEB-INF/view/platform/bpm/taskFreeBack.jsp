<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<%@include file="/commons/include/form.jsp"%>
<title>审批意见</title>
<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
<script type="text/javascript">
	/*KILLDIALOG*/
	var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)

	$().ready(function() {
		var isRequired = '${isRequired}';
		if (isRequired == '1') {
			$("#opinion").attr("validate", "{required:true,maxLength:500}");
		} else {
			$("#opinion").attr("validate", "{maxLength:500}");
		}
		// 设置初始值
		var voteContent = dialog.get("voteContent");
		$("#opinion").val(voteContent);

	});

	function save() {
		var rtn = $("#frmComm").form().valid();
		if (!rtn) return;
		var opinion = $("#opinion").val(),
			forkTask2RejectInput = $("input[name='forkTask2Reject']"),
			forkTask2RejectInputChecked = $("input[name='forkTask2Reject']:checked"),
			forkTask2Reject = [];
		if(forkTask2RejectInput && forkTask2RejectInput.length > 0){
			if(!forkTask2RejectInputChecked || forkTask2RejectInputChecked.length == 0){
				$.ligerDialog.error("退回到分发节点时，至少需要指定一个要退回的分发执行人。",'提示信息');
				return;
			}
			else{
				for(var i=0,c;c=forkTask2RejectInputChecked[i++];){
					var item = [];
					item.push($(c).val());
					item.push($(c).attr("label"));
					item.push($(c).attr("token"));
					item.push($(c).attr("nodeId"));
					item.push($(c).attr("nodeName"));
					forkTask2Reject.push(item.join('^'));
				}
			}
		}
		var call = dialog.get("sucCall");
		call({opinion:opinion,forkTask2Reject:forkTask2Reject.join('#'),"startNode":$("#startNode").val()});
		//dialog.close();
		iPadBackFrame.back(true);
	}

	function setValue(val) {
        var val = $("#selTaskAppItem").val();
		$("#opinion").val(val);
	}
</script>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<span class="tbar-label">审批意见</span>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group">
						<a class="link run" id="dataFormSave" href="javascript:;"
							onclick="save()"><span></span>确定</a>
					</div>
					<div class="l-bar-separator"></div>
					<div class="group">
						<a class="link close" href="javascript:;" onclick="dialog.hidden();"><span></span>关闭</a>
					</div>
				</div>
			</div>
		</div>
		<div class="panel-body">
			<form id="frmComm">
				<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
					<c:if test="${(reject eq 'reject') && (not empty forkTaskExecutor)}">
					
					<tr>
						<th>
							退回注意事项:
						</th>
						<td>
							<p style="margin:8px;">
								当前节点为<b>汇总节点</b>，其对应的分发节点为<b><span class="red">[${forkTaskExecutor[0].nodeName}]</span></b>，退回时<br/>只能退回到该分发节点，并且需要指定要退回的分发执行人。
							</p>
						</td>
					</tr>
					<tr>
						<th>要退回的分发执行人:</th>
						<td>
							<c:forEach var="executor" items="${forkTaskExecutor}">
								<label>
									<input type="checkbox" name="forkTask2Reject" value="${executor.userId}" nodeId="${executor.nodeId}"
										   token="${executor.token}" label="${executor.fullname}" nodeName="${executor.nodeName}">
									${executor.fullname}
								</label>
							</c:forEach>
						</td>
					</tr>
					</c:if>
					<c:if test="${(reject eq 'reject')}">
					<tr>
						<th  style="height:60px;">退回节点：</th>
						<td>
							<select id="startNode" name="startNode" style="width:59%;">
								<c:forEach items="${stackList}" var="item">
									<option value="${item.nodeId}">${item.nodeName}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					</c:if>
					<tr>
						<th>退回意见:</th>
						<td>
							<c:if test="${!empty taskAppItems}">
								常用语选择：
								<select  id="selTaskAppItem" name="selTaskAppItem" onchange="setValue()" >
									<option value="" style="text-align:center;">-- 请选择 --</option>
									<c:forEach var="item" items="${taskAppItems}">
										<option  value="${item}">${item}</option>
									</c:forEach>
								</select>
								<br>
							</c:if>
							<textarea rows="4" cols="50" style="width: 300px"
									  id="opinion" name="opinion" maxLength="500"></textarea></td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</body>
</html>