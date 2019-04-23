<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="f" uri="http://www.jee-soft.cn/functions" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="hotent" uri="http://www.jee-soft.cn/paging" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<f:link href="web.css"></f:link>
<f:link href="jquery/plugins/rowOps.css"></f:link>
<f:link href="Aqua/css/ligerui-all.css"></f:link>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<f:link href="jqGrid/jquery-ui.css" ></f:link>
<f:link href="jqGrid/ui.jqgrid.css" ></f:link>
<f:js pre="js/lang/common" ></f:js>
<f:js pre="js/lang/js" ></f:js>
<script type="text/javascript" src="${ctx}/js/dynamic.jsp"></script>
<script type="text/javascript" src="${ctx}/js/jquery/jquery.js"></script>
<script type="text/javascript" src="${ctx}/js/jqGrid/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="${ctx}/js/jqGrid/jquery.jqGrid.src.js"></script>
<script type="text/javascript" src="${ctx}/js/jquery/plugins/jquery.rowOps.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/foldBoxJqGrid.js" ></script>
<script type="text/javascript" src="${ctx}/js/hotent/absoulteInTop.js" ></script>
<script type="text/javascript" src="${ctx}/js/util/util.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/sqlquery/grid.js"></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/CommonDialog.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/ligerui.min.js"></script>
<script type="text/javascript" src="${ctx}/js/lg/plugins/ligerDialog.js" ></script>
<script type="text/javascript" src="${ctx}/js/lg/util/DialogUtil.js" ></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/system/SysDialog.js" ></script>
<script type="text/javascript" src="${ctx}/js/hotent/platform/form/SelectorUtil.js" ></script>
<script type="text/javascript" src="${ctx}/js/calendar/My97DatePicker/WdatePicker.js"></script>
