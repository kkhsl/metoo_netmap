package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.mapper.Ipv4Mapper;
import com.metoo.sqlite.service.Ipv4Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:31
 */
@Service
public class Ipv4ServiceImpl implements Ipv4Service {

    @Autowired
    private Ipv4Mapper ipv4Mapper;

    @Override
    public List<Ipv4> selectObjByMap(Map params) {
        return this.ipv4Mapper.selectObjByMap(params);
    }

    @Override
    public boolean insert(Ipv4 instance) {
        if (instance.getId() == null || instance.getId().equals("")) {
            try {
                this.ipv4Mapper.insert(instance);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;

            }
        }
        return false;
    }

    @Override
    public boolean batchInsertGather(List<Ipv4> instance) {
        if (instance != null || instance.size() > 0) {
            try {
                this.ipv4Mapper.batchInsertGather(instance);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;

            }
        }
        return false;
    }

    @Override
    public boolean copyGatherData() {

        this.ipv4Mapper.deleteTable();

        this.ipv4Mapper.copyGatherData();

        return true;
    }

    @Override
    public boolean deleteTableGather() {
        try {
            this.ipv4Mapper.deleteTableGather();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }
}
