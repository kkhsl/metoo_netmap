package com.metoo.sqlite.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Area {

    private Integer id;
    private String name;
    private Integer parentId;
    private Integer unit_id;

    private List<Area> subAreas = new ArrayList<>();

}
