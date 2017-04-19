package cn.yerl.web.spring.beetl;

import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

/**
 * Configuration for beetl
 * Created by alan on 2017/3/11.
 */
@Configuration
public class BeetlConfiguration {
    @Bean(initMethod = "init")
    public BeetlGroupUtilConfiguration beetlGroupUtilConfiguration(){
        BeetlGroupUtilConfiguration configuration = new BeetlGroupUtilConfiguration();
        try {
            ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/views");
            configuration.setResourceLoader(resourceLoader);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

        return configuration;
    }

    @Bean
    @DependsOn("beetlGroupUtilConfiguration")
    public BeetlSpringViewResolver beetlViewResolver(@Qualifier("beetlGroupUtilConfiguration") BeetlGroupUtilConfiguration beetlGroupUtilConfiguration) {
        BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
        beetlSpringViewResolver.setPrefix("/");
        beetlSpringViewResolver.setSuffix(".html");
        beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
        beetlSpringViewResolver.setOrder(0);
        beetlSpringViewResolver.setConfig(beetlGroupUtilConfiguration);
        return beetlSpringViewResolver;
    }
}
