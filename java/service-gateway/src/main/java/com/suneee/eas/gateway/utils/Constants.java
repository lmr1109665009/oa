/**
 * 
 */
package com.suneee.eas.gateway.utils;

/**
 * @author Administrator
 *
 */
public interface Constants {
	public static final String PREFIX_UCENTER="zuul.authentication.ucenter";
	/** 用户默认密码 **/
	public static final String USER_DEFAULT_PASSWORD = PREFIX_UCENTER+".user.default.password";
	/** 用户默认角色ID **/
	public static final String USER_DEFAULT_ROLEID = PREFIX_UCENTER+".user.default.roleId";
	/** 用户默认角色名称 **/
	public static final String USER_DEFAULT_ROLENAME = PREFIX_UCENTER+".user.default.roleName";
	/** 是否过期 **/
	public static final String USER_DEFAULT_ISEXPIRED = PREFIX_UCENTER+".user.default.isExpired";
	/** 是否锁定 */
	public static final String USER_DEFAULT_ISLOCK = PREFIX_UCENTER+".user.default.isLock";
	/** 当前状态 **/
	public static final String USER_DEFAULT_STATUS = PREFIX_UCENTER+".user.default.status";
	/** 员工状态 **/
	public static final String USER_DEFAULT_USERSTATUS = PREFIX_UCENTER+".user.default.userStatus";
	/** 性别 **/
	public static final String USER_DEFAULT_SEX = PREFIX_UCENTER+".user.default.sex";
	/** 邮箱后缀 **/
	public static final String USER_DEFAULT_EMAILSUFFIX = "user.default.emailsuffix";
	/** 导入组织及用户模板保存路径**/
	public static final String FILE_UPLOAD = "file.upload";

	/** 异常数据文件存放路径 **/
	public static final String IMPORT_ERRORDATA_FILEPATH = "import.errordata.filepath";
	
	/** 用户中心接口地址 **/
	public static final String UC_API_URL = PREFIX_UCENTER+".api.auth";
	/** 账号 **/
	public static final String UC_LOGIN_ACCOUNT = PREFIX_UCENTER+".login.account";
	/** 密码 **/
	public static final String UC_LOGIN_PASSWORD = PREFIX_UCENTER+".login.password";
	/** 客户端IP **/
	public static final String UC_CLIENTIP = PREFIX_UCENTER+".clientIp";
	/** 应用系统编码 **/
	public static final String UC_APPCODE = PREFIX_UCENTER+".appCode";
	/** 企业编码 **/
	public static final String UC_ENTERPRISECODE = PREFIX_UCENTER+".enterpriseCode";
	/** 加密密钥 **/
	public static final String UC_ENCRYPTCODE = PREFIX_UCENTER+".encryptCode";
	/** 数据变动来源系统 **/
	public static final String UC_SYSTEM = PREFIX_UCENTER+".system";
	/** 同步到用户中心的默认密码 **/
	public static final String UC_DEFAULT_PASSWORD = ".default.password";
	/** 用户中心批量导入接口 **/
	public static final String UC_BATCHIMPORT_API = "enterprise-api.directImportUsers";
	/** 用户中心sessionId授权登陆接口 **/
	public static final String UC_ACCESSLOGIN_API = "personal-api.accessLogin";
	/** 用户中心接口：获取用户接口 **/
	public static final String UC_GETUSER_API = "user-api.findBymobileOremail";

	/** lhf:用户中心接口:根据sessionId获取用户数据**/
	public static final String UC_GETBYUSER = "user-api.findBySessionId";
	/** 用户中心接口：根据条件获取用户信息 **/
	public static final String UC_GETUSERBYCONDITION_API = "user-api.checkParams";
	/** 用户中心接口：更新用户组织关系接口 **/
	public static final String UC_UPDUSERORG_API = "user-api.updateUserCToB";
	/** 用户中心接口：更新用户组织关系接口(新) **/
	public static final String UC_UPDUSERORG_API_NEW = "user-api.updateUserCToBNewEdition";
	/** 用户中心接口：删除用户组织关系接口  **/
	public static final String UC_DELUSERORG_API = "user-api.deleteEnterprisUser";
	/** 用户中心接口：删除用户组织关系接口(新) **/
	public static final String UC_DELUSERORG_API_NEW = "user-api.deleteEnterprisUserNewEdition";
	/** 用户中心接口：更新用户信息接口 **/
	public static final String UC_UPDUSER_API = "user-api.oAupdateUserInfo";
	/** 用户中心接口： 添加B用户接口**/
	public static final String UC_ADDUSER_API= "user-api.registerToBUser";
	/** 用户中心接口： 添加B用户接口(新)**/
	public static final String UC_ADDUSER_API_NEW = "user-api.registerToBUserNewEdition";
	/** 用户中心接口：删除用户**/
	public static final String UC_DELUSER_API = "user-api.deleteUser";
	/** 用户中心接口：恢复删除的用户**/
	public static final String UC_REVUSER_API = "user-api.oAenabledUser";
	/** 用户中心接口成功标识 **/
	public static final String UC_API_SUCCESS = "1";
	/** 用户中心接口失败标识 **/
	public static final String UC_API_FAILED = "2";
	/** 调用用户中心删除用户接口失败时用户数据存放路径 **/
	public static final String UC_DELETE_FAILED_FILEPATH="uc.delete.failed.filepath";
	
	/** 字符编码：utf-8 **/
	public static final String CHARSET_UTF8 = "UTF-8";
	
	/**  时间格式：yyyy-MM **/
	public static final String FORMAT_YEAR_MONTH = "yyyy-MM";
	
	/** 分隔符：逗号 **/
	public static final String SEPARATOR_COMMA = ",";
	/** 分隔符：下划线（_） **/
	public static final String SEPARATOR_UNDERLINE = "_";
	/** 分隔符：空格 **/
	public static final String SEPARATOR_BLANK = " ";
	/** 分隔符：全角空格 **/
	public static final String SEPARATOR_FULL_BLANK = "　";
	/** 分隔符：反斜杠（/） **/
	public static final String SEPARATOR_BACK_SLANT = "/";
	/** 分隔符：井号（#）**/
	public static final String SEPARATOR_POUND_SIGN = "#";
	/** 分隔符：冒号（:）**/
	public static final String SEPARATOR_COLON = ":";
	/** 分隔符：句号（.）**/
	public static final String SEPARATOR_PERIOD = ".";
	/** 系统换行符 **/
	public static final String SEPARATOR_LINE_BREAK = System.getProperty("line.separator", "\n");
	
	/** 消息队列接口地址（用于用户组织消息推送） **/
	public static final String MESSAGE_API_URL = "messagequene.api.url";
	/** 消息中心地址（用于定子链消息推送）**/
	public static final String MESSAGE_DZL_API_URL = "messagequene.dzl.api.url";
	/** 用户消息topic **/
	public static final String MESSAGE_SENDMSG_TOPIC = "messagequene.topic";
	/** 会议室消息topic 
	public static final String MESSAGE_SENDMSG_TOPIC_CONFERENCE = "messagequene.topic.conference";**/
	/** 消息队列发送消息接口名称  **/
	public static final String MESSAGE_SENDMSG_API = "sendMsg";
	/** 消息队列企业编码  **/
	public static final String MESSAGE_ENTERPRISE_CODE = "messagequene.enterprisecode";
	/** 消息发送失败时写入失败消息的文件路径 **/
	public static final String MESSAGE_FAILED_FILEPATH = "messagequene.failed.filepath";
	/** 定子链对接消息队列发送消息接口名称  **/
	public static final String MESSAGE_APOLLO_API = "sendApolloMsg";
	/** 定子链消息topic **/
	public static final String MESSAGE_APOLLO_TOPIC = "messagequene.apollo.topic";
	/** 定子链消息发送端编码 **/
	public static final String MESSAGE_APOLLO_CLIENTCODE = "messagequene.apollo.clientcode";
	
	/** 权限中心接口地址 **/
	public static final String AUTHORITY_CENTER_API_URL = "authority.center.api.url";
	/** 权限中心後台地址 **/
	public static final String AUTHORITY_CENTER_URL ="authority.center.url";

	/** 权限中心接口:获取企业信息列表（获取所有企业信息） **/
	public static final String AUTHORITY_CENTER_ENTERPRISELIST_API = "/company/do_comp_query";
	/** 权限中心接口:获取企业信息列表（获取企业编码对应企业及其子企业信息） **/
	public static final String AUTHORITY_CENTER_ENTERPRISEINFO_API = "/company/do_comp_single_query_bycompanycode";
	/** 接口参数：企业编码 **/
	public static final String AUTHORITY_PARAM_COMPCODE = "authority.param.compcode";
	
	/** 流程实例类型数据字典nodeKey值配置项  **/
	public static final String FLOW_TYPE_KEY = "flow.type.key";
	/** 请假流程字典项itemkey配置项 **/
	public static final String FLOW_TYPE_LEAVE_KEY = "flow.type.leave.key";
	/** 外出流程字典项itemkey配置项  **/
	public static final String FLOW_TYPE_OUT_KEY = "flow.type.out.key";
	/** 出差流程字典项itemkey配置项  **/
	public static final String FLOW_TYPE_BUSINESS_KEY = "flow.type.business.key";
	
	/** 职务级别的数据字典类型nodekey  **/
	public static final String DIC_NODEKEY_ZWJB = "zwjb";
	/** 地区的数据字典类型nodekey  **/
	public static final String DIC_NODEKEY_DQ = "dq";
	/** 定时计划的数据字典类型nodekey  **/
	public static final String DIC_NODEKEY_DSRW = "dsrw";
	/* mqtt消息信息配置  */
	/** mqtt消息监听地址 */
	public static final String MQTT_HOST = "mqtt.host";
	/** mqtt消息监听消息主题 **/
	public static final String MQTT_TOPIC = "mqtt.topic";
	/** mqtt消息接入端唯一标志 **/
	public  static final String MQTT_CLIENTID = "mqtt.clientid";
	/** mqtt消息定时器执行周期 **/
	public  static final String MQTT_SCHEDULE_PERIOD = "mqtt.schedule.period";
	
	
	/* 短信消息相关配置 */
    /** 短信平台接口地址 **/
    public static final String MESSAGE_SMS_URL = "message.sms.url";
    /** 短信签名 **/
    public static final String MESSAGE_SMS_SIGNATURE = "message.sms.signature";
    /** 短信前缀 **/
    public static final String MESSAGE_SMS_PREFIX = "message.sms.prefix";
    /** 消息类型编码 **/
    public static final String MESSAGE_SMS_TYPECODE = "message.sms.typecode";
    /** 短信接口方法 **/
    public static final String MESSAGE_SMS_API = "message-api.sendMessage";
    
    /* http连接超时设置*/
    /**连接超时时间**/
    public static final String HTTP_CONNECTION_TIMEOUT = "http.connectTimeout";
    /**响应超时时间**/
    public static final String HTTP_READ_TIMEOUT = "http.readTimeout";
    
    /* 未登陆时的页面跳转地址 */
    public static final String LOGIN_PAGE_REDIRECT = "login.page.redirect";
    
    /* 格式验证正则表达式 */
    /** 手机号正则 **/
    public static final String REGEX_MOBILE = "^(((1[3456789][0-9]{1})|(15[0-9]{1}))+\\d{8})$";
    /** 邮箱正则 **/
    public static final String REGEX_EMAIL = "^(\\w)+(.\\w+)*@(\\w)+((.\\w+)+)$";
    
    /**
     * 默认的职务分类
     */ 
    public static final String DEFAULT_JOB_CATEGORY = "uc.default.job.category";

	/** 用户中心接口地址 **/
	public static final String HRMS_API_URL = "hrms.api.url";
	public static final String CHANGE_STATUS="api.do?bpmstatus";
	
	/** 融合版本登录页面 **/
	public static final String DEFAULT_LOGIN_URL = "default.login.url";
	/** 通过sessionId获取用户信息**/
	public static final String UC_GETBYUSERANDORG_API="user-api.findBySessionId";
}
