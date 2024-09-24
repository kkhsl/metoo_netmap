package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.Arp;
import com.metoo.sqlite.manager.utils.ArpUtils;
import com.metoo.sqlite.mapper.ArpMapper;
import com.metoo.sqlite.service.IArpService;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if(list.size() > 0){
            List<Arp> filteredArpList = list.stream()
                    .filter(arp -> !"0.0.0.0".equals(arp.getIp()))
                    .collect(Collectors.toList());
            if(filteredArpList.size() > 0){
                for (Arp arp : filteredArpList) {
                    arp.setCreateTime(DateTools.getCreateTime());
                }

                return this.arpMapper.batchInsert(filteredArpList);
            }
        }
        return 0;
    }

    @Override
    public int deleteTable() {
        return this.arpMapper.deleteTable();
    }

    @Override
    public int gather() {
        try {

//            List<Arp> arpList = this.arpMapper.selectObjByMap(null);
//            for (Arp arp : arpList) {
//                this.arpMapper.delete(arp.getId());
//            }
//            List<Arp> arps = arpUtils.getArp();
            List<Arp> arps = arpUtils.getArpVersion();
//            List<Arp> arps = arpUtils.getArpVersionPanabit();

            this.deleteTable();

            if(arps.size() > 0){
                if(arps.size() > 0){
                    List<Arp> filteredArpList = arps.stream()
                            .filter(arp -> !"0.0.0.0".equals(arp.getIp()))
                            .collect(Collectors.toList());
                    if(filteredArpList.size() > 0){
                        for (Arp arp : filteredArpList) {
                            arp.setCreateTime(DateTools.getCreateTime());
                            this.arpMapper.insert(arp);
                        }
                    }
                }
            }
//            this.arpMapper.deleteTableBack();
//            this.arpMapper.copyToBck();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteTableBack() {
        return this.arpMapper.deleteTableBack();
    }

    @Override
    public int copyToBck() {
        return this.arpMapper.copyToBck();
    }
}
