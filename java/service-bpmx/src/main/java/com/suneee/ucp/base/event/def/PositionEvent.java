/**
 * 
 */
package com.suneee.ucp.base.event.def;

import com.suneee.platform.model.system.Position;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 岗位事件
 * @author Administrator
 *
 */
public class PositionEvent extends ApplicationEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 755045694231124733L;
	
	/**
	 * 操作类型：更新岗位
	 */
	public static int ACTION_UPD=1;

	/**
	 * 操作类型：增加岗位
	 */
	public static int ACTION_ADD=0;
	
	/**
	 * 操作类型
	 */
	private int action = ACTION_UPD;
	
	private List<Position> positionList;
	/**
	 * @param source
	 */
	public PositionEvent(List<Position> positionList) {
		super(positionList);
		this.positionList = positionList;
	}
	/**
	 * @return the action
	 */
	public int getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(int action) {
		this.action = action;
	}
	/**
	 * @return the positionList
	 */
	public List<Position> getPositionList() {
		return positionList;
	}
	/**
	 * @param positionList the positionList to set
	 */
	public void setPositionList(List<Position> positionList) {
		this.positionList = positionList;
	}
}
