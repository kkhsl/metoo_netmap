package com.metoo.sqlite.gather.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 10:54
 */
@Data
@Accessors(chain = true)
public class Child extends Parent {

    private String test;

}