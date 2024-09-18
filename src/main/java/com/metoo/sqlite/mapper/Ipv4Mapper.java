package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.Ipv4;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:31
 */
@Mapper
public interface Ipv4Mapper {

    List<Ipv4> selectObjByMap(Map params);

    int insert(Ipv4 instance);

    boolean update(Ipv4 instance);

    int deleteTable();

    int batchInsertGather(List<Ipv4> list);

    int deleteTableGather();

    int copyGatherData();

}
