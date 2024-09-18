package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.Area;

import java.util.List;

public interface IAreaService {

    Area selectObjById(Integer id);

    List<Area> selectObjAll();

    List<Area> selectObjByParentId(Integer parentId);
}
