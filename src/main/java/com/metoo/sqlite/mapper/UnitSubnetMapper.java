package com.metoo.sqlite.mapper;

import com.metoo.sqlite.dto.UnitSubnetDTO;
import com.metoo.sqlite.entity.UnitSubnet;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UnitSubnetMapper {
    UnitSubnet selectObjById(Integer id);
    List<UnitSubnet> selectObjByMap(Map params);
    List<UnitSubnet> selectObjConditionQuery(UnitSubnetDTO dto);
    int save(UnitSubnet instance);
    int update(UnitSubnet instance);
    int delete(Integer id);
}
