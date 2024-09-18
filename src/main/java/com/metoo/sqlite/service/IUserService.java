package com.metoo.sqlite.service;

import com.metoo.sqlite.dto.UserDto;
import com.metoo.sqlite.entity.User;
import com.metoo.sqlite.model.LoginBody;
import com.metoo.sqlite.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-19 10:53
 */
public interface IUserService {

    User selectObjById(Integer id);

    User selectObjByName(String username);

    Result selectObjByMap(Map params);

    Result selectObjConditionQuery(UserDto dto);

    Result save(UserDto user);

    Result create(UserDto user);

    Result update(User user);

    Result delete(String ids);

    Result editPassword(UserDto instance);

    Result login(HttpServletRequest request, LoginBody loginBody);
}
