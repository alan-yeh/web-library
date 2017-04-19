package cn.yerl.web.spring.beetlsql;

import cn.yerl.web.spring.beetlsql.properties.DatasourceProperties;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.OracleStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.spring4.BeetlSqlDataSource;
import org.beetl.sql.ext.spring4.BeetlSqlScannerConfigurer;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Configuration for beetl sql
 * Created by alan on 2017/3/13.
 */
@Configuration
@EnableConfigurationProperties({DatasourceProperties.class})
public class BeetlSqlConfiguration{

    @Bean("beetlSqlScannerConfigurer")
    public BeetlSqlScannerConfigurer beetlSqlScanner(@Autowired Environment environment){
        String basePackage = environment.getProperty("beetlsql.base-package");
        String daoSuffix = environment.getProperty("beetlsql.dao-suffix");


        BeetlSqlScannerConfigurer configurer = new BeetlSqlScannerConfigurer();
        configurer.setBasePackage(basePackage);
        configurer.setDaoSuffix(daoSuffix);
        configurer.setSqlManagerFactoryBeanName("sqlManagerFactoryBean");

        return configurer;
    }



    @Bean
    @DependsOn({"beetlGroupUtilConfiguration", "dataSource"})
    public SqlManagerFactoryBean sqlManagerFactoryBean(@Qualifier("dataSource")DataSource dataSource){
        SqlManagerFactoryBean factory = new SqlManagerFactoryBean();

        BeetlSqlDataSource sqlDataSource = new BeetlSqlDataSource();
        sqlDataSource.setMasterSource(dataSource);
        factory.setCs(sqlDataSource);

        factory.setDbStyle(new OracleStyle());

        factory.setSqlLoader(new ClasspathLoader("/sql"));

        factory.setNc(new UnderlinedNameConversion());
        factory.setInterceptors(new Interceptor[]{new DebugInterceptor(){
            private Logger logger = LoggerFactory.getLogger(DebugInterceptor.class);
            @Override
            protected void println(String str) {
                logger.info(str);
            }
        }});

        return factory;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource")DataSource dataSource){
        DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
        return manager;
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() throws Exception{
        DatasourceProperties jdbc = new DatasourceProperties();
        jdbc.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        jdbc.setInitialSize(3);
        jdbc.setMaxActive(4);
        jdbc.setUsername("test_base");
        jdbc.setPassword("11");
        jdbc.setJdbcUrl("jdbc:oracle:thin:@10.0.1.3:1521:orcl");


        ComboPooledDataSource dataSource = new ComboPooledDataSource("dataSource");
        dataSource.setDriverClass(jdbc.getDriverClassName());
        dataSource.setInitialPoolSize(jdbc.getInitialSize());
        dataSource.setMaxPoolSize(jdbc.getMaxActive());

        dataSource.setJdbcUrl(jdbc.getJdbcUrl());
        dataSource.setUser(jdbc.getUsername());
        dataSource.setPassword(jdbc.getPassword());
        return dataSource;
    }
}
