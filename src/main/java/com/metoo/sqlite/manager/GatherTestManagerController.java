package com.metoo.sqlite.manager;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.enums.VersionResultType;
import com.metoo.sqlite.entity.Ipv4;
import com.metoo.sqlite.entity.License;
import com.metoo.sqlite.entity.PanaSwitch;
import com.metoo.sqlite.entity.Version;
import com.metoo.sqlite.gather.factory.gather.thread.Gather;
import com.metoo.sqlite.gather.factory.gather.thread.GatherFactory;
import com.metoo.sqlite.manager.api.remote.VersionManagerRemote;
import com.metoo.sqlite.manager.api.remote.VersionStatusUpdateRemote;
import com.metoo.sqlite.manager.utils.gather.ProbeToTerminalAndDeviceScan;
import com.metoo.sqlite.manager.utils.jx.JXDataUtils;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.service.impl.PublicService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.encryption.AesEncryptUtils;
import com.metoo.sqlite.utils.license.SystemInfoUtils;
import com.metoo.sqlite.vo.LicenseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RequestMapping("/admin/gather/test")
@RestController
public class GatherTestManagerController {

    @Autowired
    private ProbeToTerminalAndDeviceScan probeToTerminalAndDeviceScan;

    @Autowired
    private PublicService publicService;
    @Autowired
    private IVersionService versionService;
    @Autowired
    private ILicenseService licenseService;
    @Autowired
    private AesEncryptUtils aesEncryptUtils;
    @Autowired
    private VersionManagerRemote versionManagerRemote;
    @Autowired
    private VersionStatusUpdateRemote versionStatusUpdateRemote;
    @Autowired
    private JXDataUtils jxDataUtils;

    public static void main(String[] args) {
        String datas = "[{\"ip\":\"fe80::70ef:cea0:c250:cf60\",\"mac\":\"d4:5d:64:26:d8:7b\",\"netif\":\"ETH2\"}]";
        JSONArray data = JSONArray.parseArray(datas);
        if(data.size() > 0){
            List<Ipv4> ipv4List = new ArrayList<>();
            for (Object o : data) {
                JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(o));
                Ipv4 ipv4 = new Ipv4();
                ipv4.setIp(json.getString("ip"));
                ipv4.setPort(json.getString("netif"));
                ipv4.setMac(json.getString("mac"));
                ipv4List.add(ipv4);
            }
            log.info("数据打印: {}", ipv4List);
        }
        System.out.println(datas);
    }

    public Long getUnitId() throws Exception {
        List<License> licenseList = licenseService.query();
        if (licenseList.size() <= 0) {
            log.error("未经授权，不允许升级");
            return null;
        }
        License obj = licenseList.get(0);
        String uuid = SystemInfoUtils.getWindowsBiosUUID();
        if (!uuid.equals(obj.getSystemSN())) {
            log.error("未经授权，不允许升级");
            return null;
        }
        String licenseInfo = aesEncryptUtils.decrypt(obj.getLicense());
        if (StringUtil.isEmpty(licenseInfo)) {
            log.error("未经授权，不允许升级");
            return null;
        }
        LicenseVo license = JSONObject.parseObject(licenseInfo, LicenseVo.class);
        // TODO: 2024/9/21 默认为单位1的测试数据
        return NumberUtil.isNumber(license.getUnit_id()) ? Long.parseLong(license.getUnit_id()) : 1L;
    }

    @GetMapping("/status")
    public Integer status() throws Exception {
        Version version = versionService.selectObjByOne();
        Long unitId = getUnitId();
        if (null == unitId) {
            //获取不到单位编码
            log.error("单位编码获取出现问题");
            return VersionResultType.ERROR.getCode();
        }
        //  服务器端查询是否有版本更新
        String versionResult = versionManagerRemote.call(versionManagerRemote.versionParam(unitId, version.getVersion()));
        return null;
    }

    @GetMapping("/encrypted")
    public void encrypted() {
        String data = jxDataUtils.getEncryptedData();
    }


    @GetMapping("/terminal")
    public void gatherTerminal() {
        String beginTime = DateTools.getCreateTime();
        int temLogId = publicService.createSureyingLog("终端分析", beginTime, 1, null, 8);
        try {
            //this.gatherArp();
            this.gatherDeviceScan();
            GatherFactory factory = new GatherFactory();
            Gather gather = factory.getGather(Global.TERMINAL);
            gather.executeMethod();
            this.probeToTerminalAndDeviceScan.finalProbe();
//            this.verifyVendorUtils.finalTerminal();
            publicService.updateSureyingLog(temLogId, 2);
        } catch (Exception e) {
            e.printStackTrace();
            publicService.updateSureyingLog(temLogId, 3);
        }
    }

    @GetMapping("/device_scan")
    public void device_scan() {
        try {
            this.gatherDeviceScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gatherDeviceScan() throws Exception {
        GatherFactory factory = new GatherFactory();
        Gather gather = factory.getGather(Global.DEVICE_SCAN);
        gather.executeMethod();
    }

    @GetMapping("/arp")
    public void arp() {
        try {
            GatherFactory factory = new GatherFactory();
            Gather gather = factory.getGather(Global.ARP);
            gather.executeMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
