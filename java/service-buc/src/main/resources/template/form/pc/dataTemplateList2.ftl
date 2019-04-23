<#-- 
Name: 数据列表模板
Desc:数据列表模板

本模板需要通过2次解析才能得到最终的Html
第一次解析：
*************************************************************
*************************************************************
*数据模型:****************************************************
*************************************************************
*************************************************************

tbarTitle：Tool Bar 的标题

********************************************
conditionFields:条件字段
--joinType：	条件联合类型
--name：	列名
--name：完全指定名
--operate：条件类型: =|>=|<=|….
--comment：注释
--type：	类型
--value：值
--valueFrom：值来源

************************************************************
displayFields：显示字段
--name：列名
--name：完全指定名
--label：别名
--index：索引
--comment：注释
--type：类型

******************************************************
tableIdCode:Table ID Code

**************************************************
displayId: 自定义显示的ID

**************************************************
pageHtml：分页的Html 详见pageAjax.xml

*************************************************
pageURL：当前页面的URL

searchFormURL：搜索表单的Action


sortField：当前排序字段

orderSeq：当前的排序类型

***********************************************
pkcols:主键列

deleteBaseURL：删除一行数据的BaseURL
editBaseURL：编辑一行数据的BaseURL
 -->


<#setting number_format="#">
<#assign displayFields=bpmDataTemplate.displayField?eval>
<#assign conditionFields=bpmDataTemplate.conditionField?eval>
<#assign filterFields=bpmDataTemplate.filterField?eval>
<#assign manageFields=bpmDataTemplate.manageField?eval>

<#noparse>
<#setting number_format="#">
<#assign displayFields=bpmDataTemplate.displayField?eval>
<#assign conditionFields=bpmDataTemplate.conditionField?eval>
<#assign filterFields=bpmDataTemplate.filterField?eval>
<#assign manageFields=bpmDataTemplate.manageField?eval>
</#noparse>
<#--日期选择器 -->
<#macro genQueryDate field>
		<#switch field.qt>
			<#case "D">
				<input type="text" name="Q_${field.na}_D" readonly="readonly"  class="wdateTime inputText"  />
			<#break>
			<#case "DL">
				<input type="text" name="Q_${field.na}_DL" readonly="readonly"  class="wdateTime inputText"  />
			<#break>
			<#case "DG">
				<input type="text" name="Q_${field.na}_DG"  readonly="readonly" class="wdateTime inputText"   />
			<#break>
			<#case "DR">
				从:
				<input type="text" name="Q_begin${field.na}_DL" readonly="readonly" class="wdateTime inputText"  />
				到:
				<input type="text" name="Q_end${field.na}_DG" readonly="readonly" class="wdateTime inputText"  />
			<#break>
			<#default>
				<input type="text" name="Q_${field.na}_${field.qt}" class="inputText"  />
			<#break>
		</#switch>
</#macro>
<#--用户选择器 -->
<#macro genQuerySelector field>
		<#switch field.ct>
			<#case "4"><#--用户单选选择器 -->
				<input type="hidden" name="Q_${field.na}ID_L" class="inputText" id="${field.na}ID">
				<input type="text" readonly="readonly" class="inputText" name="Q_${field.na}~_SL" id="${field.na}">
				<input type="button" onclick="__Selector__.selectUser({self:this});" value="...">
			<#break>
			<#case "5"><#--角色多选选择器 -->
				<input  type="hidden" name="Q_${field.na}ID_L" id="${field.na}ID">
				<input type="text" readonly="readonly" class="inputText" name="Q_${field.na}~_SL" id="${field.na}">
				<input type="button" onclick="__Selector__.selectRole({self:this});" value="...">
			<#break>
			<#case "6"><#--组织多选选择器 -->
				<input  type="hidden" name="Q_${field.na}ID_L" id="${field.na}ID">
				<input type="text" readonly="readonly" class="inputText" name="Q_${field.na}~_SL" id="${field.na}">
				<input type="button" onclick="__Selector__.selectOrg({self:this});" value="...">
			<#break>
			<#case "7"><#--岗位多选选择器 -->
				<input  type="hidden" name="Q_${field.na}ID_L" id="${field.na}ID">
				<input type="text" readonly="readonly" class="inputText" name="Q_${field.na}~_SL" id="${field.na}">
				<input type="button" onclick="__Selector__.selectPos({self:this});" value="...">
			<#break>
			<#case "8"><#--人员多选选择器 -->
				<input  type="hidden" name="Q_${field.na}ID_L" id="${field.na}ID">
				<input type="text" readonly="readonly" class="inputText" name="Q_${field.na}~_SL" id="${field.na}">
				<input type="button" onclick="__Selector__.selectUser({self:this});" value="...">
			<#break>
			<#case "17"><#--角色单选选择器 -->
				<input  type="hidden" name="Q_${field.na}ID_L" id="${field.na}ID">
				<input type="text" readonly="readonly" class="inputText" name="Q_${field.na}~_SL" id="${field.na}">
				<input type="button" onclick="__Selector__.selectRole({self:this});" value="...">
			<#break>
			<#case "18"><#--组织单选选择器 -->
				<input  type="hidden" name="Q_${field.na}ID_L" id="${field.na}ID">
				<input type="text" readonly="readonly" class="inputText" name="Q_${field.na}~_SL" id="${field.na}">
				<input type="button" onclick="__Selector__.selectOrg({self:this});" value="...">
			<#break>
			<#case "19"><#--岗位单选选择器 -->
				<input  type="hidden" name="Q_${field.na}ID_L" id="${field.na}ID">
				<input type="text" readonly="readonly" class="inputText" name="Q_${field.na}~_SL" id="${field.na}">
				<input type="button" onclick="__Selector__.selectPos({self:this});" value="...">
			<#break>
			<#default>
				<input  type="hidden" name="Q_${field.na}ID_L" id="${field.na}ID">
				<input type="text" readonly="readonly" class="inputText" name="Q_${field.na}~_SL" id="${field.na}">
				<input type="button" onclick="__Selector__.selectUser({self:this});" value="...">
			<#break>
		</#switch>
</#macro>
<#macro genQuerySelect field formatData>
	<select  name="Q_${field.na}_${field.qt}">
		<option value="">全部</option>
	<#if formatData?if_exists >
		<#list formatData?keys as key>
		<option value="${key}">${formatData[key]}</option>
		</#list>
	</#if>
	</select>
</#macro>


<#--生成查询条件宏 -->
<#macro genCondition field formatData>
	<#if field.vf=="1" >
			<span class="label">${field.cm}:</span>		
		<#switch field.ct>
			<#case "1">
				<input type="text" name="Q_${field.na}_${field.qt}" class="inputText"  />
			<#break>
			<#case "4"><#--选择器 -->
			<#case "5"><#--选择器 -->
			<#case "6"><#--选择器 -->
			<#case "7"><#--选择器 -->
			<#case "8"><#--选择器 -->
			<#case "17"><#--选择器 -->
			<#case "18"><#--选择器 -->
			<#case "19"><#--选择器 -->
				<@genQuerySelector field=field/>
			<#break>
			<#case "15"><#--日期选择器 -->
				<@genQueryDate field=field/>
			<#break>
			<#case "11"><#--下拉选择器 -->
				<#if formatData[field.na]?if_exists >
					<@genQuerySelect field=field formatData=formatData[field.na]/>
				<#else>
					<input type="text" name="Q_${field.na}_${field.qt}" class="inputText"  />
				</#if>
			<#break>
			<#default>
				<input type="text" name="Q_${field.na}_${field.qt}" class="inputText"  />
			<#break>
		</#switch>
	</#if>
</#macro>
<#--过滤条件-->
<#noparse><#if filterFields?if_exists>
<div class="panel" ajax="ajax"  displayId="${bpmDataTemplate.id}" filterKey="${filterKey}" >
<div class='panel-nav'>
	<div class="l-tab-links">
		<ul style="left: 0px; ">
			<#list filterFields as field>
			<li tabid="${field.key}" <#if field.key ==filterKey> class="l-selected"</#if>>
				<a href="${field.url}" title="${field.name}">${field.desc}</a>
			</li>
			</#list>
		</ul>
	</div>
</div>
</#noparse> 
<#--查询头-->
	<div class="panel-top">
		<div class="tbar-title">
			<span class="tbar-label">${bpmDataTemplate.name}</span>
		</div>
			<div class="panel-toolbar">
				<div class="toolBar">
					<#if hasCondition>
						<div class="group"><a class="link ajaxSearch" href="javascript:;" onclick="handlerSearchAjax(this)"><span></span>查询</a></div>
						<div class="l-bar-separator"></div>
					</#if>
					<#noparse><#list manageFields as manage>
					${service.getToolBar('${manage.name}','${manage.desc}',actionUrl,managePermission)}
					</#list></#noparse>
				</div>
			</div>
		<#if hasCondition >
			<div class="panel-search">
				<form id="searchForm" name="searchForm" method="post" action="<#noparse>${searchFormURL}</#noparse>">
				<#--查询条件-->
					<#if conditionFields?if_exists>
						<#list conditionFields as field>
							<#if (field_index)%3==0><ul class="row"></#if>
								<@genCondition field=field formatData=formatData/>
							<#if (field_index+4)%3==0|| (field_index+1)==(conditionFields?size)></ul></#if>
						</#list>
					</#if>
				</form>
			</div>
		</#if>
	</div>
<#--表头-->	
	<div class="panel-body">
			<table cellpadding="1" cellspacing="1" class="table-grid table-list">
				<tr>
					<#noparse>
					<#if checkbox>
					    <th>
	             		 <input id="chkall" type="checkbox">
	              		</th>
              		</#if>
              		</#noparse>
					<#list displayFields as field>
							<#noparse><#assign name="</#noparse>${field.name}<#noparse>"></#noparse>
				 			<#noparse><#assign desc="</#noparse>${field.desc}<#noparse>"></#noparse>
							<#if field.type!="clob">
									<#noparse>${service.getShowField('${name}','${desc}','${pageURL}&${tableIdCode}__ns__=${name}',sort,permission)}</#noparse>
							<#else>
									<#noparse>${service.getShowField('${name}','<th>${desc}</th>',permission)}</#noparse>
							</#if>
					</#list>
					<#if hasManage>
						<th width="150px">管理</th>
					</#if>
				</tr>
<#--表体-->				
				<#noparse><#list bpmDataTemplate.list as data></#noparse>
					<tr class="<#noparse><#if data_index % 2 == 0>odd</#if><#if data_index % 2 == 1>even</#if></#noparse>">
					<#noparse>
					<#if checkbox>
					</#noparse>
					    <td style="width:30px;">
                      	  <input class="pk" type="checkbox" value="<#noparse>${data.</#noparse>${pkField}<#noparse>}</#noparse>" name="${pkField}">
                        </td>
                    <#noparse>
                    </#if>
              		</#noparse>
						<#list displayFields as field>
							<#noparse><#assign showName="</#noparse>${field.name}<#noparse>"></#noparse>
							<#noparse>${service.getShowFieldList('${showName}',data,formatData,permission)}</#noparse>
						</#list>
						<#if hasManage>
							<td>
								<#noparse><#list manageFields as manage>		
									${service.getManage('${manage.name}','${manage.desc}','</#noparse>${pkField}<#noparse>',data,actionUrl,managePermission)}
								</#list></#noparse>
							</td>
						</#if>
					</tr>
				<#noparse></#list></#noparse>
			</table>
			<#noparse>
				<#if bpmDataTemplate.list?size==0>
					<div style="padding:6px 0px 12px 0px;">当前没有记录。<div>
				</#if>
			${pageHtml}</#noparse>
	</div>
</div>
<#noparse>
<#else>
   <div style="padding:6px 0px 12px 0px;">当前用户没有满足的过滤条件,请设置过滤条件。<div>
</#if>
</#noparse> 