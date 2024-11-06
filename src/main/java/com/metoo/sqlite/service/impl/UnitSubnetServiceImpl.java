package com.metoo.sqlite.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.dto.DeviceDTO;
import com.metoo.sqlite.dto.UnitSubnetDTO;
import com.metoo.sqlite.dto.page.PageInfo;
import com.metoo.sqlite.entity.Area;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.UnitSubnet;
import com.metoo.sqlite.mapper.UnitSubnetMapper;
import com.metoo.sqlite.service.IAreaService;
import com.metoo.sqlite.service.IUnitSubnetService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.net.Ipv4Utils;
import com.metoo.sqlite.utils.net.Ipv6Utils;
import com.metoo.sqlite.vo.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.ws.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UnitSubnetServiceImpl implements IUnitSubnetService {

    @Resource
    private UnitSubnetMapper unitSubnetMapper;
    @Resource
    private IAreaService areaService;

    @Override
    public Result selectObjById(Integer id) {
        UnitSubnet unitSubnet = this.unitSubnetMapper.selectObjById(id);
        return ResponseUtil.ok(unitSubnet);
    }

    @Override
    public Result save(UnitSubnet instance) {
//        if(StringUtil.isEmpty(instance.getName())){
//            return ResponseUtil.badArgument("单位名称不能为空");
//        }else{
//            Map params = new HashMap();
//            params.put("name", instance.getName());
//            params.put("notId", instance.getId());
//            List<UnitSubnet> unitSubnets = this.selectObjByMap(params);
//            if(unitSubnets.size() >= 1){
//                return ResponseUtil.badArgument("单位名称重复");
//            }
//        }
        Area area = this.areaService.selectObjById(instance.getUnitId());
        if(area == null){
            return ResponseUtil.badArgument("所选单位不存在");
        }else{
            instance.setName(area.getName());
        }
        if(StringUtil.isNotEmpty(instance.getIpv4Subnet())){
            String[] ipv4SubnetList = instance.getIpv4Subnet().split(",");
            for (String ipv4 : ipv4SubnetList) {
                if(!Ipv4Utils.verifyCidr(ipv4)){
                    return ResponseUtil.badArgument(ipv4 + " 不符合CIDR格式");
                }
            }
        }
        if(StringUtil.isNotEmpty(instance.getIpv6Subnet())){
            String[] ipv6SubnetList = instance.getIpv6Subnet().split(",");
            for (String ipv6 : ipv6SubnetList) {
                if(!Ipv6Utils.isValidIPv6CIDR(ipv6)){
                    return ResponseUtil.badArgument(ipv6 + " 不符合CIDR格式");
                }
            }
        }
        if(instance.getId() == null || instance.getId().equals("")){
            instance.setCreateTime(DateTools.getCreateTime());
            try {
                int i = this.unitSubnetMapper.save(instance);
                if(i >= 1){
                    return ResponseUtil.ok();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseUtil.saveError();
            }
        }else{
            try {
                int i = this.unitSubnetMapper.update(instance);
                if(i >= 1){
                    return ResponseUtil.ok();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseUtil.saveError();
            }
        }
        return ResponseUtil.ok();
    }

    @Override
    public Result batch(List<UnitSubnet> instances) {
        if(instances.isEmpty()){
            return ResponseUtil.ok();
        }
        for (UnitSubnet instance : instances) {
//            if(StringUtil.isEmpty(instance.getName())){
//                return ResponseUtil.badArgument("单位名称不能为空");
//            }else{
//                Map params = new HashMap();
//                params.put("name", instance.getName());
//                params.put("notId", instance.getId());
//                List<UnitSubnet> unitSubnets = this.selectObjByMap(params);
//                if(unitSubnets.size() >= 1){
//                    return ResponseUtil.badArgument("单位名称重复");
//                }
//            }
            Area area = this.areaService.selectObjById(instance.getUnitId());
            if(area == null){
                return ResponseUtil.badArgument("所选单位不存在");
            }else{
                instance.setName(area.getName());
            }
            if(StringUtil.isNotEmpty(instance.getIpv4Subnet())){
                String[] ipv4SubnetList = instance.getIpv4Subnet().split(",");
                for (String ipv4 : ipv4SubnetList) {
                    if(!Ipv4Utils.verifyCidr(ipv4)){
                        return ResponseUtil.badArgument(ipv4 + " 不符合CIDR格式");
                    }
                }
            }
            if(StringUtil.isNotEmpty(instance.getIpv6Subnet())){
                String[] ipv6SubnetList = instance.getIpv6Subnet().split(",");
                for (String ipv6 : ipv6SubnetList) {
                    if(!Ipv6Utils.isValidIPv6CIDR(ipv6)){
                        return ResponseUtil.badArgument(ipv6 + " 不符合CIDR格式");
                    }
                }
            }
        }
        for (UnitSubnet instance : instances) {
            if(instance.getId() == null || instance.getId().equals("")){
                instance.setCreateTime(DateTools.getCreateTime());
                try {
                    int i = this.unitSubnetMapper.save(instance);
                    if(i <= 0){
                        return ResponseUtil.saveError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseUtil.saveError();
                }
            }else{
                try {
                    int i = this.unitSubnetMapper.update(instance);
                    if(i <= 0){
                        return ResponseUtil.saveError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseUtil.saveError();
                }
            }
        }
        return ResponseUtil.ok();
    }

    @Override
    public Result delete(String ids) {
        if(ids != null && !ids.equals("")){
            for (String id : ids.split(",")){
                UnitSubnet unitSubnet = this.unitSubnetMapper.selectObjById(Integer.parseInt(id));
                if(unitSubnet != null){
                    try {
                        this.unitSubnetMapper.delete(Integer.parseInt(id));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return ResponseUtil.badArgument(unitSubnet.getName() + "删除失败");
                    }
                }
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.badArgument();
    }

    @Override
    public Result selectObjConditionQuery(UnitSubnetDTO dto) {
        if(dto == null){
            dto = new UnitSubnetDTO();
        }
        Page<UnitSubnet> page = PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        this.unitSubnetMapper.selectObjConditionQuery(dto);

        return ResponseUtil.ok(new PageInfo<UnitSubnet>(page));
    }

    @Override
    public List<UnitSubnet> selectObjByMap(Map params) {
        return this.unitSubnetMapper.selectObjByMap(params);
    }
}
