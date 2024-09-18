package com.metoo.sqlite.core.config.sqlite;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-20 15:00
 */
public class DataSourceConfig {

    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl("jdbc:sqlite:E:\\java\\soft\\sqlite\\sqlite\\mydatabase.sqlite");
//        config.setJdbcUrl("jdbc:sqlite:./data/mydatabase.sqlite");
        config.setJdbcUrl("jdbc:sqlite:D:\\metoo\\project\\metoo\\sql\\netmap\\mydatabase1.db");
//        config.setJdbcUrl("jdbc:sqlite::resource:data/mydatabase.sqlite");

        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
