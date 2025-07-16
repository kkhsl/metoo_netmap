package com.metoo.sqlite.api.controller;

import com.metoo.sqlite.api.dto.DeviceSysInfoDTO;
import com.metoo.sqlite.api.service.IDeviceSysInfoService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;
import java.util.Date;
import java.util.List;

/**
 * 接收系统数据
 *
 */
@Slf4j
@RequestMapping("/api/v1/device/sys")
@RestController
public class DeviceSysInfoController {

    private final IDeviceSysInfoService deviceSysInfoService;
    @Autowired
    public DeviceSysInfoController(IDeviceSysInfoService deviceSysInfoService){
        this.deviceSysInfoService = deviceSysInfoService;
    }

    @PostMapping("/info")
    public Result receiveDeviceInfo(@RequestBody DeviceSysInfoDTO deviceSysInfoDTO) {
        boolean flag = true;
        try {
            if(deviceSysInfoDTO.getMac_addresses() != null && deviceSysInfoDTO.getMac_addresses().size() > 0){
                for (String mac_address : deviceSysInfoDTO.getMac_addresses()) {
                    if(mac_address != null && !"".equals(mac_address)){
                        List<DeviceSysInfoDTO> deviceSysInfoDTOList = deviceSysInfoService.query(mac_address);
                        if(deviceSysInfoDTOList.size() > 0){
                            flag = false;
                            for (DeviceSysInfoDTO sysInfoDTO : deviceSysInfoDTOList) {
                                try {
                                    sysInfoDTO.setUpdateTime(new Date());
                                    sysInfoDTO.setCpu(deviceSysInfoDTO.getCpu());
                                    sysInfoDTO.setMac_addresses(deviceSysInfoDTO.getMac_addresses());
                                    sysInfoDTO.setManufacturer(deviceSysInfoDTO.getManufacturer());
                                    sysInfoDTO.setModel(deviceSysInfoDTO.getModel());
                                    sysInfoDTO.setOs(deviceSysInfoDTO.getOs());
                                    sysInfoDTO.setOs_name(deviceSysInfoDTO.getOs_name());
                                    deviceSysInfoService.update(sysInfoDTO);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    log.info("Error updating device system info: {}", e.getMessage());
                                    return ResponseUtil.saveError();
                                }
                            }
                            log.info("Device system info received and update successfully");
                            return ResponseUtil.ok();
                        }
                    }
                }
                if(flag){
                    int i = deviceSysInfoService.insert(deviceSysInfoDTO);
                    if(i <= 1){
                        log.info("Device system info received and saved successfully");
                        return ResponseUtil.ok();
                    }
                }
            }
            return ResponseUtil.saveError();
        } catch (Exception e) {
            log.info("Error saving device system info: {}", e.getMessage());
            return ResponseUtil.saveError();
        }
    }


}
