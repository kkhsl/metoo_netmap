package com.metoo.sqlite.manager;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.dto.DeviceDTO;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.DeviceType;
import com.metoo.sqlite.entity.DeviceVendor;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.pool.ThreadPoolUtils;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IDeviceService;
import com.metoo.sqlite.service.IDeviceTypeService;
import com.metoo.sqlite.service.IDeviceVendorService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.muyun.MuyunService;
import com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils.PanabitService;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-22 14:53
 */
@Slf4j
@RequestMapping("/admin/device")
@RestController
public class DeviceManagerController {

    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private IDeviceTypeService deviceTypeService;
    @Autowired
    private IDeviceVendorService deviceVendorService;
    @Autowired
    private PyExecUtils pyExecUtils;
    @Autowired
    private PanabitService panabitService;
    @Autowired
    private MuyunService muyunService;

    @PostMapping("/list")
    private Result list(@RequestBody DeviceDTO dto) {
        Result result = this.deviceService.selectObjConditionQuery(dto);
        return result;
    }

    @PostMapping("/save")
    private Result save(@RequestBody Device instance) {
        Result result = this.deviceService.save(instance);
        return result;
    }

    @PostMapping("/batch/save")
    private Result batchSave(@RequestBody List<Device> devices) {
        if (devices.size() > 0) {
            Result result = this.deviceService.batchSave(devices);
            return result;
        }
        return ResponseUtil.ok();
    }

    @GetMapping("/modify")
    public Result modify(@RequestParam Integer id) {
        Result result = this.deviceService.modify(id);
        return ResponseUtil.ok(result);
    }

    @DeleteMapping("/delete")
    public Result delete(@RequestParam String ids) {
        Result result = this.deviceService.delete(ids);
        return result;
    }

    @GetMapping("/test")
    public Result deviceType(@RequestParam Integer id) {
        boolean flag = false;
        Device device = this.deviceService.selectObjById(id);

        log.info("Test ====================== " + JSONObject.toJSONString(device));

        if (device != null) {
//            if (device.getLoginType() != null) {
//                if (device.getLoginType().equals("ssh") || device.getLoginType().equals("telnet")) {
//                    flag = deviceTestPy(device);
//                } else if(device.getLoginType().equals("gw")
//                        && device.getLoginType().equals("telnet")
//                        && (device.getDeviceVendorAlias().equals("huawei") || device.getDeviceVendorAlias().equals("h3c"))){
//                    device.setDeviceTypeAlias("firewall");
//
//                    flag = this.deviceTestPy(device);
//
//                }else if (device.getLoginType().equals("api")) {
//                    log.info("Test ====================== API");
//                    flag = this.deviceTestPanabit(device);
//                }
//            }

            if (device.getLoginType() != null) {
                if (device.getLoginType().equals("ssh") || device.getLoginType().equals("telnet")) {
                    flag = deviceTestPy(device);
                } else if (device.getLoginType().equals("api") && device.getDeviceVendorAlias().equals("pana")) {

                    log.info("Test ====================== API");
                    flag = this.deviceTestPanabit(device);
                } else if (device.getLoginType().equals("api") && device.getDeviceVendorAlias().equals("muyun")) {
                    log.info("Test ====================== API");
                    flag = this.deviceTestMuyun(device);
                }

            }
        }
        return ResponseUtil.ok(flag);
    }


    // 有没有boolean拆箱问题
    public boolean deviceTestPanabit(Device device) {
        String result = this.panabitService.load_ipobj_list("all", device.getIp(),
                device.getLoginPort(), device.getLoginName(), device.getLoginPassword().replaceAll("^\"|\"$", ""));
        log.info("Api result ================ " + result);
        if (StringUtil.isNotEmpty(result)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    public boolean deviceTestMuyun(Device device) {
        String result = this.muyunService.getArp(device.getIp(),
                Integer.parseInt(device.getLoginPort()), device.getLoginName(), device.getLoginPassword().replaceAll("^\"|\"$", ""));
        log.info("Api result ================ " + result);
        if (StringUtil.isNotEmpty(result)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static void main(String[] args) {
        String password = "\"123456789\"";
        String result = password.replaceAll("^\"|\"$", "");
        System.out.println(result);
    }

    public boolean deviceTestVendor(Device device) {
        String result = this.panabitService.load_ipobj_list("all", device.getIp(),
                device.getLoginPort(), device.getLoginName(), device.getLoginPassword());
        if (StringUtil.isNotEmpty(result)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public boolean deviceTestPy(Device device) {
        if (device.getDeviceTypeId() != null
                && !device.getDeviceTypeId().equals("")) {
            DeviceType deviceType = this.deviceTypeService.selectObjById(device.getDeviceTypeId());
            if (deviceType != null) {
                device.setDeviceTypeName(deviceType.getName());
                device.setDeviceTypeAlias(deviceType.getAlias());
            }
        }
        if (device.getDeviceVendorId() != null
                && !device.getDeviceVendorId().equals("")) {
            DeviceVendor deviceVendor = this.deviceVendorService.selectObjById(device.getDeviceVendorId());
            if (deviceVendor != null) {
                device.setDeviceVendorName(deviceVendor.getName());
                device.setDeviceVendorAlias(deviceVendor.getAlias());
                if(StringUtil.isNotEmpty(deviceVendor.getDeviceTypeAlias())){
                    device.setDeviceTypeAlias(deviceVendor.getDeviceTypeAlias());
                }
            }
        }
        PyCommandBuilder pyCommand = new PyCommandBuilder();
        pyCommand.setVersion(Global.py_name);
        pyCommand.setPath(Global.PYPATH);
        pyCommand.setPy_prefix("-W ignore");
        pyCommand.setName("main.pyc");
        pyCommand.setParams(new String[]{
                device.getDeviceVendorAlias(),
                device.getDeviceTypeAlias(),
                device.getIp(),
                device.getLoginType(),
                device.getLoginPort(),
                device.getLoginName(),
                device.getLoginPassword(), "test"});
        String result = this.pyExecUtils.exec(pyCommand);

        if (StringUtil.isNotEmpty(result)) {
            try {
                result = result.replaceAll("[\n\r]", "");
                return Boolean.valueOf(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Boolean.FALSE;
    }

    @GetMapping("/test/main")
    public void main() {
        PyCommandBuilder pyCommand = new PyCommandBuilder();
//        pyCommand.setVersion("py.exe");
//        pyCommand.setPath("D:/metoo/py");

        pyCommand.setVersion(Global.py_name);
        pyCommand.setPath(Global.PYPATH);
        pyCommand.setPy_prefix("-W ignore");
        pyCommand.setName("main.pyc");
        String result = PyExecUtils.execPy(pyCommand);

        System.out.println(result);
    }

    @GetMapping("/test/main2")
    public Result main2() {
        PyCommandBuilder pyCommand = new PyCommandBuilder();
        pyCommand.setVersion("py.exe");
        pyCommand.setPy_prefix("-W ignore");
        pyCommand.setPath("C:\\\\netmap\\\\script");
        pyCommand.setName("main.pyc");
        pyCommand.setParams(new String[]{
                "h3c",
                "switch",
                "192.100.101.254",
                "ssh",
                "22",
                "admin",
                "ncdyyy@2022", "test"});
        String result = PyExecUtils.execPy(pyCommand);

        String cleanedStr = result.replaceAll("[\n\r]", "");
        return ResponseUtil.ok(cleanedStr);
    }

    @GetMapping("/test/main3")
    public Result main3() {
        PyCommandBuilder pyCommand = new PyCommandBuilder();
        pyCommand.setPrefix("");
        pyCommand.setVersion("");
        pyCommand.setPath(Global.os_scanner);
        pyCommand.setName("os-scanner.exe");
        pyCommand.setParams(new String[]{
                "-i",
                "192.100.101.254",
                "-o",
                "22",
                "-c",
                "1"
        });
        String result = PyExecUtils.execPy(pyCommand);

        return ResponseUtil.ok(result);
    }

    @GetMapping("/test/main7")
    public Result main7() {
        PyCommandBuilder pyCommand = new PyCommandBuilder();
        pyCommand.setPrefix("");
        pyCommand.setVersion("");
        pyCommand.setPath(Global.os_scanner);
//        pyCommand.setName(".\\os-scanner.exe");
        pyCommand.setName(Global.os_scanner_name);
        pyCommand.setParams(new String[]{
                "-i",
                "192.100.101.254",
                "-o",
                "22",
                "-c",
                "1"
        });
        String result = PyExecUtils.execPy(pyCommand);

        return ResponseUtil.ok(result);
    }

    @GetMapping("/test/main8")
    public Result main8() {
        PyCommandBuilder pyCommand = new PyCommandBuilder();
        pyCommand.setPrefix("");
        pyCommand.setVersion("");
        pyCommand.setPath(Global.os_scanner);
        pyCommand.setName("C:\\netmap\\os-scanner.exe");
        pyCommand.setParams(new String[]{
                "-i",
                "192.100.101.254",
                "-o",
                "22",
                "-c",
                "1"
        });
        String result = PyExecUtils.execPy(pyCommand);

        return ResponseUtil.ok(result);
    }

    @Test
    public void main6() {
        PyCommandBuilder pyCommand = new PyCommandBuilder();
        pyCommand.setPrefix("");
        pyCommand.setVersion("");
        pyCommand.setPath(Global.os_scanner);
        pyCommand.setName(Global.os_scanner_name);
        pyCommand.setParams(new String[]{
                "-i",
                "192.100.101.254",
                "-o",
                "22",
                "-c",
                "1"
        });
        String result = PyExecUtils.execPy(pyCommand);

    }


    @GetMapping("/test/main4")
    public Result main4() {
        PyCommandBuilder pyCommand = new PyCommandBuilder();
        pyCommand.setPrefix("");
        pyCommand.setVersion("");
        pyCommand.setPath(Global.cf_scanner);
        pyCommand.setName("cf-scanner.exe");
        pyCommand.setParams(new String[]{
                "-i",
                "192.100.101.254" + "/" + "24",
                "-ns=true",
                "-np=false",
                "-m",
                "ping"
        });
        String result = PyExecUtils.execPy(pyCommand);

        return ResponseUtil.ok(result);
    }


    // 优化使用线程执行测试脚本（使用有返回结果的线程方法），观察abt的py执行流程
// Callable接口和Future接口来实现
    @GetMapping("/test/thread")
    public Result testDevice(@RequestParam Integer id) {
        Device device = this.deviceService.selectObjById(id);
        if (device != null) {
            if (device.getDeviceTypeId() != null
                    && !device.getDeviceTypeId().equals("")) {
                DeviceType deviceType = this.deviceTypeService.selectObjById(device.getDeviceTypeId());
                if (deviceType != null) {
                    device.setDeviceTypeName(deviceType.getName());
                    device.setDeviceTypeAlias(deviceType.getAlias());
                }
            }
            if (device.getDeviceVendorId() != null
                    && !device.getDeviceVendorId().equals("")) {
                DeviceVendor deviceVendor = this.deviceVendorService.selectObjById(device.getDeviceVendorId());
                if (deviceVendor != null) {
                    device.setDeviceVendorName(deviceVendor.getName());
                    device.setDeviceVendorAlias(deviceVendor.getAlias());
                }
            }
            PyCommand pyCommand = (PyCommand) ApplicationContextUtils.getBean("pyCommand");
            pyCommand.setVersion("python3");
            pyCommand.setPath(Global.PYPATH);
            pyCommand.setName("main.pyc");
            pyCommand.setParams(new String[]{
                    device.getDeviceVendorAlias(),
                    device.getDeviceTypeAlias(),
                    device.getIp(),
                    device.getLoginType(),
                    device.getLoginPort(),
                    device.getLoginName(),
                    device.getLoginPassword(), "test"});

            // java 线程池工具类，需要关闭吗

            MyCallableTask myCallable = new MyCallableTask(pyExecUtils, pyCommand);

            // 提交 Callable 任务，并获取 Future 对象
            Future<String> future = ThreadPoolUtils.submit(myCallable);

            try {
                String result = future.get(); // 阻塞等待任务执行完成并获取结果

                System.out.println("Callable task result: " + result);

                return ResponseUtil.ok(Boolean.valueOf(result));

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return ResponseUtil.ok(Boolean.FALSE);
    }

}

// 内部类
class MyCallableTask implements Callable<String> {

    private PyExecUtils pyExecUtils;
    private PyCommand pyCommand;

    public MyCallableTask(PyExecUtils pyExecUtils, PyCommand pyCommand) {
        this.pyExecUtils = pyExecUtils;
        this.pyCommand = pyCommand;
    }

    @Override
    public String call() throws Exception {
        String result = this.pyExecUtils.exec(pyCommand);
        return result;
    }
}