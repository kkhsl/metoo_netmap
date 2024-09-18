package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.entity.Unreach;
import com.metoo.sqlite.entity.Unsure;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 21:30
 */
public interface IUnsureService {

    List<Unsure> selectObjByMap(Map params);

    boolean insert(Unsure instance);

    int deleteTable();

    int delete(Integer id);

}
