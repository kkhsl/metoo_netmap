package com.metoo.sqlite.service;

import com.metoo.sqlite.dto.UnitSubnetDTO;
import com.metoo.sqlite.entity.UnitSubnet;
import com.metoo.sqlite.vo.Result;

import java.util.List;
import java.util.Map;

public interface IUnitSubnetService {
    Result selectObjById(Integer id);
    Result save(UnitSubnet instance);
    Result batch(List<UnitSubnet> instances);
    Result delete(String ids);
    Result selectObjConditionQuery(UnitSubnetDTO dto);
    List<UnitSubnet> selectObjByMap(Map params);
}
