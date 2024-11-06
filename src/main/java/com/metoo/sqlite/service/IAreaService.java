package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.Area;

import java.util.List;

public interface IAreaService {

    Area selectObjById(Integer id);

    Area selectObjByName(String name);

    List<Area> selectObjAll();

    Area selectObjByParentId(Integer parentId);
}
