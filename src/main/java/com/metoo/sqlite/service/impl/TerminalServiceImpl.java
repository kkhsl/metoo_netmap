package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.Terminal;
import com.metoo.sqlite.mapper.TerminalMapper;
import com.metoo.sqlite.service.ITerminalService;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:31
 */
@Service
public class TerminalServiceImpl implements ITerminalService {

    @Autowired
    private TerminalMapper terminalMapper;

    @Override
    public Terminal selectObjById(Integer id) {
        return this.terminalMapper.selectObjById(id);
    }

    @Override
    public List<Terminal> selectObjByMap(Map params) {
        return this.terminalMapper.selectObjByMap(params);
    }

    @Override
    public List<Terminal> selectGatherByMap(Map params) {
        return this.terminalMapper.selectGatherByMap(params);
    }

    @Override
    public boolean insert(Terminal instance) {
        if (instance.getId() == null || instance.getId().equals("")) {
            try {
                instance.setCreateTime(DateTools.getCreateTime());
                this.terminalMapper.insert(instance);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }else{
            try {
                this.terminalMapper.update(instance);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public int update(Terminal instance) {
        try {
            return this.terminalMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;

        }
    }

    @Override
    public int delete(Integer id) {
        try {
            return this.terminalMapper.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteTable() {
        return this.terminalMapper.deleteTable();
    }

    @Override
    public boolean batchInsertGather(List<Terminal> instance) {
        if (instance != null || instance.size() > 0) {
            try {
                this.terminalMapper.batchInsertGather(instance);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;

            }
        }
        return false;
    }

    @Override
    public boolean copyGatherData() {

        try {
            this.terminalMapper.deleteTable();

            this.terminalMapper.copyGatherData();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteTableGather() {
        try {
            this.terminalMapper.deleteTableGather();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }
}
