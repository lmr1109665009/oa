<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://www.jee-soft.cn/functions" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="java.util.regex.Matcher" %>
<%
    // \b 是单词边界(连着的两个(字母字符 与 非字母字符) 之间的逻辑上的间隔),
    // 字符串在编译时会被转码一次,所以是 "\\b"
    // \B 是单词内部逻辑间隔(连着的两个字母字符之间的逻辑上的间隔)
    String phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i"
            +"|windows (phone|ce)|blackberry"
            +"|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp"
            +"|laystation portable)|nokia|fennec|htc[-_]"
            +"|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
    String tableReg = "\\b(ipad|tablet|(Nexus 7)|up.browser"
            +"|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";

    //移动设备正则匹配：手机端、平板
    Pattern phonePat = Pattern.compile(phoneReg, Pattern.CASE_INSENSITIVE);
    Pattern tablePat = Pattern.compile(tableReg, Pattern.CASE_INSENSITIVE);
    //获取ua，用来判断是否为移动端访问
    String userAgent = request.getHeader( "USER-AGENT" ).toLowerCase();
    if(null == userAgent){
        userAgent = "";
    }
    boolean isPhone=false;
    boolean isPad=false;
    // 匹配
    Matcher matcherPhone = phonePat.matcher(userAgent);
    Matcher matcherTable = tablePat.matcher(userAgent);
    if(matcherPhone.find()){
        isPhone=true;
    }
    if(matcherTable.find()){
    	isPad=true;
    }
    request.setAttribute("isPad", isPad);

%>
<script type="text/javascript">
	function beforeClick(operatorType){<c:if test="${not empty mapButton.button}">
		switch(operatorType){<c:forEach items="${mapButton.button }" var="btn"  ><c:if test="${not empty btn.prevscript}">
				case ${btn.operatortype}:
				${btn.prevscript}
				break;</c:if></c:forEach>
			}</c:if>
	}
	
	function afterClick(operatorType){<c:if test="${not empty mapButton.button}">
		switch(operatorType){<c:forEach items="${mapButton.button }" var="btn" ><c:if test="${not empty btn.afterscript}">
			case ${btn.operatortype}:
				${btn.afterscript}
				break;</c:if></c:forEach>
			}</c:if>
	}
	
	function backTabFrame(){
		var iPad = navigator.userAgent.indexOf('iPad') > -1; //是否iPad
		if(iPad){
			iPadBackFrame.back();
		}
	}
</script>
<div class="noprint">
	<div class="panel-toolbar">
		<div class="toolBar">
		 <c:if test="${isPad == true}">
                        <div class="l-bar-separator"></div>
		<div class="group"><a class="link backBtn" onclick="backTabFrame()"><span></span>返回</a></div>
		</c:if>
			<c:choose>
				<c:when test="${ isManage==0 }">
						<c:choose>
						<c:when test="${empty mapButton }">
							<c:if test="${isSignTask && isAllowDirectExecute}">
								<div class="group">
									特权：<input type="checkbox" value="1" id="chkDirectComplete"><label for="chkDirectComplete"><span></span>直接结束</label>
								</div>
							</c:if>
						<div class="group"><a id="btnAgree" class="link agree"><span></span>批准</a></div>
							<div class="group"><a id="btnSave" class="link save" ><span></span>保存表单</a></div>
						<c:if test="${isSignTask==false && task.description!='39'}">
							<div class="group"><a class="link switchuser" onclick="showTaskTransTo()"><span></span>加签</a></div>
							<div class="l-bar-separator"></div>
						</c:if>
						<div class="l-bar-separator"></div>
						<c:if test="${isSignTask==true}">
							<div class="group"><a id="btnNotAgree" class="link notAgree"><span></span>反对</a></div>
							<div class="l-bar-separator"></div>
							<div class="group"><a id="btnAbandon" class="link abandon"><span></span>弃权</a></div>
							<div class="l-bar-separator"></div>
							<c:if test="${isAllowRetoactive==true}">
								<div class="group"><a class="link flowDesign" onclick="showAddSignWindow()"><span></span>补签</a></div>
								<div class="l-bar-separator"></div>
							</c:if>
						</c:if>
						
						<div class="l-bar-separator"></div>
						<c:if test="${isCanAssignee}">
							<div class="group"><a id="btnForward" class="link goForward " onclick="changeAssignee()"><span></span>交办</a></div>
							<div class="l-bar-separator"></div>
						</c:if>
						
						<c:if test="${isCanBack || gatherNode||isCanFreeback}">
							<div class="group"><a id="btnReject" class="link reject" ><span></span>退回</a></div>
							<div class="l-bar-separator"></div>
						</c:if>
						<c:if test="${isCanBack || gatherNode}">
							<div class="group"><a id="btnRejectToStart" class="link rejectToStart" ><span></span>退回到发起人</a></div>
							<div class="l-bar-separator"></div>
						</c:if>
						<div class="group"><a class="link setting" onclick="showTaskUserDlg()"><span></span>流程图</a></div>
						<div class="l-bar-separator"></div>
						<div class="group"><a class="link search" onclick="showTaskOpinions()"><span></span>审批历史</a></div>
						<div class="l-bar-separator"></div>
						<div class="group"><a class="link sendMessage" onclick="showTaskCommunication()"><span></span>沟通</a></div>
						<div class="l-bar-separator"></div>
						
						<div class="group"><a class="link print" onclick="window.print();"><span></span>打印</a></div>
						
						<c:if test="${isExtForm}">
							<c:choose>
								<c:when test="${!empty detailUrl && !empty form}">
									<div class="l-bar-separator"></div>
									<div class="group"><a class="link edit" onclick="openForm('${form}')" ><span></span>编辑表单</a></div>
								</c:when>
							</c:choose>
						</c:if>
					</c:when>
					<c:otherwise>
						<c:if test="${not empty mapButton.button}">
							<c:if test="${isSignTask && isAllowDirectExecute}">
								<div class="group">
									特权：<input type="checkbox" value="1" id="chkDirectComplete"><label for="chkDirectComplete">直接结束</label>
								</div>
							</c:if>
							<c:forEach items="${mapButton.button }" var="btn"  varStatus="status">
								<c:choose>
									<c:when test="${btn.operatortype==1 }">
										<!--  同意-->
										<div class="group"><a id="btnAgree" class="link agree"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
									<c:when test="${btn.operatortype==17 && task.description!='39'}">
										<!--加签-->
										<div class="group"><a class="link switchuser" onclick="showTaskTransTo()"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
									<c:when test="${btn.operatortype==2 }">
										<!-- 反对-->
										<div class="group"><a id="btnNotAgree" class="link notAgree"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
									<c:when test="${btn.operatortype==3 }">
										<!--弃权-->
										<c:if test="${isSignTask==true}">
										<div class="group"><a id="btnAbandon" class="link abandon"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
										</c:if>
									</c:when>
									
									<c:when test="${btn.operatortype==4 }">
										<!--退回-->
										<c:if test="${isCanBack || gatherNode||isCanFreeback}">
										<div class="group"><a id="btnReject" class="link reject"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
										</c:if>
									</c:when>
									<c:when test="${btn.operatortype==5 }">
										<!--退回到发起人-->
										<c:if test="${isCanBack && toBackNodeId!=task.taskDefinitionKey}">
											<div class="group"><a id="btnRejectToStart" class="link rejectToStart"><span></span>${btn.btnname }</a></div>
											<div class="l-bar-separator"></div>
										</c:if>
									</c:when>
									<c:when test="${btn.operatortype==6 && isCanAssignee}">
										<!--交办-->
										<div class="group"><a id="btnForward" class="link goForward" onclick="changeAssignee()"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
									
									<c:when test="${btn.operatortype==18}">
										<!--转发-->
										<div class="group"><a id="btnForward" class="link goForward" onclick="retransmission()"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
									<c:when test="${btn.operatortype==20}">
										<!-- 终止 -->
										<div class="group"><f:a alias="endProcess" css="link abandon"  id="btnEnd" href="javascript:;"><span></span>终止</f:a></div>
									</c:when>
									<c:when test="${btn.operatortype==7 }">
										<c:if test="${isSignTask==true}">
										<!--补签-->
										<c:if test="${isAllowRetoactive==true}">
											<div class="group"><a class="link flowDesign" onclick="showAddSignWindow()"><span></span>${btn.btnname }</a></div>
											<div class="l-bar-separator"></div>
										</c:if>
										</c:if>
									</c:when>
									<c:when test="${btn.operatortype==8 }">
										<!--保存表单-->
										<div class="group"><a id="btnSave" class="link save" ><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
									<c:when test="${btn.operatortype==9 }">
										<!--流程图-->
										<div class="group"><a class="link setting" onclick="showTaskUserDlg()"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
									<c:when test="${btn.operatortype==10 }">
										<!--打印-->
										<div class="group"><a class="link print" onclick="window.print();"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
									<c:when test="${btn.operatortype==60 }">
										<!--导出PDF-->
										<div class="group"><a class="link print" onclick="downloadToPdf(${taskId});"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
									<c:when test="${btn.operatortype==11 }">
										<!--审批历史-->
										<div class="group"><a class="link history" onclick="showTaskOpinions()"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
									<c:when test="${btn.operatortype==14 }">
										<!--Web签章-->
										<div class="group"><a class="link addWebSigns" onclick="addWebSigns()"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
									<c:when test="${btn.operatortype==15 }">
										<!--手写签章-->
										<div class="group"><a class="link addHangSigns" onclick="addHangSigns()"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
									<c:when test="${btn.operatortype==16 }">
										<!--沟通-->
										<div class="group"><a class="link sendMessage" onclick="showTaskCommunication()"><span></span>${btn.btnname }</a></div>
										<div class="l-bar-separator"></div>
									</c:when>
								</c:choose>
							</c:forEach>
						</c:if>
						<c:if test="${isExtForm}">
							<c:choose>
								<c:when test="${!empty detailUrl && !empty form}">
									<div class="l-bar-separator"></div>
									<div class="group"><a class="link edit" onclick="openForm('${form}')" ><span></span>编辑表单</a></div>
								</c:when>
							</c:choose>
						</c:if>
					</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
						<c:if test="${isSignTask && isAllowDirectExecute}">
								<div class="group">
									特权：<input type="checkbox" value="1" id="chkDirectComplete"><label for="chkDirectComplete">直接结束</label>
								</div>
						</c:if>
						<div class="group"><a id="btnAgree" class="link agree"><span></span>批准</a></div>
					    <div class="group"><a id="btnSave" class="link save" ><span></span>保存表单</a></div>
						<c:if test="${isSignTask==false && task.description!='39'}">
							<div class="group"><a class="link switchuser" onclick="showTaskTransTo()"><span></span>加签</a></div>
							<div class="l-bar-separator"></div>
						</c:if>
						<div class="l-bar-separator"></div>
						<c:if test="${isSignTask==true}">
							<div class="group"><a id="btnNotAgree" class="link notAgree"><span></span>反对</a></div>
							<div class="l-bar-separator"></div>
							<div class="group"><a id="btnAbandon" class="link abandon"><span></span>弃权</a></div>
							<div class="l-bar-separator"></div>
							<c:if test="${isAllowRetoactive==true}">
								<div class="group"><a class="link flowDesign" onclick="showAddSignWindow()"><span></span>补签</a></div>
								<div class="l-bar-separator"></div>
							</c:if>
						</c:if>
						
						<div class="l-bar-separator"></div>
						<c:if test="${isCanBack || gatherNode||isCanFreeback}">
							<div class="group"><a id="btnReject" class="link reject" ><span></span>退回</a></div>
							<div class="l-bar-separator"></div>
						</c:if>
						<c:if test="${isCanBack || gatherNode}">
							<div class="group"><a id="btnRejectToStart" class="link rejectToStart" ><span></span>退回到发起人</a></div>
							<div class="l-bar-separator"></div>
						</c:if>
						<%-- <div class="group"><f:a alias="endProcess" css="link abandon"  id="btnEnd" href="javascript:;"><span></span>终止</f:a></div> --%>
						<div class="l-bar-separator"></div>
						<div class="group"><a class="link setting" onclick="showTaskUserDlg()"><span></span>流程图</a></div>
						<div class="l-bar-separator"></div>
						<div class="group"><a class="link search" onclick="showTaskOpinions()"><span></span>审批历史</a></div>
						<div class="l-bar-separator"></div>

						<div class="group"><a class="link print" onclick="window.print();"><span></span>打印</a></div>
						<div class="l-bar-separator"></div>
						
						<div class="group"><a class="link sendMessage" onclick="showTaskCommunication();"><span></span>沟通</a></div>
						<div class="l-bar-separator"></div>
						<!--Web签章-->
						<div class="group"><a class="link addWebSigns" onclick="addWebSigns()"><span></span>Web签章</a></div>
						<div class="l-bar-separator"></div>
						<!--手写签章-->
						<div class="group"><a class="link addHangSigns" onclick="addHangSigns()"><span></span>手写签章</a></div>
						<div class="l-bar-separator"></div>
									
						<c:if test="${isExtForm}">
							<c:choose>
								<c:when test="${!empty detailUrl && !empty form}">
									<div class="l-bar-separator"></div>
									<div class="group"><a class="link edit" onclick="openForm('${form}')" >编辑表单</a></div>
								</c:when>
							</c:choose>
						</c:if>
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${ isManage==0 }">
				<c:if test="${fn:indexOf(bpmNodeSet.jumpType,'1')!=-1}">
					<label style="height: 40px;line-height: 40px;float: left;padding: 0 0 0 10px;"><input type="radio" style="vertical-align: middle;top: -1px;" <c:if test="${!isHandChoolse}"> checked='checked' </c:if> name="jumpType" onclick="chooseJumpType(1)" value="1" />&nbsp;正常跳转</label>
				</c:if>
				<c:if test="${fn:indexOf(bpmNodeSet.jumpType,'2')!=-1}">
					<label style="height: 40px;line-height: 40px;float: left;padding: 0 0 0 10px;"><input type="radio" style="vertical-align: middle;top: -1px;" <c:if test="${isHandChoolse}"> checked='checked' </c:if> name="jumpType" onclick="chooseJumpType(2)" value="2" />&nbsp;选择路径跳转</label>
				</c:if>
				<c:if test="${fn:indexOf(bpmNodeSet.jumpType,'3')!=-1}">
					<label style="height: 40px;line-height: 40px;float: left;padding: 0 0 0 10px;"><input type="radio" style="vertical-align: middle;top: -1px;" name="jumpType" onclick="chooseJumpType(3)" value="3" />&nbsp;自由跳转</label>
				</c:if>
			</c:when>
			<c:otherwise>
				<label style="height: 40px;line-height: 40px;float: left;padding: 0 0 0 10px;"><input type="radio" <c:if test="${!isHandChoolse}"> checked='checked' </c:if> name="jumpType" onclick="chooseJumpType(1)" value="1" />&nbsp;正常跳转</label>
				<label style="height: 40px;line-height: 40px;float: left;padding: 0 0 0 10px;"><input type="radio" <c:if test="${isHandChoolse}"> checked='checked' </c:if> name="jumpType" onclick="chooseJumpType(2)" value="2" />&nbsp;选择路径跳转</label>
				<label style="height: 40px;line-height: 40px;float: left;padding: 0 0 0 10px;"><input type="radio" name="jumpType" onclick="chooseJumpType(3)" value="3" />&nbsp;自由跳转</label>
			</c:otherwise>
		</c:choose>
		<c:if test="${bpmDefinition.allowRefer==1}">
			<!-- 流程参考 -->
			<div class="group"><a id="btnReference" class="link reference" onclick="reference()"><span></span>流程参考</a></div>
		</c:if>
		
			<c:if test="${bpmDefinition.attachment!=''}">
				<%@include file="incHelp.jsp" %>
			</c:if>
			
			
		</div>	
	</div>
	
</div>