package com.suneee.platform.xml.system;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.suneee.platform.model.system.AliasScript;
import com.suneee.platform.xml.constant.XmlConstant;
import com.suneee.platform.xml.constant.XmlConstant;

@XmlRootElement(name = "aliasScripts")
@XmlAccessorType(XmlAccessType.FIELD)
public class AliasScriptXml 
{
	/**
	 * 别名脚本
	 */
	@XmlElement(name = XmlConstant.SYS_ALIAS_SCRIPT, type = AliasScript.class)
	private AliasScript aliasScript;

	
	public AliasScript getAliasScript() {
		return aliasScript;
	}

	public void setAliasScript(AliasScript aliasScript) {
		this.aliasScript = aliasScript;
	}
	
	
}
