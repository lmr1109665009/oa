package com.suneee.platform.model.word;

import java.util.ArrayList;
import java.util.List;

public class TableRow {	
	
	private List<TableCell> cells=new ArrayList<TableCell>();

	public List<TableCell> getCells() {
		return cells;
	}

	public void setCells(List<TableCell> cells) {
		this.cells = cells;
	}
	
	
	public void addCell(TableCell td){
		this.cells.add(td);
	}

}
