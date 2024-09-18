package com.metoo.sqlite.utils;

import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.entity.User;

import java.lang.reflect.Field;

public class CopyPropertiesReflect {

    public static void main(String[] args) {
        Probe probe1 = new Probe();

        probe1.setVendor("15838152042");
        probe1.setCreateTime("a");

        Probe probe2 = new Probe();

        CopyPropertiesReflect.copyPropertiesExceptId(probe1, probe2);

        System.out.println(probe2);
    }

    // 反射
//    public static void copyPropertiesExceptId(Object source, Object target) {
//        Field[] fields = source.getClass().getDeclaredFields();// 获取源对象的所有字段
//        for (Field field : fields) {
//            if ("id".equals(field.getName())) {// 跳过id字段的复制。
//                continue;
//            }
//            field.setAccessible(true);// 设置字段为可访问，以便可以读取和写入其值。
//            try {
//                field.set(target, field.get(source));
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public static void copyPropertiesExceptId(Object source, Object target) {
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            if ("id".equals(field.getName())) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object value = field.get(source);
                if (value != null) { // 跳过 null 值
                    Field targetField = target.getClass().getDeclaredField(field.getName());
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
