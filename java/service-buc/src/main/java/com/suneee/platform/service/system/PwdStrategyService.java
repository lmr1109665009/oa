package com.suneee.platform.service.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.service.bpm.ProcessRunService;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.encrypt.EncryptUtil;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.dao.system.PwdStrategyDao;
import com.suneee.platform.model.system.PwdStrategy;
import com.suneee.platform.model.system.SysUser;
import net.sf.json.util.JSONUtils;
import net.sf.ezmorph.object.DateMorpher;
import com.suneee.core.bpm.model.ProcessCmd;
import com.suneee.core.util.StringUtil;
import net.sf.json.JSONObject;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.encrypt.EncryptUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.DateUtil;
import com.suneee.core.util.TimeUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.platform.model.system.LoginLog;
import com.suneee.platform.model.system.PwdStrategy;
import com.suneee.platform.model.system.SysUser;
import com.suneee.platform.dao.system.PwdStrategyDao;
import com.suneee.core.service.BaseService;

/**
 * <pre>
 * 对象功能:sys_pwd_strategy Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:liyj
 * 创建时间:2015-06-25 14:30:17
 * </pre>
 */
@Service
public class PwdStrategyService extends BaseService<PwdStrategy> {
	@Resource
	private PwdStrategyDao dao;
	@Resource
	private LoginLogService loginLogService;
	@Resource
	private SysUserService sysUserService;
	public PwdStrategyService() {
	}

	@Override
	protected IEntityDao<PwdStrategy, Long> getEntityDao() {
		return dao;
	}

	/**
	 * 更新状态
	 * 
	 * @param ids
	 *            ：以,分割的ids
	 * @param enabled
	 *            void
	 * @exception
	 * @since 1.0.0
	 */
	public void updateEnable(String[] ids, Short enable) {
		Map<String, Object> map = new HashMap<String, Object>();
		//1:如果是启动，先把所有设置为0(false)，只能有一个策略生效
		closeAll();

		//2:设置为enable 可以是启动或关闭
		map.put("ids", ids);
		map.put("enable", enable);
		dao.update("updateEnable", map);
	}

	public void closeAll() {
		Map<String, Object> map = new HashMap<String, Object>();
		//1:如果是启动，先把所有设置为0(false)，只能有一个策略生效
		map.put("enable", 0);
		dao.update("updateEnable", map);
	}

	/**
	 * 保存 sys_pwd_strategy 信息
	 * 
	 * @param pwdStrategy
	 */
	public void save(PwdStrategy pwdStrategy) {
		Long id = pwdStrategy.getId();
		if (pwdStrategy.getEnable() == 1) {//启动，那么就先关闭所有
			closeAll();
		}

		if (id == null || id == 0) {
			id = UniqueIdUtil.genId();
			pwdStrategy.setId(id);
			this.add(pwdStrategy);
		} else {
			this.update(pwdStrategy);
		}
	}

	/**
	 * 获取系统中生效的密码策略
	 * 
	 * @return PwdStrategy
	 * @exception
	 * @since 1.0.0
	 */
	public PwdStrategy getUsing() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("enable", 1);
		return (PwdStrategy) dao.getOne("getByEnable", map);
	}
	
	/**
	 * 用系统中生效的密码规则
	 * 检查这个账户需不需要验证码
	 * 就是说有没有触发在使用中的密码规则
	 * @param account	账号
	 * @return 
	 * boolean	：true:需要，false:不需要
	 * @exception 
	 * @since  1.0.0
	 */
	public boolean checkUserVcodeEnabled(String account){
		PwdStrategy pwdStrategy = getUsing();
		if(pwdStrategy==null) return false;
		int failCount = loginLogService.getTodayFailCount(account);
		if (pwdStrategy.getVerifyCodeAppear() > 0 && failCount >= pwdStrategy.getVerifyCodeAppear()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 用系统中生效的密码规则
	 * 在登录失败后，先检查用户登录失败次数是否超过规则，而锁住
	 * 2 然后检查密码是否过期 而锁住
	 * @param account
	 * @return 
	 * boolean：true:需要，false:不需要
	 * @exception 
	 * @since  1.0.0
	 */
	public boolean checkUserLockable(String account){
		SysUser user = sysUserService.getByAccount(account);
		if(user==null||user.getIsLock()==SysUser.LOCKED){//没有用户或者用户已被锁住
			return false;
		}
		PwdStrategy pwdStrategy = getUsing();
		if(pwdStrategy==null) return false;
		
		//先检查错误次数的规则
		int failCount = loginLogService.getTodayFailCount(account);
		if (pwdStrategy.getErrLockAccount() > 0 && failCount >= pwdStrategy.getErrLockAccount()) {
			return true;
		}
		
		//检查密码过期的规则
		if (pwdStrategy.getValidity() > 0) {
			long validity = Long.parseLong(pwdStrategy.getValidity().toString()) * 2592000 * 1000;//有效期，换算成毫秒数
			long pwdUpdTime = user.getPwdUpdTime().getTime();
			long now = new Date().getTime();
			if (now - pwdUpdTime > validity) {//已过了期限
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取系统中生效的策略的初始化密码
	 * @return 
	 * String
	 * @exception 
	 * @since  1.0.0
	 */
	public String getUsingInitPwd(){
		PwdStrategy pwdStrategy = getUsing();
		if(pwdStrategy==null)
			return "";
		return pwdStrategy.getInitPwd();
	}
	
	/**
	 * 检查系统密码规则
	 * 
	 * @param pwdStrategy
	 * @param user
	 *            :用户
	 * @param pwd
	 *            :没有加密前的密码
	 * @return JSONObject
	 * @throws Exception 
	 * @exception
	 * @since 1.0.0
	 */
	public JSONObject checkUser(SysUser user, String pwd) throws Exception {
		return checkUser(getUsing(), user, pwd);
	}

	/**
	 * 
	 * @param pwdStrategy
	 * @param user
	 * @param pwd
	 *            :没有加密前的密码
	 * @return JSONObject
	 * @throws Exception 
	 * @exception
	 * @since 1.0.0
	 */
	private JSONObject checkUser(PwdStrategy pwdStrategy, SysUser user, String pwd) throws Exception {

		JSONObject result = new JSONObject();
		if (pwdStrategy == null) {
			result.put("status", PwdStrategy.Status.SUCCESS);
			result.put("msg", "登录成功");
			return result;
		}

		//检查初始化密码
		if (StringUtil.isEmpty(user.getPassword())) {//密码为空就初始化密码
			if (StringUtil.isNotEmpty(pwdStrategy.getInitPwd())) {
				String enPassword = EncryptUtil.encrypt32MD5(pwdStrategy.getInitPwd());
				user.setPassword(enPassword);
				result.put("status", PwdStrategy.Status.PWD_INIT);
				result.put("msg", "初始化密码成功");
				return result;
			}
		}
		
		//强制修改初始化密码
		if(pwd.equals(pwdStrategy.getInitPwd())&&pwdStrategy.getForceChangeInitPwd()==1){
			result.put("status", PwdStrategy.Status.NEED_TO_CHANGE_PWD);
			result.put("msg", "密码与初始化密码一致，需要强制修改");
			return result;
		}
		
		//检查密码策略
		if (pwdStrategy.getPwdRule() != 0) {//非无限制
			if (pwdStrategy.getPwdRule() == 1 && !pwd.matches("^(?!^\\d+$)(?!^[a-zA-Z]+$)[0-9a-zA-Z]{2,}$")) {//策略是字母跟数字，但是不符合
				result.put("status", PwdStrategy.Status.NO_MATCH_NUMANDWORD);
				result.put("msg", "密码不符合规则：字母跟数字");
				return result;
			}
			if (pwdStrategy.getPwdRule() == 2) {//字母跟数字跟特殊字符
				//因为这个正则太难写。。所以分为两步完成验证
				//1 先确保密码有特殊字符
				boolean b = false;//是否符合规则
				String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(pwd);
				if (m.find()) {//找到特殊字符
					String temp = m.replaceAll("").trim();//删除特殊字符，开始验证数字和字母
					b = temp.matches("^(?!^\\d+$)(?!^[a-zA-Z]+$)[0-9a-zA-Z]{2,}$");
				}
				if (!b) {
					result.put("status", PwdStrategy.Status.NO_MATCH_NUMANDWORDANDSPECIAL);
					result.put("msg", "密码不符合规则：字母跟数字跟特殊字符");
					return result;
				}
			}

		}

		//检查密码长度
		if (pwdStrategy.getPwdLength() > 0) {
			if (pwd.length() < pwdStrategy.getPwdLength()) {
				result.put("status", PwdStrategy.Status.LENGTH_TOO_SHORT);
				result.put("msg", "密码太短：长度至少为 " + pwdStrategy.getPwdLength());
				return result;
			}
		}

		if (pwdStrategy.getHandleOverdue() != 0) {//在过期会处理的前提下
			//密码有效期
			if (pwdStrategy.getValidity() > 0) {
				long validity = Long.parseLong(pwdStrategy.getValidity().toString()) * 2592000 * 1000;//有效期，换算成毫秒数
				long pwdUpdTime = user.getPwdUpdTime().getTime();
				long now = new Date().getTime();
				if (now - pwdUpdTime > validity) {//已过了期限
					result.put("status", PwdStrategy.Status.PWD_OVERDUE);
					result.put("msg", "密码已过期，您的密码更新在：" + TimeUtil.getDateTimeString(pwdUpdTime) + "，而有效日期是 " + pwdStrategy.getValidity() + " 个月");
					return result;
				}

				//过期提醒
				if (pwdStrategy.getOverdueRemind() > 0) {
					long remind = (long)pwdStrategy.getOverdueRemind() * 1000 * 3600 * 24 * 7;
					if (validity-(now-pwdUpdTime)<remind) {//开始要提醒了
						result.put("remind", true);//提醒标记
						result.put("remindMsg", "您的密码即将过期，您的密码更新时间在：" + TimeUtil.getDateTimeString(pwdUpdTime) + "，而有效日期是 " + pwdStrategy.getValidity() + " 个月");
					}else{
						result.put("remind", false);
					}
				}
				
			}
		}
		
		result.put("status", PwdStrategy.Status.SUCCESS);
		result.put("msg", "登录成功");
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(new Date(new Long("1435801763092")));
	}
}
