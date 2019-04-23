package com.suneee.config;

import com.suneee.core.web.servlet.ValidJs;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/platform/form/bpmFormTable/selector").setViewName("/platform/form/bpmFormTableSelector");
        registry.addViewController("/platform/form/bpmFormTable/dialog").setViewName("/platform/form/bpmFormTableDialog");
        registry.addViewController("/platform/form/bpmFormDef/import").setViewName("/platform/form/bpmFormDefImport");
        registry.addViewController("/platform/system/sysFile/dialog").setViewName("/platform/system/sysFileDialog");
        registry.addViewController("/platform/form/bpmFormDef/dialog").setViewName("/platform/form/bpmFormDefDialog");
        registry.addViewController("/platform/system/conditionScript/editDialog").setViewName("/platform/system/conditionScriptEditDialog");
        registry.addViewController("/platform/system/personScript/dialog").setViewName("/platform/system/personScriptDialog");
        registry.addViewController("/platform/bpm/nodeMsgTemplate/edit").setViewName("/platform/bpm/nodeMsgTemplateEdit");
        registry.addViewController("/platform/expression/expression").setViewName("/platform/expression/expression");
        registry.addViewController("/platform/bpm/bpmNodeSet/list").setViewName("/platform/bpm/bpmNodeSetList");
        registry.addViewController("/platform/system/identity/selector").setViewName("/platform/system/identitySelector");
        registry.addViewController("/platform/form/bpmFormDialog/show").setViewName("/platform/form/bpmFormDialogShow");
        registry.addViewController("/platform/system/sysUser").setViewName("/platform/system/sysUserGet");
        registry.addViewController("/platform/bpm/bpmNodeUser/previewMockParams").setViewName("/platform/bpm/bpmNodeUserPreviewMockParams");
        registry.addViewController("/platform/bpm/formDefTree/edit").setViewName("/platform/bpm/formDefTreeEdit");
        registry.addViewController("/platform/system/sysFile/HtmlDialog").setViewName("/platform/system/sysFileList");
        registry.addViewController("/platform/system/sysFile/uploadDialog").setViewName("/platform/system/sysFileUploadDialog");
        registry.addViewController("/platform/system/conditionScript/dialog").setViewName("/platform/system/conditionScriptDialog");
        registry.addViewController("/platform/system/sysFile/htmlDialog").setViewName("/platform/system/sysFileList");
        registry.addViewController("/platform/system/resources/dialog").setViewName("/platform/system/resourcesIcons");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
        registry.addResourceHandler("/commons/**").addResourceLocations("/commons/");
        registry.addResourceHandler("/styles/**").addResourceLocations("/styles/");
        registry.addResourceHandler("/media/**").addResourceLocations("/media/");
        registry.addResourceHandler("/weixin/**").addResourceLocations("/weixin/");
    }
    /**
     * 添加对jsp的支持
     *
     * @return
     */
    @Bean
    public ViewResolver getJspViewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/view/");
        internalResourceViewResolver.setSuffix(".jsp");
        internalResourceViewResolver.setOrder(1);
        return internalResourceViewResolver;
    }

    /**
     * 添加对Freemarker支持
     *
     * @return
     */
    @Bean
    public FreeMarkerViewResolver getFreeMarkerViewResolver() {
        FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver();
        freeMarkerViewResolver.setCache(true);
        freeMarkerViewResolver.setSuffix(".ftl");
        freeMarkerViewResolver.setRequestContextAttribute("request");
        freeMarkerViewResolver.setOrder(0);
        freeMarkerViewResolver.setContentType("text/html;charset=UTF-8");
        return freeMarkerViewResolver;
    }

    /**
     * 根据表单名称获取客户端表单认证的JS代码servlet
     * @return
     */
    @Bean
    public ServletRegistrationBean validJs(){
        return new ServletRegistrationBean(new ValidJs(),"/servlet/ValidJs");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DataSourceIntercepter()).addPathPatterns("/**");
    }
}
