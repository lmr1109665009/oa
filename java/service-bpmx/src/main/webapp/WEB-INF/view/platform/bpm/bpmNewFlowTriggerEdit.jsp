<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>编辑 触发新流程配置</title>
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/BpmDefinitionDialog.js"></script>
	<f:link href="tree/zTreeStyle.css"></f:link>
	<script type="text/javascript" src="${ctx}/js/tree/jquery.ztree.js"></script>
	<script type="text/javascript" src="${ctx}/js/util/ZtreeCreator.js"></script>	
	<link href="${ctx}/js/jquery/plugins/token-input-facebook.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${ctx}/js/jquery/plugins/jquery.tokeninput.js"></script>
	
	<script type="text/javascript">
	var actDefId = '${actDefId}';
	var nodeId = '${nodeId}';
	var curFormTree,triggerFromTree;

	var triggerJson = $.parseJSON('${bpmNewFlowTrigger.triggerJson}');
	var jsonmaping = $.parseJSON('${bpmNewFlowTrigger.jsonmaping}');
		$(function() {
			$("a.save").click(function() {
				$("#bpmNewFlowTriggerForm").attr("action","save.ht");
				if(curFormTree && triggerFromTree){
					$("#jsonmaping").val(JSON.stringify(curFormTree.getNodes()));
					$("#triggerJson").val(JSON.stringify(triggerFromTree.getNodes()));
				}
				submitForm();
			});
			loadCurFormTree(jsonmaping);
			loadTriggerFormTree(triggerJson);
			
			 $(".token-input").each(function(){
					var _this=$(this);
					
					_this.tokenInput([],{theme:"facebook",onDelete:deleteBpmUserCondition});
	
					//初始化原有数据
					var recs = _this.val();
					if(!recs){
						return;
					}
					var conds = $.parseJSON(recs);
					var tokenData=getTokensFromConditions(conds);
					$(tokenData).each(function(){
						_this.tokenInput("add",this);
					});				
			 });
		});
		
		//提交表单
		function submitForm(){
			var options={};
			if(showResponse){
				options.success=showResponse;
			}
			var frm=$('#bpmNewFlowTriggerForm').form();
			frm.ajaxForm(options);
			if(frm.valid()){
				frm.submit();
			}
		}
		
		function showResponse(responseText) {
			var obj = new com.hotent.form.ResultMessage(responseText);
			if (obj.isSuccess()) {
				$.ligerDialog.success(obj.getMessage(),"提示信息",function(){
					 window.location.reload(); 
					});
			} else {
				$.ligerDialog.err("提示信息","触发新流程配置保存失败!",obj.getMessage());
				
			}
		}
		//选择流程定义
		function selectDefinition(){
			BpmDefinitionDialog({isSingle:true,showAll:1,returnDefKey:true,defMark:"key",callback:function(defIds,subjects,defKeys){
				$("#triggerflowkey").val(defKeys);
				$("#triggerflowname").val(subjects);
				loadTriggerFormTree();
				loadCurFormTree();
			}});
		}
		function loadCurFormTree(json){
			 var url = '${ctx}/platform/bpm/bpmNewFlowTrigger/getTableTreeByDefkey.ht?defKey=${flowKey}&type=cur'
				 var ztreeCreator = new ZtreeCreator('curFormTree',url,json)
			 	.setDataKey({idKey:"fieldId",pIdKey:"tableId",name:"fieldDesc"})
				.setCallback({beforeDrag: beforeDrag,beforeDrop:beforeDrop})
				.initZtree({},function(treeObj){curFormTree=treeObj;}); 
		}
		function loadTriggerFormTree(json){
			var defKey = $("#triggerflowkey").val();
			if(!defKey) {
				return ;
			}
			var url = '${ctx}/platform/bpm/bpmNewFlowTrigger/getTableTreeByDefkey.ht?defKey='+defKey
			 var ztreeCreator = new ZtreeCreator('triggerFromTree',url,json)
				.setDataKey({idKey:"fieldId",pIdKey:"tableId",name:"fieldDesc"})
			  	.setCallback({beforeDrag:beforeDrag,beforeDrop:beforeDrop,onDrop:onDrop})
				.initZtree({},function(treeObj){triggerFromTree = treeObj;});
		}
		function beforeDrag(treeId, treeNodes) {
			for (var i=0,l=treeNodes.length; i<l; i++) {
				if (treeNodes[i].fieldType == 'table' || treeNodes[i].style == 'cur') {
					return false;
				}
			}
			return true;
		}
		
		function beforeDrop(treeId, treeNodes, targetNode, moveType) {
			//cur =A; trigger = B
			//B表to A表，且只能挂A字段之上。
			//可以回挂至B表上。
			if(moveType !='inner'
					|| (targetNode.style=='trigger' && targetNode.fieldType != 'table') 
					|| (targetNode.fieldType == 'table' && targetNode.style=='cur')){
				return false;
			}
			//返回目标节点
			if(targetNode.fieldType == 'table' && targetNode.style=='trigger'){
				if(targetNode.fieldName != treeNodes[0].oldTableName){
					$("#span1").html("你回错家了。parentTable:"+treeNodes[0].oldTableName );
					return false;
				}
				$("#span1").html("");	return true;
			}
			//如果A某表字段已经存在B某子表挂接，则不能再挂接B其他表。//暂时不考虑treeNodes 多个情况
			var targetTable = targetNode.getParentNode();
			var triggerTable = treeNodes[0].getParentNode();
			// 主表对主表。子表对子表 后台将Table.ismain给了type
			if(targetTable.type != triggerTable.type){
				$("#span1").html(targetTable.type ==1?"主表只接受主表字段映射":"子表只接受子表映射"); 
				return false;
			}
			if(targetTable.triggerTableName && targetTable.triggerTableName != triggerTable.fieldName){
				$("#span1").html("目标节点只接受 "+targetTable.triggerTableName+"字段");
				return false;
			}
			targetTable.triggerTableName = triggerTable.fieldName; 
			targetTable.targetTableDesc = triggerTable.fieldDesc;
			treeNodes[0].oldTableName = triggerTable.fieldName;
			treeNodes[0].targetTableName = targetTable.fieldName;
			$("#span1").html("");
			return true;
		}
		//var jsonmaping = {main:{},sub:{}}; 
		//{main:{AField:BField,Af2:Bf2},sub{A{from:"B",field{AField:BField,Af2:Bf2}}}}
		function onDrop(event, treeId, treeNodes, targetNode, moveType){
			// 暂时不构建关系树，直接使用当前树
			return;
			var triggerFiled = treeNodes[0].getParentNode();
			var targetTable = targetNode.getParentNode();
			//返回trigger表
			if(targetNode.fieldType =='table'){
				if(targetTable.type == 1){
					removeField(jsonmaping.main[targetTable.fieldName],treeNodes[0].fieldName);
				}else{
					var targerField = jsonmaping.sub[treeNodes[0].targetTableName].fields[triggerFiled.fieldName];
					removeField(targerField,treeNodes[0].fieldName);
				}
				return;
			}
			//匹配主表字段
			if(targetTable.type == 1){
				if(jsonmaping.main[targetNode.fieldName])
				alert("")
			}else{
				var subTableMapping = jsonmaping.sub[targetTable.fieldName];
				if(!subTableMapping) {
					subTableMapping = {from:"",fields:{}};
					subTableMapping.from =targetTable.triggerTableName;
				}
				addField(subTableMapping.from.fields[targetNode.fieldName],treeNodes[0].fieldName);
			}
			
			
		}
		function removeField(targer,trigger){
			var fields =targer.split("-");
			var newField="";
			for(var i=0,field;field =fields[i];){
				if(field != trigger){
					if(i>0)newField+"-"
					newField = newField+field;
				}
			}
			targer =newField;
		}
		
		//设置人员的
		function getTokensFromConditions(conds){
			var tokenData=[];
			for(var i=0;i<conds.length;i++){
				var id = conds[i].id;
				var name = "批次号["+conds[i].groupNo+"] - "+conds[i].conditionShow;
				tokenData.push({id:id,name:name});
			}
			return tokenData;
		};
		//设置消息接收人
		function receiverSetting(type){
			var hw = $.getWindowRect();
			var dialogWidth = hw.width*9/10;
			var dialogHeight = hw.height*9/10;
			var url = __ctx+"/platform/bpm/bpmNodeMessage/receiverSetting.ht?actDefId="+actDefId+"&nodeId=newFlow_"+nodeId+"&type="+type;
		 	var winArgs="dialogWidth="+dialogWidth+"px;dialogHeight="+dialogHeight+"px;help:0;status:0;scroll:1;center:1;resizable:1";
			url=url.getNewUrl();
			
		 	//var rtn = window.showModalDialog(url,"",winArgs);
		 	//window.location.reload();
		 	//reloadToken(type);
		 	
		 	/*KILLDIALOG*/
		 	DialogUtil.open({
		 		height:dialogHeight,
		 		width: dialogWidth,
		 		title : '消息接收人',
		 		url: url, 
		 		isResize: true,
		 		//自定义参数
		 		sucCall:function(rtn){
				 	reloadToken(type);
		 		}
		 	});
		};	
		
	function reloadToken(type){
			var url = __ctx+"/platform/bpm/bpmNodeMessage/getReceiverUserCondition.ht";
		 	var param = {
				 	actDefId:actDefId,
				 	nodeId:nodeId,
				 	receiverType:type
		 	}
		 	var tokenContainer = $(".token-input[rtype="+type+"]");
		 	$.post(url,param,function(data){
			 	if(!data.status){
					var tokenData=getTokensFromConditions(data.conditions);
					tokenContainer.tokenInput("clearOnly");
					$(tokenData).each(function(){
						tokenContainer.tokenInput("add",this);
					});	
			 	}else{
				 	$.ligerDialog.error("重新加载出错!","出错");
			 	}
		 	});
		};	
	function deleteBpmUserCondition(data){
			var tokenContainer = $(this);
			var type = tokenContainer.attr("rtype");
			var id=data.id;
			var url =__ctx + '/platform/bpm/bpmUserCondition/delByAjax.ht';
			$.post(url,{id:id},function(t){
				var resultData=eval('('+t+')');
				if(!resultData.result){
					$.ligerDialog.error("删除出错!","出错提示");
					reloadToken(type);
				}
			});
		};	
		function del(){
			var id = $("#triggerId").val();
			var url =__ctx + '/platform/bpm/bpmNewFlowTrigger/del.ht';
			if(id)
			$.post(url,{id:id},function(data){
				if (data.result == 1){
					$.ligerDialog.success(data.message,"提示信息",function(){
					 window.location.reload(); 
					});
				} else {
					$.ligerDialog.err("提示信息","联动设置保存失败!",resp.message);
				}
			});
		};	
	</script>
<style type="text/css">
ul.ztree {margin-top: 10px;border: 1px solid #617775;background: #f0f6e4;width:220px;height:360px;overflow-y:scroll;overflow-x:auto;}
</style>
</head>
<body>
<div class="panel">
	<div class="panel-top">
		<div class="tbar-title">
		    <c:choose>
			    <c:when test="${bpmNewFlowTrigger.id !=null}">
			        <span class="tbar-label"><span></span>编辑触发新流程配置</span>
			    </c:when>
			    <c:otherwise>
			        <span class="tbar-label"><span></span>添加触发新流程配置</span>
			    </c:otherwise>			   
		    </c:choose>
		</div>
		<div class="panel-toolbar">
			<div class="toolBar">
				<div class="group"><a class="link save" id="dataFormSave" href="#"><span></span>保存</a></div>
				<div class="group"><a class="link del" id="dataFormDel" onclick="del()" href="#"><span></span>删除</a></div>
			</div>
		</div>
	</div>
	<div class="panel-body">
		<form id="bpmNewFlowTriggerForm" method="post" action="save.ht">
			<table class="table-detail" cellpadding="0" cellspacing="0" border="0" type="main">
				<tr>
					<th width="20%">名称: </th>
					<td><input type="text" id="name" name="name" value="${bpmNewFlowTrigger.name}"  class="inputText" validate="{required:false,maxlength:765}"  /></td>
					<th width="20%">节点: </th>
					<td><input type="text" id="nodeid" name="nodeid" value="${empty bpmNewFlowTrigger ? nodeId:bpmNewFlowTrigger.nodeid}"  class="inputText" validate="{required:true,maxlength:765}"  /></td>
				</tr>
				<tr>
					<th width="20%">触发动作: </th>
					<td>
					<select id="action" name="action">
						<option value="agree" <c:if test="${bpmNewFlowTrigger.action eq 'agree'}">selected="selected"</c:if>>同意</option>
						<option value="opposite" <c:if test="${bpmNewFlowTrigger.action eq 'opposite'}">selected="selected"</c:if>>反对</option>
						<option value="reject" <c:if test="${bpmNewFlowTrigger.action eq 'reject'}">selected="selected"</c:if>>退回</option>
						<option value="delete" <c:if test="${bpmNewFlowTrigger.action eq 'delete'}">selected="selected"</c:if>>删除</option>
					</select>
					<th width="20%">启动新流程选择: </th>
					<td><input type="hidden" id="triggerflowkey" name="triggerflowkey" value="${bpmNewFlowTrigger.triggerflowkey}"  class="inputText" />
						<input type="text" readonly name="triggerflowname" id="triggerflowname" value="${bpmNewFlowTrigger.triggerflowname}" validate="{required:true,maxlength:765}">
						<a class="link add" herf="#" onclick="selectDefinition()">选择流程</a>
					</td> 
				</tr>
				<tr>
					<th width="20%">发起人配置:</th>
					<td colspan="3">
						<textarea rtype="8" id="starUser" class="token-input" name="starUser"  rows="3" readonly="readonly" style="width:328px !important">${starUserCondition}</textarea>
						<a href="javascript:;" onclick="receiverSetting(8)" class="link edit">设置</a>
					</td> 
				</tr>
				<tr>
					<th width="20%">描述: </th>
					<td colspan="3"><textarea  id="note" name="note" class="inputText" validate="{required:false,maxlength:765}" >${bpmNewFlowTrigger.note}</textarea></td>
				</tr>
			</table>
			<input type="hidden" id="triggerId" name="id" value="${bpmNewFlowTrigger.id}" />
			<input type="hidden" id="triggerJson" name="triggerJson" value="${bpmNewFlowTrigger.triggerJson}" />
			<input type="hidden" id="jsonmaping" name="jsonmaping" value="${bpmNewFlowTrigger.jsonmaping}" />
			<input type="hidden" id="flowkey" name="flowkey" value="${empty bpmNewFlowTrigger ? flowKey : bpmNewFlowTrigger.flowkey}"/>
		</form>
		<table>
		<thead>
		<tr>
			<th>当前流程表单</th>
			<th></th>
			<th>新流程表单</th>
		</tr>
		</thead>
		<tr>
			<th colspan="3"> 　<span id="span1" style="color: red;"></span></td>
		</tr>
		<tr>
			<td><ul id="curFormTree" class="ztree"></ul></td>
			<td style="width: 30px"></td>
			<td><ul id="triggerFromTree" class="ztree"></ul></td>
		</tr>
		</table>
	</div>
</div>
</body>
</html>