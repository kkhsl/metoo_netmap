package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.entity.DeviceScan;
import com.metoo.sqlite.entity.Terminal;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 21:30
 */
public interface IDeviceScanService {

    List<DeviceScan> selectObjByMap(Map params);

    List<DeviceScan> selectObjByIpv4OrIpv6(String ipv4, String ipv6);

    int insert(DeviceScan instance);

    int update(DeviceScan instance);

    int batchInsert(List<DeviceScan> list);

    boolean copyGatherData(List<DeviceScan> list);

    int copyToBck();

    int deleteTable();

    int deleteTableBck();
}
