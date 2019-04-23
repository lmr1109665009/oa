<%--
	time:2015-07-28 10:23:07
	desc:edit the 权限
--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="/commons/include/html_doctype.html"%>
<html>
<head>
<title>编辑 权限</title>
<%@include file="/commons/include/form.jsp"%>
<script type="text/javascript" src="${ctx}/js/hotent/CustomValid.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js"></script>
<script type="text/javascript">
	var expandDepth  = 10;
	var temdType = "";
	$(function() {
		loadTree();//加载分类树
		initOwerData();
		$("a.save").click(function() {
			$("#sysKnowledgePerForm").attr("action", "save.ht");
			setKnowPerObj();
			submitForm();
		});
	    //人员查看详情事件
	    $("a.moreinfo").live('click',function(){
    		var me = $(this),
    			hrefStr = me.attr('hrefstr');
    		if(!hrefStr)return;
    		openDetailWin({url:hrefStr,hasClose:true});
    	});
	});
	function initOwerData(){//编辑的是初始化用户数据org..user
		var perObjList = '${perObjList}'
		var perObj = JSON.parse(perObjList);
		if(perObj.length==0) return;
		if(perObjList == "")return;
		var objArray = [];
		var objJson = {
			all:"N",
			user:"",
			org:""
		}
		for(var i=0 ;i<perObj.length;i++){
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
	//组成sysKnowledgePer对象数组
	function setKnowPerObj(){
		var perObj = getPerData();
		if(temdType==""){
			$.ligerDialog.warn("请分配人");
			throw new Error("请分配人");
		}
		var typeObj = JSON.parse(temdType);
		var sysKnowPerArry = [];
		var subject = $("#name").val();
		for(var i = 0 ; i < perObj.length ; i++){
			var typeId = perObj[i].typeId;
			var permission = perObj[i].permission;
			for(var key in typeObj){
				if(key == "all"){
					var sysKnowPer = {};
					if(typeObj[key] !='Y') continue;
					sysKnowPer.subject = subject;
					sysKnowPer.typeId = typeId;
					sysKnowPer.permissionJson = permission;
					sysKnowPer.permissionType = key;
					sysKnowPer.ownerId = 0;
					sysKnowPer.owner = "all";
					sysKnowPerArry.push(sysKnowPer);
				}else{
					var ownerArray = typeObj[key];
					for(var j = 0 ; j < ownerArray.length ; j++ ){
						var sysKnowPer = {};
						sysKnowPer.subject = subject;
						sysKnowPer.typeId = typeId;
						sysKnowPer.permissionJson = permission;
						sysKnowPer.permissionType = key;
						var ownerId = ownerArray[j].ownerId;
						var owner = ownerArray[j].ownerName;
						sysKnowPer.ownerId = ownerArray[j].ownerId;
						sysKnowPer.owner = ownerArray[j].ownerName;
						sysKnowPerArry.push(sysKnowPer);
					}
				}
			}
		}
		$("input[name='sysKnowObj']").val(JSON.stringify(sysKnowPerArry));
	}
	function submitForm() {
		var options = {};
		if (showResponse) {
			options.success = showResponse;
		}
		var frm = $('#sysKnowledgePerForm').form();
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
						window.location.href = window.location.href;
					} else {
						window.location.href = "${ctx}/platform/system/sysKnowPerRef/list.ht";
					}
				});
		} else {
			$.ligerDialog.confirm("知识库保存失败!","提示信息");
		}
	}
	
	 //打开对象选择窗口
    function selectKowPerUser(id){
    	var callBack = function(jsonStr){
    		if(objectIsEmpty(jsonStr)){
        		return;
        	}else{
        		var conf = $.parseJSON(jsonStr);
        		initOwner(jsonStr);
        	}
    	}
    	knwPerUserDialog(id,temdType,callBack)
    }
    //打开dialog
    function knwPerUserDialog(id,jsonStr,callBack){
    	var url=__ctx +"/platform/system/sysKnowledgePer/userDialog.ht?num=1";
    	var conf={};
    	if(objectIsEmpty(id)){
    		conf.id="";
    	}else{
    		conf.id=id;
    	}
    	if(objectIsEmpty(jsonStr)){
    		conf.jsonStr="";
    	}else{
    		conf.jsonStr=jsonStr;
    	}
    	url=url.getNewUrl();
    	DialogUtil.open({
            height:420,
            width: 650,
            title : '用户选择器',
            url: url, 
            isResize: true,
            //自定义参数
            conf: conf,
            sucCall:callBack
        });
    };

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

    //初始化分类树，如果是编辑单条权限，需要令其他的分类不可选择
    function iniTreeData(knowTree){
    	var perObjList = '${perObjList}'
    	var perObj = JSON.parse(perObjList);
		var knowTree = $.fn.zTree.getZTreeObj("knowTree");
    	
		$.each(perObj,function(i,permission){
    		var typeId = permission.typeId;
    		var permissionjson = permission.permissionJson;
    		var perArry = permissionjson.split(",");
			for(var i = 0 ; i < perArry.length; i ++){
				$("input[name='"+perArry[i]+"'][title='"+typeId+"']").attr("checked",true);
			}
			var node = knowTree.getNodeByParam("typeId", typeId, null);
			knowTree.checkNode(node, true,false);
		});	
    }
 	//加载树
	function loadTree(){
		var setting = {
			data: {
				key : { name: "typeName"},
				simpleData: {enable: true,idKey: "typeId",pIdKey: "parentId"}
			},
			view: {
				addDiyDom: addDiyDom,
				selectedMulti: true
			},
			check: {
				enable: true,
				chkStyle: "checkbox",
				nocheckInherit: true,
				chkboxType: { "Y": "ps", "N": "s" }
			},
			callback: {
				onCheck: zTreeOnCheck
			}
			
		};
		//一次性加载
		var url=__ctx + "/platform/system/globalType/getByCatKey.ht?catKey=KNOWLEDGE_TYPE&hasRoot=1";
		$.post(url,function(result){
			knowTree=$.fn.zTree.init($("#knowTree"), setting,eval(result));
			if(expandDepth!=0)
			{
				knowTree.expandAll(false);
				var nodes = knowTree.getNodesByFilter(function(node){
					return (node.level < expandDepth);
				});
				if(nodes.length>0){
					for(var i=0;i<nodes.length;i++){
						knowTree.expandNode(nodes[i],true,false);
					}
				}
			}else knowTree.expandAll(true);
			iniTreeData(knowTree);
		})
	}
	
 	//监听选中情况
 	function zTreeOnCheck(event, treeId, treeNode){
 		var check = treeNode.checked
		onCheckParNode(treeNode);
		onCheckChilNode(treeNode,check);
 	}
 	
 	function onCheckParNode(treeNode){
 		var obj = treeNode.getParentNode();
 		if(obj !=null && obj != "undefined"){
 			onCheckParNode(obj);
 		}
 		$("input[name='readDir'][title='"+treeNode.typeId+"']").attr("checked", true);
 	}
 	function onCheckChilNode(treeNode,check){
 		var obj =  treeNode.children;
 		if(obj !=null && obj != "undefined"){
 			$.each(obj,function(i,node){
				onCheckChilNode(node,check);
			});
 		}
 		if(check==true){
 			$("input[name='readDir'][title='"+treeNode.typeId+"']").attr("checked", true);
 		}else{
 			$("[title='"+treeNode.typeId+"']").attr("checked", false);
 		}
 	}
 	//重写分类树节点显示方式
	function addDiyDom(treeId, treeNode){
		var center = "center_docu";
		var perChe = "";
		if(treeNode.parentId != null){//如果是根节点则不会出现后面的权限选项
			perChe = '<td class="td2"><label><input name="readDir"  title="'+treeNode.typeId+'" type="checkbox" value="readDir" />读取</label>'
					 +'<td class="td2"><label><input title="'+treeNode.typeId+'"  name="del"  type="checkbox" value="del" />删除</label>'
					 +'<label><input  title="'+treeNode.typeId+'"  name="edit"  type="checkbox" value="edit" />编辑</label><label><input  title="'+treeNode.typeId+'"  name="add"  type="checkbox" value="add" />添加</label></td>';
		}else{
			perChe = '<td class="td2">'
					 +'<td class="td2">'
					 +'</td>';
		}
		if(treeNode.isParent){
			center = "center_open";
		}else{
			center = "center_docu";
		}
		var html = '<table class="formTable"> '
    			+'<tr> <td  class="td1"><span style="margin-left:'+treeNode.level*10+'px" id="'+treeNode.tId+'_switch" class="button level'+treeNode.level+' switch '+center+'" treenode_switch></span> '
				+'<span id="'+treeNode.tId+'_check"  name="treeCheck" class="button chk checkbox_false_full" treenode_check=""></span>'
    			+ treeNode.typeName+'</td>'
    			+ perChe
    			+'</tr></table>';  
			$("#" + treeNode.tId).html(html); 
	}
 	
 	//全选删除..编辑
	function allSelect(obj){
		var knowTree = $.fn.zTree.getZTreeObj("knowTree");
		var nodes = knowTree.getCheckedNodes(true);
		var o = $(obj); 
		var name = o.attr("name");
		var check = o.attr("checked");
		if(nodes.length > 0 && check=="checked"){//如果有勾选节点，则只要选已经勾选的节点,清除勾选则会全部去掉
			$.each(nodes,function(i,node){
				$("input[name='"+name+"'][title='"+node.typeId+"']").attr("checked", true);
			});
			return;
		}
		if(check=="checked"){
			$("input[name='"+name+"']").attr("checked", true);
		}else{
			$("input[name='"+name+"']").attr("checked", false);
		}
	}
 	
 	//获取分类树的权限，删除..读..写....
	function getPerData(){
		var knowTree = $.fn.zTree.getZTreeObj("knowTree");
		var nodes = knowTree.getCheckedNodes(true);
		var perArray = []
		$.each(nodes,function(i,node){
			if(node.typeId==null || node.typeId=="" || node.parentId == null) return;
			var selectedObj = $("input[title='"+node.typeId+"']:checked");
			var jsonVal = {};
			var temdValues = [];
			$.each(selectedObj,function(i,obj){
				var value = obj.value;
				temdValues.push(value);
			});
			var d = temdValues.join(",");
			jsonVal["typeId"] = node.typeId;
			jsonVal["permission"] = d;
			perArray.push(jsonVal);
		});
		if(perArray.length ==0){
			$.ligerDialog.warn("请至少选择一个节点");
			throw new Error("请至少选择一个节点");
		}
		return perArray;
	}
</script>
<style type="text/css">
.td1{width: 400px;}
.td2{width: 180px;text-align: center !important;}
.ztree li a{width: 100%}
.ztree li ul{left;margin: 0 0 0 0;padding:0 0 0 0;}
.ztree li{line-height:34px !important;padding-top: 0px !important;}
.ztree{padding:0px !important;}
</style>
</head>
<body>
	<div class="panel">
		<div class="panel-top">
			<div class="tbar-title">
				<c:choose>
					<c:when test="${sysKnowPerRef.id !=null}">
						<span class="tbar-label"><span></span>编辑</span>
					</c:when>
					<c:otherwise>
						<span class="tbar-label"><span></span>添加</span>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<div class="group">
						<a class="link save" id="dataFormSave" href="#"><span></span>保存</a>
					</div>
					<div class="l-bar-separator"></div>
					<div class="group">
						<a class="link back"
							href="list.ht"><span></span>返回</a>
					</div>
				</div>
			</div>
		</div>
		<div class="panel-body">
			<form id="sysKnowledgePerForm" method="post" action="save.ht">
				<table class="table-detail" cellpadding="0" cellspacing="0"
					border="0" type="main">
					<tr>
						<th width="20%">名称</th>
						<td><input type="text" id="name" name="name"
							value="${sysKnowPerRef.name}" class="inputText"
							validate="{required:true,maxlength:50}" /></td>
					</tr>
					<tr>
						<th width="20%">人员组织</th>
						<td>
							<table id="userSelect"   class="table-detail">
								<tr>
									<td colspan="3">
										<div class="group">
											<a class="link ok" 
												href="javascript: selectKowPerUser('${sysKnowPerRef.id}');"><span></span>选择</a>
										</div>
									</td>
								</tr>
							</table>
							<div id="ownerName_div">
								<table class="table-detail">
									<tr class="hidden">
										<th width="15%">用户</th>
										<td>
											<div class="owner-div"></div> <textarea class="hidden"
												name="ownerName"></textarea> <input type="hidden"
											name="rightType" value="user">
										</td>
									</tr>
									<tr class="hidden">
										<th width="15%">组织</th>
										<td>
											<div class="owner-div"></div> <textarea class="hidden"
												name="ownerName"></textarea> <input type="hidden"
											name="rightType" value="org">
										</td>
									</tr>
									<tr class="hidden">
										<td width="15%">所有用户</td>
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
					<tr>
						<th width="20%">拥有权限</th>
						<td>
							<ul class="ztree">
								<li><table class="ztitle">
										<tr>
											<td class="td1" style="text-align: center; ">树
												</div>
											</td>
											<td class="td2" style="">所在分类树权限<br>
												<label><input type="checkbox" class="selectAll" name="readDir"
														onclick="allSelect(this)" value="readDirAll" />读取</label>
											</td>
											<td class="td2" style="">文章权限<br>
												<label><input
												type="checkbox" name="del" onclick="allSelect(this)" class="selectAll" 
												value="delAll" />删除</label> <label><input type="checkbox" class="selectAll" 
												name="edit" onclick="allSelect(this)" value="editAll" />编辑</label><label><input type="checkbox" class="selectAll" 
												name="add" onclick="allSelect(this)" value="addAll" />添加</label>
											</td>
										</tr>
								</table></li>
							</ul>
							<ul id="knowTree" class="ztree">
							</ul>
						</td>
					</tr>
				</table>
				<input type="hidden" name="sysKnowObj" value="" /> 
				<input type="hidden" name="id" value="${sysKnowPerRef.id}" /> 
				</br>
				</br>
			</form>
		</div>
	</div>
</body>
</html>
