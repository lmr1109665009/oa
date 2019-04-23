<%@ page pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
	<title>流程启动--${bpmDefinition.subject} --版本:${bpmDefinition.versionNo}</title>
	<%@include file="/commons/include/customForm.jsp" %>
	<script type="text/javascript" src="${ctx}/js/lg/util/DialogUtilWin.js" ></script>
	<link  rel="stylesheet" type="text/css" href="${ctx}/styles/default/css/hotent/task.css"></link>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/bpm/BpmImageDialog.js"></script>
	<script type="text/javascript">
        var isExtForm=${isExtForm};
        var isFormEmpty=${isFormEmpty};
        var isNeedSubmitConfirm=${bpmDefinition.submitConfirm==1};
        var bpmGangedSets=[];
        var hasLoadComplete=false;
        var actDefId="${bpmDefinition.actDefId}";
        var defId="${param.defId}";
        var form;
        /* var dialog = frameElement.dialog; */

        $(function(){
            //设置表单。
            initForm();
            //启动流程事件绑定。
            $("a.run").click(function(){
                var flowNodes = $("input[name='flowNode']");
                if(flowNodes && flowNodes.length>1){
                    var flowNode = $("input[type='radio']:checked");
                    if(flowNode && flowNode.length==1){
                        var destTask = $("input[name='destTask']:checked");
                        if (destTask.length>0) {
                            var v = $(".checkboxNodeUserMap input[type='checkbox']:checked").length;
                            if (!v) {

                                $.ligerDialog.error("在选择签批节点中，您至少需要选择一个签批人!", '提示信息');
                                return;
                            }
                        }
                        startWorkFlow();
                    }
                    else{
                        $.ligerDialog.warn("请选择一个跳转节点!", '提示');
                        return;
                    }
                }else{
                    var destTask = $("input[name='destTask']:checked");
                    if (destTask.length>0) {
                        var v = $(".checkboxNodeUserMap input[type='checkbox'][name='"+destTask.val()+"_userId']:checked").length;
                        if (!v) {
                            $.ligerDialog.error("在选择签批节点中，您至少需要选择一个签批人!", '提示信息');
                            return;
                        }
                    }
                    startWorkFlow();
                }
            });
            //保存表单
            $("a.save").click(function(){
                saveForm(this);
            });
            //重置表单
            $("a.reset").click(function(){
                var fieldName=$(this).attr("name");
                if(fieldName!=undefined&&fieldName!=null&&fieldName!=""){
                    return;
                }
                $("#frmWorkFlow").resetForm();
                var parentObj = $(this).parent();
                $("input",parentObj).each(function(){
                    $(this).val('');
                })
            });

            $("#flowNodeList").delegate("input", "click", function() {
                $("#startNode").val($(this).val());
            });

            //选择第一步任务的执行人
            chooseJumpType();
            //初始化联动设置
            <c:if test="${!empty bpmGangedSets}">
            bpmGangedSets = ${bpmGangedSets};
            FormUtil.InitGangedSet(bpmGangedSets);
            </c:if>
            //启动流程时隐藏意见控件
            $(".taskopinion").hide();

        });

        //设置表单。
        function initForm(){
            //初始化百度编辑器
            if(isFormEmpty) return;
            //表单不为空的情况。
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
            }
        };
        function selExeUsers(btnElement,nodeId,scope){
            var spanObj=$(btnElement).prev();
            var aryCheckBox=$(":checked",spanObj);
            var selectUsers = [];
            if(aryCheckBox.length>0){
                for(var i=0;i<aryCheckBox.length;i++){
                    var obj = $(aryCheckBox[i]).val().split("^");
                    var selectUser={
                        type:obj[0],
                        id:obj[1],
                        name:obj[2]
                    }
                    selectUsers.push(selectUser)
                }
            }
            var tableHieght = $('#jumpDiv .table-detail').height();
            $('#jumpDiv').height(tableHieght);
            $('#jumpDiv .table-detail-div').height(tableHieght);
            if(!scope){
                scope={};
                scope.type='system',
                    scope.value='all';
                scope=JSON2.stringify(scope);
            }else{
                scope=scope.replaceAll("#@","\"");
            }


            FlowUserDialog({selectUsers:selectUsers,scope:scope,callback:function(aryTypes,aryIds,aryNames){
                if(aryIds==null) return;
                var aryTmp=[];
                var aryUserName=[];
                for(var i=0;i<aryIds.length;i++){
                    var val=aryTypes[i] +"^" + aryIds[i] +"^" +aryNames[i];
                    var tmp="<input type='checkbox'  include='1' name='"+nodeId+"_userId' checked='checked' value='"+val+"'/>"+aryNames[i];
                    aryTmp.push(tmp);
                }
                spanObj.html(aryTmp.join(''));
            }});
        }

        //是否点击了开始按钮。
        var isStartFlow=true;

        function saveForm(obj){
            isStartFlow=false;
            var  action="";
            if($(obj).hasClass('isDraft')){
                action="${ctx}/platform/bpm/task/saveForm.ht";
            }else{
                action="${ctx}/platform/bpm/task/saveData.ht";
            }
            submitForm(action,"a.save");
        }

        function startWorkFlow(){
            isStartFlow=true;
            var  action="${ctx}/platform/bpm/task/startFlow.ht";
            if(isNeedSubmitConfirm){
                $.ligerDialog.confirm("确认启动流程吗?","提示",function(rtn){
                    if(rtn){
                        submitForm(action,"a.run");
                    }
                });
//					});
            }
            else{
                submitForm(action,"a.run");
            }
        }

        //表单数据提交。
        //action:表单提交到的URL
        //button：点击按钮的样式。
        function submitForm(action,button){
            //百度编辑器数据处理

            var ignoreRequired=false;
            if(button=="a.save"){
                ignoreRequired=true;
            }
            var operatorType=(isStartFlow)?1:6;
            //前置事件处理
            var rtn=beforeClick(operatorType);
            if( rtn==false){
                return;
            }
            if($(button).hasClass("disabled"))return;
            if(isFormEmpty){
                $.ligerDialog.warn('流程表单为空，请先设置流程表单!',"提示信息");
                return;
            }
            var frmWorkFlow=$('#frmWorkFlow');
            frmWorkFlow.attr("action",action);

            if(isExtForm){
                //提交第三方表单时检查该表单的参数
                var rtn = true;
                if(button!="a.save"){
                    rtn=form.valid()
                }
                if(rtn){

                    if(frmWorkFlow.handleFieldName){//url表单清除命名
                        frmWorkFlow.handleFieldName();
                    }
                    if(frmWorkFlow.setData){
                        frmWorkFlow.setData();
                    }
                    $(button).addClass("disabled");
                    frmWorkFlow.submit();
                }
            }else{

                var rtn=CustomForm.validate({ignoreRequired:ignoreRequired,returnErrorMsg:true});
                if(!rtn.success){
                    //console.log(JSON.stringify(rtn))
                    $('.validError.validYz').removeClass("validYz")
                    $.ligerDialog.warn("请完善必填项 "+rtn.errorMsg,"提示信息");
                    return;
                }
                // 验证子表是否为必填
                rtn = CustomForm.isSubTableRequest();
                if(!rtn.success){
                    $.ligerDialog.warn("表单验证不成功：<br><b>子表：<span style='color: red;'>"+rtn.errorMsg+"</span>至少需要有一行数据</b>", "提示信息");
                    return;
                }
                //获取自定义表单的数据
                var data=CustomForm.getData();

                //WebSign控件提交。 有控件时才提交   xcx
                if(WebSignPlugin.hasWebSignField){
                    WebSignPlugin.submit();
                }

                $(button).addClass("disabled");

                var uaName=navigator.userAgent.toLowerCase();
                if(uaName.indexOf("firefox")>=0||uaName.indexOf("chrome")>=0){  // 火狐和谷歌 的文档提交
                    //Office控件提交。 有可以提交的文档
                    if(OfficePlugin.submitNum>0){
                        OfficePlugin.submit();
                        //火狐和谷歌 的文档提交包括了  业务提交代码部分（完成  OfficePlugin.submit()后面的回调 函数 有 业务提交代码），所以 后面就不用加上业务提交代码
                    }else{   //没有可提交的文档时 直接做 业务提交代码
                        data=CustomForm.getData();
                        //设置表单数据
                        $("#formData").val(data);
                        FormSubmitUtil.submitFormAjax(frmWorkFlow,showResponse);
                    }
                }else{        //IE内核的等
                    //Office控件提交。 有可以提交的文档
                    if(OfficePlugin.submitNum>0){
                        OfficePlugin.submit();
                        //当提交问题 等于 提交数量的变量 时 表示所有文档 都提交了  然后做 业务相关的提交
                        if(OfficePlugin.submitNum == OfficePlugin.submitNewNum){
                            //获取自定义表单的数据
                            data=CustomForm.getData();
                            //设置表单数据
                            $("#formData").val(data);
                            FormSubmitUtil.submitFormAjax(frmWorkFlow,showResponse);
                            OfficePlugin.submitNewNum = 0; //重置  提交数量的变量
                        }else{
                            $.ligerDialog.warn($lang_bpm.ntkOffice.resetOfficeKj,$lang.tip.warn);
                        }
                    }else{
                        //获取自定义表单的数据
                        data=CustomForm.getData();
                        //设置表单数据
                        $("#formData").val(data);
                        FormSubmitUtil.submitFormAjax(frmWorkFlow,showResponse);
                    }
                }
            }
        }

        function showBpmImageDlg(){
            BpmImageDialog({actDefId:"${bpmDefinition.actDefId}"});
        }

        function initSubForm(){
            $('#frmWorkFlow').ajaxForm({success:showResponse });
        }

        function showResponse(responseText){
            var button=(isStartFlow)? "a.run":"a.save";
            var operatorType=(isStartFlow)?1:6;

            var obj=new com.hotent.form.ResultMessage(responseText);
            if(obj.isSuccess()){
                var msg=(isStartFlow)?obj.data.message:"保存表单数据成功!";
                $.ligerDialog.success(msg,'提示信息',function(){
                    //添加后置事件处理
                    var rtn=afterClick(operatorType);
                    if( rtn==false){
                        return;
                    }

                    /* dialog.get('sucCall')();
                    dialog.close(); */
                    window.location.reload();
                    var iPad = navigator.userAgent.indexOf('iPad') > -1; //是否iPad
                    var iVersion= window.sessionStorage.getItem("version");//获取缓存浏览器version
                    if(window.opener){
                        window.opener.location.href = window.opener.location.href;
                        if(!iPad){
                            //window.close();
                            if(isStartFlow){

                                postMUtil.postM(postMUtil.opts.close);
                            }
                        }
                        else{
                            iPadBackFrame.back();}

                    }else{
                        if(iPad||iVersion == 'iPad'){
                            iPadBackFrame.back();
                        }
                        else{
                            //window.close()


                                postMUtil.postM(postMUtil.opts.close);

                            ;}
                    }
                });

            }
            else{
                var msg=(isStartFlow)?"启动流程失败!":"保存表单数据失败!";
                $.ligerDialog.error(obj.getMessage(), msg);
                $(button).removeClass("disabled");
            }
        }

        function chooseJumpType(){
            var obj=$('#jumpDiv');

            var url="${ctx}/platform/bpm/task/tranTaskUserMap.ht?selectPath=1&isStart=1&actDefId="+actDefId;
            url=url.getNewUrl();
            obj.html(obj.attr("tipInfo")).show().load(url);
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
        //

	</script>

</head>
<body class=taskStartB>

<form id="frmWorkFlow" method="post" >
	<input type="hidden" include="1" name="curUserId" value="${curUserId}"/>
	<input type="hidden" include="1" name="curUserName" value="${curUserName}"/>
	<input type="hidden" include="1" name="actDefId" value="${bpmDefinition.actDefId}"/>
	<input type="hidden" include="1" name="defId" value="${bpmDefinition.defId}"/>
	<input type="hidden" include="1" id="businessKey" name="businessKey" value="${businessKey}"/>
	<c:if test="${empty  runId}">
		<input type="hidden" include="1" name="runId" value="${runId}" />
	</c:if>
	<input type="hidden" include="1" id="startNode" name="startNode" />
	<c:if test="${not empty  paraMap}">
		<c:forEach items="${paraMap}" var="item">
			<input  include="1" type="hidden" name="${item.key}" value="${item.value}" />
		</c:forEach>
	</c:if>
	<div class="panel">


		<div class="panel-body printForm" style="overflow: auto;padding-top:10px;">
			<c:choose>
				<c:when test="${isMultipleFirstNode}">
					<div id="flowNodeList">
						<table class="table-grid">
							<thead>
							<tr>
								<th height="28" width="20%">选择起始路径</th>
								<td>
									<c:forEach items="${flowNodeList}" var="flowNode">
										<lable>${flowNode.nodeName}<input type="radio" name="flowNode" value="${flowNode.nodeId}" /></lable>
									</c:forEach>
								</td>
							</tr>
							</thead>
						</table>
					</div>
				</c:when>

			</c:choose>

			<c:if test="${bpmDefinition.showFirstAssignee==1}">
				<div id="jumpDiv" class="noprint" style="display:none;" tipInfo="正在加载表单请稍候..."></div>
			</c:if>
			<!-- 通知消息类型 -->
			<c:if test="${not empty handlersMap}">
				<div id="informTypeShow" style="padding-left:20px;">
					提醒消息方式:
				 	<c:forEach items="${handlersMap}" var="item">
	                   <input type="checkbox" include="1" name="informType" value="${item.key }"/>${item.value.title }
	                </c:forEach>
	            </div>
			</c:if>
			<c:if test="${not empty param.relRunId}">
				<div class="noprint">
					<jsp:include page="incTaskOpinion.jsp"></jsp:include>
				</div>
			</c:if>

			<c:choose>
				<c:when test="${isFormEmpty==true}">
					<div class="noForm">没有设置流程表单。</div>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${isExtForm}">
							<div id="divExternalForm" formUrl="${form}"></div>
						</c:when>
						<c:otherwise>
							<div class="panel-detail" type="custform" id="custformDiv">${form}</div>
							<input type="hidden" include="1" name="formKey" value="${formKey}"/>
							<input type="hidden" include="1" name="formData" id="formData" />
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</div>
		<%-- <div style="padding:6px 8px 3px 12px;" class="noprint">
            <b>流程简述：</b>${bpmDefinition.descp}
        </div> --%>
		<%@include file="incToolBarStart.jsp" %>
	</div>

</form>

</body>

</html>