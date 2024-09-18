package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.MacVendor;
import com.metoo.sqlite.mapper.MacVendorMapper;
import com.metoo.sqlite.service.IMacVendorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-26 15:47
 */
@Service
@Transactional
public class MacVendorServiceImpl implements IMacVendorService {

    @Resource
    private MacVendorMapper macVendorMapper;

    @Override
    public List<MacVendor> selectObjByMap(Map params) {
        return this.macVendorMapper.selectObjByMap(params);
    }
}
