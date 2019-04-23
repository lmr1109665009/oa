package com.suneee.platform.model.word;

public class TextContent implements IContent {
	
	private String content="";
	
	

	public TextContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "TextContent [content=" + content + "]";
	}
	
	
	
}
