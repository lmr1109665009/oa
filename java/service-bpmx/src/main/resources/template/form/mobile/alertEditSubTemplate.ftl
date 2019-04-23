 <#setting number_format="0">
 <div type="subtable" readonly="readonly" tablename="${subTable.tableName?lower_case}" tableDesc="${subTable.tableDesc}" ng-if="!permission.table.${toLower(subTable.tableName)}.hidden">
	<div>${subTable.tableDesc} <a href="javascript:;" ng-click="editRow('${toLower(subTable.tableName)}')" ng-if="permission.table.${toLower(subTable.tableName)}.add">添加</a> </div>
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
				<th ng-if="permission.table.${toLower(subTable.tableName)}.del || permission.table.${toLower(subTable.tableName)}.add">管理</th>
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
				<td ng-if="permission.table.${toLower(subTable.tableName)}.del || permission.table.${toLower(subTable.tableName)}.add">
				    <a class="am-btn am-btn-danger am-btn-xs" ng-click="removeRow('${toLower(subTable.tableName)}',$index)"
				    	ng-if="permission.table.${toLower(subTable.tableName)}.del"><i class="am-icon-remove"></i>删除</a>
					<a class="am-btn am-btn-primary am-btn-xs" ng-click="editRow('${toLower(subTable.tableName)}',$index)"
						ng-if="permission.table.${toLower(subTable.tableName)}.add"><i class="am-icon-edit"></i>编辑</a>
				</td>
			 </tr>
		</table>
	 </div>
 </div>
 
 <div class="am-popup" id="${toLower(subTable.tableName)}_editDialog">
  <div class="am-popup-inner">
    <div class="am-popup-hd">
      <h4 class="am-popup-title">${subTable.tableDesc}</h4>
      <span data-am-modal-close 
            class="am-close" onclick="javascript:$('#${toLower(subTable.tableName)}_editDialog').modal('close')">×</span>
    </div>
    <div class="am-popup-bd">
      	<form class="am-form am-form-horizontal" id="${toLower(subTable.tableName)}_editDialogForm">
		<#if teamFields??>
			<#if isShow>
				<#if showPosition == 1>
					${getAlertTeamField(teamFields,subTable.tableName)}
					${getAlertFieldList(fields,subTable.tableName)}
				<#else>
					${getAlertFieldList(fields,subTable.tableName)}
					${getAlertTeamField(teamFields,subTable.tableName)}
				</#if>
			<#else>
				${getAlertTeamField(teamFields,subTable.tableName)}
			</#if>
		<#else>
			${getAlertFieldList(fields,subTable.tableName)}
		</#if>
	 	</form>
    </div>
  </div>
	<div class="am-modal-footer am-topbar-fixed-bottom">
        <div class="am-g">
            <div class="am-u-sm-11 am-u-sm-centered" style="text-align:center;">
                <button type="button" class="am-btn  am-radius am-btn-danger" onclick="javascript:$('#${toLower(subTable.tableName)}_editDialog').modal('close')">取消</button>
                <button type="button" class="am-btn  am-radius am-btn-warning" ng-click="cleansubTempData('${toLower(subTable.tableName)}')">清除</button>
                <button type="button" class="am-btn  am-radius am-btn-primary" ng-click="pushTempDataToForm('${toLower(subTable.tableName)}',subTempData.${toLower(subTable.tableName)}.$index)">确定</button>
            </div>
        </div>
    </div>
</div>

<#function getFieldList fieldList,tableName>
 	<#assign rtn>
 		<#list fieldList as field>
			<#if  field.isHidden == 0>
			<td ng-if="permission.fields.${toLower(tableName)}.${field.fieldName}!='n'">
				<@input field=field type=2 tableName=toLower(tableName) />
			</td>
			</#if>
		</#list>
 	</#assign>
	<#return rtn>
</#function>

<#--获取分组信息 -->
<#function getTeamField teams,tableName>
 	<#assign rtn>
		 <#list teams as team>
			${getFieldList(team.teamFields,tableName)}
		</#list>
	</#assign>
	<#return rtn>
</#function>
<#--获取分组信息 -->
<#function getTeamTitle teams,tableName>
 	<#assign rtn>
		 <#list teams as team>
			${getFieldTitle(team.teamFields,tableName)}
		</#list>
	</#assign>
	<#return rtn>
</#function>
<#--获取字段th-->
<#function getFieldTitle fieldList,tableName>
 	<#assign rtn>
		<#list fieldList as field>
			<#if field.isHidden == 0>
				<th ng-if="permission.fields.${toLower(tableName)}.${toLower(field.fieldName)}!='n'">${field.fieldDesc}</th>
			</#if>
		</#list>
 	</#assign>
	<#return rtn>
</#function>

<#--下面是获取子表弹出框的列表信息 -->
<#function getAlertFieldList fieldList,tableName>
 	<#assign rtn>
		<#list fieldList as field>
			<#if field.isHidden == 0>
				 <div class="am-form-group" ng-if="permission.fields.${toLower(tableName)}.${field.fieldName}!='n'">
					<label class="am-u-sm-3 am-form-label">${field.fieldDesc}</label>
					<div class="am-u-sm-9"><@input field=field type=tableName tableName=toLower(tableName)/> </div>
				 </div>
			</#if>
		</#list>
 	</#assign>
	<#return rtn>
</#function>

<#function getAlertTeamField teams,tableName>
 	<#assign rtn>
		 <#list teams as team>
		 	<#if team.teamName?if_exists>
            	<fieldset>
					<legend> ${team.teamName}</legend>
			</#if>
				${getAlertFieldList(team.teamFields,tableName)}
		 	<#if team.teamName?if_exists>
		 	  	</fieldset>
		 	</#if>
		</#list>
	</#assign>
	<#return rtn>
</#function>

