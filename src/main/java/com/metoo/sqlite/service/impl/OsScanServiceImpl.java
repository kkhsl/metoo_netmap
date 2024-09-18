package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.OsScan;
import com.metoo.sqlite.mapper.OsScanMapper;
import com.metoo.sqlite.service.IOsScanService;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.crypto.Data;

@Service
@Transactional
public class OsScanServiceImpl implements IOsScanService {

    @Resource
    private OsScanMapper osScanMapper;

    public OsScanServiceImpl(OsScanMapper osScanMapper) {
        this.osScanMapper = osScanMapper;
    }

    @Override
    public int insert(OsScan instance) {
        try {
            instance.setCreateTime(DateTools.getCreateTime());
            return this.osScanMapper.insert(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
