package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.entity.Terminal;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-28 17:50
 */
public interface ITerminalService {

    Terminal selectObjById(Integer id);

    List<Terminal> selectObjByMap(Map params);

    List<Terminal> selectGatherByMap(Map params);

    boolean insert(Terminal instance);

    int update(Terminal instance);

    int delete(Integer id);

    int deleteTable();

    boolean copyGatherData();

    boolean batchInsertGather(List<Terminal> list);

    boolean deleteTableGather();
}
