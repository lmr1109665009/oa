<#setting number_format="0">
<#function getFieldList fieldList>
 	<#assign rtn>
		<#list fieldList as field>
			<#if field.isHidden == 0>
				 <tr>
					<th>${field.fieldDesc}：</th>
					<td><@input field=field type=1 tableName='main'/> </td> 
				 </tr>
			</#if>
		</#list>
 	</#assign>
	<#return rtn>
</#function>

<#--获取分组信息 -->
<#function setTeamField teams>
 	<#assign rtn>
		 <#list teams as team>
		 	<#if team.teamName?if_exists>
            	 <tr>
            	 	<th colspan="2" class="grouptitle">${team.teamName}</th>
            	 </tr>
			</#if>
				${getFieldList(team.teamFields)}
		</#list>
	</#assign>
	<#return rtn>
</#function>

<header data-am-widget="header" class="am-header am-header-default am-header-fixed"><div class="burger"><i></i><i></i><i></i></div>	<h1 class="am-header-title"><span>${title}</span></h1></header>
<table class="am-table am-table-striped">
<#--设置主表分组-->
<#if teamFields??>
	<#if isShow>
		<#if showPosition == 1>
			${setTeamField(teamFields)}
			${getFieldList(fields)}
		<#else>
			${getFieldList(fields)}
			${setTeamField(teamFields)}
		</#if>
	<#else>
		${setTeamField(teamFields)}
	</#if>
<#else>
		${getFieldList(fields)}
</#if>
</table>
 