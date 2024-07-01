package com.metoo.sqlite.service;

import com.metoo.sqlite.entity.MacVendor;

import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-26 15:46
 */
public interface IMacVendorService {

    List<MacVendor> selectObjByMap(Map params);
}
