package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.ProbeResult;
import com.metoo.sqlite.mapper.ProbeResultMapper;
import com.metoo.sqlite.service.IProbeResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-29 20:46
 */
@Service
@Transactional
public class ProbeResultServiceImpl implements IProbeResultService {

    @Autowired
    private ProbeResultMapper probeResultMapper;

    @Override
    public ProbeResult selectObjByOne() {
        return this.probeResultMapper.selectObjByOne();
    }

    @Override
    public List<ProbeResult> selectObjByMap(Map params) {
        try {
            return this.probeResultMapper.selectObjByMap(params);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public int update(ProbeResult result) {
        try {
            return this.probeResultMapper.update(result);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
