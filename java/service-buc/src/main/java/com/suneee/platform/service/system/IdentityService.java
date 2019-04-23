package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.db.IEntityDao;
import com.suneee.core.service.BaseService;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.util.Dom4jUtil;
import com.suneee.core.util.UniqueIdUtil;
import com.suneee.core.util.XmlBeanUtil;
import com.suneee.platform.dao.system.IdentityDao;
import com.suneee.platform.model.system.Identity;
import com.suneee.platform.model.system.SysOrg;
import com.suneee.platform.xml.system.IdentityXml;
import com.suneee.platform.xml.system.IdentityXmlList;
import com.suneee.platform.xml.util.MsgUtil;
import com.suneee.platform.xml.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 对象功能:流水号生成 Service类
 * 开发公司:广州宏天软件有限公司
 * 开发人员:ray
 * 创建时间:2012-02-03 14:40:59
 */
@Service
public class IdentityService extends BaseService<Identity>
{
	private static Logger logger = LoggerFactory.getLogger(IdentityService.class);
	
	
	@Resource
	private IdentityDao dao;
	
	public IdentityService()
	{
	}
	
	@Override
	protected IEntityDao<Identity, Long> getEntityDao()
	{
		return dao;
	}
	
	
	/**
	 * 根据别名是否存在。
	 * @param alias
	 * @return
	 */
	public boolean isAliasExisted(String alias){
		return dao.isAliasExisted(alias);
	}
	
	/**
	 * 根据别名是否存在。
	 * @param identity
	 * @return
	 */
	public boolean isAliasExistedByUpdate(Identity identity)
	{
		return dao.isAliasExistedByUpdate(identity);
	}
	

	
	/**
	 * 根据流程规则别名获取得下一个流水号。
	 * @param alias		流水号规则别名。
	 * @return
	 * @throws InterruptedException 
	 */
	public  synchronized String  nextId(String alias){
		Result result=getResult(alias);
		int i=0;
		while(result.getResult()==0){
			i++;
			result=getResult(alias);
		}
		return result.getNo();
	}
	
	/**
	 * 根据别名产生预览流水号，不会正真写入数据库。
	 * @param alias
	 * @return
	 */
	public String preview(String alias){
		Identity identity=this.dao.getByAlias(alias);
		String rule=identity.getRule();
		int step=identity.getStep();
		short genType=identity.getGenType();
		Long curValue=identity.getCurValue();
		if( curValue==-1L) curValue=new Long( identity.getInitValue());
		//每天每月每年生成。
		if(genType>0){
			String curDate=getCurDate(genType);
			String oldDate=identity.getCurDate();
			if(!oldDate.startsWith(curDate)){
				identity.setCurDate(curDate);
				curValue=new Long( identity.getInitValue());
			}
			else{
				curValue=curValue + step;
			}
		}
		else{
			curValue=curValue + step;
		}
		identity.setNewCurValue(curValue);
		
		String rtn=getByRule(rule,identity.getNoLength(),curValue);
		
		return rtn;
	}
	
	private Result getResult(String alias){
		Identity identity=this.dao.getByAlias(alias);
		String rule=identity.getRule();
		int step=identity.getStep();
		short genType=identity.getGenType();
		Long curValue=identity.getCurValue();
		if( curValue==-1L) curValue=new Long( identity.getInitValue());
		//每天每月每年生成。
		if(genType>0){
			String curDate=getCurDate(genType);
			String oldDate=identity.getCurDate();
			if(!oldDate.startsWith(curDate)){
				identity.setCurDate(curDate);
				curValue=new Long( identity.getInitValue());
			}
			else{
				curValue=curValue + step;
			}
		}
		else{
			curValue=curValue + step;
		}
		identity.setNewCurValue(curValue);
		
		int i=dao.updateVersion(identity);
		
		Result result=new Result();
		
		if(i>0){
			String rtn=getByRule(rule,identity.getNoLength(),curValue);
			result=new Result();
			result.setResult(1);
			result.setNo(rtn);
		}
		
		return result;
	}
	
	/**
	 * 返回当前日期。格式为 年月日。
	 * @return
	 */
	public static String getCurDate(short genType){
		Date date=new Date();
		String str="";
		String month = date.getMonth() < 10 ? "0" + (date.getMonth()+1) : (date.getMonth()+1) + "";
		String day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate() + "";
		switch(genType){
			case 1: 
				str=(date.getYear() +1900) +"" +month+"" +day;
				break;
			case 2:
				str=(date.getYear() +1900) +"" +month;
				break;
			case 3:
				str=(date.getYear() +1900) +"";
				break;
		}
		
		return str;
		
	}
	
	/**
	 * 根据规则返回需要显示的流水号。
	 * @param rule			流水号规则。
	 * @param length		流水号的长度。
	 * @param curValue		流水号的当前值。
	 * @return
	 */
	private String getByRule(String rule,int length,  Long curValue){
		Date date=new Date();
		
		String year=(date.getYear() +1900) +"";
		int month=date.getMonth() +1;
		int day=date.getDate();
		String shortMonth="" + month;
		String longMonth=(month<10)?"0" + month :"" + month;
		
		String seqNo=getSeqNo(rule,curValue,length);
		
		String shortDay="" + day;
		String longDay=(day<10)?"0" + day :"" + day;
		
		String rtn=rule.replace("{yyyy}", year)
				.replace("{MM}", longMonth)
				.replace("{mm}", shortMonth)
				.replace("{DD}", longDay)
				.replace("{dd}", shortDay)
				.replace("{NO}", seqNo)
				.replace("{no}", seqNo);
		
		if(rtn.indexOf("{ORG}")!=-1){
			SysOrg sysOrg=(SysOrg) ContextUtil.getCurrentOrg();
			if(sysOrg!=null&&sysOrg.getCode()!=null){
				rtn=rtn.replace("{ORG}", sysOrg.getCode());
			}else{
				rtn=rtn.replace("{ORG}", "");
			}
		}
		
		
		
		return rtn;
	}
	
	/**
	 * 根据当前流水号的值和流水号显示的长度。
	 * <pre>
	 * 比如：当前流水号为55 ，显示长度为5那么这个方法返回：00055。
	 * </pre>
	 * @param curValue		当前流水号的值。
	 * @param length		显示的长度。
	 * @return
	 */
	private static String getSeqNo(String rule,Long curValue,int length){
		String tmp=curValue +"";
		int len= 0 ;
		if(rule.indexOf("no")>-1){
			len = length;
		}else{
			len = length-tmp.length();
		}
		String rtn="";
		switch (len) {
			case 1:
				rtn= "0";
				break;
			case 2:
				rtn= "00";
				break;
			case 3:
				rtn= "000";
				break;
			case 4:
				rtn= "0000";
				break;
			case 5:
				rtn= "00000";
				break;
			case 6:
				rtn= "000000";
				break;
			case 7:
				rtn= "0000000";
				break;
			case 8:
				rtn= "00000000";
				break;
			case 9:
				rtn= "000000000";
				break;
			case 10:
				rtn= "0000000000";
				break;
			case 11:
				rtn= "00000000000";
				break;
			case 12:
				rtn= "000000000000";
				break;
		}
		if(rule.indexOf("no")>-1){
			return tmp + rtn;
		}else{
			return rtn + tmp;
		}
		
	}
	
	/**
	 * 获取流水号规则列表。
	 * @return
	 */
	public List<Identity> getList(){
		return dao.getList();
	}
	
	/**
	 * 根据alias获取流水号详细信息
	 * @param alias
	 * @return
	 */
	public Identity getByAlias(String alias) {
		Identity identity=this.dao.getByAlias(alias);
		return identity;
	}
	
	class Result{
		private int result=0;
		private String no="";
		
		
		public int getResult() {
			return result;
		}
		public void setResult(int result) {
			this.result = result;
		}
		public String getNo() {
			return no;
		}
		public void setNo(String no) {
			this.no = no;
		}
		
		
	}
	/**
	 * 导出全部流水号
	 * @param  identities
	 * @return
	 * @throws Exception
	 */
	public String exportXml(List<Identity> identities) throws Exception {
		IdentityXmlList identityXmlList = new IdentityXmlList();
		List<IdentityXml> list = new ArrayList<IdentityXml>();
		for (Identity identity:identities) {
	
			IdentityXml identityXml =this.exportIdentityXml(identity);
			list.add(identityXml);
		}
		identityXmlList.setIdentityXmlList(list);
		return XmlBeanUtil.marshall(identityXmlList, IdentityXmlList.class);
	}
	/**
	 * 导出流水号XML
	 * @param tableIds
	 * @return
	 * @throws Exception
	 */
	public String exportXml(Long[] tableIds) throws Exception {
		IdentityXmlList identityXmlList = new IdentityXmlList();
		List<IdentityXml> list = new ArrayList<IdentityXml>();
		for (int i = 0; i < tableIds.length; i++) {
			Identity identity = dao.getById(tableIds[i]);
			IdentityXml identityXml =this.exportIdentityXml(identity);
			list.add(identityXml);
		}
		identityXmlList.setIdentityXmlList(list);
		return XmlBeanUtil.marshall(identityXmlList, IdentityXmlList.class);
	}
	/**
	 * 导出表的信息
	 * @param identity
	 * @param map
	 * @return
	 */
	private IdentityXml exportIdentityXml(Identity identity)throws Exception
	{
		IdentityXml identityXml=new IdentityXml();
		Long id=identity.getId();
		if(BeanUtils.isNotIncZeroEmpty(id) ){
			//导出流水号
			identityXml.setIdentity(identity);
		}
		return identityXml;
	}
	/**
	 * 导入流水号XML
	 * @param inputStream
	 * @throws Exception
	 */
	public void importXml(InputStream inputStream) throws Exception {
		Document doc = Dom4jUtil.loadXml(inputStream);
		Element root = doc.getRootElement();
		// 验证格式是否正确
		XmlUtil.checkXmlFormat(root, "system", "identities");

		String xmlStr = root.asXML();
		IdentityXmlList identityXmlList = (IdentityXmlList) XmlBeanUtil
				.unmarshall(xmlStr, IdentityXmlList.class);

		List<IdentityXml> list = identityXmlList.getIdentityXmlList();

		for (IdentityXml identityXml : list) {
			// 导入表，并解析相关信息
			this.importIdentityXml(identityXml);

		}
		

	}
	/**
	 * 导入时生流水号
	 * @param identityXml
	 * @return
	 * @throws Exception
	 */
	private void importIdentityXml(IdentityXml identityXml) throws Exception {
		Long identityId = UniqueIdUtil.genId();
		Identity identity = identityXml.getIdentity();
		if (BeanUtils.isEmpty(identity)) {
			// MsgUtil.addMsg(MsgUtil.WARN, "什么内容也没有，请检查你的Xml文件！");
			// return;
			throw new Exception();
		}
		String alias = identity.getAlias();

		if (this.isAliasExisted(alias)) {
			MsgUtil.addMsg(MsgUtil.WARN, "别名为‘" + alias
					+ "’的流水号已经存在，请检查你的xml文件！");
			return;
		}
		identity.setId(identityId);
		dao.add(identity);
		MsgUtil.addMsg(MsgUtil.SUCCESS, "别名为" + alias + "的流水号导入成功！");
	}

}
