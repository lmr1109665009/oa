package com.suneee.weixin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suneee.core.api.util.PropertyUtil;
import com.suneee.weixin.encode.AesException;
import com.suneee.weixin.encode.WXBizMsgCrypt;
import com.suneee.core.api.util.PropertyUtil;


public class ReceiveMsgServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7238640799352347838L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String token= PropertyUtil.getByAlias("token");
		String sCorpID=PropertyUtil.getByAlias("wx.corpId");
		
		String sEncodingAESKey=PropertyUtil.getByAlias("sEncodingAESKey");
		
		System.err.println(token +"," + sCorpID +"," +sEncodingAESKey);
		try {
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, sEncodingAESKey, sCorpID);
			String msgSignature=req.getParameter("msg_signature");
			String timeStamp=req.getParameter("timestamp");
			String nonce=req.getParameter("nonce");
			String echoStr=req.getParameter("echostr");
			String rtn= wxcpt.VerifyURL(msgSignature, timeStamp, nonce, echoStr);
			resp.getWriter().print(rtn);
		} catch (AesException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
	
	

}
