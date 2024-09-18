package com.metoo.sqlite.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.mapper.Ipv4Mapper;
import com.metoo.sqlite.service.Ipv4Service;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:31
 */
@Service
public class Ipv4ServiceImpl implements Ipv4Service {

    @Autowired
    private Ipv4Mapper ipv4Mapper;

    @Override
    public List<Ipv4> selectObjByMap(Map params) {
        return this.ipv4Mapper.selectObjByMap(params);
    }

    @Override
    public boolean insert(Ipv4 instance) {
        if (instance.getId() == null || instance.getId().equals("")) {
            try {
                this.ipv4Mapper.insert(instance);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;

            }
        }
        return false;
    }

    @Override
    public boolean update(Ipv4 instance) {
        try {
            this.ipv4Mapper.update(instance);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }

    @Override
    public boolean batchInsertGather(List<Ipv4> list) {
//        if (instance != null || instance.size() > 0) {
//            try {
//                this.ipv4Mapper.batchInsertGather(instance);
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//
//            }
//        }

        if(list.size() > 0){
            List<Ipv4> filteredList = list.stream()
                    .filter(obj -> !"0.0.0.0".equals(obj.getIp()))
                    .collect(Collectors.toList());
            if(filteredList.size() > 0){
                int batchSize = 100;
                int totalSize = filteredList.size();

                try {
                    for (int i = 0; i < totalSize; i += batchSize) {
                        int end = Math.min(totalSize, i + batchSize);
                        List<Ipv4> subList = filteredList.subList(i, end);
                        try {
                            this.ipv4Mapper.batchInsertGather(subList);
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

//    @Override
//    public boolean batchInsertGather(List<Ipv4> list) {
////        if (instance != null || instance.size() > 0) {
////            try {
////                this.ipv4Mapper.batchInsertGather(instance);
////                return true;
////            } catch (Exception e) {
////                e.printStackTrace();
////                return false;
////
////            }
////        }
//
//        if(list.size() > 0){
//            List<Ipv4> filteredList = list.stream()
//                    .filter(arp -> !"0.0.0.0".equals(arp.getIp()))
//                    .collect(Collectors.toList());
//            if(filteredList.size() > 0){
//                for (Ipv4 ipv4 : filteredList) {
//                    ipv4.setCreateTime(DateTools.getCreateTime());
//                }
//                try {
//                    this.ipv4Mapper.batchInsertGather(filteredList);
//                    return true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return false;
//
//                }
//            }
//        }
//        return false;
//    }

    @Override
    public boolean copyGatherData() {

        this.ipv4Mapper.deleteTable();

        this.ipv4Mapper.copyGatherData();

        return true;
    }

    @Override
    public boolean deleteTableGather() {
        try {
            this.ipv4Mapper.deleteTableGather();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }

    @Override
    public int deleteTable() {
        return this.ipv4Mapper.deleteTable();
    }
}
