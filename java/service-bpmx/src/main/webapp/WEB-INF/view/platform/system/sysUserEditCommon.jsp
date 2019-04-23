<%--
	time:2011-11-28 10:17:09
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户表明细</title>
<%@include file="/commons/include/form.jsp"%>
<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/formdata.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/subform.js"></script>
<script type="text/javascript" src="${ctx }/js/lg/plugins/ligerTab.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/HtmlUploadDialog.js" ></script>
<script type="text/javascript">
	var dialog =null;
	try{
		dialog = frameElement.dialog; //调用页面的dialog对象(ligerui对象)
	}catch(e){
	}
	$(function() {
		var h = $('body').height();
		$("#tabMyInfo").ligerTab({
		});
	});
	
	$(function() {
		$("a.save").click(function() {
			submitForm();
		});
	});
	//提交表单
	function submitForm() {
		var options = {};
		if (showResponse) {
			options.success = showResponse;
		}
		var frm = $('#sysUserForm').form();
		frm.ajaxForm(options);
		if (frm.valid()) {
			frm.submit();
		}
	}

	function showResponse(responseText) {
		var obj = new com.hotent.form.ResultMessage(responseText);
		if (obj.isSuccess()) {
			$.ligerDialog.confirm(obj.getMessage() + ",是否继续操作","提示信息",function(rtn) {
				if (rtn) {
					window.location.reload();
				} else {
					window.location.href = "${returnUrl}";
				}
			});
		} else {
			$.ligerDialog.err("提示信息","用户信息保存失败!",obj.getMessage());
		}
	}
	
	//添加个人照片
	function picCallBack(array){
		if(!array && array!="") return;
		var fileId=array[0].fileId,
			fileName=array[0].fileName;
		var path= __ctx + "/platform/system/sysFile/getFileById.ht?fileId=" + fileId;
		if(/\w+.(png|gif|jpg)/gi.test(fileName)){
			$("#picture").val("/platform/system/sysFile/getFileById.ht?fileId=" + fileId);
			$("#personPic").attr("src",path);
		}
			
		else
			$.ligerDialog.warn("请选择*png,*gif,*jpg类型图片!");
				
	};
	//上传照片
	function addPic(){
			HtmlUploadDialog({max:1,size:10,callback:picCallBack});
	};
	//删除照片
	function delPic(){
		$("#picture").val("");
		$("#personPic").attr("src","${ctx}/commons/image/default_image_male.jpg");
	};
	
	
</script>
</head>
<body>
	<div class="panel-toolbar">
		<div class="toolBar">
			<div class="group"><a class="link save" id="dataFormSave" href="javascript:;"><span></span>保存</a></div>
			<div class="l-bar-separator"></div>
			<div class="group"><a class="link back" href="${returnUrl}"><span></span>返回</a></div>
		</div>
	</div>
	<form id="sysUserForm" method="post" action="updateCommon.ht">
		<div id="tabMyInfo" style="overflow: hidden; position: relative;">
			<div title="基本信息" tabid="userdetail"
				icon="${ctx}/styles/default/images/resicon/user.gif">
				<div class="panel-detail">
					<table class="table-detail" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td rowspan="16" align="center" width="26%">
							<div style="float:top !important;background: none;height: 80px;padding:0px 20px 0px 20px;text-align: center;">
								<a class="link uploadPhoto" href="javascript:;" onclick="addPic();">上传照片</a>
								<p style="clear: both;height:10px;">&nbsp;</p>
								<a class="link del" href="javascript:;" onclick="delPic();">删除照片</a>
							</div>
							<div class="person_pic_div">
								<p><img id="personPic" src="${ctx}/${pictureLoad}" style="height: 140px; width:140px;border-radius: 50%" alt="个人相片" /></p>
							</div>
							</td>
							<th width="18%">帐   号: <span class="required red">*</span></th>
							<td >
								${sysUser.account}
							</td>
						</tr>						
						<tr>
						    <th>用户姓名: <span class="required red">*</span></th>
							<td ><input type="text" id="fullname" name="fullname" value="${sysUser.fullname}" style="width:240px !important" class="inputText" /></td>
						</tr>
						<tr>
							<th>用户性别: </th>
							<td>
							<select name="sex" class="select" style="width:245px !important">
								<option value="1" <c:if test="${sysUser.sex==1}">selected</c:if> >男</option>
								<option value="0" <c:if test="${sysUser.sex==0}">selected</c:if> >女</option>
							</select>						
							</td>
						</tr>						
						<tr>
							<th>入职时间: </th>
							<td>
								<fmt:formatDate value='${sysUser.entryDate}' pattern='yyyy-MM-dd'/>
							</td>
						</tr>						
						<tr>
							<th>员工状态: </th>
							<td>
								${sysUser.userStatus }	
							</td>
						</tr>						
						<tr>
							<th>是否锁定: </th>
							<td >								
								<c:if test="${sysUser.isLock==0}">未锁定</c:if>
								<c:if test="${sysUser.isLock==1}">已锁定</c:if>
							</td>				  
						</tr>
						
						<tr>
						    <th>是否过期: </th>
							<td >
								<c:if test="${sysUser.isExpired==0}">未过期</c:if>
								<c:if test="${sysUser.isExpired==1}">已过期</c:if>
							</td>
						</tr>
						
						<tr>
						   <th>当前状态: </th>
							<td>
								<c:if test="${sysUser.status==1}">激活</c:if>
								<c:if test="${sysUser.status==0}">禁用</c:if>
								<c:if test="${sysUser.status==-1}">删除</c:if>
							</td>								
						</tr>						
						<tr>
						   <th>邮箱地址: <span class="required red">*</span></th>
						   <td ><input type="text" id="email" name="email" value="${sysUser.email}" style="width:240px !important" class="inputText"/></td>
						</tr>
						<tr>
					    	<th>微   信: <span class="required red">*</span></th>
							<td ><input type="text" id="weixinid" name="weixinid" value="${sysUser.weixinid}"  style="width:240px !important" class="inputText"/></td>
						</tr>
						<tr>
							<th>手   机: <span class="required red">*</span></th>
							<td ><input type="text" id="mobile" name="mobile" value="${sysUser.mobile}" style="width:240px !important" class="inputText"/></td>						   
						</tr>
						
						<tr>
						    <th>电   话: </th>
							<td ><input type="text" id="phone" name="phone" value="${sysUser.phone}"  style="width:240px !important" class="inputText"/></td>
						</tr>
						
						<tr>
						    <th>工 号: <span class="required red">*</span></th>
							<td ><input type="text" id="staffNo" name="staffNo" value="${sysUser.staffNo}"  style="width:240px !important" class="inputText"/></td>
						</tr>
						
						<tr>
						    <th>身份证号: <span class="required red">*</span></th>
							<td ><input type="text" id="identification" name="identification" value="${sysUser.identification}"  style="width:240px !important" class="inputText"/></td>
						</tr>
						
						<tr>
						    <th>考勤编号: <span class="required red">*</span></th>
							<td ><input type="text" id="attendNo" name="attendNo" value="${sysUser.attendNo}"  style="width:240px !important" class="inputText"/></td>
						</tr>						
						<tr>
							<th>工作时间: </th>
							<td>
								<input type="text" id="workDate" name="workDate" value="<fmt:formatDate value='${sysUser.workDate}' pattern='yyyy-MM-dd'/>" class="inputText date" validate="{date:true}" />
							</td>
						</tr>
												
					</table>
					<input type="hidden" name="userId" value="${sysUser.userId}" />
					<input type="hidden" id="picture" name="picture" value="${sysUser.picture}" />
				</div>
			</div>
		</div>
	</form>	
</body>
</html>
