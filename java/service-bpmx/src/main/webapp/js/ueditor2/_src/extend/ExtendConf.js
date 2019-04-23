/**
 * ueditor扩展插件的配置
 */
var ExtendConf = UE.ExtendConf = {
	iframeUrlMap: {
		'input': '~/dialogs/extend/input/input.jsp',
		'opinion': '~/dialogs/extend/opinion/opinion.jsp',
		'insertfunction': '~/dialogs/extend/insertfunction/MathExpEditor.jsp',
		'importform': '~/dialogs/extend/import/importform.jsp',
		'customdialog': '~/dialogs/extend/dialog/dialog.jsp',
		'customquery':'~/dialogs/extend/query/query.jsp',
		'customquerydialog':'~/dialogs/extend/query/dialog.jsp',
		'cascadequery':'~/dialogs/extend/query/queryMulVar.jsp',
		'numbervalidate':'~/dialogs/extend/validate/number.jsp',
		'daterangevalidate':'~/dialogs/extend/validate/daterange.jsp',
		'international':'~/dialogs/extend/international/international.jsp',
		'wordtemplate':'~/dialogs/extend/template/wordtemplate.jsp',
		'datecalculate': '~/dialogs/extend/insertfunction/datecalculate.jsp',
		'exportexceldialog': '~/dialogs/extend/exceltemp/exportexceldialog.jsp',
		'importexceldialog': '~/dialogs/extend/exceltemp/importexceldialog.jsp',
		
		'input_mb': '~/dialogs/extend/input/input.jsp?type=mb',
		'customdialog_mb': '~/dialogs/extend/dialog/dialog.jsp?type=mb',
		'insertfunction_mb': '~/dialogs/extend/insertfunction/MathExpEditor.jsp?type=mb',
		'daterangevalidate_mb':'~/dialogs/extend/validate/daterange.jsp?type=mb',
		'datecalculate_mb': '~/dialogs/extend/insertfunction/datecalculate.jsp?type=mb',
		'numbervalidate_mb':'~/dialogs/extend/validate/number.jsp?type=mb'
	},

	btnCmds:['tableformat','choosetemplate','opinion','input','taskopinion','flowchart',
	         'insertfunction','cutsubtable','pastesubtable','customdialog','clearcell',
	         'insertrownext','insertcolnext','customquery','uncustomquery','numbervalidate','daterangevalidate','datecalculate','international','serialnum',
	         'hidedomain','textinput','textarea','checkbox','radioinput','selectinput',
             'dictionary','personpicker','departmentpicker','datepicker','officecontrol','subtable',
             'myeditor','rolepicker','positionpicker','attachement','importform','exportform','pasteinput','cascadequery','wordtemplate','exportexceldialog','importexceldialog',
             'input_mb','insertfunction_mb','daterangevalidate_mb','datecalculate_mb','numbervalidate_mb','customdialog_mb','choosetemplate_mb',
             'sendperson','readperson','jumpurl' ,'taskname','htmldefform','textdefform','remaintime',
             'subject','startuser', 'startdate','starttime','businesskey','word','picture'],
 
	dialogBtns: ['importform', 'customquery','customquerydialog','cascadequery','opinion','customdialog','input','insertfunction','numbervalidate','daterangevalidate','datecalculate','international','uncascadequery','setreadonly','delreadonly','wordtemplate','exportexceldialog','importexceldialog',
	             'input_mb','insertfunction_mb','daterangevalidate_mb','datecalculate_mb','numbervalidate_mb','customdialog_mb']
	             
}