package com.metoo.sqlite.core.config.mybatis.config;//package com.metoo.sqlite.core.config.mybatis.config;
//
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//
//import javax.sql.DataSource;
//
//@Configuration
//@MapperScan("com.metoo.sqlite.mapper") // 扫描Mapper接口
//public class DatabaseConnectionFactory {
//
//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.sqlite.JDBC");
//        dataSource.setUrl("jdbc:sqlite:data/mydatabase.db?key=123456");
//        // SQLite 不需要用户名和密码
//        return dataSource;
//    }
//
//    @Bean
//    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
//        sessionFactory.setDataSource(dataSource);
////        sessionFactory.setMapperLocations(new ClassPathResource("mybatis/mapper/UserMapper.xml"));
//        // 配置 MyBatis 的 mapperLocations
//        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        sessionFactory.setMapperLocations(resolver.getResources("classpath*:mapper/*.xml"));
//
//        return sessionFactory.getObject();
//    }
//}
