<%@page import="com.hotent.platform.model.system.GlobalType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://www.jee-soft.cn/functions"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html>
<head>
<title>知识库管理</title>
<%@include file="/commons/include/get.jsp"%>
<f:link href="tree/zTreeStyle.css"></f:link>
<script type="text/javascript" src="${ctx}/js/tree/jquery.ztree.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/KnowledgeType.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/GlobalMenu.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/FlowRightDialog.js"></script>
<script type="text/javascript">
		var catKey="<%=GlobalType.CAT_KNOWLEDGE%>";
		var knowledgeTypeMenu= new KnowledgeTypeMenu();
		var curMenu=null;
		var knowledgeType=new KnowledgeType(catKey,"glTypeTree",
				{
					onClick:onClick,
					onRightClick:zTreeOnRightClick,
					url:'${ctx}/platform/system/globalType/getKnowTree.ht',
					expandByDepth:1
				});
		var knowPerData;
		$(function() {
			$("#defLayout").ligerLayout({
				leftWidth : 220,
				height : '100%',
				allowLeftResize : false
			});
			height = $('#defLayout').height();
			$("#defFrame").height(height - 25);
			knowledgeType.loadGlobalTree();
        	$(document).click(hiddenMenu);
		});
		
		function hiddenMenu(){
 			if(curMenu){
 				curMenu.hide();
 			}
		}
		 function handler(item){
           	hiddenMenu();
           	var txt=item.text;
           	switch(txt){
           		case "增加分类":
           			knowledgeType.openGlobalTypeDlg(true);
           			break;
           		case "编辑分类":
           			knowledgeType.openGlobalTypeDlg(false);
           			break;
           		case "删除分类":
           			knowledgeType.delNode();
           			break;
            	}
           }
		 /**
     	 * 树右击事件
     	 */
     	function zTreeOnRightClick(event, treeId, treeNode) {
     		hiddenMenu();
     		if (treeNode) {
     			knowledgeType.currentNode=treeNode;
     			knowledgeType.glTypeTree.selectNode(treeNode);
     			var  menu = getKnowItems(knowPerData,treeNode.typeId,handler);
     			curMenu=knowledgeTypeMenu.getMenu(treeNode.typeId,treeNode, handler,menu);
     			justifyRightClickPosition(event);
     			if(menu.length !=0){
     				curMenu.show({ top: event.pageY, left: event.pageX });
     			}
     		}
     	};
     	getKnowItems=function(knowPerData,typeId,handler){
    		var menu = [];
    		var perData = knowPerData.perMap; 
    		if(knowPerData.isSuperAdmin==true){
    			menu=[{ text: '增加分类', click: handler },
                      { text: '编辑分类', click: handler  },
                      { text: '删除分类', click: handler }
                      ];
    		}
    		return menu;
    	}
     	//左击
    	function onClick(treeNode){
     		if(treeNode.isRoot ==1){
     			return;
     		}
     		var typeId=treeNode.typeId;
    		var url="${ctx}/platform/system/sysKnowledge/list.ht?typeId="+typeId;
    		$("#defFrame").attr("src",url);
    	};
    	//展开收起
    	function treeExpandAll(type){
    		knowledgeType.treeExpandAll(type);
    	};
</script>
<style type="text/css">
html {height: 100%}
body {padding: 0px;margin: 0;overflow: auto;}
#defLayout {width: 99.5%;margin: 0;padding: 0;}
</style>
</head>
<body>
	<div id="defLayout" style="bottom: 1; top: 1">
		<div position="left" title="知识库分类管理" id="rogTree" style="height: 100%; width: 100% !important;">
			<div class="tree-toolbar" id="pToolbar">
				<div class="toolBar"
					style="text-overflow: ellipsis; overflow: hidden; white-space: nowrap">
					<div class="group">
						<a class="link reload" id="treeReFresh" href="javascript:knowledgeType.loadGlobalTree();"></a>
					</div>
					<div class="l-bar-separator"></div>
					<div class="group">
						<a class="link expand" id="treeExpand" href="javascript:treeExpandAll(true)"></a>
					</div>
					<div class="l-bar-separator"></div>
					<div class="group">
						<a class="link collapse" id="treeCollapse" href="javascript:treeExpandAll(false)"  ></a>
					</div>
				</div>
			</div>
			<ul id="glTypeTree" class="ztree"></ul>
		</div>
			<div position="center" id="orgView" style="height: 100%;">
			<div class="l-layout-header">知识库文档</div>
				<iframe id="defFrame"  frameborder="0" width="100%" height="100%"></iframe>
			</div>
	</div>
</body>
</html>


