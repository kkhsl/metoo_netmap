package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.Ipv6;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:30
 */
public interface Ipv6Service {

    List<Ipv6> selectObjByMap(Map params);

    boolean insert(Ipv6 instance);

    boolean copyGatherData();

    boolean batchInsertGather(List<Ipv6> list);

    boolean deleteTableGather();


}
