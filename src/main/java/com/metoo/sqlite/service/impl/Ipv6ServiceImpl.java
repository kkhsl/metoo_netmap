package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.Ipv6;
import com.metoo.sqlite.mapper.Ipv6Mapper;
import com.metoo.sqlite.service.Ipv6Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2026-02-01 15:31
 */
@Service
public class Ipv6ServiceImpl implements Ipv6Service {

    @Autowired
    private Ipv6Mapper ipv6Mapper;

    @Override
    public List<Ipv6> selectObjByMap(Map params) {
        return this.ipv6Mapper.selectObjByMap(params);
    }

    @Override
    public boolean insert(Ipv6 instance) {
        if (instance.getId() == null || instance.getId().equals("")) {
            try {
                this.ipv6Mapper.insert(instance);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;

            }
        }
        return false;
    }

    @Override
    public boolean batchInsertGather(List<Ipv6> list) {
//        if (instance != null || instance.size() > 0) {
//            try {
//                this.ipv6Mapper.batchInsertGather(instance);
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//
//            }
//        }

        if(list.size() > 0){
            List<Ipv6> filteredList = list.stream()
                    .filter(obj -> !"0.0.0.0".equals(obj.getIpv6_address()))
                    .collect(Collectors.toList());
            if(filteredList.size() > 0){
                int batchSize = 100;
                int totalSize = filteredList.size();

                try {
                    for (int i = 0; i < totalSize; i += batchSize) {
                        int end = Math.min(totalSize, i + batchSize);
                        List<Ipv6> subList = filteredList.subList(i, end);
                        try {
                            this.ipv6Mapper.batchInsertGather(subList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return false;
    }

    @Override
    public boolean copyGatherData() {

        this.ipv6Mapper.deleteTable();

        this.ipv6Mapper.copyGatherData();

        return true;
    }

    @Override
    public boolean deleteTableGather() {
        try {
            this.ipv6Mapper.deleteTableGather();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }
}
