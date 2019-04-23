<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="com.suneee.platform.model.system.SysUser"%>
<%@include file="/commons/include/html_doctype.html" %>
<html>
<head>
	<title>用户表管理</title>
	<%@include file="/commons/include/get.jsp" %>
	<script type="text/javascript" src="${ctx}/js/util/form.js"></script>
	<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
	<script type="text/javascript" src="${ctx}/js/suneee/base/system/CommonDialog.js"></script>
	<script type="text/javascript">
	function openUserUnder(userid,obj){
		if($(obj).hasClass('disabled')) return false;
		 
		var conf={};				
		var url=__ctx + "/platform/system/userUnder/list.ht?userId="+userid;
		conf.url=url;
		var dialogWidth=800;
		var dialogHeight=600;
		conf=$.extend({},{dialogWidth:dialogWidth ,dialogHeight:dialogHeight ,help:0,status:0,scroll:0,center:1},conf);
		DialogUtil.open({
			height:conf.dialogHeight,
			width: conf.dialogWidth,
			title : '下属管理',
			url: url, 
			isResize: true,
		});
	}
	function syncUserToWx(){
		var successMessage ;
		var aryId = $("input[type='checkbox'][disabled!='disabled'][class='pk']:checked");
		var len=aryId.length;
		if(len==0){
			successMessage ="同步所有用户、组织至微信通讯录成功！";
			$.ligerDialog.waitting('正在同步所有用户组织至微信通讯录，请等待...');
		}else successMessage ="同步"+len+"名用户至微信通讯录操作成功！";
		
		var ids="";
		aryId.each(function(i){
			ids+=$(aryId[i]).val() +",";
		})
		var url=__ctx + "/platform/system/sysUser/syncUserToWx.ht?userId="+ids;
		$.post(url,function(data){
			$.ligerDialog.closeWaitting();
			var obj=new com.hotent.form.ResultMessage(data);
			if(obj.isSuccess()){
				$.ligerDialog.success(successMessage+"（不存在组织或者尚未完善微信号信息将跳过）","提示信息");
			}
			else{
				$.ligerDialog.err("提示信息","同步用户失败",obj.data.cause);
			}
		})
	}
	
	function syncToLdap(obj){
		
		var v=$(obj);
		
		if(v.hasClass("disabled")){
			$.ligerDialog.error('没有权限!');
			return;
		}
		var confirmContent="<font>与AD服务器同步会将AD的用户同步到系统数据库，您确定要进行同步吗？</font>";
		$.ligerDialog.confirm(confirmContent,function(data){
			if(data){
				sync();
			}else{
				return false;
			}
		});
		sync=function(conf){
			var url=__ctx + "/platform/system/sysUser/syncUser.ht";
			$.ligerDialog.waitting('正在同步AD用户，请等待...');
			$.post(url,function(data){
				$.ligerDialog.closeWaitting();
				var obj=new com.hotent.form.ResultMessage(data);
				if(obj.isSuccess()){
					$.ligerDialog.success("同步用户成功!","提示信息",function(){
						location.href=location.href.getNewUrl();
					});
				}
				else{
					$.ligerDialog.err("提示信息","同步用户失败!",obj.getMessage());
				}
			})
		};
	}
	$(function(){
		$(".import").click(function(){
			var conf={};				
			var url=__ctx + "/platform/system/sysUser/importDialog.ht";
			conf.url=url;
			var dialogWidth=700;
			var dialogHeight=450;
			conf=$.extend({},{dialogWidth:dialogWidth ,dialogHeight:dialogHeight ,help:0,status:0,scroll:0,center:1},conf);
			DialogUtil.open({
				height:conf.dialogHeight,
				width: conf.dialogWidth,
				title : '用户批量导入',
				url: url, 
				isResize: true,
				sucCall:function(){
					location.href=location.href.getNewUrl();
				}
			});
		});
		
		$(".batchHandle").click(function(){
			var conf={};				
			var url=__ctx + "/platform/system/sysUser/handleDialog.ht";
			conf.url=url;
			var dialogWidth=700;
			var dialogHeight=450;
			conf=$.extend({},{dialogWidth:dialogWidth ,dialogHeight:dialogHeight ,help:0,status:0,scroll:0,center:1},conf);
			DialogUtil.open({
				height:conf.dialogHeight,
				width: conf.dialogWidth,
				title : '批量用户处理',
				url: url, 
				isResize: true,
				sucCall:function(){
					location.href=location.href.getNewUrl();
				}
			});
		});
		
		$("a.batchange").click(function(){
			$.ligerDialog.confirm('您确定删除选中的用户吗？','提示信息',function(rtn) {
				var userIds = getUserIds();
				var url = __ctx + "/platform/system/sysUser/delOrRevert.ht";
				var data = {"userId":userIds, "status":<%=SysUser.STATUS_Del%>};
				$.post(url, data, function(result){
					if(result.status == 0){
						$.ligerDialog.success("批量删除请求成功!","提示信息",function(){
							location.href=location.href.getNewUrl();
						});
					}
					else{
						$.ligerDialog.err("提示信息","批量删除请求失败!",result.message);
					}
				});
			});
		});
	});
	
	function change(userId, username){
		$.ligerDialog.confirm('您确定删除用户“' + username + '”吗','提示信息',function(rtn) {
			if(rtn) {
				var url = __ctx + "/platform/system/sysUser/delOrRevert.ht";
				var params = {"userId":userId, "status":<%=SysUser.STATUS_Del%>};
				$.post(url, params, function(result){
					if(result.status == 0){
						$.ligerDialog.success("删除请求成功!","提示信息",function(){
							location.href=location.href.getNewUrl();
						});
					}
					else{
						$.ligerDialog.err("提示信息","删除请求失败!",result.message);
					}
				});
			}
		});
		
	}
	
	function syncToUc(){
		var confirmContent="<font color='red'>与用户中心同步会将系统数据库的用户同步到用户中心，您确定要进行同步吗？</font>";
		$.ligerDialog.confirm(confirmContent,function(data){
			if(data){
				syncUc();
			}else{
				return false;
			}
		});
		syncUc=function(){
			var url=__ctx + "/platform/system/sysUser/syncUserToUc.ht";
			$.ligerDialog.waitting('正在同步到用户中心，请等待...');
			$.post(url,function(data){
				$.ligerDialog.closeWaitting();
				if(data.status == 0){
					$.ligerDialog.success("同步用户成功!","提示信息",function(){
						location.href=location.href.getNewUrl();
					});
				}
				else{
					$.ligerDialog.err("提示信息","同步用户失败!",data.message);
				}
			})
		};
	}
	function exportUsers(){	
        var url=__ctx + "/platform/system/sysUser/exportUser.ht";
        $('#searchForm').attr("action",url).submit();
		$('#searchForm').attr("action","list.ht");
       /*  $.ligerDialog.waitting('正在导出用户信息，请等待...');
        $.post(url,function(data){
			$.ligerDialog.closeWaitting();
			var obj=new com.hotent.form.ResultMessage(data);
			if(obj.isSuccess()){
				$.ligerDialog.success("导出用户成功!<br/>文件位置：C:/Users/用户信息表.xls","提示信息",function(){
					location.href=location.href.getNewUrl();
				});
			}
			else{
				$.ligerDialog.err("提示信息","导出用户失败!",obj.getMessage());
			}
		})  */
		}
	
	
	function syncTypeToUserCenter(){
		var confirmContent="<font color='red'>与用户中心同步会将系统数据库的用户在用户中心更改为企业用户，您确定要进行同步吗？</font>";
		$.ligerDialog.confirm(confirmContent,function(data){
			if(data){
				syncUc();
			}else{
				return false;
			}
		});
		syncUc=function(){
			var url=__ctx + "/platform/system/sysUser/syncToUserCenter.ht";
			$.ligerDialog.waitting('正在同步到用户中心，请等待...');
			$.post(url,function(data){
				$.ligerDialog.closeWaitting();
				if(data.status == 0){
					$.ligerDialog.success("同步用户成功!","提示信息",function(){
						location.href=location.href.getNewUrl();
					});
				}
				else{
					$.ligerDialog.err("提示信息","同步用户失败",data.message);
				}
			})
		};
	}
	function setRegion(){
		var userIds = getUserIds();
		if(userIds == ""){
			$.ligerDialog.warn('还没有选择,请选择一项记录!','提示');
			return;
		}
		
		// 回调函数
		var setRegionOk=function(result){
			var params = {userIds:userIds, region:result.region};
			var url = __ctx + "/platform/system/sysUser/setRegion.ht"
			$.post(url,params,function(data){
				if(data.status == 0){
					$.ligerDialog.success("设置地区成功!","提示信息",function(){
						location.href=location.href.getNewUrl();
					});
				}
				else{
					$.ligerDialog.err("提示信息","设置地区失败",data.message);
				}
			});
		};
		
		var conf = {url:"/platform/system/sysUser/getRegionList.ht", 
				dialogWidth:500, 
				dialogHeight:250, 
				title:"设置地区",
				callBack:setRegionOk
		};
		CommonDialog(conf);
	}
	
	function getUserIds(){
		var aryChk=$("input:checkbox[name='userId']:checked");
		if(aryChk.size()==0) return "";
		var aryUserId=[];
		aryChk.each(function(){
			aryUserId.push($(this).val());
		});
		return aryUserId.join(",");
	}
	
	function chooseUser() {
		UserDialog({isSingle:true,
			callback:function(userIds, fullnames){
				var fullname =fullnames.split("(")[0];
				$("#Q_fullname_SL").val(fullname);
			}
		});
	};
    function fromAuthorityCenter() {
        var url=__ctx + "/platform/system/sysUser/getFromAuthorityCenter.ht";
        $.post(url,function(val){
            if(val.status == 0){
                var data = val.data;
                var name = data.name;
                var password = data.password;
                var urlTo = data.url;
                var pramsUrl = data.preUrl;
                var url =urlTo+"?name="+name+"&password="+password+"&url="+pramsUrl;
                $(".userAuthLink").attr("href",url)
                //location.href=url;
                // window.open=url;
            }
        })
    }
    fromAuthorityCenter();
    /* window.location.href = "${ctx}/kaoqin/kaoqin/shiftDaySetting/list.ht";*/
    
    /*切换用户 */
    function switchUser(account){
    	if(!account){
    		$.ligerDialog.warn('系统错误，请联系管理员!','提示信息');
    	}
    	var url = __ctx + "/j_spring_security_switch_user?j_username=" + account;
    	$.get(url, function(result){
    		if(result.status == 0){
    			top.location.href = __ctx + "/platform/console/main.ht";
    		} else {
    			$.ligerDialog.err("提示信息","切换用户失败!",result.message);
    		}
    	});
    }
	</script>
</head>
<body>
<c:set var="SysUser_EXPIRED" value="<%=SysUser.EXPIRED %>" />
<c:set var="SysUser_UN_EXPIRED" value="<%=SysUser.UN_EXPIRED %>"  />

<c:set var="SysUser_LOCKED" value="<%=SysUser.LOCKED %>"/>
<c:set var="SysUser_UN_LOCKED" value="<%=SysUser.UN_LOCKED %>"/>

<c:set var="SysUser_STATUS_Del" value="<%=SysUser.STATUS_Del %>" />
	<div class="panel">

			<div class="panel-top">
				<div class="tbar-title">
					<span class="tbar-label">用户表管理列表</span>
				</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group">
						<f:a alias="searchUser" css="link search" id="btnSearch"><span></span>查询</f:a>
					</div>
					<div class="l-bar-separator"></div>
					<div class="group">
						<f:a alias="addUser" css="link add" href="edit.ht"><span></span>添加</f:a>
					</div>
					<div class="l-bar-separator"></div>
					<div class="group">
						<f:a alias="changeStatus" css="link batchange" action="changeStatus.ht"><span></span>批量删除</f:a>
					</div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link batchHandle" href="javascript:;"><span></span>批量数据处理</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link import" href="javascript:;"><span></span>导入</a></div>
					<div class="l-bar-separator"></div>
					<div class="group"><a class="link export" href="javascript:exportUsers();"><span></span>导出</a></div> 
					<div class="l-bar-separator"></div>
					<%-- <div class="group">
						<f:a alias="syncToUc" css="link reload" showNoRight="false" onclick="syncToUc()"><span></span>同步</f:a>
					</div> --%>
					<div class="group">
						<f:a alias="syncToUc" css="link reload" showNoRight="false" onclick="syncTypeToUserCenter()"><span></span>同步</f:a>
					</div>
					<div class="group">
						<f:a alias="authority" css="link reload userAuthLink" href="" target="_blank" showNoRight="false"><span></span>用户权限</f:a>
					</div>
					<c:if test="${isSupportWeixin}">
					<div class="l-bar-separator"></div>
					<div class="group">
						<f:a alias="syncUserToWx" onclick="syncUserToWx()" css="link reload" action="syncUserToWx.ht"><span></span>同步微信通讯录</f:a>
					</div>
					</c:if>
					<div class="l-bar-separator"></div>
					<div class="group">
						<f:a alias="setCategory" css="link category" showNoRight="false" onclick="setRegion()"><span></span>设置地区</f:a>
					</div>
					<div class="l-bar-separator"></div>
					<div class="group">
					    <a class="link reset" onclick="$.clearQueryForm()"><span></span>重置</a>
					</div>
					<%-- <div class="l-bar-separator"></div>
					<div class="group">
						<f:a alias="syncToLdap" css="link reload" showNoRight="false" onclick="syncToLdap(this)"><span></span>同步</f:a>
					</div> --%>
				</div>	
			</div>
			<div class="panel-search">
					<form id="searchForm" method="post" action="list.ht">
						<ul class="row">
							<li><span class="label">姓名:</span><input type="text" name="Q_fullname_SL" id="Q_fullname_SL" class="inputText"  value="${param['Q_fullname_SL']}"/>
							<a href="javascript:;" class="button"  onclick="chooseUser()"><span class="icon ok" ></span><span class="chosen" >选择</span></a>		
							</li>		
							<li><span class="label">字号:</span><input type="text" name="Q_aliasname_SL"  class="inputText"  value="${param['Q_aliasname_SL']}"/></li>						
						    <li><span class="label">账号:</span><input type="text" name="Q_account_SL"  class="inputText"  value="${param['Q_account_SL']}"/></li>
							 <li><span class="label">手机:</span><input type="text" name="Q_mobile_SL"  class="inputText"  value="${param['Q_mobile_SL']}"/></li>
						    <li><span class="label">邮箱:</span><input type="text" name="Q_email_SL"  class="inputText"  value="${param['Q_email_SL']}"/></li>
							<%-- <li><span class="label">来源:</span>
							<select name="Q_fromType_S" class="select" value="${param['Q_fromType_S']}">
								<option value="">--选择--</option>
								<option value="<%=SysUser.FROMTYPE_DB %>">系统添加</option>
								<option value="<%=SysUser.FROMTYPE_AD %>">AD同步</option>
							</select>
							</li> --%>
							<li>
								<span class="label">状态:</span>
								<select name="Q_status_S" class="select"  value="${param['Q_status_S']}">
									<option value="">--选择--</option>
									<option value="<%=SysUser.STATUS_OK %>">激活</option>
									<option value="<%=SysUser.STATUS_NO %>">禁用</option>
									<%-- <option value="<%=SysUser.STATUS_Del %>">删除</option> --%>
								</select>
							</li>
							<li>
								<span class="label">是否已同步:</span>
								<select name="Q_syncToUc_S" class="select"  value="${param['Q_syncToUc_S']}">
									<option value="">--选择--</option>
									<option value="1">是</option>
									<option value="0">否</option>
								</select>
							</li>
						
						<%-- <li>
							<span class="label">创建时间从:</span>
							<input type="text" id="Q_begincreatetime_DL" name="Q_begincreatetime_DL"  class="inputText Wdate" onfocus="WdatePicker({maxDate:'#F{$dp.$D(\'Q_endcreatetime_DG\');}'})" value="${param['Q_begincreatetime_DL']}"/>
							<span class="label label_line">_ </span>
							<input type="text" id="Q_endcreatetime_DG" name="Q_endcreatetime_DG"  class="inputText Wdate" onfocus="WdatePicker({minDate:'#F{$dp.$D(\'Q_begincreatetime_DL\');}'})"  value="${param['Q_endcreatetime_DG']}"/>
						</li>
						
							<li><span class="label">是否锁定:</span>
								<select name="Q_isLock_S" class="select"  value="${param['Q_isLock_S']}">
									<option value="">--选择--</option>
									<option value="${SysUser_LOCKED}"  <c:if test="${param['Q_isLock_S'] == 'SysUser_LOCKED' }">selected</c:if>>已锁定 </option>
									<option value="${SysUser_UN_LOCKED}" <c:if test="${param['Q_isLock_S'] == 'SysUser_UN_LOCKED' }">selected</c:if>>未锁定 </option>
								</select>
							</li>
							<li><span class="label">是否过期:</span>	
								<select name="Q_isExpired_S" class="select"  value="${param['Q_isExpired_S']}">
									<option value="">--选择--</option>
								<option value="${SysUser_EXPIRED}"  <c:if test="${param['Q_isExpired_S'] == 'SysUser_EXPIRED'}">selected</c:if>>已过期 </option>
								<option value="${SysUser_UN_EXPIRED}" <c:if test="${param['Q_isExpired_S'] == 'SysUser_UN_EXPIRED' }">selected</c:if>>未过期 </option>
								</select>
							</li> --%>
						
						<li><button class="btn">查询</button></li></ul>
					</form>
			</div>

		</div>
		<div class="panel-body">
		    	<c:set var="checkAll">
					<input type="checkbox" id="chkall"/>
				</c:set>
			    <display:table export="true" name="sysUserList" id="sysUserItem" requestURI="list.ht" sort="external" cellpadding="1" cellspacing="1"   class="table-grid">
					<display:column title="${checkAll}" media="html" style="width:30px;">
						  	<input type="checkbox" class="pk" name="userId" value="${sysUserItem.userId}">
					</display:column>
					<display:column property="fullname" title="姓名" sortable="true" sortName="fullname" style="width:100px;"></display:column>
					<display:column property="account" title="帐号" sortable="true" sortName="account" style="width:100px;"></display:column>
					<display:column property="regionName" title="地区" sortable="false"></display:column>
					<display:column  title="创建时间" sortable="true" sortName="createtime">
						<fmt:formatDate value="${sysUserItem.createtime}" pattern="yyyy-MM-dd"/>
					</display:column>
					<%--<display:column title="是否过期" sortable="true" sortName="isExpired">
						<c:choose>
							<c:when test="${sysUserItem.isExpired==1}">
								<span class="red">已过期</span>
						   	</c:when>
					       	<c:otherwise>
						    	<span class="green">未过期</span>
						   	</c:otherwise>
						</c:choose>
					</display:column>
	                <display:column title="是否可用" sortable="true" sortName="isLock">
						<c:choose>
							<c:when test="${sysUserItem.isLock==1}">
								<span class="red">已锁定</span>
						   	</c:when>
					       	<c:otherwise>
					       		<span class="green">未锁定</span>
						   	</c:otherwise>
						</c:choose>
					</display:column> --%>
                	<display:column title="状态" sortable="true" sortName="status">
						<c:choose>
							<c:when test="${sysUserItem.status==1}">
								<span class="green">激活</span>
								
						   	</c:when>
						   	<c:when test="${sysUserItem.status==0}">
						   		<span class="red">禁用</span>
								
						   	</c:when>
					       	<c:otherwise>
					       		<span class="red">删除</span>
						        
						   	</c:otherwise>
						</c:choose>
					</display:column>
					<display:column title="已同步" sortable="true" sortName="syncToUc">
						<c:choose>
							<c:when test="${sysUserItem.syncToUc==1}">
								<span class="green">是</span>
							</c:when>
							<c:otherwise>
								<span class="red">否</span>
							</c:otherwise>
						</c:choose>
					</display:column>
					<display:column title="数据来源" sortable="true" sortName="fromType">
						<c:choose>
							<c:when test="${sysUserItem.fromType==1}">
								<span class="brown">AD</span>
								
						   	</c:when>
						   	<c:when test="${sysUserItem.fromType==0}">
						   		<span class="green">系统添加</span>
						   	</c:when>
					       	<c:otherwise>
					       		<span class="red">未知</span>
						   	</c:otherwise>
						</c:choose>
					</display:column>	
					<c:if test="${isSupportWeixin}">
					<display:column title="同步至微信" sortable="true" sortName="hasSyncToWx">
						<c:choose>
							<c:when test="${sysUserItem.hasSyncToWx==1}">
								<span class="green">已同步</span>
						   	</c:when>
						   	<c:when test="${sysUserItem.hasSyncToWx==2}">
								<span class="green">已关注</span>
						   	</c:when>
					       	<c:otherwise>
					       		<span class="red">尚未同步</span>
						   	</c:otherwise>
						</c:choose>
					</display:column>
					</c:if>	
					
					<display:column title="管理" media="html" class="opsBtnb" style="width:300px;">
						<f:a alias="updateUserInfo" css="link edit" href="edit.ht?userId=${sysUserItem.userId}" >编辑</f:a> | 
						<f:a alias="userInfo" css="link detail" href="get.ht?userId=${sysUserItem.userId}">详情</f:a> | 
					    <f:a alias="userUnder" css="link primary" href="javascript:;" onclick="openUserUnder('${sysUserItem.userId}',this)">下属管理</f:a> | 
						<f:a alias="setParams" css="link parameter" href="${ctx}/platform/system/sysUserParam/editByUserId.ht?userId=${sysUserItem.userId}" >参数属性</f:a> | 
						<%--<f:a alias="resetPwd" css="link resetPwd" href="resetPwdView.ht?userId=${sysUserItem.userId}">修改密码</f:a>|--%>
						<f:a alias="setStatus" css="link setting" href="editStatusView.ht?userId=${sysUserItem.userId}">设置状态</f:a> | 
						<c:if test="${cookie.origSwitch==null  }">
							<f:a alias="switch" css="link switchuser" target="_top" href="javascript:0;" onclick="switchUser('${sysUserItem.account}')">切换用户</f:a> | 
						</c:if>
						<f:a alias="delUser" css="link change" onclick="change(${sysUserItem.userId}, '${sysUserItem.fullname }')" >删除 </f:a>
					</display:column>
				</display:table>
				<hotent:paging tableId="sysUserItem"/>
			
		</div>
	</div>
</body>
</html>