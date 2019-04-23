<%--
	time:2015-07-28 10:23:07
	desc:edit the 权限
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<title>权限明细</title>
<%@include file="/commons/include/form.jsp"%>
<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
<script type="text/javascript">
	var expandDepth  = 10;
	var temdType = "";
	$(function() {
		initOwerData();
		initPerData();
	    //人员查看详情事件
	    $("a.moreinfo").live('click',function(){
    		var me = $(this),
    			hrefStr = me.attr('hrefstr');
    		if(!hrefStr)return;
    		openDetailWin({url:hrefStr,hasClose:true});
    	});
	});
	function initPerData(){
		var perObjList = '${perObjList}'
		if(perObjList == "")return;
		var perObj = JSON.parse(perObjList);
		var html = "";
		var html = "";
		$.each(perObj,function(i,permission){
			var permissionJson = permission.permissionJson;
			if(permissionJson==""){
				return true;
			}
			var perArray = permissionJson.split(",");
			html += '<tr><th width="20%">'+permission.typeName+':</th>';
			html +='<td style="width:80%" class="formInput">';
			for(var j = 0 ; j < perArray.length ; j++){
				var perName = ""
				if(perArray[j] == "readDir"){
					perName = "分类可读取";
				}else if(perArray[j] == "del"){
					perName = "文章可删除";
				}else if(perArray[j] == "edit"){
					perName = "文章可编辑";
				}else if(perArray[j] == "add"){
					perName = "可添加文章";
				}else if(perArray[j] == "read"){
					perName = "文章可读";
				}
				html += '<span class="owner-span">'+perName+'</span>';
			}
			html += '</td></tr>';
		});
		$("#detailTr").after(html);
	}
	function initOwerData(){//编辑的是初始化用户数据org..user
		var perObjList = '${perObjList}'
		if(perObjList == "")return;
		var perObj = JSON.parse(perObjList);
		var objArray = [];
		var objJson = {
			all:"N",
			user:"",
			org:""
		}
		for(var i=0;i<perObj.length ;i++){
			var permissionType = perObj[i].permissionType;
			var ownerId = perObj[i].ownerId;
			var owner = perObj[i].owner;
			if(permissionType=='all'){
				objJson.all="Y";
			}else if(permissionType =="user"){
				var array = [];
				var u = {
					ownerId:ownerId,
					ownerName:owner
				}
				array.push(u);
				objJson.user = array;
			}else if(permissionType =="org"){
				var array = [];
				var o = {
					ownerId:ownerId,
					ownerName:owner
				}
				array.push(o);
				objJson.org = array;
			}
		}
		initOwner(objJson);
	}

    //授权用户数据
	function initOwner(objJson){
		var jsonStr = JSON2.stringify(objJson);
		temdType = jsonStr;
    	var emptyMark = true;
    	var ownerObj = [];
    	$("textarea[name='ownerName']",$("#ownerName_div")).each(function(){
    		var me = $(this),
				tr = me.closest("tr"),
				owner = $("textarea[name='ownerName']",tr);
				rightType = $("input[name='rightType']",tr).val();
			if(rightType=='all'){
				owner.val(objJson.all);
				if(objJson.all=="Y"){
					tr.show();
					emptyMark = false;
				}else{
					tr.hide();
				}
			}else if(rightType=='user'){
				if(objectIsEmptyByRep(objJson.user,'')){
					tr.hide();
				}else{
					tr.show();
					var href = __ctx+"/platform/system/sysUser/get.ht?openType=detail&userId=";
					setVal(me,objJson.user,href);
					emptyMark = false;
				}
			}else if(rightType=='org'){
				if(objectIsEmptyByRep(objJson.org,'')){
					tr.hide();
				}else{
					tr.show();
					setVal(me,objJson.org);
					emptyMark = false;
				}
			}
		});
    	//是否没有授权对象内容
    	if(emptyMark){
    		$("tr.empty-div",$("#ownerName_div")).show();
    	}else{
    		$("tr.empty-div",$("#ownerName_div")).hide();
    	}
	}
	//是否为空
	function objectIsEmpty(obj){
		// 内容是是否为空
		if(typeof(obj)==undefined||obj==null||obj==''){
			return true;
		}else{
			return false;
		}
	};
	//并且不能等str的内容，是否为空
	function objectIsEmptyByRep(obj,str){
	    // 内容是是否为空
		if(typeof(obj)==undefined||obj==null||obj==''||obj==str){
			return true;
		}else{
			return false;
		}
	};

	//设置值
	function setVal(obj,json,href){
		var tr=$(obj).closest("tr"),
			owner = $("textarea[name='ownerName']",tr);
		if(json=='textarea'){
			json = owner.val();
			json = $.parseJSON(json);
		}else{
			var jsonStr = JSON2.stringify(json);
			owner.val(jsonStr);
		}
		setOwnerSpan(tr,json,href);
	};
	
	function setOwnerSpan(tr,json,href){
		var div = $("div.owner-div",tr);
		if(!div||div.length==0)return;
		div.empty();
		if(!json||json.length==0)return;
		for(var i=0,c;c=json[i++];){
			if(c.ownerId == "")
				continue;
			var a = $('<a class="moreinfo"></a>').html(c.ownerName).attr("ownerId",c.ownerId);
			if(href){
				a.attr("hrefstr",href+c.ownerId);
				a.attr("href","#");
			}
			var	span = $('<span class="owner-span"></span>').html(a);
			div.append(span);
		}
		var html = div.html();
	};
    //显示用户详情
    function openDetailWin(conf){
    	var dialogWidth=650;
    	var dialogHeight=550;
    	
    	conf=$.extend({},{dialogWidth:dialogWidth ,dialogHeight:dialogHeight ,help:0,status:0,scroll:0,center:1},conf);

    	var winArgs="dialogWidth="+conf.dialogWidth+"px;dialogHeight="+conf.dialogHeight
    		+"px;help=" + conf.help +";status=" + conf.status +";scroll=" + conf.scroll +";center=" +conf.center;
    	var url = conf.url + '&hasClose=' +conf.hasClose;
    	DialogUtil.open({
    		height:conf.dialogHeight,
    		width: conf.dialogWidth,
    		title : '用户选择器',
    		url: url, 
    		isResize: true,
    	});
    }

 	//全选删除..编辑
	function allSelect(obj){
		var o = $(obj); 
		var name = o.attr("name");
		var check = o.attr("checked");
		if(check=="checked"){
			$("input[name='"+name+"']").attr("checked", true);
		}else{
			$("input[name='"+name+"']").attr("checked", false);
		}
	}
</script>
<style type="text/css">
.td1{width: 400px}
.td2{width: 180px}
.ztree li a{width: 100%}
.ztree li ul{left;margin: 0 0 0 0;padding:0 0 0 0;}
</style>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="l-bar-separator"></div>
					<div class="group">
						<a class="link back"
							href="list.ht"><span></span>返回</a>
					</div>
				</div>
			</div>
		</div>
		<div class="panel-body">
			<form>
				<table class="table-detail" cellpadding="0" cellspacing="0"
					border="0" type="main">
					<tr>
						<th width="20%">名称</th>
						<td>${sysKnowPerRef.name}</td>
					</tr>
					<tr id = "detailTr">
						<th width="20%">人员组织</th>
						<td>
							<div id="ownerName_div">
								<table class="table-detail" style="border: hidden !important;" >
									<tr class="hidden">
										<td>
											<div class="owner-div"></div> <textarea class="hidden"
												name="ownerName"></textarea> <input type="hidden"
											name="rightType" value="user">
										</td>
									</tr>
									<tr class="hidden">
										<td>
											<div class="owner-div"></div> <textarea class="hidden"
												name="ownerName"></textarea> <input type="hidden"
											name="rightType" value="org">
										</td>
									</tr>
									<tr class="hidden">
										<td>
											<div class="owner-div">
												<label style="float: left" for="selectAll">允许所有人访问</label>
											</div> <textarea rightType="0" class="hidden" name="ownerName"></textarea>
											<input type="hidden" name="rightType" value="all">
										</td>
									</tr>
								</table>
							</div>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</body>
</html>
