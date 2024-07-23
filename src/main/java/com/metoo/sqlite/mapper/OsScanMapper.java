package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.OsScan;
import org.apache.ibatis.annotations.Mapper;

public interface OsScanMapper {

    int insert(OsScan instance);
}
