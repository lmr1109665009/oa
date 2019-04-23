<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
    <%@include file="/commons/include/form.jsp" %>
    <title>分组设置</title>
        <style type="text/css">
            .table-grid th{
                padding: 1px 0px;
            }
            a.link.save {
                background-color: #009bdb;
                border-radius: 4px;
                color: white;
                padding: 0px 20px;
                line-height: 32px;
                height: 32px;
                font-size: 14px;
            }
            .l-layout-left .l-layout-header,.table-grid th{
                background: #fff;
            }
            .l-layout-left .l-layout-header{
                border-bottom: 1px solid #e9e9e9;
            }
            th a.link.add{
                font-family: inherit;
                font-size: 15px;
                color:white;
                background-color: #009bdb;
                border-radius: 4px;
                padding: 8px 20px;
            }
            a.link.save,a.link.add{
                margin-left: 8px;
            }

        </style>


    <f:link href="tree/zTreeStyle.css"></f:link>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.ztree.js"></script>
    <script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
    <script type="text/javascript" src="${ctx}/js/util/SelectOption.js"></script>
    <script type="text/javascript">
        var tableId = '${bpmFormTable.tableId}',valid, team = '${bpmFormTable.team}';

        $(function() {
            $("#teamLayout").ligerLayout({
                leftWidth : 200,
                height : '100%',
                allowLeftResize : false
            });

            loadTree();
            valid =  $("#teamItem").form();
            var height = $(window).height();
            $("#fieldtree").height(height-95);
            $("#teamLayout .l-layout-left .l-layout-content").height(height-40)
            parseTeam(team);

            var teamItem = 	$("#teamItem");
            $("a.add").click(function(){
                add(true);
            });
            $("a.save").click(function(){
                saveData();
            });

            teamItem.delegate("select[name='teamField']", "focus click", function() {
                selectTeam (this);
            });

            $(".moveFieldTop").click(function(){
                var td = $(this).parent().parent().parent();
                var select  = $("select[name='teamField']",td).get(0);
                __SelectOption__.moveTop(select);
            });
            $(".moveFieldUp").click(function(){
                var td = 	$(this).parent().parent().parent();
                var select  = $("select[name='teamField']",td).get(0);
                __SelectOption__.moveUp(select,'1');
            });
            $(".moveFieldDown").click( function(){
                var td = $(this).parent().parent().parent();
                var select  = $("select[name='teamField']",td).get(0);;
                __SelectOption__.moveDown(select,'1');
            });
            $(".moveFieldBottom").click( function(){
                var td = $(this).parent().parent().parent();
                var select  = $("select[name='teamField']",td).get(0);
                __SelectOption__.moveBottom(select);
            });

            $('#isShow').click(function(){
                if($(this).is(":checked"))
                    $('#showPosition').show();
                else
                    $('#showPosition').hide();
            });

            $("a.moveup,a.movedown").click(move);

        });


        //根据组名生成别名
        function autoGetGroupKey(inputObj){
            var url=__ctx + '/platform/form/bpmFormTable/getGroupKey.ht' ;
            var subject=$(inputObj).val();

            var titleFd = $(inputObj).parent().parent().parent().find("[name=teamTitle]");
            if($.trim(subject).length<1) return;
//            debugger;
            $.post(url,{'subject':subject,'tableId':tableId},function(response){
                var json=response;
                if(json.status==0 ){
                    if($.trim(	titleFd.val()).length<1){
                        titleFd.val(json.data);console.log(titleFd.val(),json.data);
                        titleFd.addClass("inputObj");
                        var teamTitleArr=[];
                        $('#teamItem [name=teamTitle]').not(".inputObj").each(function(i){
                            teamTitleArr[i]=$(this).val();
                        })

                        if(teamTitleArr.indexOf(titleFd.val())!=-1){
                            $.ligerDialog.err("提示信息","重复!","别名已存在!");
                            titleFd.val("");
                        }
                        titleFd.removeClass("inputObj");

                    };
                }else{
                    $.ligerDialog.err("提示信息","生成别名失败!",json.message);
                }
            });

        }

        //绑定上下移动
        function move(){
            var obj=$(this);
            var direct=obj.hasClass("moveup");
            var objFieldset=obj.closest('[zone="team"]');
            if(direct){
                var prevObj=objFieldset.prev();
                if(prevObj.length>0){
                    objFieldset.insertBefore(prevObj);
                }
            }
            else{
                var nextObj=objFieldset.next();
                if(nextObj.length>0){
                    objFieldset.insertAfter(nextObj);
                }
            }
        };

        //加载树
        function loadTree(){
            BpmFormTableTeam.getFieldsByTableId(tableId);
        }


    </script>
    <script type="text/javascript" src="${ctx}/js/hotent/platform/form/BpmFormTableTeam.js"></script>

<body>

    <div id="teamLayout">
        <div position="left" title="所属表名：<b>${bpmFormTable.tableDesc}</b>" style="overflow: auto; float: left; width: 100%; height: 100%;">
            <div class="panel-toolbar tree-title">
						<span class="toolBar">

							<div class="group">
								<a class="link reload" onclick="loadTree()"><span></span>刷新</a>
							</div>
						</span>
            </div>
            <ul id="colstree" class="ztree"></ul>
        </div>
        <div position="center" title="分组设置" style="overflow: auto;">

            <div>
                <table cellpadding="1" cellspacing="1"  class="table-grid">
                    <tr>
                        <th>
                            <div class="group">
                                <a class="link add"  href="javascript:;"><span></span>添加</a>
                            </div>
                        </th>
                        <th style="text-align: left;" width="75px">未分组字段：</th>
                        <th style="text-align: left;" width="28%" >

                            <input id="showPosition" name="showPosition" type="radio" value="1" checked="checked">在分组前显示&nbsp;&nbsp;
                            <input id="showPosition" name="showPosition" type="radio" value="2">在分组后显示&nbsp;&nbsp;
                            <input id="showPosition" name="showPosition" type="radio" value="0">不显示
                            <%--<input type="checkbox" id="isShow"  checked="checked">
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            <select id="showPosition"  >
                                <option value="1">在分组后显示</option>
                                <option value="2">在分组前显示</option>
                            </select>--%>


                    </th>
                    </tr>
                </table>
            </div>
            <div>
                <form  id ="teamItem" >

                </form>
            </div>
            <div class="panel-toolbar">
                <div class="toolBar">
                    <div class="group">
                        <a class="link save"  href="javascript:;"><span></span>保存</a>
                    </div>
                    <%--<div class="l-bar-separator"></div>--%>

                    <%--<div class="group">--%>
                        <%--<a class="link back" href="list.ht"><span></span>返回</a>--%>
                    <%--</div>--%>
                </div>
            </div>
            <div  id="cloneTemplate"  style="display: none;">
                <fieldset style="margin: 5px 0px 5px 0px;" zone="team" >
                    <legend>
                        <span>分组设置</span>
                        <div class="group" style="float: right;">
                            <a class="link del" var="del" title="删除"></a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            <a class='link moveup' title="上移"></a>
                            <a class='link movedown' title="下移"></a>
                        </div>
                    </legend>
                    <table cellpadding="1" cellspacing="1"  class="table-detail">
                        <tr>
                            <th width="10%">分组名称</th>
                            <td><input type="text" width="70%" name="teamName" class='inputText valid'  validate="{required:true}" onblur="autoGetGroupKey(this)" /></td>
                        </tr>
                        <tr>
                            <th width="10%">别名</th>
                            <td><input type="text" width="70%"  name="teamTitle" class='inputText valid'  validate="{required:true}" /></td>
                        </tr>
                        <tr>
                            <th>分组字段</th>
                            <td>
                                <div style="float: left;">
                                    <select size="10" style="width:200px;height:200px;display:inline-block;" id="teamField1" name="teamField" ondblclick="removeOpt(this)"  ></select>
                                </div>
                                <div style="float: left;">
                                    </br>
                                    <p>
                                        <input type="button" value="置顶" class="moveFieldTop">
                                    </p>
                                    </br>
                                    <p>
                                        <input type="button" value="上移" class="moveFieldUp" >
                                    </p>
                                    </br>
                                    <p>
                                        <input type="button" value="下移" class="moveFieldDown" >
                                    </p>
                                    </br>
                                    <p>
                                        <input type="button" value="置底" class="moveFieldBottom">
                                    </p>
                                </div>

                    </table>
                    </td>
                    </tr>
                    </table>
                </fieldset>
            </div>
        </div>
    </div>
</div>
</div>

</body>
</html>