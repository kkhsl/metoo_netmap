package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.Area;
import com.metoo.sqlite.mapper.AreaMapper;
import com.metoo.sqlite.service.IAreaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AreaServiceImpl implements IAreaService {

    @Resource
    private AreaMapper areaMapper;

    @Override
    public Area selectObjById(Integer id) {
        return this.areaMapper.selectObjById(id);
    }

    @Override
    public List<Area> selectObjAll() {
        return this.areaMapper.selectObjAll();
    }

    @Override
    public List<Area> selectObjByParentId(Integer parentId) {
        return this.areaMapper.selectObjByParentId(parentId);
    }
}
