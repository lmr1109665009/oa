<#setting number_format="0">
<#macro input field>
		<#if field.fieldType == "varchar"><#---字符串类型-->
			<#switch field.controlType>
				<#case 1><#--单行文本框--><input parser="inputtable" type="text"  name="${field.fieldName}" lablename="${field.fieldDesc}" class="inputText" value="" validate="{maxlength:${field.charLen}<#if field.isRequired == 1>,required:true</#if><#if field.validRule != "">,${field.validRule}:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" isflag="tableflag"/>
				<#break>
				<#case 2><#--多行文本框--><textarea parser="textareatable" name="${field.fieldName}" lablename="${field.fieldDesc}" class="l-textarea" rows="2" cols="40" validate="{maxlength:${field.charLen}<#if field.isRequired == 1>,required:true</#if><#if field.validRule != "">,${field.validRule}:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" isflag="tableflag"></textarea>
				<#break>
				<#case 3><#--数据字典--><input parser="dicttable" lablename="${field.fieldDesc}" class="dicComboTree" nodeKey="${field.dictType}" value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" name="${field.fieldName}" height="200" width="248"/>
				<#break>
				<#case 4><#--人员选择器(单选)--><input parser="selectortable" name="${field.fieldName}" type="text" ctlType="selector" class="user" lablename="${field.fieldDesc}"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" readonly="readonly" <#if 1==field.isReference>linktype="${field.controlType}"  refid="${field.fieldName}ID"</#if>  scope="${field.getPropertyMap().scope}"  showCurUser="<#if (field.getPropertyMap().showCurUser==null)>0<#else>${field.getPropertyMap().showCurUser}</#if>" />
				<#break>
				<#case 5><#--角色选择器(多选)--><input parser="selectortable" name="${field.fieldName}" type="text" ctlType="selector" class="roles" lablename="${field.fieldDesc}"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" readonly="readonly" <#if 1==field.isReference>linktype="${field.controlType}"  refid="${field.fieldName}ID"</#if>  />
				<#break>
				<#case 6><#--组织选择器(多选)--><input parser="selectortable" name="${field.fieldName}" type="text" ctlType="selector" class="orgs" lablename="${field.fieldDesc}"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" readonly="readonly" <#if 1==field.isReference>linktype="${field.controlType}"  refid="${field.fieldName}ID"</#if>  scope="${field.getPropertyMap().scope}" />
				<#break>
				<#case 7><#--岗位选择器(多选)--><input parser="selectortable" name="${field.fieldName}" type="text" ctlType="selector" class="positions" lablename="${field.fieldDesc}"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" readonly="readonly" <#if 1==field.isReference>linktype="${field.controlType}"  refid="${field.fieldName}ID"</#if>  scope="${field.getPropertyMap().scope}" />
				<#break>
				<#case 8><#--人员选择器(多选)--><input parser="selectortable" name="${field.fieldName}" type="text" ctlType="selector" class="users" lablename="${field.fieldDesc}"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" readonly="readonly" <#if 1==field.isReference>linktype="${field.controlType}"  refid="${field.fieldName}ID"</#if>  scope="${field.getPropertyMap().scope}"/>
				<#break>
				<#case 9><#--文件上传-->
				<img src="{{ctx}}/styles/suneee/icon_attachment.png" parser="attachmenttable" ctltype="attachment" name="${field.fieldName}" title="${field.fieldDesc}" validatable="true" validate="{<#if field.isRequired == 1>required:true</#if>}" isdirectupload="<#if field.getPropertyMap().isDirectUpLoad==1>1<#else>0</#if>" />
				<#break>
				<#case 10><#--富文本框--><textarea parser="myeditortable" class="myeditor" style="height: 300px;" name="${field.fieldName}" lablename="${field.fieldDesc}" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.validRule != "">,${field.validRule}:true</#if>}"></textarea>
				<#break>
				<#case 11><#--下拉选项--><select parser="selecttable" name="${field.fieldName}" lablename="${field.fieldDesc}" controltype="select" validate='{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}'>
							<#list field.aryOptions?keys as optkey>
							<option value="${optkey}" <#if field.isDefault[optkey]==1>selected = "selected"</#if>>${field.aryOptions[optkey]}</option>
							</#list>
						</select>
				<#break>
				<#case 12><#--Office控件-->
				<img src="{{ctx}}/styles/suneee/icon_office_control.png" parser="officetable" name="${field.fieldName}" title="${field.fieldDesc}" controltype="office"  value="">
				<#break>
				<#case 13><#--复选框--><span name="${field.fieldName}" parser="checkboxtable">
						<#list field.aryOptions?keys as optkey>
							<label><input type="checkbox" name="${field.fieldName}" value="${optkey}" <#if field.isDefault[optkey]==1> checked='checked'</#if>  validate="{<#if field.isRequired == 1>required:true</#if>}"/>${field.aryOptions[optkey]}</label>
						</#list>
						</span>
				<#break>
				<#case 14><#--单选按钮--><span name="${field.fieldName}" parser="radiotable">
						<#list field.aryOptions?keys as optkey>
							<label><input type="radio" name="${field.fieldName}" value="${optkey}" <#if field.isDefault[optkey]==1> checked</#if> lablename="${field.fieldDesc}" validate="{<#if field.isRequired == 1>required:true</#if>}"/>${field.aryOptions[optkey]}</label>
						</#list>
						</span>
				<#break>
				<#case 15><#--日期控件--><input parser="datetable" name="${field.fieldName}" type="text" class="Wdate" lablename="${field.fieldDesc}" displayDate="<#if (field.getPropertyMap().displayDate==null)>0<#else>${field.getPropertyMap().displayDate}</#if>" dateFmt="<#if (field.getPropertyMap().format==null)>yyyy-MM-dd<#else>${field.getPropertyMap().format}</#if>"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}">
				<#break>
				<#case 16><#--隐藏域--><input parser="hiddentable" name="${field.fieldName}" type="hidden" lablename="${field.fieldDesc}"  value="" validate="{<#if field.isRequired == 1>required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}">
				<#break>
				<#case 17><#--角色选择器(单选)--><input parser="selectortable" name="${field.fieldName}" type="text" ctlType="selector" class="role" lablename="${field.fieldDesc}"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" readonly="readonly" <#if 1==field.isReference>linktype="${field.controlType}"  refid="${field.fieldName}ID"</#if>  />
				<#break>
				<#case 18><#--组织选择器(单选)--><input parser="selectortable" name="${field.fieldName}" type="text" ctlType="selector" class="org" lablename="${field.fieldDesc}"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" readonly="readonly"  <#if 1==field.isReference>linktype="${field.controlType}"  refid="${field.fieldName}ID"</#if> showCurOrg="<#if (field.getPropertyMap().showCurOrg==null)>0<#else>${field.getPropertyMap().showCurOrg}</#if>" scope="${field.getPropertyMap().scope}"/>
				<#break>
				<#case 19><#--岗位选择器(单选)--><input parser="selectortable" name="${field.fieldName}" type="text" ctlType="selector" class="position" lablename="${field.fieldDesc}"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" readonly="readonly"  <#if 1==field.isReference>linktype="${field.controlType}"  refid="${field.fieldName}ID"</#if> showCurPos="<#if (field.getPropertyMap().showCurPos==null)>0<#else>${field.getPropertyMap().showCurPos}</#if>" scope="${field.getPropertyMap().scope}"/>
				<#break>
				<#case 20><#--流程引用--><input parser="proinsttable" type="text" ctltype="flowSelector" name="${field.fieldName}" lablename="${field.fieldDesc}" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" readonly="readonly"/>					
				<#break>
				<#case 21><#--WebSign签章控件--><input type="hidden" class="hidden" name="${field.fieldName}" lablename="${field.fieldDesc}" parser="websigntable" controltype="webSign"  value="" /><div id="div_${field.fieldName?replace(":","_")}" class="webSign-div" style="height: 1px;"></div>
				<#break>
				<#case 22><#--图片展示控件--><input parser="picturetable" type="text" class="pictureshowcontrol"  name="${field.fieldName}" lablename="${field.fieldDesc}"  controltype="pictureShow" value=""/>
				<#break>
				<#case 24><#--HTML解析器/多行文本框(HTML)-->
				<textarea parser="htmledittable" name="${field.fieldName}" lablename="${field.fieldDesc}" class="html-parser" rows="5" cols="40" validate="{}"></textarea>
				<#break>
				<#case 25><#--公司选择器--><input parser="selectortable" name="${field.fieldName}" type="text" ctlType="selector" class="comp" lablename="${field.fieldDesc}"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}" readonly="readonly"  <#if 1==field.isReference>linktype="${field.controlType}"  refid="${field.fieldName}ID"</#if>  scope="${field.getPropertyMap().scope}"/>
				<#break>
			</#switch>
		<#elseif field.fieldType == "number"><#---数字类型-->
			<#if  field.controlType == 16><#--隐藏域--><input name="${field.fieldName}" parser="hiddentable" type="hidden"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}">
			<#elseif field.controlType == 11><#--下拉选项--><select name="${field.fieldName}" parser="selecttable" lablename="${field.fieldDesc}" controltype="select" validate='{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}'>
						<#list field.aryOptions?keys as optkey>
						<option value="${optkey}" <#if field.isDefault[optkey]==1>selected = "selected"</#if>>${field.aryOptions[optkey]}</option>
						</#list>
					</select>
			<#elseif field.controlType == 23><#--小数只读转百分比--><input parser="decimaltopercenttable" name="${field.fieldName}" type="text" value="" showType=${field.ctlProperty}  validate="{number:true,maxIntLen:${field.intLen},maxDecimalLen:${field.decimalLen}<#if field.isRequired == 1>,required:true</#if>
			<#if field.isWebSign == 1>,isWebSign:true</#if>}"><#else><#--否则数字输入--><input parser="inputtable" name="${field.fieldName}" type="text" value="" showType=${field.ctlProperty}  validate="{number:true,maxIntLen:${field.intLen},maxDecimalLen:${field.decimalLen}<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}">
			</#if>
		<#elseif field.fieldType == "date"><#---日期类型--><#if field.controlType == 16><#--隐藏域--><input parser="datetable" name="${field.fieldName}" type="hidden" class="hidden" displayDate="<#if (field.getPropertyMap().displayDate==null)>0<#else>${field.getPropertyMap().displayDate}</#if>"  dateFmt="<#if (field.getPropertyMap().format==null)>yyyy-MM-dd<#else>${field.getPropertyMap().format}</#if>" value="">
			<#else><input parser="datetable" name="${field.fieldName}" type="text" class="Wdate" displayDate="<#if (field.getPropertyMap().displayDate==null)>0<#else>${field.getPropertyMap().displayDate}</#if>"  dateFmt="<#if (field.getPropertyMap().format==null)>yyyy-MM-dd<#else>${field.getPropertyMap().format}</#if>"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}">
			</#if>
		<#else>
			<#if field.controlType == 16><#---隐藏域--><input parser="hiddentable" name="${field.fieldName}" type="hidden"  value="" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.isWebSign == 1>,isWebSign:true</#if>}">
			<#elseif field.controlType == 10><#--富文本框editor--><textarea parser="myeditortable" class="myeditor" name="${field.fieldName}" validate="{empty:false<#if field.isRequired == 1>,required:true<#else>,required:false</#if><#if field.validRule != "">,${field.validRule}:true</#if>}"></textarea>
			<#else><#--否则多文本框--><textarea parser="textareatable"  name="${field.fieldName}" validate="{empty:false<#if field.isRequired == 1>,required:true</#if><#if field.validRule != ""><#if field.isRequired == 1>,</#if>${field.validRule}:true</#if>}"<#if field.isWebSign == 1>,isWebSign:true</#if>></textarea>
			</#if>
		</#if>

</#macro>