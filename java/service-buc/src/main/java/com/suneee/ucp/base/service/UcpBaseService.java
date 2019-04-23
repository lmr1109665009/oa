/**
 * 
 */
package com.suneee.ucp.base.service;

import com.suneee.core.service.GenericService;

/**
 * 实现业务的基本操作类，实体主键为String类型
 * @author Administrator
 * @param <E> 实体类型，如Archive
 */
public abstract class UcpBaseService<E> extends GenericService<E, Long> {

}
