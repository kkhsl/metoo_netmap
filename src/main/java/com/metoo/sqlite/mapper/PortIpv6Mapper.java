package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.PortIpv6;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 11:51
 */
public interface PortIpv6Mapper {

    List<PortIpv6> selectObjByMap(Map params);

    int insert(PortIpv6 instance);

    int update(PortIpv6 instance);

    /**
     * 批量插入
     * @param instance
     * @return
     */
    int batchInsertGather(List<PortIpv6> instance);

    int deleteTableGather();

    /**
     * 清空表数据
     *
     * @return
     */
    int deleteTable();

    int copyGatherData();
}
