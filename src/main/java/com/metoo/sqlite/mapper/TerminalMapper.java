package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.entity.Terminal;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:31
 */
@Mapper
public interface TerminalMapper {

    Terminal selectObjById(Integer id);

    List<Terminal> selectObjByMap(Map params);

    List<Terminal> selectGatherByMap(Map params);

    int insert(Terminal instance);

    int update(Terminal instance);

    int delete(Integer id);

    int deleteTable();

    int batchInsertGather(List<Terminal> list);

    int deleteTableGather();

    int copyGatherData();

}
