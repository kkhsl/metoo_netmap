package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.Unreach;
import com.metoo.sqlite.entity.Unsure;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:31
 */
@Mapper
public interface UnsureMapper {

    List<Unsure> selectObjByMap(Map params);

    int insert(Unsure instance);

    int deleteTable();

    int delete(Integer id);

}
