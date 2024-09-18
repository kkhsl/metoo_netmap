package com.metoo.sqlite.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 9:41
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceScan {

    @ApiModelProperty("Id")
    private Integer id;
    @ApiModelProperty("创建时间")
    private String createTime;

    private String device_ipv4;
    private String device_ipv6;
    private String device_product;
    private String device_type;
    private String os;

    private String mac;
    private String macVendor;

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        DeviceScan deviceScan = (DeviceScan) o;
        return Objects.equals(device_ipv4, deviceScan.getDevice_ipv4());
    }

    @Override
    public int hashCode() {
        return Objects.hash(device_ipv4);
    }

}
