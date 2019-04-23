<#function getTable tb>
<#assign rtn>
<table class="formTable" border="1" cellspacing="0" cellpadding="2">
   <#list tb.rows as row>
       
        <tr>
        	<#list row.cells as cell>
	            <td ${getWidthStyle(tb.maxCells)} ${getStyle(cell_index)}>
	             	<#list cell.contents as c>
	             		<#if c.class.simpleName =="TextContent">
	             			${c.content}
	             		</#if>
	             		<#if c.class.simpleName =="Table">
	             			${getTable(c)}
	             		</#if>
	             	</#list>
	            </td>
            </#list>
            
        </tr>
   </#list>
</table>
</#assign>
 <#return rtn>
</#function>

<#function getStyle idx>
<#assign rtn><#if idx % 2==0> class="formTitle" align="right"</#if><#if idx % 2==1>class="formInput" align="left"</#if></#assign>
 <#return rtn>
</#function>

<#function getWidthStyle maxCell>
<#assign rtn> width = ${100/maxCell}${"%"} </#assign>
<#return rtn>
</#function>