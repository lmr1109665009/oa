package com.suneee.platform.model.bpm;


import com.suneee.core.util.StringUtil;
import net.sf.json.JSONObject;

/**
 * 对象功能:流程授权
 * 开发公司:广州宏天软件有限公司
 * 开发人员:xucx
 * 创建时间:2014-03-05 09:00:53
 */

public class AuthorizeRight 
{
	
	/*
	 *  //流程授权启动类型
	 *	public static final String START="start";
	 *	//流程授权管理类型
	 *	public static final String MANAGEMENT="management";
	 * 
	 *  管理类型的权限
	 *  "rightContent":{"m_edit":"Y","m_del":"N","m_start":"Y","m_set":"N"}
	 * */
	
	
	//流程授权类型
	protected String authorizeType = BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE.MANAGEMENT;
	
	//流程授权说明
	protected String defKey;
	
	//管理类型流程授权编辑权限
	protected String managementEdit = "N";
	
	//管理类型流程授权删除权限
	protected String managementDel = "N";
	
	//管理类型流程授权启动流程权限
	protected String managementStart = "N";
	
	//管理类型流程授权设置权限
	protected String managementSet = "N";
	
	//管理类型流程授权国际化权限
	protected String managementInternational = "N";
	
	//管理类型流程授权清除数据权限
	protected String managementClean = "N";
	
	//实例类型流程授权删除流程实例权限
	protected String instanceDel = "N";
	
	//实例类型流程授权管理流程实例日志权限
	protected String instanceLog = "N";
	
	//流程授权JSON字符串
	protected String rightContent = "";
	
	//流程授权JSON对象
	protected JSONObject rightJsonObj = new JSONObject();
	

	public String getAuthorizeType()
	{
		return authorizeType;
	}

	public void setAuthorizeType(String authorizeType)
	{
		this.authorizeType = authorizeType;
	}

	public String getDefKey()
	{
		return defKey;
	}

	public void setDefKey(String defKey)
	{
		this.defKey = defKey;
	}

	public String getManagementEdit()
	{
		return managementEdit;
	}

	public void setManagementEdit(String managementEdit)
	{
		this.managementEdit = managementEdit;
	}

	public String getManagementDel()
	{
		return managementDel;
	}

	public void setManagementDel(String managementDel)
	{
		this.managementDel = managementDel;
	}

	public String getManagementStart()
	{
		return managementStart;
	}

	public void setManagementStart(String managementStart)
	{
		this.managementStart = managementStart;
	}

	public String getManagementSet()
	{
		return managementSet;
	}

	public void setManagementSet(String managementSet)
	{
		this.managementSet = managementSet;
	}

	public String getManagementInternational()
	{
		return managementInternational;
	}

	public void setManagementInternational(String managementInternational)
	{
		this.managementInternational = managementInternational;
	}

	public String getManagementClean()
	{
		return managementClean;
	}
	
	public void setManagementClean(String managementClean)
	{
		this.managementClean = managementClean;
	}

	public String getInstanceDel()
	{
		return instanceDel;
	}

	public void setInstanceDel(String instanceDel)
	{
		this.instanceDel = instanceDel;
	}

	public String getInstanceLog()
	{
		return instanceLog;
	}

	public void setInstanceLog(String instanceLog)
	{
		this.instanceLog = instanceLog;
	}

	public String getRightContent()
	{
		return rightContent;
	}

	public void setRightContent(String rightContent)
	{
		this.rightContent = rightContent;
		if(StringUtil.isNotEmpty(rightContent)){
			JSONObject obj = JSONObject.fromObject(rightContent);
			//流程定义类型的管理权限
			if(BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE.MANAGEMENT.equals(this.authorizeType)){
				if(obj.containsKey("m_edit")){
	            	this.managementEdit = obj.getString("m_edit");
	            }
				if(obj.containsKey("m_del")){
		            this.managementDel = obj.getString("m_del");
		        }
				if(obj.containsKey("m_start")){
		            this.managementStart = obj.getString("m_start");
		        }
				if(obj.containsKey("m_set")){
					this.managementSet = obj.getString("m_set");
		        }
				if(obj.containsKey("m_international")){
					this.managementInternational = obj.getString("m_international");
		        }
				if(obj.containsKey("m_clean")){
					this.managementClean = obj.getString("m_clean");
		        }
			//流程实例管理类型的管理权限
			}else if(BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE.INSTANCE.equals(this.authorizeType)){
				if(obj.containsKey("i_del")){
		            this.instanceDel = obj.getString("i_del");
		        }
				if(obj.containsKey("i_log")){
		            this.instanceLog = obj.getString("i_log");
		        }
			}
			this.rightJsonObj = obj;
		}

	}
	
	//权限有更新时，为自己需要的权限就设置
	public void setRightByNeed(String needRight,String rightContent,String authorizeType)
	{
		if(StringUtil.isNotEmpty(needRight)&&StringUtil.isNotEmpty(rightContent)){
			JSONObject obj = JSONObject.fromObject(rightContent);
			//流程定义类型的管理权限
			if(BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE.MANAGEMENT.equals(authorizeType)){
				if(obj.containsKey("m_edit")){
					String m_edit = obj.getString("m_edit");
					if(needRight.equals(m_edit)){
						this.managementEdit = m_edit;
						this.rightJsonObj.put("m_edit", m_edit);
					}
	            }
				if(obj.containsKey("m_del")){
					String m_del = obj.getString("m_del");
					if(needRight.equals(m_del)){
						this.managementDel = m_del;
						this.rightJsonObj.put("m_del", m_del);
					}
		        }
				if(obj.containsKey("m_start")){
		            String m_start = obj.getString("m_start");
					if(needRight.equals(m_start)){
						this.managementStart = m_start;
						this.rightJsonObj.put("m_start", m_start);
					}
		        }
				if(obj.containsKey("m_set")){
					String m_set = obj.getString("m_set");
					if(needRight.equals(m_set)){
						this.managementSet = m_set;
						this.rightJsonObj.put("m_set", m_set);
					}
		        }
				if(obj.containsKey("m_international")){
					String m_international = obj.getString("m_international");
					if(needRight.equals(m_international)){
						this.managementInternational = m_international;
						this.rightJsonObj.put("m_international", m_international);
					}
		        }
				if(obj.containsKey("m_clean")){
					String m_clean = obj.getString("m_clean");
					if(needRight.equals(m_clean)){
						this.managementClean = m_clean;
						this.rightJsonObj.put("m_clean", m_clean);
					}
		        }
			//流程实例管理类型的管理权限
			}else if(BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE.INSTANCE.equals(authorizeType)){
				if(obj.containsKey("i_del")){
					String i_del = obj.getString("i_del");
					if(needRight.equals(i_del)){
						this.instanceDel = i_del;
						this.rightJsonObj.put("i_del", i_del);
					}
		        }
				if(obj.containsKey("i_log")){
		            String i_log = obj.getString("i_log");
					if(needRight.equals(i_log)){
						this.instanceLog = i_log;
						this.rightJsonObj.put("i_log", i_log);
					}
		        }
			}
			this.rightContent = this.rightJsonObj.toString();
		}

	}
	
	public void setRightByAuthorizeType(String right,String authorizeType)
	{
		this.authorizeType = authorizeType;
		//流程定义类型的管理权限
		if(BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE.MANAGEMENT.equals(authorizeType)){
			this.managementEdit = right;
			this.rightJsonObj.put("m_edit", right);
			this.managementDel = right;
			this.rightJsonObj.put("m_del", right);
			this.managementStart = right;
			this.rightJsonObj.put("m_start", right);
			this.managementSet = right;
			this.rightJsonObj.put("m_set", right);
			this.managementInternational = right;
			this.rightJsonObj.put("m_international", right);
			this.managementClean = right;
			this.rightJsonObj.put("m_clean", right);
		//流程实例管理类型的管理权限
		}else if(BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE.INSTANCE.equals(authorizeType)){
			this.instanceDel = right;
			this.instanceLog = right;
		}
		this.rightContent = this.rightJsonObj.toString();
	}

	public JSONObject getRightJsonObj()
	{
		return rightJsonObj;
	}

	public void setRightJsonObj(JSONObject rightJsonObj)
	{
		this.rightJsonObj = rightJsonObj;
	}

}