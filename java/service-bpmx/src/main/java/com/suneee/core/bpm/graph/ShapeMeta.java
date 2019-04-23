package com.suneee.core.bpm.graph;

import java.io.Serializable;

/**
 * 流程图的元数据。
 * @author ray
 *
 */
public class ShapeMeta implements Serializable {


	private static final long serialVersionUID = -1893656994984459376L;
	/**
	 * 流程定义的div节点结合。
	 */
	private String xml="";
	
	/**
	 * 流程图的宽。
	 */
	private int width=0;
	
	/**
	 * 流程图的高度。
	 */
	private int height=0;

	public ShapeMeta() {
	}

	public ShapeMeta(int w, int h, String xml)
	{
		this.width=w;
		this.height=h;
		this.xml=xml;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
