<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
	$(function(){//隐藏意见控件
		var opinion = $("textarea[opinionname]");
		opinion.removeAttr("validate");
		opinion.hide();
	});
	
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
	
	function openOpinionDialog(){
		/* var rtn=CustomForm.validate();
		if(!rtn){
			$.ligerDialog.warn('表单验证不成功,请检查表单是否正确填写!','提示');
			return;
		} */
		var obj = {data:CustomForm.getData()};
		//增加抄送人员
		var url=__ctx + "/platform/bpm/task/transToOpinionDialog.ht?taskId=${task.id}";
		var winArgs="dialogWidth=500px;dialogHeight=300px;help=0;status=0;scroll=0;center=1";
		url=url.getNewUrl();
		
		/*KILLDIALOG*/
		DialogUtil.open({
			height:300,
			width: 500,
			title : '抄送人员',
			url: url, 
			isResize: true,
			//自定义参数
			obj: obj,
			sucCall:function(rtn){
				if(rtn=="ok"){
					if(window.opener && window.opener.location){
						window.opener.location.href=window.opener.location.href.getNewUrl();
					}
					window.close();
                    postMUtil.postM(postMUtil.opts.close);
				}
			}
		});
	}
	
	function backTabFrame(){
		var iPad = navigator.userAgent.indexOf('iPad') > -1; //是否iPad
		if(iPad){
			iPadBackFrame.back();
		}
	}
</script>
<div class="panel-top noprint">
	<div class="panel-toolbar">
		<div class="toolBar">
		 <c:if test="${isPad == true}">
                        <div class="l-bar-separator"></div>
		<div class="group">
		<a class="link backBtn" onclick="backTabFrame()"><span></span>返回</a></div>
		</c:if>
			<div class="group"><a id="btnNotify" class="link agree" onclick="openOpinionDialog()"><span></span>反馈</a></div>
			<div class="l-bar-separator"></div>
			<div class="group"><a id="btnSave" class="link save"><span></span>保存表单</a></div>
			<div class="l-bar-separator"></div>
			<div class="group"><a class="link setting" onclick="showTaskUserDlg()"><span></span>流程图</a></div>
			<div class="l-bar-separator"></div>
			<div class="group"><a class="link search" onclick="showTaskOpinions()"><span></span>审批历史</a></div>
			<div class="l-bar-separator"></div>
			<div class="group"><a class="link sendMessage" onclick="showTaskCommunication()"><span></span>沟通</a></div>
			<div class="l-bar-separator"></div>
			<div class="group"><a class="link switchuser" onclick="showTaskTransTo()"><span></span>加签</a></div>
			
                       
		</div>
	</div>
</div>