package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.Probe;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 21:30
 */
public interface IProbeService {

    List<Probe> selectObjByMap(Map params);

    List<Probe> selectDeduplicationByIp(Map params);

    List<Probe> mergeProbesByIp();

    boolean insert(Probe instance);

    boolean update(Probe instance);

    int delete(Integer id);

    int deleteTable();

    int deleteTableBack();

    int copyToBck();

    boolean deleteProbeByIp(String ipv4, String ipv6);

}
