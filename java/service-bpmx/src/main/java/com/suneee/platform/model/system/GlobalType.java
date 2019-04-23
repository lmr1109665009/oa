package com.suneee.platform.model.system;

import com.suneee.core.model.BaseModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

/**
 * 对象功能:总分类表 Model对象
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ljf
 * 创建时间:2011-11-23 11:07:26
 */
@SuppressWarnings("serial")
public class GlobalType extends BaseModel {
	/**
	 * 流程分类
	 */
	public static final String CAT_FLOW="FLOW_TYPE";
	/**
	 * 表单类型
	 */
	public static final String CAT_FORM="FORM_TYPE";
	
	/**
	 * 文件类型
	 */
	public static final String CAT_FILE = "FILE_TYPE";
	
	/**
	 * 附件类型
	 */
	public static final String CAT_ATTACH = "ATTACH_TYPE";
	
	/**
	 * 字典类型(数据字典)
	 */
	public final static  String CAT_DIC="DIC";//与数据库表SYS_TYPE_KEY的字段TYPEKEY对应
	
	/**t
	 * 分类类型--附件文件格式
	 */
	public static final String CAT_FILE_FORMAT = "FILEFORMAT";
	
	/**
	 * 分类类型--报表
	 */
	public static final String CAT_REPORT="REPORT_TYPE";
	
	/**
	 * 分类类型--数据字典
	 */
	public static final String NODE_KEY_DIC="DIC";	
	
	/**
	 * 分类类型--首页栏目
	 */
	
	public static final String Mobile_SCENE="MOBILE_TYPE";

	/**
	 * 场景类型--手机
	 */

	public static final String CAT_INDEX_COLUMN="INDEX_COLUMN_TYPE";

	/**
	 * 分类类型--知识库分类
	 */
	public static final String CAT_KNOWLEDGE="KNOWLEDGE_TYPE";
	
	/**
	 * 分类类型--自定义表分类
	 */
	public static final String CAT_FORM_TABLE = "FORM_TABLE_TYPE";
	
	/**
	 * 分类类型--地区分类
	 */
	public static final String CAT_REGION = "REGION_TYPE";

	/**
	 * 分类类型--默认分类
	 */
	public static final String CAT_DEFAULT = "DEFAULT_TYPE";
	/**
	 * 默认分类名称
	 */
	public static final String TYPE_NAME_DEFAULT = "其他";

	/**
	 * 流程分类根节点的名称
	 */
	public static final String TYPE_NAME_BPM="流程分类";
	/**
	 * 数据字典在sys_gl_type 中的类型名称
	 */
	public final static  String TYPE_NAME_DIC="数据字典";

	/**
	 * 树型数据 type=1
	 */
	public final static Integer DATA_TYPE_TREE=1;
	/**
	 * 平铺数据 type=0
	 */
	public final static Integer DATA_TYPE_FLAT=0;

	/**
	 * 根结点的父ID
	 */
	public final static long ROOT_PID=-1;//重要
	/**
	 * 根结点的ID
	 */
	public final static long ROOT_ID=0;
	
	/**
	 * 根结点的深度
	 */
	public final static long ROOT_DEPTH=0;
	
	/**
	 *  是否父类
	 */
	public final static String IS_PARENT_N="false";
	public final static String IS_PARENT_Y="true";
	/**
	 * 是否叶子(0否,1是)
	 */
	public final static int IS_LEAF_N=0;
	public final static int IS_LEAF_Y=1;
	/**
	 * 自编码生成方式(0	手工录入,1自动生成)
	 */
	public final static String NODE_CODE_TYPE_AUTO_N="0";
	public final static String NODE_CODE_TYPE_AUTO_Y="1";

	//分类ID
	protected Long typeId=0L;
	// 名称
	protected String typeName;
	// 节点路径
	protected String nodePath;
	// 层次
	protected Integer depth;
	// 父节点
	protected Long parentId;
	//父节点名称
	protected String parentName;
	// 节点的分类Key
	protected String nodeKey;
	// 节点分类的Key，如产品分类Key为PT
	protected String catKey;
	// 序号
	protected Long sn;
	// 所属用户
	protected Long userId=0L;
	// depId
	protected Long depId=0L;
	// 类型(0平铺,1树形)
	protected Integer type=0;
	//是否找开
	protected String open="true";
	
	// 是否父类,主要用于树的展示时用
	protected String isParent;
	// 是否叶子结点(0否,1是),主要用于数据库保存
	protected Integer isLeaf;
	//区分字典项和字典分类0:字典,1:字典分类
	protected int isType;
	//子节点的个数
	protected int childNodes=0;
	// 节点的自编码
	protected String nodeCode;
	// 节点的自编码生成方式
	protected Short nodeCodeType=0;
	
	/**
	 * 企业编码
	 */
	protected String enterpriseCode;

	//用语存放数据字典项
	protected List<Dictionary> dicList;

	public Integer getIsLeaf() {
		return isLeaf;
	}
	
	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getIsParent() {
		return this.childNodes>0?"true":"false";
	}
	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}

	public String getOpen() {
		return open;
	}
	public void setOpen(String open) {
		this.open = open;
	}
	
	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
	/**
	 * 返回 typeId
	 * @return
	 */
	public Long getTypeId() {
		return typeId;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	/**
	 * 返回 名称
	 * @return
	 */
	public String getTypeName() {
		return typeName;
	}

	public void setNodePath(String nodePath) {
		this.nodePath = nodePath;
	}
	/**
	 * 返回 nodePath
	 * @return
	 */
	public String getNodePath() {
		return this.nodePath;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}
	/**
	 * 返回 层次
	 * @return
	 */
	public Integer getDepth() {
		int i=this.nodePath.split("\\.").length-1;
		return i;
		
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	/**
	 * 返回 父节点
	 * @return
	 */
	public Long getParentId() {
		return parentId;
	}

	public String getParentName() { return parentName; }
	public void setParentName(String parentName) { this.parentName = parentName; }

	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}
	/**
	 * 返回 节点的分类Key
	 * @return
	 */
	public String getNodeKey() {
		return nodeKey;
	}

	public void setCatKey(String catKey) {
		this.catKey = catKey;
	}
	/**
	 * 返回 节点分类的Key，如产品分类Key为PT
	 * @return
	 */
	public String getCatKey() {
		return catKey;
	}

	public void setSn(Long sn) {
		this.sn = sn;
	}
	/**
	 * 返回 序号
	 * @return
	 */
	public Long getSn() {
		return sn;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * 返回 所属用户
	 * @return
	 */
	public Long getUserId() {
		return userId;
	}

	public void setDepId(Long depId) {
		this.depId = depId;
	}
	/**
	 * 返回 depId
	 * @return
	 */
	public Long getDepId() {
		return this.depId;
		
	}

	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 返回 类型(0平铺,1树形)
	 * @return
	 */
	public Integer getType() {
		return type;
	}
	
	public int getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(int childNodes) {
		this.childNodes = childNodes;
	}


	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	public Short getNodeCodeType() {
		return nodeCodeType;
	}

	public void setNodeCodeType(Short nodeCodeType) {
		this.nodeCodeType = nodeCodeType;
	}

	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}

	public int getIsType() { return isType; }
	public void setIsType(int isType) { this.isType = isType; }

	public List<Dictionary> getDicList() { return dicList; }
	public void setDicList(List<Dictionary> dicList) { this.dicList = dicList; }

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof GlobalType)) {
			return false;
		}
		GlobalType rhs = (GlobalType) object;
		return new EqualsBuilder()
		.append(this.typeId, rhs.typeId)
		.append(this.typeName, rhs.typeName)
		.append(this.nodePath, rhs.nodePath)
		.append(this.depth, rhs.depth)
		.append(this.parentId, rhs.parentId)
		.append(this.nodeKey, rhs.nodeKey)
		.append(this.catKey, rhs.catKey)
		.append(this.sn, rhs.sn)
		.append(this.userId, rhs.userId)
		.append(this.depId, rhs.depId)
		.append(this.type, rhs.type)
		.append(this.nodeCode, rhs.nodeCode)
		.append(this.nodeCodeType, rhs.nodeCodeType)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.typeId) 
		.append(this.typeName) 
		.append(this.nodePath) 
		.append(this.depth) 
		.append(this.parentId) 
		.append(this.nodeKey) 
		.append(this.catKey) 
		.append(this.sn) 
		.append(this.userId) 
		.append(this.depId) 
		.append(this.type) 
		.append(this.nodeCode) 
		.append(this.nodeCodeType) 
		.append(this.enterpriseCode)
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this)
		.append("typeId", this.typeId) 
		.append("typeName", this.typeName) 
		.append("nodePath", this.nodePath) 
		.append("depth", this.depth) 
		.append("parentId", this.parentId) 
		.append("nodeKey", this.nodeKey) 
		.append("catKey", this.catKey) 
		.append("sn", this.sn) 
		.append("userId", this.userId) 
		.append("depId", this.depId) 
		.append("type", this.type) 
		.append("nodeCode", this.nodeCode) 
		.append("nodeCodeType", this.nodeCodeType) 
		.append("enterpriseCode", this.enterpriseCode)
		.toString();
	}
	
	

}