package com.metoo.sqlite.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.dto.DeviceDTO;
import com.metoo.sqlite.dto.page.PageInfo;
import com.metoo.sqlite.entity.*;
import com.metoo.sqlite.mapper.DeviceMapper;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.StringUtils;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.vo.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-22 14:57
 */
@Service
@Transactional
public class DeviceServiceImpl implements IDeviceService {

    @Resource
    private DeviceMapper deviceMapper;
    @Autowired
    private IDeviceTypeService deviceTypeService;
    @Autowired
    private IDeviceVendorService deviceVendorService;
    @Autowired
    private IDeviceModelService deviceModelService;

    @Override
    public Device selectObjById(Integer id) {
        Device device = this.deviceMapper.selectObjById(id);
        String alias2 = "";
        if(device.getDeviceTypeId() != null
                && !device.getDeviceTypeId().equals("")){
            DeviceType deviceType = this.deviceTypeService.selectObjById(device.getDeviceTypeId());
            if(deviceType != null){
                device.setDeviceTypeName(deviceType.getName());
                device.setDeviceTypeAlias(deviceType.getAlias());
            }
        }
        if(device.getDeviceVendorId() != null
                && !device.getDeviceVendorId().equals("")){
            DeviceVendor deviceVendor = this.deviceVendorService.selectObjById(device.getDeviceVendorId());
            if(deviceVendor != null){
                device.setDeviceVendorName(deviceVendor.getName());
                device.setDeviceVendorAlias(deviceVendor.getAlias());
                device.setDeviceVendorSequence(deviceVendor.getSequence());
                alias2 = deviceVendor.getDeviceTypeAlias();
            }
        }
        if(device.getDeviceModelId() != null
                && !device.getDeviceModelId().equals("")){
            DeviceModel deviceModel = this.deviceModelService.selectObjById(device.getDeviceModelId());
            if(deviceModel != null){
                device.setDeviceModelName(deviceModel.getName());
            }
        }

        // 防火墙（firewall） + 深信服（sanfor） firewall -> linux
        if(StringUtil.isNotEmpty(alias2)){
            device.setDeviceTypeAlias(alias2);
        }
        return device;
    }

    @Override
    public List<Device> selectObjByMap(Map params) {
        List<Device> deviceList = this.deviceMapper.selectObjByMap(params);
        if(deviceList.size() > 0){
            for (Device device : deviceList) {
                String alias2 = "";
                if(device.getDeviceTypeId() != null
                        && !device.getDeviceTypeId().equals("")){
                    DeviceType deviceType = this.deviceTypeService.selectObjById(device.getDeviceTypeId());
                    if(deviceType != null){
                        device.setDeviceTypeName(deviceType.getName());
                        device.setDeviceTypeAlias(deviceType.getAlias());
                    }
                }
                if(device.getDeviceVendorId() != null
                        && !device.getDeviceVendorId().equals("")){
                    DeviceVendor deviceVendor = this.deviceVendorService.selectObjById(device.getDeviceVendorId());
                    if(deviceVendor != null){
                        device.setDeviceVendorName(deviceVendor.getName());
                        device.setDeviceVendorAlias(deviceVendor.getAlias());
                        device.setDeviceVendorSequence(deviceVendor.getSequence());
                        alias2 = deviceVendor.getDeviceTypeAlias();
                    }
                }
                if(device.getDeviceModelId() != null
                        && !device.getDeviceModelId().equals("")){
                    DeviceModel deviceModel = this.deviceModelService.selectObjById(device.getDeviceModelId());
                    if(deviceModel != null){
                        device.setDeviceModelName(deviceModel.getName());
                    }
                }

                // 防火墙（firewall） + 深信服（sanfor） firewall -> linux
                if(StringUtil.isNotEmpty(alias2)){
                    device.setDeviceTypeAlias(alias2);
                }
            }
        }
        return deviceList;
    }

    @Override
    public boolean verifyLogDevice(){
        Map params = new HashMap();
        params.put("type", 1);
        List<Device> deviceList = this.deviceMapper.selectObjByMap(params);
        if(deviceList.size() <= 0){
            return true;
        }
        return false;
    }

    @Override
    public int update(Device instance) {
        try {
            boolean flag = verifyLogDevice();
            if(flag){
                System.out.println(1);
            }
            return this.deviceMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Result selectObjConditionQuery(DeviceDTO dto) {

        Map data = new HashMap();

        if(dto == null){
            dto = new DeviceDTO();
        }
        Page<Device> page = PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
        this.deviceMapper.selectObjConditionQuery(dto);
        if (page.getResult().size() > 0) {
            for (Device instance : page.getResult()) {

                if(instance.getDeviceTypeId() != null
                        && !instance.getDeviceTypeId().equals("")){
                    DeviceType deviceType = this.deviceTypeService.selectObjById(instance.getDeviceTypeId());
                    if(deviceType != null){
                        instance.setDeviceTypeName(deviceType.getName());
                    }
                }
                if(instance.getDeviceVendorId() != null
                        && !instance.getDeviceVendorId().equals("")){
                    DeviceVendor deviceVendor = this.deviceVendorService.selectObjById(instance.getDeviceVendorId());
                    if(deviceVendor != null){
                        instance.setDeviceVendorName(deviceVendor.getName());
                        instance.setDeviceVendorSequence(deviceVendor.getSequence());
                    }
                }
                if(instance.getDeviceModelId() != null
                        && !instance.getDeviceModelId().equals("")){
                    DeviceModel deviceModel = this.deviceModelService.selectObjById(instance.getDeviceModelId());
                    if(deviceModel != null){
                        instance.setDeviceModelName(deviceModel.getName());
                    }
                }

            }

        }

        List<DeviceType> deviceTypes = this.deviceTypeService.selectObjByMap(null);
        if(deviceTypes.size() > 0){
            Map params = new HashMap();
            for (DeviceType deviceType : deviceTypes) {
                params.clear();
                params.put("deviceTypeId", deviceType.getId());
                List<DeviceVendor> deviceVendors = this.deviceVendorService.selectObjByMap(params);
                deviceType.setDeviceVendorList(deviceVendors);
            }
        }
        data.put("deviceType", deviceTypes);
//        List<DeviceVendor> deviceVendors = this.deviceVendorService.selectObjByMap(null);
//        data.put("deviceVendor", deviceVendors);
        List<DeviceModel> deviceModels = this.deviceModelService.selectObjByMap(null);
        data.put("deviceModel", deviceModels);
        return ResponseUtil.ok(new PageInfo<Device>(page, data));
    }

    @Override
    public Result save(Device instance) {
        if(instance.getType() == null || instance.getType() != 1){
            Result result = this.verifyParams(instance);
            if(result != null){
                return result;
            }
        }

        if(instance.getId() == null || instance.getId().equals("")){
            // 验证设备类型，日志设备仅允许添加一台，可在aop中实现
            if(instance.getType() != null && instance.getType() == 1){
                boolean flag = this.verifyLogDevice();
                if(!flag){
                    return ResponseUtil.badArgument("日志设备仅允许添加一台");
                }
            }
            instance.setCreateTime(DateTools.getCreateTime());
            UUID uuid = UUID.randomUUID();
            instance.setUuid(uuid.toString());
            if(StringUtil.isNotEmpty(instance.getLoginPassword())){
                instance.setLoginPassword("\""+instance.getLoginPassword()+"\"");
            }
            int i = this.deviceMapper.save(instance);
            if(i >= 1){
                return ResponseUtil.ok();
            }
        }else{
            if(StringUtil.isNotEmpty(instance.getLoginPassword())){
                if(!instance.getLoginPassword().substring(0, 1).equals("\"")){
                    instance.setLoginPassword("\""+instance.getLoginPassword()+"\"");
                }
            }
            int i = this.deviceMapper.update(instance);
            if(i >= 1){
                return ResponseUtil.ok();
            }
        }
        return ResponseUtil.saveError();
    }

    @Override
    public Result batchSave(List<Device> devices) {
        // 验证设备类型，日志设备仅允许添加一台，可在aop中实现
        long count = devices.stream().filter(device -> device.getType() != null && device.getType() == 1).count();
        if(count > 1){
            return ResponseUtil.badArgument("日志设备仅允许添加一台");
        }else if(count == 1){
            Optional<Device> typeDevice = devices.stream()
                    .filter(device -> device.getType() != null && device.getType() == 1).findFirst();
            Device device = typeDevice.orElse(new Device());
            boolean flag = this.verifyLogDevice();
            if(!flag && (device.getId() == null || device.getId().equals(""))){
                return ResponseUtil.badArgument("日志设备仅允许添加一台");
            }
        }
        // 校验id不为空对象是否修改
        for (Device device : devices) {
            if(device.getType() == null || device.getType() != 1){
                Result result = this.verifyParams2(device);
                if(result != null){
                    return result;
                }
            }
            if(device.getId() != null && !device.getId().equals("")){
                if(StringUtil.isNotEmpty(device.getLoginPassword())){
                    if(!device.getLoginPassword().substring(0, 1).equals("\"")){
                        device.setLoginPassword("\""+device.getLoginPassword()+"\"");
                    }
                }


                Device obj = this.deviceMapper.selectObjById(device.getId());
                if(obj == null){
                    return ResponseUtil.badArgument(device.getName() + "参数错误");
                }
                // 对 JSON 字符串进行 MD5 加密
                String md5Value1 = DigestUtils.md5Hex(JSONObject.toJSONString(device));
                String md5Value2 = DigestUtils.md5Hex(JSONObject.toJSONString(obj));
                if(!md5Value1.equals(md5Value2)){
                    try {
                        int i = this.deviceMapper.update(device);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseUtil.error(device.getName() + "保存失败");
                    }
                }
            }else{
                UUID uuid = UUID.randomUUID();
                device.setUuid(uuid.toString());
                device.setCreateTime(DateTools.getCreateTime());
                if(StringUtil.isNotEmpty(device.getLoginPassword())){
                    device.setLoginPassword("\""+device.getLoginPassword()+"\"");
                }
                try {
                    int i = this.deviceMapper.save(device);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseUtil.error(device.getName() + "保存失败");
                }
            }
        }
        return ResponseUtil.ok();
    }

    @Override
    public Result modify(Integer id) {
        Device instance = null;
        if(id != null && !id.equals("")){
            instance = this.deviceMapper.selectObjById(id);
            if(instance != null){
                if(instance.getDeviceTypeId() != null
                        && !instance.getDeviceTypeId().equals("")){
                    DeviceType deviceType = this.deviceTypeService.selectObjById(instance.getDeviceTypeId());
                    if(deviceType != null){
                        instance.setDeviceTypeName(deviceType.getName());
                    }
                }
                if(instance.getDeviceVendorId() != null
                        && !instance.getDeviceVendorId().equals("")){
                    DeviceVendor deviceVendor = this.deviceVendorService.selectObjById(instance.getDeviceVendorId());
                    if(deviceVendor != null){
                        instance.setDeviceVendorName(deviceVendor.getName());
                    }
                }
                if(instance.getDeviceModelId() != null
                        && !instance.getDeviceModelId().equals("")){
                    DeviceModel deviceModel = this.deviceModelService.selectObjById(instance.getDeviceModelId());
                    if(deviceModel != null){
                        instance.setDeviceModelName(deviceModel.getName());
                    }
                }
            }else{
                return ResponseUtil.badArgument("资源不存在");
            }
        }
        Map data = new HashMap();
        data.put("device", instance);
        List<DeviceType> deviceTypes = this.deviceTypeService.selectObjByMap(null);
        data.put("deviceType", deviceTypes);
        List<DeviceVendor> deviceVendors = this.deviceVendorService.selectObjByMap(null);
        data.put("deviceVendor", deviceVendors);
        List<DeviceModel> deviceModels = this.deviceModelService.selectObjByMap(null);
        data.put("deviceModel", deviceModels);
        return ResponseUtil.ok(data);
    }

    public Result verifyParams(Device instance){
        Map params = new HashMap();
        if(StringUtils.isEmpty(instance.getName())){
            return ResponseUtil.badArgument("设备名称不能为空");
        }else{
            params.clear();
            params.put("notId", instance.getId());
            params.put("name", instance.getName());
            List<Device> devices = this.deviceMapper.selectObjByMap(params);
            if(devices.size() > 0){
                return ResponseUtil.badArgument("设备名称不能重复");
            }
        }
        if(StringUtils.isEmpty(instance.getIp())){
            return ResponseUtil.badArgument("设备Ip不能为空");
        }else{
            params.clear();
            params.put("notId", instance.getId());
            params.put("ip", instance.getIp());
            List<Device> devices = this.deviceMapper.selectObjByMap(params);
            if(devices.size() > 0){
                return ResponseUtil.badArgument("设备Ip不能重复");
            }
        }
        if(StringUtils.isEmpty(instance.getLoginName())){
            return ResponseUtil.badArgument("登录名称不能为空");
        }
        if(StringUtils.isEmpty(instance.getLoginPassword())){
            return ResponseUtil.badArgument("登录密码不能为空");
        }
        if(StringUtils.isEmpty(instance.getLoginPort())){
            return ResponseUtil.badArgument("登录端口不能为空");
        }
        if(StringUtils.isEmpty(instance.getLoginType())){
            return ResponseUtil.badArgument("登录类型不能为空");
        }
        if(instance.getDeviceTypeId() == null
                || instance.getDeviceTypeId().equals("")){
            return ResponseUtil.badArgument("设备类型不能为空");
        }
        if(instance.getDeviceVendorId() == null
                || instance.getDeviceVendorId().equals("")){
            return ResponseUtil.badArgument("设备品牌不能为空");
        }
//        if(instance.getDeviceModelId() == null
//                || instance.getDeviceModelId().equals("")){
//            return ResponseUtil.badArgument("设备型号不能为空");
//        }
        return null;
    }

    public Result verifyParams2(Device instance){
        Map params = new HashMap();
        if(StringUtils.isEmpty(instance.getName())){
            return ResponseUtil.badArgument(instance.getName() + "设备名称不能为空");
        }else{
            params.clear();
            params.put("notId", instance.getId());
            params.put("name", instance.getName());
            List<Device> devices = this.deviceMapper.selectObjByMap(params);
            if(devices.size() > 0){
                return ResponseUtil.badArgument(instance.getName() + "设备名称不能重复");
            }
        }
        if(StringUtils.isEmpty(instance.getIp())){
            return ResponseUtil.badArgument(instance.getName() + "设备Ip不能为空");
        }else{
            params.clear();
            params.put("notId", instance.getId());
            params.put("ip", instance.getIp());
            List<Device> devices = this.deviceMapper.selectObjByMap(params);
            if(devices.size() > 0){
                return ResponseUtil.badArgument(instance.getName() + "设备Ip不能重复");
            }
        }
        if(StringUtils.isEmpty(instance.getLoginName())){
            return ResponseUtil.badArgument(instance.getName() + "登录名称不能为空");
        }
        if(StringUtils.isEmpty(instance.getLoginPassword())){
            return ResponseUtil.badArgument(instance.getName() + "登录密码不能为空");
        }
        if(StringUtils.isEmpty(instance.getLoginPort())){
            return ResponseUtil.badArgument(instance.getName() + "登录端口不能为空");
        }
        if(StringUtils.isEmpty(instance.getLoginType())){
            return ResponseUtil.badArgument(instance.getName() + "登录类型不能为空");
        }
        if(instance.getDeviceTypeId() == null
                || instance.getDeviceTypeId().equals("")){
            return ResponseUtil.badArgument(instance.getName() + "设备类型不能为空");
        }
        if(instance.getDeviceVendorId() == null
                || instance.getDeviceVendorId().equals("")){
            return ResponseUtil.badArgument(instance.getName() + "设备品牌不能为空");
        }
//        if(instance.getDeviceModelId() == null
//                || instance.getDeviceModelId().equals("")){
//            return ResponseUtil.badArgument("设备型号不能为空");
//        }

        return null;
    }

    @Override
    public Result delete(String ids) {
        if(ids != null && !ids.equals("")){
            for (String id : ids.split(",")){
                Device device = this.deviceMapper.selectObjById(Integer.parseInt(id));
                if(device != null){
                    try {
                        this.deviceMapper.delete(Integer.parseInt(id));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return ResponseUtil.badArgument(device.getName() + "删除失败");
                    }
                }
            }
            return ResponseUtil.ok();
        }
        return ResponseUtil.badArgument();
    }

}
