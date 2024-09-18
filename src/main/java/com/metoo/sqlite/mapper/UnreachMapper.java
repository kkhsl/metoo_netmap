package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.entity.Unreach;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:31
 */
@Mapper
public interface UnreachMapper {

    List<Unreach> selectObjByMap(Map params);

    int insert(Unreach instance);

    int deleteTable();

    int delete(Integer id);

}
