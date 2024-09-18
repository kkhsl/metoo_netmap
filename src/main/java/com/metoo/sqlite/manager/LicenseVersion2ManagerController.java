//package com.metoo.sqlite.manager;
//
//import com.alibaba.fastjson.JSONObject;
//import com.metoo.sqlite.dto.LicenseDto;
//import com.metoo.sqlite.entity.License;
//import com.metoo.sqlite.entity.Version;
//import com.metoo.sqlite.service.ILicenseService;
//import com.metoo.sqlite.service.IVersionService;
//import com.metoo.sqlite.utils.ResponseUtil;
//import com.metoo.sqlite.utils.date.DateTools;
//import com.metoo.sqlite.utils.encryption.AesEncryptUtils;
//import com.metoo.sqlite.utils.license.LicenseTools;
//import com.metoo.sqlite.utils.license.SystemInfoUtils;
//import com.metoo.sqlite.vo.LicenseVo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@RequestMapping("/admin/license")
//@RestController
//public class LicenseVersion2ManagerController {
//
//    @Autowired
//    private ILicenseService licenseService;
//    @Autowired
//    private AesEncryptUtils aesEncryptUtils;
//    @Autowired
//    private LicenseTools licenseTools;
//    @Autowired
//    private IVersionService versionService;
//
//    @RequestMapping("/systemInfo")
//    public Object systemInfo(){
//        try {
//            String sn = WindowsUniqueID.getDiskSerialNumber();
//            return ResponseUtil.ok(sn);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @GetMapping(value = "/query")
//    public Object query(){
//        License obj = this.licenseService.query().get(0);
//
//        String uuid = WindowsUniqueID.getDiskSerialNumber();
//
//        if(!uuid.equals(obj.getSystemSN())){
//            return ResponseUtil.error("未授权设备");
//        }
//
//        try {
//            String licenseInfo = this.aesEncryptUtils.decrypt(obj.getLicense());
//
//            log.info("====licenseInfo: 授权数据" + licenseInfo);
//
//            LicenseVo license = JSONObject.parseObject(licenseInfo, LicenseVo.class);
//
//
//            log.info("====licenseVo: 授权数据" + license);
//
//            long currentTime = DateTools.currentTimeMillis();
//
//            int useDay = DateTools.compare(currentTime, license.getStartTime());
//            license.setUseDay(useDay);
//
//            int surplusDay = DateTools.compare(license.getEndTime(), currentTime);
//            license.setSurplusDay(surplusDay);
//
//            int licenseDay = DateTools.compare(license.getEndTime(), license.getStartTime());
//            license.setLicenseDay(licenseDay);
//
//            Version version = this.versionService.selectObjByOne();
//            if(version != null){
//                license.setLicenseVersion(version.getVersion());
//            }
//
//            System.out.println(JSONObject.toJSONString(license));
//            return ResponseUtil.ok(license);
//        } catch (Exception e) {
//             e.printStackTrace();
//        }
//        return ResponseUtil.ok();
//    }
//
//    /**
//     * 授权
//     * @param license
//     * @return
//     */
//    @PutMapping("update")
//    public Object license(@RequestBody Map license){
////        String uuid = SystemInfoUtils.getBiosUuid();
//        String uuid = WindowsUniqueID.getDiskSerialNumber();
//
//        String code = license.get("license").toString();
//
//        boolean flag = this.licenseTools.verify(uuid, code);
//
//        if(flag){
//            List<License> list = this.licenseService.query();
//            if(list.size() > 0){
//                License obj = list.get(0);
//                if(!code.equals(obj.getLicense())){
//                    obj.setLicense(code);
//                    obj.setFrom(0);
//                    obj.setSystemSN(uuid);
//                    obj.setStatus(0);
//                    if(!this.verify(code)){
//                        obj.setStatus(2);
//                    }
//                    this.licenseService.update(obj);
//                    return ResponseUtil.ok("授权成功");
//                }
//                return ResponseUtil.badArgument("重复授权");
//            }else{
//                License instance = new License();
//                instance.setLicense(code);
//                instance.setFrom(0);
//                instance.setSystemSN(uuid);
//                instance.setStatus(1);
//                if(!this.verify(code)){
//                    instance.setStatus(2);
//                }
//                this.licenseService.save(instance);
//                return ResponseUtil.ok("授权成功");
//            }
//        }
//        return ResponseUtil.error("非法授权码");
//    }
//
//    @RequestMapping("/license")
//    public void license(){
//        // 1，获取设备唯一申请码
//        String uuid = WindowsUniqueID.getDiskSerialNumber();
//
//        // 2，查询并比对当前设备是否允许授权
//        List<License> licenseList = this.licenseService.query();
//        License license = null;
//        if(licenseList.size() > 0){
//            license = licenseList.get(0);
//        }
//        if(license != null){
//            String systemSN = license.getSystemSN();
//            boolean from = true;// 是否检测授权码：不在同意设备时不允许使用
//            // 初始化设备，并检查当前设备是否为初始化设备
//            if(systemSN == null || systemSN.isEmpty()){// 申请码为空
//                license.setSystemSN(uuid);
//            }else{
//                if(!systemSN.equals(uuid)){// 申请码不一致
//                    license.setFrom(1);
//                    license.setStatus(1);
//                    from = false;
//                }else{// 一致时恢复来源
//                    license.setFrom(0);
//                }
//            }
//            // 检测授权码是否已过期
//            if(from && license.getLicense() != null && !license.getLicense().isEmpty()){
//                String licenseInfo = license.getLicense();
//                Map map = null;
//                try {
//                    map = JSONObject.parseObject(aesEncryptUtils.decrypt(licenseInfo), Map.class);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if(map != null){
//                    String endTimeStamp = map.get("expireTime").toString();// 有效期
//                    if(endTimeStamp != null && !endTimeStamp.isEmpty()){
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTime(new Date());
//                        long currentTime = calendar.getTimeInMillis();
//                        long timeStampSec = currentTime / 1000;// 13位时间戳（单位毫秒）转换为10位字符串（单位秒）
//                        String timestamp = String.format("%010d", timeStampSec);// 当前时间
//                        if(Long.valueOf(endTimeStamp).compareTo(Long.valueOf(timestamp)) <= 0){
//                            license.setStatus(2);// 过期
//                        }else{
//                            license.setStatus(0);// 恢复为未过期
//                        }
//                    }
//                }
//                license.setStatus(1);// 未授权
//            }
//            // 更新License
//            licenseService.update(license);
//        }
//    }
//
//    public boolean verify(String code)  {
//        // 检测授权码是否已过期
//        if (code != null) {
//            License license = null;
//            try {
//                license = JSONObject.parseObject(this.aesEncryptUtils.decrypt(code), License.class);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (license != null) {
//                Long endTime = license.getEndTime();// 有效期
//                if (endTime != null) {
//                    long currentTime = DateTools.currentTimeMillis();
//                    if (endTime - currentTime >= 0) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//
//    @PostMapping("/generate")
//    public Object license(@RequestBody(required=false) LicenseDto dto){
//        if(dto.getSystemSN() != null){
//            try {
//                return this.aesEncryptUtils.encrypt(JSONObject.toJSONString(dto));
//            } catch (Exception e) {
//                e.printStackTrace();
//                return ResponseUtil.error("申请码错误");
//            }
//        }
//        return null;
//    }
//
//}
