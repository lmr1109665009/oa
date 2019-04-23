<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://www.jee-soft.cn/functions" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spr" uri="http://www.springframework.org/tags"%>
<%@include file="/commons/include/html_doctype.html"%>
<html ng-app="base">
<head> 
<title>显示通用对话框</title>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script src="${ctx}/weixin/assets/js/importJs.js"></script>
<script type="text/javascript">

importCss(["/weixin/assets/js/ztree/css/zTreeStyle/zTreeStyle.css",
           "/weixin/assets/js/ztree/css/outLook.css",
           "/weixin/assets/css/jquery.qtip.css",
           "/weixin/assets/css/formValid.css"]);

importJs(["/weixin/assets/js/angular.min.js",
          "/weixin/assets/js/zh_CN.js",
          "/weixin/assets/js/BaseService.js",
          "/weixin/assets/js/CustomValid.js",
          "/js/hotent/platform/form/rule.js",
          "/weixin/assets/js/jquery.qtip.js",
          "/weixin/assets/js/ztree/js/jquery.ztree.core-3.5.js",
          "/weixin/assets/js/ztree/ZtreeCreator.js",
          "/weixin/assets/js/dialog/Dialogs.js",
        //"/weixin/assets/js/formDirective.js",
          "/weixin/assets/js/dialog/customDialogController.js"]);
          
	var treeParamStr="${treeDialog.displayfield}";
	var isSingle=${isSingle};
	var combineField ='${combineField}';// 组合对话组合信息。
	var isCombine =${isCombine};//
	var needTree =${not empty treeDialog};//是否包含树
	var needList =${not empty listDialog};
	var returnField=${returnField};
	var listDialogalias = '${listDialog.alias}';
	var treeUrl="${ctx}/platform/form/bpmFormDialog/getTreeData.ht?alias=${treeDialog.alias}";
</script>
<style type="text/css">
.am-modal-bd { padding: 1px 1px!important;}

</style>
</head>
<body ng-controller="CustomDialogController">
	<div class="am-topbar-fixed-top am-form">
	<!-- 搜索框 begin -->
        <div class="am-g">
    	<c:if test="${fn:length(listDialog.conditionList)>0}">
       	  <div class="am-u-sm-2 am-u-md-3">
	       	 <div class="am-form-group">
		         <select data-am-selected="{btnSize: 'sm'}" ng-model="pageParam._queryName">
		           <!-- 查询参数 -->
		           <c:forEach items="${listDialog.conditionList}" var="col" varStatus="status">
		           <c:if test="${col.paraCt eq '1'}">
		           	<c:choose>
		           		<c:when test="${col.condition eq '='}"><c:set value="S" var="condtion"/></c:when>
		           		<c:otherwise><c:set value="SL" var="condtion"/></c:otherwise>
		           	</c:choose>
		           	<option value="Q_${col.fieldName}_${condtion}" <c:if test="${!hasInit}">ng-init="pageParam._queryName='Q_${col.fieldName}_${condtion}'" selected</c:if>>
		            ${col.comment }</option>
		           	<c:set value="true" var="hasInit"/>
		           	</c:if>
		           </c:forEach>
		         </select>
	      	 </div>
    	 	</div>
       		<div class="am-u-sm-8 am-u-md-7 am-u-end">
       			<div class="am-input-group am-input-group-sm">
   					<input type="text" class="am-form-field" ng-model="pageParam._queryData">
          			<span class="am-input-group-btn">
           			<button class="am-btn am-btn-default" type="button" ng-click="reload(true)">搜索</button>
          			</span>
        		</div>
 	  		</div>
     	 </div>
     	 </c:if>
     <!-- 搜索框  end -->
	</div>
    <div class="am-modal-bd">
	<div class="am-tabs" data-am-tabs id="tabs">
	  <ul class="am-tabs-nav am-nav am-nav-tabs">
	  <c:if test="${not empty treeDialog}">
	    <li class="am-active"><a href="treeTab"> 树查找</a></li>
	   </c:if>
	   <c:if test="${not empty listDialog}">
	    <li><a href="#dataListTab">查找结果</a></li>
	   </c:if>
	   <li><a href="#returnDataTab">已选({{returnData.length}})</a></li>
	  </ul>
	  <div class="am-tabs-bd">
	  <!-- 树形 -->
	   <c:if test="${not empty treeDialog}">
		  <div class="am-tab-panel am-fade am-in am-active" id="treeTab"> 
	    	<div class="am-g am-form" style="min-height: 400px">
	        	<ul id="customerTree" class="ztree" ></ul>
	        </div>
		  </div>
	   </c:if>
  	 	<!-- 表格数据  -->
  	 	<c:if test="${not empty listDialog}">
		  <div class="am-tab-panel am-fade" id="dataListTab">
				<table class="am-table am-table-striped am-table-hover">
		         <thead>
		           <tr style="text-align:center">
		             <th class="table-check" width="12px">　</th>
					<c:forEach items="${listDialog.displayList}" var="field" varStatus="status">
		           		<th <c:if test="${status.index > 1}">class="am-hide-sm-only"</c:if> style="text-align: center !important;">${field.comment }</th>
		           	</c:forEach>	             
		          	 </tr>
		       </thead> 
		       <tbody>
		         <tr ng-repeat="data in dataList" ng-click="selectOne($index)">
		           <td><input type="checkbox" ng-model="data.checked" /></td>
		           <c:forEach items="${listDialog.displayList}" var="field" varStatus="status">
			           <c:if test="${status.index == 0}">
							<c:set var="displayName" value="${field.fieldName}"/>
						</c:if>
		           	<td <c:if test="${status.index > 1}">class="am-hide-sm-only"</c:if>>{{data.${field.fieldName}}}</td>
		           </c:forEach> 
		         </tr>
		         </tbody>
		      </table>
	        <c:if test="${listDialog.needpage==1 }">
		 		<ul class="am-pagination">
				  <li><a href="" class="disabled"  ng-click="prewPage()"><span class="am-icon-angle-double-left"></span></a></li>
				  <li><span>{{pageParam.p}}<!-- <input class="am-input-sm" size="1" ng-model="pageParam.page"> -->/{{pageParam.pageCount}}页</span></li>
				  <li><a href="" ng-click="nextPage()"><span class="am-icon-angle-double-right"></span></a></li>
				  <li>每页条数</li>
				  <li>
			  	<select ng-model="pageParam.z" ng-change="reload()"> 
			  		<option value="5">5</option>
			  		<option value="10">10</option>
			  	</select>
			  </li>
			   <li>共{{pageParam.count}}条</li>
				</ul>
			</c:if>
		  </div>
		  </c:if>
		 <!-- 表格数据 end-->
		  <div class="am-tab-panel am-fade" id="returnDataTab">
		    	<div class="am-u-sm-12" style="min-height: 400px">
					<span ng-repeat="data in returnData" >
						<button style="margin: 4px" class="am-btn am-btn-secondary  fa-remove" title="移除该项" ng-click="unChoiceOne($index)">{{data.${displayName}}}</button>
					</span>
				</div>
	     </div>
	  	</div>
      </div>
	</div>
  <div class="am-modal-footer am-topbar-fixed-bottom">
	<div class="am-g">
	  <div class="am-u-sm-9 am-u-sm-centered" style="text-align: center;">
	    <button type="button" class="am-btn  am-radius am-btn-danger" ng-click="closeDialog()">取消</button>
   		<button type="button" class="am-btn  am-radius am-btn-warning" ng-click="cleanSelect()">清除</button>
   		<button type="button" class="am-btn  am-radius am-btn-primary" ng-click="dialogOk()">确定</button>　
   	  </div>
	</div>
 	</div>
</body>