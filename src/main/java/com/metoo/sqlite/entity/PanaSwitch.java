package com.metoo.sqlite.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PanaSwitch {

    @ApiModelProperty("默认为0:不用api的v4mac 1: IPv4网关在派网上")
    private Boolean state;
}
