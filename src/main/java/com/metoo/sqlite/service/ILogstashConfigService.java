package com.metoo.sqlite.service;

import java.util.List;

public interface ILogstashConfigService {
    /**
     * 根据不同的设备类型获取不同的配置路径
     *
     * @param name
     * @return
     */
    List<String> queryByName();
}
