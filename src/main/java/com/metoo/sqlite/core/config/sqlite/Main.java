package com.metoo.sqlite.core.config.sqlite;

import com.metoo.sqlite.entity.User;
import com.metoo.sqlite.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-20 15:03
 */
public class Main {

    public static void main(String[] args) {
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> user = mapper.selectUser();
            System.out.println(user);
        }
    }
}
