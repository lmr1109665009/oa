package com.suneee.platform.model.word;

import java.util.ArrayList;
import java.util.List;

public class Table implements IContent {
	
	private List<TableRow> rows=new ArrayList<TableRow>();
	//表最大表格数量。
	private int maxCells=0;

	public List<TableRow> getRows() {
		return rows;
	}

	public void setRows(List<TableRow> rows) {
		this.rows = rows;
	}
	
	
	public void addRow(TableRow tr){
		this.rows.add(tr);
	}

	public int getMaxCells()
	{
		return maxCells;
	}

	public void setMaxCells(int maxCells)
	{
		this.maxCells = maxCells;
	}
	
	

}
