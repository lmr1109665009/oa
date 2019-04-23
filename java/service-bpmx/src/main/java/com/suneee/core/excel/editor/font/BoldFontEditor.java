package com.suneee.core.excel.editor.font;

import com.suneee.core.excel.editor.IFontEditor;
import com.suneee.core.excel.style.font.BoldWeight;
import com.suneee.core.excel.style.font.Font;

/**
 * 实现一些常用的字体<br/>
 * 该类用于把字体加粗
 * @author zxh
 *
 */
public class BoldFontEditor implements IFontEditor {

	public void updateFont(Font font) {
		font.boldweight(BoldWeight.BOLD);
	}

}
