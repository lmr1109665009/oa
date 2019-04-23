package com.suneee.platform.service.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.util.AppUtil;
import com.suneee.platform.model.word.Table;
import com.suneee.platform.model.word.TableCell;
import com.suneee.platform.model.word.TableRow;
import com.suneee.platform.model.word.TextContent;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.suneee.core.engine.FreemarkEngine;
import com.suneee.core.util.AppUtil;
import com.suneee.platform.model.word.Table;
import com.suneee.platform.model.word.TableCell;
import com.suneee.platform.model.word.TableRow;
import com.suneee.platform.model.word.TextContent;

import freemarker.template.TemplateException;

public class WordUtil {
	
	
	/**
	 * 根据word路径获取表格的html。
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TemplateException 
	 * String
	 */
	public static String getHtmlByPath(String path) throws FileNotFoundException, IOException, TemplateException{
		List<Table> list = WordUtil.getTablesByPath(path);
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("tables", list);
		FreemarkEngine engine= AppUtil.getBean(FreemarkEngine.class);
		String html=engine.mergeTemplateIntoString("word/word.ftl", params);
		return html;
	}
	
	/**
	 * 根据word流获取html。 
	 * @param stream
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TemplateException 
	 * String
	 */
	public static String getHtmlByStream(InputStream stream) throws FileNotFoundException, IOException, TemplateException{
		List<Table> list= WordUtil.getTablesByStream(stream);
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("tables", list);
		
		FreemarkEngine engine=AppUtil.getBean(FreemarkEngine.class);
		String html=engine.mergeTemplateIntoString("word/word.ftl", params);
		
		return html;
	}
	
	
	
	
	/**
	 * 根据word文档路径获取word中的表。
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static List<Table> getTablesByPath(String path) throws FileNotFoundException, IOException{
		List<Table> list=new ArrayList<Table>();
		if(path.endsWith(".doc")){// word 2003
			HWPFDocument doc03 = new HWPFDocument(new FileInputStream(path));
			list = getTablesByHWPF(doc03);
		}else{
			XWPFDocument doc=new XWPFDocument(new FileInputStream(path));
			List<XWPFTable> tables= doc.getTables();
			for(XWPFTable table:tables){
				Table tb= getTableByWdTable(table);
				list.add(tb);
			}
		}
    	return list;
	}
	
	public static List<Table> getTablesByStream(InputStream inputStram) throws FileNotFoundException, IOException{
    	XWPFDocument doc=new XWPFDocument(inputStram);
    	List<XWPFTable> tables= doc.getTables();
    	List<Table> list=new ArrayList<Table>();
    	for(XWPFTable table:tables){
    		Table tb= getTableByWdTable(table);
    		list.add(tb);
    	}
    	return list;
	}
	
	private static Table getTableByWdTable(XWPFTable tb){
		Table table=new Table();
		int maxCells=0;
		List<XWPFTableRow> rows =tb.getRows();
		for(XWPFTableRow row:rows){
			TableRow tr=new TableRow();
			List<XWPFTableCell> cells= row.getTableCells();
			if(cells.size()>maxCells){
				maxCells=cells.size();
			}
			for(int i=0;i<cells.size();i++){
				TableCell td=new TableCell();
				
				XWPFTableCell cell=cells.get(i);
				List<IBodyElement> els= cell.getBodyElements();
				for(IBodyElement el:els ){
					if(el instanceof XWPFTable){
						Table tbIn=getTableByWdTable((XWPFTable)el);
						td.addContent(tbIn);
					}
					else if(el instanceof XWPFParagraph){
						XWPFParagraph wp=(XWPFParagraph)el;
						String str=wp.getText();
						if(str==null || "".equals(str)) continue;
						td.addContent(new TextContent( wp.getText()));
					}
				}
				tr.addCell(td);
			}
			table.addRow(tr);
		}
		table.setMaxCells(maxCells);
		return table;
	}
	
	/**
	 * 解析word2003表格信息
	 * @param doc
	 * @return
	 */
	private static List<Table> getTablesByHWPF(HWPFDocument doc){
		List<Table> list=new ArrayList<Table>();
		int maxCells=0;
		Range range  = doc.getRange();
		TableIterator tableIt = new TableIterator(range);
		while(tableIt.hasNext()){
			org.apache.poi.hwpf.usermodel.Table t = (org.apache.poi.hwpf.usermodel.Table)tableIt.next();
			Table table = new Table();
			for(int i = 0;i<t.numRows();i++){
				org.apache.poi.hwpf.usermodel.TableRow tr = t.getRow(i);
				TableRow tableRow = new TableRow();
				for(int j=0;j<tr.numCells();j++){
					if(tr.numCells()>maxCells){
						maxCells = tr.numCells();
					}
					TableCell td=new TableCell();
					org.apache.poi.hwpf.usermodel.TableCell tc = tr.getCell(j);
					for(int y = 0;y<tc.numParagraphs();y++){//获取单元内容
						Paragraph ph = tc.getParagraph(y);
						String str = ph.text().trim();
						if(str==null || "".equals(str)) continue;
						td.addContent(new TextContent( str));
					}
					tableRow.addCell(td);
				}
				table.addRow(tableRow);
				table.setMaxCells(maxCells);
			}
			list.add(table);
		}
		return list;
	}
	
}
