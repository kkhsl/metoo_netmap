package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.PanaSwitch;

public interface IPanaSwitchService {

    PanaSwitch selectObjByOne();

    int insert(PanaSwitch instance);

    int update(PanaSwitch instance);

}
