<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>节点设置管理</title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerWindow.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/FormDialog.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/CommonDialog.js"></script>

<script type="text/javascript">
	//流程定义ID
	var actDefId="${bpmDefinition.actDefId}";
	var defId="${bpmDefinition.defId}";
	var parentActDefId = "${parentActDefId}";
	
	function save(){
		var type=$("#bpmFormType").val();
		var isEmpty=isEmptyForm();
		if(!isEmpty){
			if(type==-1){
				$.ligerDialog.error('请设置流程实例业务表单!','提示信息');
				return;
			}else if(type==0){
				var bpmFormKey=$("#bpmFormKey").val();
				if(!bpmFormKey||bpmFormKey==0){
					$.ligerDialog.error('请设置流程实例业务表单!','提示信息');
					return;
				}
			}else if(type==1){
				var bpmFormUrl=$("#bpmFormUrl").val();
				if(!bpmFormUrl||bpmFormUrl==''){
					$.ligerDialog.error('请设置流程实例业务表单!','提示信息');
					return;
				}
			}
		}
		$('#dataForm')[0].submit();
	}
	
	function isEmptyForm(){
		var isEmpty=true;
		var globalFormObj=$("div[name='globalFormUrl']");
		var globalUrlObj=globalFormObj.find('input[name="formUrlGlobal"]');
		var globalDetailObj=globalFormObj.find('input[name="detailUrlGlobal"]');
		var globalUrl=(globalUrlObj!=undefined)?globalUrlObj.val():"";
		var globalDetailUrl=(globalDetailObj!=undefined)?globalDetailObj.val():"";
		var globalFormKey=($("#defaultFormKey")!=undefined)?$("#defaultFormKey").val():0;
		if(globalUrl!=""||globalFormKey!=0||globalDetailUrl!=""){
			isEmpty=false;
		}
		$("div[name='nodeForm']").each(function(){
			var formKeyObj=$(this).find('input[name="formKey"]');
			var formKey=(formKeyObj!=undefined)?formKeyObj.val():0;
			var formUrlObj=$(this).find('input[name="formUrl"]');
			var formUrl=(formUrlObj!=undefined)?formUrlObj.val():"";
			var formDetailObj=$(this).find('input[name="detailUrl"]');
			var formDetail=(formDetailObj!=undefined)?formDetailObj.val():"";
			if(formKey!=0||formUrl!=""||formDetail!=""){
				isEmpty=false;
			}
		});
		return isEmpty;
	}

	//选择全局表单
	function selectGlobalForm(me) {
		//全局表单
		var globalFormObj=$(me).closest("div[name='globalForm']");
		FormDialog({
			callback : function(ids, names, tableId) {
				//业务表单
				var bpmFormObj=$("div[name='bpmForm']");
				
				$("select[name='bpmFormType']").val("0");
				bpmFormObj.show();
				$("div.url_bpmForm").hide();
				
				$("input.formKey",globalFormObj).val(ids);
				$("input.formKey",bpmFormObj).val(ids);
				$("input.formDefName",globalFormObj).val(names);
				$("input.formDefName",bpmFormObj).val(names);
		
				//给表单添加 超链接，使之能看到表单明细
				var namesUrl="<a target='_blank' href="+__ctx+"/platform/form/bpmFormDef/parse.ht?formKey="+ids+" >"+names+"</a>";
				$("span[name='spanForm']",globalFormObj).html(namesUrl);
				$("span[name='spanForm']",bpmFormObj).html(namesUrl);
				
				// 是否显示子表排序功能
				hasSubTable(ids,function(res){
					var result= eval('(' + res + ')');
					if(result.success && me.id == 'formTableSel'){
						$(me).siblings("a.grantSort").show();
					}else{
						$(me).siblings("a.grantSort").hide();
					}
				});
			}
		})
	}
	
	
	
	
	function setMobileForm(obj,isGlobal){
		CommonDialog("mobileDialog",function(data){
			var formKey=data.FORM_KEY;
			var subject=data.SUBJECT;
			var parentObj=$(obj).closest("div");
			setMobileBat(formKey,subject,parentObj);
			if(isGlobal && $("#setNodeForm").attr("checked")){
				$("[name='mobileForm']").each(function(){
					var obj=$(this);
					setMobileBat(formKey,subject,obj);
				});
				$("[name='formType']").val(0);
				$("[name='nodeForm']").show();
				$("[name='bpmNodeForm']").show();
			}
			if(isGlobal){
				var bpmFormObj=$('[name="mobileBpmForm"]');
				setMobileBat(formKey,subject,bpmFormObj);
			}
			
			handShowSort(formKey);
			
		},"");
	}
	
	
	
	
	function setMobileBat(formKey,subject,parentObj){
		$("input.formKey",parentObj).val(formKey);
		$("input.formDefName",parentObj).val(subject);
		var namesUrl="<a href='#' onclick=previewMobile('"+formKey+"') >"+subject+"</a>";
		$("span[name='spanForm']",parentObj).html(namesUrl);
	}
	
	function handShowSort(formKey){
		var parentObj=$("#formBox_globalFormType");
		
		hasSubTable(formKey,function(res){
			var result= eval('(' + res + ')');
			if(result.success ){
				$("a.grantSort",parentObj).show();
			}else{
				$("a.grantSort",parentObj).hide();
			}
		}); 
	}
	
	
	function hasSubTable(formKey,callBack){
		var url= __ctx + "/platform/form/bpmFormDef/isSubTable.ht";
		var params={formKey:formKey};
		$.post(url,params,function(res){
			callBack(res);
		});
	}
	
	//选择业务表单
	function selectBpmForm(obj) {
		FormDialog({
			callback : function(ids, names, tableId) {
				var tdObj=$(obj).parent();
				$("input.formKey",tdObj).val(ids);
				$("input.formDefName",tdObj).val(names);
				//给表单添加 超链接，使之能看到表单明细
				var namesUrl="<a target='_blank' href="+__ctx+"/platform/form/bpmFormDef/parse.ht?formKey="+ids+" >"+names+"</a>";
				$("span[name='spanForm']",tdObj).html(namesUrl);
			}
		})
	}
	
	//选择节点表单
	function selectNodeForm(obj) {
		FormDialog({
			callback : function(ids, names, tableId) {
				var tdObj=$(obj).parent();
				$("input.formKey",tdObj).val(ids);
				$("input.formDefName",tdObj).val(names);
				//给表单添加 超链接，使之能看到表单明细
				var namesUrl="<a target='_blank' href="+__ctx+"/platform/form/bpmFormDef/parse.ht?formKey="+ids+" >"+names+"</a>";
				$("span[name='spanForm']",tdObj).html(namesUrl);
				// 是否显示子表授权功能
				hasSubTable(ids,function(res){
					var result= eval('(' + res + ')');
					if(result.success && obj.id == 'subNodeSel'){
						$(obj).siblings("a.grant").show();
					}else{
						$(obj).siblings("a.grant").hide();
					}
				});
			}
		})
	}


	//全局表单授权
	function authorizeDialog(aurl){
		var url=aurl;
		var winArgs="dialogWidth=850px;dialogHeight=600px;help=0;status=no;center=yes;resizable=no;";
		url=url.getNewUrl();
		DialogUtil.open({
			height:$("body").height()||600,
			width: 850,
			title : '全局表单授权',
			url: url, 
			isResize: true,
            isPostHidden: true
		});
	}
	
	//清除表单
	function clearForm(obj,clearMobile){
		var btn=$(obj);
		var tdObj=btn.parent();
		$("input.formKey",tdObj).val('');
		$("input.formDefName",tdObj).val('');
		$("span[name='spanForm']",tdObj).text('');
		$(obj).siblings("a.grant").hide();
		$(obj).siblings("a.grantSort").hide();
		
		if(clearMobile){
			var parentObj=$("[name='mobileForm']");
			$(".formKey",parentObj).val("");
			$(".formDefName",parentObj).val("");
			
			$("span[name='spanForm']",parentObj).html("");
		}
	}
	/**
	* 表单授权
	* @param obj 事件源
	* @param nodeId 节点ID
	*/
	function authorize(obj,nodeId){
	
		var btn=$(obj);
		var tdObj=btn.parent();
		var objDefId=$("input.formKey",tdObj);
		if(objDefId.val()==""||objDefId.val()==0){
			$.ligerDialog.warn('请先选择表单!','提示信息');
			return;
		}
		var formKey=objDefId.val();
	
		var url= __ctx + '/platform/form/bpmFormRights/dialog.ht?actDefId=' +actDefId+'&nodeId=' + nodeId +'&formKey=' + formKey+"&defId="+defId;
		if(parentActDefId){
			url += "&parentActDefId=" + parentActDefId;
		}
	   if(nodeId.length>0){
			var oldformKey=$("input.oldFormKey",tdObj).val();
			if(oldformKey!=formKey)
				url+='&isAlert=true'
		}
		authorizeDialog(url,nodeId,formKey);
	}
	
	// 弹出子表授权脚本填写页面
	function subDataGrant(obj,nodeId){
		var btn=$(obj);
		var tdObj=btn.parent();
		var objDefId=$("input.formKey",tdObj);
		var formKey=objDefId.val();
		var url= __ctx + '/platform/form/bpmFormDef/subRightsDialog.ht?actDefId=' 
			+actDefId+'&nodeId=' + nodeId +'&formKey=' + formKey;
		if(parentActDefId){
			url += "&parentActDefId=" + parentActDefId;
		}
		authorizeDialog(url,nodeId,formKey);
	}

	
	
	$(function(){
		
		$('#ckJumpType').on('click',function(){
			$('.jumpType').attr('checked',this.checked);
		});
		
		$('#ckHidenOption').on('click',function(){
			var checked=this.checked;
			$('.hideOption').attr('checked',checked);
		});
		
		$('#ckHidenPath').on('click',function(){
			var checked=this.checked;
			$('.hidePath').attr('checked',checked);
		});
		
		$("input[name='existSubTable']").each(function(){
			var obj = $(this);
			if(obj.val()==0){
				obj.siblings("a.grant").hide();
			}
		});
			
		
		//处理表单类型
		handFormType();
		//验证handler
		validHandler();
		
		showHelper();
		
		var globalExistSubTable =$('#globalExistSubTable').val();
		if($.isEmpty(globalExistSubTable)||globalExistSubTable==0){
			$("a.grantSort").hide();
		}
		
		//是否默认选中  隐藏执行路径
		var isNew = '${isNew}';
		if(isNew=='yes'){             //没有绑定表单时都要默认选中  隐藏执行路径
			$('#ckHidenPath').attr('checked',true);
			$('.hidePath').attr('checked',true);
		}
		$("select[name='globalFormType']").trigger("change");
	});
	
	function handFormType(){
		//业务表单
		$("select[name='bpmFormType']").change(function(){
			var value=$(this).val();
			var nodeId=$(this).attr('nodeId');
			
			if(value==-1){
				$(".bpmForm").hide();
			}
			else{
				if(value==0){
					$(".form_"+nodeId).show();
					$(".url_"+nodeId).hide();
				}
				else if(value==1){
					$(".form_"+nodeId).hide();
					$(".url_"+nodeId).show();
				}
				$(".bpmForm").show();
			}
		});
		//节点表单
		$("select[name='formType']").change(function(){
			var value=$(this).val();
			var nodeId=$(this).attr("nodeId");
			
			if(value==-1){
				$("#formBox_"+nodeId).hide();
			}
			else{
				$("#formBox_"+nodeId).show();
				if(value==0){
					$(".form_"+nodeId).show();
					$(".url_"+nodeId).hide();
				}
				else if(value==1){
					$(".form_"+nodeId).hide();
					$(".url_"+nodeId).show();
				}
			}
		});
		
		$("select[name='globalFormType']").change(function(){
			var obj=$("select[name='bpmFormType']");
			var value=$(this).val();
			var nodeId=$(this).attr("nodeId");
			var objNodeId=obj.attr('nodeId');
			
			obj.val(value);
		
			if(value==-1){
				$("#formBox_"+nodeId).hide();
			}
			else{
				$("#formBox_"+nodeId).show();
				if(value==0){
					$(".form_"+nodeId).show();
					$(".form_"+objNodeId).show();
					$(".url_"+nodeId).hide();
					$(".url_"+objNodeId).hide();
				}
				else if(value==1){
					$(".form_"+nodeId).hide();
					$(".form_"+objNodeId).hide();
					$(".url_"+nodeId).show();
					$(".url_"+objNodeId).show();
				}else{
					$(".form_"+nodeId).hide();
					$(".url_"+nodeId).hide();
					$(".form_"+objNodeId).hide();
					$(".url_"+objNodeId).hide();
				}
			}
		});
	}
	
	function validHandler(){
		$("input.handler").blur(function(){
			var obj=$(this);
			var val=obj.val();
			if(val.trim()==""){
				return;
			}
			var params={handler:val};
			$.post("validHandler.ht",params,function(data){
				var json=eval("(" +data +")");
				if(json.result!='0'){
					$.ligerDialog.warn(json.msg,"提示信息",function(){
						obj.focus();
					});
				}
			});
		});
	}
	
	function showHelper(){
		var cookie=$.getCookie("help");
		if(cookie=="hidden") {
			$("ul.help").hide();
			return ;
		}
		$("ul.help").show();
	}
	
	function toggleHelp(){
		var display=$("ul.help").css("display");
		if(display=="none"){
			$("ul.help").show();
			$.setCookie("help","show");
		}
		else{
			$("ul.help").hide();
			$.setCookie("help","hidden");
		}
	}
	
	 var actDefKey='${bpmDefinition.actDefKey}';
	 function subTableSort(obj){
		var me = $(obj);
		var divObj=$(me).closest("div[name='globalForm']");
		var formKey = $("input.formKey",divObj).val();
		var url= __ctx + '/platform/form/subTableSort/dialog.ht?actDefKey='+actDefKey+'&formKey=' + formKey;
		url=url.getNewUrl();
		
		DialogUtil.open({
			height:600,
			width: 850,
			title : '子表字段排序配置',
			url: url, 
			isResize: true,
		});
	 }
		
	 // 显示字段
	function showField(nodeId){
		var formKey=$("#defaultFormKey").val();
		if(formKey==""||formKey==0){
			$.ligerDialog.warn('请先选择全局表单!','提示信息');
			return;
		}
		var url= __ctx + '/platform/form/bpmFormDef/fieldDialog.ht?formKey=' +formKey;
		DialogUtil.open({
			height:540,
			width: 750,
			title : '字段选择',
			url: url, 
			isResize: true,
			//自定义参数
			sucCall:function(rtn){
				var name = "opinionField_"+nodeId;
				$("input[name='"+name+"']").val(rtn);
			}
		});
	}	
	 
	function clearField(nodeId){
		var name = "opinionField_"+nodeId;
		$("input[name='"+name+"']").val("");
	}
	function optionChange(obj){
		var checked=obj.checked;
		if(checked==true){
			$(obj.closest("td")).find(".isRequired").removeAttr("checked").attr("disabled","true");
			$(obj.closest("td")).find(".isPopup").removeAttr("checked").attr("disabled","true");
		}else{
			$(obj.closest("td")).find(".isRequired").removeAttr("disabled");
			$(obj.closest("td")).find(".isPopup").removeAttr("disabled");
		}
	}
	
	
	function previewMobile(formKey){
		var url='${ctx}/platform/form/bpmMobileFormDef/preview.ht?formKey='+formKey;
		win= DialogUtil.open({title:"预览表单", url: url, height:680,width:500,isResize:true,pwin:window});	
	}
   function copy(obj,type){

       var content=$($("#node").html());
       content.find('td.'+obj).parent().remove();
	   var dialog = {
		   title:"选择要复制到的节点",
		   content: content,
		   buttons: [{ text: $.ligerDefaults.DialogString.ok, onclick: function callback(item, Dialog, index){
		       Dialog.close();
		       var ids=[];
		       $("input:checked",content).each(function (pos,nodeObj) {
				   ids.push($(nodeObj).val());
               });
				var selectNode = document.getElementById("formBox_"+obj);
				var formKey = $("input[name=formKey]",selectNode)[0].value;
				var formDefName=$("input[name=formDefName]",selectNode)[0].value;
				var mobileFormKey=$("input[name=mobileFormKey]",selectNode)[0].value;
               var mobileFormName=$("input[name=mobileFormName]",selectNode)[0].value;
				var formType = document.getElementById("formType_"+obj).value;
               for(var i = 0;i<ids.length;i++){
                   var copyNode = document.getElementById("formBox_"+ids[i]);
                   console.log(copyNode);
                   document.getElementById("formType_"+ids[i]).value=formType;
                   if(type==0){
                       $("input[name=formKey]",copyNode)[0].value=formKey;
                       $("input[name=formDefName]",copyNode)[0].value=formDefName;
                       //给表单添加 超链接，使之能看到表单明细
                       var namesUrl="<a target='_blank' href="+__ctx+"/platform/form/bpmFormDef/parse.ht?formKey="+formKey+" >"+formDefName+"</a>";
                       var spanF=$("span[name='spanForm']",copyNode);
                       spanF.eq(type).html(namesUrl);
                   }else{
                       $("input[name=mobileFormKey]",copyNode)[0].value=mobileFormKey;
                       $("input[name=mobileFormName]",copyNode)[0].value=mobileFormName;
                       var namesUrl="<a target='_blank' href="+__ctx+"/platform/form/bpmFormDef/parse.ht?formKey="+mobileFormKey+" >"+mobileFormName+"</a>";
                       var spanF=$("span[name='spanForm']",copyNode);
                       spanF.eq(type).html(namesUrl);
				   }
                   if(formType==-1){
                       $("#formBox_"+ids[i]).hide();
                   }
                   else{
                       $("#formBox_"+ids[i]).show();
                       if(formType==0){
                           $(".form_"+ids[i]).show();
                           $(".url_"+ids[i]).hide();
                       }
                       else if(formType==1){
                           $(".form_"+ids[i]).hide();
                           $(".url_"+ids[i]).show();
                       }
                   }




               }

               // 是否显示子表授权功能
               hasSubTable(ids,function(res){
                   var result= eval('(' + res + ')');
                   if(result.success && obj.id == 'subNodeSel'){
                       $(obj).siblings("a.grant").show();
                   }else{
                       $(obj).siblings("a.grant").hide();
                   }})
		   }},{ text: $.ligerDefaults.DialogString.cancel, onclick: function cancel(item, Dialog, index){
               Dialog.close();
		   }}],
		   showMax: false,
		   showToggle: false,
		   showMin: false,
		   allowClose:true,
		   width:900,
		   height:600,

	   };
       return $.ligerDialog(dialog);
       }

</script>
<style type="text/css">
	ul.help li { list-style:inside circle;list-style-type:decimal ;padding-left: 10px;font-weight: normal; } 
</style>

</head>
<body>
     <jsp:include page="incDefinitionHead.jsp">
   		<jsp:param value="节点表单设置" name="title"/>
	</jsp:include>
	<div class="panel-container">
    <f:tab curTab="form" tabName="flow"/>

	<div class="panel">

		<div class="panel-top" usesclflw="false"  style="margin-left:5px;margin-right:5px">
				 <h2 class="setting" style="margin-top:12px">流程表单设置</h2>
				  <div class="panel-toolbar inline" style="border-left: 1px solid #e9e9e9; border-radius: 5px; margin-left: 10px;">
				     <a class="link save" onclick="save()"> <span></span>保存</a>
				 </div>	
				 <div class="foldBox" hasScroll="true">
					<a class="drop" value="1">
						<span class="dropicon"></span>
						帮助
					</a>
					<div class="content"  style=" background: none repeat scroll 0 0 #EFEFEF;">

								<ul class="help" style="display:none;">
									<li>表单
										<ul>
											<li>在线表单,为系统自定义表单。</li>
											<li>url表单,是外部表单。地址写法规则为：如果表单页面平台在同一个应用中，路径从根开始写，<span class="red">不需要上下文路径</span>，例如 ：/form/addUser.ht。
												如果需要使用的表单不再同一个应用下，则需要写完整路径如:http://***/crm/addUser.ht 。
											</li>
										</ul>
									</li>
									<li>处理器
										<ul>
											<li>处理器是需要开发人员定制的,一般情况下<span class="red">URL表单的处理器需要填写</span>。处理器的写法是 service类名首字母小写 +"." + 方法名称。
										需要注意的是这个service是需要通过spring容器管理的。另外方法的参数必须是ProcessCmd类。
										例如：userService.add,这个表示是UserService类中有一个，add(ProcessCmd cmd)的方法。</li>
											<li>
											前置和后置处理器区别在于执行时机不同，前置处理器是在启动流程或完成下一步这个动作之前执行的。
											后置处理器在启动流程或完成下一步之后执行的。
											</li>
										</ul>
										
									</li>
								</ul>

					</div>
				</div>
		</div>

		<div class="panel-body">			
				<form action="save.ht" method="post" id="dataForm">				
				    <input type="hidden" name="defId" value="${bpmDefinition.defId}"/>
				    <input type="hidden" name="parentActDefId" value="${parentActDefId}"/>
				     <div class="foldBox formBox" style="min-height: 20px;">
			               <div class="title" >
			               
								全局表单
								<select name="globalFormType" class="selectForm" nodeId="globalFormType" >
									<c:choose >
										<c:when test="${globalForm==null }">
											<option value="-1" selected="selected" >请选择..</option>
											<option value="0" >在线表单</option>
											<option value="1" >URL表单</option>
										</c:when>
										<c:otherwise>
											<option value="-1" <c:if test="${globalForm.formType==-1 }">selected="selected"</c:if>>请选择..</option>
											<option value="0"  <c:if test="${globalForm.formType==0 }">selected="selected"</c:if>>在线表单</option>
											<option value="1"  <c:if test="${globalForm.formType==1 }">selected="selected"</c:if>>URL表单</option>
										</c:otherwise>
									</c:choose>
								</select>
							</div>
			     			<div  class="content" id="formBox_globalFormType">
	     						<table  class="table-noborder form_globalFormType" <c:if test="${globalForm!=null && globalForm.formType!=0 }">style="display: none" </c:if>>
									<tr>
										<th nowrap="nowrap">表单:</th>
										<td>
											<div name="globalForm" >
												<input id="defaultFormKey" class="formKey"  type="hidden" name="defaultFormKey" value="${globalForm.formKey }" >
												<input id="defaultFormName" class="formDefName"  type="hidden" name="defaultFormName" value="${globalForm.formDefName }">
												<span name="spanForm"><a target="_blank" href="${ctx}/platform/form/bpmFormDef/parse.ht?formKey=${globalForm.formKey}" >${globalForm.formDefName }</a></span>
												<a href="javascript:;" class="link get" onclick="selectGlobalForm(this)" id="formTableSel">选择</a>
												<a href="javascript:;" class="link clean" onclick="clearForm(this)">重选</a>
												<a href="javascript:;" class="link auth" onclick="authorize(this,'')">表单授权</a>
												<a href="javascript:;" class="tipinfo"><span>设置全局表单授权，多个流程定义绑定该表单时，则使用该表单的授权信息一致；倘若其他流程定义绑定该表单且需使用不同表单权限控制则不需设置全局表单授权。</span></a>
												<input type="hidden" id="globalExistSubTable" value="${globalForm.existSubTable }">
												<a href="javascript:;" class="link grantSort" onclick="subTableSort(this)">子表数据排序</a>
											</div>
										</td>
									</tr>
									<tr >
										<th nowrap="nowrap">手机表单:</th>
										<td>
											<div >
												<input type="checkbox" id="setNodeForm" >设置节点表单
												<input class="formKey"  type="hidden" name="defaultMobileFormKey" value="${globalForm.mobileFormKey }" >
												<input class="formDefName"  type="hidden" name="defaultMobileFormName" value="${globalForm.mobileFormName }">
												<span name="spanForm"><a target="_blank" href="#" onclick="previewMobile('${globalForm.mobileFormKey}')" >${globalForm.mobileFormName }</a></span>
												<a href="javascript:;" mobile="mobile" class="link get" onclick="setMobileForm(this,true)">选择</a>
												<a href="javascript:;" mobile="mobile" class="link clean" onclick="clearForm(this,true)">重选</a>
											</div>
										</td>
									</tr>
								</table>
									
								<div class="url_globalFormType" <c:if test="${globalForm==null || globalForm.formType!=1 }">style="display: none" </c:if>  >
									表单URL:<input type="text" name="formUrlGlobal" value="${globalForm.formUrl }" class="inputText" style="width: 250px;"/>
									<br/>
									明细URL:<input type="text" name="detailUrlGlobal" value="${globalForm.detailUrl }" class="inputText" style="width: 250px;"/>
								</div>
								
								
				     	</div>
				     </div>
					<div class="foldBox formBox" style="min-height: 20px;">
							 <div class="title" >流程实例业务表单:
							 	 <select name="bpmFormType"   nodeId="bpmForm">
									<c:choose >
										<c:when test="${bpmForm==null }">
											<option value="-1" selected="selected" >请选择..</option>
											<option value="0" >在线表单</option>
											<option value="1" >URL表单</option>
										</c:when>
										<c:otherwise>
											<option value="-1" <c:if test="${bpmForm.formType==-1 }">selected="selected"</c:if>>请选择..</option>
											<option value="0"  <c:if test="${bpmForm.formType==0 }">selected="selected"</c:if> >在线表单</option>
											<option value="1"  <c:if test="${bpmForm.formType==1 }">selected="selected"</c:if>>URL表单</option>
										</c:otherwise>
									</c:choose>
								</select>
							 </div>
							 <div class="content bpmForm" >
							 	<div class="form_bpmForm"  <c:if test="${empty bpmForm || bpmForm.formType!=0}">style="display: none"</c:if>>
								 	<div  >
								 		表单：
										<input id="bpmFormKey" class="formKey"  type="hidden" name="bpmFormKey" value="${bpmForm.formKey}">
										<input id="bpmFormName" class="formDefName"  type="hidden" name="bpmFormName" value="${bpmForm.formDefName}">
										<span name="spanForm"><a target="_blank" href="${ctx}/platform/form/bpmFormDef/parse.ht?formKey=${bpmForm.formKey}" >${bpmForm.formDefName}</a></span>
										<a href="javascript:;" class="link get" onclick="selectBpmForm(this)">选择</a>
										<a href="javascript:;" class="link clean" onclick="clearForm(this)">重选</a>
									</div>
									<div name="mobileBpmForm">
										手机表单:
										<input  class="formKey"  type="hidden" name="mobileBpmFormKey" value="${bpmForm.mobileFormKey }" >
										<input  class="formDefName"  type="hidden" name="mobileBpmFormName" value="${bpmForm.mobileFormName }">
										<span name="spanForm"><a target="_blank" href="#" onclick="previewMobile('${bpmForm.mobileFormKey}')" > ${bpmForm.mobileFormName }</a></span>
										<a href="javascript:;" class="link get" onclick="setMobileForm(this)">选择</a>
										<a href="javascript:;" class="link clean" onclick="clearForm(this)">重选</a>
									</div>
							 	</div>
							 	
								<div  class="url_bpmForm" <c:if test="${empty bpmForm || bpmForm.formType!=1}">style="display: none"</c:if>>
									表单明细URL:<input type="text" id="bpmFormUrl" name="bpmFormUrl" value="${bpmForm.formUrl}" class="inputText" size="40"/>
								</div>
							 </div>
					</div>
					<div class="foldBox">
					      <div class="title" >节点表单</div>
					       <div class="content">
					           <table cellpadding="1" cellspacing="1" class="table-grid table-list">
									<thead>
									<tr>
										<th style="width:10%">节点名</th>
										<th style="width:15%">
											<label><input type="checkbox" id="ckJumpType">跳转类型</label>
										</th>
										<th style="width:15%">
											<label title="表单意见处理">意见处理</label>
										</th>
										<th style="width:10%">
											<label title="隐藏执行路径"><input type="checkbox" id="ckHidenPath">隐藏路径</label>
										</th>
										<th>处理器</th>
										<th>表单</th>
									</tr>
									</thead>
									<c:forEach items="${bpmNodeSetList}" var="item" varStatus="status">
									<tr  <c:if test="${status.index%2=='0' }">class="odd"</c:if>>
										<td style="width:10%">
											<input type="hidden" name="nodeId" value="${item.nodeId}"/>
											<input type="hidden" name="nodename" value="${item.nodeName}"/>${item.nodeName}
										</td>
										<td nowrap="nowrap" style="width:15%">
											<ul>
												<li><input type="checkbox" class="jumpType" name="jumpType_${item.nodeId}" value="1"  <c:if test="${fn:indexOf(item.jumpType,'1')!=-1}">checked="checked"</c:if> />正常跳转</li>
												<li><input type="checkbox" class="jumpType" name="jumpType_${item.nodeId}" value="2" <c:if test="${fn:indexOf(item.jumpType,'2')!=-1}">checked="checked"</c:if> />选择路径跳转</li>
												<li><input type="checkbox" class="jumpType" name="jumpType_${item.nodeId}" value="3" <c:if test="${fn:indexOf(item.jumpType,'3')!=-1}">checked="checked"</c:if> />自由跳转</li>
											</ul>
										</td>
										<td style="width:15%">
											<ul>
												<li><input type="checkbox" id ="isRequired"  class="isRequired" name="isRequired_${item.nodeId}" value="1" <c:if test="${item.isRequired==1}">checked="checked"</c:if> <c:if test="${item.isHideOption==1}">disabled="disabled"</c:if> />是否必填</li>
												<li><input type="checkbox" id = "isPopup" class="isPopup" name="isPopup_${item.nodeId}" value="1" <c:if test="${item.isPopup==1}">checked="checked"</c:if> <c:if test="${item.isHideOption==1}">disabled="disabled"</c:if> />是否弹窗</li>
												<li><input type="checkbox" id = "opinionHtml" class="opinionHtml" name="opinionHtml_${item.nodeId}" value="1" <c:if test="${item.opinionHtml==1}">checked="checked"</c:if> />是否HTML</li>
												<li><input type="checkbox" id="isHideOption" onchange="optionChange(this);" class="isHideOption" name="isHideOption_${item.nodeId}" value="1" <c:if test="${item.isHideOption==1}">checked="checked"</c:if> />是否隐藏意见表单</li>
												<li><input type="text" class="inputText" name="opinionField_${item.nodeId}" value="${item.opinionField }" style="width: 120px;" readonly="readonly"/>
													<br>
													<a href="javascript:showField('${item.nodeId}');">意见回填字段</a>
													<a href="javascript:clearField('${item.nodeId}');">清除</a>
												</li>
											</ul>
										</td>
										<td style="width:10%">
											<input type="checkbox" class="hidePath" name="isHidePath_${item.nodeId}" value="1" <c:if test="${item.isHidePath==1}">checked="checked"</c:if> />
										</td>

										<td>
											<table class="table-detail">
												<tr>
														<td nowrap="nowrap" class="head">前置:</td>
														<td>
															<input type="text" name="beforeHandler" value="${item.beforeHandler}" class="inputText handler " style="width: 120px;" size="20"/>
														</td>
													</tr>
													<tr>
														<td nowrap="nowrap" class="head">后置:</td>
														<td>
															<input type="text" name="afterHandler" value="${item.afterHandler}" class="inputText handler" style="width: 120px;" size="20"/>
														</td>
													</tr>
											</table>
										</td>
										<td class='formBox' >
											<table class="table-detail" >
												<tr>
													<td nowrap="nowrap" class="head">表单类型:</td>
													<td>
														<select name="formType" id="formType_${item.nodeId}" class="selectForm" nodeId="${item.nodeId}" style="width: 120px;">
															<option value="-1" <c:if test="${item.formType==-1}">selected</c:if>>请选择..</option>
															<option value="0" <c:if test="${item.formType==0}">selected</c:if>>在线表单</option>
															<option value="1" <c:if test="${item.formType==1}">selected</c:if>>URL表单</option>
														</select>
													</td>
												</tr>
											</table>
											<div id="formBox_${item.nodeId}" name="nodeForm" <c:if test="${item.formType==-1}">style="display:none"</c:if>>
												<table class="table-detail  form_${item.nodeId}" name="bpmNodeForm" <c:if test="${item!=null && item.formType!=0 }">style="display: none" </c:if> >
													<tr>
														<td nowrap="nowrap">表单:</td>
														<td>
															<div>
																<input type="hidden" class="formKey" name="formKey" value="${item.formKey}">
																<input type="hidden" class="formDefName" name="formDefName" value="${item.formDefName}">
																<span name="spanForm"><a target="_blank" href="${ctx}/platform/form/bpmFormDef/parse.ht?formKey=${item.formKey}" >${item.formDefName}</a></span>
																<a href="javascript:;" class="link get" onclick="selectNodeForm(this)" id="subNodeSel">选择</a>
																<a href="javascript:;" class="link clean" onclick="clearForm(this)">重选</a>
																<a href="javascript:;" class="link auth" onclick="authorize(this,'${item.nodeId}')">表单授权</a>
																<input type="hidden" name="existSubTable" value="${item.existSubTable }">
																<%--<a href="javascript:;" class="link grant" onclick="subDataGrant(this,'${item.nodeId}')">子表数据授权</a>--%>
																<a href="javascript:;" class="link copy" onclick="copy('${item.nodeId}',0)" >复制到</a>
															</div>
														</td>
													</tr>
													<tr>
														<td nowrap="nowrap">手机表单:</td>
														<td>
															<div name="mobileForm">
																<input type="hidden" class="formKey" name="mobileFormKey" value="${item.mobileFormKey}">
																<input type="hidden" class="formDefName" name="mobileFormName" value="${item.mobileFormName}">
																<span name="spanForm"><a target="_blank" href="${ctx}/platform/form/bpmFormDef/parse.ht?formKey=${item.mobileFormKey}" >${item.mobileFormName}</a></span>
																<a href="javascript:;" class="link get" onclick="setMobileForm(this)" >选择</a>
																<a href="javascript:;" class="link clean" onclick="clearForm(this)">重选</a>
																<a href="javascript:;" class="link copy" onclick="copy('${item.nodeId}',1)" >复制到</a>
															</div>
														</td>
													</tr>
												</table>
												<table class="table-detail url_${item.nodeId}" <c:if test="${item!=null && item.formType!=1 }">style="display: none" </c:if> >
													<tr >
													<td>表单URL</td>
														<td><input type="text" name="formUrl" value="${item.formUrl}" class="inputTexts" size="30"/></td>
													</tr>
													<tr >
													<td>明细URL</td>
														<td><input type="text" name="detailUrl" value="${item.detailUrl}" class="inputTexts" size="30"/></td>
													</tr>
												</table>
											</div>
										</td>
									</tr>
									</c:forEach>
								</table>
					       </div>
					</div>
				</form>
		</div>
		<div class="node-body" name="node-body" id="node" style= "display: none">
			<display:table  name="bpmNodeSetList" id="bpmNodeItem"  cellpadding="1" cellspacing="1" export="false"  class="table-grid">
				<display:column class="${bpmNodeItem.nodeId}"  title="${checkAll}" media="html" style="width:30px;">
					<input type="checkbox" class="pk" name="userData" value="${bpmNodeItem.nodeId}">
				</display:column>
				<display:column  class="${bpmNodeItem.nodeId}" style="width:30px;"  property="nodeName" title="节点名" >
				</display:column>
			</display:table>
		</div>
	</div>
	</div>
</body>
</html>


