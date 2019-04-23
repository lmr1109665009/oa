<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
	<%@include file="/commons/include/get.jsp" %>
	<title>选择流程任务节点</title>
	<script type="text/javascript">
		/*KILLDIALOG*/
		var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)
		//选择任务节点
		function selectTaskNode(){
			var nodeIdRd=$("[name='nodeId']:checked");
			if(nodeIdRd){
				//window.returnValue={nodeId:nodeIdRd.val(),nodeName:nodeIdRd.attr('nodeName')};
				var rtn={nodeId:nodeIdRd.val(),nodeName:nodeIdRd.attr('nodeName')};
				dialog.get("sucCall")(rtn);
			}else{
				//window.returnValue={nodeId:'',nodeName:''};
				var rtn={nodeId:'',nodeName:''};
				dialog.get("sucCall")(rtn);
			}
			dialog.close();
		}
	</script>
</head>
<body>
<div class="panel">
		<div class="hide-panel">
			<div class="panel-top">
				
				<div class="panel-toolbar">
					<div class="toolBar">
						<div class="group"><a class="link save" onclick="selectTaskNode()"><span></span>选择</a></div>
						<div class="l-bar-separator"></div>
						<div class="group"><a class="link del" onclick="javasrcipt:dialog.close()"><span></span>关闭</a></div>
						<div class="l-bar-separator"></div>
					</div>	
				</div>
			</div>
		</div>
		<div class="panel-body">
			<table cellpadding="1" cellspacing="1"  class="table-grid">
				<thead>
					<tr>
						<th width="120">
							序号
						</th>
						<th>
							节点名称
						</th>
					</tr>
				</thead>
				<c:forEach items="${taskNodeMap}" var="map" varStatus="i">
						<c:choose>
					    	<c:when test="${i.index%2==0}" >
					            <tr class="odd">  
					        </c:when>  
					        <c:when test="${i.index%2==1}" >  
					            <tr class="even">  
					       </c:when>  
   						</c:choose> 
						<td>
							<span>${i.count}</span>
							<input type="radio" class="pk" name="nodeId" value="${map.key}" nodeName="${map.value}"/>
						</td>
						<td>
							${map.value}
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
</div>
	
</body>
</html>