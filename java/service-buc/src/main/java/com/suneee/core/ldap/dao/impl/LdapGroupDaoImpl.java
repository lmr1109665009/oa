package com.suneee.core.ldap.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.naming.InvalidNameException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Repository;

import com.suneee.core.util.StringUtil;
import com.suneee.core.ldap.dao.LdapGroupDao;
import com.suneee.core.ldap.map.LdapGroupAssemabller;
import com.suneee.core.ldap.model.LdapGroup;

@Repository
public class LdapGroupDaoImpl implements LdapGroupDao {
	Logger logger=LoggerFactory.getLogger(this.getClass());
	@Resource
	LdapTemplate ldapTemplate;

	@Override
	public List<LdapGroup> getAll() {
		return getByDN(getDn(""));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LdapGroup> getByDN(DistinguishedName dn) {
		EqualsFilter filter = new EqualsFilter("objectcategory",LdapGroup.OBJECTCLASS);
		logLdapQuey(filter.encode());
		try{
			return ldapTemplate.search(dn, filter.encode(), getContextMapper());
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	

	private ContextMapper getContextMapper() {
		return new LdapGroupAssemabller();
	}

	private DistinguishedName getDn(String dnstr) {
		DistinguishedName dn = new DistinguishedName();
		try {
			if (!StringUtil.isEmpty(dnstr)) {
				dn.add(dnstr);
			}
		} catch (InvalidNameException e) {
			e.printStackTrace();
		}
		return dn;
	}
	
	private void logLdapQuey(String message,Object... args){
		String formatStr = String.format(message, args);
		logger.info("LDAP query statement:"+formatStr);
	}
	
}
