package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.Ipv4;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:30
 */
public interface Ipv4Service {

    List<Ipv4> selectObjByMap(Map params);

    boolean insert(Ipv4 instance);

    boolean update(Ipv4 instance);

    boolean copyGatherData();

    boolean batchInsertGather(List<Ipv4> list);

    boolean deleteTableGather();

    int deleteTable();


}
