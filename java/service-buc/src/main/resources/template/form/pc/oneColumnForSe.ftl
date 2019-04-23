<script>
function handRowEvent(ev,table){
		$("td.tdNo",table).each(function(i){
			$(this).text(i+1);
		});
	}
</script>
<#setting number_format="0">
<#function getFieldList fieldList>
 	<#assign rtn>
		<#list fieldList as field>
			<#if  field.isHidden == 0>
				<tr>
					<td class="Tsche_left" valign="middle"  nowrap="nowarp"  >${field.fieldDesc}：</td>
					<td align="left" valign="middle">
						<@input field=field/>
					</td>
				</tr>
			</#if>
		</#list>
 	</#assign>
	<#return rtn>
</#function>
<#function setTeamField teams>
 	<#assign rtn>
		 <#list teams as team>
				
				${getFieldList(team.teamFields)}
		</#list>
	</#assign>
	<#return rtn>
</#function>

<div class="tank_title">${table.tableDesc }</div>
<div class="TKmain_site">
	<div class="Tdetail_cont">
<table width="100%" cellpadding="0" cellspacing="0"  parser="addpermission">
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
</div>
</div>
<br />
