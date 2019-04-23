package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import com.suneee.core.util.StringUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 对象功能:sys_popup_remind Model对象 开发公司:广州宏天软件有限公司 开发人员:liyj 创建时间:2015-03-18 11:36:24
 */
public class SysPopupRemind extends BaseModel {
	// ID
	protected Long id;
	// 主题 弹框时的信息
	protected String subject;
	// 点击该提醒的跳转URL
	protected String url;
	// SQL 返回数据大小
	protected String sql;
	// 数据源别名
	protected String dsalias;
	// 排序
	protected Long sn;
	// 是否启用 0:否   1:是
	protected Short enabled;
	// createTime
//	protected Date createTime;
	// 创建人 json.id,json.name
	protected String creator;
	//描叙字段
	protected String desc;
	//弹框方式
	protected String popupType;
	//预留json格式字段，目前为弹框方式为dialog时的宽高数据
	protected String reserve;
	
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 返回 ID
	 * 
	 * @return
	 */
	public Long getId() {
		return this.id;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * 返回 主题 弹框时的信息
	 * 
	 * @return
	 */
	public String getSubject() {
		return this.subject;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 返回 点击该提醒的跳转URL
	 * 
	 * @return
	 */
	public String getUrl() {
		return this.url;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * 返回 SQL 返回数据大小
	 * 
	 * @return
	 */
	public String getSql() {
		return this.sql;
	}

	public void setDsalias(String dsalias) {
		this.dsalias = dsalias;
	}

	/**
	 * 返回 数据源别名
	 * 
	 * @return
	 */
	public String getDsalias() {
		return this.dsalias;
	}

	public void setSn(Long sn) {
		this.sn = sn;
	}

	/**
	 * 返回 排序
	 * 
	 * @return
	 */
	public Long getSn() {
		return this.sn;
	}

	public void setEnabled(Short enabled) {
		this.enabled = enabled;
	}

	/**
	 * 返回 是否启用
	 * 
	 * @return
	 */
	public Short getEnabled() {
		return this.enabled;
	}

	public void setCreatetime(java.util.Date createtime) {
		this.createtime = createtime;
	}

	/**
	 * 返回 创建时间
	 * 
	 * @return
	 */
	public java.util.Date getCreatetime() {
		return createtime;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 返回 创建人 json.id,json.name
	 * 
	 * @return
	 */
	public String getCreator() {
		return this.creator;
	}
	
	/**
	 * 把creator字段翻译成json然后返回ID
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public String getCreatorId() {
		if (StringUtil.isEmpty(creator))
			return "";
		return JSONObject.fromObject(creator).getString("id");
	}

	/**
	 * 把creator字段翻译成json然后返回NAME
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public String getCreatorName() {
		if (StringUtil.isEmpty(creator))
			return "";
		return JSONObject.fromObject(creator).getString("name");
	}

	/**
	 * desc
	 * @return  the desc
	 * @since   1.0.0
	 */
	
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * popupType
	 * @return  the popupType
	 * @since   1.0.0
	 */
	
	public String getPopupType() {
		return popupType;
	}

	/**
	 * @param popupType the popupType to set
	 */
	public void setPopupType(String popupType) {
		this.popupType = popupType;
	}

	/**
	 * reserve
	 * @return  the reserve
	 * @since   1.0.0
	 */
	
	public String getReserve() {
		return reserve;
	}

	/**
	 * @param reserve the reserve to set
	 */
	public void setReserve(String reserve) {
		this.reserve = reserve;
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof SysPopupRemind)) {
			return false;
		}
		SysPopupRemind rhs = (SysPopupRemind) object;
		return rhs.getId().equals(this.id);
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).append("createtime", this.createtime).append("subject", this.subject)
				.append("url", this.url).append("sql", this.sql).append("desc", this.desc).append("dsalias", this.dsalias)
				.append("sn", this.sn).append("enabled", this.enabled).append("reserve", this.reserve).append("createBy", this.createBy)
				.append("creator", this.creator).append("popupType", this.popupType).append("retype").toString();
	}
	
	/**
	 * <pre> 
	 * 描述：弹框方式
	 * 构建组：bpm33
	 * 作者：aschs
	 * 邮箱:liyj@jee-soft.cn
	 * 日期:2015-8-28-上午10:27:52
	 * 版权：广州宏天软件有限公司版权所有
	 * </pre>
	 */
	public static class PopupType{
		/**
		 * tab方式
		 */
		public static final String TAB="tab";
		/**
		 * 新窗口
		 */
		public static final String NEW_WIN="newWin";
		/**
		 * 对话框
		 */
		public static final String DIALOG="dialog";
	}
}