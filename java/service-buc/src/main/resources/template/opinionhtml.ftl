<#if opinion.opinion??> ${opinion.opinion}<br/><#else>无<br/></#if>
<#if opinion.webSignUrl??>
<img class="websign-img" src="{{staticUrl}}${opinion.webSignUrl}">
<#else>
    <#if opinion.sysOrg??&&opinion.sysOrg.orgName??>
    ${opinion.sysOrg.orgName}/
    </#if>
${opinion.exeFullname}&nbsp;
</#if>
${opinion.endTime?string("yyyy-MM-dd HH:mm:ss")}<br/>
<br/>