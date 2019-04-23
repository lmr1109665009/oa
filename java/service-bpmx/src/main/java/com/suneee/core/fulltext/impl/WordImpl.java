package com.suneee.core.fulltext.impl;

import com.suneee.core.fulltext.IDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;





public class WordImpl implements IDocument {

	private String fileName="";
	@Override
	public void setFileName(String fileName) {
		this.fileName=fileName;
	}
	
	@Override
	public String extract() {
		String str = "";
		FileInputStream in = null;
		try {
			in = new FileInputStream(fileName);
			WordExtractor extractor = new WordExtractor(in);
			str = extractor.getText();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {

		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				
					e.printStackTrace();
				}
		}

		return str;
	}

}
