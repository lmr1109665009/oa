package com.suneee.platform.service.system;

import com.suneee.core.api.util.ContextUtil;
import com.suneee.core.bpm.model.ProcessTask;
import com.suneee.core.page.PageBean;
import com.suneee.core.page.PageList;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.oa.model.bpm.TaskAmount;
import com.suneee.platform.dao.bpm.BpmProCopytoDao;
import com.suneee.platform.dao.bpm.BpmTaskExeDao;
import com.suneee.platform.dao.bpm.ProcessRunDao;
import com.suneee.platform.dao.bpm.TaskDao;
import com.suneee.platform.dao.mail.OutMailDao;
import com.suneee.platform.dao.system.MessageSendDao;
import com.suneee.platform.model.bpm.BpmDefAuthorizeType;
import com.suneee.platform.model.bpm.BpmDefinition;
import com.suneee.platform.model.bpm.BpmTaskExe;
import com.suneee.platform.model.bpm.ProcessRun;
import com.suneee.platform.model.index.IndexTab;
import com.suneee.platform.model.index.IndexTabList;
import com.suneee.platform.model.index.Infobox;
import com.suneee.platform.model.mail.OutMail;
import com.suneee.platform.model.system.*;
import com.suneee.platform.model.util.WarningSetting;
import com.suneee.platform.service.bpm.BpmDefAuthorizeService;
import com.suneee.platform.service.bpm.BpmDefinitionService;
import com.suneee.platform.service.bpm.BpmService;
import com.suneee.platform.service.bpm.TaskReminderService;
import com.suneee.platform.web.listener.UserSessionListener;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class IndexService {

	@Resource
	private TaskDao taskDao;
	@Resource
	private MessageSendDao messageSendDao;
	@Resource
	private ProcessRunDao processRunDao;
	@Resource
	private OutMailDao outMailDao;
	@Resource
	private BpmDefinitionService bpmDefinitionService;
	@Resource
	private BpmDefAuthorizeService bpmDefAuthorizeService;
	@Resource
	private SysBulletinService sysBulletinService;
	@Resource
	private BpmService bpmService;
	@Resource
	private BpmTaskExeDao bpmTaskExeDao;
	@Resource
	private TaskReminderService reminderService;
	@Resource
	private BpmProCopytoDao bpmProCopytoDao;
	@Resource
	private SysPlanService planService;

	public String test(){
		return "测试";
	}

	public IndexTabList processCenter(Integer curPage, Integer pageSize,
                                      String curTab) {
		PageList<ProcessTask> list1 = pendingMatterPage(curPage, pageSize);

		IndexTab tab1 = new IndexTab();
		tab1.setTitle("我的待办");
		tab1.setKey("pendingMatter");
		tab1.setBadge(list1.getTotalCount() + "");
		tab1.setActive(true);
		tab1.setList(list1);

		PageList<?> list2 = completedMattersPage(curPage, pageSize);
		IndexTab tab2 = new IndexTab();
		tab2.setTitle("办结事宜");
		tab2.setKey("completedMatters");
		tab2.setBadge(list2.getTotalCount() + "");
		tab2.setList(list2);

		List<IndexTab> tabList = new ArrayList<IndexTab>();
		tabList.add(tab1);
		tabList.add(tab2);

		IndexTabList indexTabList = new IndexTabList();

		indexTabList.setCurTab(curTab);
		setIndexTabList(indexTabList, curTab, tabList);

		indexTabList.setIndexTabList(tabList);
		return indexTabList;
	}

	private void setIndexTabList(IndexTabList indexTabList, String curTab,
			List<IndexTab> tabList) {
		for (IndexTab indexTab : tabList) {
			if(curTab.equalsIgnoreCase(	indexTab.getKey())){
				if(BeanUtils.isNotEmpty(indexTab.getList()))
					indexTabList.setPageBean(	indexTab.getList().getPageBean());
				indexTab.setActive(true);
			}else{
				indexTab.setActive(false);
			}
		}

	}

	public PageList<ProcessTask> pendingMatterPage(Integer curPage,
                                                   Integer pageSize) {

		PageList<ProcessTask> list = new PageList<ProcessTask>();

		PageBean pageBean = new PageBean();

		try {
			pageBean.setCurrentPage(BeanUtils.isNotEmpty(curPage) ? curPage : 1);
			pageBean.setPagesize(BeanUtils.isNotEmpty(pageSize) ? pageSize : 10);
			list = (PageList<ProcessTask>) taskDao.getTasks(
					ContextUtil.getCurrentUserId(), null, null, null, null,
					"desc", pageBean);
			// 为待办添加上颜色用Description 字段
			Map<Integer, WarningSetting> waringSetMap = reminderService
					.getWaringSetMap();
			for (ProcessTask task : list) {
				int priority = task.getPriority();
				if (waringSetMap.containsKey(priority))
					task.setDescription(waringSetMap.get(priority).getColor());
				else
					task.setDescription("");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<ProcessTask> pendingMatters(Integer curPage, Integer pageSize) {

		List<ProcessTask> list = new ArrayList<ProcessTask>();

		PageBean pb = new PageBean();

		try {
			pb.setCurrentPage(BeanUtils.isNotEmpty(curPage) ? curPage : 1);
			pb.setPagesize(BeanUtils.isNotEmpty(pageSize) ? pageSize : 10);
			list = taskDao.getTasks(ContextUtil.getCurrentUserId(), null, null,
					null, null, "desc", pb);
			// 为待办添加上颜色用Description 字段
			Map<Integer, WarningSetting> waringSetMap = reminderService
					.getWaringSetMap();
			for (ProcessTask task : list) {
				int priority = task.getPriority();
				if (waringSetMap.containsKey(priority))
					task.setDescription(waringSetMap.get(priority).getColor());
				else
					task.setDescription("");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 待办任务
	 *
	 * @return
	 */
	public List<ProcessTask> pendingMatters() {
		return pendingMatters(1, 10);
	}

	/**
	 * 个人信息
	 *
	 * @return
	 */
	public SysUser userInfo() {
		return (SysUser) ContextUtil.getCurrentUser();
	}

	/**
	 * 内部消息
	 *
	 * @return
	 */
	public List<?> getMessage() {
		List<?> list = new ArrayList<MessageSend>();

		PageBean pb = new PageBean();
		pb.setCurrentPage(1);
		pb.setPagesize(10);
		try {
			list = messageSendDao.getNotReadMsgByUserId(
					ContextUtil.getCurrentUserId(), pb);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 我的审批的流程
	 *
	 * @return
	 */
	public List<ProcessRun> myAttend() {
		List<ProcessRun> list = new ArrayList<ProcessRun>();
		PageBean pb = new PageBean();
		pb.setCurrentPage(1);
		pb.setPagesize(10);
		// 去掉进行分页的总记录数的查询
		pb.setShowTotal(false);
		try {
			list = processRunDao.getMyAttend(ContextUtil.getCurrentUserId(),
					null, pb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 我发起的流程
	 *
	 * @return
	 */
	public List<ProcessRun> myStart() {
		List<ProcessRun> list = new ArrayList<ProcessRun>();
		PageBean pb = new PageBean();
		pb.setCurrentPage(1);
		pb.setPagesize(10);
		try {
			list = processRunDao.myStart(ContextUtil.getCurrentUserId(), pb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取用户未读邮件。 以时间降序排序，最多取10条。
	 *
	 * @return 用户未读邮件对象列表
	 */
	public List<OutMail> myNewMail() {
		List<OutMail> list = new ArrayList<OutMail>();
		PageBean pb = new PageBean();
		pb.setCurrentPage(1);
		pb.setPagesize(10);
		try {
			list = outMailDao.getMailByUserId(ContextUtil.getCurrentUserId(),
					pb);
		} catch (Exception e) {
			e.getStackTrace();
		}

		return list;

	}

	/**
	 * 获取用户可以访问的流程定义
	 *
	 * @return
	 */
	public List<BpmDefinition> myProcess() {
		List<BpmDefinition> list = new ArrayList<BpmDefinition>();
		Long curUserId = ContextUtil.getCurrentUserId();
		try {
			// 获得流程分管授权与用户相关的信息
			Map<String, Object> actRightMap = bpmDefAuthorizeService
					.getActRightByUserMap(curUserId,
							BpmDefAuthorizeType.BPMDEFAUTHORIZE_RIGHT_TYPE.START, false, false);
			// 获得流程分管授权与用户相关的信息集合的流程KEY
			String actRights = (String) actRightMap.get("authorizeIds");
			list = bpmDefinitionService.getMyDefListForDesktop(actRights);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 我的办结
	 *
	 * @return
	 */
	public List<ProcessRun> myCompleted() {
		List<ProcessRun> list = new ArrayList<ProcessRun>();
		long curUserId = ContextUtil.getCurrentUserId();
		PageBean pb = new PageBean();
		pb.setCurrentPage(1);
		pb.setPagesize(10);
		// 去掉进行分页的总记录数的查询
		pb.setShowTotal(false);
		try {
			list = processRunDao.getMyCompletedList(curUserId, pb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 已办事宜
	 *
	 * @return
	 */
	public List<ProcessRun> alreadyMatters() {
		List<ProcessRun> list = new ArrayList<ProcessRun>();
		PageBean pb = new PageBean();
		pb.setCurrentPage(1);
		pb.setPagesize(10);
		// 去掉进行分页的总记录数的查询
		pb.setShowTotal(false);
		try {
			list = processRunDao.Myalready(ContextUtil.getCurrentUserId(), pb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public PageList<ProcessRun> completedMattersPage(Integer curPage,
			Integer pageSize) {
		PageList<ProcessRun> list = new PageList<ProcessRun>();
		PageBean pb = new PageBean();
		pb.setCurrentPage((BeanUtils.isNotEmpty(curPage) ? curPage : 1));
		pb.setPagesize(BeanUtils.isNotEmpty(pageSize) ? pageSize : 10);
		try {
			list = (PageList<ProcessRun>) processRunDao.completedMatters(
					ContextUtil.getCurrentUserId(), pb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 办结事宜
	 *
	 * @return
	 */
	public List<ProcessRun> completedMatters() {
		List<ProcessRun> list = new ArrayList<ProcessRun>();
		PageBean pb = new PageBean();
		pb.setCurrentPage(1);
		pb.setPagesize(10);
		// 去掉进行分页的总记录数的查询
		pb.setShowTotal(false);
		try {
			list = processRunDao.completedMatters(
					ContextUtil.getCurrentUserId(), pb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 公告信息
	 *
	 * @return
	 */
	public List<SysBulletin> getBulletin(String alias) {
		List<SysBulletin> list = sysBulletinService.getAllByAlias(alias);
		return list;
	}

	/**
	 * 个人信息
	 *
	 * @return
	 */
	public Map<String, Object> getCurUser() {
		Map<String, Object> root = new HashMap<String, Object>();
		SysUser user = (SysUser) ContextUtil.getCurrentUser();
		root.put("user", user);
		SysOrg org = (SysOrg) ContextUtil.getCurrentOrg();
		root.put("org", org);
		Position pos = (Position) ContextUtil.getCurrentPos();
		root.put("pos", pos);
		return root;
	}

	/**
	 * 信息盒子
	 *
	 * @return
	 */
	public List<Infobox> getInfobox() {
		List<Infobox> list = new ArrayList<Infobox>();
		try {
			Infobox myTaksBox = this.getMyTaksBox();	//	我的待办
			Infobox myMessBox = this.getMyMessBox();	//	内部消息
			Infobox myProCopytoBox = this.getMyProCopytoBox();	//	抄送转发
			Infobox myAlreadyCompletedBox = getMyAlreadyCompletedBox();	//	已办、办结事宜
			Infobox myCompletedBox = getMyCompletedBox();	//	我的办结

			Infobox myAccordingMattersBox = this.getMyAccordingMattersBox();	//	转办代理事宜
			Infobox myRequestBox = this.getMyRequestBox();	//	 我的请求
			Infobox myDraftBox = this.getMyDraftBox();	//	 我的草稿
			Infobox myPlanBox = this.getMyPlanBox();	//	我的日程
			Infobox onLineUsersBox = getOnLineUsersBox(); //	在线人数

			list.add(myTaksBox);	//	我的待办
			list.add(myMessBox);	//	内部消息
			list.add(myProCopytoBox);	//	抄送转发
			list.add(myAlreadyCompletedBox);	//	已办、办结事宜
			list.add(myCompletedBox);	//	我的办结

			list.add(myAccordingMattersBox);	//	转办代理事宜
			list.add(myRequestBox);	//	我的请求
			list.add(myDraftBox);	//	我的草稿
			list.add(myPlanBox);	//	我的日程
			list.add(onLineUsersBox);	//	在线人数

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	/**
	 * 在线人数
	 * @return
	 */
	private Infobox getOnLineUsersBox() {
		Infobox infobox = new Infobox();
		infobox.setType(2);
		infobox.setColor(Infobox.COLOR_LIGHT_BROWN);
		infobox.setDataText(UserSessionListener.getOnLineUsers().size() + "");
		infobox.setDataContent("在线人数");
		infobox.setData("196,128,202,177,154,94,100,170,224");
		return infobox;
	}

	/**
	 * 我的日程
	 * @return
	 */
	private Infobox getMyPlanBox() {
		Infobox infobox = new Infobox();
		infobox.setIcon("fa-check-square-o");
		infobox.setColor(Infobox.COLOR_BROWN);
		infobox.setDataContent("我的日程");
		infobox.setUrl("/platform/system/sysPlan/myList.ht");

		QueryFilter filter = new QueryFilter(new JSONObject());
		filter.addFilter("rate", 0);
		filter.addFilter("userId", ContextUtil.getCurrentUserId());
		List plans = planService.getMyList(filter);
		infobox.setDataText(plans.size() + "");
		return infobox;
	}

	/**
	 * 我的草稿
	 * @return
	 */
	private Infobox getMyDraftBox() {
		List<ProcessRun> list = processRunDao.getMyDraft(ContextUtil.getCurrentUserId(), null);
		Infobox infobox = new Infobox();
		infobox.setIcon("fa-pencil-square-o");
		infobox.setColor(Infobox.COLOR_WOOD);
		infobox.setDataText(list.size() + "");
		infobox.setDataContent("我的草稿");
		infobox.setUrl("/platform/bpm/processRun/myForm.ht");
		return infobox;
	}

	/**
	 * 我的办结
	 * @return
	 */
	private Infobox getMyCompletedBox() {
		List<ProcessRun> list = processRunDao.getMyCompletedList(ContextUtil.getCurrentUserId(),null);
		Infobox infobox = new Infobox();
		infobox.setIcon("fa-check-square-o");
		infobox.setColor(Infobox.COLOR_BROWN);
		infobox.setDataText(list.size() + "");
		infobox.setDataContent("我的办结");
		infobox.setUrl("/platform/bpm/processRun/myCompleted.ht");
		return infobox;
	}

	/**
	 * 我的请求
	 * @return
	 */
	private Infobox getMyRequestBox() {
		List<ProcessRun> list = processRunDao.getMyRequestList(ContextUtil.getCurrentUserId(),null);
		Infobox infobox = new Infobox();
		infobox.setIcon("fa-hand-o-up");
		infobox.setColor(Infobox.COLOR_BLUE2);
		infobox.setDataText(list.size() + "");
		infobox.setDataContent("我的请求");
		infobox.setUrl("/platform/bpm/processRun/myRequest.ht");
		return infobox;
	}

	/**
	 * 转办代理事宜
	 * @return
	 */
	private Infobox getMyAccordingMattersBox() {
		List<BpmTaskExe> list = bpmTaskExeDao.accordingMattersList(ContextUtil.getCurrentUserId(),null);
		Infobox infobox = new Infobox();
		infobox.setIcon("fa-share");
		infobox.setColor(Infobox.COLOR_PINK);
		infobox.setDataText(list.size() + "");
		infobox.setDataContent("转办代理事宜");
		infobox.setUrl("/platform/bpm/bpmTaskExe/accordingMatters.ht");
		return infobox;
	}

	/**
	 * 办结事宜
	 * @return
	 */
	private Infobox getMyCompletedMattersBox() {
		List<ProcessRun> list = processRunDao.completedMatters(ContextUtil.getCurrentUserId(),null);
		Infobox infobox = new Infobox();
		infobox.setIcon("fa-check");
		infobox.setColor(Infobox.COLOR_GREEN);
		infobox.setDataText(list.size() + "");
		infobox.setDataContent("办结事宜");
		infobox.setUrl("/platform/bpm/processRun/completedMatters.ht");
		return infobox;
	}

	/**
	 * 已办事宜
	 * @return
	 */
	private Infobox getMyAlreadyBox() {
		List<ProcessRun> list = processRunDao.Myalready(ContextUtil.getCurrentUserId(), null);
		Infobox infobox = new Infobox();
		infobox.setIcon("fa-flag");
		infobox.setColor(Infobox.COLOR_RED);
		infobox.setDataText(list.size() + "");
		infobox.setDataContent("已办事宜");
		infobox.setUrl("/platform/bpm/processRun/alreadyMatters.ht");
		return infobox;
	}

	/**
	 * 已办、办结事宜
	 * @return
	 */
	private Infobox getMyAlreadyCompletedBox() {
		List<ProcessRun> list = processRunDao.Myalready(ContextUtil.getCurrentUserId(), null);	// 总数还是拿已办的
		Infobox infobox = new Infobox();
		infobox.setIcon("fa-flag");
		infobox.setColor(Infobox.COLOR_RED);
		infobox.setDataText(list.size() + "");
		infobox.setDataContent("已办、办结事宜");
		infobox.setUrl("/platform/bpm/processRun/alreadyCompletedMatters.ht");
		return infobox;
	}

	/**
	 *  查看抄送转发
	 * @return
	 */
	private Infobox getMyProCopytoBox() {
		Integer proCount = bpmProCopytoDao.getCountByUser(ContextUtil
				.getCurrentUserId());
		Integer noReadProCount = bpmProCopytoDao
				.getCountNotReadByUser(ContextUtil.getCurrentUserId());
		Infobox infobox = new Infobox();
		infobox.setIcon("fa-comments");
		infobox.setColor(Infobox.COLOR_BLUE3);
		infobox.setDataText("(" + noReadProCount + "/" + proCount + ")");
		infobox.setDataContent("抄送转发");
		infobox.setUrl("/platform/bpm/bpmProCopyto/myList.ht");
		return infobox;
	}

	/**
	 * 查看内部消息
	 * @return
	 */
	private Infobox getMyMessBox() {
		Integer messCount = messageSendDao
				.getCountReceiverByUser(ContextUtil.getCurrentUserId());
		Integer noReadMessCount = messageSendDao
				.getCountNotReadMsg(ContextUtil.getCurrentUserId());
		Infobox infobox = new Infobox();
		infobox.setIcon("fa-comments");
		infobox.setColor(Infobox.COLOR_BLUE2);
		infobox.setDataText("(" + noReadMessCount + "/" + messCount + ")");
		infobox.setDataContent("内部消息");
		infobox.setUrl("/platform/system/messageReceiver/list.ht");
		return infobox;
	}

	/**
	 * 获取我的待办消息盒子
	 * @return
	 */
	private Infobox getMyTaksBox() {
		List<TaskAmount> countlist = bpmService.getMyTasksCount(ContextUtil.getCurrentUserId());
		int count = 0;
		int notRead = 0;
		for (TaskAmount taskAmount : countlist) {
			count += taskAmount.getTotal();
			notRead += taskAmount.getNotRead();
		}
		Infobox infobox = new Infobox();
		infobox.setIcon("fa-comments");
		infobox.setColor(Infobox.COLOR_BLUE);
		infobox.setDataText("(" + notRead + "/" + count + ")");
		infobox.setDataContent("待办事宜");
		infobox.setUrl("/platform/bpm/task/pendingMatters.ht");
		return infobox;
	}


	/**
	 *
	 * 我的日历例子 [{ title: '所有事件', start: new Date(y, m, 1), backgroundColor:
	 * Theme.colors.blue }, { title: '长事件', start: new Date(y, m, d-5), end: new
	 * Date(y, m, d-2), backgroundColor: Theme.colors.red }, { id: 999, title:
	 * '重复事件', start: new Date(y, m, d-3, 16, 0), allDay: false,
	 * backgroundColor: Theme.colors.yellow }, { id: 999, title: '重复事件', start:
	 * new Date(y, m, d+4, 16, 0), allDay: false, backgroundColor:
	 * Theme.colors.primary }, { title: '会议', start: new Date(y, m, d, 10, 30),
	 * allDay: false, backgroundColor: Theme.colors.green }, { title: '午餐',
	 * start: new Date(y, m, d, 12, 0), end: new Date(y, m, d, 14, 0), allDay:
	 * false, backgroundColor: Theme.colors.red }, { title: '生日聚会', start: new
	 * Date(y, m, d+1, 19, 0), end: new Date(y, m, d+1, 22, 30), allDay: false,
	 * backgroundColor: Theme.colors.gray }, { title: '链接到百度', start: new
	 * Date(y, m, 28), end: new Date(y, m, 29), url: 'http://www.baidu.com/',
	 * backgroundColor: Theme.colors.green }
	 *
	 *
	 * @return
	 */
	public String myCalendar() {

		JSONArray jsonAry = new JSONArray();

		// {title: '所有事件',start: new Date(y, m, 1),backgroundColor:
		// Theme.colors.blue

		/*for (int i = 0; i < 100; i++) {
			JSONObject json = new JSONObject();
			json.accumulate("title", "所有事件")
					.accumulate("start",
							DateFormatUtil.format(new Date(), "yyyy-MM-dd"))
					.accumulate("backgroundColor", "#70AFC4")
					.accumulate("eventClick",
							"function(calEvent){" + "alert(calEvent.title)}");
			jsonAry.add(json);

		}*/

		//
		JSONObject json2 = new JSONObject();
		Calendar ca = Calendar.getInstance();

		// {title: '所有事件',start: new Date(y, m, 1),backgroundColor:
		// Theme.colors.blue
		/*json2.accumulate("title", "长事件").accumulate("start",
				DateFormatUtil.format(ca.getTime(), "yyyy-MM-dd"));
		ca.add(Calendar.DAY_OF_MONTH, 3);
		json2.accumulate("end",
				DateFormatUtil.format(ca.getTime(), "yyyy-MM-dd")).accumulate(
				"backgroundColor", "#D9534F");
		jsonAry.add(json2);*/

		/*JSONObject json3 = new JSONObject();
		ca.add(Calendar.DAY_OF_MONTH, 6);*/

		// {title: '所有事件',start: new Date(y, m, 1),backgroundColor:
		// Theme.colors.blue
		/*json3.accumulate("title", "连接到公司网站")
				.accumulate("start",
						DateFormatUtil.format(ca.getTime(), "yyyy-MM-dd"))
				.accumulate("url", "http://www.jee-soft.cn/")
				.accumulate("backgroundColor", "#A8BC7B");
		jsonAry.add(json3);*/
		return jsonAry.toString();
	}

	/**
	 * 柱状图例子
	 *
	 * @return
	 */
	public String barChart() {
		float data1[] = new float[12];
		float data2[] = new float[12];
		// 随机
		for (int i = 0; i <= 11; i++) {
			DecimalFormat dcmFmt = new DecimalFormat("0.0");
			float f1 = new Random().nextFloat() * 1000;
			float f2 = new Random().nextFloat() * 2000;
			data1[i] = Float.parseFloat(dcmFmt.format(f1));
			data2[i] = Float.parseFloat(dcmFmt.format(f2));
		}
		String d1 = JSONArray.fromObject(data1).toString();
		String d2 = JSONArray.fromObject(data2).toString();
		String data = "{title:{text:'',subtext:'纯属虚构'},tooltip:{trigger:'axis'},legend:{data:['蒸发量','降水量']},toolbox:{sho:true,feature:{mark:{sho:true},dataVie:{sho:true,readOnly:false},magicType:{sho:true,type:['line','bar']},restore:{sho:true},saveAsImage:{sho:true}}},calculable:true,xAxis:[{type:'category',data:['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']}],yAxis:[{type:'value'}],series:[{name:'蒸发量',type:'bar',"
				+ "data:"
				+ d1
				+ ",markPoint:{data:[{type:'max',name:'最大值'},{type:'min',name:'最小值'}]},markLine:{data:[{type:'average',name:'平均值'}]}},{name:'降水量',type:'bar',data:"
				+ d2
				+ ",markPoint:{data:[{name:'年最高',value:182.2,xAxis:7,yAxis:183,symbolSize:18},{name:'年最低',value:2.3,xAxis:11,yAxis:3}]},markLine:{data:[{type:'average',name:'平均值'}]}}]}";

		return data;
	}

	/**
	 * 折线图例子
	 *
	 * @return
	 */
	public String lineChart() {
		int data1[] = new int[7];
		int data2[] = new int[7];
		// 随机
		for (int i = 0; i <= 6; i++) {
			data1[i] = (int) (Math.random() * 10) + 15;
			data2[i] = (int) (Math.random() * 10);
		}
		String d1 = JSONArray.fromObject(data1).toString();
		String d2 = JSONArray.fromObject(data2).toString();
		String data = "{" + "title:{" + "text:\"\"," + "subtext:\"纯属虚构\""
				+ "}," + "tooltip:{" + "trigger:\"axis\"" + "}," + "legend:{"
				+ "data:[\"最高气温\",\"最低气温\"]" + "}," + "calculable:\"true\","
				+ "xAxis:[" + "{" + "type:\"category\","
				+ "boundaryGap:\"false\","
				+ "data:[\"周一\",\"周二\",\"周三\",\"周四\",\"周五\",\"周六\",\"周日\"]"
				+ "}" + "]," + "yAxis:[" + "{" + "type:\"value\","
				+ "axisLabel:{" + "formatter:\"{value} °C\"" + "}" + "}" + "],"
				+ "series:[" + "{" + "name:\"最高气温\"," + "type:\"line\","
				+ "data:"
				+ d1
				+ ","
				+ "markPoint:{"
				+ "data:["
				+ "{type:\"max\",name:\"最大值\"},"
				+ "{type:\"min\",name:\"最小值\"}"
				+ "]"
				+ "},markLine:{"
				+ "data:["
				+ "{type:\"average\",name:\"平均值\"}"
				+ "]"
				+ "}"
				+ "},"
				+ "{name:\"最低气温\","
				+ "type:\"line\","
				+ "data:"
				+ d2
				+ ","
				+ "markPoint:{"
				+ "data:["
				+ "{name:\"周最低\",value:\"-2\",xAxis:\"1\",yAxis:\"-1.5\"}"
				+ "]"
				+ "},"
				+ "markLine:{"
				+ "data:["
				+ "{type:\"average\",name:\"平均值\"}" + "]" + "}" + "}" + "]}";
		return data;

	}

	public Map<String,Object> mobileProcessStatusInfo(){
		Map<String,Object> dataMap=new HashMap<String, Object>();
		//待办数量
		int pendingCount=0;
		List<TaskAmount> countList = bpmService.getMyTasksCount(ContextUtil.getCurrentUserId());
		for (TaskAmount amount:countList){
			pendingCount+=amount.getTotal();
		}

		//我的承办
		List<ProcessRun> matterList = processRunDao.Myalready(ContextUtil.getCurrentUserId(), null);
		//我的办结
		List<ProcessRun> completedList = processRunDao.getMyCompletedList(ContextUtil.getCurrentUserId(),null);
		//我的申请
		List<ProcessRun> myRequestList = processRunDao.getMyRequestList(ContextUtil.getCurrentUserId(),null);
		dataMap.put("pendingCount",pendingCount);
		dataMap.put("myCompleteCount",completedList.size());
		dataMap.put("myMatterCount",matterList.size());
		dataMap.put("myRequestCount",myRequestList.size());
		return dataMap;
	}

}
