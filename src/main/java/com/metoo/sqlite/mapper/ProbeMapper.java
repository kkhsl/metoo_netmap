package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.Probe;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:31
 */
@Mapper
public interface ProbeMapper {

    int insert(Probe instance);

    List<Probe> selectObjByMap(Map params);

    int delete(Integer id);

    int deleteTable();

}
