package com.metoo.sqlite.manager;

import com.metoo.sqlite.dto.DeviceDTO;
import com.metoo.sqlite.dto.UnitSubnetDTO;
import com.metoo.sqlite.entity.UnitSubnet;
import com.metoo.sqlite.service.IUnitSubnetService;
import com.metoo.sqlite.vo.Result;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("单位网段")
@RequestMapping("/admin/unit/subnet")
@RestController
public class UnitSubnetManagerController {

    @Autowired
    private IUnitSubnetService unitSubnetService;

    @PostMapping("/list")
    private Result list(@RequestBody UnitSubnetDTO dto) {
        Result result = this.unitSubnetService.selectObjConditionQuery(dto);
        return result;
    }

    @PostMapping
    public Result save(@RequestBody UnitSubnet unitSubnet){
        return this.unitSubnetService.save(unitSubnet);
    }

    @PostMapping("/batch")
    public Result batch(@RequestBody List<UnitSubnet> unitSubnetList){
        return this.unitSubnetService.batch(unitSubnetList);
    }


    @DeleteMapping
    public Result delete(@RequestParam String ids) {
        return this.unitSubnetService.delete(ids);
    }
}
