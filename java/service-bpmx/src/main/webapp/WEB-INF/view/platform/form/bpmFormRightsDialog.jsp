<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>表单授权</title>
	<%@include file="/commons/include/form.jsp"%>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/util/easyTemplate.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/form/Permission.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/ScriptDialog.js" ></script>


	<script type="text/javascript">
        function checkAll(checked,me){
            me.closest("[name=tableContainer]").find("input[type='checkbox'][class='pk']").each(function(){
                $(this).attr("checked", checked);
            });
        }
        function handlerCheckAll(){
            $("#chkall,#chkallOpinion").click(function(){
                var state=$(this).attr("checked");
                var me=$(this);
                if(state==undefined)
                    checkAll(false,me);
                else
                    checkAll(true,me);
            });
        }
        /*KILLDIALOG*/
        var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)

        var nodeId="${nodeId}";
        var actDefId="${actDefId}";
        var formKey="${formKey}";
        var tableId="${tableId}";
        var isNodeRights=${isNodeRights};
        var parentActDefId = "${parentActDefId}";


        var __Permission__;

        $(function() {
            handlerCheckAll();
            //权限处理
            __Permission__=new Permission();
            //设置节点权限。
            if(isNodeRights){
                __Permission__.loadByNode(actDefId,nodeId,formKey,parentActDefId);
                $("#nodeId").change(changeNode);
            }else if(actDefId){
                __Permission__.loadByActDefId(actDefId,formKey,parentActDefId);
            }else{
                //根据表单key获取表单权限。
                __Permission__.loadPermission(formKey);
            }
            $("#dataFormSave").click(savePermission);
            $("body").delegate("input:radio[name^='rdoPermissiont']","click", setPermision);
            $("body").delegate("input:radio[name^='rdoPermissionChild']","click", setPermisionChild);
            //$("body").delegate("#tableEdit,#tableRequired","click",showtableEdit );

            $("#initRights").click(function(){
                $.ligerDialog.confirm("是否确定重置权限设置?",function (r){
                    if(r){
                        var url = __ctx+"/platform/form/bpmFormRights/initRights.ht";
                        $.post(url,{formKey:formKey,actDefId:actDefId,nodeId:nodeId,parentActDefId:parentActDefId},function(d){
                            if(d.result)
                                $.ligerDialog.success(d.message,function(){
                                    //location.reload();
                                    dialog.close();
                                });
                            else
                                $.ligerDialog.warn(d.message);
                        });
                    }
                });
            });
            //changeScrollHeight(200);
            $(".masterFieldfixed .drop a").click(function(){
                if($(this).hasClass("activi")){
                    $(this).text('收起');
                    $("#masterFieldPermission tbody").show();
                    $(this).removeClass('activi')}
                else {
                    $(this).text('展开');
                    $(this).addClass('activi');
                    $("#masterFieldPermission tbody").hide();
                }
            });

            $("#yijian-table .drop a").click(function(){
                if($(this).hasClass("activi")){
                    $(this).text('收起');
                    $(this).parent().parent().parent().parent().siblings("tbody").show();
                    $(this).removeClass('activi')}
                else {
                    $(this).text('展开');
                    $(this).addClass('activi');
                    $(this).parent().parent().parent().parent().siblings("tbody").hide();
                }

            });
        });



        //批量设置组的权限。
        function setPermisionChild(){
            var val=$(this).val();
            //console.log(radioname);
            var obj=$(this).closest(".table-bg").find("[type=field]");

            $("span[name='r_span'],span[name='w_span'],span[name='b_span']",obj).hide();
            switch(val){
                //hidden
                case "1":
                    $(".r_select,.w_select,.b_select",obj).val("none");
                    $("[name=rpost]",obj).removeAttr("checked");
                    break;
                //readonly
                case "2":
                    $(".r_select",obj).val("everyone");
                    $(".w_select,.b_select",obj).val("none");
                    $('input:checkbox[value="y"]', obj).each(function(){
                        $(this).siblings('input').removeAttr('checked');
                        $(this).siblings('input').attr('show', 'false');
                    });
                    break;
                //edit
                case "3":
                    $(".r_select,.w_select",obj).val("everyone");
                    $(".b_select",obj).val("none");
                    $("[name=rpost]",obj).removeAttr("checked");
                    break;

                case "4":
                    $(".r_select,.w_select,.b_select",obj).val("everyone");
                    $("[name=rpost]",obj).removeAttr("checked");
                    break;
            }
        }
        //批量设置权限。
        function setPermision(){
            var val=$(this).val();
            var obj=$(this).closest("[name=tableContainer]");
            if($(obj).find('input[type="checkbox"]:checked').closest("tr").length>0){
                obj = $(obj).find('input[type="checkbox"]:checked').closest("tr");
            }
            $("span[name='r_span'],span[name='w_span'],span[name='b_span']",obj).hide();
            switch(val){
                //hidden
                case "1":
                    $(".r_select,.w_select,.b_select",obj).val("none");
                    $("[name=rpost]",obj).removeAttr("checked");
                    break;
                //readonly
                case "2":
                    $(".r_select",obj).val("everyone");
                    $(".w_select,.b_select",obj).val("none");
                    $('input:checkbox[value="y"]', obj).each(function(){
                        $(this).siblings('input').removeAttr('checked');
                        $(this).siblings('input').attr('show', 'false');
                    });
                    break;
                //edit
                case "3":
                    $(".r_select,.w_select",obj).val("everyone");
                    $(".b_select",obj).val("none");
                    $("[name=rpost]",obj).removeAttr("checked");
                    $(this).parent().parent().parent().find("input:checkbox[value=addRow]").attr("checked", true);
                    break;

                case "4":
                    $(".r_select,.w_select,.b_select",obj).val("everyone");
                    $("[name=rpost]",obj).removeAttr("checked");
                    $(this).parent().parent().parent().find("input:checkbox[value=addRow]").attr("checked", true);
                    break;
            }
        }

        //重新加载任务节点的权限
        function changeNode(){
            var obj=$("#nodeId");
            nodeId=obj.val();
            __Permission__.loadByNode(actDefId,nodeId,formKey,parentActDefId,platform);
        };

        //保存权限数据。
        function savePermission(){
            //设置所有的权限。
            __Permission__.setAllPermission();
            var json=__Permission__.getPermissionJson();
            var params={};
            params.formKey=formKey;
            params.permission=json;
            params.actDefId=actDefId;
            if(parentActDefId!=""){
                params.parentActDefId = parentActDefId;
            }
            //流程节点权限。
            if(isNodeRights){
                //params.actDefId=actDefId;
                params.nodeId=nodeId;
            }
            $.post("savePermission.ht",params,showResponse);
        }

        function showResponse(data){
            var obj=new com.hotent.form.ResultMessage(data);
            if(obj.isSuccess()){//成功
                $.ligerDialog.confirm('操作成功,继续操作吗?','提示信息',function(rtn){
                    if(!rtn){
                        dialog.close();
                    }
                });
            }else{//失败
                $.ligerDialog.err('出错信息',"表单授权失败",obj.getMessage());
            }
        };
        function showtableEdit(){
            $(this).parent().parent().parent().find("input:checkbox[value=addRow]").attr("checked", true);
        }
        //主表下的只读提交选中事件
        function clickCheckbox(target){
            var isChecked = $(target).is(":checked");
            if(!isChecked) return;
            var obj = $(target).closest('tr');
            $(".r_select",obj).val("everyone");
            $(".w_select,.b_select",obj).val("none");
        };
        function changeScrollHeight(time){
            window.setTimeout(function(){
                var currentThead=$(".panel-bpmFormDefForm .table-grid.tableContainer>thead");
                var currentTable=$(".panel-bpmFormDefForm .table-grid.tableContainer>tbody").eq(0);

                if(! currentTable) return;
                var tableBody=currentTable;
                var bodyheight=$("body").height()-140;
                currentTable.each(function(index,element){
                    var isbodyheight= $(this).height()>bodyheight ? bodyheight : $(this).height();
                    if($(this).height()>bodyheight){
                        $(this).height(isbodyheight);
                        $(this).parent().addClass("isbodyheight")
                        $(this).css("overflow-y","auto").css("margin-top","0px");
                    }
                });
            },time||10)
        }
	</script>
	<style type="text/css">
		.table-grid td a.officeMenu{
			color: #ff0000;
		}

		.table-grid td a.officeMenu:hover{
			text-decoration: underline;
		}
		.subtable-grid .table-grid th{
			background: #e0edff;
		}
		.panel-bpmFormDefForm .table-grid:nth-child(n) th,.panel-bpmFormDefForm .table-grid:nth-child(2n) tbody th{
			background: #e0edff;
		}
		.panel-bpmFormDefForm .table-grid:nth-child(2n) th,.panel-bpmFormDefForm .table-grid:nth-child(3n) tbody th{
			background: #fbebe6;
		}
		.panel-bpmFormDefForm .table-grid:nth-child(3n) th,.panel-bpmFormDefForm .table-grid:nth-child(4n) tbody th{
			background: #fbf9e6;
		}
		.panel-bpmFormDefForm .table-grid:nth-child(4n) th,.panel-bpmFormDefForm .table-grid:nth-child(5n) tbody th{
			background: #f0fbe6;
		}
		.panel-bpmFormDefForm .table-grid:nth-child(5n) th,.panel-bpmFormDefForm .table-grid:nth-child(6n) tbody th{
			background: #e6f9fb;
		}
		.panel-bpmFormDefForm .table-grid:nth-child(6n) th,.panel-bpmFormDefForm .table-grid:nth-child(7n) tbody th{
			background: #e9e6fb;
		}
		.panel-bpmFormDefForm .table-grid:nth-child(7n) th,.panel-bpmFormDefForm .table-grid:nth-child(8n) tbody th{
			background: #f9e6fb;
		}

		.panel-bpmFormDefForm .drop {
			font-size: 12px;
			position: absolute;
			right: 0;
			top: 14px;
			width: 40px;
			cursor: pointer;
		}
		.dropdiv{position: relative; padding-right: 10px;}
		.panel-bpmFormDefForm .table-grid thead  th{
			position: relative;
		}
		.panel-bpmFormDefForm .table-grid.isbodyheight tr:last-child td{ border-bottom: 0;}
		.panel-bpmFormDefForm .table-grid.isbodyheight>tbody {
			display:block;
			background: #fff;
		}
		.panel-bpmFormDefForm .table-grid.isbodyheight>thead,.panel-bpmFormDefForm .table-grid.isbodyheight>tbody>tr {
			display:table;
			width:100%;
			table-layout:fixed;
		}
		.panel-bpmFormDefForm .table-grid.isbodyheight>thead tr:last-child td{
			background: #eef2f3;
		}
		.panel-bpmFormDefForm .table-grid.isbodyheight{background: #ebeff1;}
		.panel-bpmFormDefForm .table-grid.isbodyheight>thead {
			width: calc( 100% - 17px );
			position: relative;
		}
		/* é€‚é…1400px çš„ç¬”è®°æœ¬ */
		@media screen and (min-width: 700px) and (max-width: 1250px) {
			.panel-bpmFormDefForm table.table-grid.isbodyheight th{font-size: 12px;padding: 4px 2px 4px 2px;}
			.panel-bpmFormDefForm table.table-grid.isbodyheight td{line-height:25px;padding: 4px 2px 4px 2px;}
			.btn-gray {padding:0 5px;}
		}
		.masterFieldPermission{}
		.masterFieldfixedH{ padding-top: 78px;}
		.bpmFormDefFormH{ padding-top: 45px;}
		.bpmFormDefFormFixed{
			position: fixed;
			left: 10px;
			right: 10px;
			/* width: 100%; */
			top: 39px;
			filter:alpha(opacity=95);opacity:0.95;
			background: #fff;    z-index: 111;}

		.masterFieldfixed{position: fixed;
			left: 10px;
			right: 10px;
			/* width: 100%; */
			top: 39px;
			filter:alpha(opacity=95);opacity:0.95;
			background: #fff;    z-index: 111;}
		.masterFieldfixedF{top: 84px;}
		.masterFieldfixed .table-grid td,.masterFieldfixed .table-grid{ border-bottom: 0;}
	</style>
</head>
<body >
<div class="panel-top">
	<div class="tbar-title">
		<span class="tbar-label">表单权限</span>
	</div>
	<div class="panel-toolbar">
		<div class="toolBar">
			<div class="group">
				<a class="link save" id="dataFormSave" href="javascript:;"><span></span>保存</a>
			</div>

			<div class="l-bar-separator"></div>
			<div class="group">
				<a class="link initRights" id="initRights" href="javascript:;"><span></span>重置权限设置</a>
			</div>
			<div class="l-bar-separator"></div>
			<div class="group">
				<a class="link close" href="javascript:dialog.close();"><span></span>关闭</a>
			</div>
		</div>
	</div>
</div>

<div  class="panel-body panel-bpmFormDefForm">
	<c:if test="${isNodeRights}">
		<form id="bpmFormDefForm" class="bpmFormDefFormFixed">
			<table cellpadding="0" cellspacing="0" border="0" style=" margin: 4px 0px;"  class="table-detail">
				<tr style="height:25px;">
					<td>流程节点:
						<select id="nodeId" >
							<c:forEach items="${bpmNodeSetList }" var="bpmNodeSet">
								<option value="${bpmNodeSet.nodeId}" <c:if test="${bpmNodeSet.nodeId== nodeId}"> selected="selected" </c:if> >${bpmNodeSet.nodeName}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
			</table>

		</form>
		<div class="bpmFormDefFormH"></div>
	</c:if>

    <div name="tableContainer">
	<div class="masterFieldfixed <c:if test="${isNodeRights}"> masterFieldfixedF </c:if>">
		<table cellpadding="1" cellspacing="1" class="table-grid tableContainer" >
			<thead>
			<tr>

				<th width="10%"><input type="checkbox" id="chkall"></th>
				<th width="18%">字段</th>
				<th width="18%">只读权限</th>
				<th width="18%">编辑权限</th>
				<th width="18%">必填权限</th>
				<th width="18%">只读提交<div class="drop"><a>收起</a></div></th>
			</tr>
			<tr>
				<td colspan="6">
					<input type="radio" value="1" name="rdoPermissiont" id="fieldHidden" ><label for="fieldHidden">隐藏</label>
					<input type="radio" value="2" name="rdoPermissiont" id="fieldReadonly"><label for="fieldReadonly">只读</label>
					<input type="radio" value="3" name="rdoPermissiont" id="fieldEdit" ><label for="fieldEdit">编辑</label>
					<input type="radio" value="4" name="rdoPermissiont" id="fieldRequired" ><label for="fieldRequired">必填</label>
				</td>
			</tr>
			</thead>
		</table>
	</div>
	<div class="masterFieldfixedH <c:if test="${isNodeRights}"> masterFieldfixedF </c:if>" ></div>

	<table cellpadding="1" cellspacing="1" id="masterFieldPermission" class="table-grid tableContainer" name="tableContainer" >
		<tbody id="fieldPermission"></tbody>
		<tbody id="teamFieldPermission"></tbody>
	</table>
	</div>

	<table  cellpadding="1" cellspacing="1" class="table-grid" style="margin-top: 5px;"  >
		<tbody id="tablePermission"></tbody>
	</table>

	<table id="yijian-table" cellpadding="1" cellspacing="1" class="table-grid" style="margin-top: 5px;" name="tableContainer">
		<thead>
		<tr>
			<th width="10%"><input type="checkbox" id="chkallOpinion"></th>
			<th width="18%">意见</th>
			<th width="18%">只读权限</th>
			<th width="18%">编辑权限</th>
			<th width="18%">必填权限</th>
			<th width="18%">显示历史<div class="drop"><a>收起</a></div></th>
		</tr>
		<tr>
			<td colspan="6">
				<input type="radio" value="1" name="rdoPermissionto" id="opinionHidden" ><label for="opinionHidden">隐藏</label>
				<input type="radio" value="2" name="rdoPermissionto" id="opinionReadonly"><label for="opinionReadonly">只读</label>
				<input type="radio" value="3" name="rdoPermissionto" id="opinionEdit" ><label for="opinionEdit">编辑</label>
				<input type="radio" value="4" name="rdoPermissionto" id="opinionRequired" ><label for="opinionRequired">必填</label>
			</td>
		</tr>
		</thead>
		<tbody id="opinionPermission"></tbody>
	</table>
	<table  cellpadding="1" cellspacing="1" class="table-grid table-biaoqian" style="margin-top: 5px;" name="tableContainer">
		<thead>
		<tr>
			<th width="20%">标签名</th>
			<th width="80%">显示权限</th>
		</tr>
		</thead>
		<tbody id="formTabPermission"></tbody>
	</table>
</div>


<textarea class="hidden" id="subtableTemplate">
	<tr name="tableContainer">
		<td   class="subtable-grid" >
			<table   cellpadding="1" cellspacing="1" class="table-grid" style="margin-top: 5px;" name="subTablePermission" value="\${data.title}">
				<tr id="thead_\${data.title}">
					<th colspan="5" style="text-align:left;">
						<b>\${data.memo} </b>&nbsp; &nbsp;
						&nbsp; <input type="checkbox" value="y" name="checkbox_\${data.title}"  onclick="changeCheckbox(this)" >
						<label for="SubtableShow">子表隐藏<a href="javascript:;" class="tipinfo"><span>子表将直接隐藏</span></a></label>
						&nbsp; <input type="checkbox" value="b" name="checkbox_\${data.title}"  onclick="changeCheckbox(this)" >
						<label for="SubtableShow">子表必填<a href="javascript:;" class="tipinfo"><span>子表要求至少有一行数据</span></a></label>
						&nbsp; <input type="checkbox" value="addRow" name="checkbox_\${data.title}"  onclick="changeCheckbox(this)" >
						<label for="SubtableShow">允许添加</label>
						&nbsp; <input type="checkbox" value="del" name="checkbox_\${data.title}"  onclick="changeCheckbox(this)" >
						<label for="SubtableShow">允许删除</label>
					</th>
				</tr>
				<tr>
					<td colspan="5">
						<input type="radio" value="1" name="rdoPermissiontt" id="tableHidden" ><label for="tableHidden">隐藏</label>
						<input type="radio" value="2" name="rdoPermissiontt" id="tableReadonly"><label for="tableReadonly">只读</label>
						<input type="radio" value="3" name="rdoPermissiontt" id="tableEdit" ><label for="tableEdit">编辑</label>
						<input type="radio" value="4" name="rdoPermissiontt" id="tableRequired" ><label for="tableRequired">必填</label>
					</td>
				</tr>
				<tr><th width="10%"></th>
					<th width="18%">字段</th>
					<th width="18%">只读权限</th>
					<th width="18%">编辑权限</th>
					<th width="18%">必填权限</th>
				</tr>
				<tbody id="tableId_\${data.title}">
					\${data.additionalHtml}
				</tbody>
			</table>
		</td>
	</tr>
	</textarea>
<textarea class="hidden" id="fieldPermissionTemplate">
	<tr type="\${data.type}" controlType="\${data.permission.controlType}"  tableName="\${data.permission.tableName}" >
		<td width="10%">
		<input name="Fruit" type="checkbox" value="" class="pk"/>
		</td>
		<td width="18%">
		<span class="mySpan">\${data.permission.memo}</span>
		<#if (data.permission.controlType == 12)>
		[<a class="officeMenu" href="javascript:;" onclick="selectOfficeMenu(this)" menuRight="\${data.permission.menuRight}">Office设置</a>]
		</#if>
		</td>
		\${data.readHtml}
		\${data.writeHtml}
		\${data.requiredHtml}
		<#if (data.type == 'field')>
		<td width="18%"><input name="rpost" type="checkbox" onclick="clickCheckbox(this);" <#if (data.permission.rpost != null && data.permission.rpost!='')>checked="checked"</#if> /></td>
		</#if>
	</tr>
	</textarea>
<textarea class="hidden" id="formTabPermissionTemplate" >
	<tr type="\${data.type}"  tableName="\${data.permission.tableName}" >
		<td>
		<span class="mySpan">\${data.permission.title}</span>
		</td>
		\${data.readHtml}
	</tr>
	</textarea>

<textarea class="hidden" id="opinionPermissionTemplate">
	<tr type="\${data.type}" controlType="\${data.permission.controlType}"  tableName="\${data.permission.tableName}" >
		<td width="10%">
		<input name="Opinion" type="checkbox" value="" class="pk"/>
		</td>
		<td width="18%">
		<span class="mySpan">\${data.permission.memo}</span>
		</td>
		\${data.readHtml}
		\${data.writeHtml}
		\${data.requiredHtml}
		<td width="18%"><input name="showOpinion" type="checkbox" <#if (data.permission.showOpinion)>checked="checked"</#if> /></td>
</tr>
	</textarea>


<script class="hidden" id="tdTemplate" type="text/template">
	<td <#if (data.type == 'subtable')> width="25%"</#if>>
	<select class="\${data.right}_select"  permissonType="\${data.permission.#rightName.type}" name="\${data.permission.title}">
		<option value="user">用户</option>
		<option value="role">角色</option>
		<option value="org">组织</option>
		<option value="orgMgr">组织负责人</option>
		<option value="pos">岗位</option>
		<option value="script">脚本</option>
		<option value="everyone">所有人</option>
		<option value="none">无</option>
	</select>
	<span name="\${data.right}_span">
			<input type="hidden" value="\${data.permission.#rightName.id}"/>
			<textarea  >\${data.permission.#rightName.fullname}</textarea>
			<a href="javascript:;" class="link-get" mode="#rightName" ><span class="link-btn">选择</span></a>
		</span>
	</td>
</script>
<script class="hidden" id="formTabTdTemplate" type="text/template">
	<td width="18%">
		<select class="\${data.right}_select"  permissonType="\${data.permission.#rightName.type}" name="\${data.permission.title}">
			<option value="user">用户</option>
			<option value="role">角色</option>
			<option value="org">组织</option>
			<option value="orgMgr">组织负责人</option>
			<option value="pos">岗位</option>
			<option value="script">脚本</option>
			<option value="everyone">所有人</option>
			<option value="none">无</option>
		</select>
		<span name="\${data.right}_span">
			<input type="hidden" value="\${data.permission.#rightName.id}"/>
			<textarea  >\${data.permission.#rightName.fullname}</textarea>
			<a href="javascript:;" class="link-get" mode="#rightName" ><span class="link-btn">选择</span></a>
		</span>
	</td>
</script>
<script class="hidden" id="teamPermissionTemplate" type="text/template">
	<tr id="team">
		<th colspan="1" width="10%">分组名称：</th>
		<td colspan="1" width="18%">
			<b>\${data.permission.memo}</b>
		</td>
		<th colspan="1" width="18%">
			显示权限：
		</th>
		<td colspan="1" id = "teamPserssion" width="18%">
			\${data.teamHtml}
		</td>
		<th colspan="1" width="18%">
			分组权限：
		</th>
		<td colspan="1" width="18%">
			<input type="radio" value="1" name="rdoPermissionChild\${data.permission.title}" radioname="\${data.permission.title}" id="\${data.permission.title}Hidden" ><label for="\${data.permission.title}Hidden">隐藏</label>
			<input type="radio" value="2" name="rdoPermissionChild\${data.permission.title}" radioname="\${data.permission.title}" id="\${data.permission.title}Readonly"><label for="\${data.permission.title}Readonly">只读</label>
			<br/><input type="radio" value="3" name="rdoPermissionChild\${data.permission.title}" radioname="\${data.permission.title}" id="\${data.permission.title}Edit" ><label for="\${data.permission.title}Edit">编辑</label>
			<input type="radio" value="4" name="rdoPermissionChild\${data.permission.title}" radioname="\${data.permission.title}" id="\${data.permission.title}Required" ><label for="\${data.permission.title}Required">必填</label>
		</td>
	</tr>
	\${data.fieldHtml}
</script>

<script class="hidden" id="teamTdTemplate" type="text/template">
	<select class="\${data.right}_select"  permissonType="\${data.permission.#rightName.type}" name="\${data.permission.title}" memo="\${data.permission.memo}">
		<option value="user">用户</option>
		<option value="role">角色</option>
		<option value="org">组织</option>
		<option value="orgMgr">组织负责人</option>
		<option value="pos">岗位</option>
		<option value="script">脚本</option>
		<option value="everyone">所有人</option>
		<option value="none">无</option>
	</select>
	<span name="\${data.right}_span">
			<input type="hidden" value="\${data.permission.#rightName.id}"/>
			<textarea  >\${data.permission.#rightName.fullname}</textarea>
			<a href="javascript:;" class="link-get" mode="#rightName" ><span class="link-btn">选择</span></a>
		</span>
	<input type="hidden" id="teamField" value="\${data.teamField}">
</script>


<script class="hidden" id="opinionTdTemplate" type="text/template">
	<td>
		<select class="\${data.right}_select"  permissonType="\${data.permission.#rightName.type}" name="\${data.permission.title}">
			<option value="user">用户</option>
			<option value="role">角色</option>
			<option value="org">组织</option>
			<option value="orgMgr">组织负责人</option>
			<option value="pos">岗位</option>
			<option value="script">脚本</option>
			<option value="everyone">所有人</option>
			<option value="none">无</option>
		</select>
		<span name="\${data.right}_span">
			<input type="hidden" value="\${data.permission.#rightName.id}"/>
			<textarea  >\${data.permission.#rightName.fullname}</textarea>
			<a href="javascript:;" class="link-get" mode="#rightName" ><span class="link-btn">选择</span></a>
		</span>
	</td>
</script>


</body>
</html>

