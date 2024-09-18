package com.metoo.sqlite.manager;

import com.metoo.sqlite.entity.SubnetIpv6;
import com.metoo.sqlite.gather.factory.gather.thread.Gather;
import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
import com.metoo.sqlite.service.ISubnetIpv6Service;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-04-24 15:23
 */
@RequestMapping("/admin/subnet/ipv6")
@RestController
public class SubnetIpv6ManagerController {

    @Autowired
    private ISubnetIpv6Service subnetIpv6Service;

    @GetMapping("comb")
    public Result comb(){
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.SUBNET_IPV6);
        gather.executeMethod();
        return ResponseUtil.ok();
    }

    @GetMapping("list")
    public Result list(){
        // 获取所有子网一级
        List<SubnetIpv6> parentList = this.subnetIpv6Service.selectSubnetByParentId(null);
        if (parentList.size() > 0) {
            for (SubnetIpv6 subnet : parentList) {
                this.genericSubnet(subnet);
            }
            return ResponseUtil.ok(parentList);
        }
        return ResponseUtil.ok();
    }

    public List<SubnetIpv6> genericSubnet(SubnetIpv6 subnetIpv6) {
        List<SubnetIpv6> subnets = this.subnetIpv6Service.selectSubnetByParentId(subnetIpv6.getId());
        if (subnets.size() > 0) {
            for (SubnetIpv6 child : subnets) {
                List<SubnetIpv6> subnetList = genericSubnet(child);
                if (subnetList.size() > 0) {
                    child.setSubnetList(subnetList);
                }
            }
            subnetIpv6.setSubnetList(subnets);
        }
        return subnets;
    }

    @ApiOperation("根据网段Ip查询直接从属子网")
    @GetMapping(value = {"", "/{id}"})
    public Object getSubnet(@PathVariable(value = "id", required = false) Integer id) {
        SubnetIpv6 subnetIpv6 = this.subnetIpv6Service.selectObjById(id);
        if (subnetIpv6 != null) {
            // 当前网段
            Map map = new HashMap();
            map.put("subnetIpv6", subnetIpv6);
            return ResponseUtil.ok(map);
        }
        return ResponseUtil.ok();
    }


    @PutMapping
    public Result update(@RequestBody SubnetIpv6 instance){
        return this.subnetIpv6Service.update(instance);
    }
}
