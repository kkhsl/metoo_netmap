package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.Unreach;
import com.metoo.sqlite.mapper.UnreachMapper;
import com.metoo.sqlite.service.IUnreachService;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:31
 */
@Service
@Transactional
public class UnreachServiceImpl implements IUnreachService {

    @Autowired
    private UnreachMapper unreachMapper;

    @Override
    public List<Unreach> selectObjByMap(Map params) {
        return this.unreachMapper.selectObjByMap(params);
    }

    public boolean insert(Unreach instance) {
        try {
            instance.setCreateTime(DateTools.getCreateTime());
            this.unreachMapper.insert(instance);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int deleteTable() {
        try {
            return this.unreachMapper.deleteTable();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int delete(Integer id) {
        try {
            return this.unreachMapper.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
