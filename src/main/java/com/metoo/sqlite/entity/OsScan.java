package com.metoo.sqlite.entity;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class OsScan {

    private Integer id;
    private String createTime;
    private String IP;
    private String openPort;
    private String closePort;
    private String Dst_GW_MAC;
    private String manufacturer;
    private String ttl;
    private String FingerID;
    private String Reliability;
    private String OsVendor;
    private String OsGen;
    private String OsFamily;

}
