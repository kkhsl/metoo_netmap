package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.Area;

import java.util.List;

public interface AreaMapper {

    Area selectObjById(Integer id);

    List<Area> selectObjAll();

    List<Area> selectObjByParentId(Integer parentId);
}
