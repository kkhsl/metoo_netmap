package com.metoo.sqlite.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-19 11:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginBody {

    @NonNull
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("用户密码")
    private String password;

    @ApiModelProperty("验证码")
    private String captcha;
}
