package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.Arp;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 21:30
 */
public interface IArpService {

    List<Arp> selectObjByMap(Map params);

    int delete(Integer Id);

    int batchInsert(List<Arp> list);

    int deleteTable();

    int gather();

    int deleteTableBack();

    int copyToBck();
}
