<#if opinion.opinion??> ${opinion.opinion}<br/><#else><br/></#if>
<#if opinion.webSignUrl??>
<img class="websign-img" src="{{staticUrl}}${opinion.webSignUrl}">
<#else>
    <#if opinion.status??&&opinion.superExecution??>
    ${opinion.superExecution}
    </#if>
    <#if opinion.sysOrg??&&opinion.sysOrg.orgName??>${opinion.sysOrg.orgName}/</#if>${opinion.exeFullname}</#if>${opinion.createtime?string("yyyy-MM-dd HH:mm:ss")}


<br/>