<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>组织排序</title>
	
	<%@include file="/commons/include/form.jsp" %>
	<script type="text/javascript">
	    var dialog = frameElement.dialog;
        var orgTree; //树
        var height;
        var nodes=[
            <c:forEach items="${SysOrgList}" var="d" varStatus="status">
				<c:choose>
					<c:when test="${status.last}">
            			{id:${d.orgId},pId:${d.orgSupId},name:"${d.orgName}"}
					</c:when>
					<c:otherwise>
            			{id:${d.orgId},pId:${d.orgSupId},name:"${d.orgName}"},
					</c:otherwise>
				</c:choose>
			</c:forEach>
		];
        var setting = {
            view : {
                selectedMulti : false
            }

        };
        $().ready(function (){
            height = $('#layout').css("height","100%").height();
            $("#viewFrame").height(height);
            $("#orgTree").height(height-90-30);
            orgTree=$.fn.zTree.init($("#orgTree"),setting,nodes);
            updateNode();
            $("#btn_top").click(function(){
                sortNode(getSelectNode(orgTree),0);
            });
            $("#btn_up").click(function(){
                sortNode(getSelectNode(orgTree),orgTree.getNodeIndex(getSelectNode(orgTree))-1);
            });
            $("#btn_down").click(function(){
                sortNode(getSelectNode(orgTree),orgTree.getNodeIndex(getSelectNode(orgTree))+1);
            });
            $("#btn_bottom").click(function(){
                sortNode(getSelectNode(orgTree),orgTree.getNodes().length-1);
            });

            $("a.save").click(function() {
                var nodeList=orgTree.getNodes();
                var orgIds = "";
                for(var i in nodeList){
                    orgIds+=nodeList[i].id+",";
                }
                if(orgIds.length>1){
                    orgIds=orgIds.substring(0,orgIds.length-1);
                    var url="${ctx}/platform/system/sysOrg/sort.ht";
                    var params={"orgIds":orgIds};
                    $.post(url,params,function(result){
                        var top=window.top;
                        var obj=new com.hotent.form.ResultMessage(result);
                        if(obj.isSuccess()){//成功
                            top.$.ligerDialog.success("组织排序完成!",'提示信息',function(){
                                dialog.close();});
                            /* var conf=window.dialogArguments;
                            if(conf.callBack){
                                conf.callBack();
                            } */
                            dialog.get("sucCall")();
                        }
                        else{
                            top.$.ligerDialog.err('出错信息',"组织排序失败",obj.getMessage());
                        }
                    });
                }
            });
        });
        //获取选择节点
        function getSelectNode(orgTree) {
			var nodeList=orgTree.getSelectedNodes()
			return nodeList[0];
        }

        /**
		 * 排序节点排序
         * @param node
         * @param pos
         */
        function sortNode(orgNode,pos){
            if(!orgNode){
                $.ligerDialog.warn("请选择需要排序的节点！",'提示信息');
                return;
			}
            var nodeList=orgTree.getNodes();
			if (pos<0||pos>=nodeList.length){
                return;
			}
            var orgPos=orgTree.getNodeIndex(orgNode);
            var destNode=nodeList[pos];
            if (orgPos>pos){
                orgTree.moveNode(destNode,orgNode,"prev")
			}else if (orgPos<pos){
                orgTree.moveNode(destNode,orgNode,"next")
			}
		}

        //判断是否为子结点,以改变图标
        function updateNode() {
            var nodeList=orgTree.getNodes();
            for (var i in nodeList){
                treeNode=nodeList[i];
                treeNode.isParent=false;
                orgTree.updateNode(treeNode);
			}
        };
	</script>
</head>
<body>
<div class="panel-top">
				<div class="tbar-title">
					<span class="tbar-label">组织排序</span>
				</div>
				<div class="panel-toolbar">
					<div class="toolBar">
						<div class="group"><a class="link save" id="dataFormSave" href="javascript:;"><span></span>保存</a></div>
						<div class="l-bar-separator"></div>
						<div class="group"><a class="link close"  href="javascript:;" onclick="dialog.close();"><span></span>关闭</a></div>
					</div>
				</div>
		</div>
	<form id="dataForm" method="post" action="sort.ht">
		<div class="panel-data">
			<table class="table-detail"  border="0" cellspacing="0" cellpadding="0" >
			
				<tr>
					<td class="form_title">
						<div position="left" title="组织机构" id="rogTree">
							<ul id="orgTree" class="ztree" style="overflow:auto;height: 160px;"></ul>
						</div>
					</td>
					<td class="form_title" style="text-align:left;width:80px">
						<input type="button" id="btn_top" value="顶部" /><br/>
						<input type="button" id="btn_up" value="向上" /><br/>
						<input type="button" id="btn_down" value="向下" /><br/>
						<input type="button" id="btn_bottom" value="底部" /><br/>
					</td>
				</tr>
			</table>
		</div>
	</form>
</div>
</body>
</html>
