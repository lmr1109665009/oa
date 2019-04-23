<%@page import="com.suneee.core.util.StringUtil"%>
<%@page import="com.suneee.platform.model.system.GlobalType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>表单定义管理</title>
<%@include file="/commons/include/form.jsp" %>
<f:link href="tree/zTreeStyle.css"></f:link>
<script type="text/javascript" src="${ctx}/js/tree/jquery.ztree.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/GlobalType.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/FormDefMenu.js"></script>
<style type="text/css">
#defLayout .l-layout-left .l-layout-header{height: 30px;}
#defLayout .l-layout-left .l-layout-header-inner{padding-top: 0;}
</style>
<%
	String type = request.getParameter("type");
	if(StringUtil.isEmpty(type))type="pc_form";	
%>
	<script type="text/javascript">

		var catKey="<%=GlobalType.CAT_FORM%>";
		var type ="<%=type%>";
		var curMenu=null;
		var url ="${ctx}/platform/form/bpmFormDef/list.ht";
		
		$(function(){
			if(type=='pc_form')
			 	url ="${ctx}/platform/form/bpmFormDef/list.ht";
			 else if(type=='mb_form')
				url ="${ctx}/platform/form/bpmMobileFormDef/list.ht";
			
			$("#defFrame").attr("src",url);
		});
		
		
		var globalType=new GlobalType(catKey,"glTypeTree",{onClick:onClick,onRightClick:zTreeOnRightClick,expandByDepth:1});
		var formDefMenu=new FormDefMenu();
				
		function onClick(treeNode){
			if(treeNode.isRoot){
				var src=url;
			}
			if(treeNode.isRoot==undefined){
				var typeId=treeNode.typeId;
				var src=url+"?categoryId="+typeId;
			}
			$("#defFrame").attr("src",src); 
		}
				
		function hiddenMenu(){
			if(curMenu){
				curMenu.hide();
			}
		}
		
		$(function (){
		  	//布局
		    $("#defLayout").ligerLayout({ leftWidth:210,height:$(window).height(),allowLeftResize:false});
		  	$(document).click(hiddenMenu);
			globalType.loadGlobalTree();
		});
		        
		function handler(item){
		 	hiddenMenu();
		 	var txt=item.text;
		 	switch(txt){
		 		case "增加分类":
		 			globalType.openGlobalTypeDlg(true);
		 			break;
		 		case "编辑分类":
		 			globalType.openGlobalTypeDlg(false);
		 			break;
		 		case "删除分类":
		 			globalType.delNode();
		 			break;
		 	}
		}
		/**
		 * 树右击事件
		 */
		function zTreeOnRightClick(event, treeId, treeNode) {
			hiddenMenu();
			if (treeNode) {
				globalType.currentNode=treeNode;
				globalType.glTypeTree.selectNode(treeNode);
				curMenu=formDefMenu.getMenu(treeNode, handler);
				justifyRightClickPosition(event);
				curMenu.show({ top: event.pageY, left: event.pageX });
			}
		};
		//展开收起
		function treeExpandAll(type){
			globalType.treeExpandAll(type);
		};
	</script> 
</head>
<body>
   	<div id="defLayout" >
         <div position="left" title="表单分类管理" style="overflow: auto;float:left;width:100%">
         	<div class="tree-toolbar">
				<span class="toolBar">
					<div class="group"><a class="link reload" id="treeFresh" href="javascript:globalType.loadGlobalTree();" ></a></div>
					<!-- <div class="l-bar-separator"></div> -->
					<div class="group"><a class="link expand" id="treeExpandAll" href="javascript:treeExpandAll(true)" ></a></div>
					<!-- <div class="l-bar-separator"></div> -->
					<div class="group"><a class="link collapse" id="treeCollapseAll" href="javascript:treeExpandAll(false)" ></a></div>
				</span>
			</div>
			<ul id="glTypeTree" class="ztree"></ul>
         </div>
         <div position="center">
         	<iframe id="defFrame" height="100%" width="100%" frameborder="0"></iframe>
         </div>
     </div>
</body>
</html>
