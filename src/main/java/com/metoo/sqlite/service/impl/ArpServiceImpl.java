package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.manager.utils.ArpUtils;
import com.metoo.sqlite.mapper.ArpMapper;
import com.metoo.sqlite.service.IArpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 21:31
 */
@Service
@Transactional
public class ArpServiceImpl implements IArpService {

    @Autowired
    private ArpMapper arpMapper;
    @Autowired
    private ArpUtils arpUtils;

    @Override
    public List<Arp> selectObjByMap(Map params) {
        return this.arpMapper.selectObjByMap(params);
    }

    @Override
    public int delete(Integer Id) {
        try {
            return this.arpMapper.delete(Id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int batchInsert(List<Arp> list) {
        return this.arpMapper.batchInsert(list);
    }

    @Override
    public int deleteTable() {
        return this.arpMapper.deleteTable();
    }

    @Override
    public int gather() {
        try {
            List<Arp> arps = arpUtils.getArp();
            this.deleteTable();
            if(arps.size() > 0){
                this.batchInsert(arps);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
