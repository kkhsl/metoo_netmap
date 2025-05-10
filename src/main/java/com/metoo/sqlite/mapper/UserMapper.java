package com.metoo.sqlite.mapper;

import com.metoo.sqlite.dto.UserDto;
import com.metoo.sqlite.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-19 10:56
 */
public interface UserMapper {

    User selectObjById(Integer id);

    User selectObjByName(String username);

    List<User> selectObjByMap(Map params);

    List<User> selectObjConditionQuery(UserDto dto);

    int save(UserDto instance);

    int update(UserDto instance);

    int updateUser(User instance);

    int delete(Integer id);

    @Select("SELECT * FROM metoo_user")
    List<User> selectUser();
}
