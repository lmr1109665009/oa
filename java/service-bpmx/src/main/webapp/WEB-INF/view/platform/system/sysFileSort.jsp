<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>附件排序</title>
	
	<%@include file="/commons/include/form.jsp" %>
	<style type="text/css">
		html,body{
			height: auto;
		}
	</style>
	<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerDialog.js" ></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/util/SelectOption.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/form/AttachMent.js" ></script>
	<script type="text/javascript">
	$(function() {
        /*KILLDIALOG*/
        var dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)

        //格子
        //for(var i=1;i<=10;i++)$("#sel_cell").append("<option value='"+i+"'>"+i+"</option>");
        //顶部、向上、向下、底部
        var obj = document.getElementById('dicIds');
        $("#btn_top").click(function () {

            __SelectOption__.moveTop(obj);
        });
        $("#btn_up").click(function () {
            __SelectOption__.moveUp(obj, 1);
        });
        $("#btn_down").click(function () {
            __SelectOption__.moveDown(obj, 1);
        });
        $("#btn_bottom").click(function () {
            __SelectOption__.moveBottom(obj);
        });




    });
    function changeSort() {
        var obj = document.getElementById('dicIds');
        var arryJson = [];
        for (var i = 0; i < obj.length; i++) {
            var id = obj[i].id;
            var name = document.getElementById(id).text;
            var json = {id: id, name: name};
            arryJson.push(json);
        }
        $("#dataForm").val(JSON.stringify(arryJson));
    }
	
	</script>
</head>
<body>
<div class="panel-top">
				<div class="tbar-title">
					<span class="tbar-label">附件排序</span>
				</div>
		</div>
	<form id="dataForm" method="post" action="">
		<div class="panel-data">
			<table class="table-detail"  border="0" cellspacing="0" cellpadding="0" >
			
				<tr>
					<td class="form_title" align="center" >
						<select id="dicIds" name="dicIds" size="12" style="width:100%;height:180px;" multiple="multiple">
							<c:forEach items="${aryJson}" var="d">
								<option id="${d.id}">${d.name}</option>
							</c:forEach>
						</select>
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
