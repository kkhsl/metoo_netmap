package com.metoo.sqlite.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * NULL: 空值。
 * INTEGER: 有符号整数，根据存储的整数大小可以存储为1、2、3、4、6或8字节。
 * REAL: 浮点数，存储为8字节的浮点数。
 * TEXT: 文本字符串，存储使用数据库编码（UTF-8、UTF-16BE 或 UTF-16LE）。
 * BLOB: 二进制大对象，存储完全根据输入。
 */

@ApiModel("用户实体类")
@Data//  注解在类上, 为类提供读写属性, 此外还提供了 equals()、hashCode()、toString() 方法
@Accessors(chain = true) // fluent、chain、prefix、注解用来配置lombok如何产生和显示getters和setters的方法
@AllArgsConstructor
@NoArgsConstructor
public class Version {

    private Integer id;

    private String version;

}
