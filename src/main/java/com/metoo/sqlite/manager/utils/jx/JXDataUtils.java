package com.metoo.sqlite.manager.utils.jx;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.metoo.sqlite.entity.*;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.utils.file.DataFileWrite;
import com.metoo.sqlite.utils.encryption.AesEncryptUtils;
import com.metoo.sqlite.vo.*;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class JXDataUtils {


    @Autowired
    private IGatewayInfoService gatewayInfoService;
    @Autowired
    private ITerminalService terminalService;
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private IDeviceScanService deviceScanService;
    @Autowired
    private IUnreachService unreachService;
    @Autowired
    private IProbeService probeService;
    @Autowired
    private ILicenseService licenseService;
    @Autowired
    private AesEncryptUtils aesEncryptUtils;
    @Autowired
    private DataUtils dataUtils;

    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    @Test
    public void testEncryptedData() {
        String encryptedData = this.getEncryptedData();
        System.out.println("加密数据" + encryptedData);
        try {
            System.out.println("解密数据" + EncrypUtils.decrypt(encryptedData));
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }
    }




    public String getEncryptedData() {
        String encryptedData = "";

        String data = getData();

        try {
            encryptedData = EncrypUtils.encrypt(data);

            DataFileWrite.write(encryptedData, "encrypt.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedData;
    }


    public String getData() {
        Map data = new HashMap();

        // 保留空值属性的设置

        data.put("areaInfo", new HashMap<>());
        data.put("gatewayInfo", new ArrayList<>());
        data.put("terminalInfo", new ArrayList<>());
        data.put("deviceInfo", new ArrayList<>());
        data.put("deviceScanInfo", new ArrayList<>());
//        data.put("UnreachIpInfo", new HashMap<>());
//        data.put("UnsureIpInfo", new HashMap<>());
        try {
            List<GatewayInfo> gatewayInfoList = this.gatewayInfoService.selectObjByMap(null);
            List<GatewayInfoVo> list1 = new ArrayList();
            if (gatewayInfoList.size() > 0) {
                //                gatewayInfoList.forEach();
                for (GatewayInfo gatewayInfo : gatewayInfoList) {
                    GatewayInfoVo vo = new GatewayInfoVo(gatewayInfo.getOperator(), gatewayInfo.getPort(), gatewayInfo.getIp_address()
                            , gatewayInfo.getIpv6_address(), gatewayInfo.getDeviceName());

                    list1.add(vo);
                }
            }
            data.put("gatewayInfo", list1);

            List<Terminal> terminalList = this.terminalService.selectObjByMap(null);
            List<TerminalInfoVo> list2 = new ArrayList();
            if (terminalList.size() > 0) {
                for (Terminal terminal : terminalList) {
                    TerminalInfoVo vo = new TerminalInfoVo(terminal.getMac(), terminal.getService(), terminal.getActive_port(), terminal.getOs(),
                            terminal.getIpv4addr(), terminal.getIpv6addr(), terminal.getMacvendor());
                    list2.add(vo);
                }
            }
            data.put("terminalInfo", list2);

            List<Device> deviceList = this.deviceService.selectObjByMap(null);
            List<DeviceInfoVo> list3 = new ArrayList();
            if (deviceList.size() > 0) {
                for (Device device : deviceList) {
                    DeviceInfoVo vo = new DeviceInfoVo(device.getModel(), device.getDeviceTypeId().toString(), device.getDeviceVendorId().toString(), device.getVersion(),
                            device.getName(), device.getIp(), device.getIpv6_address(), device.getIpv6Forward(), device.getIpv6_keyword(), device.getIpv6Addrcount());
                    list3.add(vo);
                }
            }
            data.put("deviceInfo", list3);

            List<DeviceScan> deviceScanList = this.deviceScanService.selectObjByMap(null);
            List<DeviceScanInfoVo> lsit4 = new ArrayList();
            if (deviceList.size() > 0) {
                for (DeviceScan deviceScan : deviceScanList) {
                    DeviceScanInfoVo vo = new DeviceScanInfoVo(deviceScan.getDevice_ipv4(),
                            deviceScan.getDevice_ipv6(), deviceScan.getDevice_product());
                    lsit4.add(vo);
                }
            }
            data.put("deviceScanInfo", lsit4);

//            List<Unreach> unreaches = this.unreachService.selectObjByMap(null);
//            List<UnsearchInfoVo> lsit5 = new ArrayList();
//            if (unreaches.size() > 0) {
//                for (Unreach unreach : unreaches) {
//                    UnsearchInfoVo vo = new UnsearchInfoVo(unreach.getIp_addr(),
//                            unreach.getMac_addr(), unreach.getMac_vendor());
//                    lsit5.add(vo);
//                }
//                data.put("UnreachIpInfo", lsit5);
//            }

//            List<Terminal> unsures = this.dataUtils.getUnsureInfo();
//            List<TerminalInfoVo> lsit6 = new ArrayList();
//            if (unsures.size() > 0) {
//                for (Terminal unsure : unsures) {
//                    TerminalInfoVo vo = new TerminalInfoVo(unsure.getMac(), unsure.getService(),
//                            unsure.getActive_port(), unsure.getOs(),
//                            unsure.getIpv4addr(), unsure.getIpv6addr(), unsure.getMacvendor());
//                    lsit6.add(vo);
//                }
//                data.put("UnsureIpInfo", lsit6);
//            }

            List<License> licenseList = this.licenseService.query();
            if (licenseList.size() > 0) {
                License obj = licenseList.get(0);

                String licenseInfo = this.aesEncryptUtils.decrypt(obj.getLicense());
                System.out.println("====licenseInfo:" + licenseInfo);
                LicenseVo license = JSONObject.parseObject(licenseInfo, LicenseVo.class);

                Map areaInfo = new HashMap();
                areaInfo.put("unitId", license.getUnit_id());
                areaInfo.put("unit", license.getUnit());
                areaInfo.put("area", license.getArea());
                areaInfo.put("city", license.getCity());
                areaInfo.put("version", license.getVersion());
                areaInfo.put("date", getDate());
                data.put("areaInfo", areaInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        String json = JSONObject.toJSONString(data, SerializerFeature.WriteMapNullValue);

////        写入文件
//        try {
//            // 创建一个 ObjectMapper 实例
//            ObjectMapper mapper = new ObjectMapper();
//            // 创建一个带有默认漂亮打印功能的 ObjectWriter 实例
//            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
//            String jsonString = writer.writeValueAsString(data);
//            DataFileWrite.write(jsonString, "unencrypt.txt");
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        return json;
//        org.json.JSONObject jsonObject = new org.json.JSONObject(new JSONTokener(org.json.JSONObject.valueToString(data)));
//        String formattedJson = jsonObject.toString(4); // 缩进4个空格

//        String jsonString = JSONObject.toJSONString(data, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);


//        String jsonString = ObjectWriter.writerWithDefaultPrettyPrinter(data, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);


    }

    @Test
    public void test() {
        System.out.println(getDate());
    }
}
