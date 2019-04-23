 <#setting number_format="0">
 <div type="subtable" tablename="${subTable.tableName?lower_case}" ng-if="!permission.table.${toLower(subTable.tableName)}.hidden">
	<div> <a class="am-btn am-btn-primary" ng-click="addRow('${toLower(subTable.tableName)}')" ng-if="permission.table.${toLower(subTable.tableName)}.add">添加${subTable.tableDesc}</a> </div>
	<div ng-repeat="item in data.sub.${subTable.tableName?lower_case}.rows" class="am-scrollable-horizontal">
        <table class="am-table am-table-bordered am-table-striped">
			<thead>
                  <tr class="ub ub-f1 am-primary">
                      <th colspan="2" style="text-align: center;">${subTable.tableDesc}<div style="float: right;"><a class="am-btn am-btn-danger am-btn-xs" ng-click="removeRow(&#39;${subTable.tableName?lower_case}&#39;,$index)" ng-if="permission.table.${subTable.tableName?lower_case}.del"><em class="am-icon-remove"></em>删除</a></div>
                       </th>
                  </tr>
              </thead>
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
		</table>
	 </div>
 </div>
 

<#function getFieldList fieldList tableName>
 	<#assign rtn>
 		<#list fieldList as field>
			<#if  field.isHidden == 0>
			<tr ng-if="permission.fields.${toLower(tableName)}.${toLower(field.fieldName)}!='n'">
				<th>${field.fieldDesc}：</th>
				<td><@input field=field type=2 tableName=toLower(tableName)/></td>
			</tr>
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

<#function toLower str>
 	<#assign rtn>${str?lower_case}</#assign>
	<#return rtn>
</#function>