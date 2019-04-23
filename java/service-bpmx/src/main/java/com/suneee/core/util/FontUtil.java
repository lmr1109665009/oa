package com.suneee.core.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;


/**
 * 获取字体
 * <pre>
 * 在生成图片时可能需要用到字体，如果使用Font font = new Font(); 获取字体，
 * 则会依赖部署应用的服务器的本地字体库，而有些时候服务器上会缺少中文字体库，这里使用
 * 工具类来获取字体，工具类中可以判断当前服务器是否安装了需要的字体，未安装时使用war中
 * 自带的字体库。
 * </pre>
 * @author heyifan
 */
public class FontUtil {
	private static final String FONT_PATH = "fonts/simsun.ttf";
	
	/**
	 * 判断当前是否安装了指定字体库
	 * @param fontName
	 * @return
	 */
	public static Boolean isExist(String fontName){
		if(StringUtil.isEmpty(fontName)) return false;
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontNames = e.getAvailableFontFamilyNames();
		for(int i = 0; i<fontNames.length ; i++){
			String curFontName = fontNames[i];
			if(fontName.equals(curFontName)) return true;
		}    
		return false;
	}

	public static Font getFont(String fontName, Integer style, Integer size){
		if(isExist(fontName)){
			return new Font(fontName,style,size);
		}
		try {
			InputStream resourceAsStream = FontUtil.class.getClassLoader().getResourceAsStream(FONT_PATH);
			if(BeanUtils.isEmpty(resourceAsStream)){
				throw new IOException("未加载到字体文件：" + FONT_PATH);
			}
			Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT, resourceAsStream);
			float parseFloat = Float.parseFloat(size.toString());
			dynamicFont = dynamicFont.deriveFont(parseFloat);
			return dynamicFont;
		} catch (FontFormatException ex){
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}  
		return null;
	}
}