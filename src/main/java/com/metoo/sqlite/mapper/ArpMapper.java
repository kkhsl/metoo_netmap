package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.Arp;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 21:31
 */
public interface ArpMapper {

    List<Arp> selectObjByMap(Map params);

    int insert(Arp instance);

    int delete(Integer Id);

    int batchInsert(List<Arp> list);

    int deleteTable();

    int deleteTableBack();

    int copyToBck();

}
