package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.Version;

public interface IVersionService {

    Version selectObjByOne();

    int save(Version instance);

    int update(Version instance);

}
