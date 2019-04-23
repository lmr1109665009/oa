<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
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
	
	function showTaskOpinions(runId){
		var url="${ctx}/platform/bpm/taskOpinion/list.ht?runId="+runId+"&isOpenDialog=1";
		url=url.getNewUrl();
		DialogUtil.open({
			url:url,
			title:'审批历史',
			height:'600',
			width:'800'
		});
	}
	
	function backStartFrame(){
		var iPad = navigator.userAgent.indexOf('iPad') > -1; //是否iPad
		if(iPad){
			iPadBackFrame.back();
		}
	}

    //直接打印
    function justPrint()
    {
        var headstr = "<html><head><title></title></head><body>";
        var footstr = "</body>";
        var printData = document.getElementById("custformDiv").innerHTML;
        var oldstr = document.body.innerHTML;
        document.body.innerHTML = headstr+printData+footstr;
        window.print();
        document.body.innerHTML = oldstr;
        return false;
    }

</script>
<!-- 悬浮工具栏实现的CSS -->

<div id="topNavWrapper">
	<ul id="topNav">
	<iframe  frameborder="0"  style="position:absolute; visibility:inherit; top:0px; left:0px; width:100%; height:42px; z-index:-1;"></iframe>
	<div class="hide-panel">
		<div class="panel-top">
			<div class="tbar-title noprint">
				<span class="tbar-label">启动流程--${bpmDefinition.subject} --V${bpmDefinition.versionNo}</span>
			</div>
			<div class="panel-toolbar noprint" >
				<div class="toolBar">
					
					<c:choose>
						<c:when test="${empty mapButton }">
							<div class="group"><a class="run am-btn am-btn-primary"><span></span>提交</a></div>
							<c:choose>
								<c:when test="${isDraft}">
									<div class="l-bar-separator"></div>
									<div class="group"><a class="save am-btn am-btn-secondary"><span></span>保存111</a></div>
								</c:when>
								<c:otherwise>
									<div class="l-bar-separator"></div>
									<div class="group"><a class="save am-btn am-btn-warning"><span></span>保存数据</a></div>
								</c:otherwise>
							</c:choose>
							<div class="l-bar-separator"></div>
							<div class="group"><a class="reset am-btn am-btn-warning"><span></span>重置</a></div>
							<div class="l-bar-separator"></div>
							<div class="group"><a class="am-btn am-btn-success" onclick="showBpmImageDlg()"><span></span>流程图</a></div>
							
						</c:when>
						<c:otherwise>
							<c:if test="${not empty mapButton.button}">
								<c:forEach items="${mapButton.button }" var="btn"  varStatus="status">
									<c:choose>
										
										<c:when test="${btn.operatortype==1 }">
											<!-- 启动流程 -->
											<div class="group"><a class="run am-btn am-btn-primary"><span></span>${btn.btnname }</a></div>
										</c:when>
										
										<c:when test="${btn.operatortype==2 }">
											<!--流程示意图 -->
											<div class="group"><a class="am-btn am-btn-success" onclick="showBpmImageDlg()"><span></span>${btn.btnname }</a></div>
										</c:when>
										
										<c:when test="${btn.operatortype==3 }">
											<!--打印 -->
											<%--<div class="group"><a class="am-btn am-btn-danger" onclick="justPrint();"><span></span>${btn.btnname }</a></div>--%>
											<div class="group"><a class="am-btn am-btn-danger" onclick="window.print();"><span></span>${btn.btnname }</a></div>
										</c:when>
										
										<c:when test="${btn.operatortype==6 }">
											<!--保存表单 -->
											<c:choose>
												<c:when test="${isDraft}">
													<div class="l-bar-separator"></div>
													<div class="group"><a class="save isDraft am-btn am-btn-primary"><span></span>保存222</a></div>
												</c:when>
												<c:otherwise>
													<div class="l-bar-separator"></div>
													<div class="group"><a class="save am-btn am-btn-danger"><span></span>保存数据</a></div>
												</c:otherwise>
											</c:choose>
											<div class="l-bar-separator"></div>
											<div class="group"><a class="reset am-btn am-btn-warning"><span></span>重置</a></div>
										</c:when>
										
		
										<c:when test="${btn.operatortype==14 }">
											<!--Web签章-->
											<div class="group"><a class="am-btn am-btn-danger" onclick="addWebSigns()"><span></span>${btn.btnname }</a></div>
										</c:when>
									
										<c:when test="${btn.operatortype==15 }">
											<!--手写签章-->
											<div class="group"><a class="am-btn am-btn-danger" onclick="addHangSigns()"><span></span>${btn.btnname }</a></div>
										</c:when>
									</c:choose>
									
									<c:if test="${not status.last}">
										<div class="l-bar-separator"></div>
									</c:if>
									
									
								</c:forEach>
							</c:if>
							
						</c:otherwise>
						
					</c:choose>	
					<c:if test="${not empty param.relRunId}">
						<div class="l-bar-separator"></div>
						<div class="group"><a class="link search" onclick="showTaskOpinions(${param.relRunId})"><span></span>审批历史</a></div>
					</c:if>
                             <c:if test="${ isPad == true}">
                             <div class="l-bar-separator"></div>
							<div class="group"><a class="am-btn am-btn-success" onclick="backStartFrame()"><span></span>返回</a></div>
							</c:if>

					<%@include file="incHelp.jsp" %>
				</div>
			</div>
		</div>
	</div>

</ul>
</div>
							
								
						