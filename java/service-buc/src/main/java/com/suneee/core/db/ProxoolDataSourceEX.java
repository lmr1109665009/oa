package com.suneee.core.db;

import com.suneee.core.encrypt.EncryptUtil;
import org.logicalcobwebs.proxool.ProxoolDataSource;

import com.suneee.core.encrypt.EncryptUtil;


/**
 * 继承ProxoolDataSource实现密码加密。
 * <pre>
 * 在app-resources.xml
 * &lt;bean id="dataSource" class="com.suneee.core.db.ProxoolDataSourceEX">
 * ...
 * &lt;bean>
 * </pre>  
 * @author ray
 *
 */
public class ProxoolDataSourceEX extends ProxoolDataSource {
	
	private String password="";
	
	/**
	 * 设置密码
	 */
    public void setPassword(String password) {
    	String pwd="";
		try {
			pwd = EncryptUtil.decrypt(password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.password = pwd;
    }

	public String getPassword() {
        return password;
    } 
	
	
}
