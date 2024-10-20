package com.metoo.sqlite.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.DeviceScan;
import com.metoo.sqlite.mapper.DeviceScanMapper;
import com.metoo.sqlite.service.IDeviceScanService;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-28 17:14
 */
@Service
@Transactional
public class DeviceScanServiceImpl implements IDeviceScanService {

    @Resource
    private DeviceScanMapper deviceScanMapper;

    @Override
    public List<DeviceScan> selectObjByMap(Map params) {
        return this.deviceScanMapper.selectObjByMap(params);
    }

    @Override
    public List<DeviceScan> selectObjByIpv4OrIpv6(String ipv4, String ipv6) {
        Map params = new HashMap();
        List<DeviceScan> deviceScanList = new ArrayList<>();
        if(StringUtil.isNotEmpty(ipv4)){
            params.clear();
            params.put("device_ipv4", ipv4);
            deviceScanList = deviceScanMapper.selectObjByMap(params);
        }
        if(deviceScanList.size() <= 0 && StringUtil.isNotEmpty(ipv6)){
            params.put("device_ipv6", ipv6);
            deviceScanList = deviceScanMapper.selectObjByMap(params);
        }
        return deviceScanList;
    }

    @Override
    public int insert(DeviceScan instance) {
        if(instance.getId() == null){
            try {
                instance.setCreateTime(DateTools.getCreateTime());
                return this.deviceScanMapper.insert(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }else{
            try {
                return this.deviceScanMapper.update(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    @Override
    public int update(DeviceScan instance) {
        try {
            instance.setCreateTime(DateTools.getCreateTime());
            return this.deviceScanMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int batchInsert(List<DeviceScan> list) {
        try {
            return this.deviceScanMapper.batchInsert(list);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean copyGatherData(List<DeviceScan> list) {
        try {

            this.deviceScanMapper.deleteTable();

            if(list.size() > 0){
                this.deviceScanMapper.batchInsert(list);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int copyToBck() {
        return this.deviceScanMapper.copyToBck();
    }

    @Override
    public int deleteTable() {
        return this.deviceScanMapper.deleteTable();
    }

    @Override
    public int deleteTableBck() {
        return this.deviceScanMapper.deleteTableBck();
    }
}
