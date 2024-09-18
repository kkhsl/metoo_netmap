package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.License;

import java.util.List;

public interface ILicenseService {

    /**
     * 根据UUID检测是否为被允许设备
     */
    License detection();

    List<License> query();

    int save(License instance);

    int update(License instance);

}
