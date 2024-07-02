package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.GatherSemaphore;
import com.metoo.sqlite.mapper.GatherSemaphoreMapper;
import com.metoo.sqlite.service.IGatherSemaphoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-07-01 17:40
 */
@Service
@Transactional
public class GatherSemaphoreServiceImpl implements IGatherSemaphoreService {

    @Autowired
    private GatherSemaphoreMapper gatherSemaphoreMapper;

    @Override
    public GatherSemaphore selectObjByOne() {
        return this.gatherSemaphoreMapper.selectObjByOne();
    }

    @Override
    public List<GatherSemaphore> selectObjByMap(Map params) {
        return this.gatherSemaphoreMapper.selectObjByMap(params);
    }

    @Override
    public int update(GatherSemaphore result) {
        try {
            return this.gatherSemaphoreMapper.update(result);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
