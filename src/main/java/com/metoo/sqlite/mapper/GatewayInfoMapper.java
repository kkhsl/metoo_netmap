package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.GatewayInfo;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-27 23:03
 */
public interface GatewayInfoMapper {

    List<GatewayInfo> selectObjByMap(Map params);

    int insert(GatewayInfo instance);

    int update(GatewayInfo instance);

    int deleteTable();

    int copyGatherData();

    int batchInsertGather(List<GatewayInfo> list);

    int deleteTableGather();
}
