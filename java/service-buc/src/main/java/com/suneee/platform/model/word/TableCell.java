package com.suneee.platform.model.word;

import java.util.ArrayList;
import java.util.List;

public class TableCell {

	private List<IContent> contents=new ArrayList<IContent>();

	public List<IContent> getContents() {
		return contents;
	}

	public void setContents(List<IContent> contents) {
		this.contents = contents;
	}
	
	
	public void addContent(IContent conent){
		this.contents.add(conent);
	}
}
