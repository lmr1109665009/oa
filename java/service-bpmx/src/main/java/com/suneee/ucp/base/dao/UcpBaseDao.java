/**
 * 
 */
package com.suneee.ucp.base.dao;

import com.suneee.core.db.GenericDao;

/**
 * 数据库操作基类。<br>
 * 包含基本的操作：增，查，改，删，列表，分页操作。
 * @author Administrator
 *
 */
public abstract class UcpBaseDao<E> extends GenericDao<E, Long> {

}
