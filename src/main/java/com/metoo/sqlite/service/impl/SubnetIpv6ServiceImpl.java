package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.SubnetIpv6;
import com.metoo.sqlite.mapper.SubnetIpv6Mapper;
import com.metoo.sqlite.service.ISubnetIpv6Service;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-04-24 14:42
 */
@Slf4j
@Service
@Transactional
public class SubnetIpv6ServiceImpl implements ISubnetIpv6Service {

    @Autowired
    private SubnetIpv6Mapper subnetIpv6Mapper;

    @Override
    public SubnetIpv6 selectObjById(Integer id) {
        return this.subnetIpv6Mapper.selectObjById(id);
    }

    @Override
    public List<SubnetIpv6> selectSubnetByParentId(Integer id) {
        return this.subnetIpv6Mapper.selectSubnetByParentId(id);
    }

    @Override
    public boolean save(SubnetIpv6 instance) {
        try {
            instance.setCreateTime(DateTools.getCreateTime());
            this.subnetIpv6Mapper.save(instance);
            log.info("=============================gather ipv6 subnet " + instance.getIp() + " | " + instance.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Result update(SubnetIpv6 instance) {
        try {
            if(this.selectObjById(instance.getId()) != null) {
                int i = this.subnetIpv6Mapper.update(instance);
                if(i >= 0){
                    return ResponseUtil.ok();
                }
            }
            return ResponseUtil.dataNotFound();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.error();
        }
    }

    @Override
    public int deleteTable() {
        return this.subnetIpv6Mapper.deleteTable();
    }


}
