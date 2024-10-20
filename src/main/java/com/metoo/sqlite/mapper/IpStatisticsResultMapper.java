package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.es.IpStatisticsResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * es mappper
 * @author zzy
 * @version 1.0
 * @date 2024-10-11 14:57
 */
public interface IpStatisticsResultMapper {


    List<IpStatisticsResult> selectObjByMap(Map params);

    int save(IpStatisticsResult instance);

    int delete();
}
