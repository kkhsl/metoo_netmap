package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.PortIpv4;
import com.metoo.sqlite.mapper.PortIpv4Mapper;
import com.metoo.sqlite.service.IPortIpv4Service;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-23 15:30
 */
@Service
@Transactional
public class PortIpv4ServiceImpl implements IPortIpv4Service {

    @Resource
    private PortIpv4Mapper portIpv4Mapper;

    @Override
    public List<PortIpv4> selectObjByMap(Map params) {
        return this.portIpv4Mapper.selectObjByMap(params);
    }

    @Override
    public boolean insert(PortIpv4 instance) {
        if(instance.getId() == null || instance.getId().equals("")){
            instance.setCreateTime(DateTools.getCreateTime());
            try {
                int i = this.portIpv4Mapper.insert(instance);
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
                int i = this.portIpv4Mapper.update(instance);
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
    public boolean update(PortIpv4 instance) {
        try {
            int i = this.portIpv4Mapper.update(instance);
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
    public void batchInsertGather(List<PortIpv4> instance) {
        try {
            int i = this.portIpv4Mapper.batchInsertGather(instance);
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
        return this.portIpv4Mapper.deleteTableGather();
    }

    /**
     * 清空表数据
     *
     * @return
     */
    @Override
    public boolean deleteTable() {
        try {
            int i = this.portIpv4Mapper.deleteTable();
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
        this.portIpv4Mapper.deleteTable();
        this.portIpv4Mapper.copyGatherData();
        return 1;
    }

}
