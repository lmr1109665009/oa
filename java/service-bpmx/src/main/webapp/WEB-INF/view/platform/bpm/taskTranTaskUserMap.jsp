<%
	//对应TaskController里的tranTaskUserMap方法，返回某个任务节点的所有跳转分支的任务节点及其执行人员列表
%>
<%@page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
#divselect{width:236px; position:relative; z-index:1;}
#divselect cite{width:200px;height: 30px; overflow:hidden;
line-height: 30px;display:block; color:#807a62; cursor:pointer;font-style:normal;
padding-left:4px; padding-right:30px; border:1px solid #E8EEF0; border-radius: 3px;
background:url(../../../styles/blue/css/Aqua/images/controls/select-down.png) no-repeat right center;}
#divselect ul{width:234px;border:1px solid #E8EEF0; background-color:#ffffff; position:absolute; z-index:20000; margin-top:-1px; display:none;}
#divselect ul li{line-height:20px; padding: 5px 10px;display:block;color:#807a62; text-decoration:none;}
#divselect ul li:hover{background-color:#009bdb;color:#fff }
.nextPath,.nodeUserMap{display: none}
</style>
<div class="table-detail-div">
	<table class="table-detail">
		<tr>
		    <th width="15%">选择签批节点</th>
			<td width="35%">
			<div id="divselect">
		      <cite>请选择执行路径</cite>
		      <ul>
		      <c:forEach items="${nodeTranUserList}" var="nodeTranUser" varStatus="i">
				 <li count="${i.count}" ><label>
					<c:if test="${selectPath==1 }">
						<input type="radio" style="display: none" name="destTask" include="1" value="${nodeTranUser.nodeId}" <c:if test="${i.count==1}">checked="checked"</c:if> />
					</c:if>
					${nodeTranUser.nodeName}<!-- 跳转的目标节点 -->
				</label>
				</li>
				</c:forEach>
		      </ul>
		    </div>
 	
					
			</td>
			<c:if test="${canChoicePath}">
			<th width="10%">选择同步条件后的执行路径</th>
			<td >
				<c:forEach items="${nodeTranUserList}" var="nodeTranUser" varStatus="i">
						<c:forEach items="${nodeTranUser.nextPathMap}" var="nextPath">
								<div id="nextPath${i.count}" class="nextPath nextPath${i.count}" <c:if test="${i.count==1}">style="display:block"</c:if>>
									<label><input type="checkbox" include="1" name="nextPathId" value="${nextPath.key}"/>
									${nextPath.value}</label>
								</div>
						</c:forEach>
				</c:forEach>			
			</td>
			</c:if>
				
			<th width="15%">选择签批人</th>
			<td>
			<c:forEach items="${nodeTranUserList}" var="nodeTranUser" varStatus="i">
			<c:forEach items="${nodeTranUser.nodeUserMapSet}" var="nodeUserMap">
						<div id="nodeUserMap${i.count}" class="nextPath nextPath${i.count}" <c:if test="${i.count==1}">style="display:block"</c:if>>
							${nodeUserMap.nodeName}
							<input type="hidden" include='1' name="lastDestTaskId" value="${nodeUserMap.nodeId}"/>
							<span name="spanSelectUser" class="checkboxNodeUserMap">
							<c:forEach items="${nodeUserMap.taskExecutors}" var="executor">
								<label><input  type="checkbox" include='1'  name="${nodeUserMap.nodeId}_userId" checked="checked" value="${executor.type}^${executor.executeId}^${executor.executor}"/>&nbsp;${executor.executor}</label>
							</c:forEach>
							</span>
							&nbsp;&nbsp;<a href="javascript:;" class="link grant" onclick="selExeUsers(this,'${nodeUserMap.nodeId}','${scope}')">选择...</a>
						</div>
			</c:forEach>
			</c:forEach>
			</td>
			
		</tr>
	</table>
</div>
	<script type="text/javascript">
		jQuery.divselect = function(divselectid) {
			$(divselectid+" cite").html($(divselectid+" ul li").eq(0).text());
		    $(divselectid+" cite").click(function(){
		        var ul = $(divselectid+" ul");
		        if(ul.css("display")=="none"){
		            ul.slideDown("fast");
		        }else{
		            ul.slideUp("fast");
		        }
		    });
		    $(divselectid+" ul li").click(function(){
		        var txt = $(this).text();
		        var count = $(this).attr("count");
		        $('.nextPath').hide();
		        $(".nextPath"+count+"").show();
		        $(divselectid+" cite").html(txt);
		        var value = $(this).find("[name=destTask]").val();
		        $("[name=destTask]").attr("checked",false).removeAttr("include");
		        $(this).find("[name=destTask]").attr("checked","checked").attr("include","1");
		        $(divselectid+" ul").hide();
		    });
		};
		//初始化
		$(function() {
			$.divselect("#divselect");
			
			//点击空白处或者自身隐藏弹出层
            $(document).click(function (event) { $('#divselect ul').hide()});    
            $('#divselect').click(function (event) {  
                //$(this).fadeOut(1000)  
                event.stopPropagation();   
            });    
            
			//删除没选中的include，不提交
			/* $("[name=destTask]").not(":checked").each(function(){
				var me=$(this);
				me.closest("li").find("input").each(function(){
					$(this).removeAttr("include");
				});
			}); */
			//建立click方法
			/* $("[name=destTask]").click(function(){
				$("[name=destTask]").each(function(){
					var me=$(this);
					if(me.attr("checked")=="checked"){
						me.closest("li").find("input").each(function(){
							$(this).attr("include","1");
						});
					}else{
						me.closest("li").find("input").each(function(){
							$(this).removeAttr("include");
						});
					}
				});
			}); */
		});
	</script>