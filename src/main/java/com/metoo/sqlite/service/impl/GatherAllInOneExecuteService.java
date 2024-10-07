package com.metoo.sqlite.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.dto.GatherJsonDto;
import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.entity.Ipv6;
import com.metoo.sqlite.entity.PortIpv4;
import com.metoo.sqlite.entity.PortIpv6;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.factory.gather.thread.ExecThread;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.other.Ipv6MuyunCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv6OanabitCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.file.JsonFileToDto;
import com.metoo.sqlite.utils.muyun.MuyunService;
import com.metoo.sqlite.utils.net.Ipv4Utils;
import com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils.PanabitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 测绘采集服务-新版本
 *
 * @author zzy
 * @version 1.0
 * @date 2024/10/5 11:32
 */
@Service
@Slf4j
public class GatherAllInOneExecuteService {

    @Autowired
    private MuyunService muyunService;
    @Autowired
    private Ipv4Service ipv4Service;
    @Autowired
    private Ipv6Service ipv6Service;
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private IPortIpv4Service portIpv4Service;
    @Autowired
    private IPortIpv6Service portIpv6Service;
    @Autowired
    private PanabitService panabitService;
    @Autowired
    private PyExecUtils pyExecUtils;

    /**
     * 调用main.pyc执行脚本及读取文件内容
     *
     * @return
     */
    public void callMainExecute() throws Exception {
        Long time = System.currentTimeMillis();
        log.info("all in one  Start......");
        ipv4Service.deleteTableGather();
        ipv6Service.deleteTableGather();
        portIpv4Service.deleteTableGather();
        portIpv6Service.deleteTableGather();
        List<Device> deviceList = deviceService.selectObjByMap(null);

        if (deviceList.size() > 0) {
            CountDownLatch latch = Global.isConcurrent ? new CountDownLatch(deviceList.size()) : null;
            for (Device device : deviceList) {
                Context context = new Context();
                context.setCreateTime(DateTools.getCreateTime());
                context.setLatch(latch);
                context.setEntity(device);

                if (device.getLoginType().equals("api") && device.getDeviceTypeAlias().equals("api")
                        && device.getDeviceVendorAlias().equals("muyun")) {
                    Ipv6MuyunCollectionStrategy collectionStrategy = new Ipv6MuyunCollectionStrategy(ipv6Service,
                            muyunService);
                    ExecThread.exec(collectionStrategy, context);
                } else if (device.getLoginType().equals("api") && device.getDeviceTypeAlias().equals("api")
                        && device.getDeviceVendorAlias().equals("pana")) {
                    Ipv6OanabitCollectionStrategy collectionStrategy = new Ipv6OanabitCollectionStrategy(ipv6Service,
                            panabitService);
                    ExecThread.exec(collectionStrategy, context);
                } else if ((device.getLoginType().equals("ssh") || device.getLoginType().equals("telnet")) &&
                        device.getDeviceTypeAlias().equals("router") || device.getDeviceTypeAlias().equals("switch")) {
                    collectData(context);

                } else if ((device.getDeviceTypeAlias().equals("firewall") || device.getDeviceTypeAlias().equals("linux"))

                        && (device.getDeviceVendorAlias().equals("huawei") || device.getDeviceVendorAlias().equals("h3c")
                        || device.getDeviceVendorAlias().equals("sanfor")
                        || device.getDeviceVendorAlias().equals("sanforip"))

                        || device.getDeviceVendorAlias().equals("stone")
                        || device.getDeviceVendorAlias().equals("leadsec")
                        || device.getDeviceVendorAlias().equals("hd")

                        && (device.getLoginType().equals("ssh") || device.getLoginType().equals("telnet"))) {
                    collectData(context);
                } else {
                    if (latch != null) {
                        latch.countDown();
                    }
                }
            }
            if (Global.isConcurrent && latch != null) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        ipv4Service.copyGatherData();
        // 复制数据到端口ip表
        portIpv4Service.copyGatherData();
        portIpv6Service.copyGatherData();
        // 复制数据到端口ip表
        ipv6Service.copyGatherData();
        log.info("all in one  End......" + (System.currentTimeMillis() - time));
    }

    /**
     * 采集数据
     *
     * @param context
     */
    public void collectData(Context context) throws Exception {
        Device device = (Device) context.getEntity();
        if (device != null) {
            PyCommandBuilder pyCommand = new PyCommandBuilder();
            pyCommand.setVersion(Global.py_name);
            pyCommand.setPy_prefix("-W ignore");
            pyCommand.setPath(Global.PYPATH);
            pyCommand.setName("main.pyc");
            pyCommand.setParams(new String[]{
                    device.getDeviceVendorAlias(),
                    device.getDeviceTypeAlias(),
                    device.getIp(),
                    device.getLoginType(),
                    device.getLoginPort(),
                    device.getLoginName(),
                    device.getLoginPassword(), Global.ALL_IN_ONE});
            String result = this.pyExecUtils.exec(pyCommand);
            if (StringUtil.isNotEmpty(result) && "1".equals(result)) {
                throw new Exception("ipv4采集错误");
            } else if (StringUtil.isNotEmpty(result) && "2".equals(result)) {
                throw new Exception("ipv6采集错误");
            }
            GatherJsonDto resultJson = JsonFileToDto.readDataFromJsonFile(Global.resultFile);
            log.info("result返回结果=================={}", JSONObject.toJSONString(resultJson));
            if (null != resultJson && CollectionUtil.isNotEmpty(resultJson.getArp())) {
                resultJson.getArp().forEach(e -> {
                    e.setDeviceUuid(device.getUuid());
                    e.setCreateTime(context.getCreateTime());
                });
                this.ipv4Service.batchInsertGather(resultJson.getArp());
            } else {
                throw new Exception("ipv4采集错误");
            }
            if (null != resultJson && CollectionUtil.isNotEmpty(resultJson.getAliveint())) {
                List<PortIpv6> ipv6s = CollectionUtil.newArrayList();
                List<PortIpv4> ipv4s = CollectionUtil.newArrayList();
                resultJson.getAliveint().forEach(e -> {
                    e.setStatus(1);
                    e.setDeviceUuid(device.getUuid());
                    e.setCreateTime(context.getCreateTime());
                    if (StringUtil.isNotEmpty(e.getMask())) {
                        if (!e.getMask().contains(".")) {
                            e.setMask(Ipv4Utils.bitMaskConvertMask(Integer.parseInt(e.getMask())));
                        }
                    }
                    if (e.getIpv6_address() != null && !e.getIpv6_address().isEmpty()) {
                        ipv6s.add(Convert.convert(PortIpv6.class, e));
                    } else {
                        ipv4s.add(Convert.convert(PortIpv4.class, e));
                    }
                });
                this.portIpv6Service.batchInsertGather(ipv6s);
                this.portIpv4Service.batchInsertGather(ipv4s);
            }
            if (null != resultJson && CollectionUtil.isNotEmpty(resultJson.getIpv6neighbors())) {
                List<Ipv6> filteredObjects = CollectionUtil.newArrayList();
                resultJson.getIpv6neighbors().forEach(e -> {
                    e.setDeviceUuid(device.getUuid());
                    e.setCreateTime(context.getCreateTime());
                    if ((e.getIpv6_address() != null
                            && !e.getIpv6_address().toLowerCase().startsWith("FE80".toLowerCase()))) {
                        filteredObjects.add(Convert.convert(Ipv6.class, e));
                    }
                });
                this.ipv6Service.batchInsertGather(filteredObjects);
            } else {
                throw new Exception("ipv6采集错误");
            }
        }
    }
}
