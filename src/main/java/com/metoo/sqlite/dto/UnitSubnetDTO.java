package com.metoo.sqlite.dto;

import com.metoo.sqlite.dto.page.PageDto;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.UnitSubnet;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@ApiModel("")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UnitSubnetDTO extends PageDto<UnitSubnet> {
    private String name;
}
