package com.metoo.sqlite.utils.muyun;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MuyunResult {

    private Integer code;
    private String msg;
    private String data;

}
