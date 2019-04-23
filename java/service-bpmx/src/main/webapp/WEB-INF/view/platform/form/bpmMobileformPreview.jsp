<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html ng-app="startFlow">
<head>
<meta charset="UTF-8">
<title>表单预览</title>
<script src="${pageContext.request.contextPath}/weixin/assets/js/importJs.js"></script>
<script type="text/javascript">

importCss(["/weixin/assets/js/ztree/css/zTreeStyle/zTreeStyle.css",
           "/weixin/assets/js/ztree/css/outLook.css",
           "/weixin/assets/css/jquery.qtip.css",
           "/weixin/assets/css/mobiscroll.custom-2.5.2.min.css",
           "/weixin/assets/css/form.css",
           "/weixin/assets/css/formValid.css"]);

importJs(["/weixin/assets/js/angular.min.js",
          "/weixin/assets/js/zh_CN.js",
          "/weixin/assets/js/BaseService.js",
          "/weixin/assets/js/CustomValid.js",
          "/weixin/assets/js/mobiscroll.custom-2.5.2.min.js",
          "/js/hotent/platform/form/rule.js",
          "/weixin/assets/js/jquery.qtip.js",
          "/weixin/assets/js/ztree/js/jquery.ztree.core-3.5.js",
          "/weixin/assets/js/ztree/ZtreeCreator.js",
          "/weixin/assets/js/dialog/Dialogs.js",
          "/weixin/assets/js/formDirective.js",
          "/weixin/assets/js/bpm/FlowService.js",
          "/weixin/assets/js/bpm/FormUtil.js",
          "/weixin/assets/js/bpm/FormService.js",
          "/weixin/assets/js/bpm/formPreviewController.js",
          "/weixin/assets/editor/ueditor.config.js",
          "/weixin/assets/editor/ueditor.all.min.js"]);
          

</script>
<style type="text/css">
.am-form-group span{
border:none;
}
</style>
</head>
<body ng-controller="ctrl">
<header data-am-widget="header" class="am-header am-header-default am-header-fixed">
	<div class="am-header-left am-header-nav">
	</div>
	<h1 class="am-header-title">
			<a href="#" class="" data-am-modal="{target: '#my-popup'}" id="columnTitle" >查看数据 </a>
	</h1>
</header>
<div id="formHtml" ht-bind-html="formHtml" style="width: 100%;height:100%;overflow:auto;"></div>
<div id="formMsg">
<input type="hidden" id="tableId" value="${formDef.tableId}">
<textarea id="formTemplate" style="display: none;">${formDef.formHtml}</textarea>
</div>
<div data-am-widget="navbar" class="am-navbar am-cf am-navbar-default">
	<ul class="am-navbar-nav am-cf am-avg-sm-4">
		<li>
			<a href="#" >
			 <i class="am-icon fa fa-check" ></i>
			 	<del><span class="am-navbar-label">提交</span></del>
				</a>
			</li>
		<li>
			<a href="#" class="" > 
				<i class="am-icon fa fa-save" ></i> 
			 	<del><span class="am-navbar-label">保存</span></del>
			</a>
		</li>
		<li>
			<a href="#" class=""> 
				<i class="am-icon fa fa-picture-o" ></i> 
			 	<del><span class="am-navbar-label">流程图</span></del>
			</a>
		</li>
	</ul>
</div>



<div class="am-popup" id="my-popup">
  <div class="am-popup-inner">
    <div class="am-popup-hd">
      <h4 class="am-popup-title">数据预览</h4>
      <span data-am-modal-close
            class="am-close">&times;</span>
    </div>
    <div class="am-popup-bd">
     	 主表数据：
       <br/>
      ${data.main}
      <br/>
      <br/>
      	子表数据：
       <br/>
      ${data.sub}
    </div>
  </div>
</div>
</body>
</html>