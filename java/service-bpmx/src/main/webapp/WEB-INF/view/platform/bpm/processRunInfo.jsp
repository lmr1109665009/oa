<%@ page pageEncoding="UTF-8"%>
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
<style type="text/css">
	/*将工具栏按钮a标签文字设置为12px*/
	.panel-toolbar a.link, .panel-toolbar button.link{
		font-size: 12px !important;
	}
</style>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
	<%@include file="/commons/include/customForm.jsp" %>
	<title></title>
	<link rel="stylesheet" type="text/css" href="${ctx}/styles/other/css/setdisabledreadonlyformcolor.css"></link>
	<link rel="stylesheet" type="text/css" href="${ctx}/styles/default/css/hotent/task.css"></link>
	<script type="text/javascript" src="${ctx}/js/util/form.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/ProcessUrgeDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/FlowUtil.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/FlowRightDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/CheckVersion.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/SelectUtil.js" ></script>
	<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerDialog.js" ></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/FlowUtil.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/form/postMUtil.js"></script>
	<script type="text/javascript">
		/*KILLDIALOG*/
		var dialog = window;//调用页面的dialog对象(ligerui对象)
		if(frameElement){
			dialog = frameElement.dialog;
		}
	
		var isExtForm=eval('${isExtForm}');
		
		var runId=${processRun.runId};
		var status = ${processRun.status==1};
		$(function(){
			if(isExtForm){
				var formUrl=$('#divExternalForm').attr("formUrl");
				if(formUrl){
					$('#divExternalForm').load(formUrl, function() {});
				}
			}
			$(".taskopinion").each(function(){
				$(this).removeClass("taskopinion");
				var actInstId=$(this).attr("instanceId");
				$(this).load("${ctx}/platform/bpm/taskOpinion/listform.ht?actInstId="+actInstId);
			});
			
			if(status){
				var add = $("tr.toolBar").find("a.add");
				add.hide();
			}

            //初始化联动设置
            <c:if test="${!empty bpmGangedSets}">
            bpmGangedSets = ${bpmGangedSets};
            FormUtil.InitGangedSet(bpmGangedSets);
            </c:if>
		});
		
	
		
		//显示审批历史
		function showProcessRunInfo(obj){
			var url=$(obj).attr("action"),
				title = $(obj).attr("title");
			url=url.getNewUrl();
			DialogUtil.open({
				url:url,
				title:title,
				height:'650',
				width:'1200'
			});
		};
		function showProcessRunInfoImg(obj){
			var url=$(obj).attr("action"),
			title = $(obj).attr("title");
		url=url.getNewUrl();
		DialogUtil.open({
			url:url,
			title:title,
			height:'650',
			width:'1200'
		});
		}
		
		//催办
		function urge(id){
			ProcessUrgeDialog({actInstId : id});
		};
		//追回
		function recover(runId){
			FlowUtil.recover({runId:runId,backToStart:0,callback:function(){
				//window.opener.location.href=window.opener.location.href.getNewUrl();
                postMUtil.postM(postMUtil.opts.close);
			}});
		};
		//重新提交
		function executeTask(procInstId){
			 var url= "${ctx}/platform/bpm/task/toStart.ht?instanceId="+procInstId+"&voteArgee=34";
			 jQuery.openFullWindow(url);
		};
		
		//打印表单
		function printForm(runId){
			var url="${ctx}/platform/bpm/processRun/printForm.ht?runId="+runId;
			jQuery.openFullWindow(url);
		}

		//删除 
		function delByInstId(instanceId){
			/*var url="${ctx}/platform/bpm/processRun/delByRunId.ht?runId=" + runId;
			var winArgs="dialogWidth=500px;dialogHeight=250px;help=0;status=0;scroll=0;center=1";
			url=url.getNewUrl();

			DialogUtil.open({
				height:250,
				width: 500,
				title : '删除',
				url: url,
				isResize: true,
				//自定义参数
				sucCall:function(rtn){
					if(rtn!=undefined){
						try{
							window.opener.location.href=window.opener.location.href.getNewUrl();
						}
						catch(e){};
						dialog.close();
					}
				}
			});*/
           var outerWindow = window.top;
            outerWindow.$.ligerDialog.confirm('确定要删除该流程实例吗?','提示', function(rtn) {
                if(!rtn) return ;

                var url="${ctx}/platform/bpm/processRun/delByRunId.ht?runId=" + runId;
                $.post(url,function(val){
                    debugger;
                    if(val.status==1){
                        $.ligerDialog.success("删除实例成功!","提示信息",function(){
                            //jsp版关闭本页面在刷新上一级页面
                           /* window.close();
                            window.opener.location.href=window.opener.location.href.getNewUrl();*/
                            postMUtil.postM(postMUtil.opts.close);
                        });
                    }
                    else{
                        $.ligerDialog.err("提示信息","删除实例失败!",obj.getMessage());
                    }
                });
            });
		};
		function onClose(obj){
			if(window.opener ){
				try{
					window.opener.location.href=window.opener.location.href.getNewUrl();
				}
				catch(e){}
			}
			dialog.close();
		};
		
		//转发
		function divert(){
			var runId = "${param.runId}";
			forward({runId:runId});
		}
		
		function forward(conf)
		{
			if(!conf) conf={};	
			var url=__ctx + '/platform/bpm/bpmProCopyto/forward.ht?runId=' + conf.runId;
			var dialogWidth=500;
			var dialogHeight=380;
			conf=$.extend({},{dialogWidth:dialogWidth ,dialogHeight:dialogHeight ,help:0,status:0,scroll:0,center:1,reload:true},conf);

			var winArgs="dialogWidth="+conf.dialogWidth+"px;dialogHeight="+conf.dialogHeight
				+"px;help=" + conf.help +";status=" + conf.status +";scroll=" + conf.scroll +";center=" +conf.center;
			url=url.getNewUrl();
			
			/*KILLDIALOG*/
			DialogUtil.open({
				height:conf.dialogHeight, 
				width: conf.dialogWidth,
				title : '转发窗口',
				url: url, 
				isResize: true,
				sucCall:function(rtn){
				}
			});
			
		}
		
		
		//追回
		function redo(runId)
		{
			FlowUtil.recover({runId:runId,backToStart:0,callback:function(){
			}});
		}
		
		//导出word文档
		/*function downloadToWord(runId){
			var cl=$(".panel-body").clone();
			var form=cl.children();
			handFile(form);
		 	var frm=new com.suneee.form.Form();
		 	frm.creatForm("bpmPreview",__ctx+"/platform/bpm/processRun/downloadToWord.ht");
			frm.addFormEl("form",cl.html());
			frm.addFormEl("runId",runId);
			frm.submit();
		}*/
		function downloadToPdf(runId) {
            $.ligerDialog.waitting("正在导出PDF文件...");
			$.get("downloadToPdf.ht",{"runId":runId},function (data,status) {
                $.ligerDialog.closeWaitting();
				if(status!="success"){
                    $.ligerDialog.warn("请求不到网络！");
				    return;
				}
				if (data.status==0){
                    location.href="${ctx}/platform/system/sysFile/downloadPdf.ht?path="+encodeURIComponent(data.data.url);
				}else {
                    $.ligerDialog.warn(data.message);
				}
            });
        }
		
		//处理附件上传框，变成只显示附件名称
		function handFile(form){
			$("div[name='div_attachment_container']",form).each(function(){
				var me=$(this);
				var attachment=$("a.attachment",me);
				var title = attachment.attr("title");
				me.empty();
				me.text(title);
			})
		}
		
		// 自定义打印
		function customPrint(printAlias){
			var url="${ctx}/platform/bpm/processRun/printForm.ht?runId=${processRun.runId}&printAlias="+printAlias;
			jQuery.openFullWindow(url);
		}
		
		//直接打印
		function justPrint()  
		{  
			var headstr = "<html><head><title></title></head><body>";  
			var footstr = "</body>";  
			var printData = document.getElementById("justPrintDiv").innerHTML;
			var oldstr = document.body.innerHTML;  
			document.body.innerHTML = headstr+printData+footstr;  
			window.print();  
			document.body.innerHTML = oldstr;  
			return false;
		}
		
		function backTabFrame(){
			var iPad = navigator.userAgent.indexOf('iPad') > -1; //是否iPad
			if(iPad){
				iPadBackFrame.back();
			}
		}
	</script>
</head>
<body class="runinfoB">
      <div class="panel">
	      <div class="hide-panel noprint">
	       	<div class="panel-top">
				<div class="tbar-title">
					<span class="tbar-label">流程明细--${processRun.subject}</span>
					<input id="businessKey" type="hidden" value="${processRun.businessKey }" />
					<input id="pkField" type="hidden" value="${processRun.runId }" />
				</div>
				<div class="panel-toolbar">
					<div class="toolBar">
					<c:if test="${isPad == true}">
                        <div class="l-bar-separator"></div>
		<div class="group">
		<a class="link backBtn" onclick="backTabFrame()"><span></span>返回</a></div>
		</c:if>
							<c:if test='${isCanRedo and (param.prePage =="myFinishedTask")}'>
								<div class="l-bar-separator"></div>
								<div class="group">
									<a href="javascript:void(0);" onclick="recover(${processRun.runId})" class="link redo"><span></span>撤回</a>
								</div>
								<div class="l-bar-separator"></div>
								<div class="group">
									<a href="javascript:void(0);" onclick="urge(${processRun.actInstId})" class="link urge"><span></span>催办</a>
								</div>
							</c:if>
							<c:if test="${isFirst and (processRun.status==4 or processRun.status==5)}">
								<div class="l-bar-separator"></div>
								<div class="group">
									<a href="javascript:executeTask('${processRun.actInstId}')" class="link run"><span></span>重新提交</a>
								</div>
								<div class="l-bar-separator"></div>
								<div class="group">
									<a href="javascript:delByInstId(${processRun.actInstId})" class="link del"><span></span>删除</a>
								</div>
							</c:if>
							<%-- <c:if test="${isCopy}">
								<div class="l-bar-separator"></div>
								<div class="group">
									<a href="javascript:void(0);" class="link copy" onclick="checkVersion({type:false,defId:'${processRun.defId}',businessKey:'${processRun.businessKey}'});"><span></span>复制</a>
								</div>
							</c:if> --%>
							<%-- <c:if test="${isFinishedDiver}"> --%>
							<div class="l-bar-separator"></div>
							<div class="group">
								<a href="javascript:void(0);" onclick="divert(${processRun.runId},${processRun.actInstId})" class="link goForward"><span></span>转发</a>
							</div>
							<%-- </c:if> --%>
						 <div class="l-bar-separator"></div>
						 <div class="group"><a action="${ctx}/platform/bpm/processRun/get.ht?runId=${processRun.runId}" onclick="showProcessRunInfo(this)" class="link detail" title="运行明细"><span></span>运行明细</a></div>
						 <div class="l-bar-separator"></div>
						 <div class="group"><a action="${ctx}/platform/bpm/processRun/processImage.ht?runId=${processRun.runId}" onclick="showProcessRunInfoImg(this)" class="link flowDesign" title="流程图"><span></span>流程图</a></div>
						 <div class="l-bar-separator"></div>
						 <div class="group"><a action="${ctx}/platform/bpm/taskOpinion/list.ht?action=process&runId=${processRun.runId}" onclick="showProcessRunInfo(this)" class="link history" title="审批历史"><span></span>审批历史</a></div>
						 <div class="l-bar-separator"></div>
						 <%-- <c:if test="${processRun.status==2}"> --%>
						 	<div class="group"><a action="${ctx}/platform/bpm/bpmProCopyto/getCopyUserByInstId.ht?runId=${processRun.runId}" onclick="showProcessRunInfo(this)" class="link copyTo" title="抄送人"><span></span>抄送人</a></div>
						 	<div class="l-bar-separator"></div>
					 	<%-- </c:if> --%>
						<div class="group"><a href="javascript:void(0);" onclick="downloadToPdf(${processRun.runId})" class="link print"><span></span>导出成PDF文档</a></div>
						<div class="l-bar-separator"></div>
					 	<c:if test="${isPrintForm}">
						    <a href="javascript:void(0);" onclick="justPrint()" class="link print"><span></span>打印表单</a>
					  		 <div class="l-bar-separator"></div>
					   </c:if>
						<div class="group"><a class="link print" onclick="printForm(${processRun.runId});"><span></span>打印</a></div>
					</div>
				</div>
			</div>
		</div>
		<div class="panel-body panel-detail printForm" id="justPrintDiv">
			<c:if test="${hasGlobalFlowNo }">
				<div align="left" class="flownoL" style="display:none;"><span>工单号：</span><P>${processRun.globalFlowNo}</P></div>
			</c:if>
			<c:choose>
				<c:when test="${isExtForm==true }">
					<div id="divExternalForm" formUrl="${form}"></div>
				</c:when>
				<c:otherwise>
					${form}
				</c:otherwise>
			</c:choose>
	   </div>
	   <input type="hidden" id="businessKey" name="businessKey" value="${processRun.businessKey}"/>
     </div> 
</body>
</html>