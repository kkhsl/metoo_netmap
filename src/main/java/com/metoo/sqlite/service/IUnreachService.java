package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.Unreach;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 21:30
 */
public interface IUnreachService {

    List<Unreach> selectObjByMap(Map params);

    boolean insert(Unreach instance);

    int deleteTable();

    int delete(Integer id);

}
