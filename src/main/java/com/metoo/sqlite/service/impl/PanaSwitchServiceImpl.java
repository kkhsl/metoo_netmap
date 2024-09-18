package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.PanaSwitch;
import com.metoo.sqlite.mapper.PanaSwitchMapper;
import com.metoo.sqlite.service.IPanaSwitchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class PanaSwitchServiceImpl implements IPanaSwitchService {

    @Resource
    private PanaSwitchMapper panaSwitchMapper;

    @Override
    public PanaSwitch selectObjByOne() {
        return this.panaSwitchMapper.selectObjByOne();
    }

    @Override
    public int insert(PanaSwitch instance) {
       try {
           int i = panaSwitchMapper.insert(instance);
           return i;
       } catch (Exception e) {
           e.printStackTrace();
           return 0;
       }
    }

    @Override
    public int update(PanaSwitch instance) {
        try {
            int i = panaSwitchMapper.update(instance);
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
