package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.PortIpv4;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 11:53
 */
public interface PortIpv4Mapper {

    List<PortIpv4> selectObjByMap(Map params);

    int insert(PortIpv4 instance);

    int update(PortIpv4 instance);

    /**
     * 批量插入
     * @param instance
     * @return
     */
    int batchInsertGather(List<PortIpv4> instance);

    int deleteTableGather();

    /**
     * 清空表数据
     *
     * @return
     */
    int deleteTable();

    int copyGatherData();
}
