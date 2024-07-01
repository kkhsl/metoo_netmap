package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.GatewayInfo;
import com.metoo.sqlite.mapper.GatewayInfoMapper;
import com.metoo.sqlite.service.IGatewayInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-27 23:02
 */
@Service
@Transactional
public class GatewayInfoServiceImpl implements IGatewayInfoService {

    @Autowired
    private GatewayInfoMapper gatewayInfoMapper;

    @Override
    public List<GatewayInfo> selectObjByMap(Map params) {
        return this.gatewayInfoMapper.selectObjByMap(params);
    }

    @Override
    public int insert(GatewayInfo instance) {
        try {
            return this.gatewayInfoMapper.insert(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int update(GatewayInfo instance) {
        try {
            return this.gatewayInfoMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean deleteTable() {
        try {
            this.gatewayInfoMapper.deleteTable();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean copyGatherData() {

        try {
            this.gatewayInfoMapper.deleteTable();

            this.gatewayInfoMapper.copyGatherData();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean batchInsertGather(List<GatewayInfo> list) {
        try {
            this.gatewayInfoMapper.batchInsertGather(list);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteTableGather() {
        try {
            this.gatewayInfoMapper.deleteTableGather();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
