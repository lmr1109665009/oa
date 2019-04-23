/**
 * 描述：TODO
 * 包名：com.suneee.platform.event.listener
 * 文件名：LdapObjectEventListener.java
 * 作者：User-mailto:liyj@jee-soft.cn
 * 日期2015-6-12-下午4:56:34
 *  2015广州宏天软件有限公司版权所有
 * 
 */
package com.suneee.platform.event.listener;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.suneee.core.ldap.LdapObjectEvent;
import com.suneee.core.util.TimeUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

import com.suneee.core.ldap.LdapObjectEvent;
import com.suneee.core.util.TimeUtil;
import com.suneee.platform.model.system.SysOrg;

/**
 * <pre>
 * 描述：TODO
 * 构建组：bpm33
 * 作者：aschs
 * 邮箱:liyj@jee-soft.cn
 * 日期:2015-6-12-下午4:56:34
 * 版权：广州宏天软件有限公司版权所有
 * </pre>
 */
public class LdapObjectEventListener implements ApplicationListener<LdapObjectEvent>, Ordered {

	@Override
	public int getOrder() {
		return 3;
	}

	@Override
	public void onApplicationEvent(LdapObjectEvent event) {
		List<Map<String, String>> list = (List<Map<String, String>>) event.getSource();

		// 模拟处理list
		for (Map<String, String> map : list) {
			String name = map.get("name");
			String id = map.get("uSNCreated");
			String distinguishedName = map.get("distinguishedName");
			String whenCreated = map.get("whenCreated");

			Date createDate = TimeUtil.convertString(whenCreated.replace(".OZ", ""), "yyyyMMddHHmmss");
			SysOrg org = new SysOrg();
			org.setOrgId(Long.parseLong(id));
			org.setOrgName(name);
			org.setCreatetime(createDate);

			// 查找他的父
			for (Map<String, String> pmap : list) {
				String pdistinguishedName = pmap.get("distinguishedName");
				String str = "OU=" + name + "," + pdistinguishedName;
				if (!distinguishedName.equals(str)) {
					continue;
				}

				String pname = pmap.get("name");
				String pid = pmap.get("uSNCreated");
				org.setOrgSupId(Long.parseLong(pid));
				org.setOrgSupName(pname);
				break;
			}

			System.out.println(org);

		}
	}

}
