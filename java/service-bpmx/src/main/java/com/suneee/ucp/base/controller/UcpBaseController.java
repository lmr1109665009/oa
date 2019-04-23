/**
 * 
 */
package com.suneee.ucp.base.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.suneee.core.util.AppUtil;
import com.suneee.core.util.BeanUtils;
import com.suneee.core.web.controller.BaseController;
import com.suneee.core.web.query.QueryFilter;
import com.suneee.platform.attachment.AttachmentHandler;
import com.suneee.platform.attachment.AttachmentHandlerFactory;
import com.suneee.platform.model.system.SysFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controller基类
 * 
 * @author Administrator
 */
public class UcpBaseController extends BaseController {
	private AttachmentHandler currentmentHander;
	/**
	 * ajax返回信息
	 * 
	 * @param status
	 *            成功标识 0-成功 1-失败
	 * @param msg
	 *            返回信息
	 * @param code
	 *            错误编码
	 * @param response
	 */
	private void initCurrentmentHander() throws Exception{
		if(BeanUtils.isEmpty(currentmentHander)){
			AttachmentHandlerFactory attachmentHandlerFactory = AppUtil.getBean(AttachmentHandlerFactory.class);
			currentmentHander=attachmentHandlerFactory.getCurrentHandler();
		}
	}
	public String addMessage(int status, String msg, String code, HttpServletResponse response) {

		JSONObject ob = new JSONObject();
		ob.put("status", status);
		ob.put("message", msg);
		ob.put("code", code);
		renderString(response, ob);
		return null;
	}

	/**
	 * 
	 * @Title: addMessage
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param status
	 * @param @param msg
	 * @param @param code
	 * @param @param data
	 * @param @param response
	 * @param @return    参数
	 * @return String    返回类型
	 * @throws
	 */
	public String addMessage(int status, String msg, String code, Object data, HttpServletResponse response) {

		JSONObject ob = new JSONObject();
		ob.put("status", status);
		ob.put("message", msg);
		ob.put("code", code);
		ob.put("data", data);
		renderString(response, ob);
		return null;
	}
	
	/**
	 * ajax返回信息
	 * 
	 * @param status
	 *            成功标识 0-成功 1-失败
	 * @param msg
	 *            返回信息
	 * @param code
	 *            错误编码
	 * @param data
	 *            数据
	 * @param response
	 */
	public String addMessage(int status, String msg, String code, JSONObject data, HttpServletResponse response) {

		JSONObject ob = new JSONObject();
		ob.put("status", status);
		ob.put("message", msg);
		ob.put("code", code);
		ob.put("data", data);
		renderString(response, ob);
		return null;
	}

	/**
	 * 客户端返回JSON字符串
	 * 
	 * @param response
	 * @param object
	 * @return
	 */
	protected String renderString(HttpServletResponse response, Object object) {
		return renderString(response, JSON.toJSONString(object), "application/json");
	}

	/**
	 * 客户端返回字符串
	 * 
	 * @param response
	 * @param string
	 * @return
	 */
	protected String renderString(HttpServletResponse response, String string, String type) {
		try {
			response.reset();
			response.setContentType(type);
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(string);
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 文件上传 返回一个数组内容的list，数组【0】为文件名称，【1】为文件路径+实际名称
	 * 
	 * @param request
	 * @param savePath//文件路径
	 * @throws IOException
	 * @author 游刃
	 */
	public List<String[]> uplodeFile(HttpServletRequest request,AttachmentHandler currentHander,String eid) throws Exception {
 
		List<String[]> list = new ArrayList<>();

		// 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 检查form中是否有enctype="multipart/form-data"
		if (multipartResolver.isMultipart(request)) {
			// 将request变成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 获取multiRequest 中所有的文件名
			Iterator iter = multiRequest.getFileNames();

			while (iter.hasNext()) {
				// 一次遍历所有文件(我们只允许一个)
				MultipartFile file = multiRequest.getFile(iter.next().toString());
				if (file != null) {
					String sqlfileName = file.getOriginalFilename();
					if (StringUtils.isBlank(sqlfileName))
						return null;
					// imgPath为原文件名
					int idx = sqlfileName.lastIndexOf(".");
					// 文件后缀
					String extention = sqlfileName.substring(idx);
					Date date = new Date(System.currentTimeMillis());
					SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
					String time = fmt.format(date);

					// 新的文件名(日期+后缀)
					sqlfileName = time + extention;
					String path = eid+"/"+sqlfileName;
					SysFile sysFile = new SysFile();
					sysFile.setFilePath(path);
					currentHander.upload(sysFile, file.getInputStream());
					
					String[] nameAndPath = new String[3];
					double d = Double.valueOf(file.getSize());
					nameAndPath[0] = file.getOriginalFilename();
					nameAndPath[1] = path;
					nameAndPath[2] = getsize(d);
					list.add(nameAndPath);
				}
			}
		}

		return list;
	}

	
	/** 
     * 从网络Url中下载文件并上传到FTP服务器
     * @param urlStr 
     * @param fileName 
     * @param savePath 
     * @param enterpriseCode 
	 * @throws Exception 
     */  
    public List<String[]>  downLoadFromUrlToFTP(String urlStr,String fileName,String savePath,String enterpriseCode) throws Exception{  
    	 URL url = new URL(urlStr);    
         HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
                 //设置超时间为3秒  
         conn.setConnectTimeout(6*1000);  
         //防止屏蔽程序抓取而返回403错误  
         conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
         int size = conn.getContentLength();
         //得到输入流  
         InputStream inputStream = conn.getInputStream();       
         initCurrentmentHander();
         int idx = fileName.lastIndexOf(".");
			// 文件后缀
			String extention = fileName.substring(idx);
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String time = fmt.format(date);

			// 新的文件名(日期+后缀)
			fileName = time + extention;
			String path = enterpriseCode+File.separator+fileName;
			SysFile sysFile = new SysFile();
			sysFile.setFilePath(path);                        
         try {
			currentmentHander.upload(sysFile, inputStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
         List<String[]> list = new ArrayList<>();
         String[] nameAndPath = new String[3];
 		double d = Double.valueOf(size);
 		nameAndPath[0] = fileName;
 		nameAndPath[1] = enterpriseCode +File.separator + fileName;
 		nameAndPath[2] = getsize(d);
 		list.add(nameAndPath);
         
         if(inputStream!=null){  
             inputStream.close();  
         }  
         System.out.println("info:"+url+" download success");  
         return list;
     }  
    /** 
     * 从网络Url中下载文件
     * @param urlStr 
     * @param fileName 
     * @param savePath 
	 * @throws Exception 
     */ 
    public List<String[]>  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{  
        URL url = new URL(urlStr);    
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
                //设置超时间为3秒  
        conn.setConnectTimeout(6*1000);  
        //防止屏蔽程序抓取而返回403错误  
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
  
        //得到输入流  
        InputStream inputStream = conn.getInputStream();    
        //获取自己数组  
        byte[] getData = readInputStream(inputStream);      
  
        //文件保存位置  
        File saveDir = new File(savePath);  
        if(!saveDir.exists()){  
            saveDir.mkdir();  
        }  
        File file = new File(saveDir+File.separator+fileName);      
        FileOutputStream fos = new FileOutputStream(file);       
        fos.write(getData);   
        if(fos!=null){  
            fos.close();    
        }  
        
        List<String[]> list = new ArrayList<>();
        String[] nameAndPath = new String[3];
		double d = Double.valueOf(getData.length);
		nameAndPath[0] = fileName;
		nameAndPath[1] = savePath + "/" + fileName;
		nameAndPath[2] = getsize(d);
		list.add(nameAndPath);
        
        if(inputStream!=null){  
            inputStream.close();  
        }  
        System.out.println("info:"+url+" download success");  
        return list;
    }
    /** 
     * 从输入流中获取字节数组 
     * @param inputStream 
     * @return 
     * @throws IOException 
     */  
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
        while((len = inputStream.read(buffer)) != -1) {    
            bos.write(buffer, 0, len);    
        }    
        bos.close();    
        return bos.toByteArray();    
    }    
	
	
	/**
	 * 计算文件大小
	 * 
	 * @param length
	 * @return
	 * @author 游刃
	 */
	public static String getsize(Double length) {
		return length > 1024
				? ((length /= 1024) > 1024 ? ((length /= 1024) > 1024 ? String.format("%.2f", (length /= 1024)) + "GB"
						: String.format("%.2f", length) + "M") : String.format("%.2f", length) + "KB")
				: String.format("%.2f", length) + "B";
	}

	/**
	 * 文件下载
	 * 
	 * @param filePath
	 * @param fileName
	 * @param trueName//下载下去显示的文件名
	 * @param request
	 * @param response
	 * @return
	 * @author 游刃
	 * @throws UnsupportedEncodingException 
	 */
	public boolean downloadFile(String filePath, String trueName, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		File file = new File(filePath);
		if (file.exists()) {
			String agent = (String)request.getHeader("USER-AGENT");  
			response.setContentType("application/force-download");// 设置强制下载不打开
			// FireFox
			if(agent != null && agent.indexOf("Firefox") != -1) {  
				String enableFileName = "=?UTF-8?B?" + (new String(Base64.getEncoder().encodeToString(trueName.getBytes("utf-8")))) + "?=";
			    response.setHeader("Content-Disposition", "attachment; filename=" + enableFileName);    
			}
			else{
				trueName = URLEncoder.encode(trueName,"utf-8");
				response.addHeader("Content-Disposition", "attachment;filename=" + trueName);
			}
			byte[] buffer = new byte[1024];
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			try {
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				OutputStream os = response.getOutputStream();
				int i = bis.read(buffer);
				while (i != -1) {
					os.write(buffer, 0, i);
					i = bis.read(buffer);
				}
				return true;
			} catch (Exception e) {
				return false;
			} finally {
				if (bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
					}
				}
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return false;
	}

	/**
	 * 返回分页数据
	 *
	 * @param list
	 * @param filter
	 * @return
	 */
	public Map<String, Object> getPageList(List<?> list, QueryFilter filter) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("results", list);
		map.put("totalCounts", filter.getPageBean().getTotalCount());
		return map;
	}
}
