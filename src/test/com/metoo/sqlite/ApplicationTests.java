package com.metoo.sqlite;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApplicationTests {

    @Test
    void contextLoads() {

        String data = getData();
    }


    public String getEncryptedData() {
        String encryptedData = "";

        String data = getData();

        try {
            encryptedData = EncrypUtils.encrypt(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedData;
    }

    public String getData() {
        Map data = new HashMap();
        data.put("gatewayInfo", new ArrayList<>());
        data.put("terminalInfo", new ArrayList<>());
        data.put("deviceInfo", new ArrayList<>());
        data.put("areaInfo", new HashMap<>());
        try {
            List<GatewayInfo> gatewayInfoList = this.gatewayInfoService.selectObjByMap(null);
            List list1 = new ArrayList();
            if (gatewayInfoList.size() > 0) {
                //                gatewayInfoList.forEach();
                for (GatewayInfo gatewayInfo : gatewayInfoList) {
                    GatewayInfoVo vo = new GatewayInfoVo(gatewayInfo.getOperator(), gatewayInfo.getPort(), gatewayInfo.getIp_address()
                            , gatewayInfo.getIpv6_address(), gatewayInfo.getDeviceName());
                    list1.add(vo);
                }
                data.put("gatewayInfo", list1);
            }

            List<Terminal> terminalList = this.terminalService.selectObjByMap(null);
            List list2 = new ArrayList();
            if (terminalList.size() > 0) {
                for (Terminal terminal : terminalList) {
                    TerminalInfoVo vo = new TerminalInfoVo(terminal.getMac(), terminal.getService(), terminal.getActive_port(), terminal.getOs(),
                            terminal.getIpv4addr(), terminal.getIpv6addr(), terminal.getMacvendor());
                    list2.add(vo);
                }
                data.put("terminalInfo", list2);
            }

            List<Device> deviceList = this.deviceService.selectObjByMap(null);
            List list3 = new ArrayList();
            if (deviceList.size() > 0) {
                for (Device device : deviceList) {
                    DeviceInfoVo vo = new DeviceInfoVo(device.getModel(), device.getDeviceTypeId().toString(), device.getDeviceVendorId().toString(), device.getVersion(),
                            device.getName(), device.getIp(), device.getIpv6_address(), device.getIpv6Forward(), device.getIpv6_keyword(), device.getIpv6Addrcount());
                    list3.add(vo);
                }
                data.put("deviceInfo", list3);
            }

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

        return JSONObject.toJSONString(data);
    }
}
