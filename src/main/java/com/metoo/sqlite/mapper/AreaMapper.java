package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.Area;

import java.util.List;

public interface AreaMapper {

    Area selectObjById(Integer id);

    Area selectObjByName(String name);

    List<Area> selectObjAll();

    Area selectObjByParentId(Integer parentId);
}
