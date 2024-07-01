package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.License;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface LicenseMapper {

    List<License> query();

    int update(License instance);

    int save(License instance);
}
