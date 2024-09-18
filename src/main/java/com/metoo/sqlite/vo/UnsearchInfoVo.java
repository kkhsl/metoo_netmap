package com.metoo.sqlite.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors
@NoArgsConstructor
public class UnsearchInfoVo {

    private String ipAddr;
    private String macAddr;
    private String macVendor;

    public UnsearchInfoVo(String ipAddr, String macAddr, String macVendor) {
        this.ipAddr = ipAddr;
        this.macAddr = macAddr;
        this.macVendor = macVendor;
    }
}
