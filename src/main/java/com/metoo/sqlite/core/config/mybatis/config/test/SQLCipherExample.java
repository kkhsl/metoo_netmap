//package com.metoo.sqlite.core.config.mybatis.config.test;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public class SQLCipherExample {
//
//    public static void main(String[] args) {
//        Connection connection = null;
//        Statement statement = null;
//
//        try {
//            // 加载 SQLCipher 驱动程序
//            Class.forName("org.sqlite.JDBC");
//
//            // 连接加密数据库或创建新的加密数据库
//            String url = "jdbc:sqlite:encrypted.db";
//            connection = DriverManager.getConnection(url);
//
//            // 设置加密密钥
//            statement = connection.createStatement();
//            statement.execute("PRAGMA key = 'whhc@123456'");
//
//            // 创建一个加密表
//            String sql = "CREATE TABLE IF NOT EXISTS users (" +
//                    "id INTEGER PRIMARY KEY," +
//                    "username TEXT NOT NULL," +
//                    "password TEXT NOT NULL);";
//            statement.execute(sql);
//
//            // 插入一条数据
//            String insertSQL = "INSERT INTO users (username, password) VALUES ('testuser', 'testpassword');";
//            statement.execute(insertSQL);
//
//            System.out.println("Database encrypted and table created successfully!");
//
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (statement != null) statement.close();
//                if (connection != null) connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
