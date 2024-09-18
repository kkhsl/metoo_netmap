package com.metoo.sqlite.core.config.sqlite;

import com.metoo.sqlite.mapper.OsScanMapper;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-20 15:01
 */
public class MyBatisUtil {

    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            // 创建Hikari数据源
            DataSource dataSource = DataSourceConfig.getDataSource();

            // 创建环境
            Environment environment = new Environment("development", new JdbcTransactionFactory(), dataSource);

            // 创建MyBatis配置
            Configuration configuration = new Configuration(environment);

            configuration.addMapper(OsScanMapper.class);

            // 构建SqlSessionFactory
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
