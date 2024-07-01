package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.GatewayInfo;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-27 23:02
 */
public interface IGatewayInfoService {

    List<GatewayInfo> selectObjByMap(Map params);

    int insert(GatewayInfo instance);

    int update(GatewayInfo instance);

    boolean deleteTable();

    boolean copyGatherData();

    boolean batchInsertGather(List<GatewayInfo> list);

    boolean deleteTableGather();

}
