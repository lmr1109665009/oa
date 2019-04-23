package com.suneee.weixin.model.msg.impl;

import com.alibaba.fastjson.JSONArray;
import com.suneee.weixin.model.msg.BaseMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * news消息
 * <pre>
 * {
   "touser": "UserID1|UserID2|UserID3",
   "toparty": " PartyID1 | PartyID2 ",
   "totag": " TagID1 | TagID2 ",
   "msgtype": "news",
   "agentid": "1",
   "news": {
       "articles":[
           {
               "title": "Title",
               "description": "Description",
               "url": "URL",
               "picurl": "PIC_URL"
           },
           {
               "title": "Title",
               "description": "Description",
               "url": "URL",
               "picurl": "PIC_URL"
           }    
       ]
   }
}
 * </pre>
 * @author ray
 *
 */
public class NewsMessage extends BaseMessage {
	
	private List<Article> news=new ArrayList<Article>();
	
	public String getMsgtype() {
		return "news";
	}

	public List<Article> getNews() {
		return news;
	}

	public void setNews(List<Article> news) {
		this.news = news;
	}
	
	public void addArticle(Article article){
		this.news.add(article);
	}


	@Override
	public String toString() {
		String json="{\"touser\": \"%s\",\"toparty\": \"%s\",\"msgtype\": \"news\",\"agentid\": \"%s\",\"news\": {\"articles\":%s}}";	
		String newsJson=JSONArray.toJSONString(this.news);
		json=String.format(json, this.getTouser(),this.getToparty(),this.getAgentid(),newsJson);
		return json;
	}




	public static void main(String[] args) {
		NewsMessage message=new NewsMessage();
		message.setTouser("zyg");
		message.setAgentid("13");
		Article a1=new Article("通知","通知","http://www.ifeng.com","");
		Article a2=new Article("兄弟我要请假","兄弟我要请假","http://www.163.com","");
		message.addArticle(a1);
		message.addArticle(a2);
		System.out.println(message);
	}
}
