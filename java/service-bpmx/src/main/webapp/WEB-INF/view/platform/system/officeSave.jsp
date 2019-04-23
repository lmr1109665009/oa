<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
    window.onload=function () {
        var parent=this.parent;
        if (!parent){
            return;
        }
        <c:choose>
        <c:when test="${hasError==true}">
        parent.$.ligerDialog.warn("${msg}");
        </c:when>
        <c:otherwise>
        parent.$.ligerDialog.confirm("${msg}","提示信息", function(rtn) {
            parent.location.href = "${ctx}/platform/system/office/list.ht";
        });
        </c:otherwise>
        </c:choose>
    };
</script>