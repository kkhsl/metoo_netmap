package com.metoo.sqlite.core.config.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-20 17:59
 */
public class SQLiteApp {

    public static void main(String[] args) {
        Connection conn = null;
        try {
            // 注册 JDBC 驱动程序
            Class.forName("org.sqlite.JDBC");
            // 打开连接
            conn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");

            // 执行查询
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM metoo_user");
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                System.out.println("ID: " + id + ", Username: " + username);
            }
            // 关闭连接
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
