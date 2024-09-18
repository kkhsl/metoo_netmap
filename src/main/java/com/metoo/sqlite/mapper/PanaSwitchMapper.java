package com.metoo.sqlite.mapper;

import com.metoo.sqlite.entity.PanaSwitch;

public interface PanaSwitchMapper {

    PanaSwitch selectObjByOne();

    int insert(PanaSwitch instance);

    int update(PanaSwitch instance);
}
