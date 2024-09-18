package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.PortIpv6;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 11:42
 */
public interface IPortIpv6Service {

    List<PortIpv6> selectObjByMap(Map params);

    boolean insert(PortIpv6 instance);

    boolean update(PortIpv6 instance);

    /**
     * 批量插入
     * @param instance
     * @return
     */
    void batchInsertGather(List<PortIpv6> instance);

    int deleteTableGather();

    /**
     * 清空表数据
     *
     * @return
     */
    boolean deleteTable();

    int copyGatherData();
}
