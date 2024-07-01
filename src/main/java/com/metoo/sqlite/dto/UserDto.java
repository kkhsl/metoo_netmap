package com.metoo.sqlite.dto;

import com.metoo.sqlite.dto.page.PageDto;
import com.metoo.sqlite.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@ApiModel("用户DTO")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends PageDto<User> {

    /**
     * AllArgsConstructor注解和NotNull注解配合使用，参数不为null
     */
    private Integer id;

    private Date addTime;

    private Integer deleteStatus;

    private String username;

    private String password;

    private String verifyPassword;

    private String oldPassword;

    @ApiModelProperty("用户年龄")
    private Integer age;

    @ApiModelProperty("用户性别")
    private Integer sex;

    private String salt;

    @ApiModelProperty("角色ID字符串")
    private Integer[] role_id;

    @ApiModelProperty("默认0：普通用户 1：管理员 ")
    private String type;

    @ApiModelProperty("是否强制退出用户")
    private boolean flag;

    @ApiModelProperty("用户邮箱")
    private String email;

    @ApiModelProperty("手机号码")
    private String mobile;

    private Long[] userIds;

    @ApiModelProperty("用户状态 0：启用 1：未启用")
    private Integer status;
}