package com.metoo.sqlite.manager;

import com.metoo.sqlite.entity.Subnet;
import com.metoo.sqlite.service.ISubnetService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.net.Ipv4Utils;
import com.metoo.sqlite.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api("子网管理")
@RequestMapping("/admin/subnet")
@RestController
public class SubnetManagerController {

    @Autowired
    private ISubnetService subnetService;

    /**
     * 网段梳理
     * @return
     */
    @RequestMapping(value = {"/comb"})
    public Result comb() {
        return this.subnetService.comb();
    }

    @RequestMapping("/list")
    public Result list() {
        // 获取所有子网一级
        List<Subnet> parentList = this.subnetService.selectSubnetByParentId(null);
        if (parentList.size() > 0) {
            for (Subnet subnet : parentList) {
                this.genericSubnet(subnet);
            }
            return ResponseUtil.ok(parentList);
        }
        return ResponseUtil.ok();
    }

    public List<Subnet> genericSubnet(Subnet subnet) {
        List<Subnet> subnets = this.subnetService.selectSubnetByParentId(subnet.getId());
        if (subnets.size() > 0) {
            for (Subnet child : subnets) {
                List<Subnet> subnetList = genericSubnet(child);
                if (subnetList.size() > 0) {
                    child.setSubnetList(subnetList);
                }
            }
            subnet.setSubnetList(subnets);
        }
        return subnets;
    }

    @ApiOperation("删除网段")
    @DeleteMapping
    public Object delete(@RequestParam(value = "id") Integer id) {
        Subnet subnet = this.subnetService.selectObjById(id);
        if (subnet != null) {
            this.subnetService.delete(subnet.getId());
        }
        return ResponseUtil.ok();
    }

    @ApiOperation("根据网段Ip查询直接从属子网")
    @GetMapping(value = {"", "/{id}"})
    public Object getSubnet(@PathVariable(value = "id", required = false) Integer id) {
        if (id == null) {
            // 获取所有子网一级
            List<Subnet> parentList = this.subnetService.selectSubnetByParentId(null);
            if (parentList.size() > 0) {
                for (Subnet subnet : parentList) {
                    this.genericSubnet(subnet);
                }
                return ResponseUtil.ok(parentList);
            }
        } else {
            // 校验子网是否存在
            Subnet subnet = this.subnetService.selectObjById(id);
            if (subnet != null) {
                // 当前网段
                Map map = new HashMap();
                map.put("subnet", subnet);
                // 获取从子网列表
                List<Subnet> subnetList = this.subnetService.selectSubnetByParentId(id);
                //
                map.put("subnets", subnetList);
                // 查询IP addresses in subnets
                if (subnetList.size() <= 0 && subnet.getMask() >= 24) {
                    // 获取地址列表
                    // 获取最大Ip地址和最小Ip地址
                    String mask = Ipv4Utils.bitMaskConvertMask(subnet.getMask());
                    Map networkMap = Ipv4Utils.getNetworkIp(subnet.getIp(), mask);
                } else if (subnetList.size() <= 0 && subnet.getMask() < 24 && subnet.getMask() >= 16) {
                    // 获取网段数量
                    String mask = Ipv4Utils.bitMaskConvertMask(subnet.getMask());
                    Map networkMap = Ipv4Utils.getNetworkIp(subnet.getIp(), mask);
                    String[] ips = Ipv4Utils.getSubnetList(networkMap.get("network").toString(),
                            subnet.getMask());
                    int sum = ips.length / 255;
                    List list = new ArrayList();
                    if (sum > 0) {
                        for (int i = 0; i < sum; i++) {
                            String ip = subnet.getIp();
                            String[] seg = ip.split("\\.");
                            StringBuffer buffer = new StringBuffer();
                            buffer.append(seg[0]).append(".").append(seg[1]).append(".").append(i).append(".").append(seg[3]).append("-").append("255");
                            list.add(buffer);
                        }
                    }
                    map.put("segmentation", list);
                }
                return ResponseUtil.ok(map);
            }
            return ResponseUtil.badArgument("网段不存在");
        }
        return ResponseUtil.ok();
    }

    @PutMapping
    public Result update(@RequestBody Subnet instance){
        return this.subnetService.update(instance);
    }
}
