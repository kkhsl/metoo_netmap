package com.metoo.sqlite.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.metoo.sqlite.dto.GatherLogDTO;
import com.metoo.sqlite.dto.page.PageInfo;
import com.metoo.sqlite.entity.GatherLog;
import com.metoo.sqlite.mapper.GatherLogMapper;
import com.metoo.sqlite.service.IGatherLogService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-07-01 15:56
 */
@Service
@Transactional
public class GatherLogServiceImpl implements IGatherLogService {

    @Resource
    private GatherLogMapper gatherLogMapper;

    @Override
    public List<GatherLog> selectObjByMap(Map params) {
        return this.gatherLogMapper.selectObjByMap(params);
    }

    @Override
    public Result selectObjConditionQuery(GatherLogDTO dto) {
        if(dto == null){
            dto = new GatherLogDTO();
        }
        Page<GatherLog> page = PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        this.gatherLogMapper.selectObjConditionQuery(dto);
        return ResponseUtil.ok(new PageInfo<GatherLog>(page));
    }

    @Override
    public int insert(GatherLog instance) {
        try {
            return this.gatherLogMapper.insert(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteTable() {
        try {
            return this.gatherLogMapper.deleteTable();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
