package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.Subnet;
import com.metoo.sqlite.vo.Result;

import java.util.List;
import java.util.Map;

public interface ISubnetService {

    Subnet selectObjById(Integer id);

    Subnet selectObjByIp(String ip);

    Subnet selectObjByIpAndMask(String ip, Integer mask);

    List<Subnet> selectSubnetByParentId(Integer id);

    List<Subnet> selectSubnetByParentIp(Integer ip);

    List<Subnet> selectObjByMap(Map params);

    int save(Subnet instance);

    Result update(Subnet instance);

    int delete(Integer id);

    int deleteTable();

    /**
     * 根据ipv4地址进行网段梳理
     * @return
     */
    Result comb();

}
