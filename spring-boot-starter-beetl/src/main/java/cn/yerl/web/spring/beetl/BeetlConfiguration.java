package cn.yerl.web.spring.beetl;

import org.beetl.core.Function;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.beetl.ext.spring.UtilsFunctionPackage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

        Map<String, Object> functions = new HashMap<>();
        functions.put("sputil", new UtilsFunctionPackage());
        configuration.setFunctionPackages(functions);

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
