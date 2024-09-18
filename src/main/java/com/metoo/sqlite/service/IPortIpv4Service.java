package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.PortIpv4;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-23 15:25
 */
public interface IPortIpv4Service {

    List<PortIpv4> selectObjByMap(Map params);

    boolean insert(PortIpv4 instance);

    boolean update(PortIpv4 instance);

    /**
     * 批量插入 采集表
     * @param instance
     * @return
     */
    void batchInsertGather(List<PortIpv4> instance);

    /**
     * 清空采集表
     * @return
     */
    int deleteTableGather();

    /**
     * 清空表数据
     *
     * @return
     */
    boolean deleteTable();

    /**
     * 清空表，并复制采集表数据
     * @return
     */
    int copyGatherData();


}
