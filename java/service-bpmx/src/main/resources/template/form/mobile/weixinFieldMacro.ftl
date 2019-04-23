<#--字段宏-->
<#setting number_format="0">

<#macro input field type tableName >
		<#if field.fieldType == "varchar"><#---字符串类型-->
			<#switch field.controlType>
				<#case 1><#--单行文本框-->
					 <input ht-input="${getFieldName(field,type)}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" placeholder="${field.fieldDesc}" class="am-form-field" />
				<#break>
				<#case 2><#--多行文本框-->
					<textarea ht-textarea="${getFieldName(field,type)}" ng-model="${getFieldName(field,type)}"  ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class="am-form-field" placeholder="${field.fieldDesc}"></textarea>		
				<#break>
				<#case 3><#--数据字典-->
					<span ht-dic="${getFieldName(field,type)}" dictype="${field.dictType}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class="am-form-field" > </span>
				<#break>
				<#case 4><#--人员选择器(单选)-->
					 <span ht-selector="${getFieldName(field,type)}" selectorConfig="{type:'UserDialog',display:'fullname', single:true,bind:{userId:'${getFieldName(field,type)}ID',fullname:'${getFieldName(field,type)}'},scope:${field.getPropertyMap().scope}}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class="am-form-field"> </span>
				<#break>
				<#case 5><#--角色选择器(多选)-->
				<#break>
				<#case 6><#--组织选择器(多选)-->
					 <span ht-selector='${getFieldName(field,type)}' selectorConfig="{type:'OrgDialog',display:'orgName', single:false,bind:{orgId:'${getFieldName(field,type)}ID',orgName:'${getFieldName(field,type)}'},scope:${field.getPropertyMap().scope}}" ng-model="${getFieldName(field,type)}" ht-validate='${getValidRule(field)}' permission="${getPermission(field,type,tableName)}" class='am-form-field'> </span>
				<#break>
				<#case 7><#--岗位选择器(多选)-->
				<span ht-selector="${getFieldName(field,type)}" selectorConfig="{type:'PositionDialog',display:'posName', single:false,bind:{posId:'${getFieldName(field,type)}ID',posName:'${getFieldName(field,type)}'},scope:${field.getPropertyMap().scope}}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class="am-form-field"> </span>
                 <#break>
				<#case 8><#--人员选择器(多选)-->
					 <span ht-selector='${getFieldName(field,type)}' selectorConfig="{type:'UserDialog',display:'fullname', single:false,bind:{userId:'${getFieldName(field,type)}ID',fullname:'${getFieldName(field,type)}'},scope:${field.getPropertyMap().scope}}" ng-model='${getFieldName(field,type)}' ht-validate='${getValidRule(field)}' permission="${getPermission(field,type,tableName)}"  class='am-form-field'> </span>
				<#break>
				<#case 9><#--文件上传-->
					 <span ht-upload="${getFieldName(field,type)}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class="am-form-field"></span>
				<#break>
				<#case 10><#--富文本框ueditor-->
					 <span ht-editor="${getFieldName(field,type)}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class="am-form-field"> </span>
				<#break>
				<#case 11><#--下拉选项-->
					<select ht-select="${getFieldName(field,type)}"  data-am-selected="{btnStyle:'success'}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class="am-form-field">
						<option value=''>请选择</option>
						<#list field.aryOptions?keys as optkey>
							<option value="${optkey}">${field.aryOptions[optkey]}</option>
						</#list>
					</select>
				<#break>
				<#case 12><#--Office控件-->
				<#break>
				<#case 13><#--复选框-->
					<div class="am-checkbox" ht-checkboxs="${getFieldName(field,type)}"  values="${getOptionArrs(field)}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}">
						<!-- 此checkbox只做展示使用-->
						<#list field.aryOptions?keys as optkey>
							<div class="am-checkbox-inline"> <input type="checkbox" value="${optkey}">${field.aryOptions[optkey]}</div>
						</#list>
					</div>
				<#break>
				<#case 14><#--单选按钮-->
				<div ht-radios="${getFieldName(field,type)}" values="${getOptionArrs(field)}" ng-model="${getFieldName(field,type)}"  ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}">
			      	<!-- 此radio只做展示使用-->
					<#list field.aryOptions?keys as optkey>
						<div class="am-radio-inline"> <input type="radio"  value="${optkey}">${field.aryOptions[optkey]}</div>
					</#list>
			      	</div>
				<#break>
				<#case 15><#--日期控件-->
					<input  ht-date="${getFieldName(field,type)}" options="{format:'<#if (field.getPropertyMap().format==null)>yyyy-MM-dd<#else>${field.getPropertyMap().format}</#if>'}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class="am-form-field"/>
				<#break>
				<#case 16><#--隐藏域-->
				<#break>
				<#case 17><#--角色选择器(单选)-->
               	<#break>
				<#case 18><#---组织选择器(单选)-->
					 <span ht-selector='${getFieldName(field,type)}' selectorConfig="{type:'OrgDialog',display:'orgName', single:true,bind:{orgId:'${getFieldName(field,type)}ID',orgName:'${getFieldName(field,type)}'},scope:${field.getPropertyMap().scope}}" ng-model='${getFieldName(field,type)}' ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class='am-form-field' ></span>
				<#break>
				<#case 19><#--岗位选择器(单选)-->
				<span ht-selector="${getFieldName(field,type)}" selectorConfig="{type:'PositionDialog',display:'posName', single:true,bind:{posId:'${getFieldName(field,type)}ID',posName:'${getFieldName(field,type)}'},scope:${field.getPropertyMap().scope}}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class="am-form-field"> </span>
                 <#break>
				<#case 20><#--流程引用-->

				<#break>

				<#case 24><#--HTML解析器/多行文本框(HTML)-->
				<textarea ht-htmledittable="${getFieldName(field,type)}" ng-model="${getFieldName(field,type)}"  ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class="am-form-field" placeholder="${field.fieldDesc}"></textarea>
				<#break>
				<#case 25><#--公司选择器-->
				<span ht-selector='${getFieldName(field,type)}' selectorConfig="{type:'OrgDialog',display:'orgName', single:true,bind:{orgId:'${getFieldName(field,type)}ID',orgName:'${getFieldName(field,type)}'},scope:${field.getPropertyMap().scope}}" ng-model='${getFieldName(field,type)}' ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class='am-form-field' ></span>
				<#break>
			</#switch>
		<#elseif field.fieldType == "number"><#---数字类型-->
			<#if  field.controlType == 16><#--隐藏域-->
			<#else><#--否则数字输入-->
				 <input ht-formator='${field.ctlProperty}' ht-input="${getFieldName(field,type)}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" placeholder="${field.fieldDesc}" class="am-form-field" />
			</#if>
		<#elseif field.fieldType == "date"><#---日期类型-->
			<#if  field.controlType == 16><#--隐藏域-->
			<#else>
				<input  ht-date="${getFieldName(field,type)}" options="{format:'<#if (field.getPropertyMap().format==null)>yyyy-MM-dd<#else>${field.getPropertyMap().format}</#if>'}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class="am-form-field"/>
			</#if>
		<#else>
			<#if  field.controlType == 16><#---隐藏域-->
			<#elseif field.controlType == 10><#--富文本框ckeditor-->
				<div ht-editor="${getFieldName(field,type)}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}"></div>
			<#else><#--否则多文本框-->
				<textarea ht-textarea="${getFieldName(field,type)}" ng-model="${getFieldName(field,type)}" ht-validate="${getValidRule(field)}" permission="${getPermission(field,type,tableName)}" class="am-form-field" placeholder="${field.fieldDesc}" ></textarea>		
			</#if>
		</#if>
</#macro>

<#function getOptionArrs field>
	<#assign rtn>[<#list field.aryOptions?keys as optkey> {'val':'${optkey}','text':'${field.aryOptions[optkey]}'}<#if optkey_has_next>,</#if></#list>]</#assign><#return rtn>
</#function>
<#--获取字段名字。 1为主表。2为子表。其他则为弹出框模式子表的字段model type：子表名-->
<#function getFieldName field type>
	<#assign rtn="" />
	<#if  type == 1>
		<#assign rtn>data.main.${field.fieldName?lower_case}</#assign>
	<#elseif type== 2>
		<#assign rtn>item.${field.fieldName?lower_case}</#assign>
	<#else>
		<#assign rtn>subTempData.${type}.${field.fieldName?lower_case}</#assign>
	</#if>
	<#return rtn>
</#function>



<#function getPermission field type tableName>
	<#assign rtn="" />
	<#if  type == 1>
		<#assign rtn>{{main.${field.fieldName?lower_case}}}</#assign>
	<#elseif type== 2>
		<#assign rtn>{{fields.${tableName}.${field.fieldName?lower_case}}}</#assign>
	<#else>
		<#assign rtn>{{fields.${type}.${field.fieldName?lower_case}}}</#assign>
	</#if>
	<#return rtn>
</#function>

<#-- 根据字段获取验证规则  -->
<#function getValidRule field>
	<#assign rtn="" />
	<#if field.isRequired == 1>
		<#assign rtn >${rtn}required:true,</#assign>
	</#if>
	<#if field.fieldType == "number">
		<#assign rtn >${rtn}number:true,maxIntLen:${field.intLen},maxDecimalLen:${field.decimalLen},</#assign>
	<#elseif  (field.charLen??) && (field.charLen?length>0)&&((field.controlType?? && (field.controlType==1))) && field.fieldType != "date" >
		<#assign rtn >${rtn}maxlength:${field.charLen},</#assign>
	</#if>
	
	<#if  (field.validRule??) && (field.validRule?length>0) >
		<#assign rtn >${rtn}${field.validRule}:true,</#assign>
	</#if>
	
	<#if rtn!="">
		<#assign rtn>{${rtn?substring(0,rtn?length-1)}}</#assign>
	</#if>
	<#if rtn=="">
		<#assign rtn>{}</#assign>
	</#if>
	<#return rtn>
</#function>

<#-- 转小写  -->
<#function toLower str>
 	<#assign rtn>${str?lower_case}</#assign>
	<#return rtn>
</#function>