package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.Subnet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SubnetMapper {

    Subnet selectObjById(Integer id);

    Subnet selectObjByIp(String ip);

    Subnet selectObjByIpAndMask(@Param("ip") String ip, @Param("mask") Integer mask);

    List<Subnet> selectSubnetByParentId(@Param("parentId") Integer parentId);

    List<Subnet> selectSubnetByParentIp(Integer ip);

    List<Subnet> selectObjByMap(Map params);

    int save(Subnet instance);

    int update(Subnet instance);

    int delete(Integer id);

    int deleteTable();


}
