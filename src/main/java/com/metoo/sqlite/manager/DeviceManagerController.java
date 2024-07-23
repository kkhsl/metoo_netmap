package com.metoo.sqlite.manager;

import com.metoo.sqlite.core.config.application.ApplicationContextUtils;
import com.metoo.sqlite.dto.DeviceDTO;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.DeviceType;
import com.metoo.sqlite.entity.DeviceVendor;
import com.metoo.sqlite.gather.common.PyCommand;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.pool.GatherDataThreadPool;
import com.metoo.sqlite.gather.pool.ThreadPoolUtils;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.IDeviceService;
import com.metoo.sqlite.service.IDeviceTypeService;
import com.metoo.sqlite.service.IDeviceVendorService;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import io.swagger.annotations.ApiOperation;
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

    @PostMapping("/list")
    private Result list(@RequestBody DeviceDTO dto){
        Result result = this.deviceService.selectObjConditionQuery(dto);
        return result;
    }

    @PostMapping("/save")
    private Result save(@RequestBody Device instance){
        Result result = this.deviceService.save(instance);
        return result;
    }

    @PostMapping("/batch/save")
    private Result batchSave(@RequestBody List<Device> devices){
        if(devices.size() > 0){
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
    public Result test(@RequestParam Integer id){
        Device device = this.deviceService.selectObjById(id);
        if(device != null){
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
                }
            }
            PyCommandBuilder pyCommand = new PyCommandBuilder();
            pyCommand.setVersion("python3");
            pyCommand.setPath(Global.PYPATH);
            pyCommand.setName("main.py");
            pyCommand.setParams(new String[]{
                    device.getDeviceVendorAlias(),
                    device.getDeviceTypeAlias(),
                    device.getIp(),
                    device.getLoginType(),
                    device.getLoginPort(),
                    device.getLoginName(),
                    device.getLoginPassword(), "test"});
            String result = this.pyExecUtils.exec(pyCommand);
            return ResponseUtil.ok(Boolean.valueOf(result));
        }
       return ResponseUtil.ok(Boolean.FALSE);
    }


// 优化使用线程执行测试脚本（使用有返回结果的线程方法），观察abt的py执行流程
// Callable接口和Future接口来实现
    @GetMapping("/test/thread")
    public Result testDevice(@RequestParam Integer id){
        Device device = this.deviceService.selectObjById(id);
        if(device != null){
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
                }
            }
            PyCommand pyCommand = (PyCommand) ApplicationContextUtils.getBean("pyCommand");
            pyCommand.setVersion("python3");
            pyCommand.setPath(Global.PYPATH);
            pyCommand.setName("main.py");
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

    public MyCallableTask(PyExecUtils pyExecUtils, PyCommand pyCommand){
        this.pyExecUtils = pyExecUtils;
        this.pyCommand = pyCommand;
    }

    @Override
    public String call() throws Exception {
        String result = this.pyExecUtils.exec(pyCommand);
        return result;
    }
}