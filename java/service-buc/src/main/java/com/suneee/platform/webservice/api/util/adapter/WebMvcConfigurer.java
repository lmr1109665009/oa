package com.suneee.platform.webservice.api.util.adapter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
		converter.setFeatures(SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullStringAsEmpty,//字符串null返回空字符串
				SerializerFeature.WriteNullBooleanAsFalse,
				SerializerFeature.PrettyFormat);
		converters.add(converter);
	}
}
