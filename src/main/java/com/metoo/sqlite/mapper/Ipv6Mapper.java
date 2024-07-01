package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.Ipv6;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:31
 */
@Mapper
public interface Ipv6Mapper {

    List<Ipv6> selectObjByMap(Map params);

    int insert(Ipv6 instance);

    int deleteTable();

    int batchInsertGather(List<Ipv6> list);

    int deleteTableGather();

    int copyGatherData();

}
