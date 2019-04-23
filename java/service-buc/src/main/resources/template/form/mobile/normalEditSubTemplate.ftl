 <#setting number_format="0">
 <div type="subtable" tablename="${subTable.tableName?lower_case}" ng-if="!permission.table.${toLower(subTable.tableName)}.hidden">
	<div>${subTable.tableDesc} <a href="javascript:;" ng-click="addRow('${toLower(subTable.tableName)}')" ng-if="permission.table.${toLower(subTable.tableName)}.add">添加</a> </div>
	<div class="am-scrollable-horizontal">
        <table class="am-table am-table-bordered am-table-striped am-text-nowrap">
			<thead>
            <tr class="ub ub-f1">
			<#if teamFields??>
				<#if isShow>
					<#if showPosition == 1>
						${getTeamTitle(teamFields,subTable.tableName)}
						${getFieldTitle(fields,subTable.tableName)}
					<#else>
						${getFieldTitle(fields,subTable.tableName)}
						${getTeamTitle(teamFields,subTable.tableName)}
					</#if>
				<#else>
					${getTeamTitle(teamFields,subTable.tableName)}
				</#if>
			<#else>
				${getFieldTitle(fields,subTable.tableName)}
			</#if>
				<th ng-if="permission.table.${toLower(subTable.tableName)}.del">管理</th>
			</tr>
			</thead>
			 <tr ng-repeat="item in data.sub.${subTable.tableName?lower_case}.rows">
			 <#if teamFields??>
				<#if isShow>
					<#if showPosition == 1>
						${getTeamField(teamFields,subTable.tableName)}
						${getFieldList(fields,subTable.tableName)}
					<#else>
						${getFieldList(fields,subTable.tableName)}
						${getTeamField(teamFields,subTable.tableName)}
					</#if>
				<#else>
					${getTeamField(teamFields,subTable.tableName)}
				</#if>
			<#else>
				${getFieldList(fields,subTable.tableName)}
			</#if>
				<td ng-if="permission.table.${toLower(subTable.tableName)}.del">
				<a class="am-btn am-btn-danger am-btn-xs" ng-click="removeRow('${toLower(subTable.tableName)}',$index)"
				 ng-if="permission.table.${toLower(subTable.tableName)}.del"><i class="am-icon-remove"></i>删除</a>
				</td>
			 </tr>
		</table>
	 </div>
 </div>
 

<#function getFieldList fieldList tableName>
 	<#assign rtn>
 		<#list fieldList as field>
			<#if  field.isHidden == 0>
			<td ng-if="permission.fields.${toLower(tableName)}.${toLower(field.fieldName)}!='n'">
				<@input field=field type=2 tableName=toLower(tableName)/>
			</td>
			</#if>
		</#list>
 	</#assign>
	<#return rtn>
</#function>

<#--获取分组信息 -->
<#function getTeamField teams tableName>
 	<#assign rtn>
		 <#list teams as team>
			${getFieldList(team.teamFields,tableName)}
		</#list>
	</#assign>
	<#return rtn>
</#function>
<#--获取分组信息 -->
<#function getTeamTitle teams tableName>
 	<#assign rtn>
		 <#list teams as team>
			${getFieldTitle(team.teamFields,tableName)}
		</#list>
	</#assign>
	<#return rtn>
</#function>
<#--获取字段th-->
<#function getFieldTitle fieldList tableName>
 	<#assign rtn>
		<#list fieldList as field>
			<#if field.isHidden == 0>
				<th ng-if="permission.fields.${toLower(tableName)}.${field.fieldName}!='n'">${field.fieldDesc}</th>
			</#if>
		</#list>
 	</#assign>
	<#return rtn>
</#function>

