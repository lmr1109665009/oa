package com.suneee.weixin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import com.suneee.core.util.HttpUtil;
import com.suneee.core.util.StringUtil;
import com.suneee.core.util.HttpUtil;
import com.suneee.core.util.StringUtil;

public class SendRequest {
	
	public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
		
		String msg="{\"touser\": \"zyg\",  \"msgtype\": \"text\",\"agentid\": \"6\",\"text\": {\"content\": \"<a href='http://www.baidu.com'>baidu</a>\"  },   \"safe\":\"0\"}";
		
		//String url="http://qydev.weixin.qq.com/cgi-bin/apiagent?tid=36&URL=http%3A%2F%2Fhotent.eicp.net%2Fbpmx3%2Freceive&Token=hotent&EncodingAESKey=DJt7nwKc6zfy96K3QPY8ywdsyohCSWoemEWi4uhByyQ&EchoStr=11111222223333344444123&ToUserName=wxc6e093e6b81b98d1";
		
		String url="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=52kScBCzqTlsZxR3OjdmHLFnniOOWrEXQJWpaiQbS9obSOWRFeaBdAU5fAXK831jNP9DPr4OU4tbKPju86T5Iw" ;
		
		String rtn= HttpUtil.sendHttpsRequest(url, msg, "POST");
		
		System.out.println(rtn);
		
	}
	
	
	public static String sendData(String url,String data){
		URL uRL;
		URLConnection conn;
		
		BufferedReader bufferedReader = null;
		try {
			uRL = new URL(url);
			conn = uRL.openConnection();
			conn.setDoOutput(true);
			if(StringUtil.isNotEmpty(data)){
				OutputStreamWriter   outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
				outputStreamWriter.write(data);
				outputStreamWriter.flush();
				outputStreamWriter.close();
			}
			

			// Get the response
			bufferedReader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer response = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				response.append(line);
			}

			bufferedReader.close();
			
			return response.toString();
		}
		 catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}
