package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.mapper.ProbeMapper;
import com.metoo.sqlite.service.IProbeService;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:31
 */
@Service
@Transactional
public class ProbeServiceImpl implements IProbeService {

    @Autowired
    private ProbeMapper probeMapper;

    @Override
    public List<Probe> selectObjByMap(Map params) {
        List<Probe> probes = this.probeMapper.selectObjByMap(params);
        return probes;
    }

    @Override
    public boolean insert(Probe instance) {
        try {
            instance.setCreateTime(DateTools.getCreateTime());
            this.probeMapper.insert(instance);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Probe instance) {
        try {
            this.probeMapper.update(instance);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int delete(Integer id) {
        try {
            return this.probeMapper.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteTable() {
        try {
            return this.probeMapper.deleteTable();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteTableBack() {
        return this.probeMapper.deleteTableBack();
    }

    @Override
    public int copyToBck() {
        return this.probeMapper.copyToBck();
    }

}
