package com.metoo.sqlite.utils;

import com.metoo.sqlite.entity.User;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

public class CopyPropertiesBeanUtils {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        User user1 = new User();

        user1.setMobile("15838152042");

        User user2 = new User();

        BeanUtils.copyProperties(user2, user1);
        user2.setId(null); // 手动设置ID属性为null或者其他值

        System.out.println(user2);
    }

}


class MyObject {
    private Long id;
    private String name;
    private String description;
    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

class Main {
    public static void main(String[] args) {
        MyObject source = new MyObject();
        source.setId(1L);
        source.setName("Source Name");
        source.setDescription("Source Description");

        MyObject target = new MyObject();

        try {
            BeanUtils.copyProperties(target, source);
            target.setId(null); // 手动设置ID属性为null或者其他值
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        System.out.println("Source ID: " + source.getId());
        System.out.println("Target ID: " + target.getId());
        System.out.println("Source Name: " + source.getName());
        System.out.println("Target Name: " + target.getName());
        System.out.println("Source Description: " + source.getDescription());
        System.out.println("Target Description: " + target.getDescription());
    }
}
