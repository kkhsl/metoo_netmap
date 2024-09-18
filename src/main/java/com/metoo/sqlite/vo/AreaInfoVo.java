package com.metoo.sqlite.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors
@NoArgsConstructor
public class AreaInfoVo {

    private String uniytId;
    private String unit;
    private String area;
    private String city;
    private String version;

    public AreaInfoVo(String uniytId, String unit, String area, String city, String version) {
        this.uniytId = uniytId;
        this.unit = unit;
        this.area = area;
        this.city = city;
        this.version = version;
    }
}
