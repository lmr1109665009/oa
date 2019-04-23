var controlObj;

var reg=new RegExp("\{(.*?)_0_(.*?)\}","gi");

/**
 * 填充主表数据
 * @param main
 */
function initMainTable(main){
	
	controlObj = OfficePlugin.officeObjs[0].controlObj;
	var bookMarks = controlObj.ActiveDocument.BookMarks;
	var bookMarkCount = bookMarks.Count;
	
	for(var i=1;i<=bookMarks.Count;i++){
		var item = bookMarks.Item(i);
		var name = item.Name;
		if(name.indexOf('_0_')>0) continue;
		
		name = name.split('_1_')[0];
		var nameValue = main[name];
		if(nameValue==undefined || nameValue==null) continue;
		
		var currentRange = item.Range;
		currentRange.Text = nameValue ? nameValue : '';
		i--;// 因替换了一个书签，所以需要减一，否则bookMarks.Item(i)会取不到后面的书签
	}
}

/**
 * 填充子表。
 * @param subTables
 */
function fillSubtables(subTables){
	//读取表的元数据
	var aryTableMeta=getMetaData();
	var tables = controlObj.ActiveDocument.Tables; //得到tables
		
	for(var i=0;i<aryTableMeta.length;i++){
		var tableMeta=aryTableMeta[i];
		var idx=tableMeta.tableIndex;
		var tableName=tableMeta.tableName;
		var currentTable = tables.Item(idx);
		var rows=subTables[tableName].dataList;
		//添加序号
		addRownumber(rows);
		
		//遍历加载每一个子表的数据。
		fillTable(currentTable,rows,tableMeta);
	}
	
}

/**
 * 列添加序列号
 * @param rows
 */
function addRownumber(rows){
	for(var i=0;i<rows.length;i++){
		var obj=rows[i];
		obj.rn=i+1;
	}
}

/**
 * 填充子表
 * @param currentTable		office表格
 * @param rows				子表对应的行数据
 * @param tableMeta			表格的元数据
 */
function fillTable(currentTable,rows,tableMeta){
	var tbColMap=tableMeta.tbColMap;
	var rowCount = currentTable.rows.Count;
	var colCount = currentTable.columns.Count;
	//数据为空的情况
	if(rows.length==0){
		currentTable.rows(rowCount).Delete();
	}
	for(var i=0;i<rows.length;i++){
		var row=rows[i];
		//按照列填充。
		for(var col=1;col<=colCount;col++){
			var template=getTemplate(tbColMap,col);
			var rtn=template.replace(reg,function(p1,p2,p3){
		  		return row[p3];
		  	});
			var cell = currentTable.Cell(rowCount, col);
			cell.Range.Text=rtn;
		}
		//遍历到最后一行 不加数据。
		if(i<rows.length-1){
			currentTable.rows.add();
			rowCount++;
		}
	}
}

/**
 * 根据列编号获取列对应的模版。
 * @param tbColMap
 * @param col
 * @returns
 */
function getTemplate(tbColMap,col){
	for(var i=0;i<tbColMap.length;i++){
		var obj=tbColMap[i];
		if(obj.colIndex==col){
			return obj.template;
		}
	}
	return "";
}
	
/**
读取表的元数据。
*[
*	{
*		tableName:"",
*		tableIndex:1,
*		tbColMap:[{template:"{表名_0_字段名称1}",colIndex:1},{template:"{表名_0_字段名称2}",colIndex:1}]
*	}
*]
*/
function getMetaData(){
	var aryTableMeta=[];
	
	var tables = controlObj.ActiveDocument.Tables; //得到tables
	var tableCount = tables.Count; //得到table的数量
	
	if(tableCount==0) return aryTableMeta;
	for(var i=1;i<=tableCount;i++){
		var currentTable = tables.Item(i);
		// 表格列数
		var columnCount = currentTable.Columns.Count;
		var rowCount = currentTable.rows.Count;
		
		var isValidSubTalbe=isSubTable(currentTable);
		
		if(!isValidSubTalbe) continue;
		
		var tableMeta={};
		//表元数据索引
		tableMeta.tableIndex=i;
		var aryRowMeta=[];
		//列元数据读取
		for(var c=1; c<=columnCount;c++){
			var cell = currentTable.Cell(rowCount, c);
			var val = cell.Range.Text;
			var rowMetaObj={};
			rowMetaObj.colIndex=c;
			rowMetaObj.template=val.substring(0,val.length-2);
			aryRowMeta.push(rowMetaObj);
		}
		tableMeta.tbColMap=aryRowMeta;
		
		aryTableMeta.push(tableMeta);
	}
	handTableMeta(aryTableMeta);
	
	return aryTableMeta;
	
}

/**
 * 判断最后一行的第一个单元格内容是否包含_0_数据，如果包含则表示这是一个子表。
 * @param currentTable
 * @returns {Boolean}
 */
function isSubTable(currentTable){
	
	// 表格列数
	var columnCount = currentTable.Columns.Count;
	var rowCount = currentTable.rows.Count;
	
	var cell = currentTable.Cell(rowCount, 1);
	
	var val = cell.Range.Text;
	return (val.indexOf("_0_")>-1) ;
	//_0_
	
}
	
function handTableMeta(aryTableMeta){
	//splice
	for(var i=aryTableMeta.length-1;i>=0;i--){
		var tableMeta=aryTableMeta[i];
		validTable(tableMeta);
		var isValid=tableMeta.isValid;
		if(!isValid){
			//删除无效元数据
			aryTableMeta.splice(i,1);
		}
	}
}
/**
 * 验证表的元数据。
 * @param tableMeta
 */
function validTable(tableMeta){
	var aryCol=tableMeta.tbColMap;
	var rowMetaData={};
	
	var isValid=false;
	for(var i=0;i<aryCol.length;i++){
		var colObj=aryCol[i];
		var gridText=colObj.template;
		if(gridText.trim()=="") {
			continue;
		}
		
		var match = reg.exec(gridText);
		if(match==null) continue;
		
		tableMeta.tableName=match[1];
		isValid=true;
		if(isValid){
			break;
		}
		
	}
	tableMeta.isValid=isValid;
}
	
	
function printWord(){
	var oldOption;	
	try
	{
		var objOptions =  controlObj.ActiveDocument.Application.Options;
		oldOption = objOptions.PrintBackground;
		objOptions.PrintBackground = false;
	}
	catch(err){};
	controlObj.printout(true);
	try
	{
		var objOptions =  controlObj.ActiveDocument.Application.Options;
		objOptions.PrintBackground = oldOption;
	}
	catch(err){};	
}
	