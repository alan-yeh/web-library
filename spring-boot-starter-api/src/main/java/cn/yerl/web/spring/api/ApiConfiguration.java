package cn.yerl.web.spring.api;

import cn.yerl.web.spring.api.reporter.ActionReporterInterceptor;
import org.springframework.boot.web.filter.OrderedHttpPutFormContentFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Configuration for Api
 * Created by alan on 2017/3/13.
 */
@Configuration
public class ApiConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(byteArrayHttpMessageConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ActionReporterInterceptor());
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new JsonExceptionHandlerExceptionResolver());
    }

    private ByteArrayHttpMessageConverter byteArrayHttpMessageConverter(){
        return new ByteArrayHttpMessageConverter();
    }


//    @Bean
//    public FilterRegistrationBean putFormContentFilter(){
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(new HttpPutFormContentFilter());
//        registrationBean.addUrlPatterns("/*");
//        registrationBean.setName("HttpPutFormContentFilter");
//        registrationBean.setOrder(1);
//        return registrationBean;
//    }
}
