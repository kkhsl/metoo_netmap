package com.metoo.sqlite.core.config.mybatis.config;

import com.metoo.sqlite.core.config.mybatis.config.json.ListStringToJsonTypeHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-20 16:22
 */
@Configuration
@MapperScan({"com.metoo.sqlite.mapper", "com.metoo.sqlite.api.mapper"}) // 扫描Mapper接口
public class MyBatisConfig {

    @Value("${sqlite.db.path}")
    private String sqliteDbPath;

    @Bean
    public DataSource dataSource() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
//        dataSource.setUrl("jdbc:sqlite::resource:data/mydatabase.db"); // 类路径（classpath）资源写法
        dataSource.setUrl("jdbc:sqlite:" + sqliteDbPath);
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        // 配置 MyBatis 的 mapperLocations
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath*:mapper/*.xml"));

        return sessionFactory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            // 注册自定义 TypeHandler
            configuration.getTypeHandlerRegistry().register(ListStringToJsonTypeHandler.class);
        };
    }
}
