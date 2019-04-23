/**
 * 描述：TODO
 * 包名：com.suneee.platform.service.form.parser.util
 * 文件名：FieldRight.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2015-12-30-下午5:55:28
 *  2015广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.service.form.parser.util;

/**
 * <pre>
 * 描述：表单字段的权限
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-12-30-下午5:55:28
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
public enum FieldRight {
	/**
	 * 编辑
	 */
	W("w", "编辑"),
	/**
	 * 只读
	 */
	R("r", "只读"),
	/**
	 * 必填
	 */
	B("b", "必填"),
	/**
	 * 隐藏
	 */
	Y("", "隐藏"),
	/**
	 * 只读提交
	 */
	RP("rp", "只读提交");
	private String val;
	private String desc;

	private FieldRight(String val, String desc) {
		this.val = val;
		this.desc = desc;
	}

	/**
	 * val
	 * 
	 * @return the val
	 * @since 1.0.0
	 */

	public String getVal() {
		return val;
	}

	/**
	 * @param val
	 *            the val to set
	 */
	public void setVal(String val) {
		this.val = val;
	}

	/**
	 * desc
	 * 
	 * @return the desc
	 * @since 1.0.0
	 */

	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean equals(String val) {
		return this.val.toLowerCase().equals(val.toLowerCase());
	}
}
