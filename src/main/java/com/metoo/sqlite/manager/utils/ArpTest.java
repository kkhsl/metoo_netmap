package com.metoo.sqlite.manager.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-25 20:59
 */
public class ArpTest {

    public static void main(String[] args) {
        // 假设有两个对象列表
        List<ObjectA> listA = new ArrayList<>();
        listA.add(new ObjectA(1, "A1"));
        listA.add(new ObjectA(2, "A2"));
        listA.add(new ObjectA(3, "A3"));

        List<ObjectB> listB = new ArrayList<>();
        listB.add(new ObjectB(1, "B1"));
        listB.add(new ObjectB(3, "B3"));
        listB.add(new ObjectB(4, "B4"));

        // 根据 id 属性去重 List<ObjectA>
        Map<Integer, ObjectA> mapA = listA.stream()
                .collect(Collectors.toMap(ObjectA::getId, a -> a, (existing, replacement) -> replacement));

        List<ObjectA> deduplicatedListA = new ArrayList<>(mapA.values());
        System.out.println("Deduplicated List A: " + deduplicatedListA);

        // 根据 id 属性去重 List<ObjectB>
        Map<Integer, ObjectB> mapB = listB.stream()
                .collect(Collectors.toMap(ObjectB::getId, b -> b, (existing, replacement) -> replacement));

        List<ObjectB> deduplicatedListB = new ArrayList<>(mapB.values());
        System.out.println("Deduplicated List B: " + deduplicatedListB);
    }
}


class ObjectA {
    private int id;
    private String name;

    public ObjectA(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ObjectA{id=" + id + ", name='" + name + '\'' + '}';
    }
}

// 假设的对象类 ObjectB
class ObjectB {
    private int id;
    private String description;

    public ObjectB(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ObjectB{id=" + id + ", description='" + description + '\'' + '}';
    }
}