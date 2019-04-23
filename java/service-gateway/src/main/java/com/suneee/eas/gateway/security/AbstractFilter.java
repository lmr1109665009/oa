package com.suneee.eas.gateway.security;

import com.suneee.core.util.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 忽略URL，有需要的继承此类。
 * @author ray
 *
 */
public class AbstractFilter {
	
private List<Pattern> ingoreUrls=new ArrayList<Pattern>();
	
	public void setIngoreUrls(List<String> urls){
		if(BeanUtils.isEmpty(urls)) return;
		for(String url:urls){
			Pattern regex = Pattern.compile(url, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | 
					Pattern.DOTALL | Pattern.MULTILINE);
			ingoreUrls.add(regex);
		}
	}
	
	
	/**
	 * 判断当前URL是否在忽略的地址中。
	 * @param requestUrl
	 * @return
	 */
	protected boolean isContainUrl(String requestUrl){
		for(Pattern pattern:ingoreUrls){
			Matcher regexMatcher = pattern.matcher(requestUrl);
			if(regexMatcher.find()){
				return true;
			}
		}
		return false;
	}

}
