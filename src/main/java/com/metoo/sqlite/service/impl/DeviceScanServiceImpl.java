package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.DeviceScan;
import com.metoo.sqlite.mapper.DeviceScanMapper;
import com.metoo.sqlite.service.IDeviceScanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    public int insert(DeviceScan instance) {
        try {
            return this.deviceScanMapper.insert(instance);
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
}
