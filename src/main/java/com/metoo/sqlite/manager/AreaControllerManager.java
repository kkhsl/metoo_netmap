package com.metoo.sqlite.manager;

import com.metoo.sqlite.entity.Area;
import com.metoo.sqlite.service.IAreaService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/area")
public class AreaControllerManager {

    @Autowired
    private IAreaService areaService;

    @GetMapping("all")
    public Result list(){
        List<Area> areaList = this.areaService.selectObjAll();
        if(areaList.size() > 0){
            // 查找所有一级地址
            List<Area> parentList = areaList.stream().filter(area -> area.getParentId() == null).collect(Collectors.toList());
            if(parentList.size() > 0){
                // 使用递归，查出下级数据
                for (Area area : parentList) {
                    area.setSubAreas(findSubAreas(area.getId(), areaList));
                }
            }
            return ResponseUtil.ok(parentList);
        }
        return ResponseUtil.ok();
    }


    // 方法：通过递归查找下级数据
    public List<Area> findSubAreas(Integer parentId, List<Area> allAreas) {
        List<Area> subAreas = allAreas.stream()
                .filter(area -> parentId.equals(area.getParentId()))
                .collect(Collectors.toList());

        // 对每个下级数据递归查找其下级数据
        for (Area subArea : subAreas) {
            subArea.setSubAreas(findSubAreas(subArea.getId(), allAreas));
        }

        return subAreas;
    }
}
