package com.metoo.sqlite.service.impl;


import com.metoo.sqlite.entity.PortIpv6;
import com.metoo.sqlite.mapper.PortIpv6Mapper;
import com.metoo.sqlite.service.IPortIpv6Service;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2026-06-23 15:30
 */
@Service
@Transactional
public class PortIpv6ServiceImpl implements IPortIpv6Service {

    @Resource
    private PortIpv6Mapper ipv6PortMapper;

    @Override
    public List<PortIpv6> selectObjByMap(Map params) {
        return this.ipv6PortMapper.selectObjByMap(params);
    }

    @Override
    public boolean insert(PortIpv6 instance) {
        if(instance.getId() == null || instance.getId().equals("")){
            instance.setCreateTime(DateTools.getCreateTime());
            try {
                int i = this.ipv6PortMapper.insert(instance);
                if(i >= 1){
                    return true;
                }else{
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }else{
            try {
                int i = this.ipv6PortMapper.update(instance);
                if(i >= 1){
                    return true;
                }else{
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public boolean update(PortIpv6 instance) {
        try {
            int i = this.ipv6PortMapper.update(instance);
            if(i >= 1){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 批量插入
     *
     * @param instance
     * @return
     */
    @Override
    public void batchInsertGather(List<PortIpv6> instance) {
        try {
            int i = this.ipv6PortMapper.batchInsertGather(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空采集表
     *
     * @return
     */
    @Override
    public int deleteTableGather() {
        return this.ipv6PortMapper.deleteTableGather();
    }

    /**
     * 清空表数据
     *
     * @return
     */
    @Override
    public boolean deleteTable() {
        try {
            int i = this.ipv6PortMapper.deleteTable();
            if(i >= 1){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int copyGatherData() {
        this.ipv6PortMapper.deleteTable();
        this.ipv6PortMapper.copyGatherData();
        return 1;
    }
}
