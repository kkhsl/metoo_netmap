package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.SubnetIpv6;
import com.metoo.sqlite.vo.Result;

import java.util.List;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-04-24 14:41
 */
public interface ISubnetIpv6Service {

    SubnetIpv6 selectObjById(Integer id);

    List<SubnetIpv6> selectSubnetByParentId(Integer id);

    boolean save(SubnetIpv6 instance);

    Result update(SubnetIpv6 instance);

    int deleteTable();
}
