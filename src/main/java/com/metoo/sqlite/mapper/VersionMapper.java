package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.Version;

public interface VersionMapper {

    Version selectObjByOne();

    int save(Version instance);

    int update(Version instance);

}
