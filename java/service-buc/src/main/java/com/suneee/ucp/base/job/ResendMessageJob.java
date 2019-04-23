/**
 * 
 */
package com.suneee.ucp.base.job;

import java.io.File;
import java.util.Date;

import org.quartz.JobExecutionContext;

import com.suneee.core.scheduler.BaseJob;
import com.suneee.core.util.AppConfigUtil;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.DateFormatUtil;
import com.suneee.core.util.FileUtil;
import com.suneee.ucp.base.common.Constants;
import com.suneee.ucp.base.service.system.MessageService;

/**
 * @author xiongxianyun
 *
 */
public class ResendMessageJob extends BaseJob{

	@Override
	public void executeJob(JobExecutionContext context) throws Exception {
		// 获取发送失败消息文件路径
		String filePath = AppConfigUtil.get(Constants.MESSAGE_FAILED_FILEPATH);
		File file = new File(filePath);
		String[] fileArr = file.list();
		if(fileArr == null){
			return;
		}
		String formatDate = DateFormatUtil.format(new Date(), "yyyyMMdd");
		MessageService msgService = AppUtil.getBean(MessageService.class);
		for(String fileName : fileArr){
			// 不解析非消息文件
			if(!fileName.contains("message-failed-")){
				continue;
			}
			// 不解析当日的消息文件
			if(fileName.contains(formatDate)){
				continue;
			}
			
			String path = filePath + fileName;
			String msgData = FileUtil.readFile(path);
			String[] dataArr = msgData.split(Constants.SEPARATOR_LINE_BREAK);
			for(String jsonData : dataArr){
				msgService.sendMessage(jsonData, Constants.MESSAGE_SENDMSG_TOPIC);
			}
			// 解析完成之后删除文件
			FileUtil.deleteFile(path);
		}
	}
}
