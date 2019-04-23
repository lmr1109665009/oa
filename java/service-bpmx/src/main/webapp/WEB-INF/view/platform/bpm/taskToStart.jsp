<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="f" uri="http://www.jee-soft.cn/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@include file="/commons/include/html_doctype.html"%>
<style type="text/css">
	/*将工具栏按钮a标签文字设置为12px*/
	.panel-toolbar a.link, .panel-toolbar button.link {
		font-size: 12px !important;
	}
</style>
<html>
<head>
	<title>流程任务-[${task.name}]执行</title>
	<%@include file="/commons/include/customForm.jsp" %>
	<%@include file="/commons/include/ueditor.jsp" %>
	<link rel="stylesheet" type="text/css" href="${ctx}/styles/other/css/setdisabledreadonlyformcolor.css"></link>
	<link rel="stylesheet" type="text/css" href="${ctx}/styles/default/css/hotent/task.css"></link>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/TaskAddSignWindow.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/TaskBackWindow.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/TaskImageUserDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/tabOperator.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/BpmTaskExeAssignDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/BpmRetransmissionDialog.js"></script>
	<script type="text/javascript">

        var taskId="${task.id}";
        var nodeId = "${task.taskDefinitionKey}";
        var nodeName = "${task.name}";
        var runId="${processRun.runId}";
        var isExtForm=${isExtForm};
        var isEmptyForm=${isEmptyForm};
        var isSignTask=${isSignTask};
        var isHidePath=${isHidePath};
        var isManage=${isManage};
        var isNeedSubmitConfirm=${bpmDefinition.submitConfirm==1};
        var isHandChoolse=${isHandChoolse};
        var bpmGangedSets=[];
        var tabid = $.getParameter("tabid");
        var form;
        //操作类型
        //1.完成任务
        //2.保存数据
        var operatorType=1;

        //补签
        function showAddSignWindow(){
            TaskAddSignWindow({taskId:taskId,callback:function(sign){
                loadTaskSign();
            }});
        }

        //加载会签数据。
        function loadTaskSign(){
            $(".taskOpinion").load('${ctx}/platform/bpm/task/toSign.ht?taskId=${task.id}');
        }
        //显示流程图
        function showTaskUserDlg(){
            TaskImageUserDialog({actInstId:${processRun.actInstId}});
        }
        //显示沟通意见。
        function showTaskCommunication(conf){
            var obj = {data:CustomForm.getData()};
            isTaskEnd(function(){
                if(!conf) conf={};
                //输入子页面
                var url=__ctx + "/platform/bpm/task/toStartCommunicate.ht?taskId=${task.id}&setId=${bpmNodeSet.setId}";

                /*KILLDIALOG*/
                DialogUtil.open({
                    height:450,
                    width: 600,
                    title : '沟通意见',
                    url: url,
                    isResize: true,
                    //自定义参数
                    obj: obj
                });
            })
        }
        //显示流转意见
        function showTaskTransTo(conf) {
            var obj = {data:CustomForm.getData()};
            isTaskEnd(function(){
                if(!conf) conf={};
                //输入子页面
                var url=__ctx + "/platform/bpm/task/toTransTo.ht?taskId=${task.id}&curUserId=${curUserId}";

                url=url.getNewUrl();


                DialogUtil.open({
                    height:500,
                    width: 550,
                    title : '显示流转意见',
                    url: url,
                    isResize: true,
                    //自定义参数
                    obj: obj,
                    sucCall:function(rtn){
                        if(rtn=="ok"){
                            handJumpOrClose();
                        }
                    }
                });
            })
        }

        function isTaskEnd(callBack){
            var url="${ctx}/platform/bpm/task/isTaskExsit.ht";
            var params={taskId:"${task.id}"};

            $.post(url,params,function(responseText){
                var obj=new com.hotent.form.ResultMessage(responseText);
                if(obj.isSuccess()){
                    callBack.apply(this);
                }
                else{
                    $.ligerDialog.warn("这个任务已经完成或被终止了!",'提示');
                }
            });
        }

        var buttonTextForm='';
        function handFormJson(json){}

        function submitForm(action,button){

            var ignoreRequired=false;
            if(button=="#btnSave" || button=="#btnReject" || button=="#btnRejectToStart"){
                ignoreRequired=true;
            }
            buttonTextForm=button;

            if($(button).hasClass("disabled"))return;
            if(isEmptyForm){
                $.ligerDialog.error("还没有设置表单!",'提示信息');
                return;
            }

            var frmWorkFlow=$('#frmWorkFlow');
            frmWorkFlow.attr("action",action);
            if(isExtForm){
                var rtn = true;
                if(button!="a.save"){
                    rtn=form.valid()
                }
                if(!rtn) return;

                $(button).addClass("disabled");
                frmWorkFlow.ajaxForm({success:showResponse }); //terry add
                if(frmWorkFlow.handleFieldName){//url表单清除命名
                    frmWorkFlow.handleFieldName();
                }
                if(frmWorkFlow.setData){
                    frmWorkFlow.setData();
                }
                frmWorkFlow.submit();

            }else{
                var rtn=CustomForm.validate({ignoreRequired:ignoreRequired});
                if(!rtn){
                    $.ligerDialog.warn("表单验证不成功,请检查表单是否正确填写!","提示信息");
                    return;
                }

                if(button!="#btnSave" || button=="#btnReject" || button=="#btnRejectToStart" ){
                    //子表必填检查(兼容新旧版本)
                    rtn = SubtablePermission.subRequired();
                    if(!rtn){
                        $.ligerDialog.warn("请填写子表表单数据！","提示信息");
                        return;
                    }
                }

                var data=CustomForm.getData();
                //WebSign控件提交。 有控件时才提交   xcx
                if(WebSignPlugin.hasWebSignField){
                    WebSignPlugin.submit();
                }

                $(button).addClass("disabled");

                var uaName=navigator.userAgent.toLowerCase();
                if(uaName.indexOf("firefox")>=0||uaName.indexOf("chrome")>=0){  //异步处理
                    //Office控件提交。 有可以提交的文档
                    if(OfficePlugin.submitNum>0){
                        OfficePlugin.submit();             //火狐和谷歌 的文档提交包括了  业务提交代码部分（完成  OfficePlugin.submit()后面的回调 函数 有 业务提交代码），所以 后面就不用加上业务提交代码
                    }else{   //没有可提交的文档时 直接做 业务提交代码
                        data=CustomForm.getData();
                        //设置表单数据
                        $("#formData").val(data);
                        FormSubmitUtil.submitFormAjax(frmWorkFlow,showResponse,handFormJson);
                    }
                }else{   //同步处理
                    //Office控件提交。 有可以提交的文档
                    if(OfficePlugin.submitNum>0){
                        OfficePlugin.submit();
                        //当提交问题 等于 提交数量的变量 时 表示所有文档 都提交了  然后做 业务相关的提交
                        if(OfficePlugin.submitNum == OfficePlugin.submitNewNum){
                            //获取自定义表单的数据
                            data=CustomForm.getData();
                            //设置表单数据
                            $("#formData").val(data);

                            FormSubmitUtil.submitFormAjax(frmWorkFlow,showResponse,handFormJson);
                            OfficePlugin.submitNewNum = 0; //重置  提交数量的变量
                        }else{
                            $.ligerDialog.warn("提交失败,OFFICE控件没能正常使用，请重新安装 ！！！","提示");
                        }
                    }else{
                        //获取自定义表单的数据
                        data=CustomForm.getData();
                        //设置表单数据
                        $("#formData").val(data);

                        FormSubmitUtil.submitFormAjax(frmWorkFlow,showResponse,handFormJson);
                    }
                }

            }
        }

        function saveData(){
            var operatorObj=getByOperatorType();
            var button="#" +operatorObj.btnId;

            var rtn=beforeClick(operatorType);

            if( rtn==false){
                return;
            }
            if(isExtForm){//提交第三方表单时检查该表单的参数
                var frm=$('#frmWorkFlow');
                if(!frm.valid()) return ;
                if(frm.setData)frm.setData();
            }

            var action="${ctx}/platform/bpm/task/saveData.ht";
            submitForm(action,button);
        }

        // 在完成任务之前，加上判断是否需要弹出意见对话框
        function completeTaskBefore(t){
            // 加上判断是否需要弹出意见对话框
            var isPopup = '${bpmNodeSet.isPopup}',
                gatherNode = '${gatherNode}';
            // 如果为汇总节点退回，必须通过弹窗模式
            if(isPopup == 1){
                opintionDialog(t);
            } else {
                //如果不需要意见填写的话，进行弹框提示确认动作！
                /*$.ligerDialog.confirm("您确定执行此操作吗？","提示",function(rtn){
                    if(rtn){
                        completeTask();
                    }
                });*/
                if (t=="reject"){
                    var isFreeBack=false;
                    if (t=="reject"){
                        isFreeBack=true;
                    }
                    opintionDialog(t,isFreeBack);
                }else {
                    $.ligerDialog.confirm("您确定执行此操作吗？","提示",function(rtn){
                        if(rtn){
                            completeTask();
                        }
                    });
                }
            }
        }


        //完成当前任务。
        function completeTask(){
            var destTask=$("input[name='destTask']:checked");
            if($("input[name='nextPathId']").length>0){
                var b=false;//是否需要判断同步条件
                if(destTask.length==0){
                    b=true;
                }else{
                    var nextPathId=destTask.parents("tr").find("[name=nextPathId]");
                    if(nextPathId.length>0){
                        b=true;
                    }
                }
                if(b){
                    var v=$("input[name='nextPathId']:checked").val();
                    if(!v){
                        $.ligerDialog.error("在同步条件后的执行路径中，您至少需要选择一条路径!",'提示信息');
                        return;
                    }

                }
            }
            //判断每条执行路径是否已选择用户
			var flag = checkChooseUsers(destTask);
			if (!flag){
				$.ligerDialog.warn("当前选择路径至少要选择一个用户!",'提示信息');
				return;
            }

            var operatorObj=getByOperatorType();
            var button="#" +operatorObj.btnId;
            var action="${ctx}/platform/bpm/task/complete.ht";
            //执行前置脚本
            var rtn=beforeClick(operatorType);
            if( rtn==false){
                return;
            }
            //确认执行任务。
            if(isNeedSubmitConfirm){
                $.ligerDialog.confirm("您确定执行此操作吗？","提示",function(rtn){
                    if(rtn){
                        submitForm(action,button);
                    }
                });
            }
            else{
                submitForm(action,button);
            }
        }

		/*
		* 检查是否当前路径是否选人
		* */
		function checkChooseUsers(destTask) {
		    var flag=false;
		    //如果是隐藏路径则不需要选人
            if($("#jumpDiv").length==0){
                flag=true;
                return flag;
            }
		    //如果是结束节点不需要选人
		    if (isEndEvent()){
		        flag=true;
		        return flag;
			}
			//如果是退回、退回到发起人操作就不需要选人
			if (operatorType==4||operatorType==5){
		        flag=true;
		        return flag;
			}

            var taskId=destTask.val();
            var multiPath=$(".nextPath");
            if (multiPath.length>0){
                for (var i=0;i<multiPath.length;i++){
                    if (multiPath.eq(i).css("display")!='block'){
                        continue;
					}
					if (isChooseUser(multiPath.eq(i).find("input[name='lastDestTaskId']").val())){
                        flag=true;
                        return flag;
					}
                }
			}else {
                if (!taskId){
                    taskId=$("input[name='lastDestTaskId']").val();
                }
                if (!taskId){
                    flag=true;
                    return flag;
                }
			}
            if(isChooseUser(taskId)){
                flag=true;
                return flag;
            }
        }


        /**
         * 是否为结束事件
		 * **/
        function isEndEvent() {
            var group=$("#destTask option:selected").parent();
            if(group&&group.attr("label")=="结束节点"){
                return true;
            }
            var destVal=$("input[name='destTask']:checked").val();
            if (destVal&&destVal.indexOf("EndEvent")>-1){
                return true;
			}
            return false;
        }

        /***
		 * 判断是否已经选择人了
         * @param taskId
         * @returns {boolean}
         */
        function isChooseUser(taskId) {
            //判断当前选择路径是否选择人员
            var lenChil = $("input[name='"+taskId+"_userId']:checked").length;
            if (lenChil>0){
                return true;
			}
			return false;
        }


        function getByOperatorType(){
            var obj={};
            switch(operatorType){
                //同意
                case 1:
                    obj.btnId="btnAgree";
                    obj.msg="执行任务成功!";
                    break;
                //反对
                case 2:
                    obj.btnId="btnNotAgree";
                    obj.msg="执行任务成功!";
                    break;
                //弃权
                case 3:
                    obj.btnId="btnAbandon";
                    obj.msg="执行任务成功!";
                    break;
                //退回
                case 4:
                    obj.btnId="btnReject";
                    obj.msg="退回任务成功!";
                    break;
                //退回到发起人
                case 5:
                    obj.btnId="btnRejectToStart";
                    obj.msg="退回到发起人成功!";
                    break;
                //保存表单
                case 8:
                    obj.btnId="btnSave";
                    obj.msg="保存表单数据成功!";
                    break;

            }
            return obj;
        }

        function getErrorByOperatorType(){
            var rtn="";
            switch(operatorType){
                //同意
                case 1:
                //反对
                case 2:
                //弃权
                case 3:
                    rtn="执行任务失败!";
                    break;
                //退回
                //退回
                case 4:
                    rtn="退回任务失败!";
                    break;
                //退回到发起人
                case 5:
                    rtn="退回到发起人失败!";
                    break;
                //保存表单
                case 8:
                    rtn="保存表单数据失败!";
                    break;
            }
            return rtn;
        }

        function showResponse(responseText){
            var operatorObj=getByOperatorType();
            $("#" +operatorObj.btnId).removeClass("disabled");
            var obj=new com.hotent.form.ResultMessage(responseText);
            if(obj.isSuccess()){
                $.ligerDialog.success(obj.data.message,'提示信息',function(){
                    var rtn=afterClick(operatorType);
                    if( rtn==false){
                        return;
                    }
                    //发送消息给父页面
                    if(buttonTextForm!="#btnSave"){
                        postMUtil.postM(postMUtil.opts.mattersList.agree);
                    }
                    handJumpOrClose();
                });
            }else{
                var title=getErrorByOperatorType();
                $.ligerDialog.err('出错信息',title,obj.getMessage());
            }
        }

        //弹出回退窗口 TODO 去除
        function showBackWindow(){
            new TaskBackWindow({taskId:taskId}).show();
        }

        $(function(){
            initForm();
            //显示路径
            if(isHandChoolse){
                chooseJumpType(2);
            }else{
                chooseJumpType(1);
            }
            resizeIframe();
            //初始化联动设置
            <c:if test="${!empty bpmGangedSets}">
            bpmGangedSets = ${bpmGangedSets};
            FormUtil.InitGangedSet(bpmGangedSets);
            </c:if>

            createQRCode();

            $("select[name='taskApprovalItems']").on("change",function(event){
                var value = $(this).val();
                $(this).next().val(value);
            });

        });
        // 生成查看流程实例的二维码
        function createQRCode(){
            var qrcode = $("#custformDiv #qrcode.qrcode");
            if(!qrcode || !qrcode.length) return;
            // 设置参数方式
            var qrcode = new QRCode('qrcode', {
                text: window.location.origin + __ctx + "/platform/bpm/processRun/getForm.ht?tab=1&runId="+runId,
                width: 128,
                height: 128,
                colorDark : '#000000',
                colorLight : '#ffffff',
                correctLevel : QRCode.CorrectLevel.H
            });
        }

        //获取是否允许直接结束。
        function getIsDirectComplete(){
            var isDirectComlete = false;
            if($("#chkDirectComplete").length>0){
                if($("#chkDirectComplete").attr("checked")=="checked"){
                    isDirectComlete = true;
                }
            }
            return isDirectComlete;
        }

        //提交第三方表单时检查该表单的参数
        function setExtFormData(){
            if(isExtForm){
                var frm=$('#frmWorkFlow');
                if(!frm.valid()) return ;
                if(frm.setData)frm.setData();
            }
        }

        function initBtnEvent(){
            //0，弃权，1，同意，2反对。
            var objVoteAgree=$("#voteAgree");
            var objBack=$("#back");

            //同意
            if($("#btnAgree").length>0){
                $("#btnAgree").click(function(){
                    setDestTaskInclude(true);
                    var voteContent = $('#voteContent'),
                        content = voteContent.val();

                    setExtFormData();

                    var isDirectComlete = getIsDirectComplete();

                    operatorType=1;
                    //同意:5，一票通过。
                    var tmp =isDirectComlete?"5":"1";

                    objVoteAgree.val(tmp);
                    objBack.val("0");

                    // 提前校验表单
                    var beforeScript=beforeClick(getByOperatorType());
                    if(beforeScript==false) return;
                    var rtn=CustomForm.validate({ignoreRequired:false});
                    if(!rtn){
                        $.ligerDialog.warn("表单验证不成功,请检查表单是否正确填写!","提示信息");
                        return;
                    }
                    completeTaskBefore();
                });

            }
            //反对
            if($("#btnNotAgree").length>0){
                $("#btnNotAgree").click(function(){
                    setExtFormData();
                    setDestTaskInclude(true);
                    var isDirectComlete = getIsDirectComplete();
                    operatorType=2;
                    ////直接一票通过
                    var tmp =isDirectComlete?'6':'2';
                    objVoteAgree.val(tmp);
                    objBack.val("0");
                    completeTaskBefore();
                });
            }
            //弃权
            if($("#btnAbandon").length>0){
                $("#btnAbandon").click(function(){
                    setExtFormData();
                    setDestTaskInclude(true);
                    operatorType=3;
                    objVoteAgree.val(0);
                    objBack.val("");
                    completeTaskBefore();
                })
            }
            //退回
            /*if($("#btnReject").length>0){
                $("#btnReject").click(function(){
                        setExtFormData();
                        setDestTaskInclude(false);
                        operatorType=4;
                        objVoteAgree.val(3);
                        objBack.val(1);
                        completeTaskBefore("reject");
                })
            }*/
            //自由退回
            if($("#btnReject").length>0){
                $("#btnReject").click(function(){
                    setExtFormData();
                    setDestTaskInclude(false);
                    operatorType=4;
                    objVoteAgree.val(3);
                    objBack.val(3);
                    completeTaskBefore("reject");
                })
            }
            //退回到发起人
            if($("#btnRejectToStart").length>0){
                $("#btnRejectToStart").click(function(){
                    var voteContent = $('#voteContent'),
                        content = voteContent.val();
                    setExtFormData();
                    setDestTaskInclude(false);

                    operatorType=5;
                    //退回到发起人
                    objVoteAgree.val(3);
                    objBack.val(2);
                    completeTaskBefore();
                })
            }
            //保存表单
            if($("#btnSave").length>0){
                $("#btnSave").click(function(){
                    setExtFormData();
                    operatorType=8;
                    saveData();
                })
            }

            //终止流程
            $("#btnEnd").click(function(){
                isTaskEnd(endTask);
            });
        }


        //退回时设置是否提交目标路径。
        function setDestTaskInclude(isNeed){
            $("[name='destTask']").each(function(){
                if(isNeed){
                    $(this).attr("include","1");
                }
                else{
                    $(this).removeAttr("include");
                }
            })
        }

        // 弹出意见对话框
        function opintionDialog(reject,isFreeBack){
            var isRequired = '${bpmNodeSet.isRequired}';
            var actDefId = $("#actDefId").val();
            if(isFreeBack||$("#back").val()==3){
                var url= __ctx + '/platform/bpm/task/freeBack.ht?taskId=${task.id}&isRequired=' +isRequired+'&actDefId='+actDefId + '&reject=' + reject;
            }else {
                var url= __ctx + '/platform/bpm/task/opDialog.ht?taskId=${task.id}&isRequired=' +isRequired+'&actDefId='+actDefId + '&reject=' + reject;
            }

            var voteContent = $("#voteContent").val();
            DialogUtil.open({
                height:350,
                width: 500,
                title : '填写意见',
                url: url,
                isResize: true,
                //自定义参数
                voteContent:voteContent,
                sucCall:function(rtn){
                    console.log(rtn);
                    if(typeof rtn=="string"){
                        // 填写到系统的意见
                        $("#voteContent").val(rtn);
                    }
                    else if(typeof rtn=="object"){
                        // 填写到系统的意见
                        $("#voteContent").val(rtn.opinion);
                        $("#forkTask2Reject").val(rtn.forkTask2Reject);
                        if(rtn.startNode){
                            $("#startNode").val(rtn.startNode);
                        }
                    }
                    // 完成当前任务
                    completeTask();
                }
            });
        }


        //终止流程。
        function endTask(){
            var url=__ctx + "/platform/bpm/task/toStartEnd.ht";
            url=url.getNewUrl();

            DialogUtil.open({
                height:350,
                width: 500,
                title : '终止流程',
                url: url,
                isResize: true,
                //自定义参数
                sucCall:function(rtn){
                    var url="${ctx}/platform/bpm/task/endProcess.ht?taskId=${task.id}";
                    var params={taskId:taskId,memo:rtn};
                    $.post(url,params,function(responseText){
                        var obj=new com.hotent.form.ResultMessage(responseText);
                        if(!obj.isSuccess()){
                            $.ligerDialog.err("提示信息","终止任务失败!",obj.getMessage());
                            return;
                        }
                        $.ligerDialog.success(obj.getMessage(),"提示信息",function(){
                            handJumpOrClose();
                        });
                    });
                }
            });
        }

        function handJumpOrClose(){
            //如果按钮类型为保存则不关闭窗口。
            if(operatorType==8) return;
            var tab = top.$("#framecenter").ligerGetTabManager();
            if(window.opener){
                try{
                    window.opener.location.href=window.opener.location.href.getNewUrl();
                }
                catch(e){}
            }
            if(tab!=null){
                top.$("iframe#"+tabid)[0].contentWindow.location.reload();
                tab.removeSelectedTabItem();
            }else{
                if(history.length>1){
                    //window.history.go(-1);//历史记录大于0，则返回
                }else{
                    window.close();//否则就关闭。
                }


            }
        }

        function initForm(){
            //初始化按钮事件。
            initBtnEvent();

            if(isEmptyForm) return;

            if(isExtForm){
                form=$('#frmWorkFlow').form({excludes:"[type=append]"});
                var formUrl=$('#divExternalForm').attr("formUrl");
                $('#divExternalForm').load(formUrl, function() {
                    hasLoadComplete=true;
                    //动态执行第三方表单指定执行的js
                    try{
                        afterOnload();
                    }catch(e){}
                    initSubForm();
                    OfficePlugin.init();
                });
            }else{
                $(".taskopinion").each(function(){
                    $(this).removeClass("taskopinion");
                    var actInstId=$(this).attr("instanceId");
                    $(this).load("${ctx}/platform/bpm/taskOpinion/listform.ht?actInstId="+actInstId);
                });

            }
        }

        function initSubForm(){
            $('#frmWorkFlow').ajaxForm({success:showResponse });
        }

        function showRoleDlg(){
            RoleDialog({callback:function(roleId,roleName){$('#forkUserUids').val(roleId);}});
        }

        function chooseJumpType(val){
            //如果是沟通不需要显示跳转信息，而且它没有executionId 会报错
            if(${task.description==15}) return;
            if(isHidePath&&isManage==0) return;
            var obj=$('#jumpDiv');
            var url="";
            if(val==1){
                url="${ctx}/platform/bpm/task/tranTaskUserMap.ht?taskId=${task.id}&selectPath=0&runId=${processRun.runId}";
            }
            //选择路径跳转
            else if(val==2){
                url="${ctx}/platform/bpm/task/tranTaskUserMap.ht?taskId=${task.id}&runId=${processRun.runId}";
            }
            //自由跳转
            else if(val==3){
                url="${ctx}/platform/bpm/task/freeJump.ht?taskId=${task.id}&runId=${processRun.runId}";
            }

            url=url.getNewUrl();

            if(val==3){  //自由跳转
                $.ajaxSetup({async:false});  //同步 获得结果后  再去查询相关的人员
                obj.html(obj.attr("tipInfo")).show().load(url);
                $.ajaxSetup({async:true}); //异步
                //自由跳转 时 从下拉节点的默认的值 中查出相关的人员
                var destTask=$('#destTask');   //默认的选中的对象
                changeDestTask(destTask);    //查出相关的人员 并改显示出来
            }else{
                obj.html(obj.attr("tipInfo")).show().load(url);
            }

        };

        //为目标节点选择执行的人员列表
        function selExeUsers(obj,nodeId,scope){
            var span=$(obj).siblings("span");

            var aryChk=$(":checkbox",span);
            var selectExecutors =[];
            aryChk.each(function(){
                var val=$(this).val();
                var k=val.split("^");
                var userObj={};
                userObj.type=k[0];
                userObj.id=k[1];
                userObj.name=k[2];
                selectExecutors.push(userObj);
            });

            if(!scope){
                scope={};
                scope.type='system',
                    scope.value='all';
                scope=JSON2.stringify(scope);
            }else{
                scope=scope.replaceAll("#@","\"");
            }


            FlowUserDialog({selectUsers:selectExecutors,scope:scope,callback:function(types,objIds,objNames){
                if(objIds==null) return;
                var aryTmp=[];
                for(var i=0;i<objIds.length;i++){
                    var check="<input type='checkbox' include='1' name='" + nodeId + "_userId' checked='checked' value='"+types[i] +"^"+  objIds[i]+"^"+objNames[i] +"'/>&nbsp;"+objNames[i];
                    aryTmp.push(check);
                }
                span.html(aryTmp.join(''));
            }});
        }

        function selectExeUsers(obj,scope){
            var destTask=$("#destTask");
            if(destTask){
                $("#lastDestTaskId").val(destTask.val());
            }
            selExeUsers(obj,destTask.val(),scope);
        }
        //显示审批历史
        function showTaskOpinions(){
            var url="${ctx}/platform/bpm/taskOpinion/list.ht?runId=${processRun.runId}&isOpenDialog=1";
            url=url.getNewUrl();
            DialogUtil.open({
                url:url,
                title:'审批历史',
                height:'600',
                width:'800'
            });
        }
        //更改
        function changeDestTask(sel){
            var nodeId=sel.value;
            if(typeof nodeId == 'undefined'){    //对象不是用原始JS的，而是通过Jquery获取的对象
                nodeId = sel.val();
            }
            if(typeof nodeId == 'undefined' || nodeId==null || nodeId==""){
                $('#jumpUserDiv').html("");
                $('#lastDestTaskId').val("");
                return;
            }
            $('#lastDestTaskId').val(nodeId);
            var url="${ctx}/platform/bpm/task/getTaskUsers.ht?taskId=${task.id}&runId=${processRun.runId}&nodeId="+nodeId;
            $.getJSON(url, function(dataJson){
                var data=eval(dataJson);
                var aryHtml=[];
                for(var i=0;i<data.length;i++){
                    var span="<input type='checkbox' include='1' name='" + nodeId + "_userId' checked='checked' value='"+data[i].type+"^"+data[i].executeId+"^"+data[i].executor+"'/>&nbsp;"+data[i].executor;
                    aryHtml.push(span);
                }
                $('#jumpUserDiv').html(aryHtml.join(''));
            });

        }

        function isTaskEnd(callBack){
            var url="${ctx}/platform/bpm/task/isTaskExsit.ht";
            var params={taskId:"${task.id}"};

            $.post(url,params,function(responseText){
                var obj=new com.hotent.form.ResultMessage(responseText);
                if(obj.isSuccess()){
                    callBack.apply(this);
                }
                else{
                    $.ligerDialog.warn("当前任务已经完成或被终止","提示信息");
                }
            });
        }

        //转交待办
        function changeAssignee(){
            if(${isCanAssignee}){
                isTaskEnd(function(){
                    BpmTaskExeAssignDialog({taskId:taskId,callback:function(rtn){
                        if(rtn){
                            handJumpOrClose();
                        }
                    }
                    });
                });
            }
            else{
                $.ligerDialog.warn("当前任务不能转办!","提示信息");
            }
        };

        //转发
        function retransmission(){
            isTaskEnd(function(){
                BpmRetransmissionDialog({runId:runId,callback:function(rtn){
                    if(rtn){
                        handJumpOrClose();
                    }
                }
                });
            });
        }


        function resizeIframe(){
            if($("#frameDetail").length==0) return;
            $("#frameDetail").load(function() {
                $(this).height($(this).contents().height()+20);
            }) ;
        }


        // 选择常用语
        function addComment(){
            var objContent=document.getElementById("voteContent");
            var selItem = $('#selTaskAppItem').val();
            jQuery.insertText(objContent,selItem);
        }

        function openForm(formUrl){
            var winArgs="dialogWidth=500px;dialogHeight=400px;help=0;status=0;scroll=0;center=1";
            var url=formUrl.getNewUrl();
            window.open(url,"",winArgs);
        }

        function reference(){
            var defId=${bpmDefinition.defId};

            var url="${ctx}/platform/bpm/processRun/getRefList.ht?defId=" +defId;

            var params="height=400,width=700,status=no,toolbar=no,menubar=no,location=no,resizable=1,scrollbars=1";
            window.open(url);
        }

        function openHelpDoc(fileId){
            var h=screen.availHeight-35;
            var w=screen.availWidth-5;
            var vars="top=0,left=0,height="+h+",width="+w+",status=no,toolbar=no,menubar=no,location=no,resizable=1,scrollbars=1";
            var showUrl = __ctx+"/platform/form/office/get.ht?fileId=" + fileId;
            window.open(showUrl,"myWindow",vars);
        }

        //增加Web签章
        function addWebSigns(){
            AddSecSignFromServiceX();          //WebSignPlugin JS类
        }

        //增加手写签章
        function addHangSigns(){
            AddSecHandSignNoPromptX();          //WebSignPlugin JS类
        }

        // 自定义打印
        function customPrint(printAlias){
            var url="${ctx}/platform/bpm/processRun/printForm.ht?runId=${processRun.runId}&printAlias="+printAlias;
            jQuery.openFullWindow(url);
        }


        //流程引用控件超链接访问流程实例
        function openProRun(obj){
            var runId = $(obj).attr("runid");
            var url="${ctx}/platform/bpm/processRun/info.ht?link=1&runId="+runId;
            jQuery.openFullWindow(url);
        }

        /**
		 * 导出PDF
         * @param runId
         */
        function downloadToPdf(runId) {
            $.ligerDialog.waitting("正在导出PDF文件...");
            $.get("${ctx}/platform/bpm/processRun/downloadToPdf.ht",{"runId":runId,"isPendingMatter":true},function (data,status) {
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
	</script>
	<style type="text/css">
		/*修正下一执行人复选框的居中*/
		#nodeUserMap1 input[type="checkbox"]{
			vertical-align: middle;
		}

	</style>

</head>
<body>

<form id="frmWorkFlow"  method="post" >
	<div class="panel">

		<div class="panel-top">
			<!-- 悬浮工具栏实现的对象topNavWrapper 和 topNav 名称ID可以更改,但要和css的对象一致-->
			<div id="topNavWrapper">
				<ul id="topNav">
					<iframe  style="position:absolute; visibility:inherit; top:0px; left:0px; width:100%; height:40px; z-index:-1; border-width:0px;"></iframe>
					<div class="l-layout-header noprint" >
						任务审批处理--<b>${task.name}</b>--<i>[${bpmDefinition.subject}-V${bpmDefinition.versionNo}]</i>
					</div>
					<c:choose>
						<c:when test="${(empty task.executionId) && task.description=='15' }">
							<jsp:include page="incTaskNotify.jsp"></jsp:include>
						</c:when>
						<c:when test="${(empty task.executionId) && (task.description=='38' || task.description=='39') }">
							<jsp:include page="incTaskTransTo.jsp"></jsp:include>
						</c:when>
						<c:otherwise>
							<jsp:include page="incToolBarNode.jsp"></jsp:include>
						</c:otherwise>
					</c:choose>
				</ul>
			</div>
		</div>


		<div class="panel-body">
			<c:choose>
				<c:when test="${bpmNodeSet.isHidePath==0||isManage==1}">
					<div id="jumpDiv" class="noprint" style="display:none;" tipInfo="正在加载表单请稍候...">
					</div>
				</c:when>
			</c:choose>
			<!-- 通知消息类型 -->
			<c:if test="${not empty handlersMap}">
				<div id="informTypeShow"  style="padding-left:20px;">
					提醒消息方式:
				 	<c:forEach items="${handlersMap}" var="item">
	                   <input type="checkbox" include="1" name="informType" value="${item.key }"/>${item.value.title }
	                </c:forEach>
	            </div>
			</c:if>
			<!--审批意见-->
			<c:choose>
				<c:when test="${bpmNodeSet.isHideOption ==0 && bpmNodeSet.isPopup==0  && task.description!='15' &&  task.description!='38' && task.description!='39'}">
					<div class="noprint">
						<jsp:include page="incTaskOpinion.jsp"></jsp:include>
					</div>
				</c:when>
				<c:when test="${  bpmNodeSet.isPopup == 1 && task.description!='15' &&  task.description!='38' && task.description!='39'}">
					<div class="hidden">
						<textarea class="hidden" include="1" id="voteContent" name="voteContent" >${taskOpinion.opinion}</textarea>
					</div>
				</c:when>
				<c:otherwise>
					<div class="hidden">
						<textarea class="hidden" include="1" id="voteContent" name="voteContent" >${taskOpinion.opinion}</textarea>
					</div>
				</c:otherwise>
			</c:choose>
			<div class="printForm panel-detail" >
				<c:choose>
					<c:when test="${isEmptyForm==true}">
						<div class="noForm">没有设置流程表单。</div>
					</c:when>
					<c:otherwise>
						<c:if test="${hasGlobalFlowNo }">
							<div align="right" style="display:none;">工单号：${processRun.globalFlowNo}</div>
						</c:if>
						<c:choose>
							<c:when test="${isExtForm}">
								<c:choose>
									<c:when test="${!empty detailUrl}">
										<iframe id="frameDetail" src="${detailUrl}" scrolling="no"  frameborder="no"  width="100%" height="100%"></iframe>
									</c:when>
									<c:otherwise>
										<div class="printForm" id="divExternalForm" formUrl="${form}"></div>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<div id="custformDiv" class="printForm" type="custform">
										${form}
								</div>
								<input type="hidden" include="1" name="formData" id="formData" />
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</div>
			<input type="hidden" id="taskId" include="1" name="taskId" value="${task.id}"/>
			<%--删除的附件id--%>
			<input type="hidden" id="delFileIds" include="1" name="delFileIds">
			<%--驳回和撤销投票为再次提交 --%>
			<c:choose>
				<c:when test="${processRun.status eq 5 or processRun.status eq 6}">
					<input type="hidden" include="1" id="voteAgree" name="voteAgree" value="34"/>
				</c:when>
				<c:otherwise>
					<input type="hidden" include="1" id="voteAgree" name="voteAgree" value="1"/>
				</c:otherwise>
			</c:choose>
			<input type="hidden" include="1" id="back" name="back" value=""/>
			<input type="hidden" include="1" id="actDefId" name="actDefId" value="${bpmDefinition.actDefId}"/>
			<input type="hidden" include="1" name="defId" value="${bpmDefinition.defId}"/>
			<input type="hidden" include="1" id="isManage" name="isManage" value="${isManage}"/>
			<input type="hidden" include="1" id="businessKey" name="businessKey" value="${processRun.businessKey}"/>
			<input type="hidden" include="1" id="startNode" name="startNode" value="${toBackNodeId}"/>
			<input type="hidden" include="1" name="curUserId" value="${curUserId}"/>
			<input type="hidden" include="1" name="curUserName" value="${curUserName}"/>
			<input type="hidden" include="1" id="currentNode" name="currentNode" value="${task.taskDefinitionKey}"/>
			<input type="hidden" include="1" name="formKey" value="${formKey}"/>
			<input type="hidden" include="1" id="forkTask2Reject" name="forkTaskRejects" />
		</div>
	</div>

</form>

</body>
</html>