package com.metoo.sqlite.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.dto.GatherJsonDto;
import com.metoo.sqlite.dto.PortIpv4AndIpv6Dto;
import com.metoo.sqlite.entity.*;
import com.metoo.sqlite.gather.common.GatherCacheManager;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.factory.gather.thread.ExecThread;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4MuyunCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4PanabitCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv6MuyunCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv6OanabitCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.*;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.file.FileUtils;
import com.metoo.sqlite.utils.file.JsonFileToDto;
import com.metoo.sqlite.utils.muyun.MuyunService;
import com.metoo.sqlite.utils.net.Ipv4Utils;
import com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils.PanabitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.sun.corba.se.impl.util.RepositoryId.cache;

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
    @Autowired
    private PublicService publicService;
    @Autowired
    private IPanaSwitchService panaSwitchService;

    /**
     * 调用main.pyc执行脚本及读取文件内容
     *
     * @return
     */
    public void callMainExecute(Integer parentId) throws Exception {
        Long time = System.currentTimeMillis();
        log.info("all in one  Start......");
        ipv4Service.deleteTableGather();
        ipv6Service.deleteTableGather();
        portIpv4Service.deleteTableGather();
        portIpv6Service.deleteTableGather();

        Map params = new HashMap();
        params.put("type", 0);
        List<Device> deviceList = deviceService.selectObjByMap(params);

        if (deviceList.size() > 0) {
            CountDownLatch latch = Global.isConcurrent ? new CountDownLatch(deviceList.size()) : null;
            for (Device device : deviceList) {
                if (!GatherCacheManager.running) {
                    throw new RuntimeException("测绘已手动中止");
                }
                boolean flag = true;
                // 设备日志添加
                String beginTime = DateTools.getCreateTime();
                int logId = publicService.createSureyingLog("设备:" + device.getName() + "采集分析", beginTime, 1, parentId, 4);
                try {
                    // 调用main.pyc执行脚本
                    Context context = new Context();
                    context.setCreateTime(DateTools.getCreateTime());
                    context.setLatch(latch);
                    context.setEntity(device);
                    context.setLogId(logId);
                    if (device.getLoginType().equals("api") && device.getDeviceTypeAlias().equals("api")
                            && device.getDeviceVendorAlias().equals("muyun")) {
                        DataCollectionStrategy collectionStrategy = new Ipv6MuyunCollectionStrategy(ipv6Service,
                                muyunService);
                        ExecThread.exec(collectionStrategy, context);

                        collectionStrategy = new Ipv4MuyunCollectionStrategy(ipv4Service,
                                muyunService);
                        ExecThread.exec(collectionStrategy, context);
                    } else if (device.getLoginType().equals("api") && device.getDeviceTypeAlias().equals("api")
                            && device.getDeviceVendorAlias().equals("pana")) {
                        DataCollectionStrategy collectionStrategy = new Ipv6OanabitCollectionStrategy(ipv6Service,
                                panabitService);
                        ExecThread.exec(collectionStrategy, context);

                        collectionStrategy = new Ipv4PanabitCollectionStrategy(ipv4Service,
                                panabitService, panaSwitchService);
                        ExecThread.exec(collectionStrategy, context);
                    } else {
                        flag = collectData(context);
                    }
                    if (latch != null) {
                        latch.countDown();
                    }
                    // 标记设备分析成功
                    if (flag) {
                        publicService.updateSureyingLog(logId, 2);
                    } else {
                        // 标记设备分析失败
                        publicService.updateSureyingLog(logId, 3);
                    }
                } catch (Exception ex) {
                    log.info(ex.getMessage());
                    publicService.updateSureyingLog(logId, 3);
                }
            }

            if (Global.isConcurrent && latch != null) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("失败：{}", e);
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
    public boolean collectData(Context context) throws Exception {
        Device device = (Device) context.getEntity();
        boolean ipv4Flag = false;
        boolean ipv6Flag = false;
        String result = "";
        if (device != null) {
            int ipv4Id = publicService.createSureyingLog("ipv4采集", DateTools.getCreateTime(), 1, context.getLogId(), 5);
            int ipv6Id = publicService.createSureyingLog("ipv6采集", DateTools.getCreateTime(), 1, context.getLogId(), 6);
            try {
                PyCommandBuilder pyCommand = new PyCommandBuilder();
//                pyCommand.setVersion(Global.py_name);
//                pyCommand.setPy_prefix("-W ignore");
                pyCommand.setPath(Global.PYPATH);
                pyCommand.setName("main.exe");
                pyCommand.setParams(new String[]{
                        device.getDeviceVendorAlias(),
                        device.getDeviceTypeAlias(),
                        device.getIp(),
                        device.getLoginType(),
                        device.getLoginPort(),
                        device.getLoginName(),
                        device.getLoginPassword(), Global.ALL_IN_ONE});
                result = this.pyExecUtils.exec(pyCommand);
                log.info("main.pyc==========:{}", result);
                if (StringUtil.isNotEmpty(result) && "1".equals(result)) {
                    publicService.updateSureyingLog(ipv4Id, 3);
                    ipv4Flag = true;
                } else if (StringUtil.isNotEmpty(result) && "2".equals(result)) {
                    publicService.updateSureyingLog(ipv6Id, 3);
                    ipv6Flag = true;
                }
                // 解析json文件
                GatherJsonDto resultJson = new GatherJsonDto();
                ObjectMapper objectMapper = new ObjectMapper();
                File file = new File(Global.resultFile);
                JsonNode rootNode = objectMapper.readTree(file);
                try {
                    // 解析arp数组
                    JsonNode arpArray = rootNode.get("arp");
                    List<Ipv4> arp = new ArrayList<>();
                    if (null != arpArray && arpArray.isArray()) {
                        for (JsonNode arpNode : arpArray) {
                            Ipv4 temp = new Ipv4();
                            temp.setIp(arpNode.get("ip") == null ? "" : arpNode.get("ip").asText());
                            temp.setMac(arpNode.get("mac") == null ? "" : arpNode.get("mac").asText());
                            temp.setPort(arpNode.get("port") == null ? "" : arpNode.get("port").asText());
                            temp.setVlan(arpNode.get("vlan") == null ? "" : arpNode.get("vlan").asText());
                            temp.setType(arpNode.get("type") == null ? "" : arpNode.get("type").asText());
                            arp.add(temp);
                        }
                        resultJson.setArp(arp);
                    }
                } catch (Exception e) {
                    log.error("arp解析出现错误：{}", e);
                    if (!ipv4Flag) {
                        publicService.updateSureyingLog(ipv4Id, 3);
                        ipv4Flag = true;
                    }
                }
                try {
                    JsonNode aliveintArray = rootNode.get("aliveint");
                    List<PortIpv4AndIpv6Dto> aliveint = new ArrayList<>();
                    if (null != aliveintArray && aliveintArray.isArray()) {
                        for (JsonNode aliveintNode : aliveintArray) {
                            PortIpv4AndIpv6Dto temp = new PortIpv4AndIpv6Dto();
                            temp.setPort(aliveintNode.get("port") == null ? "" : aliveintNode.get("port").asText());
                            temp.setIp(aliveintNode.get("ip") == null ? "" : aliveintNode.get("ip").asText());
                            temp.setMask(aliveintNode.get("mask") == null ? "" : aliveintNode.get("mask").asText());
                            temp.setIpv6_address(aliveintNode.get("ipv6_address") == null ? "" : aliveintNode.get("ipv6_address").asText());
                            temp.setIpv6_subnet(aliveintNode.get("ipv6_subnet") == null ? "" : aliveintNode.get("ipv6_subnet").asText());
                            aliveint.add(temp);
                        }
                        resultJson.setAliveint(aliveint);
                    }
                } catch (Exception e) {
                    log.error("aliveint解析出现错误：{}", e);
                }
                try {
                    JsonNode ipv6neighborsArray = rootNode.get("ipv6_neighbors");
                    List<Ipv6> ipv6neighbors = new ArrayList<>();
                    if (null != ipv6neighborsArray && ipv6neighborsArray.isArray()) {
                        for (JsonNode jsonNode : ipv6neighborsArray) {
                            Ipv6 temp = new Ipv6();
                            temp.setIpv6_address(jsonNode.get("ipv6_address") == null ? "" : jsonNode.get("ipv6_address").asText());
                            temp.setIpv6_mac(jsonNode.get("ipv6_mac") == null ? "" : jsonNode.get("ipv6_mac").asText());
                            temp.setPort(jsonNode.get("port") == null ? "" : jsonNode.get("port").asText());
                            temp.setVpninstance(jsonNode.get("vpninstance") == null ? "" : jsonNode.get("vpninstance").asText());
                            temp.setState(jsonNode.get("state") == null ? "" : jsonNode.get("state").asText());
                            temp.setAge(jsonNode.get("age") == null ? "" : jsonNode.get("age").asText());
                            temp.setType(jsonNode.get("type") == null ? "" : jsonNode.get("type").asText());
                            ipv6neighbors.add(temp);
                        }
                        resultJson.setIpv6neighbors(ipv6neighbors);
                    }

                } catch (Exception e) {
                    log.error("ipv6neighbors解析出现错误：{}", e);
                    // 保存错误图片
                    FileUtils.copyFileAndNewName(Global.errorImageUrl, Global.errorImageFileName, context.getLogId() + "");
                    if (!ipv6Flag) {
                        publicService.updateSureyingLog(ipv6Id, 3);
                        ipv6Flag = true;
                    }
                }
                log.info("result返回结果=================={}", JSONObject.toJSONString(resultJson));
                if (null != resultJson && CollectionUtil.isNotEmpty(resultJson.getArp())) {
                    try {
                        resultJson.getArp().forEach(e -> {
                            e.setDeviceUuid(device.getUuid());
                            e.setCreateTime(context.getCreateTime());
                        });
                        this.ipv4Service.batchInsertGather(resultJson.getArp());
                    } catch (Exception ex) {
                        log.error("arp解析入库失败：{}", ex);
                        if (!ipv4Flag) {
                            publicService.updateSureyingLog(ipv4Id, 3);
                            ipv4Flag = true;
                        }
                    }
                }
                if (null != resultJson && CollectionUtil.isNotEmpty(resultJson.getAliveint())) {
                    try {
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
                    } catch (Exception ex) {
                        log.error("aliveint解析入库失败：{}", ex);
                        if (!ipv4Flag) {
                            publicService.updateSureyingLog(ipv4Id, 3);
                            ipv4Flag = true;
                        }
                    }
                }
                if (null != resultJson && CollectionUtil.isNotEmpty(resultJson.getIpv6neighbors())) {
                    try {
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
                    } catch (Exception e) {
                        log.error("ipv6neighbors解析入库失败：{}", e);
                        if (!ipv6Flag) {
                            publicService.updateSureyingLog(ipv6Id, 3);
                            ipv6Flag = true;
                        }
                    }
                }
                // 全部执行完毕，则都认为是成功
                if (!ipv4Flag) {
                    publicService.updateSureyingLog(ipv4Id, 2);
                }
                if (!ipv6Flag) {
                    publicService.updateSureyingLog(ipv6Id, 2);
                }
            } catch (Exception e) {
                log.error("{}：采集数据出现异常：{}", device.getName(), e);
                publicService.updateSureyingLog(ipv4Id, 3);
                publicService.updateSureyingLog(ipv6Id, 3);
                return false;
            }
        }
        if (ipv4Flag && ipv6Flag) {
            // ipv4和ipvd6都采集失败
            return false;
        }
        return true;
    }
}
