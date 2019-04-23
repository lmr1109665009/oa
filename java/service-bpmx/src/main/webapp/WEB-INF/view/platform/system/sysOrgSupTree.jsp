<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://www.jee-soft.cn/functions"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html>
<head>
    <title>组织架构管理</title>
    <%@include file="/commons/include/get.jsp"%>
    <f:link href="tree/zTreeStyle.css"></f:link>

    <script type="text/javascript" src="${ctx }/js/tree/jquery.ztree.js"></script>
    <script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysOrgSearch.js"></script>
    <script type="text/javascript">
        var orgTree; //树
        var height;
        var type ="system";
        var typeVal="all";
        $().ready(function (){
            height = $('#layout').css("height","100%").height();
            $("#viewFrame").height(height);
            $('#demensionId').change(function() {
                var demensionId = $(this).val();
                loadTree(demensionId);
            });
            $("#treeReFresh").click(function() {
                var demensionId = $("#demensionId").val();
                loadTree(demensionId);
            });

            $("#treeExpand").click(function() {
                orgTree = $.fn.zTree.getZTreeObj("orgTree");
                var treeNodes = orgTree.transformToArray(orgTree.getNodes());
                for(var i=1;i<treeNodes.length;i++){
                    if(treeNodes[i].children){
                        orgTree.expandNode(treeNodes[i], true, false, false);
                    }
                }
            });
            $("#treeCollapse").click(function() {
                orgTree.expandAll(false);
            });
            //首先加载行政维度
            loadTree(1);
            $("#demensionId").val(1);
            $("#orgTree").height(height-90-30);
        });

        //刷新
        function refreshNode(){
            var selectNode=getSelectNode();
            reAsyncChild(selectNode);
        };
        //刷新节点
        function reAsyncChild(targetNode){
            var orgId=targetNode.orgId;
            if(orgId==0){
                loadTree(selid);
            }else{
                orgTree = $.fn.zTree.getZTreeObj("orgTree");
                if(targetNode.isParent){
                    orgTree.reAsyncChildNodes(targetNode, "refresh", false);
                }else{
                    var targetParentNode = orgTree.getNodeByParam("orgId",targetNode.orgSupId, null);
                    orgTree.reAsyncChildNodes(targetParentNode, "refresh", false);
                }
            }
        };

        function loadTree(selid) {
            var setting = {
                data: {
                    key : {
                        name: "orgName" // orgPathname
                    },

                    simpleData: {
                        enable: true,
                        idKey: "orgId",
                        pIdKey: "orgSupId",
                        rootPId: 0
                    }
                },
                view : {
                    selectedMulti : false
                },
                async: {
                    enable: true,
                    url:"${ctx}/platform/system/sysOrg/getTreeData.ht?demId="+selid+"&type="+type+"&typeVal="+typeVal,
                    autoParam:["orgId","orgSupId"],
                    dataFilter: filter
                },
                callback:{
                    onClick:function zTreeOnClick(event, treeId, treeNode) {
                        window.top.__resultData__.orgName=treeNode.orgName;
                        window.top.__resultData__.orgSupId=treeNode.orgId;
                    },
                    onAsyncSuccess: zTreeOnAsyncSuccess
                }

            };
            orgTree=$.fn.zTree.init($("#orgTree"), setting);
        };

        //过滤节点
        function filter(treeId, parentNode, childNodes) {
            if (!childNodes) return null;
            for (var i=0, l=childNodes.length; i<l; i++) {
                var node = childNodes[i];
                if (node.isRoot == 1) {
                } else {
                    if (node.ownUser == null || node.ownUser.length < 1) {

                    }
                }
            }
            return childNodes;
        };

        //判断是否为子结点,以改变图标
        function zTreeOnAsyncSuccess(event, treeId, treeNode, msg) {
            if(treeNode){
                var children=treeNode.children;
                if(children.length==0){
                    treeNode.isParent=true;
                    orgTree = $.fn.zTree.getZTreeObj("orgTree");
                    orgTree.updateNode(treeNode);
                }
            }
            var node = orgTree.getNodesByFilter(function (nodeItem) {
                if(nodeItem.orgName==window.top.__resultData__.curOrgName){
                    return true;
                }
                return false;
            }, true);
            if(node!=null){
                orgTree.removeNode(node);
            }
        };

        function getSelectNode(){
            orgTree = $.fn.zTree.getZTreeObj("orgTree");
            var nodes = orgTree.getSelectedNodes();
            var treeNode = nodes[0];
            return treeNode;
        }
    </script>
    <style type="text/css">
        html,body{ padding:0px; margin:0; width:100%;height:100%;overflow: hidden;}
    </style>
</head>
<body>

<div position="left" title="组织机构管理" id="rogTree"
     style="height: 100%; width: 100% !important;">
    <div style="width: 100%;">

        <select id="demensionId" style="width: 99.8% !important;">
            <option value="0">---------全部---------</option>
            <c:forEach var="dem" items="${demensionList}">
                <option value="${dem.demId}">${dem.demName}</option>
            </c:forEach>
        </select>
    </div>
    <div class="tree-toolbar" id="pToolbar">
        <div class="toolBar"
             style="text-overflow: ellipsis; overflow: hidden; white-space: nowrap">
            <div class="group" >
                <a class="link reload" id="treeReFresh" ></a>
            </div>
            <div class="l-bar-separator"></div>
            <div class="group" >
                <a class="link expand" id="treeExpand" ></a>
            </div>
            <div class="l-bar-separator"></div>
            <div class="group" >
                <a class="link collapse" id="treeCollapse" ></a>
            </div>
        </div>
    </div>
    <ul id="orgTree" class="ztree" style="overflow:auto;height: 396px;"></ul>
</div>

</body>
</html>